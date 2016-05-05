package edu.grinnell.sandb.Services.Implementation;

import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.QueryResponse;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;
import edu.grinnell.sandb.Util.StringUtility;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * This class is a retrofit implementation of the RemoteServiceAPI for data retrieval
 * from a remote server.
 *
 * <p> Most of the method calls perform an asynchronous fetching of data from some data end points
 * at a given Base URL. Retrofit abstracts the nifty gritty details of the http calls.
 * </p>
 *
 *
 * @author Albert Owusu-Asare
 * @version 1.1  Wed Mar 16 23:38:58 CDT 2016
 * @see retrofit2.Retrofit
 * @see retrofit2.Call
 * @see java.util.Collections
 * @see edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI
 * @see Observable
 * @see Serializable
 */
public class WordPressService extends Observable implements RemoteServiceAPI,Serializable {

    /* Class Fields */
    private Retrofit retrofit;
    private RestAPI restService;
    private LocalCacheClient localCacheClient;
    private Map<String,Boolean> completedMap;


    public WordPressService(){
        this(new RealmDbClient());
    }

    public WordPressService(LocalCacheClient localCacheClient){
        this.retrofit = initializeRetrofit();
        this.restService = this.retrofit.create(RestAPI.class);
        this.localCacheClient = localCacheClient;
        this.completedMap = new HashMap<>();
    }


    @Override
    public void getAll(final int page,int count, final String lastArticleDate) {
        Call<QueryResponse> call  =restService.posts(page, count);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                int responseCode = response.code();
                List<RealmArticle> posts = response.body().getPosts();
                int numPosts = posts.size();
                localCacheClient.saveArticles(posts);
                SyncMessage message;
                setChanged();
                message = new SyncMessage(responseCode,Constants.ArticleCategories.ALL.toString()
                        ,page,lastArticleDate);
                notifyObservers(message);
            }
            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {
                Log.e("Get All failure", t.getMessage());
            }
        });
    }


    @Override
    public void getFirst() {
      //  getAll(Constants.ONE, Constants.ONE);
    }

    @Override
    public void  getAfter(final String date,final String category) {
        Log.i("WordPressService", "Fetching most recent " + category);
        if(category.equals("all")) {
            getAllAfter(date, category);
        }
        else {
            getAfterByCategory(date,category);
        }
    }
    private void getAfterByCategory(String date, String category){
        Call<QueryResponse> call = restService.postsAfter(date,10,category);
        refreshData(date,category,call);
    }

    private void getAllAfter(final String date, final String category) {
        Call<QueryResponse> call = restService.postsAfter(date);
        refreshData(date, category, call);
    }

    private void refreshData(final String date, final String category, Call<QueryResponse> call) {
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                List<RealmArticle> posts = response.body().getPosts();
                localCacheClient.saveArticles(posts);
                SyncMessage message;
                message = new SyncMessage(Constants.UpdateType.REFRESH, 200, 1, category, date, posts);
                setChanged();
                notifyObservers(message);
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {
                Log.e("Get All failure", t.getMessage());
            }
        });
    }


    @Override
    public void getAll(List<String> fields) {
       return;
    }

    @Override
    public boolean isUpdated(RealmArticle localFirst) {
       // return (localFirst == null) ? false : (localFirst.equals(this.getFirst()));
        return true;
    }

    @Override
    public void addObservers(List<Observer> observers) {
        for(Observer observer :observers)
            addObserver(observer);
    }

    @Override
    public void syncWithLocalCache(final RealmArticle localFirst, final String category) {
        /* The local cache might not be updated yet due to an ongoing sync process in the background.
           We return and use the results of the ongoing sync process*/
        if(localFirst == null) {
            return;
        }

        Call<QueryResponse> call = restService.posts(Constants.ONE, Constants.ONE);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                int responseCode = response.code();
                RealmArticle remoteFirst = response.body().getFirstPost();
                if (!localFirst.equals(remoteFirst)) {
                    Log.i("Equality Test", "Local and remoteNotEqual");
                    getAfter(localFirst.getPubDate(), category);
                } else {
                    Log.i(category + " Equality Test", "Local and remote Equal");
                    //SyncMessage message = new SyncMessage(responseCode,category,currentPageNumber,null);
                    setChanged();
                    notifyObservers();
                }
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {
                Log.e("Remote Service Failure:", category);
                t.printStackTrace();
            }
        });
    }





    @Override
    public void getNextPage(final int page, int offset, final String category, int number){
        Log.i("Remote Client","Getting page "+ 2 +" for " + category);
        Call<QueryResponse> call = restService.postsNextPage(page,offset,category,number);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                Log.i("Remote Client", "Response Recieved get next page for "+ category);
                int responseCode = response.code();
                List<RealmArticle> articles = response.body().getPosts();
                localCacheClient.saveArticles(articles);
                SyncMessage message =
                        new SyncMessage(Constants.UpdateType.NEXT_PAGE,200,page,category,null,articles);
                setChanged();
                notifyObservers(message);
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {
                Log.i("Remote Client", "NOT SUCCESSFUL get next page for "+ category);
            }
        });

    }

    @Override
    public void initialize() {
        Call<QueryResponse> call = restService.posts(1,20);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                Log.i("Remote Client", "Response Recieved");
                int responseCode = response.code();
                List<RealmArticle> articles = response.body().getPosts();
                //System.out.println(articles);
                localCacheClient.saveArticles(articles);
                SyncMessage message =
                        new SyncMessage(Constants.UpdateType.INITIALIZE,200,1,"All",null,null);
                setChanged();
                notifyObservers(message);

               // sendMessage();
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {

            }
        });

        Log.i("Remote Client","Calling initialize in Remote Client");
     /*   for(String category : Constants.CATEGORIES){
           getNext(category,0,10);
        }
        */

    }

    @Override
    public void getByCategory(String category, String lastArticleUpdated, Integer topUpNum) {
       Log.i("WordPressService ","Getting posts before..");
        Call<QueryResponse> call = restService.postsBefore(lastArticleUpdated,topUpNum,category);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                Log.i("WordPressServices ", "Response recieved in get before");
                int responseCode = response.code();
                List<RealmArticle> articles = response.body().getPosts();
                //System.out.println(articles);
                localCacheClient.saveArticles(articles);
                /*SyncMessage message =
                        new SyncMessage(Constants.UpdateType.INITIALIZE,200,1,"All",null,null);
                setChanged();
                notifyObservers(message);
                */

                // sendMessage();
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {

            }
        });

    }

    public  void sendMessage(){
        Log.i("Remote Client", "Sending message to network client");
        //List<Article> articles = localCacheClient.getAll();
      //  Log.i("Remote Client", "Message body" + articles.size());

        //SyncMessage message = new SyncMessage(articles);
       // setChanged();
        //notifyObservers(message);
    }

    public boolean isFetchAllCompleted(){
        for(Boolean value :completedMap.values()){
            if(value == false) return false;
        }
        return true;
    }

    public void getNext(final String category, final int pageNumber,final int numArticlesPerPage){
        Call<QueryResponse> call = restService.posts(category, pageNumber, numArticlesPerPage);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                int responseCode = response.code();
                List<RealmArticle> articles = response.body().getPosts();
                localCacheClient.saveArticles(articles);
                setPullCompleted(category);
                if(isFetchAllCompleted()){
                    Log.i("RemoteClient", "Last category updated");
                    sendMessage();
                }
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {

            }
        });
    }

    private void setPullCompleted(String category) {
        this.completedMap.put(category, true);
    }


    /* Private Helper methods */
    private Retrofit initializeRetrofit() {
        ExclusionStrategy exclusionStrategy = new SandBGsonExclusionStrategy();
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(exclusionStrategy)
                .create();
         return new Retrofit.Builder()
                .baseUrl(Constants.PUBLIC_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void makeAsyncCall(Call<QueryResponse> call, final String category,
                              final String lastArticleDate) {
        call.enqueue(new Callback<QueryResponse>() {
        @Override
        public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
            Log.d("makeAsyncCall success", "makeAsyncCall()");
            int responseCode = response.code();
            SyncMessage message;
            QueryResponse responseBody = response.body();
            List<RealmArticle> posts = responseBody.getPosts();
            localCacheClient.saveArticles(posts);
            setChanged();
            message = new SyncMessage(responseCode,category,Constants.ZERO,lastArticleDate);
            notifyObservers(message);
        }
        @Override
        public void onFailure(Call<QueryResponse> call, Throwable t) {
            //TODO : Implement Failure response SnackBar?
            Log.e("makeAsyncCall Failure", category);
            t.printStackTrace();
        }
    });
    }

     /* Private Classes  and interfaces */
    private interface RestAPI {
        @GET("posts/")
        Call<QueryResponse> posts(@Query("page") int pageNumber, @Query("number") int count);

        @GET("posts/")
         Call<QueryResponse> postsAfter(@Query("after") String dateTime);
         @GET("posts/")
        // https://public-api.wordpress.com/rest/v1.1/sites/www.thesandb.com/posts/?category=%22news%22&page=1&number=4
         Call<QueryResponse> posts(@Query("category") String category,@Query("page") int pageNumber,
                                   @Query("number") int count);
         @GET("posts/")
         Call<QueryResponse> postsBefore(@Query("before") String dateBefore,@Query("number") int count,
                                   @Query("category") String category);
         @GET("posts/")
         Call<QueryResponse> postsAfter(@Query("after") String dateAfter,@Query("number") int count,
                                         @Query("category") String category);
         @GET("posts/")
         Call<QueryResponse> postsNextPage(@Query("page") int page,@Query("offset") int offset,
                                        @Query("category") String category, @Query("number") int number);

     }

    /*
     * This class is useful in the deserialization process by the Gson Converter. It specifies
     * the fields that we will like to exclude from the serialization/ deserialization process.
     *
     * E.g in this particular case we are excluding article id field because we want to avoid
     * conflicts with yet another "id" field which comes from the Sugar ORM extensions of the
     * Article class.
     */
    private class SandBGsonExclusionStrategy implements ExclusionStrategy{
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return (f.getDeclaringClass() == SugarRecord.class && (f.getName().equals("id") ||
            f.getName().equals("categories")));
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}

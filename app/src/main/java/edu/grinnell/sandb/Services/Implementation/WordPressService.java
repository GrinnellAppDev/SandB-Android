package edu.grinnell.sandb.Services.Implementation;

import android.support.design.widget.Snackbar;
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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.QueryResponse;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Services.Interfaces.RemoteServiceAPI;
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
 * @see retrofit2.Retrofit  r
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
    private final List<Article> articles = Collections.synchronizedList(new ArrayList<Article>());
    private int numArticlesPerPage;
    private int currentPageNumber;


    public WordPressService(){
        this(new ORMDbClient());
    }

    public WordPressService(LocalCacheClient localCacheClient){
        this.retrofit = initializeRetrofit();
        this.restService = this.retrofit.create(RestAPI.class);
        this.localCacheClient = localCacheClient;
        this.numArticlesPerPage = localCacheClient.getNumArticlesPerPage();
        this.currentPageNumber = Constants.FIRST_PAGE;
    }


    @Override
    public void getFirst() {
        getAll(Constants.ONE, Constants.ONE);
    }

    @Override
    public void  getAfter(final String date,final String category) {
        Call<QueryResponse> call = restService.postsAfter(date);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                int code = response.code();
                List<Article> articles = response.body().getPosts();
                localCacheClient.saveArticles(articles);
                SyncMessage message = new SyncMessage(
                        code,category,localCacheClient.getArticlesAfter(category,date));
                setChanged();
                notifyObservers(message);
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void getAll() {
        getAll(currentPageNumber, numArticlesPerPage);
    }

    @Override
    public void getAll(int page, int count) {
        makeAsyncCall(restService.posts(page,count),Constants.ArticleCategories.ALL.toString());
    }

    @Override
    public void getAll(List<String> fields) {
       return;
    }

    @Override
    public boolean isUpdated(Article localFirst) {
       // return (localFirst == null) ? false : (localFirst.equals(this.getFirst()));
        return true;
    }

    @Override
    public void addObservers(List<Observer> observers) {
        for(Observer observer :observers)
            addObserver(observer);
    }

    @Override
    public void syncWithLocalCache(final Article localFirst, final String category) {
        /* The local cache might not be updated yet due to an ongoing sync process in the background.
           We return and use the results of the ongoing sync process*/
        if(localFirst == null) {
            return;
        }

        Call<QueryResponse> call = restService.posts(Constants.ONE,Constants.ONE);
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                Article remoteFirst = response.body().getFirstPost();
                if (!localFirst.equals(remoteFirst)) {
                    getAfter(localFirst.getPubDate(),category);
                }
            }

            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {
                Log.e("Remote Service Failure:",category);
                t.printStackTrace();
            }
        });
    }

    public void firstTimeSyncWithLocalCache() {
        this.getAll();
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

    private void makeAsyncCall(Call<QueryResponse> call, final String category) {
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                Log.d("makeAsyncCall success", "makeAsyncCall()");
                SyncMessage message;
                QueryResponse responseBody = response.body();
                List<Article> posts = responseBody.getPosts();
                articles.addAll(posts);
                localCacheClient.saveArticles(posts);
                setChanged();
                message = new SyncMessage(response.code(),category,localCacheClient.getAll());
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

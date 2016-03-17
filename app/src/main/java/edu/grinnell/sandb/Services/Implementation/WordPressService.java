package edu.grinnell.sandb.Services.Implementation;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orm.SugarRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.QueryResponse;
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
 */
public class WordPressService implements RemoteServiceAPI {

    /* Constants */
    private static final String PUBLIC_API =
            "https://public-api.wordpress.com/rest/v1.1/sites/www.thesandb.com/";
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int FIRST_PAGE = ONE;

    /* Class Fields */
    private Retrofit retrofit;
    private RestAPI restService;
    private int numArticlesPerPage;
    private int currentPageNumber;
    private final List<Article> articles = Collections.synchronizedList(new ArrayList<Article>());

    public WordPressService(int numArticlesPerPage){
        this.retrofit = initializeRetrofit();
        this.restService = this.retrofit.create(RestAPI.class);
        this.numArticlesPerPage = numArticlesPerPage;
        this.currentPageNumber = FIRST_PAGE;
    }

    @Override
    public Article getFirst() {
        getAll(ONE,ONE);
        return this.articles.get(ZERO);
    }

    @Override
    public List<Article> getAfter(String date) {
        makeAsyncCall(restService.postsAfter(date));
        return this.articles;
    }

    @Override
    public List<Article> getAll() {
        getAll(currentPageNumber,numArticlesPerPage);
        return this.articles;
    }

    @Override
    public List<Article> getAll(int page, int count) {
        Call<QueryResponse> call = restService.posts(page,count);
        makeAsyncCall(call);
        return this.articles;
    }

    @Override
    public List<Article> getAll(List<String> fields) {
        return null;  //TODO  Implementation get all with dynamic querry parameters
    }
    /* Private Helper methods */
    private Retrofit initializeRetrofit() {
        ExclusionStrategy exclusionStrategy = new SandBGsonExclusionStrategy();
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(exclusionStrategy)
                .create();
         return new Retrofit.Builder()
                .baseUrl(PUBLIC_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void makeAsyncCall(Call<QueryResponse> call) {
        call.enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                QueryResponse responseBody = response.body();
                List<Article> posts = responseBody.getPosts();
                articles.clear();
                articles.addAll(posts);
            }
            @Override
            public void onFailure(Call<QueryResponse> call, Throwable t) {
                //TODO : Implement Failure response SnackBar?
            }
        });
    }

     /* Private Classes  and interfaces */
    private interface RestAPI {
        @GET("/posts/")
        Call<QueryResponse> posts(@Query("page") int pageNumber, @Query("number") int count);

        @GET("/posts/")
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
            return (f.getDeclaringClass() == SugarRecord.class && f.getName().equals("id"));
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}

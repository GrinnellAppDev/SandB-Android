package edu.grinnell.sandb.Services;

import android.util.Log;

import java.util.List;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.SandBResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public class SandBNetClient {
    private SandBServiceApi serviceApi;
    private Retrofit retrofit;
    private List<Article> articles;
    public SandBNetClient(){
        initialize();
        this.serviceApi = retrofit.create(SandBServiceApi.class);
    }

    private void initialize(){
         retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ENDPOINT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * returns the first numArticles from database(wordPress)
     * @param numArticles
     */
    public List<Article> getArticles(int numArticles){
        Log.i(SandBNetClient.class.getName() + "getArticles :","Inside getArticles");
        List<Article> articles = null;
        Call<SandBResponse> call = serviceApi.getRecentPost(numArticles);
        call.enqueue(new Callback<SandBResponse>() {
            @Override
            public void onResponse(Call<SandBResponse> call, Response<SandBResponse> response) {
                int statusCode = response.code();
                Log.e("Response status Code :", "" + statusCode);
                List<Article> articles = response.body().getArticles();
                saveArticles(articles);
            }

            @Override
            public void onFailure(Call<SandBResponse> call, Throwable t) {
                Log.e(SandBNetClient.class.getName(),"On Failure " + t.getMessage());
            }
        });
        return articles;
    }

    public List<Article> saveArticles (List<Article> articles){
        for(Article article : articles)
            article.save();
        return articles;
    }



}

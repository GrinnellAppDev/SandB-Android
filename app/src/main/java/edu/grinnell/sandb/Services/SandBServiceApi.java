package edu.grinnell.sandb.Services;


import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.SandBResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by albertowusu-asare on 3/8/16.
 */
public interface SandBServiceApi {
    //"http://www.thesandb.com/api/get_recent_posts?count=50/";
   // @GET("get_recent_posts/")
    @GET("api/get_recent_posts/")
    Call<SandBResponse> getRecentPost(@Query("count") int count);
}

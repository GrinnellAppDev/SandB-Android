package edu.grinnell.sandb.Services.Interfaces;



import edu.grinnell.sandb.Model.QueryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;



/**
 * Created by albertowusu-asare on 3/13/16.
 */
public interface SandBServiceAPI {
    //@GET("/api/get_recent_posts/")
    @GET("/rest/v1.1/sites/www.thesandb.com/posts/")
    Call<QueryResponse> posts(@Query("page") int offset, @Query("count") int count);
}
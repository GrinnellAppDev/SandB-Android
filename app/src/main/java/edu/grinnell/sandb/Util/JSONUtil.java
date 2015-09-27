package edu.grinnell.sandb.Util;

import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Model.Article;

public class JSONUtil {

    public static List<Article> parseArticleJSON(Response response) throws IOException, JSONException {
        JSONObject responseObject = new JSONObject(response.body().string());
        JSONArray posts = responseObject.getJSONArray("posts");

        List<Article> articles = new ArrayList<Article>();
        Article newArticle;
        for (int i = 0; i < posts.length(); ++i) {
            newArticle = new Article();
            JSONObject thisPost = posts.getJSONObject(i);
            newArticle.setArticleID(Integer.parseInt(thisPost.getString("id")));
            newArticle.setAuthor(thisPost.getJSONObject("author").getString("name"));
            newArticle.setBody(thisPost.getString("content"));
            newArticle.setDescription(thisPost.getString("excerpt"));
            newArticle.setTitle(thisPost.getString("title"));
            newArticle.setCategory(thisPost.getJSONArray("categories").getJSONObject(0).getString("title"));
            newArticle.setPubDate(thisPost.getString("date"));
            newArticle.setLink(thisPost.getString("url"));
            articles.add(newArticle);

        }
        return articles;
    }
}

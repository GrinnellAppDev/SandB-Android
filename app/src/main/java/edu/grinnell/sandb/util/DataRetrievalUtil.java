package edu.grinnell.sandb.util;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.grinnell.sandb.model.Article;

public class DataRetrievalUtil {
    final String TAG = DataRetrievalUtil.class.getSimpleName();
    private static final String baseUrl = "http://www.thesandb.com/api/";

    public void getRecentArticles() {
        String url = baseUrl + "get_recent_posts?count=30/";
        String[] params = {url};
        new ArticleFetchTask().execute(params);
    }

    public class ArticleFetchTask extends AsyncTask<String, Void, Integer> {

        final int SUCCESS = 0;
        final int CONNECTIVITY_PROBLEMS = 1;
        final int PARSING_PROBLEMS = 2;
        final int UNKNOWN = 3;

        public ArticleFetchTask() {}

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            String url = arg0[0];
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                JSONObject responseObject = new JSONObject(response.body().string());

                Article.deleteAll(Article.class);
                JSONArray posts = responseObject.getJSONArray("posts");

                Article newArticle;
                for (int i = 0; i < posts.length(); ++i) {
                    newArticle = new Article();
                    JSONObject thisPost = posts.getJSONObject(i);
                    newArticle.setArticleID(thisPost.getInt("id"));
                    newArticle.setAuthor(thisPost.getJSONObject("author").getString("name"));
                    newArticle.setBody(thisPost.getString("content"));
                    newArticle.setDescription(thisPost.getString("exerpt"));
                    newArticle.setTitle(thisPost.getString("title"));
                    newArticle.setCategory(thisPost.getJSONArray("catagories").getJSONObject(0).getString("title"));
                    newArticle.setPubDate(thisPost.getString("date"));
                    newArticle.setLink(thisPost.getString("url"));
                    newArticle.save();
                }
                return SUCCESS;
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return CONNECTIVITY_PROBLEMS;
            }
            catch (JSONException e1) {
                Log.e(TAG, e1.getMessage());
                return PARSING_PROBLEMS;
            }
            finally {
                return UNKNOWN;
            }
        }


        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            if (status != 0) {
                //notify user of error
            }
        }

        /* Return true if the device has a network adapter that is capable of
         * accessing the network. */
        protected boolean networkEnabled(ConnectivityManager cm) {
            NetworkInfo n = cm.getActiveNetworkInfo();
            return (n != null) && n.isConnectedOrConnecting();
        }
    }

    }

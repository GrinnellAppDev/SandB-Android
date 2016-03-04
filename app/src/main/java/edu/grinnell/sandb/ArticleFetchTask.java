package edu.grinnell.sandb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Util.BodyImageGetter;
import edu.grinnell.sandb.Util.JSONUtil;
import edu.grinnell.sandb.Util.NetworkUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by prabir on 3/4/16.
 */

    /* Task to download, parse, and save JSON article data from WordPress S&B api */
public class ArticleFetchTask extends AsyncTask<String, Void, Integer> {

    public static final String TAG = ArticleFetchTask.class.getSimpleName();

    private Context context;

    final int SUCCESS = 0;
    final int CONNECTIVITY_PROBLEMS = 1;
    final int PARSING_PROBLEMS = 2;

    public ArticleFetchTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        mTabsAdapter.setRefreshing(true);
        mUpdateInProgress = true;
    }

    @Override
    protected Integer doInBackground(String... arg0) {

        if (!NetworkUtil.isNetworkEnabled(context)) {
            return CONNECTIVITY_PROBLEMS;
        }

        String url = arg0[0];
        //See http://square.github.io/okhttp/
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            List<Article> articleList = JSONUtil.parseArticleJSON(response);

            if (!articleList.isEmpty()) {
                Article.deleteAll(Article.class);
                for (Article article : articleList) {
                    article.save();
                    //Parse out and save images from article body
                    BodyImageGetter.readImages(article);
                }
                return SUCCESS;
            } else {
                return PARSING_PROBLEMS;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return CONNECTIVITY_PROBLEMS;
        } catch (JSONException e1) {
            Log.e(TAG, e1.getMessage());
            return PARSING_PROBLEMS;
        }
    }

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        if (status != 0) {
            Toast.makeText(context, "Error Downloading Articles", Toast.LENGTH_SHORT).show();
        }
        // Clear the loading bar when the articles are loaded
        mUpdateInProgress = false;
        mTabsAdapter.setRefreshing(false);
        mTabsAdapter.refresh();
    }
}
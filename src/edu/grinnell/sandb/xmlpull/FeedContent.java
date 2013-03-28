package edu.grinnell.sandb.xmlpull;

import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;

import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.ArticleTable;

public class FeedContent {

	private static final String TAG = "FeedContent";
	
	public static List<Article> articles;
	public static boolean loading = false;
	
	private FeedContent() {
	}
	
	public static void loadCache(Context context) {
		ArticleTable table = new ArticleTable(context);
		try {
			table.open();
			articles = table.getAllArticles();
		} catch (SQLException sqle) {
			Log.e(TAG, "SqlException", sqle);
		} finally {
			table.close();
		}
	}
	
	public static void loadCacheAsync(Context context) {
		(new LoadCacheTask(context)).execute();
	}
	
	private static class LoadCacheTask extends AsyncTask<Integer, Void, Void> {

		private Context context;
		
		protected LoadCacheTask(Context context) {
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Integer... arg0) {
			loadCache(context);
			return null;
		
		}
		
	}
}

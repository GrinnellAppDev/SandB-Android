package edu.grinnell.sandb;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.ParseException;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockListActivity;

import edu.grinnell.sandb.comments.Comment;
import edu.grinnell.sandb.comments.CommentTable;
import edu.grinnell.sandb.xmlpull.CommentParseTask;

public class CommentsActivity extends SherlockListActivity {
	public static final String TAG = "CommentsActivity";
	private ArrayList<Comment> mData;
	private CommentListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		setContentView(R.layout.activity_comments);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Intent intent = getIntent();
		String feed = intent.getStringExtra(ArticleDetailFragment.FEED_LINK);

		new ParseComments().execute(feed);
	}

	private class ParseComments extends AsyncTask<String, Void, List<Comment>> {

		private CommentTable mTable;
		private Context mAppContext;

		/* Setup the loading dialog. */
		@Override
		protected void onPreExecute() {
			// begin loading animation
		}

		//not using database, for now...
		@Override
		protected List<Comment> doInBackground(String... arg0) {

			mAppContext = getApplicationContext();
			//mTable = new CommentTable(mAppContext);

			InputStream stream = downloadDataFromServer(arg0[0]);

			try {
				/*
				mTable.open();
				mTable.clearTable();
				return CommentParseTask.parseCommentsFromStream(stream, mAppContext, mTable);
				*/
				return CommentParseTask.parseCommentsFromStream(stream, mAppContext, null);
			} catch (IOException ioe) {
				Log.e(TAG, "parseCommentsFromStream", ioe);
			} catch (XmlPullParserException xppe) {
				Log.e(TAG, "parseCommentsFromStream", xppe);
			} catch (SQLException sqle) {
				Log.e(TAG, "SQLExeption", sqle);
			} catch (Exception e) {
				Log.e(TAG, "parseCommentsFromStream", e);
			} finally {
				//mTable.close();
			}
			return new ArrayList<Comment>();
		}

		/*
		 * Stop the dialog and notify the main thread that the new menu is
		 * loaded.
		 */
		@Override
		protected void onPostExecute(List<Comment> comments) {
			super.onPostExecute(comments);
			Log.i(TAG, "comments parsed!");
			// end loading animation
			mData = (ArrayList<Comment>) comments;
			fillList(mData);
		}
	}
	
	protected void fillList (List<Comment> comments) {
		
		if (mData == null)
			mData = new ArrayList<Comment>();

		mAdapter = new CommentListAdapter(
				(CommentsActivity) this,
				R.layout.comments_row, mData);
		
		//be sure to show message if no comments
        setListAdapter(mAdapter);
	}

	protected static InputStream downloadDataFromServer(String urlstr) {
		InputStream stream = null;
		try {
			URL url = new URL(urlstr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			stream = conn.getInputStream();
		} catch (IOException e) {
			Log.e(TAG, "exception: " + e.toString());
			Log.e(TAG, "message: " + e.getMessage());
		} catch (ParseException p) {
			Log.e(TAG, "ParseException: " + p.toString());
		}

		return stream;
	}
	
	
}

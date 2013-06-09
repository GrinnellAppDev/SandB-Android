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
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import edu.grinnell.sandb.comments.Comment;
import edu.grinnell.sandb.xmlpull.CommentParseTask;

public class ArticleDetailActivity extends SherlockFragmentActivity {

	public static final String DETAIL_ARGS = "detail_args";
	public static final String COMMENTS_FEED = "Comments Feed";
	public static final String TAG = "ArticleDetailActivity";

	private int mIDKey = 0;
	private String comments_feed = null;
	private ArrayList<Comment> mComments = null;

	private boolean mArticleSide = true;
	private boolean mCommentsParsed = false;

	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);

		// should set the title as the article date or something
		setTitle("");
		setContentView(R.layout.activity_article_detail);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Intent i = getIntent();

		ArticleDetailFragment fragment = new ArticleDetailFragment();
		;

		mIDKey = i.getIntExtra(ArticleDetailFragment.ARTICLE_ID_KEY, 0);
		comments_feed = i.getStringExtra(COMMENTS_FEED);

		new ParseComments().execute(comments_feed);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.article_detail_container, fragment).commit();

	}

	public int getIDKey() {
		return mIDKey;
	}

	public String getCommentsFeed() {
		return comments_feed;
	}

	public List<Comment> getComments() {
		return mComments;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent upIntent = new Intent(this, MainActivity.class);
			upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			NavUtils.navigateUpTo(this, upIntent);
			overridePendingTransition(R.anim.article_slide_in,
					R.anim.article_slide_out);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void flip() {
		if (mArticleSide) {

			if (mComments == null) {
				Toast.makeText(this, "Comments downloading...",
						Toast.LENGTH_LONG).show();
				return;
			} else if (mComments.isEmpty()) {
				Toast.makeText(this, "No Comments For this Article",
						Toast.LENGTH_LONG).show();
				return;
			}

			mArticleSide = false;

			getSupportFragmentManager()
					.beginTransaction()

					.setCustomAnimations(R.anim.card_flip_right_in,
							R.anim.card_flip_right_out,
							R.anim.card_flip_left_in, R.anim.card_flip_left_out)

					.replace(R.id.article_detail_container,
							new CommentListFragment())

					.addToBackStack(null)

					.commit();
		}

		else {
			mArticleSide = true;
			getSupportFragmentManager().popBackStack();
			return;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.article_slide_in,
				R.anim.article_slide_out);
	}

	// Still respond to swipe back gesture even if it also triggers scroll
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TouchEvent dispatcher.
		if (ArticleDetailFragment.gestureDetector != null) {
			if (ArticleDetailFragment.gestureDetector.onTouchEvent(ev))
				// If the gestureDetector handles the event, a swipe has been
				// executed and no more needs to be done.
				return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	private class ParseComments extends AsyncTask<String, Void, List<Comment>> {

		// private CommentTable mTable;
		private Context mAppContext;

		/* Setup the loading dialog. */
		@Override
		protected void onPreExecute() {
			// begin loading animation
		}

		// not using database, for now...
		@Override
		protected List<Comment> doInBackground(String... arg0) {

			mAppContext = getApplicationContext();
			// mTable = new CommentTable(mAppContext);

			InputStream stream = downloadDataFromServer(arg0[0]);

			try {
				/*
				 * mTable.open(); mTable.clearTable(); return
				 * CommentParseTask.parseCommentsFromStream(stream, mAppContext,
				 * mTable);
				 */
				return CommentParseTask.parseCommentsFromStream(stream,
						mAppContext, null);
			} catch (IOException ioe) {
				Log.e(TAG, "parseCommentsFromStream", ioe);
			} catch (XmlPullParserException xppe) {
				Log.e(TAG, "parseCommentsFromStream", xppe);
			} catch (SQLException sqle) {
				Log.e(TAG, "SQLExeption", sqle);
			} catch (Exception e) {
				Log.e(TAG, "parseCommentsFromStream", e);
			} finally {
				// mTable.close();
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
			mComments = (ArrayList<Comment>) comments;
			mCommentsParsed = true;
		}
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

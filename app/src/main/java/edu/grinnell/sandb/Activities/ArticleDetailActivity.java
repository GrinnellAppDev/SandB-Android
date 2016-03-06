package edu.grinnell.sandb.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Fragments.ArticleDetailFragment;
import edu.grinnell.sandb.Fragments.CommentListFragment;
import edu.grinnell.sandb.Model.Comment;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.VersionUtil;

/* This activity displays the text, images, and comments for a selected article */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String DETAIL_ARGS = "detail_args";
    public static final String COMMENTS_FEED = "Comments Feed";
    public static final String TAG = "ArticleDetailActivity";

    private long mIDKey = 0;
    private String comments_feed = null;
    private ArrayList<Comment> mComments = null;

    private boolean mArticleSide = true;
    private boolean mCommentsParsed = false;

    @Override
    public void onCreate(Bundle ofJoy) {
        super.onCreate(ofJoy);

        // set transition things for lollipop
        if (VersionUtil.isLollipop()) {
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }

        setTitle("");
        setContentView(R.layout.activity_article_detail);

        // setup toolbar and back navigation
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        ArticleDetailFragment fragment = new ArticleDetailFragment();
        mIDKey = i.getLongExtra(ArticleDetailFragment.ARTICLE_ID_KEY, 0);
        comments_feed = i.getStringExtra(COMMENTS_FEED);

		/* Download the comments as soon as the article is opened */
        new ParseComments().execute(comments_feed);

		/*
         * Show the article detail fragment initially(as opposed to the comments
		 * list fragment
		 */
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.article_detail_container, fragment).commit();

    }

    public long getIDKey() {
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
            // Add a smooth transition animation
            overridePendingTransition(R.anim.article_slide_in,
                    R.anim.article_slide_out);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * This is called when the "Comments" button is pressed This method will
     * flip between the comments fragment and article fragment.
     */
    public void flip() {
        // If the user is viewing the article fragment
        if (mArticleSide) {
            // Display a toast and do not flip if the comments are still
            // downloading
            if (mComments == null) {
                Toast.makeText(this, "Comments downloading...",
                        Toast.LENGTH_LONG).show();
                return;
                // Display a toast and do not flip if there are no comments for
                // the article
            } else if (mComments.isEmpty()) {
                Toast.makeText(this, "No Comments For this Article",
                        Toast.LENGTH_LONG).show();
                return;
            }

            mArticleSide = false;

            // Replace the article detail fragment with the comments fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    // must use custom library NineOldAndroids for these 3d
                    // animations
                    // to be compatable with actionbarsherlock
                    // .setCustomAnimations(R.anim.card_flip_right_in,
                    // R.anim.card_flip_right_out,
                    // R.anim.card_flip_left_in, R.anim.card_flip_left_out)
                    .replace(R.id.article_detail_container,
                            new CommentListFragment()).addToBackStack(null)
                    .commit();
        }

        // If the comments fragment is showing, pop back the stack to display
        // the article fragment
        else {
            mArticleSide = true;
            getSupportFragmentManager().popBackStack();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Return to the article list if a swipe motion is made
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

    /*
     * Async task to download the comments for an article. Unfortunately most
     * articles are not commented on. In a future update an interface to submit
     * comments must be added(github issue #2)
     */
    private class ParseComments extends AsyncTask<String, Void, List<Comment>> {

        // private CommentTable mTable;
        private Context mAppContext;

        /* Setup the loading dialog. */
        @Override
        protected void onPreExecute() {
            // begin loading animation
        }

        @Override
        protected List<Comment> doInBackground(String... arg0) {
/*
			mAppContext = getApplicationContext();

			// mTable = new CommentTable(mAppContext);

			InputStream stream = downloadDataFromServer(arg0[0]);

			try {

				return CommentParseTask.parseCommentsFromStream(stream,mAppContext, null);
			}
            catch (SQLException sqle) {
				Log.e(TAG, "SQLExeption", sqle);
			} catch (Exception e) {
				Log.e(TAG, "parseCommentsFromStream", e);
			} finally {
				// mTable.close();
			}
  */
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

    /*
     * This method will download the comments stream(in xml) from the S&B
     * website
     */
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
        } catch (Exception p) {
            Log.e(TAG, "ParseException: " + p.toString());
        }

        return stream;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "B3PJX5MJNYMNSB9XQS3P");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

}

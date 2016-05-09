package edu.grinnell.sandb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Fragments.ArticleDetailFragment;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.VersionUtil;

/* This activity displays the text, images, and comments for a selected article */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String TAG = "ArticleDetailActivity";
    public static final String COMMENTS_FEED = "Comments Feed";
    public static final int SCROLL_DOWN_THRESHOLD = 10;
    public static final int SCROLL_UP_THRESHOLD = 2;

    private long articleId = 0;
    private ArticleDetailFragment fragment;
    private String comments_feed = null;
    private List mComments = new ArrayList();
    private boolean mArticleSide = true;
    private Toolbar toolbar;
    private boolean isToolbarShown;

    @Override
    public void onCreate(Bundle ofJoy) {
        super.onCreate(ofJoy);

        // set transition things for lollipop
        if (VersionUtil.isLollipop()) {
            getWindow().setEnterTransition(new Slide());
            getWindow().setExitTransition(new Slide());
        }

        setTitle("");
        setContentView(R.layout.activity_article_detail);

        // Setup toolbar and back navigation
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        articleId = intent.getIntExtra(ArticleDetailFragment.ARTICLE_ID_KEY, 0);
        Log.d(TAG, "Article ID: " + articleId);
        comments_feed = intent.getStringExtra(COMMENTS_FEED);

        initializeFragment();

		/*
         * Show the article detail fragment initially(as opposed to the comments
		 * list fragment
		 */
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.article_detail_container, fragment).commit();

    }

    private void initializeFragment() {
        fragment = new ArticleDetailFragment();
        fragment.setArticle(articleId);
        fragment.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int dy = scrollY - oldScrollY;
                if (dy < 0 && Math.abs(dy) > SCROLL_UP_THRESHOLD && !isToolbarShown) {
                    showToolbar();
                } else if (dy > 0 && dy > SCROLL_DOWN_THRESHOLD && isToolbarShown) {
                    hideToolbar();
                }
            }
        });

        showToolbar();
    }

    public void showToolbar() {
        Log.d(TAG, "SHOW TOOLBAR");
        Animation showAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0);
        showAnimation.setDuration(Constants.TOOLBAR_ANIMATION_DURATION);
        showAnimation.setInterpolator(new DecelerateInterpolator());
        toolbar.startAnimation(showAnimation);
        getSupportActionBar().show();
        isToolbarShown = true;
    }

    public void hideToolbar() {
        Log.d(TAG, "HIDE TOOLBAR");
        Animation hideAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1);
        hideAnimation.setDuration(Constants.TOOLBAR_ANIMATION_DURATION);
        hideAnimation.setInterpolator(new AccelerateInterpolator());
        toolbar.startAnimation(hideAnimation);
        getSupportActionBar().hide();
        isToolbarShown = false;
    }


    public long getArticleId() {
        return articleId;
    }

    public String getCommentsFeed() {
        return comments_feed;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /*
     * This is called when the "Comments" button is pressed This method will
     * flip between the comments fragment and article fragment.
     */
    public void flip() {
        // TODO: 5/9/16 Handle comments
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

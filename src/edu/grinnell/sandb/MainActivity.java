package edu.grinnell.sandb;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.xmlpull.FeedContent;
import edu.grinnell.sandb.xmlpull.XMLParseTask.Article;
import edu.grinnell.sandb.xmlpull.XmlPullReceiver;
import edu.grinnell.sandb.xmlpull.XmlPullService;

public class MainActivity extends FragmentActivity implements ArticleListFragment.Callbacks {
	
	private static final String TAG = "MainActivity";
	
	private PendingIntent mSendFeedLoaded;
	private ArticleListFragment mListFrag;
	
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListFrag = (ArticleListFragment) 
				getSupportFragmentManager().findFragmentById(R.id.articles_fragment);
		
		if (FeedContent.articles != null && FeedContent.articles.size() > 0) {
			Log.d(TAG, "Content alreay exits..");
			mListFrag.update();
		} else {
			Log.d(TAG, "Perparing to start service..");
			Intent feedLoaded = new Intent();
			feedLoaded.setAction(XmlPullReceiver.FEED_PROCESSED);
			mSendFeedLoaded = PendingIntent.getBroadcast(this, 0, feedLoaded, 0);
			Intent loadFeed = new Intent(this, XmlPullService.class);
			loadFeed.setAction(XmlPullService.DOWNLOAD_FEED);
			loadFeed.putExtra(XmlPullService.RESULT_ACTION, mSendFeedLoaded);
			startService(loadFeed);
		}
		
	}

	@Override
	protected void onNewIntent(Intent i) {
		String action = i.getAction();
		if (ArticleListFragment.REFRESH.equals(action)) {
			mListFrag.update();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mSendFeedLoaded != null) 
			mSendFeedLoaded.cancel();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onItemSelected(int position) {
		if(mTwoPane) {
			// add two pane layout
		} else {
			Intent detailIntent = new Intent(this, ArticleDetailActivity.class);
            detailIntent.putExtra(ArticleDetailFragment.ARTICLE_ID_KEY, position);
            startActivity(detailIntent);
		}		
	}

	@Override
	public void setListActivateState() {
		// TODO Auto-generated method stub
		
	}

}

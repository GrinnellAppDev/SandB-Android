package edu.grinnell.sandb;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.xmlpull.FeedContent;
import edu.grinnell.sandb.xmlpull.XmlPullReceiver;
import edu.grinnell.sandb.xmlpull.XmlPullService;

public class MainActivity extends FragmentActivity implements ArticleListFragment.Callbacks {
	
	private static final String TAG = "MainActivity";
	
	private PendingIntent mSendFeedLoaded;
	private ArticleListFragment mListFrag;
	private View mLoading;
	
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLoading = (View) findViewById(R.id.loading);
		mListFrag = (ArticleListFragment) 
				getSupportFragmentManager().findFragmentById(R.id.articles_fragment);
		
		if (FeedContent.articles != null && FeedContent.articles.size() > 0) {
			Log.d(TAG, "Content alreay exits..");
			if (!FeedContent.loading) mLoading.setVisibility(View.GONE);
			mListFrag.update();
		} else {
			// Loading View
			Log.d(TAG, "Perparing to start service..");
			mLoading.setVisibility(View.VISIBLE);
			startXmlPullService();
		}
		
	}

	private void startXmlPullService() {
		Intent feedLoaded = new Intent();
		feedLoaded.setAction(XmlPullReceiver.FEED_PROCESSED);
		mSendFeedLoaded = PendingIntent.getBroadcast(this, 0, feedLoaded, 0);
		Intent loadFeed = new Intent(this, XmlPullService.class);
		loadFeed.setAction(XmlPullService.DOWNLOAD_FEED);
		loadFeed.putExtra(XmlPullService.RESULT_ACTION, mSendFeedLoaded);
		startService(loadFeed);
	}
	
	@Override
	protected void onNewIntent(Intent i) {
		String action = i.getAction();
		Log.d(TAG, "onNewIntent called | " + action);

		if (ArticleListFragment.UPDATE.equals(action)) {
			mListFrag.update();
			mLoading.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//if (mSendFeedLoaded != null) 
		//	mSendFeedLoaded.cancel();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			mLoading.setVisibility(View.VISIBLE);
			this.startXmlPullService();
			break;
		case R.id.menu_settings:
			// startActivityForResult(new Intent(this, PrefActiv.class), PREFS);
			break;
		default:
			
			break;
		}

		return false;
	}
	
	@Override
	public void onItemSelected(int position) {
		if(mTwoPane) {
			// add two pane layout
		} else {
			if (mSendFeedLoaded != null) mSendFeedLoaded.cancel();
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

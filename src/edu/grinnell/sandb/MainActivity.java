package edu.grinnell.sandb;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.xmlpull.XmlPullReceiver;
import edu.grinnell.sandb.xmlpull.XmlPullService;

public class MainActivity extends SherlockFragmentActivity implements ArticleListFragment.Callbacks {
	
	private static final String TAG = "MainActivity";
	private static final String SELECTED_TAB = "selected_tab";
	
	private PendingIntent mSendFeedLoaded;
	private ArticleListFragment mListFrag;
	private View mLoading;
	
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLoading = (View) findViewById(R.id.loading);
		
	    // setup action bar for tabs
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    addTabs(actionBar);
	    
		
		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB));
		}
		
	    //TODO
//		Log.d(TAG, "Perparing to start service..");
//		mLoading.setVisibility(View.VISIBLE);
//		startXmlPullService();
	    
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
			findViewById(android.R.id.content).invalidate();
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
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
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
		}		
	}

	@Override
	public void setListActivateState() {
		// TODO Auto-generated method stub
		
	}
	
	private void addTabs(ActionBar actionBar) {
		
	    Tab tab = actionBar.newTab()
	            .setText(R.string.all)
	            .setTabListener(new TabListener<ArticleListFragment>(
	                    this, "all", ArticleListFragment.class));
	    actionBar.addTab(tab);
	    
	    tab = actionBar.newTab()
	            .setText(R.string.arts)
	            .setTabListener(new TabListener<ArticleListFragment>(
	                    this, "arts", ArticleListFragment.class));
	    actionBar.addTab(tab);
	    
	    tab = actionBar.newTab()
	            .setText(R.string.sports)
	            .setTabListener(new TabListener<ArticleListFragment>(
	                    this, "sports", ArticleListFragment.class));
	    actionBar.addTab(tab);
	    
	    tab = actionBar.newTab()
	            .setText(R.string.community)
	            .setTabListener(new TabListener<ArticleListFragment>(
	                    this, "community", ArticleListFragment.class));
	    actionBar.addTab(tab);
	    
	    tab = actionBar.newTab()
	            .setText(R.string.opinion)
	            .setTabListener(new TabListener<ArticleListFragment>(
	                    this, "opinion", ArticleListFragment.class));
	    actionBar.addTab(tab);

	    tab = actionBar.newTab()
	        .setText(R.string.features)
	        .setTabListener(new TabListener<ArticleListFragment>(
	                this, "features", ArticleListFragment.class));
	    actionBar.addTab(tab);
	}
	
	public class TabListener<T extends Fragment> implements ActionBar.TabListener {
	    private ArticleListFragment mFragment;
	    private final Activity mActivity;
	    private final String mTag;
	    private final Class<T> mClass;

	    /** Constructor used each time a new tab is created.
	      * @param activity  The host Activity, used to instantiate the fragment
	      * @param tag  The identifier tag for the fragment
	      * @param clz  The fragment's Class, used to instantiate the fragment
	      */
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }

	    /* The following are each of the ActionBar.TabListener callbacks */
	    @Override
	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	            mFragment = (ArticleListFragment) Fragment.instantiate(mActivity, mClass.getName());
	            Bundle args = new Bundle();
	            args.putString(ArticleListFragment.ARTICLE_CATEGORY_KEY, tab.getText().toString());
	            mFragment.setArguments(args);
	            ft.replace(R.id.content, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	        MainActivity.this.mListFrag = this.mFragment;
	    }

	    @Override
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    @Override
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
			
	}
	
    @Override
    public void onSaveInstanceState(Bundle state)
    {
        state.putInt(SELECTED_TAB, getSupportActionBar().getSelectedNavigationIndex());
        super.onSaveInstanceState(state);
    }
    
}

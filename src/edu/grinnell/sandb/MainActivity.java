package edu.grinnell.sandb;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.crittercism.app.Crittercism;

import edu.grinnell.sandb.xmlpull.XmlPullReceiver;
import edu.grinnell.sandb.xmlpull.XmlPullService;

/* The main activity that the app will initialize to. This activity hosts the ArticleListFragment */
public class MainActivity extends SherlockFragmentActivity implements ArticleListFragment.Callbacks {
	
	private static final String TAG = "MainActivity";
	private static final String SELECTED_TAB = "selected_tab";
	
	private PendingIntent mSendFeedLoaded;
	private ArticleListFragment mListFrag;
	private View mLoading;
	private ImageView mLoadingImage;
	private ViewPager mPager;
	private TabsAdapter mTabsAdapter;
	
	private boolean mUpdateInProgress;
	private BroadcastReceiver mUpdateReceiver;
	
	private MainPrefs mPrefs;
	
	private boolean mTwoPane = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Initialize Crittercism with the application key..
		Crittercism.init(getApplicationContext(), "5183e0d7c463c23fa4000019");
		
		mPrefs = new MainPrefs(this);
		
		mLoading = (View) findViewById(R.id.loading);
		mLoadingImage = (ImageView) mLoading.findViewById(R.id.spinner);
		
	    // setup action bar for tabs
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    //setup a pager to scroll between catagory tabs
	    mPager = (ViewPager) findViewById(R.id.pager);
	    mTabsAdapter = new TabsAdapter(getSupportFragmentManager(), mPager);
	    addTabs(actionBar, mTabsAdapter);
	    
		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(SELECTED_TAB));
		}

		//Notify the app when the feed data has been downloaded
		mUpdateReceiver = new XmlPullReceiver();
		registerReceiver(mUpdateReceiver, new IntentFilter(XmlPullReceiver.FEED_PROCESSED));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mPrefs.refresh();
		registerReceiver(mUpdateReceiver, new IntentFilter(XmlPullReceiver.FEED_PROCESSED));
		//If this is the first time the app is run, download the feed
		if (mPrefs.firstRun) {
			//Show a spinner while the list is loading
			if (!mUpdateInProgress) {
				mLoading.setVisibility(View.VISIBLE);
				mLoadingImage.startAnimation(AnimationUtils.loadAnimation(this,
						R.anim.loading));
				mUpdateInProgress = true;
			}
			startXmlPullService(true);
		} else {
			startXmlPullService(false);
		}
	}
	
	//Start a service to download the 
	private void startXmlPullService(boolean forced) {
		Intent loadFeed = new Intent(this, XmlPullService.class);
		Intent feedLoaded = new Intent();
		feedLoaded.setAction(XmlPullReceiver.FEED_PROCESSED);
		mSendFeedLoaded = PendingIntent.getBroadcast(this, 0, feedLoaded, PendingIntent.FLAG_UPDATE_CURRENT);
		loadFeed.putExtra(XmlPullService.RESULT_ACTION, mSendFeedLoaded);
		
		if (forced) {
			loadFeed.setAction(XmlPullService.DOWNLOAD_FEED);
		} else {
			loadFeed.putExtra(XmlPullService.LAST_CHECKED_MS, mPrefs.lastUpdated);
			loadFeed.setAction(XmlPullService.CHECK_DOWNLOAD);
		}
		startService(loadFeed);
	}
	
	@Override
	protected void onNewIntent(Intent i) {
		String action = i.getAction();
		Log.i(TAG, "onNewIntent called | " + action);

		//Clear the loading bar when the articles are loaded
		if (ArticleListFragment.UPDATE.equals(action)) {
			if (mUpdateInProgress) {
				mUpdateInProgress = false;
				mLoading.setVisibility(View.GONE);
			}
			mTabsAdapter.refresh();
		}
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mUpdateReceiver);
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
			if (!mUpdateInProgress) {
				//Reload the feed when the refresh button is pressed
				mLoading.setVisibility(View.VISIBLE);
				mLoadingImage.startAnimation(AnimationUtils.loadAnimation(this,
						R.anim.loading));
				mUpdateInProgress = true; 
				this.startXmlPullService(true);
			}	
			break;
//		case R.id.menu_settings:
//			// startActivityForResult(new Intent(this, PrefActiv.class), PREFS);
//			break;
		default:
			
			break;
		}

		return false;
	}
	
	@Override
	public void onItemSelected(int position) {
		if(mTwoPane) {
			//TODO add two pane layout
		} else {
			if (mSendFeedLoaded != null) mSendFeedLoaded.cancel();
		}		
	}

	@Override
	public void setListActivateState() {		
	}
	
	//Add the catagory tabs
	private void addTabs(ActionBar actionBar, TabsAdapter ta) {    
        for (String category : ArticleListFragment.CATEGORIES) {
        	Bundle args = new Bundle();
            args.putString(ArticleListFragment.ARTICLE_CATEGORY_KEY, category);
            Tab tab = actionBar.newTab()
    	            .setText(category);
    	    ta.addTab(tab, ArticleListFragment.class, args);
        }
	}
	
	/* FragmentPagerAdapter to handle the catagory tabs */
	public class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {

		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;
		
			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(FragmentManager fm, ViewPager pager) {
			super(fm);
			mActionBar = getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			mListFrag = (ArticleListFragment) 
				Fragment.instantiate(MainActivity.this, 
						info.clss.getName(),
						info.args);	
			return mListFrag;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
        public void onPageSelected(int position)
        {
           mActionBar.getTabAt(position).select();
           ViewParent root = findViewById(android.R.id.content).getParent();
           findAndUpdateSpinner(root, position);
        }

        /**
         * Searches the view hierarchy excluding the content view 
         * for a possible Spinner in the ActionBar. 
         * 
         * @param root The parent of the content view
         * @param position The position that should be selected
         * @return if the spinner was found and adjusted
         */
        private boolean findAndUpdateSpinner(Object root, int position)
        {
           if (root instanceof android.widget.Spinner)
           {
              // Found the Spinner
              Spinner spinner = (Spinner) root;
              spinner.setSelection(position);
              return true;
           }
           else if (root instanceof ViewGroup)
           {
              ViewGroup group = (ViewGroup) root;
              if (group.getId() != android.R.id.content)
              {
                 // Found a container that isn't the container holding our screen layout
                 for (int i = 0; i < group.getChildCount(); i++)
                 {
                    if (findAndUpdateSpinner(group.getChildAt(i), position))
                    {
                       // Found and done searching the View tree
                       return true;
                    }
                 }
              }
           }
           // Nothing found
           return false;
        }
		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
		
		public void refresh() {
			for (int i = 0; i < getCount(); i++) {
				Fragment f = getSupportFragmentManager().findFragmentByTag(
						getFragmentTag(i));
				if (f != null)
					((ArticleListFragment) f).update();
			}
		}
		
		private String getFragmentTag(int pos){
		    return "android:switcher:"+R.id.pager+":"+pos;
		}
	}
	
    @Override
    public void onSaveInstanceState(Bundle state)
    {
        state.putInt(SELECTED_TAB, getSupportActionBar().getSelectedNavigationIndex());
        super.onSaveInstanceState(state);
    }
}

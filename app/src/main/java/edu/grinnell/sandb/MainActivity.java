package edu.grinnell.sandb;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.model.Article;
import edu.grinnell.sandb.util.BodyImageGetter;
import edu.grinnell.sandb.util.JSONUtil;

/* The main activity that the app will initialize to. This activity hosts the ArticleListFragment */
public class MainActivity extends ActionBarActivity {

	private static final String TAG = "MainActivity";
	private static final String SELECTED_TAB = "selected_tab";

	private ArticleListFragment mListFrag;
	private View mLoading;
	private ViewPager mPager;
	private TabsAdapter mTabsAdapter;

	private boolean mUpdateInProgress;

	private boolean mTwoPane = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_main);

		mLoading = (View) findViewById(R.id.loading);

		// setup action bar for tabs
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// setup a pager to scroll between catagory tabs
		mPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(getSupportFragmentManager(), mPager);
		addTabs(actionBar, mTabsAdapter);

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState
					.getInt(SELECTED_TAB));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


    @Override
    protected void onPause() {
        super.onPause();

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
                if (!mUpdateInProgress) {
                    updateArticles();
                }
                break;
            default:

                break;
        }

        return false;
    }

    public void updateArticles() {
        if (!mUpdateInProgress) {
            String url = "http://www.thesandb.com/api/get_recent_posts?count=50/";
            String[] params = {url};
            mTabsAdapter.setRefreshing(true);
            mUpdateInProgress = true;
            new ArticleFetchTask().execute(params);
        }
    }

    public class ArticleFetchTask extends AsyncTask<String, Void, Integer> {

        final int SUCCESS = 0;
        final int CONNECTIVITY_PROBLEMS = 1;
        final int PARSING_PROBLEMS = 2;

        public ArticleFetchTask() {}

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... arg0) {

            String url = arg0[0];
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
                        BodyImageGetter.readImages(article);
                    }
                    return SUCCESS;
                }
                else {
                    return PARSING_PROBLEMS;
                }
            }
            catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return CONNECTIVITY_PROBLEMS;
            }
            catch (JSONException e1) {
                Log.e(TAG, e1.getMessage());
                return PARSING_PROBLEMS;
            }
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            if (status != 0) {
                //notify user of error
            }
            // Clear the loading bar when the articles are loaded
            if (mUpdateInProgress) {
                mUpdateInProgress = false;
                }
            mTabsAdapter.setRefreshing(false);
            mTabsAdapter.refresh();
            }
        }

        /* Return true if the device has a network adapter that is capable of
         * accessing the network. */
        protected boolean networkEnabled(ConnectivityManager cm) {
            NetworkInfo n = cm.getActiveNetworkInfo();
            return (n != null) && n.isConnectedOrConnecting();
        }

	// Add the catagory tabs
	private void addTabs(ActionBar actionBar, TabsAdapter ta) {
		for (String category : ArticleListFragment.CATEGORIES) {
			Bundle args = new Bundle();
			args.putString(ArticleListFragment.ARTICLE_CATEGORY_KEY, category);
			ActionBar.Tab tab = actionBar.newTab().setText(category);
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
			mListFrag = (ArticleListFragment) Fragment.instantiate(
					MainActivity.this, info.clss.getName(), info.args);
			return mListFrag;
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			mActionBar.getTabAt(position).select();
			ViewParent root = findViewById(android.R.id.content).getParent();
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
		}

		public void refresh() {
			for (int i = 0; i < getCount(); i++) {
				Fragment f = getSupportFragmentManager().findFragmentByTag(
						getFragmentTag(i));
				if (f != null)
					((ArticleListFragment) f).update();
			}
		}

		private String getFragmentTag(int pos) {
			return "android:switcher:" + R.id.pager + ":" + pos;
		}

        public void setRefreshing(boolean refreshing) {
            for (int i = 0; i < getCount(); i++) {
                Fragment f = getSupportFragmentManager().findFragmentByTag(
                        getFragmentTag(i));
                if (f != null)
                    ((ArticleListFragment) f).setRefreshing(refreshing);
            }
        }
    }


	@Override
	public void onSaveInstanceState(Bundle state) {
		state.putInt(SELECTED_TAB, getSupportActionBar()
				.getSelectedNavigationIndex());
		super.onSaveInstanceState(state);
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

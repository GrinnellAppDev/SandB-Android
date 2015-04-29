package edu.grinnell.sandb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
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
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.grinnell.sandb.model.Article;
import edu.grinnell.sandb.model.Image;
import edu.grinnell.sandb.util.BodyImageGetter;

/* The main activity that the app will initialize to. This activity hosts the ArticleListFragment */
public class MainActivity extends ActionBarActivity implements
		ArticleListFragment.Callbacks {

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
		Crashlytics.start(this);
		setContentView(R.layout.activity_main);

		mPrefs = new MainPrefs(this);

		mLoading = (View) findViewById(R.id.loading);
		mLoadingImage = (ImageView) mLoading.findViewById(R.id.spinner);

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

        updateArticles();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

    public void updateArticles() {
        String url = "http://www.thesandb.com/api/get_recent_posts?count=30/";
        String[] params = {url};
        new ArticleFetchTask().execute(params);
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
                JSONObject responseObject = new JSONObject(response.body().string());

                Article.deleteAll(Article.class);
                Image.deleteAll(Image.class);
                JSONArray posts = responseObject.getJSONArray("posts");

                Article newArticle;
                for (int i = 0; i < posts.length(); ++i) {
                    newArticle = new Article();
                    JSONObject thisPost = posts.getJSONObject(i);
                    newArticle.setArticleID(Integer.parseInt(thisPost.getString("id")));
                    newArticle.setAuthor(thisPost.getJSONObject("author").getString("name"));
                    newArticle.setBody(thisPost.getString("content"));
                    newArticle.setDescription(thisPost.getString("excerpt"));
                    newArticle.setTitle(thisPost.getString("title"));
                    newArticle.setCategory(thisPost.getJSONArray("categories").getJSONObject(0).getString("title"));
                    newArticle.setPubDate(thisPost.getString("date"));
                    newArticle.setLink(thisPost.getString("url"));
                    newArticle.save();
                    BodyImageGetter.readImages(newArticle);
                }
                return SUCCESS;
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
                    mLoading.setVisibility(View.GONE);
                }
            mTabsAdapter.refresh();
            }
        }

        /* Return true if the device has a network adapter that is capable of
         * accessing the network. */
        protected boolean networkEnabled(ConnectivityManager cm) {
            NetworkInfo n = cm.getActiveNetworkInfo();
            return (n != null) && n.isConnectedOrConnecting();
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
				// Reload the feed when the refresh button is pressed
				mLoading.setVisibility(View.VISIBLE);
				mLoadingImage.startAnimation(AnimationUtils.loadAnimation(this,
						R.anim.loading));
				mUpdateInProgress = true;
				updateArticles();
			}
			break;
		// case R.id.menu_settings:
		// // startActivityForResult(new Intent(this, PrefActiv.class), PREFS);
		// break;
		default:

			break;
		} 

		return false;
	}

	@Override
	public void onItemSelected(int position) {
		if (mTwoPane) {
			// TODO add two pane layout
		} else {
			if (mSendFeedLoaded != null)
				mSendFeedLoaded.cancel();
		}
	}

	@Override
	public void setListActivateState() {
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
			findAndUpdateSpinner(root, position);
		}

		/**
		 * Searches the view hierarchy excluding the content view for a possible
		 * Spinner in the ActionBar.
		 * 
		 * @param root
		 *            The parent of the content view
		 * @param position
		 *            The position that should be selected
		 * @return if the spinner was found and adjusted
		 */
		private boolean findAndUpdateSpinner(Object root, int position) {
			if (root instanceof android.widget.Spinner) {
				// Found the Spinner
				Spinner spinner = (Spinner) root;
				spinner.setSelection(position);
				return true;
			} else if (root instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) root;
				if (group.getId() != android.R.id.content) {
					// Found a container that isn't the container holding our
					// screen layout
					for (int i = 0; i < group.getChildCount(); i++) {
						if (findAndUpdateSpinner(group.getChildAt(i), position)) {
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

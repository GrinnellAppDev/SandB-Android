package edu.grinnell.sandb.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.grinnell.sandb.ArticleFetchTask;
import edu.grinnell.sandb.Fragments.ArticleListFragment;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.BodyImageGetter;
import edu.grinnell.sandb.Util.JSONUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/* The main activity that the app will initialize to. This activity hosts the ArticleListFragment */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String SELECTED_TAB = "selected_tab";

    private ArticleListFragment mListFrag;
    private Menu mMenu;
    private ViewPager mPager;
    private TabsAdapter mTabsAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private boolean mUpdateInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        // setup action bar for tabs
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // setup navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, ));
        mDrawerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // setup a pager to scroll between catagory tabs
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager(), mPager);

        // setup the sliding tab strip
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
        tabs.setDividerColorResource(R.color.gdarkred);
        addTabs(actionBar, mTabsAdapter);

        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState
                    .getInt(SELECTED_TAB));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "B3PJX5MJNYMNSB9XQS3P");
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
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mMenu = menu;
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
            new ArticleFetchTask().execute(params);
        }
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
                try {
                    Fragment f = getSupportFragmentManager().findFragmentByTag(
                            getFragmentTag(i));
                    ((ArticleListFragment) f).setRefreshing(refreshing);
                } catch (NullPointerException e1) {
                    Log.e(TAG, "Tried to Access A Null Fragment: " + e1.getMessage());
                }
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(SELECTED_TAB, getSupportActionBar()
                .getSelectedNavigationIndex());
        super.onSaveInstanceState(state);
    }

}

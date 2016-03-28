package edu.grinnell.sandb.Activities;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.astuetz.PagerSlidingTabStrip;
import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.DialogSettings;
import edu.grinnell.sandb.Fragments.ArticleListFragment;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Util.VersionUtil;

/**
 * Main Activity for the application. This activity is the host for the ArticleList fragments.
 * <p> The activity also listens for any updates from the initial remote call to fetch data
 * to the local cache. </p>
 *
 * @see Observer
 * @see NetworkClient
 * @see edu.grinnell.sandb.Activities.MainActivity.TabsAdapter
 * @see ArticleListFragment
 */
public class MainActivity extends AppCompatActivity implements Observer{

    //Fields
    private static final String SELECTED_TAB = "selected_tab";
    private ViewPager mPager;
    private TabsAdapter mTabsAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CoordinatorLayout mCoordinatorLayout;
    NetworkClient networkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        initializeNetworkClient();
        setLollipopTransitions();
        setUpToolBar();
        setUpNavigationDrawer();
        slidingTabStripConfig(savedInstanceState);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                new DialogSettings(MainActivity.this).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(SELECTED_TAB, mPager.getCurrentItem());
        super.onSaveInstanceState(state);
    }

    @Override
    public void update(Observable observable, Object data) {
    // TODO : Implement Action to take upon observing data changepull Navigation drawer list items?
    }

    /*
        Custom FragmentPagerAdapter to handle the category tabs
     */
    public class TabsAdapter extends FragmentStatePagerAdapter {
        private final ArrayList<ArticleListFragment> fragments = new ArrayList<>();

        //  Constructor
        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(ArticleListFragment fragment){
            this.fragments.add(fragment);
        }

        @Override
        public int getCount() {
           return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Constants.CATEGORIES[position];
        }

        @Override
        public int getItemPosition(Object item){
            return POSITION_NONE;
        }
    }


    /**
     * Gets the base view for the main activity.
     *
     * This is useful when implementing SnackBar Messages to the base view from outside of the
     * main activity.
     * @return
     */
    public View getRootView(){
        return mCoordinatorLayout;
    }



    /* Private Methods */
    private void initializeNetworkClient(){
        this.networkClient = new NetworkClient();
        this.networkClient.addObserver(this);
        this.networkClient.deleteLocalCache();//TODO : Must we delete the cache upon start up?
    }

    private void setLollipopTransitions() {
        if (VersionUtil.isLollipop()) {
            Window window = getWindow();
            window.setExitTransition(new Fade());
            window.setEnterTransition(new Fade());
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        setSupportActionBar(toolbar);
    }

    private void setUpNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, Constants.CATEGORIES));
        // R.layout.drawer_list_item, ArticleListFragment.CATEGORIES));
        mDrawerList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                mDrawerList.setSelection(position);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                // listener to make sure view pager changes only after drawer is fully closed
                mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                        });
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        mPager.setCurrentItem(position, true);
                    }
                });

            }
        });
    }


    private void slidingTabStripConfig(Bundle savedInstanceState) {
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        addFragments(mTabsAdapter);
        mPager.setAdapter(mTabsAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mDrawerList.setItemChecked(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Setup the sliding tab strip
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);

        if (savedInstanceState != null) {
            // if resuming to a page from before
            int pos = savedInstanceState.getInt(SELECTED_TAB);
            mPager.setCurrentItem(pos, false);
            mDrawerList.setItemChecked(pos, true);
        } else {
            // start off at the 'All' page
            mPager.setCurrentItem(0, false);
            mDrawerList.setItemChecked(0, true);

        }
    }

    /* Creates "Constants.CATEGORIES.length" number of fragments to be attached to the tabs adapter*/
    private void addFragments(TabsAdapter ta){
        for(String category :Constants.CATEGORIES){
            ArticleListFragment fragment = ArticleListFragment.newInstance(networkClient,category);
            ta.addFragment(fragment);
        }
    }

}

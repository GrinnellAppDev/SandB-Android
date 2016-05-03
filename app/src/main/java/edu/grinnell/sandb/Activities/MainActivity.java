package edu.grinnell.sandb.Activities;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.DialogSettings;
import edu.grinnell.sandb.Fragments.ArticleListFragment;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Implementation.SyncMessage;
import edu.grinnell.sandb.Util.VersionUtil;

import static edu.grinnell.sandb.Constants.CATEGORIES;

/**
 * Main Activity for the application. This activity is the host for the ArticleList fragments.
 * <p/>
 * <p> The class listens for any updates from remote client upon a call  to fetch remote
 * data.</p>
 *
 * @see Observer
 * @see NetworkClient
 */
public class MainActivity extends AppCompatActivity implements Observer {

    //Fields

    private static final String TAG = "MainActivity";
    private static final String SELECTED_CATEGORY = "selected_category";
    private ArticleListFragment articleListFragment;
    private int currentCategory;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private CoordinatorLayout coordinatorLayout;
    private boolean updateInProgress;
    private NetworkClient networkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        //Coordinator layout reference for use by SnackBar
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        initializeNetworkClient();
        setLollipopTransitions();
        setUpToolBar();
        setUpNavigationDrawer();
        initArticleListFragment(savedInstanceState);
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
        state.putInt(SELECTED_CATEGORY, currentCategory);
        super.onSaveInstanceState(state);
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.i("Main Activity", "Message reached main activity");

        SyncMessage message = (SyncMessage) data;

        if (message != null) {
            if (message.getUpdateType().equals(Constants.UpdateType.REFRESH)
                    && articleListFragment.getCategory().equals(message.getCategory())) {
                Log.i("Main Activity", "Active Fragment " + message.getCategory() + " Refreshing..");
                articleListFragment.refreshList((List<RealmArticle>) message.getMessageData());
            }
        }
        // Get current fragment
        if (message != null && message.getMessageData() != null) {

            // activeFragment.update();
            // Ask fragment to update its list
            // activeFragment.update(message.getMessageData());
            //Log.i("Main Activity", "Message contents " + message.getMessageData().size());
        }
    }

    public NetworkClient getNetworkClient() {
        return this.networkClient;
    }

    /**
     * Change the fragment contents to the given category.
     * Creates a new {@code ArticleListFragment} and replaces the old one
     *
     * @param category for the articles
     */
    public void switchToFragment(int category) {
        articleListFragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARTICLE_CATEGORY_KEY, CATEGORIES[category]);
        articleListFragment.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, articleListFragment);
        ft.addToBackStack(null);
        ft.commit();

        drawerListView.setItemChecked(category, true);
    }


    private void initializeNetworkClient() {
        this.networkClient = new NetworkClient();
        this.networkClient.addObserver(this);
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
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        setSupportActionBar(toolbar);
    }

    private void setUpNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);
        drawerListView.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, CATEGORIES));
        drawerListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                drawerListView.setSelection(position);
                drawerLayout.closeDrawer(GravityCompat.START);
                // listener to make sure view pager changes only after drawer is fully closed
                drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {

                        });
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        currentCategory = position;
                        switchToFragment(currentCategory);
                    }
                });

            }
        });
    }


    private void initArticleListFragment(Bundle savedInstanceState) {
        int category = 0; // load 'All' category by default
        if (savedInstanceState != null)
            category = savedInstanceState.getInt(SELECTED_CATEGORY);
        switchToFragment(category);
    }

}

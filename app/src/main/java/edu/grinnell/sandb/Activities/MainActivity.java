package edu.grinnell.sandb.Activities;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
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

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.util.List;

import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.DialogSettings;
import edu.grinnell.sandb.Fragments.ArticleListFragment;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Util.VersionUtil;

import static edu.grinnell.sandb.Constants.CATEGORIES;

/**
 * The main activity that the app will initialize to.
 * This activity hosts the ArticleListFragment
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String SELECTED_CATEGORY = "selected_category";
    private ArticleListFragment articleListFragment;
    private int currentCategory;
    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private CoordinatorLayout coordinatorLayout;
    private boolean updateInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);

        //Coordinator layout reference for use by SnackBar
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        // set transition things for lollipop
        if (VersionUtil.isLollipop()) {
            Window window = getWindow();
            window.setExitTransition(new Fade());
            window.setEnterTransition(new Fade());
        }

        //UI configurations
        setUpToolBar();
        setUpNavigationDrawer();
        initArticleListFragment(savedInstanceState);
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
        args.putString(ArticleListFragment.ARTICLE_CATEGORY_KEY, CATEGORIES[category]);
        articleListFragment.setArguments(args);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, articleListFragment);
        ft.addToBackStack(null);
        ft.commit();

        drawerListView.setItemChecked(category, true);
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

    /*
        Update all articles
     */
    public void updateArticles() {
        //TODO: Refactor updateArticles to use HttpClient service calls
        if (!updateInProgress) {
            String[] params = {Constants.JSON_API_URL};
            /*
            ArticleFetchTask task = new ArticleFetchTask(getApplicationContext()) {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mTabsAdapter.setRefreshing(true);
                    updateInProgress = true;
                }

                @Override
                protected void onPostExecute(Integer status) {
                    super.onPostExecute(status);
                    if (status != 0) {
                        Snackbar.make(coordinatorLayout, "Error downloading articles", Snackbar.LENGTH_LONG).show();
                    }
                    // Clear the loading bar when the articles are loaded
                    updateInProgress = false;
                    mTabsAdapter.setRefreshing(false);
                    mTabsAdapter.refresh();
                    Snackbar.make(coordinatorLayout, "Articles updated", Snackbar.LENGTH_SHORT).show();
                }
            };
            task.execute(params);

            */

            NetworkClient nc = new NetworkClient() {
                @Override
                public void onArticlesRetrieved(List<Article> articles) {
                    updateInProgress = false;
                    Snackbar.make(coordinatorLayout, "Articles updated", Snackbar.LENGTH_SHORT).show();

                }
            };
            nc.updateLocalCache();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle state) {
        state.putInt(SELECTED_CATEGORY, currentCategory);
        super.onSaveInstanceState(state);
    }

}

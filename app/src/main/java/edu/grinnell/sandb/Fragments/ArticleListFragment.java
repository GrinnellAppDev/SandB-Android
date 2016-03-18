package edu.grinnell.sandb.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Adapters.ArticleRecyclerViewAdapter;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Implementation.ORMDbClient;
import edu.grinnell.sandb.Services.Interfaces.LocalCacheClient;
import edu.grinnell.sandb.Util.DatabaseUtil;
import edu.grinnell.sandb.Util.NetworkUtil;

/*
    Custom Fragment to show the list of all Articles
 */
public class ArticleListFragment extends Fragment {
    //Fields
    public static String ARTICLE_CATEGORY_KEY = "category";
    public static final Map<String, String> titleToKey = new LinkedHashMap<String, String>(); // LinkedHashMap retains insertion ordering
    public static final String[] CATEGORIES;
    public MainActivity mActivity;
    public String mCategory;
    public static final String UPDATE = "edu.grinnell.sandb.UPDATE";
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private RecyclerView mRecyclerView;
    private ArticleRecyclerViewAdapter mAdapter;
    // private List<Article> mData;
    private SwipeRefreshLayout pullToRefresh;
    private static final String TAG = "ArticleListFragment";

    // TODO: TESTING NEW NETWORK STUFF
    List<Article> TEST_DATA;
    NetworkClient mNetworkClient;
    LocalCacheClient mLocalClient;

    // Fill in the a map to correspond to section tabs for the article list
    static {
        titleToKey.put("All", null);
        titleToKey.put("News", "News");
        titleToKey.put("Arts", "Arts");
        titleToKey.put("Community", "Community");
        titleToKey.put("Features", "Features");
        titleToKey.put("Opinion", "Opinion");
        titleToKey.put("Sports", "Sports");
        CATEGORIES = titleToKey.keySet().toArray(new String[titleToKey.size()]);
    }


    //Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle b = getArguments();

        mCategory = null;
        if (b != null)
            mCategory = titleToKey.get(b.getString(ARTICLE_CATEGORY_KEY));

        // Retrieve the articles for the selected category
        //mData = loadDataFromCache(mCategory);
        Log.i(TAG, "Loading data for the '" + mCategory + "' category..");

        //if (mData == null) {
        //    mData = new ArrayList<Article>();
        //}

        mActivity = (MainActivity) getActivity();

        // initialize network and local clients
        mLocalClient = new ORMDbClient(50);
        mNetworkClient = new NetworkClient() {
            @Override
            public void onArticlesRetrieved(List<Article> articles) {
                // code to run when articles are updated
                TEST_DATA = articles;
                mAdapter.setData(TEST_DATA);
                pullToRefresh.setRefreshing(false);
            }
        };

        // load data from cache to populate the list initially
        TEST_DATA = mLocalClient.getAll();

        // start the network call to get articles
        mNetworkClient.getArticles(NetworkUtil.isNetworkEnabled(getContext()), mCategory);

    }

    // Retrieve the articles for a given category from the SQLite database
    private List<Article> loadDataFromCache(String category) {
        return DatabaseUtil.getArticlesByCategory(category);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create new view and store inflated layout in a View.
        View rootView = inflater.inflate(R.layout.fragment_article_list,
                container, false);

        // set up the recycler view
        mAdapter = new ArticleRecyclerViewAdapter((MainActivity) getActivity(), TEST_DATA);
        // TODO  - USING TEST DATA RIGHT NOW

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // set up pull-to-refresh
        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        pullToRefresh.setColorSchemeResources(R.color.gred,
                R.color.accent, R.color.primary);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mActivity.updateArticles();
                // TODO: TEST STUFF
                update();
            }
        });


        return rootView;
    }

    /* Update the article list */
    public void update() {
        //mData = loadDataFromCache(mCategory);

        // TODO: TEST STUFF
        mNetworkClient.getArticles(NetworkUtil.isNetworkEnabled(getContext()), mCategory);
        Log.d(TAG, "update()");
    }

    /* If no articles are available, notify the user */
    public void setEmptyText(String text) {
        //TextView empty = (TextView) mRecyclerView.getEmptyView();
        //empty.setText(text);
        // TODO: 2/7/16  Don't forget
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    //Configure pull-to-refresh
    public void setRefreshing(boolean refreshing) {
        pullToRefresh.setRefreshing(refreshing);
    }
}

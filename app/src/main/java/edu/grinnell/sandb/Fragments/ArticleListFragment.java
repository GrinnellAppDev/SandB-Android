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
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Adapters.ArticleRecyclerViewAdapter;
import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Implementation.SyncMessage;
import edu.grinnell.sandb.Util.DatabaseUtil;

/*
    Custom Fragment to show the list of all Articles
 */
public class ArticleListFragment extends Fragment implements Observer {

    public MainActivity mActivity;
    public String mCategory;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private RecyclerView mRecyclerView;
    private ArticleRecyclerViewAdapter mAdapter;
    private List<Article> mData;
    private SwipeRefreshLayout pullToRefresh;
    private static final String TAG = "ArticleListFragment";
    private NetworkClient networkClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        networkClient = new NetworkClient();
        networkClient.addObserver(this);
        Bundle b = getArguments();

        /* Set the category of this fragment */
        mCategory = null;
        if (b != null)
            mCategory = Constants.titleToKey.get(b.getString(Constants.ARTICLE_CATEGORY_KEY));
        populateListData();

        mActivity = (MainActivity) getActivity();
        mAdapter = new ArticleRecyclerViewAdapter((MainActivity) getActivity(),
                R.layout.articles_row, mData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create new view and store inflated layout in a View.
        View rootView = inflater.inflate(R.layout.fragment_article_list,
                container, false);

        // set up the recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // set up pull-to-refresh
        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        pullToRefresh.setColorSchemeResources(R.color.gred,
                R.color.accent, R.color.primary);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkClient.getArticles(mCategory);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle ofJoy) {
        super.onActivityCreated(ofJoy);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            // setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
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

    /* This method is called whenever the observable updates its state */
    @Override
    public void update(Observable observable, Object data) {
        SyncMessage message = (SyncMessage) data;
        if(message != null){
            mData = (List<Article>)((SyncMessage) data).getMessageData();
            mAdapter.notifyDataSetChanged();
        }

    }

    /* Update the article list TODO : Deprecate this */
    public void update() {
        mData = loadDataFromCache(mCategory);
        mAdapter.notifyDataSetChanged();
    }

    /* If no articles are available, notify the user  TODO : Deprecate this*/
    public void setEmptyText(String text) {
        //TextView empty = (TextView) mRecyclerView.getEmptyView();
        //empty.setText(text);
        // TODO: 2/7/16  Don't forget
    }


    /* Private Helper methods */

    private void populateListData() {
        Log.i(TAG, "Loading data for the '" + mCategory + "' category..");
        if(mCategory.equals("All") && Constants.FIRST_CALL_TO_UPDATE){
            Constants.FIRST_CALL_TO_UPDATE = false;
            networkClient.firstTimeSyncLocalAndRemoteData();
        } else {
            mData = networkClient.getArticles(mCategory);
        }
        if (mData == null) {
            mData = new ArrayList<Article>();
        }
    }

    // Retrieve the articles for a given category from the SQLite database TODO:Deprecate this
    private List<Article> loadDataFromCache(String category) {
        return DatabaseUtil.getArticlesByCategory(category);
    }

}

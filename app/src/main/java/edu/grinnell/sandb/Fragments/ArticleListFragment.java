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
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Services.Implementation.NetworkClient;
import edu.grinnell.sandb.Services.Implementation.SyncMessage;
import edu.grinnell.sandb.Util.EndlessScrollListener;

/**
 *  Custom Fragment to show the list of all Articles.
 *
 *  <p> This class is responsible for defining and creating the views that hold the
 *  respective articles. Each category of Articles will have a corresponding @code{ArticleListFragment}
 *  instance.The respective @code{ArticleListFragment} instances will house the respective data
 *  for each category. Each @code{ArticleListFragment} instance is also an observer. As a result
 *  each @code{ArticleListFragment} is able to listen for any updates in the data source and
 *  dynamically pull in new data when necessary</p>
 *
 *  @Author Prabir
 *  @Author Albert Owusu-Asare
 *  @see Fragment
 *  @see Observer
 *
 */
public class ArticleListFragment extends Fragment  {

    public MainActivity mActivity;
    public String mCategory;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private RecyclerView mRecyclerView;
    private ArticleRecyclerViewAdapter mAdapter;
    private List<RealmArticle> mData;
    private SwipeRefreshLayout pullToRefresh;
    private NetworkClient networkClient;
    private Bundle args;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;
    private int currentPage = 1;
    private static final String TAG = ArticleListFragment.class.getName();

    /*
    Provides a convenient means of instantiating a new object by handling the
    bundling of the necessary parameters locally instead of having to do so externally.(Outside of
    this class.)
     */
    public static ArticleListFragment newInstance(String category){
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARTICLE_CATEGORY_KEY, category);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        args = getArguments();
        setCategory();
        mActivity = (MainActivity) getActivity();
        networkClient = mActivity.getNetworkClient();
        mData = networkClient.getArticles(mCategory,currentPage);
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
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i(TAG, mCategory + ":Request to load page" + page);
                List<RealmArticle> newData = networkClient.getNextPage(mCategory, page);
                mAdapter.updateDataBelow(newData);
                currentPage = page;
            }
        });

        // set up pull-to-refresh
        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        pullToRefresh.setColorSchemeResources(R.color.gred,
                R.color.accent, R.color.primary);
        swipeRefreshListener =  new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkClient.setSyncing(true);
                RealmArticle mostRecentArticle = mData.get(0);
                List<RealmArticle> latestArticles
                        = networkClient.getLatestArticles(mCategory,mostRecentArticle.getRealmDate());
                mAdapter.updateDataAbove(latestArticles);
            }
        };
        pullToRefresh.setOnRefreshListener(swipeRefreshListener);
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
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Updates the top of the data list with new data.
     *
     * <p> Turns off the refreshing spinning wheel</p>
     * @param articles the new data to add to existing data.
     */
    public void refreshList(List<RealmArticle> articles){
        mAdapter.updateDataAbove(articles);
        if(pullToRefresh.isRefreshing()) {
            pullToRefresh.setRefreshing(false);
        }
    }

    /**
     * Updates the bottom of the data list with new data.
     *
     * <p>This method will usually be called by the underlying activity whenevever new data for the
     * next page arrives at the activity</p>
     * @param articles
     */
    public void updateNextPageData(List<RealmArticle> articles){
        mAdapter.updateDataBelow(articles);
    }

    /**
     * @return the category that this fragment belongs to
     */
    public String getCategory(){
        return mCategory;
    }

    /* Private helper methods */
    private void setCategory() {
        /* Set the category of this fragment */
        mCategory = null;
        if (args != null)
            mCategory = Constants.titleToKey.get(args.getString(Constants.ARTICLE_CATEGORY_KEY));
    }
}

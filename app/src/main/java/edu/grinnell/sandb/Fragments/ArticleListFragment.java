package edu.grinnell.sandb.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.grinnell.sandb.Activities.ArticleDetailActivity;
import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Adapters.ArticleListAdapter;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.DatabaseUtil;

public class ArticleListFragment extends ListFragment {

	public static String ARTICLE_CATEGORY_KEY = "category";
	// LinkedHashMap retains insertion ordering
	public static final Map<String, String> titleToKey = new LinkedHashMap<String, String>();

	public static final String[] CATEGORIES;

    public MainActivity mActivity;

	// Fill in the a map to correspond to section tabs for the article list
	static {
		titleToKey.put("All", null);
		titleToKey.put("News", "News");
		titleToKey.put("Arts", "Arts");
		titleToKey.put("Community", "Community");
		titleToKey.put("Features", "Features");
		titleToKey.put("Opinion", "Opinion");
		titleToKey.put("Sports", "Sports");

		CATEGORIES = titleToKey.keySet().toArray(new String[0]);
	}

	public String mCategory;

	public static final String UPDATE = "edu.grinnell.sandb.UPDATE";
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private int mActivatedPosition = ListView.INVALID_POSITION;

	private ArticleListAdapter mAdapter;
	private List<Article> mData;
	private SwipeRefreshLayout pullToRefresh;
	private static final String TAG = "ArticleListFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		Bundle b = getArguments();

		mCategory = null;
		if (b != null)
			mCategory = titleToKey.get(b.getString(ARTICLE_CATEGORY_KEY));

		// Retrieve the articles for the selected category
		mData = loadDataFromCache(mCategory);
		Log.i(TAG, "Loading data for the '" + mCategory + "' category..");

		if (mData == null) {
            mData = new ArrayList<Article>();
        }

        mActivity = (MainActivity) getActivity();

		mAdapter = new ArticleListAdapter((MainActivity) getActivity(),
				R.layout.articles_row, mData);
	}

	// Retrieve the articles for a given category from the sqlite database 
	private List<Article> loadDataFromCache(String category) {
        return DatabaseUtil.getArticlesByCategory(category);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Create new view and store inflated layout in a View.
		View rootView = inflater.inflate(R.layout.fragment_article_list,
				container, false);

        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        pullToRefresh.setColorScheme(R.color.gred,
                R.color.DarkGray, R.color.black,R.color.gred );
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActivity.updateArticles();
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
		setListAdapter(mAdapter);

        if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			// setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	/* Update the article list */
	public void update() {
		mData = loadDataFromCache(mCategory);
		mAdapter.clear();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			mAdapter.addAll(mData);
		} else {
			for (Article a : mData) {
				mAdapter.add(a);
			}
		}

		mAdapter.notifyDataSetChanged();
	}

	/* If no articles are available, notify the user */
	public void setEmptyText(String text) {
		TextView empty = (TextView) getListView().getEmptyView();
		empty.setText(text);
	}

	/* Open the ArticleDetailActivity when a list item is selected */
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
        Article thisArticle = mData.get(position);
		Intent detailIntent = new Intent(getActivity(),
				ArticleDetailActivity.class);
		detailIntent.putExtra(ArticleDetailFragment.ARTICLE_ID_KEY,
                thisArticle.getId());
		detailIntent.putExtra(ArticleDetailActivity.COMMENTS_FEED,
                thisArticle.getComments());
		startActivity(detailIntent);
		// Add a smooth animation
		getActivity().overridePendingTransition(R.anim.slide_in,
				R.anim.slide_out);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

    public void setRefreshing(boolean refreshing) {
        pullToRefresh.setRefreshing(refreshing);
    }
}

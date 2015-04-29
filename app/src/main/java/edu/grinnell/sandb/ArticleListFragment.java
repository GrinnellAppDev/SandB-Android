package edu.grinnell.sandb;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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

import edu.grinnell.sandb.model.Article;
import edu.grinnell.sandb.util.DatabaseUtil;

public class ArticleListFragment extends ListFragment {

	public static String ARTICLE_CATEGORY_KEY = "category";
	// LinkedHashMap retains insertion ordering
	public static final Map<String, String> titleToKey = new LinkedHashMap<String, String>();

	public static final String[] CATEGORIES;

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

	private Callbacks mCallbacks = sDummyCallbacks;
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

		// Add Swipe-down to refresh functionality
		pullToRefresh = (SwipeRefreshLayout) rootView
				.findViewById(R.id.swipeRefresh);
		pullToRefresh.setOnRefreshListener(mRefresh);
		pullToRefresh.setColorScheme(R.color.gred,
			R.color.DarkGray, R.color.black,R.color.gred );
		return rootView;
	}

	
	// Set OnRefreshListener 
	OnRefreshListener mRefresh = new OnRefreshListener(){
		@Override
		public void onRefresh() {
			// Update the article list. 
			update();
			pullToRefresh.setRefreshing(true);
            Log.d("Swipe", "Refreshing Number");
            ( new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullToRefresh.setRefreshing(false);
                    
                }
            }, 1500);
			// Stop the refreshing after list is updated
			//pullToRefresh.setRefreshing(false);
		}
	};
	
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

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	/* Open the ArticleDetailActivity when a list item is selected */
	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
        Article thisArticle = mData.get(position);
		mCallbacks.onItemSelected(position);
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

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	public interface Callbacks {
		public void onItemSelected(int position);

		public void setListActivateState();
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int position) {
		}

		@Override
		public void setListActivateState() {
		}
	};

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}
		mCallbacks = (Callbacks) activity;
	}

}

package edu.grinnell.sandb;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.xmlpull.FeedContent;
import edu.grinnell.sandb.xmlpull.XMLParseTask.Article;

public class ArticleListFragment extends ListFragment {
	
	public static final String REFRESH = "edu.grinnell.sandb.REFRESH";
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String ALF = "ArticleListFragment";

    private Callbacks mCallbacks = sDummyCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;
    
    private ArticleListAdapter mAdapter;
    private List<Article> mData;

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
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) activity;
    }
     
    public void update() {
    	mData = FeedContent.articles;
    	mAdapter.clear();
    	mAdapter.addAll(mData);
    	mAdapter.notifyDataSetChanged();
    	
    }
    
    public void setEmptyText(String text) {
    	TextView empty = (TextView) getListView().getEmptyView();
    	empty.setText(text);
    }
        
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mData = new ArrayList<Article>();
        mAdapter = new ArticleListAdapter((MainActivity) getActivity(), 
        		R.layout.articles_row, 
        		mData);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	return inflater.inflate(R.layout.fragment_article_list, container, false);
    }
    
    
    @Override
    public void onActivityCreated(Bundle ofJoy) {
    	super.onActivityCreated(ofJoy);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setListAdapter(mAdapter);
        
        if (savedInstanceState != null && savedInstanceState
                .containsKey(STATE_ACTIVATED_POSITION)) {
         //   setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }
    
    @Override
    public void onDestroy() {
    	//mInstances.remove(mMenuKey);
    	super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mCallbacks.onItemSelected(position);
        // mCallbacks.onItemSelected((Article) listView.getAdapter().getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
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
    
    
}

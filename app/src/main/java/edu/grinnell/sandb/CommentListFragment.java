package edu.grinnell.sandb;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.grinnell.sandb.model.Comment;

public class CommentListFragment extends ListFragment {
	public static final String TAG = "CommentsActivity";
	private CommentListAdapter mAdapter;
	public String COMMENTS = "article comments";

	ArticleDetailActivity mActivity = (ArticleDetailActivity) getActivity();

	List<Comment> mComments;

	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_comment_list,
				container, false);

		mActivity = (ArticleDetailActivity) getActivity();
		mComments = mActivity.getComments();
		fillList();

		return rootView;
	}

	protected void fillList() {

		if (mComments == null)
			mActivity.flip();
		else if (mComments.isEmpty())
			mActivity.flip();

		mAdapter = new CommentListAdapter(mActivity, R.layout.comments_row,
				mComments);

		// be sure to show message if no comments
		setListAdapter(mAdapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.comment_list_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_articleView:
			mActivity.flip();
			break;
		default:
			break;
		}
		return false;
	}
}

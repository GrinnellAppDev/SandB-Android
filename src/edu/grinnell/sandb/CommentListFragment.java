package edu.grinnell.sandb;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.grinnell.sandb.comments.Comment;

public class CommentListFragment extends SherlockListFragment {
	public static final String TAG = "CommentsActivity";
	private CommentListAdapter mAdapter;

	ArticleDetailActivity mActivity = (ArticleDetailActivity) getSherlockActivity();

	List<Comment> mComments = null;

	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_comment_list,
				container, false);

		mActivity = (ArticleDetailActivity) getSherlockActivity();

		mComments = mActivity.getComments();

		fillList();
		
		return rootView;
	}

	protected void fillList() {

		if (mComments.isEmpty())
			// TODO print no comments message
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

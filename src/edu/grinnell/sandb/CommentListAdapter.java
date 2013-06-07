package edu.grinnell.sandb;

import java.util.List;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.grinnell.sandb.comments.Comment;

public class CommentListAdapter extends ArrayAdapter<Comment> {
	private CommentsActivity mActivity;
	private List<Comment> mData;

	public CommentListAdapter(CommentsActivity a, int layoutId, List<Comment> data) {
		super(a, layoutId, data);
		mActivity = a;
		mData = data;
	}

	private class ViewHolder {
		TextView author;
		TextView body;
		TextView date;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater li = mActivity.getLayoutInflater();
			convertView = li.inflate(R.layout.comments_row, parent, false);
			holder = new ViewHolder();
			holder.author = (TextView) convertView
					.findViewById(R.id.titleText);
			holder.body = (TextView) convertView
					.findViewById(R.id.descriptionText);
			holder.date = (TextView) convertView
					.findViewById(R.id.dateText);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Comment a = mData.get(position);
				
		if (a != null) {
			holder.author.setText(a.getAuthor());
			holder.body.setText(Html.fromHtml(a.getBody()));
			//holder.date.setText(a.getPostDate());
		}

	//	Log.i("comm ad", a.getAuthor());
		
		return convertView;
	}
}

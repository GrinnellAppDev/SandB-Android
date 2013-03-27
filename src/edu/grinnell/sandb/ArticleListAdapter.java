package edu.grinnell.sandb;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.data.Article;

public class ArticleListAdapter extends ArrayAdapter<Article> {
	private MainActivity mActivity;
	private List<Article> mData;
	
	public ArticleListAdapter(MainActivity a, int layoutId, List<Article> data) {
		super(a, layoutId, data);
		mActivity = a;
		mData = data;
	}
	
	private static class ViewHolder
    {
        TextView title;
        TextView description;
        TextView date;
        ImageView image;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup  parent) {
		
		ViewHolder holder;
		
		if (convertView == null) {
			LayoutInflater li = mActivity.getLayoutInflater();
			convertView = li.inflate(R.layout.articles_row, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.titleText);
			holder.description = (TextView) convertView.findViewById(R.id.descriptionText);
			//holder.image = (ImageView) convertView.findViewById(R.id.articleThumb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Article a = mData.get(position);
		
		
		if (a != null) {
			
			holder.title.setText(a.getTitle());
			holder.description.setText(a.getDescription());
			//Resources r = mActivity.getResources();
			holder.title.setPadding(3, 3, 3, 3);
			
			}
		
		return convertView;
	}
}

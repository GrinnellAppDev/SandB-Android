package edu.grinnell.sandb;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.img.UniversalLoaderUtility;

/* List Adapter to populate the article list */
public class ArticleListAdapter extends ArrayAdapter<Article> {
	private MainActivity mActivity;
	private List<Article> mData;
	protected UniversalLoaderUtility mLoader;

	public ArticleListAdapter(MainActivity a, int layoutId, List<Article> data) {
		super(a, layoutId, data);
		mActivity = a;
		mData = data;
		mLoader = new UniversalLoaderUtility();
	}

	private class ViewHolder {
		TextView title;
		TextView description;
		ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater li = mActivity.getLayoutInflater();
			convertView = li.inflate(R.layout.articles_row, parent, false);
			holder = new ViewHolder();
			//set the article title
			holder.title = (TextView) convertView
					.findViewById(R.id.titleText);
			//set the article description
			holder.description = (TextView) convertView
					.findViewById(R.id.descriptionText);
			//set the article thumbnail image
			holder.image = (ImageView) convertView
					.findViewById(R.id.articleThumb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.image.setVisibility(View.VISIBLE);
		final Article a = mData.get(position);
				
		if (a != null) {
			holder.image.setVisibility(View.VISIBLE);
			mLoader.loadArticleImage(a, holder.image, mActivity);
			holder.title.setText(a.getTitle());
			holder.title.setPadding(3, 3, 3, 3);
			holder.description.setText(a.getDescription());
		}
		
		return convertView;
	}
}

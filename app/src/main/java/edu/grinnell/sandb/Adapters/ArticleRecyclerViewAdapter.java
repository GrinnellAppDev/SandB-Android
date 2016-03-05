package edu.grinnell.sandb.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.grinnell.sandb.Activities.ArticleDetailActivity;
import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Fragments.ArticleDetailFragment;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.Image;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.DatabaseUtil;
import edu.grinnell.sandb.Util.UniversalLoaderUtility;

/**
 * Created by prabir on 2/7/16.
 */
public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {
    private MainActivity mActivity;
    private List<Article> mData;
    protected UniversalLoaderUtility mLoader;

    public ArticleRecyclerViewAdapter(MainActivity a, int layoutId, List<Article> data) {
        super();
        mActivity = a;
        mData = data;
        mLoader = new UniversalLoaderUtility();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = mActivity.getLayoutInflater();
        View view = li.inflate(R.layout.articles_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.image.setVisibility(View.VISIBLE);
        final Article a = mData.get(position);
        if (a != null) {
            holder.image.setVisibility(View.VISIBLE);
            Image articleImage = DatabaseUtil.getArticleImage(a);

            if (articleImage != null) {
                mLoader.loadImage(articleImage.getURL(), holder.image, mActivity);
            } else {
                holder.image.setImageResource(R.drawable.sb);
            }

            holder.title.setText(Html.fromHtml(a.getTitle()));
            holder.title.setPadding(3, 3, 3, 3);
            //holder.description.setText(Html.fromHtml(a.getDescription()));
            holder.category.setText(a.getCategory());
            holder.date.setText(a.getPubDate());


            // open the article when it is clicked
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(mActivity,
                            ArticleDetailActivity.class);
                    detailIntent.putExtra(ArticleDetailFragment.ARTICLE_ID_KEY,
                            a.getId());
                    detailIntent.putExtra(ArticleDetailActivity.COMMENTS_FEED,
                            a.getComments());
                    mActivity.startActivity(detailIntent);
                    // Add a smooth animation
                    mActivity.overridePendingTransition(R.anim.slide_in,
                            R.anim.slide_out);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        TextView category;
        TextView date;
        ImageView image;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            //find the article title view
            title = (TextView) itemView.findViewById(R.id.titleText);
            //find the article description view
            description = (TextView) itemView.findViewById(R.id.descriptionText);
            //find the article image view
            image = (ImageView) itemView.findViewById(R.id.articleThumb);
            //find the category text view
            category = (TextView) itemView.findViewById(R.id.category);
            //find the date text view
            date = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}

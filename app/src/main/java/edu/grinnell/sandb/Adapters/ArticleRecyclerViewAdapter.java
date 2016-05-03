package edu.grinnell.sandb.Adapters;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.AbstractList;
import java.util.List;
import java.util.ListIterator;

import edu.grinnell.sandb.Activities.ArticleDetailActivity;
import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Fragments.ArticleDetailFragment;
import edu.grinnell.sandb.Model.Article;
import edu.grinnell.sandb.Model.Image;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Model.RealmImage;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.DatabaseUtil;
import edu.grinnell.sandb.Util.UniversalLoaderUtility;
import edu.grinnell.sandb.Util.VersionUtil;

/**
 * Created by prabir on 2/7/16.
 */
public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {
    private MainActivity mActivity;
    private List<RealmArticle> mData;
    protected UniversalLoaderUtility mLoader;

    public ArticleRecyclerViewAdapter(MainActivity a, int layoutId, List<RealmArticle> data) {
        super();
        mActivity = a;
        mData = (AbstractList<RealmArticle>) data;
        mLoader = new UniversalLoaderUtility();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = mActivity.getLayoutInflater();
        View view = li.inflate(R.layout.articles_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.image.setVisibility(View.VISIBLE);
        final RealmArticle a = mData.get(position);
        if (a != null) {
            holder.image.setVisibility(View.VISIBLE);
            Image articleImage = null;//DatabaseUtil.getArticleImage(a);
            RealmImage articleThumbnail = a.getArticleThumbnail();

            if (articleThumbnail != null) {
                mLoader.loadImage(articleThumbnail.getUrl(), holder.image, mActivity);
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
                            a.getArticleID());
                  //  detailIntent.putExtra(ArticleDetailActivity.COMMENTS_FEED,
                    //        a.getComments());

                    if (VersionUtil.isLollipop()) {
                        Pair<View, String> p1 = new Pair<>((View) holder.title, "article_title");
                        //mActivity.startActivity(detailIntent,
                        //        ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, p1).toBundle());;
                        mActivity.startActivity(detailIntent,
                                ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity).toBundle());
                    } else {
                        mActivity.startActivity(detailIntent);
                    }
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

    public void updateData(List<RealmArticle> newData) {
        if(mData != null) {
            Log.i("Tabs Adapter:", "Updating dataSet in Adapter");
           // mData.clear();
            newData.addAll(mData);
            mData = (AbstractList<RealmArticle>) newData;
           // mData.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public void updateDataAbove(List<RealmArticle> newData){
        if(mData != null) {
            Log.i("Tabs Adapter:", "Updating dataSet above  in Adapter");
            newData.addAll(mData);
            mData =  newData;
            notifyDataSetChanged();
        }

    }

    public void updateDataBelow(List<RealmArticle> newData){
        if(mData != null) {
            Log.i("Tabs Adapter:", "Updating dataSet above  in Adapter");
            int curSize = getItemCount();
            mData.addAll(newData);
          //  notifyDataSetChanged();
            notifyItemRangeInserted(curSize, mData.size() - 1);
        }

    }

    public void addPage(List<RealmArticle> newPageData){
        int curSize = getItemCount();
        mData.addAll(newPageData);

        // for efficiency purposes, only notify the adapter of what elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        notifyItemRangeInserted(curSize, mData.size() - 1);
    }
}

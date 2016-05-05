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

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.Date;
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
import edu.grinnell.sandb.Util.ISO8601;
import edu.grinnell.sandb.Util.UniversalLoaderUtility;
import edu.grinnell.sandb.Util.VersionUtil;

/**
 * This class acts as the bridge between the recycler view and the underlying data that will
 * be populated in the view.
 *
 * @author Prabir Pradhan
 * @author Albert Owusu-Asare
 * Create  on 2/7/16.
 */
public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {
    private MainActivity mActivity;
    private List<RealmArticle> mData;
    protected UniversalLoaderUtility mLoader;
    private final String TAG = ArticleRecyclerViewAdapter.class.getName();
    private int imgThumbWidth;
    private int imgThumbHeight;

    private SimpleDateFormat dateFormat;


    public ArticleRecyclerViewAdapter(MainActivity a, int layoutId, List<RealmArticle> data) {
        super();
        mActivity = a;
        mData = data;
        mLoader = new UniversalLoaderUtility();
        dateFormat = new SimpleDateFormat("d MMMM, yyyy");
        imgThumbWidth = Math.round(mActivity.getResources().getDimension(R.dimen.article_image_thumb_width));
        imgThumbHeight = Math.round(mActivity.getResources().getDimension(R.dimen.article_image_thumb_height));
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

            String imgUrl = a.getThumbnailUrl();

            if (imgUrl != null && !imgUrl.isEmpty()) {
                holder.image.setVisibility(View.VISIBLE);
                Picasso.with(mActivity)
                        .load(imgUrl)
                        .placeholder(R.drawable.sb)
                        .error(R.drawable.sb)
                        .resize(imgThumbWidth, imgThumbHeight)
                        .centerCrop()
                        .into(holder.image);
            } else {
                holder.image.setVisibility(View.GONE);
            }

            holder.title.setText(Html.fromHtml(a.getTitle()));
            holder.title.setPadding(3, 3, 3, 3);
            holder.category.setText(a.getCategory());
            try {
                holder.date.setVisibility(View.VISIBLE);
                Date date = ISO8601.toDate(a.getPubDate());
                holder.date.setText(dateFormat.format(date));

            } catch (Exception e) {
                holder.date.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }


            // open the article when it is clicked
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(mActivity,
                            ArticleDetailActivity.class);
                    detailIntent.putExtra(ArticleDetailFragment.ARTICLE_ID_KEY,
                            a.getArticleID());

                    if (VersionUtil.isLollipop()) {
                        Pair<View, String> p1 = new Pair<>((View) holder.title, "article_title");
                        //activity.startActivity(detailIntent,
                        //        ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1).toBundle());;
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

    /**
     * Adds data to the top of the list containing the current data.
     *
     * <P> This method is called on whenever a fragment refreshes to fetch most recent data</P>
     * @param newData the incoming data to be added to the existing data
     */
    public void updateDataAbove(List<RealmArticle> newData){
        if(mData != null) {
            Log.i(TAG, "Updating dataSet above  in Adapter");
            newData.addAll(mData);
            mData =  newData;
            notifyDataSetChanged();
        }

    }

    /**
     * Adds data to the top of the list containing the current data.
     *
     * <P> This method is called on whenever a fragment receives the next page of data</P>
     * @param newData the incoming data to be added to the existing data
     */
    public void updateDataBelow(List<RealmArticle> newData){
        if(mData != null) {
            Log.i(TAG, "Updating dataSet above  in Adapter");
            int curSize = getItemCount();
            mData.addAll(newData);
            notifyItemRangeInserted(curSize, mData.size() - 1);
        }

    }
}

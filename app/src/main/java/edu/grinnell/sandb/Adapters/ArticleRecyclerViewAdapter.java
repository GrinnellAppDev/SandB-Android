package edu.grinnell.sandb.Adapters;

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

import java.util.List;

import edu.grinnell.sandb.Activities.ArticleDetailActivity;
import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Fragments.ArticleDetailFragment;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.UniversalLoaderUtility;
import edu.grinnell.sandb.Util.VersionUtil;

public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<ArticleRecyclerViewAdapter.ViewHolder> {
    private MainActivity activity;
    private List<RealmArticle> data;
    protected UniversalLoaderUtility universalLoaderUtility;


    public ArticleRecyclerViewAdapter(MainActivity a, int layoutId, List<RealmArticle> data) {
        super();
        activity = a;
        this.data = data;
        universalLoaderUtility = new UniversalLoaderUtility();
    }


    public void setData(List<RealmArticle>data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = activity.getLayoutInflater();
        View view = li.inflate(R.layout.articles_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
    
        holder.image.setVisibility(View.VISIBLE);
        final RealmArticle a = data.get(position);
        if (a != null) {
            holder.image.setVisibility(View.VISIBLE);

            /*
            Image articleImage = DatabaseUtil.getArticleImage(a);
            Image articleImage = null;//DatabaseUtil.getArticleImage(a);

            if (articleImage != null) {
                universalLoaderUtility.loadImage(articleImage.getURL(), holder.image, activity);
            } else {
                holder.image.setImageResource(R.drawable.sb);
            }
            */
            // TODO: 5/2/16 bind image
/*
            Picasso.with(activity)
                    .load(a.())
                    .placeholder(R.drawable.sb)
                    .error(R.drawable.sb)
                    .into(holder.image);
            Log.d("RecyclerView", "onBindViewHolder: " + a.getThumbnail());
*/
            holder.title.setText(Html.fromHtml(a.getTitle()));
            holder.title.setPadding(3, 3, 3, 3);
            //holder.description.setText(Html.fromHtml(a.getDescription()));
            holder.category.setText(a.getCategory());
            holder.date.setText(a.getPubDate());


            // open the article when it is clicked
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailIntent = new Intent(activity,
                            ArticleDetailActivity.class);
                    detailIntent.putExtra(ArticleDetailFragment.ARTICLE_ID_KEY,
                            a.getArticleID());

                    if (VersionUtil.isLollipop()) {
                        Pair<View, String> p1 = new Pair<>((View) holder.title, "article_title");
                        //activity.startActivity(detailIntent,
                        //        ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1).toBundle());;
                        activity.startActivity(detailIntent,
                                ActivityOptionsCompat.makeSceneTransitionAnimation(activity).toBundle());
                    } else {
                        activity.startActivity(detailIntent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
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
        if(data != null) {
            Log.i("Tabs Adapter:", "Updating dataSet in Adapter");
           // data.clear();
            newData.addAll(data);
            data = newData;
           // data.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public void updateDataAbove(List<RealmArticle> newData){
        if(data != null) {
            Log.i("Tabs Adapter:", "Updating dataSet above  in Adapter");
            newData.addAll(data);
            data = newData;
            notifyDataSetChanged();
        }

    }

    public void addPage(List<RealmArticle> newPageData){
        int curSize = getItemCount();
        data.addAll(newPageData);

        // for efficiency purposes, only notify the adapter of what elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        notifyItemRangeInserted(curSize, data.size() - 1);
    }
}

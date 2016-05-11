package edu.grinnell.sandb.Fragments;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;

import edu.grinnell.sandb.Activities.ArticleDetailActivity;
import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.DialogSettings;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Preferences.MainPrefs;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.UniversalLoaderUtility;
import io.realm.Realm;
import io.realm.RealmResults;

/*
    Custom Fragment to show Articles in details
 */
public class ArticleDetailFragment extends Fragment {

    public static final String TAG = "ArticleDetailFragment";
    public static final String ARTICLE_ID_KEY = "article_id";
    private RealmArticle article;
    protected UniversalLoaderUtility universalLoaderUtility;
    private int fontSize;
    private NestedScrollView.OnScrollChangeListener scrollChangeListener;

    //These variables will govern where a picture is saved if the user chooses to download it
    @SuppressLint("NewApi")
    File path = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    String fileName = "SBimage";
    File file = new File(path, fileName);
    DownloadManager mManager;


    //Methods
    @Override
    public void onCreate(Bundle ofJoy) {
        super.onCreate(ofJoy);
        setHasOptionsMenu(true);
        setRetainInstance(true);

        fontSize = new MainPrefs(getContext()).getArticleFontSize();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflate layout
        View rootView = inflater.inflate(R.layout.fragment_article_detail,
                container, false);

        // Navigate up if there is no article information..
        if (article == null) {
            Intent up = new Intent(getActivity(), MainActivity.class);
            up.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(getActivity(), up);

            // end the activity
            getActivity().finish();
        }

        NestedScrollView scrollView = (NestedScrollView) rootView.findViewById(R.id.article_nested_scroll_view);
        scrollView.setOnScrollChangeListener(scrollChangeListener);

        displayArticle(rootView);

        return rootView;
    }

    /**
     * Set the article that is to be displayed by the fragment
     */
    public void setArticle(long articleId) {
        Log.d(TAG, "Article ID: " + articleId);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmArticle> results = realm.where(RealmArticle.class)
                .equalTo("articleID", articleId)
                .findAll();
        article = results.first();
    }

    /**
     * Set the scroll change listener to be used by the scroll view
     *
     * @param listener
     */
    public void setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener listener) {
        scrollChangeListener = listener;
    }

    /**
     * Check to see if the current article has an associated feature image
     *
     * @return
     */
    public boolean hasFeatureImage() {
        String url = article.getThumbnailUrl();
        return url != null && !url.isEmpty();
    }


    //Set transitions
    private void setTransitionListener(final View rootView) {
        final Transition transition = getActivity().getWindow().getEnterTransition();
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                // load the article after the end of the transition
                displayArticle(rootView);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

    }


    //Configuring the display of articles
    private void displayArticle(View rootView) {
        // add the author to the article

        String dateString;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy");
        dateString = dateFormat.format(article.getRealmDate());
        // add the date to the article
        ((TextView) rootView.findViewById(R.id.article_date)).setText(dateString);

        // add the title to the article
        ((TextView) rootView.findViewById(R.id.article_title)).setText(Html.fromHtml(article
                .getTitle()));

        // load the feature image
        String featureImgUrl = article.getThumbnailUrl();
        ImageView featureImageView = (ImageView) rootView.findViewById(R.id.feature_image);
        if (featureImgUrl != null && !featureImgUrl.isEmpty()) {
            featureImageView.setVisibility(View.VISIBLE);
            Picasso.with(getContext())
                    .load(featureImgUrl)
                    .centerCrop()
                    .fit()
                    .error(R.drawable.sb)
                    .noPlaceholder()
                    .into(featureImageView);
        } else {
            featureImageView.setVisibility(View.GONE);
        }

        LinearLayout body = (LinearLayout) rootView
                .findViewById(R.id.article_body_group);
        universalLoaderUtility = new UniversalLoaderUtility();

        String bodyHTML = article.getBody();
        Log.d(TAG, "Body: " + bodyHTML);

        // make text more readable
        bodyHTML = bodyHTML.replaceAll("<br />", "<br><br>");

        // remove image descriptions
        bodyHTML = bodyHTML.replaceAll("<p class=\"wp-caption-text\">.+?</p>",
                "");

        String imgtags = "<img.+?>";
        // String imgtags = "<div.+?</div>";

        //Split the article text around the images
        String[] sections = bodyHTML.split(imgtags);

        // TODO: 5/9/16 Parse in-text images here and add new ImageViews here

        LayoutInflater inflater = getActivity().getLayoutInflater();

        for (String section : sections) {
            addTextView(body, inflater, section);
        }

        Log.i(TAG, article.getTitle());
    }


    private void addTextView(ViewGroup v, LayoutInflater li, String text) {
        TextView tv = (TextView) li
                .inflate(R.layout.text_section, v, false);
        tv.setText(Html.fromHtml(text));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_TO_SP[fontSize]);
        tv.setLineSpacing(0, 1.3f);
        tv.setTypeface(Typeface.SERIF);
        v.addView(tv);

    }


    @Override
    public void onResume() {
        super.onResume();
        if (article == null) {
            // Navigate Up..
            Intent upIntent = new Intent(getActivity(),
                    MainActivity.class);
            NavUtils.navigateUpTo(getActivity(), upIntent);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.article_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // case R.id.menu_settings:
            // // startActivityForResult(new Intent(this, PrefActiv.class), PREFS);
            // break;
            case R.id.menu_share:
                share();
                break;
            case R.id.settings:
                DialogSettings ds = new DialogSettings(getContext()) {
                    @Override
                    public void onSettingsSaved() {
                        fontSize = new MainPrefs(getContext()).getArticleFontSize();
                        reloadArticle();
                    }
                };
                ds.show();
                break;
            default:
                break;
        }
        return false;
    }

    //Reload all articles
    public void reloadArticle() {
        LinearLayout body = (LinearLayout) getView()
                .findViewById(R.id.article_body_group);
        body.removeAllViewsInLayout();
        displayArticle(getView());
        getView().invalidate();

    }


    /* Allow the user to share the article url using the app of their choice */
    public void share() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String shareBody = article.getLink();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "S&B Article Link");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        // Share to any compatible app on the device
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARTICLE_ID_KEY, article.getArticleID());
        super.onSaveInstanceState(outState);
    }
}


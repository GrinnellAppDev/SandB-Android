package edu.grinnell.sandb.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.grinnell.sandb.Activities.ArticleDetailActivity;
import edu.grinnell.sandb.Activities.ImagePagerActivity;
import edu.grinnell.sandb.Activities.MainActivity;
import edu.grinnell.sandb.Constants;
import edu.grinnell.sandb.DialogSettings;
import edu.grinnell.sandb.Model.Image;
import edu.grinnell.sandb.Model.RealmArticle;
import edu.grinnell.sandb.Preferences.MainPrefs;
import edu.grinnell.sandb.R;
import edu.grinnell.sandb.Util.ISO8601;
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
    ArrayList<Image> mImages;


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
     * @param listener
     */
    public void setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener listener) {
        scrollChangeListener = listener;
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
        try {
            Date articleDate = ISO8601.toDate(article.getPubDate());
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM, yyyy");
            dateString = dateFormat.format(articleDate);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            dateString = "";
        }
        // add the date to the article
        ((TextView) rootView.findViewById(R.id.article_date)).setText(dateString);

        // add the title to the article
        ((TextView) rootView.findViewById(R.id.article_title)).setText(Html.fromHtml(article
                .getTitle()));

        // load the feature image
        String featureImgUrl = article.getFeaturedImgUrl();
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

        //mImages = (ArrayList) DatabaseUtil.getArticleImages(article);

        final int maxUrls = (mImages == null) ? 0 : mImages.size();
        LayoutInflater inflater = getActivity().getLayoutInflater();

        int i = 0;
        for (String section : sections) {
            String url = (i < maxUrls) ? mImages.get(i++).getURL() : null;
            addSectionViews(body, inflater, section, url);
        }

        Log.i(TAG, article.getTitle());
    }


    //Add Section Views
    private void addSectionViews(ViewGroup v, LayoutInflater li, String text,
                                 final String img) {
        //Check for no image and inflate layout
        if (img != null) {
            ImageView imgView = (ImageView) li.inflate(R.layout.img_section, v,
                    false);

            // Open a full screen image pager if image is clicked
            OnClickListener imgClick = new OnClickListener() {
                public void onClick(View v) {
                    String[] URLS = new String[mImages.size()];
                    String[] titles = new String[mImages.size()];
                    for (int i = 0; i < mImages.size(); i++) {
                        URLS[i] = universalLoaderUtility.getHiResImage(mImages.get(i).getURL());
                        titles[i] = mImages.get(i).getImgTitle();
                    }
                    Intent intent = new Intent(getActivity(),
                            ImagePagerActivity.class);
                    intent.putExtra("ArticleImages", URLS);
                    intent.putExtra("ImageTitles",
                            titles);

                    startActivity(intent);
                }
            };

            //Display an option to download the image if it is held
            OnLongClickListener imgHold = new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getActivity());

                    // set title
                    alertDialogBuilder.setTitle("Download Image?");

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(true)
                            .setPositiveButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("Download",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            new DownloadFile().execute(universalLoaderUtility
                                                    .getHiResImage(img));
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                    return false;
                }
            };

            //Configure image view actions
            imgView.setOnClickListener(imgClick);
            imgView.setOnLongClickListener(imgHold);

            // set max height so that image does not go off screen
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay()
                    .getMetrics(metrics);

            int scrnHeight = metrics.heightPixels - 100;
            imgView.setMaxHeight(scrnHeight);

            //Load the full resolution images using universal image loader
            universalLoaderUtility.loadHiResArticleImage(img, imgView, getActivity());
            v.addView(imgView);
        }

        if (text != null) {
            TextView tv = (TextView) li
                    .inflate(R.layout.text_section, v, false);
            tv.setText(Html.fromHtml(text));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.FONT_SIZE_TO_SP[fontSize]);
            v.addView(tv);
        }
    }


    /* Download the selected image to "downloads" on user request */
    private class DownloadFile extends AsyncTask<String, Integer, Drawable> {

        @SuppressLint("NewApi")
        protected Drawable doInBackground(String... sUrl) {
            Uri img = Uri.parse(sUrl[0]);
            Uri dest = Uri.fromFile(file);

            //Use the built in Android download manager
            mManager = (DownloadManager) getActivity()
                    .getSystemService(getActivity().DOWNLOAD_SERVICE);

            mManager.enqueue(new DownloadManager.Request(img)
                    .setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI
                                    | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("S&B Image")
                    .setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(dest));
            return Drawable
                    .createFromPath(Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            + "/" + fileName);
        }
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
            case R.id.menu_comments:
                ((ArticleDetailActivity) getActivity()).flip();
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


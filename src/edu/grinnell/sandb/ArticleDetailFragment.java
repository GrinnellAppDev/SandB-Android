package edu.grinnell.sandb;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.ArticleTable;
import edu.grinnell.sandb.img.ImageTable;
import edu.grinnell.sandb.img.UniversalLoaderUtility;

public class ArticleDetailFragment extends SherlockFragment {

	public final static String FEED_LINK = null;
	public final static String ARTICLE_LINK = null;

	//These variables will govern the touch gesture to return user to the article list
	private static int scrnHeight;
	private static final int SWIPE_MIN_DISTANCE = 300;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	static GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;

	public static final String ARTICLE_ID_KEY = "article_id";
	private Article mArticle;
	protected UniversalLoaderUtility mLoader;

	public static final String TAG = "ArticleDetailFragment";

	private PendingIntent mSendFeedLoaded;
	ArticleDetailActivity activity = (ArticleDetailActivity) getSherlockActivity();

	//These variables will govern where a picture is saved if the user chooses to download it
	File path = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
	String fileName = "SBimage";
	File file = new File(path, fileName);
	DownloadManager mManager;

	public ArticleDetailFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);
		setHasOptionsMenu(true);
		setRetainInstance(true);

		// Scale the touch gesture listener sensitivty for the screen size
		DisplayMetrics metrics = new DisplayMetrics();
		getSherlockActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);

		scrnHeight = metrics.heightPixels - 100;

		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		// Apply a gesture detector to return to the article list on a horizonal screen swipe
		gestureDetector = new GestureDetector(getSherlockActivity(),
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
								&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
							getSherlockActivity().onBackPressed();
							return true;
						} else
							return false;
					}
				});
		
		//Find the article in the sqlite database using the ID key
		activity = (ArticleDetailActivity) getSherlockActivity();
		int id = activity.getIDKey();
		ArticleTable table = new ArticleTable(getSherlockActivity());
		table.open();
		Log.i(TAG, "Looking for article with id = " + id);
		mArticle = table.findById(id);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_article_detail,
				container, false);

		// Navigate up if there is no article information..
		if (mArticle == null) {
			Intent up = new Intent(getSherlockActivity(), MainActivity.class);
			up.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			NavUtils.navigateUpTo(getSherlockActivity(), up);
		}

		// add the author to the article
		((TextView) rootView.findViewById(R.id.article_author)).setText("By: "
				+ mArticle.getAuthor());

		// add the date to the article
		((TextView) rootView.findViewById(R.id.article_date)).setText(mArticle
				.getPubDate().toString());

		// add the title to the article
		((TextView) rootView.findViewById(R.id.article_title)).setText(mArticle
				.getTitle());

		LinearLayout body = (LinearLayout) rootView
				.findViewById(R.id.article_body_group);
		mLoader = new UniversalLoaderUtility();

		String bodyHTML = mArticle.getBody();

		// make text more readable
		bodyHTML = bodyHTML.replaceAll("<br />", "<br><br>");

		// remove image descriptions
		bodyHTML = bodyHTML.replaceAll("<p class=\"wp-caption-text\">.+?</p>",
				"");

		String imgtags = "<img.+?>";
		// String imgtags = "<div.+?</div>";

		//Split the article text around the images
		String[] sections = bodyHTML.split(imgtags);

		//Load the images for the article
		ImageTable imgTable = new ImageTable(getActivity());
		imgTable.open();
		String[] urls = imgTable.findUrlsByArticleId(mArticle.getId());
		imgTable.close();
		final int maxUrls = (urls == null) ? 0 : urls.length;
		LayoutInflater i = getActivity().getLayoutInflater();

		int cnt = 0;
		for (String section : sections) {
			String url = (cnt < maxUrls) ? urls[cnt++] : null;
			addSectionViews(body, i, section, url);
		}

		body.setOnTouchListener(gestureListener);

		Log.i(TAG, mArticle.getTitle());
		return rootView;
	}

	private void addSectionViews(ViewGroup v, LayoutInflater li, String text,
			final String img) {

		if (img != null) {
			ImageView imgView = (ImageView) li.inflate(R.layout.img_section, v,
					false);
			
			// Open a full screen image pager if image is clicked
			OnClickListener imgClick = new OnClickListener() {
				public void onClick(View v) {

					ImageTable imgTable = new ImageTable(getSherlockActivity());
					imgTable.open();

					int id = mArticle.getId();
					String[] URLS = imgTable.findUrlsByArticleId(id);

					for (int i = 0; i < URLS.length; i++) {
						URLS[i] = mLoader.getHiResImage(URLS[i]);
						// System.out.println(URLS[i]);
					}

					Intent intent = new Intent(getSherlockActivity(),
							ImagePagerActivity.class);
					intent.putExtra("ArticleImages", URLS);
					intent.putExtra("ImageTitles",
							imgTable.findTitlesbyArticleId(id));

					imgTable.close();
					startActivity(intent);
				}
			};

			//Display an option to download the image if it is held
			OnLongClickListener imgHold = new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getSherlockActivity());

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
											new DownloadFile().execute(mLoader
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

			imgView.setOnTouchListener(gestureListener);
			imgView.setOnClickListener(imgClick);
			imgView.setOnLongClickListener(imgHold);

			// set max height so that image does not go off screen
			DisplayMetrics metrics = new DisplayMetrics();
			getSherlockActivity().getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);

			int scrnHeight = metrics.heightPixels - 100;
			imgView.setMaxHeight(scrnHeight);

			//Load the full resolution images using universal image loader
			mLoader.loadHiResArticleImage(img, imgView, getSherlockActivity());
			v.addView(imgView);
		}

		if (text != null) {
			TextView tv = (TextView) li
					.inflate(R.layout.text_section, v, false);
			tv.setText(Html.fromHtml(text));
			v.addView(tv);
		}
	}

	/* Download the selected image to "downloads" on user request */
	public class DownloadFile extends AsyncTask<String, Integer, Drawable> {

		@SuppressLint("NewApi")
		protected Drawable doInBackground(String... sUrl) {
			Uri img = Uri.parse(sUrl[0]);
			Uri dest = Uri.fromFile(file);

			//Use the built in Android download manager
			mManager = (DownloadManager) getSherlockActivity()
					.getSystemService(getSherlockActivity().DOWNLOAD_SERVICE);

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
		if (mArticle == null) {
			// Navigate Up..
			Intent upIntent = new Intent(getSherlockActivity(),
					MainActivity.class);
			NavUtils.navigateUpTo(getSherlockActivity(), upIntent);
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
		case R.id.menu_share2:
			share();
			break;
		case R.id.menu_comments:
			activity.flip();
			break;
		default:
			break;
		}
		return false;
	}

	/* Allow the user to share the article url using the app of their choice */
	public void share() {

		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");

		String shareBody = mArticle.getLink();
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"S&B Article Link");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

		// Share to any compatible app on the device
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ARTICLE_ID_KEY, mArticle.getId());
		super.onSaveInstanceState(outState);
	}
}


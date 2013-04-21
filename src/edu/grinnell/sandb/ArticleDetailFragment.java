package edu.grinnell.sandb;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.ArticleTable;
import edu.grinnell.sandb.data.ImageTable;

public class ArticleDetailFragment extends SherlockFragment {

	public static final String ARTICLE_ID_KEY = "article_id";
	private Article mArticle;

	public static final String TAG = "ArticleDetailFragment";

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public ArticleDetailFragment() {
		super();
	}

	@Override
	public void onCreate(Bundle ofJoy) {
		super.onCreate(ofJoy);

		setHasOptionsMenu(true);

		Bundle b = (ofJoy == null) ? getArguments() : ofJoy;

		if (b != null) {
			int id = b.getInt(ARTICLE_ID_KEY);
			ArticleTable table = new ArticleTable(getSherlockActivity());
			table.open();
			Log.d(TAG, "Looking for article with id = " + id);
			mArticle = table.findById(id);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_article_detail,
				container, false);

		((TextView) rootView.findViewById(R.id.article_title)).setText(mArticle
				.getTitle());
		TextView body = (TextView) rootView.findViewById(R.id.article_body);

		// show first article image
		ImageView imgView = (ImageView) rootView
				.findViewById(R.id.articleImage1);

		// DbImageGetter ImageGetter = new DbImageGetter(getSherlockActivity());
		// Drawable articleImage =
		// ImageGetter.fetchDrawableForArticle(mArticle);

		ImageTable imgTable = new ImageTable(getSherlockActivity());
		imgTable.open();

		int id = mArticle.getId();
		String[] URLS = imgTable.findURLSbyArticleId(id);
		
		if (URLS != null){
			String imgUrl = URLS[0];
			imgUrl = getHiResImage(imgUrl);
			loadImage(imgUrl, imgView);
		}

		/*
		 * // TODO display hi res image in article
		 * 
		 * if (articleImage != null) { Bitmap imageBitmap =
		 * scaleImage(articleImage, rootView); //
		 * imgView.setImageDrawable(articleImage);
		 * imgView.setImageBitmap(imageBitmap); }
		 * 
		 * OnClickListener imgClick = new OnClickListener() { public void
		 * onClick(View v) {
		 * 
		 * ImageTable imgTable = new ImageTable(getSherlockActivity());
		 * imgTable.open();
		 * 
		 * int id = mArticle.getId(); String[] URLS =
		 * imgTable.findURLSbyArticleId(id);
		 * 
		 * for (int i = 0; i < URLS.length; i++) { URLS[i] =
		 * getHiResImage(URLS[i]); System.out.println(URLS[i]); }
		 * 
		 * Intent intent = new Intent(getSherlockActivity(),
		 * ImagePagerActivity.class); intent.putExtra("ArticleImages", URLS);
		 * intent.putExtra("ImageTitles", imgTable.findTitlesbyArticleId(id));
		 * 
		 * imgTable.close(); startActivity(intent); } };
		 * 
		 * imgView.setOnClickListener(imgClick);
		 */

		String bodyHTML = mArticle.getBody();

		// make text more readable
		bodyHTML = bodyHTML.replaceAll("<br />", "<br><br>");

		// remove images
		bodyHTML = bodyHTML.replaceAll("<a.+?</a>", "");
		bodyHTML = bodyHTML.replaceAll("<div.+?</div>", "");
		body.setText(Html.fromHtml(bodyHTML));

		Log.d(TAG, mArticle.getTitle());

		return rootView;
	}

	// remove the ends of each image URL to download full sized images
	private String getHiResImage(String lowResImg) {
		// add "contains" for error testing

		if (lowResImg == null) {
			return null;
		}

		int readTo = lowResImg.lastIndexOf("-");

		if (readTo != -1) {
			String hiResImg = lowResImg.substring(0, readTo);
			hiResImg = hiResImg.concat(".jpg");
			return hiResImg;
		} else
			return lowResImg;
	}

	// Scale the image to fill the screen width
	private Bitmap scaleImage(Drawable img, View rootView) {

		// convert drawable to bitmap
		Bitmap bm = ((BitmapDrawable) img).getBitmap();
		// Get display width from device
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);

		int displayWidth = metrics.widthPixels;

		// Calculate scaling factor
		float scalingFactor = ((float) displayWidth / (float) bm.getWidth());

		int scaleHeight = (int) (bm.getHeight() * scalingFactor);
		int scaleWidth = (int) (bm.getWidth() * scalingFactor);

		return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
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
		// TODO adds item for each rotation BAD
		// if (menu.removeItem(id))
		inflater.inflate(R.menu.article_detail_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			// startActivityForResult(new Intent(this, PrefActiv.class), PREFS);
			break;
		case R.id.menu_share:
			share();
			break;
		case R.id.menu_share2:
			share();
			break;
		default:
			break;
		}
		return false;
	}

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

	public void loadImage(String imgUrl, ImageView imageView) {

		DisplayImageOptions options;

		options = new DisplayImageOptions.Builder()
				// change these images to error messages
				.showImageForEmptyUri(R.drawable.sandblogo)
				.showImageOnFail(R.drawable.sandblogo).resetViewBeforeLoading()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		// ImageView imageView = (ImageView) getActivity()
		// .findViewById(R.id.image);

		ImageLoaderConfiguration configuration = ImageLoaderConfiguration
				.createDefault(getActivity().getApplicationContext());
		imageLoader.init(configuration);
		imageLoader.displayImage(imgUrl, imageView, options, null);
	}

}

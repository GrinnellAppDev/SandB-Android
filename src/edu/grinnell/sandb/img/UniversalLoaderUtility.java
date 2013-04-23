package edu.grinnell.sandb.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import edu.grinnell.grinnellsandb.R;
import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.ImageTable;

public class UniversalLoaderUtility {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ProgressBar spinner = null;

	public UniversalLoaderUtility() {
	}

	protected SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {
		@Override
		public void onLoadingStarted(String imageUri, View view) {
			spinner.setVisibility(View.VISIBLE);
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			@SuppressWarnings("unused")
			String message = null;
			switch (failReason.getType()) {
			case IO_ERROR:
				message = "Input/Output error";
				break;
			case DECODING_ERROR:
				message = "Image can't be decoded";
				break;
			case NETWORK_DENIED:
				message = "Downloads are denied";
				break;
			case OUT_OF_MEMORY:
				message = "Out Of Memory error";
				break;
			case UNKNOWN:
				message = "Unknown error";
				break;
			}
		}
	};

	// load image based on URL
	public void loadImage(String imgUrl, ImageView imgView, Context context) {

		DisplayImageOptions options;

		options = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY)
				// change these images to error messages
				.showImageForEmptyUri(R.drawable.sandblogo)
				.showImageOnFail(R.drawable.sandblogo).resetViewBeforeLoading()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		spinner = new ProgressBar(context, null,
				android.R.attr.progressBarStyleSmall);

		ImageLoaderConfiguration configuration = ImageLoaderConfiguration
				.createDefault(imgView.getContext().getApplicationContext());
		imageLoader.init(configuration);
		imageLoader.displayImage(imgUrl, imgView, options, listener);
	}

	// load first image from an article, in low res
	public void loadArticleImage(Article a, ImageView imgView, Context context) {

		ImageTable imgTable = new ImageTable(imgView.getContext());
		imgTable.open();

		int id = a.getId();
		String[] URLS = imgTable.findURLSbyArticleId(id);
		imgTable.close();

		try {
			String imgUrl = URLS[0];

			DisplayImageOptions options;

			options = new DisplayImageOptions.Builder()
					.imageScaleType(ImageScaleType.EXACTLY)
					// change these images to error messages
					.showImageForEmptyUri(R.drawable.sandblogo)
					.showImageOnFail(R.drawable.sandblogo)
					.resetViewBeforeLoading().cacheOnDisc()
					.imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new FadeInBitmapDisplayer(300)).build();

			spinner = new ProgressBar(context, null,
					android.R.attr.progressBarStyleSmall);

			ImageLoaderConfiguration configuration = ImageLoaderConfiguration
					.createDefault(imgView.getContext().getApplicationContext());
			imageLoader.init(configuration);
			imageLoader.displayImage(imgUrl, imgView, options, listener);
		} catch (NullPointerException e) {
			imageLoader.displayImage(null, imgView, null, listener);
		}
	}
}

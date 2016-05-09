package edu.grinnell.sandb.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.orm.query.Condition;
import com.orm.query.Select;

public class UniversalLoaderUtility {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ProgressBar spinner = null;

	public UniversalLoaderUtility() {
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	protected SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
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
			Animation a = ((ImageView) view).getAnimation();
			if (a != null)
				view.clearAnimation();
				//a.cancel();
			view.setVisibility(View.GONE);
		}
	};

    public void loadPrimaryArticleImage(Article article, ImageView imgView, Context context) {
        Select<Image> articleImageQuery = Select.from(Image.class)
                .where(Condition.prop("article_Id").eq(article.getArticle_id()));
        Image articleImage = articleImageQuery.first();
        loadImage(articleImage.getURL(), imgView, context);
    }

	// load image based on URL
	public void loadImage(String imgUrl, ImageView imgView, Context context) {

		DisplayImageOptions options;

		options = new DisplayImageOptions.Builder()
				.imageScaleType(ImageScaleType.EXACTLY).cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		spinner = new ProgressBar(context, null,
				android.R.attr.progressBarStyleSmall);

		imageLoader.displayImage(imgUrl, imgView, options, listener);
	}

	// load first image from an article, in low res
	public void loadHiResArticleImage(String imgUrl, ImageView imgView,
			Context context) {

		try {
			// throw exception if no image
			String hiResImgUrl = getHiResImage(imgUrl);

			DisplayImageOptions options;

			options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new FadeInBitmapDisplayer(300)).build();

			//imgView.startAnimation(AnimationUtils.loadAnimation(context,
			//		R.anim.loading));

			spinner = new ProgressBar(context, null,
					android.R.attr.progressBarStyleSmall);

			imageLoader.displayImage(hiResImgUrl, imgView, options, listener);
			imgView.setVisibility(View.VISIBLE);

		} catch (NullPointerException e) {
			// imageLoader.displayImage(null, imgView, null, listener);
			imgView.setVisibility(View.GONE);
		}
	}


	// remove the ends of each image URL to download full sized images
	public String getHiResImage(String lowResImg) {
		if (lowResImg == null)
			return null;
		
		// do nothing if there is no resolution tag
		if (!lowResImg.contains("-")  || !lowResImg.contains("x"))
			return lowResImg;

		int readTo = lowResImg.lastIndexOf("-");

		if (readTo != -1) {
			String hiResImg = lowResImg.substring(0, readTo);
			hiResImg = hiResImg.concat(".jpg");
			return hiResImg;
		} else
			return lowResImg;
	}
}

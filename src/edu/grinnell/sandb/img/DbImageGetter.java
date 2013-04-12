package edu.grinnell.sandb.img;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.animation.Animation;
import android.widget.ImageView;
import edu.grinnell.sandb.Utility;
import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.Image;
import edu.grinnell.sandb.data.ImageTable;

public class DbImageGetter implements ImageGetter {

	private int transactions = 0;
	private ImageTable table;
	private Context c;
	private int maxWidth = 0;
	private int maxHeight = 0;
	
	public DbImageGetter(Context c) {
		this.c = c;
		table = new ImageTable(c);
		table.open();
	}
	
	public DbImageGetter(Context c, int maxWidth, int maxHeight) {
		this(c);
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
	}
	
	@Override
	public Drawable getDrawable(String arg0) {
		return fetchDrawable(arg0);
	}
	
	/***
     * Get the Drawable from URL
     * @param urlString
     * @return
     */
    public Drawable fetchDrawable(String urlString) {
    	Image img = table.findByUrl(urlString);
    	return (img != null) ? img.toDrawable(c) : new BitmapDrawable(c.getResources());
    }
    
    public DbImageGetterAsyncTask fetchDrawableForArticleAsync(Article art, ImageView container) {
    	
    	DbImageGetterAsyncTask asyncTask = 
                new DbImageGetterAsyncTask(container);

        asyncTask.execute(art.getId());  
        return asyncTask;
    }

    public Drawable fetchDrawableForArticle(Article a) {
    	Image img = table.findByArticleId(a.getId());
        return (img == null) ? null : img.toDrawable(c);
    }
    
    public class DbImageGetterAsyncTask extends AsyncTask<Integer, Void, Drawable>  {
        ImageView container;
        
        public DbImageGetterAsyncTask(ImageView container) {
            this.container = container;
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            int id = params[0];
            
            Image img = table.findByArticleId(id);
            
            if (img == null) return null;
            Bitmap bm = img.toBitmap();
            bm = Utility.resizeBitmap(img.toBitmap(), maxWidth, maxHeight);
            
            return new BitmapDrawable(c.getResources(), bm);
        }

        @Override
        protected void onPostExecute(Drawable result) {
        	// Cancel the loading animation
        	Animation ani = container.getAnimation();
        	if (ani != null) {
            	ani.cancel();
        	}
            container.setImageDrawable(result);  
        }	
    }
    
    public void close() {
    	table.close();
    }
}

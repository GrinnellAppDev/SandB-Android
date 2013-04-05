package edu.grinnell.sandb.img;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.widget.ImageView;
import edu.grinnell.sandb.data.Article;
import edu.grinnell.sandb.data.Image;
import edu.grinnell.sandb.data.ImageTable;

public class DbImageGetter implements ImageGetter {

	private Context c;
	
	public DbImageGetter(Context c) {
		this.c = c;
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
    	
    	Image img = lookupInTableByUrl(urlString);
    	return (img != null) ? img.toDrawable(c) : new BitmapDrawable(c.getResources());
    }
    
    public void fetchDrawableForArticleAsync(Article art, ImageView container) {
    	
    	DbImageGetterAsyncTask asyncTask = 
                new DbImageGetterAsyncTask(container);

        asyncTask.execute(art.getId());  
    }

    public Drawable fetchDrawableForArticle(Article a) {
    	Image img = lookupInTableByArticleId(a.getId());
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
            
            Image img = lookupInTableByArticleId(id);
            return (img == null) ? null : img.toDrawable(c);
        }

        @Override
        protected void onPostExecute(Drawable result) {
        	if (result == null)
        		return;
        		        	
        	// set the correct bound according to the result from HTTP call
            //result.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 
            //        + result.getIntrinsicHeight()); 

            container.setImageDrawable(result);            
        }
    }
    
    private Image lookupInTableByUrl(String source) {
    	Image img = null;
    	ImageTable table = new ImageTable(c);
    	table.open();
    	img = table.findByUrl(source);
    	table.close();
    	return img;
    }
    
    private Image lookupInTableByArticleId(int id) {
    	Image img = null;
    	ImageTable table = new ImageTable(c);
    	table.open();
    	img = table.findByArticleId(id);
    	table.close();
    	return img;
    }

    
}

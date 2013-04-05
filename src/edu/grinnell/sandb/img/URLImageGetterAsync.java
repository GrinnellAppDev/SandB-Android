package edu.grinnell.sandb.img;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import edu.grinnell.sandb.Utility;
import edu.grinnell.sandb.data.Image;
import edu.grinnell.sandb.data.ImageTable;

public class URLImageGetterAsync implements ImageGetter {
	
	private static final String URLIMGP = "UrlImageParser";
	
    Context c;
    View container;
    Bitmap bm;
    int maxw;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     * @param t
     * @param c
     */
    public URLImageGetterAsync(View t, Context c, int maxw) {
        this.c = c;
        this.container = t;
        this.maxw = maxw;
    }

    public Drawable getDrawable(String source) {
    	Log.d(URLIMGP, "Getting Image: " + source);
        MutableDrawable urlDrawable = new MutableDrawable(c.getResources());

        // get the actual source
        ImageGetterAsyncTask asyncTask = 
            new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
        MutableDrawable urlDrawable;

        public ImageGetterAsyncTask(MutableDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            
            Bitmap bm = null;
            Image img = lookupInTable(source);
            if (img != null)
            	bm = img.toBitmap();
            else
            	bm = fetchRemoteBitmap(source);
            
            if (bm == null)
            	return null;
            
            if (maxw < bm.getWidth()) {
            	bm = Utility.resizeBitmap(bm, maxw);
            }
            
            return new BitmapDrawable(c.getResources(), bm);
        }

        @Override
        protected void onPostExecute(Drawable result) {
        	if (result == null)
        		return;
        		
        	Log.d(URLIMGP, "ImageLoaded: " + result.toString());
        	
        	// set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 
                    + result.getIntrinsicHeight()); 

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            TextView t = (TextView) URLImageGetterAsync.this.container;
            t.setText(t.getText());
        }

        private Image lookupInTable(String source) {
        	Image img = null;
        	ImageTable table = new ImageTable(c);
        	table.open();
        	img = table.findByUrl(source);
        	table.close();
        	return img;
        }
        
        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Bitmap fetchRemoteBitmap(String urlString) {        	
        	InputStream is  = null;
        	try {
        		is = fetch(urlString);
        		
        	} catch (MalformedURLException e) {
        		e.printStackTrace();
        	} catch (IOException e) {
        		e.printStackTrace();
        	} 
        	
        	return BitmapFactory.decodeStream(is);        	
        }
        
        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Drawable fetchRemoteDrawable(String urlString) {
        	
        	try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 
                        + drawable.getIntrinsicHeight()); 
                return drawable;
            } catch (Exception e) {
            	return null;
            	
            } 
        }

        private InputStream fetch(String urlString) throws MalformedURLException, IOException {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
        }
    }
}

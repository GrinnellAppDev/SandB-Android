package edu.grinnell.sandb.img;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class URLImageGetterAsync implements ImageGetter {
	
	private static final String URLIMGP = "UrlImageParser";
	
    Context c;
    View container;
    Bitmap bm;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     * @param t
     * @param c
     */
    public URLImageGetterAsync(View t, Context c) {
        this.c = c;
        this.container = t;
    }

    public Drawable getDrawable(String source) {
    	Log.d(URLIMGP, "Getting Image: " + source);
        URLDrawable urlDrawable = new URLDrawable(c.getResources());

        // get the actual source
        ImageGetterAsyncTask asyncTask = 
            new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
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

           	// URLImageGetterAsync.this.container.requestLayout();
            TextView t = (TextView) URLImageGetterAsync.this.container;
            t.setText(t.getText());
            
            // redraw the image by invalidating the container
            // URLImageGetterAsync.this.container.invalidate();
        }

        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 
                        + drawable.getIntrinsicHeight()); 
                return drawable;
            } catch (Exception e) {
                //Bitmap bm = BitmapFactory.decodeResource(c.getResources(), R.drawable.sandblogo);
                //return new BitmapDrawable(c.getResources(), bm);
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

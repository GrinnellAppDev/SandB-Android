package edu.grinnell.sandb.img;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.util.Log;

public class URLImageGetter implements ImageGetter {

	@Override
	public Drawable getDrawable(String arg0) {
		return fetchDrawable(arg0);
	}
	
	/***
     * Get the Drawable from URL
     * @param urlString
     * @return
     */
    public static Drawable fetchDrawable(String urlString) {
        try {
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");
            drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 
                    + drawable.getIntrinsicHeight()); 
            return drawable;
        } catch (Exception e) {
        	Log.d("fetchDrawable", "Exception: ", e);
            return null;
        } 
    }

    private static InputStream fetch(String urlString) throws MalformedURLException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

}

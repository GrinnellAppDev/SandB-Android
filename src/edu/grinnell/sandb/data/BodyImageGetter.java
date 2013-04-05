package edu.grinnell.sandb.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class BodyImageGetter {
	
	private static ImageTable mImageTable;
	
	private static int numTasks = 0;
	
	public BodyImageGetter(Context context){
		if (mImageTable == null)
			mImageTable = new ImageTable(context);
	}
	
	public void buildImageCache (Article article){
		new ImageHelper().execute(article);
	}
	
	public class ImageHelper extends AsyncTask<Article, Integer, Integer>{
		
		//TODO on preexecute open table increment variable
		@Override
		protected void onPreExecute () {
			if (numTasks++ == 0)
				mImageTable.open();
		}
		
		@Override
		protected Integer doInBackground(Article... article) {
		
			String body = article[0].getBody();
			int articleId = article[0].getId();
			
	        readImage(body, articleId);
	        return null;
		}
	}
	
	protected void onPostExecute (Integer i) {
		if (--numTasks == 0)
			mImageTable.close();
	}


	// Read an image from the body as a byte array
	public void readImage(String body, int articleID) {
		
		int divStart = 0;
		String url = "";
		byte[] image = null;
		String title = "";
		
		while ((divStart = body.indexOf("<div", divStart+1)) >= 0){
		
		url = getSubstring("src=\"", body, divStart);		
		image = getImage(url, divStart);
		title = getSubstring("title=\"", body, divStart);
		
		mImageTable.createImage(articleID, url, image, title);
		}
	}
	
	//TODO on post execute close table decrement variable

	private static byte[] getImage(String imgSource, int start) {
		// download image as byte array
		URL imgSrc;
		try {
			imgSrc = new URL(imgSource);
			HttpURLConnection conn;
			conn = (HttpURLConnection) imgSrc.openConnection();
			BufferedInputStream in = new BufferedInputStream(
					conn.getInputStream());

			int read = 0;
			ByteArrayBuffer bytesBuffer = new ByteArrayBuffer(1024);
			while ((read = in.read()) != -1) {
				bytesBuffer.append(read);
			}

			in.close();
			byte[] image = bytesBuffer.toByteArray();
			return image;

		} catch (IOException e) {
			Log.d("Image Download Error=", e.toString());
		}
		
		return null;
	}

	// return a string starting immediately after the key, and ending at the
	// first quotation mark
	private static String getSubstring(String key, String body, int start) {
		int subStart = 0; 
		int subEnd = 0;
		String substring = "";

		// start at beginning of link
		subStart = body.indexOf(key, start) + key.length();
		subEnd = body.indexOf("\"", subStart);

		substring = body.substring(subStart, subEnd);
		
		return substring;
	}
}

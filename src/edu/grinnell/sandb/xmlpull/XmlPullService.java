package edu.grinnell.sandb.xmlpull;

import java.io.InputStream;
import java.util.List;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.util.Log;
import edu.grinnell.sandb.xmlpull.WebRequestTask.Result;
import edu.grinnell.sandb.xmlpull.XMLParseTask.Article;

public class XmlPullService extends IntentService {

	private static final String TAG = "XmlPullService";
	
	public static final String DOWNLOAD_FEED = "edu.grinnell.sandb.xmlpull.DOWNLOAD_FEED";
	public static final String RESULT_ACTION = "edu.grinnell.sandb.xmlpull.RESULT_ACTION";
	
	public static final String FEED_URL = "http://www.thesandb.com/feed";
	
	private WebRequestTask mWRT;
	private PendingIntent finished;
	
	public XmlPullService() {
		super(TAG);
		mWRT = null;
	}

	@Override
	protected void onHandleIntent(Intent i) {
		String action = i.getAction();
		Log.d(TAG, "Action: " + action);
		
		if (DOWNLOAD_FEED.equals(action)) {
			
			finished = i.getParcelableExtra(RESULT_ACTION);
			
			if (mWRT == null) {
				Log.d(TAG, "Fetching feed from server..");
				mWRT = new WebRequestTask(this, new WebRequstCallback());
				mWRT.execute(FEED_URL);
			}
		} else {
			Log.d(TAG, "Unknown Intent.  Action = " + action);
		}

	}
	
	private void parseXmlFromStream(InputStream xmlStream) {
		XMLParseTask xpt= new XMLParseTask(this, new DataParsedCallback());
		xpt.execute(xmlStream);
	}
	
	private void finishUp() {
		
		try {
			finished.send();
		} catch (CanceledException e) {
			Log.e(TAG, "Intent canceled by MainActivity..");
			e.printStackTrace();
		}
		
		stopSelf();
	}
	
	private class WebRequstCallback implements WebRequestTask.RetrieveDataListener {
		
		@Override
		public void onRetrieveData(Result result) {
			
			InputStream s = result.getStream();
			Log.d("DataReceived", "resultingStream: " + result.getStream());

			if (s != null)
				parseXmlFromStream(result.getStream());
			else {
				Log.d("DataReceived", "stream is NULL!");
			}
			
			mWRT = null;
		}
	}
	
	private class DataParsedCallback implements XMLParseTask.ParseDataListener {

		@Override
		public void onDataParsed(List<Article> articles) {
			Log.d("ParseDataListener", "Feed parsed!");
			FeedContent.articles = articles;
			finishUp();
		}
		
	}

}

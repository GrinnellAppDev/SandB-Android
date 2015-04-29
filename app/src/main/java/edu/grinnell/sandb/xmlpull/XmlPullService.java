package edu.grinnell.sandb.xmlpull;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.database.SQLException;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import edu.grinnell.sandb.MainPrefs;

public class XmlPullService extends IntentService {

	private static final String TAG = "XmlPullService";
	
	public static final String DOWNLOAD_FEED    = "edu.grinnell.sandb.xmlpull.DOWNLOAD_FEED";
	public static final String CHECK_DOWNLOAD   = "edu.grinnell.sandb.xmlpull.CHECK_DOWNLOAD";
	public static final String RESULT_ACTION    = "edu.grinnell.sandb.xmlpull.RESULT_ACTION";
	public static final String LAST_CHECKED_MS  = "edu.grinnell.sandb.xmlpull.LAST_CHECKED_MS";
	
	public static final String FEED_URL = "http://www.thesandb.com/feed";
	
	private PendingIntent finished;
	
	private MainPrefs mPrefs;
	
	public XmlPullService() {
		super(TAG);
		mPrefs = new MainPrefs(this);
	}

	@Override
	protected void onHandleIntent(Intent i) {
		String action = i.getAction();
		Log.i(TAG, "Action: " + action);
		
		finished = i.getParcelableExtra(RESULT_ACTION);
		if (DOWNLOAD_FEED.equals(action)) {
			finished = i.getParcelableExtra(RESULT_ACTION);
			downloadFeed();
		} else if (CHECK_DOWNLOAD.equals(action)) {
			finished = i.getParcelableExtra(RESULT_ACTION);
			long lastCheckedMs = i.getLongExtra(LAST_CHECKED_MS, 0);
			lastCheckedMs = mPrefs.lastUpdated;
			checkAndDownload(lastCheckedMs);
		} else {
			Log.e(TAG, "Unknown Intent.  Action = " + action);
		}
	}
	
	private void downloadFeed() {

		InputStream feed = XmlFetchTask.downloadDataFromServer(FEED_URL);
		if (feed != null)
			parseXmlFromStream(feed);
		else {
			Log.i("DataReceived", "stream is NULL!");
			finishUp();
		}
	}
	
	private void checkAndDownload(long lastCheckedMs) {

		try {
			long lastModifiedMs = XmlCheckAgeTask.lastModified(FEED_URL);
			
			Calendar lastUpdated = Calendar.getInstance();
			lastUpdated.setTimeInMillis(lastModifiedMs);
			Calendar lastChecked = Calendar.getInstance();
			lastChecked.setTimeInMillis(lastCheckedMs);
			Long now = Calendar.getInstance().getTimeInMillis();
			mPrefs.setLastUpdated(now);
			if (lastChecked.before(lastUpdated) || lastUpdated == null) {
				Log.i(TAG, "Feed out of date, updating..");
				downloadFeed();
			} else {
				AlarmManager m = (AlarmManager) XmlPullService.this.getSystemService(ALARM_SERVICE);
				Intent i = new Intent(CHECK_DOWNLOAD);
				i.putExtra(LAST_CHECKED_MS, now);
				PendingIntent pi = PendingIntent.getService(XmlPullService.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				m.set(AlarmManager.RTC, AlarmManager.INTERVAL_DAY, pi);
				stopSelf();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseXmlFromStream(InputStream xmlStream) {
		
		try {
			XmlParseTask.parseArticlesFromStream(xmlStream, this);
		} catch (IOException ioe) {
			Log.e(TAG, "parseArticlesFromStream", ioe);
		} catch (XmlPullParserException xppe) {
			Log.e(TAG, "parseArticlesFromStream", xppe);
		} catch (SQLException sqle) {
			Log.e(TAG, "SQLExeption", sqle);
		} catch (Exception e) {
			Log.e(TAG, "parseArticlesFromStream", e);
		} finally {
			finishUp();
		}
	}
	
	private void finishUp() {
		mPrefs.setFirstRun(false);
		try {	
			finished.send();
		} catch (CanceledException e) {
			Log.w(TAG, "Intent canceled by MainActivity..");
		} finally {
			stopSelf();
		}
	}

}

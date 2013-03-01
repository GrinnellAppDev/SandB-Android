package edu.grinnell.sandb.xmlpull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.ParseException;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import edu.grinnell.sandb.xmlpull.WebRequestTask.Result;

public class WebRequestTask extends AsyncTask<String, Void, Result> {

	private Context mAppContext;
	private RetrieveDataListener mRetrieveDataListener;
	private ProgressDialog mStatus;
		
	public static final String WRT 	= "WebRequestTask";
	
	public WebRequestTask(Context context, RetrieveDataListener rdl) {
		super();
		mAppContext = context;
		mRetrieveDataListener = rdl;		
	}

	/* Setup the progress bar. */
	@Override
	protected void onPreExecute() {
		//mStatus = ProgressDialog.show(mAppContext,"","Loading Feed...", true);
	}
	
	@Override
	protected Result doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		ConnectivityManager cm = (ConnectivityManager)
				mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		Result r = new Result();
		
		if (!networkEnabled(cm)) 
			return (r.setCode(Result.NO_NETWORK));
		
		InputStream stream = downloadDataFromServer(arg0[0]);

		if (stream == null)
			return r.setCode(Result.NO_DATA);
		
		r.setStream(stream);
		
		return r.setCode(Result.SUCCESS);
	}
	
	/* Stop the dialog and notify the main thread that the new menu
	 * is loaded. */
	@Override
	protected void onPostExecute(Result result) {
		
		Log.i(WRT, "xml loaded from server");
		// dismiss dialogue..
		//mStatus.dismiss();
		
		// notify the UI thread listener ..
		mRetrieveDataListener.onRetrieveData(result);
		
		super.onPostExecute(result);
	}
	
	/* Return true if the device has a network adapter that is capable of 
	 * accessing the network. */
	protected static boolean networkEnabled(ConnectivityManager cm) {
		NetworkInfo n = cm.getActiveNetworkInfo();
		return (n != null) && n.isConnectedOrConnecting();
	}
	
	protected static InputStream downloadDataFromServer(String urlstr) {
		InputStream stream = null;
		try {
			URL url = new URL(urlstr);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        stream = conn.getInputStream();
		} catch (IOException e) {
			Log.e(WRT, e.toString());
			Log.e(WRT, e.getMessage());
		} catch (ParseException p) {
			Log.e("ParseException", p.toString());} 
		
		return stream;
	}

	
	/* Listener you should implement for the callback method in the UI thread
	 * and pass to the constructor of GetMenuTask */
	public interface RetrieveDataListener {
		public void onRetrieveData(Result result);
	}
	
	public class Result {
		
		/* Result Code Constants */
		public static final int UNKNOWN = -1;
		public static final int SUCCESS = 0;
		public static final int NO_NETWORK = 1;
		public static final int NO_ROUTE = 2;
		public static final int HTTP_ERROR = 3;
		public static final int NO_CACHE = 4;
		public static final int NO_DATA = 5;
		
		private int code;
		private InputStream stream;
		
		public Result (int resultCode, InputStream resultStream) {
			code = resultCode;
			stream = resultStream;
		}
		public Result () {this(-1, null);}
		public int getCode() {return code;}
		public InputStream getStream() {return stream;}
		public Result setCode(int c) {code = c; return this;}
		public Result setStream(InputStream s) {stream = s; return this;}
	}
}

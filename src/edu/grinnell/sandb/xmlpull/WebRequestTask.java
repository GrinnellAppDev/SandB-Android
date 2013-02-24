package edu.grinnell.sandb.xmlpull;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
	
	private static final int 		MAX_ATTEMPTS 	= 3;
	
	public static final String WRT 	= "WebRequestTask";
	
	public WebRequestTask(Context context, RetrieveDataListener rdl) {
		super();
		mAppContext = context;
		mRetrieveDataListener = rdl;		
	}

	/* Setup the progress bar. */
	@Override
	protected void onPreExecute() {
		mStatus = ProgressDialog.show(mAppContext,"","Loading Feed...", true);
	}
	
	@Override
	protected Result doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		ConnectivityManager cm = (ConnectivityManager)
				mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		Result r = new Result();
		
		if (!networkEnabled(cm)) 
			return (r.setCode(Result.NO_NETWORK));
		
		String menu = downloadDataFromServer(arg0[0]);

		if (menu == null)
			return r.setCode(Result.NO_DATA);
		else if (menu.equals(Integer.valueOf(Result.HTTP_ERROR).toString())) {
			return r.setCode(Result.HTTP_ERROR);
		}
		
		r.setValue(menu);
		
		return r.setCode(Result.SUCCESS);
	}
	
	/* Stop the dialog and notify the main thread that the new menu
	 * is loaded. */
	@Override
	protected void onPostExecute(Result result) {
		
		Log.i("getMenuTask", "menu loaded from the server");
		
		try {
			// dismiss loading..
			mStatus.dismiss();
			// notify the UI thread listener ..
			mRetrieveDataListener.onRetrieveData(result);
		} catch (Exception e) {
			Log.d("post execute", e.toString());
		}
		
		
		super.onPostExecute(result);
	}
	
	/* Return true if the device has a network adapter that is capable of 
	 * accessing the network. */
	protected static boolean networkEnabled(ConnectivityManager cm) {
		NetworkInfo n = cm.getActiveNetworkInfo();
		return (n != null) && n.isConnectedOrConnecting();
	}
	
	protected static String downloadDataFromServer(String urlstr) {
		String r = null;
		int attempts = 0;
		try {				
			while (attempts < MAX_ATTEMPTS) {
				
				//Log.i(HTTP, request);
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(urlstr);
				
				HttpResponse response = client.execute(post);
				//Log.i(HTTP, response.getStatusLine().toString());
				// Make sure the result is okay.
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					r = EntityUtils.toString(response.getEntity());
					//Log.i(HTTP, "JSON = " + r);
					break;
				}
				attempts++;
			}
		} catch (IOException e) {
			Log.e(WRT, e.toString());
			Log.e(WRT, e.getMessage());
		} catch (ParseException p) {
			Log.e("ParseException", p.toString());} 
		
		return (attempts == MAX_ATTEMPTS) ? Integer.valueOf(Result.HTTP_ERROR).toString() : r;
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
		private String value;
		
		public Result (int resultCode, String resultValue) {
			code = resultCode;
			value = resultValue;
		}
		public Result () {this(-1, null);}
		public int getCode() {return code;}
		public String getValue() {return value;}
		public Result setCode(int c) {code = c; return this;}
		public Result setValue(String v) {value = v; return this;}
	}
}

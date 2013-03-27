package edu.grinnell.sandb.xmlpull;

import edu.grinnell.sandb.ArticleListFragment;
import edu.grinnell.sandb.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class XmlPullReceiver extends BroadcastReceiver {

	private static final String TAG = "Receiver";

	public static final String FEED_PROCESSED = "edu.grinnell.sandb.xmlpull.FEED_PROCESSED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Intent Received: " + intent.getAction());
		if (FEED_PROCESSED.equals(intent.getAction())) {
			Intent i = new Intent(context, MainActivity.class);
			i.setAction(ArticleListFragment.REFRESH);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}

}

package edu.grinnell.sandb;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainPrefs {

	/* SharedPreferences */
	private static SharedPreferences mPrefs = null;
	
	private static final String AUTO_REFRESH = "auto_refresh";
	public boolean autoRefresh = false;
	
	MainPrefs (Context context) {
		if (mPrefs == null)
			mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		refresh();
	}
	
	public void refresh() {
		autoRefresh = mPrefs.getBoolean(AUTO_REFRESH, true);
	}
}

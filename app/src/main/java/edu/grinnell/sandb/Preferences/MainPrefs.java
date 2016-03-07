package edu.grinnell.sandb.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

//store persistent info on when the list was last updated
public class MainPrefs {

    /* SharedPreferences */
    private static SharedPreferences mPrefs = null;

    private static final String AUTO_REFRESH = "auto_refresh";
    private static final String LAST_UPDATED = "last_updated";
    private static final String FIRST_RUN = "first_run";
    private static final String ARTICLE_FONT_SIZE = "article_font_size";
    public boolean autoRefresh;
    public long lastUpdated;
    public boolean firstRun;

    public MainPrefs(Context context) {
        if (mPrefs == null)
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        refresh();
    }

    public void refresh() {
        autoRefresh = mPrefs.getBoolean(AUTO_REFRESH, true);
        lastUpdated = mPrefs.getLong(LAST_UPDATED, 0);
        firstRun = mPrefs.getBoolean(FIRST_RUN, true);
    }

    public void setLastUpdated(long v) {
        Editor e = mPrefs.edit();
        e.putLong(LAST_UPDATED, v);
        e.apply();
    }

    public void setFirstRun(boolean v) {
        Editor e = mPrefs.edit();
        e.putBoolean(FIRST_RUN, v);
        e.apply();
    }

    public int getArticleFontSize() {
        return mPrefs.getInt(ARTICLE_FONT_SIZE, 2);
    }

    public void setArticleFontSize(int fontSize) {
        Editor e = mPrefs.edit();
        e.putInt(ARTICLE_FONT_SIZE, fontSize);
        e.apply();
    }
}

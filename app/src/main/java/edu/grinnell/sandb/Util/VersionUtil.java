package edu.grinnell.sandb.Util;

import android.os.Build;

/**
 * Created by prabir on 3/5/16, AppDev Grinnell.
 */
public class VersionUtil {

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}

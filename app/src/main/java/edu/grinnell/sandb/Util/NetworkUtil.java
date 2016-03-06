package edu.grinnell.sandb.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by prabir on 3/4/16.
 */
public class NetworkUtil {

    /* Return true if the device has a network adapter that is capable of
  * accessing the network. */
    public static boolean isNetworkEnabled(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = cm.getActiveNetworkInfo();
        return (n != null) && n.isConnectedOrConnecting();
    }
}

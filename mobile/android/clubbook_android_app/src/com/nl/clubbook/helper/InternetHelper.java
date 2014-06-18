package com.nl.clubbook.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by odats on 18/06/2014.
 */
public class InternetHelper {
    public static boolean isNetworkConnected(Context context) {
        // http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}

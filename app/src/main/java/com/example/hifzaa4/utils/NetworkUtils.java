package com.example.hifzaa4.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * NetworkUtils - Utility class for network connectivity checks
 * Determines if device has internet access
 */
public class NetworkUtils {

  /**
   * Check if the device has an active network connection
   * 
   * @param context Application context
   * @return true if connected to internet, false otherwise
   */
  public static boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);

    if (connectivityManager == null) {
      return false;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      // For Android 6.0 (API 23) and above
      NetworkCapabilities capabilities = connectivityManager
          .getNetworkCapabilities(connectivityManager.getActiveNetwork());

      if (capabilities != null) {
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
      }
      return false;
    } else {
      // For older versions
      NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
      return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
  }

  /**
   * Check if connected via WiFi
   * 
   * @param context Application context
   * @return true if connected via WiFi
   */
  public static boolean isWifiConnected(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);

    if (connectivityManager == null) {
      return false;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      NetworkCapabilities capabilities = connectivityManager
          .getNetworkCapabilities(connectivityManager.getActiveNetwork());
      return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    } else {
      NetworkInfo wifiNetwork = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      return wifiNetwork != null && wifiNetwork.isConnected();
    }
  }
}

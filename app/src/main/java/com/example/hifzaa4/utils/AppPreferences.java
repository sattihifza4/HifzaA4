package com.example.hifzaa4.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PreferenceManager - Utility class for SharedPreferences management
 * Handles user session and app preferences
 */
public class AppPreferences {

  // SharedPreferences constants
  private static final String PREF_NAME = "app_prefs";
  private static final String KEY_IS_LOGGED_IN = "is_logged_in";
  private static final String KEY_USERNAME = "username";
  private static final String KEY_LAST_SYNC = "last_sync_time";

  private final SharedPreferences preferences;

  /**
   * Constructor
   * 
   * @param context Application context
   */
  public AppPreferences(Context context) {
    preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
  }

  /**
   * Check if user is logged in
   * 
   * @return true if logged in, false otherwise
   */
  public boolean isLoggedIn() {
    return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
  }

  /**
   * Set login status
   * 
   * @param isLoggedIn Login status
   */
  public void setLoggedIn(boolean isLoggedIn) {
    preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
  }

  /**
   * Save username
   * 
   * @param username User's username
   */
  public void setUsername(String username) {
    preferences.edit().putString(KEY_USERNAME, username).apply();
  }

  /**
   * Get saved username
   * 
   * @return Username or empty string if not set
   */
  public String getUsername() {
    return preferences.getString(KEY_USERNAME, "");
  }

  /**
   * Save last API sync timestamp
   * 
   * @param timestamp Sync timestamp in milliseconds
   */
  public void setLastSyncTime(long timestamp) {
    preferences.edit().putLong(KEY_LAST_SYNC, timestamp).apply();
  }

  /**
   * Get last sync timestamp
   * 
   * @return Last sync timestamp or 0 if never synced
   */
  public long getLastSyncTime() {
    return preferences.getLong(KEY_LAST_SYNC, 0);
  }

  /**
   * Clear all user data on logout
   */
  public void clearUserData() {
    preferences.edit()
        .remove(KEY_IS_LOGGED_IN)
        .remove(KEY_USERNAME)
        .apply();
  }

  /**
   * Login user - saves credentials and sets logged in flag
   * 
   * @param username User's username
   */
  public void login(String username) {
    preferences.edit()
        .putBoolean(KEY_IS_LOGGED_IN, true)
        .putString(KEY_USERNAME, username)
        .apply();
  }

  /**
   * Logout user - clears session data
   */
  public void logout() {
    clearUserData();
  }
}

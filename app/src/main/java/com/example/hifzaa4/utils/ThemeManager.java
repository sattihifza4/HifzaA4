package com.example.hifzaa4.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.hifzaa4.R;

/**
 * ThemeManager - Utility class for managing app themes
 * Handles theme switching, persistence, and application
 */
public class ThemeManager {

  // Theme constants
  public static final int THEME_LIGHT = 0;
  public static final int THEME_DARK = 1;
  public static final int THEME_OCEAN = 2;

  // SharedPreferences constants
  private static final String PREF_NAME = "theme_prefs";
  private static final String KEY_THEME = "selected_theme";

  /**
   * Apply the saved theme to an activity
   * Must be called before setContentView() in onCreate()
   * 
   * @param activity The activity to apply theme to
   */
  public static void applyTheme(Activity activity) {
    int theme = getSavedTheme(activity);
    switch (theme) {
      case THEME_DARK:
        activity.setTheme(R.style.Theme_HifzaA4_Dark);
        break;
      case THEME_OCEAN:
        activity.setTheme(R.style.Theme_HifzaA4_Ocean);
        break;
      case THEME_LIGHT:
      default:
        activity.setTheme(R.style.Theme_HifzaA4_Light);
        break;
    }
  }

  /**
   * Save the selected theme to SharedPreferences
   * 
   * @param context Application context
   * @param theme   Theme constant (THEME_LIGHT, THEME_DARK, or THEME_OCEAN)
   */
  public static void saveTheme(Context context, int theme) {
    SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    prefs.edit().putInt(KEY_THEME, theme).apply();
  }

  /**
   * Get the currently saved theme
   * 
   * @param context Application context
   * @return Theme constant (defaults to THEME_LIGHT if not set)
   */
  public static int getSavedTheme(Context context) {
    SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    return prefs.getInt(KEY_THEME, THEME_LIGHT);
  }

  /**
   * Get the theme name as a string for display
   * 
   * @param context Application context
   * @param theme   Theme constant
   * @return Theme name string
   */
  public static String getThemeName(Context context, int theme) {
    switch (theme) {
      case THEME_DARK:
        return context.getString(R.string.theme_dark);
      case THEME_OCEAN:
        return context.getString(R.string.theme_ocean);
      case THEME_LIGHT:
      default:
        return context.getString(R.string.theme_light);
    }
  }

  /**
   * Change theme and recreate activity to apply changes
   * 
   * @param activity The activity to recreate
   * @param theme    Theme constant
   */
  public static void setThemeAndRecreate(Activity activity, int theme) {
    saveTheme(activity, theme);
    activity.recreate();
  }
}

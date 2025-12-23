package com.example.hifzaa4.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DatabaseHelper - SQLite database creation and version management
 * Handles table creation and database upgrades
 */
public class DatabaseHelper extends SQLiteOpenHelper {

  // Database info
  private static final String DATABASE_NAME = "data_viewer.db";
  private static final int DATABASE_VERSION = 2; // Incremented version

  // Table names
  public static final String TABLE_POSTS = "posts";
  public static final String TABLE_USERS = "users";

  // Posts Column names
  public static final String COLUMN_ID = "id";
  public static final String COLUMN_USER_ID = "user_id";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_BODY = "body";
  public static final String COLUMN_IS_FAVORITE = "is_favorite";

  // Users Column names
  public static final String COLUMN_USERNAME = "username";
  public static final String COLUMN_PASSWORD = "password";

  // Table creation SQL statements
  private static final String CREATE_TABLE_POSTS = "CREATE TABLE " + TABLE_POSTS + " (" +
      COLUMN_ID + " INTEGER PRIMARY KEY, " +
      COLUMN_USER_ID + " INTEGER NOT NULL, " +
      COLUMN_TITLE + " TEXT NOT NULL, " +
      COLUMN_BODY + " TEXT NOT NULL, " +
      COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0" +
      ");";

  private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
      COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // Local ID
      COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
      COLUMN_PASSWORD + " TEXT NOT NULL" +
      ");";

  // Singleton instance
  private static DatabaseHelper instance;

  /**
   * Get singleton instance
   * 
   * @param context Application context
   * @return DatabaseHelper instance
   */
  public static synchronized DatabaseHelper getInstance(Context context) {
    if (instance == null) {
      instance = new DatabaseHelper(context.getApplicationContext());
    }
    return instance;
  }

  /**
   * Private constructor (use getInstance())
   * 
   * @param context Application context
   */
  private DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Create tables
    db.execSQL(CREATE_TABLE_POSTS);
    db.execSQL(CREATE_TABLE_USERS);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Simple upgrade strategy: drop and recreate
    // For production, implement proper migration
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
    onCreate(db);
  }

  @Override
  public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onUpgrade(db, oldVersion, newVersion);
  }
}

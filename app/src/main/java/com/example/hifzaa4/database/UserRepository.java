package com.example.hifzaa4.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * UserRepository - Handles database operations for User entity
 */
public class UserRepository {

  private final DatabaseHelper dbHelper;

  public UserRepository(Context context) {
    dbHelper = DatabaseHelper.getInstance(context);
  }

  /**
   * Register a new user
   * 
   * @param username Username
   * @param password Password
   * @return true if registration successful, false otherwise
   */
  public boolean registerUser(String username, String password) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(DatabaseHelper.COLUMN_USERNAME, username);
    values.put(DatabaseHelper.COLUMN_PASSWORD, password);

    long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
    return result != -1;
  }

  /**
   * Login user
   * 
   * @param username Username
   * @param password Password
   * @return true if credentials match, false otherwise
   */
  public boolean loginUser(String username, String password) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    String[] columns = { DatabaseHelper.COLUMN_ID };
    String selection = DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
    String[] selectionArgs = { username, password };

    try (Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
      return cursor.getCount() > 0;
    }
  }

  /**
   * Check if username already exists
   * 
   * @param username Username to check
   * @return true if exists, false otherwise
   */
  public boolean checkUsernameExists(String username) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    String[] columns = { DatabaseHelper.COLUMN_ID };
    String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
    String[] selectionArgs = { username };

    try (Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null)) {
      return cursor.getCount() > 0;
    }
  }
}

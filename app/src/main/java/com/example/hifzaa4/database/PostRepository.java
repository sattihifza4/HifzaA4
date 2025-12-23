package com.example.hifzaa4.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.hifzaa4.models.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * PostRepository - Repository class for Post CRUD operations
 * Provides data access layer for Posts table
 */
public class PostRepository {

  private final DatabaseHelper dbHelper;

  /**
   * Constructor
   * 
   * @param context Application context
   */
  public PostRepository(Context context) {
    dbHelper = DatabaseHelper.getInstance(context);
  }

  /**
   * Insert a single post
   * 
   * @param post Post to insert
   * @return Row ID of inserted post, or -1 if error
   */
  public long insertPost(Post post) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = createContentValues(post);

    // Use REPLACE to handle conflicts (update if exists)
    return db.insertWithOnConflict(
        DatabaseHelper.TABLE_POSTS,
        null,
        values,
        SQLiteDatabase.CONFLICT_REPLACE);
  }

  /**
   * Insert multiple posts (bulk insert)
   * 
   * @param posts List of posts to insert
   */
  public void insertPosts(List<Post> posts) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.beginTransaction();

    try {
      for (Post post : posts) {
        ContentValues values = createContentValues(post);
        db.insertWithOnConflict(
            DatabaseHelper.TABLE_POSTS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE);
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * Get all posts
   * 
   * @return List of all posts
   */
  public List<Post> getAllPosts() {
    List<Post> posts = new ArrayList<>();
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Cursor cursor = db.query(
        DatabaseHelper.TABLE_POSTS,
        null,
        null,
        null,
        null,
        null,
        DatabaseHelper.COLUMN_ID + " DESC");

    try {
      while (cursor.moveToNext()) {
        posts.add(cursorToPost(cursor));
      }
    } finally {
      cursor.close();
    }

    return posts;
  }

  /**
   * Get a single post by ID
   * 
   * @param postId Post ID
   * @return Post or null if not found
   */
  public Post getPostById(int postId) {
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Cursor cursor = db.query(
        DatabaseHelper.TABLE_POSTS,
        null,
        DatabaseHelper.COLUMN_ID + " = ?",
        new String[] { String.valueOf(postId) },
        null,
        null,
        null);

    try {
      if (cursor.moveToFirst()) {
        return cursorToPost(cursor);
      }
    } finally {
      cursor.close();
    }

    return null;
  }

  /**
   * Update a post
   * 
   * @param post Post with updated data
   * @return Number of rows affected
   */
  public int updatePost(Post post) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = createContentValues(post);

    return db.update(
        DatabaseHelper.TABLE_POSTS,
        values,
        DatabaseHelper.COLUMN_ID + " = ?",
        new String[] { String.valueOf(post.getId()) });
  }

  /**
   * Delete a post by ID
   * 
   * @param postId Post ID to delete
   * @return Number of rows affected
   */
  public int deletePost(int postId) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    return db.delete(
        DatabaseHelper.TABLE_POSTS,
        DatabaseHelper.COLUMN_ID + " = ?",
        new String[] { String.valueOf(postId) });
  }

  /**
   * Delete all posts
   */
  public void deleteAllPosts() {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    db.delete(DatabaseHelper.TABLE_POSTS, null, null);
  }

  /**
   * Get count of posts
   * 
   * @return Number of posts in database
   */
  public int getPostCount() {
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery(
        "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_POSTS,
        null);

    try {
      if (cursor.moveToFirst()) {
        return cursor.getInt(0);
      }
    } finally {
      cursor.close();
    }

    return 0;
  }

  /**
   * Get favorite posts
   * 
   * @return List of favorite posts
   */
  public List<Post> getFavoritePosts() {
    List<Post> posts = new ArrayList<>();
    SQLiteDatabase db = dbHelper.getReadableDatabase();

    Cursor cursor = db.query(
        DatabaseHelper.TABLE_POSTS,
        null,
        DatabaseHelper.COLUMN_IS_FAVORITE + " = ?",
        new String[] { "1" },
        null,
        null,
        DatabaseHelper.COLUMN_ID + " DESC");

    try {
      while (cursor.moveToNext()) {
        posts.add(cursorToPost(cursor));
      }
    } finally {
      cursor.close();
    }

    return posts;
  }

  /**
   * Toggle favorite status of a post
   * 
   * @param postId     Post ID
   * @param isFavorite New favorite status
   */
  public void toggleFavorite(int postId, boolean isFavorite) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(DatabaseHelper.COLUMN_IS_FAVORITE, isFavorite ? 1 : 0);

    db.update(
        DatabaseHelper.TABLE_POSTS,
        values,
        DatabaseHelper.COLUMN_ID + " = ?",
        new String[] { String.valueOf(postId) });
  }

  // Helper method to create ContentValues from Post
  private ContentValues createContentValues(Post post) {
    ContentValues values = new ContentValues();
    values.put(DatabaseHelper.COLUMN_ID, post.getId());
    values.put(DatabaseHelper.COLUMN_USER_ID, post.getUserId());
    values.put(DatabaseHelper.COLUMN_TITLE, post.getTitle());
    values.put(DatabaseHelper.COLUMN_BODY, post.getBody());
    values.put(DatabaseHelper.COLUMN_IS_FAVORITE, post.isFavorite() ? 1 : 0);
    return values;
  }

  // Helper method to convert Cursor to Post
  private Post cursorToPost(Cursor cursor) {
    return new Post(
        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID)),
        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BODY)),
        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_FAVORITE)) == 1);
  }
}

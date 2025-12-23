package com.example.hifzaa4.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Post - Data model for API posts
 * Implements Parcelable for passing between activities via Intent
 */
public class Post implements Parcelable {

  // Fields matching JSONPlaceholder API structure
  private int id;
  private int userId;
  private String title;
  private String body;

  // Additional local fields
  private boolean isFavorite;

  /**
   * Default constructor
   */
  public Post() {
  }

  /**
   * Full constructor
   */
  public Post(int id, int userId, String title, String body, boolean isFavorite) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.isFavorite = isFavorite;
  }

  /**
   * Constructor for new posts (without id)
   */
  public Post(int userId, String title, String body) {
    this.userId = userId;
    this.title = title;
    this.body = body;
    this.isFavorite = false;
  }

  // Parcelable implementation
  protected Post(Parcel in) {
    id = in.readInt();
    userId = in.readInt();
    title = in.readString();
    body = in.readString();
    isFavorite = in.readByte() != 0;
  }

  public static final Creator<Post> CREATOR = new Creator<Post>() {
    @Override
    public Post createFromParcel(Parcel in) {
      return new Post(in);
    }

    @Override
    public Post[] newArray(int size) {
      return new Post[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(id);
    dest.writeInt(userId);
    dest.writeString(title);
    dest.writeString(body);
    dest.writeByte((byte) (isFavorite ? 1 : 0));
  }

  // Getters and Setters
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public boolean isFavorite() {
    return isFavorite;
  }

  public void setFavorite(boolean favorite) {
    isFavorite = favorite;
  }

  @Override
  public String toString() {
    return "Post{" +
        "id=" + id +
        ", userId=" + userId +
        ", title='" + title + '\'' +
        ", body='" + body + '\'' +
        ", isFavorite=" + isFavorite +
        '}';
  }
}

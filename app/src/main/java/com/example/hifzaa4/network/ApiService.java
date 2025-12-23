package com.example.hifzaa4.network;

import android.os.Handler;
import android.os.Looper;

import com.example.hifzaa4.models.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ApiService - Network layer for fetching data from JSONPlaceholder API
 * Uses HttpURLConnection for REST API calls
 */
public class ApiService {

  // API base URL
  private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
  private static final String POSTS_ENDPOINT = "/posts";

  // Connection settings
  private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
  private static final int READ_TIMEOUT = 15000; // 15 seconds

  // Executor for background tasks
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());

  /**
   * Callback interface for API responses
   */
  public interface ApiCallback<T> {
    void onSuccess(T result);

    void onError(String errorMessage);
  }

  /**
   * Fetch all posts from API
   * 
   * @param callback Callback for results
   */
  public void fetchPosts(ApiCallback<List<Post>> callback) {
    executor.execute(() -> {
      HttpURLConnection connection = null;
      BufferedReader reader = null;

      try {
        // Create connection
        URL url = new URL(BASE_URL + POSTS_ENDPOINT);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestProperty("Accept", "application/json");

        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
          notifyError(callback, "Server error: " + responseCode);
          return;
        }

        // Read response
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }

        // Parse JSON
        List<Post> posts = parsePostsJson(response.toString());
        notifySuccess(callback, posts);

      } catch (IOException e) {
        notifyError(callback, "Network error: " + e.getMessage());
      } catch (JSONException e) {
        notifyError(callback, "JSON parsing error: " + e.getMessage());
      } finally {
        // Clean up resources
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if (connection != null) {
          connection.disconnect();
        }
      }
    });
  }

  /**
   * Fetch a single post by ID
   * 
   * @param postId   Post ID
   * @param callback Callback for result
   */
  public void fetchPostById(int postId, ApiCallback<Post> callback) {
    executor.execute(() -> {
      HttpURLConnection connection = null;
      BufferedReader reader = null;

      try {
        URL url = new URL(BASE_URL + POSTS_ENDPOINT + "/" + postId);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
          notifyError(callback, "Server error: " + responseCode);
          return;
        }

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }

        Post post = parsePostJson(new JSONObject(response.toString()));
        notifySuccess(callback, post);

      } catch (IOException e) {
        notifyError(callback, "Network error: " + e.getMessage());
      } catch (JSONException e) {
        notifyError(callback, "JSON parsing error: " + e.getMessage());
      } finally {
        if (reader != null) {
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if (connection != null) {
          connection.disconnect();
        }
      }
    });
  }

  /**
   * Parse JSON array into list of Posts
   */
  private List<Post> parsePostsJson(String json) throws JSONException {
    List<Post> posts = new ArrayList<>();
    JSONArray jsonArray = new JSONArray(json);

    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject jsonObject = jsonArray.getJSONObject(i);
      posts.add(parsePostJson(jsonObject));
    }

    return posts;
  }

  /**
   * Parse JSON object into Post
   */
  private Post parsePostJson(JSONObject jsonObject) throws JSONException {
    return new Post(
        jsonObject.getInt("id"),
        jsonObject.getInt("userId"),
        jsonObject.getString("title"),
        jsonObject.getString("body"),
        false // Default not favorite
    );
  }

  /**
   * Notify success on main thread
   */
  private <T> void notifySuccess(ApiCallback<T> callback, T result) {
    mainHandler.post(() -> callback.onSuccess(result));
  }

  /**
   * Notify error on main thread
   */
  private <T> void notifyError(ApiCallback<T> callback, String error) {
    mainHandler.post(() -> callback.onError(error));
  }

  /**
   * Shutdown executor service
   */
  public void shutdown() {
    executor.shutdown();
  }
}

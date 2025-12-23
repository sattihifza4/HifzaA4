package com.example.hifzaa4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hifzaa4.R;
import com.example.hifzaa4.adapters.PostAdapter;
import com.example.hifzaa4.database.PostRepository;
import com.example.hifzaa4.models.Post;
import com.example.hifzaa4.network.ApiService;
import com.example.hifzaa4.utils.AppPreferences;
import com.example.hifzaa4.utils.NetworkUtils;
import com.example.hifzaa4.utils.ThemeManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity - Main posts list screen
 * Displays posts with RecyclerView, handles offline mode, theme switching
 */
public class MainActivity extends AppCompatActivity implements
    PostAdapter.OnPostClickListener,
    PostAdapter.OnPostLongClickListener,
    PostAdapter.OnPostMenuClickListener {

  // UI Elements
  private Toolbar toolbar;
  private SwipeRefreshLayout swipeRefresh;
  private RecyclerView rvPosts;
  private LinearLayout layoutEmpty;
  private ProgressBar progressBar;
  private TextView tvOfflineBanner;
  private FloatingActionButton fabAdd;

  // Data
  private PostAdapter adapter;
  private PostRepository repository;
  private ApiService apiService;
  private AppPreferences preferences;
  private List<Post> postsList = new ArrayList<>();

  // State
  private boolean isOfflineMode = false;
  private int contextMenuPosition = -1;

  // Request codes
  private static final int REQUEST_ADD_POST = 100;
  private static final int REQUEST_EDIT_POST = 101;
  private static final int REQUEST_VIEW_DETAIL = 102;

  // State keys
  private static final String KEY_POSTS = "posts_list";
  private static final String KEY_OFFLINE_MODE = "offline_mode";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Apply theme before super.onCreate()
    ThemeManager.applyTheme(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Initialize
    initViews();
    setupToolbar();
    setupRecyclerView();
    setupSwipeRefresh();
    setupFab();

    // Initialize data sources
    repository = new PostRepository(this);
    apiService = new ApiService();
    preferences = new AppPreferences(this);

    // Load data
    if (savedInstanceState != null) {
      // Restore state after configuration change
      ArrayList<Post> savedPosts = savedInstanceState.getParcelableArrayList(KEY_POSTS);
      isOfflineMode = savedInstanceState.getBoolean(KEY_OFFLINE_MODE, false);
      if (savedPosts != null && !savedPosts.isEmpty()) {
        postsList = savedPosts;
        adapter.setPosts(postsList);
        updateEmptyState();
      } else {
        loadData();
      }
    } else {
      loadData();
    }

    updateOfflineBanner();
  }

  /**
   * Initialize view references
   */
  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    swipeRefresh = findViewById(R.id.swipe_refresh);
    rvPosts = findViewById(R.id.rv_posts);
    layoutEmpty = findViewById(R.id.layout_empty);
    progressBar = findViewById(R.id.progress_bar);
    tvOfflineBanner = findViewById(R.id.tv_offline_banner);
    fabAdd = findViewById(R.id.fab_add);
  }

  /**
   * Setup toolbar
   */
  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setTitle(R.string.title_posts);
    }
  }

  /**
   * Setup RecyclerView with adapter
   */
  private void setupRecyclerView() {
    adapter = new PostAdapter();
    adapter.setOnPostClickListener(this);
    adapter.setOnPostLongClickListener(this);
    adapter.setOnPostMenuClickListener(this);

    rvPosts.setLayoutManager(new LinearLayoutManager(this));
    rvPosts.setAdapter(adapter);

    // Register for context menu
    registerForContextMenu(rvPosts);
  }

  /**
   * Setup swipe to refresh
   */
  private void setupSwipeRefresh() {
    swipeRefresh.setColorSchemeResources(
        R.color.light_primary,
        R.color.light_secondary);
    swipeRefresh.setOnRefreshListener(this::refreshData);
  }

  /**
   * Setup FAB for adding new posts
   */
  private void setupFab() {
    fabAdd.setOnClickListener(v -> {
      Intent intent = new Intent(this, EditPostActivity.class);
      startActivityForResult(intent, REQUEST_ADD_POST);
    });
  }

  /**
   * Load data - from API if online, from SQLite if offline
   */
  private void loadData() {
    if (NetworkUtils.isNetworkAvailable(this)) {
      isOfflineMode = false;
      fetchFromApi();
    } else {
      isOfflineMode = true;
      loadFromDatabase();
    }
    updateOfflineBanner();
  }

  /**
   * Refresh data from API
   */
  private void refreshData() {
    if (NetworkUtils.isNetworkAvailable(this)) {
      isOfflineMode = false;
      fetchFromApi();
    } else {
      isOfflineMode = true;
      swipeRefresh.setRefreshing(false);
      Toast.makeText(this, R.string.error_network, Toast.LENGTH_SHORT).show();
      loadFromDatabase();
    }
    updateOfflineBanner();
  }

  /**
   * Fetch posts from API
   */
  private void fetchFromApi() {
    showLoading(true);

    apiService.fetchPosts(new ApiService.ApiCallback<List<Post>>() {
      @Override
      public void onSuccess(List<Post> result) {
        showLoading(false);
        swipeRefresh.setRefreshing(false);

        // Save to database on background thread
        new Thread(() -> {
          repository.deleteAllPosts();
          repository.insertPosts(result);
          preferences.setLastSyncTime(System.currentTimeMillis());
        }).start();

        // Update UI
        postsList = result;
        adapter.setPosts(postsList);
        updateEmptyState();

        Toast.makeText(MainActivity.this, R.string.refresh_success, Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onError(String errorMessage) {
        showLoading(false);
        swipeRefresh.setRefreshing(false);

        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

        // Fallback to local data
        loadFromDatabase();
      }
    });
  }

  /**
   * Load posts from SQLite database
   */
  private void loadFromDatabase() {
    postsList = repository.getAllPosts();
    adapter.setPosts(postsList);
    updateEmptyState();
    showLoading(false);
  }

  /**
   * Show/hide loading indicator
   */
  private void showLoading(boolean show) {
    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    if (show) {
      layoutEmpty.setVisibility(View.GONE);
    }
  }

  /**
   * Update empty state visibility
   */
  private void updateEmptyState() {
    layoutEmpty.setVisibility(postsList.isEmpty() ? View.VISIBLE : View.GONE);
    rvPosts.setVisibility(postsList.isEmpty() ? View.GONE : View.VISIBLE);
  }

  /**
   * Update offline banner visibility
   */
  private void updateOfflineBanner() {
    tvOfflineBanner.setVisibility(isOfflineMode ? View.VISIBLE : View.GONE);
  }

  // ==================== Menu Implementation ====================

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();

    if (itemId == R.id.action_refresh) {
      swipeRefresh.setRefreshing(true);
      refreshData();
      return true;
    } else if (itemId == R.id.theme_light) {
      ThemeManager.setThemeAndRecreate(this, ThemeManager.THEME_LIGHT);
      return true;
    } else if (itemId == R.id.theme_dark) {
      ThemeManager.setThemeAndRecreate(this, ThemeManager.THEME_DARK);
      return true;
    } else if (itemId == R.id.theme_ocean) {
      ThemeManager.setThemeAndRecreate(this, ThemeManager.THEME_OCEAN);
      return true;
    } else if (itemId == R.id.action_logout) {
      showLogoutConfirmation();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Show logout confirmation dialog
   */
  private void showLogoutConfirmation() {
    new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_logout_title)
        .setMessage(R.string.dialog_logout_message)
        .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
          preferences.logout();
          Intent intent = new Intent(this, LoginActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
          startActivity(intent);
          finish();
        })
        .setNegativeButton(R.string.btn_no, null)
        .show();
  }

  // ==================== Context Menu ====================

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    if (contextMenuPosition >= 0) {
      getMenuInflater().inflate(R.menu.menu_context, menu);
      Post post = adapter.getPostAt(contextMenuPosition);
      if (post != null) {
        menu.setHeaderTitle(post.getTitle());
      }
    }
  }

  @Override
  public boolean onContextItemSelected(@NonNull MenuItem item) {
    if (contextMenuPosition < 0)
      return false;

    Post post = adapter.getPostAt(contextMenuPosition);
    if (post == null)
      return false;

    int itemId = item.getItemId();
    if (itemId == R.id.action_edit) {
      editPost(post, contextMenuPosition);
      return true;
    } else if (itemId == R.id.action_delete) {
      confirmDeletePost(post, contextMenuPosition);
      return true;
    } else if (itemId == R.id.action_view_web) {
      openWebView(post);
      return true;
    }

    return super.onContextItemSelected(item);
  }

  // ==================== Adapter Callbacks ====================

  @Override
  public void onPostClick(Post post, int position) {
    Intent intent = new Intent(this, DetailActivity.class);
    intent.putExtra("post", post);
    intent.putExtra("position", position);
    startActivityForResult(intent, REQUEST_VIEW_DETAIL);
  }

  @Override
  public void onPostLongClick(Post post, int position, View view) {
    contextMenuPosition = position;
    openContextMenu(view);
  }

  @Override
  public void onEditClick(Post post, int position) {
    editPost(post, position);
  }

  @Override
  public void onDeleteClick(Post post, int position) {
    confirmDeletePost(post, position);
  }

  @Override
  public void onShareClick(Post post, int position) {
    sharePost(post);
  }

  @Override
  public void onFavoriteClick(Post post, int position) {
    toggleFavorite(post, position);
  }

  @Override
  public void onWebViewClick(Post post, int position) {
    openWebView(post);
  }

  // ==================== Post Actions ====================

  private void editPost(Post post, int position) {
    Intent intent = new Intent(this, EditPostActivity.class);
    intent.putExtra("post", post);
    intent.putExtra("position", position);
    startActivityForResult(intent, REQUEST_EDIT_POST);
  }

  private void confirmDeletePost(Post post, int position) {
    new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_delete_title)
        .setMessage(R.string.dialog_delete_message)
        .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
          repository.deletePost(post.getId());
          adapter.removePostAt(position);
          postsList.remove(position);
          updateEmptyState();
          Toast.makeText(this, R.string.post_deleted, Toast.LENGTH_SHORT).show();
        })
        .setNegativeButton(R.string.btn_no, null)
        .show();
  }

  private void sharePost(Post post) {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
    shareIntent.putExtra(Intent.EXTRA_TEXT, post.getTitle() + "\n\n" + post.getBody());
    startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_share)));
  }

  private void toggleFavorite(Post post, int position) {
    post.setFavorite(!post.isFavorite());
    repository.toggleFavorite(post.getId(), post.isFavorite());
    adapter.updatePostAt(position, post);

    String message = post.isFavorite() ? "Added to favorites" : "Removed from favorites";
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  private void openWebView(Post post) {
    Intent intent = new Intent(this, WebViewActivity.class);
    intent.putExtra("url", "https://jsonplaceholder.typicode.com/posts/" + post.getId());
    intent.putExtra("title", post.getTitle());
    startActivity(intent);
  }

  // ==================== Activity Result ====================

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (resultCode == RESULT_OK) {
      // Reload data after add/edit
      loadFromDatabase();
    }
  }

  // ==================== Lifecycle ====================

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(KEY_POSTS, new ArrayList<>(postsList));
    outState.putBoolean(KEY_OFFLINE_MODE, isOfflineMode);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (apiService != null) {
      apiService.shutdown();
    }
  }
}

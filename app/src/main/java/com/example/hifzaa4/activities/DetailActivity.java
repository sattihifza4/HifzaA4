package com.example.hifzaa4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hifzaa4.R;
import com.example.hifzaa4.database.PostRepository;
import com.example.hifzaa4.models.Post;
import com.example.hifzaa4.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;

/**
 * DetailActivity - Displays full post details
 * Shows complete post information with edit/delete/webview actions
 */
public class DetailActivity extends AppCompatActivity {

  // UI Elements
  private Toolbar toolbar;
  private TextView tvPostId;
  private TextView tvUserId;
  private TextView tvTitle;
  private TextView tvBody;
  private LinearLayout layoutFavorite;
  private MaterialButton btnEdit;
  private MaterialButton btnDelete;
  private MaterialButton btnViewWeb;

  // Data
  private Post post;
  private int position;
  private PostRepository repository;
  private boolean isModified = false;

  // Request codes
  private static final int REQUEST_EDIT = 100;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Apply theme
    ThemeManager.applyTheme(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);

    // Get intent data
    post = getIntent().getParcelableExtra("post");
    position = getIntent().getIntExtra("position", -1);

    if (post == null) {
      Toast.makeText(this, "Error loading post", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    // Initialize
    repository = new PostRepository(this);
    initViews();
    setupToolbar();
    displayPost();
    setupButtons();
  }

  /**
   * Initialize view references
   */
  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    tvPostId = findViewById(R.id.tv_post_id);
    tvUserId = findViewById(R.id.tv_user_id);
    tvTitle = findViewById(R.id.tv_title);
    tvBody = findViewById(R.id.tv_body);
    layoutFavorite = findViewById(R.id.layout_favorite);
    btnEdit = findViewById(R.id.btn_edit);
    btnDelete = findViewById(R.id.btn_delete);
    btnViewWeb = findViewById(R.id.btn_view_web);
  }

  /**
   * Setup toolbar with back navigation
   */
  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(R.string.title_detail);
    }
    toolbar.setNavigationOnClickListener(v -> onBackPressed());
  }

  /**
   * Display post data in views
   */
  private void displayPost() {
    tvPostId.setText(getString(R.string.label_post_id, post.getId()));
    tvUserId.setText(getString(R.string.label_user_id, post.getUserId()));
    tvTitle.setText(post.getTitle());
    tvBody.setText(post.getBody());
    layoutFavorite.setVisibility(post.isFavorite() ? View.VISIBLE : View.GONE);
  }

  /**
   * Setup button click listeners
   */
  private void setupButtons() {
    // Edit button
    btnEdit.setOnClickListener(v -> {
      Intent intent = new Intent(this, EditPostActivity.class);
      intent.putExtra("post", post);
      intent.putExtra("position", position);
      startActivityForResult(intent, REQUEST_EDIT);
    });

    // Delete button
    btnDelete.setOnClickListener(v -> confirmDelete());

    // WebView button
    btnViewWeb.setOnClickListener(v -> {
      Intent intent = new Intent(this, WebViewActivity.class);
      intent.putExtra("url", "https://jsonplaceholder.typicode.com/posts/" + post.getId());
      intent.putExtra("title", post.getTitle());
      startActivity(intent);
    });
  }

  /**
   * Show delete confirmation dialog
   */
  private void confirmDelete() {
    new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_delete_title)
        .setMessage(R.string.dialog_delete_message)
        .setPositiveButton(R.string.btn_yes, (dialog, which) -> {
          repository.deletePost(post.getId());
          Toast.makeText(this, R.string.post_deleted, Toast.LENGTH_SHORT).show();
          isModified = true;
          setResult(RESULT_OK);
          finish();
        })
        .setNegativeButton(R.string.btn_no, null)
        .show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
      // Reload post data after edit
      isModified = true;
      Post updatedPost = repository.getPostById(post.getId());
      if (updatedPost != null) {
        post = updatedPost;
        displayPost();
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (isModified) {
      setResult(RESULT_OK);
    }
    super.onBackPressed();
  }
}

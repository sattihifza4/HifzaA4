package com.example.hifzaa4.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hifzaa4.R;
import com.example.hifzaa4.database.PostRepository;
import com.example.hifzaa4.models.Post;
import com.example.hifzaa4.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * EditPostActivity - Create or edit a post
 * Demonstrates input controls: EditText, Spinner, Switch, Buttons
 */
public class EditPostActivity extends AppCompatActivity {

  // UI Elements
  private Toolbar toolbar;
  private Spinner spinnerUser;
  private TextInputLayout tilTitle;
  private TextInputLayout tilBody;
  private TextInputEditText etTitle;
  private TextInputEditText etBody;
  private SwitchMaterial switchFavorite;
  private MaterialButton btnCancel;
  private MaterialButton btnSave;

  // Data
  private Post post;
  private int position;
  private boolean isEditMode = false;
  private PostRepository repository;

  // State keys
  private static final String KEY_TITLE = "title";
  private static final String KEY_BODY = "body";
  private static final String KEY_USER_ID = "user_id";
  private static final String KEY_FAVORITE = "favorite";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Apply theme
    ThemeManager.applyTheme(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_post);

    // Initialize repository
    repository = new PostRepository(this);

    // Get intent data
    post = getIntent().getParcelableExtra("post");
    position = getIntent().getIntExtra("position", -1);
    isEditMode = (post != null);

    initViews();
    setupToolbar();
    setupSpinner();
    setupButtons();

    // Restore state or populate form
    if (savedInstanceState != null) {
      restoreState(savedInstanceState);
    } else if (isEditMode) {
      populateForm();
    }
  }

  /**
   * Initialize view references
   */
  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    spinnerUser = findViewById(R.id.spinner_user);
    tilTitle = findViewById(R.id.til_title);
    tilBody = findViewById(R.id.til_body);
    etTitle = findViewById(R.id.et_title);
    etBody = findViewById(R.id.et_body);
    switchFavorite = findViewById(R.id.switch_favorite);
    btnCancel = findViewById(R.id.btn_cancel);
    btnSave = findViewById(R.id.btn_save);
  }

  /**
   * Setup toolbar
   */
  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(isEditMode ? R.string.title_edit_post : R.string.title_new_post);
    }
    toolbar.setNavigationOnClickListener(v -> onBackPressed());
  }

  /**
   * Setup user selection spinner
   */
  private void setupSpinner() {
    // Create user options (User 1 to User 10)
    String[] users = new String[10];
    for (int i = 0; i < 10; i++) {
      users[i] = "User " + (i + 1);
    }

    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this,
        android.R.layout.simple_spinner_item,
        users);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerUser.setAdapter(adapter);
  }

  /**
   * Setup button click listeners
   */
  private void setupButtons() {
    btnCancel.setOnClickListener(v -> finish());
    btnSave.setOnClickListener(v -> savePost());
  }

  /**
   * Populate form with existing post data
   */
  private void populateForm() {
    if (post != null) {
      etTitle.setText(post.getTitle());
      etBody.setText(post.getBody());
      spinnerUser.setSelection(post.getUserId() - 1); // User IDs are 1-based
      switchFavorite.setChecked(post.isFavorite());
    }
  }

  /**
   * Restore state after configuration change
   */
  private void restoreState(Bundle savedInstanceState) {
    etTitle.setText(savedInstanceState.getString(KEY_TITLE, ""));
    etBody.setText(savedInstanceState.getString(KEY_BODY, ""));
    spinnerUser.setSelection(savedInstanceState.getInt(KEY_USER_ID, 0));
    switchFavorite.setChecked(savedInstanceState.getBoolean(KEY_FAVORITE, false));
  }

  /**
   * Validate and save post
   */
  private void savePost() {
    // Clear previous errors
    tilTitle.setError(null);
    tilBody.setError(null);

    // Get values
    String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
    String body = etBody.getText() != null ? etBody.getText().toString().trim() : "";
    int userId = spinnerUser.getSelectedItemPosition() + 1; // Convert to 1-based
    boolean isFavorite = switchFavorite.isChecked();

    // Validate
    boolean isValid = true;

    if (TextUtils.isEmpty(title)) {
      tilTitle.setError(getString(R.string.error_empty_title));
      isValid = false;
    }

    if (TextUtils.isEmpty(body)) {
      tilBody.setError(getString(R.string.error_empty_body));
      isValid = false;
    }

    if (!isValid) {
      return;
    }

    // Create or update post
    if (isEditMode) {
      post.setTitle(title);
      post.setBody(body);
      post.setUserId(userId);
      post.setFavorite(isFavorite);
      repository.updatePost(post);
    } else {
      // Generate new ID (simple approach: get max ID + 1)
      int newId = repository.getPostCount() + 1000; // Offset to avoid conflicts
      Post newPost = new Post(newId, userId, title, body, isFavorite);
      repository.insertPost(newPost);
    }

    Toast.makeText(this, R.string.post_saved, Toast.LENGTH_SHORT).show();
    setResult(RESULT_OK);
    finish();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (etTitle.getText() != null) {
      outState.putString(KEY_TITLE, etTitle.getText().toString());
    }
    if (etBody.getText() != null) {
      outState.putString(KEY_BODY, etBody.getText().toString());
    }
    outState.putInt(KEY_USER_ID, spinnerUser.getSelectedItemPosition());
    outState.putBoolean(KEY_FAVORITE, switchFavorite.isChecked());
  }
}

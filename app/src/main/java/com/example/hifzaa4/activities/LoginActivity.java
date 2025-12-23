package com.example.hifzaa4.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hifzaa4.R;
import com.example.hifzaa4.database.UserRepository;
import com.example.hifzaa4.utils.AppPreferences;
import com.example.hifzaa4.utils.ThemeManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity - Welcome/Login screen
 * Handles user authentication and session management
 */
public class LoginActivity extends AppCompatActivity {

  // UI Elements
  private TextInputLayout tilUsername;
  private TextInputLayout tilPassword;
  private TextInputEditText etUsername;
  private TextInputEditText etPassword;
  private Button btnLogin;
  private TextView tvRegisterLink;
  private Toolbar toolbar;

  // Preferences & Repository
  private AppPreferences preferences;
  private UserRepository userRepository;

  // State keys for saving instance state
  private static final String KEY_USERNAME = "username";
  private static final String KEY_PASSWORD = "password";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Apply theme before super.onCreate()
    ThemeManager.applyTheme(this);

    super.onCreate(savedInstanceState);

    // Initialize preferences
    preferences = new AppPreferences(this);

    // Check if already logged in - redirect to MainActivity
    if (preferences.isLoggedIn()) {
      navigateToMain();
      return;
    }

    setContentView(R.layout.activity_login);

    // Initialize database repository
    userRepository = new UserRepository(this);

    // Initialize views
    initViews();

    // Restore saved state if available
    if (savedInstanceState != null) {
      if (etUsername != null)
        etUsername.setText(savedInstanceState.getString(KEY_USERNAME, ""));
      if (etPassword != null)
        etPassword.setText(savedInstanceState.getString(KEY_PASSWORD, ""));
    }

    // Setup listeners
    if (btnLogin != null)
      btnLogin.setOnClickListener(v -> attemptLogin());
    if (tvRegisterLink != null)
      tvRegisterLink.setOnClickListener(v -> navigateToRegister());
  }

  /**
   * Initialize view references
   */
  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    tilUsername = findViewById(R.id.til_username);
    tilPassword = findViewById(R.id.til_password);
    etUsername = findViewById(R.id.et_username);
    etPassword = findViewById(R.id.et_password);
    btnLogin = findViewById(R.id.btn_login);
    tvRegisterLink = findViewById(R.id.tv_register_link);
  }

  /**
   * Validate inputs and attempt login
   */
  private void attemptLogin() {
    // Clear previous errors
    if (tilUsername != null)
      tilUsername.setError(null);
    if (tilPassword != null)
      tilPassword.setError(null);

    // Get input values
    String username = etUsername != null && etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
    String password = etPassword != null && etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

    boolean isValid = true;

    // Validate username
    if (TextUtils.isEmpty(username)) {
      if (tilUsername != null)
        tilUsername.setError(getString(R.string.error_empty_username));
      isValid = false;
    }

    // Validate password
    if (TextUtils.isEmpty(password)) {
      if (tilPassword != null)
        tilPassword.setError(getString(R.string.error_empty_password));
      isValid = false;
    }

    if (isValid) {
      // Check credentials against database
      if (userRepository.loginUser(username, password)) {
        preferences.login(username);
        Toast.makeText(this, "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();
        navigateToMain();
      } else {
        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
      }
    }
  }

  /**
   * Navigate to RegisterActivity
   */
  private void navigateToRegister() {
    Intent intent = new Intent(this, RegisterActivity.class);
    startActivity(intent);
  }

  /**
   * Navigate to MainActivity
   */
  private void navigateToMain() {
    Intent intent = new Intent(this, MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // Save input state for configuration changes
    if (etUsername != null && etUsername.getText() != null) {
      outState.putString(KEY_USERNAME, etUsername.getText().toString());
    }
    if (etPassword != null && etPassword.getText() != null) {
      outState.putString(KEY_PASSWORD, etPassword.getText().toString());
    }
  }
}

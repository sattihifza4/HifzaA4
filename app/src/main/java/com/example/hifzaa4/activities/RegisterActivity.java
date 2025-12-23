package com.example.hifzaa4.activities;

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
import com.example.hifzaa4.utils.ThemeManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * RegisterActivity - User registration screen
 */
public class RegisterActivity extends AppCompatActivity {

  // UI Elements
  private TextInputLayout tilUsername;
  private TextInputLayout tilPassword;
  private TextInputLayout tilConfirmPassword;
  private TextInputEditText etUsername;
  private TextInputEditText etPassword;
  private TextInputEditText etConfirmPassword;
  private Button btnRegister;
  private TextView tvLoginLink;
  private Toolbar toolbar;

  // Repository
  private UserRepository userRepository;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Apply theme
    ThemeManager.applyTheme(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    userRepository = new UserRepository(this);
    initViews();

    btnRegister.setOnClickListener(v -> attemptRegister());
    tvLoginLink.setOnClickListener(v -> finish()); // Go back to Login
  }

  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle("Register");
    }
    toolbar.setNavigationOnClickListener(v -> finish());

    tilUsername = findViewById(R.id.til_username);
    tilPassword = findViewById(R.id.til_password);
    tilConfirmPassword = findViewById(R.id.til_confirm_password);
    etUsername = findViewById(R.id.et_username);
    etPassword = findViewById(R.id.et_password);
    etConfirmPassword = findViewById(R.id.et_confirm_password);
    btnRegister = findViewById(R.id.btn_register);
    tvLoginLink = findViewById(R.id.tv_login_link);
  }

  private void attemptRegister() {
    // Reset errors
    tilUsername.setError(null);
    tilPassword.setError(null);
    tilConfirmPassword.setError(null);

    String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
    String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
    String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

    boolean isValid = true;

    // Validate Username
    if (TextUtils.isEmpty(username)) {
      tilUsername.setError(getString(R.string.error_empty_username));
      isValid = false;
    } else if (userRepository.checkUsernameExists(username)) {
      tilUsername.setError("Username already exists");
      isValid = false;
    }

    // Validate Password
    if (TextUtils.isEmpty(password)) {
      tilPassword.setError(getString(R.string.error_empty_password));
      isValid = false;
    } else if (password.length() < 4) {
      tilPassword.setError(getString(R.string.error_short_password));
      isValid = false;
    }

    // Validate Confirm Password
    if (!password.equals(confirmPassword)) {
      tilConfirmPassword.setError("Passwords do not match");
      isValid = false;
    }

    if (isValid) {
      boolean success = userRepository.registerUser(username, password);
      if (success) {
        Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
        finish();
      } else {
        Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
      }
    }
  }
}

package com.example.hifzaa4.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hifzaa4.R;
import com.example.hifzaa4.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;

/**
 * WebViewActivity - Displays web content within the app
 * Demonstrates WebView with JavaScript, loading progress, and error handling
 */
public class WebViewActivity extends AppCompatActivity {

  // UI Elements
  private Toolbar toolbar;
  private ProgressBar progressBar;
  private WebView webView;
  private LinearLayout layoutError;
  private MaterialButton btnRetry;

  // Data
  private String url;
  private String title;

  // State key
  private static final String KEY_URL = "current_url";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // Apply theme
    ThemeManager.applyTheme(this);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_webview);

    // Get intent data
    url = getIntent().getStringExtra("url");
    title = getIntent().getStringExtra("title");

    if (url == null || url.isEmpty()) {
      url = "https://jsonplaceholder.typicode.com/";
    }

    // Restore URL if rotated
    if (savedInstanceState != null) {
      url = savedInstanceState.getString(KEY_URL, url);
    }

    initViews();
    setupToolbar();
    setupWebView();
    setupRetryButton();

    // Load URL
    loadUrl(url);
  }

  /**
   * Initialize view references
   */
  private void initViews() {
    toolbar = findViewById(R.id.toolbar);
    progressBar = findViewById(R.id.progress_bar);
    webView = findViewById(R.id.web_view);
    layoutError = findViewById(R.id.layout_error);
    btnRetry = findViewById(R.id.btn_retry);
  }

  /**
   * Setup toolbar
   */
  private void setupToolbar() {
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(title != null ? title : getString(R.string.title_webview));
    }
    toolbar.setNavigationOnClickListener(v -> onBackPressed());
  }

  /**
   * Setup WebView with settings and clients
   */
  @SuppressLint("SetJavaScriptEnabled")
  private void setupWebView() {
    WebSettings webSettings = webView.getSettings();

    // Enable JavaScript
    webSettings.setJavaScriptEnabled(true);

    // Additional settings
    webSettings.setDomStorageEnabled(true);
    webSettings.setLoadWithOverviewMode(true);
    webSettings.setUseWideViewPort(true);
    webSettings.setBuiltInZoomControls(true);
    webSettings.setDisplayZoomControls(false);

    // WebViewClient - handle page loading and errors
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        showLoading(true);
        showError(false);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        showLoading(false);
      }

      @Override
      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        // Only show error for main frame
        if (request.isForMainFrame()) {
          showLoading(false);
          showError(true);
        }
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        // Keep navigation within WebView (don't open external browser)
        view.loadUrl(request.getUrl().toString());
        return true;
      }
    });

    // WebChromeClient - handle progress updates
    webView.setWebChromeClient(new WebChromeClient() {
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        progressBar.setProgress(newProgress);

        if (newProgress == 100) {
          progressBar.setVisibility(View.GONE);
        }
      }

      @Override
      public void onReceivedTitle(WebView view, String pageTitle) {
        super.onReceivedTitle(view, pageTitle);
        // Update toolbar title with page title if available
        if (getSupportActionBar() != null && pageTitle != null && !pageTitle.isEmpty()) {
          getSupportActionBar().setSubtitle(pageTitle);
        }
      }
    });
  }

  /**
   * Setup retry button
   */
  private void setupRetryButton() {
    btnRetry.setOnClickListener(v -> {
      showError(false);
      loadUrl(url);
    });
  }

  /**
   * Load a URL in WebView
   */
  private void loadUrl(String urlToLoad) {
    this.url = urlToLoad;
    webView.loadUrl(urlToLoad);
  }

  /**
   * Show/hide loading indicator
   */
  private void showLoading(boolean show) {
    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    if (show) {
      progressBar.setProgress(0);
    }
  }

  /**
   * Show/hide error view
   */
  private void showError(boolean show) {
    layoutError.setVisibility(show ? View.VISIBLE : View.GONE);
    webView.setVisibility(show ? View.GONE : View.VISIBLE);
  }

  @Override
  public void onBackPressed() {
    // Handle back button - go back in WebView history if possible
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      super.onBackPressed();
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_URL, webView.getUrl());
    webView.saveState(outState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    webView.restoreState(savedInstanceState);
  }

  @Override
  protected void onPause() {
    super.onPause();
    webView.onPause();
  }

  @Override
  protected void onResume() {
    super.onResume();
    webView.onResume();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    webView.destroy();
  }
}

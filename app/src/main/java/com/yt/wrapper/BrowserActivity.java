package com.yt.wrapper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class BrowserActivity extends Activity {
    private WebView webView;
    private FrameLayout root;
    private ProgressBar pageProgress;
    private View fullscreenView;
    private WebChromeClient.CustomViewCallback fullscreenCallback;
    private int oldSystemUiVisibility;
    private boolean oldFullscreenFlag;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);
        webView = new WebView(this);
        webView.setLayerType(View.LAYER_TYPE_NONE, null);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setDrawingCacheEnabled(false);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        pageProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pageProgress.setMax(100);
        pageProgress.setProgress(0);
        pageProgress.setVisibility(View.GONE);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(3), Gravity.TOP);
        root.addView(pageProgress, progressParams);
        setContentView(root);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // Android 4.4 supports this setting (API 17+). Let the site control its
        // normal in-page video playback instead of adding a WebView gesture gate.
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(false);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // Leave renderer/layer decisions to KitKat WebView; forcing a hardware
        // layer or deprecated render priority can increase memory pressure.

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int progress) {
                pageProgress.setProgress(progress);
                if (fullscreenView != null || progress >= 100) {
                    pageProgress.setVisibility(View.GONE);
                } else if (progress > 0) {
                    pageProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onShowCustomView(View view, CustomViewCallback callback) {
                if (fullscreenView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                fullscreenView = view;
                fullscreenCallback = callback;
                oldSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                oldFullscreenFlag = (getWindow().getAttributes().flags
                        & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
                pageProgress.setVisibility(View.GONE);
                root.setVisibility(View.GONE);

                FrameLayout decor = (FrameLayout) getWindow().getDecorView();
                decor.addView(view, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                // Immersive flags exist on the app's Android 4.4 minimum (API 19).
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }

            @Override public void onHideCustomView() {
                hideFullscreenVideo();
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Let WebView handle normal web navigation itself. Calling loadUrl()
                // here and returning true can trigger an unnecessary second navigation
                // on older WebView versions, causing extra network and rendering work.
                if (url != null && (url.startsWith("https://") || url.startsWith("http://"))) {
                    return false;
                }
                return true;
            }
        });

        if (savedInstanceState != null && webView.restoreState(savedInstanceState) != null) {
            // Restored the existing YouTube page.
        } else {
            String requestedUrl = getIntent().getStringExtra("start_url");
            // Only accept the built-in Shorts entry point; arbitrary external URLs
            // are deliberately not accepted by this lightweight browser.
            if ("shorts".equals(requestedUrl)) {
                webView.loadUrl("https://m.youtube.com/shorts/");
            } else {
                webView.loadUrl("https://m.youtube.com/");
            }
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void hideFullscreenVideo() {
        if (fullscreenView == null) return;
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        decor.removeView(fullscreenView);
        fullscreenView = null;
        if (fullscreenCallback != null) {
            fullscreenCallback.onCustomViewHidden();
            fullscreenCallback = null;
        }
        root.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(oldSystemUiVisibility);
        if (!oldFullscreenFlag) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override protected void onPause() {
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onBackPressed() {
        if (fullscreenView != null) {
            hideFullscreenVideo();
        } else if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override public void onLowMemory() {
        // KitKat WebView can hold substantial renderer memory. Ask it to release
        // reclaimable memory when Android reports pressure, without clearing its cache.
        if (webView != null) webView.freeMemory();
        super.onLowMemory();
    }

    @Override public void onTrimMemory(int level) {
        if (level >= TRIM_MEMORY_RUNNING_LOW && webView != null) {
            webView.freeMemory();
        }
        super.onTrimMemory(level);
    }

    @Override protected void onDestroy() {
        if (fullscreenView != null) hideFullscreenVideo();
        if (webView != null) {
            webView.stopLoading();
            webView.loadUrl("about:blank");
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}

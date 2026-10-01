package com.yt.wrapper;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- HARDWARE ACCELERATION STEP 1: Window Level GPU Rendering ---
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );

        webView = new WebView(this);
        setContentView(webView);

        // --- HARDWARE ACCELERATION STEP 2: View Level GPU Layer ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        WebSettings settings = webView.getSettings();
        
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        
        // Disable heavy caching to conserve 512MB RAM
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDatabaseEnabled(false);
        
        // --- HARDWARE ACCELERATION STEP 3: Media & Render Prioritization ---
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setPluginState(WebSettings.PluginState.ON);
        
        // Force desktop/mobile layout hardware viewport
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // KitKat-optimized User Agent (serves lightweight UI that GPUs can composite fast)
        settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.4.2; en-us; LGMS323 Build/KOT49I.MS32310c) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // --- HARDWARE ACCELERATION STEP 4: Full WebChromeClient for GPU Video Decoding ---
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public View getVideoLoadingProgressView() {
                return super.getVideoLoadingProgressView();
            }
        });

        webView.loadUrl("https://m.youtube.com");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

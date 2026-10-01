package com.yt.wrapper;

import android.app.Activity;
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

        // 1. Force GPU Hardware Acceleration at Window level
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );

        webView = new WebView(this);
        setContentView(webView);

        // 2. Hardware Layering for 60fps GPU rendering
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        WebSettings settings = webView.getSettings();
        
        // Critical WebView settings
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        
        // Disable heavy caching to conserve 512MB RAM
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDatabaseEnabled(false);
        settings.setGeolocationEnabled(false);
        
        // Prioritize rendering speed
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setLoadsImagesAutomatically(true);
        settings.setBlockNetworkImage(false);

        // Custom User Agent: Spoofs classic Chrome Mobile (Bypasses heavy Desktop/Polymer JS)
        settings.setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.4.2; en-us; LGMS323 Build/KOT49I.MS32310c) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Keep all navigations internal
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                // Inject custom JS/CSS into m.youtube.com to kill heavy scripts and ads
                String jsOptimizations = "javascript:(function() {" +
                    "var style = document.createElement('style');" +
                    "style.innerHTML = 'ytm-promoted-sparkles-web-renderer, ytm-compact-promoted-item-renderer, ytm-companion-ad-renderer { display: none !important; }';" +
                    "document.head.appendChild(style);" +
                    "})()";
                view.loadUrl(jsOptimizations);
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        // Load official mobile YouTube directly
        webView.loadUrl("https://m.youtube.com");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
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

package app.kindred;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

/**
 * Kindred runs as a local web app inside a WebView.
 *
 * The app files are served through WebViewAssetLoader rather than file:// so the
 * page gets a real https origin (appassets.androidplatform.net). That matters:
 * without it the Anthropic API rejects the request on CORS, and localStorage
 * behaves inconsistently across Android versions.
 */
public class MainActivity extends AppCompatActivity {

    private static final String HOME =
            "https://appassets.androidplatform.net/assets/index.html";

    private WebView web;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web = new WebView(this);
        web.setBackgroundColor(0xFF0B0912);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setTextZoom(100);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                // keep the app inside the app; hand real links to the browser
                if ("appassets.androidplatform.net".equals(url.getHost())) return false;
                startActivity(new Intent(Intent.ACTION_VIEW, url));
                return true;
            }
        });

        if (savedInstanceState == null) web.loadUrl(HOME);

        // Hardware back closes the open chat, menu or sheet before it leaves the app
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                web.evaluateJavascript(
                        "(window.__androidBack && window.__androidBack()) ? 'true' : 'false'",
                        value -> {
                            if (!"true".equals(value)) finish();
                        });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    protected void onRestoreInstanceState(Bundle in) {
        super.onRestoreInstanceState(in);
        web.restoreState(in);
    }
}

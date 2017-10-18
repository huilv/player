package com.music.android.ui.mvp.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.music.android.R;
import com.music.android.base.BaseActivity;
import com.music.android.network.UrlConst;
import com.music.android.utils.ColorStatusUtils;
import com.music.android.utils.Constants;
import com.music.android.utils.ShareHelper;
import com.music.android.utils.SharedPreferencesHelper;
import com.music.android.utils.StatusUtils;

import org.greenrobot.eventbus.EventBus;


public class AboutUsActivity extends BaseActivity {

    private final String TAG="AboutUsActivity";
    private WebView webView;
    private String url="https://sites.google.com/view/privacy-policy-of-smusic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusUtils.init(this);
        setContentView(R.layout.activity_about_us);
        ColorStatusUtils.buildPadding(this);
        initView();
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(webViewClient);
        webView.loadUrl(url);

        findViewById(R.id.imgBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }
    };
}

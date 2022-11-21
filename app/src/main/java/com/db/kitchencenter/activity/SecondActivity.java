package com.db.kitchencenter.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.db.kitchencenter.App;
import com.db.kitchencenter.Config;
import com.db.kitchencenter.Helper.Progress_Dialogue;
import com.db.kitchencenter.R;
import com.db.kitchencenter.widget.AdvancedWebView;
import com.db.kitchencenter.widget.webview.WebToAppChromeClient;
import com.db.kitchencenter.widget.webview.WebToAppWebClient;

import java.util.Set;

public class SecondActivity extends AppCompatActivity implements  SwipeRefreshLayout.OnRefreshListener{
    //Layouts
    public AdvancedWebView browser,browser2;
    public SwipeRefreshLayout swipeLayout;
    public ProgressBar progressBar;
    FrameLayout webview_layout;
  //  Progress_Dialogue DialogUtils = null;
    public WebToAppChromeClient chromeClient;
    public WebToAppWebClient webClient;

    //WebView Session
    public String mainUrl2 = "";
 //   Dialog myDialog;

    public int firstLoad = 0;
    private boolean clearHistory = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent= getIntent();
        mainUrl2=intent.getStringExtra("redirect_url");
       // Log.d("urls",mainUrl2);
        //myDialog = DialogUtils.showProgressDialog(SecondActivity.this, "Loading Please Wait...");
        progressBar = (ProgressBar) findViewById(R.id.progressbar_se);
        progressBar.setEnabled(true);
        progressBar.setVisibility(View.VISIBLE);

        browser = (AdvancedWebView) findViewById(R.id.scrollable_se);
        webview_layout = (FrameLayout) findViewById(R.id.webview_layout_se);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container_se);
//        if (getArguments() != null && mainUrl == null) {
//            mainUrl = getArguments().getString(URL);
//            firstLoad = 0;
//        }
        if (Config.PULL_TO_REFRESH)
            swipeLayout.setOnRefreshListener(SecondActivity.this);
        else
            swipeLayout.setEnabled(false);

        // Setting the webview listeners

        browser.requestFocus();
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(false);
        browser.getSettings().setAppCacheEnabled(true);
        browser.getSettings().setDatabaseEnabled(true);
        browser.getSettings().setDomStorageEnabled(true);
        // Below required for geolocation
        browser.setGeolocationEnabled(true);
        // 3RD party plugins (on older devices)
      //  browser.getSettings().setPluginState(WebSettings.PluginState.ON);

        if (Config.MULTI_WINDOWS) {
            browser.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            browser.getSettings().setSupportMultipleWindows(true);
        }




      //  Log.d("url",mainUrl);
        // load url (if connection available

        browser.loadUrl(mainUrl2);
        browser.setWebViewClient(new Browser_Home());
        swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                browser.loadUrl(mainUrl2);
                swipeLayout.setRefreshing(false);

            }
        });


    }
    private class Browser_Home extends WebViewClient {
        Browser_Home() {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //myDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            progressBar.setEnabled(false);
            swipeLayout.setRefreshing(false);
            mainUrl2 = url;
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onRefresh() {
    browser.reload();

    }
    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
        browser.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        browser.onDestroy();
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        browser.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:

                    if ((keyCode == KeyEvent.KEYCODE_BACK) && browser.canGoBack()) {
                        browser.goBack();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        return true;
                    }else{
                       onBackPressed();
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(SecondActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.db.kitchencenter.activity;

import com.db.kitchencenter.Helper.Progress_Dialogue;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.db.kitchencenter.Config;
import com.db.kitchencenter.R;

import com.db.kitchencenter.util.ThemeUtils;
import com.db.kitchencenter.widget.SwipeableViewPager;
import com.db.kitchencenter.adapter.NavigationAdapter;
import com.db.kitchencenter.fragment.WebFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {


    public View mHeaderView;
    public SwipeableViewPager mViewPager;
    Dialog myDialog;
    Progress_Dialogue DialogUtils = null;
    private NavigationAdapter mAdapter;
    private int interstitialCount = -1;
    private InterstitialAd mInterstitialAd;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);

        setContentView(R.layout.activity_main);

        mHeaderView = (View) findViewById(R.id.header_container);
        mViewPager = (SwipeableViewPager) findViewById(R.id.pager);
        mAdapter = new NavigationAdapter(getSupportFragmentManager(), this);

        final Intent intent = getIntent();
        final String action = intent.getAction();

        hasPermissionToDo(this, Config.PERMISSIONS_REQUIRED);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();

        mViewPager.setLayoutParams(lp);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount() - 1);


        if (!getResources().getString(R.string.ad_banner_id).equals("")) {
            // Look up the AdView as a resource and load a request.
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            adView.loadAd(adRequest);
        } else {
            AdView adView = (AdView) findViewById(R.id.adView);
            adView.setVisibility(View.GONE);
        }
        if (getResources().getString(R.string.ad_interstitial_id).length() > 0 && Config.INTERSTITIAL_INTERVAL > 0) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_interstitial_id));
            AdRequest adRequestInter = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
            mInterstitialAd.loadAd(adRequestInter);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
                }

            });
        }

        //Application rating

//        if (Config.SPLASH) {
//            findViewById(R.id.imageLoading1).setVisibility(View.VISIBLE);
//            //getFragment().browser.setVisibility(View.GONE);
//        }

    }

    // using the back button of the device
    @Override
    public void onBackPressed() {
        View customView = null;
        WebChromeClient.CustomViewCallback customViewCallback = null;
        if (getFragment().chromeClient != null) {
            customView = getFragment().chromeClient.getCustomView();
            customViewCallback = getFragment().chromeClient.getCustomViewCallback();
        }

        if ((customView == null)
                && getFragment().browser.canGoBack()) {
            getFragment().browser.goBack();
        } else if (customView != null
                && customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }




    public WebFragment getFragment(){

        return (WebFragment) mAdapter.getCurrentFragment();
    }

//    public void hideSplash() {
//        if (Config.SPLASH) {
//            if (findViewById(R.id.imageLoading1).getVisibility() == View.VISIBLE) {
//                Handler mHandler = new Handler();
//                mHandler.postDelayed(new Runnable() {
//                    public void run() {
//                        // hide splash image
//                        findViewById(R.id.imageLoading1).setVisibility(
//                                    View.GONE);
//                    }
//                    // set a delay before splashscreen is hidden
//                }, Config.SPLASH_SCREEN_DELAY);
//            }
//        }
//    }

    private static boolean hasPermissionToDo(final Activity context, final String[] permissions) {
        boolean oneDenied = false;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ContextCompat.checkSelfPermission(context, permission)
                            != PackageManager.PERMISSION_GRANTED)
                oneDenied = true;
        }

        if (!oneDenied) return true;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.common_permission_explaination);
        builder.setPositiveButton(R.string.common_permission_grant, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Fire off an async request to actually get the permission
                // This will show the standard permission request dialog UI
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    context.requestPermissions(permissions,1);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }


    public void showInterstitial(){
        if (interstitialCount == (Config.INTERSTITIAL_INTERVAL - 1)) {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

            interstitialCount = 0;
        } else {
            interstitialCount++;
        }
    }



}

package com.db.kitchencenter.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.db.kitchencenter.R;

public class Splash_Activity extends AppCompatActivity {
    boolean isConnected;
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_);
        Uri uri=getIntent().getData();
//        isConnected = ConnectivityReceiver.isConnected(Splash_Activity.this);
//        if (!isConnected) {
//            checkinternet();
//        } else {
            next();

//        }

        Handler handler = new Handler();
        if(uri!=null){
            String path=uri.toString();
        }


    }

    private void checkinternet() {
        new AlertDialog.Builder(Splash_Activity.this)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.checkinternet))
//                .setIcon(R.drawable.ic_warning_black_24dp)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.tryagain),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                if (!ConnectivityReceiver.isConnected(Splash_Activity.this)) {
//                                    checkinternet();
//                                } else {
                                    next();
                                    dialog.cancel();
//                                }

                            }
                        })
                .show();


    }

    private void next() {
        Thread background = new Thread() {
            public void run() {
                try {
                    // Thread will sleep for 5 seconds
                    sleep(2*1000);

                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(getBaseContext(),NewWebviewActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();
                } catch (Exception e) {
                }
            }
        };
        // start thread
        background.start();
    }

}
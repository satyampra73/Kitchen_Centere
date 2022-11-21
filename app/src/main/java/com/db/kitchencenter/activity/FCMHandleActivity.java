package com.db.kitchencenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.db.kitchencenter.R;

public class FCMHandleActivity extends AppCompatActivity {
    String key1;
        Bundle savedInstanceState;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_);
            savedInstanceState = getIntent().getExtras();
            this.savedInstanceState = savedInstanceState;
            System.out.println("savedInstanceStateBundle= " + savedInstanceState);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Object value = extras.get(key);
                    Log.d("gdfh", "Extras received at onNewIntent:  Key: " + key + " Value: " + value);
                }
                key1 = extras.getString("redirect_url");
               Log.d("key1",key1);
               Intent i = new Intent(FCMHandleActivity.this, SecondActivity.class);
                    i.putExtra("redirect_url", key1);
                    startActivity(i);
                    finish();


            }
        }
        @Override
        public void onBackPressed() {
            super.onBackPressed();
            Intent intent=new Intent(FCMHandleActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
}



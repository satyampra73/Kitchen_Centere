package com.db.kitchencenter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.db.kitchencenter.activity.FCMHandleActivity;
import com.db.kitchencenter.activity.MainActivity;
import com.db.kitchencenter.activity.SecondActivity;
import com.db.kitchencenter.activity.Splash_Activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessageReceiver extends FirebaseMessagingService {


    private static final String TAG = FirebaseMessageReceiver.class.getSimpleName();
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
//        FirebaseMessaging.getInstance().subscribeToTopic("weather")
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        String msg = getString(R.string.msg_subscribed);
//                        Log.e("msg",msg);
//                        if (!task.isSuccessful()) {
//                            msg = getString(R.string.msg_subscribe_failed);
//                        }
//                        Log.d(TAG, msg);
//                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//                    }
//                });

    }



    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {


        if (remoteMessage.getNotification() != null) {
            System.out.println("Message Body===" + remoteMessage.getData());

            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData().get("redirect_url"),remoteMessage.getData().get("click_action"));
        }
    }

    // Method to get the custom Design for the display of
    // notification.
    private RemoteViews getCustomDesign(String title, String message,String redirect_url,String click_action) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message,message);
        remoteViews.setImageViewResource(R.id.icon, R.drawable.site_logo);
        return remoteViews;
    }

    // Method to display the notifications
    public void showNotification(String title, String message,String redirect_url,String click_action) {


        // Pass the intent to switch to the MainActivity
        // Assign channel ID

        Intent  intent = new Intent(this, FCMHandleActivity.class);
            intent.putExtra("redirect_url", redirect_url);
            intent.putExtra("id","1");

       // intent.setAction(Intent.ACTION_MAIN);
       // intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            String channel_id = "notification_channel";

            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat
                    .Builder(getApplicationContext(),
                    channel_id)
                    .setSmallIcon(R.drawable.site_logo)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setOnlyAlertOnce(true)
                    .setContentIntent(pendingIntent);
            // condition for the same is checked here.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                builder = builder.setContent(getCustomDesign(title, message,redirect_url,click_action));
            } else {
                builder = builder.setContentTitle(title)
                        .setContentText(message)
                        .setContentText(redirect_url)
                        .setContentText(click_action)
                        .setSmallIcon(R.drawable.site_logo);
            }
            // user of events that happen in the background.
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // Check if the Android Version is greater than Oreo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel
                        = new NotificationChannel(
                        channel_id, "web_app",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(
                        notificationChannel);
            }

            notificationManager.notify(0, builder.build());
        }
    }

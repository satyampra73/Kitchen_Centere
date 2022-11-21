package com.db.kitchencenter.widget.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.db.kitchencenter.Config;
import com.db.kitchencenter.R;
import com.db.kitchencenter.activity.MainActivity;
import com.db.kitchencenter.activity.SecondActivity;
import com.db.kitchencenter.fragment.WebFragment;
import com.db.kitchencenter.widget.AdvancedWebView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class WebToAppWebClient extends WebViewClient {

    WebFragment fragment;
    WebView browser;
    Context context;
    String FcmToken;
    public  String notifictionUrl="https://panel.kitchencentre.in/webservices/login";
    public WebToAppWebClient(WebFragment fragment,Context context, WebView browser)
    {
        super();
        this.fragment = fragment;
        this.browser = browser;
        this.context=context;
    }


    @TargetApi(android.os.Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoading(view, request.getUrl().toString());
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (urlShouldOpenExternally(url)) {
            // Load new URL Don't override URL Link
            try {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch(ActivityNotFoundException e) {
                if (url.startsWith("intent://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("intent://", "http://"))));
                } else {
                    Toast.makeText(fragment.getActivity(), fragment.getActivity().getResources().getString(R.string.no_app_message), Toast.LENGTH_LONG).show();
                }
            }

            return true;
        } else if (url.endsWith(".mp4") || url.endsWith(".avi")
                || url.endsWith(".flv")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/mp4");
                view.getContext().startActivity(intent);
            } catch (Exception e) {
                // error
            }

            return true;
        } else if (url.endsWith(".mp3") || url.endsWith(".wav")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "audio/mp3");
                view.getContext().startActivity(intent);
            } catch (Exception e) {
                // error
            }

            return true;
        }else
        {
           String currentUrl = url;

            Log.e("currenturl", currentUrl);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("failedFCM", "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            // Get new FCM registration token
                            FcmToken = task.getResult();
                            // Toast.makeText(getActivity(), FcmToken, Toast.LENGTH_SHORT).show();

                        }
                    });

            try {
                java.net.URL url2 = new URL(currentUrl);
                Log.d("hfjhjf",url2.toString());
               String urlid = currentUrl.substring(currentUrl.lastIndexOf('/')+1);
                Log.d("dataurl",urlid);
                checkvali(urlid);
            } catch (Exception e) {
                e.printStackTrace();

            }
//
            proceedUrl(view, url);


        }

        // Return true to override url loading (In this case do
        // nothing).
        return super.shouldOverrideUrlLoading(view, url);
    }
    public void checkvali(String urlid) {

        //  myDialog = DialogUtils.showProgressDialog(getActivity(), "Loading Please Wait...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,notifictionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            String status = obj.getString("status");
                            Log.d("status",status);
                            Log.v("res",response.toString());
                            if (status.equals("1")) {
                                //  myDialog.dismiss();
                                JSONObject object = obj.getJSONObject("data_result");
                              //  Toast.makeText(context, "Done"+urlid, Toast.LENGTH_SHORT).show();
                            } else {
                               // Toast.makeText(context, "urlId: "+urlid+"\nFcm Token: ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //  myDialog.dismiss();
                            e.printStackTrace();
                          //   Toast.makeText(context, "Somthing went wrong"+urlid, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("error","no internet");
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id",urlid);
                params.put("fcm_token", FcmToken);
                params.put("device_id", "1");
                Log.v("param",new Gson().toJson(params));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("KITCHENCENTER-API-KEY", "454c8e6121882ee87ddc7a432c9776eb48e7d93f");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        //
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
        // Redirect to deprecated method, so you can use it in all SDK versions

        onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
    }

    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setMessage(R.string.notification_error_ssl_cert_invalid);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    // handeling errors
    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        if (hasConnectivity("", false)
                && !failingUrl.equals(((AdvancedWebView) view).lastDownloadUrl)) {
            //If an error occurred while we had connectivity, the page must be borken
            fragment.showErrorScreen(fragment.getActivity().getString(R.string.error));
        } else {
            //If we don't have connectivity, and this isn't an online page, let hasConnectivity handle the error
            if (!failingUrl.startsWith("file:///android_asset")) {
                hasConnectivity("", true);
            }
        }
      //  proceedUrl(view,failingUrl);
    }

    /**
     * Check if we need an internet connection to load an url, and if we a connection, if it is present
     * @param loadUrl The url we are trying to load
     * @param showDialog If a dialog should be shown if a connection is required, but not found
     * @return If we can load the url (based on the fact if we need an connection for it, and if the connection, if needed, is present0
     */
    public boolean hasConnectivity(String loadUrl, boolean showDialog) {
        boolean enabled = true;

        if (loadUrl.startsWith("file:///android_asset")){
            return true;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) fragment.getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable())) {

            enabled = false;

            if (showDialog){
                if (Config.NO_CONNECTION_PAGE.length() > 0 && Config.NO_CONNECTION_PAGE.startsWith("file:///android_asset")) {
                    browser.loadUrl(Config.NO_CONNECTION_PAGE);
                } else {
                    fragment.showErrorScreen(fragment.getActivity().getString(R.string.no_connection));
                }
            }
        }
        return enabled;
    }
    private void proceedUrl(View view, String uri) {
        if (uri.toString().startsWith("mailto:")) {
            context.startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(uri)));
        } else if (uri.toString().startsWith("tel:")) {
            context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(uri)));
        } else {
            browser.loadUrl(uri.toString());
        }
    }

    /**
     * Check if an url should load externally and not in the WebView
     * @param url The url that we would like to load
     * @return If it should be loaded inside or outside the WebView
     */
    public static boolean urlShouldOpenExternally(String url){

        /*
         * If there is a set of urls defined that may only open inside the WebView and
         * the passed url does not match to one of these urls, it should be opened outside the WebView
         */
        if (Config.OPEN_ALL_OUTSIDE_EXCEPT.length > 0) {
            for (String pattern : Config.OPEN_ALL_OUTSIDE_EXCEPT) {
                if (!url.contains(pattern))
                    return true;
            }
        }

        /*
         * If there is an url defined that should open outside the WebView, these urls will be loaded outside the webview
         */
        for (String pattern : Config.OPEN_OUTSIDE_WEBVIEW){
            if (url.contains(pattern))
                return true;
        }

        return false;
    }
}
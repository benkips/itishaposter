package com.digitalexperts.itishaposter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import maes.tech.intentanim.CustomIntent;

public class login extends AppCompatActivity {
    private WebView web;
    private ProgressBar progressBar;
    private SharedPreferences preferences;
    final String Tag=this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        web = (WebView) findViewById(R.id.wvlogin);
        progressBar=findViewById(R.id.pgbar);

        final String myerrorpage = "file:///android_asset/android/errorpage.html";

        preferences=getSharedPreferences("logininfo.conf",MODE_PRIVATE);
        final String phn=preferences.getString("phone","");
        Log.d(Tag, phn);

        if(!phn.equals("")){
            /*startActivity here*/
            Log.d(Tag, phn);
            startActivity(new Intent(login.this,index.class));
            login.this.finish();
            CustomIntent.customType(login.this, "left-to-right");

        }


        web.getSettings().setJavaScriptEnabled(true);
        JavaScriptInterface jsInterface = new JavaScriptInterface(this);
        web.addJavascriptInterface(jsInterface, "JSInterface");
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setDatabaseEnabled(true);
        web.clearHistory();

        web.loadUrl("http://poster.ekarantechnologies.com/login.php");
        web.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return  true;
            }
        });
        web.setLongClickable(false);



        web.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                String key = "app";
                String val = "android";
                if(!phn.equals("")) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        web.evaluateJavascript("localStorage.setItem('" + key + "','" + val + "');", null);
                    } else {
                        web.loadUrl("javascript:localStorage.setItem('" + key + "','" + val + "');");
                    }
                }else{
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        web.evaluateJavascript("localStorage.setItem('" + key + "','" + val + "');", null);
                    } else {
                        web.loadUrl("javascript:localStorage.setItem('" + key + "','" + val + "');");
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        web.evaluateJavascript("localStorage.removeItem('phone');", null);
                    } else {
                        web.loadUrl("javascript:localStorage.removeItem('phone');");
                    }

                }
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressBar.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                web.loadUrl(myerrorpage);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        web.canGoBack();
        web.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        &&  web.canGoBack()) {
                    web.goBack();
                    return true;
                }
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= 19) {
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 19) {
            web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    @Override
    public void onBackPressed() {
        if (web.isFocused() && web.canGoBack()) {
                web.goBack();
            }else{
                super.onBackPressed();
        }


    }
    private class JavaScriptInterface {
        private Activity activity;
        private ProgressDialog progressDialog;

        public JavaScriptInterface(Activity activity) {
            this.activity = activity;
        }
        @JavascriptInterface
        public void toastt(String toast) {
            Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void  alertt(String show){
            AlertDialog.Builder alert = new AlertDialog.Builder(activity);
            alert.setMessage(show);
            alert.setNeutralButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            alert.show();
        }


        @JavascriptInterface
        public void  showprogress(String m){
            progressDialog=new ProgressDialog(activity);
            progressDialog.setMessage(m);
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
        @JavascriptInterface
        public void  setcache(String p){
            preferences = getSharedPreferences("logininfo.conf", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("phone",p);
            editor.apply();
            startActivity(new Intent(login.this,index.class));
            login.this.finish();
            CustomIntent.customType(login.this, "left-to-right");
            Log.d(Tag, "saved");
        }


        @JavascriptInterface
        public void  hideprogress(){
            progressDialog.dismiss();
        }
    }
}

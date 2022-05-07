package com.digitalexperts.itishaposter;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import maes.tech.intentanim.CustomIntent;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class profile extends Fragment {
    private WebView web;
    private ProgressBar progressBar;
    private SharedPreferences preferences;
    final String Tag=this.getClass().getName();
    private  String  phn;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> uploadMessage;
    private final int REQUEST_SELECT_FILE = 100;
    private final int FILECHOOSER_RESULTCODE = 1;

    public profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = getActivity().getSharedPreferences("logininfo.conf", MODE_PRIVATE);
        phn = preferences.getString("phone", "");


        web =view.findViewById(R.id.wvprofile);
        progressBar=view.findViewById(R.id.pgbarprofile);

        preferences = getActivity().getSharedPreferences("logininfo.conf", MODE_PRIVATE);
        phn = preferences.getString("phone", "");

        final String myerrorpage = "file:///android_asset/android/errorpage.html";

        web.getSettings().setJavaScriptEnabled(true);
        JavaScriptInterface jsInterface = new JavaScriptInterface(getContext());
        web.addJavascriptInterface(jsInterface, "JSInterface");
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setDatabaseEnabled(true);
        web.clearHistory();

        web.loadUrl("http://poster.ekarantechnologies.com/profile.php?acc="+phn);
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
                String key = "phone";
                String val = phn;
                String key2 = "app";
                String val2 = "android";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    web.evaluateJavascript("localStorage.setItem('"+ key +"','"+ val +"');", null);
                    web.evaluateJavascript("localStorage.setItem('"+key2+"','"+val2+"');", null);
                } else {
                    web.loadUrl("javascript:localStorage.setItem('"+key+"','"+val+"');");
                    web.loadUrl("javascript:localStorage.setItem('"+key2+"','"+val2+"');");
                }
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                progressBar.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("please check your internet connectivity");
                alert.setNeutralButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                alert.show();
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

       /* if (Build.VERSION.SDK_INT >= 19) {
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 19) {
            web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/


        web.setWebChromeClient(new WebChromeClient() {
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices

            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    intent = fileChooserParams.createIntent();
                }
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(getContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
    }
    private  class JavaScriptInterface {
        private Context activity;
        private ProgressDialog progressDialog;

        public JavaScriptInterface(Context activity) {
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
        public void  notify(String m){
            index.setupbadge(m);
            Log.d(Tag, "notify: "+ m);
        }

        @JavascriptInterface
        public void  updatecache(String p){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("phone",p);
            editor.apply();
            Log.d(Tag, "saved");
        }
        @JavascriptInterface
        public void  hideprogress(){
            progressDialog.dismiss();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != index.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getContext(), "Failed to Upload file", Toast.LENGTH_LONG).show();

    }
}

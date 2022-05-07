package com.digitalexperts.itishaposter;


import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class notification extends Fragment {
    private WebView web;
    private ProgressBar progressBar;
    private SharedPreferences preferences;
    final String Tag=this.getClass().getName();
    private  String  phn;

    public notification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = getActivity().getSharedPreferences("logininfo.conf", MODE_PRIVATE);
        phn = preferences.getString("phone", "");


        web =view.findViewById(R.id.wvnf);
        progressBar=view.findViewById(R.id.pgbarnf);

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

        web.loadUrl("http://poster.ekarantechnologies.com/notifications.php?acc="+phn);
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
        web.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimeType);
                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("resource file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getContext(), "Downloading File", Toast.LENGTH_LONG).show();
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
        public void  notify(String m){
            index.setupbadge(m);
            Log.d(Tag, "notify: "+ m);
        }
        @JavascriptInterface
        public void  showprogress(String m){
            progressDialog=new ProgressDialog(activity);
            progressDialog.setMessage(m);
            progressDialog.show();
            progressDialog.setCancelable(false);
        }


        @JavascriptInterface
        public void  hideprogress(){
            progressDialog.dismiss();
        }
    }

}

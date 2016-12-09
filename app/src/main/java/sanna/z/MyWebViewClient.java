package sanna.z;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.RecoverySystem;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static sanna.z.MainActivity.pbar;

/**
 * Created by sanna on 30/11/16.
 */

class MyWebViewClient extends WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO Auto-generated method stub
        super.onPageStarted(view, url, favicon);
        pbar.setVisibility(View.VISIBLE);

    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        return false;
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        pbar.setVisibility(View.GONE);
        String javascript="javascript:document.getElementsByName('viewport')[0].setAttribute('content', 'initial-scale=1.0,maximum-scale=10.0');";
        view.loadUrl(javascript);
    }
}

class MyWebChromeClient extends WebChromeClient {
    private ProgressListener mListener;
    public MyWebChromeClient(ProgressListener listener) {
        mListener = listener;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mListener.onUpdateProgress(newProgress);
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.v("msg", message);
        new AlertDialog.Builder(view.getContext())
                .setMessage(message).setCancelable(true).show();
        result.confirm();
        return true;
    }

    public interface ProgressListener {
        public void onUpdateProgress(int progressValue);
    }

}
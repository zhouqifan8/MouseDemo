package com.example.test_webview_demo;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class Main2Activity extends AppCompatActivity {
    private WebView webview, newWebView;
    private String Url = "https://yunny.yunzhiqu.com/h5receiver/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        webview = (WebView) findViewById(R.id.webview);
        inntView(webview);
        webview.loadUrl(Url);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                webView.loadUrl(s);
                return true;
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView webView, boolean b, boolean b1, Message message) {
                Log.d("22222222222222", "创建新窗口：" + b + ";" + b1 + ";" + message);
                newWebView = new WebView(Main2Activity.this);
                inntView(newWebView);
                RelativeLayout.LayoutParams vlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                Main2Activity.this.addContentView(newWebView, vlp);
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                        webView.loadUrl(s);
                        return true;
                    }
                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport) message.obj;//以下的操作应该就是让新的webview去加载对应的url等操作。
                transport.setWebView(newWebView);
                message.sendToTarget();
                return true;
            }
        });
    }

    private void inntView(WebView webview) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//支持js调用window.open方法
        webview.getSettings().setSupportMultipleWindows(true);// 设置允许开启多窗口
    }

}

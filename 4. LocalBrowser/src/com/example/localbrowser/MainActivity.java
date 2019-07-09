package com.example.localbrowser;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "LocalBrowser";
    private final Handler handler = new Handler();
    private WebView webView;
    private TextView textView;
    private Button button;
    
    /** 暴露给JavaScript的对象 */
	private class AndroidBridge {
	    @JavascriptInterface // 在Android 4.2+中必不可少
	    public void callAndroid(final String arg) { // 必须为final
	        handler.post(new Runnable() {
	            public void run() {
	                Log.d(TAG, "callAndroid(" + arg + ")");
	                textView.setText(arg);
	            }
	        });
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 得到标签
		webView = (WebView) findViewById(R.id.web_view);
        textView = (TextView) findViewById(R.id.text_view);
        button = (Button) findViewById(R.id.button);
        // 在内嵌的浏览器中启用JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // 在浏览器中向JavaScript暴露一个Java对象
        webView.addJavascriptInterface(new AndroidBridge(),
                                       "android");
        
        // 创建一个函数，这个函数将在JavaScript试图打开提示窗口时被调用
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view,
                                     final String url, final String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert(" + view + ", " + url + ", "
                      + message + ", " + result + ")");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm();
                return true; // 已处理过
            }
        });
        // 加载本地素材中的网页
        webView.loadUrl("file:///android_asset/index.html");
        // 用户点击按钮时，将在Android端调用这个函数
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "onClick(" + view + ")");
                webView.loadUrl("javascript:callJS('Hello from Android')");
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}


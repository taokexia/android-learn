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
    
    /** ��¶��JavaScript�Ķ��� */
	private class AndroidBridge {
	    @JavascriptInterface // ��Android 4.2+�бز�����
	    public void callAndroid(final String arg) { // ����Ϊfinal
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
		// �õ���ǩ
		webView = (WebView) findViewById(R.id.web_view);
        textView = (TextView) findViewById(R.id.text_view);
        button = (Button) findViewById(R.id.button);
        // ����Ƕ�������������JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // �����������JavaScript��¶һ��Java����
        webView.addJavascriptInterface(new AndroidBridge(),
                                       "android");
        
        // ����һ�������������������JavaScript��ͼ����ʾ����ʱ������
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view,
                                     final String url, final String message,
                                     JsResult result) {
                Log.d(TAG, "onJsAlert(" + view + ", " + url + ", "
                      + message + ", " + result + ")");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm();
                return true; // �Ѵ����
            }
        });
        // ���ر����ز��е���ҳ
        webView.loadUrl("file:///android_asset/index.html");
        // �û������ťʱ������Android�˵����������
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


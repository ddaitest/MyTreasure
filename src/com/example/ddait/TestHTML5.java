package com.example.ddait;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TestHTML5 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_html5);
		WebView wv = (WebView) findViewById(R.id.webView1);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setBuiltInZoomControls(true); // 设置显示缩放按钮
		wv.getSettings().setSupportZoom(true);
		wv.getSettings().setDefaultZoom(
				android.webkit.WebSettings.ZoomDensity.FAR);
		wv.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wv.getSettings().setDefaultTextEncodingName("utf-8"); //设置文本编码
		wv.getSettings().setAppCacheEnabled(true);
//		wv.setWebChromeClient(new WebChromeClient());
		wv.setWebViewClient(new WebViewClient());
		wv.loadUrl("http://fff.cmiscm.com/#!/main");
	}

}

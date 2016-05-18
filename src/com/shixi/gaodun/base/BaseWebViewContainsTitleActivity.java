package com.shixi.gaodun.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * webview的基类 暂定没有交互
 * 
 * @author ronger
 * @date:2015-12-5 上午9:39:42
 */
@SuppressLint("SetJavaScriptEnabled")
public abstract class BaseWebViewContainsTitleActivity extends TitleBaseActivity {
	protected WebView mWebView;

	protected abstract String getUrl();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.webview_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mBackIcon.setImageResource(R.drawable.icon_back);
		initWebView();
		String url = getUrl();
		mWebView.loadUrl(url);
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void initWebView() {
		mWebView = (WebView) findViewById(R.id.webview);
		// 设置支持JavaScript脚本
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		// 设置支持缩放
		webSettings.setBuiltInZoomControls(true);
		// 第一版本暂时用不到,把本类的一个实例添加到js的全局对象window中，
		// 这样就可以使用window.injs来调用它的方法 :window.nhbapp.goBack(data);
		// mWebView.addJavascriptInterface(new InJavaScript(this, webView), "androidFunction");
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				setProgressDialogShow(TAG);
				if (newProgress == 100) {
					myProgressDialog.dismiss();
				}
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				ToastUtils.showToastInCenter("加载网页错误，无法访问");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

		});
		webSettings.setDatabaseEnabled(true);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			mWebView.destroy();
		}catch (Exception ex){
		}

		super.onDestroy();
	}
}

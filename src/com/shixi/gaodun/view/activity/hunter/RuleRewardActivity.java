package com.shixi.gaodun.view.activity.hunter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.TaskInfoBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 任务规则及奖励页面
 * 
 * @author guolinyao
 * @date 2016年2月26日 上午11:24:45
 */
public class RuleRewardActivity extends Activity implements OnClickListener {

	private WebView mWebviewReward;
	private String mRuleUrl;

	// activity跳转
	public static void startAction(Context context, String rule_url) {
		Intent intent = new Intent(context, RuleRewardActivity.class);
		intent.putExtra("rule_url", rule_url);
		context.startActivity(intent);
	}

	protected void getIntentData() {
		 mRuleUrl = getIntent().getStringExtra("rule_url");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_hunt_reward_layout);
		initView();
	}

	private void initView() {
		ImageView iv_title_bar_icon = (ImageView) findViewById(R.id.iv_title_bar_icon);
		iv_title_bar_icon.setImageResource(R.drawable.icon_back);
		TextView tv_title_bar_titlename = (TextView) findViewById(R.id.tv_title_bar_titlename);
		tv_title_bar_titlename.setText("任务规则及奖励");
		findViewById(R.id.fl_title_bar_back).setOnClickListener(this);
		mWebviewReward = (WebView) findViewById(R.id.webview_reward);
		WebSettings webSettings = mWebviewReward.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// webSettings.setDefaultFixedFontSize(13);
		webSettings.setDefaultTextEncodingName("UTF-8");
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setSupportZoom(true);
		webSettings.setSaveFormData(false);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		mWebviewReward.setWebViewClient(new WebViewClient() {
			// @Override
			// public boolean shouldOverrideUrlLoading(WebView view, String url)
			// {
			// // TODO Auto-generated method stub
			// // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
			// view.loadUrl(url);
			// return true;
			// }

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
		mWebviewReward.setWebChromeClient(new WebChromeClient() {
		});
		mWebviewReward.loadUrl(mRuleUrl);
	}

	@Override
	public void onClick(View v) {
		finish();
	}

	@SuppressLint("NewApi")
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("RuleRewardActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("RuleRewardActivity");
		MobclickAgent.onPause(this);
	}
}

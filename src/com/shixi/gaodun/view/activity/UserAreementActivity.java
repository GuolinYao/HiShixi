package com.shixi.gaodun.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseWebViewContainsTitleActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 用户协议
 * 
 * @author ronger
 * @date:2015-10-20 下午2:57:23
 */
public class UserAreementActivity extends BaseWebViewContainsTitleActivity {
	public static void startAction(Activity context) {
		Intent intent = new Intent(context, UserAreementActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = false;
		setContentView(R.layout.webview_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("用户协议");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
		}
	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getUrl() {
		return "http://shixi.gaodun.com/AppView/userProtocol";
	}
	
	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("protocal"); //协议 
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("protocal"); 
		MobclickAgent.onPause(this);
	}
}
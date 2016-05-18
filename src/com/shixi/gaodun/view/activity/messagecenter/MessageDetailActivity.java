package com.shixi.gaodun.view.activity.messagecenter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseWebViewContainsTitleActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 消息详情
 * 
 * @author ronger
 * @date:2015-12-5 上午9:48:24
 */
public class MessageDetailActivity extends BaseWebViewContainsTitleActivity {
	public static void startAction(FragmentActivity context, String url) {
		Intent intent = new Intent(context, MessageDetailActivity.class);
		intent.putExtra("url", url);
		context.startActivity(intent);
	}

	@Override
	protected String getUrl() {
		return getIntent().getStringExtra("url");
	}

	@Override
	protected void getIntentData() {
	}

	@Override
	public void initView() {
		super.initView();
		isExcute = false;
		mTitleName.setText("消息");
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back)
			finish();

	}
	
	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("systemDetail"); //系统消息详情：systemDetail
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("systemDetail"); 
		MobclickAgent.onPause(this);
	}
}

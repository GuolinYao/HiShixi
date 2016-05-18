package com.shixi.gaodun.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseWebViewContainsTitleActivity;

public class BannerImageDetail extends BaseWebViewContainsTitleActivity {
	/**
	 * 登陆页的跳转
	 * 
	 * @param context
	 * @param imageLink
	 *            ：其他页进到登陆页，0：注册页进到登陆页
	 */
	public static void startAction(Context context, String imageLink) {
		Intent intent = new Intent(context, BannerImageDetail.class);
		intent.putExtra("imageLink", imageLink);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		context.startActivity(intent);
	}

	@Override
	public void initView() {
		super.initView();
		isExcute = false;
		// mTitleName.setText("消息");
	}

	@Override
	protected void getIntentData() {
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back)
			finish();
	}

	@Override
	protected String getUrl() {
		// TODO Auto-generated method stub
		return getIntent().getStringExtra("imageLink");
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}

}
// public class BannerImageDetail extends BaseMainActivity {
//
// private WebView mWebView;
// private String mImageLink;
//
// @Override
// protected void onCreate(Bundle arg0) {
// super.onCreate(arg0);
// BaseApplication.mApp.mCenterTaskActivitys.add(this);
// getIntentData();
//
// setContentView(R.layout.banner_image_link_detail);
// mWebView = (WebView) findViewById(R.id.banner_link);
// //加载h5
// mWebView.loadUrl(mImageLink);
// }
//
// /**
// * 登陆页的跳转
// *
// * @param context跳转页
// * @param sourceType来源类型1
// * ：其他页进到登陆页，0：注册页进到登陆页
// */
// public static void startAction(Context context, String imageLink) {
// Intent intent = new Intent(context, BannerImageDetail.class);
// intent.putExtra("imageLink", imageLink);
// // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
// context.startActivity(intent);
// }
//
// @Override
// protected void getIntentData() {
// Intent data = getIntent();
// mImageLink = data.getStringExtra("imageLink");
// }
//
// @Override
// public void onClick(View v) {
// }
//
// }

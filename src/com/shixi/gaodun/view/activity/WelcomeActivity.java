package com.shixi.gaodun.view.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.view.GifView;
import com.shixi.gaodun.view.activity.guide.GuideActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

/**
 * @author ronger guolin
 * @date:2015-11-12 上午11:38:10
 */
public class WelcomeActivity extends BaseMainActivity {

	private BaseMainActivity mContext;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// UmengUpdateAgent.update(this);// 友盟自动更新
		setContentView(R.layout.splash_layout);
		GifView gifview = (GifView) findViewById(R.id.gifview);
		gifview.setMovieResource(R.raw.splash);//设置gif动画
		mContext = WelcomeActivity.this;
		/**
		 * 友盟统计： 禁止默认的页面统计方式，这样将不会再自动统计Activity。
		 */
		MobclickAgent.openActivityDurationTrack(false);
		// 调试模式 TODO
		// MobclickAgent.setDebugMode(true);
		getToken();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// 判断是否是第一次打开
				if (CacheUtils.getIsFirstOpen(WelcomeActivity.this)) {
					// 如果是第一次打开
					CacheUtils.saveIsFirstOpen(WelcomeActivity.this, false);
					Intent intent = new Intent(WelcomeActivity.this,
							GuideActivity.class);
//					// 跳转到引导界面
					startActivity(intent);
					mContext.overridePendingTransition(R.anim.push_fade_in, R.anim.push_fade_out);
//					GuideActivity.startAction(mContext);
				} else {
					//如果不是第一次打开，直接跳转到主页
					delayedJump();
				}
				finish();// 结束当前界面
			}
		}, 2000);
	}
	
	
	@Override
	public void finish() {
		super.finish();
		finishTranstition();
	}
	
	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void finishTranstition() {
		overridePendingTransition(R.anim.push_fade_in, R.anim.push_fade_out);
	}
	
	@Override
	public void onResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0)
				return;
			JSONObject tokenObject = new JSONObject(httpRes.getReturnData());
			if (tokenObject.has("ticket"))
				CacheUtils.saveToken(this, tokenObject.getString("ticket"));
			if (tokenObject.has("expireTime"))
				CacheUtils.saveOldGetTokenTime(this,
						tokenObject.getLong("expireTime"));
		} catch (Exception e) {
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void delayedJump() {
		Intent intentMain = new Intent(this, MainActivity.class);
		startActivity(intentMain);
		mContext.overridePendingTransition(R.anim.push_fade_in, R.anim.push_fade_out);
	}

	@Override
	protected void getIntentData() {

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
	

}

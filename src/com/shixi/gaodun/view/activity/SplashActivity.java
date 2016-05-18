package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * @author guolinyao
 * @date 2016年2月23日 下午2:06:55
 */
public class SplashActivity extends Activity implements Listener<JSONObject>, ErrorListener {
	private String sharedPreferencesName = "spn_name";
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getToken();
		setContentView(R.layout.splash_layout);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				delayedJump();
			}
		}, 200);
	}

	/**
	 * 跳转 第一次进来的话跳转至引导页，否则跳转至首页
	 */
	@SuppressLint("CommitPrefEdits")
	public void delayedJump() {
		SharedPreferences mSharedPreferences = getSharedPreferences(sharedPreferencesName, 0);
		boolean ifFirst = mSharedPreferences.getBoolean("first", true);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		if (ifFirst) {
			mEditor.putBoolean("first", false);
			mEditor.commit();
			Intent intentMain = new Intent(this, MainActivity.class);
			startActivity(intentMain);
		} else {
			Intent intentMain = new Intent(this, MainActivity.class);
			startActivity(intentMain);
		}
		finish();
	}

	@Override
	protected void onStop() {
		BaseApplication.mApp.cancelPendingRequests("com.shixi.gaodun.view.activity.SplashActivity");
		super.onStop();
	}

	/**
	 * 获取所有命令的入口值token
	 */
	public void getToken() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("authUserAcc", GlobalContants.API_ACCOUNT_NUMER);
		map.put("authUserPwd", GlobalContants.API_PASSWORD);
		map.put("authSign", StringUtils.getMD5Str());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP() + GlobalContants.POWER_URL,
				map, this, this);
		BaseApplication.mApp.addToRequestQueue(request, "com.shixi.gaodun.view.activity.SplashActivity");
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				return;
			}
			JSONObject tokenObject = new JSONObject(httpRes.getReturnData());
			if (tokenObject.has("ticket"))
				CacheUtils.saveToken(this, tokenObject.getString("ticket"));
			if (tokenObject.has("expireTime"))
				CacheUtils.saveOldGetTokenTime(this, tokenObject.getLong("expireTime"));
		} catch (Exception e) {

		}

	}

}

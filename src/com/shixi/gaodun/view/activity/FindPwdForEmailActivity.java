package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ClearEditTextView;
import com.umeng.analytics.MobclickAgent;

/**
 * 通过邮箱找回密码
 * 
 * @author ronger
 * @date:2015-10-22 下午2:55:49
 */
public class FindPwdForEmailActivity extends TitleBaseActivity implements TextWatcher {
	private ClearEditTextView mEmailAccount;
	private Button mTestEmailBtn;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, FindPwdForEmailActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.find_pwd_by_email_layout);
		isExcute = false;
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("找回密码");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mEmailAccount = (ClearEditTextView) findViewById(R.id.cetv_forgetpwd_email_account);
		mTestEmailBtn = (Button) findViewById(R.id.btn_test_email);
		mEmailAccount.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			move = right;
			break;
		case R.id.btn_test_email:
			if (!StringUtils.isEmail(mEmailAccount.getText().toString())) {
				ToastUtils.showToastInCenter("邮箱格式有误!");
				return;
			}
			myProgressDialog.setTitle("正在发送验证邮件...");
			setRequestParamsPre(TAG);
			break;
		default:
			break;
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			dissMissProgress();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			ToastUtils.showCustomToastInCenter("请查看邮件完成重置密码", FindPwdForEmailActivity.this);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					delayedJump();
				}
			}, Finals.delayTime);
		} catch (Exception e) {
			dissMissProgress();
			setDebugLog(e);
			exceptionToast();
		}
	}

	@Override
	public void delayedJump() {
		super.delayedJump();
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("sourceType", 1);
		intent.putExtra("ifOutLogin", false);
		// 如果activity在task存在，将Activity之上的所有Activity结束掉)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		// LoginActivity.startAction(this, 1, false);
		// finish();
	}

	/**
	 * 验证邮箱
	 */
	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("retrieveAcc", mEmailAccount.getText().toString());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.FINDPSSWORD_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String emailAccount = mEmailAccount.getText().toString();
		if (StringUtils.isNotEmpty(emailAccount)) {
			mTestEmailBtn.setEnabled(true);
		} else {
			mTestEmailBtn.setEnabled(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("emailFindPassWord"); //邮箱找回密码 
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("emailFindPassWord"); 
		MobclickAgent.onPause(this);
	}
}

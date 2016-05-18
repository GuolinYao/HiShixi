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
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.TimerCountDownCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TimerCountDown;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ClearEditTextView;

/**
 * 修改密码
 * 
 * @author ronger
 * @date:2015-10-22 下午6:04:00
 */
public class UpdatePwdActivity extends TitleBaseActivity implements TextWatcher, TimerCountDownCallBack {
	// 是否获取验证码或重置密码：true:获取验证码，false:重置密码
	private boolean ifGetCodeOrResetPwd;
	private ClearEditTextView mVerificationCode;
	private ClearEditTextView mNewPassWord;
	private Button mVerificationCodeBtn;
	private Button mResetPwdBtn;
	private TextView mPhoneNumberText;
	private String mPhoneNumber;

	public static void startAction(Activity context, String account) {
		Intent intent = new Intent(context, UpdatePwdActivity.class);
		intent.putExtra("account", account);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.update_password_layout);
		mPhoneNumberText.setText(mPhoneNumber);
	}

	@Override
	protected void getIntentData() {
		mPhoneNumber = getIntent().getStringExtra("account");
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		super.initView();
		mTitleName.setText("修改密码");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mVerificationCode = (ClearEditTextView) findViewById(R.id.cetv_updatepwd_verification_code);
		mNewPassWord = (ClearEditTextView) findViewById(R.id.cetv_updatepwd_newpwd);
		mVerificationCodeBtn = (Button) findViewById(R.id.btn_updatepwd_verification_code);
		mResetPwdBtn = (Button) findViewById(R.id.btn_update_pssword);
		mPhoneNumberText = (TextView) findViewById(R.id.tv_updatepwd_phone_number);
		mVerificationCode.addTextChangedListener(this);
		mNewPassWord.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		// 获取验证码
		if (v.getId() == R.id.btn_updatepwd_verification_code) {
			ifGetCodeOrResetPwd = true;
			myProgressDialog.setTitle("获取验证码中...");
			setRequestParamsPre(TAG);
			return;
		}
		// 返回
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
			return;
		}
		// 重置密码
		if (v.getId() == R.id.btn_update_pssword) {
			ifGetCodeOrResetPwd = false;
			myProgressDialog.setTitle("正在提交...");
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (ifGetCodeOrResetPwd) {
			getCode();
		} else {
			resetPwd();
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			dissMissProgress();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			if (ifGetCodeOrResetPwd) {
				toGetCodeSuccess(httpRes.getReturnDataJson());
			} else {
				toResetPwdSuccess(httpRes.getReturnDataJson());
			}
		} catch (Exception e) {
			dissMissProgress();
			setDebugLog(e);
			exceptionToast();
		}
	}

	/**
	 * 获取验证码
	 */
	public void getCode() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("registMobile", mPhoneNumber);
		map.put("type", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.REGISTER_GETCODE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取验证码成功了
	 * 
	 * @param resultData
	 */
	public void toGetCodeSuccess(String resultData) {
		ToastUtils.showToastInCenter("验证码已发送，请注意查收!");
		mVerificationCodeBtn.setEnabled(false);
		new TimerCountDown(Finals.codedesplay, 1000, this).start();
	}

	/**
	 * 重置密码
	 */
	public void resetPwd() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("mobile", mPhoneNumber);
		map.put("newPwd", mNewPassWord.getText().toString());
		map.put("code", mVerificationCode.getText().toString());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.UPDATEPWD_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void toResetPwdSuccess(String resultData) {
		// 谈toast,并且延迟跳转
		ToastUtils.showCustomToastInCenter("密码重置成功", this);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				delayedJump();
			}
		}, Finals.delayTime);
	}

	@Override
	public void delayedJump() {
		super.delayedJump();
		finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String code = mVerificationCode.getText().toString();
		String pwd = mNewPassWord.getText().toString();
		if (StringUtils.isEmpty(code) || StringUtils.isEmpty(pwd)) {
			mResetPwdBtn.setEnabled(false);
			return;
		}
		mResetPwdBtn.setEnabled(true);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timing(long millisUntilFinished) {
		mVerificationCodeBtn.setEnabled(false);
		mVerificationCodeBtn.setText(millisUntilFinished / 1000 + "s后重新获取");
	}

	@Override
	public void timeFinish() {
		mVerificationCodeBtn.setEnabled(true);
		mVerificationCodeBtn.setText("获取验证码");
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}
}

package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
 * 找回密码
 * 
 * @author ronger
 * @date:2015-10-20 下午4:24:35
 */
public class FindPwdForPhoneActivity extends TitleBaseActivity implements TextWatcher, TimerCountDownCallBack {
	private ClearEditTextView mPhoneNumber;
	private ClearEditTextView mVerificationCode;
	private Button mVerificationCodeBtn;
	private ClearEditTextView mPassWord;
	private ImageView mIfShowPwd;
	private Button mResetPwdBtn;
	// 是否隐藏密码:true:隐藏，false:不隐藏
	private boolean ifShowPwd = false;
	private boolean ifGetCode;
	private boolean isRuning = false;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, FindPwdForPhoneActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = false;
		setContentView(R.layout.forget_password_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("找回密码");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mPhoneNumber = (ClearEditTextView) findViewById(R.id.cetv_forgetpwd_phone_number);
		mVerificationCode = (ClearEditTextView) findViewById(R.id.cetv_forgetpwd_verification_code);
		mVerificationCodeBtn = (Button) findViewById(R.id.btn_forgetpwd_verification_code);
		mPassWord = (ClearEditTextView) findViewById(R.id.cetv_forgetpwd_password);
		mIfShowPwd = (ImageView) findViewById(R.id.iv_forgetpwd_show_password);
		mResetPwdBtn = (Button) findViewById(R.id.btn_reset_password);
		mPhoneNumber.addTextChangedListener(this);
		mVerificationCode.addTextChangedListener(this);
		mPassWord.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			move = right;
			break;
		case R.id.btn_forgetpwd_verification_code:
			ifGetCode = true;
			setRequestParamsPre(TAG);
			break;
		case R.id.btn_reset_password:
			ifGetCode = false;
			setRequestParamsPre(TAG);
			break;
		case R.id.fl_register_show_password:
			ifShowPwd = !ifShowPwd;
			setPassWordShow();
			break;
		default:
			break;
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (ifGetCode) {
			getCode();
		} else {
			resetPwd();
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		dissMissProgress();
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			if (ifGetCode) {
				toGetCodeSuccess(httpRes.getReturnData());
			} else {
				toResetPwdSuccess(httpRes.getReturnData());
			}
		} catch (Exception e) {
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
		map.put("registMobile", mPhoneNumber.getText().toString());
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
		map.put("retrieveAcc", mPhoneNumber.getText().toString());
		map.put("retrievePwd", mPassWord.getText().toString());
		map.put("retrieveCode", mVerificationCode.getText().toString());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.FINDPSSWORD_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 重置密码成功
	 * 
	 * @param resultData
	 */
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
		// 把中间打开的页面消掉
		// LoginActivity.startAction(this, 1, false);
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("sourceType", 1);
		intent.putExtra("ifOutLogin", false);
		// 如果activity在task存在，将Activity之上的所有Activity结束掉)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 * 设置密码的显示与隐藏
	 */
	public void setPassWordShow() {
		mIfShowPwd.setImageResource(ifShowPwd ? R.drawable.icon_hide_password : R.drawable.icon_show_password);
		// 普通文本
		int inputtype_showpwd = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
		// 密码
		int inputtype_hidpwd = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
		mPassWord.setInputType(ifShowPwd ? inputtype_hidpwd : inputtype_showpwd);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String phoneNumber = mPhoneNumber.getText().toString();
		String password = mPassWord.getText().toString();
		String verificationCode = mVerificationCode.getText().toString();
		if (StringUtils.isNotEmpty(phoneNumber) && StringUtils.isPhoneNum(phoneNumber) && !isRuning) {
			mVerificationCodeBtn.setEnabled(true);
		} else {
			mVerificationCodeBtn.setEnabled(false);
		}
		if (StringUtils.isEmpty(phoneNumber) || StringUtils.isEmpty(password) || StringUtils.isEmpty(verificationCode)) {
			mResetPwdBtn.setEnabled(false);
			return;
		}
		mResetPwdBtn.setEnabled(true);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void timing(long millisUntilFinished) {
		isRuning = true;
		mVerificationCodeBtn.setEnabled(false);
		mVerificationCodeBtn.setText(millisUntilFinished / 1000 + "s后重新获取");
	}

	@Override
	public void timeFinish() {
		isRuning = false;
		mVerificationCodeBtn.setEnabled(true);
		mVerificationCodeBtn.setText("获取验证码");
	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}

}

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
 * 验证手机号
 * 
 * @author ronger
 * @date:2015-10-22 下午6:11:13
 */
public class TestPhoneNumberActivity extends TitleBaseActivity implements TextWatcher, TimerCountDownCallBack {
	// 是否获取验证码或验证：true:获取验证码，false:验证
	private boolean ifGetCodeOrTest;
	private String mPhoneNumber;
	private ClearEditTextView mVerificationCode;
	private ClearEditTextView mPhoneNumberText;
	private Button mTestBtn;
	private Button mVerificationCodeBtn;
	private boolean isFinish = true;

	public static void startAction(Activity context, String account, int requestCode) {
		Intent intent = new Intent(context, TestPhoneNumberActivity.class);
		intent.putExtra("phoneNumber", account);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		mPhoneNumber = getIntent().getStringExtra("phoneNumber");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.test_phonenumber_layout);
		mPhoneNumberText.setText(StringUtils.isEmpty(mPhoneNumber) ? "" : mPhoneNumber);
		mTestBtn.setEnabled(StringUtils.isEmpty(mPhoneNumber) ? false : true);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("验证手机");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mVerificationCode = (ClearEditTextView) findViewById(R.id.cetv_testphone_verification_code);
		mPhoneNumberText = (ClearEditTextView) findViewById(R.id.cetv_testphone_number);
		mTestBtn = (Button) findViewById(R.id.btn_testphone);
		mVerificationCodeBtn = (Button) findViewById(R.id.btn_testphone_verification_code);
		mVerificationCode.addTextChangedListener(this);
		mPhoneNumberText.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		// 获取验证码
		if (v.getId() == R.id.btn_testphone_verification_code) {
			ifGetCodeOrTest = true;
			myProgressDialog.setTitle("正在发送验证码...");
			setRequestParamsPre(TAG);
			return;
		}
		// 返回
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
			return;
		}
		// 验证
		if (v.getId() == R.id.btn_testphone) {
			ifGetCodeOrTest = false;
			myProgressDialog.setTitle("正在提交...");
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (ifGetCodeOrTest) {
			getCode();
		} else {
			toTest();
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
			if (ifGetCodeOrTest) {
				toGetCodeSuccess(httpRes.getReturnDataJson());
			} else {
				testPhoneSuccess(httpRes.getReturnDataJson());
			}
		} catch (Exception e) {
			dissMissProgress();
			exceptionToast();
		}
	}

	/**
	 * 获取验证码
	 */
	public void getCode() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("registMobile", mPhoneNumberText.getText().toString());
		map.put("type", "3");
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
	 * 验证手机号
	 */
	public void toTest() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("mobile", mPhoneNumberText.getText().toString());
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("code", mVerificationCode.getText().toString());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.TESTPHONENUMBER_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void testPhoneSuccess(String resultData) {
		// 谈toast,并且延迟跳转
		ToastUtils.showCustomToastInCenter("验证成功", this);
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
		setResult(Activity.RESULT_OK);
		finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String phoneNumber = mPhoneNumberText.getText().toString();
		String code = mVerificationCode.getText().toString();
		if (StringUtils.isNotEmpty(phoneNumber) && StringUtils.isPhoneNum(phoneNumber) && isFinish) {
			mVerificationCodeBtn.setEnabled(true);
		} else {
			mVerificationCodeBtn.setEnabled(false);
		}
		if (StringUtils.isEmpty(code)) {
			mTestBtn.setEnabled(false);
			return;
		}
		mTestBtn.setEnabled(true);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timing(long millisUntilFinished) {
		isFinish = false;
		mVerificationCodeBtn.setEnabled(false);
		mVerificationCodeBtn.setText(millisUntilFinished / 1000 + "s后重新获取");
	}

	@Override
	public void timeFinish() {
		isFinish = true;
		mVerificationCodeBtn.setEnabled(true);
		mVerificationCodeBtn.setText("获取验证码");
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}
}

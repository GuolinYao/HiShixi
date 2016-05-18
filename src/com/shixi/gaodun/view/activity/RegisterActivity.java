package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
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
import com.umeng.analytics.MobclickAgent;

/**
 * 注册页
 * 
 * @author ronger
 * @date:2015-10-20 上午9:53:51
 */
public class RegisterActivity extends TitleBaseActivity implements TimerCountDownCallBack, TextWatcher {
	// 1其他页进到注册页，0：登陆页进到注册页
	private int mSourceType = 1;
	private ClearEditTextView mPhoneNumber;
	private ClearEditTextView mVerificationCode;
	private Button mVerificationCodeBtn;
	private ClearEditTextView mPassWord;
	private ImageView mIfShowPwd;
	private Button mRegisterBtn;
	private TextView mUserAgreement;
	private boolean ifGetCode;
	// 是否隐藏密码:true:隐藏，false:不隐藏
	private boolean ifShowPwd = false;
	private boolean isRuning = false;

	public static void startAction(BaseMainActivity context, int sourceType, int move) {
		Intent intent = new Intent(context, RegisterActivity.class);
		intent.putExtra("sourceType", sourceType);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.setMove(move);
		context.startActivity(intent);
		// 还原
		context.setMove(1);
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mSourceType = data.getIntExtra("sourceType", 1);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		BaseApplication.mApp.mCenterTaskActivitys.add(this);
		isExcute = false;
		getIntentData();
		setContentView(R.layout.register_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("注册");
		mBackIcon.setImageResource(mSourceType == 1 ? R.drawable.icon_close : R.drawable.icon_back);
		mOtherName.setTextColor(getResources().getColor(R.color.title_bar_font_other_color));
		mOtherName.setVisibility(mSourceType == 1 ? View.VISIBLE : View.GONE);
		mOtherName.setText("登录");
		mPhoneNumber = (ClearEditTextView) findViewById(R.id.cetv_register_phone_number);
		mVerificationCode = (ClearEditTextView) findViewById(R.id.cetv_register_verification_code);
		mVerificationCodeBtn = (Button) findViewById(R.id.btn_verification_code);
		mPassWord = (ClearEditTextView) findViewById(R.id.cetv_register_password);
		mIfShowPwd = (ImageView) findViewById(R.id.iv_register_show_password);
		mRegisterBtn = (Button) findViewById(R.id.btn_register);
		mUserAgreement = (TextView) findViewById(R.id.tv_user_agreement);
		mPhoneNumber.addTextChangedListener(this);
		mVerificationCode.addTextChangedListener(this);
		mPassWord.addTextChangedListener(this);
		setUserAgreement();
	}

	/**
	 * 注册用户协议文字处理
	 */
	public void setUserAgreement() {
		String str = "注册即表示你已同意《Hi实习用户协议》";
		SpannableString spannableString1 = new SpannableString(str);
		spannableString1.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				UserAreementActivity.startAction(RegisterActivity.this);
			}

			@Override
			public void updateDrawState(TextPaint ds) {
				// super.updateDrawState(ds);
				ds.setUnderlineText(false); // 去掉下划线
			}
		}, 9, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.user_agreement_color)), 9,
				str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mUserAgreement.setText(spannableString1);
		mUserAgreement.setMovementMethod(LinkMovementMethod.getInstance());
	}

	/**
	 * 注册或获取验证码
	 */
	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (ifGetCode) {
			getCode();
		} else {
			toRegister();
		}
	}

	/**
	 * 去注册
	 */
	public void toRegister() {
		BaseApplication.mApp.cancelPendingRequests(TAG);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("registAcc", mPhoneNumber.getText().toString());
		map.put("registPwd", mPassWord.getText().toString());
		map.put("registCode", mVerificationCode.getText().toString());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.REGISTER_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 注册成功了
	 * 
	 * @throws JSONException
	 */
	public void toRegisterSuccess(String resultData) throws JSONException {
		JSONObject jsonObject = new JSONObject(resultData);
		if (jsonObject.has("studentId")) {
			CacheUtils.saveStudentId(this, jsonObject.getString("studentId"));
		}
		if (jsonObject.has("accountId")) {
			CacheUtils.saveAccountId(this, jsonObject.getString("accountId"));
		}
		CacheUtils.saveLoginAccount(this, mPhoneNumber.getText().toString());
		// 谈toast,并且延迟跳转
		ToastUtils.showToastInCenter("注册成功");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				delayedJump();
			}
		}, Finals.delayTime);
	}

	/**
	 * 注册完了之后直接跳转到引导页，引导用户补充基本信息
	 */
	@Override
	public void delayedJump() {
		// 从登陆页进到注册页
		if (mSourceType == 0) {
			sendBroadcast();
			finishPartProcess();
			return;
		}
		// 非登陆页进入注册页
		sendBroadcast();
		finish();
	}

	// public void updateOther() {
	// ActivityUtils.getLocalBroadcastManager(this);
	// Intent intent = new Intent();
	// intent.setAction(Contants.UPDATE_SETTING);// 更新设置页
	// intent.setAction(Contants.UPDATE_USERBASIVINFO);// 更新基本信息页
	// intent.setAction(Contants.ALL_UPDATE_ACTION);// 更新触发当前页面的数据
	// BaseApplication.mApp.mLocalBroadcastManager.sendBroadcast(intent);
	// }

	/**
	 * 获取验证码成功了
	 * 
	 * @param resultData
	 * @throws JSONException
	 */
	public void toGetCodeSuccess(String resultData) throws JSONException {
		// JSONObject object = new JSONObject(resultData);
		// if (object.has("registCode")) {
		// mVerificationCode.setText(object.getString("registCode"));
		// }
		ToastUtils.showToastInCenter("验证码已发送，请注意查收!");
		mVerificationCodeBtn.setEnabled(false);
		// mVerificationCodeBtn.setText(30000 / 1000 + "秒后可重新获取");
		new TimerCountDown(Finals.codedesplay, 1000, this).start();

	}

	/**
	 * 获取验证码
	 */
	public void getCode() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("registMobile", mPhoneNumber.getText().toString());
		map.put("type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.REGISTER_GETCODE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/***
	 * 注册请求或获取验证码请求服务器成功
	 */
	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			myProgressDialog.dismiss();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			if (ifGetCode) {
				toGetCodeSuccess(httpRes.getReturnData());
			} else {
				toRegisterSuccess(httpRes.getReturnData());
			}
		} catch (Exception e) {
			myProgressDialog.dismiss();
			ToastUtils.showToastInCenter("数据解析错误");
		}

	}

	@SuppressLint("NewApi")
	@Override
	public void finishTranstition() {
		// x：向下滑出(点击对话框中的登陆按钮进来)，返回按钮向右滑出
		if (mSourceType == 1) {
			// overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
			overridePendingTransition(0, R.anim.slide_down_out);
		} else {
			// overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
			overridePendingTransition(0, R.anim.slide_right);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			// // 设置关闭动画
			// if (mSourceType == 1) {
			// // 向上滑出出现，向下滑出隐藏
			// move = bottom;
			// } else {
			// // 向左滑出出现，向右滑出隐藏
			// move = right;
			// }
			finish();
			break;
		case R.id.tv_title_bar_otherbtn:
			LoginActivity.startAction(this, 0, false);
			break;
		case R.id.fl_register_show_password:
			ifShowPwd = !ifShowPwd;
			setPassWordShow();
			break;
		case R.id.btn_verification_code:
			myProgressDialog.setTitle("获取验证码中...");
			ifGetCode = true;
			setRequestParamsPre(TAG);
			break;
		case R.id.btn_register:
			myProgressDialog.setTitle("注册中...");
			ifGetCode = false;
			// setRequestParams(TAG);
			setRequestParamsPre(TAG);
			break;
		default:
			break;
		}
	}

	// new TimerCountDown(Final.millisInFuture, Final.countDownInterval).start();
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
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

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
			mRegisterBtn.setEnabled(false);
			mRegisterBtn.setTextColor(Color.parseColor("#80ffffff"));
			return;
		}
		mRegisterBtn.setEnabled(true);
		mRegisterBtn.setTextColor(Color.parseColor("#ffffff"));
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

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
	public void onBackPressed() {
		BaseApplication.mApp.cancelPendingRequests(TAG);
		myProgressDialog.dismiss();
		super.onBackPressed();
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
		MobclickAgent.onPageStart("regist"); // 注册
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("regist");
		MobclickAgent.onPause(this);
	}
}

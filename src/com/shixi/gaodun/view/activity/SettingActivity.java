package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 设置
 * 
 * @author ronger guolin
 * @date:2015-10-22 下午3:45:20
 */
public class SettingActivity extends TitleBaseActivity {
	// 是否点击了退出登录true:点击了，false:修改密码
	private boolean ifClickOutLogin = false;
	private Dialog mDialog;
	private String mPhoneNumber, mEmailAccount, mPhoneTestState,
			mEmailTestState;
	// 验证手机号
	private final int TESTPHONE_REQUESTCODE = 1001, TOLOGIN_REQUESTCODE = 1002;
	private LinearLayout mClickTestPhone, mClickTestEmail;
	private TextView mPhoneText, mEmailText;
	private TextView mCurrentVersion;
	private Button mOutLoginBtn;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, SettingActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.setting_layout);
		isExcute = false;
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this))) {
			setRequestParamsPre(TAG);
		} else {
			noLoginShow();
		}
		setFilterRegister();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("设置");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mClickTestPhone = (LinearLayout) findViewById(R.id.ll_setting_phone);
		mClickTestEmail = (LinearLayout) findViewById(R.id.ll_setting_email);
		mPhoneText = (TextView) findViewById(R.id.tv_setting_phone);
		mEmailText = (TextView) findViewById(R.id.tv_setting_email);
		mCurrentVersion = (TextView) findViewById(R.id.tv_current_version);
		mOutLoginBtn = (Button) findViewById(R.id.btn_login);
		if (StringUtils.isEmpty(CacheUtils.getAccountId(this))) {
			mOutLoginBtn.setVisibility(View.GONE);
		}
		mCurrentVersion.setText("当前版本：  " + ActivityUtils.getVersionName());
	}

	/**
	 * 未登陆时显示
	 */
	public void noLoginShow() {
		mClickTestEmail.setEnabled(true);
		mEmailText.setText("未验证");
		mClickTestPhone.setEnabled(true);
		mPhoneText.setText("未验证");
		mPhoneText.setTextColor(getResources().getColor(
				R.color.title_bar_font_color));
		mEmailText.setTextColor(getResources().getColor(
				R.color.title_bar_font_color));
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		noLoginShow();
		super.onErrorResponse(error);
	}

	/**
	 * 获取验证信息
	 */
	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETTESTINFO_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
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
			getTestInfoSuccess(httpRes.getReturnData());
		} catch (Exception e) {
			setDebugLog(e);
			dissMissProgress();
			exceptionToast();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.tv_setting_opinion_feedback:
			OpinionFeedbackActivity.startAction(this);
			break;
		case R.id.tv_setting_update_pwd:
			ifClickOutLogin = false;
			if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				return;
			}
			// 手机号已验证
			if (StringUtils.isNotEmpty(mPhoneTestState)
					&& mPhoneTestState.equals("1")) {
				UpdatePwdActivity.startAction(SettingActivity.this,
						mPhoneNumber);
				break;
			}
			// 手机号未验证
			mDialog = CustomDialog.CancelAlertToDialog("修改密码前需要先验证手机", "验证手机",
					"取消", this, this);
			break;
		case R.id.btn_login:
			ifClickOutLogin = true;
			mDialog = CustomDialog.CancelAlertToDialog("确定要退出登录吗？", "退出",
					"再逛逛", this, this);
			break;
		// 退出登陆或者验证手机
		case R.id.tv_confrim_btn:
			mDialog.dismiss();
			if (ifClickOutLogin) {
				CacheUtils.clearUserInfo(this);
				// 跳转至登录页
//				LoginActivity.startAction(this, 1, true);
				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				move = top;
				break;
			}
			TestPhoneNumberActivity.startAction(this, mPhoneNumber,
					TESTPHONE_REQUESTCODE);
			break;
		// 再逛逛或者取消
		case R.id.tv_cancel_btn:
			if (mDialog.isShowing())
				mDialog.dismiss();
			break;
		case R.id.tv_title_cancel_btn:
			if (mDialog.isShowing())
				mDialog.dismiss();
			break;
		case R.id.tv_title_confrim_btn:
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			LoginActivity.startAction(this, 1, false);
			move = left;
			if (mDialog.isShowing())
				mDialog.dismiss();
			break;
		case R.id.btn_select_two:// 去登陆
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			LoginActivity.startAction(this, 1, false);
			mDialog.dismiss();
			break;
		case R.id.btn_select_three:// 去注册
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			RegisterActivity.startAction(this, 1, top);
			mDialog.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (null != mDialog && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	/**
	 * 验证手机号的点击
	 * 
	 * @param view
	 */
	public void onClickTestPhone(View view) {
		// 判断手机号码是否已经验证过
		if (!(mPhoneText.getText().toString().equals("未验证") || mPhoneText
				.getText().toString().equals("马上验证"))) {
			Toast.makeText(this, "手机号码已验证", 0).show();
			return;
		}

		if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
			mDialog = CustomDialog.AlertToCustomDialog(this, this);
			return;
		}

		TestPhoneNumberActivity.startAction(SettingActivity.this, mPhoneNumber,
				TESTPHONE_REQUESTCODE);
	}

	/**
	 * 验证邮箱号的点击
	 * 
	 * @param view
	 */
	public void onClickTestEmail(View view) {
		// 判断邮箱是否已经验证过
		if (!(mEmailText.getText().toString().equals("未验证") || mEmailText
				.getText().toString().equals("马上验证"))) {
			Toast.makeText(this, "邮箱已验证", 0).show();
			return;
		}

		if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
			mDialog = CustomDialog.AlertToCustomDialog(this, this);
			return;
		}

		TestEmailAccountActivity.startAction(SettingActivity.this,
				mEmailAccount);
	}

	/**
	 * 评价APP
	 * 
	 * getPackageName():对应的是该APP在应用市场上的包名
	 * 
	 * @param view
	 */
	public void onClickComment(View view) {
		try {
			Uri uri = Uri.parse("market://details?id=" + getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} catch (Exception e) {
			setDebugLog(e);
			Toast.makeText(this, "Couldn't launch the market !",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		// 验证手机或登陆后返回
		if (requestCode == TESTPHONE_REQUESTCODE
				|| requestCode == TOLOGIN_REQUESTCODE) {
			setRequestParamsPre(TAG);
		}
	}

	/**
	 * 获取验证信息成功
	 * 
	 * @param resultData
	 * @throws JSONException
	 */
	public void getTestInfoSuccess(String resultData) throws JSONException {
		JSONObject jsonObject = new JSONObject(resultData);
		if (jsonObject.has("mobile_verify")) {
			mPhoneTestState = jsonObject.getString("mobile_verify");
		}
		if (jsonObject.has("email_verify")) {
			mEmailTestState = jsonObject.getString("email_verify");
		}
		if (jsonObject.has("login_email")) {
			mEmailAccount = jsonObject.getString("login_email");
		}
		if (jsonObject.has("login_mobile")) {
			mPhoneNumber = jsonObject.getString("login_mobile");
		}
		if (null != mPhoneTestState && mPhoneTestState.equals("1")) {
			// mClickTestPhone.setEnabled(false);
			mPhoneText.setText(mPhoneNumber);
			mPhoneText.setTextColor(getResources().getColor(
					R.color.nomal_btn_color));
		} else {
			mClickTestPhone.setEnabled(true);
			mPhoneText.setText("马上验证");
			mPhoneText.setTextColor(getResources().getColor(
					R.color.main_font_nonomal_color));
		}
		if (null != mEmailTestState && mEmailTestState.equals("1")) {
			// mClickTestEmail.setEnabled(false);
			mEmailText.setText(mEmailAccount);
			mEmailText.setTextColor(getResources().getColor(
					R.color.nomal_btn_color));
		} else {
			mClickTestEmail.setEnabled(true);
			mEmailText.setText("马上验证");
			mEmailText.setTextColor(getResources().getColor(
					R.color.main_font_nonomal_color));
		}

	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("set"); // 设置页面：set
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("set");
		MobclickAgent.onPause(this);
	}
}

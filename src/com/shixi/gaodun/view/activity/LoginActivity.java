package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
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
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.ClearEditTextView;
import com.umeng.analytics.MobclickAgent;

/**
 * 登陆页
 * 
 * @author ronger
 * @date:2015-10-20 上午9:27:16
 */
public class LoginActivity extends TitleBaseActivity implements TextWatcher, OnAlertSelectId {
	private int mSourceType = 1;
	private ClearEditTextView mAccountNumber;
	private ClearEditTextView mPassWord;
	private ImageView mIfShowPwdIcon;
	private Button mLoginBtn;
	// private TextView mForgetPwd;
	// 是否隐藏密码:true:隐藏，false:不隐藏
	private boolean ifShowPwd = false;
	private boolean ifOutLogin = false;
	private Dialog mDialog;

	/**
	 * 登陆页的跳转
	 * 
	 * @param context跳转页
	 * @param sourceType来源类型1
	 *            ：其他页进到登陆页，0：注册页进到登陆页
	 */
	public static void startAction(BaseMainActivity context, int sourceType, boolean ifOutLogin) {
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("sourceType", sourceType);
		intent.putExtra("ifOutLogin", ifOutLogin);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.setMove(3);
		context.startActivity(intent);
		context.setMove(1);
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mSourceType = data.getIntExtra("sourceType", 1);
		ifOutLogin = data.getBooleanExtra("ifOutLogin", false);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		CacheUtils.saveIsFirstLogin(this, false);
		BaseApplication.mApp.mCenterTaskActivitys.add(this);
		getIntentData();
		if (mSourceType == 0) {
			move = bottom;
		}
		isExcute = false;
		setContentView(R.layout.login_layout);
		ifShowPwd = true;
		setPassWordShow();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("登录");
		mBackIcon.setImageResource(mSourceType == 1 ? R.drawable.icon_close : R.drawable.icon_back);
		mOtherName.setTextColor(getResources().getColor(R.color.title_bar_font_other_color));
		mOtherName.setVisibility(mSourceType == 1 ? View.VISIBLE : View.GONE);
		mOtherName.setText("注册");
		mAccountNumber = (ClearEditTextView) findViewById(R.id.cetv_account_number);
		mPassWord = (ClearEditTextView) findViewById(R.id.cetv_password);
		mIfShowPwdIcon = (ImageView) findViewById(R.id.iv_show_password);
		mLoginBtn = (Button) findViewById(R.id.btn_login);
		// mForgetPwd = (TextView) findViewById(R.id.tv_forget_password);
		mAccountNumber.addTextChangedListener(this);
		mPassWord.addTextChangedListener(this);
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
			if (ifOutLogin) {// 从设置页点击退出登录后进入
				Intent intent = new Intent(this, MainActivity.class);
				// intent.putExtra("currentIndex", 3);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				break;
			}
			// // setResult(Activity.RESULT_OK);
			// // 设置关闭动画
			// if (mSourceType == 1) {
			// // 非注册页进入，向下滑出
			// move = bottom;
			// } else {
			// // 注册页进入
			// move = right;
			// }
			finish();
			break;
		case R.id.tv_title_bar_otherbtn:
			RegisterActivity.startAction(this, 0, left);
			break;
		case R.id.btn_login:
			myProgressDialog.setTitle("登陆中...");
			setRequestParamsPre(TAG);
			break;
		case R.id.tv_forget_password:
			mDialog = ActionSheetDialog.showAlert(this, this, "手机找回密码", "邮箱找回密码");
			// ForgetPwdActivity.startAction(this);
			break;
		case R.id.fl_show_password:
			ifShowPwd = !ifShowPwd;
			setPassWordShow();
			break;
		case R.id.tv_confrim_btn:
			finishProcess();
			break;
		case R.id.tv_cancel_btn:
			mDialog.dismiss();
			break;
		default:
			break;
		}
	}

	@Override
	public void onResponse(JSONObject response) {

		super.onResponse(response);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub
		super.onErrorResponse(error);
	}

	/**
	 * 设置密码的显示与隐藏
	 */
	public void setPassWordShow() {
		mIfShowPwdIcon.setImageResource(ifShowPwd ? R.drawable.icon_hide_password : R.drawable.icon_show_password);
		// 普通文本
		int inputtype_showpwd = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
		// 密码
		int inputtype_hidpwd = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
		mPassWord.setInputType(ifShowPwd ? inputtype_hidpwd : inputtype_showpwd);
	}

	@Override
	public void setRequestParams(String className) {
		// TODO Auto-generated method stub
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("loginAcc", mAccountNumber.getText().toString());
		map.put("loginPwd", mPassWord.getText().toString());
		map.put("type", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP() + GlobalContants.LOGIN_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 登陆请求服务器成功时调用 完了之后需要关闭获取token时或者走本接口时的进度
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
			toLoginSuccecc(httpRes.getReturnData());
		} catch (Exception e) {
			myProgressDialog.dismiss();
			ToastUtils.showToastInCenter("数据解析错误");
		}
	}

	/**
	 * 登陆成功
	 * 
	 * @param data
	 * @throws JSONException
	 */
	public void toLoginSuccecc(String data) throws JSONException {
		JSONObject jsonObject = new JSONObject(data);
		if (jsonObject.has("accountId")) {
			CacheUtils.saveAccountId(this, jsonObject.getString("accountId"));
		}
		if (jsonObject.has("studentId")) {
			CacheUtils.saveStudentId(this, jsonObject.getString("studentId"));
		}
		if (jsonObject.has("complete_rate"))
			CacheUtils.saveComplete_rate(this, jsonObject.getString("complete_rate"));
		if (jsonObject.has("complete_rate_en")) {
			CacheUtils.saveComplete_rate_en(this, jsonObject.getString("complete_rate_en"));
		}
		//是否是猎头
		if (jsonObject.has("is_hunter")) {
			CacheUtils.saveIf_is_hunter(this, jsonObject.getString("is_hunter"));
		}
		System.out.println("是猎头===="+jsonObject.getString("is_hunter"));
		//如果是 猎头id
		if (jsonObject.has("hunter_id")) {
			CacheUtils.save_hunter_id(this, jsonObject.getString("hunter_id"));
		}
		CacheUtils.saveLoginAccount(this, mAccountNumber.getText().toString());
		// 谈toast,并且延迟跳转
		// ToastUtils.showToastInCenter("登陆成功");
		// float complete_rate = StringUtils.isEmpty(CacheUtils.getComplete_rate(this)) ? 0.0f : Float
		// .parseFloat(CacheUtils.getComplete_rate(this));
		// // if (complete_rate <= 11) {
		// // BasicInfoOneActivity.startAction(this);
		// // return;
		// // }
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				delayedJump();
			}
		}, Finals.delayTime);
	}

	/**
	 * 登陆成功的跳转:跳转到首页，关闭当前页
	 */
	@Override
	public void delayedJump() {
		// 从注册页进到登陆页
		if (mSourceType == 0) {
			sendBroadcast();
			finishPartProcess();
			return;
		}
		// 非注册页进入登陆页
		sendBroadcast();
		finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String accountNumber = mAccountNumber.getText().toString();
		String passWord = mPassWord.getText().toString();
		if (StringUtils.isEmpty(accountNumber) || StringUtils.isEmpty(passWord)) {
			mLoginBtn.setEnabled(false);
			mLoginBtn.setTextColor(Color.parseColor("#80ffffff"));
			return;
		}
		mLoginBtn.setEnabled(true);
		mLoginBtn.setTextColor(Color.parseColor("#ffffff"));
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void onClick(int whichButton) {
		if (whichButton == 1) {
			FindPwdForPhoneActivity.startAction(this);
			return;
		}
		if (whichButton == 2) {
			FindPwdForEmailActivity.startAction(this);
		}
	}

	@Override
	public void onBackPressed() {
		if (ifOutLogin) {
			exit();
			return;
		}
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
		MobclickAgent.onPageStart("login"); //登陆
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("login"); 
		MobclickAgent.onPause(this);
	}
}

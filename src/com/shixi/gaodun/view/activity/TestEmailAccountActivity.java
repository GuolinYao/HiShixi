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

/**
 * 验证邮箱号
 * 
 * @author ronger
 * @date:2015-10-23 上午11:03:30
 */
public class TestEmailAccountActivity extends TitleBaseActivity implements TextWatcher {
	// 若是用邮箱号登陆的
	private String mEmailAccount;
	private ClearEditTextView mEmailAccountEdit;
	private TextView mEmailAccountText;
	private Button mTestBtn;

	public static void startAction(Activity context, String account) {
		Intent intent = new Intent(context, TestEmailAccountActivity.class);
		intent.putExtra("account", account);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		mEmailAccount = getIntent().getStringExtra("account");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.test_emailaccount_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("验证邮箱");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mEmailAccountEdit = (ClearEditTextView) findViewById(R.id.cetv_testemail_account);
		mEmailAccountText = (TextView) findViewById(R.id.tv_testemail_account);
		mTestBtn = (Button) findViewById(R.id.btn_testemail);
		// 若登陆账号是邮箱账号
		if (StringUtils.isEmail(CacheUtils.getLoginAccount(this))) {
			mTestBtn.setEnabled(true);
			mEmailAccountText.setVisibility(View.VISIBLE);
			mEmailAccountEdit.setVisibility(View.GONE);
			mEmailAccountText.setText(CacheUtils.getLoginAccount(this));
			mEmailAccount = CacheUtils.getLoginAccount(this);
			return;
		}
		// 登陆账号为手机号
		mEmailAccountEdit.setVisibility(View.VISIBLE);
		mEmailAccountText.setVisibility(View.GONE);
		if (StringUtils.isNotEmpty(mEmailAccount)) {
			mEmailAccountEdit.setText(mEmailAccount);
			mTestBtn.setEnabled(true);
		}
		mEmailAccountEdit.addTextChangedListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
			return;
		}
		if (v.getId() == R.id.btn_testemail) {
			mEmailAccount = (mEmailAccountEdit.getVisibility() == View.VISIBLE) ? mEmailAccountEdit.getText()
					.toString() : mEmailAccountText.getText().toString();
			if (!StringUtils.isEmail(mEmailAccount)) {
				ToastUtils.showToastInCenter("邮箱格式有误");
				return;
			}
			myProgressDialog.setTitle("正在发送验证邮件...");
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("email", mEmailAccount);
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.TESTEMAIL_URL, map, new RequestResponseLinstner(this), this);
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
			sendEmailSuccess(httpRes.getReturnData());
		} catch (Exception e) {
			dissMissProgress();
			exceptionToast();
		}
	}

	public void sendEmailSuccess(String resultData) {
		// 谈toast,并且延迟跳转
		ToastUtils.showCustomToastInCenter("请查看邮件完成验证", this);
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
		String emailAccount = mEmailAccountEdit.getText().toString();
		if (StringUtils.isEmpty(emailAccount)) {
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
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}
}

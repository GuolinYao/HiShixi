package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
 * 意见反馈
 * 
 * @author ronger
 * @date:2015-10-22 下午4:33:18
 */
public class OpinionFeedbackActivity extends TitleBaseActivity {
	private Button mCommitBtn;
	private EditText mContent;
	private ClearEditTextView mPhone;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, OpinionFeedbackActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = false;
		setContentView(R.layout.opinion_feedback_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("意见反馈");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mCommitBtn = (Button) findViewById(R.id.btn_commit_feedback);
		mContent = (EditText) findViewById(R.id.et_opinion_content);
		mPhone = (ClearEditTextView) findViewById(R.id.cetv_opinion_phone);
		mCommitBtn.setEnabled(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back)
			finish();
		if (v.getId() == R.id.btn_commit_feedback) {
			if (StringUtils.isEmpty(mContent.getText().toString())) {
				ToastUtils.showToastInCenter("还是写点什么吧！");
				return;
			}
			myProgressDialog.setTitle("正在提交...");
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("email", mPhone.getText().toString());
		map.put("text", mContent.getText().toString());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.OPINIONFEEDBACK_URL, map, new RequestResponseLinstner(this), this);
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
			// 谈toast,并且延迟跳转
			ToastUtils.showCustomToastInCenter("提交成功", OpinionFeedbackActivity.this);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					delayedJump();
				}
			}, Finals.delayTime);
		} catch (Exception e) {
			setDebugLog(e);
			dissMissProgress();
			exceptionToast();
		}
	}

	@Override
	public void delayedJump() {
		super.delayedJump();
		finish();
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

}

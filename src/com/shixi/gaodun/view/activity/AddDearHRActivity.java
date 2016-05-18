package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.MaxLengthWatcher;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;

/**
 * HR
 * 
 * @author ronger
 * @date:2015-11-5 上午11:45:51
 */
public class AddDearHRActivity extends TitleBaseActivity {
	private String mDearHR;
	private Dialog confirmDialog = null;
	private EditText mContentEdit;
	private TextView mContentLengthText;
	private Button mDeleteBtn;
	private boolean ifDelete;

	public static void startAction(Activity context, int requestCode, String dearHR) {
		Intent intent = new Intent(context, AddDearHRActivity.class);
		intent.putExtra("dear_hr", dearHR);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		mDearHR = getIntent().getStringExtra("dear_hr");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.dear_hr_layout);
		initDataShow();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("添加Dear HR");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mContentEdit = (EditText) findViewById(R.id.et_intership_expericence_content);
		mDeleteBtn = (Button) findViewById(R.id.btn_delete);
		mContentLengthText = (TextView) findViewById(R.id.tv_expericence_content_length);
		mContentEdit.addTextChangedListener(new MaxLengthWatcher(300, mContentEdit, mContentLengthText));
	}

	public void initDataShow() {
		if (StringUtils.isEmpty(mDearHR)) {
			mDeleteBtn.setVisibility(View.GONE);
			return;
		}
		mDeleteBtn.setVisibility(View.VISIBLE);
		mContentEdit.setText(mDearHR);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.btn_delete:
			confirmDialog = CustomDialog.CancelAlertToDialog("确定要删除此项内容吗？", "删除", "取消", this, this);
			break;
		case R.id.tv_cancel_btn:
			confirmDialog.cancel();
			break;
		case R.id.tv_confrim_btn:
			ifDelete = true;
			setRequestParamsPre(TAG);
			break;
		case R.id.btn_addorupdate:
			ifDelete = false;
			if (StringUtils.isEmpty(mContentEdit.getText().toString())) {
				ToastUtils.showToastInCenter("内容不能为空!");
				break;
			}
			mDearHR = mContentEdit.getText().toString();
			setRequestParamsPre(TAG);
			break;
		default:
			break;
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (ifDelete)
			deleteHRToserver();
		else
			changeBasicInfo();
	}

	/**
	 * 更新HR
	 */
	public void changeBasicInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("dear_hr", mDearHR);
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CHANGEBAISICINFO_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void deleteHRToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETEDEAR_HR_URL, map, new RequestResponseLinstner(this), this);
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
			if (ifDelete)
				mDearHR = "";
			returnLastPage();
		} catch (Exception e) {
			dissMissProgress();
			exceptionToast();
			setDebugLog(e);
		}
	}

	public void returnLastPage() {
		Intent intent = new Intent();
		intent.putExtra("dear_hr", mDearHR);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void setRetryRequest() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);
	}
}

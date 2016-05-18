package com.shixi.gaodun.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ClearEditTextView;
import com.shixi.gaodun.view.CustomDialog;

/**
 * 基本信息设置包括: 姓名、身份证、就读学校、专业名称、联系电话、联系邮箱、
 * 
 * @author guolin
 * @date:2015-10-26 下午2:16:41
 */
public class BasicInfoSetActivity extends TitleBaseActivity implements TextWatcher {
	private int mRequestType;
	private String mRequestStr;
	private ClearEditTextView mBasicInfo;
	private Dialog mDialog;

	/**
	 * 跳转至该页面
	 * 
	 * @param context
	 * @param requestType请求的类型姓名0
	 *            、身份证1、就读学校2、专业名称3、联系电话4、联系邮箱5 
	 * @param requestCode请求时code
	 * @param requestStr请求时带过来的上次已经填写过的数据
	 */
	public static void startAction(Activity context, int requestType, int requestCode, String requestStr) {
		Intent intent = new Intent(context, BasicInfoSetActivity.class);
		intent.putExtra("requestType", requestType);
		intent.putExtra("requestStr", requestStr);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		Intent dataIntent = getIntent();
		mRequestStr = dataIntent.getStringExtra("requestStr");
		mRequestType = dataIntent.getIntExtra("requestType", 0);

	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.basicinfo_editext_layout);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void initView() {
		super.initView();
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setOnClickListener(this);
		mBasicInfo = (ClearEditTextView) findViewById(R.id.cetv_edit_basicinfo);
		setTitleShow();

		mOtherName.setEnabled(StringUtils.isEmpty(mRequestStr) ? false : true);
		mOtherName.setText("保存");
		mBasicInfo.setText(StringUtils.isEmpty(mRequestStr) ? "" : mRequestStr);
		mBasicInfo.addTextChangedListener(this);
	}

	public void setTitleShow() {
		switch (mRequestType) {
		case 0:
			mTitleName.setText("姓名");
			mBasicInfo.setInputType( EditorInfo.TYPE_CLASS_TEXT);
			break;
		case 1:
			mTitleName.setText("身份证");
			mBasicInfo.setInputType( EditorInfo.TYPE_CLASS_TEXT);
			break;
		case 2:
			mTitleName.setText("就读学校");
			mBasicInfo.setInputType( EditorInfo.TYPE_CLASS_TEXT);
			break;
		case 3:
			mTitleName.setText("专业名称");
			mBasicInfo.setInputType( EditorInfo.TYPE_CLASS_TEXT);
			break;
		case 4:
			mTitleName.setText("联系电话");
			mBasicInfo.setInputType( EditorInfo.TYPE_CLASS_PHONE);
			break;
		case 5:
			mTitleName.setText("联系邮箱");
			mBasicInfo.setInputType( EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			if (StringUtils.isEmpty(mBasicInfo.getText().toString())
					|| (null != mRequestStr && mRequestStr.equals(mBasicInfo.getText().toString()))) {
				finish();
				return;
			}
			mDialog = CustomDialog.CancelAlertToDialog("此项为必填项\n确认要放弃已写好的内容吗?", "确定", "取消", this, this);
			// CustomDialog.CancelAlertToTagDialog("此项为必填项", "确认要放弃已写好的内容吗?", "确定", "取消", this, this);
			return;
		}
		if (v.getId() == R.id.tv_confrim_btn) {
			finish();
			move = right;
		}
		if (v.getId() == R.id.tv_cancel_btn) {
			mDialog.dismiss();
		}
		if (v.getId() == R.id.tv_title_bar_otherbtn) {
			String result = mBasicInfo.getText().toString();
			if (saveData(result)) {
				Intent toReturn = new Intent();
				toReturn.putExtra("resultInfo", result);
				setResult(Activity.RESULT_OK, toReturn);
				finish();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (null != mDialog && mDialog.isShowing()) {
			mDialog.cancel();
		}
	}

	public boolean saveData(String result) {
		boolean ifGoon = true;
		switch (mRequestType) {
		case 0:
			if (result.length() > 15) {
				ToastUtils.showToastInCenter("不能超过15个字");
				ifGoon = false;
			}
			break;
		case 1:
			if (!StringUtils.isIdentityCard(result)) {
				ToastUtils.showToastInCenter("身份证号填写有误");
				ifGoon = false;
			}
			break;
		case 2:
			if (result.length() > 15) {
				ToastUtils.showToastInCenter("输入不能超过15个字");
				ifGoon = false;
			}
			break;
		case 3:
			if (result.length() > 15) {
				ToastUtils.showToastInCenter("输入内容不能超过15个字");
				ifGoon = false;
			}
			break;
		case 4:
			if (!StringUtils.isPhoneNum(result)) {
				ToastUtils.showToastInCenter("请填写11位手机号");
				ifGoon = false;
			}
			break;
		case 5:
			if (!StringUtils.isEmail(result)) {
				ToastUtils.showToastInCenter("邮箱格式有误");
				ifGoon = false;
			}
			break;
		default:
			break;
		}
		return ifGoon;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (StringUtils.isEmpty(mBasicInfo.getText().toString())) {
			mOtherName.setEnabled(false);
			return;
		}
		mOtherName.setEnabled(true);
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

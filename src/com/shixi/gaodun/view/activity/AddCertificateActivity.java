package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CertificateBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.DateWheelDialog;
import com.shixi.gaodun.view.DateWheelFrameLayout;
import com.shixi.gaodun.view.DateWheelFrameLayout.OnDateChangedListener;

/**
 * 证书
 * 
 * @author ronger
 * @date:2015-11-4 下午3:51:09
 */
public class AddCertificateActivity extends TitleBaseActivity {
	private ArrayList<CertificateBean> mCertificateLists;
	private CertificateBean mCertificateBean;
	private Button mDeleteBtn;
	private TextView mCertificateGetTimeText, mCertificateTypeText, mCertificateStateText;
	// mCertificateNameText;
	private EditText mCertificateNameEdit;
	private View mCertificateNameLayout;
	private int position = -1;
	private int[] mTimeArray;
	// 请求类型1添加2编辑3删除
	private int requestType = 1;
	private String mSplitChar = ".";
	private DateWheelDialog mDateWheelDialog;
	private Dialog confirmDialog = null;
	private boolean ifTypeOrName;
	private MapFormatBean mTypeBean;
	private MapFormatBean mNameBean;

	public static void startAction(Activity context, int requestCode, int position,
			ArrayList<CertificateBean> mCertificateLists) {
		Intent intent = new Intent(context, AddCertificateActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("mCertificateLists", mCertificateLists);
		context.startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		position = data.getIntExtra("position", -1);
		mCertificateLists = (ArrayList<CertificateBean>) data.getSerializableExtra("mCertificateLists");
		if (null == mCertificateLists)
			mCertificateLists = new ArrayList<CertificateBean>();
	}

	public void returnLastPage() {
		Intent intent = new Intent();
		intent.putExtra("mCertificateLists", mCertificateLists);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.add_certificate_layout);
		initDataShow();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("添加证书");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mCertificateGetTimeText = (TextView) findViewById(R.id.et_get_time_value);
		mCertificateTypeText = (TextView) findViewById(R.id.tv_certificate_type_value);
		mCertificateNameEdit = (EditText) findViewById(R.id.et_certificate_name_value);
		// mCertificateNameText = (TextView) findViewById(R.id.tv_certificate_name_value);
		mCertificateStateText = (TextView) findViewById(R.id.tv_certificate_state_value);
		mCertificateNameLayout = (View) findViewById(R.id.rl_certificate_name_layout);
		mDeleteBtn = (Button) findViewById(R.id.btn_delete);
	}

	public void initDataShow() {
		if (position != -1) {
			requestType = 2;
			mDeleteBtn.setVisibility(View.VISIBLE);
			mCertificateBean = mCertificateLists.get(position);
			mCertificateGetTimeText.setText(mCertificateBean.getFinish_time());
			mTypeBean = mCertificateBean.getCertificate_type();
			mCertificateTypeText.setText(mTypeBean.getValue());
			mNameBean = new MapFormatBean(mCertificateBean.getCertificate_id(), mCertificateBean.getCertificate_name());
			mCertificateNameEdit.setText(mNameBean.getValue());
			setCanClickAndEdit();
			// 1在读2通过
			mCertificateStateText.setText(mCertificateBean.getStatus().equals("1") ? "在读" : "通过");
			mTimeArray = StringUtils.getTime(mCertificateBean.getFinish_time(), mSplitChar);
		} else {
			mDeleteBtn.setVisibility(View.GONE);
			mCertificateBean = new CertificateBean();
			requestType = 1;
			mTimeArray = StringUtils.getTime(mSplitChar);
			setCanClickAndEdit();
		}
	}

	/*
	 * 新增情况下设置证书名称的显示
	 */
	public void setCanClickAndEdit() {
		if (null == mTypeBean) {
			mCertificateNameEdit.setEnabled(false);
			return;
		}
		if (mTypeBean.getValue().equals("自定义")) {
			mCertificateNameEdit.setEnabled(true);
			mCertificateNameLayout.setVisibility(View.GONE);
		} else {
			mCertificateNameEdit.setEnabled(false);
			mCertificateNameLayout.setVisibility(View.VISIBLE);
		}
	}

	// /**
	// * 存在分类数据数据情况下的证书名称的显示
	// */
	// public void setCanClickAndEditExitTypeData() {
	// if (null == mTypeBean)
	// return;
	// if (mTypeBean.getValue().equals("自定义")) {
	// mCertificateNameEdit.setVisibility(View.VISIBLE);
	// mCertificateNameEdit.setText(null != mNameBean && StringUtils.isNotEmpty(mNameBean.getValue()) ? mNameBean
	// .getValue() : "");
	// mCertificateNameText.setVisibility(View.GONE);
	// } else {
	// mCertificateNameEdit.setVisibility(View.GONE);
	// mCertificateNameText.setVisibility(View.VISIBLE);
	// mCertificateNameText.setText(null != mNameBean && StringUtils.isNotEmpty(mNameBean.getValue()) ? mNameBean
	// .getValue() : "");
	// }
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.rl_get_time_layout:
			StringUtils.getTime(mCertificateGetTimeText.getText().toString(), ".");
			mDateWheelDialog = new DateWheelDialog(this, mTimeArray[0], mTimeArray[1], mTimeArray[2]);
			mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayout);
			break;
		case R.id.rl_certificate_type_layout:
			ifTypeOrName = true;
			CertificateTypeListActivity.startAction(this, Finals.REQUESTCODE_ONE, mTypeBean, "证书分类", ifTypeOrName, "");
			break;
		case R.id.rl_certificate_name_layout:
			ifTypeOrName = false;
			if (null == mTypeBean || StringUtils.isEmpty(mTypeBean.getKey())) {
				ToastUtils.showToastInCenter("请先选择证书分类");
				break;
			}
			if (mTypeBean.getValue().equals("自定义")) {
				break;
			}
			CertificateTypeListActivity.startAction(this, Finals.REQUESTCODE_TWO, mNameBean, "证书名称", ifTypeOrName,
					mTypeBean.getKey());
			break;
		// case R.id.et_certificate_name_value:
		// ifTypeOrName = false;
		// if (null == mTypeBean || StringUtils.isEmpty(mTypeBean.getKey())) {
		// ToastUtils.showToastInCenter("请先选择证书分类");
		// break;
		// }
		// if (mTypeBean.getValue().equals("自定义")) {
		// break;
		// }
		// CertificateTypeListActivity.startAction(this, Finals.REQUESTCODE_TWO, mNameBean, "证书名称", ifTypeOrName,
		// mTypeBean.getKey());
		// break;
		case R.id.rl_certificate_state_layout:
			CertificateTypeListActivity.startAction(this, Finals.REQUESTCODE_THREE, mCertificateStateText.getText()
					.toString(), "状态", true);
			break;
		case R.id.btn_delete:
			requestType = 3;
			confirmDialog = CustomDialog.CancelAlertToDialog("确定要删除此项内容吗？", "删除", "取消", this, this);
			break;
		case R.id.tv_cancel_btn:
			confirmDialog.cancel();
			break;
		case R.id.tv_confrim_btn:
			setRequestParamsPre(TAG);
			break;
		case R.id.btn_addorupdate:
			if (position == -1) {
				requestType = 1;
			} else {
				requestType = 2;
			}
			checkIfCanCommit();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 != Activity.RESULT_OK) {
			return;
		}
		if (arg0 == Finals.REQUESTCODE_THREE) {
			String status = arg2.getStringExtra("mStatus");
			mCertificateStateText.setText(StringUtils.isNotEmpty(status) ? status : "");
		}
		MapFormatBean bean = (MapFormatBean) arg2.getSerializableExtra("mapbean");
		if (null == bean) {
			return;
		}
		if (arg0 == Finals.REQUESTCODE_ONE) {// 证书分类返回
			mTypeBean = bean;
			String oldType = mCertificateTypeText.getText().toString();
			mCertificateTypeText.setText(mTypeBean.getValue());
			if (oldType.equals(mTypeBean.getValue())) {
				return;
			}
			// 清空上次的证书名称
			mNameBean = null;
			mCertificateNameEdit.setText("");
			setCanClickAndEdit();
		}
		if (arg0 == Finals.REQUESTCODE_TWO) {// 证书名称返回
			mNameBean = bean;
			mCertificateNameEdit.setText(mNameBean.getValue());
		}

	}

	/**
	 * 检测是否可提交
	 */
	public void checkIfCanCommit() {
		if (StringUtils.isEmpty(mCertificateGetTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择获得时间");
			return;
		}
		if (StringUtils.isEmpty(mCertificateTypeText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择证书分类");
			return;
		}
		if (StringUtils.isEmpty(mCertificateNameEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请选择证书名称");
			return;
		}
		if (StringUtils.isEmpty(mCertificateStateText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择证书状态");
			return;
		}
		saveCertificateData();
	}

	/**
	 * 保存证书信息
	 */
	public void saveCertificateData() {
		mCertificateBean.setFinish_time(mCertificateGetTimeText.getText().toString());
		mCertificateBean.setCertificate_type(mTypeBean);
		// 自定义的证书名称
		if (null == mNameBean || mTypeBean.getValue().equals("自定义")) {
			mCertificateBean.setCertificate_name(mCertificateNameEdit.getText().toString());
		} else {
			mCertificateBean.setCertificate_name(mNameBean.getValue());
			mCertificateBean.setCertificate_id(mNameBean.getKey());
		}
		String status = mCertificateStateText.getText().toString();
		if (status.equals("在读")) {
			status = "1";
		}
		if (status.equals("通过")) {
			status = "2";
		}
		mCertificateBean.setStatus(status);
		setRequestParamsPre(TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (requestType == 1) {
			addCertificateToServer();
		}
		if (requestType == 2) {
			changeCertificateToServer();
		}
		if (requestType == 3) {
			deleteCertificateToServer();
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
			if (requestType == 1) {
				mCertificateBean.setPkid(httpRes.getReturnData());
				if (null == mCertificateLists) {
					mCertificateLists = new ArrayList<CertificateBean>();
				}
				mCertificateLists.add(mCertificateBean);
				returnLastPage();
				return;
			}
			if (requestType == 2) {
				returnLastPage();
				return;
			}
			if (requestType == 3) {
				deleteCertificate();
			}
		} catch (Exception e) {
			dissMissProgress();
			exceptionToast();
			setDebugLog(e);
		}
	}

	/**
	 * 删除某一个证书
	 */
	public void deleteCertificate() {
		mCertificateLists.remove(position);
		returnLastPage();
	}

	/**
	 * 新增证书
	 */
	public void addCertificateToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("status", mCertificateBean.getStatus());
		// 证书分类名
		MapFormatBean typeBean = mCertificateBean.getCertificate_type();
		map.put("name", typeBean.getKey());
		map.put("certificate_name", mCertificateBean.getCertificate_name());
		String start = mCertificateBean.getFinish_time().replace(".", "-");
		map.put("finish_time", start);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.ADDCERTIFICATE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 修改证书提交至服务器
	 */
	public void changeCertificateToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mCertificateBean.getPkid());
		map.put("status", mCertificateBean.getStatus());
		// 证书分类名
		MapFormatBean typeBean = mCertificateBean.getCertificate_type();
		map.put("name", typeBean.getKey());
		map.put("certificate_name", mCertificateBean.getCertificate_name());
		String start = mCertificateBean.getFinish_time().replace(".", "-");
		map.put("finish_time", start);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.EDITCERTIFICATE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 删除证书并提交至服务器
	 */
	public void deleteCertificateToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mCertificateBean.getPkid());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETECERTIFICATE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 设置选择日期后的回调
	 * 
	 * @param mDateWheelFrameLayout
	 */
	public void setDateCallBack(DateWheelFrameLayout mDateWheelFrameLayout) {
		mDateWheelFrameLayout.setOnDateChangedListener(new OnDateChangedListener() {
			@Override
			public void onDateChanged(String year, String month, String day) {
				String startTime = year + mSplitChar + month + mSplitChar + day;
				mCertificateGetTimeText.setText(startTime);
				mDateWheelDialog.cancel();
			}

			@Override
			public void onCancel() {
				mDateWheelDialog.cancel();
			}
		});
	}

	@Override
	protected void setRetryRequest() {
		setNolmalShow();
		setRequestParamsPre(TAG);
	}
}

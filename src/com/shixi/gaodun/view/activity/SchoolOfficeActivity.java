package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
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
import com.shixi.gaodun.model.domain.EducationBGBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DateUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.DateWheelDialog;
import com.shixi.gaodun.view.DateWheelFrameLayout;
import com.shixi.gaodun.view.DateWheelFrameLayout.OnDateChangedListener;

/**
 * 校内职务
 * 
 * @author ronger
 * @date:2015-11-5 上午10:14:55
 */
public class SchoolOfficeActivity extends TitleBaseActivity {
	private ArrayList<EducationBGBean> mSchoolOfficeLists;
	private EducationBGBean mSchoolOffice;
	private String mSplitChar = ".";
	private int position = -1, requestType = 1;
	private int[] mStartArray, mEndArray;
	// true开始false结束
	private boolean startOrEndTime;
	private DateWheelDialog mDateWheelDialog;
	private Dialog confirmDialog = null;
	private EditText mSchoolJobTitleEdit, mContentEdit;
	private TextView mStartTimeText, mEndTimeText, mContentLengthText;
	private Button mDeleteBtn;

	public static void startAction(Activity context, int requestCode, int position,
			ArrayList<EducationBGBean> mSchoolOfficeLists) {
		Intent intent = new Intent(context, SchoolOfficeActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("mSchoolOfficeLists", mSchoolOfficeLists);
		context.startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		position = data.getIntExtra("position", -1);
		mSchoolOfficeLists = (ArrayList<EducationBGBean>) data.getSerializableExtra("mSchoolOfficeLists");
		if (null == mSchoolOfficeLists) {
			mSchoolOfficeLists = new ArrayList<EducationBGBean>();
		}
	}

	public void returnLastPage() {
		Intent intent = new Intent();
		intent.putExtra("mSchoolOfficeLists", mSchoolOfficeLists);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = false;
		getIntentData();
		setContentView(R.layout.school_office_layout);
		initDataShow();
		mSchoolJobTitleEdit.requestFocus();//职务名称获取焦点
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("添加校内职务");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mStartTimeText = (TextView) findViewById(R.id.tv_join_intership_time_value);
		mEndTimeText = (TextView) findViewById(R.id.tv_end_intership_time_value);
		mSchoolJobTitleEdit = (EditText) findViewById(R.id.et_school_job_title_value);
		mContentEdit = (EditText) findViewById(R.id.et_intership_expericence_content);
		mDeleteBtn = (Button) findViewById(R.id.btn_delete);
		mStartTimeText = (TextView) findViewById(R.id.tv_school_start_time_value);
		mEndTimeText = (TextView) findViewById(R.id.tv_school_end_time_value);
		mContentLengthText = (TextView) findViewById(R.id.tv_expericence_content_length);
		mContentLengthText.setText("0/300");
		mContentEdit.addTextChangedListener(new MaxLengthWatcher(300, mContentEdit, mContentLengthText));
	}

	public void initDataShow() {
		if (position != -1) {
			requestType = 2;
			mDeleteBtn.setVisibility(View.VISIBLE);
			mSchoolOffice = mSchoolOfficeLists.get(position);
			mSchoolJobTitleEdit.setText(mSchoolOffice.getJob_title());
			mStartTimeText.setText(mSchoolOffice.getStart_time());
			mEndTimeText.setText(mSchoolOffice.getFinish_time());
			mContentEdit.setText(mSchoolOffice.getDescription());
			setTime(mSchoolOffice.getStart_time(), mSchoolOffice.getFinish_time());
		} else {
			mDeleteBtn.setVisibility(View.GONE);
			mSchoolOffice = new EducationBGBean();
			requestType = 1;
			setTime("", "");
		}
	}

	public void setTime(String startTime, String endTime) {
		mStartArray = StringUtils.getTime(startTime, mSplitChar);
		mEndArray = StringUtils.getTime(endTime, mSplitChar);
	}

	public void setStartTime(String startTime) {
		mStartArray = StringUtils.getTime(startTime, mSplitChar);
	}

	public void setEndTime(String endTime) {
		mEndArray = StringUtils.getTime(endTime, mSplitChar);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.rl_school_start_time_layout:
			startOrEndTime = true;
			setStartTime(mStartTimeText.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mStartArray[0], mStartArray[1], mStartArray[2]);
			mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayout);
			break;
		case R.id.rl_school_end_time_layout:
			startOrEndTime = false;
			setEndTime(mEndTimeText.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mEndArray[0], mEndArray[1], mEndArray[2]);
			mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayoutGraduate = mDateWheelDialog.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayoutGraduate);
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

	/**
	 * 检测是否可提交
	 */
	public void checkIfCanCommit() {
		if (StringUtils.isEmpty(mSchoolJobTitleEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请填写职务名称");
			return;
		}
		if (StringUtils.isEmpty(mContentEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请填写职务经历");
			return;
		}
		if (StringUtils.isEmpty(mStartTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择开始时间");
			return;
		}
		if (StringUtils.isEmpty(mEndTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择结束时间");
			return;
		}
		if (DateUtils.DateCompare(mStartTimeText.getText().toString(), mEndTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("开始时间必须小于结束时间");
			return;
		}
		saveSchoolOfficeData();
	}

	/**
	 * 保存职务经历
	 */
	public void saveSchoolOfficeData() {
		mSchoolOffice.setJob_title(mSchoolJobTitleEdit.getText().toString());
		mSchoolOffice.setDescription(mContentEdit.getText().toString());
		mSchoolOffice.setStart_time(mStartTimeText.getText().toString());
		mSchoolOffice.setFinish_time(mEndTimeText.getText().toString());
		setRequestParamsPre(TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (requestType == 1) {
			addSchoolOfficeToserver();
		}
		if (requestType == 2) {
			changeSchoolOfficeToserver();
		}
		if (requestType == 3) {
			deleteSchoolOfficeToserver();
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			isFirstJoin = false;
			dissMissProgress();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			// 若是增加需要先获取到返回的id,复制给mInterShipExperience的id
			if (requestType == 1) {
				mSchoolOffice.setPkid(httpRes.getReturnData());
				if (null == mSchoolOfficeLists) {
					mSchoolOfficeLists = new ArrayList<EducationBGBean>();
				}
				mSchoolOfficeLists.add(mSchoolOffice);
				returnLastPage();
				return;
			}
			if (requestType == 2) {
				returnLastPage();
				return;
			}
			if (requestType == 3) {
				deleteExperice();
			}
		} catch (Exception e) {
			setDebugLog(e);
			dissMissProgress();
			exceptionToast();
		}
	}

	/**
	 * 删除某一个校内职务经历
	 */
	public void deleteExperice() {
		mSchoolOfficeLists.remove(position);
		returnLastPage();
	}

	public void addSchoolOfficeToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("job_title", mSchoolOffice.getJob_title());
		map.put("description", mSchoolOffice.getDescription());
		String start = mSchoolOffice.getStart_time().replace(".", "-");
		map.put("start_time", start);
		String end = mSchoolOffice.getFinish_time().replace(".", "-");
		map.put("finish_time", end);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.ADDSCHOOLOFFICE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void changeSchoolOfficeToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mSchoolOffice.getPkid());
		map.put("job_title", mSchoolOffice.getJob_title());
		map.put("description", mSchoolOffice.getDescription());
		String start = mSchoolOffice.getStart_time().replace(".", "-");
		map.put("start_time", start);
		String end = mSchoolOffice.getFinish_time().replace(".", "-");
		map.put("finish_time", end);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.EDITSCHOOLOFFICE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void deleteSchoolOfficeToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mSchoolOffice.getPkid());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETESCHOOLOFFICE_URL, map, new RequestResponseLinstner(this), this);
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
				if (startOrEndTime) {
					String startTime = year + mSplitChar + month + mSplitChar + day;
					mStartTimeText.setText(startTime);
				} else {
					String endTime = year + mSplitChar + month + mSplitChar + day;
					mEndTimeText.setText(endTime);
				}
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
	}
}

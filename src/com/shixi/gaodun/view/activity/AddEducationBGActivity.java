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
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.EducationBGBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.Finals;
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
 * 添加教育背景
 * 
 * @author ronger
 * @date:2015-11-3 下午1:31:53
 */
public class AddEducationBGActivity extends TitleBaseActivity {
	private EditText mSchoolNameEdit, mMajorNameEdit;
	private TextView mEducationBgText, mJoinTimeText, mGraduateText;
	private Button mDeleteBtn;
	private ArrayList<EducationBGBean> mEducationLists;
	private int position = -1;
	private int[] mStartArray, mEndArray;
	private String mSplitChar = ".";
	// true开始false结束
	private boolean startOrEndTime;
	private DateWheelDialog mDateWheelDialog;
	private Dialog confirmDialog = null;
	private EducationBGBean mCurrentEducationbg;
	// 请求类型1添加2编辑3删除
	private int requestType = 1;

	public static void startAction(Activity context, int requestCode,
			int position, ArrayList<EducationBGBean> mEducationLists) {
		Intent intent = new Intent(context, AddEducationBGActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("mEducationLists", mEducationLists);
		context.startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		position = data.getIntExtra("position", -1);
		mEducationLists = (ArrayList<EducationBGBean>) data
				.getSerializableExtra("mEducationLists");
		if (null == mEducationLists) {
			mEducationLists = new ArrayList<EducationBGBean>();
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.add_educationbg_layout);
		initDataShow();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("添加教育背景");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mSchoolNameEdit = (EditText) findViewById(R.id.et_school_name_value);
		mMajorNameEdit = (EditText) findViewById(R.id.et_major_name_value);
		mEducationBgText = (TextView) findViewById(R.id.tv_education_value);
		mJoinTimeText = (TextView) findViewById(R.id.tv_join_school_time_value);
		mGraduateText = (TextView) findViewById(R.id.tv_graduate_time_value);
		mDeleteBtn = (Button) findViewById(R.id.btn_delete);
	}

	/**
	 * 根据是添加还是编辑来显示数据
	 */
	public void initDataShow() {
		if (position != -1) {// 编辑
			requestType = 2;
			mDeleteBtn.setVisibility(View.VISIBLE);
			mCurrentEducationbg = mEducationLists.get(position);
			mSchoolNameEdit.setText(mCurrentEducationbg.getSchool_name());
			mMajorNameEdit.setText(mCurrentEducationbg.getMajor_title());
			mEducationBgText.setText(mCurrentEducationbg.getDegreeName());
			mJoinTimeText.setText(mCurrentEducationbg.getStart_time());
			mGraduateText.setText(mCurrentEducationbg.getFinish_time());
			setTime(mCurrentEducationbg.getStart_time(),
					mCurrentEducationbg.getFinish_time());
		} else {// 添加
			mDeleteBtn.setVisibility(View.GONE);
			mCurrentEducationbg = new EducationBGBean();
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
		case R.id.rl_education_layout:
			EducationListActivity.startAction(this,
					Finals.TOSELECTHIGHEST_EDUCATION_REQUEST, mEducationBgText
							.getText().toString());
			break;
		case R.id.rl_join_school_time_layout:
			startOrEndTime = true;
			setStartTime(mJoinTimeText.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mStartArray[0],
					mStartArray[1], mStartArray[2]);
			mDateWheelDialog.show();
			// mDateWheelDialog = new DateWheelDialog(this, mEndArray[0],
			// mEndArray[1], mEndArray[2]);
			// mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog
					.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayout);
			break;
		case R.id.rl_graduate_time_layout:
			startOrEndTime = false;
			setEndTime(mGraduateText.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mEndArray[0],
					mEndArray[1], mEndArray[2]);
			mDateWheelDialog.show();
			// mDateWheelDialog = new DateWheelDialog(this, mStartArray[0],
			// mStartArray[1], mStartArray[2]);
			// mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayoutGraduate = mDateWheelDialog
					.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayoutGraduate);
			break;
		case R.id.btn_delete:
			requestType = 3;
			confirmDialog = CustomDialog.CancelAlertToDialog("确定要删除此项内容吗？",
					"删除", "取消", this, this);
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
	 * 删除某一个教育背景
	 */
	public void deleteEducationBG() {
		mEducationLists.remove(position);
		returnLastPage();
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (requestType == 1) {
			addEducationBGToServer();
		}
		if (requestType == 2) {
			changeEducationBGToServer();
		}
		if (requestType == 3) {
			deleteEducationBGToServer();
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
				mCurrentEducationbg.setPkid(httpRes.getReturnData());
				if (null == mEducationLists) {
					mEducationLists = new ArrayList<EducationBGBean>();
				}
				mEducationLists.add(mCurrentEducationbg);
				returnLastPage();
				return;
			}
			if (requestType == 2) {
				returnLastPage();
				return;
			}
			if (requestType == 3) {
				deleteEducationBG();
			}
		} catch (Exception e) {
			dissMissProgress();
			exceptionToast();
			setDebugLog(e);
		}
	}

	/**
	 * 新增教育背景至服务器
	 */
	public void addEducationBGToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("school_name", mCurrentEducationbg.getSchool_name());
		map.put("major_title", mCurrentEducationbg.getMajor_title());
		map.put("degree", mCurrentEducationbg.getDegreeKey());
		String start = mCurrentEducationbg.getStart_time().replace(".", "-");
		map.put("start_time", start);
		String end = mCurrentEducationbg.getFinish_time().replace(".", "-");
		map.put("finish_time", end);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.ADDEDUCATIONBG_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 修改教育背景提交至服务器
	 */
	public void changeEducationBGToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("id", mCurrentEducationbg.getPkid());
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("school_name", mCurrentEducationbg.getSchool_name());
		map.put("major_title", mCurrentEducationbg.getMajor_title());
		map.put("degree", mCurrentEducationbg.getDegreeKey());
		map.put("start_time", mCurrentEducationbg.getStart_time());
		map.put("finish_time", mCurrentEducationbg.getFinish_time());
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.UPDATEEDUCATIONBG_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 删除教育背景并提交至服务器
	 */
	public void deleteEducationBGToServer() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mCurrentEducationbg.getPkid());
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.DELETEEDUCATIONBG_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 检测是否可提交
	 */
	public void checkIfCanCommit() {
		if (StringUtils.isEmpty(mSchoolNameEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请填写就读学校");
			return;
		}
		if (StringUtils.isEmpty(mMajorNameEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请填写专业名称");
			return;
		}
		if (StringUtils.isEmpty(mEducationBgText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择学历");
			return;
		}
		if (StringUtils.isEmpty(mJoinTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择入学时间");
			return;
		}
		if (StringUtils.isEmpty(mGraduateText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择毕业时间");
			return;
		}
		if (DateUtils.DateCompare(mJoinTimeText.getText().toString(),
				mGraduateText.getText().toString())) {
			ToastUtils.showToastInCenter("毕业时间必须小于入学时间");
			return;
		}
		saveEducationData();
	}

	/**
	 * 保存教育背景
	 */
	public void saveEducationData() {
		mCurrentEducationbg
				.setSchool_name(mSchoolNameEdit.getText().toString());
		mCurrentEducationbg.setMajor_title(mMajorNameEdit.getText().toString());
		mCurrentEducationbg.setStart_time(mJoinTimeText.getText().toString());
		mCurrentEducationbg.setFinish_time(mGraduateText.getText().toString());
		// 1:专科,2:本科,3:硕士,4:博士
		String educationBG = mEducationBgText.getText().toString();
		mCurrentEducationbg.setDegreeName(educationBG);
		if (educationBG.equals("专科")) {
			mCurrentEducationbg.setDegreeKey("1");
		}
		if (educationBG.equals("本科")) {
			mCurrentEducationbg.setDegreeKey("2");
		}
		if (educationBG.equals("硕士")) {
			mCurrentEducationbg.setDegreeKey("3");
		}
		if (educationBG.equals("博士")) {
			mCurrentEducationbg.setDegreeKey("4");
		}

		setRequestParamsPre(TAG);
		// returnLastPage();
	}

	public void returnLastPage() {
		Intent intent = new Intent();
		intent.putExtra("mEducationLists", mEducationLists);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == Finals.TOSELECTHIGHEST_EDUCATION_REQUEST) {
			String selectStr = arg2.getStringExtra("selectStr");
			if (StringUtils.isNotEmpty(selectStr)) {
				mEducationBgText.setText(selectStr);
			}
		}
	}

	/**
	 * 设置选择日期后的回调
	 * 
	 * @param mDateWheelFrameLayout
	 */
	public void setDateCallBack(DateWheelFrameLayout mDateWheelFrameLayout) {
		mDateWheelFrameLayout
				.setOnDateChangedListener(new OnDateChangedListener() {

					@Override
					public void onDateChanged(String year, String month,
							String day) {
						if (startOrEndTime) {
							String startTime = year + mSplitChar + month
									+ mSplitChar + day;

							mJoinTimeText.setText(startTime);
						} else {
							String endTime = year + mSplitChar + month
									+ mSplitChar + day;

							mGraduateText.setText(endTime);
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
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);
	}
}

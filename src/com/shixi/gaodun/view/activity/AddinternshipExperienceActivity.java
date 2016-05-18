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
 * 添加编辑实习经历
 * 
 * @author ronger
 * @date:2015-11-3 下午5:39:07
 */
public class AddinternshipExperienceActivity extends TitleBaseActivity {
	private TextView mStartTimeText, mEndTimeText, mContentLengthText;
	private EditText mCompanyNameEdit, mJobTitleEdit, mIntershipContentEdit;
	private Button mDeleteBtn;
	private ArrayList<EducationBGBean> mInterShipExperienceLists;
	private int position = -1;
	private int[] mStartArray, mEndArray;
	private String mSplitChar = ".";
	// true开始false结束
	private boolean startOrEndTime;
	private DateWheelDialog mDateWheelDialog;
	private Dialog confirmDialog = null;
	private EducationBGBean mInterShipExperience;
	// 请求类型1添加2编辑3删除
	private int requestType = 1;

	public static void startAction(Activity context, int requestCode,
			int position, ArrayList<EducationBGBean> mInternshipExperienceLists) {
		Intent intent = new Intent(context,
				AddinternshipExperienceActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("mInternshipExperienceLists",
				mInternshipExperienceLists);
		context.startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		position = data.getIntExtra("position", -1);
		mInterShipExperienceLists = (ArrayList<EducationBGBean>) data
				.getSerializableExtra("mInternshipExperienceLists");
		if (null == mInterShipExperienceLists) {
			mInterShipExperienceLists = new ArrayList<EducationBGBean>();
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.add_intership_expericence_layout);
		initDataShow();
		mCompanyNameEdit.requestFocus();//是企业名称获取焦点
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("添加实习经历");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mStartTimeText = (TextView) findViewById(R.id.tv_join_intership_time_value);
		mEndTimeText = (TextView) findViewById(R.id.tv_end_intership_time_value);
		mCompanyNameEdit = (EditText) findViewById(R.id.et_company_name_value);
		mJobTitleEdit = (EditText) findViewById(R.id.et_job_name_value);
		mIntershipContentEdit = (EditText) findViewById(R.id.et_intership_expericence_content);
		mContentLengthText = (TextView) findViewById(R.id.tv_expericence_content_length);
		mContentLengthText.setText("0/300");
		mDeleteBtn = (Button) findViewById(R.id.btn_delete);
		mIntershipContentEdit.addTextChangedListener(new MaxLengthWatcher(300,
				mIntershipContentEdit, mContentLengthText));
	}

	public void initDataShow() {
		if (position != -1) {
			requestType = 2;
			mDeleteBtn.setVisibility(View.VISIBLE);
			mInterShipExperience = mInterShipExperienceLists.get(position);
			mCompanyNameEdit.setText(mInterShipExperience.getOrganization());
			mJobTitleEdit.setText(mInterShipExperience.getQuarters());
			mStartTimeText.setText(mInterShipExperience.getStart_time());
			mEndTimeText.setText(mInterShipExperience.getFinish_time());
			mIntershipContentEdit.setText(mInterShipExperience.getContent());
			setTime(mInterShipExperience.getStart_time(),
					mInterShipExperience.getFinish_time());
		} else {
			mDeleteBtn.setVisibility(View.GONE);
			mInterShipExperience = new EducationBGBean();
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
		case R.id.rl_join_intership_time_layout:
			startOrEndTime = true;
			setStartTime(mStartTimeText.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mStartArray[0],
					mStartArray[1], mStartArray[2]);
			mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog
					.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayout);
			break;
		case R.id.rl_end_intership_time_layout:
			startOrEndTime = false;
			setEndTime(mEndTimeText.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mEndArray[0],
					mEndArray[1], mEndArray[2]);
			mDateWheelDialog.show();
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
							mStartTimeText.setText(startTime);
						} else {
							String endTime = year + mSplitChar + month
									+ mSplitChar + day;
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
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (requestType == 1) {
			addExperienceToserver();
		}
		if (requestType == 2) {
			changeExperienceToserver();
		}
		if (requestType == 3) {
			deleteExperienceToserver();
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
			// 若是增加需要先获取到返回的id,复制给mInterShipExperience的id
			if (requestType == 1) {
				mInterShipExperience.setPkid(httpRes.getReturnData());
				if (null == mInterShipExperienceLists) {
					mInterShipExperienceLists = new ArrayList<EducationBGBean>();
				}
				mInterShipExperienceLists.add(mInterShipExperience);
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
			dissMissProgress();
			exceptionToast();
			setDebugLog(e);
		}
	}

	/**
	 * 删除某一个教育背景
	 */
	public void deleteExperice() {
		mInterShipExperienceLists.remove(position);
		returnLastPage();
	}

	public void addExperienceToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("organization", mInterShipExperience.getOrganization());
		map.put("quarters", mInterShipExperience.getQuarters());
		map.put("content", mInterShipExperience.getContent());
		String start = mInterShipExperience.getStart_time().replace(".", "-");
		map.put("start_time", start);
		String end = mInterShipExperience.getFinish_time().replace(".", "-");
		map.put("finish_time", end);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.ADDINTERSHIPEXPERIENCE_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void changeExperienceToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mInterShipExperience.getPkid());
		map.put("organization", mInterShipExperience.getOrganization());
		map.put("quarters", mInterShipExperience.getQuarters());
		map.put("content", mInterShipExperience.getContent());
		String start = mInterShipExperience.getStart_time().replace(".", "-");
		map.put("start_time", start);
		String end = mInterShipExperience.getFinish_time().replace(".", "-");
		map.put("finish_time", end);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.EDITINTERSHIPEXPERIENCE_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void deleteExperienceToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mInterShipExperience.getPkid());
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.DELETETINTERSHIPEXPERIENCE_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 检测是否可提交
	 */
	public void checkIfCanCommit() {
		if (StringUtils.isEmpty(mCompanyNameEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请填写企业名称");
			return;
		}
		if (StringUtils.isEmpty(mJobTitleEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请填写岗位名称");
			return;
		}
		if (StringUtils.isEmpty(mIntershipContentEdit.getText().toString())) {
			ToastUtils.showToastInCenter("请填写实习经历");
			return;
		}
		if (StringUtils.isEmpty(mStartTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择实习开始时间");
			return;
		}
		if (StringUtils.isEmpty(mEndTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择实习结束时间");
			return;
		}
		if (DateUtils.DateCompare(mStartTimeText.getText().toString(),
				mEndTimeText.getText().toString())) {
			ToastUtils.showToastInCenter("开始时间必须小于结束时间");
			return;
		}
		saveExperienceData();
	}

	/**
	 * 保存实习经历
	 */
	public void saveExperienceData() {
		mInterShipExperience.setOrganization(mCompanyNameEdit.getText()
				.toString());
		mInterShipExperience.setQuarters(mJobTitleEdit.getText().toString());
		mInterShipExperience.setContent(mIntershipContentEdit.getText()
				.toString());
		mInterShipExperience.setStart_time(mStartTimeText.getText().toString());
		mInterShipExperience.setFinish_time(mEndTimeText.getText().toString());
		setRequestParamsPre(TAG);
	}

	public void returnLastPage() {
		Intent intent = new Intent();
		intent.putExtra("mInternshipExperienceLists", mInterShipExperienceLists);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void setRetryRequest() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);
	}

}

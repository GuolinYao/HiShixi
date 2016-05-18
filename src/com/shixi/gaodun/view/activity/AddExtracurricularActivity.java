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
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.DateWheelDialog;
import com.shixi.gaodun.view.DateWheelFrameLayout;
import com.shixi.gaodun.view.DateWheelFrameLayout.OnDateChangedListener;

/**
 * 添加课外活动
 * 
 * @author ronger
 * @date:2015-11-4 下午1:44:30
 */
public class AddExtracurricularActivity extends TitleBaseActivity {
	private Button mDeleteBtn;
	private TextView mTime;
	private TextView mContentLength;
	private EditText mContent;
	private Dialog confirmDialog = null;
	// 请求类型1添加2编辑3删除
	private int requestType = 1;
	private int position = -1;
	private int[] mStartArray;
	private String mSplitChar = ".";
	private ArrayList<EducationBGBean> mExtracurricularLists;
	private EducationBGBean mExtracurricular;
	private DateWheelDialog mDateWheelDialog;

	public static void startAction(Activity context, int requestCode, int position,
			ArrayList<EducationBGBean> mExtracurricularLists) {
		Intent intent = new Intent(context, AddExtracurricularActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("mExtracurricularLists", mExtracurricularLists);
		context.startActivityForResult(intent, requestCode);
	}

	public void returnLastPage() {
		Intent intent = new Intent();
		intent.putExtra("mExtracurricularLists", mExtracurricularLists);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		position = data.getIntExtra("position", -1);
		mExtracurricularLists = (ArrayList<EducationBGBean>) data.getSerializableExtra("mExtracurricularLists");
		if (null == mExtracurricularLists)
			mExtracurricularLists = new ArrayList<EducationBGBean>();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.add_extracurricular_layout);
		initDataShow();
//		getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
//						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("添加课外活动");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mDeleteBtn = (Button) findViewById(R.id.btn_delete);
		mTime = (TextView) findViewById(R.id.tv_end_intership_time_value);
		mContentLength = (TextView) findViewById(R.id.tv_activity_content_length);
		mContentLength.setText("0/32");
		mContent = (EditText) findViewById(R.id.et_activity_content_content);
		mContent.addTextChangedListener(new MaxLengthWatcher(32, mContent, mContentLength));
	}

	public void initDataShow() {
		if (position != -1) {
			requestType = 2;
			mDeleteBtn.setVisibility(View.VISIBLE);
			mExtracurricular = mExtracurricularLists.get(position);
			mContent.setText(mExtracurricular.getDescription());
			mTime.setText(mExtracurricular.getPeriod());
			setTime(mExtracurricular.getPeriod());
		} else {
			mDeleteBtn.setVisibility(View.GONE);
			mExtracurricular = new EducationBGBean();
			requestType = 1;
			mStartArray = StringUtils.getTime("", mSplitChar);
		}
	}

	public void setTime(String startTime) {
		mStartArray = StringUtils.getTime(startTime, mSplitChar);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.rl_join_activity_time_layout:
			setTime(mTime.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mStartArray[0], mStartArray[1], mStartArray[2]);
			mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayout);
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
		if (StringUtils.isEmpty(mTime.getText().toString())) {
			ToastUtils.showToastInCenter("请选择活动时间");
			return;
		}
		if (StringUtils.isEmpty(mContent.getText().toString())) {
			ToastUtils.showToastInCenter("请填写活动内容");
			return;
		}
		saveExtracurricularData();
	}

	/**
	 * 保存活动数据
	 */
	public void saveExtracurricularData() {
		mExtracurricular.setPeriod(mTime.getText().toString());
		mExtracurricular.setDescription(mContent.getText().toString());
		setRequestParamsPre(TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (requestType == 1) {
			addExtracurricularToserver();
		}
		if (requestType == 2) {
			changeExtracurricularToserver();
		}
		if (requestType == 3) {
			deleteExtracurricularToserver();
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
				mExtracurricular.setPkid(httpRes.getReturnData());
				if (null == mExtracurricularLists) {
					mExtracurricularLists = new ArrayList<EducationBGBean>();
				}
				mExtracurricularLists.add(mExtracurricular);
				returnLastPage();
				return;
			}
			if (requestType == 2) {
				returnLastPage();
				return;
			}
			if (requestType == 3) {
				deleteExtracurricular();
			}
		} catch (Exception e) {
			dissMissProgress();
			exceptionToast();
			setDebugLog(e);
		}
	}

	/**
	 * 删除某一个活动
	 */
	public void deleteExtracurricular() {
		mExtracurricularLists.remove(position);
		returnLastPage();
	}

	public void addExtracurricularToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("description", mExtracurricular.getDescription());
		String start = mExtracurricular.getPeriod().replace(".", "-");
		map.put("period", start);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.ADDEXTRACURRICULAR_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void changeExtracurricularToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mExtracurricular.getPkid());
		map.put("description", mExtracurricular.getDescription());
		String start = mExtracurricular.getPeriod().replace(".", "-");
		map.put("period", start);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.EDITEXTRACURRICULAR_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void deleteExtracurricularToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mExtracurricular.getPkid());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETEEXTRACURRICULAR_URL, map, new RequestResponseLinstner(this), this);
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
				mTime.setText(startTime);
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

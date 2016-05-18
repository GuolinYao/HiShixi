package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
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
 * 荣誉
 * 
 * @author ronger
 * @date:2015-11-5 上午11:02:30
 */
public class AddHonorActivity extends TitleBaseActivity {
	private Button mDeleteBtn;
	private TextView mTime;
	private TextView mContentLength;
	private EditText mContent;
	private Dialog confirmDialog = null;
	// 请求类型1添加2编辑3删除
	private int requestType = 1;
	private int position = -1;
	private int[] mTimeArray;
	private String mSplitChar = ".";
	private ArrayList<EducationBGBean> mHonorLists;
	private EducationBGBean mHonorBean;
	private DateWheelDialog mDateWheelDialog;

	public static void startAction(Activity context, int requestCode, int position,
			ArrayList<EducationBGBean> mHonorLists) {
		Intent intent = new Intent(context, AddHonorActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("mHonorLists", mHonorLists);
		context.startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		position = data.getIntExtra("position", -1);
		mHonorLists = (ArrayList<EducationBGBean>) data.getSerializableExtra("mHonorLists");
		if (null == mHonorLists)
			mHonorLists = new ArrayList<EducationBGBean>();
	}

	public void returnLastPage() {
		Intent intent = new Intent();
		intent.putExtra("mHonorLists", mHonorLists);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.add_extracurricular_layout);
		initDataShow();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("添加荣誉");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mDeleteBtn = (Button) findViewById(R.id.btn_delete);
		mTime = (TextView) findViewById(R.id.tv_end_intership_time_value);
		mContentLength = (TextView) findViewById(R.id.tv_activity_content_length);
		mContent = (EditText) findViewById(R.id.et_activity_content_content);

		Resources resources = getResources();
		mContent.setHint(resources.getString(R.string.honor_name_hint));
		mTime.setHint(resources.getString(R.string.honor_get_time_hint));
		TextView mGetHonorTime = (TextView) findViewById(R.id.tv_join_activity_time_key);
		mGetHonorTime.setText(resources.getString(R.string.honor_get_time_str));
		TextView mHonorName = (TextView) findViewById(R.id.tv_activity_content_key);
		mHonorName.setText(resources.getString(R.string.honor_name_str));
		mContentLength.setText("0/32");
		mContent.addTextChangedListener(new MaxLengthWatcher(32, mContent, mContentLength));
	}

	public void initDataShow() {
		if (position != -1) {
			requestType = 2;
			mDeleteBtn.setVisibility(View.VISIBLE);
			mHonorBean = mHonorLists.get(position);
			mContent.setText(mHonorBean.getDescription());
			mTime.setText(mHonorBean.getPeriod());
			setTime(mHonorBean.getPeriod());
		} else {
			mDeleteBtn.setVisibility(View.GONE);
			mHonorBean = new EducationBGBean();
			requestType = 1;
			setTime("");
		}
	}

	public void setTime(String startTime) {
		mTimeArray = StringUtils.getTime(startTime, mSplitChar);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.rl_join_activity_time_layout:
			setTime(mTime.getText().toString());
			mDateWheelDialog = new DateWheelDialog(this, mTimeArray[0], mTimeArray[1], mTimeArray[2]);
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
			ToastUtils.showToastInCenter("请选择获奖时间");
			return;
		}
		if (StringUtils.isEmpty(mContent.getText().toString())) {
			ToastUtils.showToastInCenter("请填写奖项名称");
			return;
		}
		saveHonorData();
	}

	/**
	 * 保存荣誉
	 */
	public void saveHonorData() {
		mHonorBean.setPeriod(mTime.getText().toString());
		mHonorBean.setDescription(mContent.getText().toString());
		setRequestParamsPre(TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (requestType == 1) {
			addHonorToserver();
		}
		if (requestType == 2) {
			changeHonorToserver();
		}
		if (requestType == 3) {
			deleteHonorToserver();
		}
	}

	public void addHonorToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("description", mHonorBean.getDescription());
		String start = mHonorBean.getPeriod().replace(".", "-");
		map.put("period", start);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.ADDHONOR_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void changeHonorToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mHonorBean.getPkid());
		map.put("description", mHonorBean.getDescription());
		String start = mHonorBean.getPeriod().replace(".", "-");
		map.put("period", start);
		map.put("language_type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.EDITHONOR_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void deleteHonorToserver() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("id", mHonorBean.getPkid());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETEHONOR_URL, map, new RequestResponseLinstner(this), this);
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
			// 若是增加需要先获取到返回的id,复制给mInterShipExperience的id
			if (requestType == 1) {
				mHonorBean.setPkid(httpRes.getReturnData());
				if (null == mHonorLists) {
					mHonorLists = new ArrayList<EducationBGBean>();
				}
				mHonorLists.add(mHonorBean);
				returnLastPage();
				return;
			}
			if (requestType == 2) {
				returnLastPage();
				return;
			}
			if (requestType == 3) {
				deleteHonorlar();
			}
		} catch (Exception e) {
			dissMissProgress();
			exceptionToast();
			setDebugLog(e);
		}
	}

	public void deleteHonorlar() {
		mHonorLists.remove(position);
		returnLastPage();
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
		setRequestParamsPre(TAG);
	}
}

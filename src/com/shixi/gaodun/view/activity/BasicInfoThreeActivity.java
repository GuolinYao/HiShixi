package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.db.IndexPageDB;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InternshipIntentionBean;
import com.shixi.gaodun.model.domain.MajorClassifyBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 基本信息引导页三
 * 
 * @author ronger
 * @date:2015-11-6 下午2:46:23
 */
public class BasicInfoThreeActivity extends TitleBaseActivity {
	private Button mNextBtn;
	/** 期望实习地、每周实习天数、期望日薪、期望实习职位、可实习阶段 */
	private TextView mExpectInternshipAreaText, mEveryweekDayText,
			mExpectDailywageText, mExpectInternshipPositionText,
			mCanInternshipStage;
	// 已结选择过了的期望实习地
	private ArrayList<CityBean> mSelectCitys;
	// 已经选择过了的职业
	private MajorClassifyBean mSelectPosition;
	// 实习阶段
	private String mStartTime, mEndTime;
	// 期望薪资
	private MapFormatBean mSelectWageBean;
	private InternshipIntentionBean mInternshipIntention;
	private IndexPageDB mIndexPageDB;
	private UserInfoBean mOldBasicInfo;
	private Dialog mDialog;
	private boolean isLoadFinishBaicInfo = false,
			isLoadFinishIntership = false;

	public static void startAction(Activity context, int requestCode) {
		Intent intent = new Intent(context, BasicInfoThreeActivity.class);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		BaseApplication.mApp.mCenterTaskActivitys.add(this);
		setContentView(R.layout.basicinfo_three_layout);
		initDataShow();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("基本信息（3/3）");
		mBackIcon.setVisibility(View.GONE);
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setText("跳过");
		mExpectInternshipAreaText = (TextView) findViewById(R.id.tv_expect_intern_area_value);
		mEveryweekDayText = (TextView) findViewById(R.id.tv_everyweek_canexpectday_value);
		mExpectDailywageText = (TextView) findViewById(R.id.tv_expect_daily_wage_value);
		mExpectInternshipPositionText = (TextView) findViewById(R.id.tv_expect_internship_position_value);
		mCanInternshipStage = (TextView) findViewById(R.id.tv_can_internship_stage_value);
		mNextBtn = (Button) findViewById(R.id.btn_next);
		mNextBtn.setText("完成创建");
	}

	/**
	 * 保存当页的数据 // 期望实习地 // 每周实习天数 // 期望日薪 // 期望实习职位 // 可实习阶段
	 */
	public void saveCurrentPageData() {
		if (null == mOldBasicInfo)
			mOldBasicInfo = new UserInfoBean();
		mInternshipIntention = new InternshipIntentionBean();
		// 期望实习地
		mInternshipIntention.setExpect_city(mSelectCitys);
		// 每周实习天数
		String day = mEveryweekDayText.getText().toString();
		if (StringUtils.isNotEmpty(day) && day.contains("天")) {
			day = day.replace("天", "");
		}
		mInternshipIntention.setWeek_available(day);
		// 期望日薪
		mInternshipIntention.setSalary_range(mSelectWageBean);
		// 期望实习职位
		if (null != mSelectPosition) {
			MapFormatBean positionBean = new MapFormatBean(
					mSelectPosition.getPkid(), mSelectPosition.getTitle());
			mInternshipIntention.setExpect_category(positionBean);
		}
		// 期望实习阶段
		String time = mCanInternshipStage.getText().toString();
		if (StringUtils.isNotEmpty(time)) {
			if (StringUtils.isNotEmpty(mEndTime)
					&& StringUtils.isNotEmpty(mStartTime)) {
				mInternshipIntention.setPeriod_start(mStartTime);
				mInternshipIntention.setPeriod_finish(mEndTime);
			}
		}
		mOldBasicInfo.setmIntershipBean(mInternshipIntention);
		try {
			mIndexPageDB.insertBasicInfo(mOldBasicInfo,
					CacheUtils.getStudentId(this));
		} catch (Exception e) {
			setDebugLog(e);
		}
	}

	/**
	 * 初始化数据显示
	 */
	public void initDataShow() {
		if (null == mOldBasicInfo)
			return;
		mInternshipIntention = mOldBasicInfo.getmIntershipBean();
		if (null == mInternshipIntention)
			return;
		// 期望实习地
		mSelectCitys = mInternshipIntention.getExpect_city();
		// TODO 这句加上会空指针 if (null != mSelectCitys || mSelectCitys.size() > 0)
		setAreaShow();
		// 每周实习天数
		setEveryweekDayTextShow(mInternshipIntention.getWeek_available());
		// 期望日薪
		mSelectWageBean = mInternshipIntention.getSalary_range();
		if (null != mSelectWageBean)
			setExpectDailywageTextShow(mSelectWageBean);
		// 期望实习职位
		MapFormatBean positionBean = mInternshipIntention.getExpect_category();
		if (null != positionBean) {
			mSelectPosition = new MajorClassifyBean(positionBean.getKey(),
					positionBean.getValue(), true);
			setExpectInternshipPositionTextShow(mSelectPosition);
		}
		// 可实习阶段
		if (StringUtils.isNotEmpty(mInternshipIntention.getPeriod_start())
				&& StringUtils.isNotEmpty(mInternshipIntention
						.getPeriod_finish())) {
			mStartTime = mInternshipIntention.getPeriod_start();
			mEndTime = mInternshipIntention.getPeriod_finish();
			setCanIntershipStageText(mStartTime, mEndTime);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_expect_intern_area_layout:// 期望实习地
			SelectInternshipAreaActivity.startAction(this,
					Finals.EXPECT_INTERN_AREA, mSelectCitys, 1);
			break;
		case R.id.rl_everyweek_canexpectday_layout:// 每周实习天数
			EveryWeekDayActivity.startAction(this,
					Finals.EVERYWEEK_CANEXPECTDAY, mEveryweekDayText.getText()
							.toString());
			break;
		case R.id.rl_expect_daily_wage_layout:// 期望日薪
			ExpectDailyWageActivity.startAction(this, Finals.EXPECT_DAILY_WAGE,
					mExpectDailywageText.getText().toString());
			break;
		case R.id.rl_expect_internship_position_layout:// 期望实习职位
			MajorClassifyActivity.startAction(this,
					Finals.EXPECT_INTERNSHIP_POSITION, mSelectPosition,
					"期望岗位类型");
			break;
		case R.id.rl_can_internship_stage_layout:// 可实习阶段
			CanIntershipStagActivity.startAction(this,
					Finals.CAN_INTERNSHIP_STAGE, mStartTime, mEndTime);
			break;
		case R.id.btn_next:
			checkIntershipIntentionIfCanCommit();
			break;
		case R.id.tv_title_bar_otherbtn:// 跳过
			mDialog = CustomDialog.CancelAlertToDialog("跳过将失去已填写的信息", "跳过",
					"继续填写", this, this);
			break;
		case R.id.tv_confrim_btn:
			mDialog.cancel();
			try {
				mIndexPageDB.clearBasicInfo(CacheUtils.getStudentId(this));
			} catch (Exception e) {
				setDebugLog(e);
			}
			finishPartProcess();
			break;
		case R.id.tv_cancel_btn:
			mDialog.cancel();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		saveCurrentPageData();
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case Finals.EXPECT_INTERN_AREA:// 期望实习地
			toSelectIntershipAreaReturn(arg2);
			break;
		case Finals.EVERYWEEK_CANEXPECTDAY:// 每周实习天数
			toSelectEveryWeekDayReturn(arg2);
			break;
		case Finals.EXPECT_DAILY_WAGE:// 期望日薪
			toSelectExpectDailyWageRerurn(arg2);
			break;
		case Finals.EXPECT_INTERNSHIP_POSITION:// 期望实习职位
			toSelectExpectIntershipPositionReturn(arg2);
			break;
		case Finals.CAN_INTERNSHIP_STAGE:// 实习阶段
			toSelectCanIntershipStageReturn(arg2);
			break;
		default:
			break;
		}
	}

	/**
	 * 去选择期望实习地后返回
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void toSelectIntershipAreaReturn(Intent data) {
		mSelectCitys = (ArrayList<CityBean>) data
				.getSerializableExtra("selectCity");
		setAreaShow();
	}

	/**
	 * 设置实习地区的显示
	 */
	public void setAreaShow() {
		if (null == mSelectCitys || mSelectCitys.isEmpty())
			return;
		StringBuilder selectCitySB = new StringBuilder();
		for (int i = 0; i < mSelectCitys.size(); i++) {
			CityBean cityBean = mSelectCitys.get(i);
			selectCitySB.append(cityBean.getRegion_name());
			if (i != mSelectCitys.size())
				selectCitySB.append("	");
		}
		mExpectInternshipAreaText.setText(selectCitySB.toString());
	}

	/**
	 * 选择每周实习天数返回
	 * 
	 * @param data
	 */
	public void toSelectEveryWeekDayReturn(Intent data) {
		String day = data.getStringExtra("day");
		setEveryweekDayTextShow(day);
	}

	public void setEveryweekDayTextShow(String day) {
		if (StringUtils.isNotEmpty(day)) {
			mEveryweekDayText.setText(day.contains("天") ? day : day + "天");
		}
	}

	/**
	 * 选择期望薪资后返回
	 * 
	 * @param data
	 */
	public void toSelectExpectDailyWageRerurn(Intent data) {
		mSelectWageBean = (MapFormatBean) data.getSerializableExtra("wage");
		setExpectDailywageTextShow(mSelectWageBean);
	}

	public void setExpectDailywageTextShow(MapFormatBean wage) {
		if (null != wage && StringUtils.isNotEmpty(wage.getValue())) {
			mExpectDailywageText.setText(wage.getValue());
		}
	}

	/**
	 * 选择期望实习职位返回
	 * 
	 * @param data
	 */
	public void toSelectExpectIntershipPositionReturn(Intent data) {
		mSelectPosition = (MajorClassifyBean) data.getSerializableExtra("bean");
		setExpectInternshipPositionTextShow(mSelectPosition);
	}

	public void setExpectInternshipPositionTextShow(
			MajorClassifyBean mSelectPosition) {
		if (null != mSelectPosition) {
			mExpectInternshipPositionText.setText(StringUtils
					.isEmpty(mSelectPosition.getTitle()) ? "" : mSelectPosition
					.getTitle());
		}
	}

	/**
	 * 选择可实习阶段后返回
	 * 
	 * @param data
	 */
	public void toSelectCanIntershipStageReturn(Intent data) {
		mStartTime = data.getStringExtra("startTime");
		mEndTime = data.getStringExtra("endTime");
		setCanIntershipStageText(mStartTime, mEndTime);
	}

	public void setCanIntershipStageText(String mStartTime, String mEndTime) {
		if (StringUtils.isNotEmpty(mStartTime)
				&& StringUtils.isNotEmpty(mEndTime)) {
			mCanInternshipStage.setText(mStartTime + "-" + mEndTime);
		}
	}

	/**
	 * 检测实习意向是否可提交
	 */
	public void checkIntershipIntentionIfCanCommit() {
		if (null == mSelectCitys || mSelectCitys.isEmpty()) {
			ToastUtils.showToastInCenter("请选择期望实习地");
			return;
		}
		if (StringUtils.isEmpty(mEveryweekDayText.getText().toString())) {
			ToastUtils.showToastInCenter("请选择每周期望实习天数");
			return;
		}
		if (StringUtils.isEmpty(mExpectDailywageText.getText().toString())
				|| null == mSelectWageBean
				|| StringUtils.isEmpty(mSelectWageBean.getKey())) {
			ToastUtils.showToastInCenter("请选择日薪范围");
			return;
		}
		if (StringUtils.isEmpty(mExpectInternshipPositionText.getText()
				.toString())
				|| null == mSelectPosition
				|| StringUtils.isEmpty(mSelectPosition.getPkid())) {
			ToastUtils.showToastInCenter("请选择实习职位");
			return;
		}
		// 先保存当页数据，再请求服务器
		saveCurrentPageData();
		setRequestParamsPre(TAG);
		// saveIntershipIntentionData();
	}

	// /**
	// * 保存实习意向相关数据
	// */
	// public void saveIntershipIntentionData() {
	// mInternshipIntention = new InternshipIntentionBean();
	// mInternshipIntention.setExpect_city(mSelectCitys);
	// String day = mEveryweekDayText.getText().toString();
	// if (day.contains("天")) {
	// day = day.replace("天", "");
	// }
	// mInternshipIntention.setWeek_available(day);
	// mInternshipIntention.setSalary_range(mSelectWageBean);
	// MapFormatBean positionBean = new MapFormatBean(mSelectPosition.getPkid(),
	// mSelectPosition.getTitle());
	// mInternshipIntention.setExpect_category(positionBean);
	// String time = mCanInternshipStage.getText().toString();
	// String[] timeArray = time.split("\\-");
	// if (null != timeArray && timeArray.length == 2) {
	// mInternshipIntention.setPeriod_start(timeArray[0]);
	// mInternshipIntention.setPeriod_finish(timeArray[1]);
	// }
	// setRequestParamsPre(TAG);
	// }

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		commitBasicInfo();
		changeInternshipIntention(mInternshipIntention);
	}

	/**
	 * 更新实习意向
	 */
	public void changeInternshipIntention(
			InternshipIntentionBean mInternshipIntention) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("week_available", mInternshipIntention.getWeek_available());
		map.put("expect_city", mInternshipIntention.getCityListStr(","));
		map.put("salary_range", mInternshipIntention.getSalary_range().getKey());
		map.put("expect_category", mInternshipIntention.getExpect_category()
				.getKey());
		if (StringUtils.isNotEmpty(mInternshipIntention.getToServerStartTime())) {
			map.put("period_start", mInternshipIntention.getToServerStartTime());
		}

		if (StringUtils.isNotEmpty(mInternshipIntention.getToServerEndTime())) {
			map.put("period_finish", mInternshipIntention.getToServerEndTime());
		}

		// JsonObjectPostRequest request = new
		// JsonObjectPostRequest(StringUtils.getCommonIP()
		// + GlobalContants.CHANGEINTERSHIPINTENTION_URL, map, this, this);
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.CHANGEINTERSHIPINTENTION_URL, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								dissMissProgress();
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							Log.d("gaodun", httpRes.getReturnData());
							isLoadFinishIntership = true;
							if (isLoadFinishBaicInfo && isLoadFinishIntership) {
								dissMissProgress();
								delayedJump();
							}
						} catch (Exception e) {
							dissMissProgress();
							setDebugLog(e);
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 提交基本信息
	 */
	private void commitBasicInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("name", mOldBasicInfo.getName());
		map.put("gender", mOldBasicInfo.getGender());
		map.put("avatar", mOldBasicInfo.getAvatar());
		map.put("birth_date", mOldBasicInfo.getBirth_date());
		if (StringUtils.isNotEmpty(mOldBasicInfo.getId_number()))
			map.put("id_number", mOldBasicInfo.getId_number());
		String politics_status = mOldBasicInfo.getPolitics_status();
		if (StringUtils.isNotEmpty(politics_status))
			map.put("politics_status", politics_status);
		if (null != mOldBasicInfo.getLiving_city()
				&& StringUtils.isNotEmpty(mOldBasicInfo.getLiving_city()
						.getRegion_id())) {
			map.put("living_city", mOldBasicInfo.getLiving_city()
					.getRegion_id());
		}

		if (StringUtils.isNotEmpty(mOldBasicInfo.getGraduate_school())) {
			map.put("graduate_school", mOldBasicInfo.getGraduate_school());
		}
		MapFormatBean mSelectMajorBean = mOldBasicInfo.getMajor_type();
		if (null != mSelectMajorBean
				&& StringUtils.isNotEmpty(mSelectMajorBean.getKey())) {
			map.put("major_type", mSelectMajorBean.getKey());
		}
		if (StringUtils.isNotEmpty(mOldBasicInfo.getDetail_major())) {
			map.put("detail_major", mOldBasicInfo.getDetail_major());
		}
		if (StringUtils.isNotEmpty(mOldBasicInfo.getEducation())) {
			map.put("education", mOldBasicInfo.getEducation());
		}
		if (StringUtils.isNotEmpty(mOldBasicInfo.getContact_email()))
			map.put("contact_email", mOldBasicInfo.getContact_email());
		if (StringUtils.isNotEmpty(mOldBasicInfo.getContact_mobile())) {
			map.put("mobile", mOldBasicInfo.getContact_mobile());
		}
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.UPDATE_BASIINFO_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							if (response.getInt("returnCode") != 0) {
								dissMissProgress();
								ToastUtils.showToastInCenter(response
										.getString("returnDesc"));
								return;
							}
							isLoadFinishBaicInfo = true;
							if (isLoadFinishBaicInfo && isLoadFinishIntership) {
								dissMissProgress();
								delayedJump();
							}
						} catch (Exception e) {
							dissMissProgress();
							setDebugLog(e);
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void delayedJump() {
		super.delayedJump();
		sendBroadcast();
		CacheUtils.saveIndex(BasicInfoThreeActivity.this, 3);
		MainActivity.startAction(BasicInfoThreeActivity.this, 1);
	}

	@Override
	protected void getIntentData() {
		mIndexPageDB = IndexPageDB.getInstance(this);
		mOldBasicInfo = mIndexPageDB
				.getBasicInfo(CacheUtils.getStudentId(this));
	}

	@Override
	protected void setRetryRequest() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("threeGuid"); // 引导页3
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("threeGuid");
		MobclickAgent.onPause(this);
	}
}

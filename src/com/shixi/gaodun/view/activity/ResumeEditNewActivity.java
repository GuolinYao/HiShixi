package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CertificateBean;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.EducationBGBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InternshipIntentionBean;
import com.shixi.gaodun.model.domain.MajorClassifyBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.RefersBean;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 简历编辑
 * 
 * @author ronger guolin
 * @date:2015-11-2 上午9:03:10
 */
@SuppressLint("InflateParams")
public class ResumeEditNewActivity extends TitleBaseActivity {
	/** 期望实习地、每周实习天数、期望日薪、期望实习职位、可实习阶段 */
	private TextView mExpectInternshipAreaText, mEveryweekDayText,
			mExpectDailywageText, mExpectInternshipPositionText,
			mCanInternshipStage;
	private LinearLayout mEducationalBgLayout, MinternshipExperienceLayout,
			mExtracurricularLayout, mSchoolOfficeLayout, mCertificateLayout,
			mHonorLayout, mHRlayout;
	// 已结选择过了的期望实习地
	private ArrayList<CityBean> mSelectCitys;
	// 已经选择过了的职业
	private MajorClassifyBean mSelectPosition;
	private String mStartTime, mEndTime;
	private final String mEducationTag = "教育",
			mInternshipExperienceTag = "实习经历", mExtracurricularTag = "课外活动",
			mSchoolOfficeTag = "校内职务", mCertificateTag = "证书",
			mHonorTag = "荣誉", mHRTag = "HR";
	private ArrayList<EducationBGBean> mEducationLists,
			mIntershipExperienceLists, mExtracurricularLists,
			mSchoolOfficeLists, mHonorLists;
	// 实习意向
	private InternshipIntentionBean mInternshipIntention;
	private ArrayList<CertificateBean> mCertificateLists;
	// 判断所有异步是否加载完成加载完成
	private boolean mRequestIntership, mRequestEducation, mRequestExperience,
			mRequestExtracurricular, mRequestSchoolOffice, mQequestCertificate,
			mRequestHonor, mRequestHR;
	private String mDearHr;
	private MapFormatBean mSelectWageBean;
	private boolean ifSave = true;

	private UserInfoBean mOldBasicInfo;
	private boolean isLoadFinishBaicInfo = false,
			isLoadFinishIntership = false;

	// public static void startAction(Activity context) {
	// Intent intent = new Intent(context, ResumeEditActivity.class);
	// context.startActivity(intent);
	// }

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, ResumeEditNewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = true;
		BaseApplication.mApp.mCenterTaskActivitys.add(this);
		setContentView(R.layout.edit_resume_layout);
		setRequestParamsPre(TAG);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("编辑我的简历");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setText("保存");
		mOtherName.setVisibility(View.VISIBLE);
		mExpectInternshipAreaText = (TextView) findViewById(R.id.tv_expect_intern_area_value);
		mEveryweekDayText = (TextView) findViewById(R.id.tv_everyweek_canexpectday_value);
		mExpectDailywageText = (TextView) findViewById(R.id.tv_expect_daily_wage_value);
		mExpectInternshipPositionText = (TextView) findViewById(R.id.tv_expect_internship_position_value);
		mCanInternshipStage = (TextView) findViewById(R.id.tv_can_internship_stage_value);
		mEducationalBgLayout = (LinearLayout) findViewById(R.id.fl_educational_background_layout);
		MinternshipExperienceLayout = (LinearLayout) findViewById(R.id.fl_internship_experience_layout);
		mExtracurricularLayout = (LinearLayout) findViewById(R.id.fl_extracurricular_activities_layout);
		mSchoolOfficeLayout = (LinearLayout) findViewById(R.id.fl_school_office_layout);
		mCertificateLayout = (LinearLayout) findViewById(R.id.fl_certificate_layout);
		mHonorLayout = (LinearLayout) findViewById(R.id.fl_honor_layout);
		mHRlayout = (LinearLayout) findViewById(R.id.fl_hr_layout);
	}

	/**
	 * 给某个FlowLayout添加一个单独的添加按钮 通用
	 * 
	 * @param title
	 * @param floaLayout
	 */
	@SuppressLint("InflateParams")
	public void addSingleAddBtn(String title, LinearLayout floaLayout,
			String type) {
		RelativeLayout mEducationLayout = (RelativeLayout) LayoutInflater.from(
				this).inflate(R.layout.resume_nomal_flow_addbtn_layout, null);
		LayoutParams layoutParams = new LayoutParams(
				ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(this, 49));
		mEducationLayout.setLayoutParams(layoutParams);
		TextView textView = (TextView) mEducationLayout
				.findViewById(R.id.tv_flow_btn);
		textView.setText(title);
		mEducationLayout.setTag(type);
		mEducationLayout.setOnClickListener(this);
		floaLayout.addView(mEducationLayout);
	}

	/**
	 * 得到一个普通的View 通用
	 */
	public View addSingelFlowItem(RefersBean bean) {
		RelativeLayout layout = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.resume_nomal_flow_item_layout, null);
		RelativeLayout hrLayout = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.resume_nomal_flow_hr_item_layout, null);
		LayoutParams layoutParams = new LayoutParams(
				ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(this, 49));
		layout.setLayoutParams(layoutParams);
		layout.setOnClickListener(this);
		hrLayout.setLayoutParams(layoutParams);
		hrLayout.setOnClickListener(this);
		com.shixi.gaodun.view.ByLengthSetShowTextView textview = (com.shixi.gaodun.view.ByLengthSetShowTextView) layout
				.findViewById(R.id.tv_flow_data_name);
		TextView hrTextView = (TextView) hrLayout
				.findViewById(R.id.tv_flow_hr_data_time);
		TextView textViewValue = (TextView) layout
				.findViewById(R.id.tv_flow_data_time);
		// 教育背景:时间显示为年月没有日
		if (bean.getClassName().equals(mEducationTag)) {
			EducationBGBean educationBean = (EducationBGBean) bean.getObject();
			textview.setValue(educationBean.getSchool_name());
			textview.setText(educationBean.getSchool_name());
			textViewValue.setText(getYearAndMonth(
					educationBean.getStart_time(), ".")
					+ "-"
					+ getYearAndMonth(educationBean.getFinish_time(), "."));
			// textViewValue.setText(educationBean.getStart_time() + "-" +
			// educationBean.getFinish_time());
		}
		// 实习经历
		if (bean.getClassName().equals(mInternshipExperienceTag)) {
			EducationBGBean experienceBean = (EducationBGBean) bean.getObject();
			textview.setValue(experienceBean.getOrganization());
			textview.setText(experienceBean.getOrganization());
			textViewValue.setText(getYearAndMonth(
					experienceBean.getStart_time(), ".")
					+ "-"
					+ getYearAndMonth(experienceBean.getFinish_time(), "."));
			// textViewValue.setText(experienceBean.getStart_time() + "-" +
			// experienceBean.getFinish_time());
		}
		// 课外活动
		if (bean.getClassName().equals(mExtracurricularTag)) {
			EducationBGBean experienceBean = (EducationBGBean) bean.getObject();
			textview.setValue(experienceBean.getDescription());
			textview.setText(experienceBean.getDescription());
			textViewValue.setText(getYearAndMonth(experienceBean.getPeriod(),
					"."));
			// textViewValue.setText(experienceBean.getPeriod());
		}
		// 校内职务
		if (bean.getClassName().equals(mSchoolOfficeTag)) {
			EducationBGBean experienceBean = (EducationBGBean) bean.getObject();
			textview.setValue(experienceBean.getJob_title());
			textview.setText(experienceBean.getJob_title());
			textViewValue.setText(getYearAndMonth(
					experienceBean.getStart_time(), ".")
					+ "-"
					+ getYearAndMonth(experienceBean.getFinish_time(), "."));
			// textViewValue.setText(experienceBean.getStart_time() + "-" +
			// experienceBean.getFinish_time());
		}
		// 证书
		if (bean.getClassName().equals(mCertificateTag)) {
			CertificateBean certificate = (CertificateBean) bean.getObject();
			textview.setValue(certificate.getCertificate_name());
			textview.setText(certificate.getCertificate_name());
			textViewValue.setText(getYearAndMonth(certificate.getFinish_time(),
					"."));
			// textViewValue.setText(certificate.getFinish_time());
		}
		// 荣誉
		if (bean.getClassName().equals(mHonorTag)) {
			EducationBGBean honor = (EducationBGBean) bean.getObject();
			textview.setValue(honor.getDescription());
			textview.setText(honor.getDescription());
			textViewValue.setText(getYearAndMonth(honor.getPeriod(), "."));
			// textViewValue.setText(honor.getPeriod());
		}
		// HR
		if (bean.getClassName().equals(mHRTag)) {
			hrTextView.setText(mDearHr);
			hrLayout.setTag(bean);
			return hrLayout;
		}
		layout.setTag(bean);
		return layout;
	}

	/**
	 * 获取年月
	 * 
	 * @return
	 */
	public String getYearAndMonth(String time, String pinStr) {
		if (StringUtils.isEmpty(time))
			return "";
		StringBuilder returnSB = new StringBuilder();
		String[] startArray = time.split("\\" + pinStr);
		if (null != startArray && startArray.length >= 2) {
			returnSB.append(startArray[0]);
			returnSB.append(pinStr);
			returnSB.append(startArray[1]);
		}
		return returnSB.toString();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case Finals.EXPECT_INTERN_AREA:
			toSelectIntershipAreaReturn(arg2);
			break;
		case Finals.EVERYWEEK_CANEXPECTDAY:
			toSelectEveryWeekDayReturn(arg2);
			break;
		// 期望日薪
		case Finals.EXPECT_DAILY_WAGE:
			toSelectExpectDailyWageRerurn(arg2);
			break;
		// 期望实习职位
		case Finals.EXPECT_INTERNSHIP_POSITION:
			toSelectExpectIntershipPositionReturn(arg2);
			break;
		case Finals.CAN_INTERNSHIP_STAGE:
			toSelectCanIntershipStageReturn(arg2);
			break;
		// 教育背景
		case Finals.EDUCATIONBG_REQUEST:
			toEditEducationBGReturn(arg2);
			break;
		// 实习经历
		case Finals.INTERSHIPEXPERIENCE_REQUEST:
			toEditExperienceReturn(arg2);
			break;
		// 课外活动
		case Finals.EXTRACURRICULAR_REQUEST:
			toEditExtracurricularReturn(arg2);
			break;
		// 校内职务
		case Finals.SCHOOLOFFICE_REQUEST:
			toEditSchoolOfficeReturn(arg2);
			break;
		// 证书
		case Finals.CERTIFICATE_REQUEST:
			toCertificateReturn(arg2);
			break;
		// 荣誉
		case Finals.HONOR_REQUEST:
			toHonorReturn(arg2);
			break;
		// HR
		case Finals.HR_REQUEST:
			mDearHr = arg2.getStringExtra("dear_hr");
			if (StringUtils.isNotEmpty(mDearHr)) {
				updateHRFlowShow();
			} else {
				// 被删除
				mHRlayout.removeAllViews();
				addSingleAddBtn("+   Dear HR", mHRlayout, mHRTag);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_again:
			setRetryRequest();
			break;
		case R.id.fl_title_bar_back:
			sendBroadcast(Contants.PREVIEW_RESUME_UPDATE_ACTION);
			finish();
			break;
		case R.id.rl_expect_intern_area_layout:
			SelectInternshipAreaActivity.startAction(this,
					Finals.EXPECT_INTERN_AREA, mSelectCitys, 1);
			break;
		case R.id.rl_everyweek_canexpectday_layout:
			EveryWeekDayActivity.startAction(this,
					Finals.EVERYWEEK_CANEXPECTDAY, mEveryweekDayText.getText()
							.toString());
			break;
		case R.id.rl_expect_daily_wage_layout:
			ExpectDailyWageActivity.startAction(this, Finals.EXPECT_DAILY_WAGE,
					mExpectDailywageText.getText().toString());
			break;
		case R.id.rl_expect_internship_position_layout:
			MajorClassifyActivity.startAction(this,
					Finals.EXPECT_INTERNSHIP_POSITION, mSelectPosition,
					"期望岗位类型");
			break;
		// 可实习阶段
		case R.id.rl_can_internship_stage_layout:
			CanIntershipStagActivity.startAction(this,
					Finals.CAN_INTERNSHIP_STAGE, mStartTime, mEndTime);
			break;
		// 预览简历
		case R.id.rl_preview_resume:
			checkIntershipIntentionIfCanCommit();
			break;
		case R.id.rl_flow_addbtn:
			String tag = (String) v.getTag();
			onClickAddBtn(tag);
			break;
		case R.id.rl_exitdata_flow_layout:
			RefersBean dataTag = (RefersBean) v.getTag();
			onClickShowData(dataTag);
			break;
		case R.id.rl_exitdata_hr_flow_layout:
			AddDearHRActivity.startAction(this, Finals.HR_REQUEST, mDearHr);
			break;
		// 保存修改过的简历
		case R.id.tv_title_bar_otherbtn:

			// myProgressDialog.setTitle("加载中...");
			// setRequestParamsPre(TAG);
			checkIntershipIntentionIfCanCommit();
			if (ifSave) {
				// finish();
				break;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 保存当页的数据 // 期望实习地 // 每周实习天数 // 期望日薪 // 期望实习职位 // 可实习阶段
	 */

	@Override
	public void onBackPressed() {
		sendBroadcast(Contants.PREVIEW_RESUME_UPDATE_ACTION);
		super.onBackPressed();
	}

	/**
	 * 添加按钮的点击 mEducationTag = "教育", mInternshipExperienceTag = "实习经历",
	 * mExtracurricularTag = "课外活动", mSchoolOfficeTag = "校内职务", mCertificateTag
	 * = "证书", mHonorTag = "荣誉", mHRTag = "HR"
	 * 
	 * @param tag
	 */
	public void onClickAddBtn(String tag) {
		if (tag.equals(mEducationTag)) {// 教育背景
			AddEducationBGActivity.startAction(this,
					Finals.EDUCATIONBG_REQUEST, -1, mEducationLists);
		}
		if (tag.equals(mInternshipExperienceTag)) {// 实习经历
			AddinternshipExperienceActivity.startAction(this,
					Finals.INTERSHIPEXPERIENCE_REQUEST, -1,
					mIntershipExperienceLists);
		}
		if (tag.equals(mExtracurricularTag)) {// 课外活动
			AddExtracurricularActivity.startAction(this,
					Finals.EXTRACURRICULAR_REQUEST, -1, mExtracurricularLists);
		}
		if (tag.equals(mSchoolOfficeTag)) {// 校内职务
			SchoolOfficeActivity.startAction(this, Finals.SCHOOLOFFICE_REQUEST,
					-1, mSchoolOfficeLists);
		}
		if (tag.equals(mCertificateTag)) {// 证书
			AddCertificateActivity.startAction(this,
					Finals.CERTIFICATE_REQUEST, -1, mCertificateLists);
		}
		if (tag.equals(mHonorTag)) {// 荣誉
			AddHonorActivity.startAction(this, Finals.HONOR_REQUEST, -1,
					mHonorLists);
		}
		if (tag.equals(mHRTag)) {
			AddDearHRActivity.startAction(this, Finals.HR_REQUEST, mDearHr);
		}
	}

	/**
	 * flowlayout里面有数据的点击
	 * 
	 * @param bean
	 */
	public void onClickShowData(RefersBean bean) {
		String className = bean.getClassName();
		if (className.equals(mEducationTag)) {// 教育背景
			AddEducationBGActivity.startAction(this,
					Finals.EDUCATIONBG_REQUEST, bean.getPosition(),
					mEducationLists);
		}
		if (className.equals(mInternshipExperienceTag)) {// 实习经历
			AddinternshipExperienceActivity.startAction(this,
					Finals.INTERSHIPEXPERIENCE_REQUEST, bean.getPosition(),
					mIntershipExperienceLists);
		}
		if (className.equals(mExtracurricularTag)) {// 课外活动
			AddExtracurricularActivity.startAction(this,
					Finals.EXTRACURRICULAR_REQUEST, bean.getPosition(),
					mExtracurricularLists);
		}
		if (className.equals(mSchoolOfficeTag)) {// 校内职务
			SchoolOfficeActivity.startAction(this, Finals.SCHOOLOFFICE_REQUEST,
					bean.getPosition(), mSchoolOfficeLists);
		}
		if (className.equals(mCertificateTag)) {// 证书
			AddCertificateActivity.startAction(this,
					Finals.CERTIFICATE_REQUEST, bean.getPosition(),
					mCertificateLists);
		}
		if (className.equals(mHonorTag)) {// 荣誉
			AddHonorActivity.startAction(this, Finals.HONOR_REQUEST,
					bean.getPosition(), mHonorLists);
		}

	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		getResumeInfo();
		// getInternshipIntention();
		// getEducationalBgList();
		// getInternshipExperienceList();
		// getExtracurricularActivitiesList();
		// getSchoolOfficeList();
		// getCertificateList();
		// getHonorList();
		// getHRList();

	}

	public boolean ifStopLoding() {
		if (mRequestIntership && mRequestEducation && mRequestExperience
				&& mRequestExtracurricular && mRequestSchoolOffice
				&& mQequestCertificate && mRequestHonor && mRequestHR) {
			return true;
		}
		return false;
	}

	/**
	 * 获取实习意向包括 意向城市、可实习天数、期望日薪、期望岗位类型，可实习阶段
	 */

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
		saveIntershipIntentionData();
	}

	/**
	 * 保存实习意向相关数据
	 */
	public void saveIntershipIntentionData() {
		if (null == mInternshipIntention)
			mInternshipIntention = new InternshipIntentionBean();
		mInternshipIntention.setExpect_city(mSelectCitys);
		String day = mEveryweekDayText.getText().toString();
		if (day.contains("天")) {
			day = day.replace("天", "");
		}
		mInternshipIntention.setWeek_available(day);
		mInternshipIntention.setSalary_range(mSelectWageBean);
		MapFormatBean positionBean = new MapFormatBean(
				mSelectPosition.getPkid(), mSelectPosition.getTitle());
		mInternshipIntention.setExpect_category(positionBean);
		String time = mCanInternshipStage.getText().toString();
		String[] timeArray = time.split("\\-");
		if (null != timeArray && timeArray.length == 2) {
			mInternshipIntention.setPeriod_start(timeArray[0]);
			mInternshipIntention.setPeriod_finish(timeArray[1]);
		}
		Log.i("----", "实习天数/周" + day);
		changeInternshipIntention();// TODO
	}

	/**
	 * 更新实习意向
	 */
	public void changeInternshipIntention() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("week_available", mInternshipIntention.getWeek_available());
		map.put("expect_city", mInternshipIntention.getCityListStr(","));
		map.put("salary_range", mInternshipIntention.getSalary_range().getKey());
		map.put("expect_category", mInternshipIntention.getExpect_category()
				.getKey());
		if (StringUtils.isNotEmpty(mInternshipIntention.getToServerStartTime()))
			map.put("period_start", mInternshipIntention.getToServerStartTime());
		if (StringUtils.isNotEmpty(mInternshipIntention.getToServerEndTime()))
			map.put("period_finish", mInternshipIntention.getToServerEndTime());
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
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
							} else {
								MinePreviewResumeActivity
										.startAction(ResumeEditNewActivity.this);
								// }

							}
						} catch (Exception e) {
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 解析实习意向数据
	 * 
	 * @param response
	 */
	public void getInternshipIntentionSuccess(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			} else {
				mInternshipIntention = TransFormModel.getResponseResultObject(
						httpRes.getReturnData(), InternshipIntentionBean.class);
				setInternshipIntentionShow();
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 实习意向显示
	 */
	public void setInternshipIntentionShow() {
		mSelectCitys = mInternshipIntention.getExpect_city();
		setAreaShow();
		String day = mInternshipIntention.getWeek_available();
		setEveryweekDayTextShow(day);
		mSelectWageBean = mInternshipIntention.getSalary_range();
		setExpectDailywageTextShow(mSelectWageBean);
		mSelectPosition = new MajorClassifyBean(mInternshipIntention
				.getExpect_category().getKey(), mInternshipIntention
				.getExpect_category().getValue(), true);
		setExpectInternshipPositionTextShow(mSelectPosition);
		mStartTime = mInternshipIntention.getPeriod_start();
		mEndTime = mInternshipIntention.getPeriod_finish();
		if (mStartTime.equals("0000-00-00") || mStartTime.equals("0000.00.00")
				|| mEndTime.equals("0000-00-00")
				|| mEndTime.equals("0000.00.00")) {
			mCanInternshipStage.setHint("选填");
		} else
			setCanIntershipStageText(mInternshipIntention.getPeriod_start(),
					mInternshipIntention.getPeriod_finish());

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
	 * 获取简历信息
	 */
	public void getResumeInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.EDITRESUME, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						mRequestEducation = true;
						mRequestIntership = true;
						isFirstJoin = false;
						try {
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
							} else {// TODO
								String returnData = httpRes.getReturnData();
								getEducationalBgListSuccess(response);
								getInternshipExperienceListSuccess(response);
								getInternshipIntentionSuccess(response);
							}
							if (ifStopLoding()) {
								setNolmalShow();
								dissMissProgress();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取教育背景成功，这里直接处理异常，防止因为此处错误影响下一步的异步请求
	 * 
	 * @param resultData
	 */
	public void getEducationalBgListSuccess(JSONObject response) {

		// setEducationBtnShow();

	}

	/**
	 * 获取教育背景列表成功后的显示
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void setEducationBtnShow(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			addSingleAddBtn("+   添加教育背景", mEducationalBgLayout, mEducationTag);
			return;
		} else {
			mEducationLists = TransFormModel.getEducationBGList(resultData);
			updateEducationFlowShow();
		}
	}

	/**
	 * 编辑教育背景后返回
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void toEditEducationBGReturn(Intent data) {
		mEducationLists = (ArrayList<EducationBGBean>) data
				.getSerializableExtra("mEducationLists");
		updateEducationFlowShow();
	}

	/**
	 * 更新教育背景的显示
	 */
	public void updateEducationFlowShow() {
		mEducationalBgLayout.removeAllViews();
		if (null == mEducationLists || mEducationLists.isEmpty()) {
			mEducationLists = new ArrayList<EducationBGBean>();
			addSingleAddBtn("+   添加教育背景", mEducationalBgLayout, mEducationTag);
			return;
		}
		for (int i = 0; i < mEducationLists.size(); i++) {
			EducationBGBean educationbg = mEducationLists.get(i);
			RefersBean bean = new RefersBean(educationbg, i, mEducationTag);
			View view = addSingelFlowItem(bean);
			mEducationalBgLayout.addView(view);
		}
		addSingleAddBtn("+   添加教育背景", mEducationalBgLayout, mEducationTag);
		// setLayoutHight(mEducationalBgLayout, mEducationLists.size() + 1,
		// defaultItemHeight);
	}

	/**
	 * 获取实习经历列表
	 */
	public void getInternshipExperienceList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.GETINTERNSHIPEXPERIENCELIST, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						mRequestExperience = true;
						getInternshipExperienceListSuccess(response);
						if (ifStopLoding()) {
							setNolmalShow();
							dissMissProgress();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void getInternshipExperienceListSuccess(JSONObject response) {
		// 获取实习经历成功之后获取课外活动
		// mRequestType = 3;
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			} else {
				setIntershipExperienceShow(httpRes.getReturnData());
			}
		} catch (Exception e) {
		}
		// getExtracurricularActivitiesList(mRequestType);
	}

	/**
	 * 设置实习经历的显示
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void setIntershipExperienceShow(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			addSingleAddBtn("+   添加实习经历", MinternshipExperienceLayout,
					mInternshipExperienceTag);
			return;
		} else {
			mIntershipExperienceLists = TransFormModel
					.getEducationBGList(resultData);
			updateIntershipExperienceFlowShow();
		}

	}

	/**
	 * 更新实习经历的显示
	 */
	public void updateIntershipExperienceFlowShow() {
		MinternshipExperienceLayout.removeAllViews();
		if (null == mIntershipExperienceLists
				|| mIntershipExperienceLists.isEmpty()) {
			addSingleAddBtn("+   添加实习经历", MinternshipExperienceLayout,
					mInternshipExperienceTag);
			return;
		}
		for (int i = 0; i < mIntershipExperienceLists.size(); i++) {
			EducationBGBean educationbg = mIntershipExperienceLists.get(i);
			RefersBean bean = new RefersBean(educationbg, i,
					mInternshipExperienceTag);
			View view = addSingelFlowItem(bean);
			MinternshipExperienceLayout.addView(view);
		}
		addSingleAddBtn("+   添加实习经历", MinternshipExperienceLayout,
				mInternshipExperienceTag);
	}

	/**
	 * 编辑实习经历后返回
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void toEditExperienceReturn(Intent data) {
		mIntershipExperienceLists = (ArrayList<EducationBGBean>) data
				.getSerializableExtra("mInternshipExperienceLists");
		updateIntershipExperienceFlowShow();
	}

	/**
	 * 获取课外活动列表
	 */
	public void getExtracurricularActivitiesList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.GETEXTRACURRICULARACTIVITIESLIST, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						mRequestExtracurricular = true;
						getExtracurricularActivitiesListSuccess(response);
						if (ifStopLoding()) {
							setNolmalShow();
							dissMissProgress();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void getExtracurricularActivitiesListSuccess(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			} else {
				setExtracurricularShow(httpRes.getReturnData());
			}
		} catch (Exception e) {
		}
	}

	public void setExtracurricularShow(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			addSingleAddBtn("+   添加课外活动", mExtracurricularLayout,
					mExtracurricularTag);
			return;
		}
		mExtracurricularLists = TransFormModel.getEducationBGList(resultData);
		updateExtracurricularFlowShow();
	}

	/**
	 * 编辑课外活动后返回
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void toEditExtracurricularReturn(Intent data) {
		mExtracurricularLists = (ArrayList<EducationBGBean>) data
				.getSerializableExtra("mExtracurricularLists");
		updateExtracurricularFlowShow();
	}

	/**
	 * 更新课外活动的显示
	 */
	public void updateExtracurricularFlowShow() {
		mExtracurricularLayout.removeAllViews();
		if (null == mExtracurricularLists || mExtracurricularLists.isEmpty()) {
			addSingleAddBtn("+   添加课外活动", mExtracurricularLayout,
					mExtracurricularTag);
			return;
		}
		for (int i = 0; i < mExtracurricularLists.size(); i++) {
			EducationBGBean educationbg = mExtracurricularLists.get(i);
			RefersBean bean = new RefersBean(educationbg, i,
					mExtracurricularTag);
			View view = addSingelFlowItem(bean);
			mExtracurricularLayout.addView(view);
		}
		addSingleAddBtn("+   添加课外活动", mExtracurricularLayout,
				mExtracurricularTag);
	}

	/**
	 * 获取校内职务列表
	 */
	public void getSchoolOfficeList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETSCHOOLOFFICELIST,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						mRequestSchoolOffice = true;
						getSchoolOfficeListSuccess(response);
						if (ifStopLoding()) {
							setNolmalShow();
							dissMissProgress();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取校内职务列表成功
	 * 
	 * @param response
	 */
	public void getSchoolOfficeListSuccess(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			} else {
				setSchoolOfficeShow(httpRes.getReturnData());
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 获取校内职务数据成功后设置显示
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void setSchoolOfficeShow(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			addSingleAddBtn("+   添加校内职务", mSchoolOfficeLayout, mSchoolOfficeTag);
			return;
		}
		mSchoolOfficeLists = TransFormModel.getEducationBGList(resultData);
		updateSchoolOfficeFlowShow();
	}

	public void updateSchoolOfficeFlowShow() {
		mSchoolOfficeLayout.removeAllViews();
		if (null == mSchoolOfficeLists || mSchoolOfficeLists.isEmpty()) {
			addSingleAddBtn("+   添加校内职务", mSchoolOfficeLayout, mSchoolOfficeTag);
			return;
		}
		for (int i = 0; i < mSchoolOfficeLists.size(); i++) {
			EducationBGBean educationbg = mSchoolOfficeLists.get(i);
			RefersBean bean = new RefersBean(educationbg, i, mSchoolOfficeTag);
			View view = addSingelFlowItem(bean);
			mSchoolOfficeLayout.addView(view);
		}
		addSingleAddBtn("+   添加校内职务", mSchoolOfficeLayout, mSchoolOfficeTag);
	}

	/**
	 * 编辑校内职务后返回
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void toEditSchoolOfficeReturn(Intent data) {
		mSchoolOfficeLists = (ArrayList<EducationBGBean>) data
				.getSerializableExtra("mSchoolOfficeLists");
		updateSchoolOfficeFlowShow();
	}

	/**
	 * 获取证书列表
	 */
	public void getCertificateList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETCERTIFICATELIST,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						mQequestCertificate = true;
						getCertificateListSuccess(response);
						if (ifStopLoding()) {
							setNolmalShow();
							dissMissProgress();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取证书列表成功
	 * 
	 * @param response
	 */
	public void getCertificateListSuccess(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			} else {
				setCertificateShow(httpRes.getReturnData());
			}
		} catch (Exception e) {
		}
	}

	public void setCertificateShow(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			addSingleAddBtn("+   添加证书", mCertificateLayout, mCertificateTag);
			return;
		}
		mCertificateLists = TransFormModel.getCertificateBeanList(resultData);
		updateCertificateFlowShow();
	}

	public void updateCertificateFlowShow() {
		mCertificateLayout.removeAllViews();
		if (null == mCertificateLists || mCertificateLists.isEmpty()) {
			addSingleAddBtn("+   添加证书", mCertificateLayout, mCertificateTag);
			return;
		}
		for (int i = 0; i < mCertificateLists.size(); i++) {
			CertificateBean certificateBean = mCertificateLists.get(i);
			RefersBean bean = new RefersBean(certificateBean, i,
					mCertificateTag);
			View view = addSingelFlowItem(bean);
			mCertificateLayout.addView(view);
		}
		addSingleAddBtn("+   添加证书", mCertificateLayout, mCertificateTag);
	}

	/**
	 * 编辑证书后返回
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void toCertificateReturn(Intent data) {
		mCertificateLists = (ArrayList<CertificateBean>) data
				.getSerializableExtra("mCertificateLists");
		updateCertificateFlowShow();
	}

	/**
	 * 编辑证书后
	 * 
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void toEditCertificateReturn(Intent data) {
		mCertificateLists = (ArrayList<CertificateBean>) data
				.getSerializableExtra("mCertificateLists");
		updateIntershipExperienceFlowShow();
	}

	/**
	 * 荣誉列表
	 */
	public void getHonorList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETHONORLIST, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						mRequestHonor = true;
						getHonorListSuccess(response);
						if (ifStopLoding()) {
							setNolmalShow();
							dissMissProgress();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void getHonorListSuccess(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			} else {
				setHonorShow(httpRes.getReturnData());
			}
		} catch (Exception e) {
		}
	}

	public void setHonorShow(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			addSingleAddBtn("+   添加荣誉", mHonorLayout, mHonorTag);
			return;
		}
		mHonorLists = TransFormModel.getEducationBGList(resultData);
		updateHonorFlowShow();
	}

	/*
	 * 编辑荣誉返回
	 */
	@SuppressWarnings("unchecked")
	public void toHonorReturn(Intent data) {
		mHonorLists = (ArrayList<EducationBGBean>) data
				.getSerializableExtra("mHonorLists");
		updateHonorFlowShow();
	}

	/**
	 * 更新荣誉的显示
	 */
	public void updateHonorFlowShow() {
		mHonorLayout.removeAllViews();
		if (null == mHonorLists || mHonorLists.isEmpty()) {
			addSingleAddBtn("+   添加荣誉", mHonorLayout, mHonorTag);
			return;
		}
		for (int i = 0; i < mHonorLists.size(); i++) {
			EducationBGBean educationbg = mHonorLists.get(i);
			RefersBean bean = new RefersBean(educationbg, i, mHonorTag);
			View view = addSingelFlowItem(bean);
			mHonorLayout.addView(view);
		}
		addSingleAddBtn("+   添加荣誉", mHonorLayout, mHonorTag);
	}

	public void getHRList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETHTDATA_URL, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						mRequestHR = true;
						getHRListSuccess(response);
						if (ifStopLoding()) {
							setNolmalShow();
							dissMissProgress();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void getHRListSuccess(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			} else {
				setHRShow(httpRes.getReturnData());
			}
		} catch (Exception e) {
		}
	}

	public void setHRShow(String resultData) throws JSONException {
		JSONObject json = new JSONObject(resultData);
		String hrContent = json.getString("dear_hr");
		if (StringUtils.isEmpty(hrContent)) {
			addSingleAddBtn("+   Dear HR", mHRlayout, mHRTag);
			return;
		}
		mDearHr = hrContent;
		updateHRFlowShow();
	}

	public void updateHRFlowShow() {
		mHRlayout.removeAllViews();
		RefersBean bean = new RefersBean(mDearHr, -1, mHRTag);
		View view = addSingelFlowItem(bean);
		mHRlayout.addView(view);
	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("jianliEdit"); // 简历编辑：jianliEdit
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("jianliEdit");
		MobclickAgent.onPause(this);
	}

}

package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CertificateBean;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.EducationBGBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InternshipIntentionBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.ResumeBean;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DateUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 预览简历
 * 
 * @author ronger
 * @date:2015-11-6 下午4:19:26
 */
@SuppressLint("InflateParams")
public class MinePreviewResumeActivity extends TitleBaseActivity {
	private ImageView mImage;
	private TextView mStudyInfo;
	private TextView mNameOfSex;
	// 基本信息
	private TextView mBirthText, mAddressText, mMobileText, mEmailText, mIdentityCardText, mPoliticsstatusText;
	private LinearLayout mBirthLayout, mAddressLayout, mMobileLayout, mEmailLayout, mIdentityCardLayout,
			mPoliticsstatusLayout;
	// 实习意向
	private TextView mExpectAddressText, mExpectDaysText, mExpectPostTypeText, mExpectSalaryText, mExpectStageText;
	// 教育经历
	private LinearLayout mEducationLayout;
	private LinearLayout mEducationTitleLayout;
	// 实习经历
	private LinearLayout mInternshipExperienceLayout;
	private LinearLayout mInternshipExperienceTitleLayout;
	// 校园经历(课外活动+校内职务)
	private LinearLayout mSchoolExpericeLayout;
	private LinearLayout mSchoolExpericeTitleLayout;
	// 荣誉和证书
	private LinearLayout mCertificateofHonorLayout;
	private LinearLayout mCertificateofHonorTitleLayout;
	// 人事
	private TextView mHRlayout;
	private LinearLayout mHrTitleLayout;
	private ResumeBean mResumeInfo;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, MinePreviewResumeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = true;
		BaseApplication.mApp.mCenterTaskActivitys.add(this);
		setContentView(R.layout.mine_previewresume_layout);
		setRequestParamsPre(TAG);
		setBroadcastReceiver();
		baseRegisterIntentFilter(Contants.PREVIEW_RESUME_UPDATE_ACTION);
		System.out.println("预览简历=====");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		System.out.println("onnewintent预览简历=====");
	}
	
	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("我的简历");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mImage = (ImageView) findViewById(R.id.image_top);
		mStudyInfo = (TextView) findViewById(R.id.text_studyinfo);
		mNameOfSex = (TextView) findViewById(R.id.text_name);
		initBasicInfo();
		initInternshipIntention();
		initOther();
	}

	@Override
	public void toRequestServer(Intent intent) {
		if (intent.getAction().equals(Contants.PREVIEW_RESUME_UPDATE_ACTION)) {
			setRequestParamsPre(TAG);
		}
	}

	/**
	 * 基本信息
	 */
	public void initBasicInfo() {
		mBirthText = (TextView) findViewById(R.id.text_preview_birthvalue);
		mAddressText = (TextView) findViewById(R.id.text_preview_addressvalue);
		mMobileText = (TextView) findViewById(R.id.text_preview_contactmobile_value);
		mEmailText = (TextView) findViewById(R.id.text_preview_contactemail_value);
		mIdentityCardText = (TextView) findViewById(R.id.text_preview_indentitycard_value);
		mPoliticsstatusText = (TextView) findViewById(R.id.text_preview_politicsstatus_value);
		mBirthLayout = (LinearLayout) findViewById(R.id.layout_birth);
		mAddressLayout = (LinearLayout) findViewById(R.id.layout_address);
		mMobileLayout = (LinearLayout) findViewById(R.id.layout_contactmobile);
		mEmailLayout = (LinearLayout) findViewById(R.id.layout_contactemail);
		mIdentityCardLayout = (LinearLayout) findViewById(R.id.layout_indentitycard);
		mPoliticsstatusLayout = (LinearLayout) findViewById(R.id.layout_politicsstatus);
	}

	/**
	 * 实习意向
	 */
	public void initInternshipIntention() {
		mExpectAddressText = (TextView) findViewById(R.id.text_expect_address_value);
		mExpectDaysText = (TextView) findViewById(R.id.text_expect_days_value);
		mExpectPostTypeText = (TextView) findViewById(R.id.text_expect_posttype_value);
		mExpectSalaryText = (TextView) findViewById(R.id.text_expect_salary_value);
		mExpectStageText = (TextView) findViewById(R.id.text_expect_stage_value);
	}

	/**
	 * 其他需要动态添加的数据
	 */
	public void initOther() {
		mEducationLayout = (LinearLayout) findViewById(R.id.layout_education_group);
		mEducationTitleLayout = (LinearLayout) findViewById(R.id.layout_education_experience);
		mInternshipExperienceTitleLayout = (LinearLayout) findViewById(R.id.layout_internship_experience);
		mInternshipExperienceLayout = (LinearLayout) findViewById(R.id.layout_internship_group);
		mSchoolExpericeLayout = (LinearLayout) findViewById(R.id.layout_school_group);
		mSchoolExpericeTitleLayout = (LinearLayout) findViewById(R.id.layout_school_experience);
		mCertificateofHonorLayout = (LinearLayout) findViewById(R.id.layout_certificateofhonor_group);
		mCertificateofHonorTitleLayout = (LinearLayout) findViewById(R.id.layout_certificateofhonor);
		mHrTitleLayout = (LinearLayout) findViewById(R.id.layout_dearhr);
		mHRlayout = (TextView) findViewById(R.id.text_dearhr_group);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
		}
		if (v.getId() == R.id.rl_edit_resume) {
			finish();
			ResumeEditActivity.startAction(this);
		}
		if (v.getId() == R.id.button_again) {
			setRetryRequest();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == Finals.REQUESTCODE_ONE) {
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		getResumeInfo();
	}

	/**
	 * 获取简历信息
	 */
	public void getResumeInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.GETRESUMEINFO_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			isFirstJoin = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			dissMissProgress();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setNolmalShow();
			mResumeInfo = TransFormModel.getResumeBean(httpRes.getReturnData());
			setViewShow();
		} catch (Exception e) {
			setDebugLog(e);
			dissMissProgress();
			exceptionToast();
		}
	}

	/**
	 * 显示
	 */
	public void setViewShow() {
		setBasicShow();
		setIntershipIntention();
		setEducationListShow();
		setInternshipExperienceListShow();
		setSchoolExperienceListShow();
		setCertificateofHonorListShow();
	}

	/**
	 * 基本信息展示
	 */
	public void setBasicShow() {
		UserInfoBean userInfoBean = mResumeInfo.getBasic();
		if (null == userInfoBean)
			return;
		mNameOfSex.setText(NumberUtils.getString(userInfoBean.getName(), CacheUtils.getLoginAccount(this)));
		String gender = userInfoBean.getGender();
		if (StringUtils.isNotEmpty(gender)) {
			Drawable right = getResources().getDrawable(gender.equals("2") ? R.drawable.female : R.drawable.male);
			right.setBounds(0, 0, right.getMinimumWidth(), right.getMinimumHeight());
			mNameOfSex.setCompoundDrawables(null, null, right, null);
		}
		String educationName = "";
		if (StringUtils.isNotEmpty(userInfoBean.getEducation())) {
			String education = userInfoBean.getEducation();
			if (education.equals("1"))
				educationName = "专科";
			if (education.equals("2"))
				educationName = "本科";
			if (education.equals("3"))
				educationName = "硕士";
			if (education.equals("4"))
				educationName = "博士";
		}
		String schoolName = NumberUtils.getString(userInfoBean.getGraduate_school(), "");
		String majorName = "";
//		MapFormatBean mapFormatBean = userInfoBean.getMajor_type();
//		if (null != mapFormatBean) {
//			majorName = NumberUtils.getString(mapFormatBean.getValue(), "");
//		}
		//获取专业名称
		majorName = NumberUtils.getString(userInfoBean.getDetail_major(), "");
		StringBuilder studySB = new StringBuilder();
		studySB.append(StringUtils.isNotEmpty(schoolName) ? schoolName + " | " : "");
		studySB.append(StringUtils.isNotEmpty(educationName) ? educationName + " | " : "");
		studySB.append(StringUtils.isNotEmpty(majorName) ? majorName : "");
		String studyInfoStr = studySB.toString();
		studyInfoStr = NumberUtils.getPinString(studyInfoStr, "|");
		mStudyInfo.setText(studyInfoStr);
		if (StringUtils.isEmpty(userInfoBean.getBirth_date())) {
			mBirthLayout.setVisibility(View.GONE);
		} else {
			mBirthText.setText(NumberUtils.getReplaceStr(userInfoBean.getBirth_date(), "-", ".", ""));
		}
		CityBean cityBean = userInfoBean.getLiving_city();
		if (null == cityBean || StringUtils.isEmpty(cityBean.getRegion_name())) {
			mAddressLayout.setVisibility(View.GONE);
		} else {
			mAddressText.setText(cityBean.getRegion_name());
		}
		if (StringUtils.isEmpty(userInfoBean.getContact_mobile())) {
			mMobileLayout.setVisibility(View.GONE);
		} else {
			mMobileText.setText(NumberUtils.getString(userInfoBean.getContact_mobile(), ""));
		}
		if (StringUtils.isEmpty(userInfoBean.getContact_mobile())) {
			mEmailLayout.setVisibility(View.GONE);
		} else {
			mEmailText.setText(NumberUtils.getString(userInfoBean.getContact_email(), ""));
		}
		if (StringUtils.isEmpty(userInfoBean.getId_number())) {
			mIdentityCardLayout.setVisibility(View.GONE);
		} else {
			mIdentityCardText.setText(NumberUtils.getString(userInfoBean.getId_number(), ""));
		}
		if (StringUtils.isEmpty(userInfoBean.getPolitics_status())) {
			mPoliticsstatusLayout.setVisibility(View.GONE);
		} else {
			String politicsStatus = NumberUtils.getString(userInfoBean.getPolitics_status(), "2");
			mPoliticsstatusText.setText(politicsStatus.equals("1") ? "党员" : "非党员");
		}
		setCirImage(mImage, userInfoBean.getAvatar());
		setDearHRShow(userInfoBean);
	}

	/**
	 * 实习意向
	 */
	public void setIntershipIntention() {
		InternshipIntentionBean expect = mResumeInfo.getExpect();
		if (null == expect) {
			return;
		}
		mExpectAddressText.setText(setAreaShow(expect.getExpect_city()));
		String day = expect.getWeek_available();
		mExpectDaysText.setText(getEveryweekDay(day));
		MapFormatBean category = expect.getExpect_category();
		if (null != category) {
			mExpectPostTypeText.setText(NumberUtils.getString(category.getValue(), ""));
		}
		MapFormatBean salary = expect.getSalary_range();
		if (null != salary) {
			mExpectSalaryText.setText(NumberUtils.getString(salary.getValue(), ""));
		}
		if (StringUtils.isEmpty(expect.getPeriod_start()) || StringUtils.isEmpty(expect.getPeriod_finish()))
			return;
		if (expect.getPeriod_start().equals("0000.00.00") || expect.getPeriod_start().equals("0000-00-00")) {
			mExpectStageText.setText("对时间没有要求，可快速到岗");
		} else
			mExpectStageText.setText(expect.getPeriod_start() + "-" + expect.getPeriod_finish());
	}

	/**
	 * 设置实习地区的显示
	 */
	public String setAreaShow(ArrayList<CityBean> mSelectCitys) {
		if (null == mSelectCitys || mSelectCitys.isEmpty())
			return "";
		StringBuilder selectCitySB = new StringBuilder();
		for (int i = 0; i < mSelectCitys.size(); i++) {
			CityBean cityBean = mSelectCitys.get(i);
			selectCitySB.append(cityBean.getRegion_name());
			if (i != mSelectCitys.size())
				selectCitySB.append("	");
		}
		return selectCitySB.toString();
	}

	public String getEveryweekDay(String day) {
		if (StringUtils.isNotEmpty(day)) {
			return day.contains("天") ? day : day + "天";
		}
		return "";
	}

	// private void setCirImage(final ImageView headImg, final String url) {
	// ImageRequest imgRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
	// @Override
	// public void onResponse(Bitmap arg0) {
	// // 在这里设置头像
	// MakeCircleImg cirImg = new MakeCircleImg();
	// headImg.setImageBitmap(cirImg.creatCircle(arg0));
	// }
	// }, 300, 200, Config.ARGB_8888, new ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError arg0) {
	// }
	// });
	// BaseApplication.mApp.addToRequestQueue(imgRequest, TAG);
	// }

	/**
	 * 设置教育经历显示
	 */
	private void setEducationListShow() {
		ArrayList<EducationBGBean> education = mResumeInfo.getEducation();
		if (null == education || education.isEmpty()) {
			mEducationLayout.setVisibility(View.GONE);
			mEducationTitleLayout.setVisibility(View.GONE);
			return;
		}
		mEducationLayout.removeAllViews();
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i = 0; i < education.size(); i++) {
			View view = mInflater.inflate(R.layout.preview_resume_education_item, null);
			View line = view.findViewById(R.id.view_dash_line);
			if (i == education.size() - 1) {
				line.setVisibility(View.GONE);
			}
			EducationBGBean bean = education.get(i);
			setTextViewShow(R.id.text_education_schoolname, view, bean.getSchool_name());
			if (StringUtils.isNotEmpty(bean.getStart_time()) && StringUtils.isNotEmpty(bean.getFinish_time())) {
				String time = DateUtils.getYearAndMonthFormatTime(bean.getStart_time(), ".", "\\.") + "-"
						+ DateUtils.getYearAndMonthFormatTime(bean.getFinish_time(), ".", "\\.");
				setTextViewShow(R.id.text_education_date, view, time);
			}
			MapFormatBean map = bean.getDegree();
			setTextViewShow(R.id.text_diploma, view, map.getValue());
			setTextViewShow(R.id.text_majorname, view, bean.getMajor_title());
			mEducationLayout.addView(view);
		}
	}

	/**
	 * 设置实习经历的显示
	 */
	private void setInternshipExperienceListShow() {
		ArrayList<EducationBGBean> internship = mResumeInfo.getPractice();
		if (null == internship || internship.isEmpty()) {
			mInternshipExperienceLayout.setVisibility(View.GONE);
			mInternshipExperienceTitleLayout.setVisibility(View.GONE);
			return;
		}
		mInternshipExperienceLayout.removeAllViews();
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i = 0; i < internship.size(); i++) {
			View view = mInflater.inflate(R.layout.preview_resume_internshipexperience_item, null);
			View line = view.findViewById(R.id.view_dash_line);
			if (i == internship.size() - 1) {
				line.setVisibility(View.GONE);
			}
			EducationBGBean bean = internship.get(i);
			setTextViewShow(R.id.text_education_schoolname, view, bean.getQuarters());
			if (StringUtils.isNotEmpty(bean.getStart_time()) && StringUtils.isNotEmpty(bean.getFinish_time())) {
				String time = DateUtils.getYearAndMonthFormatTime(bean.getStart_time(), ".", "\\.") + "-"
						+ DateUtils.getYearAndMonthFormatTime(bean.getFinish_time(), ".", "\\.");
				setTextViewShow(R.id.text_education_date, view, time);
			}
			setTextViewShow(R.id.text_commpany_name, view, bean.getOrganization());
			setTextViewShow(R.id.layout_commpany_desc, view, bean.getContent());
			mInternshipExperienceLayout.addView(view);
		}
	}

	/**
	 * 设置校园经历的显示:课外活动+校内职务
	 */
	private void setSchoolExperienceListShow() {
		ArrayList<EducationBGBean> activity = mResumeInfo.getActivity();
		ArrayList<EducationBGBean> school = mResumeInfo.getSchool();
		ArrayList<EducationBGBean> allSchool = null;
		if ((null == activity || activity.isEmpty()) && (null == school || school.isEmpty())) {
			mSchoolExpericeLayout.setVisibility(View.GONE);
			mSchoolExpericeTitleLayout.setVisibility(View.GONE);
			return;
		}
		if ((null == activity || activity.isEmpty()) && (null != school && school.size() > 0)) {
			allSchool = school;
		} else {
			allSchool = new ArrayList<EducationBGBean>();
			allSchool.addAll(activity);
			allSchool.addAll(school);
		}
		mSchoolExpericeLayout.removeAllViews();
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i = 0; i < allSchool.size(); i++) {
			View view = mInflater.inflate(R.layout.preview_resume_school_experience_item, null);
			View line = view.findViewById(R.id.view_dash_line);
			if (i == allSchool.size() - 1) {
				line.setVisibility(View.GONE);
			}
			EducationBGBean bean = allSchool.get(i);
			String preiod = bean.getPeriod();
			// 校内职务
			if (StringUtils.isEmpty(preiod)) {
				if (StringUtils.isNotEmpty(bean.getStart_time()) && StringUtils.isNotEmpty(bean.getFinish_time())) {
					String time = DateUtils.getYearAndMonthFormatTime(bean.getStart_time(), ".", "\\.") + "-"
							+ DateUtils.getYearAndMonthFormatTime(bean.getFinish_time(), ".", "\\.");
					setTextViewShow(R.id.text_education_schoolname, view, time);
				}
				setTextViewShow(R.id.text_education_date, view, bean.getJob_title());
				setTextViewShow(R.id.layout_commpany_desc, view, bean.getDescription());
			} else {
				setTextViewShow(R.id.text_education_schoolname, view,
						DateUtils.getYearAndMonthFormatTime(bean.getPeriod(), ".", "\\."));
				setTextViewShow(R.id.text_education_date, view, bean.getDescription());
				setTextViewGon(R.id.layout_commpany_desc, view, View.GONE);
			}
			mSchoolExpericeLayout.addView(view);
		}
	}

	/**
	 * 设置荣誉+证书
	 */
	private void setCertificateofHonorListShow() {
		ArrayList<CertificateBean> cert = mResumeInfo.getCert();
		ArrayList<EducationBGBean> prize = mResumeInfo.getPrize();
		if ((null == cert || cert.isEmpty()) && (null == prize || prize.isEmpty())) {
			mCertificateofHonorLayout.setVisibility(View.GONE);
			mCertificateofHonorTitleLayout.setVisibility(View.GONE);
			return;
		}
		mCertificateofHonorLayout.removeAllViews();
		int count = 0;
		// 证书
		if ((null == prize || prize.isEmpty()) && (null != cert && cert.size() > 0)) {
			count = cert.size();
			setCertificateListShow(mCertificateofHonorLayout, cert, count);
			return;
		}
		// 荣誉
		if ((null == cert || cert.isEmpty()) && (null != prize && prize.size() > 0)) {
			count = prize.size();
			setHonorListShow(mCertificateofHonorLayout, prize, count);
			return;
		}
		// // 证书
		// if ((null == cert || cert.isEmpty()) && (null != prize && prize.size() > 0)) {
		// count = prize.size();
		// setCertificateListShow(mCertificateofHonorLayout, cert, count);
		// return;
		// }
		// // 荣誉
		// if ((null == prize || prize.isEmpty()) && (null != cert && cert.size() > 0)) {
		// count = cert.size();
		// setHonorListShow(mCertificateofHonorLayout, prize, count);
		// return;
		// }
		// 荣誉+证书
		count = prize.size() + cert.size();
		setCertificateListShow(mCertificateofHonorLayout, cert, count);
		setHonorListShow(mCertificateofHonorLayout, prize, count);

	}

	/**
	 * 荣誉
	 * 
	 * @param layout
	 * @param prize
	 */
	public void setHonorListShow(LinearLayout layout, ArrayList<EducationBGBean> prize, int count) {
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i = 0; i < prize.size(); i++) {
			View view = mInflater.inflate(R.layout.preview_resume_school_experience_item, null);
			View line = view.findViewById(R.id.view_dash_line);
			// 有荣誉加证书:最后一个荣誉item的线不显示，若只有荣誉，荣誉最后一个Item的线也不显示
			if (i == prize.size() - 1) {
				line.setVisibility(View.GONE);
			}
			EducationBGBean bean = prize.get(i);
			// 课外活动
			setTextViewShow(R.id.text_education_schoolname, view,
					DateUtils.getYearAndMonthFormatTime(bean.getPeriod(), ".", "\\."));
			setTextViewShow(R.id.text_education_date, view, bean.getDescription());
			setTextViewGon(R.id.layout_commpany_desc, view, View.GONE);
			layout.addView(view);
		}
	}

	/**
	 * 证书
	 * 
	 * @param layout
	 * @param prize
	 */
	public void setCertificateListShow(LinearLayout layout, ArrayList<CertificateBean> cert, int count) {
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i = 0; i < cert.size(); i++) {
			View view = mInflater.inflate(R.layout.preview_resume_school_experience_item, null);
			View line = view.findViewById(R.id.view_dash_line);
			// 有荣誉加证书:最后一个证书item的线显示，若只有证书，证书最后一个Item的线也不显示
			if ((cert.size() == count) && i == cert.size() - 1) {
				line.setVisibility(View.GONE);
			}
			CertificateBean bean = cert.get(i);
			setTextViewShow(R.id.text_education_schoolname, view,
					DateUtils.getYearAndMonthFormatTime(bean.getFinish_time(), ".", "\\."));
			setTextViewShow(R.id.text_education_date, view, bean.getCertificate_name());
			int status = NumberUtils.getInt(bean.getStatus(), 1);
			String strStatus = status == 1 ? "在读" : "通过";
			setTextViewShow(R.id.layout_commpany_desc, view, "证书状态:  " + strStatus);
			layout.addView(view);
		}
	}

	/**
	 * dearhr的显示
	 */
	public void setDearHRShow(UserInfoBean userInfoBean) {
		if (StringUtils.isEmpty(userInfoBean.getDear_hr())) {
			mHRlayout.setVisibility(View.GONE);
			mHrTitleLayout.setVisibility(View.GONE);
			return;
		}
		mHRlayout.setText(userInfoBean.getDear_hr());
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
		MobclickAgent.onPageStart("preJianli"); //简历预览：preJianli
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("preJianli"); 
		MobclickAgent.onPause(this);
	}
}

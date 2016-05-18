package com.shixi.gaodun.model.domain;

import com.shixi.gaodun.model.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 基本信息
 * 
 * @author ronger
 * @date:2015-10-26 下午3:48:00
 */
public class UserInfoBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String account_id;
	private String name;
	private String contact_email;
	private String mobile;
	private String contact_mobile;
	private String gender;
	private String sexName;
	private String education;
	private String graduate_school;
	private MapFormatBean major_type;
	// // // 专业分类名称
	// private String major_type_name;
	// // 专业分类id
	// private String major_type_id;
	// private MajorClassifyBean major_type_bean;
	private String detail_major;
	private String avatar;
	private String create_time;
	private String complete_rate;
	private String birth_date;
	private String graduate_year;
	private String politics_status;
	private String politics_status_name;
	private String id_number;
	private CityBean living_province;
	private CityBean living_city;
	private String resumeViewNum;
	private String resumePostStatus;
	private String dear_hr;
	private String current_grade;
	private String resume_id;
	private ArrayList<CityBean> expect_city;// 实习意向城市
	private String week_available;// 每周可实习天数
	private MapFormatBean salary_range;// 期望日薪
	private MapFormatBean expect_category;// 期望实习岗位
	private String period_start;// 实习开始时间
	private String period_finish;// 实习结束时间

	public ArrayList<CityBean> getExpect_city() {
		return expect_city;
	}

	public void setExpect_city(ArrayList<CityBean> expect_city) {
		this.expect_city = expect_city;
	}

	public String getWeek_available() {
		return week_available;
	}

	public void setWeek_available(String week_available) {
		this.week_available = week_available;
	}

	public MapFormatBean getSalary_range() {
		return salary_range;
	}

	public void setSalary_range(MapFormatBean salary_range) {
		this.salary_range = salary_range;
	}

	public MapFormatBean getExpect_category() {
		return expect_category;
	}

	public void setExpect_category(MapFormatBean expect_category) {
		this.expect_category = expect_category;
	}

	public String getPeriod_start() {
		return period_start;
	}

	public void setPeriod_start(String period_start) {
		this.period_start = period_start;
	}

	public String getPeriod_finish() {
		return period_finish;
	}

	public void setPeriod_finish(String period_finish) {
		this.period_finish = period_finish;
	}

	public String getResume_id() {
		return resume_id;
	}

	public void setResume_id(String resume_id) {
		this.resume_id = resume_id;
	}

	private InternshipIntentionBean mIntershipBean;// 实习意向

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContact_email() {
		return contact_email;
	}

	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}

	public String getMobile() {
		// 后期因为接口改了字段名所以这样写，避免其他字段还是用原来的字段名称，防止报错
		if (StringUtils.isNotEmpty(contact_mobile))
			return contact_mobile;
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getGraduate_school() {
		return graduate_school;
	}

	public void setGraduate_school(String graduate_school) {
		this.graduate_school = graduate_school;
	}

	public String getDetail_major() {
		return detail_major;
	}

	public void setDetail_major(String detail_major) {
		this.detail_major = detail_major;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getComplete_rate() {
		return complete_rate;
	}

	public void setComplete_rate(String complete_rate) {
		this.complete_rate = complete_rate;
	}

	public String getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}

	public String getGraduate_year() {
		return graduate_year;
	}

	public void setGraduate_year(String graduate_year) {
		this.graduate_year = graduate_year;
	}

	public String getPolitics_status() {
		return politics_status;
	}

	public void setPolitics_status(String politics_status) {
		this.politics_status = politics_status;
	}

	public String getId_number() {
		return id_number;
	}

	public void setId_number(String id_number) {
		this.id_number = id_number;
	}

	public String getResumeViewNum() {
		return resumeViewNum;
	}

	public void setResumeViewNum(String resumeViewNum) {
		this.resumeViewNum = resumeViewNum;
	}

	public String getResumePostStatus() {
		return resumePostStatus;
	}

	public void setResumePostStatus(String resumePostStatus) {
		this.resumePostStatus = resumePostStatus;
	}

	public String getDear_hr() {
		return dear_hr;
	}

	public void setDear_hr(String dear_hr) {
		this.dear_hr = dear_hr;
	}

	// public String getMajor_type_name() {
	// return major_type_name;
	// }
	//
	// public void setMajor_type_name(String major_type_name) {
	// this.major_type_name = major_type_name;
	// }
	//
	// public String getMajor_type_id() {
	// return major_type_id;
	// }
	//
	// public void setMajor_type_id(String major_type_id) {
	// this.major_type_id = major_type_id;
	// }

	public CityBean getLiving_province() {
		return living_province;
	}

	public void setLiving_province(CityBean living_province) {
		this.living_province = living_province;
	}

	public CityBean getLiving_city() {
		return living_city;
	}

	public void setLiving_city(CityBean living_city) {
		this.living_city = living_city;
	}

	// public MajorClassifyBean getMajor_type_bean() {
	// return major_type_bean;
	// }
	//
	// public void setMajor_type_bean(MajorClassifyBean major_type_bean) {
	// this.major_type_bean = major_type_bean;
	// }

	public String getSexName() {
		return sexName;
	}

	public void setSexName(String sexName) {
		this.sexName = sexName;
	}

	public String getPolitics_status_name() {
		return politics_status_name;
	}

	public void setPolitics_status_name(String politics_status_name) {
		this.politics_status_name = politics_status_name;
	}

	public MapFormatBean getMajor_type() {
		return major_type;
	}

	public void setMajor_type(MapFormatBean major_type) {
		this.major_type = major_type;
	}

	public String getContact_mobile() {
		return contact_mobile;
	}

	public void setContact_mobile(String contact_mobile) {
		this.contact_mobile = contact_mobile;
	}

	public String getCurrent_grade() {
		return current_grade;
	}

	public void setCurrent_grade(String current_grade) {
		this.current_grade = current_grade;
	}

	public InternshipIntentionBean getmIntershipBean() {
		return mIntershipBean;
	}

	public void setmIntershipBean(InternshipIntentionBean mIntershipBean) {
		this.mIntershipBean = mIntershipBean;
	}

}

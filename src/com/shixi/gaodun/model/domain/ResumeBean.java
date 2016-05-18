package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 简历信息
 * 
 * @author ronger
 * @date:2015-11-6 下午4:26:00
 */
public class ResumeBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 基本信息
	private UserInfoBean basic;
	// 实习意向
	private InternshipIntentionBean expect;
	// 教育列表
	private ArrayList<EducationBGBean> education;
	// 实习经历
	private ArrayList<EducationBGBean> practice;
	// 课外活动
	private ArrayList<EducationBGBean> activity;
	// 校内职务
	private ArrayList<EducationBGBean> school;
	// 证书
	private ArrayList<CertificateBean> cert;
	// 荣誉
	private ArrayList<EducationBGBean> prize;

	public UserInfoBean getBasic() {
		return basic;
	}

	public void setBasic(UserInfoBean basic) {
		this.basic = basic;
	}

	public InternshipIntentionBean getExpect() {
		return expect;
	}

	public void setExpect(InternshipIntentionBean expect) {
		this.expect = expect;
	}

	public ArrayList<EducationBGBean> getEducation() {
		return education;
	}

	public void setEducation(ArrayList<EducationBGBean> education) {
		this.education = education;
	}

	public ArrayList<EducationBGBean> getPractice() {
		return practice;
	}

	public void setPractice(ArrayList<EducationBGBean> practice) {
		this.practice = practice;
	}

	public ArrayList<EducationBGBean> getActivity() {
		return activity;
	}

	public void setActivity(ArrayList<EducationBGBean> activity) {
		this.activity = activity;
	}

	public ArrayList<EducationBGBean> getSchool() {
		return school;
	}

	public void setSchool(ArrayList<EducationBGBean> school) {
		this.school = school;
	}

	public ArrayList<CertificateBean> getCert() {
		return cert;
	}

	public void setCert(ArrayList<CertificateBean> cert) {
		this.cert = cert;
	}

	public ArrayList<EducationBGBean> getPrize() {
		return prize;
	}

	public void setPrize(ArrayList<EducationBGBean> prize) {
		this.prize = prize;
	}

}

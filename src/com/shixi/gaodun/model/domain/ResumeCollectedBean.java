package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 已收集简历
 * @author guolinyao
 * @date 2016年4月18日 上午11:10:23
 */
public class ResumeCollectedBean implements Serializable {
	private String name;//学生姓名
	private String login_email;// 邮箱
	private String mobile_type;// 手机号类型
	private String mobile;// 手机
	private String education;// 学历
	private String graduate_school;//毕业学校
	private String major_type;// 专业类型
	private String detail_major;// 专业
	private String send_time;// 投递时间
	private String create_time;// 投递或注册时间
	private String title;// 职位名
	private String enterprise_name;// 企业名
	private String post_id;// 职位id
	private String pid;// 投递id
	private String enterprise_id;//企业id
	private String item_type;//条目类型 1 邮箱信息或注册信息 2 基本信息或投递信息
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLogin_email() {
		return login_email;
	}
	public void setLogin_email(String login_email) {
		this.login_email = login_email;
	}
	public String getMobile_type() {
		return mobile_type;
	}
	public void setMobile_type(String mobile_type) {
		this.mobile_type = mobile_type;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getMajor_type() {
		return major_type;
	}
	public void setMajor_type(String major_type) {
		this.major_type = major_type;
	}
	public String getDetail_major() {
		return detail_major;
	}
	public void setDetail_major(String detail_major) {
		this.detail_major = detail_major;
	}
	public String getSend_time() {
		return send_time;
	}
	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getEnterprise_name() {
		return enterprise_name;
	}
	public void setEnterprise_name(String enterprise_name) {
		this.enterprise_name = enterprise_name;
	}
	public String getPost_id() {
		return post_id;
	}
	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getEnterprise_id() {
		return enterprise_id;
	}
	public void setEnterprise_id(String enterprise_id) {
		this.enterprise_id = enterprise_id;
	}
	public String getItem_type() {
		return item_type;
	}
	public void setItem_type(String item_type) {
		this.item_type = item_type;
	}


}

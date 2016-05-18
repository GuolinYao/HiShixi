package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 简历动态信息
 * 
 * @author ronger
 * @date:2015-12-5 下午1:01:16
 */
public class ResumeDynamicBean implements Serializable {
	private String pkid;// 主键ID
	private String status;// 投递状态
	private String post_status;// 职位状态 1 上架 2 下架
	private String create_time;// 处理时间或者查看时间
	private String enterprise_name;// 企业名
	private String post_title;// 职位名
	private String city_id;// 城市ID
	private String logo;// 企业logo
	private String post_id;// 职位ID
	private String enterprise_id;// 企业ID
	private String read_time;// 阅读时间
	private String deal_time;// 处理时间
	private String post_city;// 职位所在城市
	private String resumepost_status;// 投递状态描述,1:未查看,2:未处理,3:允许面试,4:拒绝面试,5:待定

	private String title;// 职位名
	private String full_name;// 企业名
	private String collect_status;// 收藏状态

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPost_status() {
		return post_status;
	}

	public void setPost_status(String post_status) {
		this.post_status = post_status;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getEnterprise_name() {
		return enterprise_name;
	}

	public void setEnterprise_name(String enterprise_name) {
		this.enterprise_name = enterprise_name;
	}

	public String getPost_title() {
		return post_title;
	}

	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}

	public String getCity_id() {
		return city_id;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getEnterprise_id() {
		return enterprise_id;
	}

	public void setEnterprise_id(String enterprise_id) {
		this.enterprise_id = enterprise_id;
	}

	public String getRead_time() {
		return read_time;
	}

	public void setRead_time(String read_time) {
		this.read_time = read_time;
	}

	public String getDeal_time() {
		return deal_time;
	}

	public void setDeal_time(String deal_time) {
		this.deal_time = deal_time;
	}

	public String getPost_city() {
		return post_city;
	}

	public void setPost_city(String post_city) {
		this.post_city = post_city;
	}

	public String getResumepost_status() {
		return resumepost_status;
	}

	public void setResumepost_status(String resumepost_status) {
		this.resumepost_status = resumepost_status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getCollect_status() {
		return collect_status;
	}

	public void setCollect_status(String collect_status) {
		this.collect_status = collect_status;
	}

}

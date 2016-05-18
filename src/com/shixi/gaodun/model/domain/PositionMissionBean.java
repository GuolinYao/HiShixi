package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 任务职位信息
 * 
 * @author guolinyao
 * @date 2016年3月3日 下午2:55:28
 */
public class PositionMissionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String create_time;
	private String education;
	private String enterprise_id;
	private String enterprise_name;
	private String graduate_year;
	private String industry_name;
	private String is_hot;
	private String keep_on;
	private String logo;
	private String major_wish;
	private String pkid;
	private String post_id;// 职位id
	private String share_url;// 猎头分享职位
	private List<PositionLabelBean> taglist;//职位标签

	public List<PositionLabelBean> getTaglist() {
		return taglist;
	}

	public void setTaglist(List<PositionLabelBean> taglist) {
		this.taglist = taglist;
	}

	public String getShare_url() {
		return share_url;
	}

	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	private String region_name;
	private String salary_range;
	private String scale_name;
	private String title;
	private String week_available;

	private String address;// 地址
	private String quota;// 名额数量
	private String description;// 职位描述
	private String major_title;//
	private String industry_id;
	private String category_name;
	private String scale_description;// 企业规模描述
	private String scale_title;// 企业规模
	private String enter_city_name;// 企业所在地
	private String resume_id;// 学生简历id
	private String resume_type;// 学生简历类型
	private String can_post;// 是否可投递
	private String collect_status;// 职位收藏状态
	private String complete_rate;// 中文简历完整度
	private String complete_rate_en;// 英文简历完整度

	private String post_city;// 职位所在地
	private String post_status;// 职位状态1 上架 2 已下架
	private String get_resume;// 已收集简历
	private String aim_resume;// 目标简历数

	public String getGet_resume() {
		return get_resume;
	}

	public void setGet_resume(String get_resume) {
		this.get_resume = get_resume;
	}

	public String getAim_resume() {
		return aim_resume;
	}

	public void setAim_resume(String aim_resume) {
		this.aim_resume = aim_resume;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getEnterprise_id() {
		return enterprise_id;
	}

	public void setEnterprise_id(String enterprise_id) {
		this.enterprise_id = enterprise_id;
	}

	public String getEnterprise_name() {
		return enterprise_name;
	}

	public void setEnterprise_name(String enterprise_name) {
		this.enterprise_name = enterprise_name;
	}

	public String getGraduate_year() {
		return graduate_year;
	}

	public void setGraduate_year(String graduate_year) {
		this.graduate_year = graduate_year;
	}

	public String getIndustry_name() {
		return industry_name;
	}

	public void setIndustry_name(String industry_name) {
		this.industry_name = industry_name;
	}

	public String getIs_hot() {
		return is_hot;
	}

	public void setIs_hot(String is_hot) {
		this.is_hot = is_hot;
	}

	public String getKeep_on() {
		return keep_on;
	}

	public void setKeep_on(String keep_on) {
		this.keep_on = keep_on;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMajor_wish() {
		return major_wish;
	}

	public void setMajor_wish(String major_wish) {
		this.major_wish = major_wish;
	}

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public String getSalary_range() {
		return salary_range;
	}

	public void setSalary_range(String salary_range) {
		this.salary_range = salary_range;
	}

	public String getScale_name() {
		return scale_name;
	}

	public void setScale_name(String scale_name) {
		this.scale_name = scale_name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWeek_available() {
		return week_available;
	}

	public void setWeek_available(String week_available) {
		this.week_available = week_available;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getQuota() {
		return quota;
	}

	public void setQuota(String quota) {
		this.quota = quota;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMajor_title() {
		return major_title;
	}

	public void setMajor_title(String major_title) {
		this.major_title = major_title;
	}

	public String getIndustry_id() {
		return industry_id;
	}

	public void setIndustry_id(String industry_id) {
		this.industry_id = industry_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getScale_description() {
		return scale_description;
	}

	public void setScale_description(String scale_description) {
		this.scale_description = scale_description;
	}

	public String getScale_title() {
		return scale_title;
	}

	public void setScale_title(String scale_title) {
		this.scale_title = scale_title;
	}

	public String getEnter_city_name() {
		return enter_city_name;
	}

	public void setEnter_city_name(String enter_city_name) {
		this.enter_city_name = enter_city_name;
	}

	public String getResume_id() {
		return resume_id;
	}

	public void setResume_id(String resume_id) {
		this.resume_id = resume_id;
	}

	public String getResume_type() {
		return resume_type;
	}

	public void setResume_type(String resume_type) {
		this.resume_type = resume_type;
	}

	public String getCan_post() {
		return can_post;
	}

	public void setCan_post(String can_post) {
		this.can_post = can_post;
	}

	public String getCollect_status() {
		return collect_status;
	}

	public void setCollect_status(String collect_status) {
		this.collect_status = collect_status;
	}

	public String getComplete_rate() {
		return complete_rate;
	}

	public void setComplete_rate(String complete_rate) {
		this.complete_rate = complete_rate;
	}

	public String getComplete_rate_en() {
		return complete_rate_en;
	}

	public void setComplete_rate_en(String complete_rate_en) {
		this.complete_rate_en = complete_rate_en;
	}

	public String getPost_city() {
		return post_city;
	}

	public void setPost_city(String post_city) {
		this.post_city = post_city;
	}

	public String getPost_status() {
		return post_status;
	}

	public void setPost_status(String post_status) {
		this.post_status = post_status;
	}


	public class PositionLabelBean implements Serializable{
		String title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
}

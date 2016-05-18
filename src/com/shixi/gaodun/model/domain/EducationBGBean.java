package com.shixi.gaodun.model.domain;

import java.io.Serializable;

import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 简历信息相关实体
 * 
 * @author ronger
 * @date:2015-11-3 下午2:16:26
 */
public class EducationBGBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pkid;
	private String start_time;
	private String finish_time;
	// 教育经历实体相关
	private String school_name;
	private String major_title;
	private String language_type;
	private String education;
	private String degreeName;
	private String degreeKey;
	// 实习经历相关
	private String organization;// 实习单位
	private String quarters;// 实习岗位
	private String content;// 实习内容
	// 课外活动相关
	private String period;// 活动日期
	private String description;// 活动描述
	// 校内职务
	private String job_title;
	private MapFormatBean degree;

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public String getStart_time() {
		if (StringUtils.isNotEmpty(start_time))
			start_time = start_time.replace("-", ".");
		return start_time;
	}

	public void setStart_time(String start_time) {
		// 若有-则换成显示需要用的.
		if (StringUtils.isNotEmpty(start_time))
			start_time = start_time.replace("-", ".");
		this.start_time = start_time;
	}

	public String getFinish_time() {
		if (StringUtils.isNotEmpty(finish_time))
			finish_time = finish_time.replace("-", ".");
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		if (StringUtils.isNotEmpty(finish_time))
			finish_time = finish_time.replace("-", ".");
		this.finish_time = finish_time;
	}

	public String getSchool_name() {
		return school_name;
	}

	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}

	public String getMajor_title() {
		return major_title;
	}

	public void setMajor_title(String major_title) {
		this.major_title = major_title;
	}

	public String getLanguage_type() {
		return language_type;
	}

	public void setLanguage_type(String language_type) {
		this.language_type = language_type;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getDegreeName() {
		return degreeName;
	}

	public void setDegreeName(String degreeName) {
		this.degreeName = degreeName;
	}

	public String getDegreeKey() {
		return degreeKey;
	}

	public void setDegreeKey(String degreeKey) {
		this.degreeKey = degreeKey;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getQuarters() {
		return quarters;
	}

	public void setQuarters(String quarters) {
		this.quarters = quarters;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPeriod() {
		if (StringUtils.isNotEmpty(period))
			period = period.replace("-", ".");
		return period;
	}

	public void setPeriod(String period) {
		if (StringUtils.isNotEmpty(period))
			period = period.replace("-", ".");
		this.period = period;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJob_title() {
		return job_title;
	}

	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}

	public MapFormatBean getDegree() {
		return degree;
	}

	public void setDegree(MapFormatBean degree) {
		this.degree = degree;
	}

}

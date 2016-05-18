package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 企业信息
 * 
 * @author ronger
 * @date:2015-11-12 下午1:55:47
 */
public class CompanyBean implements Serializable {
	private String logo;
	private String full_name;
	private String scale_id;
	private String industry_id;
	private String description;
	private String city_name;
	private String scale_title;
	private String scale_description;
	private String industry_title;
	private String editor_note;
	private String pkid;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getScale_id() {
		return scale_id;
	}

	public void setScale_id(String scale_id) {
		this.scale_id = scale_id;
	}

	public String getIndustry_id() {
		return industry_id;
	}

	public void setIndustry_id(String industry_id) {
		this.industry_id = industry_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getScale_title() {
		return scale_title;
	}

	public void setScale_title(String scale_title) {
		this.scale_title = scale_title;
	}

	public String getScale_description() {
		return scale_description;
	}

	public void setScale_description(String scale_description) {
		this.scale_description = scale_description;
	}

	public String getIndustry_title() {
		return industry_title;
	}

	public void setIndustry_title(String industry_title) {
		this.industry_title = industry_title;
	}

	public String getEditor_note() {
		return editor_note;
	}

	public void setEditor_note(String editor_note) {
		this.editor_note = editor_note;
	}

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

}

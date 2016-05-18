package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 地区
 * 
 * @author ronger
 * @date:2015-10-27 上午11:39:27
 */
public class CityBean implements Serializable {
	// 拼音首字母
	private String sortLetters;
	// 地区Id
	private String region_id;
	// 地区名称
	private String region_name;
	// 是否选中
	private boolean ifSelected;

	public CityBean(String region_id, String region_name, String sortLetters, boolean ifSelected) {
		this.region_id = region_id;
		this.region_name = region_name;
		this.sortLetters = sortLetters;
		this.ifSelected = isIfSelected();
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getRegion_id() {
		return region_id;
	}

	public void setRegion_id(String region_id) {
		this.region_id = region_id;
	}

	public String getRegion_name() {
		return region_name;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	public boolean isIfSelected() {
		return ifSelected;
	}

	public void setIfSelected(boolean ifSelected) {
		this.ifSelected = ifSelected;
	}

}

package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 专业分类
 * 
 * @author ronger
 * @date:2015-10-28 上午11:25:02
 */
public class MajorClassifyBean implements Serializable {
	private String pkid;
	private String title;
	private boolean ifSelected;

	public MajorClassifyBean(String pkid, String title) {
		this.pkid = pkid;
		this.title = title;
	}

	public MajorClassifyBean(String pkid, String title, boolean ifSelected) {
		this.pkid = pkid;
		this.title = title;
		this.ifSelected = ifSelected;
	}

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isIfSelected() {
		return ifSelected;
	}

	public void setIfSelected(boolean ifSelected) {
		this.ifSelected = ifSelected;
	}

}

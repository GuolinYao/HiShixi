package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 关键词
 * 
 * @author ronger
 * @date:2015-11-10 下午4:51:31
 */
public class KeyWordBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pkid;// 主键
	private String title;// 职位名称
	private String enterprise_id;// 企业ID
	private String key;// 筛选关键词 1 职位 2 企

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

	public String getEnterprise_id() {
		return enterprise_id;
	}

	public void setEnterprise_id(String enterprise_id) {
		this.enterprise_id = enterprise_id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}

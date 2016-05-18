package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 热搜词汇
 * 
 * @author ronger
 * @date:2015-11-9 下午3:13:49
 */
public class HotSearchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String type;// 1:企业,2:职位,3地点,4每周实习天数,5岗位分类,6企业行业,7学历
	private String value;
	private String range;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

}

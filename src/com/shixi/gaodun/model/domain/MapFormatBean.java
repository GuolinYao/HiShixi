package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 类键值队形式的bean 通用
 * 
 * @author ronger
 * @date:2015-11-5 下午1:24:14
 */
public class MapFormatBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String key;
	private String value;
	private boolean isSelected;

	public MapFormatBean(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public MapFormatBean() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}

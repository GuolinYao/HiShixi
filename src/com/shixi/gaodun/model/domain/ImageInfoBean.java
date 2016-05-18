package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 图片信息
 * 
 * @author ronger
 * @date:2015-11-27 下午4:11:37
 */
public class ImageInfoBean implements Serializable {
	private String value;
	private int width;
	private int height;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}

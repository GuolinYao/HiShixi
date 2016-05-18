package com.shixi.gaodun.model.domain;

/**
 * 主要用来对动态添加的布局起标示作用
 * 
 * @author ronger
 * @param <T>
 * @date:2015-11-3 下午3:42:58
 */
public class RefersBean {
	private int position = -1;
	private Object object;
	private String className;

	public RefersBean(Object object, int position, String className) {
		this.object = object;
		this.position = position;
		this.className = className;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}

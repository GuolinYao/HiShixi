package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 已经选中了的图片
 * 
 * @author ronger
 * @date:2015-11-25 下午1:49:45
 */
public class SelectedImageBean implements Serializable {
	private String path;// 图片的地址
	private int selectedPosition;// 选中图片时的顺序
	private boolean isSelected;// 是否被选中,主要用来和列表比较时用，与此处是否选中无关
	private int currentIndex;// 当前位置

	private int index;// 发帖页显示图片和删除图片时的索引位置,仅在flowlayout中用到

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public SelectedImageBean(String path, int selectedPosition, boolean isSelected) {
		this.path = path;
		this.selectedPosition = selectedPosition;
		this.isSelected = isSelected;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public SelectedImageBean(String path, boolean isSelected) {
		this.path = path;
		this.isSelected = isSelected;
	}

	public SelectedImageBean(String path, int selectedPosition) {
		this.path = path;
		this.isSelected = isSelected;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}

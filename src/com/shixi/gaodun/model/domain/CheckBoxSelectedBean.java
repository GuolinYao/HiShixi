package com.shixi.gaodun.model.domain;

import java.util.ArrayList;

/**
 * @author ronger
 * @date:2015-11-25 下午2:27:46
 */
public class CheckBoxSelectedBean {
	private boolean isSelected;
	private ArrayList<SelectedImageBean> selectedList;
	private int currentIndex;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public ArrayList<SelectedImageBean> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(ArrayList<SelectedImageBean> selectedList) {
		this.selectedList = selectedList;
	}

	public CheckBoxSelectedBean(boolean isSelected, ArrayList<SelectedImageBean> selectedList, int current) {
		super();
		this.isSelected = isSelected;
		this.selectedList = selectedList;
		this.currentIndex = current;
	}

	public CheckBoxSelectedBean(boolean isSelected, ArrayList<SelectedImageBean> selectedList) {
		super();
		this.isSelected = isSelected;
		this.selectedList = selectedList;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

}

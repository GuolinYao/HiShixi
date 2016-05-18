package com.shixi.gaodun.model.utils;

import java.util.ArrayList;

import com.shixi.gaodun.model.domain.CheckBoxSelectedBean;
import com.shixi.gaodun.model.domain.SelectedImageBean;

/**
 * 集合的工具类
 * 
 * @author ronger
 * @date:2015-11-25 下午1:52:35
 */
public class ListUitls {
	/**
	 * 图片初始时的选择
	 * 
	 * @param selectedPicture
	 * @param currentPath
	 * @return
	 */
	public static SelectedImageBean getImageIsSelected(ArrayList<SelectedImageBean> selectedPicture, String currentPath) {
		if (null == selectedPicture || selectedPicture.isEmpty())
			return new SelectedImageBean(null, -1, false);
		SelectedImageBean bean = new SelectedImageBean(null, -1, false);
		for (SelectedImageBean selectedImageBean : selectedPicture) {
			String path = selectedImageBean.getPath();
			if (path.equals(currentPath)) {
				bean = new SelectedImageBean(path, selectedImageBean.getSelectedPosition(), true);
				break;
			}
		}
		return bean;
	}

	/**
	 * 选中按钮的点击事件
	 * 
	 * 需要处理对已经选择过了的集合的所有selectedPosition都减1 对没有选择过的添加进入此集合，并且当前SelectedImageBean的selectedPosition为之前的集合大小+1
	 * 
	 * @param selectedPicture
	 * @param currentPath
	 * @return
	 */
	public static CheckBoxSelectedBean getSelectedIsContains(ArrayList<SelectedImageBean> selectedPicture,
			String currentPath) {
		CheckBoxSelectedBean bean = null;
		if (null == selectedPicture || selectedPicture.isEmpty()) {
			selectedPicture = new ArrayList<SelectedImageBean>(0);
		}
		boolean isGoon = true;
		ArrayList<SelectedImageBean> mSeletedBean = selectedPicture;
		for (int i = 0; i < selectedPicture.size(); i++) {
			SelectedImageBean selectedImageBean = selectedPicture.get(i);
			String path = selectedImageBean.getPath();
			if (path.equals(currentPath)) {
				int currentPosition = selectedImageBean.getSelectedPosition();
				// 已经选择过了，需要移除当前位置的SelectedImageBean
				mSeletedBean.remove(i);
				mSeletedBean = getRemoveSeletedLists(mSeletedBean, currentPosition);
				isGoon = false;
				bean = new CheckBoxSelectedBean(false, mSeletedBean);
				break;
			}
		}
		if (isGoon) {// 新增
			mSeletedBean = getSeletedLists(mSeletedBean, false, currentPath);
			bean = new CheckBoxSelectedBean(true, mSeletedBean, mSeletedBean.size());
		}
		return bean;
	}

	/**
	 * 新增
	 * 
	 * @param selected
	 * @param isContains
	 * @param currentPath
	 * @return
	 */
	public static ArrayList<SelectedImageBean> getSeletedLists(ArrayList<SelectedImageBean> selected,
			boolean isContains, String currentPath) {
		ArrayList<SelectedImageBean> mSeletedBean = selected;
		mSeletedBean.add(new SelectedImageBean(currentPath, selected.size() + 1, true));
		// if (isContains) {// 移除之前选中的后改变选中位置
		// mSeletedBean = new ArrayList<SelectedImageBean>(selected.size());
		// for (SelectedImageBean selectedImageBean : selected) {
		// mSeletedBean.add(new SelectedImageBean(selectedImageBean.getPath(), true));
		// }
		// } else {// 新增
		// mSeletedBean.add(new SelectedImageBean(currentPath, selected.size() + 1, true));
		// }
		return mSeletedBean;
	}

	/**
	 * 返回移除某一个已选中了的集合
	 * 
	 * @param selected
	 * @param currentPath
	 * @param setCureentIndex
	 * @return
	 */
	public static ArrayList<SelectedImageBean> getRemoveSeletedLists(ArrayList<SelectedImageBean> selected,
			int currentPosition) {
		ArrayList<SelectedImageBean> mSeletedBean = selected;
		mSeletedBean = new ArrayList<SelectedImageBean>(selected.size());
		for (SelectedImageBean selectedImageBean : selected) {
			int position = selectedImageBean.getSelectedPosition();
			// 若移除的图片的位置在现有的图片的后面，则现有的图片的位置不需要改变，否则，现有的图片的位置需要在原有的基础上-1
			if (position > currentPosition) {
				position = position - 1;
			}
			mSeletedBean.add(new SelectedImageBean(selectedImageBean.getPath(), position, true));

		}
		return mSeletedBean;
	}
}

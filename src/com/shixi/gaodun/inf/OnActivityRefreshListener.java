package com.shixi.gaodun.inf;

/**
 * @author ronger
 * @date:2015-12-7 上午11:40:44
 */
public interface OnActivityRefreshListener {
	public void onRefreshList();// 刷新列表

	public void onRefreshList(int index);

	public void onCheckClearBtnCanClick();// 检测是否可清空，部分使用
}

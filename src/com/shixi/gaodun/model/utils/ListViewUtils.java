package com.shixi.gaodun.model.utils;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListViewUtils {
	public static void setListViewHeight(AbsListView absListView, int lineNumber, int verticalSpace) {
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		ListAdapter mListAdapter = absListView.getAdapter();
		if (mListAdapter == null) {
			return;
		}
		int totalHeight = 0;
		int count = mListAdapter.getCount();
		ViewGroup.LayoutParams params = absListView.getLayoutParams();
		if (absListView instanceof ListView) {
			for (int i = 0; i < count; i++) {
				View listItem = mListAdapter.getView(i, null, absListView);
				listItem.measure(w, h);
				totalHeight += listItem.getMeasuredHeight();
			}
			if (count == 0) {
				View listItem = mListAdapter.getView(0, null, absListView);
				listItem.measure(w, h);
				totalHeight += listItem.getMeasuredHeight();
				params.height = totalHeight;
			} else {
				params.height = totalHeight + (((ListView) absListView).getDividerHeight() * (count - 1));
			}

		} else if (absListView instanceof GridView) {
			int remain = count % lineNumber;
			if (remain > 0) {
				remain = 1;
			}
			View listItem = mListAdapter.getView(0, null, absListView);
			listItem.measure(w, h);
			int line = count / lineNumber + remain;
			params.height = line * listItem.getMeasuredHeight() + (line + 1) * verticalSpace + verticalSpace;
		}

		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
		absListView.setLayoutParams(params);

	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
			listItem.measure(desiredWidth, 0);
			// listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		// if (listView.getHeaderViewsCount() != 0) {
		// totalHeight += 50;
		// }
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 获取listview的高度
	 * 
	 * @param listView
	 * @return
	 */
	public static int getListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return 0;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return 0;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
			listItem.measure(desiredWidth, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		return totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	}
}

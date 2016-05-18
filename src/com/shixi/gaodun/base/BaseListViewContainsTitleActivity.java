package com.shixi.gaodun.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.libpull.PullToRefreshListView;

/**
 * 下拉刷新的listview的基类，非特殊pullrefreshListview
 * 
 * @author ronger
 * @param <T>
 * @date:2015-12-4 上午11:56:33
 */
public abstract class BaseListViewContainsTitleActivity<T> extends BaseListViewActivity<T> {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.base_pullrefresh_listview);
	}

	@SuppressLint("InflateParams")
	@Override
	public void initView() {
		super.initView();
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.base_pullrefresh_listview);
		myListView = (ListView) mPullToRefreshListView.getRefreshableView();
		// mMainNoneDataLayout = (FrameLayout) findViewById(R.id.layout_nonedata);
	}

}

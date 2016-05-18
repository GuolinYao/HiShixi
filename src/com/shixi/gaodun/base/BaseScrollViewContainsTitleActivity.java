package com.shixi.gaodun.base;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.shixi.gaodun.R;
import com.shixi.gaodun.libpull.PullToRefreshScrollView;
import com.shixi.gaodun.view.ObservableScrollView;

/**
 * 下拉刷新的ScrollView的基类，含标题
 * 
 * @author ronger
 * @date:2015-12-4 上午11:56:10
 */
public abstract class BaseScrollViewContainsTitleActivity<T> extends BaseScrollViewActivity<T> {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.base_pullrefresh_scrollview);
	}

	@Override
	public void initView() {
		super.initView();
		mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.base_pullrefresh_scrollview);
		mScrollView = (ObservableScrollView) mPullToRefreshScrollView.getRefreshableView();
		mMainNoneDataLayout = (FrameLayout) findViewById(R.id.layout_nonedata);
	}
}

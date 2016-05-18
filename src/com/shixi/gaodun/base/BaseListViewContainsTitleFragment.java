package com.shixi.gaodun.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shixi.gaodun.R;
import com.shixi.gaodun.view.TitleHeaderBar;

/**
 * 带标题的刷新
 * 
 * @author ronger
 * @param <T>
 * @date:2015-12-7 下午4:00:41
 */
public abstract class BaseListViewContainsTitleFragment<T> extends BaseListViewFragment<T> {
	protected TitleHeaderBar mTitleHeaderBar;
	protected LinearLayout mContentContainer;

	/**
	 * 整体布局
	 */
	@Override
	protected int getFrameLayoutId() {
		return R.layout.base_content_frame_with_title_header;
	}

	/**
	 * 整体
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 整体容器
		ViewGroup view = (ViewGroup) inflater.inflate(getFrameLayoutId(), null);
		LinearLayout contentContainer = (LinearLayout) view.findViewById(R.id.content_frame_content);
		mTitleHeaderBar = (TitleHeaderBar) view.findViewById(R.id.content_frame_title_header);
		mTitleHeaderBar.setLeftOnClickListener(this);
		mContentContainer = contentContainer;
		// 内容
		View contentView = createView(inflater, view, savedInstanceState);
		contentView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
		contentContainer.addView(contentView);
		// 有标题的fragment错误（没有数据网络异常、服务器异常等）时的容器
		mMainNoneDataLayout = (FrameLayout) view.findViewById(R.id.layout_containstitle_error);
		return view;
	}

	/**
	 * 标题下内容
	 */
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(getContentLayoutId(), null);
		initListView(view);
		// 初始化头部
		// initView(view);
		return view;
	}

	/**
	 * 获取内容布局此处为PullToRefreshListView
	 */
	public int getContentLayoutId() {
		return R.layout.base_pullrefresh_listview;
	}

	/*
	 * 设置标题
	 */
	protected void setHeaderTitle(String title) {
		mTitleHeaderBar.getTitleTextView().setText(title);
	}

	@Override
	public void setListPullShow() {
		if (null != mMainNoneDataLayout && mMainNoneDataLayout.getVisibility() == View.VISIBLE)
			mMainNoneDataLayout.setVisibility(View.GONE);
		if (null != mContentContainer && mContentContainer.getVisibility() == View.GONE)
			mContentContainer.setVisibility(View.VISIBLE);
	}

	@Override
	public void setNoneDataShow() {
		if (null != mMainNoneDataLayout) {
			mMainNoneDataLayout.removeAllViews();
			mMainNoneDataLayout.addView(mCustomNoneDataView);
			if (mMainNoneDataLayout.getVisibility() == View.GONE)
				mMainNoneDataLayout.setVisibility(View.VISIBLE);
			mContentContainer.setVisibility(View.GONE);
		}
	}

	@Override
	protected void setErrorShow() {
		if (null != mMainNoneDataLayout) {
			mMainNoneDataLayout.removeAllViews();
			mMainNoneDataLayout.addView(mCustomErrorView);
			if (mMainNoneDataLayout.getVisibility() == View.GONE)
				mMainNoneDataLayout.setVisibility(View.VISIBLE);
			mContentContainer.setVisibility(View.GONE);
		}
	}

}

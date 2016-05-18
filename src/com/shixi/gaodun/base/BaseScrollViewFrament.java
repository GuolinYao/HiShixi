package com.shixi.gaodun.base;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.libpull.PullToRefreshBase.OnRefreshListener;
import com.shixi.gaodun.libpull.PullToRefreshScrollView;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.MyListView;
import com.shixi.gaodun.view.ObservableScrollView;

/**
 * 不带头部的ScrollView
 * 
 * @author ronger
 * @param <T>
 * @date:2015-12-7 下午2:04:56
 */
public abstract class BaseScrollViewFrament<T> extends ParentFragment implements OnRefreshListener,
		OnItemClickListener, OnScrollListener {
	protected LinearLayout mLayoutViewGroup;// 父容器
	protected FrameLayout mMainNoneDataLayout;
	protected PullToRefreshScrollView mPullToRefreshScrollView;
	protected ObservableScrollView mScrollView;
	protected MyListView myListView;
	// 每页的起始位置
	protected int mPage = 1;
	// 每页的数量
	protected int mPageNumber = 10;
	// 加载更多时的view
	protected View mLoadMoreLayout;
	// 是否需要加载更多
	private boolean isLoadMore;
	// 集合
	protected List<T> mLists;
	// 总数量
	protected int mListTotal;
	// 列表的适配器
	protected CommonAdapter<T> mListAdapter;

	/**
	 * 初始化列表的适配器
	 */
	public abstract void initAdapter();

	/** 刷新列表 */
	public abstract void refreshList();

	/** 加载更多 */
	public abstract void loadMoreList();

	/** 对应的布局 */
	protected abstract int getFrameLayoutId();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View contentView = getContentView(inflater, container, savedInstanceState);
		return contentView;
	}

	public View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(getFrameLayoutId(), null);
		initView(view);
		return view;
	}

	public void initListView(View view) {
		setRefreshOrLoadMoreListener();
	}

	public void setRefreshOrLoadMoreListener() {
		mPullToRefreshScrollView.setOnRefreshListener(this);
		myListView.setOnScrollListener(this);
	}

	/**
	 * 获取列表成功
	 * 
	 * @param lists
	 */
	public void getListSuccess(List<T> lists) {
		if (mPage > 1) {// 非第一次加载
			mLists.addAll(lists);
		} else {
			mLists = lists;
		}
		// 判断是否有更多数据
		if (mListTotal - mLists.size() > 0) {// 有更多数据
			if (myListView.getFooterViewsCount() == 0) {
				isLoadMore = true;
				myListView.addFooterView(mLoadMoreLayout);
			}
		} else {
			if (myListView.getFooterViewsCount() != 0) {
				isLoadMore = false;
				myListView.removeFooterView(mLoadMoreLayout);
			}
		}
		setListDataShow();
		mPage++;
	}

	public void setListDataShow() {
		if (null == mListAdapter || mPage * mPageNumber <= mPageNumber) {// 第一次加载或刷新
			mListAdapter = null;
			initAdapter();
			myListView.setAdapter(mListAdapter);
		} else {
			mListAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 是否显示加载更多
	 * 
	 * @param isShow
	 */
	private void showLoadingControl(boolean isShow) {
		mLoadMoreLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	/**
	 * 请求服务器完成
	 */
	public void getResponseComplete() {
		myProgressDialog.dismiss();
		mPullToRefreshScrollView.onRefreshComplete();
		showLoadingControl(false);
	}

	@Override
	public void onRefresh() {
		if (ActivityUtils.isNetAvailable()) {
			mPage = 1;
			refreshList();
		} else {
			ToastUtils.showToastInCenter("网络未连接");
			if (myListView.getFooterViewsCount() != 0) {
				myListView.removeFooterView(mLoadMoreLayout);
			}
			mPullToRefreshScrollView.onRefreshComplete();
		}
	}

	/**
	 * 加载更多
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (null == mLists || mLists.size() == 0)
			return;
		boolean scrollEnd = false;// 判断是否滚动到底部
		try {
			if (view.getPositionForView(mLoadMoreLayout) == view.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}
		if (isLoadMore && scrollEnd) {
			if (ActivityUtils.isNetAvailable()) {
				showLoadingControl(true);
				loadMoreList();
			} else {
				ToastUtils.showToastInCenter("网络未连接");
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}
}

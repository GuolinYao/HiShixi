package com.shixi.gaodun.base;

import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.libpull.PullToRefreshBase.OnRefreshListener;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * 不带头部的listview
 * 
 * @author ronger
 * @param <T>
 * @date:2015-12-5 下午12:00:32
 */
@SuppressLint("InflateParams")
public abstract class BaseListViewFragment<T> extends ParentFragment implements
		OnRefreshListener, OnItemClickListener, OnScrollListener {
	protected LinearLayout mLayoutViewGroup;// 父容器
	protected PullToRefreshListView mPullToRefreshListView;
	// protected ListView myListView;
	protected ListView myListView;
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
	protected boolean isLoadPositionListFinish = true;// 是否加载完毕

	/**
	 * 初始化列表的适配器
	 */
	public abstract void initAdapter();

	/** 刷新列表 */
	public abstract void refreshList();

	/** 加载更多 */
	public abstract void loadMoreList();

	/** 整体布局 */
	protected abstract int getFrameLayoutId();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = getContentView(inflater, container,
				savedInstanceState);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
	}

	public View getContentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup view = (ViewGroup) inflater.inflate(getFrameLayoutId(), null);
		// initView(view);
		return view;
	}

	public void initListView(View view) {
		mLayoutViewGroup = (LinearLayout) view
				.findViewById(R.id.layout_groupview);
		// 若此fragment有额外的标题，则需重新赋值
		mMainNoneDataLayout = (FrameLayout) view
				.findViewById(R.id.layout_nonedata);
		mPullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.base_pullrefresh_listview);
		mLoadMoreLayout = LayoutInflater.from(mContext).inflate(
				R.layout.loadmore_layout, null);
		myListView = mPullToRefreshListView.getRefreshableView();
		myListView.setCacheColorHint(Color.parseColor("#00000000"));
		myListView.setOnItemClickListener(this);
		setRefreshOrLoadMoreListener();
	}

	public void setRefreshOrLoadMoreListener() {
		mPullToRefreshListView.setOnRefreshListener(this);
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
	public void showLoadingControl(boolean isShow) {
		mLoadMoreLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	/**
	 * 请求服务器完成
	 */
	public void getResponseComplete() {
		dissMissProgress();
		mPullToRefreshListView.onRefreshComplete();
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
			mPullToRefreshListView.onRefreshComplete();
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
			if (view.getPositionForView(mLoadMoreLayout) == view
					.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}
		if (isLoadMore && scrollEnd && isLoadPositionListFinish) {// 判断当前是否是否正在请求加载
			if (ActivityUtils.isNetAvailable()) {
				isLoadPositionListFinish = false;
				showLoadingControl(true);
				loadMoreList();
			} else {
				ToastUtils.showToastInCenter("网络未连接");
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		super.onErrorResponse(error);
		getResponseComplete();
	}

	@Override
	protected void setErrorShow() {
		ViewGroup parent = (ViewGroup) mCustomErrorView.getParent();
		if (parent != null) {
			parent.removeAllViews();
		}
		mMainNoneDataLayout.addView(mCustomErrorView);
		// mMainNoneDataLayout.removeAllViews();
		// mMainNoneDataLayout.addView(mCustomErrorView);
		mMainNoneDataLayout.setVisibility(View.VISIBLE);
		mPullToRefreshListView.setVisibility(View.GONE);
	}

	@Override
	public void setNoneDataShow() {
		ViewGroup parent = (ViewGroup) mCustomNoneDataView.getParent();
		if (parent != null) {
			parent.removeAllViews();
		}
		mMainNoneDataLayout.addView(mCustomNoneDataView);
		// mMainNoneDataLayout.removeAllViews();
		// mMainNoneDataLayout.addView(mCustomNoneDataView);
		mMainNoneDataLayout.setVisibility(View.VISIBLE);
		mPullToRefreshListView.setVisibility(View.GONE);
	}

	public void setListPullShow() {
		if (null != mMainNoneDataLayout
				&& mMainNoneDataLayout.getVisibility() == View.VISIBLE)
			mMainNoneDataLayout.setVisibility(View.GONE);
		if (null != mPullToRefreshListView
				&& mPullToRefreshListView.getVisibility() == View.GONE)
			mPullToRefreshListView.setVisibility(View.VISIBLE);
	}
}

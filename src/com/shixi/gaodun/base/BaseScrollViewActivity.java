package com.shixi.gaodun.base;

import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.inf.ScrollViewListener;
import com.shixi.gaodun.libpull.PullToRefreshBase.OnRefreshListener;
import com.shixi.gaodun.libpull.PullToRefreshScrollView;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.MyListView;
import com.shixi.gaodun.view.ObservableScrollView;

/**
 * @author ronger
 * @param <T>
 * @date:2015-12-4 下午1:40:35
 */
@SuppressLint("InflateParams")
public abstract class BaseScrollViewActivity<T> extends TitleBaseActivity implements OnItemClickListener,
		ScrollViewListener, OnRefreshListener {
	// 每页的起始位置
	protected int mPage = 1;
	// 每页的数量
	protected int mPageNumber = 10;
	// 加载更多时的view
	protected View mLoadMoreLayout;
	// 是否需要加载更多
	protected boolean isLoadMore;
	// 集合
	protected List<T> mLists;
	// 总数量
	protected int mListTotal;
	// 列表的适配器
	protected CommonAdapter<T> mListAdapter;
	protected PullToRefreshScrollView mPullToRefreshScrollView;
	protected ObservableScrollView mScrollView;
	protected MyListView myListView;
	protected boolean isExcuteNoneData = false;

	/**
	 * 初始化列表的适配器
	 */
	public abstract void initAdapter();

	/** 刷新列表 */
	public abstract void refreshList();

	/** 加载更多 */
	public abstract void loadMoreList();

	/**
	 * 设置没有数据时的描述
	 */
	protected abstract void setNoneDataDesc();

	@Override
	public void initView() {
		super.initView();
		mLoadMoreLayout = LayoutInflater.from(this).inflate(R.layout.loadmore_layout, null);
	}

	public void setRefreshOrLoadMoreListener() {
		mPullToRefreshScrollView.setOnRefreshListener(this);
		mScrollView.setScrollViewListener(this);
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
			isLoadMore = true;
			if (myListView.getFooterViewsCount() == 0) {
				myListView.addFooterView(mLoadMoreLayout);
			}
		} else {
			isLoadMore = false;
			if (myListView.getFooterViewsCount() != 0) {
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
			refreshFinish();
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

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		if (scrollView.getChildAt(0).getMeasuredHeight() <= scrollView.getHeight() + scrollView.getScrollY()) {
			if (isLoadMore) {
				if (ActivityUtils.isNetAvailable()) {
					showLoadingControl(true);
					loadMoreList();
				} else {
					ToastUtils.showToastInCenter("网络未连接");
				}
			}
		}
	}

	public void setNoneDataShow() {
		if (isExcuteNoneData) {
			mMainNoneDataLayout.removeAllViews();
			mMainNoneDataLayout.addView(mCustomNoneDataView);
			mMainNoneDataLayout.setVisibility(View.VISIBLE);
			setNoneDataDesc();
		}
	}

	public void setListPullShow() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		// mPullToRefreshScrollView.setVisibility(View.VISIBLE);
	}

	/**
	 * 刷新完成，可以用来置顶界面
	 */
	public void refreshFinish() {
		// mScrollView.smoothScrollTo(0, 0);
	}
}

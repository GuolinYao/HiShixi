package com.shixi.gaodun.base;

import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.libpull.PullToRefreshBase.OnRefreshListener;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * 下拉刷新和加载更多的基类，含标题栏,特殊pullrefreshListview需要自己重写xml
 * 
 * @author ronger
 * @param <T>
 * @date:2015-12-4 下午1:36:55
 */
@SuppressLint("InflateParams")
public abstract class BaseListViewActivity<T> extends TitleBaseActivity
		implements OnRefreshListener, OnItemClickListener, OnScrollListener {
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
	protected PullToRefreshListView mPullToRefreshListView;
	protected ListView myListView;
	// 是否设置没有数据的显示,默认为是
	protected boolean isExcuteNoneData = true;
	protected boolean isLoadPositionListFinish = true;// 是否加载完毕

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

	// public void setNoneDataDesc(String title, String desc) {
	// mTextNoneDataWarnTitle.setText(title);
	// mTextNoneDataWarnDesc.setText(desc);
	// }

	/**
	 * 重试
	 */
	// @Override
	// public void onClick(View v) {
	// // if (v.getId() == R.id.button_again) {
	// // refreshList();
	// // }
	// }

	@Override
	public void initView() {
		super.initView();
		// initNoneDataView();
		mLoadMoreLayout = LayoutInflater.from(this).inflate(
				R.layout.loadmore_layout, null);
	}

	public void setRefreshOrLoadMoreListener() {
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.setOnScrollListener(this);
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
		dissMissProgress();
		if (null != mPullToRefreshListView)
			mPullToRefreshListView.onRefreshComplete();
		showLoadingControl(false);
	}

	/**
	 * 设置列表的显示
	 */
	public void setPullListShow() {
		if (mMainNoneDataLayout.getVisibility() == View.VISIBLE)
			mMainNoneDataLayout.setVisibility(View.GONE);
	}

	/**
	 * 设置没有数据的显示
	 * 
	 * @return
	 */
	protected void setNoneDataShow() {
		if (isExcuteNoneData) {
			mMainNoneDataLayout.removeAllViews();
			mMainNoneDataLayout.addView(mCustomNoneDataView);
			mCustomNoneDataView.setVisibility(View.VISIBLE);
			mMainNoneDataLayout.setVisibility(View.VISIBLE);
			setNoneDataDesc();
		}
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
			if (null != mPullToRefreshListView)
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
}

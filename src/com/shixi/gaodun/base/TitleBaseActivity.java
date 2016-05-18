package com.shixi.gaodun.base;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 带标题的activity的基类
 * 
 * @author ronger
 * @date:2015-11-18 下午2:32:13
 */
public abstract class TitleBaseActivity extends BaseMainActivity {
	// 标题layout
	protected RelativeLayout mTitleLayout;
	// 返回按钮
	protected ImageView mBackIcon;
	// 页面标题
	protected TextView mTitleName;
	// 标题栏最右边入口
	protected TextView mOtherName;
	// 填充内容的layout
	protected FrameLayout mainView;
	// protected FrameLayout mOtherLayout;
	protected ImageView mRightTwoIcon;
	protected ImageView mRightOneIcon;
	protected FrameLayout mRightOneLayout;
	protected FrameLayout mRightTwoLayout;
	public RelativeLayout mRlTitlebarbg;//titlebar的整体布局

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		View contentLayout = LayoutInflater.from(this).inflate(layoutResID, null);
		setContentView(contentLayout);
	}

	/**
	 * 加载整体主布局，初始化头部和内容控件
	 */
	@Override
	public void setContentView(View view) {
		super.setContentView(R.layout.activity_base);
		mainView = (FrameLayout) findViewById(R.id.content_container);
		FrameLayout.LayoutParams params = new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		mainView.addView(view, params);
		initView();
	}

	/**
	 * 初始化控件
	 */
	@Override
	public void initView() {
		super.initView();
		// 初始化异常时的父容器
		mMainNoneDataLayout = (FrameLayout) findViewById(R.id.layout_mainerror_viewgroup);
		mTitleLayout = (RelativeLayout) findViewById(R.id.title_bar_container);
		mBackIcon = (ImageView) findViewById(R.id.iv_title_bar_icon);
		mTitleName = (TextView) findViewById(R.id.tv_title_bar_titlename);
		mOtherName = (TextView) findViewById(R.id.tv_title_bar_otherbtn);
//		mRlTitlebarbg = (RelativeLayout) mTitleLayout.findViewById(R.id.rl_titlebar_bg);
		mRightOneIcon = (ImageView) findViewById(R.id.tv_title_bar_one);
		mRightTwoIcon = (ImageView) findViewById(R.id.tv_title_bar_two);
		mRightOneLayout = (FrameLayout) findViewById(R.id.fl_title_bar_one);
		// mRightOneLayout.setOnClickListener(this);
		mRightTwoLayout = (FrameLayout) findViewById(R.id.fl_title_bar_two);
		// mRightTwoLayout.setOnClickListener(this);
	}

	/***
	 * 重新请求
	 */
	protected abstract void setRetryRequest();

	// @Override
	// public void onClick(View v) {
	// // if (v.getId() == R.id.button_again) {
	// // setRetryRequest();
	// // }
	// }

	/**
	 * 异常时的显示
	 * 
	 * 此处是一般的没有刷新和加载更多的显示
	 */
	protected void setErrorShow(boolean isExcute) {
		if (isExcute) {
			mMainNoneDataLayout.removeAllViews();
			mMainNoneDataLayout.addView(mCustomErrorView);
			mMainNoneDataLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置正常显示
	 */
	protected void setNolmalShow() {
		// if (isExcute) {
		// if (mMainNoneDataLayout.getVisibility() == View.VISIBLE)
		// mMainNoneDataLayout.setVisibility(View.GONE);
		// }
		if (mMainNoneDataLayout.getVisibility() == View.VISIBLE)
			mMainNoneDataLayout.setVisibility(View.GONE);
	}

	/**
	 * 设置右边按钮的显示
	 */
	protected void setRightOneIconShow() {
		if (mRightOneLayout.getVisibility() == View.GONE)
			mRightOneLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (!isExcute) {
			super.onErrorResponse(error);
			return;
		}
		setOnErrorResponse(error);
	}

	public void setOnErrorResponse(VolleyError error) {
		// 先关闭加载动画
		dissMissProgress();
		// 显示异常时应该显示的view
		setErrorShow(isExcute);
		// 异常描述
		String errorDes = getErrorDetailDesc(error);
		setErrorInfoShow(StringUtils.isEmpty(errorDes) ? "服务器异常，请稍后再试!" : errorDes);
	}
}

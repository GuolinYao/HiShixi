package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.BaseFragmentPagerAdapter;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.MyOnPageChangeListenerInf;
import com.shixi.gaodun.inf.OnActivityRefreshListener;
import com.shixi.gaodun.inf.imp.MyOnPageChangeListener;
import com.shixi.gaodun.view.fragment.ResumeDynamicFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * 简历动态
 * 
 * @author ronger
 * @date:2015-11-6 下午4:12:37
 */
public class MineResumeDynamicActivity extends TitleBaseActivity implements MyOnPageChangeListenerInf {
	private ViewPager mViewPager;
	private TextView mTextTabAll, mTextTabInterview, mTextTabInappropriate;
	private List<Fragment> mFragments;
	private OnActivityRefreshListener mRefreshListener;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, MineResumeDynamicActivity.class);
		context.startActivity(intent);
	}

	public void setOnActivityRefreshListener(OnActivityRefreshListener refreshListener) {
		this.mRefreshListener = refreshListener;
	}

	@Override
	protected void getIntentData() {
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = false;
		setContentView(R.layout.mine_resume_dynamic_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("简历动态");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mViewPager = (ViewPager) findViewById(R.id.viewpage_resume_dynamic);
		mTextTabAll = (TextView) findViewById(R.id.text_resume_tablerow_one);
		mTextTabInterview = (TextView) findViewById(R.id.text_resume_tablerow_two);
		mTextTabInappropriate = (TextView) findViewById(R.id.text_resume_tablerow_three);
		findViewById(R.id.resume_tablerow_one).setOnClickListener(this);
		findViewById(R.id.resume_tablerow_two).setOnClickListener(this);
		findViewById(R.id.resume_tablerow_three).setOnClickListener(this);
		initViewPager();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.resume_tablerow_one:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.resume_tablerow_two:
			mViewPager.setCurrentItem(1);
			break;
		case R.id.resume_tablerow_three:
			mViewPager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}

	public void initViewPager() {
		// 关闭预加载，默认一次只加载一个Fragment
		mViewPager.setOffscreenPageLimit(1);
		mFragments = new ArrayList<Fragment>(0);
		mFragments.add(ResumeDynamicFragment.newInstance(1, 0));
		mFragments.add(ResumeDynamicFragment.newInstance(3, 1));
		mFragments.add(ResumeDynamicFragment.newInstance(4, 2));
		// mFragments.add(new ResumeDynamicFragment(1));
		// mFragments.add(new ResumeDynamicFragment(3));
		// mFragments.add(new ResumeDynamicFragment(4));
		mViewPager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
		// mViewPager.setCurrentItem(0);
		updateTabStatus(true, false, false);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener(this));
	}

	public void updateTabStatus(boolean tabOne, boolean tabTwo, boolean tabThree) {
		mTextTabAll.setSelected(tabOne);
		mTextTabInterview.setSelected(tabTwo);
		mTextTabInappropriate.setSelected(tabThree);
	}

	@Override
	public void onPageSelected(int arg0) {
		switch (arg0) {
		case 0:
			updateTabStatus(true, false, false);
			// mRefreshListener.onRefreshList(1);
			break;
		case 1:
			updateTabStatus(false, true, false);
			// mRefreshListener.onRefreshList(3);
			break;
		case 2:
			updateTabStatus(false, false, true);
			// mRefreshListener.onRefreshList(4);
			break;
		}

	}

	@Override
	protected void setRetryRequest() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("resumeState"); // 简历动态：resumeState
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("resumeState");
		MobclickAgent.onPause(this);
	}

	@Override
	public void cancelRequests() {
	}
}

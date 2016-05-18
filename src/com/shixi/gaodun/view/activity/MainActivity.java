package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.FragmentTabAdapter;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.view.fragment.TabHomeFindFragment;
import com.shixi.gaodun.view.fragment.TabHomeNewFragment;
import com.shixi.gaodun.view.fragment.TabHomePositionFrament;
import com.shixi.gaodun.view.fragment.TabMineFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * 主页面
 * 
 * UI框架:
 * 
 * @author gaodun
 * 
 */
public class MainActivity extends BaseMainActivity {
	private RadioGroup mRadioGroup;
	private List<Fragment> mFragments;
	// private Fragment mFragment;
	private int currentIndex = 1;
	private RadioButton mButtonHome, mButtomPosition, mButtonDiscover,
			mButtonMine;
	private int mSource;

	/**
	 * 
	 * @param context
	 * @param source来源0其他1创建简历后
	 */
	public static void startAction(Activity context, int source) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.putExtra("source", source);
		// 如果activity在task存在，将Activity之上的所有Activity结束掉
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		// currentIndex = getIntent().getIntExtra("currentIndex", 1);
		currentIndex = CacheUtils.getIndex(this);
		mSource = getIntent().getIntExtra("source", 0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.activity_main);
		
		initView();
	}

	@Override
	public void initView() {
		super.initView();
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new TabHomeNewFragment());
		// mFragments.add(new TabHomeFragment());
		mFragments.add(new TabHomePositionFrament());
		mFragments.add(new TabHomeFindFragment());
		TabMineFragment mineFragment = new TabMineFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("source", mSource);
		mineFragment.setArguments(bundle);
		mFragments.add(mineFragment);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_tabs);
		mButtonHome = (RadioButton) findViewById(R.id.rb_tab_one);
		mButtomPosition = (RadioButton) findViewById(R.id.rb_tab_two);
		mButtonDiscover = (RadioButton) findViewById(R.id.rb_tab_three);
		mButtonMine = (RadioButton) findViewById(R.id.rb_tab_four);
		setTabBG();
		FragmentTabAdapter mAdapter = new FragmentTabAdapter(this, mFragments,
				R.id.fl_tab_content, mRadioGroup, currentIndex);
	}

	public void setTabBG() {
		if (currentIndex == 0) {
			mButtonHome.setChecked(true);
		}
		if (currentIndex == 1) {
			mButtomPosition.setChecked(true);
		}
		if (currentIndex == 2) {
			mButtonDiscover.setChecked(true);
		}
		if (currentIndex == 3) {
			mButtonMine.setChecked(true);
		}
	}

	@Override
	public void onBackPressed() {
		exit();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}

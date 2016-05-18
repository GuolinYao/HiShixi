package com.shixi.gaodun.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;

import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.inf.OnTabClickListener;
import com.shixi.gaodun.model.utils.CacheUtils;

/**
 * 首页底部导航的点击
 * 
 * @author ronger
 * @date:2015-10-23 下午4:24:55
 */
public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener {
	private List<Fragment> fragments;
	private RadioGroup group;
	private FragmentActivity fragmentActivity;
	// Activity中所要被替换的区域的id
	private int fragmentContentId;
	// 当前Tab页面索引
	private int currentTab;
	private OnTabClickListener mOnTabClickListener;

	public FragmentTabAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments, int fragmentContentId,
			RadioGroup group, int firstPage) {
		this.fragments = fragments;
		this.group = group;
		this.fragmentActivity = fragmentActivity;
		this.fragmentContentId = fragmentContentId;
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
		ft.add(fragmentContentId, fragments.get(firstPage));
		ft.commit();
		group.setOnCheckedChangeListener(this);
	}

	public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
		this.mOnTabClickListener = onTabClickListener;
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		for (int i = 0; i < group.getChildCount(); i++) {
			if (group.getChildAt(i).getId() == checkedId) {
				Fragment fragment = fragments.get(i);
				FragmentTransaction ft = obtainFragmentTransaction(i);
				getCurrentFragment().onPause(); // 暂停当前tab
				// 若已添加则直接重新启动，若未添加则添加
				if (fragment.isAdded()) {
					fragment.onResume();// 启动目标tab的onresume
				} else {
					ft.add(fragmentContentId, fragment);
				}
				showTab(i); // 显示目标tab
				ft.commit();
				if (null != mOnTabClickListener) {
					mOnTabClickListener.onButtonChecked(i);
				}
			}
		}
	}

	private void showTab(int idx) {
		for (int i = 0; i < fragments.size(); i++) {
			Fragment fragment = fragments.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(idx);
			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commit();
		}
		currentTab = idx;
		CacheUtils.saveIndex(BaseApplication.mApp, currentTab);
	}

	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
		return ft;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public Fragment getCurrentFragment() {
		return fragments.get(currentTab);
	}
}

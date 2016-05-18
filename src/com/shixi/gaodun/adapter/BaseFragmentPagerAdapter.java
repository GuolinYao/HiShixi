package com.shixi.gaodun.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * @author ronger
 * @date:2015-11-12 下午1:40:05
 */
// public class BaseFragmentPagerAdapter extends FragmentpaperViewAdapater {
// private List<Fragment> mFraments;
//
// public BaseFragmentPagerAdapter(FragmentManager fm) {
// super(fm);
// }
//
// public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> framents) {
// super(fm);
// this.mFraments = framents;
// }
//
// @Override
// public Fragment getItem(int position) {
// return mFraments.get(position);
// }
//
// @Override
// public int getCount() {
// return mFraments.size();
// }
//
// }
public class BaseFragmentPagerAdapter extends
// FragmentStatePagerAdapter {
		FragmentPagerAdapter {
	private List<Fragment> fragments;

	public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	public BaseFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		return super.instantiateItem(container, position);
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}
}

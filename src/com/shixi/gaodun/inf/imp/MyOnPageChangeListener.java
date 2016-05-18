package com.shixi.gaodun.inf.imp;

import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.shixi.gaodun.inf.MyOnPageChangeListenerInf;

/**
 * @author ronger
 * @date:2015-12-5 上午11:49:08
 */
public class MyOnPageChangeListener implements OnPageChangeListener {
	private MyOnPageChangeListenerInf mListenerInf;

	public MyOnPageChangeListener(MyOnPageChangeListenerInf inf) {
		this.mListenerInf = inf;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageSelected(int arg0) {
		mListenerInf.onPageSelected(arg0);
	}
}

package com.shixi.gaodun.model.utils;

/**
 * 动态设置view的宽高，主要用来获取LayoutParams
 * 
 * @author ronger
 * @date:2015-11-10 上午11:49:08
 */
public class LayoutParamsUtils {
	/**
	 * 获取线性布局的LayoutParams
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static android.widget.LinearLayout.LayoutParams geLineartLayoutParams(int width, int height) {
		android.widget.LinearLayout.LayoutParams layoutParams = new android.widget.LinearLayout.LayoutParams(width,
				height);
		return layoutParams;
	}

	public static android.support.v4.view.ViewPager.LayoutParams getViewPagerLayoutParams(int width, int height) {
		android.support.v4.view.ViewPager.LayoutParams layoutParams = new android.support.v4.view.ViewPager.LayoutParams();
		layoutParams.width = width;
		layoutParams.height = height;
		return layoutParams;
	}
}

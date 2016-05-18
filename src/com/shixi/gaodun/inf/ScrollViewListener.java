package com.shixi.gaodun.inf;

import com.shixi.gaodun.view.ObservableScrollView;

/**
 * @author ronger
 * @date:2015-11-12 上午9:24:49
 */
public interface ScrollViewListener {
	void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
}

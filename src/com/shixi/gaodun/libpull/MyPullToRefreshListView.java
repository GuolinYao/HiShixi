package com.shixi.gaodun.libpull;

import android.content.Context;
import android.util.AttributeSet;

public class MyPullToRefreshListView extends PullToRefreshListView {

	public MyPullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public MyPullToRefreshListView(Context context, int mode) {
		super(context, mode);
		
	}

	public MyPullToRefreshListView(Context context) {
		super(context);
		
	}

	@Override
	protected boolean isReadyForPullDown() {
		return false;
	}
}

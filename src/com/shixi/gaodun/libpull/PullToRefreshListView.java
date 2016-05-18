package com.shixi.gaodun.libpull;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.shixi.gaodun.view.BannerLayout;

public class PullToRefreshListView extends PullToRefreshAdapterViewBase<ListView> {

	// private LoadingLayout headerLoadingView;
	// private LoadingLayout footerLoadingView;
	boolean onHorizontal = false;
	float x = 0.0f;
	float y = 0.0f;
	private BannerLayout viewflow = null;

	class InternalListView extends ListView implements EmptyViewMethodAccessor {

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		@Override
		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			// TODO Auto-generated method stub
			if (viewflow == null) {
				return super.onInterceptTouchEvent(ev);
			}
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				onHorizontal = false;
				x = ev.getX();
				y = ev.getY();
				break;
			case MotionEvent.ACTION_UP:
				onHorizontal = false;
				break;
			default:
				break;
			}

			// if (ViewFlow.onTouch) {
			float dx = Math.abs(ev.getX() - x);

			Log.i("MyViewFlow", "dx:=" + dx);
			if (dx > 10.0) {
				onHorizontal = true;
			}
			if (onHorizontal) {
				viewflow.onInterceptTouchEvent(ev);
				return false;
			} else {
				return super.onInterceptTouchEvent(ev);
			}
		}
	}

	public PullToRefreshListView(Context context) {
		super(context);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshListView(Context context, int mode) {
		super(context, mode);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDisableScrollingWhileRefreshing(false);
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalListView) getRefreshableView()).getContextMenuInfo();
	}

	public void setViewFlow(BannerLayout viewflow) {
		this.viewflow = viewflow;
	}

	@Override
	protected final ListView createRefreshableView(Context context, AttributeSet attrs) {
		ListView lv = new InternalListView(context, attrs);
		lv.setId(android.R.id.list);
		return lv;
	}
}

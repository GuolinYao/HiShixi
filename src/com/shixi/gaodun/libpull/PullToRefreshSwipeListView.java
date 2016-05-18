package com.shixi.gaodun.libpull;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MotionEvent;
import android.view.View;

import com.shixi.gaodun.custom.view.SwipeMenuListView;
import com.shixi.gaodun.view.BannerLayout;

/**
 * 侧滑删除相关
 * 
 * @author ronger
 * @date:2015-12-22 下午3:33:32
 */
public class PullToRefreshSwipeListView extends PullToRefreshAdapterViewBase<SwipeMenuListView> {
	boolean onHorizontal = false;
	float x = 0.0f;
	float y = 0.0f;
	private BannerLayout viewflow = null;

	class InternalSwipeListView extends SwipeMenuListView implements EmptyViewMethodAccessor {

		public InternalSwipeListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshSwipeListView.this.setEmptyView(emptyView);
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

	public PullToRefreshSwipeListView(Context context) {
		super(context);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshSwipeListView(Context context, int mode) {
		super(context, mode);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshSwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDisableScrollingWhileRefreshing(false);
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalSwipeListView) getRefreshableView()).getContextMenuInfo();
	}

	public void setViewFlow(BannerLayout viewflow) {
		this.viewflow = viewflow;
	}

	@Override
	protected final SwipeMenuListView createRefreshableView(Context context, AttributeSet attrs) {
		SwipeMenuListView lv = new InternalSwipeListView(context, attrs);
		lv.setId(android.R.id.list);
		return lv;
	}

}

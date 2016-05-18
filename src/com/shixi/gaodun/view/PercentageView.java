package com.shixi.gaodun.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.shixi.gaodun.R;

/**
 * 完整度展示，以百分比的形式
 * 
 * @author ronger
 * @date:2015-10-28 上午10:24:16
 */
public class PercentageView extends View {
	// 当前百分比
	private float progress;
	// 是否刷新
	private boolean refresh;
	private GradientDrawable gdLeft;
	private GradientDrawable gdRight;
	private GradientDrawable fullBg;// 充满的情况
	private GradientDrawable nomalBg;// 为0的情况

	public PercentageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PercentageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PercentageView(Context context) {
		super(context);
		init(context);
	}

	public float getPreogress() {
		return progress;
	}

	public void setPreogress(float preogress) {
		this.progress = preogress;
	}

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}

	public void init(Context context) {
		Resources res = context.getResources();
		gdLeft = (GradientDrawable) res.getDrawable(R.drawable.percentage_left_bg);
		gdRight = (GradientDrawable) res.getDrawable(R.drawable.percentage_right_bg);
		fullBg = (GradientDrawable) res.getDrawable(R.drawable.percentage_full_bg);
		nomalBg = (GradientDrawable) res.getDrawable(R.drawable.percentage_bg);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (refresh) {
			int left = (int) (getWidth() * progress / 100);
			if (progress <= 0) {
				nomalBg.setBounds(0, 0, getWidth(), getHeight());
				nomalBg.draw(canvas);
				return;
			}
			if (progress >= 100) {
				fullBg.setBounds(0, 0, getWidth(), getHeight());
				fullBg.draw(canvas);
				return;
			}
			gdLeft.setBounds(0, 0, left, getHeight());
			gdLeft.draw(canvas);
			gdRight.setBounds(left, 0, getWidth(), getHeight());
			gdRight.draw(canvas);
		}

	}
}

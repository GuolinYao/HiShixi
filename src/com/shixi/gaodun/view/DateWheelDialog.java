package com.shixi.gaodun.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.DateUtils;

/**
 * @author ronger guolin
 * @date:2015-10-28 下午5:08:30
 */
public class DateWheelDialog extends Dialog implements android.view.View.OnClickListener {
	private DateWheelFrameLayout mDateWheelFrameLayout;

	public DateWheelDialog(Context context, int year, int month, int day) {
		super(context, R.style.MMTheme);
		mDateWheelFrameLayout = new DateWheelFrameLayout(context, year, month, day, DateUtils.getCurrentYear());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(mDateWheelFrameLayout);
		Window w = getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.width = ActivityUtils.getScreenWidth();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		onWindowAttributesChanged(lp);
	}

	public DateWheelFrameLayout getmDateWheelFrameLayout() {
		return mDateWheelFrameLayout;
	}

	public void setmDateWheelFrameLayout(DateWheelFrameLayout mDateWheelFrameLayout) {
		this.mDateWheelFrameLayout = mDateWheelFrameLayout;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}

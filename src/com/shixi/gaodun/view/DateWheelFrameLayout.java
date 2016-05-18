package com.shixi.gaodun.view;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.widget.NumericWheelAdapter;
import com.example.widget.OnWheelChangedListener;
import com.example.widget.WheelView;
import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.ActivityUtils;

/**
 * @author ronger
 * @date:2015-10-28 下午5:09:51
 */
public class DateWheelFrameLayout extends FrameLayout implements android.view.View.OnClickListener {
	private Button btn_submit, btn_cancel;
	private WheelView wv_year, wv_month, wv_day;
	// 选择的年月日
	private String yearStr, monthStr, dayStr;
	private OnDateChangedListener dateChangedListener;
	// 起始年份和结束年份
	private int START_YEAR = 1900, END_YEAR = 2100;
	private Context mContext;
	private int year, month, day;

	public interface OnDateChangedListener {
		void onDateChanged(String year, String month, String day);

		void onCancel();
	}

	public DateWheelFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 对外的公开方法
	 * 
	 * @param callback
	 */
	public void setOnDateChangedListener(OnDateChangedListener callback) {
		dateChangedListener = callback;
	}

	public DateWheelFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// public DateWheelFrameLayout(Context context) {
	// super(context);
	// this.mContext = context;
	// inflate(context, R.layout.date_wheel_layout, this);
	// initView();
	// setData();
	// }

	@SuppressWarnings("static-access")
	public DateWheelFrameLayout(Context context, int year, int month, int day, int currentYear) {
		super(context);
		this.mContext = context;
		this.year = year;
		this.month = month;
		this.day = day;
		this.START_YEAR = year - 100;
		this.END_YEAR = year + 100;
		this.inflate(context, R.layout.date_wheel_layout, this);
		initView();
		setData();
	}

	public void initView() {
		wv_year = (WheelView) findViewById(R.id.year);
		wv_month = (WheelView) findViewById(R.id.month);
		wv_day = (WheelView) findViewById(R.id.day);
		btn_submit = (Button) findViewById(R.id.submit);
		btn_cancel = (Button) findViewById(R.id.cancel);
		btn_submit.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
	}

	public void setData() {
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);
		// Calendar calendar = Calendar.getInstance();
		// int year = calendar.get(Calendar.YEAR);
		// int month = calendar.get(Calendar.MONTH);
		// int day = calendar.get(Calendar.DAY_OF_MONTH);
		// 年
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);
		setLinster(list_big, list_little);
		// 设置文字显示的大小
		int textSize = ActivityUtils.sp2px(mContext, 20);
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;
	}

	/**
	 * 设置年和月的监听
	 */
	public void setLinster(final List<String> list_big, final List<String> list_little) {
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		yearStr = wv_year.getCurrentItem() + START_YEAR + "";
		monthStr = wv_month.getCurrentItem() + 1 + "";
		dayStr = wv_day.getCurrentItem() + 1 + "";
		// sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-").append((wv_month.getCurrentItem() + 1))
		// .append("-").append((wv_day.getCurrentItem() + 1));
		return sb.toString();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.submit) {
			getTime();
			dateChangedListener.onDateChanged(yearStr, monthStr, dayStr);
		}
		if (v.getId() == R.id.cancel) {
			dateChangedListener.onCancel();
		}
	}
}

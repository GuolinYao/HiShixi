package com.shixi.gaodun.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.model.utils.ActivityUtils;

/**
 * @author ronger
 * @date:2015-11-23 下午5:17:27
 */
public class TagImageSpan extends ImageSpan {

	public int expandWidth = 0; // 设置之后可能会出现显示不全的问题，可通过TextView的 padding解决
	public int expandHeight = 0;// 设置之后可能会出现显示不全的问题，可通过TextView的 padding解决

	public TagImageSpan(Drawable bg) {
		super(bg);
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
			Paint paint) {
		paint.setTypeface(Typeface.create("normal", Typeface.NORMAL));
		paint.setTextSize(ActivityUtils.sp2px(BaseApplication.mApp, 15));
		int len = Math.round(paint.measureText(text, start, end));
		Drawable drawable = getDrawable();
		// 画背景矩形圆角的位置
		getDrawable().setBounds(0, 0, len, ActivityUtils.sp2px(BaseApplication.mApp, 20));
		super.draw(canvas, text, start, end, x, top, y, bottom, paint);
		// 指定画笔颜色和字体大小
		paint.setColor(Color.WHITE);
		paint.setTypeface(Typeface.create("normal", Typeface.NORMAL));
		paint.setTextSize(ActivityUtils.sp2px(BaseApplication.mApp, 11));
		Rect rect = drawable.getBounds();
		// 画文本,使文本在圆角矩形中居中
		// canvas.drawText(text.subSequence(start, end).toString(), x, y, paint);
		FontMetricsInt fontMetrics = paint.getFontMetricsInt();
		int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(text.subSequence(start, end).toString(), rect.centerX(), baseline, paint);
	}

	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
		return getWidth(text, start, end, paint);
	}

	/**
	 * 计算span的宽度
	 * 
	 * @param text
	 * @param start
	 * @param end
	 * @param paint
	 * @return
	 */
	private int getWidth(CharSequence text, int start, int end, Paint paint) {
		return Math.round(paint.measureText(text, start, end)) + expandWidth;
	}

}
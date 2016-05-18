package com.shixi.gaodun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 带阴影的imageview 
 * 使用 要给图片添加padding才有效果 imageView.setPadding(3, 3, 5, 5);
 * 
 * @author guolinyao
 * @date 2016年3月25日 上午11:57:56
 */
public class ShadowImageView extends ImageView {

	public ShadowImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

	}

	public ShadowImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public ShadowImageView(Context context) {
		super(context);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 画边框
		Rect rect1 = getRect(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setStyle(Paint.Style.STROKE);

		// 画边框
		canvas.drawRect(rect1, paint);
		paint.setColor(Color.LTGRAY);
		
		// 画一条竖线,模拟右边的阴影
//		canvas.drawLine(rect1.right + 1, rect1.top + 2, rect1.right + 1,
//				rect1.bottom + 2, paint);
		// 画一条横线,模拟下边的阴影
//		canvas.drawLine(rect1.left + 2, rect1.bottom + 1, rect1.right + 2,
//				rect1.bottom + 1, paint);

		// 画一条竖线,模拟右边的阴影
//		canvas.drawLine(rect1.right + 2, rect1.top + 3, rect1.right + 2,
//				rect1.bottom + 3, paint);
		// 画一条横线,模拟下边的阴影
		canvas.drawLine(rect1.left + 3, rect1.bottom + 2, rect1.right + 3,
				rect1.bottom + 2, paint);
	}

	Rect getRect(Canvas canvas) {
		Rect rect = canvas.getClipBounds();
		rect.bottom -= getPaddingBottom();
		rect.right -= getPaddingRight();
		rect.left += getPaddingLeft();
		rect.top += getPaddingTop();
		return rect;
	}

}

package com.shixi.gaodun.custom.view;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * @author ronger
 * @date:2015-12-17 下午2:38:07
 * 
 *                  显示原型图片的ImageLoader使用的显示器
 */
public class CircleBitmapDisplayer implements BitmapDisplayer {
	// public CircleBitmapDisplayer() {
	// super(0);
	// }
	//
	// @Override
	// public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
	// if (!(imageAware instanceof ImageViewAware)) {
	// throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
	// }
	//
	// imageAware.setImageDrawable(new CircleDrawable(bitmap, margin));
	// }
	//
	// public static class CircleDrawable extends RoundedDrawable {
	//
	// private Bitmap mBitmap;
	//
	// public CircleDrawable(Bitmap bitmap, int margin) {
	// super(bitmap, 0, margin);
	// this.mBitmap = bitmap;
	// }
	//
	// @Override
	// public void draw(Canvas canvas) {
	// int radius = 0;
	// if (mBitmap.getWidth() > mBitmap.getHeight()) {
	// radius = mBitmap.getHeight() / 2;
	// } else {
	// radius = mBitmap.getWidth() / 2;
	// }
	// canvas.drawRoundRect(mRect, radius, radius, paint);
	// }
	// }
	protected final int margin;

	public CircleBitmapDisplayer() {
		this(0);
	}

	public CircleBitmapDisplayer(int margin) {
		this.margin = margin;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		if (!(imageAware instanceof ImageViewAware)) {
			throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
		}
		imageAware.setImageDrawable(new CircleDrawable(bitmap, margin));
	}

}

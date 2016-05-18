package com.shixi.gaodun.model.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 图片加载配置类
 * 
 * @author ronger
 * @date:2015-11-17 上午8:54:07
 */
public class DisplayImageOptionsUtils {

	/**
	 * 获取矩形圆角图片的配置
	 * 
	 * @param roundPixels
	 * @return
	 */
	public static DisplayImageOptions getRoundBitMap(int roundPixels, int drawable) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).displayer(new RoundedBitmapDisplayer(roundPixels)).build();
		return options;
	}

	/**
	 * 获取圆形图片的配置
	 * 
	 * @param drawable
	 * @return
	 */
	public static DisplayImageOptions getCircleBitMap(int drawable) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true)
				.displayer(new com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer()).build();
		return options;
	}

	public static DisplayImageOptions getBitMapOptions(int drawable) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).build();
		return options;
	}

	/**
	 * 获取相册列表对应的配置
	 * 
	 * @param roundPixels
	 * @param drawable
	 * @return
	 */
	public static DisplayImageOptions getPickBitMap(int drawable) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		return options;
	}

	public static DisplayImageOptions getBigBitMap(int drawable) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageForEmptyUri(drawable)
				.showImageOnLoading(drawable).showImageOnFail(drawable).resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.build();
		return options;
	}
}

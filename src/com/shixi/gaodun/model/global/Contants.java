package com.shixi.gaodun.model.global;

import java.io.File;

import android.os.Environment;

/**
 * 常用常量
 * 
 * @author ronger
 * @date:2015-10-26 上午10:46:17
 */
public class Contants {
	// // 更新设置页
	// public static final String UPDATE_SETTING = "update.setting";
	// // 更新个人基本信息页
	// public static final String UPDATE_USERBASIVINFO = "update.userinfo.basic";
	// 共用的通知action
	public static final String ALL_UPDATE_ACTION = "all.update.action";
	// 刷新预览简历
	public static final String PREVIEW_RESUME_UPDATE_ACTION = "preview.resume.update.action";
	public static final boolean DEBUG_LIFE_CYCLE = false;
	public static final boolean DEBUG = true;
	// 上传图片之前图片的保存地址
	public static String ImageLoadPath = Environment.getExternalStorageDirectory() + File.separator + "Android"
			+ File.separator + "data" + File.separator + "ImageLoad";
	// 上传多图时的图片地址
	public static String postImgePath = Environment.getExternalStorageDirectory() + File.separator + "Android"
			+ File.separator + "data" + File.separator + "postImagePath";
}

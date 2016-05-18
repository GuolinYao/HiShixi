package com.shixi.gaodun.model.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 常用小数据的保存
 * 
 * @author Administrator
 * 
 */
public class CacheUtils {
	private static SharedPreferences userPreferences;// 个人信息
	private static SharedPreferences tabPositionPreferences;// 底部导航点击的位置
	private static SharedPreferences urlPreferences;// 服务器地址
	private static SharedPreferences cityPrefrences;

	/**
	 * 保存token
	 * 
	 * 
	 * @param context
	 * @param token
	 */
	public static void saveToken(Context context, String token) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("token", token);
		editor.commit();
	}

	/**
	 * 获取token的时间
	 * 
	 * @param context
	 * @return
	 */
	public static String getTokenTime(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("tokenTime", "");
	}

	public static void saveTokenTime(Context context, String tokenTime) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("tokenTime", tokenTime);
		editor.commit();
	}

	/**
	 * 获取token
	 * 
	 * @param context
	 * @return
	 */
	public static String getToken(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("token", "");
	}

	/**
	 * 保存账号id
	 * 
	 * @param context
	 * @param accountId
	 */
	public static void saveAccountId(Context context, String accountId) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("accountId", accountId);
		editor.commit();
	}

	public static String getAccountId(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("accountId", "");
	}

	/**
	 * 保存学生Id
	 * 
	 * @param context
	 * @param studentId
	 */
	public static void saveStudentId(Context context, String studentId) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("studentId", studentId);
		editor.commit();
	}

	public static String getStudentId(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("studentId", "");
	}

	/**
	 * 保存学生头像
	 * 
	 * @param context
	 * @param studentId
	 */
	public static void saveStudentImage(Context context, String studentImage) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("iamge", studentImage);
		editor.commit();
	}

	public static String getStudentImage(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("iamge", "");
	}

	/**
	 * 保存中文简历完整度数据
	 * 
	 * @param context
	 * @param complete_rate
	 * @return
	 */
	public static void saveComplete_rate(Context context, String complete_rate) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("complete_rate", complete_rate);
		editor.commit();
	}

	public static String getComplete_rate(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("complete_rate", "");
	}

	public static void saveComplete_rate_en(Context context,
			String complete_rate) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("complete_rate_en", complete_rate);
		editor.commit();
	}

	public static String getComplete_rate_en(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("complete_rate_en", "");
	}

	/**
	 * 保存 获取是否是猎头
	 * 
	 * @param context
	 * @param isHunter
	 */
	public static void saveIf_is_hunter(Context context, String isHunter) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("is_hunter", isHunter);
		editor.commit();
	}

	public static String getIf_is_hunter(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("is_hunter", "");
	}

	/**
	 * 保存 获取猎头id
	 * 
	 * @param context
	 * @param hunter_id
	 */
	public static void save_hunter_id(Context context, String hunter_id) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("hunter_id", hunter_id);
		editor.commit();
	}

	public static String get_hunter_id(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("hunter_id", "");
	}

	/**
	 * 保存上次获取token的时间
	 * 
	 * @param context
	 * @param oldTime
	 */
	public static void saveOldGetTokenTime(Context context, long oldTime) {
		// 存本地时间，两个本地时间相比较减小误差
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		// editor.putLong("oldTime", oldTime);
		editor.putLong("oldTime", DateUtils.getCurrentTimeSecond());
		editor.commit();
	}

	public static long getOldGetTokenTime(Context context) {
		try {
			userPreferences = context.getSharedPreferences("userinfo",
					Context.MODE_PRIVATE);
			return userPreferences.getLong("oldTime", 0);
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 获取最近获取数据时间
	 * 
	 * @param context
	 * @return
	 */
	public static long getOldGetCityTime(Context context) {
		try {
			cityPrefrences = context.getSharedPreferences("cityTime",
					Context.MODE_PRIVATE);
			return cityPrefrences.getLong("oldCityTime", 0);
		} catch (Exception e) {
			return 0;
		}
	}

	public static void saveOldGetCityTime(Context context, long time) {
		cityPrefrences = context.getSharedPreferences("cityTime",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = cityPrefrences.edit();
		editor.putLong("oldCityTime", time);
		editor.commit();
	}

	/**
	 * 清楚用户信息
	 * 
	 * @param context
	 */
	public static void clearUserInfo(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.clear();
		editor.commit();
	}

	// public static void savePhoneNumber(Context context, String phoneNumber) {
	// userPreferences = context.getSharedPreferences("userinfo",
	// Context.MODE_PRIVATE);
	// SharedPreferences.Editor editor = userPreferences.edit();
	// editor.putString("phoneNumber", phoneNumber);
	// editor.commit();
	// }
	//
	// public static String getPhoneNumber(Context context) {
	// userPreferences = context.getSharedPreferences("userinfo",
	// Context.MODE_PRIVATE);
	// return userPreferences.getString("phoneNumber", "");
	// }

	/**
	 * 登陆账号：可以是邮箱也可以是手机号
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	public static void saveLoginAccount(Context context, String account) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putString("account", account);
		editor.commit();
	}

	public static String getLoginAccount(Context context) {
		userPreferences = context.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		return userPreferences.getString("account", "");
	}

	/**
	 * 保存是否是第一次打开
	 * 
	 * @param context
	 * @param isFirst
	 */
	public static void saveIsFirstOpen(Context context, boolean isFirst) {
		userPreferences = context.getSharedPreferences("installInfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putBoolean("isFirstOpen", isFirst);
		editor.commit();
	}

	public static boolean getIsFirstOpen(Context context) {
		userPreferences = context.getSharedPreferences("installInfo",
				Context.MODE_PRIVATE);
		return userPreferences.getBoolean("isFirstOpen", true);
	}

	/**
	 * 保存是否是第一次登陆
	 * 
	 * @param context
	 * @param isFirst
	 */
	public static void saveIsFirstLogin(Context context, boolean isFirst) {
		userPreferences = context.getSharedPreferences("installInfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putBoolean("isFirstLogin", isFirst);
		editor.commit();
	}

	public static boolean getIsFirstLogin(Context context) {
		userPreferences = context.getSharedPreferences("installInfo",
				Context.MODE_PRIVATE);
		return userPreferences.getBoolean("isFirstLogin", true);
	}

	/**
	 * 保存是否是第一次进入人才库
	 * 
	 * @param context
	 * @param isFirst
	 */
	public static void saveIsFirstEnterTalentBank(Context context,
			boolean isFirst) {
		userPreferences = context.getSharedPreferences("installInfo",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = userPreferences.edit();
		editor.putBoolean("isFirstEnterTalentBank", isFirst);
		editor.commit();
	}

	public static boolean getIsFirstEnterTalentBank(Context context) {
		userPreferences = context.getSharedPreferences("installInfo",
				Context.MODE_PRIVATE);
		return userPreferences.getBoolean("isFirstEnterTalentBank", false);
	}

	/**
	 * 保存底部导航点击的位置
	 * 
	 * @param context
	 * @param index
	 */
	public static void saveIndex(Context context, int index) {
		tabPositionPreferences = context.getSharedPreferences("tabindex",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = tabPositionPreferences.edit();
		editor.putInt("index", index);
		editor.commit();
	}

	public static int getIndex(Context context) {
		tabPositionPreferences = context.getSharedPreferences("tabindex",
				Context.MODE_PRIVATE);
		return tabPositionPreferences.getInt("index", 0);
	}

	public static void clearTabIndex(Context context) {
		tabPositionPreferences = context.getSharedPreferences("tabindex",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = tabPositionPreferences.edit();
		editor.clear();
		editor.commit();
	}

	public static void saveUrl(Context context, String url) {
		urlPreferences = context.getSharedPreferences("url",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = urlPreferences.edit();
		editor.putString("url", url);
		editor.commit();
	}

	public static String getUrl(Context context) {
		urlPreferences = context.getSharedPreferences("url",
				Context.MODE_PRIVATE);
		return urlPreferences.getString("url", "");
	}

}

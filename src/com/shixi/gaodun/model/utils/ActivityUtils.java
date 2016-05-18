package com.shixi.gaodun.model.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.WindowManager;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.db.SearchDB;

/**
 * 常用的工具类
 * 
 * @author guolin
 */
public class ActivityUtils {
	/**
	 * 判断某个activity是否正在运行
	 * 
	 * @param mContext
	 * @param activityClassName
	 * @return
	 */
	public static boolean isActivityRunning(Context mContext,
			String activityClassName) {
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> info = activityManager.getRunningTasks(1);
		if (info != null && info.size() > 0) {
			ComponentName component = info.get(0).topActivity;
			if (activityClassName.equals(component.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * dp转换成px数值
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				res.getDisplayMetrics());
	}

	/**
	 * px换成dp数值
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/** 获取屏幕的宽度 */
	@SuppressWarnings("deprecation")
	public static int getScreenWidth() {
		WindowManager manager = (WindowManager) BaseApplication.mApp
				.getSystemService(Context.WINDOW_SERVICE);
		return manager.getDefaultDisplay().getWidth();
		// Point point = new Point();
		// manager.getDefaultDisplay().getSize(point);
		// return point.x;
	}

	/***
	 * 获取屏幕的高度
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int getScreenHeight() {
		WindowManager manager = (WindowManager) BaseApplication.mApp
				.getSystemService(Context.WINDOW_SERVICE);
		return manager.getDefaultDisplay().getHeight();
	}

	/**
	 * 获取手机唯一标识码
	 * 
	 * @return
	 */
	public static String getIMEI() {
		TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.mApp
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		System.out.println("手机imei" + imei);
		return imei;
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public static int getVersionCode() {
		PackageInfo pi = null;
		try {
			PackageManager packageManager = BaseApplication.mApp
					.getPackageManager();
			pi = packageManager.getPackageInfo(
					BaseApplication.mApp.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pi.versionCode;
	}

	/** 检测网络 **/
	public static boolean isNetAvailable() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) BaseApplication.mApp
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/** 检测网络是否处于wifi状态下 **/
	public static boolean isWiFiNetAvailable(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 获取版本名称
	 * 
	 * @return
	 */
	public static String getVersionName() {
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = BaseApplication.mApp
					.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					BaseApplication.mApp.getPackageName(), 0);
			return packInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 判断当前设备是否存在sd卡
	 * 
	 * @return
	 */
	public static boolean isSDCardHas() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 通过判断本地广播管理器是否为空来获取本地广播管理器
	 * 
	 * @param context
	 */
	public static void getLocalBroadcastManager(Context context) {
		if (null == BaseApplication.mApp.mLocalBroadcastManager)
			BaseApplication.mApp.mLocalBroadcastManager = LocalBroadcastManager
					.getInstance(context);

	}

	/**
	 * 清楚所有的缓存
	 */
	public static void clearAllData(Context context) {
		CacheUtils.clearUserInfo(context);
		CacheUtils.clearTabIndex(context);
		SearchDB db = new SearchDB(context);
		db.dropTable();
	}

	/**
	 * 十进制数转换为16进制颜色
	 * 
	 * @param def百分比
	 * @return
	 */
	public static String decimalNumeralToSexadecimal(float def, int colormax,
			String color, String pinStr) {
		int result = (int) (def * colormax);
		return pinStr + Integer.toHexString(result) + color;
	}

	/**
	 * 十进制数转换为16进制颜色
	 * 
	 * @param def百分比
	 * @return
	 */
	public static int decimalNumeralToSexadecimal(float def, int colormax) {
		int result = (int) (def * colormax);
		String resultStr = Integer.toHexString(result);
		return Integer.parseInt(resultStr);
	}

	public static int getImageWidth(int allmargin, int numColumns,
			int horizontalSpacing, Context context) {
		int viewwidth = getScreenWidth() - dip2px(context, allmargin)
				- dip2px(context, (numColumns - 1) * horizontalSpacing);
		return viewwidth / numColumns;
	}

	/**
	 * 检查某应用是否安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 检查某应用是否安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean checkApkAviliable(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		List<String> pName = new ArrayList<String>();// 用于存储所有已安装程序的包名
		// 从pinfo中将包名字逐一取出，压入pName list中
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);// 判断pName中是否有目标程序的包名，有TRUE，没有FALSE
	}

	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}

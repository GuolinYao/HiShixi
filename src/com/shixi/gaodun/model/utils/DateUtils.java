package com.shixi.gaodun.model.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;

import com.shixi.gaodun.base.BaseApplication;

/**
 * 时间工具类
 * 
 * @author guolin ronger
 * @date:2015-10-22 上午9:48:17
 */
public class DateUtils {
	/**
	 * 获取系统当前 按指定格式返回
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate() {
		 Date date = new Date();
		// String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		String time = simpleDateFormat.format(date);
//		System.out.println(time);
		// 当前毫秒数
		// long currentTime = date.getTime();
		// 保存上次获取token时的时间
		// CacheUtils.saveOldGetTokenTime(BaseApplication.mApp,
		// String.valueOf(currentTime));
		// Log.e("gaodun", "当前时间：" + time + "毫秒数:" + currentTime);
		return time;
	}

	/**
	 * 获取当前时间的描述
	 * 
	 * @return
	 */
	public static Long getCurrentTimeSecond() {
		Date currDate = new Date();
		long currTime = currDate.getTime();
		long currTimeSecond = currTime / 1000;
		return currTimeSecond;
	}

	/**
	 * 获取时间间隔，接口规定token两小时内失效:最好是提前一点时间获取token,减小程序运行时间差 这里提前10分钟获取token
	 * 
	 * @param d
	 * @return
	 */
	public static boolean getTimeInterval() {
		Date currDate = new Date(); // 当前时间
		long currTime = currDate.getTime(); // 当前时间
		long oldTime = CacheUtils.getOldGetTokenTime(BaseApplication.mApp);// 上次获取token时的时间
		// 获取新时间的小时对应的秒数
		long currTimeSecond = currTime / 1000;
		long intervalSecond = Math.abs(oldTime - currTimeSecond);
		// 上次获取token时间与现在时间的分钟差
		int intervalMinute = (int) (intervalSecond / 60);
		if (intervalMinute >= 110) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param desplayHour
	 * @param oldTime
	 * @return false未过期，true过期
	 */
	public static boolean getTimeInterval(float desplayHour, long oldTime) {
		Date currDate = new Date(); // 当前时间
		long currTime = currDate.getTime(); // 当前时间
		// 获取新时间的小时对应的秒数
		long currTimeSecond = currTime / 1000;
		long intervalSecond = Math.abs(oldTime - currTimeSecond);
		// 上次获取时间与现在时间的分钟差
		int intervalMinute = (int) (intervalSecond / 60);
		if (intervalMinute >= desplayHour * 60) {
			return true;
		}
		return false;
	}

	/**
	 * 返回当前年份
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 比较开始时间和结束时间谁大 开始时间大于结束时间return true 开始时间小于结束时间return false
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean DateCompare(String startTime, String endTime) {
		// 将.替换为-防止用到UI显示时用到的时间格式
		startTime = startTime.replace(".", "-");
		endTime = endTime.replace(".", "-");
		try {
			// 设定时间的模板
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// 得到指定模范的时间
			Date d1 = sdf.parse(startTime);
			Date d2 = sdf.parse(endTime);
			long d = d1.getTime() - d2.getTime();
			if (d > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * 获取只显示年月的格式的时间：如:2015.06 pinStr为.
	 * 
	 * @param time
	 * @param pinStr
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getYearAndMonthFormatTime(String time, String pinStr,
			String splitStr) {
		if (StringUtils.isEmpty(time))
			return "";
		String[] startArray = time.split(splitStr);
		if (startArray.length == 3) {
			return startArray[0] + pinStr + startArray[1];
		}
		return time;
	}
}

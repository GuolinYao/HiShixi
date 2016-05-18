package com.shixi.gaodun.model.utils;

/**
 * 数字常用类如数字比较转换等
 * 
 * @author ronger
 * @date:2015-11-6 下午1:25:39
 */
public class NumberUtils {

	/**
	 * 将字符串转换为float类型
	 * 
	 * @param str
	 * @return
	 */
	public static float getFloat(String str, float defaultValue) {
		return StringUtils.isEmpty(str) ? defaultValue : Float.parseFloat(str);
	}

	public static int getInt(String str, int defaultValue) {
		return StringUtils.isEmpty(str) ? defaultValue : Integer.parseInt(str);
	}

	public static String getString(String str, String defaultValue) {
		return StringUtils.isEmpty(str) ? defaultValue : str;
	}

	public static String getPinString(String str, String pinStr) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			sb.append(pinStr);
		}
		String pinSb = sb.toString();
		// 连着的pin
		if (str.lastIndexOf(pinSb) == str.length() - 2) {
			str = str.substring(0, str.lastIndexOf(pinSb));
		}
		if (str.indexOf(pinStr) == 0) {// 删除第一个
			str = str.substring(1);
		}
		if (str.contains(pinStr + pinStr)) {// 删除中间重复的
			str = str.replace(pinStr + pinStr, pinStr);
		}

		return str;
	}

	public static String getReplaceStr(String str, String oldChar, String newChar, String defaultStr) {
		if (StringUtils.isEmpty(str))
			return defaultStr;
		str = str.replace(oldChar, newChar);
		return str;
	}
}

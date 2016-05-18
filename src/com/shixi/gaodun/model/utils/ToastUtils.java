package com.shixi.gaodun.model.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.model.global.Finals;

/**
 * @author ronger
 * @date:2015-10-19 下午3:47:26
 */
public class ToastUtils {
	public static Toast toast, strToast;

	/**
	 * 默认只带文字的toast
	 * 
	 * @param str
	 */
	@SuppressLint("ShowToast")
	public static void showToastInCenter(String str) {
		if (strToast == null) {
			strToast = Toast.makeText(BaseApplication.mApp, str, Finals.toastTime);
		}
		strToast.setText(str);
		strToast.setGravity(Gravity.CENTER, 0, 0);
		strToast.show();
	}

	/**
	 * 自定义的toast弹框，一般用来提交成功的提示
	 * 
	 * @param str
	 * @param context
	 */
	@SuppressLint("InflateParams")
	public static void showCustomToastInCenter(String str, Context context) {
		if (toast == null) {
			toast = new Toast(context);
		}
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View view = layoutInflater.inflate(R.layout.activity_base_toast, null);
		TextView toastDec = (TextView) view.findViewById(R.id.tv_toast_successdec);
		toastDec.setText(str);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(2000);
		toast.setView(view);
		toast.show();
	}
}

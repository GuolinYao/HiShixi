package com.shixi.gaodun.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.ActivityUtils;

/**
 * 
 * @author guolinyao
 * @date 2016年2月29日 上午10:35:04
 */
public class ActionSheetDialog {
	public interface OnAlertSelectId {
		void onClick(int whichButton);
	}

	public static Dialog showShareAlert(final Context context,
			final OnAlertSelectId alertDo) {
		return showShareAlert(context, null, alertDo);
	}

	/**
	 * 第二种对话框
	 * 
	 * @param context
	 * @param alertDo
	 * @return
	 */
	public static Dialog showShareAlert2(final Context context,
			final OnAlertSelectId alertDo) {
		return showShareAlert2(context, null, alertDo);
	}

	public static Dialog showAlert(final Context context,
			final OnAlertSelectId alertDo, String selectOne, String selectTwo) {
		return showAlert(context, null, alertDo, selectOne, selectTwo);
	}

	@SuppressLint("InflateParams")
	public static Dialog showAlert(final Context context,
			OnCancelListener cancelListener, final OnAlertSelectId alertDo,
			String selectOne, String selectTwo) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.action_sheet_layout, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		final Button cancelBtn = (Button) layout
				.findViewById(R.id.btn_select_cancel);
		final Button oneBtn = (Button) layout.findViewById(R.id.btn_select_one);
		final Button twoBtn = (Button) layout.findViewById(R.id.btn_select_two);
		oneBtn.setText(selectOne);
		twoBtn.setText(selectTwo);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		oneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDo.onClick(1);
				dlg.dismiss();
			}
		});

		twoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDo.onClick(2);
				dlg.dismiss();
			}
		});
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		int width = ActivityUtils.getScreenWidth()
				- ActivityUtils.dip2px(context, 20);
		LayoutParams params = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
		dlg.setContentView(layout, params);
		// dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 分享框
	 * 
	 * @param context
	 * @param cancelListener
	 * @param alertDo
	 * @return
	 */
	public static Dialog showShareAlert(final Context context,
			OnCancelListener cancelListener, final OnAlertSelectId alertDo) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.share_dialog_layout, null);
		final int cFullFillWidth = 10000;
		boolean wxIsAviliable = false;
		boolean qqIsAviliable = false;
		boolean wbIsAviliable = false;
		layout.setMinimumWidth(cFullFillWidth);

		final TextView weixin = (TextView) layout
				.findViewById(R.id.tv_share_weixin);
		final TextView friendCircle = (TextView) layout
				.findViewById(R.id.tv_share_friend_circle);
		final TextView qq = (TextView) layout.findViewById(R.id.tv_share_qq);
		final TextView sinaweibo = (TextView) layout
				.findViewById(R.id.tv_share_sina);
		final Button cancel = (Button) layout
				.findViewById(R.id.btn_select_cancel);

		View mask1 = (View) layout.findViewById(R.id.mask1);
		View mask2 = (View) layout.findViewById(R.id.mask2);
		View mask3 = (View) layout.findViewById(R.id.mask3);
		View mask4 = (View) layout.findViewById(R.id.mask4);
		// 查看微信 qq 新浪微博是否安装
		if (ActivityUtils.checkApkAviliable(context, "com.tencent.mm")) {
			wxIsAviliable = true;
			weixin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(1);
					dlg.dismiss();
				}
			});
			friendCircle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(2);
					dlg.dismiss();
				}
			});
		} else {
			mask1.setVisibility(View.VISIBLE);
			mask2.setVisibility(View.VISIBLE);
			weixin.setClickable(false);
			friendCircle.setClickable(false);
		}

		if (ActivityUtils.checkApkAviliable(context, "com.tencent.mobileqq")) {
			qqIsAviliable = true;
			qq.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(3);
					dlg.dismiss();
				}
			});

		} else {
			mask3.setVisibility(View.VISIBLE);
		}

		if (ActivityUtils.checkApkAviliable(context, "com.sina.weibo")) {
			wbIsAviliable = true;

			sinaweibo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(4);
					dlg.dismiss();
				}
			});
		} else {
			mask4.setVisibility(View.VISIBLE);
		}

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 分享框2
	 * 
	 * @param context
	 * @param cancelListener
	 * @param alertDo
	 * @return
	 */
	public static Dialog showShareAlert2(final Context context,
			OnCancelListener cancelListener, final OnAlertSelectId alertDo) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.share_dialog2_layout, null);
		final int cFullFillWidth = 10000;
		boolean wxIsAviliable = false;
		boolean qqIsAviliable = false;
		boolean wbIsAviliable = false;
		layout.setMinimumWidth(cFullFillWidth);

		final TextView weixin = (TextView) layout
				.findViewById(R.id.tv_share_weixin);
		final TextView friendCircle = (TextView) layout
				.findViewById(R.id.tv_share_friend_circle);
		final TextView qq = (TextView) layout.findViewById(R.id.tv_share_qq);
		final TextView sinaweibo = (TextView) layout
				.findViewById(R.id.tv_share_sina);
		final TextView copyLink = (TextView) layout.findViewById(R.id.tv_copy);
		final Button cancel = (Button) layout
				.findViewById(R.id.btn_select_cancel);
		View mask1 = (View) layout.findViewById(R.id.mask1);
		View mask2 = (View) layout.findViewById(R.id.mask2);
		View mask3 = (View) layout.findViewById(R.id.mask3);
		View mask4 = (View) layout.findViewById(R.id.mask4);
		// 查看微信 qq 新浪微博是否安装
		if (ActivityUtils.checkApkAviliable(context, "com.tencent.mm")) {
			wxIsAviliable = true;
			weixin.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(1);
					dlg.dismiss();
				}
			});
			friendCircle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(2);
					dlg.dismiss();
				}
			});
		} else {
			mask1.setVisibility(View.VISIBLE);
			mask2.setVisibility(View.VISIBLE);
			weixin.setClickable(false);
			friendCircle.setClickable(false);
		}

		if (ActivityUtils.checkApkAviliable(context, "com.tencent.mobileqq")) {
			qqIsAviliable = true;
			qq.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(3);
					dlg.dismiss();
				}
			});

		} else {
			mask3.setVisibility(View.VISIBLE);
		}

		if (ActivityUtils.checkApkAviliable(context, "com.sina.weibo")) {
			wbIsAviliable = true;

			sinaweibo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDo.onClick(4);
					dlg.dismiss();
				}
			});
		} else {
			mask4.setVisibility(View.VISIBLE);
		}

		copyLink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDo.onClick(5);
				dlg.dismiss();
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});

		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}
}

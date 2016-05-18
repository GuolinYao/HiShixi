package com.shixi.gaodun.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.shixi.gaodun.R;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.model.utils.ActivityUtils;

/**
 * @author ronger
 * @date:2015-12-2 上午9:36:11
 */
public class InvitationActionSheetDialog {
	@SuppressLint("InflateParams")
	public static Dialog showAlert(final Context context, final OnclickSelectedIdCallBack alertDo, String name,
			int color) {
		final Dialog dlg = new Dialog(context, R.style.MMTheme);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.invitation_actionsheet_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		final Button cancelBtn = (Button) layout.findViewById(R.id.btn_select_cancel);
		final Button selectBtn = (Button) layout.findViewById(R.id.btn_select);
		selectBtn.setText(name);
		selectBtn.setTextColor(color);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		selectBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDo.onClickCallBack(R.id.btn_select);
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
		int width = ActivityUtils.getScreenWidth() - ActivityUtils.dip2px(context, 20);
		LayoutParams params = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
		dlg.setContentView(layout, params);
		dlg.show();
		return dlg;
	}
}

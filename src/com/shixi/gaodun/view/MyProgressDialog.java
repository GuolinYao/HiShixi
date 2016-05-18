package com.shixi.gaodun.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shixi.gaodun.R;

/**
 * 正在加载时控件
 * 
 * @author ronger guolin
 * @date:2015-10-19 上午11:48:16
 */
public class MyProgressDialog {
	private Dialog mProgressDialog;
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private View mProgressView;
	private TextView mLoadingText;

	public MyProgressDialog(Context context) {
		super();
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mProgressView = mLayoutInflater.inflate(R.layout.activity_base_loading, null);
		mLoadingText = (TextView) mProgressView.findViewById(R.id.tv_progress_title);
		mProgressDialog = createPD();
	}

	public Dialog createPD() {
		if (null != mProgressDialog)
			return mProgressDialog;
		Dialog progressdialog = new Dialog(mContext, R.style.Dialog);
		progressdialog.setContentView(mProgressView);
		progressdialog.setCanceledOnTouchOutside(true);//TODO 设置对话框以外区域可点击
		progressdialog.setCancelable(true);//设置点击对话框以外区域，对话框消失
		return progressdialog;
	}

	public void show() {
		try {
			if (null != mProgressDialog && !mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
		} catch (Exception e) {
		}

	}

	public void dismiss() {
		if (null != mProgressDialog && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	public void setTitle(String title) {
		if (TextUtils.isEmpty(title))
			mLoadingText.setText("加载中");
		else
			mLoadingText.setText(title);
	}
}

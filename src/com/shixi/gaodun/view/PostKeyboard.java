package com.shixi.gaodun.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.inf.imp.SoftKeyboardStateHelper;
import com.shixi.gaodun.inf.imp.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;

/**
 * 发表的底部布局
 * 
 * @author ronger
 * @date:2015-11-24 下午1:31:39
 */
public class PostKeyboard extends RelativeLayout implements SoftKeyboardStateListener {
	private Context mContext;
	private SoftKeyboardStateHelper mKeyboardHelper;
	public static final int LAYOUT_TYPE_HIDE = 0;
	// public static final int LAYOUT_TYPE_FACE = 1;
	public static final int LAYOUT_TYPE_MORE = 2;
	private int layoutType = LAYOUT_TYPE_HIDE;
	// 最上层选择框
	private RelativeLayout mBtnFace;// 表情
	private RelativeLayout mBtnMore;// 更多
	private CheckBox mBtnAnonymity;// 是否匿名
	private TextView mTextNumber;// 已输入字数
	private Dialog mDialog;
	private OnAlertSelectId mActionDialogListener;

	public boolean isShowing() {
		if (null != mDialog && mDialog.isShowing()) {
			return true;
		}
		return false;
	}

	public void setMoreBtnEnabled(boolean enabled) {
		mBtnMore.setEnabled(enabled);
	}

	public PostKeyboard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PostKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PostKeyboard(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化布局
	 * 
	 * @param context
	 */
	public void init(Context context) {
		this.mContext = context;
		View root = View.inflate(context, R.layout.single_touch_view_layout, null);
		this.addView(root);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initDate();
		initWidget();
	}

	public void initDate() {
		mKeyboardHelper = new SoftKeyboardStateHelper(((Activity) getContext()).getWindow().getDecorView());
		mKeyboardHelper.addSoftKeyboardStateListener(this);
	}

	public void initWidget() {
		mBtnFace = (RelativeLayout) findViewById(R.id.layout_face);
		mBtnMore = (RelativeLayout) findViewById(R.id.layout_photo);
		mBtnAnonymity = (CheckBox) findViewById(R.id.checkbox_anonymity);
		mTextNumber = (TextView) findViewById(R.id.text_number);
		// 一期暂时不做表情，所以这里不需要初始化表情页，后期若需要则在这里添加
		mBtnFace.setVisibility(View.GONE);
		// 设置点击事件
		mBtnMore.setOnClickListener(getFunctionBtnListener(LAYOUT_TYPE_MORE));
	}

	/**
	 * 内部方法
	 * 
	 * @param which
	 * @return
	 */
	private OnClickListener getFunctionBtnListener(final int which) {
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		};
	}

	/**
	 * 显示对话框
	 */
	public void showDialog() {
		mDialog = ActionSheetDialog.showAlert(mContext, mActionDialogListener, "拍照", "手机相册");
	}

	/**
	 * 显示软键盘
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public static void showKeyboard(Context context) {
		Activity activity = (Activity) context;
		if (activity != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public void hideKeyboard(Context context) {
		Activity activity = (Activity) context;
		if (activity != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive() && activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
			}
		}
	}

	public CheckBox getmBtnAnonymity() {
		return mBtnAnonymity;
	}

	public void setmBtnAnonymity(CheckBox mBtnAnonymity) {
		this.mBtnAnonymity = mBtnAnonymity;
	}

	public TextView getmTextNumber() {
		return mTextNumber;
	}

	public void setmTextNumber(String str) {
		if (null != mTextNumber)
			this.mTextNumber.setText(str);
	}

	/**
	 * 打开键盘
	 */
	@Override
	public void onSoftKeyboardOpened(int keyboardHeightInPx) {
		// 显示该view
		setVisibility(View.VISIBLE);
	}

	/**
	 * 关闭键盘
	 */
	@Override
	public void onSoftKeyboardClosed() {
		// 隐藏该view
		setVisibility(View.GONE);
	}

	public OnAlertSelectId getmActionDialogListener() {
		return mActionDialogListener;
	}

	public void setmActionDialogListener(OnAlertSelectId mActionDialogListener) {
		this.mActionDialogListener = mActionDialogListener;
	}

}

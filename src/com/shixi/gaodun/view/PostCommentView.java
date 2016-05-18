package com.shixi.gaodun.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.shixi.gaodun.R;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.inf.imp.SoftKeyboardStateHelper;
import com.shixi.gaodun.inf.imp.SoftKeyboardStateHelper.SoftKeyboardStateListener;
import com.shixi.gaodun.model.domain.CommentInvitationBean;
import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * @author ronger
 * @date:2015-12-1 下午3:14:22
 */
public class PostCommentView extends LinearLayout implements SoftKeyboardStateListener, TextWatcher {
	private Context mContext;
	private LinearLayout mAnonymityLayout;
	private CheckBox mBtnAnonymity;// 是否匿名
	private TextView mTextNumber;// 已输入字数
	private EditText mEditContent;
	private Button mBtnSend;
	private SoftKeyboardStateHelper mKeyboardHelper;// 键盘监听器助手
	private View root;
	private OnClickListener mClickListener;
	private CommentInvitationBean mCurrentCommentBean;// 当前选中的评论信息
	private FrameLayout mFrameLayout;
	private OnclickSelectedIdCallBack mSendCallBack;

	public void setSendCallBack(OnclickSelectedIdCallBack callBack) {
		this.mSendCallBack = callBack;
	}

	public CommentInvitationBean getmCurrentCommentBean() {
		return mCurrentCommentBean;
	}

	public void setmCurrentCommentBean(CommentInvitationBean mCurrentCommentBean) {
		this.mCurrentCommentBean = mCurrentCommentBean;
	}

	public void setEditextHint(String str) {
		mEditContent.setHint(str);
	}

	public CheckBox getmBtnAnonymity() {
		return mBtnAnonymity;
	}

	public EditText getmEditContent() {
		return mEditContent;
	}

	/**
	 * 清空编辑的数据
	 */
	public void clearEditContent() {
		mEditContent.setText("");
	}

	public Button getmBtnSend() {
		return mBtnSend;
	}

	public void setmClickListener(OnClickListener mClickListener) {
		this.mClickListener = mClickListener;
		mBtnSend.setOnClickListener(mClickListener);
	}

	@SuppressLint("NewApi")
	public PostCommentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PostCommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PostCommentView(Context context) {
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
		root = View.inflate(context, R.layout.post_comment_layout, null);
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
		mAnonymityLayout = (LinearLayout) findViewById(R.id.layout_anonymity);
		mBtnAnonymity = (CheckBox) findViewById(R.id.checkbox_anonymity);
		mTextNumber = (TextView) findViewById(R.id.text_number);
		mEditContent = (EditText) findViewById(R.id.edit_content);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setVisibility(View.GONE);
		mEditContent.setHint("输入评论内容...");
		mEditContent.addTextChangedListener(this);
		mEditContent.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// IME_ACTION_SEND发送
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					mSendCallBack.onClickCallBack(R.id.btn_send);
				}
				return false;
			}
		});
		// mEditContent.addTextChangedListener(new MaxLengthWatcher(140, mEditContent, mTextNumber, "最多可输入140字"));
	}

	@Override
	public void onSoftKeyboardOpened(int keyboardHeightInPx) {
		mAnonymityLayout.setVisibility(View.VISIBLE);
		setFragmentHideOrShow(View.VISIBLE);
		mEditContent.setBackgroundResource(R.drawable.edit_bg);
	}

	@Override
	public void onSoftKeyboardClosed() {
		mAnonymityLayout.setVisibility(View.GONE);
		setFragmentHideOrShow(View.GONE);
		mEditContent.setBackgroundResource(R.drawable.hui_edit_bg);
		mCurrentCommentBean = null;
		setEditextHint("输入评论内容...");
	}

	public void setFrameLayout(FrameLayout framentLayout) {
		mFrameLayout = framentLayout;
	}

	public void setFragmentHideOrShow(int visibility) {
		if (null != mFrameLayout)
			mFrameLayout.setVisibility(visibility);
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

	/**
	 * 显示软键盘
	 */
	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	public void showKeyboard(Context context) {
		Activity activity = (Activity) context;
		if (activity != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Editable editable = mEditContent.getText();
		setTextViewShow(editable);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	public void setTextViewShow(Editable editable) {
		int len = editable.length();
		if (len > 0) {
			mBtnSend.setEnabled(true);
		} else {
			mBtnSend.setEnabled(false);
		}
		if (len > 140) {
			ToastUtils.showToastInCenter("字数超过限制");
			int selEndIndex = Selection.getSelectionEnd(editable);
			String str = editable.toString();
			// 截取新字符串
			String newStr = str.substring(0, 140);
			mEditContent.setText(newStr);
			editable = mEditContent.getText();
			// 新字符串的长度
			int newLen = editable.length();
			// 旧光标位置超过字符串长度
			if (selEndIndex > newLen) {
				selEndIndex = editable.length();
			}
			// 设置新光标所在的位置
			Selection.setSelection(editable, selEndIndex);
			return;
		}
		if (null != mTextNumber)
			mTextNumber.setText(len + "/" + 140);
	}
}

package com.shixi.gaodun.inf;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * 对长度超过指定长度的处理
 * 
 * @author ronger
 * @date:2015-11-5 下午3:38:03
 */
public class MaxLengthWatcher implements TextWatcher {
	// 指定最大可输入长度
	private int maxLen = 0;
	// 对应的编辑框
	private EditText editText = null;
	// 需要更新的textview
	private TextView textView = null;

	// 需要提示的文本
	private String mWarnStr = "字数超过限制";

	public MaxLengthWatcher(int maxLen, EditText editText, TextView textView) {
		this.maxLen = maxLen;
		this.editText = editText;
		this.textView = textView;
	}

	public MaxLengthWatcher(int maxLen, EditText editText, TextView textView, String warnStr) {
		this.maxLen = maxLen;
		this.editText = editText;
		this.mWarnStr = warnStr;
		this.textView = textView;
	}

	public MaxLengthWatcher(int maxLen, EditText editText, String warnStr) {
		this.maxLen = maxLen;
		this.editText = editText;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Editable editable = editText.getText();
		setTextViewShow(editable);
		// if (null != textView) {
		// setTextViewShow(editable);
		// } else {
		// setExceedLenthShow(editable);
		// }
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	public void setTextViewShow(Editable editable) {
		int len = editable.length();
		if (len > maxLen) {
			ToastUtils.showToastInCenter("字数超过限制");
			int selEndIndex = Selection.getSelectionEnd(editable);
			String str = editable.toString();
			// 截取新字符串
			String newStr = str.substring(0, maxLen);
			editText.setText(newStr);
			editable = editText.getText();
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
		if (null != textView)
			textView.setText(len + "/" + maxLen);
	}

	public void setExceedLenthShow(Editable editable) {
		int len = editable.length();
		if (len > maxLen) {
			ToastUtils.showToastInCenter(mWarnStr);
			int selEndIndex = Selection.getSelectionEnd(editable);
			String str = editable.toString();
			// 截取新字符串
			String newStr = str.substring(0, maxLen);
			editText.setText(newStr);
			editable = editText.getText();
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
	}
}

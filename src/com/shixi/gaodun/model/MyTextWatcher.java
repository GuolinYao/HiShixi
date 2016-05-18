package com.shixi.gaodun.model;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * @author ronger
 * @date:2015-11-24 下午4:49:43
 */
public abstract class MyTextWatcher implements TextWatcher {
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	// public abstract void setBtnEnabled(int maxLen, TextView textView, EditText editText, String warnStr);

	public void setTextViewShow(int maxLen, TextView textView, EditText editText, String warnStr) {
		Editable editable = editText.getText();
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
		textView.setText(len + "/" + maxLen);
	}

}

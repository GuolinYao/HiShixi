package com.shixi.gaodun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 通过制定长度来显示textview多余的用省略号表示
 * 
 * @author ronger
 * @date:2015-11-2 上午10:53:57
 */
public class ByLengthSetShowTextView extends TextView {
	// 默认指定显示8个长度后显示对应的省略号
	private int default_textlength = 8;
	// 默认制定显示default_textlength长度后拼接default_textPin
	private String default_textPin = "...";
	// 默认值
	private final String default_value = "";
	// 设置值
	private String value = default_value;

	public ByLengthSetShowTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ByLengthSetShowTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ByLengthSetShowTextView(Context context) {
		super(context);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		text = setShowText(text);
		super.setText(text, type);
	}

	// @Override
	// protected void onDraw(Canvas canvas) {
	// String str = getText().toString();
	// setText(setShowText(str));
	// // super.onDraw(canvas);
	// }

	public int getDefault_textlength() {
		return default_textlength;
	}

	public void setDefault_textlength(int default_textlength) {
		this.default_textlength = default_textlength;
	}

	public String getDefault_textPin() {
		return default_textPin;
	}

	public void setDefault_textPin(String default_textPin) {
		this.default_textPin = default_textPin;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 得到需要显示的字符串 仅限汉字
	 * 
	 * @param value
	 */
	public String setShowText(CharSequence text) {
		if (StringUtils.isEmpty(value))
			return "";
		if (StringUtils.isHanzi(text.toString()) && value.length() > 8) {
			StringBuilder sb = new StringBuilder();
			sb.append(value.substring(0, default_textlength)).append(default_textPin);
			return sb.toString();
		}
		return value;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
}

package com.shixi.gaodun.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.shixi.gaodun.R;

/**
 * 自定义的EditText,主要用来在获取焦点时显示可清除已编辑数据的按钮，点击按钮可清除已编辑数据
 *
 * @author ronger
 * @date:2015-10-19 下午5:19:34
 */
@SuppressLint("ClickableViewAccessibility")
public class ClearEditTextView extends EditText implements OnFocusChangeListener, TextWatcher {
    // 删除按钮
    private Drawable mClearDrawable;

    // 控件是否有焦点
    private boolean hasFocus;

    private boolean ifCanClear = true;

    public ClearEditTextView(Context context) {
        this(context, null);
    }

    public ClearEditTextView(Context context, AttributeSet attrs) {
        // 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        // 获取EditText的DrawableRight,假如没有设置就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (null == mClearDrawable) {
            mClearDrawable = getResources().getDrawable(R.drawable.icon_clear);
        }
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        // 默认设置图标为隐藏状态
        setClearIconVisible(false);
        // 设置焦点改变监听
        setOnFocusChangeListener(this);
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFocus && ifCanClear) {
            setClearIconVisible(text.length() > 0);
        }
    }

    public boolean isIfCanClear() {
        return ifCanClear;
    }

    public void setIfCanClear(boolean ifCanClear) {
        this.ifCanClear = ifCanClear;
    }

}

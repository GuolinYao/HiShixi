package com.shixi.gaodun.custom.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 自定义的标题
 * author：ronger on 2016/1/15 09:14
 * email：dengyarong@gaodun.cn
 */
public class TopBar extends RelativeLayout{
    //左按钮、右按钮，标题
    private Button mLeftButton,mRightButton;
    private TextView mTitleView;
    //布局属性，用来控制元素在ViewGroup中的位置
    private LayoutParams mLeftParams,mTitleParams,mRightParams;
    //左按钮的属性值，即在attrs中定义的
    private int mLeftTextColors;
    private Drawable mLeftBackground;
    private String mLeftText;
    //右按钮的属性，即在attrs中定义的
    private int mRightTextColors;
    private Drawable mRightBackground;
    private String mRightText;
    //标题的属性值
    private float mTitleTextSize;
    private int mTitleTextColor;
    private String mTitle;

    private TopBarClickListener mTopbarClickListener;
    public TopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TopBar(Context context) {
        super(context);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        mLeftTextColors=a.getColor(R.styleable.TopBar_leftTextColor,0);
        mLeftBackground = a.getDrawable(R.styleable.TopBar_leftBackground);
        mLeftText = a.getString(R.styleable.TopBar_leftText);
        mRightBackground = a.getDrawable(R.styleable.TopBar_rightBackground);
        mRightTextColors = a.getColor(R.styleable.TopBar_rightTextColor, 0);
        mRightText=a.getString(R.styleable.TopBar_rightText);
        mTitleTextSize=a.getDimension(R.styleable.TopBar_titleTextSize, 30);
        mTitleTextColor=a.getColor(R.styleable.TopBar_leftTextColor, 0);
        mTitle=a.getString(R.styleable.TopBar_title);
        a.recycle();
        mLeftButton=new Button(context);
        mRightButton=new Button(context);
        mTitleView=new TextView(context);
        setViewAttrs();
    }

    /**
     * 为组件设置属性
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setViewAttrs(){
        mLeftButton.setTextColor(mLeftTextColors);
        mLeftButton.setBackground(mLeftBackground);
        if(StringUtils.isNotEmpty(mLeftText))
         mLeftButton.setText(mLeftText);
        mRightButton.setTextColor(mLeftTextColors);
        mRightButton.setBackground(mRightBackground);
        if(StringUtils.isNotEmpty(mRightText))
            mRightButton.setText(mRightText);
        mTitleView.setTextSize(mTitleTextSize);
        mTitleView.setTextColor(mTitleTextColor);
        if(StringUtils.isNotEmpty(mTitle)){
            mTitleView.setText(mTitle);
        }
        mLeftParams=new LayoutParams( LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        mLeftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        addView(mLeftButton, mLeftParams);
        mRightParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        mRightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,TRUE);
        addView(mRightButton, mRightParams);
        mTitleParams=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        mTitleParams.addRule(RelativeLayout.CENTER_IN_PARENT,TRUE);
        addView(mTitleView, mTitleParams);
        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTopbarClickListener.leftClick();
            }
        });
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTopbarClickListener.rightClick();
            }
        });
    }

    /**
     * 设置按钮的显示与隐藏
     *
     * @param id
     * @param flag 是否显示
     */
    public void setVisible(int id,boolean flag){
        if(flag){
            if(id==0){
                mLeftButton.setVisibility(View.VISIBLE);
            }else{
                mRightButton.setVisibility(View.VISIBLE);
            }
        }else{
            if(id==0){
                mLeftButton.setVisibility(View.GONE);
            }else{
                mRightButton.setVisibility(View.GONE);
            }
        }
    }
    public void setTopBarClickListener(TopBarClickListener listener){
        this.mTopbarClickListener=listener;
    }
    /**
     * 回调的接口
     */
    public interface TopBarClickListener{
        //左按钮点击事件
        void leftClick();
        //右按钮点击事件
        void rightClick();
    }
}

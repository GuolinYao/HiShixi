package com.shixi.gaodun.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;

/**
 * 通用的标题
 * 
 * @author ronger
 * @date:2015-11-23 上午9:26:42
 */
public class TitleHeaderBar extends RelativeLayout {
	private TextView mCenterTitleTextView;
	private ImageView mLeftReturnImageView;
	private ImageView mRightViewImageView;
	private RelativeLayout mLeftViewContainer;
	private RelativeLayout mRightViewContainer;
	private RelativeLayout mCenterViewContainer;
	private RelativeLayout mViewContainer;
	private String mTitle;
	private int defaultWidth = -2, defaultHeight = -1;

	public TitleHeaderBar(Context context) {
		this(context, null);
	}

	public TitleHeaderBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleHeaderBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(getHeaderViewLayoutId(), this);
		mLeftViewContainer = (RelativeLayout) findViewById(R.id.ly_title_bar_left);
		mCenterViewContainer = (RelativeLayout) findViewById(R.id.ly_title_bar_center);
		mViewContainer = (RelativeLayout) findViewById(R.id.rl_base_header_bar);
		mRightViewContainer = (RelativeLayout) findViewById(R.id.ly_title_bar_right);
		mLeftReturnImageView = (ImageView) findViewById(R.id.iv_title_bar_left);
		mCenterTitleTextView = (TextView) findViewById(R.id.tv_title_bar_title);
		mRightViewImageView = (ImageView) findViewById(R.id.iv_title_bar_right);
	}

	protected int getHeaderViewLayoutId() {
		return R.layout.base_header_bar_title;
	}

	public ImageView getLeftImageView() {
		return mLeftReturnImageView;
	}

	public TextView getTitleTextView() {
		return mCenterTitleTextView;
	}

	public void setTitle(String title) {
		mTitle = title;
		mCenterTitleTextView.setText(title);
	}

	public String getTitle() {
		return mTitle;
	}

	public void setLeftImageView(int drawable) {
		mLeftReturnImageView.setImageResource(drawable);
	}

	public void setRightImageView(int drawable) {
		mRightViewImageView.setImageResource(drawable);
	}

	public void setTitleBarBackgroud(int resid) {
		mViewContainer.setBackgroundResource(resid);
	}

	public void setTitleBarTextColor(int color) {
		mCenterTitleTextView.setTextColor(color);
	}

	private RelativeLayout.LayoutParams makeLayoutParams(View view) {
		ViewGroup.LayoutParams lpOld = view.getLayoutParams();
		RelativeLayout.LayoutParams lp = null;
		if (lpOld == null) {
			lp = new RelativeLayout.LayoutParams(defaultWidth, defaultHeight);
		} else {
			lp = new RelativeLayout.LayoutParams(lpOld.width, lpOld.height);
		}
		return lp;
	}

	/**
	 * set customized view to left side
	 * 
	 * @param view
	 *            the view to be added to left side
	 */
	public void setCustomizedLeftView(View view) {
		mLeftReturnImageView.setVisibility(GONE);
		RelativeLayout.LayoutParams lp = makeLayoutParams(view);
		lp.addRule(CENTER_IN_PARENT);
		// lp.addRule(CENTER_VERTICAL);
		// lp.addRule(ALIGN_PARENT_LEFT);
		getLeftViewContainer().addView(view, lp);
	}

	/**
	 * set customized view to left side
	 * 
	 * @param layoutId
	 *            the xml layout file id
	 */
	public View setCustomizedLeftView(int layoutId) {
		View view = inflate(getContext(), layoutId, null);
		setCustomizedLeftView(view);
		return view;
	}

	public View setCustomizedLeftView(int layoutId, int width, int height) {
		View view = inflate(getContext(), layoutId, null);
		this.defaultWidth = width;
		this.defaultHeight = height;
		setCustomizedLeftView(view);
		return view;
	}

	/**
	 * set customized view to center
	 * 
	 * @param view
	 *            the view to be added to center
	 */
	public void setCustomizedCenterView(View view) {
		mCenterTitleTextView.setVisibility(GONE);
		RelativeLayout.LayoutParams lp = makeLayoutParams(view);
		lp.addRule(CENTER_IN_PARENT);
		getCenterViewContainer().addView(view, lp);
	}

	/**
	 * set customized view to center
	 * 
	 * @param layoutId
	 *            the xml layout file id
	 */
	public void setCustomizedCenterView(int layoutId) {
		View view = inflate(getContext(), layoutId, null);
		setCustomizedCenterView(view);
	}

	/**
	 * set customized view to right side
	 * 
	 * @param view
	 *            the view to be added to right side
	 */
	public void setCustomizedRightView(View view) {
		RelativeLayout.LayoutParams lp = makeLayoutParams(view);
		lp.addRule(CENTER_VERTICAL);
		lp.addRule(ALIGN_PARENT_RIGHT);
		getRightViewContainer().addView(view, lp);
	}

	public RelativeLayout getLeftViewContainer() {
		return mLeftViewContainer;
	}

	public RelativeLayout getCenterViewContainer() {
		return mCenterViewContainer;
	}

	public RelativeLayout getRightViewContainer() {
		return mRightViewContainer;
	}

	public void setLeftOnClickListener(OnClickListener l) {
		mLeftViewContainer.setOnClickListener(l);
	}

	public void setCenterOnClickListener(OnClickListener l) {
		mCenterViewContainer.setOnClickListener(l);
	}

	public void setRightOnClickListener(OnClickListener l) {
		mRightViewContainer.setOnClickListener(l);
	}

	/**
	 * 设置左边自定义的图片的显示
	 * 
	 * @param imageView
	 * @param url
	 * @param roundPixels
	 * @param defaultDrawable
	 */
	public void setLeftImageViewUrl(ImageView imageView, String url, int roundPixels, int defaultDrawable) {
		DisplayImageOptions options = DisplayImageOptionsUtils.getRoundBitMap(roundPixels, defaultDrawable);
		try {
			ImageLoader.getInstance().displayImage(url, imageView, options);
		} catch (Exception e) {
			ImageLoader.getInstance().displayImage("drawable://" + defaultDrawable, imageView, options);
		}
	}
}

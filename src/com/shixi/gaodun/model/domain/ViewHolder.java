package com.shixi.gaodun.model.domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.view.TagImageSpan;

/**
 * 通用的适配器的显示实体类
 *
 * @author ronger
 * @date:2015-11-16 下午2:44:14
 */
public class ViewHolder {
    private final SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    private Context context;

    private ViewHolder(Context context, ViewGroup parent, int layoutId,
                       int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        this.context = context;
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(StringUtils.isEmpty(text) ? "" : text);
        return this;
    }

    public ViewHolder setText(int viewId, String text,
                              android.widget.LinearLayout.LayoutParams layoutParams) {
        TextView view = getView(viewId);
        view.setText(StringUtils.isEmpty(text) ? "" : text);
        if (null != layoutParams)
            view.setLayoutParams(layoutParams);
        return this;
    }

    public ViewHolder setText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setText(int viewId, String text, String defaultStr) {
        TextView view = getView(viewId);
        view.setText(StringUtils.isEmpty(text) ? defaultStr : text);
        return this;
    }

    public ViewHolder setLeftCompoundDrawables(int drawables, int viewId) {
        TextView view = getView(viewId);
        Drawable left = context.getResources().getDrawable(drawables);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        view.setCompoundDrawables(left, null, null, null);
        return this;
    }

    /**
     * 设置需要复文本显示
     *
     * @param viewId
     * @param text
     * @param pinStr
     * @param style
     * @param fontColor
     * @return
     */
    public ViewHolder setText(int viewId, String text, String pinStr,
                              Drawable bg) {
        TextView view = getView(viewId);
        if (StringUtils.isEmpty(pinStr)) {
            view.setText(StringUtils.isEmpty(text) ? "" : text);
            return this;
        }
        if (StringUtils.isEmpty(text)) {
            view.setText(StringUtils.isEmpty(text) ? "" : text);
            return this;
        }
        String str = pinStr + " " + text;
        SpannableString msp = new SpannableString(str);
        // Drawable bg =
        // context.getResources().getDrawable(R.drawable.stick_bg);
        msp.setSpan(new TagImageSpan(bg), 0, pinStr.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(msp);
        return this;
    }

    // <font color=\"white\">pinStr</font>

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    public ViewHolder setImageResource(int viewId, int drawableId,
                                       LayoutParams layoutParams) {
        ImageView view = getView(viewId);
        view.setLayoutParams(layoutParams);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为imageview设置图片
     *
     * @param viewId
     * @param url
     * @param defaultDrawable
     * @param roundPixels
     * @return
     */
    public ViewHolder setImageByUrl(int viewId, String url,
                                    int defaultDrawable, final int roundPixels) {
        final ImageView imageView = (ImageView) getView(viewId);
		/*DisplayImageOptions options = DisplayImageOptionsUtils.getRoundBitMap(
				roundPixels, defaultDrawable);
		try {
			ImageLoader.getInstance().displayImage(url, imageView, options);
		} catch (Exception e) {
			ImageLoader.getInstance().displayImage(
					"drawable://" + defaultDrawable, imageView, options);
		}*/
        //使用Glide优化加载
        Glide.with(context)
                .load(url)
                .asBitmap()
                .centerCrop()
				.placeholder(defaultDrawable)
//				.crossFade()
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCornerRadius(roundPixels);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
        return this;
    }

    /**
     * 设置圆形图片
     *
     * @param viewId
     * @param url
     * @param defaultDrawable
     * @return
     */
    public ViewHolder setCircleImageByUrl(int viewId, String url,
                                          int defaultDrawable) {
        ImageView imageView = (ImageView) getView(viewId);
        DisplayImageOptions options = DisplayImageOptionsUtils
                .getCircleBitMap(defaultDrawable);
        try {
            ImageLoader.getInstance().displayImage(url, imageView, options);
        } catch (Exception e) {
            ImageLoader.getInstance().displayImage(
                    "drawable://" + defaultDrawable, imageView, options);
        }
        return this;
    }

    /**
     * 显示本地图片
     *
     * @param viewId
     * @param path
     * @param defaultDrawable
     * @return
     */
    public ViewHolder setImageView(int viewId, String path,
                                   int defaultDrawable, LayoutParams layoutParams) {
        ImageView imageView = (ImageView) getView(viewId);
        if (null != layoutParams) {
            imageView.setLayoutParams(layoutParams);
        }
        DisplayImageOptions options = DisplayImageOptionsUtils
                .getPickBitMap(defaultDrawable);
        try {
            ImageLoader.getInstance().displayImage(path, imageView, options);
        } catch (Exception e) {
            ImageLoader.getInstance().displayImage(
                    "drawable://" + defaultDrawable, imageView, options);
            // imageView.setImageDrawable(BaseApplication.mApp.getResources().getDrawable(defaultDrawable));
        }
        return this;
    }

    /**
     * 为RatingBar设置评分
     *
     * @param viewId
     * @param rate
     * @return
     */
    public ViewHolder setRating(int viewId, String rate) {
        RatingBar view = getView(viewId);
        float rating = Float.parseFloat(rate);
        if (rating < 0) {
            rating = 0;
        }
        view.setRating(rating);
        return this;
    }

    public int getPosition() {
        return mPosition;
    }

    /**
     * 设置相对布局的显示和影藏
     *
     * @param viewId
     * @param visibility
     */
    public void setRelativeLayoutVisibility(int viewId, int visibility) {
        RelativeLayout relativeLayout = getView(viewId);
        relativeLayout.setVisibility(visibility);
    }

    /**
     * 设置线性布局的显示和隐藏
     *
     * @param viewId
     * @param visibility
     */
    public void setLinearLayoutVisibility(int viewId, int visibility) {
        LinearLayout linearLayout = getView(viewId);
        linearLayout.setVisibility(visibility);
    }

    /**
     * 设置imageview的显示与隐藏
     *
     * @param viewId
     * @param visibility
     */
    public void setImageViewVisibility(int viewId, int visibility) {
        ImageView view = getView(viewId);
        view.setVisibility(visibility);
    }

    /**
     * 设置textview的显示与隐藏
     *
     * @param viewId
     * @param visibility
     */
    public void setTextViewVisibility(int viewId, int visibility) {
        TextView view = getView(viewId);
        view.setVisibility(visibility);
    }

    public void setButtonVisibility(int viewId, int visibility) {
        Button view = getView(viewId);
        view.setVisibility(visibility);
    }

    public void setButtonSelected(int viewId, boolean isSelected) {
        Button view = getView(viewId);
        view.setSelected(isSelected);
    }

    public ViewHolder setViewVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    /**
     * 设置进度条
     *
     * @param viewId
     * @param text
     * @return
     */
    @SuppressLint("UseValueOf")
    public ViewHolder setProgress(int viewId, String text) {
        ProgressBar view = getView(viewId);
        view.setProgress(new Integer(text));
        return this;
    }

    /**
     * 设置进度条最大进度
     *
     * @param viewId
     * @param text
     * @return
     */
    @SuppressLint("UseValueOf")
    public ViewHolder setProgressMax(int viewId, String text) {
        ProgressBar view = getView(viewId);
        view.setMax(new Integer(text));
        return this;
    }
}

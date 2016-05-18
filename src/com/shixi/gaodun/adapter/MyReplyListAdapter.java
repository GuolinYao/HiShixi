package com.shixi.gaodun.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.model.domain.CommentInvitationBean;
import com.shixi.gaodun.model.domain.ImageInfoBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 我的回复
 * 
 * @author ronger
 * @date:2015-12-3 下午3:47:51
 */
@SuppressLint("InflateParams")
public class MyReplyListAdapter extends CommonAdapter<CommentInvitationBean> {

	public MyReplyListAdapter(Activity context,
			List<CommentInvitationBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void convert(ViewHolder helper, CommentInvitationBean item,
			int position) {
		setDataShow(helper, item);

	}

	public void setDataShow(ViewHolder helper, CommentInvitationBean item) {
		// 自己部分显示
		helper.setText(R.id.text_name, item.getNickname(), "某同学");
		helper.setText(R.id.text_time, item.getRefresh_time());
		TextView mTextContent = helper.getView(R.id.text_content);
		String content = item.getContent();
		if (StringUtils.isEmpty(content))
			mTextContent.setVisibility(View.GONE);
		else
			mTextContent.setText(content);
		int sex = NumberUtils.getInt(item.getGender(), 1);
		// if (sex == 1) {// 男
		// helper.setImageResource(R.id.image_sex_icon, R.drawable.topic_male);
		// }
		// if (sex == 2) {// 女
		// helper.setImageResource(R.id.image_sex_icon,
		// R.drawable.topic_female);
		// }
		helper.setImageByUrl(R.id.image_user, item.getSns_avatar(),
				R.drawable.default_touxiang_xiaoerhei, 50);
		LinearLayout mLayoutImages = helper
				.getView(R.id.layout_invitation_images);
		List<ImageInfoBean> images = item.getImage();
		if (null == images || images.size() <= 0) {
			mLayoutImages.setVisibility(View.GONE);
		} else {
			mLayoutImages.setVisibility(View.VISIBLE);
			setTopicImagesShow(images, mLayoutImages);
		}
		// 对象，回复的别人的
		TextView otherText = helper.getView(R.id.text_topic_info);
		ImageView otherImage = helper.getView(R.id.image_topic_icon);
		// 1:跟帖2:评论,3:回复,4:楼主贴
		int type = NumberUtils.getInt(item.getType(), 0);
		System.out.println("type===" + type);
		switch (type) {
		case 1:
			setInvitationShow(otherText, otherImage, item);
			break;
		case 2:
			setCommentShow(otherText, otherImage, item);
			break;
		case 3:
			setReplyShow(otherText, otherImage, item);
			break;
		default:
			break;
		}

	}

	public boolean setTextViewShow(String g_status, TextView textView,
			ImageView imageView, String desc) {
		// 目标状态 1 已删除 2 未删除
		if (StringUtils.isNotEmpty(g_status) && g_status.equals("1")) {
			imageView.setVisibility(View.GONE);
			textView.setText(desc);
			return false;
		}
		return true;
	}

	public void setImageViewShow(String url, ImageView imageView) {
		if (StringUtils.isEmpty(url)) {
			imageView.setVisibility(View.GONE);
		} else {
			imageView.setVisibility(View.VISIBLE);
			setImageByUrl(imageView, url, R.drawable.default_image_icon, 0);
		}
	}

	/**
	 * 我评论别人跟帖的显示
	 * 
	 * @param textView
	 */
	public void setCommentShow(TextView textView, ImageView imageView,
			CommentInvitationBean item) {
		// 目标状态 1 已删除 2 未删除
		boolean isGoOn = setTextViewShow(item.getG_status(), textView,
				imageView, "此贴已删除");
		if (!isGoOn)
			return;
		setImageViewShow(item.getG_image(), imageView);
		int atStartIndex = 0;// @的起始位置
		int nameEndIndex = 0;// 被评论人的结束位置
		StringBuilder sb = new StringBuilder("评论");
		atStartIndex = sb.toString().length();
		sb.append("@");
		sb.append(StringUtils.isEmpty(item.getG_nickname()) ? "某同学" : item
				.getG_nickname());
		sb.append("的发帖");
		nameEndIndex = sb.toString().length();
		sb.append(": ");
		String showContent = StringUtils.isNotEmpty(item.getG_title()) ? item
				.getG_title() : item.getG_content();
		sb.append(StringUtils.isEmpty(showContent) ? "" : showContent);
		SpannableString spannableString = new SpannableString(sb.toString());
		// 设置被评论人的字体颜色
		ForegroundColorSpan nickSpan = new ForegroundColorSpan(mContext
				.getResources().getColor(R.color.nomal_btn_color));
		spannableString.setSpan(nickSpan, atStartIndex, nameEndIndex,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(spannableString);
	}

	/**
	 * 回复的显示 3
	 * 
	 * @param textView
	 */
	public void setReplyShow(TextView textView, ImageView imageView,
			CommentInvitationBean item) {
		boolean isGoOn = setTextViewShow(item.getG_status(), textView,
				imageView, "此评论已删除");
		if (!isGoOn)
			return;
		setImageViewShow(item.getG_image(), imageView);
		int atStartIndex = 0;// @的起始位置
		int nameEndIndex = 0;// 被评论人的结束位置
		StringBuilder sb = new StringBuilder("回复");
		atStartIndex = sb.toString().length();
		sb.append("@");
		sb.append(StringUtils.isEmpty(item.getG_nickname()) ? "某同学" : item
				.getG_nickname());
		sb.append("的回复");
		nameEndIndex = sb.toString().length();
		sb.append(": ");
		String showContent = StringUtils.isNotEmpty(item.getG_title()) ? item
				.getG_title() : item.getG_content();
		sb.append(StringUtils.isEmpty(showContent) ? "" : showContent);
		SpannableString spannableString = new SpannableString(sb.toString());
		// 设置被评论人的字体颜色
		ForegroundColorSpan nickSpan = new ForegroundColorSpan(mContext
				.getResources().getColor(R.color.nomal_btn_color));
		spannableString.setSpan(nickSpan, atStartIndex, nameEndIndex,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(spannableString);
	}

	/**
	 * 个人跟楼主贴
	 * 
	 * @param textView
	 * @param imageView
	 * @param item
	 */
	public void setInvitationShow(TextView textView, ImageView imageView,
			CommentInvitationBean item) {
		boolean isGoOn = setTextViewShow(item.getG_status(), textView,
				imageView, "此贴已删除");
		if (!isGoOn)
			return;
		setImageViewShow(item.getG_image(), imageView);
		int atStartIndex = 0;// @的起始位置
		int nameEndIndex = 0;// 被评论人的结束位置
		StringBuilder sb = new StringBuilder("评论");
		atStartIndex = sb.toString().length();
		sb.append("@");
		sb.append(StringUtils.isEmpty(item.getG_nickname()) ? "某同学" : item
				.getG_nickname());
		sb.append("的评论");
		nameEndIndex = sb.toString().length();
		sb.append(": ");
		String showContent = StringUtils.isNotEmpty(item.getG_title()) ? item
				.getG_title() : item.getG_content();
		sb.append(StringUtils.isEmpty(showContent) ? "" : showContent);
		SpannableString spannableString = new SpannableString(sb.toString());
		// 设置被评论人的字体颜色
		ForegroundColorSpan nickSpan = new ForegroundColorSpan(mContext
				.getResources().getColor(R.color.nomal_btn_color));
		spannableString.setSpan(nickSpan, atStartIndex, nameEndIndex,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(spannableString);
	}

	public void setTopicImagesShow(List<ImageInfoBean> images,
			LinearLayout mLayoutImages) {
		mLayoutImages.removeAllViews();
		for (int i = 0; i < images.size(); i++) {
			ImageInfoBean imageInfoBean = images.get(i);
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.invitation_image_item, null);
			View dividerView = view.findViewById(R.id.view_divider);
			if (i == images.size() - 1) {
				dividerView.setVisibility(View.GONE);
			}
			ImageView imageView = (ImageView) view
					.findViewById(R.id.image_item);
			float scale = imageInfoBean.getHeight()
					/ (float) imageInfoBean.getWidth();
			int imageViewWidth = ActivityUtils.getScreenWidth()
					- ActivityUtils.dip2px(mContext, 15);
			int imageViewHeight = (int) (imageViewWidth * scale);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					imageViewWidth, imageViewHeight);
			imageView.setLayoutParams(lp);
			imageView.setBackgroundColor(mContext.getResources().getColor(
					R.color.main_bg_color));
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setAdjustViewBounds(true);
			try {
				DisplayImageOptions options = DisplayImageOptionsUtils
						.getBigBitMap(R.drawable.default_image_banner);
				ImageLoader.getInstance().displayImage(
						imageInfoBean.getValue(), imageView, options);
			} catch (Exception e) {
				imageView.setImageDrawable(BaseApplication.mApp.getResources()
						.getDrawable(R.drawable.default_image_banner));
			}
			mLayoutImages.addView(view);
		}
	}

}

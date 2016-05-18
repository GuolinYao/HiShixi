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
import com.shixi.gaodun.model.domain.FollowInvitationBean;
import com.shixi.gaodun.model.domain.ImageInfoBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 跟帖列表
 * 
 * @author ronger
 * @date:2015-12-1 上午10:16:22
 */
@SuppressLint("InflateParams")
public class InvitationDetailAdapter extends CommonAdapter<FollowInvitationBean> {
	private String mTopicId;

	public InvitationDetailAdapter(Activity context, List<FollowInvitationBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public void setTopicId(String topicId) {
		mTopicId = topicId;
	}

	@SuppressLint({ "InflateParams", "ResourceAsColor" })
	@Override
	public void convert(ViewHolder helper, FollowInvitationBean item, int position) {
		helper.setText(R.id.text_name, item.getNickname(), "某同学");
		helper.setText(R.id.text_time, item.getCreate_time());
		helper.setCircleImageByUrl(R.id.image_user, item.getSns_avatar(), R.drawable.default_touxiang_xiaoerhei);
		// helper.setImageByUrl(R.id.image_user, item.getSns_avatar(), R.drawable.default_big_toux_icon, 50);
		int sex = NumberUtils.getInt(item.getGender(), 1);
		if (sex == 1) {// 男
			helper.setImageResource(R.id.image_sex_icon, R.drawable.topic_male);
		}
		if (sex == 2) {// 女
			helper.setImageResource(R.id.image_sex_icon, R.drawable.topic_female);
		}
		TextView floorText = helper.getView(R.id.text_floor);
		floorText.setText("第" + item.getFloor() + "楼");
		TextView mTextContent = helper.getView(R.id.text_content);
		if (StringUtils.isEmpty(item.getContent())) {
			mTextContent.setVisibility(View.GONE);
		} else {
			helper.setText(R.id.text_content, item.getContent());
			mTextContent.setVisibility(View.VISIBLE);
		}
		LinearLayout imageLayout = helper.getView(R.id.layout_invitation_images);
		if (null == item.getImage() || item.getImage().isEmpty()) {
			imageLayout.setVisibility(View.GONE);
		} else {
			setTopicImagesShow(item.getImage(), imageLayout, helper);
		}
		final FollowInvitationBean currentitem = item;
		LinearLayout mConnentList = helper.getView(R.id.comment_list);
		List<CommentInvitationBean> commentList = currentitem.getComment();
		if (null != commentList && commentList.size() > 0) {
			mConnentList.setVisibility(View.VISIBLE);
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			mConnentList.removeAllViews();
			for (int i = 0; i < commentList.size(); i++) {
				View commentView = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, null);
				TextView commentText = (TextView) commentView.findViewById(R.id.text_floor_comment);
				// commentText.setText("查看全部" + currentitem.getCommentTotal() + "条评论");
				commentView.setBackgroundColor(android.R.color.transparent);
				setCommentContent(commentText, commentList.get(i), i);
				mConnentList.addView(commentView);
			}
			if (currentitem.getCommentTotal() > 3) {
				View footerView = mInflater.inflate(R.layout.look_up_allcomment_layout, null);
				TextView textAllComment = (TextView) footerView.findViewById(R.id.text_all_comment);
				textAllComment.setText("查看全部" + currentitem.getCommentTotal() + "条评论");
				mConnentList.addView(footerView);
			}
		} else {
			mConnentList.setVisibility(View.GONE);
		}

		// MyListView commentListView = helper.getView(R.id.listview_comment);
		// if (null != currentitem.getComment() && currentitem.getComment().size() > 0) {
		// CommonAdapter<CommentInvitationBean> commentAdapter = new CommentAdapter(mContext,
		// currentitem.getComment(), R.layout.comment_item_layout);
		// if (currentitem.getCommentTotal() > 3) {
		// if (commentListView.getFooterViewsCount() == 0) {
		// View footerView = LayoutInflater.from(mContext).inflate(R.layout.look_up_allcomment_layout, null);
		// TextView textAllComment = (TextView) footerView.findViewById(R.id.text_all_comment);
		// textAllComment.setText("查看全部" + currentitem.getCommentTotal() + "条评论");
		// commentListView.addFooterView(footerView);
		// }
		// }
		// commentListView.setAdapter(commentAdapter);
		// } else {
		// commentListView.setVisibility(View.GONE);
		// }

	}

	/**
	 * 设置跟帖的图片的显示
	 * 
	 * @param images
	 * @param imageLayout
	 * @param helper
	 */
	public void setTopicImagesShow(List<ImageInfoBean> images, LinearLayout imageLayout, ViewHolder helper) {
		imageLayout.removeAllViews();
		for (int i = 0; i < images.size(); i++) {
			ImageInfoBean imageInfoBean = images.get(i);
			View view = LayoutInflater.from(mContext).inflate(R.layout.invitation_image_item, null);
			View dividerView = view.findViewById(R.id.view_divider);
			if (i == images.size() - 1) {
				dividerView.setVisibility(View.GONE);
			}
			ImageView imageView = (ImageView) view.findViewById(R.id.image_item);
			float scale = imageInfoBean.getHeight() / (float) imageInfoBean.getWidth();
			int imageViewWidth = ActivityUtils.getScreenWidth() - ActivityUtils.dip2px(mContext, 15);
			int imageViewHeight = (int) (imageViewWidth * scale);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageViewWidth, imageViewHeight);
			imageView.setLayoutParams(lp);
			imageView.setBackgroundColor(mContext.getResources().getColor(R.color.main_bg_color));
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setAdjustViewBounds(true);
			BaseApplication.mApp.setBigNomalImageByUrl(imageView, imageInfoBean.getValue(),
					R.drawable.default_image_banner);
//			try {
//				DisplayImageOptions options = DisplayImageOptionsUtils.getBigBitMap(R.drawable.default_image_banner);
//				ImageLoader.getInstance().displayImage(imageInfoBean.getValue(), imageView, options);
//			} catch (Exception e) {
//				imageView.setImageDrawable(BaseApplication.mApp.getResources().getDrawable(
//						R.drawable.default_image_banner));
//			}
			imageLayout.addView(view);
		}
	}

	public void setCommentContent(TextView commentText, CommentInvitationBean item, int position) {
		// 类型2评论3回复
		int type = NumberUtils.getInt(item.getType(), 2);
		StringBuilder sb = new StringBuilder();
		// 内容的起始位置
		int contentStartIndex = 0;
		// 内容的结束位置
		int contentEndIndex = 0;
		// 时间的起始位置
		int timeStartIndex = 0;
		// 时间的结束位置
		int timeEndIndex = 0;
		// 被回复人的起始位置
		int gNicknameStartIndex = 0;
		// 回复人的结束位置
		int nickNameEndIndex = 0;
		// TextView textView = helper.getView(R.id.text_floor_comment);

		if (type == 2) {
			sb.append(StringUtils.isEmpty(item.getNickname()) ? "某同学：" : item.getNickname() + ":");
			contentStartIndex = sb.toString().length();
			sb.append(StringUtils.isEmpty(item.getContent()) ? "" : item.getContent());
			contentEndIndex = sb.toString().length();
			timeStartIndex = sb.toString().length();
			sb.append(StringUtils.isEmpty(item.getCreate_time()) ? "" : " " + item.getCreate_time());
			timeEndIndex = sb.toString().length();
			String commentStr = sb.toString();
			SpannableString spannableString = new SpannableString(commentStr);
			// 设置评论人的字体颜色
			ForegroundColorSpan span = new ForegroundColorSpan(mContext.getResources()
					.getColor(R.color.nomal_btn_color));
			// 时间的字体颜色
			ForegroundColorSpan timeSpan = new ForegroundColorSpan(mContext.getResources().getColor(
					R.color.hint_font_color));
			spannableString.setSpan(span, 0, contentStartIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(timeSpan, timeStartIndex, timeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			// SpannableString spannableString2 = spannableString;
			// spannableString2.setSpan(timeSpan, timeStartIndex, timeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

			commentText.setText(spannableString);
		} else {// XX回复XX:内容。时间
			sb.append(StringUtils.isEmpty(item.getNickname()) ? "某同学" : item.getNickname());
			nickNameEndIndex = sb.toString().length();
			sb.append("回复");
			gNicknameStartIndex = sb.toString().length();
			sb.append(StringUtils.isEmpty(item.getG_nickname()) ? "某同学：" : item.getG_nickname() + ":");
			contentStartIndex = sb.toString().length();
			sb.append(StringUtils.isEmpty(item.getContent()) ? "" : item.getContent());
			timeStartIndex = sb.toString().length();
			sb.append(StringUtils.isEmpty(item.getCreate_time()) ? "" : " " + item.getCreate_time());
			timeEndIndex = sb.toString().length();
			SpannableString spannableString = new SpannableString(sb.toString());
			// 设置评论人的字体颜色
			ForegroundColorSpan nickSpan = new ForegroundColorSpan(mContext.getResources().getColor(
					R.color.nomal_btn_color));
			// 设置被回复者的字体颜色
			ForegroundColorSpan gNickSpan = new ForegroundColorSpan(mContext.getResources().getColor(
					R.color.nomal_btn_color));
			// 时间的字体颜色
			ForegroundColorSpan timeSpan = new ForegroundColorSpan(mContext.getResources().getColor(
					R.color.hint_font_color));
			spannableString.setSpan(nickSpan, 0, nickNameEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString
					.setSpan(gNickSpan, gNicknameStartIndex, contentStartIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannableString.setSpan(timeSpan, timeStartIndex, timeEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			commentText.setText(spannableString);
		}
		commentText.setFocusable(false);
	}
}

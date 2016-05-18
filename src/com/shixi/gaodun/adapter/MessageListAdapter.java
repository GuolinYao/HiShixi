package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.TopicMessageBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 消息列表
 * 
 * @author ronger
 * @date:2015-12-4 下午3:09:05
 */
public class MessageListAdapter extends CommonAdapter<TopicMessageBean> {

	public MessageListAdapter(Activity context, List<TopicMessageBean> mDatas,
			int itemLayoutId) {
		super(context, mDatas, itemLayoutId);

	}

	@Override
	public void convert(ViewHolder helper, TopicMessageBean item, int position) {
		int type = NumberUtils.getInt(item.getType(), 1);
//		System.out.println("apapter---------"+type);
		// 1 点赞 2 跟帖 3 评论 4 回复
		if (type == 1) {
			setPraiseMessageShow(helper, item);
			return;
		}
		if (type == 2) {
			setFollowUpInvitationShow(helper, item);
			return;
		}
		if (type == 3) {
			setCommentShow(helper, item);
			return;
		}
		if (type == 4) {
			setReplyCommentShow(helper, item);
		}
	}

	/**
	 * 赞，
	 * 
	 * @param helper
	 * @param item
	 */
	public void setPraiseMessageShow(ViewHolder helper, TopicMessageBean item) {
		// 操作者部分
		setSexShow(helper, item.getGender());
		helper.setText(R.id.text_message_name, item.getNickname(), "某同学");
		helper.setImageByUrl(R.id.image_message_photo, item.getSns_avatar(),
				R.drawable.default_touxiang_xiaoerhei, 50);
		helper.setTextViewVisibility(R.id.text_message_content, View.GONE);
		helper.setImageViewVisibility(R.id.image_praise_icon, View.VISIBLE);
		helper.setText(R.id.text_message_time, item.getCreate_time());
		// 自己的部分
		String topicStatus = item.getTopic_status();
		String topicImage = item.getTopic_image();
		if (StringUtils.isEmpty(topicStatus))
			return;
		if (topicStatus.equals("2")) {
			setRightTextViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content,
					Color.parseColor("#ff2525"), "该贴内容已被删除");
			return;
		}
		if (StringUtils.isNotEmpty(topicImage)) {
			setRightImageViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content, topicImage);
		} else {
			setRightTextViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content,
					Color.parseColor("#656565"), item.getTopic_title());
		}
	}

	public void setRightTextViewShow(ViewHolder helper, int imageID,
			int textID, int textColor, String str) {
		helper.setImageViewVisibility(imageID, View.GONE);
		TextView textView = helper.getView(textID);
		textView.setVisibility(View.VISIBLE);
		textView.setTextColor(textColor);
		textView.setText(str);
	}

	public void setRightImageViewShow(ViewHolder helper, int imageID,
			int textID, String str) {
		helper.setImageViewVisibility(imageID, View.VISIBLE);
		helper.setImageByUrl(imageID, str, R.drawable.default_image_icon, 0);
		helper.setTextViewVisibility(textID, View.GONE);
	}

	/**
	 * 别人跟帖,个人是楼主贴
	 * 
	 * @param helper
	 * @param item
	 */
	public void setFollowUpInvitationShow(ViewHolder helper,
			TopicMessageBean item) {
		System.out.println("setFollowUpInvitationShow==============");
		// 操作者部分
		setSexShow(helper, item.getGender());
		helper.setImageByUrl(R.id.image_message_photo, item.getSns_avatar(),
				R.drawable.default_touxiang_xiaoerhei, 50);
		helper.setText(R.id.text_message_name, item.getNickname(), "某同学");
		helper.setImageViewVisibility(R.id.image_praise_icon, View.GONE);
		helper.setTextViewVisibility(R.id.text_message_content, View.VISIBLE);
		// String repleyStatus = item.getReply_status();
		if (item.getReply_image() == null || "".equals(item.getReply_image())) {
			// 如果跟帖不是图片
			helper.setText(R.id.text_message_content, item.getContent());
		} else {
			// 如果跟帖是图片，那么存在图片地址，显示【图片】字样
			helper.setText(R.id.text_message_content, item.getReply_image());
		}
		// // 跟帖已被删除
		// if (StringUtils.isNotEmpty(repleyStatus) && repleyStatus.equals("2"))
		// {
		// helper.setText(R.id.text_message_content, "跟帖已被删除");
		// } else {
		// String content = item.getReply_content();
		// String image = item.getReply_image();
		// if (StringUtils.isEmpty(content) && StringUtils.isNotEmpty(image)) {
		// helper.setText(R.id.text_message_content, "【图片】");
		// } else {
		// helper.setText(R.id.text_message_content, content);
		// }
		// }
		helper.setText(R.id.text_message_time, item.getCreate_time());
		// 自己的部分(可能是跟帖也可能是楼主贴)
		int topicStatus = NumberUtils.getInt(item.getTopic_status(), 0);
		int replyStatus = NumberUtils.getInt(item.getReply_status(), 0);
		String topicImage = item.getTopic_image();
		String replyImage = item.getReply_image();
		// 已被删除
		if (topicStatus == 2 || replyStatus == 2) {
			setRightTextViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content,
					Color.parseColor("#ff2525"), "该贴内容已被删除");
		} else {// 未被删除状态
		// if (StringUtils.isNotEmpty(topicImage) ||
		// StringUtils.isNotEmpty(replyImage)) {
		// String url = StringUtils.isNotEmpty(topicImage) ? topicImage :
		// replyImage;
		// setRightImageViewShow(helper, R.id.image_message_icon,
		// R.id.text_message_lose_content, url);
		// return;
		// }
			if (StringUtils.isNotEmpty(topicImage)) {
				String url = topicImage;
				setRightImageViewShow(helper, R.id.image_message_icon,
						R.id.text_message_lose_content, url);
				return;
			}
			String topicTitle = item.getTopic_title();
			String replyContent = item.getReply_content();
			if (StringUtils.isEmpty(topicTitle)
					&& StringUtils.isNotEmpty(replyContent)) {
				setRightTextViewShow(helper, R.id.image_message_icon,
						R.id.text_message_lose_content,
						Color.parseColor("#656565"), replyContent);
			} else {
				setRightTextViewShow(helper, R.id.image_message_icon,
						R.id.text_message_lose_content,
						Color.parseColor("#656565"), topicTitle);
			}
		}
	}

	/**
	 * 评论,可评论我的跟帖或者楼主贴
	 * 
	 * 右边都显示话题:楼主贴或者跟帖
	 * 
	 * @param helper
	 * @param item
	 */
	public void setCommentShow(ViewHolder helper, TopicMessageBean item) {
		// 操作者部分
		setSexShow(helper, item.getGender());
		helper.setImageByUrl(R.id.image_message_photo, item.getSns_avatar(),
				R.drawable.default_touxiang_xiaoerhei, 50);
		helper.setText(R.id.text_message_name, item.getNickname(), "某同学");
		helper.setImageViewVisibility(R.id.image_praise_icon, View.GONE);
		helper.setTextViewVisibility(R.id.text_message_content, View.VISIBLE);
		helper.setText(R.id.text_message_content, item.getComment());
		helper.setText(R.id.text_message_time, item.getCreate_time());
		// 1未删除2已删除
		int topicStatus = NumberUtils.getInt(item.getTopic_status(), 0);
		int replyStatus = NumberUtils.getInt(item.getReply_status(), 0);
		String reply_image = item.getReply_image();
		if (topicStatus == 2 || replyStatus == 2) {
			setRightTextViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content,
					Color.parseColor("#ff2525"), "该贴内容已被删除");
			return;
		}
		if (StringUtils.isNotEmpty(reply_image)) {
			setRightImageViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content, reply_image);
		} else {
			// String showContent = StringUtils.isEmpty(item.getTopic_title()) ?
			// item.getReply_content() : item
			// .getTopic_title();
			// // setRightTextViewShow(helper, R.id.image_message_icon,
			// R.id.text_message_lose_content,
			// // Color.parseColor("#656565"), item.getTopic_title());
			// setRightTextViewShow(helper, R.id.image_message_icon,
			// R.id.text_message_lose_content,
			// Color.parseColor("#656565"), showContent);
			// 接口暂定都用content展示
			setRightTextViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content,
					Color.parseColor("#656565"), item.getContent());
		}
	}

	/**
	 * 回复
	 * 
	 * 右边都显示话题
	 * 如果没有删除，右边优先显示reply_image再次content
	 * 
	 * @param helper
	 * @param item
	 */
	public void setReplyCommentShow(ViewHolder helper, TopicMessageBean item) {
		System.out.println("setReplyCommentShow--------"+item.getContent());
		// 操作者部分(对于我的评论的回复)
		setSexShow(helper, item.getGender());
		helper.setImageByUrl(R.id.image_message_photo, item.getSns_avatar(),
				R.drawable.default_touxiang_xiaoerhei, 50);
		helper.setText(R.id.text_message_name, item.getNickname(), "某同学");
		helper.setImageViewVisibility(R.id.image_praise_icon, View.GONE);
		helper.setTextViewVisibility(R.id.text_message_content, View.VISIBLE);
		helper.setText(R.id.text_message_content, item.getReceve());
		helper.setText(R.id.text_message_time, item.getReceve());
		helper.setText(R.id.text_message_time, item.getCreate_time());
		// 1未删除2已删除
		int topicStatus = NumberUtils.getInt(item.getTopic_status(), 0);
		int replyStatus = NumberUtils.getInt(item.getReply_status(), 0);
		String topicImage = item.getTopic_image();
		String replyImage = item.getReply_image();
		if (topicStatus == 2 || replyStatus == 2) {
			setRightTextViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content,
					Color.parseColor("#ff2525"), "该贴内容已被删除");
			return;
		}
		// 楼主贴是一定有标题的
		if (StringUtils.isNotEmpty(replyImage)) {
			setRightImageViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content, replyImage);
		} else {//如果没有回复图片 那么显示回复内容 如果回复内容也没有 就显示话题标题
			String showContent = StringUtils.isEmpty(item.getContent()) ? item
					.getTopic_title() : item.getContent();
			setRightTextViewShow(helper, R.id.image_message_icon,
					R.id.text_message_lose_content,
					Color.parseColor("#656565"), showContent);
		}
	}

	public void setSexShow(ViewHolder helper, String gender) {
		int sex = NumberUtils.getInt(gender, 1);
		if (sex == 1) {// 男
			helper.setImageResource(R.id.image_message_sex,
					R.drawable.topic_male);
		}
		if (sex == 2) {// 女
			helper.setImageResource(R.id.image_message_sex,
					R.drawable.topic_female);
		}
	}
}

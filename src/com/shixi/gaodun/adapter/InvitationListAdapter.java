package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.TopicInfo;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.view.TagImageSpan;

/**
 * 帖子列表的适配器
 * 
 * @author ronger
 * @date:2015-12-3 下午3:10:21
 */
public class InvitationListAdapter extends CommonAdapter<InvitationInfoBean> {
	private TopicInfo mTopicInfo;

	public InvitationListAdapter(Activity context,
			List<InvitationInfoBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public InvitationListAdapter(Activity context,
			List<InvitationInfoBean> mDatas, int itemLayoutId,
			TopicInfo topicInfo) {
		super(context, mDatas, itemLayoutId);
		this.mTopicInfo = topicInfo;
	}

	@Override
	public int getCount() {
		if (null == mLists || mLists.size() == 0) {
			return 1;
		}
		return super.getCount() + 1;
	}

	@Override
	public InvitationInfoBean getItem(int position) {
		if (position == 0) {
			return null;
		}
		return super.getItem(position - 1);
	}

	@SuppressWarnings("unused")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {
			return setTopicInfo();
		}
		ViewHolder viewHolder = null;
		if (null == convertView || null == viewHolder) {
			convertView = mInflater.inflate(mItemLayoutId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.invitation_title = (TextView) convertView
					.findViewById(R.id.invitation_title);
			viewHolder.text_name = (TextView) convertView
					.findViewById(R.id.text_name);
			viewHolder.invitation_desc = (TextView) convertView
					.findViewById(R.id.invitation_desc);
			viewHolder.text_praisenumber = (TextView) convertView
					.findViewById(R.id.text_praisenumber);
			viewHolder.text_commentnumber = (TextView) convertView
					.findViewById(R.id.text_commentnumber);
			viewHolder.text_time = (TextView) convertView
					.findViewById(R.id.text_time);
			viewHolder.image_user = (ImageView) convertView
					.findViewById(R.id.image_user);
			viewHolder.image_view = (ImageView) convertView
					.findViewById(R.id.image_view);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		InvitationInfoBean bean = mLists.get(position - 1);
		setItemShow(bean, viewHolder);
		return convertView;
	}

	class ViewHolder {
		TextView invitation_title, text_name, invitation_desc,
				text_praisenumber, text_commentnumber, text_time;
		ImageView image_user, image_view;
	}

	// @Override
	// public void convert(ViewHolder helper, InvitationInfoBean item, int
	// position) {
	//
	// }

	public View setTopicInfo() {
		View view = mInflater.inflate(R.layout.invitation_topic_layout, null);
		TextView title = (TextView) view.findViewById(R.id.topic_title);
		TextView des = (TextView) view.findViewById(R.id.topic_desc);
		ImageView image = (ImageView) view.findViewById(R.id.topic_image);
		setTextViewShow(title, mTopicInfo.getTips_title());
		setTextViewShow(des, mTopicInfo.getTips_content());
		setImageByUrl(image, mTopicInfo.getIcon_image(),
				R.drawable.default_image_icon, 10);
		return view;
	}

	/**
	 * 设置item显示
	 * 
	 * @param helper
	 * @param item
	 * @param position
	 */
	public void setItemShow(InvitationInfoBean item, ViewHolder viewHolder) {
		int official_posting = NumberUtils
				.getInt(item.getOfficial_posting(), 2);
		int is_top = NumberUtils.getInt(item.getIs_top(), 2);
		Resources resources = mContext.getResources();
		Drawable drawable = null;
		String pinStr = "";
		if (official_posting == 1) {// 官方发帖
			drawable = resources.getDrawable(R.drawable.official_bg);
			pinStr = "官方";
		} else {
			if (is_top == 1) {// 置顶
				drawable = resources.getDrawable(R.drawable.stick_bg);
				pinStr = "置顶";
			}
		}

		// 标题
		if (null == drawable) {
			setTextViewShow(viewHolder.invitation_title, item.getTitle());
		} else {
			setText(viewHolder.invitation_title, item.getTitle(), pinStr,
					drawable);
		}
		// 官方贴需要隐藏昵称和头像
		if (official_posting == 1) {
			viewHolder.image_user.setVisibility(View.GONE);
			viewHolder.text_name.setVisibility(View.GONE);
		} else {
			// 头像+昵称+发布时间
			setTextViewShow(viewHolder.text_name, item.getNickname());
			setTextViewShow(viewHolder.text_time, item.getRefresh_time());
			setImageByUrl(viewHolder.image_user, item.getSns_avatar(),
					R.drawable.touxiang, 50);
		}
		// 设置帖子图片的显示与隐藏
		if (StringUtils.isEmpty(item.getSingeimage())) {
			viewHolder.image_view.setVisibility(View.GONE);
		} else {
			setImageByUrl(viewHolder.image_view, item.getSingeimage(),
					R.drawable.default_image_icon, 0);
		}
		// 帖子内容
		setTextViewShow(viewHolder.invitation_desc, item.getContent());
		// 点赞数
		setTextViewShow(viewHolder.text_praisenumber, item.getFavor_num());
		// 评论数
		setTextViewShow(viewHolder.text_commentnumber, item.getComment_num());
	}

	public void setText(TextView view, String text, String pinStr, Drawable bg) {
		if (StringUtils.isEmpty(pinStr)) {
			view.setText(StringUtils.isEmpty(text) ? "" : text);
			return;
		}
		if (StringUtils.isEmpty(text)) {
			view.setText(StringUtils.isEmpty(text) ? "" : text);
			return;
		}
		String str = pinStr + " " + text;
		SpannableString msp = new SpannableString(str);
		msp.setSpan(new TagImageSpan(bg), 0, pinStr.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(msp);
	}

	@Override
	public void convert(com.shixi.gaodun.model.domain.ViewHolder helper,
			InvitationInfoBean item, int position) {
		// TODO Auto-generated method stub

	}

}

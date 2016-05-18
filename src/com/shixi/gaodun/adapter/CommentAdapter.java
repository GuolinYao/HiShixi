package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.CommentInvitationBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 跟帖的评论列表
 * 
 * @author ronger
 * @date:2015-12-1 上午10:53:25
 */
public class CommentAdapter extends CommonAdapter<CommentInvitationBean> {
	private int mInitCount = 3;

	public CommentAdapter(Activity context, List<CommentInvitationBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		// TODO Auto-generated constructor stub
	}

	public void setInitCount(int initCount) {
		this.mInitCount = initCount;
	}

	public void setNotifyData(List<CommentInvitationBean> mDatas) {
		this.mLists = mDatas;
		notifyDataSetChanged();
	}

	@Override
	public void convert(ViewHolder helper, CommentInvitationBean item, int position) {
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
		TextView textView = helper.getView(R.id.text_floor_comment);

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

			textView.setText(spannableString);
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
			textView.setText(spannableString);
		}
		textView.setFocusable(false);
		//textView.setMovementMethod(TextViewFixTouchConsume.LocalLinkMovementMethod.getInstance());
	}
}

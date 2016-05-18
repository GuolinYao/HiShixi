package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * @author ronger
 * @date:2015-12-14 上午11:17:55
 */
public class MineIvitationListAdapter extends CommonAdapter<InvitationInfoBean> {
	public MineIvitationListAdapter(Activity context, List<InvitationInfoBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void convert(com.shixi.gaodun.model.domain.ViewHolder helper, InvitationInfoBean item, int position) {
		setItemShow(helper, item, position);

	}

	/**
	 * 设置item显示
	 * 
	 * @param helper
	 * @param item
	 * @param position
	 */
	public void setItemShow(com.shixi.gaodun.model.domain.ViewHolder helper, InvitationInfoBean item, int position) {
		int official_posting = NumberUtils.getInt(item.getOfficial_posting(), 2);
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
			helper.setText(R.id.invitation_title, item.getTitle());
		} else {
			helper.setText(R.id.invitation_title, item.getTitle(), pinStr, drawable);
		}
		// 官方贴需要隐藏昵称和头像
		if (official_posting == 1) {
			helper.setImageViewVisibility(R.id.image_user, View.GONE);
			helper.setTextViewVisibility(R.id.text_name, View.GONE);
			//设置 刷新时间
			helper.setText(R.id.text_time, item.getRefresh_time());
		} else {
			// 头像+昵称+发布时间
			helper.setText(R.id.text_name, item.getNickname());
			helper.setCircleImageByUrl(R.id.image_user, item.getSns_avatar(), R.drawable.default_touxiang_xiaoerhei);
			// helper.setImageByUrl(R.id.image_user, item.getSns_avatar(), R.drawable.default_list_toux_icon, 50);
			helper.setText(R.id.text_time, item.getRefresh_time());
		}
		// 设置帖子图片的显示与隐藏
		if (StringUtils.isEmpty(item.getSingeimage())) {
			helper.setImageViewVisibility(R.id.image_view, View.GONE);
		} else {
			helper.setImageViewVisibility(R.id.image_view, View.VISIBLE);
			helper.setImageByUrl(R.id.image_view, item.getSingeimage(), R.drawable.default_image_icon, 0);
		}
		// 帖子内容
		helper.setText(R.id.invitation_desc, item.getContent());
		// 点赞数
		helper.setText(R.id.text_praisenumber, item.getFavor_num());
		// 评论数
		helper.setText(R.id.text_commentnumber, item.getComment_num());
	}
}

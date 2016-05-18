package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.RelativeLayout;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.TopicMessageBean;
import com.shixi.gaodun.model.domain.ViewHolder;

/**
 * @author ronger
 * @date:2015-12-4 下午5:29:19
 */
public class SystemMessageAdapter extends CommonAdapter<TopicMessageBean> {

	public SystemMessageAdapter(Activity context, List<TopicMessageBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void convert(ViewHolder helper, TopicMessageBean item, int position) {
		// 背景
		RelativeLayout layout = helper.getView(R.id.layout_system_message);
		Resources mResources = mContext.getResources();
		// 阅读状态1未阅读，2已阅读
		if (item.getStatus().equals("1")) {
			layout.setBackgroundColor(mResources.getColor(R.color.main_bg));
			helper.setImageResource(R.id.image_system_icon, R.drawable.picinfo);
		}
		if (item.getStatus().equals("2")) {
			layout.setBackgroundColor(Color.parseColor("#f6f6f6"));
			helper.setImageResource(R.id.image_system_icon, R.drawable.textinfo);
			// helper.setImageResource(R.id.image_system_icon, R.drawable.systeminfo);
		}
		// 标题
		helper.setText(R.id.text_system_title, item.getTitle());
		// 时间
		helper.setText(R.id.text_system_time, item.getSend_time());
	}

}

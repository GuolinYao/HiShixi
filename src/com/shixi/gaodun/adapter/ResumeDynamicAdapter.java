package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.ResumeDynamicBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * @author ronger
 * @date:2015-12-5 下午1:15:54
 */
public class ResumeDynamicAdapter extends CommonAdapter<ResumeDynamicBean> {

	public ResumeDynamicAdapter(Activity context, List<ResumeDynamicBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void convert(ViewHolder helper, ResumeDynamicBean item, int position) {
		helper.setImageByUrl(R.id.image_position_icon, item.getLogo(), R.drawable.default_image_icon, 10);
		helper.setText(R.id.text_positionname, item.getPost_title());
		helper.setText(R.id.text_commpany_name, item.getEnterprise_name());

		helper.setText(R.id.text_address, item.getPost_city());
		helper.setText(R.id.text_post_time, item.getCreate_time());
		helper.setText(R.id.text_status, "【" + item.getResumepost_status() + "】");
		// 职位状态 1 上架 2 下架
		String poststatus = item.getPost_status();
		if (StringUtils.isNotEmpty(poststatus)) {
			if (poststatus.equals("2")) {
				View positionItem = helper.getView(R.id.layout_offline);
				positionItem.setVisibility(View.VISIBLE);
			}
		}
	}

}

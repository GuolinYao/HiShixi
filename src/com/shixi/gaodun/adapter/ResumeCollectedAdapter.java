package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.ResumeCollectedBean;
import com.shixi.gaodun.model.domain.ResumeDynamicBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * @author ronger
 * @date:2015-12-5 下午1:15:54
 */
public class ResumeCollectedAdapter extends CommonAdapter<ResumeCollectedBean> {

	public ResumeCollectedAdapter(Activity context, List<ResumeCollectedBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void convert(ViewHolder helper, ResumeCollectedBean item, int position) {
//		helper.setImageByUrl(R.id.image_position_icon, item.getLogo(), R.drawable.default_image_icon, 10);
//		helper.setText(R.id.text_positionname, item.getPost_title());
		
	}

}

package com.shixi.gaodun.adapter;

import android.app.Activity;
import android.content.Context;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.CollectedResumeBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.media.UMImage;

import java.util.List;

/**
 * 收集简历适配器
 * 
 * @author guolin
 * @date:2016-2-24
 */
public class CollectedResumeAdapter extends CommonAdapter<CollectedResumeBean>
		 {
	//
	// public SeachPositionAdapter(Activity context, ArrayList<PositionBean>
	// lists) {
	// super(context, lists);
	// // TODO Auto-generated constructor stub
	// }
	private Context context;
	private Activity activity;
	private UMImage image;
	private UMShareListener umShareListener;

	public CollectedResumeAdapter(Activity context,
								  List<CollectedResumeBean> mDatas, int itemLayoutId) {

		super(context, mDatas, itemLayoutId);
		this.context = context;
		activity = context;
	}

	public void setData(ViewHolder helper, CollectedResumeBean item) {
		helper.setText(R.id.tv_name, item.getName());
		helper.setText(R.id.tv_code, item.getCode());
		helper.setText(R.id.tv_school, item.getGraduate_school()+" | ");
		helper.setText(R.id.tv_major, item.getDetail_major());
		helper.setText(R.id.tv_enterprise,"来源："+ item.getEnterprise_name());
		helper.setText(R.id.tv_position, " | "+item.getTitle());
		helper.setText(R.id.tv_drop_time, "投递时间："+item.getCreate_time());

	}

			 // class ViewHolder {
	// ImageView image;
	// TextView publishTime, positionName, addressName, salaryRange, education,
	// week_Available, industryName,
	// scaleName;
	// ByLengthSetShowTextView commpanyName;
	// }

	@Override
	public void convert(ViewHolder helper, CollectedResumeBean item,
			int position) {
		setData(helper, item);
	}

}

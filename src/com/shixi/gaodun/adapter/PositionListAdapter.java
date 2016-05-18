package com.shixi.gaodun.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.domain.ViewHolder;

/**
 * 职位列表
 * 
 * @author ronger
 * @date:2015-11-9 下午1:20:12
 */
@SuppressLint("InflateParams")
public class PositionListAdapter extends CommonAdapter<PositionBean> {

	// public PositionListAdapter(Activity context, ArrayList<PositionBean> lists) {
	// super(context, lists);
	// }

	public PositionListAdapter(Activity context, List<PositionBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder viewHolder = null;
	// if (null == convertView) {
	// convertView = mInflater.inflate(R.layout.tab_position_item_layout, null);
	// viewHolder = new ViewHolder();
	// viewHolder.image = (ImageView) convertView.findViewById(R.id.iv_commpany_image);
	// viewHolder.positionName = (TextView) convertView.findViewById(R.id.tv_position_name);
	// viewHolder.publishTime = (TextView) convertView.findViewById(R.id.tv_publish_time);
	// viewHolder.addressName = (TextView) convertView.findViewById(R.id.tv_position_address);
	// viewHolder.salaryRange = (TextView) convertView.findViewById(R.id.tv_salary_range);
	// viewHolder.education = (TextView) convertView.findViewById(R.id.tv_education);
	// viewHolder.week_Available = (TextView) convertView.findViewById(R.id.tv_weekday);
	// viewHolder.commpanyName = (TextView) convertView.findViewById(R.id.tv_commpany_name);
	// viewHolder.industryName = (TextView) convertView.findViewById(R.id.tv_industry_name);
	// viewHolder.scaleName = (TextView) convertView.findViewById(R.id.tv_commpany_number);
	//
	// convertView.setTag(viewHolder);
	// } else {
	// viewHolder = (ViewHolder) convertView.getTag();
	// }
	// setData(viewHolder, mLists.get(position));
	// return convertView;
	// }

	public void setData(ViewHolder helper, PositionBean bean) {
		helper.setText(R.id.tv_publish_time, bean.getCreate_time());
		helper.setText(R.id.tv_position_name, bean.getTitle());
		helper.setText(R.id.tv_position_address, "[" + bean.getRegion_name() + "]");
		helper.setText(R.id.tv_education, bean.getEducation());
		helper.setText(R.id.tv_weekday, bean.getWeek_available());
		helper.setText(R.id.tv_commpany_name, bean.getEnterprise_name());
		helper.setText(R.id.tv_industry_name, bean.getIndustry_name());
		helper.setText(R.id.tv_commpany_number, bean.getScale_name());

		String str = bean.getSalary_range();
		int endIndex = str.indexOf("元");
		SpannableString spannableString = new SpannableString(str);
		spannableString.setSpan(
				new ForegroundColorSpan(mContext.getResources().getColor(R.color.salary_range_font_color)), 0,
				endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		helper.setText(R.id.tv_salary_range, spannableString);
		helper.setImageByUrl(R.id.iv_commpany_image, bean.getLogo(), R.drawable.default_image_icon, 10);
	}

	@Override
	public void convert(com.shixi.gaodun.model.domain.ViewHolder helper, PositionBean item, int position) {
		setData(helper, item);

	}
}

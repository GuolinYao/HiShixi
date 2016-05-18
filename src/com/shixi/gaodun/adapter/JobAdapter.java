package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * @author ronger
 * @date:2015-11-13 上午9:55:28
 */
public class JobAdapter extends CommonAdapter<PositionBean> {

	public JobAdapter(Activity context, List<PositionBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder viewHolder = null;
	// if (null == convertView) {
	// convertView = mInflater.inflate(R.layout.company_position_item_layout, null);
	// viewHolder = new ViewHolder();
	// viewHolder.positionName = (TextView) convertView.findViewById(R.id.tv_position_name);
	// viewHolder.publishTime = (TextView) convertView.findViewById(R.id.tv_publish_time);
	// viewHolder.addressName = (TextView) convertView.findViewById(R.id.tv_position_address);
	// viewHolder.salaryRange = (TextView) convertView.findViewById(R.id.tv_salary_range);
	// viewHolder.education = (TextView) convertView.findViewById(R.id.tv_education);
	// viewHolder.week_Available = (TextView) convertView.findViewById(R.id.tv_weekday);
	//
	// convertView.setTag(viewHolder);
	// } else {
	// viewHolder = (ViewHolder) convertView.getTag();
	// }
	// setData(viewHolder, mLists.get(position));
	// return convertView;
	// }

	public void setData(com.shixi.gaodun.model.domain.ViewHolder helper, PositionBean bean) {
		helper.setText(R.id.tv_publish_time, bean.getCreate_time());
		helper.setText(R.id.tv_position_name, bean.getTitle());
		helper.setText(R.id.tv_position_address, "[" + bean.getRegion_name() + "]");
		helper.setText(R.id.tv_education, bean.getEducation());
		helper.setText(R.id.tv_weekday, bean.getWeek_available());
		String str = bean.getSalary_range();
		if (StringUtils.isNotEmpty(str)) {
			int endIndex = str.indexOf("元");
			SpannableString spannableString = new SpannableString(str);
			spannableString.setSpan(
					new ForegroundColorSpan(mContext.getResources().getColor(R.color.salary_range_font_color)), 0,
					endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			helper.setText(R.id.tv_salary_range, spannableString);
		}
	}

	// class ViewHolder {
	// TextView publishTime, positionName, addressName, salaryRange, education, week_Available;
	// }

	@Override
	public void convert(com.shixi.gaodun.model.domain.ViewHolder helper, PositionBean item, int position) {
		setData(helper, item);
	}
}

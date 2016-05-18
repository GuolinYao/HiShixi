package com.shixi.gaodun.adapter;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.StringUtils;

import java.util.List;

/**
 * 搜索职位列表适配器
 * 
 * @author ronger
 * @date:2015-11-10 下午2:07:58
 */
public class SeachPositionAdapter extends CommonAdapter<PositionBean> {
	//
	// public SeachPositionAdapter(Activity context, ArrayList<PositionBean> lists) {
	// super(context, lists);
	// // TODO Auto-generated constructor stub
	// }

	public SeachPositionAdapter(Activity context, List<PositionBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder viewHolder = null;
	// if (null == convertView) {
	// convertView = mInflater.inflate(R.layout.search_position_item_layout, null);
	// viewHolder = new ViewHolder();
	// viewHolder.image = (ImageView) convertView.findViewById(R.id.iv_commpany_image);
	// viewHolder.positionName = (TextView) convertView.findViewById(R.id.tv_position_name);
	// viewHolder.publishTime = (TextView) convertView.findViewById(R.id.tv_publish_time);
	// viewHolder.addressName = (TextView) convertView.findViewById(R.id.tv_position_address);
	// viewHolder.salaryRange = (TextView) convertView.findViewById(R.id.tv_salary_range);
	// viewHolder.education = (TextView) convertView.findViewById(R.id.tv_education);
	// viewHolder.week_Available = (TextView) convertView.findViewById(R.id.tv_weekday);
	// viewHolder.commpanyName = (ByLengthSetShowTextView) convertView.findViewById(R.id.tv_commpany_name);
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

	public void setData(ViewHolder helper, PositionBean item) {
		helper.setImageByUrl(R.id.iv_commpany_image, item.getLogo(), R.drawable.default_image_icon, 5);
		helper.setText(R.id.tv_position_name, item.getTitle());
		helper.setText(R.id.tv_publish_time, item.getCreate_time());
		helper.setText(R.id.tv_position_address, "[" + item.getRegion_name() + "]");
		String str = item.getSalary_range();
		if (StringUtils.isNotEmpty(str)) {
			int endIndex = str.indexOf("元");
			TextView textView = helper.getView(R.id.tv_salary_range);
			if (endIndex < 0) {
				textView.setText(str);
				return;
			}
			SpannableString spannableString = new SpannableString(str);
			spannableString.setSpan(
					new ForegroundColorSpan(mContext.getResources().getColor(R.color.salary_range_font_color)), 0,
					endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textView.setText(spannableString);
		}
		helper.setText(R.id.tv_education, item.getEducation());
		helper.setText(R.id.tv_weekday, item.getWeek_available());
		helper.setText(R.id.tv_commpany_name, item.getEnterprise_name());
		helper.setText(R.id.tv_industry_name, item.getIndustry_name());
		helper.setText(R.id.tv_commpany_number, item.getScale_name());
		// helper.setText(R.id.tv_publish_time, bean.getCreate_time());
		// helper.setText(R.id.tv_position_name, bean.getTitle());
		// helper.setText(R.id.tv_position_address, "[" + bean.getRegion_name() + "]");
		// helper.setText(R.id.tv_education, bean.getEducation());
		// helper.setText(R.id.tv_weekday, bean.getWeek_available());
		// ByLengthSetShowTextView commpanyName = helper.getView(R.id.tv_commpany_name);
		// commpanyName.setDefault_textlength(5);
		// commpanyName.setValue(bean.getEnterprise_name());
		// commpanyName.setText(bean.getEnterprise_name());
		// // 公司规模
		// helper.setText(R.id.tv_commpany_number, bean.getScale_name());
		// // 行业名称
		// helper.setText(R.id.tv_industry_name, bean.getIndustry_name());
		// String str = bean.getSalary_range();
		// if (StringUtils.isNotEmpty(str)) {
		// int endIndex = str.indexOf("元");
		// SpannableString spannableString = new SpannableString(str);
		// spannableString.setSpan(
		// new ForegroundColorSpan(mContext.getResources().getColor(R.color.salary_range_font_color)), 0,
		// endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// TextView salaryRange = helper.getView(R.id.tv_salary_range);
		// salaryRange.setText(spannableString);
		// }
		//
		// helper.setImageByUrl(R.id.iv_commpany_image, bean.getLogo(), R.drawable.default_image_icon, 10);
	}

	// class ViewHolder {
	// ImageView image;
	// TextView publishTime, positionName, addressName, salaryRange, education, week_Available, industryName,
	// scaleName;
	// ByLengthSetShowTextView commpanyName;
	// }

	@Override
	public void convert(com.shixi.gaodun.model.domain.ViewHolder helper, PositionBean item, int position) {
		setData(helper, item);
	}
}

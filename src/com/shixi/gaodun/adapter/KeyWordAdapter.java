package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.KeyWordBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.ActivityUtils;

/**
 * 关键词
 * 
 * @author ronger
 * @date:2015-11-10 下午4:58:51
 */
public class KeyWordAdapter extends CommonAdapter<KeyWordBean> {
	private String keyWord;
	private LayoutParams layoutParams;

	//
	// public KeyWordAdapter(Activity context, ArrayList<KeyWordBean> lists, String keyWord) {
	// super(context, mDatas, itemLayoutId);
	// this.keyWord = keyWord;
	// }
	//
	// public KeyWordAdapter(Activity context, ArrayList<KeyWordBean> lists) {
	// }

	public KeyWordAdapter(Activity context, List<KeyWordBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(mContext, 44));
		// TODO Auto-generated constructor stub
	}

	public KeyWordAdapter(Activity context, List<KeyWordBean> mDatas, int itemLayoutId, String keyWord) {
		super(context, mDatas, itemLayoutId);
		layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(mContext, 44));
		this.keyWord = keyWord;
	}

	// @SuppressLint("InflateParams")
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// ViewHolder holder = null;
	// if (null == convertView) {
	// convertView = mInflater.inflate(R.layout.filter_item_layout, null, false);
	// holder = new ViewHolder();
	// holder.name = (TextView) convertView.findViewById(R.id.name_text);
	// holder.line = convertView.findViewById(R.id.view_line);
	// LayoutParams layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(mContext,
	// 44));
	// holder.line.setVisibility(View.GONE);
	// holder.name.setLayoutParams(layoutParams);
	// convertView.setTag(holder);
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// }
	// holder.name.setText(mLists.get(position).getTitle());
	// return convertView;
	// }

	@Override
	public void convert(ViewHolder helper, KeyWordBean item, int position) {
		helper.setViewVisibility(R.id.view_line, View.GONE);
		helper.setText(R.id.name_text, item.getTitle(), layoutParams);
	}

}

package com.shixi.gaodun.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.CityBean;

/**
 * 选择居住地的适配器
 * 
 * @author ronger
 * @date:2015-10-27 下午2:11:21
 */
public class SelectAddressAdapter extends BaseAdapter implements SectionIndexer {
	private ArrayList<CityBean> listCitys;
	private Activity mContext;
	private LayoutInflater mInflater;

	public SelectAddressAdapter(Activity context, ArrayList<CityBean> listCitys) {
		this.mContext = context;
		this.listCitys = listCitys;
		this.mInflater = LayoutInflater.from(context);
	}

	public void updateListView(ArrayList<CityBean> list) {
		this.listCitys = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listCitys.size();
	}

	@Override
	public Object getItem(int position) {
		return listCitys.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		if (null == convertView) {
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.select_address_item_layout, null);
			mHolder.addressName = (TextView) convertView.findViewById(R.id.tv_address_name);
			mHolder.catalog = (TextView) convertView.findViewById(R.id.catalog);
			mHolder.selected = (TextView) convertView.findViewById(R.id.tv_ifseleted_address);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		CityBean bean = listCitys.get(position);
		mHolder.selected.setVisibility(bean.isIfSelected() ? View.VISIBLE : View.GONE);
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			mHolder.catalog.setVisibility(View.VISIBLE);
			mHolder.catalog.setText(bean.getSortLetters());

			// if (bean.getSortLetters().equals("热")) {
			// // bean.setSortLetters("热");
			// mHolder.catalog.setText("热门词汇");
			// } else
			// mHolder.catalog.setText(bean.getSortLetters());
		} else {
			mHolder.catalog.setVisibility(View.GONE);
		}
		mHolder.addressName.setText(bean.getRegion_name());
		return convertView;
	}

	class ViewHolder {
		TextView addressName, selected, catalog;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = this.listCitys.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return 0;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return listCitys.get(position).getSortLetters().charAt(0);
	}
}

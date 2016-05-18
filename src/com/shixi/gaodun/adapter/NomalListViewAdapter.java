package com.shixi.gaodun.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.MajorClassifyBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * @author ronger
 * @param <E>
 * @param <T>
 * @date:2015-10-28 上午11:40:42
 */
public class NomalListViewAdapter<E> extends BaseAdapter {
	private ArrayList<MajorClassifyBean> mMajorLists;
	private LayoutInflater mInflater;
	// 0专业分类2证书分类证书名称
	private int type;
	// private ArrayList<String> mLists;
	private String mSelectStr;
	private String[] mSelectArray;
	private ArrayList<E> mLists;
	private Object obj;

	private List<Object> list;

	public NomalListViewAdapter(Context context, ArrayList<MajorClassifyBean> majorLists) {
		this.mMajorLists = majorLists;
		this.mInflater = LayoutInflater.from(context);
		this.type = 0;
	}

	// public void somalListViewAdapter2(List<Object> list) {
	//
	// }

	/**
	 * 一般情况使用基本的listview都用此构造方法
	 * 
	 * @param context
	 * @param lists
	 * @param type
	 */
	public NomalListViewAdapter(Context context, ArrayList<E> lists, int type) {
		this.mLists = lists;
		this.mInflater = LayoutInflater.from(context);
		this.type = type;
	}

	public NomalListViewAdapter(Context context, String[] lists, String selectStr) {
		this.mSelectArray = lists;
		this.mInflater = LayoutInflater.from(context);
		this.type = 1;
		this.mSelectStr = selectStr;
	}

	@Override
	public int getCount() {
		if (type == 0)
			return mMajorLists.size();
		if (type == 1) {
			return mSelectArray.length;
		}
		return mLists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (type == 0)
			return mMajorLists.get(position);
		if (type == 1) {
			return mSelectArray[position];
		}
		return mLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.nomal_listview_item_layout, null);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_nomal_listview_icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_nomal_listview_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (type == 0)
			setMajorClassifyListData(viewHolder, position);
		if (type == 1)
			setOtherListData(viewHolder, position);
		if (type == 2) {
			setCommonListData(viewHolder, position);
		}
		return convertView;
	}

	/**
	 * 专业列表的数据显示
	 * 
	 * @param viewHolder
	 * @param position
	 */
	public void setMajorClassifyListData(ViewHolder viewHolder, int position) {
		MajorClassifyBean bean = mMajorLists.get(position);
		viewHolder.name.setText(bean.getTitle());
		viewHolder.icon.setVisibility(bean.isIfSelected() ? View.VISIBLE : View.GONE);
	}

	/**
	 * 设置其他的列表数据的显示
	 * 
	 * @param viewHolder
	 * @param position
	 */
	public void setOtherListData(ViewHolder viewHolder, int position) {
		viewHolder.name.setText(mSelectArray[position]);
		if (StringUtils.isEmpty(mSelectStr)) {
			viewHolder.icon.setVisibility(View.GONE);
			return;
		}
		viewHolder.icon.setVisibility(mSelectArray[position].equals(mSelectStr) ? View.VISIBLE : View.GONE);
	}

	/**
	 * 设置常用的列表的显示
	 * 
	 * @param viewHolder
	 * @param position
	 */
	public void setCommonListData(ViewHolder viewHolder, int position) {
		MapFormatBean bean = (MapFormatBean) mLists.get(position);
		viewHolder.name.setText(bean.getValue());
		viewHolder.icon.setVisibility(bean.isSelected() ? View.VISIBLE : View.GONE);
	}

	class ViewHolder {
		TextView name;
		ImageView icon;
	}
}

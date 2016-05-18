package com.shixi.gaodun.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.ListViewUtils;

/**
 * 筛选弹框
 * 
 * @author ronger
 * @date:2015-11-9 下午3:47:50
 */
public class FilterDialog implements OnScrollListener {
	private LinearLayout mResultLayout;
	private Activity context;
	private FilterAdapter adapter;
	private ListView listview;
	private SelectListener leftListener;
	private View oldView;
	private ArrayList<MapFormatBean> list;
	private int position = 0;
	private TextView mTvAll;// 全部
	private View viewLine;
	private boolean isAll = true;// 是否选择全部

	public interface SelectListener {
		public void select(int Position, View view, long ViewId);
	}

	public LinearLayout getResultLayout() {
		return mResultLayout;
	}

	/**
	 * 设置更新
	 * 
	 * @param list
	 */
	public void setNotify(ArrayList<MapFormatBean> list) {
		this.list = list;
		adapter.notifyDataSetChanged();
	}

	public void selectPosition() {
		// TODO Auto-generated method stub
		listview.setSelection(position);
	}

	public void setSelectListener(SelectListener leftListener) {
		this.leftListener = leftListener;
	}

	public void getFocus() {
		listview.requestFocus();
	}

	public FilterDialog(Activity context) {
		this.context = context;
		init();
	}

	private void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		mResultLayout = (LinearLayout) inflater.inflate(R.layout.filter_dialog,
				null);
		listview = (ListView) mResultLayout.findViewById(R.id.left_listview);
		mTvAll = (TextView) mResultLayout.findViewById(R.id.tv_all);
		viewLine = (View) mResultLayout.findViewById(R.id.view_line);
		mTvAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTvAll.setSelected(true);
				isAll = true;
				adapter.setSelectedPosition(-1);
				adapter.notifyDataSetChanged();
				if (leftListener != null) {
					leftListener.select(-1, null, 0);
				}
			}
		});
		// listview.setOnScrollListener(this);
	}

	public void setAdapter(ArrayList<MapFormatBean> list) {
		this.list = list;
		// adapter = new FilterAdapter(context, this.list);
		adapter = new FilterAdapter(context, this.list,
				R.layout.filter_item_layout);
		listview.setAdapter(adapter);
		int listViewHeight = ListViewUtils
				.getListViewHeightBasedOnChildren(listview);
		if (listViewHeight > ActivityUtils.getScreenHeight() * 1 / 2) {
			listview.setLayoutParams(new LayoutParams(ActivityUtils
					.getScreenWidth(), ActivityUtils.getScreenHeight() * 1 / 2));
		} else {
			listview.setLayoutParams(new LayoutParams(ActivityUtils
					.getScreenWidth(), listViewHeight));
		}
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				position = arg2;
				isAll = false;
				adapter.setSelectedPosition(arg2);
				adapter.notifyDataSetChanged();
				if (leftListener != null) {
					leftListener.select(arg2, arg1, arg3);
				}
			}
		});
	}

	@SuppressWarnings("unused")
	@SuppressLint("InflateParams")
	private class FilterAdapter extends CommonAdapter<MapFormatBean> {

		ViewHolder holder;
		LayoutInflater inflater;
		View view;
		private int selectedPosition = 0;// 选中的位置
		LayoutParams layoutParams;

		public FilterAdapter(Activity context, List<MapFormatBean> mDatas,
				int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
			layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(),
					ActivityUtils.dip2px(mContext, 44));
		}

		// public FilterAdapter(Activity context, ArrayList<MapFormatBean>
		// lists) {
		// super(context, lists);
		// }

		@SuppressWarnings("unused")
		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		// @SuppressLint("ViewHolder")
		// @Override
		// public View getView(int position, View convertView, ViewGroup parent)
		// {
		// inflater = (LayoutInflater)
		// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// view = inflater.inflate(R.layout.filter_item_layout, null, false);
		// holder = (ViewHolder) view.getTag();
		// if (holder == null) {
		// holder = new ViewHolder();
		// holder.name = (TextView) view.findViewById(R.id.name_text);
		// holder.line = view.findViewById(R.id.view_line);
		// LayoutParams layoutParams = new
		// LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(
		// mContext, 44));
		// holder.name.setLayoutParams(layoutParams);
		// view.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }
		// if (position == selectedPosition) {
		// holder.name.setSelected(true);
		// holder.line.setBackgroundColor(mContext.getResources().getColor(R.color.nomal_btn_color));
		// } else {
		// holder.name.setSelected(false);
		// holder.line.setBackgroundColor(mContext.getResources().getColor(R.color.title_bar_buttom_color));
		// }
		// holder.name.setText(mLists.get(position).getValue());
		//
		// return view;
		// }

		@Override
		public void convert(ViewHolder helper, MapFormatBean item, int position) {
			TextView name = helper.getView(R.id.name_text);
			name.setLayoutParams(layoutParams);
			View line = helper.getView(R.id.view_line);
			if (position == selectedPosition && isAll == false) {
				name.setSelected(true);
				line.setBackgroundColor(mContext.getResources().getColor(
						R.color.nomal_btn_color));
				mTvAll.setSelected(false);
			} else {
				name.setSelected(false);
				line.setBackgroundColor(mContext.getResources().getColor(
						R.color.title_bar_buttom_color));
			}
			helper.setText(R.id.name_text, item.getValue());

		}

		// private class ViewHolder {
		// TextView name;
		// View line;
		// }

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// System.out.println("firstVisibleItem--------"+firstVisibleItem);
		// if(firstVisibleItem>=1){
		// mTvAll.setVisibility(View.VISIBLE);
		// }else if(firstVisibleItem==0){
		// mTvAll.setVisibility(View.GONE);
		// }
	}

}

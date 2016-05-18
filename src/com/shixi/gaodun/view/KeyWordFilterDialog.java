package com.shixi.gaodun.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.model.domain.KeyWordBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.ActivityUtils;

/**
 * 匹配对话框
 * 
 * @author ronger
 * @date:2015-11-10 下午5:43:42
 */
public class KeyWordFilterDialog {
	private LinearLayout mResultLayout;
	private Activity context;
	private FilterKeyWordAdapter adapter;
	private ListView listview;
	private onKeyWordItemClickListener itemClickListener;
	private ArrayList<KeyWordBean> list;

	public interface onKeyWordItemClickListener {
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
	public void setNotify(ArrayList<KeyWordBean> list) {
		this.list = list;
		adapter.mLists = list;
		adapter.notifyDataSetChanged();
	}

	public void setSelectListener(onKeyWordItemClickListener leftListener) {
		this.itemClickListener = leftListener;
	}

	public void getFocus() {
		listview.requestFocus();
	}

	public KeyWordFilterDialog(Activity context) {
		this.context = context;
		init();
	}

	@SuppressLint("InflateParams")
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		mResultLayout = (LinearLayout) inflater.inflate(R.layout.filter_dialog, null);
		listview = (ListView) mResultLayout.findViewById(R.id.left_listview);
		FrameLayout moreLayout = (FrameLayout) mResultLayout.findViewById(R.id.filter_more_layout);
		moreLayout.setVisibility(View.GONE);
	}

	public void setAdapter(ArrayList<KeyWordBean> list, String keyWord) {
		this.list = list;
		adapter = new FilterKeyWordAdapter(context, this.list, R.layout.filter_item_layout, keyWord);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				adapter.notifyDataSetChanged();
				if (itemClickListener != null) {
					itemClickListener.select(arg2, arg1, arg3);
				}
			}
		});
	}

	@SuppressWarnings("unused")
	@SuppressLint("InflateParams")
	private class FilterKeyWordAdapter extends CommonAdapter<KeyWordBean> {

		ViewHolder holder;
		LayoutInflater inflater;
		View view;
		private String keyWord;
		private LayoutParams layoutParams;

		public FilterKeyWordAdapter(Activity context, List<KeyWordBean> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
			layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(mContext, 44));
		}

		public FilterKeyWordAdapter(Activity context, List<KeyWordBean> mDatas, int itemLayoutId, String keyWord) {
			super(context, mDatas, itemLayoutId);
			this.keyWord = keyWord;
			layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(mContext, 44));
		}

		// public FilterKeyWordAdapter(Activity context, ArrayList<KeyWordBean> lists) {
		// super(context, lists);
		// }
		//
		// public FilterKeyWordAdapter(Activity context, ArrayList<KeyWordBean> lists, String keyWord) {
		// super(context, lists);
		// this.keyWord = keyWord;
		// }

		// @SuppressLint("ViewHolder")
		// @Override
		// public View getView(int position, View convertView, ViewGroup parent) {
		// inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// view = inflater.inflate(R.layout.filter_item_layout, null, false);
		// holder = (ViewHolder) view.getTag();
		// if (holder == null) {
		// holder = new ViewHolder();
		// holder.name = (TextView) view.findViewById(R.id.name_text);
		// holder.line = view.findViewById(R.id.view_line);
		// LayoutParams layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(
		// mContext, 44));
		// holder.name.setLayoutParams(layoutParams);
		// view.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }
		// String title = mLists.get(position).getTitle();
		// holder.name.setText(title);
		// return view;
		// }

		//
		@Override
		public void convert(ViewHolder helper, KeyWordBean item, int position) {
			helper.setText(R.id.name_text, item.getTitle(), layoutParams);
		}

		// private class ViewHolder {
		// TextView name;
		// View line;
		// }

	}
}

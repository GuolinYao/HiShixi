package com.shixi.gaodun.view.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewFragment;
import com.shixi.gaodun.custom.view.SwipeMenu;
import com.shixi.gaodun.custom.view.SwipeMenuCreator;
import com.shixi.gaodun.custom.view.SwipeMenuItem;
import com.shixi.gaodun.inf.OnActivityRefreshListener;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.InvitationActionSheetDialog;
import com.shixi.gaodun.view.activity.enterprise.PositionDetailActivity;

/**
 * 收藏的职位列表
 * 
 * @author ronger
 * @date:2015-12-5 下午5:15:33
 */
public class CollectPositionFragment extends BaseListViewFragment<PositionBean> implements OnItemLongClickListener,
		OnclickSelectedIdCallBack, OnActivityRefreshListener, SwipeMenuCreator {
	// private View mNoneDataLayout;
	private com.shixi.gaodun.view.activity.mine.MineCollectionActivity mParentActivity;
	private int mRequestType = 0;
	private int mDeleteType = 2;// 1 单条删除 2 清空
	private int mCurrentPosition;
	private String post_id;// 职位id

	// @Override
	// public void onResume() {
	// super.onResume();
	// if (null == mLists || mLists.size() <= 0)
	// mParentActivity.setTextEnabled(false, 0);
	// else {
	// mParentActivity.setTextEnabled(true, 0);
	// }
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isExcute = true;
		setFilterRegister();
		// getPositionList();
	}

	@Override
	public void toRequestServer(Intent intent) {
		if (intent.getAction().equals(Contants.ALL_UPDATE_ACTION)) {
			mRequestType = 0;
			mPage = 1;
			setRequestParamsPre(TAG, false);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mRequestType == 0) {
			getPositionList();
			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();
			}
			return;
		}
		if (mRequestType == 1) {
			deleteCollectPosition();
			return;
		}
	}

	/**
	 * 获取职位列表
	 */
	public void getPositionList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("student_id", CacheUtils.getStudentId(mContext));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.COLLECT_POSITIONLIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
		Log.i("----", "mLists----" + mLists);

	}

	public void deleteCollectPosition() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("student_id", CacheUtils.getStudentId(mContext));
		if (mDeleteType == 1) {
			map.put("post_id", post_id);
		}
		map.put("type", String.valueOf(mDeleteType));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CANCEL_COLLECTPOSITION_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					mRequestType = 0;
					HttpRes httpRes = TransFormModel.getResponseData(response);
					dissMissProgress();
					if (httpRes.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(httpRes.getReturnDesc());
						return;
					}
					if (mDeleteType == 1) {// 删除的单个的，移除对应的item
						mLists.remove(mCurrentPosition);
						mListAdapter.notifyDataSetChanged();
						return;
					}
					if (mDeleteType == 2) {// 删除所有，移除对应的item
						mLists.clear();
						mListAdapter.notifyDataSetChanged();
						return;
					}
				} catch (Exception e) {
					dissMissProgress();
					setDebugLog(e);
					exceptionToast();
				}

			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	// @SuppressLint("HandlerLeak")
	// private Handler mHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// if (msg.what == 0) {
	// getResponseComplete();
	// }
	// }
	// };

	@Override
	public void getResponse(JSONObject response) {
		Log.i("----", "getResponse");
		try {
			// mHandler.sendEmptyMessage(0);
			isFristRequest = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				getResponseComplete();
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			JSONObject object = new JSONObject(httpRes.getReturnData());
			mListTotal = object.getInt("listTotal");
			if (mListTotal <= 0) {
				getResponseComplete();
				setNoneDataShow();
				mParentActivity.setTextEnabled(false, 0);
				mLists = null;
				return;
			}
			String resultStr = object.getString("list");
			mParentActivity.setTextEnabled(true, 0);
			setListPullShow();
			List<PositionBean> lists = TransFormModel.getResponseResults(resultStr, PositionBean.class);
			getListSuccess(lists);
			getResponseComplete();
		} catch (Exception e) {
			getResponseComplete();
			setDebugLog(e);
			exceptionToast();
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		getResponseComplete();
		if (isExcute && isFristRequest && mPage == 1 && mRequestType == 0) {
			setOnErrorResponse(error);
			return;
		}
		nomalOnErrorResponse(error);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		PositionBean item = mLists.get(index);
		// 职位状态 1 上架 2 已下架
		String post_status = item.getPost_status();
		String pkId = item.getPkid();
		if (StringUtils.isNotEmpty(post_status) && post_status.equals("2")) {
			return;
		}
		PositionDetailActivity.startAction(mContext, pkId,1);

	}

	// @Override
	// public void setListDataShow() {
	// if (null == mDeletListAdapter || mPage * mPageNumber <= mPageNumber) {//
	// 第一次加载或刷新
	// mDeletListAdapter = null;
	// initAdapter();
	// myListView.setAdapter(mDeletListAdapter);
	// } else {
	// mDeletListAdapter.notifyDataSetChanged();
	// }
	// }

	@Override
	public void initAdapter() {
		// mListAdapter = new SeachPositionAdapter(mContext, mLists,
		// R.layout.tab_position_item_layout);
		mListAdapter = new CommonAdapter<PositionBean>(mContext, mLists, R.layout.collect_position_item_layout) {
			@Override
			public void convert(ViewHolder helper, PositionBean item, int position) {
				helper.setImageByUrl(R.id.iv_commpany_image, item.getLogo(), R.drawable.default_image_icon, 5);
				helper.setText(R.id.tv_position_name, item.getTitle());
				helper.setText(R.id.tv_publish_time, item.getCreate_time());
				helper.setText(R.id.tv_position_address, item.getPost_city());
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
							new ForegroundColorSpan(mContext.getResources().getColor(R.color.salary_range_font_color)),
							0, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					textView.setText(spannableString);
				}
				// helper.setText(R.id.tv_salary_range, item.getSalary_range());
				helper.setText(R.id.tv_education, item.getEducation());
				helper.setText(R.id.tv_weekday, item.getWeek_available());
				helper.setText(R.id.tv_commpany_name, item.getEnterprise_name());
				helper.setText(R.id.tv_industry_name, item.getIndustry_name());
				helper.setText(R.id.tv_commpany_number, item.getScale_name());
				// 职位状态 1 上架 2 已下架
				String post_status = item.getPost_status();
				if (StringUtils.isNotEmpty(post_status) && post_status.equals("2")) {
					helper.setRelativeLayoutVisibility(R.id.layout_offline, View.VISIBLE);
				} else {
					helper.setRelativeLayoutVisibility(R.id.layout_offline, View.GONE);
				}
			}
		};
	}

	@Override
	public void refreshList() {
		mPage = 1;
		setRequestParamsPre(TAG, false);
	}

	@Override
	public void loadMoreList() {
		setRequestParamsPre(TAG, false);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_again) {
			refreshList();
		}

	}

	/**
	 * 获取整体布局
	 */
	@Override
	protected int getFrameLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.base_pullrefresh_listview;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void initView(View view) {
		mParentActivity = (com.shixi.gaodun.view.activity.mine.MineCollectionActivity) getActivity();
		mParentActivity.setOnActivityRefreshListenerPosition(this);
		initListView(view);
		// myListView.setMenuCreator(this);
		mPullToRefreshListView.setBackgroundColor(Color.parseColor("#F2F2F5"));
		myListView.setOnItemLongClickListener(this);
		myListView.setDivider(new ColorDrawable(Color.parseColor("#F2F2F5")));
		myListView.setDividerHeight(ActivityUtils.dip2px(mContext, 15));
		setNoneDataDesc("暂无收藏", "没有收藏癖的你，去哪里找回忆");
		setRequestParamsPre(TAG, true);
		// refreshList();
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return true;
		mDeleteType = 1;
		mCurrentPosition = index;
		post_id = mLists.get(index).getPkid();
		InvitationActionSheetDialog.showAlert(mContext, this, "取消收藏", getResources().getColor(R.color.nomal_btn_color));
		return true;
	}

	@Override
	public void onClickCallBack(int viewId) {
		if (viewId == R.id.btn_select) {// 删除消息
			mRequestType = 1;
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void onRefreshList() {
		refreshList();
	}

	@Override
	public void onCheckClearBtnCanClick() {
		mParentActivity.setTextEnabled(null == mLists || mLists.size() <= 0 ? false : true, 0);
	}

	@Override
	public void onRefreshList(int index) {
	}

	/**
	 * 创建删除按钮
	 */
	@Override
	public void create(SwipeMenu menu) {
		SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
		// set item background
		deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
		// set item width
		deleteItem.setWidth(ActivityUtils.dip2px(mContext, 90));
		// set a icon
		deleteItem.setIcon(R.drawable.ic_delete);
		// add to menu
		menu.addMenuItem(deleteItem);
	}
}

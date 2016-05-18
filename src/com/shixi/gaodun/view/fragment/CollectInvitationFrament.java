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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewFragment;
import com.shixi.gaodun.inf.OnActivityRefreshListener;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.InvitationActionSheetDialog;
import com.shixi.gaodun.view.activity.invitation.InvitationDetailListActivity;

/**
 * 收藏的帖子
 * 
 * @author ronger
 * @date:2015-12-7 上午9:18:39
 */
public class CollectInvitationFrament extends BaseListViewFragment<InvitationInfoBean> implements
		OnItemLongClickListener, OnclickSelectedIdCallBack, OnActivityRefreshListener {
	private com.shixi.gaodun.view.activity.mine.MineCollectionActivity mParentActivity;
	private int mRequestType = 0;
	private int mDeleteType = 2;// 1 单条删除 2 清空
	private int mCurrentPosition;
	private String topic_id;// 帖子id

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isExcute = true;
		setFilterRegister();
		// getInvitationList();//TODO
	}

	@Override
	public void toRequestServer(Intent intent) {
		if (intent.getAction().equals(Contants.ALL_UPDATE_ACTION)) {
			mRequestType = 0;
			mPage = 1;
			setRequestParamsPre(TAG, true);
		}
	}

	@Override
	public void setRequestParams(String className) {
		// super.setRequestParams(className);
		if (mRequestType == 0) {
			getInvitationList();
			return;
		}
		if (mRequestType == 1) {
			deleteCollectInvitation();
			return;
		}
	}

	public void getInvitationList() {
		// Log.i("----", "getInvitationList");
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		// map.put("student_id", "212");
		map.put("student_id", CacheUtils.getStudentId(mContext));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.COLLECT_INVITATIONLIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void deleteCollectInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("student_id", CacheUtils.getStudentId(mContext));
		if (mDeleteType == 1) {
			map.put("topic_id", topic_id);
		}
		map.put("type", String.valueOf(mDeleteType));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CANCEL_COLLECTINVITATION_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
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

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				getResponseComplete();
			}
		}
	};

	@Override
	public void getResponse(JSONObject response) {
		Log.i("----", "getResponse--invitation");
		try {
			// mHandler.sendEmptyMessage(0);
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				getResponseComplete();
				return;
			}
			JSONObject object = new JSONObject(httpRes.getReturnData());
			mListTotal = object.getInt("collectTotal");
			if (mListTotal <= 0) {
				setNoneDataShow();
				getResponseComplete();
				mParentActivity.setTextEnabled(false, 1);
				mLists = null;
				return;
			}
			String resultStr = object.getString("collect");
			mParentActivity.setTextEnabled(true, 1);
			setListPullShow();
			List<InvitationInfoBean> lists = TransFormModel.getInvitationInfoList(resultStr, false);
			getListSuccess(lists);
			getResponseComplete();
		} catch (Exception e) {
			getResponseComplete();
			exceptionToast();
			setDebugLog(e);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		InvitationDetailListActivity.startAction(mContext, mLists.get(index));
	}

	@Override
	public void initAdapter() {
		mListAdapter = new CommonAdapter<InvitationInfoBean>(mContext, mLists, R.layout.invitation_item_layout) {
			@Override
			public void convert(ViewHolder helper, InvitationInfoBean item, int position) {
				helper.setText(R.id.invitation_title, item.getTitle());
				// 头像+昵称+发布时间
				helper.setText(R.id.text_name, item.getNickname());
				helper.setCircleImageByUrl(R.id.image_user, item.getSns_avatar(), R.drawable.default_touxiang_xiaoerhei);
				// helper.setImageByUrl(R.id.image_user, item.getSns_avatar(), R.drawable.default_list_toux_icon, 50);
				helper.setText(R.id.text_time, item.getRefresh_time());
				// 设置帖子图片的显示与隐藏
				if (StringUtils.isEmpty(item.getSingeimage())) {
					helper.setImageViewVisibility(R.id.image_view, View.GONE);
				} else {
					helper.setImageByUrl(R.id.image_view, item.getSingeimage(), R.drawable.default_image_icon, 0);
				}
				// 帖子内容
				helper.setText(R.id.invitation_desc, item.getContent());
				// 点赞数
				helper.setText(R.id.text_praisenumber, item.getFavor_num());
				// 评论数
				helper.setText(R.id.text_commentnumber, item.getComment_num());
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

	@Override
	protected int getFrameLayoutId() {
		// TODO Auto-generated method stub
		return R.layout.base_pullrefresh_listview;
	}

	@SuppressLint("InflateParams")
	@Override
	protected void initView(View view) {
		mParentActivity = (com.shixi.gaodun.view.activity.mine.MineCollectionActivity) getActivity();
		// mParentActivity = (MineCollectionActivity) getActivity();
		mParentActivity.setOnActivityRefreshListenerInvitation(this);
		initListView(view);
		mPullToRefreshListView.setBackgroundColor(Color.parseColor("#F2F2F5"));
		myListView.setOnItemLongClickListener(this);
		myListView.setDivider(new ColorDrawable(Color.parseColor("#F2F2F5")));
		myListView.setDividerHeight(ActivityUtils.dip2px(mContext, 15));
		setNoneDataDesc("暂无收藏", "没有收藏癖的你，去哪里找回忆");
		// mNoneDataLayout = LayoutInflater.from(mContext).inflate(R.layout.pisition_none_data_layout, null);
		// TextView name = (TextView) mNoneDataLayout.findViewById(R.id.text_title);
		// name.setText("暂无收藏");
		// TextView desc = (TextView) mNoneDataLayout.findViewById(R.id.text_desc);
		// desc.setText("没有收藏癖的你，去哪里找回忆");
		// mMainNoneDataLayout.removeAllViews();
		// mMainNoneDataLayout.addView(mNoneDataLayout);
		// refreshList();
		setRequestParamsPre(TAG, true);
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
			return false;
		mDeleteType = 1;
		mCurrentPosition = index;
		topic_id = mLists.get(index).getTopic_id();
		InvitationActionSheetDialog.showAlert(mContext, this, "取消收藏", getResources().getColor(R.color.nomal_btn_color));
		return false;
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
		mParentActivity.setTextEnabled(null == mLists || mLists.size() <= 0 ? false : true, 1);
	}

	@Override
	public void onRefreshList(int index) {
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		getResponseComplete();
		if (isExcute && isFristRequest && mPage == 1 && mRequestType == 0) {
			setOnErrorResponse(error);
			mParentActivity.setTextEnabled(false, 1);
			return;
		}
		nomalOnErrorResponse(error);
	}

}

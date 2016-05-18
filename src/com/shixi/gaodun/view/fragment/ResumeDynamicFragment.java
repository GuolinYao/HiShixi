package com.shixi.gaodun.view.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.ResumeDynamicAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewFragment;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.ResumeDynamicBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.activity.enterprise.PositionDetailActivity;

/**
 * 简历动态的列表对应的fragment
 * 
 * @author ronger
 * @date:2015-12-5 上午11:58:32
 */
public class ResumeDynamicFragment extends BaseListViewFragment<ResumeDynamicBean> {
	private static final String FRAGMENT_INDEX = "fragment_index";
	private static final String FRAGMENT_TYPE = "fragment_type";
	private int mType;// 1 全部 3 通知面试 4不合适
	private int mIndex;
	private boolean isInit; // 是否可以开始加载数据
	private View rootView;
	private boolean isCreate = false;

	public static ResumeDynamicFragment newInstance(int type, int index) {
		Bundle bundle = new Bundle();
		bundle.putInt(FRAGMENT_INDEX, index);
		bundle.putInt(FRAGMENT_TYPE, type);
		ResumeDynamicFragment newFragment = new ResumeDynamicFragment();
		newFragment.setArguments(bundle);
		// newFragment.mType = type;
		return newFragment;
	}

	// public ResumeDynamicFragment(int type) {
	// this.mType = type;
	// getClassName();
	// TAG = TAG + type;
	// }

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// if (null == mView) {
	// mView = super.onCreateView(inflater, container, savedInstanceState);
	// Bundle bundle = getArguments();
	// if (bundle != null) {
	// mCurIndex = bundle.getInt(FRAGMENT_INDEX);
	// mType = bundle.getInt(FRAGMENT_TYPE);
	// }
	// isPrepared = true;
	// lazyLoad();
	// }
	// // 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
	// ViewGroup parent = (ViewGroup) mView.getParent();
	// if (parent != null) {
	// parent.removeView(mView);
	// }
	// return mView;
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isExcute = true;
		getClassName();
		Bundle bundle = getArguments();
		if (bundle != null) {
			mType = bundle.getInt(FRAGMENT_TYPE);
			mIndex = bundle.getInt(FRAGMENT_INDEX);
			TAG = TAG + mIndex;
		}
		isInit = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = super.onCreateView(inflater, container, savedInstanceState);
			isCreate = true;
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		// 判断当前fragment是否显示
		if (getUserVisibleHint()) {
			showData();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// 每次切换fragment时调用的方法
		if (isVisibleToUser) {
			showData();
		}
	}

	private void showData() {
		if (isInit && isCreate) {
			isInit = false;// 加载数据完成
			// 加载各种数据
			setRequestParamsPre(TAG, true);
			Log.e("gaodun", "type:" + mType);
		}
	}

	@Override
	public void setRequestParams(String className) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("type", String.valueOf(mType));
		map.put("student_id", CacheUtils.getStudentId(mContext));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.RESUMEBYNAMIC_LIST_URL, map, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				getResponseSuccess(response);
			}
		}, this);
		// new JsonObjectPostRequest(StringUtils.getCommonIP()
		// + GlobalContants.RESUMEBYNAMIC_LIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void getResponseSuccess(JSONObject response) {
		try {
			isFristRequest = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				getResponseComplete();
				return;
			}
			JSONObject object = new JSONObject(httpRes.getReturnData());
			mListTotal = object.getInt("listTotal");
			String resultStr = object.getString("list");
			if (mListTotal <= 0 || StringUtils.isEmpty(resultStr) || resultStr.length() <= 2) {
				setNoneDataShow();
				setNoneDataDesc("暂无记录", "快去投递心仪的职位吧");
				getResponseComplete();
				return;
			}
			setListPullShow();
			List<ResumeDynamicBean> lists = TransFormModel.getResponseResults(resultStr, ResumeDynamicBean.class);
			getListSuccess(lists);
			getResponseComplete();
		} catch (Exception e) {
			exceptionToast();
			setDebugLog(e);
			getResponseComplete();
		}
	}

	// @Override
	// public void getResponse(JSONObject response) {
	// try {
	// getResponseComplete();
	// isFristRequest = false;
	// HttpRes httpRes = TransFormModel.getResponseData(response);
	// if (httpRes.getReturnCode() != 0) {
	// ToastUtils.showToastInCenter(httpRes.getReturnDesc());
	// return;
	// }
	// JSONObject object = new JSONObject(httpRes.getReturnData());
	// mListTotal = object.getInt("listTotal");
	// String resultStr = object.getString("list");
	// if (mListTotal <= 0 || StringUtils.isEmpty(resultStr) || resultStr.length() <= 2) {
	// setNoneDataShow();
	// setNoneDataDesc("暂无记录", "快去投递心仪的职位吧");
	// return;
	// }
	// setListPullShow();
	// List<ResumeDynamicBean> lists = TransFormModel.getResponseResults(resultStr, ResumeDynamicBean.class);
	// getListSuccess(lists);
	// } catch (Exception e) {
	// exceptionToast();
	// setDebugLog(e);
	// }
	// }

	@Override
	public void onErrorResponse(VolleyError error) {
		getResponseComplete();
		if (isExcute && isFristRequest && mPage == 1) {
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
		ResumeDynamicBean item = mLists.get(index);
		String postStatus = item.getPost_status();
		String postId = item.getPost_id();
		// 职位状态 1 上架 2 下架
		if (StringUtils.isNotEmpty(postStatus) && postStatus.equals("2")) {
			return;
		}
		PositionDetailActivity.startAction(mContext, postId,1);
	}

	@Override
	public void initAdapter() {
		mListAdapter = new ResumeDynamicAdapter(mContext, mLists, R.layout.resume_dynamic_item_layout);
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
			setRequestParamsPre(TAG);
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
		initListView(view);
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

}

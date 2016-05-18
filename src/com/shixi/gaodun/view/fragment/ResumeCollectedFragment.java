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
import com.shixi.gaodun.adapter.ResumeCollectedAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewFragment;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.ResumeCollectedBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * 猎头已收集简历fragment
 * @author guolinyao
 * @date 2016年4月18日 上午11:05:58
 */
public class ResumeCollectedFragment extends BaseListViewFragment<ResumeCollectedBean> {
	private static final String FRAGMENT_INDEX = "fragment_index";
	private static final String FRAGMENT_TYPE = "fragment_type";
	private int mType;// 1 未验证 2 已验证 3 有效简历
	private int mIndex;
	private boolean isInit; // 是否可以开始加载数据
	private View rootView;
	private boolean isCreate = false;

	public static ResumeCollectedFragment newInstance(int type, int index) {
		Bundle bundle = new Bundle();
		bundle.putInt(FRAGMENT_INDEX, index);
		bundle.putInt(FRAGMENT_TYPE, type);
		ResumeCollectedFragment newFragment = new ResumeCollectedFragment();
		newFragment.setArguments(bundle);
		return newFragment;
	}

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
		map.put("school_hunter_id", CacheUtils.get_hunter_id(mContext));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.HUNTER_LIST_RESUMEList, map, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				getResponseSuccess(response);
			}
		}, this);
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
//			System.out.println("listtotal=="+mListTotal);
//			System.out.println("resultStr=="+resultStr);
			if (mListTotal <= 0 || StringUtils.isEmpty(resultStr) || resultStr.length() <= 2) {
				setNoneDataShow();
				setNoneDataDesc("暂无记录", "");
				getResponseComplete();
				return;
			}
			setListPullShow();
			List<ResumeCollectedBean> lists = TransFormModel.getResponseResults(resultStr, ResumeCollectedBean.class);
			getListSuccess(lists);
			getResponseComplete();
		} catch (Exception e) {
			exceptionToast();
			setDebugLog(e);
			getResponseComplete();
		}
	}

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
//		int index = position - myListView.getHeaderViewsCount();
//		if (index < 0)
//			return;
//		ResumeCollectedBean item = mLists.get(index);
//		String postId = item.getPost_id();
//		PositionDetailActivity.startAction(mContext, postId,1);
	}

	@Override
	public void initAdapter() {
		mListAdapter = new ResumeCollectedAdapter(mContext, mLists, R.layout.item_collected_resume_layout);
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

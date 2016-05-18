package com.shixi.gaodun.view.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.BannerAdapter;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.ParentFragment;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.libpull.PullToRefreshBase.OnRefreshListener;
import com.shixi.gaodun.libpull.PullToRefreshScrollView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CompanyBean;
import com.shixi.gaodun.model.domain.HomeBannerBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.TopicInfo;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.BannerLayout;
import com.shixi.gaodun.view.CircleFlowIndicator;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.MyListView;
import com.shixi.gaodun.view.ObservableScrollView;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.shixi.gaodun.view.activity.enterprise.EnterpriseDetailActivity;
import com.shixi.gaodun.view.activity.enterprise.PositionSearchActivity;
import com.shixi.gaodun.view.activity.invitation.InvitationDetailListActivity;
import com.shixi.gaodun.view.activity.messagecenter.MessageActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页
 * 
 * @author ronger
 * @date:2015-11-13 下午5:00:45
 */
public class TabHomeFragment extends ParentFragment {
	// private ObservableScrollView mScrollView;
	private PullToRefreshScrollView mPullRefreshScrollView;
	private RelativeLayout mContentView;
	private BannerLayout mBannerLayout;
	private CircleFlowIndicator mIndicator;
	private MyListView mTopicListView, mCommpanyListView;
	private boolean isLoadBannerFinish, isLoadTopicFinish,
			isLoadCommpanyFinish;
	private ArrayList<HomeBannerBean> mBannerLists;
	private BannerAdapter mBannerAdapter;
	private ArrayList<CompanyBean> mCommpanylists;
	private ArrayList<TopicInfo> mTopicLists;
	private Dialog mDialog;
	private ScrollView mScrollView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.tab_home_layout, container, false);
		setRequestParamsPre(TAG);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isExcute = true;
	}

	@SuppressLint("InflateParams")
	@Override
	public void initView(View view) {
		mMainNoneDataLayout = (FrameLayout) view
				.findViewById(R.id.layout_error);
		view.findViewById(R.id.fl_title_bar_back);
		view.findViewById(R.id.fl_title_bar_collect);
		mPullRefreshScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.scrollview_home);
		mScrollView = mPullRefreshScrollView.getRefreshableView();
		// 设置首页下拉刷新监听
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// new GetDataTask().execute();
				setRequestParamsPre(TAG, false);
				// 刷新时停止轮播
				mBannerLayout.stopAutoFlowTimer();
			}

		});

		view.findViewById(R.id.fl_title_bar_back).setOnClickListener(this);
		view.findViewById(R.id.fl_title_bar_collect).setOnClickListener(this);
		mContentView = (RelativeLayout) LayoutInflater.from(mContext).inflate(
				R.layout.tab_home_content_layout, null);
		RelativeLayout.LayoutParams LP_FW = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mScrollView.addView(mContentView, LP_FW);
		mCommpanyListView = (MyListView) mContentView
				.findViewById(R.id.listview_commpany_home);
		mCommpanyListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String companyId = mCommpanylists.get(position).getPkid();
				EnterpriseDetailActivity.startAction(mContext, companyId,1);
			}
		});
		mTopicListView = (MyListView) mContentView
				.findViewById(R.id.listview_topic_home);
		mTopicListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String topicId = mTopicLists.get(position).getTopic_id();
				InvitationDetailListActivity.startAction(mContext,
						new InvitationInfoBean(topicId));
			}
		});
		mIndicator = (CircleFlowIndicator) mContentView
				.findViewById(R.id.indicator);
		mBannerLayout = (BannerLayout) mContentView
				.findViewById(R.id.viewpager_banner);
		mBannerLayout.setViewGroup(mPullRefreshScrollView);
		mIndicator.setVisibility(View.GONE);

	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				// 刷新时停止轮播
				mBannerLayout.stopAutoFlowTimer();
				setRequestParamsPre(TAG, false);
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Do some stuff here

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshScrollView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button_again) {
			isLoadBannerFinish = false;
			isLoadTopicFinish = false;
			isLoadCommpanyFinish = false;
			if (ActivityUtils.isNetAvailable()) {
				setMainShow();
				setRequestParamsPre(TAG);
			} else {
				setErrorShow();
				setErrorInfoShow("你的网络不给力，数据加载失败了");
			}
		}
		if (v.getId() == R.id.fl_title_bar_back) {
			PositionSearchActivity.startAction(mContext);
		}
		if (v.getId() == R.id.fl_title_bar_collect) {
			if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
				mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
				return;
			}
			MessageActivity.startAction(mContext);
		}
		if (v.getId() == R.id.btn_select_two) {// 登陆
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			LoginActivity.startAction(mContext, 1, false);
			mDialog.dismiss();
		}
		if (v.getId() == R.id.btn_select_three) {// 注册
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			RegisterActivity.startAction(mContext, 1, top);
			mDialog.dismiss();
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		getBannerList();
		getHotTopic();
		getHotEnterprise();
	}

	/**
	 * 热门话题
	 */
	public void getHotTopic() {
		isLoadTopicFinish = false;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(mContext)))
			map.put("student_id", CacheUtils.getStudentId(mContext));

		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETHOTTOPIC_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						isLoadTopicFinish = true;
						if (isLoadBannerFinish && isLoadCommpanyFinish
								&& isLoadTopicFinish)
							dissMissProgress();
						try {
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}

							JSONObject object = new JSONObject(httpRes
									.getReturnData());
							String hotStr = object.getString("list");
							if (StringUtils.isEmpty(hotStr)
									|| hotStr.length() <= 2) {
								setListViewShow();
								return;
							}
							mTopicLists = (ArrayList<TopicInfo>) TransFormModel
									.getResponseResults(hotStr, TopicInfo.class);
							setListViewShow();
							// setTopicShow();
						} catch (Exception e) {
							dissMissProgress();
							exceptionToast();
						}

					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void setListViewShow() {
		if (isLoadTopicFinish && isLoadCommpanyFinish) {
			setTopicShow();
			setCommpanyShow();
			// 刷新完成
			mPullRefreshScrollView.onRefreshComplete();
		}
	}

	public void setTopicShow() {
		CommonAdapter<TopicInfo> mTopicAdapter = new CommonAdapter<TopicInfo>(
				mContext, mTopicLists, R.layout.home_item_topic_layout) {
			@Override
			public void convert(ViewHolder helper, TopicInfo item, int position) {
				helper.setRelativeLayoutVisibility(R.id.home_item_title,
						position == 0 ? View.VISIBLE : View.GONE);
				helper.setImageByUrl(R.id.image_home, item.getBanner(),
						R.drawable.default_image_banner, 10);
			}
		};
		mTopicListView.setAdapter(mTopicAdapter);
	}

	public void setCommpanyShow() {
		CommonAdapter<CompanyBean> mCommpanyAdapter = new CommonAdapter<CompanyBean>(
				mContext, mCommpanylists, R.layout.home_item_layout) {
			@Override
			public void convert(ViewHolder helper, CompanyBean item,
					int position) {
				helper.setRelativeLayoutVisibility(R.id.home_item_title,
						position == 0 ? View.VISIBLE : View.GONE);
				helper.setText(R.id.text_commpany_name, item.getFull_name());
				helper.setText(R.id.text_commpany_desc, item.getEditor_note());
				helper.setImageByUrl(R.id.image_hotcommpany, item.getLogo(),
						R.drawable.default_image_icon, 10);
			}
		};
		mCommpanyListView.setAdapter(mCommpanyAdapter);
	}

	public void getBannerList() {
		isLoadBannerFinish = false;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("range", "2");
		map.put("position_id", "1");

		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GTEBANNER_URL, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						isLoadBannerFinish = true;
						if (isLoadBannerFinish && isLoadCommpanyFinish
								&& isLoadTopicFinish)
							dissMissProgress();
						try {
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								mIndicator.setVisibility(View.GONE);
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							JSONObject object = new JSONObject(httpRes
									.getReturnData());
							String hotStr = object.getString("list");
							if (StringUtils.isEmpty(hotStr)
									|| hotStr.length() <= 2) {
								mIndicator.setVisibility(View.GONE);
								return;
							}
							mBannerLists = (ArrayList<HomeBannerBean>) TransFormModel
									.getResponseResults(hotStr,
											HomeBannerBean.class);
							// TODO 设置banner图片数量
							// mBannerLayout.setmSideBuffer(mBannerLists.size());
							// System.out.println("--mBannerLists.size()--"+mBannerLists.size());
							// mBannerLists.addAll(mBannerLists);
							// mBannerLists.addAll(mBannerLists);
							setViewFlow();
						} catch (Exception e) {
							dissMissProgress();
							exceptionToast();
						}

					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 热门企业
	 */
	public void getHotEnterprise() {
		isLoadCommpanyFinish = false;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETHOTCOMMPANY_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						isLoadCommpanyFinish = true;
						try {
							if (isLoadBannerFinish && isLoadCommpanyFinish
									&& isLoadTopicFinish)
								dissMissProgress();
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							// TODO
							// Log.i("---", httpRes.getReturnData());
							if (httpRes.getReturnCode() != 0) {
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							String hotStr = httpRes.getReturnData();
							if (StringUtils.isEmpty(hotStr)
									|| hotStr.length() <= 2) {
								setListViewShow();
								return;
							}
							mCommpanylists = (ArrayList<CompanyBean>) TransFormModel
									.getResponseResults(hotStr,
											CompanyBean.class);
							// setCommpanyShow();
							setListViewShow();
						} catch (Exception e) {
							dissMissProgress();
							exceptionToast();
						}

					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	// 轮播图
	public void setViewFlow() {
		if (null != mBannerLists) {
			System.out.println("mBannerLists.size()===="+mBannerLists.size());
			if(mBannerLists.size()==1){
				mIndicator.setVisibility(View.VISIBLE);
				mBannerAdapter = new BannerAdapter(mContext, mBannerLists);
				mBannerLayout.setmSideBuffer(mBannerLists.size());// 实际图片张数
//				int initialPosition = (Integer.MAX_VALUE / 2 - Integer.MAX_VALUE
//						/ 2 % mBannerLists.size());
				mBannerLayout.setAdapter(mBannerAdapter); // 对viewFlow添加图片
				// 设置开始位置
//				mBannerLayout.setSelection(initialPosition);
//				mBannerLayout.setFlowIndicator(mIndicator);

//				mBannerLayout.setTimeSpan(3000);
//				mBannerLayout.stopAutoFlowTimer(); // 停止自动播放
				
			}else{
				mIndicator.setVisibility(View.VISIBLE);
				mBannerAdapter = new BannerAdapter(mContext, mBannerLists);
				mBannerLayout.setmSideBuffer(mBannerLists.size());// 实际图片张数
				int initialPosition = (Integer.MAX_VALUE / 2 - Integer.MAX_VALUE
						/ 2 % mBannerLists.size());
				mBannerLayout.setAdapter(mBannerAdapter); // 对viewFlow添加图片
				// 设置开始位置
				mBannerLayout.setSelection(initialPosition);
				mBannerLayout.setFlowIndicator(mIndicator);

				mBannerLayout.setTimeSpan(3000);
				mBannerLayout.startAutoFlowTimer(); // 启动自动播放
			}
			return;
		}
		mIndicator.setVisibility(View.GONE);
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setErrorShow() {
		mMainNoneDataLayout.removeAllViews();
		mMainNoneDataLayout.addView(mCustomErrorView);
		mMainNoneDataLayout.setVisibility(View.VISIBLE);
		mPullRefreshScrollView.setVisibility(View.GONE);
	}

	public void setMainShow() {
		if (mPullRefreshScrollView.getVisibility() == View.GONE)
			mPullRefreshScrollView.setVisibility(View.VISIBLE);
		mMainNoneDataLayout.setVisibility(View.GONE);
	}

	/**
	 * 友盟统计
	 */
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("home"); // 统计页面，"home"为页面名称，可自定义
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("home");
	}
}

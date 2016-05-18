package com.shixi.gaodun.view.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.BannerAdapter;
import com.shixi.gaodun.adapter.HomeAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.ParentFragment;
import com.shixi.gaodun.libpull.PullToRefreshBase;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HomeBannerBean;
import com.shixi.gaodun.model.domain.HomeBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.BannerLayout;
import com.shixi.gaodun.view.CircleFlowIndicator;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.activity.TalentBankGuideActivity;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.shixi.gaodun.view.activity.enterprise.EnterpriseDetailActivity;
import com.shixi.gaodun.view.activity.enterprise.PositionSearchActivity;
import com.shixi.gaodun.view.activity.invitation.InvitationDetailListActivity;
import com.shixi.gaodun.view.activity.messagecenter.MessageActivity;
import com.umeng.socialize.net.utils.Base64;
import com.zxing.activity.QRCodeUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * author：ronger on 2016/1/15 10:55
 */
public class TabHomeNewFragment extends ParentFragment implements
		PullToRefreshBase.OnRefreshListener, AdapterView.OnItemClickListener {
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private View mBannerViewLayout;
	private BannerLayout mBannerLayout;
	private CircleFlowIndicator mIndicator;
	private Dialog mDialog;
	// 列表
	private ArrayList<HomeBean> mLists;
	// 话题的总数和企业的总数
	private int mTopicNumber, mCommpanyNumber;
	private ArrayList<HomeBannerBean> mBannerLists;
	// 生成二维码的字段
	private String content;
	private Bitmap qrcodeBitmap;
	private ImageView mIvQRCode;// 分享猎头二维码按钮
	private String hunterIdStr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isExcute = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tab_home_new_layout, container,
				false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		setRequestParamsPre(TAG);
	}

	@Override
	protected void initView(View view) {
		view.findViewById(R.id.fl_title_bar_back).setOnClickListener(this);
		view.findViewById(R.id.fl_title_bar_collect).setOnClickListener(this);
		mMainNoneDataLayout = (FrameLayout) view
				.findViewById(R.id.layout_error);
		mPullListView = (PullToRefreshListView) view
				.findViewById(R.id.home_pullrefresh_listview);
		mPullListView.setOnRefreshListener(this);
		mListView = mPullListView.getRefreshableView();
		mListView.setSelector(R.color.transparent);
		mListView.setDividerHeight(0);
		mListView.setOnItemClickListener(this);
		mBannerViewLayout = LayoutInflater.from(mContext).inflate(
				R.layout.home_banner_new_layout, null);
		mIndicator = (CircleFlowIndicator) mBannerViewLayout
				.findViewById(R.id.indicator);
		mBannerLayout = (BannerLayout) mBannerViewLayout
				.findViewById(R.id.viewpager_banner);

		// hunterIdStr = Base64.encodeBase64String(CacheUtils.get_hunter_id(
		// mContext).getBytes());
		// content =
		// "http://shixipre.gaodun.cn/MobileAccount/login/btn/regin/hid/"
		// + hunterIdStr;
		// // 根据猎头id设置二维码图片
		// QRCodeUtil qrCodeUtil = new QRCodeUtil();// 生成二维码工具
		// qrcodeBitmap = qrCodeUtil.createQRCode2(mContext, null,
		// R.drawable.icon_logo_black, content);
		// 置为空
		// qrCodeUtil = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_again:
			if (ActivityUtils.isNetAvailable()) {
				setMainShow();
				setRequestParamsPre(TAG);
			} else {
				setErrorShow();
				setErrorInfoShow("你的网络不给力，数据加载失败了");
			}
			break;
		case R.id.fl_title_bar_back:
			PositionSearchActivity.startAction(mContext);
			break;
		case R.id.fl_title_bar_collect:
			if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
				mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
				return;
			}
			MessageActivity.startAction(mContext);
			break;
		case R.id.btn_select_two:// 登陆
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			LoginActivity.startAction(mContext, 1, false);
			mDialog.dismiss();
			break;
		case R.id.btn_select_three:// 注册
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			RegisterActivity.startAction(mContext, 1, top);
			mDialog.dismiss();
			break;
		}

	}

	/**
	 * 获取首页信息
	 */
	public void getHomePageList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		// map.put("range", "2");
		// map.put("position_id", "1");
		// map.put("student_id","");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.HOMEPAGE_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				mPullListView.onRefreshComplete();
				dissMissProgress();
				return;
			}
			// Log.e("ronger",httpRes.getReturnData());
			JSONObject object = new JSONObject(httpRes.getReturnData());
			// banner部分
			String bannerStr = object.getString("adver_list");
			// 企业
			String enterpriseStr = object.getString("enterprise_list");
			// 话题
			String snstopicStr = object.getString("snstopic_list");
			// 人才库
			String talentBankStr = object.getString("talent");
			// System.out.println("talentBankStr==" + talentBankStr);
			if (StringUtils.isNotEmpty(bannerStr) || getJSONStrType(bannerStr)) {
				mBannerLists = (ArrayList<HomeBannerBean>) TransFormModel
						.getResponseResults(bannerStr, HomeBannerBean.class);
			}
			if (StringUtils.isNotEmpty(enterpriseStr)
					&& getJSONStrType(enterpriseStr)) {
				mLists = (ArrayList<HomeBean>) TransFormModel
						.getResponseResults(enterpriseStr, HomeBean.class);
				mCommpanyNumber = mLists.size();
			}
			if (StringUtils.isNotEmpty(snstopicStr)
					&& getJSONStrType(snstopicStr)) {
				ArrayList<HomeBean> list = (ArrayList<HomeBean>) TransFormModel
						.getResponseResults(snstopicStr, HomeBean.class);
				if (null != mLists && !mLists.isEmpty()) {
					mLists.addAll(list);
				} else {
					mLists = list;
				}
				mTopicNumber = list.size();
			}
			if (StringUtils.isNotEmpty(talentBankStr)) {
				if (null != mLists && !mLists.isEmpty()) {
					HomeBean talentInfo = TransFormModel
							.getResponseResultObject(talentBankStr,
									HomeBean.class);
					mLists.add(0, talentInfo);
				}
			}
			mPullListView.onRefreshComplete();
			dissMissProgress();
			// 设置所有数据的显示
			setAllDataShow();
		} catch (Exception e) {
			mPullListView.onRefreshComplete();
			dissMissProgress();
			exceptionToast();
		}
	}

	public boolean getJSONStrType(String str) throws Exception {
		// 判断字符串是JSONObjectJSONArray
		Object object = new JSONTokener(str).nextValue();
		boolean isJSONArray = object instanceof JSONArray;
		return isJSONArray;
	}

	@Override
	public void setRequestParams(String className) {
		getHomePageList();
	}

	public void setAllDataShow() {
		HomeAdapter adapter = new HomeAdapter(getContext(), mLists,
				R.layout.home_item_new_layout, mCommpanyNumber);
		setViewFlow();
		mBannerLayout.setViewGroup(mListView);
		if (mListView.getHeaderViewsCount() > 0) {
			mListView.removeHeaderView(mBannerViewLayout);
		}
		mListView.addHeaderView(mBannerViewLayout);
		mListView.setAdapter(adapter);
	}

	// 轮播图
	public void setViewFlow() {
		if (null != mBannerLists) {
			if (mBannerLists.size() == 1) {
				BannerAdapter mBannerAdapter = new BannerAdapter(mContext,
						mBannerLists);
				// 设置只有一张图片
				mBannerAdapter.setCount(1);
				mBannerLayout.setmSideBuffer(mBannerLists.size());// 实际图片张数
				mBannerLayout.setAdapter(mBannerAdapter); // 对viewFlow添加图片
				mBannerLayout.setFlowIndicator(mIndicator);
				mIndicator.requestLayout();// 请求重新measure layout
			} else {
				BannerAdapter mBannerAdapter = new BannerAdapter(mContext,
						mBannerLists);
				// 设置图片数为无限
				mBannerAdapter.setCount(Integer.MAX_VALUE);
				mBannerLayout.setmSideBuffer(mBannerLists.size());// 实际图片张数
				int initialPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE
						/ 2 % mBannerLists.size();
				mBannerLayout.setAdapter(mBannerAdapter, initialPosition); // 对viewFlow添加图片
				// mBannerLayout.setSelection(0);
				// TODO
				// 设置开始位置 无限循环
				// mBannerLayout.setSelection(initialPosition);
				mBannerLayout.setFlowIndicator(mIndicator);
				mIndicator.requestLayout();// 请求重新measure layout
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
		return null;
	}

	@Override
	protected void setErrorShow() {
		mMainNoneDataLayout.removeAllViews();
		mMainNoneDataLayout.addView(mCustomErrorView);
		mMainNoneDataLayout.setVisibility(View.VISIBLE);
		mPullListView.setVisibility(View.GONE);
	}

	public void setMainShow() {
		if (mPullListView.getVisibility() == View.GONE)
			mPullListView.setVisibility(View.VISIBLE);
		mMainNoneDataLayout.setVisibility(View.GONE);
	}

	@Override
	public void onRefresh() {
		if (ActivityUtils.isNetAvailable()) {
			mBannerLayout.stopAutoFlowTimer(); // 停止自动播放
			// mIndicator.invalidate();
			setRequestParamsPre(TAG, false);
		} else {
			ToastUtils.showToastInCenter("网络未连接");
			mPullListView.onRefreshComplete();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HomeBean bean = mLists.get(position - mListView.getHeaderViewsCount());
		if (position == 1) {
			if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {

				if (CacheUtils.getIsFirstLogin(mContext)) {
					mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
				} else {
					BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
					LoginActivity.startAction(mContext, 1, false);
				}
				return;
			}
			// 判断是否是第一次进入人才库
			if (CacheUtils.getIsFirstEnterTalentBank(mContext)) {
				TalentBankGuideActivity.startAction(mContext, 2);
			} else {// 第一次进入
				CacheUtils.saveIsFirstEnterTalentBank(mContext, true);
				TalentBankGuideActivity.startAction(mContext, 1);
			}
		} else if ((position - 1) <= mCommpanyNumber) {
			String companyId = bean.pkid;
			EnterpriseDetailActivity.startAction(mContext, companyId, 1);
		} else {
			String topicId = bean.topic_id;
			InvitationDetailListActivity.startAction(mContext,
					new InvitationInfoBean(topicId));
		}
	}
}

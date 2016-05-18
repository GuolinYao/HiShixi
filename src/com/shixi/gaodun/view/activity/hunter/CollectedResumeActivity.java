package com.shixi.gaodun.view.activity.hunter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CollectedResumeAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewActivity;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CollectedResumeBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 校园猎头收集简历页面
 * 
 * @author guolin
 * @date:2016-2-24
 */
public class CollectedResumeActivity extends
		BaseListViewActivity<CollectedResumeBean> {

	// private PositionBean positionBean;
	// 职位名称下面的控件相对父布局的高度
	// private boolean ifGoon;
	// 0获取详情信息，1投递简历
	private Activity mActivityContext;
	private Context mContext;
	private boolean isLoadPositionListFinish = false;

	private RelativeLayout mNoneDataLayout;
	private View noneLayout;

	// activity跳转
	public static void startAction(Context context, String recommendId) {
		Intent intent = new Intent(context, CollectedResumeActivity.class);
		intent.putExtra("recommendid", recommendId);
		// intent.putExtra("topictitle", topicTitle);
		context.startActivity(intent);
	}

	// activity跳转
	public static void startAction(Context context) {
		Intent intent = new Intent(context, CollectedResumeActivity.class);
		// intent.putExtra("recommendid", recommendId);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		// mRecommendId = getIntent().getStringExtra("recommendid");
		// mTopicTitle = getIntent().getStringExtra("topictitle");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// getIntentData();
		super.onCreate(arg0);
		setContentView(R.layout.activity_collected_resume_layout);
		// Log.e("---","执行了======");
		initView();
		mActivityContext = CollectedResumeActivity.this;
		mContext = this;
		isExcuteNoneData = false;
		isExcute = true;
		setRequestParamsPre(TAG, true);

	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("已收集");
		mBackIcon.setImageResource(R.drawable.icon_back);
		// mRightOneLayout.setVisibility(View.VISIBLE);
		// mRightOneIcon.setImageResource(R.drawable.favouriteh);
		// mRightOneLayout.setOnClickListener(this);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.prlv_positionlist_pull);
		myListView = mPullToRefreshListView.getRefreshableView();
		// myListView.setDividerHeight(ActivityUtils.dip2px(this, 10));
		myListView.setOnScrollListener(this);
		mPullToRefreshListView.setOnRefreshListener(this);
		myListView.setOnItemClickListener(this);
		mNoneDataLayout = (RelativeLayout) findViewById(R.id.list_null);
		initNoneLayout();

	}

	@Override
	public void initAdapter() {
		mListAdapter = new CollectedResumeAdapter(mActivityContext, mLists,
				R.layout.item_collected_resume_layout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button_again:
			setRetryRequest();
			break;
		case R.id.fl_title_bar_back:
			finish();
			break;
		// case R.id.fl_title_bar_one://跳转到hunter中心界面
		// // if (null == positionBean)
		// // break;
		// if (StringUtils.isEmpty(CacheUtils.getAccountId(this))) {
		// mDialog = CustomDialog.AlertToCustomDialog(this, this);
		// break;
		// }
		// HunterMineActivity.startAction(this);
		// break;

		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		getPositionList();
	}

	/**
	 * 获取职位列表
	 */
	public void getPositionList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", "20");
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(mContext))) {
			map.put("school_hunter_id", CacheUtils.get_hunter_id(this));
		}

		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.HUNTER_LIST_RESUME,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		getResponseComplete();
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			// if (httpRes.getReturnCode() != 0) {
			// isLoadPositionListFinish = true;
			// ToastUtils.showToastInCenter(httpRes.getReturnDesc());
			// // mYHandler.sendEmptyMessage(0);
			// return;
			// }
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String resultData = object.getString("list");
//			System.out.println("resultData-----"+resultData);
			mListTotal = NumberUtils.getInt(object.getString("listTotal"), 0);
			if (mListTotal == 0) {
				setNoneLayoutShow();
				return;
			}
			setPullShow();
			List<CollectedResumeBean> lists = TransFormModel
					.getResponseResults(resultData, CollectedResumeBean.class);
			getListSuccess(lists);
			isLoadPositionListFinish = true;
		} catch (Exception e) {
			ToastUtils.showToastInCenter("数据解析错误");
			setDebugLog(e);
		}
	}

	/**
	 * 无数据界面
	 */
	public void setNoneLayoutShow() {
		mNoneDataLayout.removeAllViews();
		mNoneDataLayout.addView(noneLayout);
		if (mNoneDataLayout.getVisibility() == View.GONE)
			mNoneDataLayout.setVisibility(View.VISIBLE);
		if (mPullToRefreshListView.getVisibility() == View.VISIBLE)
			mPullToRefreshListView.setVisibility(View.GONE);
	}

	public void setPullShow() {
		if (mNoneDataLayout.getVisibility() == View.VISIBLE)
			mNoneDataLayout.setVisibility(View.GONE);
		if (mPullToRefreshListView.getVisibility() == View.GONE)
			mPullToRefreshListView.setVisibility(View.VISIBLE);
	}

	// 初始化错误界面
	public void initNoneLayout() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		noneLayout = layoutInflater.inflate(R.layout.resume_none_data_layout,
				null);
		mCustomErrorView = layoutInflater.inflate(R.layout.activity_base_nonet,
				null);
		mCustomErrorView.findViewById(R.id.button_again).setOnClickListener(
				this);
		TextView textErrorWarn = (TextView) mCustomErrorView
				.findViewById(R.id.text_server_warn);
		TextView textNoneDataWarnTitle = (TextView) noneLayout
				.findViewById(R.id.text_title);
		TextView textNoneDataWarnDesc = (TextView) noneLayout
				.findViewById(R.id.text_desc);
		textNoneDataWarnTitle.setText("暂无简历");
		textNoneDataWarnDesc.setText("还没有已收集到简历，继续加油吧！");
		// if (mFlErrorBg == null) {
		// System.out.println("为空----");
		// }
		// // 添加错误页面
		// mFlErrorBg.addView(customNoneDataView);
	}

	@Override
	public void getListSuccess(List<CollectedResumeBean> lists) {
		if (null == lists) {
			setListDataShow();
			return;
		}
		super.getListSuccess(lists);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// int index = position - myListView.getHeaderViewsCount();
		// if (index < 0) {
		// return;
		// }
		// CollectedResumeBean bean = mLists.get(index);
		// PositionDetailActivity.startAction(mActivityContext,
		// bean.getPkid(),2);

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
	protected void setNoneDataDesc() {
		mTextNoneDataWarnTitle.setText("暂时没有推荐信息");
		mTextNoneDataWarnDesc.setText("官方马上就会有推荐职位了，敬请期待……");
	}

	@Override
	public void setPullListShow() {
		if (null != mMainNoneDataLayout
				&& mMainNoneDataLayout.getVisibility() == View.VISIBLE)
			mMainNoneDataLayout.setVisibility(View.GONE);
	}

	@Override
	protected void setErrorShow(boolean isExcute) {
		if (isExcute) {
			mMainNoneDataLayout = (FrameLayout) findViewById(R.id.layout_mainerror_viewgroup);
			mMainNoneDataLayout.removeAllViews();
			mMainNoneDataLayout.addView(mCustomErrorView);
			mMainNoneDataLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// 第一次进来时显示错误页面
		if (isExcute && mPage == 1 && isFirstJoin) {
			setOnErrorResponse(error);
			return;
		}
		nomalOnErrorResponse(error);
	}

	/**
	 * 重试
	 */
	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	/**
	 * 返回按钮 ,直接调用后退键
	 * 
	 * @param view
	 */
	// @SuppressLint("NewApi")
	// public void back(View view) {
	// this.onBackPressed();
	// }

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("CollectedResumeActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("CollectedResumeActivity");
		MobclickAgent.onPause(this);
	}
}

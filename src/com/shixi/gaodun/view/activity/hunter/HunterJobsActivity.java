package com.shixi.gaodun.view.activity.hunter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.SharePositionAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewActivity;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.PositionMissionBean;
import com.shixi.gaodun.model.domain.TaskInfoBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.activity.banner.RecommendActivity;
import com.shixi.gaodun.view.activity.enterprise.PositionDetailActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校园猎头转发职位页面
 * 
 * @author guolin
 * @date:2016-2-24
 */
public class HunterJobsActivity extends
		BaseListViewActivity<PositionMissionBean> {

	private Dialog mDialog;
	private Activity mActivityContext;
	private Context mContext;
	private RelativeLayout mNoneDataLayout;
	private View noneLayout;
	private ProgressBar mPbFinishResume;
	private TextView mTvFinishResume_2;
	private TextView mTvMissionNum;
	private TextView mTvMissionDeadline;
	private String mIsUrgency = "2";// 是否有紧急任务，1 有。2 没有
	private RelativeLayout mRlUrgencyMission;

	// activity跳转
	public static void startAction(Context context, String recommendId) {
		Intent intent = new Intent(context, RecommendActivity.class);
		context.startActivity(intent);
	}

	// activity跳转
	public static void startAction(Context context) {
		Intent intent = new Intent(context, HunterJobsActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_hunter_jobs_layout);
		mActivityContext = HunterJobsActivity.this;
		mContext = this;
		isExcuteNoneData = false;
		isExcute = true;
		setRequestParamsPre(TAG, true);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("Hi 猎头");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mRightOneLayout.setVisibility(View.VISIBLE);
		mRightOneIcon.setImageResource(R.drawable.hunt_center);
		mRightOneLayout.setOnClickListener(this);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.prlv_positionlist_pull);
		myListView = mPullToRefreshListView.getRefreshableView();
		myListView.setDividerHeight(ActivityUtils.dip2px(this, 10));
		myListView.setOnScrollListener(this);
		mPullToRefreshListView.setOnRefreshListener(this);
		myListView.setOnItemClickListener(this);
		mNoneDataLayout = (RelativeLayout) findViewById(R.id.list_null);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout headerLayout = (LinearLayout) inflater.inflate(
				R.layout.header_hunt_mission, null);
		myListView.addHeaderView(headerLayout);
		mPbFinishResume = (ProgressBar) headerLayout
				.findViewById(R.id.pb_finish_resume);// kpi完成进度条
		mRlUrgencyMission = (RelativeLayout) headerLayout
				.findViewById(R.id.Rl_urgency_mission);//紧急任务
		mRlUrgencyMission.setOnClickListener(this);
		mTvFinishResume_2 = (TextView) findViewById(R.id.tv_finish_resume_2);// 已完成简历
		mTvMissionNum = (TextView) findViewById(R.id.tv_mission_num);// 任务期数
		mTvMissionDeadline = (TextView) findViewById(R.id.tv_mission_deadline);// 距离任务结束时间
		initNoneLayout();
	}

	@Override
	public void initAdapter() {
		mListAdapter = new SharePositionAdapter(mActivityContext, mLists,
				R.layout.share_position_item_layout);
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
		case R.id.Rl_urgency_mission://紧急任务
			EmergencyTaskActivity.startAction(this);
			break;
		case R.id.fl_title_bar_one:// 跳转到hunter中心界面
			// if (null == positionBean)
			// break;
			if (StringUtils.isEmpty(CacheUtils.getAccountId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				break;
			}
			HunterMineActivity.startAction(this);
			break;

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
		map.put("pageNumber", String.valueOf(mPageNumber));
		if (StringUtils.isNotEmpty(CacheUtils.get_hunter_id(mContext))) {
			map.put("school_hunter_id", CacheUtils.get_hunter_id(mContext));
		}

		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.HUNTER_MISSION, map,
				new RequestResponseLinstner(this), this);
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
			String taskInfo = object.getString("task");
			mListTotal = NumberUtils.getInt(object.getString("count"), 0);
			if (mListTotal == 0) {
				setNoneLayoutShow();
				return;
			}
			setPullShow();
			List<PositionMissionBean> lists = TransFormModel
					.getResponseResults(resultData, PositionMissionBean.class);
			TaskInfoBean taskInfoBean = TransFormModel.getResponseResultObject(
					taskInfo, TaskInfoBean.class);
			initTaskInfo(taskInfoBean);
			getListSuccess(lists);
			isLoadPositionListFinish = true;
		} catch (Exception e) {
			ToastUtils.showToastInCenter("数据解析错误");
			setDebugLog(e);
		}
	}

	/**
	 * 初始化任务信息
	 * 
	 * @param taskInfoBean
	 */
	@SuppressLint("UseValueOf")
	private void initTaskInfo(TaskInfoBean taskInfoBean) {
		String collectedResume = taskInfoBean.getGet_total();
		String aim_total = taskInfoBean.getAim_total();
		String name = taskInfoBean.getName();
		String days = taskInfoBean.getDays();
		mIsUrgency = taskInfoBean.getIs_urgency();// 是否有紧急任务
		if(mIsUrgency.equals("1")){
			mRlUrgencyMission.setVisibility(View.VISIBLE);//显示紧急任务
		}else{
			mRlUrgencyMission.setVisibility(View.GONE);//隐藏紧急任务
		}
		mPbFinishResume.setProgress(new Integer(collectedResume));
		mPbFinishResume.setMax(new Integer(aim_total));
		mTvFinishResume_2
				.setText(" " + collectedResume + "/" + aim_total + " ");
		mTvMissionNum.setText(name);
		mTvMissionDeadline.setText(days);

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
		noneLayout = layoutInflater.inflate(R.layout.pisition_none_data_layout,
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
		textNoneDataWarnTitle.setText("您还没有任务");
		// // 添加错误页面
		// mFlErrorBg.addView(customNoneDataView);
	}

	@Override
	public void getListSuccess(List<PositionMissionBean> lists) {
		if (null == lists) {
			setListDataShow();
			return;
		}
		super.getListSuccess(lists);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		int index = position - myListView.getHeaderViewsCount();
		if (index < 0) {
			return;
		}
		PositionMissionBean bean = mLists.get(index);
		PositionDetailActivity.startAction(mActivityContext, bean.getPost_id(),
				2);

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
		MobclickAgent.onPageStart("HunterJobsActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("HunterJobsActivity");
		MobclickAgent.onPause(this);
	}
}

package com.shixi.gaodun.view.activity.hunter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.animation.Transformation;

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
import com.shixi.gaodun.view.activity.banner.RecommendActivity;
import com.shixi.gaodun.view.activity.enterprise.PositionDetailActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 校园猎头紧急任务页面
 * 
 * @author guolinyao
 * @date 2016年4月15日 上午11:00:19
 */
public class EmergencyTaskActivity extends
		BaseListViewActivity<PositionMissionBean> {

	private Activity mActivityContext;
	private Context mContext;
	private RelativeLayout mNoneDataLayout;
	private View noneLayout;
	private TextView mTvUrgencyDesc;
	private ImageView mIvArrow;
	private boolean isExpanded = false;// 判断展开状态
	private RelativeLayout mIvExpand;
	private Animation rotateAnimation;
	private Animation rotateAnimation2;
	private int maxLine = 4;// 默认允许行数4

	// activity跳转
	public static void startAction(Context context, String recommendId) {
		Intent intent = new Intent(context, RecommendActivity.class);
		context.startActivity(intent);
	}

	// activity跳转
	public static void startAction(Context context) {
		Intent intent = new Intent(context, EmergencyTaskActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// getIntentData();
		super.onCreate(arg0);
		setContentView(R.layout.activity_hunter_jobs_layout);
		mActivityContext = EmergencyTaskActivity.this;
		mContext = this;
		isExcuteNoneData = false;
		isExcute = true;
		setRequestParamsPre(TAG, true);

	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("紧急任务");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.prlv_positionlist_pull);
		myListView = mPullToRefreshListView.getRefreshableView();
		myListView.setDividerHeight(ActivityUtils.dip2px(this, 10));
		myListView.setOnScrollListener(this);
		mPullToRefreshListView.setOnRefreshListener(this);
		myListView.setOnItemClickListener(this);
		mNoneDataLayout = (RelativeLayout) findViewById(R.id.list_null);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout headerLayout = (LinearLayout) inflater.inflate(
				R.layout.header_hunt_urgency_mission, null);
		myListView.addHeaderView(headerLayout);
		mTvUrgencyDesc = (TextView) headerLayout
				.findViewById(R.id.tv_urgency_desc);
		mIvArrow = (ImageView) headerLayout.findViewById(R.id.iv_arrow);
		mIvExpand = (RelativeLayout) headerLayout.findViewById(R.id.rl_expand);

		mIvArrow.setOnClickListener(this);
		mIvExpand.setOnClickListener(this);
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
		case R.id.rl_expand:
			rotate();
			break;
		case R.id.iv_arrow:
			rotate();
			break;

		}
	}

	@SuppressLint("NewApi")
	private void rotate() {
		mTvUrgencyDesc.clearAnimation(); // 清除动画
		final int tempHight;
		final int startHight = mTvUrgencyDesc.getHeight(); // 起始高度
		int durationMillis = 200;
		isExpanded = !isExpanded;
		if (!isExpanded) {// 收拢
//			mTvUrgencyDesc.setMaxLines(maxLine);
//			mTvUrgencyDesc.setEllipsize(android.text.TextUtils.TruncateAt.END);
			tempHight = mTvUrgencyDesc.getLineHeight() * maxLine - startHight; // 为正值，长文减去短文的高度差
			
			// if (rotateAnimation2 == null) {
			// rotateAnimation2 = AnimationUtils.loadAnimation(this,
			// R.anim.rotate_arrow_down);
			// AccelerateInterpolator ai = new AccelerateInterpolator();
			// rotateAnimation.setFillAfter(true);
			// rotateAnimation2.setInterpolator(ai);
			// }
			// mIvArrow.startAnimation(rotateAnimation2);
			RotateAnimation ra = new RotateAnimation(180, 0,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			ra.setDuration(durationMillis);
			ra.setFillAfter(true);
			mIvArrow.startAnimation(ra);

		} else {// 展开
//			mTvUrgencyDesc.setMaxLines(30);
			tempHight = mTvUrgencyDesc.getLineHeight()
					* mTvUrgencyDesc.getLineCount() - startHight;// 为负值，即短文减去长文的高度差
			// if (rotateAnimation == null) {
			// rotateAnimation = AnimationUtils.loadAnimation(this,
			// R.anim.rotate_arrow_up);
			// AccelerateInterpolator ai = new AccelerateInterpolator();
			// rotateAnimation.setFillAfter(true);
			// rotateAnimation.setInterpolator(ai);
			// }
			// mIvArrow.startAnimation(rotateAnimation);
			RotateAnimation ra = new RotateAnimation(0, 180,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			ra.setDuration(durationMillis);
			ra.setFillAfter(true);
			mIvArrow.startAnimation(ra);
			// mTvUrgencyDesc.setEllipsize(null);

		}
		Animation animation = new Animation() {
			// interpolatedTime 为当前动画帧对应的相对时间，值总在0-1之间
			protected void applyTransformation(float interpolatedTime,
					Transformation t) { // 根据ImageView旋转动画的百分比来显示textview高度，达到动画效果
				mTvUrgencyDesc.setHeight((int) (startHight + tempHight
						* interpolatedTime));// 原始长度+高度差*（从0到1的渐变）即表现为动画效果

			}
		};
		animation.setDuration(durationMillis);
		mTvUrgencyDesc.startAnimation(animation);
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
				StringUtils.getCommonIP() + GlobalContants.URGENCY_MISSION,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		getResponseComplete();
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
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
		mTvUrgencyDesc.setText(taskInfoBean.getDescription());
		mTvUrgencyDesc.setHeight(mTvUrgencyDesc.getLineHeight() * (maxLine));// 设置默认高度
		mTvUrgencyDesc.post(new Runnable() {

			@Override
			public void run() {
				mIvExpand.setVisibility(mTvUrgencyDesc.getLineCount() > maxLine ? View.VISIBLE
						: View.GONE);// 判断文字是否大于最小高度
				System.out.println("几行   " + mTvUrgencyDesc.getLineCount());
			}
		});
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
		textNoneDataWarnTitle.setText("紧急任务已下线！");
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
		mTextNoneDataWarnTitle.setText("紧急任务已下线！");
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

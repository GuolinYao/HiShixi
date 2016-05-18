package com.shixi.gaodun.view.activity.hunter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseScrollViewActivity;
import com.shixi.gaodun.libpull.PullToRefreshScrollView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.BitMapUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.zxing.activity.QRCodeUtil;

/**
 * 校园猎头中心页面
 * 
 * @author guolin
 * @date:2016-2-24
 */
public class HunterMineActivity extends BaseScrollViewActivity<String> {

	private TextView mTvCollectedTotal;
	// private PullToRefreshScrollView mPullRefreshScrollView;
	private ScrollView mScrollView;
	private String mRuleURL;// 任务规则url
	private String shareURL;// 分享链接

	// activity跳转
	public static void startAction(Context context, String recommendId) {
		Intent intent = new Intent(context, HunterMineActivity.class);
		context.startActivity(intent);
	}

	// activity跳转
	public static void startAction(Context context) {
		Intent intent = new Intent(context, HunterMineActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_hunter_mime_layout);
//		String release = android.os.Build.VERSION.RELEASE;
//	int sdkInt = android.os.Build.VERSION.SDK_INT;
		isExcuteNoneData = false;
		isExcute = true;
		setRequestParamsPre(TAG, false);
	}

	@SuppressLint("InlinedApi")
	@Override
	public void initView() {
		super.initView();
		mBackIcon.setImageResource(R.drawable.hunt_return);
		mTitleLayout.setBackgroundColor(Color.parseColor("#00ACF0"));
		mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.scrollview_hunter_mine);
		mScrollView = mPullToRefreshScrollView.getRefreshableView();

		// 设置首页下拉刷新监听
		mPullToRefreshScrollView.setOnRefreshListener(this);
		// mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// // new GetDataTask().execute();
		// setRequestParamsPre(TAG, false);
		// }
		//
		// });

		LinearLayout head_mine_hunter = (LinearLayout) LayoutInflater
				.from(this).inflate(R.layout.head_hunter_mime, null);
		RelativeLayout.LayoutParams LP_FW = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		mScrollView.addView(head_mine_hunter, LP_FW);
		head_mine_hunter.findViewById(R.id.rl_resume_collected_detail)
				.setOnClickListener(this);
		head_mine_hunter.findViewById(R.id.rl_mission_and_reward)
				.setOnClickListener(this);
		head_mine_hunter.findViewById(R.id.rl_hunter_tool).setOnClickListener(
				this);
		mTvCollectedTotal = (TextView) head_mine_hunter
				.findViewById(R.id.tv_collected_total);

		// setRefreshOrLoadMoreListener();

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

		// 已收集简历
		case R.id.rl_resume_collected_detail:
			CollectedResumeActivity.startAction(this);
			break;
		// 任务规划及奖励
		case R.id.rl_mission_and_reward:
			RuleRewardActivity.startAction(this, mRuleURL);
			break;
		// 猎头工具
		case R.id.rl_hunter_tool:
			HuntToolActivity.startAction(this, shareURL);
			break;

		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		if (CacheUtils.get_hunter_id(this) != null) {
			map.put("school_hunter_id", CacheUtils.get_hunter_id(this));
		}
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.HUNTER_TOTAL_KPI,
				map, new RequestResponseLinstner(this), this);

		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			getResponseComplete();
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			// setPullListShow();
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String total = object.getString("total");
			mRuleURL = object.getString("rule_url");
			shareURL = object.getString("share_url");
			// System.out.println("total          "+total);
			mTvCollectedTotal.setText(total);

			// 根据猎头id设置二维码图片
			QRCodeUtil qrCodeUtil = new QRCodeUtil();// 生成二维码工具
			Bitmap qrcodeBitmap = qrCodeUtil.createQRCode2(this, null,
					R.drawable.icon_logo_black, shareURL);
			qrCodeUtil = null;
			// mListTotal = NumberUtils.getInt(object.getString("listTotal"),
			// 0);
			// if (mListTotal <= 0) {
			// getListSuccess(null);
			// 刷新完成
			// mPullToRefreshScrollView.onRefreshComplete();
			// return;
			// }

		} catch (Exception e) {
			setDebugLog(e);
		}
	}

	// public void setTextShow(TextView textView, String str, String defaultStr)
	// {
	// textView.setText(StringUtils.isEmpty(str) ? defaultStr : str);
	// }

	@Override
	public void refreshList() {
		// setRequestParamsPre(TAG, false);
	}

	@Override
	protected void setNoneDataDesc() {
		mTextNoneDataWarnTitle.setText("暂时没有推荐信息");
		mTextNoneDataWarnDesc.setText("官方马上就会有推荐职位了，敬请期待……");
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

	@Override
	public void onRefresh() {
		super.onRefresh();
		setRequestParamsPre(TAG, false);// 不带进度条
	}

	/**
	 * 重试
	 */
	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	@Override
	public void initAdapter() {

	}

	@Override
	public void loadMoreList() {

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
		MobclickAgent.onPageStart("HunterMineActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("HunterMineActivity");
		MobclickAgent.onPause(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	protected void getIntentData() {
	}

}

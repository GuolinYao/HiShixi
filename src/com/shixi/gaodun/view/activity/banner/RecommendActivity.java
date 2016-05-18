package com.shixi.gaodun.view.activity.banner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.RecommendListAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewActivity;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.RecommendInfo;
import com.shixi.gaodun.model.domain.RecommendInfoBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.activity.BannerImageDetail;
import com.shixi.gaodun.view.activity.invitation.InvitationDetailListActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 每周推荐（企业+职位+扩展信息）
 * 
 * @author guolinyao
 * @date 2016年1月8日 下午3:40:37
 */
public class RecommendActivity extends BaseListViewActivity<RecommendInfoBean> {

	// 顶部banner图片
	private ImageView mIvBannerTop;
	// 小编寄语标题
	private TextView mTvEditorTitle;
	// 小编寄语内容
	private TextView mTvEditorContent;
	// 推荐的标题
	private TextView mTvRecommendTitle;
	// 每周推荐的id
	private String mRecommendId;

	private RecommendInfo mRecommendInfo;// 每周推荐信息
	private ImageButton mIbBack;
	private float mLastY;// 滑动的y轴坐标
	private TextView mTvMore;
	// 定义手势检测器实例
	private GestureDetector detector;
	private FrameLayout mFlMore;
	private FrameLayout mFlErrorBg;

	// private boolean isNoneData = false;

	// activity跳转
	public static void startAction(Context context, String recommendId) {
		Intent intent = new Intent(context, RecommendActivity.class);
		intent.putExtra("recommendid", recommendId);
		// intent.putExtra("topictitle", topicTitle);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		mRecommendId = getIntent().getStringExtra("recommendid");
		// mTopicTitle = getIntent().getStringExtra("topictitle");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		getIntentData();
		super.onCreate(arg0);
		setContentView(R.layout.activity_recommend_layout);
		// 创建手势检测器
		// detector = new GestureDetector(this, this);
		mTitleLayout.setVisibility(View.GONE);
		isExcuteNoneData = false;
		isExcute = true;
		setRequestParamsPre(TAG);
	}

	@Override
	public void initView() {
		super.initView();
		mIbBack = (ImageButton) findViewById(R.id.ib_back);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.recommend_pullrefresh_listview);
		mFlErrorBg = (FrameLayout) findViewById(R.id.fl_error_bg);
		myListView = (ListView) mPullToRefreshListView.getRefreshableView();
		myListView.setOnItemClickListener(this);
		myListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mLastY = event.getRawY();
					// Log.i("---", "down------:" + mLastY);
					break;
				case MotionEvent.ACTION_MOVE:
					float y = event.getRawY();
					if (y > mLastY && (y - mLastY) > 10) {
						// 向下
						// Log.i("---", "向下---" + "mlasty--" + mLastY + "--y--"
						// + y);
						showBackButton();
					} else if (mLastY > y && (mLastY - y) > 10) {
						// 向上
						// Log.i("---", "向上---" + "mlasty--" + mLastY + "--y--"
						// + y);
						hideBackButton();
					}
					mLastY = y;
					break;

				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_CANCEL:
					float y2 = event.getRawY();
					if (y2 > mLastY && (y2 - mLastY) > 10) {
						// 向下
						showBackButton();
					} else if (mLastY > y2 && (mLastY - y2) > 10) {
						// 向上
						hideBackButton();
					}

					break;
				}
				return false;
			}
		});

		setRefreshOrLoadMoreListener();
		initHeaderView();
	}

	// 将该activity上的触碰事件交给GestureDetector处理

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	//
	// return detector.onTouchEvent(event);
	// }

	/**
	 * 显示返回按钮
	 */
	private void showBackButton() {
		if (!mIbBack.isEnabled()) {
			AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
			aa.setDuration(300);
			aa.setFillAfter(true);
			mIbBack.startAnimation(aa);
			mIbBack.setEnabled(true);
		}
	}

	/**
	 * 隐藏返回按钮
	 */
	private void hideBackButton() {
		if (mIbBack.isEnabled()) {
			AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
			aa.setDuration(300);
			aa.setFillAfter(true);
			mIbBack.startAnimation(aa);
			mIbBack.setEnabled(false);
		}
	}

	/**
	 * 初始化listview头布局
	 */
	@SuppressLint("InflateParams")
	public void initHeaderView() {
		View mRecommendHeadView = LayoutInflater.from(this).inflate(
				R.layout.head_recommend_listview, null);
		// mRecommendHeadView.findViewById(R.id.tv_more).setOnClickListener(this);
		mIvBannerTop = (ImageView) mRecommendHeadView
				.findViewById(R.id.iv_banner_top);
		mTvEditorTitle = (TextView) mRecommendHeadView
				.findViewById(R.id.tv_editor_title);
		mTvEditorContent = (TextView) mRecommendHeadView
				.findViewById(R.id.tv_editor_content);
		mTvRecommendTitle = (TextView) mRecommendHeadView
				.findViewById(R.id.tv_recommend_title);
		mFlMore = (FrameLayout) mRecommendHeadView.findViewById(R.id.fl_more);

		mTvMore = (TextView) mRecommendHeadView.findViewById(R.id.tv_more);
		mTvMore.setOnClickListener(this);// 查看更多
		// 添加头布局
		myListView.addHeaderView(mRecommendHeadView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.button_again:
			setRetryRequest();
			break;
		case R.id.tv_more:// 查看更多，根据topic_id 或者 link_url 进入帖子详情 或者 h5页面
			if (!(mRecommendInfo.getTopic_id().equals(""))) {
				// 进入帖子详情
				InvitationInfoBean invitationInfo = new InvitationInfoBean();
				invitationInfo.setTopic_id(mRecommendInfo.getTopic_id());
				InvitationDetailListActivity.startAction(this, invitationInfo);
			} else if (!(mRecommendInfo.getLink_url().equals(""))) {
				// 进入h5
				BannerImageDetail.startAction(RecommendActivity.this,
						mRecommendInfo.getLink_url());
			}
			break;
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("id", mRecommendId);
		map.put("page", String.valueOf(mPage));
		int mPageNumber = 8;
		map.put("pageNumber", String.valueOf(mPageNumber));
		// if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this)))
		// map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.WEEK_RECOMMEND_URL,
				map, new RequestResponseLinstner(this), this);

		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			// isFirstJoin = false;
			getResponseComplete();
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setPullListShow();
			JSONObject object = new JSONObject(httpRes.getReturnData());
			mRecommendInfo = TransFormModel.getResponseResultObject(
					object.getString("recommend"), RecommendInfo.class);
			// 如果没有查看更多的link_url 那么就隐藏查看更多
			if (mRecommendInfo.getTopic_id().equals("")
					&& mRecommendInfo.getLink_url().equals("")) {
				mFlMore.setVisibility(View.GONE);
			} else {
				mFlMore.setVisibility(View.VISIBLE);
			}
			setRecommendInfo();
			mListTotal = NumberUtils.getInt(object.getString("listTotal"), 0);
			if (mListTotal <= 0) {
				getListSuccess(null);
				return;
			}
			if (mListTotal == 0) {
				mFlErrorBg.setVisibility(View.VISIBLE);
			} else {
				mFlErrorBg.setVisibility(View.GONE);
			}
			List<RecommendInfoBean> lists = TransFormModel
					.getRecommendInfoList(object.getString("list"), false);
			getListSuccess(lists);
		} catch (Exception e) {
			setDebugLog(e);
		}
	}

	// //初始化错误界面
	// public void initNoneLayout() {
	// LayoutInflater layoutInflater = LayoutInflater.from(this);
	// View customNoneDataView =
	// layoutInflater.inflate(R.layout.pisition_none_data_layout, null);
	// customNoneDataView = layoutInflater.inflate(R.layout.activity_base_nonet,
	// null);
	// customNoneDataView.findViewById(R.id.button_again).setOnClickListener(this);
	// TextView textErrorWarn = (TextView)
	// mCustomErrorView.findViewById(R.id.text_server_warn);
	// TextView textNoneDataWarnTitle = (TextView)
	// customNoneDataView.findViewById(R.id.text_title);
	// TextView textNoneDataWarnDesc = (TextView)
	// mCustomNoneDataView.findViewById(R.id.text_desc);
	// textNoneDataWarnTitle.setText("你来晚了，该活动已下线");
	// if(mFlErrorBg==null){
	// System.out.println("为空----");
	// }
	// //添加错误页面
	// mFlErrorBg.addView(customNoneDataView);
	// }

	public void setRecommendInfo() {
		// System.out.println("banner"+mRecommendInfo.getTop_banner());
		if (!mRecommendInfo.getTop_banner().equals("")) {
			// banner top图片
			BaseApplication.mApp.setBigNomalImageByUrl(mIvBannerTop,
					mRecommendInfo.getTop_banner(),
					R.drawable.default_image_banner);
		} else {
			mIvBannerTop.setVisibility(View.GONE);
		}

		if (!mRecommendInfo.getTips_title().equals("")) {
			// 设置小编寄语标题
			setTextViewShow(mTvEditorTitle, mRecommendInfo.getTips_title());
		} else {
			mTvEditorTitle.setVisibility(View.GONE);
		}

		if (!mRecommendInfo.getTips_content().equals("")) {
			// 设置小编寄语内容
			setTextViewShow(mTvEditorContent, mRecommendInfo.getTips_content());
		} else {
			mTvEditorContent.setVisibility(View.GONE);
		}

		// 设置推荐标题
		setTextViewShow(mTvRecommendTitle, mRecommendInfo.getTitle());
	}

	@Override
	public void getListSuccess(List<RecommendInfoBean> lists) {
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
		// if (index < 0)
		// return;

		// // 进入职位详情
		// EnterpriseDetailActivity.startAction(this,
		// mLists.get(index).enterprise_id);
		// // 进入职位详情
		// PositionDetailActivity
		// .startAction(this, mLists.get(index).post.post_id);

	}

	@Override
	public void initAdapter() {
		mListAdapter = new RecommendListAdapter(this, mLists,
				R.layout.item_recommend_listview);
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
		mTextNoneDataWarnDesc.setText("小编马上就会有推荐信息了，敬请期待……");
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
	}

	/**
	 * 返回按钮 ,直接调用后退键
	 * 
	 * @param view
	 */
	@SuppressLint("NewApi")
	public void back(View view) {
		this.onBackPressed();
	}

	//
	// /**
	// * 检测手势滑动
	// *
	// * @param motionEvent
	// * @return
	// */
	// @Override
	// public boolean onDown(MotionEvent motionEvent) {
	// return false;
	// }
	//
	// @Override
	// public void onShowPress(MotionEvent motionEvent) {
	//
	// }
	//
	// @Override
	// public boolean onSingleTapUp(MotionEvent motionEvent) {
	// return false;
	// }
	//
	// @Override
	// public boolean onScroll(MotionEvent motionEvent, MotionEvent
	// motionEvent1, float v, float v1) {
	// return false;
	// }
	//
	// @Override
	// public void onLongPress(MotionEvent motionEvent) {
	//
	// }
	//
	// @Override
	// public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	// float velocityY) {
	// float minMove = 10; //最小滑动距离
	// float minVelocity = 0; //最小滑动速度
	// // float beginX = e1.getX();
	// // float endX = e2.getX();
	// float beginY = e1.getY();
	// float endY = e2.getY();
	//
	// Log.i("---", beginY - endY + "");
	// if (beginY - endY > minMove && Math.abs(velocityY) > minVelocity) { //上滑
	// hideBackButton();//隐藏按钮
	// } else if (endY - beginY > minMove && Math.abs(velocityY) > minVelocity)
	// { //下滑
	// showBackButton();//显示按钮
	// }
	// return false;
	// }

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("recommendweekly");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("recommendweekly");
		MobclickAgent.onPause(this);
	}
}

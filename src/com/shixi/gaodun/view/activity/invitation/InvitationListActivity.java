package com.shixi.gaodun.view.activity.invitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.MineIvitationListAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewContainsTitleActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.TopicInfo;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.shixi.gaodun.view.activity.TopicIntroduceActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 帖子列表
 * 
 * @author ronger guolin
 * @date:2015-12-11 下午4:18:28
 */
public class InvitationListActivity extends
		BaseListViewContainsTitleActivity<InvitationInfoBean> {
	private String mTopicId;
	// 话题标题
	private String mTopicTitle;
	// 昵称是否存在 1 不存在 2 存在
	private int mNickname_status = 1;
	// 禁言状态 1 已被禁言 2 未被禁言
	private int mForbid_status;
	// 话题信息
	private TopicInfo mTopicInfo;
	// 主要部分（话题+帖子列表）
	private Dialog mDialog;

	// 头部
	private TextView mTextTopicTitle;
	private TextView mTextTopicDes;
	private ImageView mImageTopicIcon;

	public static void startAction(Activity context, String topicId,
			String topicTitle) {
		Intent intent = new Intent(context, InvitationListActivity.class);
		intent.putExtra("topicid", topicId);
		intent.putExtra("topictitle", topicTitle);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		mTopicId = getIntent().getStringExtra("topicid");
		mTopicTitle = getIntent().getStringExtra("topictitle");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		getIntentData();
		super.onCreate(arg0);
		isExcuteNoneData = false;
		isExcute = true;
		setRequestParamsPre(TAG);
		setFilterRegister();
	}

	@Override
	public void toRequestServer(Intent intent) {
		if (intent.getAction().equals(Contants.ALL_UPDATE_ACTION)) {
			isLoadMore = false;
			mPage = 1;
			setRequestParamsPre(TAG, false);
		}
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText(mTopicTitle);
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setText("发帖");
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setOnClickListener(this);
		myListView.setDividerHeight(0);
		myListView.setOnItemClickListener(this);
		mainView.setBackgroundColor(getResources().getColor(
				R.color.main_content_bg_color));
		setRefreshOrLoadMoreListener();
		initTopicView();
	}

	@SuppressLint("InflateParams")
	public void initTopicView() {
		View mTopicView = LayoutInflater.from(this).inflate(
				R.layout.invitation_topic_layout, null);
		mTopicView.findViewById(R.id.layout_top).setOnClickListener(this);
		mTextTopicDes = (TextView) mTopicView.findViewById(R.id.topic_desc);
		mTextTopicTitle = (TextView) mTopicView.findViewById(R.id.topic_title);
		mImageTopicIcon = (ImageView) mTopicView.findViewById(R.id.topic_image);
		myListView.addHeaderView(mTopicView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.tv_title_bar_otherbtn:
			if (StringUtils.isEmpty(CacheUtils.getAccountId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				break;
			}
			if (mNickname_status == 1) {// 昵称不存在
				mDialog = CustomDialog.CancelAlertToDialog("没图没真相的你还不能执行此操作",
						"完善资料", "取消", this, this);
				break;
			}
			if (mForbid_status == 1) {// 被禁言
				ToastUtils.showToastInCenter("您已被管理员禁言");
				break;
			}
			PostInvitationActivity.startAction(this, mTopicInfo,
					mForbid_status, 0);
			break;
		case R.id.tv_confrim_btn:// 完善资料
			mDialog.dismiss();
			EditProfileActivity.startAction(this, Finals.REQUESTCODE_ONE, null,
					null, 1);
			break;
		case R.id.tv_cancel_btn:
			mDialog.dismiss();
			break;
		case R.id.btn_select_two:// 去登陆
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			LoginActivity.startAction(this, 1, false);
			mDialog.dismiss();
			break;
		case R.id.btn_select_three:// 去注册
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			RegisterActivity.startAction(this, 1, top);
			mDialog.dismiss();
			break;
		case R.id.layout_top:
			if (null == mTopicInfo)
				break;
			TopicIntroduceActivity.startAction(this, mTopicInfo);
			break;
		case R.id.button_again:
			setRetryRequest();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		InvitationDetailListActivity.startAction(this, mLists.get(index));
	}

	@Override
	public void initAdapter() {
		mListAdapter = new MineIvitationListAdapter(this, mLists,
				R.layout.invitation_item_layout);
		// new InvitationListAdapter(this, mLists,
		// R.layout.invitation_item_layout, mTopicInfo);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("forum_id", mTopicId);
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this)))
			map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.INVITATION_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			isFirstJoin = false;
			getResponseComplete();
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setPullListShow();
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String status = object.getString("status");
			if (StringUtils.isNotEmpty(status) && status.length() > 2) {
				JSONObject statusObject = new JSONObject(status);
				mNickname_status = NumberUtils.getInt(
						statusObject.getString("nickname_status"), 1);
				mForbid_status = NumberUtils.getInt(
						statusObject.getString("forbid_status"), 2);
			}
			mTopicInfo = TransFormModel.getResponseResultObject(
					object.getString("forum"), TopicInfo.class);
			setTopicInfo();
			mListTotal = NumberUtils.getInt(object.getString("topicTotal"), 0);
			if (mListTotal <= 0) {
				getListSuccess(null);
				return;
			}
			List<InvitationInfoBean> lists = TransFormModel
					.getInvitationInfoList(object.getString("topic"), false);
			getListSuccess(lists);
		} catch (Exception e) {
			setDebugLog(e);
		}
	}

	public void setTopicInfo() {
		setTextViewShow(mTextTopicTitle, mTopicInfo.getTips_title());
		setTextViewShow(mTextTopicDes, mTopicInfo.getTips_content());
		BaseApplication.mApp.setRoundedImageByUrl(mImageTopicIcon,
				mTopicInfo.getIcon_image(), R.drawable.default_image_icon, 10);
		// setImageByUrl(mImageTopicIcon, mTopicInfo.getIcon_image(),
		// R.drawable.default_image_icon, 10);
	}

	@Override
	public void getListSuccess(List<InvitationInfoBean> lists) {
		if (null == lists) {
			setListDataShow();
			return;
		}
		super.getListSuccess(lists);
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
		mTextNoneDataWarnTitle.setText("暂时没有帖子列表");
		mTextNoneDataWarnDesc.setText("赶快去发布跟帖吧");
	}

	/**
	 * 重试
	 */
	@Override
	protected void setRetryRequest() {

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

	// TODO
	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("topicList");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("topicList");
		MobclickAgent.onPause(this);
	}
}

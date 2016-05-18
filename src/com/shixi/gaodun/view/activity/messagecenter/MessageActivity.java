package com.shixi.gaodun.view.activity.messagecenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.MessageListAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseScrollViewContainsTitleActivity;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.TopicMessageBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.InvitationActionSheetDialog;
import com.shixi.gaodun.view.MyListView;
import com.shixi.gaodun.view.activity.invitation.CommentDetailActivity;
import com.shixi.gaodun.view.activity.invitation.InvitationDetailListActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 消息中心的消息列表
 * 
 * @author ronger
 * @date:2015-12-4 下午1:55:23
 */
public class MessageActivity extends BaseScrollViewContainsTitleActivity<TopicMessageBean> implements
		OnItemLongClickListener, OnclickSelectedIdCallBack {
	private View mContentView;
	private ImageView mImagePointIcon;
	private Dialog mDialog;
	private int mDeleteType = 2;
	private String mMessageId;
	private int mCurrentPosition;
	private int mRequestType = 0;
	private TextView mClearMessageBtn;
	private Drawable mNomalDrawable, mDrawable;

	public static void startAction(FragmentActivity context) {
		Intent intent = new Intent(context, MessageActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = false;
		setRequestParamsPre(TAG);
		// 设置进入Activity的Activity特效动画，同理可拓展为布局动画
		// setEnterSwichLayout();
	}

	@SuppressLint("InflateParams")
	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("消息");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mContentView = LayoutInflater.from(this).inflate(R.layout.message_list_top_layout, null);
		mImagePointIcon = (ImageView) mContentView.findViewById(R.id.image_redpoint_icon);
		mContentView.findViewById(R.id.text_system_message).setOnClickListener(this);
		mClearMessageBtn = (TextView) mContentView.findViewById(R.id.text_message_cleartopic);
		mClearMessageBtn.setOnClickListener(this);
		mClearMessageBtn.setEnabled(false);
		myListView = (MyListView) mContentView.findViewById(R.id.listview_message_list);
		mScrollView.addView(mContentView);
		setRefreshOrLoadMoreListener();
		myListView.setOnItemClickListener(this);
		myListView.setOnItemLongClickListener(this);
		mNomalDrawable = getResources().getDrawable(R.drawable.message_delete);
		mDrawable = getResources().getDrawable(R.drawable.deletedan);
		mNomalDrawable.setBounds(0, 0, mNomalDrawable.getMinimumWidth(), mNomalDrawable.getMinimumHeight());
		mNomalDrawable.setBounds(0, 0, mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.text_system_message:
			SystemMessageActivity.startAction(this, Finals.REQUESTCODE_ONE);
			break;
		case R.id.text_message_cleartopic:
			mDialog = CustomDialog.warnDialogHasTitle("清空话题消息", "确定要删除所有话题消息吗，删除后数据将不可恢复", "清空", "取消", this, this);
			break;
		case R.id.tv_title_confrim_btn:// 取消
			mDialog.dismiss();
			break;
		case R.id.tv_title_cancel_btn:// 清空
			mDeleteType = 2;
			mDialog.dismiss();
			mRequestType = 1;
			setRequestParamsPre(TAG);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == Finals.REQUESTCODE_ONE) {
			mRequestType = 0;
			mPage = 1;
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mRequestType == 0) {
			getMessageList();
			return;
		}
		if (mRequestType == 1) {
			clearTopicMessage();
			return;
		}
	}

	/**
	 * 获取消息列表
	 */
	public void getMessageList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("account_id", CacheUtils.getAccountId(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		// map.put("account_id", "331");
		// map.put("student_id", "212");
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.TOPICMESSAGE_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 清空话题消息 type1 单条删除 2 清空
	 */
	public void clearTopicMessage() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		if (mDeleteType == 1) {
			map.put("id", mMessageId);
		}
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("type", String.valueOf(mDeleteType));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETE_MESSAGE_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					mRequestType = 0;
					HttpRes httpRes = TransFormModel.getResponseData(response);
					myProgressDialog.dismiss();
					if (httpRes.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(httpRes.getReturnDesc());
						return;
					}
					if (mDeleteType == 1) {// 删除的单个的，移除对应的item
						mLists.remove(mCurrentPosition);
						mListAdapter.updateData(mLists);
						// mListAdapter.notifyDataSetChanged();
						return;
					}
					if (mDeleteType == 2) {// 删除所有，移除对应的item
						mLists.clear();
						mListAdapter.notifyDataSetChanged();
						return;
					}
				} catch (Exception e) {
					myProgressDialog.dismiss();
					setDebugLog(e);
				}

			}
		}), this);
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
			setNolmalShow();
			JSONObject object = new JSONObject(httpRes.getReturnData());
			// 小红点显示状态1 显示 2 隐藏
			int redPoint = NumberUtils.getInt(object.getString("redpoint"), 2);
			if (redPoint == 1) {
				mImagePointIcon.setVisibility(View.VISIBLE);
			} else {
				mImagePointIcon.setVisibility(View.GONE);
			}
			mListTotal = NumberUtils.getInt(object.getString("listTotal"), 0);
			if (mListTotal <= 0) {
				mClearMessageBtn.setEnabled(false);
				mClearMessageBtn.setCompoundDrawables(mDrawable, null, null, null);
			}
			String list = object.getString("list");
			if (StringUtils.isEmpty(list) || list.length() <= 2) {
				mClearMessageBtn.setEnabled(false);
				mClearMessageBtn.setCompoundDrawables(mDrawable, null, null, null);
				return;
			}
			mClearMessageBtn.setEnabled(true);
			mClearMessageBtn.setCompoundDrawables(mNomalDrawable, null, null, null);
			List<TopicMessageBean> lists = TransFormModel.getResponseResults(list, TopicMessageBean.class);
			getListSuccess(lists);
		} catch (Exception e) {
			getResponseComplete();
			exceptionToast();
			setDebugLog(e);
		}
	}

	@Override
	// <<<<<<< HEAD
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// =======
	public void setListDataShow() {
		if (null == mListAdapter || mPage * mPageNumber <= mPageNumber) {// 第一次加载或刷新
			mListAdapter = null;
			initAdapter();
			myListView.setAdapter(mListAdapter);
			mScrollView.smoothScrollTo(0, 0);
			refreshFinish();
		} else {
			mListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// >>>>>>> 19bd45e969524783b189a25b8ad391065ae0c9bd
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		TopicMessageBean bean = mLists.get(index);
		int topicStatus = NumberUtils.getInt(bean.getTopic_status(), 0);
		int replyStatus = NumberUtils.getInt(bean.getReply_status(), 0);
		if (topicStatus == 2 || replyStatus == 2)
			return;
		// 楼主贴跳转到帖子详情，跟帖跳转到评论详情
		// 1 点赞 2 跟帖 3 评论 4 回复
		int type = NumberUtils.getInt(bean.getType(), 0);
		switch (type) {
		case 1:
			InvitationDetailListActivity.startAction(this, new InvitationInfoBean(bean.getTopic_id()));
			break;
		case 2:
			InvitationDetailListActivity.startAction(this, new InvitationInfoBean(bean.getTopic_id()));
			break;
		case 3:
			CommentDetailActivity.startAtion(this, bean.getTopic_id(), bean.getReply_id(), bean.getFloor());
			break;
		case 4:
			CommentDetailActivity.startAtion(this, bean.getTopic_id(), bean.getReply_id(), bean.getFloor());
			break;

		default:
			break;
		}
	}

	@Override
	public void initAdapter() {
		mListAdapter = new MessageListAdapter(this, mLists, R.layout.message_item_layout);
	}

	@Override
	public void refreshList() {
		mPage = 1;
		mRequestType = 0;
		setRequestParamsPre(TAG, false);
	}

	@Override
	public void loadMoreList() {
		mRequestType = 0;
		setRequestParamsPre(TAG, false);
	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	/**
	 * 长按删除
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 * @return
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return false;
		mDeleteType = 1;
		mCurrentPosition = index;
		mMessageId = mLists.get(index).getMessage_id();
		InvitationActionSheetDialog.showAlert(this, this, "删除", getResources().getColor(R.color.nomal_btn_color));
		return false;
	}

	@Override
	public void onClickCallBack(int viewId) {
		if (viewId == R.id.btn_select) {// 删除消息
			mRequestType = 1;
			setRequestParamsPre(TAG);
		}
	}

	@Override
	protected void setNoneDataDesc() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		setNolmalShow();
		setRequestParamsPre(TAG);

	}

	// TODO
	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("topicMessage"); // 话题消息
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("topicMessage");
		MobclickAgent.onPause(this);
	}

}

package com.shixi.gaodun.view.activity.messagecenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.SystemMessageAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewContainsTitleActivity;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.TopicMessageBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.InvitationActionSheetDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * 系统消息列表
 * 
 * @author ronger
 * @date:2015-12-4 下午5:02:23
 */
public class SystemMessageActivity extends BaseListViewContainsTitleActivity<TopicMessageBean> implements
		OnItemLongClickListener, OnclickSelectedIdCallBack {
	private int mDeleteType = 2;
	private String mMessageId;
	private int mCurrentPosition;
	private int mRequestType = 0;
	private Dialog mDialog;

	public static void startAction(FragmentActivity context, int requestCode) {
		Intent intent = new Intent(context, SystemMessageActivity.class);
		context.startActivityForResult(intent, requestCode);
	}

	public static void startAction(FragmentActivity context) {
		Intent intent = new Intent(context, SystemMessageActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = true;
		setRequestParamsPre(TAG);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("系统消息");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setText("清空");
		mOtherName.setOnClickListener(this);
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setEnabled(false);
		myListView.setDividerHeight(0);
		myListView.setOnItemClickListener(this);
		myListView.setOnItemLongClickListener(this);
		setRefreshOrLoadMoreListener();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.tv_title_bar_otherbtn:
			mDialog = CustomDialog.warnDialogHasTitle("清空话题消息", "确定要删除所有话题消息吗，删除后数据将不可恢复", "清空", "取消", this, this);
			break;
		case R.id.tv_title_confrim_btn:// 取消
			mDialog.dismiss();
			break;
		case R.id.tv_title_cancel_btn:// 清空
			mRequestType = 1;
			mDialog.dismiss();
			mDeleteType = 2;
			setRequestParamsPre(TAG);
			break;
		case R.id.button_again:// 重试
			setRetryRequest();
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mRequestType == 0) {
			getSystemMessageList();
			return;
		}
		if (mRequestType == 1) {
			clearTopicMessage();
			return;
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			isFirstJoin = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			getResponseComplete();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				setNoneDataShow();
				return;
			}
			JSONObject object = new JSONObject(httpRes.getReturnData());
			mListTotal = object.getInt("messTotal");
			String resultStr = object.getString("messlist");
			if (StringUtils.isEmpty(resultStr) || resultStr.length() <= 2) {
				mOtherName.setEnabled(false);
				setNoneDataShow();
				return;
			}
			setPullListShow();
			mOtherName.setEnabled(true);
			List<TopicMessageBean> lists = TransFormModel.getResponseResults(resultStr, TopicMessageBean.class);
			getListSuccess(lists);
		} catch (Exception e) {
			mOtherName.setEnabled(false);
			getResponseComplete();
			exceptionToast();
			setDebugLog(e);
		}
	}

	public void getSystemMessageList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("account_id", CacheUtils.getAccountId(this));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.SYSTEM_URL, map, new RequestResponseLinstner(this), this);
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
		map.put("account_id", CacheUtils.getAccountId(this));
		map.put("type", String.valueOf(mDeleteType));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELTE_SYSTEM_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
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
						mListAdapter.notifyDataSetChanged();
						return;
					}
					if (mDeleteType == 2) {// 删除所有，移除对应的item
						setNoneDataShow();
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		String url = mLists.get(index).getUrl();
		MessageDetailActivity.startAction(this, url);
	}

	@Override
	public void initAdapter() {
		mListAdapter = new SystemMessageAdapter(this, mLists, R.layout.system_message_item_layout);
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
	public void onClickCallBack(int viewId) {
		if (viewId == R.id.btn_select) {// 删除消息
			mRequestType = 1;
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return false;
		mDeleteType = 1;
		mCurrentPosition = index;
		mMessageId = mLists.get(index).getQueue_id();
		InvitationActionSheetDialog.showAlert(this, this, "删除", getResources().getColor(R.color.nomal_btn_color));
		return false;
	}

	@Override
	protected void setNoneDataDesc() {
		mTextNoneDataWarnTitle.setText("暂无系统消息");
	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (isExcute && isFirstJoin) {
			setOnErrorResponse(error);
			return;
		}
		nomalOnErrorResponse(error);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("systemMessage"); // 系统消息：systemMessage
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("systemMessage");
		MobclickAgent.onPause(this);
	}
}

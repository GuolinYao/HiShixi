package com.shixi.gaodun.view.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewContainsTitleActivity;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.ResumeDynamicBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.activity.enterprise.PositionDetailActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 浏览记录
 * 
 * @author ronger
 * @date:2015-11-6 下午4:15:19
 */
public class MineBrowsingHistoryActivity extends BaseListViewContainsTitleActivity<ResumeDynamicBean> {
	private int mRequestType = 0;
	private Dialog mDialog;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, MineBrowsingHistoryActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		String pkId = mLists.get(index).getPkid();
		PositionDetailActivity.startAction(this, pkId,1);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = true;
		setRequestParamsPre(TAG);
		setFilterRegister();
	}

	@Override
	public void setBroadcastReceiver() {
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				mRequestType = 0;
				mPage = 1;
				toRequestServer(intent);
			}
		};
	}

	@SuppressLint("InflateParams")
	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("浏览记录");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setText("清空");
		mOtherName.setOnClickListener(this);
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setEnabled(false);
		myListView.setDividerHeight(0);
		myListView.setOnItemClickListener(this);
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
			mDialog = CustomDialog.warnDialogHasTitle("清空浏览记录", "确定要删除所有浏览记录吗？删除后数据将不可恢复", "清空", "取消", this, this);
			break;
		case R.id.tv_title_confrim_btn:// 取消
			mDialog.dismiss();
			break;
		case R.id.tv_title_cancel_btn:// 清空
			mDialog.dismiss();
			mRequestType = 1;
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
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mRequestType == 0) {
			getBrowseList();
			return;
		}
		if (mRequestType == 1) {
			clearBrowseHistory();
			return;
		}
	}

	public void getBrowseList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.BROWSELIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void clearBrowseHistory() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETE_BROWSELIST_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
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
					mOtherName.setEnabled(false);
					mLists.clear();
					mListAdapter.notifyDataSetChanged();
					setNoneDataShow();
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
			isFirstJoin = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			getResponseComplete();
			if (httpRes.getReturnCode() != 0) {
				// setNoneDataShow();
				// mMainNoneDataLayout.setVisibility(View.VISIBLE);
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			String resultStr = httpRes.getReturnData();
			JSONObject object = new JSONObject(resultStr);
			mListTotal = NumberUtils.getInt(object.getString("listTotal"), -1);
			if (mListTotal <= 0) {
				setNoneDataShow();
				return;
			}
			setPullListShow();
			String list = object.getString("list");
			mOtherName.setEnabled(true);
			List<ResumeDynamicBean> lists = TransFormModel.getResponseResults(list, ResumeDynamicBean.class);
			getListSuccess(lists);
		} catch (Exception e) {
			getResponseComplete();
			setDebugLog(e);
			exceptionToast();
		}
	}

	@Override
	public void initAdapter() {
		mListAdapter = new CommonAdapter<ResumeDynamicBean>(this, mLists, R.layout.browse_hietory_item_layout) {
			@Override
			public void convert(ViewHolder helper, ResumeDynamicBean item, int position) {
				helper.setImageByUrl(R.id.image_position_icon, item.getLogo(), R.drawable.default_image_icon, 10);
				helper.setText(R.id.text_positionname, item.getTitle());
				helper.setText(R.id.text_commpany_name, item.getFull_name());
				helper.setText(R.id.text_address, item.getCreate_time());
				helper.setText(R.id.text_position_city, "  ["+item.getPost_city()+"]");
				// 1 已收藏 2 未收藏
				String collect_status = item.getCollect_status();
				if (StringUtils.isNotEmpty(collect_status) && collect_status.equals("1")) {
					helper.setImageViewVisibility(R.id.image_like_icon, View.VISIBLE);
				} else {
					helper.setImageViewVisibility(R.id.image_like_icon, View.GONE);
				}
				helper.setText(R.id.text_status, "未投递");
			}
		};

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

	}

	@Override
	protected void setNoneDataDesc() {
		mTextNoneDataWarnTitle.setText("暂无记录");
		mTextNoneDataWarnDesc.setText("快去浏览心仪的职位吧");
	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (isFirstJoin && mRequestType == 0) {
			mOtherName.setEnabled(false);
			setOnErrorResponse(error);
		}
		nomalOnErrorResponse(error);
	}
	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("scanHistory"); //浏览记录：scanHistory
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("scanHistory"); 
		MobclickAgent.onPause(this);
	}
}

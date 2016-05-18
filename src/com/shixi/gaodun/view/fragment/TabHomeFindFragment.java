package com.shixi.gaodun.view.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewContainsTitleFragment;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.TopicInfo;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.shixi.gaodun.view.activity.invitation.EditActivity;
import com.shixi.gaodun.view.activity.invitation.EditProfileActivity;
import com.shixi.gaodun.view.activity.invitation.InvitationListActivity;
import com.shixi.gaodun.view.activity.messagecenter.MessageActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * @author ronger
 * @date:2015-12-7 下午3:55:55
 */
public class TabHomeFindFragment extends
		BaseListViewContainsTitleFragment<TopicInfo> {
	private ImageView mHeadImg;
	private Dialog mDialog;
	// 昵称是否存在 1 不存在 2 存在
	private int mNickname_status = 1;
	private String mUrl;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void initView(View view) {
		mPullToRefreshListView.setBackgroundColor(Color.parseColor("#ffffff"));
		setRequestParamsPre(TAG);
		setHeaderTitle("发现");
		mTitleHeaderBar.setTitleBarBackgroud(R.drawable.title_bar_bg);
		mTitleHeaderBar.setTitleBarTextColor(R.color.main_font_color);
		mTitleHeaderBar.setRightImageView(R.drawable.youxianghui);
		mTitleHeaderBar.setLeftOnClickListener(this);
		mTitleHeaderBar.setRightOnClickListener(this);
		int width = ActivityUtils.dip2px(mContext, 30);
		View leftView = mTitleHeaderBar.setCustomizedLeftView(
				R.layout.base_imageview_layout, width, width);
		mHeadImg = (ImageView) leftView.findViewById(R.id.imageview_base);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFilterRegister();
		isExcute = true;
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// if (null == mLists || null == mListAdapter) {
	// setListPullShow();
	// mPage = 1;
	// setRequestParamsPre(TAG);
	// }
	// }

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(mContext)))
			map.put("student_id", CacheUtils.getStudentId(mContext));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETALLTOPIC_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			getResponseComplete();
			isFristRequest = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setListPullShow();
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String nickName = object.getString("nickname");
			if (StringUtils.isNotEmpty(nickName)) {
				mNickname_status = 2;
			}
			// 头像
			if (object.has("sns_avatar")) {
				mUrl = object.getString("sns_avatar");
				mTitleHeaderBar.setLeftImageViewUrl(mHeadImg,
						object.getString("sns_avatar"), 50,
						R.drawable.default_touxiang_xiaoerhei);
			}
			mListTotal = NumberUtils.getInt(object.getString("total"), 0);
			if (mListTotal == 0) {
				setNoneDataShow();
				return;
			}
			String hotStr = object.getString("list");
			List<TopicInfo> lists = TransFormModel.getResponseResults(hotStr,
					TopicInfo.class);
			getListSuccess(lists);
		} catch (Exception e) {
			setDebugLog(e);
			exceptionToast();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TopicInfo topicInfo = mLists.get(position
				- myListView.getHeaderViewsCount());
		InvitationListActivity.startAction(mContext, topicInfo.getPkid(),
				topicInfo.getTitle());
	}

	@Override
	public void toRequestServer(Intent intent) {
		mPage = 1;
		setRequestParamsPre(TAG, false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_again:
			if (ActivityUtils.isNetAvailable()) {
				setRequestParamsPre(TAG);
			} else {
				setErrorShow();
			}
			break;
		case R.id.ly_title_bar_left:
			toEditSource();
			break;
		case R.id.tv_confrim_btn:// 完善资料,编辑昵称和话题icon
			mDialog.dismiss();
			EditProfileActivity.startAction(mContext, Finals.REQUESTCODE_ONE,
					null, mUrl, 0);
			break;
		case R.id.tv_cancel_btn:
			mDialog.dismiss();
			break;
		case R.id.btn_select_two:// 去登陆
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			LoginActivity.startAction(mContext, 1, false);
			mDialog.dismiss();
			break;
		case R.id.btn_select_three:// 去注册
			BaseApplication.mApp.mCenterTaskActivitys = new ArrayList<Activity>();
			RegisterActivity.startAction(mContext, 1, top);
			mDialog.dismiss();
			break;
		case R.id.ly_title_bar_right:
			if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
				mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
				return;
			}
			MessageActivity.startAction(mContext);
			break;
		default:
			break;
		}

	}

	/**
	 * 去个人编辑页
	 */
	public void toEditSource() {
		if (StringUtils.isEmpty(CacheUtils.getAccountId(mContext))) {
			mDialog = CustomDialog.AlertToCustomDialog(mContext, this);
			return;
		}
		if (mNickname_status == 1) {// 昵称不存在
			EditProfileActivity.startAction(mContext, Finals.REQUESTCODE_ONE,
					null, null, 0);
			// mDialog = CustomDialog.CancelAlertToDialog("没图没真相的你还不能执行此操作",
			// "完善资料", "取消", mContext, this);
			return;
		}
		// // MineEditActivity.startAction(mContext);
		EditActivity.startAction(mContext);
	}

	@Override
	public void initAdapter() {
		mListAdapter = new CommonAdapter<TopicInfo>(mContext, mLists,
				R.layout.topic_item) {
			@Override
			public void convert(ViewHolder helper, TopicInfo item, int position) {
				helper.setImageByUrl(R.id.topic_image, item.getIcon_image(),
						R.drawable.default_image_icon, 10);
				helper.setText(R.id.topic_content, item.getTitle());
			}
		};

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
	public void onErrorResponse(VolleyError error) {
		if (isFristRequest && isExcute && mPage == 1) {
			setOnErrorResponse(error);
			return;
		}
		nomalOnErrorResponse(error);
	}

	/**
	 * 友盟统计
	 */
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("find"); // 统计页面，"find"为页面名称，可自定义
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("find");
	}
}

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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.InvitationDetailAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewActivity;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.FollowInvitationBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.ImageInfoBean;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.BitmapCache;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.model.utils.BitmapCache.ImageCallback;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.InvitationActionSheetDialog;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 帖子详情
 * 
 * @author ronger
 * @date:2015-12-15 下午1:27:39
 */
public class InvitationDetailListActivity extends BaseListViewActivity<FollowInvitationBean> implements
		OnclickSelectedIdCallBack {
	// 帖子详情view
	private TextView mTextInvitationTitle;
	private TextView mTextInvitationContent;
	private TextView mTextCreateTime;
	private TextView mTextCreateName;
	private TextView mTextLikeBtn;
	private ImageView mImageCreatePhoto, mImageSexIcon;
	private LinearLayout mLayoutImages;
	// 帖子详情
	private InvitationInfoBean mInvitationInfoBean;
	// 是否是作者
	private boolean isAuthor;
	// 是否已经举报
	private Dialog mDialog;
	// 请求的类型默认为0，请求帖子详情，1为点赞2为取消赞3为收藏4为取消收藏5删除此贴6举报帖子
	private int mrequestType = 0;
	private int likeNumber;
	private int likeStatus;
	private int collectStatus;
	private int mDialogType;// 0点击过删除或者举报后的对话框类型，1提示没有完善资料的类型
	// 昵称是否存在 1 不存在 2 存在
	private int mNickname_status = 1;
	// 禁言状态 1 已被禁言 2 未被禁言
	private int mForbid_status;


	public static void startAction(Activity context, InvitationInfoBean invitationInfo) {
		Intent intent = new Intent(context, InvitationDetailListActivity.class);
		intent.putExtra("invitationInfo", invitationInfo);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		mInvitationInfoBean = (InvitationInfoBean) getIntent().getSerializableExtra("invitationInfo");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = true;
		isExcuteNoneData = false;
		setContentView(R.layout.invitation_detail_layout);
		setRequestParamsPre(TAG);
		setFilterRegister();
	}

	@Override
	public void toRequestServer(Intent intent) {
		mrequestType = 0;
		isLoadMore = false;
		mPage = 1;
		setRequestParamsPre(TAG, false);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setVisibility(View.GONE);
		mBackIcon.setImageResource(R.drawable.icon_back);
		mRightOneLayout.setVisibility(View.VISIBLE);
		mRightTwoLayout.setVisibility(View.VISIBLE);
		mRightOneIcon.setImageResource(R.drawable.gengduo);
		mRightTwoIcon.setImageResource(R.drawable.favourite);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.scrollview_layout);
		myListView = mPullToRefreshListView.getRefreshableView();
		myListView.setOnItemClickListener(this);
		myListView.setCacheColorHint(Color.parseColor("#00000000"));
		setRefreshOrLoadMoreListener();
		initInvitationView();
	}

	/**
	 * 帖子详情
	 */
	@SuppressLint("InflateParams")
	public void initInvitationView() {
		View mInvitationView = LayoutInflater.from(this).inflate(R.layout.invitation_detail_top_layout, null);
		initInvitationDetailView(mInvitationView);
	}

	public void initInvitationDetailView(View view) {
		mTextInvitationTitle = (TextView) view.findViewById(R.id.text_invitation_title);
		mTextInvitationContent = (TextView) view.findViewById(R.id.text_topic_content);
		mTextCreateTime = (TextView) view.findViewById(R.id.text_time);
		mTextCreateName = (TextView) view.findViewById(R.id.text_name);
		mImageCreatePhoto = (ImageView) view.findViewById(R.id.image_user);
		mImageSexIcon = (ImageView) view.findViewById(R.id.image_sex_icon);
		mLayoutImages = (LinearLayout) view.findViewById(R.id.layout_invitation_images);
		mTextLikeBtn = (TextView) view.findViewById(R.id.text_like_btn);
		mTextLikeBtn.setOnClickListener(this);
		myListView.addHeaderView(view);
	}

	/**
	 * 删除帖子楼主贴
	 */
	public void deleteInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("topic_id", mInvitationInfoBean.getTopic_id());
		map.put("type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.DELETEINVITATION_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					myProgressDialog.dismiss();
					HttpRes httpRes = TransFormModel.getResponseData(response);
					if (httpRes.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(httpRes.getReturnDesc());
						return;
					}
					sendBroadcast();
					finish();
				} catch (Exception e) {
					setDebugLog(e);
				}
			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 举报帖子
	 */
	public void reportInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("topic_id", mInvitationInfoBean.getTopic_id());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.REPORTINVITATION_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					myProgressDialog.dismiss();
					HttpRes httpRes = TransFormModel.getResponseData(response);
					if (httpRes.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(httpRes.getReturnDesc());
						return;
					}
				} catch (Exception e) {
					setDebugLog(e);
				}
			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 赞帖子
	 */
	public void praiseInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("topic_id", mInvitationInfoBean.getTopic_id());
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.PRAISE_URL, map, new RequestResponseLinstner(
						new ResponseCallBack() {
							@Override
							public void getResponse(JSONObject response) {
								try {
									myProgressDialog.dismiss();
									HttpRes httpRes = TransFormModel.getResponseData(response);
									if (httpRes.getReturnCode() != 0) {
										ToastUtils.showToastInCenter(httpRes.getReturnDesc());
										return;
									}
									likeNumber++;
									likeStatus = 1;
									setPraiseStatus();
								} catch (Exception e) {
									setDebugLog(e);
								}
							}
						}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 取消赞
	 */
	public void CancelPraiseInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("topic_id", mInvitationInfoBean.getTopic_id());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CANCELPRAISE_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					myProgressDialog.dismiss();
					HttpRes httpRes = TransFormModel.getResponseData(response);
					if (httpRes.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(httpRes.getReturnDesc());
						return;
					}
					likeNumber--;
					likeStatus = 2;
					setPraiseStatus();
				} catch (Exception e) {
					setDebugLog(e);
				}

			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 收藏帖子
	 */
	public void collectInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("topic_id", mInvitationInfoBean.getTopic_id());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.COLLECTINVITATION_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					myProgressDialog.dismiss();
					HttpRes httpRes = TransFormModel.getResponseData(response);
					if (httpRes.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(httpRes.getReturnDesc());
						return;
					}
					collectStatus = 1;
					setCollectStatus();
				} catch (Exception e) {
					setDebugLog(e);
				}
			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 取消收藏帖子
	 */
	public void cancelCollectInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("topic_id", mInvitationInfoBean.getTopic_id());
		map.put("type", "1");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CANCELCOLLECTINVITATION_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					myProgressDialog.dismiss();
					HttpRes httpRes = TransFormModel.getResponseData(response);
					if (httpRes.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(httpRes.getReturnDesc());
						return;
					}
					collectStatus = 2;
					setCollectStatus();
				} catch (Exception e) {
					setDebugLog(e);
				}

			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 设置帖子详情信息
	 * 
	 * @param bean
	 */
	public void setInvitationDetailData(InvitationInfoBean bean) {
		try {
			if (CacheUtils.getStudentId(this).equals(bean.getStudent_id())) {
				isAuthor = true;
			}
		} catch (Exception e) {
			isAuthor = false;
		}
		setTextViewShow(mTextInvitationTitle, bean.getTitle());
		setTextViewShow(mTextInvitationContent, bean.getContent());
		setTextViewShow(mTextCreateTime, bean.getRefresh_time());
		setTextViewShow(mTextCreateName, bean.getNickname(), "某同学");
		BaseApplication.mApp.setCircleImageByUrl(mImageCreatePhoto, bean.getSns_avatar(),
				R.drawable.default_touxiang_xiaoerhei);
		// setImageByUrl(mImageCreatePhoto, bean.getSns_avatar(), R.drawable.default_touxiang_xiaoerhei, 50);
		int sex = NumberUtils.getInt(bean.getGender(), 1);
		if (sex == 1) {// 男
			setImageResource(mImageSexIcon, R.drawable.topic_male);
		}
		if (sex == 2) {// 女
			setImageResource(mImageSexIcon, R.drawable.topic_female);
		}
		likeNumber = NumberUtils.getInt(bean.getFavor_num(), 0);
		likeStatus = NumberUtils.getInt(bean.getTopic_favor(), 2);
		collectStatus = NumberUtils.getInt(bean.getTopic_collect(), 2);
		setCollectStatus();
		setPraiseStatus();
		if (null == bean.getImages() || bean.getImages().isEmpty()) {
			return;
		}
		setTopicImagesShow(bean.getImages());
	}

	/**
	 * 设置收藏状态
	 */
	public void setCollectStatus() {
		mRightTwoIcon.setImageResource(collectStatus == 1 ? R.drawable.favouriteh : R.drawable.favourite);
	}

	public void setPraiseStatus() {
		Drawable likeIcon = getResources().getDrawable(likeStatus == 2 ? R.drawable.likered : R.drawable.likedred);
		likeIcon.setBounds(0, 0, likeIcon.getMinimumWidth(), likeIcon.getMinimumHeight());
		mTextLikeBtn.setCompoundDrawables(likeIcon, null, null, null);
		int textSize = ActivityUtils.sp2px(this, 11);
		String likeStr = "喜欢\n" + likeNumber;
		SpannableString spannableString = new SpannableString(likeStr);
		spannableString.setSpan(new AbsoluteSizeSpan(textSize), 0, likeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTextLikeBtn.setText(spannableString);
	}

	/**
	 * 设置图片显示
	 * 
	 * @param images
	 */
	public void setTopicImagesShow(List<ImageInfoBean> images) {
		mLayoutImages.removeAllViews();
		for (int i = 0; i < images.size(); i++) {
			ImageInfoBean imageInfoBean = images.get(i);
			View view = LayoutInflater.from(this).inflate(R.layout.invitation_image_item, null);
			View dividerView = view.findViewById(R.id.view_divider);
			if (i == images.size() - 1) {
				dividerView.setVisibility(View.GONE);
			}
			ImageView imageView = (ImageView) view.findViewById(R.id.image_item);
			float scale = imageInfoBean.getHeight() / (float) imageInfoBean.getWidth();
			int imageViewWidth = ActivityUtils.getScreenWidth() - ActivityUtils.dip2px(this, 15);
			int imageViewHeight = (int) (imageViewWidth * scale);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageViewWidth, imageViewHeight);
			imageView.setLayoutParams(lp);
			imageView.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
			imageView.setScaleType(ScaleType.FIT_XY);//TODO
			imageView.setAdjustViewBounds(true);
//			DisplayImageOptions options = DisplayImageOptionsUtils.getBigBitMap(R.drawable.default_image_banner);
//			ImageLoader.getInstance().displayImage(imageInfoBean.getValue(), imageView, options);
			BaseApplication.mApp.setBigNomalImageByUrl(imageView, imageInfoBean.getValue(),
					R.drawable.default_image_banner);
//			cache.displayBmp(imageView, null, imageInfoBean.getValue(),
//					callback);
			// setBigImageByUrl(imageView, imageInfoBean.getValue(), R.drawable.default_image_banner);
			mLayoutImages.addView(view);
		}
	}

	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	/**
	 * 获取帖子详情
	 */
	public void getInvitationDetail() {
		isLoadMore = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("topic_id", mInvitationInfoBean.getTopic_id());
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this)))
			map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.INVITATIONDETAIL_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mrequestType == 0) {
			getInvitationDetail();
			return;
		}
		if (mrequestType == 1) {
			praiseInvitation();
			return;
		}
		if (mrequestType == 2) {
			CancelPraiseInvitation();
			return;
		}
		if (mrequestType == 3) {
			collectInvitation();
			return;
		}
		if (mrequestType == 4) {
			cancelCollectInvitation();
			return;
		}
		if (mrequestType == 5) {
			deleteInvitation();
		}
		if (mrequestType == 6) {
			reportInvitation();
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			isFirstJoin = false;
			dissMissProgress();
			isLoadMore = false;
			mPullToRefreshListView.onRefreshComplete();
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
				mNickname_status = NumberUtils.getInt(statusObject.getString("nickname_status"), 1);
				mForbid_status = NumberUtils.getInt(statusObject.getString("forbid_status"), 2);
			}
			mInvitationInfoBean = TransFormModel.getInvitationInfoBean(object.getString("topic"), true);
			setInvitationDetailData(mInvitationInfoBean);
			mInvitationInfoBean.setListTotal(mListTotal);
			mListTotal = object.getInt("listTotal");
			if (mListTotal <= 0) {
				myListView.setAdapter(null);// 为了展示帖子详情部分
			}
			String topicList = object.getString("list");
			List<FollowInvitationBean> mLists = ((List<FollowInvitationBean>) TransFormModel.getResponseResults(
					topicList, FollowInvitationBean.class));
			if (null == mLists || mLists.isEmpty())
				myListView.setAdapter(null);
			getListSuccess(mLists);
		} catch (Exception e) {
			setDebugLog(e);
			exceptionToast();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
			mDialog = CustomDialog.AlertToCustomDialog(this, this);
			return;
		}
		FollowInvitationBean bean = mLists.get(index);
		CommentDetailActivity.startAtion(this, mInvitationInfoBean.getTopic_id(), bean.getPkid(), bean.getFloor());

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
		case R.id.fl_title_bar_two:
			if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				break;
			}
			if (collectStatus == 1) {// 已收藏
				mrequestType = 4;
			} else {
				mrequestType = 3;
			}
			setRequestParamsPre(TAG);
			break;
		case R.id.fl_title_bar_one:
			if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				break;
			}
			mDialogType = 0;
			String name;
			if (isAuthor) {
				name = "删除此贴";
			} else {
				name = "举报此贴";
			}
			InvitationActionSheetDialog.showAlert(this, this, name, getResources().getColor(R.color.nomal_btn_color));
			break;
		case R.id.follow_up_invitation:// 跟帖
			if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				break;
			}
			if (mNickname_status == 1) {// 昵称不存在
				mDialogType = 1;
				mDialog = CustomDialog.CancelAlertToDialog("没图没真相的你还不能执行此操作", "完善资料", "取消", this, this);
				break;
			}
			if (mForbid_status == 1) {// 被禁言
				ToastUtils.showToastInCenter("您已被管理员禁言");
				break;
			}
			PostInvitationActivity.startAction(this, mInvitationInfoBean, 2, 1);
			break;
		case R.id.text_like_btn:
			if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				break;
			}
			if (likeStatus == 1) {// 已点赞
				mrequestType = 2;
			} else {
				mrequestType = 1;
			}
			setRequestParamsPre(TAG);
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
		case R.id.tv_confrim_btn:
			mDialog.dismiss();
			if (mDialogType == 1) {
				EditProfileActivity.startAction(this, Finals.REQUESTCODE_ONE, null, null, 1);
				break;
			}
			if (isAuthor) {// 删除帖子
				mrequestType = 5;
			} else {// 举报帖子
				mrequestType = 6;
			}
			setRequestParamsPre(TAG);
			break;
		case R.id.tv_cancel_btn:
			mDialog.dismiss();
			break;
		default:
			break;
		}

	}

	@Override
	public void initAdapter() {
		mListAdapter = new InvitationDetailAdapter(this, mLists, R.layout.invitation_detail_item);
	}

	@Override
	public void refreshList() {
		mrequestType = 0;
		mPage = 1;
		setRequestParamsPre(TAG);
	}

	@Override
	public void loadMoreList() {
		mrequestType = 0;
		setRequestParamsPre(TAG, false);
	}

	@Override
	protected void setNoneDataDesc() {

	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	/**
	 * 删除帖子对话框的点击
	 */
	@Override
	public void onClickCallBack(int viewId) {
		if (viewId == R.id.btn_select) {
			if (isAuthor) {// 删除帖子
				mDialog = CustomDialog.CancelAlertToDialog("确认删除此条帖子及相关所有跟帖吗,删除后数据将不可恢复", "删除", "取消", this, this);
			} else {// 举报帖子
				mDialog = CustomDialog.CancelAlertToDialog("如果此帖含有政治敏感，色情暴力，人生攻击等不合法内容请举报", "举报", "取消", this, this);
			}
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (isExcute && mrequestType == 0 && mPage == 1 && isFirstJoin) {
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
			MobclickAgent.onPageStart("topicDetail"); 
			MobclickAgent.onResume(this); // 统计时长
		}

		@Override
		protected void onPause() {
			super.onPause();
			MobclickAgent.onPageEnd("topicDetail"); 
			MobclickAgent.onPause(this);
		}
}

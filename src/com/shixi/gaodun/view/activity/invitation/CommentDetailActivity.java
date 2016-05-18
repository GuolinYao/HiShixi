package com.shixi.gaodun.view.activity.invitation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommentAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.OnclickSelectedIdCallBack;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.inf.ScrollViewListener;
import com.shixi.gaodun.libpull.PullToRefreshBase.OnRefreshListener;
import com.shixi.gaodun.libpull.PullToRefreshScrollView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CommentInvitationBean;
import com.shixi.gaodun.model.domain.FollowInvitationBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.ImageInfoBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.InvitationActionSheetDialog;
import com.shixi.gaodun.view.MyListView;
import com.shixi.gaodun.view.ObservableScrollView;
import com.shixi.gaodun.view.PostCommentView;
import com.shixi.gaodun.view.activity.LoginActivity;
import com.shixi.gaodun.view.activity.RegisterActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 评论详情
 * 
 * @author ronger guolin
 * @date:2015-12-1 下午3:34:29
 */
@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
public class CommentDetailActivity extends TitleBaseActivity implements
		ScrollViewListener, OnItemClickListener, OnclickSelectedIdCallBack {
	private String mTopicId;
	private String mReplyId;
	private String mFloor;
	private PullToRefreshScrollView mPullToRefreshScrollView;
	private ObservableScrollView mScrollView;
	private MyListView mListView;
	private TextView mTextName, mTextTime, mTextFloor, mTextContent;
	private ImageView mImagePhoto, mImageSex;
	private LinearLayout mLayoutImages;
	private PostCommentView mEditComment;
	private View mContentView;
	private int mRequestType = 0;// 跟帖的评论详情，1删除帖子，2举报帖子，3发送评论，4删除回复和评论
	private int mPage = 1, mPageNumber = 20;
	private boolean isLoadMore;// 是否有加载更多
	private View mLoadMoreLayout;// 加载更多时布局
	private List<CommentInvitationBean> mFollowInvitationCommentLists;// 跟帖列表
	private FollowInvitationBean mFollowUpInvitationDetail;// 跟帖详情
	private int mFiCommentTotal;// 跟帖的评论总数
	private CommentAdapter mCommentAdapter;
	private CommentInvitationBean mCurrentCommentBean = null;// 当前选择的评论信息,用于回复别人评论时
	private boolean isAuthor;
	private int mDialogType;// 1删除此贴，2举报此贴，3删除评论
	private Dialog mDialog;
	private int mForbid_status;// 禁言状态1 已被禁言 2 未被禁言

	public static void startAtion(Context context, String topic_id,
			String reply_id, String floor) {
		Intent intent = new Intent(context, CommentDetailActivity.class);
		intent.putExtra("topic_id", topic_id);
		intent.putExtra("reply_id", reply_id);
		intent.putExtra("floor", floor);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mTopicId = data.getStringExtra("topic_id");
		mReplyId = data.getStringExtra("reply_id");
		mFloor = data.getStringExtra("floor");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = true;
		setContentView(R.layout.comment_detail_layout);
		setRequestParamsPre(TAG);
		mMainNoneDataLayout.setBackgroundColor(Color.TRANSPARENT);
		mMainNoneDataLayout.setVisibility(View.VISIBLE);
		mMainNoneDataLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mEditComment.hideKeyboard(CommentDetailActivity.this);
				return false;
			}
		});
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.layout_comment_icon:// 评论按钮
			mEditComment.showKeyboard(this);
			break;
		case R.id.btn_send:// 发送评论
			sendCommentPre();
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
		case R.id.fl_title_bar_one: // 1删除此贴，2举报此贴，3删除评论
			String name;
			if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
				mDialog = CustomDialog.AlertToCustomDialog(this, this);
				break;
			}
			if (isAuthor) {
				mDialogType = 1;
				mRequestType = 1;
				name = "删除此贴";
			} else {
				mDialogType = 2;
				mRequestType = 2;
				name = "举报此贴";
			}
			InvitationActionSheetDialog.showAlert(this, this, name,
					getResources().getColor(R.color.nomal_btn_color));
			break;
		case R.id.tv_confrim_btn:
			mDialog.dismiss();
			if (mDialogType == 1) {// 删除帖子
				mRequestType = 1;
			} else if (mDialogType == 2) {// 举报帖子
				mRequestType = 2;
			}
			setRequestParamsPre(TAG);
			break;
		case R.id.tv_cancel_btn:
			mDialog.dismiss();
			break;
		case R.id.button_again:
			setRetryRequest();
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("评论详情");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mRightOneIcon.setImageResource(R.drawable.gengduo);
		mRightOneLayout.setVisibility(View.VISIBLE);
		mRightOneLayout.setOnClickListener(this);
		mOtherName.setVisibility(View.GONE);

		mLoadMoreLayout = LayoutInflater.from(this).inflate(
				R.layout.loadmore_layout, null);
		FrameLayout mFrameLayout = (FrameLayout) findViewById(R.id.soft_keyboard_layout);
		mEditComment = (PostCommentView) findViewById(R.id.view_bottom);
		mEditComment.setSendCallBack(this);
		mEditComment.setFrameLayout(mFrameLayout);
		mEditComment.setmClickListener(this);
		mainView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditComment.hideKeyboard(CommentDetailActivity.this);
			}
		});

		mFrameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEditComment.hideKeyboard(CommentDetailActivity.this);
			}
		});

		mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.follow_comment_scroll);
		mScrollView = (ObservableScrollView) mPullToRefreshScrollView
				.getRefreshableView();
		mScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mContentView = LayoutInflater.from(this).inflate(
				R.layout.comment_detail_content_layout, null);
		mScrollView.addView(mContentView);
		initContentView();
		setRefreshOrLoadMoreListener();
	}

	public void initContentView() {
		mListView = (MyListView) mContentView
				.findViewById(R.id.listview_comment);
		mTextName = (TextView) mContentView.findViewById(R.id.text_name);
		mTextTime = (TextView) mContentView.findViewById(R.id.text_time);
		mTextFloor = (TextView) mContentView.findViewById(R.id.text_floor);
		mTextContent = (TextView) mContentView.findViewById(R.id.text_content);
		mImagePhoto = (ImageView) mContentView.findViewById(R.id.image_user);
		mImageSex = (ImageView) mContentView.findViewById(R.id.image_sex_icon);
		mLayoutImages = (LinearLayout) mContentView
				.findViewById(R.id.layout_invitation_images);
		mListView.setOnItemClickListener(this);
	}

	public void setFollowUpInvitationDataShow() {
		setTextViewShow(mTextName, mFollowUpInvitationDetail.getNickname(),
				"某同学");
		setTextViewShow(mTextTime, mFollowUpInvitationDetail.getCreate_time());
		isAuthor = NumberUtils.getInt(
				mFollowUpInvitationDetail.getReply_status(), 2) == 1 ? true
				: false;
		mTextFloor.setText("第" + mFloor + "楼");
		if (StringUtils.isEmpty(mFollowUpInvitationDetail.getContent()))
			mTextContent.setVisibility(View.GONE);
		else
			setTextViewShow(mTextContent,
					mFollowUpInvitationDetail.getContent());
		if (!StringUtils.isEmpty(mFollowUpInvitationDetail.getSns_avatar()))
			BaseApplication.mApp.setCircleImageByUrl(mImagePhoto,
					mFollowUpInvitationDetail.getSns_avatar(),
					R.drawable.default_touxiang_xiaoerhei);
		// setImageByUrl(mImagePhoto, mFollowUpInvitationDetail.getSns_avatar(),
		// R.drawable.default_touxiang_xiaoerhei,
		// ActivityUtils.dip2px(this, 45));
		int sex = NumberUtils.getInt(mFollowUpInvitationDetail.getGender(), 1);
		if (sex == 1) {// 男
			setImageResource(mImageSex, R.drawable.topic_male);
		}
		if (sex == 2) {// 女
			setImageResource(mImageSex, R.drawable.topic_female);
		}
		List<ImageInfoBean> images = mFollowUpInvitationDetail.getImage();
		if (null == images || images.size() <= 0) {
			mLayoutImages.setVisibility(View.GONE);
		} else {
			mLayoutImages.setVisibility(View.VISIBLE);
			setTopicImagesShow(images);
		}
	}

	public void setTopicImagesShow(final List<ImageInfoBean> images) {
		mLayoutImages.removeAllViews();
		for ( int i = 0; i < images.size(); i++) {
			final int position = i;
			ImageInfoBean imageInfoBean = images.get(i);
			View view = LayoutInflater.from(this).inflate(
					R.layout.invitation_image_item, null);
			View dividerView = view.findViewById(R.id.view_divider);
			if (i == images.size() - 1) {
				dividerView.setVisibility(View.GONE);
			}
			ImageView imageView = (ImageView) view
					.findViewById(R.id.image_item);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					GalleryActivity.startAction(CommentDetailActivity.this,
							(ArrayList<ImageInfoBean>) images, position);
				}
			});
			float scale = imageInfoBean.getHeight()
					/ (float) imageInfoBean.getWidth();
			int imageViewWidth = ActivityUtils.getScreenWidth()
					- ActivityUtils.dip2px(this, 15);
			int imageViewHeight = (int) (imageViewWidth * scale);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					imageViewWidth, imageViewHeight);
			imageView.setLayoutParams(lp);
			imageView.setBackgroundColor(getResources().getColor(
					R.color.main_bg_color));
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setAdjustViewBounds(true);
			BaseApplication.mApp.setBigNomalImageByUrl(imageView,
					imageInfoBean.getValue(),
					R.drawable.default_touxiang_xiaoerhei);
			// setBigImageByUrl(imageView, imageInfoBean.getValue(),
			// R.drawable.default_image_banner);
			mLayoutImages.addView(view);
		}
	}

	/**
	 * 设置刷新或加载更多的监听
	 */
	public void setRefreshOrLoadMoreListener() {
		mPullToRefreshScrollView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshCommentDetail();
			}
		});
		mScrollView.setScrollViewListener(this);
	}

	/**
	 * 刷新评论列表
	 */
	public void refreshCommentDetail() {
		mRequestType = 0;
		if (ActivityUtils.isNetAvailable()) {
			mPage = 1;
			setRequestParamsPre(TAG);
		} else {
			ToastUtils.showToastInCenter("网络未连接");
			if (mListView.getFooterViewsCount() != 0) {
				mListView.removeFooterView(mLoadMoreLayout);
			}
			mPullToRefreshScrollView.onRefreshComplete();
		}

	}

	/**
	 * 跟帖的评论详情，1删除帖子，2举报帖子，3发送评论，4删除回复和评论
	 */
	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mRequestType == 0) {
			getCommentDetailList();
			return;
		}
		if (mRequestType == 1) {
			deleteInvitation();
			return;
		}
		if (mRequestType == 2) {
			reportInvitation();
			return;
		}
		if (mRequestType == 3) {
			sendComment();
			return;
		}
		if (mRequestType == 4) {
			deleteComment();
			return;
		}
	}

	public void getCommentDetailList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("topic_id", mTopicId);
		map.put("reply_id", mReplyId);
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this)))
			map.put("student_id", CacheUtils.getStudentId(this));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.COMMENTDETAIL_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			dissMissProgress();
			isFirstJoin = false;
			mPullToRefreshScrollView.onRefreshComplete();
			showLoadingControl(false);
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setNolmalShow();
			mRightOneLayout.setEnabled(true);
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String status = object.getString("status");
			if (StringUtils.isNotEmpty(status)) {
				JSONObject statusObject = new JSONObject(status);
				mForbid_status = NumberUtils.getInt(
						statusObject.getString("forbid_status"), 2);
			}

			// 跟帖详情
			String followUpInvitationDetail = object.getString("reply");
			mFollowUpInvitationDetail = TransFormModel.getResponseResultObject(
					followUpInvitationDetail, FollowInvitationBean.class);
			setFollowUpInvitationDataShow();
			// 跟帖的评论总数
			mFiCommentTotal = object.getInt("commentTotal");
			mFollowUpInvitationDetail.setCommentTotal(mFiCommentTotal);
			// 跟帖的评论列表
			if (mFiCommentTotal <= 0) {
				mListView.setVisibility(View.GONE);
				return;
			}
			mListView.setVisibility(View.VISIBLE);
			String fiCommentLists = object.getString("comment");
			if (StringUtils.isEmpty(fiCommentLists)
					&& fiCommentLists.length() <= 2) {
				mListView.setVisibility(View.GONE);
				return;
			}
			mListView.setVisibility(View.VISIBLE);
			// 跟帖评论列表,根据总数判断是否有下一页
			getFICommentListSuccess(fiCommentLists);
		} catch (Exception e) {
			setDebugLog(e);
			exceptionToast();
		}
	}

	/**
	 * 获取跟帖列表成功
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void getFICommentListSuccess(String resultData) throws Exception {
		List<CommentInvitationBean> mLists = ((List<CommentInvitationBean>) TransFormModel
				.getResponseResults(resultData, CommentInvitationBean.class));
		if (mPage > 1) {// 非第一次加载
			mFollowInvitationCommentLists.addAll(mLists);
		} else {
			mFollowInvitationCommentLists = mLists;
		}
		// 判断是否有更多数据
		if (mFiCommentTotal - mFollowInvitationCommentLists.size() > 0) {// 有更多数据
			if (mListView.getFooterViewsCount() == 0) {
				isLoadMore = true;
				mListView.addFooterView(mLoadMoreLayout);
			}
		} else {
			if (mListView.getFooterViewsCount() != 0) {
				isLoadMore = false;
				mListView.removeFooterView(mLoadMoreLayout);
			}
		}
		setCommentListDataShow();
		mPage++;
	}

	/**
	 * 设置跟帖的评论列表的显示
	 */
	public void setCommentListDataShow() {
		if (null == mCommentAdapter || mPage * mPageNumber <= mPageNumber) {// 第一次加载或刷新
			mCommentAdapter = null;
			mCommentAdapter = new CommentAdapter(this,
					mFollowInvitationCommentLists, R.layout.comment_item_layout);
			mListView.setAdapter(mCommentAdapter);
		} else {
			mCommentAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 主要用来到底部时加载更多评论
	 */
	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		if (scrollView.getChildAt(0).getMeasuredHeight() <= scrollView
				.getHeight() + scrollView.getScrollY()) {
			if (isLoadMore) {
				if (ActivityUtils.isNetAvailable()) {
					// 加载更多
					mRequestType = 0;
					showLoadingControl(true);
					setRequestParamsPre(TAG, false);
				} else {
					ToastUtils.showToastInCenter("网络未连接");
				}
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mCurrentCommentBean = mFollowInvitationCommentLists.get(position
				- mListView.getHeaderViewsCount());
		// 若是点击的自己的回复，弹出删除对话框,否则弹出软键盘
		String commentStatus = mCurrentCommentBean.getComment_status();
		if (null != commentStatus && commentStatus.equals("1")) {
			mDialogType = 3;
			mRequestType = 4;
			InvitationActionSheetDialog.showAlert(this, this, "删除",
					getResources().getColor(R.color.delete_btn_color));
			return;
		}
		String pinHint = StringUtils.isEmpty(mCurrentCommentBean.getNickname()) ? "某同学"
				: mCurrentCommentBean.getNickname();
		mEditComment.setEditextHint("回复@" + pinHint);
		// 打开软键盘
		mEditComment.showKeyboard(this);
		mEditComment.setmCurrentCommentBean(mCurrentCommentBean);
	}

	/**
	 * 删除评论
	 */
	public void deleteComment() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		// 楼主贴id
		map.put("topic_id", mTopicId);
		// 跟帖id
		map.put("floor_reply_id", mReplyId);
		// 互动评论或回复主键ID
		map.put("reply_id", mCurrentCommentBean.getPkid());
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.DELETECOMMENT_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								myProgressDialog.dismiss();
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							mRequestType = 0;
							mPage = 1;
							setRequestParamsPre(TAG);
						} catch (Exception e) {
							myProgressDialog.dismiss();
							setDebugLog(e);
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 发送评论
	 */
	public void sendComment() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		// 评论或回复的内容
		map.put("content", mEditComment.getmEditContent().getText().toString());
		// 楼主贴id
		map.put("topic_id", mTopicId);
		// 跟帖id
		map.put("floor_reply_id", mReplyId);
		mCurrentCommentBean = mEditComment.getmCurrentCommentBean();
		if (mCurrentCommentBean == null) {
			map.put("type", "2");
		} else {
			map.put("type", "3");
			map.put("reply_id", mCurrentCommentBean.getPkid());
		}
		CheckBox checkBox = mEditComment.getmBtnAnonymity();
		int anonymous = checkBox.isChecked() ? 1 : 2;
		map.put("anonymous", String.valueOf(anonymous));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.COMMENTFLOOR_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								myProgressDialog.dismiss();
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							mEditComment.clearEditContent();
							mRequestType = 0;
							mPage = 1;
							setRequestParamsPre(TAG);
						} catch (Exception e) {
							setDebugLog(e);
							myProgressDialog.dismiss();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 删除跟帖
	 */
	public void deleteInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("topic_id", mTopicId);
		map.put("reply_id", mReplyId);
		map.put("type", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.DELETEINVITATION_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							myProgressDialog.dismiss();
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							mRequestType = 0;
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
		map.put("topic_id", mTopicId);
		map.put("floor_reply_id", mReplyId);
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.REPORTINVITATION_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							myProgressDialog.dismiss();
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							mRequestType = 0;
						} catch (Exception e) {
							setDebugLog(e);
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void onClickCallBack(int viewId) {
		if (viewId == R.id.btn_select) {// 1删除此贴，2举报此贴，3删除评论
			if (mDialogType == 1) {// 删除帖子
				mDialog = CustomDialog.CancelAlertToDialog(
						"确认删除此条帖子及相关所有跟帖吗,删除后数据将不可恢复", "删除", "取消", this, this);
				return;
			}
			if (mDialogType == 2) {// 举报帖子
				mDialog = CustomDialog
						.CancelAlertToDialog("如果此帖含有政治敏感，色情暴力，人生攻击等不合法内容请举报",
								"举报", "取消", this, this);
				return;
			}
			if (mDialogType == 3) {// 删除评论
				mRequestType = 4;
				setRequestParamsPre(TAG);
				return;
			}
		}
		// 发送按钮
		if (viewId == R.id.btn_send) {
			sendCommentPre();
		}
	}

	public void sendCommentPre() {
		String content = mEditComment.getmEditContent().getText().toString();
		if (StringUtils.isEmpty(content) || null == mFollowUpInvitationDetail) {
			return;
		}
		if (StringUtils.isEmpty(CacheUtils.getStudentId(this))) {
			mDialog = CustomDialog.AlertToCustomDialog(this, this);
			return;
		}
		if (mForbid_status == 1) {// 已被禁言
			ToastUtils.showToastInCenter("您已被管理员禁言");
			return;
		}
		mRequestType = 3;
		setRequestParamsPre(TAG);
	}

	// 加载更多时
	private void showLoadingControl(boolean isShow) {
		mLoadMoreLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (isExcute && isFirstJoin && mPage == 1 && mRequestType == 0) {
			setOnErrorResponse(error);
			mRightOneLayout.setEnabled(false);
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
		MobclickAgent.onPageStart("commentDetail"); // 评论详情：commentDetail

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("commentDetail");
	}

}

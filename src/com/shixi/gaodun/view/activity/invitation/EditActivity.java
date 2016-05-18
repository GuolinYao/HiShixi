package com.shixi.gaodun.view.activity.invitation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.adapter.MineIvitationListAdapter;
import com.shixi.gaodun.adapter.MyReplyListAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.inf.ScrollViewListener;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CommentInvitationBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.MyListView;
import com.shixi.gaodun.view.ObservableScrollView;
import com.umeng.analytics.MobclickAgent;

/**
 * 编辑页
 * 
 * 我的帖子是指我发布的楼主贴和跟帖
 * 
 * 我的回复是指我回复的别人的（包括我的跟帖、我对于某跟帖的评论、我针对于某条评论的回复）
 * 
 * @author ronger guolin
 * @date:2015-12-3 上午11:43:44
 */
@SuppressLint("InflateParams")
public class EditActivity extends BaseMainActivity implements ScrollViewListener, OnItemClickListener {
	private ObservableScrollView mScrollView;
	private View mTitleView;
	private ImageView mTitleBack;
	private TextView mEditOther;
	private ImageView mImagePhoto, mImageSex;
	private TextView mTextName;
	private TextView mTabInvitation, mTabReply;
	private MyListView myInvitationListView, myReplyListView;
	private int mType = 1;// 1 我的帖子 2 我的回复(包括跟帖、评论、回复)
	private int mInvitationPage = 1, mPageNumber = 10, mReplyPage = 1;
	private View mLoadMoreLayout;// 加载更多时布局
	private boolean isLoadMoreInvitation, isLoadMoreReply;// 是否有加载更多
	private boolean isLoadMoreInvitationFinish, isLoadMoreReplyFinish;// 是否有加载更多
	private List<InvitationInfoBean> mInvitationLists;
	private List<CommentInvitationBean> mRePlyLists;
	private int mInvitationListTotal, mReplyListTotal;// 总数
	private CommonAdapter<InvitationInfoBean> mInvitationListAdapter;
	private CommonAdapter<CommentInvitationBean> mReplyAdapter;
	private View mTabView;
	private View mMainTabView;
	private TextView mMainTabInvitation, mMainTabReply;
	private TextView mMainTextTitleName;
	private String mUrl;// 圈子头像
	// tab的Y坐标
	private float tabY;
	// 标题栏透明度，随y轴变化,初始为0全透明
	private float titleAlpha = 0.0f;
	// 后续上下滑动时需要乘的值
	private float scaleY = 1.0f;

	public static void startAction(FragmentActivity context) {
		Intent intent = new Intent(context, EditActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// tabY = mTabView.getY() - mTabView.getHeight();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.mine_edit_layout);
		isExcute = false;
		initView();
		// 初始获取我的帖子列表
		setRequestParamsPre(TAG);
		setFilterRegister();
	}

	@Override
	public void setBroadcastReceiver() {
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (mType == 1)
					mInvitationPage = 1;
				else
					mReplyPage = 1;
				toRequestServer(intent);
			}
		};
	}

	@Override
	public void initView() {
		super.initView();
		mEditOther = (TextView) findViewById(R.id.text_edit);
		mTitleBack = (ImageView) findViewById(R.id.iv_title_bar_icon);
		mMainTextTitleName = (TextView) findViewById(R.id.text_title_name);
		mMainTabView = findViewById(R.id.layout_tab);
		mMainTabInvitation = (TextView) findViewById(R.id.text_tab_invitation);
		mMainTabReply = (TextView) findViewById(R.id.text_tab_reply);
		mMainTabReply.setOnClickListener(this);
		mMainTabInvitation.setOnClickListener(this);
		mTitleView = findViewById(R.id.layout_title);
		mScrollView = (ObservableScrollView) findViewById(R.id.refresh_scrollview);
		mScrollView.setScrollViewListener(this);
		findViewById(R.id.text_edit).setOnClickListener(this);
		findViewById(R.id.text_edit).setOnClickListener(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View mContentView = inflater.inflate(R.layout.edit_layout, null);
		mLoadMoreLayout = inflater.inflate(R.layout.loadmore_layout, null);
		mScrollView.addView(mContentView);
		initContentView(mContentView);
	}

	public void initContentView(View mContentView) {
		mTabView = mContentView.findViewById(R.id.layout_tab);
		mTabView.post(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				tabY = mTabView.getY() - mTabView.getHeight();
			}
		});
		mImagePhoto = (ImageView) mContentView.findViewById(R.id.image_url);
		mImagePhoto.setOnClickListener(this);
		mImageSex = (ImageView) mContentView.findViewById(R.id.image_sex);
		mTextName = (TextView) mContentView.findViewById(R.id.text_name);
		mTabInvitation = (TextView) mContentView.findViewById(R.id.text_tab_invitation);
		mTabReply = (TextView) mContentView.findViewById(R.id.text_tab_reply);
		myInvitationListView = (MyListView) mContentView.findViewById(R.id.eidt_invitation_listview);
		myReplyListView = (MyListView) mContentView.findViewById(R.id.eidt_reply_listview);
		myInvitationListView.setOnItemClickListener(this);
		myReplyListView.setOnItemClickListener(this);
		// 初始为选中帖子
		mTabInvitation.setSelected(true);
		mMainTabInvitation.setSelected(true);
		mTabInvitation.setOnClickListener(this);
		mTabReply.setOnClickListener(this);
		mTabReply.setSelected(false);
		mMainTabReply.setSelected(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_tab_invitation:
			mType = 1;
			mInvitationPage = 1;
			updateTabStatus(true, false);
			myInvitationListView.setVisibility(View.VISIBLE);
			myReplyListView.setVisibility(View.GONE);
			if (null != mInvitationLists && !mInvitationLists.isEmpty()) {
				mScrollView.smoothScrollTo(0, 0);
				break;
			}
			setRequestParamsPre(TAG);
			break;
		case R.id.text_tab_reply:
			mType = 2;
			mReplyPage = 1;
			updateTabStatus(false, true);
			myInvitationListView.setVisibility(View.GONE);
			myReplyListView.setVisibility(View.VISIBLE);
			if (null != mRePlyLists && !mRePlyLists.isEmpty()) {
				mScrollView.smoothScrollTo(0, 0);
				break;
			}
			setRequestParamsPre(TAG);
			break;
		case R.id.text_edit:
			EditProfileActivity.startAction(this, Finals.REQUESTCODE_ONE, mTextName.getText().toString(), mUrl, 1);
			break;
		case R.id.image_url:
			EditProfileActivity.startAction(this, Finals.REQUESTCODE_ONE, mTextName.getText().toString(), mUrl, 1);
			break;
		case R.id.fl_title_bar_back:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 更新tab的状态
	 * 
	 * @param tabOne
	 * @param tabTwo
	 */
	public void updateTabStatus(boolean tabOne, boolean tabTwo) {
		mTabInvitation.setSelected(tabOne);
		mMainTabInvitation.setSelected(tabOne);
		mTabReply.setSelected(tabTwo);
		mMainTabReply.setSelected(tabTwo);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		if (mType == 1)
			map.put("page", String.valueOf(mInvitationPage));
		else {
			map.put("page", String.valueOf(mReplyPage));
		}
		map.put("type", String.valueOf(mType));

		map.put("pageNumber", String.valueOf(mPageNumber));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.MYINVITATION_REPLY_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			dissMissProgress();
			showLoadingControl(false);
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			JSONObject object = new JSONObject(httpRes.getReturnData());
			// 个人信息
			if (object.has("my")) {
				setPersonageBasicInfo(object.getString("my"));
			}
			// 总数
			int mListTotal = object.getInt("listTotal");
			// 1 我的帖子 2 我的回复
			if (mType == 1) {
				mInvitationListTotal = mListTotal;
				getInvitationListSuccess(object.getString("list"));
				return;
			}
			if (mType == 2) {
				mReplyListTotal = mListTotal;
				getReplyListsSuccess(object.getString("list"));
				return;
			}
		} catch (Exception e) {
			setDebugLog(e);
			exceptionToast();
		}
	}

	/**
	 * 获取我的帖子列表成功
	 * 
	 * @param str
	 * @throws Exception
	 */
	public void getInvitationListSuccess(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2)
			return;
		List<InvitationInfoBean> reInvitationLists = TransFormModel.getInvitationInfoList(resultData, false);
		if (mInvitationPage > 1) {// 非第一次加载
			mInvitationLists.addAll(reInvitationLists);
		} else {
			mInvitationLists = reInvitationLists;
		}
		// 判断是否有更多数据
		if (mInvitationListTotal - mInvitationLists.size() > 0) {// 有更多数据
			isLoadMoreInvitation = true;
			if (myInvitationListView.getFooterViewsCount() == 0) {
				myInvitationListView.addFooterView(mLoadMoreLayout);
			}
		} else {
			isLoadMoreInvitation = false;
			if (myInvitationListView.getFooterViewsCount() != 0) {
				myInvitationListView.removeFooterView(mLoadMoreLayout);
			}
		}
		setInvitationListDataShow();
		isLoadMoreInvitationFinish = true;
		mInvitationPage++;
	}

	/**
	 * 设置帖子列表的显示
	 */
	public void setInvitationListDataShow() {
		if (null == mInvitationListAdapter || mInvitationPage * mPageNumber <= mPageNumber) {// 第一次加载或者刷新的显示
			mInvitationListAdapter = null;
			mInvitationListAdapter = new MineIvitationListAdapter(this, mInvitationLists,
					R.layout.invitation_item_layout);
			myInvitationListView.setAdapter(mInvitationListAdapter);
			mScrollView.smoothScrollTo(0, 0);
		} else {
			mInvitationListAdapter.notifyDataSetChanged();
		}
		// ListViewUtils.setListViewHeightBasedOnChildren(myInvitationListView);
	}

	/**
	 * 获取我的回复列表成功
	 * 
	 * @param str
	 * @throws Exception
	 */
	public void getReplyListsSuccess(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2)
			return;
		List<CommentInvitationBean> rePlyLists = ((List<CommentInvitationBean>) TransFormModel.getResponseResults(
				resultData, CommentInvitationBean.class));
		if (mReplyPage > 1) {// 非第一次加载
			mRePlyLists.addAll(rePlyLists);
		} else {
			mRePlyLists = rePlyLists;
		}
		if (mReplyListTotal - mRePlyLists.size() > 0) {// 有更多数据
			isLoadMoreReply = true;
			if (myReplyListView.getFooterViewsCount() == 0) {
				myReplyListView.addFooterView(mLoadMoreLayout);
			}
		} else {
			isLoadMoreReply = false;
			if (myReplyListView.getFooterViewsCount() != 0) {
				myReplyListView.removeFooterView(mLoadMoreLayout);
			}
		}
		setReplyListDataShow();
		isLoadMoreReplyFinish = true;
		mReplyPage++;
	}

	/**
	 * 设置评论列表的显示
	 */
	public void setReplyListDataShow() {
		if (null == mReplyAdapter || mReplyPage * mPageNumber <= mPageNumber) {// 第一次加载或刷新
			mReplyAdapter = null;
			mReplyAdapter = new MyReplyListAdapter(this, mRePlyLists, R.layout.reply_item_layout);
			myReplyListView.setAdapter(mReplyAdapter);
			mScrollView.smoothScrollTo(0, 0);
		} else {
			mReplyAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 设置个人基本信息
	 * 
	 * @param info
	 * @throws JSONException
	 */
	public void setPersonageBasicInfo(String info) throws JSONException {
		JSONObject userinfo = new JSONObject(info);
		String name = userinfo.getString("nickname");
		String sex = userinfo.getString("gender");
		String url = userinfo.getString("sns_avatar");
		mUrl = url;
		setTextViewShow(mTextName, name);
		setTextViewShow(mMainTextTitleName, name, "某同学");

		if (StringUtils.isNotEmpty(url)) {
			BaseApplication.mApp.setCircleImageByUrl(mImagePhoto, url, R.drawable.touxiang);
			// try {
			// DisplayImageOptions options = DisplayImageOptionsUtils
			// .getCircleBitMap(R.drawable.default_touxiang_xiaoerhei);
			// ImageLoader.getInstance().displayImage(url, mImagePhoto, options);
			// } catch (Exception e) {
			// mImagePhoto.setImageDrawable(BaseApplication.mApp.getResources().getDrawable(
			// R.drawable.default_touxiang_xiaoerhei));
			// }
			// // setImageByUrl(mImagePhoto, url, R.drawable.touxiang, 50);
		}
		if (StringUtils.isEmpty(sex))
			return;
		if (sex.equals("1")) {// 男
			setImageResource(mImageSex, R.drawable.topic_male);
		}
		if (sex.equals("2")) {// 女
			setImageResource(mImageSex, R.drawable.topic_female);
		}
	}

	// 加载更多时
	private void showLoadingControl(boolean isShow) {
		mLoadMoreLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	@SuppressLint("NewApi")
	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
		if (tabY == 0)
			return;
		// 顶部滑动到tab位置之前，不断的改变标题栏的透明度
		if (y >= tabY) {
			titleAlpha = 1.0f;
			mTitleView.setAlpha(titleAlpha);
			setTitleViewBG(true);
			mMainTabView.setVisibility(View.VISIBLE);
		} else {
			// 算变化值,动态改变透明度
			scaleY = y / tabY;
			// 滑动到间距一半的时候改变标题栏的背景
			if (y >= (tabY / 2)) {
				setTitleViewBG(true);
				mTitleView.setAlpha(scaleY);
			} else {
				setTitleViewBG(false);
				mTitleView.setAlpha(1.0f);
			}
			mMainTabView.setVisibility(View.GONE);
		}
		if (mType == 1 && null != mInvitationLists) {
			loadMore(scrollView);
			return;
		}
		if (mType == 2 && null != mRePlyLists) {
			loadMore(scrollView);
			return;
		}

	}

	public void loadMore(ObservableScrollView scrollView) {
		if (scrollView.getChildAt(0).getMeasuredHeight() <= scrollView.getHeight() + scrollView.getScrollY()) {
			if (mType == 1 && isLoadMoreInvitation && isLoadMoreInvitationFinish) {
				isLoadMoreInvitationFinish = false;
				loadMoreData();
				return;
			}
			if (mType == 2 && isLoadMoreReply && isLoadMoreReplyFinish) {
				isLoadMoreReplyFinish = false;
				loadMoreData();
			}
		}
	}

	public void loadMoreData() {
		if (ActivityUtils.isNetAvailable()) {
			// 加载更多
			showLoadingControl(true);
			setRequestParamsPre(TAG, false);
		} else {
			ToastUtils.showToastInCenter("网络未连接");
		}
	}

	public void setTitleViewBG(boolean isOver) {
		int bgColor = isOver ? getResources().getColor(R.color.main_bg_color) : android.R.color.transparent;
		mTitleView.setBackgroundColor(bgColor);
		int backImage = isOver ? R.drawable.icon_back : R.drawable.fanhuibai;
		mTitleBack.setImageResource(backImage);
		int otherColor = isOver ? getResources().getColor(R.color.nomal_btn_color) : getResources().getColor(
				R.color.main_bg);
		mEditOther.setTextColor(otherColor);
		mMainTextTitleName.setVisibility(isOver ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int index = 0;
		if (mType == 1) {
			index = position - myInvitationListView.getHeaderViewsCount();
		}
		if (mType == 2) {
			index = position - myReplyListView.getHeaderViewsCount();
		}
		if (index < 0)
			return;
		// 1 我的帖子 2 我的回复(包括跟帖、评论、回复)
		if (mType == 1) {
			InvitationInfoBean invitationBean = mInvitationLists.get(index);
			if (null == invitationBean)
				return;
			InvitationDetailListActivity.startAction(this, invitationBean);
			return;
		}
		if (mType == 2) {
			CommentInvitationBean commentBean = mRePlyLists.get(index);
			if (null == commentBean)
				return;
			String g_status = commentBean.getG_status();
			if (g_status.equals("1")) {// 目标贴已被删除
				return;
			}
			int type = NumberUtils.getInt(commentBean.getType(), 3);// 1:跟帖2:评论,3:回复,4:楼主贴
			String topic_id = commentBean.getTopic_id();
			String floor = commentBean.getFloor();
			String reply_id = commentBean.getFloor_reply_id();
			switch (type) {
			case 1:
				InvitationDetailListActivity.startAction(this, new InvitationInfoBean(topic_id));
				break;
			case 2:
				CommentDetailActivity.startAtion(this, topic_id, reply_id, floor);
				break;
			case 3:
				CommentDetailActivity.startAtion(this, topic_id, reply_id, floor);
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("myTopic"); //我的帖子和回复 
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("myTopic"); 
		MobclickAgent.onPause(this);
	}
}

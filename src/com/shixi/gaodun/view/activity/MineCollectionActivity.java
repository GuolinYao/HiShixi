package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.BaseFragmentPagerAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.inf.MyOnPageChangeListenerInf;
import com.shixi.gaodun.inf.OnActivityRefreshListener;
import com.shixi.gaodun.inf.imp.MyOnPageChangeListener;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.fragment.CollectInvitationFrament;
import com.shixi.gaodun.view.fragment.CollectPositionFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的收藏
 * 
 * @author ronger
 * @date:2015-11-6 下午4:16:53
 */
public class MineCollectionActivity extends BaseMainActivity implements MyOnPageChangeListenerInf {
	private TextView mTabPosition, mTabInvitation;
	private TextView mTextClear;
	private ViewPager mViewPager;
	private List<Fragment> mFragments;
	private int mCurrentIndex = 0;
	private OnActivityRefreshListener mRefreshListenerPosition, mRefreshListenerIvitation;
	private Dialog mDialog;

	public void setOnActivityRefreshListenerPosition(OnActivityRefreshListener refreshListener) {
		this.mRefreshListenerPosition = refreshListener;
	}

	public void setOnActivityRefreshListenerInvitation(OnActivityRefreshListener refreshListener) {
		this.mRefreshListenerIvitation = refreshListener;
	}

	public void setTextEnabled(boolean enabled, int currentIndex) {
		if (mCurrentIndex == currentIndex) {
			mTextClear.setEnabled(enabled);
		}
	}

	public int getmCurrentIndex() {
		return mCurrentIndex;
	}

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, MineCollectionActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		isExcute = false;
		setContentView(R.layout.mine_collect_layout);
		initView();
	}

	@Override
	public void initView() {
		super.initView();
		mTabPosition = (TextView) findViewById(R.id.text_resume_tablerow_one);
		mTabPosition.setText("收藏职位");
		mTabPosition.setOnClickListener(this);
		mTabInvitation = (TextView) findViewById(R.id.text_resume_tablerow_two);
		mTabInvitation.setText("收藏帖子");
		mTabInvitation.setOnClickListener(this);
		mTextClear = (TextView) findViewById(R.id.tv_title_bar_otherbtn);
		mTextClear.setText("清空");
		setTextEnabled(false, 0);
		mViewPager = (ViewPager) findViewById(R.id.viewpage_resume_dynamic);
		initViewPager();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.text_resume_tablerow_one:// 收藏的职位
			mViewPager.setCurrentItem(0);
			break;
		case R.id.text_resume_tablerow_two:// 收藏的帖子
			mViewPager.setCurrentItem(1);
			break;
		case R.id.tv_title_bar_otherbtn:// 清空
			mDialog = CustomDialog.warnDialogHasTitle(mCurrentIndex == 0 ? "清空收藏职位" : "清空收藏帖子",
					mCurrentIndex == 0 ? "确定要删除所有收藏职位吗？删除后数据将不可恢复" : "确定要删除所有收藏帖子吗？删除后数据将不可恢复", "清空", "取消", this, this);
			break;
		case R.id.tv_title_confrim_btn:// 取消
			mDialog.dismiss();
			break;
		case R.id.tv_title_cancel_btn:// 清空
			mDialog.dismiss();
			setRequestParamsPre(TAG);
			break;
		default:
			break;
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mCurrentIndex == 0) {
			clearCollectPosition();
			return;
		}
		if (mCurrentIndex == 1) {
			clearCollectInvitation();
			return;
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			myProgressDialog.dismiss();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			if (mCurrentIndex == 0) {
				mRefreshListenerPosition.onRefreshList();
				return;
			}
			if (mCurrentIndex == 1) {
				mRefreshListenerIvitation.onRefreshList();
				return;
			}
		} catch (Exception e) {
			myProgressDialog.dismiss();
			setDebugLog(e);
		}
	}

	public void clearCollectPosition() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("type", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CANCEL_COLLECTPOSITION_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void clearCollectInvitation() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("type", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CANCEL_COLLECTINVITATION_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void initViewPager() {
		mFragments = new ArrayList<Fragment>(0);
		mFragments.add(new CollectPositionFragment());
		mFragments.add(new CollectInvitationFrament());
		mViewPager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), mFragments));
		mViewPager.setCurrentItem(mCurrentIndex);
		updateTabStatus(true, false);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener(this));
	}

	public void updateTabStatus(boolean tabOne, boolean tabTwo) {
		mTabPosition.setSelected(tabOne);
		mTabInvitation.setSelected(tabTwo);
	}

	@Override
	public void onPageSelected(int arg0) {
		switch (arg0) {
		case 0:
			mCurrentIndex = 0;
			updateTabStatus(true, false);
			// 刷新清空按钮的显示
			mRefreshListenerPosition.onCheckClearBtnCanClick();
			break;
		case 1:
			mCurrentIndex = 1;
			updateTabStatus(false, true);
			// 刷新清空按钮的显示
			mRefreshListenerIvitation.onCheckClearBtnCanClick();
			break;
		}

	}

	@Override
	protected void getIntentData() {

	}

	@Override
	public void cancelRequests() {
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("myCollect"); // 我的收藏
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("myCollect");
		MobclickAgent.onPause(this);
	}

}

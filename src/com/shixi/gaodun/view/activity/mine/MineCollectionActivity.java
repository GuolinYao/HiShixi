package com.shixi.gaodun.view.activity.mine;

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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.FragmentTabAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.inf.OnActivityRefreshListener;
import com.shixi.gaodun.inf.OnTabClickListener;
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
 * @date:2015-12-22 下午2:39:39
 */
public class MineCollectionActivity extends BaseMainActivity implements OnTabClickListener {
	// private TextView mTabPosition, mTabInvitation;
	private TextView mTextClear;
	private int mCurrentIndex = 0;
	private FragmentManager mFragmentManager;
	private OnActivityRefreshListener mRefreshListenerPosition, mRefreshListenerIvitation;
	private Dialog mDialog;
	private List<Fragment> mFragments;
	private RadioGroup mRadioGroup;
	private RadioButton mButtonPosition;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, com.shixi.gaodun.view.activity.mine.MineCollectionActivity.class);
		context.startActivity(intent);
	}

	public void setOnActivityRefreshListenerPosition(OnActivityRefreshListener refreshListener) {
		this.mRefreshListenerPosition = refreshListener;
	}

	public void setOnActivityRefreshListenerInvitation(OnActivityRefreshListener refreshListener) {
		this.mRefreshListenerIvitation = refreshListener;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		isExcute = false;
		setContentView(R.layout.mine_collect__new_layout);
		mFragmentManager = getSupportFragmentManager();
		initView();
	}

	@Override
	public void initView() {
		super.initView();
		mTextClear = (TextView) findViewById(R.id.tv_title_bar_otherbtn);
		mTextClear.setText("清空");
		mRadioGroup = (RadioGroup) findViewById(R.id.mine_collect_rg);
		mButtonPosition = (RadioButton) findViewById(R.id.rb_tab_position);
		mButtonPosition.setChecked(true);
		setTextEnabled(false, 0);
		initFragments();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		// case R.id.text_resume_tablerow_one:// 收藏的职位
		// mCurrentIndex = 0;
		// updateTabStatus(true, false);
		// changeFragment(mCurrentIndex);
		// // 刷新清空按钮的显示
		// if (null != mRefreshListenerPosition)
		// mRefreshListenerPosition.onCheckClearBtnCanClick();
		// // mViewPager.setCurrentItem(0);
		// break;
		// case R.id.text_resume_tablerow_two:// 收藏的帖子
		// mCurrentIndex = 1;
		// updateTabStatus(false, true);
		// changeFragment(mCurrentIndex);
		// // 刷新清空按钮的显示
		// if (null != mRefreshListenerIvitation)
		// mRefreshListenerIvitation.onCheckClearBtnCanClick();
		// break;
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
		Log.i("----", "getResponse-minecollection");
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

	public void initFragments() {
		mFragments = new ArrayList<Fragment>(2);
		mFragments.add(new CollectPositionFragment());
		mFragments.add(new CollectInvitationFrament());
		FragmentTabAdapter mAdapter = new FragmentTabAdapter(this, mFragments, R.id.fl_collect_tab_content,
				mRadioGroup, mCurrentIndex);
		mAdapter.setOnTabClickListener(this);
	}

	public void initFragment() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.fl_collect_tab_content, mFragments.get(0));
		ft.commit();
	}

	/**
	 * 改变容器中的fragment
	 * 
	 * @param f
	 * @param init
	 */
	private void changeFragment(int currentIndex) {
		for (int i = 0; i < mFragments.size(); i++) {
			// 当前页面,若存在，则直接显示，若不存在，则创建
			if (i == currentIndex) {
				Fragment fragment = mFragments.get(i);
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				fragment.onPause(); // 暂停当前tab
				// 若已添加则直接重新启动，若未添加则添加
				if (fragment.isAdded()) {
					fragment.onResume();// 启动目标tab的onresume
				} else {
					ft.add(R.id.mine_collect_layout, fragment);
				}
				showTab(i); // 显示目标tab,隐藏已存在的tab
				ft.commit();
			}
		}
	}

	/**
	 * 目标tab的显示
	 * 
	 * @param idx
	 */
	private void showTab(int idx) {
		for (int i = 0; i < mFragments.size(); i++) {
			Fragment fragment = mFragments.get(i);
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commit();
		}
	}

	// public void updateTabStatus(boolean tabOne, boolean tabTwo) {
	// mTabPosition.setSelected(tabOne);
	// mTabInvitation.setSelected(tabTwo);
	// }

	public void setTextEnabled(boolean enabled, int currentIndex) {
		if (mCurrentIndex == currentIndex) {
			mTextClear.setEnabled(enabled);
		}
	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

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

	@Override
	public void onButtonChecked(int index) {
		mCurrentIndex = index;
		if (index == 0) {
			if (null != mRefreshListenerPosition)
				mRefreshListenerPosition.onCheckClearBtnCanClick();
			return;
		}
		if (index == 1) {
			if (null != mRefreshListenerIvitation)
				mRefreshListenerIvitation.onCheckClearBtnCanClick();
			return;
		}
	}
}

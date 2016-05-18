package com.shixi.gaodun.view.activity.enterprise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.ScrollViewListener;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CompanyBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ObservableScrollView;
import com.umeng.analytics.MobclickAgent;

/**
 * 企业详情
 * 
 * @author ronger
 * @date:2015-12-24 上午9:27:10
 */
public class EnterpriseDetailActivity extends TitleBaseActivity implements
		ScrollViewListener {
	private String companyId;
	private ImageView mCompanyLogo;
	private TextView mCompanyNameText, mCompanyAddressText, mCompanyScaleText,
			mCompanyIndustryText;
	private LinearLayout mLeftTabLayout, mRightTabLayout;
	private TextView mLeftTabText, mRightTabText;
	private ObservableScrollView mObservableScrollView;
	private int mAddressPostionY;
	private List<Fragment> mFraments;
	private CompanyBean mCompanyBean;
	private FragmentManager mFragmentManager;
	private int mWhichActivity;// 1 正常跳转 2 是从猎头页面跳转过来的
	private TableRow mTableRow;

	public static void startAction(Activity context, String companyId,
			int whichActivity) {
		Intent intent = new Intent(context, EnterpriseDetailActivity.class);
		intent.putExtra("companyid", companyId);
		intent.putExtra("whichActivity", whichActivity);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = true;
		if (mWhichActivity == 1) {
			setContentView(R.layout.company_detail_layout);
		} else if (mWhichActivity == 2) {
			setContentView(R.layout.company_detail_layout2);
		}
		mFragmentManager = getSupportFragmentManager();
		setRequestParamsPre(TAG);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("企业详情");
		int textWidth = ActivityUtils.getScreenWidth()
				- ActivityUtils.dip2px(this, 100);
		mTitleName.setMaxWidth(textWidth);
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mObservableScrollView = (ObservableScrollView) findViewById(R.id.company_scrollView);
		mObservableScrollView.setScrollViewListener(this);
		mCompanyNameText = (TextView) findViewById(R.id.tv_commpany_name);
		mCompanyAddressText = (TextView) findViewById(R.id.tv_address);
		mCompanyScaleText = (TextView) findViewById(R.id.tv_scale);
		mCompanyIndustryText = (TextView) findViewById(R.id.tv_industry);
		mCompanyLogo = (ImageView) findViewById(R.id.iv_commpany_image);
		mLeftTabLayout = (LinearLayout) findViewById(R.id.ll_company_introduce);
		mRightTabLayout = (LinearLayout) findViewById(R.id.ll_company_employment);
		mLeftTabText = (TextView) findViewById(R.id.text_company_introduce);
		mRightTabText = (TextView) findViewById(R.id.text_company_employment);

		if (mWhichActivity == 1) {// 需要展示tab
			mLeftTabText.setText("公司介绍");
			mRightTabText.setText("招聘职位");
			mLeftTabLayout.setOnClickListener(this);
			mRightTabLayout.setOnClickListener(this);
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mAddressPostionY = mCompanyAddressText.getTop();
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("id", companyId);
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETCOMPANYDETAIL_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			isFirstJoin = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			dissMissProgress();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setNolmalShow();
			getCompanyDetailSuccess(httpRes.getReturnData());
			initFragments();
			setCompanyDetailShow();
		} catch (Exception e) {
			dissMissProgress();
			setDebugLog(e);
			exceptionToast();
		}
	}

	public void initFragments() {
		mFraments = new ArrayList<Fragment>(2);
		Bundle bundle = new Bundle();
		bundle.putString("introduce", mCompanyBean.getDescription());
		bundle.putString("companyid", companyId);
		EnterpriseIntroduceFragment introduceFragment = new EnterpriseIntroduceFragment();
		introduceFragment.setArguments(bundle);
		mFraments.add(introduceFragment);
		EnterpriseJobVacancyFrament positionFragment = new EnterpriseJobVacancyFrament();
		positionFragment.setArguments(bundle);
		mFraments.add(positionFragment);
		initFragment();
	}

	public void initFragment() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.replace(R.id.position_layout, mFraments.get(0));
		ft.commit();
	}

	public void getCompanyDetailSuccess(String resultData) throws Exception {
		mCompanyBean = TransFormModel.getResponseResultObject(resultData,
				CompanyBean.class);
	}

	public void setCompanyDetailShow() {
		setTextShow(mCompanyNameText, mCompanyBean.getFull_name(), "");
		setTextShow(mCompanyAddressText, mCompanyBean.getCity_name(), "");
		setTextShow(mCompanyScaleText, mCompanyBean.getScale_title(), "");
		setTextShow(mCompanyIndustryText, mCompanyBean.getIndustry_title(), "");
		BaseApplication.mApp.setRoundedImageByUrl(mCompanyLogo,
				mCompanyBean.getLogo(), R.drawable.default_image_icon, 5);
	}

	public void setTextShow(TextView textView, String str, String defaultStr) {
		textView.setText(StringUtils.isEmpty(str) ? defaultStr : str);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back)
			finish();
		if (v.getId() == R.id.ll_company_introduce) {
			changeTabShow(0);
			return;
		}
		if (v.getId() == R.id.ll_company_employment) {
			changeTabShow(1);
			return;
		}
		if (v.getId() == R.id.button_again) {
			setRetryRequest();
		}

	}

	/**
	 * 更新显示
	 * 
	 * @param index
	 */
	public void changeTabShow(int index) {
		if (index == 0) {
			mLeftTabText.setTextColor(getResources().getColor(R.color.main_bg));
			mRightTabText.setTextColor(getResources().getColor(
					R.color.form_value_color));
			mLeftTabLayout
					.setBackgroundResource(R.drawable.companey_detail_top_left);
			mRightTabLayout.setBackgroundColor(Color.TRANSPARENT);
		}
		if (index == 1) {
			mLeftTabText.setTextColor(getResources().getColor(
					R.color.form_value_color));
			mRightTabText
					.setTextColor(getResources().getColor(R.color.main_bg));
			mLeftTabLayout.setBackgroundColor(Color.TRANSPARENT);
			mRightTabLayout
					.setBackgroundResource(R.drawable.company_detail_top_right_bg);
		}
		changeFragment(index);
		mObservableScrollView.smoothScrollTo(0, 0);
		// if (index == 1) {
		// mObservableScrollView.smoothScrollTo(0, 0);
		// }
	}

	private void changeFragment(int currentIndex) {
		for (int i = 0; i < mFraments.size(); i++) {
			// 当前页面,若存在，则直接显示，若不存在，则创建
			if (i == currentIndex) {
				Fragment fragment = mFraments.get(i);
				FragmentTransaction ft = mFragmentManager.beginTransaction();
				fragment.onPause(); // 暂停当前tab
				// 若已添加则直接重新启动，若未添加则添加
				if (fragment.isAdded()) {
					fragment.onResume();// 启动目标tab的onresume
				} else {
					ft.add(R.id.position_layout, fragment);
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
		for (int i = 0; i < mFraments.size(); i++) {
			Fragment fragment = mFraments.get(i);
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			if (idx == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commit();
		}
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		if (y >= mAddressPostionY) {
			mTitleName.setText(mCompanyBean.getFull_name());
		} else {
			mTitleName.setText("企业详情");
		}
	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	@Override
	protected void getIntentData() {
		companyId = getIntent().getStringExtra("companyid");
		mWhichActivity = getIntent().getIntExtra("whichActivity", 1);
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
		MobclickAgent.onPageStart("companyDetail"); // 公司详情：companyDetail
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("companyDetail");
		MobclickAgent.onPause(this);
	}
}

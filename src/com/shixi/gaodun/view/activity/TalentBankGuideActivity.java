package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout.Alignment;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.VerticalViewPager;
import com.umeng.analytics.MobclickAgent;

/**
 * 人才库介绍页
 * 
 * @author guolinyao
 * @date 2016年5月3日 上午8:46:47
 */
public class TalentBankGuideActivity extends BaseMainActivity implements
		OnPageChangeListener {

	private Button mBtGuide;
	// 初始化背景图片
	int[] ICON_RES = new int[] { R.drawable.hr_guide1, R.drawable.hr_guide2,
			R.drawable.hr_guide3, R.drawable.hr_guide4, R.drawable.hr_guide5 };
	private List<RelativeLayout> mResList;
	private VerticalViewPager mVerticalViewPager;
	private int isFirst = 2;// 是否是第一次进入 1 第一次 2 不是第一次
	private int isComplete = 1;// 是否已完成人才库填写 1 未完成 2 已完成
	private boolean isLastPager;// 是否滑到最后一张
	private String mMobile;// 手机号
	private static final float MIN_SCALE = 0.75f;
	private static final float MIN_ALPHA = 0.75f;

	public static void startAction(Activity context, int isFirst) {
		Intent intent = new Intent(context, TalentBankGuideActivity.class);
		intent.putExtra("isFirst", isFirst);
		// 如果activity在task存在，将Activity之上的所有Activity结束掉
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		// isFirst = getIntent().getIntExtra("isFirst", 1);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		setContentView(R.layout.activity_hr_source_guide);
		initview();
		setRequestParamsPre(TAG);
		initListener();
	}

	private void initview() {
		TextView titlename = (TextView) findViewById(R.id.tv_title_bar_titlename);
		titlename.setText("人才库");
		ImageView backIcon = (ImageView) findViewById(R.id.iv_title_bar_icon);
		backIcon.setImageResource(R.drawable.icon_back);
		// Set up the pager
		mVerticalViewPager = (VerticalViewPager) findViewById(R.id.pager);
	}

	private void initListener() {
		mVerticalViewPager.setOnPageChangeListener(this);
	}

	private void initData() {
		// 初始化背景的图片
		mResList = new ArrayList<RelativeLayout>();
		// 迭代存放当前的背景图片
		for (int i = 0; i < ICON_RES.length; i++) {
			RelativeLayout relativeLayout = new RelativeLayout(this);
			ImageView imageView = new ImageView(getApplicationContext());
			imageView.setBackgroundResource(ICON_RES[i]);
			relativeLayout.addView(imageView);
			if (i == (ICON_RES.length - 1)) {
				ImageButton button = new ImageButton(this);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (isComplete == 2) {// 已完成人才库
						 MyTalentBankPreviewActivity.startAction(
						 TalentBankGuideActivity.this, 0);// 跳转到预览人才库
//							TalentBankEditActivity.startAction(
//									TalentBankGuideActivity.this, mMobile);
						} else {
							TalentBankEditActivity.startAction(
									TalentBankGuideActivity.this, mMobile);
						}
					}
				});
				if (isComplete == 2) {// isFirst == 2 && 不是第一次进入且已完成人才库
					button.setBackgroundResource(R.drawable.check_my_hrsource);
				} else {
					button.setBackgroundResource(R.drawable.add2hrsource);
				}

				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.bottomMargin = ActivityUtils.dip2px(this, 40f);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
				relativeLayout.addView(button, lp);// 如果是最后一张图片
			}
			mResList.add(relativeLayout);
		}

		MyPagerAdapter adapter = new MyPagerAdapter();
		mVerticalViewPager.setAdapter(adapter);
		// mVerticalViewPager.setPageMargin(ActivityUtils.dip2px(this, 10));
		// 设置viewpager动画
		// mVerticalViewPager.setPageTransformer(true,
		// new DepthPageTransformer());

		// mVerticalViewPager.setPageTransformer(true,
		// new ViewPager.PageTransformer() {
		// @SuppressLint("NewApi")
		// @Override
		// public void transformPage(View view, float position) {
		// int pageWidth = view.getWidth();
		// int pageHeight = view.getHeight();
		//
		// if (position < -1) { // [-Infinity,-1)
		// // This page is way off-screen to the left.
		// view.setAlpha(0);
		//
		// } else if (position <= 1) { // [-1,1]
		// // Modify the default slide transition to shrink the
		// // page as well
		// float scaleFactor = Math.max(MIN_SCALE,
		// 1 - Math.abs(position));
		// float vertMargin = pageHeight * (1 - scaleFactor)
		// / 2;
		// float horzMargin = pageWidth * (1 - scaleFactor)
		// / 2;
		// if (position < 0) {
		// view.setTranslationY(vertMargin - horzMargin
		// / 2);
		// } else {
		// view.setTranslationY(-vertMargin + horzMargin
		// / 2);
		// }
		//
		// // Scale the page down (between MIN_SCALE and 1)
		// view.setScaleX(scaleFactor);
		// view.setScaleY(scaleFactor);
		//
		// // Fade the page relative to its size.
		// view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
		// / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
		//
		// } else { // (1,+Infinity]
		// // This page is way off-screen to the right.
		// view.setAlpha(0);
		// }
		// }
		// });

	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// if (isLastPager) {
		// mBtGuide.setVisibility(View.VISIBLE);
		// } else {
		// mBtGuide.setVisibility(View.INVISIBLE);
		// }
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.TALENT_STATUS, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			dissMissProgress();
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			getTalentStatusSuccess(httpRes.getReturnData());
			initData();
		} catch (Exception e) {
			setDebugLog(e);
			dissMissProgress();
			exceptionToast();
		}

	}

	/**
	 * 获取人才库完成状态
	 * 
	 * @param returnData
	 */
	private void getTalentStatusSuccess(String returnData) {
		try {
			JSONObject jsonObject = new JSONObject(returnData);
			if (jsonObject.has("status")) {
				isComplete = Integer.parseInt(jsonObject.getString("status"));
			}
			if (jsonObject.has("mobile")) {
				mMobile = jsonObject.getString("mobile");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	/**
	 * 当某个page被选择时
	 */
	@Override
	public void onPageSelected(int position) {
		// if (position == mResList.size() - 1) {
		// // 当滑动到最后一个页面时 显示立即进入按钮
		// isLastPager = true;
		// // mBtGuide.setVisibility(View.VISIBLE);
		// } else {
		// isLastPager = false;
		// mBtGuide.setVisibility(View.INVISIBLE);
		// }
	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 移除对应位置的viewpager中的view
			container.removeView(mResList.get(position));
			// mViewPager.removeViewChild(position);
		}

		@Override
		public Object instantiateItem(View container, int position) {
			// 往viewpager中添加view
			((VerticalViewPager) container).addView(mResList.get(position));
			// mViewPager.addViewChild(position, mResList.get(position));

			return mResList.get(position);
		}

		@Override
		public int getCount() {
			return mResList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return object == view;
		}

	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		setRequestParamsPre(TAG);
		MobclickAgent.onPageStart("TalentBankGuideActivity"); //
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("TalentBankGuideActivity");
		MobclickAgent.onPause(this);
	}

}

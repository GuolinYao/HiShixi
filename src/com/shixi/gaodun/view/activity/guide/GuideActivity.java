package com.shixi.gaodun.view.activity.guide;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.view.DepthPageTransformer;
import com.shixi.gaodun.view.activity.MainActivity;
import com.shixi.gaodun.view.activity.SelectPresentAddressActivity;
import com.shixi.gaodun.view.activity.WelcomeActivity;

/**
 * @author guolinyao
 * @date 2016年2月23日 下午2:07:14
 */
public class GuideActivity extends Activity implements OnPageChangeListener,
		OnClickListener {

	// private ViewPagerTransformer mViewPager;
	private ViewPager mViewPager;
	private Button mBtGuide;
//	private LinearLayout mAllPoint;
//	private View mPoint;
//	private int mWidth;// 两个点之间的距离
	// 初始化背景图片
	int[] ICON_RES = new int[] { R.drawable.guide_1, R.drawable.guide_2,
			R.drawable.guide_3 };
	private List<ImageView> mResList;

	public static void startAction(BaseMainActivity context) {
		Intent intent = new Intent(context, GuideActivity.class);
//		context.setMove(5);
		context.startActivity(intent);
//		context.setMove(1);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
		initData();
		initListener();
	}

	private void initListener() {
		mViewPager.setOnPageChangeListener(this);
		mBtGuide.setOnClickListener(this);
	}

	private void initData() {
		// 初始化背景的图片
		mResList = new ArrayList<ImageView>();
		// 迭代存放当前的背景图片
		for (int i = 0; i < ICON_RES.length; i++) {
			ImageView imageView = new ImageView(getApplicationContext());
			imageView.setBackgroundResource(ICON_RES[i]);
			mResList.add(imageView);

			// 初始化点
			View view = new View(getApplicationContext());
			// 初始化点的宽和高
			// 通过代码设置布局参数的时候需要加上前缀 否则报错
			int width = ActivityUtils.dip2px(getApplicationContext(), 10);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					width, width);
			// 判断是否是第零个点
			if (i != 0) {
				// 设置两点之间的间距
				params.leftMargin = width;
			}

			view.setLayoutParams(params);
			// 设置背景为普通的点
			view.setBackgroundResource(R.drawable.dot_normal);
			// 添加点到线性布局
//			mAllPoint.addView(view);

		}

		MyPagerAdapter adapter = new MyPagerAdapter();

		mViewPager.setAdapter(adapter);
		// 设置viewpager动画
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		// 获得左边到右边点的宽度 oncreate方法中没有测量完毕，所以需要发消息 延迟加载
//		mPoint.post(new Runnable() {
//
//			@Override
//			public void run() {
//				// 获取到2个孩子的间距
//				// 获取2个点之间的位置
//
//				mWidth = mAllPoint.getChildAt(1).getLeft()
//						- mAllPoint.getChildAt(0).getLeft();
//			}
//		});

	}

	private void initView() {
		setContentView(R.layout.activity_guide);
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		// 立即进入的Button
		mBtGuide = (Button) findViewById(R.id.button);
		// 点的集合

		// mAllPoint = (LinearLayout) findViewById(R.id.ll_all_point);

		// 选中的点
		// mPoint = findViewById(R.id.point);

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
			((ViewPager) container).addView(mResList.get(position));
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
	 * 滑动时 时时调用 左右滑动的方法 position:位置 0 positionOffset:比例 positionOffsetPixels:像素
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

//		int newPosition = (int) (mWidth * (position + positionOffset));
		// 获得mPoint的参数
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPoint
//				.getLayoutParams();
		// 设置移动的点的左边位置
//		params.leftMargin = newPosition;
//		mPoint.setLayoutParams(params);
	}

	/**
	 * 当某个page被选择时
	 */
	@Override
	public void onPageSelected(int position) {
		if (position == mResList.size() - 1) {
			// 当滑动到最后一个页面时 显示立即进入按钮
			mBtGuide.setVisibility(View.VISIBLE);
		} else {
			mBtGuide.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(GuideActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

}

package com.shixi.gaodun.view.activity.invitation;

import java.util.ArrayList; 
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.model.domain.ImageInfoBean;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.view.zoom.PhotoView;
import com.shixi.gaodun.view.zoom.ViewPagerFixed;

/**
 * 这个是用于进行图片预览（图片放大查看）时的界面
 */
public class GalleryActivity extends Activity {

	// 获取前一个activity传过来的position
	private int position;
	// 当前的位置
	private int location = 0;

	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;
	private ArrayList<ImageInfoBean> images = new ArrayList<ImageInfoBean>();

	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	public List<String> del = new ArrayList<String>();

	private Context mContext;

	RelativeLayout photo_relativeLayout;

	public static void startAction(Activity context,
			ArrayList<ImageInfoBean> images, int position) {
		Intent intent = new Intent(context, GalleryActivity.class);
		intent.putExtra("images", images);
		intent.putExtra("position", position);
		context.startActivity(intent);
	}

	@SuppressWarnings("unchecked")
	protected void getIntentData() {
		position = getIntent().getIntExtra("position", 0);
		images = (ArrayList<ImageInfoBean>) getIntent().getSerializableExtra(
				"images");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		setContentView(R.layout.activity_gallery_layout);
		mContext = this;
		pager = (ViewPagerFixed) findViewById(R.id.vp_gallery);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < images.size(); i++) {
			initListViews(BaseApplication.mApp.getBitmapFromURL(images.get(i)
					.getValue()));
		}
		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin(ActivityUtils.dip2px(mContext, 10));
		pager.setCurrentItem(position);
	}

	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			location = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};

	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;

		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
}

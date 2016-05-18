package com.shixi.gaodun.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.CheckBoxSelectedBean;
import com.shixi.gaodun.model.domain.ImageItem;
import com.shixi.gaodun.model.domain.SelectedImageBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.BitmapCache;
import com.shixi.gaodun.model.utils.BitmapCache.ImageCallback;
import com.shixi.gaodun.model.utils.ListUitls;

/**
 * 相片列表，第一个为拍照入口
 * 
 * @author ronger
 * @date:2015-11-24 下午5:41:54
 */
public class PictureAdapter extends CommonAdapter<ImageItem> {
	protected static final String TAG = "PictureAdapter";
	private ArrayList<SelectedImageBean> mSelectedPicture;
	private int MAX_NUM;
	private TextCallback mTextCallback;// 已选中图片个数的显示回调
	private int mImageWidth;
	private BitmapCache cache;
	private DisplayMetrics dm;

	// private ArrayList<String> mDefaultPicture;// 第一次进来时已经选中了的
	// private int mDefaultCount = -1;// 第一次进来时已经选中的图片的个数
	// private int mCurrentIndex = 1;

	public PictureAdapter(Activity context, ArrayList<ImageItem> mDatas,
			int itemLayoutId, ArrayList<SelectedImageBean> selectedPicture,
			int MAX_NUM, int imageWidth) {
		super(context, mDatas, itemLayoutId);
		this.mSelectedPicture = selectedPicture;
		// this.mDefaultPicture = mSelectedPicture;
		// if (null != mDefaultPicture && !mDefaultPicture.isEmpty()) {
		// mDefaultCount = mDefaultPicture.size();
		// }
		this.MAX_NUM = MAX_NUM;
		this.mImageWidth = imageWidth;
		cache = new BitmapCache();
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
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

	public void setTextCallback(TextCallback textCallback) {
		this.mTextCallback = textCallback;
	}

	@Override
	public int getCount() {
		return mLists.size() + 1;// 需要加上拍照入口
	}

	@Override
	public ImageItem getItem(int position) {
		if (position == 0) {
			return null;
		}
		return mLists.get(position - 1);
	}

	@Override
	public void convert(final ViewHolder helper, ImageItem item, int position) {
		// ImageView imageView = helper.getView(R.id.iv);
		LayoutParams layoutParams = new LayoutParams(mImageWidth, mImageWidth);
		// imageView.setLayoutParams(layoutParams);
		// helper.getView(R.id.image_item).setBackground(background);
		if (position == 0) {
			// helper.setImageResource(R.id.iv,
			// R.drawable.pickphotos_to_camera_normal);
			helper.setImageResource(R.id.iv,
					R.drawable.pickphotos_to_camera_normal, layoutParams);
			helper.setButtonVisibility(R.id.check, View.INVISIBLE);
		} else {
			final ImageItem imageItem = item;
			// helper.setImageView(R.id.iv, "file://" + item.imagePath,
			// R.drawable.default_image_icon, layoutParams);
			ImageView imageview = (ImageView) helper.getView(R.id.iv);
			imageview.setTag(item.imagePath);
			cache.displayBmp(imageview, item.thumbnailPath, item.imagePath,
					callback);
			// boolean isSelected = mSelectedPicture.contains(item.path);
			SelectedImageBean bean = ListUitls.getImageIsSelected(
					mSelectedPicture, item.imagePath);
			boolean isSelected = bean.isSelected();
			Button checkBox = helper.getView(R.id.check);
			if (isSelected) {
				checkBox.setText(String.valueOf(bean.getSelectedPosition()));
				// // 初始化时
				// if (mDefaultCount > 0 && mCurrentIndex <= mDefaultCount) {
				// checkBox.setText(String.valueOf(mCurrentIndex));
				// mCurrentIndex++;
				// } else {
				// mDefaultCount = -1;
				// mCurrentIndex = 1;
				// }
				// 设置背景颜色 蓝色
				helper.getView(R.id.image_item).setBackgroundColor(
						Color.parseColor("#00ABF0"));
			} else {
				checkBox.setText("");
				helper.getView(R.id.image_item).setBackgroundColor(
						Color.parseColor("#ffffff"));
			}
			checkBox.setVisibility(View.VISIBLE);
			checkBox.setSelected(isSelected);
			checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!v.isSelected()
							&& mSelectedPicture.size() + 1 > MAX_NUM) {
						Toast.makeText(mContext, "最多选择" + MAX_NUM + "张",
								Toast.LENGTH_SHORT).show();

						return;
					}

					// 如果已经选择了四张照片 那么其他的照片变为不可点击,同时变灰色
					// if (mSelectedPicture.size() + 1 > MAX_NUM) {
					// FrameLayout mFrameLayout = helper
					// .getView(R.id.image_item);
					// TextView textView = new TextView(mContext);
					// textView.setBackgroundColor(Color
					// .parseColor("#88ff0000"));
					// mFrameLayout.addView(textView);
					// }

					CheckBoxSelectedBean result = ListUitls
							.getSelectedIsContains(mSelectedPicture,
									imageItem.imagePath);
					mSelectedPicture = result.getSelectedList();
					boolean isSelected = result.isSelected();
					v.setSelected(isSelected);
					notifyDataSetChanged();
					// if (mSelectedPicture.contains(imageItem.path)) {// 取消选中
					// mSelectedPicture.remove(imageItem.path);
					// } else {
					// mSelectedPicture.add(imageItem.path);
					// }
					mTextCallback.onTextListen(mSelectedPicture.size());
					// v.setSelected(mSelectedPicture.contains(imageItem.path));
				}
			});
		}

	}

	public ArrayList<SelectedImageBean> getmSelectedPicture() {
		return mSelectedPicture;
	}

	public void setmSelectedPicture(
			ArrayList<SelectedImageBean> mSelectedPicture) {
		this.mSelectedPicture = mSelectedPicture;
	}

	public static interface TextCallback {
		public void onTextListen(int count);
	}

}

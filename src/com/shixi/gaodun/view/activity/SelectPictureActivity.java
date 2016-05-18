package com.shixi.gaodun.view.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.FolderAdapter;
import com.shixi.gaodun.adapter.PictureAdapter;
import com.shixi.gaodun.adapter.PictureAdapter.TextCallback;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.domain.ImageFloder;
import com.shixi.gaodun.model.domain.ImageItem;
import com.shixi.gaodun.model.domain.SelectedImageBean;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.AlbumHelper;
import com.shixi.gaodun.model.utils.BitmapModel;
import com.shixi.gaodun.model.utils.FileModel;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.CustomDialog;

/**
 * 选择相册相片
 * 
 * @author ronger
 * @date:2015-11-24 下午5:15:01
 */
public class SelectPictureActivity extends TitleBaseActivity implements
		TextCallback {
	private AlbumHelper helper;
	/**
	 * 最多选择图片的个数
	 */
	private static int MAX_NUM = 4;
	public static Bitmap bimap;
	public static final String INTENT_MAX_NUM = "intent_max_num";
	public static final String INTENT_SELECTED_PICTURE = "intent_selected_picture";
	private GridView gridview;
	private FrameLayout mFilterLayout;
	private PictureAdapter adapter;
	private ArrayList<ImageFloder> mDirPaths = new ArrayList<ImageFloder>();
	private TextView mSelectNumber;
	private Button btn_select;
	private ListView listview;
	private FolderAdapter folderAdapter;
	private ImageFloder mImageAll, mCurrentImageFolder;

	/**
	 * 已选择的图片
	 */
	private ArrayList<SelectedImageBean> selectedPicture = new ArrayList<SelectedImageBean>();
	private String cameraPath = null;

	private String mCameraName;
	private String mCameraPhotoPath;
	private int mImageWidth;
	private ArrayList<ImageItem> dataList;

	public static void startAction(Activity context, int requestCode,
			int maxnum, ArrayList<SelectedImageBean> selectImages) {
		Intent intent = new Intent(context, SelectPictureActivity.class);
		intent.putExtra(INTENT_MAX_NUM, maxnum);
		intent.putExtra("selectImages", selectImages);
		context.startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		MAX_NUM = getIntent().getIntExtra(INTENT_MAX_NUM, 4);
		selectedPicture = (ArrayList<SelectedImageBean>) getIntent()
				.getSerializableExtra("selectImages");
		if (null == selectedPicture)
			selectedPicture = new ArrayList<SelectedImageBean>(0);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = false;
		getIntentData();
		setContentView(R.layout.activity_select_picture);
		getAllImage();
	}

	/**
	 * 初始化控件，初次进来默认文件夹为所有文件夹
	 */
	@Override
	public void initView() {
		super.initView();
		mBackIcon.setOnClickListener(this);
		mTitleName.setText("相片");
		mImageAll = new ImageFloder();
		mImageAll.setDir("/所有图片");
		mCurrentImageFolder = new ImageFloder();
		mCurrentImageFolder = mImageAll;
		mDirPaths.add(mImageAll);
		mSelectNumber = (TextView) findViewById(R.id.text_phone_number);
		mSelectNumber.setText("完成" + selectedPicture.size() + "/" + MAX_NUM);
		mSelectNumber.setOnClickListener(this);
		btn_select = (Button) findViewById(R.id.btn_select);
		gridview = (GridView) findViewById(R.id.gridview);
		mImageWidth = (int) ((ActivityUtils.getScreenWidth() - ActivityUtils
				.dip2px(this, 6)) / 3.0);
		adapter = new PictureAdapter(this, mCurrentImageFolder.getImageList(),
				R.layout.grid_item_picture, selectedPicture, MAX_NUM,
				mImageWidth);
		adapter.setTextCallback(this);
		gridview.setAdapter(adapter);
		mFilterLayout = (FrameLayout) findViewById(R.id.filter_layout);
		listview = (ListView) findViewById(R.id.listview);
		// folderAdapter = new FolderAdapter(this, mDirPaths,
		// R.layout.list_dir_item, mCurrentImageFolder);
		//   listview.setAdapter(folderAdapter);
		setOnItemClick();
		mFilterLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				CustomDialog.hideListAnimation(mFilterLayout, 8);
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
			return;
		}
		if (v.getId() == R.id.text_phone_number) {
			selectedPicture = adapter.getmSelectedPicture();
			onComplete();
		}
	}

	public void setOnItemClick() {
		// 图片选中
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					goCamare();
				}
			}
		});
		// 相册item选中
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentImageFolder = mDirPaths.get(position);
				folderAdapter.setmCurrentImageFolder(mCurrentImageFolder);
				// hideListAnimation();
				CustomDialog.hideListAnimation(mFilterLayout, 8);
				adapter.notifyDataSetChanged();
				adapter = new PictureAdapter(SelectPictureActivity.this,
						mCurrentImageFolder.getImageList(),
						R.layout.grid_item_picture, selectedPicture, MAX_NUM,
						mImageWidth);
				gridview.setAdapter(adapter);
				btn_select.setText(mCurrentImageFolder.getBucketName() != null ? mCurrentImageFolder
						.getBucketName() : "所有照片");
			}
		});
	}

	/**
	 * 得到相册及图片
	 */
	public void getAllImage() {
		helper = AlbumHelper.getHelper();
		// helper.init(this, mImageAll, mCurrentImageFolder, mDirPaths);
		// helper.getThumbnail();
		// mDirPaths = helper.getmDirPaths();
		// mImageAll = helper.getImageAll();
		helper.init(this, mImageAll, mCurrentImageFolder, mDirPaths);
		mDirPaths = (ArrayList<ImageFloder>) helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for (int i = 1; i < mDirPaths.size(); i++) {
			dataList.addAll(mDirPaths.get(i).imageList);
		}
		mImageAll.imageList.addAll(dataList);
		// folderAdapter.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		helper.clearHelper();
	}

	/**
	 * 点击完成回到上一页面，并把已经选择的图片集合带回
	 */
	public void onComplete() {
		Intent intent = new Intent();
		intent.putExtra("selectedPicture", selectedPicture);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	/**
	 * 底部选择文件夹的点击事件
	 * 
	 * @param v
	 */
	public void select(View v) {
		if (mFilterLayout.getVisibility() == 0) {
			CustomDialog.hideListAnimation(mFilterLayout, 8);
		} else {
			folderAdapter = new FolderAdapter(this, mDirPaths,
					R.layout.list_dir_item, mCurrentImageFolder);
			listview.setAdapter(folderAdapter);

			mFilterLayout.setVisibility(View.VISIBLE);
			CustomDialog.showListAnimation(mFilterLayout);
			folderAdapter.notifyDataSetChanged();
		}
		// if (listview.getVisibility() == 0) {
		// hideListAnimation();
		// } else {
		// listview.setVisibility(0);
		// showListAnimation();
		// folderAdapter.notifyDataSetChanged();
		// }
	}

	// public void showListAnimation() {
	// TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 1f, 1,
	// 0f);
	// ta.setDuration(200);
	// listview.startAnimation(ta);
	// }

	// public void hideListAnimation() {
	// TranslateAnimation ta = new TranslateAnimation(1, 0f, 1, 0f, 1, 0f, 1,
	// 1f);
	// ta.setDuration(200);
	// mFilterLayout.startAnimation(animation);
	// //listview.startAnimation(ta);
	// ta.setAnimationListener(new AnimationListener() {
	// @Override
	// public void onAnimationStart(Animation animation) {
	// }
	//
	// @Override
	// public void onAnimationRepeat(Animation animation) {
	// }
	//
	// @Override
	// public void onAnimationEnd(Animation animation) {
	// listview.setVisibility(8);
	// }
	// });
	// }

	/**
	 * 使用相机拍照
	 * 
	 * @version 1.0
	 * @author zyh
	 */
	protected void goCamare() {
		if (selectedPicture.size() + 1 > MAX_NUM) {
			Toast.makeText(this, "最多选择" + MAX_NUM + "张", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		String state = Environment.getExternalStorageState();
		mCameraName = "gaodunshixi/" + System.currentTimeMillis() + ".jpg";
		/**
		 * 判断sd卡是否存在，并且具有读写权限
		 */
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent toCarmeraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File PHOTO_DIR = Environment.getExternalStorageDirectory();
			File file = new File(PHOTO_DIR, mCameraName);
			FileModel.deleteDir(mCameraName);
			if (!file.exists()) {
				file.getParentFile().mkdir();
			}
			Uri imageUri = Uri.fromFile(file);
			mCameraPhotoPath = file.getAbsolutePath();
			toCarmeraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(toCarmeraIntent, Finals.TOCAMERA_REQUEST);
		} else {
			ToastUtils.showToastInCenter("请确认插入SD卡");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		// 相机返回
		if (requestCode == Finals.TOCAMERA_REQUEST) {
			if (StringUtils.isEmpty(mCameraPhotoPath)) {
				return;
			}
			if (null == selectedPicture) {
				selectedPicture = new ArrayList<SelectedImageBean>(0);
			}
			selectedPicture.add(new SelectedImageBean(mCameraPhotoPath,
					selectedPicture.size() + 1, true));
			// 提示更新系统相册
			changeSystemPick();
			onComplete();
			try {
				FileModel.deleteDir(Contants.ImageLoadPath);
				BitmapModel.saveBitmap(selectedPicture, Contants.ImageLoadPath,
						"headpic");
			} catch (IOException e) {
				setDebugLog(e);
			}
		}
	}

	/**
	 * 更新系统相册
	 */
	public void changeSystemPick() {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File PHOTO_DIR = Environment.getExternalStorageDirectory();
		File file = new File(PHOTO_DIR, mCameraName);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		sendBroadcast(intent);
	}

	@Override
	public void onTextListen(int count) {
		mSelectNumber.setEnabled(count > 0);
		mSelectNumber.setText("完成" + count + "/" + MAX_NUM);
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}

}

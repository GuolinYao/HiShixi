package com.shixi.gaodun.view.activity.invitation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.inf.MaxLengthWatcher;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.BitMapUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.CutPhotoUtils;
import com.shixi.gaodun.model.utils.HttpUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.ClearEditTextView;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.MakeCircleImg;
import com.umeng.analytics.MobclickAgent;

/**
 * 编辑资料
 * 
 * @author ronger
 * @date:2015-11-27 上午9:45:19
 */
@SuppressLint("HandlerLeak")
public class EditProfileActivity extends TitleBaseActivity implements
		OnAlertSelectId {
	private String mOldName;// 原昵称，没有设置过时为空
	private ClearEditTextView mEditNickName;
	private ImageView mImage;
	private Dialog mDialog;
	private File mImageFile = null;
	private boolean isSaveNick = true;
	private String mImagePath;
	private List<BasicNameValuePair> mBasicNameValuePairs;
	private int sourceType;

	/**
	 * sourceType:来源0 其他页过来1编辑页过来
	 * 
	 * @param context
	 * @param requestCode
	 * @param name
	 * @param photoUrl
	 * @param sourceType
	 */
	public static void startAction(Activity context, int requestCode,
			String name, String photoUrl, int sourceType) {
		Intent intent = new Intent(context, EditProfileActivity.class);
		intent.putExtra("name", name);
		intent.putExtra("url", photoUrl);
		intent.putExtra("sourceType", sourceType);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		mOldName = getIntent().getStringExtra("name");
		mImagePath = getIntent().getStringExtra("url");
		sourceType = getIntent().getIntExtra("sourceType", 0);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.edit_profile_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("编辑资料");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setOnClickListener(this);
		mOtherName.setEnabled(StringUtils.isEmpty(mOldName) ? false : true);
		mOtherName.setText("保存");
		mEditNickName = (ClearEditTextView) findViewById(R.id.edit_nickname);
		mEditNickName.setText(StringUtils.isEmpty(mOldName) ? "" : mOldName);
		mImage = (ImageView) findViewById(R.id.image_topic_photo);
		findViewById(R.id.fl_change_image).setOnClickListener(this);
		if (StringUtils.isNotEmpty(mImagePath)) {
			BaseApplication.mApp.setCircleImageByUrl(mImage, mImagePath,
					R.drawable.default_touxiang_xiaoerhei);
			// setImageByUrl(mImage, mImagePath,
			// R.drawable.default_touxiang_xiaoerhei, ActivityUtils.dip2px(this,
			// 81));
		}
		mEditNickName.addTextChangedListener(new MaxLengthWatcher(10,
				mEditNickName, "限10字内") {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				super.onTextChanged(s, start, before, count);
				Editable editable = mEditNickName.getText();
				if (editable.length() > 0) {
					mOtherName.setEnabled(true);
				} else {
					mOtherName.setEnabled(false);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			if (StringUtils.isEmpty(mEditNickName.getText().toString())
					|| (null != mOldName && mOldName.equals(mEditNickName
							.getText().toString()))) {
				finish();
				break;
			}
			mDialog = CustomDialog.CancelAlertToDialog(
					"放弃编辑将失去已填写的内容\n确认要放弃吗？", "确定", "取消", this, this);
			break;
		case R.id.tv_confrim_btn:
			mDialog.dismiss();
			finish();
			break;
		case R.id.tv_cancel_btn:
			mDialog.dismiss();
			break;
		// 更换图片
		case R.id.fl_change_image:
			ActionSheetDialog.showAlert(this, this, "拍照", "手机相册");
			break;
		case R.id.tv_title_bar_otherbtn:
			String name = mEditNickName.getText().toString();
			if (StringUtils.isNotEmpty(mOldName) && mOldName.equals(name)) {
				finish();
			}
			isSaveNick = true;
			setRequestParamsPre(TAG);
			break;
		default:
			break;
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			myProgressDialog.dismiss();
			if (response.getInt("returnCode") != 0) {
				ToastUtils.showToastInCenter(response.getString("returnDesc"));
				return;
			}
			if (sourceType == 1) {
				Intent intent = new Intent();
				intent.putExtra("nickName", mEditNickName.getText().toString());
				setResult(RESULT_OK, intent);
				sendBroadcast();
			} else {
				EditActivity.startAction(this);
			}
			finish();
		} catch (Exception e) {
			setDebugLog(e);
		}
	}

	@Override
	public void onClick(int whichButton) {
		// 拍照
		if (whichButton == 1) {
			Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 下面这句指定调用相机拍照后的照片存储的路径
			intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), "photo.jpg")));
			startActivityForResult(intentCamera, Finals.TOCAMERA_REQUEST);
			return;
		}
		// 从手机相册选择
		if (whichButton == 2) {
			Intent intentPick = new Intent(Intent.ACTION_PICK, null);
			intentPick.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intentPick, Finals.TOPICK_REQUEST);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		// 相册返回
		if (requestCode == Finals.TOPICK_REQUEST) {
			if (null == arg2.getData())
				return;
			startActivityForResult(
					CutPhotoUtils.startPhotoZoom(arg2.getData()),
					Finals.TOCUTPHOTO_REQUEST);
		}
		// 拍照返回
		if (requestCode == Finals.TOCAMERA_REQUEST) {
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/" + "photo.jpg");
			startActivityForResult(
					CutPhotoUtils.startPhotoZoom(Uri.fromFile(picture)),
					Finals.TOCUTPHOTO_REQUEST);
		}
		// 裁剪返回
		if (requestCode == Finals.TOCUTPHOTO_REQUEST) {
			if (arg2 != null) {
				setPicToView(arg2);
			}
		}
	}

	/*
	 * 裁剪图片返回后设置图片显示
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Bitmap cirbitmap = new MakeCircleImg().creatCircle(bitmap);
			// mImage.setImageResource(getResources().getColor(R.color.transparent));
			mImage.setImageBitmap(cirbitmap);
			mImageFile = BitMapUtils.writeToSDCard(bitmap, this);
			bitmap.recycle();
			isSaveNick = false;
			checkExitToken();
		}
	}

	/**
	 * 上传图片前检测是否存在token或者token是否有效
	 */
	public void checkExitToken() {
		setRequestParamsPre(TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (isSaveNick) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("ticket", CacheUtils.getToken(this));
			map.put("student_id", CacheUtils.getStudentId(this));
			map.put("nickname", mEditNickName.getText().toString());
//			if (StringUtils.isNotEmpty(mImagePath)) {
//				map.put("avatar", mImagePath);
//			}
			JsonObjectPostRequest request = new JsonObjectPostRequest(
					StringUtils.getCommonIP()
							+ GlobalContants.UPDATE_BASIINFO_URL, map,
					new RequestResponseLinstner(this), this);
			BaseApplication.mApp.addToRequestQueue(request, TAG);
			return;
		}

		//更新圈子头像
		mBasicNameValuePairs = new ArrayList<BasicNameValuePair>();
		mBasicNameValuePairs.add(new BasicNameValuePair("ticket", CacheUtils
				.getToken(EditProfileActivity.this)));
		mBasicNameValuePairs.add(new BasicNameValuePair("student_id",
				CacheUtils.getStudentId(EditProfileActivity.this)));
		mBasicNameValuePairs.add(new BasicNameValuePair("type", "2"));
		new UploadPhoto().execute();

	}

	/**
	 * 发布帖子
	 */
	class UploadPhoto extends AsyncTask<Integer, Integer, HttpRes> {
		@Override
		protected HttpRes doInBackground(Integer... params) {
			try {
				return HttpUtils.postSingleFile(StringUtils.getCommonIP()
						+ GlobalContants.UPLOAD_URL, mBasicNameValuePairs,
						mImageFile, "headpic");
			} catch (Exception e) {
				setDebugLog(e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(HttpRes result) {
			try {
				dissMissProgress();
				if (result.getReturnCode() != 0) {
					ToastUtils.showToastInCenter(result.getReturnDesc());
					return;
				}
				mImagePath = result.getReturnData();
				sendBroadcast();
			} catch (Exception e) {
				setDebugLog(e);
				exceptionToast();
			}
		}
	}

	@Override
	protected void setRetryRequest() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);

	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("editTopicNikName"); // 昵称编写
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("editTopicNikName");
		MobclickAgent.onPause(this);
	}

	// /**
	// * 圈子头像
	// *
	// * @param file
	// */
	// @SuppressWarnings("unused")
	// private void uploadPhoto(final File file) {
	// new Thread() {
	// @Override
	// public void run() {
	// super.run();
	// List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
	// params.add(new BasicNameValuePair("ticket",
	// CacheUtils.getToken(EditProfileActivity.this)));
	// params.add(new BasicNameValuePair("student_id",
	// CacheUtils.getStudentId(EditProfileActivity.this)));
	// params.add(new BasicNameValuePair("type", "1"));
	// try {
	// Message message = Message.obtain();
	// message.what = 0;
	// message.obj = HttpUtils.postSingleFile(StringUtils.getCommonIP() +
	// GlobalContants.UPLOAD_URL,
	// params, file, "headpic");
	// uploadPhotoHandler.sendMessage(message);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }.start();
	// }
	//
	// /**
	// * 上传图片完成
	// */
	// private Handler uploadPhotoHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// if (msg.what == 0) {
	// HttpRes message = (HttpRes) msg.obj;
	// myProgressDialog.dismiss();
	// try {
	// if (message.getReturnCode() != 0) {
	// ToastUtils.showToastInCenter(message.getReturnDesc());
	// return;
	// }
	// mImagePath = message.getReturnData();
	// CacheUtils.saveStudentImage(EditProfileActivity.this, mImagePath);
	// } catch (Exception e) {
	// setDebugLog(e);
	// }
	// }
	// }
	// };

}

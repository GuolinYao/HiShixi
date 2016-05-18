package com.shixi.gaodun.view.activity.invitation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.inf.MaxLengthWatcher;
import com.shixi.gaodun.model.MyTextWatcher;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.SelectedImageBean;
import com.shixi.gaodun.model.domain.TopicInfo;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.BitmapModel;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.shixi.gaodun.model.utils.FileModel;
import com.shixi.gaodun.model.utils.HttpUtils;
import com.shixi.gaodun.model.utils.ListUitls;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.CustomDialog;
import com.shixi.gaodun.view.FlowLayout;
import com.shixi.gaodun.view.PostKeyboard;
import com.shixi.gaodun.view.activity.SelectPictureActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 发帖、跟帖
 * 
 * @author ronger
 * @date:2015-11-24 上午11:10:19
 */
public class PostInvitationActivity extends BaseMainActivity implements
		OnAlertSelectId {
	private TopicInfo mTopicInfo;
	private int status;// 是否被禁言1 已被禁言 2 未被禁言
	private int type;// 帖子类型0楼主贴1跟帖
	private PostKeyboard mBottomView;
	private FlowLayout mFlowLayout;
	private EditText mEditTitle, mEditContent;
	private TextView mTextTitle, mTextPost;
	private TextView mTextNumberLimit;
	private Dialog mDialog;
	// 已选择的图片
	private ArrayList<SelectedImageBean> selectedPicture = new ArrayList<SelectedImageBean>(
			0);
	// 图片的宽高
	private int mImageWidth;
	// 需要上传的图片文件
	private File mPostFile;
	private List<BasicNameValuePair> mBasicNameValuePairs;
	private String mCameraName;
	private String mCameraPhotoPath;
	private InvitationInfoBean flowupTopicInfo;

	public static void startAction(BaseMainActivity context,
			TopicInfo topicInfo, int status, int type) {
		Intent intent = new Intent(context, PostInvitationActivity.class);
		intent.putExtra("topicinfo", topicInfo);
		intent.putExtra("status", status);
		intent.putExtra("type", type);
		context.setMove(3);
		context.startActivity(intent);
		context.setMove(1);
	}

	/**
	 * 跟帖
	 * 
	 * @param context
	 * @param topicInfo
	 * @param status
	 * @param type
	 */
	public static void startAction(BaseMainActivity context,
			InvitationInfoBean topicInfo, int status, int type) {
		Intent intent = new Intent(context, PostInvitationActivity.class);
		intent.putExtra("floowtopicinfo", topicInfo);
		intent.putExtra("status", status);
		intent.putExtra("type", type);
		context.setMove(3);
		context.startActivity(intent);
		context.setMove(1);
	}

	@Override
	protected void getIntentData() {
		Intent intent = getIntent();
		mTopicInfo = (TopicInfo) intent.getSerializableExtra("topicinfo");
		flowupTopicInfo = (InvitationInfoBean) intent
				.getSerializableExtra("floowtopicinfo");
		status = intent.getIntExtra("status", 2);
		type = intent.getIntExtra("type", 0);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.post_invitation_layout);
		initView();
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// 隐藏软键盘
		// imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
		// 显示软键盘
		// imm.showSoftInputFromInputMethod(mEditTitle.getWindowToken(), 0);
		// 启动Activity时弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mImageWidth = mFlowLayout.getWidth() / 4;
	}

	@Override
	public void initView() {
		super.initView();
		findViewById(R.id.iv_title_bar_left).setOnClickListener(this);
		mBottomView = (PostKeyboard) findViewById(R.id.layout_single_touch);
		mBottomView.setmActionDialogListener(this);
		mTextNumberLimit = mBottomView.getmTextNumber();
		mFlowLayout = (FlowLayout) findViewById(R.id.flow_invitation_image);
		mFlowLayout.setHorizontalSpacing(0);
		mTextTitle = (TextView) findViewById(R.id.tv_title_bar_title);
		mTextTitle.setText(type == 0 ? "发帖" : "跟帖");
		mTextPost = (TextView) findViewById(R.id.iv_title_bar_right);
		mTextPost.setOnClickListener(this);
		mEditTitle = (EditText) findViewById(R.id.edittext_invitation_title);
		// 楼主贴带标题，跟帖不带标题
		mEditTitle.setVisibility(type == 0 ? View.VISIBLE : View.GONE);
		mEditContent = (EditText) findViewById(R.id.edittext_invitation_content);
		mTextPost.setEnabled(false);
		mEditTitle.addTextChangedListener(new MyTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Editable editable = mEditTitle.getText();
				int len = editable.length();
				// 楼主贴
				if (type == 0) {
					if (len <= 0) {
						mTextPost.setEnabled(false);
					} else {
						mTextPost.setEnabled(true);
					}
				}
				setTextViewShow(30, mTextNumberLimit, mEditTitle, "限30字");
			}
		});
		mEditTitle.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mBottomView.setMoreBtnEnabled(false);
				} else {
					mBottomView.setMoreBtnEnabled(true);
				}
			}
		});
		// mEditContent.addTextChangedListener(new MaxLengthWatcher(800,
		// mEditContent, mTextNumberLimit));
		mEditContent.addTextChangedListener(new MaxLengthWatcher(800,
				mEditContent, mTextNumberLimit) {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 跟帖时内容或者图片必须有一个不为空
				if (type != 0) {
					setPostBtnCanClick();
				}
				super.onTextChanged(s, start, before, count);
			}
		});
	}

	/**
	 * 设置跟帖时发布按钮是否可点击
	 */
	public void setPostBtnCanClick() {
		String str = mEditContent.getText().toString();
		int len = str.length();
		if (null != selectedPicture && selectedPicture.size() > 0 || len > 0) {
			mTextPost.setEnabled(true);
		} else {
			mTextPost.setEnabled(false);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void finishTranstition() {
		overridePendingTransition(0, R.anim.slide_down_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_bar_right:
			setRequestParamsPre(TAG);
			break;
		case R.id.iv_title_bar_left:
			String title = mEditTitle.getText().toString();
			String content = mEditContent.getText().toString();
			if (StringUtils.isEmpty(title) && StringUtils.isEmpty(content)) {
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
		default:
			break;
		}
	}

	/**
	 * 获取发布楼主贴时的参数
	 */
	public void getLandlordInvitationParams() {
		mPostFile = FileModel.getPostFile(Contants.ImageLoadPath);
		mBasicNameValuePairs = new ArrayList<BasicNameValuePair>();
		mBasicNameValuePairs.add(new BasicNameValuePair("ticket", CacheUtils
				.getToken(PostInvitationActivity.this)));
		mBasicNameValuePairs.add(new BasicNameValuePair("student_id",
				CacheUtils.getStudentId(this)));
		mBasicNameValuePairs.add(new BasicNameValuePair("title", mEditTitle
				.getText().toString()));
		String content = mEditContent.getText().toString();
		if (StringUtils.isNotEmpty(content))
			mBasicNameValuePairs
					.add(new BasicNameValuePair("content", content));
		mBasicNameValuePairs.add(new BasicNameValuePair("forum_id", mTopicInfo
				.getForum_id()));
		CheckBox checkBox = mBottomView.getmBtnAnonymity();
		mBasicNameValuePairs.add(new BasicNameValuePair("anonymous", checkBox
				.isChecked() ? "1" : "2"));
		mBasicNameValuePairs.add(new BasicNameValuePair("type", "2"));
		myProgressDialog.show();
		new UploadPhoto().execute();
	}

	/**
	 * 发跟帖
	 */
	public void getInvitationParams() {
		mPostFile = FileModel.getPostFile(Contants.ImageLoadPath);
		mBasicNameValuePairs = new ArrayList<BasicNameValuePair>();
		mBasicNameValuePairs.add(new BasicNameValuePair("ticket", CacheUtils
				.getToken(PostInvitationActivity.this)));
		mBasicNameValuePairs.add(new BasicNameValuePair("student_id",
				CacheUtils.getStudentId(this)));
		mBasicNameValuePairs.add(new BasicNameValuePair("topic_id",
				flowupTopicInfo.getTopic_id()));
		String title = flowupTopicInfo.getTitle();
		if (StringUtils.isNotEmpty(title)) {
			mBasicNameValuePairs.add(new BasicNameValuePair("title", title));
		}
		String content = mEditContent.getText().toString();
		if (StringUtils.isNotEmpty(content))
			mBasicNameValuePairs
					.add(new BasicNameValuePair("content", content));
		mBasicNameValuePairs.add(new BasicNameValuePair("forum_id",
				flowupTopicInfo.getForum_id()));
		CheckBox checkBox = mBottomView.getmBtnAnonymity();
		mBasicNameValuePairs.add(new BasicNameValuePair("anonymous", checkBox
				.isChecked() ? "1" : "2"));
		mBasicNameValuePairs.add(new BasicNameValuePair("type", "1"));
		myProgressDialog.show();
		new UploadPhoto().execute();
	}

	@Override
	protected void onDestroy() {
		FileModel.deleteDir(Contants.ImageLoadPath);
		super.onDestroy();
	}

	@Override
	public void onClick(int whichButton) {
		// 拍照
		if (whichButton == 1) {
			goCamera();
			return;
		}
		// 从手机相册选择
		if (whichButton == 2) {
			SelectPictureActivity.startAction(this, Finals.TOPICK_REQUEST, 4,
					selectedPicture);
		}

	}

	/**
	 * 去拍照
	 */
	public void goCamera() {
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

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		// 相册返回
		if (requestCode == Finals.TOPICK_REQUEST) {
			selectedPicture = (ArrayList<SelectedImageBean>) data
					.getSerializableExtra("selectedPicture");
			setPostBtnCanClick();
			changeFlowLayoutShow();
			try {
				FileModel.deleteDir(Contants.ImageLoadPath);
				BitmapModel.saveBitmap(selectedPicture, Contants.ImageLoadPath,
						"headpic");
			} catch (IOException e) {
				setDebugLog(e);
			}
		}
		// 相机返回
		if (requestCode == Finals.TOCAMERA_REQUEST) {
			if (StringUtils.isEmpty(mCameraPhotoPath)) {
				return;
			}
			if (null == selectedPicture) {
				selectedPicture = new ArrayList<SelectedImageBean>(0);
			}
			if (selectedPicture.size() < 4) {
				selectedPicture.add(new SelectedImageBean(mCameraPhotoPath,
						selectedPicture.size() + 1, true));
			} else {
				ToastUtils.showToastInCenter("最多添加4张照片，您已添加4张照片");
			}
			setPostBtnCanClick();
			changeFlowLayoutShow();
			// 提示更新系统相册
			changeSystemPick();
			try {
				FileModel.deleteDir(Contants.ImageLoadPath);
				BitmapModel.saveBitmap(selectedPicture, Contants.ImageLoadPath,
						"headpic");
			} catch (IOException e) {
				setDebugLog(e);
			}
		}
		mEditContent.requestFocus();
		mEditContent.setFocusable(true);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		// 隐藏软键盘
		// imm.hideSoftInputFromWindow(editView.getWindowToken(), 0);
		// 显示软键盘
//		imm.showSoftInputFromInputMethod(mEditContent.getWindowToken(), 0);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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

	/**
	 * 去相机或者相册返回
	 */
	public void changeFlowLayoutShow() {
		mFlowLayout.removeAllViews();
		if (null == selectedPicture || selectedPicture.isEmpty()) {
			return;
		}
		LayoutParams params = new LayoutParams(mImageWidth, mImageWidth);
		for (int i = 0; i < selectedPicture.size(); i++) {
			View convertView = LayoutInflater.from(this).inflate(
					R.layout.add_image_item, null);
			convertView.setLayoutParams(params);
			ViewHolder viewHolder = getViewHolder(convertView);
			viewHolder.deleteBtn.setVisibility(View.VISIBLE);
			SelectedImageBean bean = selectedPicture.get(i);
			bean.setIndex(i);
			setImageViewShow(viewHolder.image, bean);
			viewHolder.image.setTag(bean);
			viewHolder.deleteBtn.setTag(bean);
			viewHolder.deleteBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteImage(v);
				}
			});
			mFlowLayout.addView(convertView, i);
		}
		if (selectedPicture.size() < 4) {
			View convertView = LayoutInflater.from(this).inflate(
					R.layout.add_image_item, null);
			convertView.setLayoutParams(params);
			ViewHolder viewHolder = getViewHolder(convertView);
			viewHolder.deleteBtn.setVisibility(View.GONE);
			viewHolder.image.setImageDrawable(getResources().getDrawable(
					R.drawable.addpics));
			viewHolder.image.setTag("add");
			viewHolder.image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDialog = ActionSheetDialog.showAlert(
							PostInvitationActivity.this,
							PostInvitationActivity.this, "拍照", "手机相册");
				}
			});
			mFlowLayout.addView(convertView);
		}
	}

	/**
	 * 删除对应的图片
	 * 
	 * @param v
	 */
	public void deleteImage(View v) {
		SelectedImageBean currentBean = (SelectedImageBean) v.getTag();
		selectedPicture.remove(currentBean.getIndex());
		selectedPicture = ListUitls.getRemoveSeletedLists(selectedPicture,
				currentBean.getSelectedPosition());
		String path = currentBean.getPath();
		// 删除图片对应在文件夹中的地址
		String fileName = path.substring(path.lastIndexOf("/") + 1,
				path.lastIndexOf("."));
		FileModel.delFile(Contants.ImageLoadPath, fileName);
		changeFlowLayoutShow();
	}

	public ViewHolder getViewHolder(View convertView) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.image = (ImageView) convertView
				.findViewById(R.id.image_item);
		viewHolder.deleteBtn = (RelativeLayout) convertView
				.findViewById(R.id.layout_delete);
		// FrameLayout.LayoutParams params =
		// (android.widget.FrameLayout.LayoutParams)
		// viewHolder.image.getLayoutParams();
		// params.width = mImageWidth;
		// params.height = mImageWidth;
		return viewHolder;
	}

	class ViewHolder {
		ImageView image;
		RelativeLayout deleteBtn;
	}

	/**
	 * 设置本地imageview显示 只考虑本地的情况,后续若有编辑需重新判断
	 * 
	 * @param imageview
	 */
	public void setImageViewShow(ImageView imageview, SelectedImageBean bean) {
		try {
			DisplayImageOptions options = DisplayImageOptionsUtils
					.getPickBitMap(R.drawable.touxiang_listdefault);
			ImageLoader.getInstance().displayImage("file://" + bean.getPath(),
					imageview, options);
			imageview.setTag(bean);
		} catch (Exception e) {
			e.printStackTrace();
			imageview.setImageDrawable(getResources().getDrawable(
					R.drawable.touxiang_listdefault));
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (type == 0) {// 楼主贴
			getLandlordInvitationParams();
		} else {// 跟帖
			getInvitationParams();
		}
	}

	/**
	 * 发布帖子
	 */
	class UploadPhoto extends AsyncTask<Integer, Integer, HttpRes> {
		@Override
		protected HttpRes doInBackground(Integer... params) {
			try {
				return HttpUtils.postFormFiles(StringUtils.getCommonIP()
						+ GlobalContants.POSTINVITATION_URL,
						mBasicNameValuePairs, mPostFile, "headpic");
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
				sendBroadcast();
				finish();
			} catch (Exception e) {
				setDebugLog(e);
				exceptionToast();
			}
		}
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("sendTopic"); // 发布帖子和跟帖：sendTopic
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("sendTopic");
		MobclickAgent.onPause(this);
	}
}

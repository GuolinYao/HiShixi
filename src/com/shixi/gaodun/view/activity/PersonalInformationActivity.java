package com.shixi.gaodun.view.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.renderscript.RenderScript;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.MajorClassifyBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.BitMapUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.CutPhotoUtils;
import com.shixi.gaodun.model.utils.HttpUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.DateWheelDialog;
import com.shixi.gaodun.view.DateWheelFrameLayout;
import com.shixi.gaodun.view.DateWheelFrameLayout.OnDateChangedListener;
import com.shixi.gaodun.view.MakeCircleImg;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人基本信息
 * 
 * @author ronger guolin
 * @date:2015-10-26 下午3:45:22
 */
@SuppressLint("HandlerLeak")
public class PersonalInformationActivity extends TitleBaseActivity implements
		OnAlertSelectId {
	private int requestType, sexOrPoliticsStatus, selectSexIndex = -1,
			selectPoliticsStatusIndex = -1;
	private final int ONELINE_REQUESTCODE = 1001, TWOLINE_REQUESTCODE = 1002,
			PRESENTADDRESS_REQUEST = 1003, MAJORCLASSIFY_REQUEST = 1004,
			HIGHEST_EDUCATION_REQUESTCODE = 1005, TOPICK_REQUESTCODE = 1006,
			TOCUT_REQUESTCODE = 1007, TOCAMERA_REQUESTCODE = 1008;
	private ImageView mHeadImage;
	private TextView mName, mSex, mBirth;
	private TextView mIdentity_card, mPolitics_status, mPresent_address;
	private TextView mSchool_name, mMajor_type, mMajor_name,
			mHighest_education;
	private TextView mContact_number, mContact_email;
	private CityBean selectCity;
	private MajorClassifyBean mSelectMajorBean;
	private DateWheelDialog mDateWheelDialog;
	private String mUserPic;
	// 请求类型：1获取基本信息，2上传图片，3修改基本信息
	private int requestAPIType = 1;
	private boolean ifSave = false;
	private File file;
	private UserInfoBean userinfo;

	public static void startAction(FragmentActivity activity, int requestCode) {
		Intent intent = new Intent(activity, PersonalInformationActivity.class);
		activity.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.personal_info_layout);
		isExcute = true;
		requestAPIType = 1;
		setRequestParamsPre(TAG);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("个人信息");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setEnabled(true);
		mOtherName.setText("保存");
		mHeadImage = (ImageView) findViewById(R.id.detail_shop_image);
		mName = (TextView) findViewById(R.id.tv_basicinfo_name);
		mSex = (TextView) findViewById(R.id.tv_basicinfo_sex);
		mBirth = (TextView) findViewById(R.id.tv_basicinfo_birth);
		mIdentity_card = (TextView) findViewById(R.id.tv_identity_card);
		mPolitics_status = (TextView) findViewById(R.id.tv_politics_status);
		mPresent_address = (TextView) findViewById(R.id.tv_present_address);
		mSchool_name = (TextView) findViewById(R.id.tv_school_name);
		mMajor_type = (TextView) findViewById(R.id.tv_major_type);
		mMajor_name = (TextView) findViewById(R.id.tv_major_name);
		mHighest_education = (TextView) findViewById(R.id.tv_highest_education);
		mContact_number = (TextView) findViewById(R.id.tv_contact_number);
		mContact_email = (TextView) findViewById(R.id.tv_contact_email);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_again:
			setRetryRequest();
			break;
		case R.id.fl_title_bar_back:
			// if (!ifSave) {
			// finish();
			// break;
			// }
			finish();
			break;
		// 保存个人信息
		case R.id.tv_title_bar_otherbtn:
			if (!ifSave) {
				finish();
				break;
			}
			requestAPIType = 3;
			myProgressDialog.setTitle("加载中...");
			setRequestParamsPre(TAG);
			break;
		case R.id.rl_head_layout:
			ActionSheetDialog.showAlert(this, this, "拍照", "手机相册");
			break;
		case R.id.rl_name_layout:
			requestType = 0;
			BasicInfoSetActivity.startAction(this, requestType,
					ONELINE_REQUESTCODE, mName.getText().toString());
			break;
		case R.id.rl_sex_layout:
			sexOrPoliticsStatus = 0;
			PoliticsStatusOrSexActivity.startAction(this, TWOLINE_REQUESTCODE,
					mSex.getText().toString(), selectSexIndex,
					sexOrPoliticsStatus);
			break;
		case R.id.rl_birth_layout:
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			mDateWheelDialog = new DateWheelDialog(this, year, month, day);
			mDateWheelDialog.show();
			DateWheelFrameLayout mDateWheelFrameLayout = mDateWheelDialog
					.getmDateWheelFrameLayout();
			setDateCallBack(mDateWheelFrameLayout);
			// dateWheel = new DateWheelActivity(this);
			// dateWheel.showAtLocation(mainView, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.rl_identity_card_layout:
			requestType = 1;
			BasicInfoSetActivity.startAction(this, requestType,
					ONELINE_REQUESTCODE, mIdentity_card.getText().toString());
			break;
		case R.id.rl_politics_status_layout:
			sexOrPoliticsStatus = 1;
			PoliticsStatusOrSexActivity.startAction(this, TWOLINE_REQUESTCODE,
					mPolitics_status.getText().toString(),
					selectPoliticsStatusIndex, sexOrPoliticsStatus);
			break;
		case R.id.rl_present_address_layout:
			SelectPresentAddressActivity.startAction(this,
					PRESENTADDRESS_REQUEST, selectCity);
			break;
		case R.id.rl_school_name_layout:
			requestType = 2;
			BasicInfoSetActivity.startAction(this, requestType,
					ONELINE_REQUESTCODE, mSchool_name.getText().toString());
			break;
		case R.id.rl_major_type_layout:
			MajorClassifyActivity.startAction(this, MAJORCLASSIFY_REQUEST,
					mSelectMajorBean, "专业分类");
			break;
		case R.id.rl_major_name_layout:
			requestType = 3;
			BasicInfoSetActivity.startAction(this, requestType,
					ONELINE_REQUESTCODE, mMajor_name.getText().toString());
			break;
		case R.id.rl_highest_education_layout:
			EducationListActivity.startAction(this,
					HIGHEST_EDUCATION_REQUESTCODE, mHighest_education.getText()
							.toString());
			break;
		case R.id.rl_contact_number_layout:
			requestType = 4;
			BasicInfoSetActivity.startAction(this, requestType,
					ONELINE_REQUESTCODE, mContact_number.getText().toString());
			break;
		case R.id.rl_contact_email_layout:
			requestType = 5;
			BasicInfoSetActivity.startAction(this, requestType,
					ONELINE_REQUESTCODE, mContact_email.getText().toString());
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		// 姓名、身份证、就读学校、专业名称、联系电话、联系邮箱返回
		if (requestCode == ONELINE_REQUESTCODE) {
			ifSave = true;
			String resultStr = arg2.getStringExtra("resultInfo");
			switch (requestType) {
			case 0:
				mName.setText(resultStr);
				userinfo.setName(resultStr);
				break;
			case 1:
				mIdentity_card.setText(resultStr);
				userinfo.setId_number(resultStr);
				break;
			case 2:
				mSchool_name.setText(resultStr);
				userinfo.setGraduate_school(resultStr);
				break;
			case 3:
				mMajor_name.setText(resultStr);
				userinfo.setDetail_major(resultStr);
				break;
			case 4:
				mContact_number.setText(resultStr);
				userinfo.setMobile(resultStr);
				break;
			case 5:
				mContact_email.setText(resultStr);
				userinfo.setContact_email(resultStr);
				break;
			default:
				break;
			}
		}
		if (requestCode == TWOLINE_REQUESTCODE) {
			ifSave = true;
			String result = arg2.getStringExtra("selectStr");
			if (sexOrPoliticsStatus == 0) {
				mSex.setText(result);
				selectSexIndex = arg2.getIntExtra("selectIndex", -1);
				// 1代表男2代表女
				userinfo.setGender(String.valueOf(selectSexIndex));
				return;
			}
			if (sexOrPoliticsStatus == 1) {
				mPolitics_status.setText(result);
				selectPoliticsStatusIndex = arg2.getIntExtra("selectIndex", -1);
				// 1代表党员2代表非党员
				userinfo.setPolitics_status(String
						.valueOf(selectPoliticsStatusIndex));
				return;
			}
		}
		if (requestCode == PRESENTADDRESS_REQUEST) {
			selectCity = (CityBean) arg2.getSerializableExtra("selectCity");
			if (null != selectCity) {
				ifSave = true;
				mPresent_address.setText(selectCity.getRegion_name());
				userinfo.setLiving_city(selectCity);
			}
			return;
		}
		if (requestCode == MAJORCLASSIFY_REQUEST) {
			mSelectMajorBean = (MajorClassifyBean) arg2
					.getSerializableExtra("bean");
			if (null != mSelectMajorBean) {
				ifSave = true;
				// userinfo.setMajor_type_bean(mSelectMajorBean);
				userinfo.setMajor_type(new MapFormatBean(mSelectMajorBean
						.getPkid(), mSelectMajorBean.getTitle()));
				mMajor_type.setText(StringUtils.isEmpty(mSelectMajorBean
						.getTitle()) ? "" : mSelectMajorBean.getTitle());
			}
			return;
		}
		if (requestCode == HIGHEST_EDUCATION_REQUESTCODE) {
			String selectStr = arg2.getStringExtra("selectStr");
			if (StringUtils.isNotEmpty(selectStr)) {
				ifSave = true;
				mHighest_education.setText(selectStr);
				userinfo.setEducation(selectStr);
			}
		}
		// 相册返回
		if (requestCode == TOPICK_REQUESTCODE) {
			if (null == arg2.getData())
				return;
			startActivityForResult(
					CutPhotoUtils.startPhotoZoom(arg2.getData()),
					TOCUT_REQUESTCODE);
		}
		// 拍照返回
		if (requestCode == TOCAMERA_REQUESTCODE) {
			File picture = new File(Environment.getExternalStorageDirectory()
					+ "/" + "photo.jpg");
			startActivityForResult(
					CutPhotoUtils.startPhotoZoom(Uri.fromFile(picture)),
					TOCUT_REQUESTCODE);
		}
		// 裁剪返回
		if (requestCode == TOCUT_REQUESTCODE) {
			if (arg2 != null) {
				ifSave = true;
				setPicToView(arg2);
			}
		}
	}

	// @Override
	// public void onBackPressed() {
	// if (!ifSave) {
	// finish();
	// return;
	// }
	// requestAPIType = 3;
	// myProgressDialog.setTitle("加载中...");
	// setRequestParamsPre(TAG);
	// }

	/**
	 * 设置选择日期后的回调
	 * 
	 * @param mDateWheelFrameLayout
	 */
	public void setDateCallBack(DateWheelFrameLayout mDateWheelFrameLayout) {
		mDateWheelFrameLayout
				.setOnDateChangedListener(new OnDateChangedListener() {

					@Override
					public void onDateChanged(String year, String month,
							String day) {
						mBirth.setText(year + "-" + month + "-" + day);
						mDateWheelDialog.cancel();
					}

					@Override
					public void onCancel() {
						mDateWheelDialog.cancel();
					}
				});
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
			startActivityForResult(intentCamera, TOCAMERA_REQUESTCODE);
			return;
		}
		// 从手机相册选择
		if (whichButton == 2) {
			Intent intentPick = new Intent(Intent.ACTION_PICK, null);
			intentPick.setDataAndType(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intentPick, TOPICK_REQUESTCODE);
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
//			mHeadImage.setImageResource(getResources().getColor(
//					R.color.transparent));
			mHeadImage.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			mHeadImage.setImageBitmap(cirbitmap);
			file = BitMapUtils.writeToSDCard(bitmap, this);
			bitmap.recycle();
			requestAPIType = 2;
			checkExitToken();
		}

	}

	/**
	 * 上传图片前检测是否存在token或者token是否有效
	 */
	public void checkExitToken() {
		myProgressDialog.setTitle("上传图片中...");
		setRequestParamsPre(TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (requestAPIType == 1) {
			getUserBasicInfo();
			return;
		}
		if (requestAPIType == 2) {
			uploadPhoto(file);
			return;
		}
		if (requestAPIType == 3 && ifSave)
			commitUpdateUserInfo();
	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		dissMissProgress();
		try {
			isFirstJoin = false;
			if (response.getInt("returnCode") != 0) {
				ToastUtils.showToastInCenter(response.getString("returnDesc"));
				if (requestAPIType == 3) {
					finish();
				}
				return;
			}
			setNolmalShow();
			if (requestAPIType == 1) {
				getUserBasicInfoSuccess(response.getString("returnData"));
				setUserInfoShow();
				return;
			}
			if (requestAPIType == 3) {
				sendBroadcast();
				finish();
			}
		} catch (Exception e) {
			setDebugLog(e);
			exceptionToast();
		}
	}

	/*
	 * 获取用户基本信息
	 */
	public void getUserBasicInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.GET_USERBASICINFO_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取个人信息成功
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void getUserBasicInfoSuccess(String resultData) throws Exception {
		userinfo = TransFormModel.getResponseResultObject(resultData,
				UserInfoBean.class);
		JSONObject jsonObject = new JSONObject(resultData);
		if (jsonObject.has("major_type")) {
			JSONObject major = new JSONObject(
					jsonObject.getString("major_type"));
			mSelectMajorBean = new MajorClassifyBean(major.getString("key"),
					major.getString("value"));
			// userinfo.setMajor_type_bean(mSelectMajorBean);
		}
		selectCity = userinfo.getLiving_city();
		selectSexIndex = Integer.parseInt(userinfo.getGender());
		// 1男2女
		userinfo.setSexName(selectSexIndex == 1 ? "男" : "女");
		String politics_status = userinfo.getPolitics_status();
		selectPoliticsStatusIndex = StringUtils.isNotEmpty(politics_status) ? Integer
				.parseInt(politics_status) : -1;
		// 1党员2非党员
		if (StringUtils.isNotEmpty(politics_status)) {
			userinfo.setPolitics_status_name(selectPoliticsStatusIndex == 1 ? "党员"
					: "非党员");
		}
		if (StringUtils.isNotEmpty(userinfo.getAvatar())) {
			mUserPic = userinfo.getAvatar();
		}
	}

	/**
	 * 设置用户信息显示
	 */
	public void setUserInfoShow() {
		mName.setText(userinfo.getName());
		mSex.setText(userinfo.getSexName());
		mBirth.setText(userinfo.getBirth_date());
		mIdentity_card
				.setText(StringUtils.isNotEmpty(userinfo.getId_number()) ? userinfo
						.getId_number() : "");
		mPolitics_status.setText(StringUtils.isEmpty(userinfo
				.getPolitics_status_name()) ? "" : userinfo
				.getPolitics_status_name());
		if (null != selectCity
				&& StringUtils.isNotEmpty(selectCity.getRegion_name())) {
			mPresent_address.setText(selectCity.getRegion_name());
		}
		mSchool_name.setText(StringUtils.isNotEmpty(userinfo
				.getGraduate_school()) ? userinfo.getGraduate_school() : "");
		if (null != mSelectMajorBean
				&& StringUtils.isNotEmpty(mSelectMajorBean.getTitle())) {
			mMajor_type.setText(mSelectMajorBean.getTitle());
		}
		mMajor_name
				.setText(StringUtils.isNotEmpty(userinfo.getDetail_major()) ? userinfo
						.getDetail_major() : "");
		String education = userinfo.getEducation();
		if (StringUtils.isNotEmpty(education)) {
			String educationName = "";
			if (education.equals("1"))
				educationName = "专科";
			if (education.equals("2"))
				educationName = "本科";
			if (education.equals("3"))
				educationName = "硕士";
			if (education.equals("4"))
				educationName = "博士";
			mHighest_education.setText(educationName);
		}
		mContact_number
				.setText(StringUtils.isNotEmpty(userinfo.getMobile()) ? userinfo
						.getMobile() : "");
		mContact_email.setText(StringUtils.isNotEmpty(userinfo
				.getContact_email()) ? userinfo.getContact_email() : "");
		setCirImage(mHeadImage, userinfo.getAvatar());
		// if (!StringModel.isEmpty(DataSaveModel.getUserPic(this))) {
		// try {
		// setCirImage(iv_head_img, DataSaveModel.getUserPic(this));
		// } catch (Exception e) {
		// }
		// }

	}

	public void setOtherData(UserInfoBean userinfo) {

	}

	// private void setCirImage(final ImageView headImg, final String url) {
	// if (StringUtils.isEmpty(url)) {
	// headImg.setImageResource(R.drawable.touxiang_listdefault);
	// }
	// ImageRequest imgRequest = new ImageRequest(url, new
	// Response.Listener<Bitmap>() {
	// @Override
	// public void onResponse(Bitmap arg0) {
	// // 在这里设置头像
	// MakeCircleImg cirImg = new MakeCircleImg();
	// headImg.setImageBitmap(cirImg.creatCircle(arg0));
	// }
	// }, 300, 200, Config.ARGB_8888, new ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError arg0) {
	// }
	// });
	// BaseApplication.mApp.addToRequestQueue(imgRequest, TAG);
	// }

	/**
	 * 提交修改后的个人信息
	 */
	public void commitUpdateUserInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		map.put("name", mName.getText().toString());
		map.put("gender", mSex.getText().toString().equals("男") ? "1" : "2");
		if (StringUtils.isNotEmpty(mUserPic))
			map.put("avatar", mUserPic);
		if (StringUtils.isNotEmpty(mBirth.getText().toString())) {
			map.put("birth_date", mBirth.getText().toString());
		}
		if (StringUtils.isNotEmpty(mIdentity_card.getText().toString()))
			map.put("id_number", mIdentity_card.getText().toString());

		String politics_status = mPolitics_status.getText().toString();
		if (StringUtils.isNotEmpty(politics_status))
			map.put("politics_status", politics_status.equals("非党员") ? "2"
					: "1");
		if (StringUtils.isNotEmpty(mPresent_address.getText().toString())
				&& null != selectCity) {
			map.put("living_city", selectCity.getRegion_id());
		}

		if (StringUtils.isNotEmpty(mSchool_name.getText().toString())) {
			map.put("graduate_school", mSchool_name.getText().toString());
		}
		if (StringUtils.isNotEmpty(mMajor_type.getText().toString())
				&& null != mSelectMajorBean) {
			map.put("major_type", mSelectMajorBean.getPkid());
		}
		if (StringUtils.isNotEmpty(mMajor_name.getText().toString())) {
			map.put("detail_major", mMajor_name.getText().toString());
		}
		String highest_education = mHighest_education.getText().toString();
		if (StringUtils.isNotEmpty(highest_education)) {
			if (highest_education.equals("专科"))
				map.put("education", "1");
			if (highest_education.equals("本科"))
				map.put("education", "2");
			if (highest_education.equals("硕士"))
				map.put("education", "3");
			if (highest_education.equals("博士"))
				map.put("education", "4");
		}
		if (StringUtils.isNotEmpty(mContact_email.getText().toString()))
			map.put("contact_email", mContact_email.getText().toString());
		if (StringUtils.isNotEmpty(mContact_number.getText().toString())) {
			map.put("contact_mobile", mContact_number.getText().toString());
		}

		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.UPDATE_BASIINFO_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 上传用户头像
	 * 
	 * @param file
	 */
	private void uploadPhoto(final File file) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair("ticket", CacheUtils
						.getToken(PersonalInformationActivity.this)));
				params.add(new BasicNameValuePair("student_id", CacheUtils
						.getStudentId(PersonalInformationActivity.this)));
				params.add(new BasicNameValuePair("type", "1"));
				try {
					Message message = Message.obtain();
					message.what = 0;
					message.obj = HttpUtils.postSingleFile(
							StringUtils.getCommonIP()
									+ GlobalContants.UPLOAD_URL, params, file,
							"headpic");
					uploadPhotoHandler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 上传图片完成
	 */
	private Handler uploadPhotoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				HttpRes message = (HttpRes) msg.obj;
				myProgressDialog.dismiss();
				try {
					if (message.getReturnCode() != 0) {
						ToastUtils.showToastInCenter(message.getReturnDesc());
						return;
					}
					// ToastUtils.showToastInCenter("上传图片成功");
					mUserPic = message.getReturnData();
					userinfo.setAvatar(mUserPic);
				} catch (Exception e) {
				}

			}
		}
	};

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (isFirstJoin && isExcute && requestAPIType == 1) {
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
		MobclickAgent.onPageStart("personInfo"); // 我的信息：personInfo
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("personInfo");
		MobclickAgent.onPause(this);
	}
}

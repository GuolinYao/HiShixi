package com.shixi.gaodun.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.TalentInfoBean;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.BitMapUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.MyProgressDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 预览人才库
 * 
 * @author guolinyao
 * @date 2016年4月27日 下午5:05:40
 */
@SuppressLint("InflateParams")
public class MyTalentBankPreviewActivity extends TitleBaseActivity implements
		OnAlertSelectId {
	// 基本信息
	private TextView mMobileText, mEmailText, mName, mSchool, mMajorType,
			mMajorName, mHighestEdu, mGraduateYear, mDearHR;
	// 实习意向
	private TextView mExpectAddressText, mExpectStageText;
	private TextView mInvitedFriends;// 已邀请的朋友
	// 人事
	private TextView mHRlayout;
	private LinearLayout mHrTitleLayout;
	private int type;// 0 普通进入 1 编辑人才库页面跳转过来
	private TalentInfoBean mTalentInfo;
	private UMShareListener umShareListener;
	private UMImage image1;// 链接
	private Bitmap defaultMap;
	private String content;// 分享的链接
	private LinearLayout mLlDearHR;

	public static void startAction(BaseMainActivity context, int type) {
		Intent intent = new Intent(context, MyTalentBankPreviewActivity.class);
		intent.putExtra("type", type);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		type = getIntent().getIntExtra("type", 0);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		isExcute = true;
		getIntentData();
		BaseApplication.mApp.mCenterTaskActivitys.add(this);
		setContentView(R.layout.mine_preview_talent_bank_layout);
		if (type == 1) {
			ToastUtils.showCustomToastInCenter("成功加入人才库", this);
		}
		setRequestParamsPre(TAG);
		setBroadcastReceiver();
		baseRegisterIntentFilter(Contants.PREVIEW_RESUME_UPDATE_ACTION);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("人才库");
		mBackIcon.setImageResource(R.drawable.icon_back);
		initBasicInfo();
		initInternshipIntention();
		// // 开启可编辑
		Config.OpenEditor = false;
		// 设置进度条
		MyProgressDialog dialog = new MyProgressDialog(this);
		Dialog createPD = dialog.createPD();
		Window window = createPD.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		WindowManager.LayoutParams params = window.getAttributes();
		params.dimAmount = 0.7f;
		window.setAttributes(params);
		// dialog.setTitle("11");
		// dialog.setMessage("222");
		Config.dialog = createPD;
		Drawable drawable = getResources().getDrawable(R.drawable.icon_logo);
		defaultMap = BitMapUtils.drawableToBitmap(drawable);
		image1 = new UMImage(this, defaultMap);
	}

	@Override
	public void toRequestServer(Intent intent) {
		if (intent.getAction().equals(Contants.PREVIEW_RESUME_UPDATE_ACTION)) {
			setRequestParamsPre(TAG);
		}
	}

	/**
	 * 基本信息
	 */
	public void initBasicInfo() {
		mName = (TextView) findViewById(R.id.tv_preview_name);
		mSchool = (TextView) findViewById(R.id.tv_preview_school);
		mMajorType = (TextView) findViewById(R.id.tv_preview_major_type);
		mEmailText = (TextView) findViewById(R.id.tv_preview_contactemail_value);
		mMobileText = (TextView) findViewById(R.id.tv_preview_contactmobile_value);
		mMajorName = (TextView) findViewById(R.id.tv_preview_major_name);
		mHighestEdu = (TextView) findViewById(R.id.tv_preview_highest_edu);
		mGraduateYear = (TextView) findViewById(R.id.tv_graduate_year);
		mDearHR = (TextView) findViewById(R.id.tv_dear_hr_type);
		mInvitedFriends = (TextView) findViewById(R.id.tv_invited_friends);
		mLlDearHR = (LinearLayout) findViewById(R.id.ll_dear_hr);
	}

	/**
	 * 实习意向
	 */
	public void initInternshipIntention() {
		mExpectAddressText = (TextView) findViewById(R.id.text_expect_address_value);
		mExpectStageText = (TextView) findViewById(R.id.text_expect_stage_value);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.rl_invite:// 邀请小伙伴
			// 点击转发后弹出分享页面
			ActionSheetDialog.showShareAlert(this,
					MyTalentBankPreviewActivity.this);
			break;
		case R.id.ibtn_invite_rule:// 邀请规则
			TalentInviteRuleActivity.startAction(this, content);
			break;
		case R.id.button_again:
			setRetryRequest();
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		if (requestCode == Finals.REQUESTCODE_ONE) {
			setRequestParamsPre(TAG);
		}
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		getTalentInfo();
	}

	/**
	 * 获取简历信息
	 */
	public void getTalentInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("student_id", CacheUtils.getStudentId(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GET_TALENT_BANK,
				map, new RequestResponseLinstner(this), this);
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
			setNolmalShow();
			mTalentInfo = TransFormModel.getTalentBean(httpRes.getReturnData());
			setViewShow();
		} catch (Exception e) {
			setDebugLog(e);
			dissMissProgress();
			exceptionToast();
		}
	}

	/**
	 * 显示
	 */
	public void setViewShow() {
		setBasicShow();
		setIntershipIntention();
	}

	/**
	 * 基本信息展示
	 */
	public void setBasicShow() {
		if (null == mTalentInfo)
			return;
		if (StringUtils.isNotEmpty(mTalentInfo.getName())) {
			mName.setText(NumberUtils.getString(mTalentInfo.getName(), ""));
		}
		if (StringUtils.isNotEmpty(mTalentInfo.getGraduate_school())) {
			mSchool.setText(NumberUtils.getString(
					mTalentInfo.getGraduate_school(), ""));
		}
		// 专业分类
		if (StringUtils.isNotEmpty(mTalentInfo.getMajor_type())) {
			mMajorType.setText(mTalentInfo.getMajor_type());
		}
		// 联系邮箱
		if (StringUtils.isNotEmpty(mTalentInfo.getContact_email())) {
			mEmailText.setText(NumberUtils.getString(
					mTalentInfo.getContact_email(), ""));
		}
		// 联系电话
		if (StringUtils.isNotEmpty(mTalentInfo.getContact_mobile())) {
			mMobileText.setText(NumberUtils.getString(mTalentInfo.getContact_mobile(),
					""));
		}
		// 专业名称
		if (StringUtils.isNotEmpty(mTalentInfo.getDetail_major())) {
			mMajorName.setText(NumberUtils.getString(
					mTalentInfo.getDetail_major(), ""));
		}

		if (StringUtils.isNotEmpty(mTalentInfo.getEducation())) {
			mHighestEdu.setText(mTalentInfo.getEducation());
		}
		// 毕业年份
		if (StringUtils.isNotEmpty(mTalentInfo.getGraduate_year())) {
			mGraduateYear.setText(NumberUtils.getString(
					mTalentInfo.getGraduate_year(), ""));
		}
		// 对HR说
		if (StringUtils.isNotEmpty(mTalentInfo.getDear_hr())) {
			mLlDearHR.setVisibility(View.VISIBLE);
			mDearHR.setText(NumberUtils.getString(mTalentInfo.getDear_hr(), ""));
		}else{
			mLlDearHR.setVisibility(View.GONE);
		}
		// 已邀请人数
		String invitedFriend = "已获得"
				+ NumberUtils.getString(mTalentInfo.getCount(), "0") + "个好友支持!";
		SpannableString spannableString = new SpannableString(invitedFriend);
		ForegroundColorSpan span = new ForegroundColorSpan(
				Color.parseColor("#fcff00"));
		spannableString.setSpan(span, 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mInvitedFriends.setText(spannableString);
		content = mTalentInfo.getShare_url();
	}

	/**
	 * 实习意向
	 */
	public void setIntershipIntention() {
		mExpectAddressText.setText(mTalentInfo.getExpect_city());
		if (StringUtils.isEmpty(mTalentInfo.getPeriod_start())
				|| StringUtils.isEmpty(mTalentInfo.getPeriod_finish()))
			return;
		if (mTalentInfo.getPeriod_start().equals("0000.00.00")
				|| mTalentInfo.getPeriod_start().equals("0000-00-00")) {
			mExpectStageText.setText("对时间没有要求，可快速到岗");
		} else
			mExpectStageText.setText(mTalentInfo.getPeriod_start() + "-"
					+ mTalentInfo.getPeriod_finish());
	}

	/**
	 * 设置实习地区的显示
	 */
	@SuppressLint("NewApi")
	public String setAreaShow(String mSelectCitys) {
		if (null == mSelectCitys || mSelectCitys.isEmpty())
			return "";
		StringBuilder selectCitySB = new StringBuilder();
		String replace = mSelectCitys.replace(",", "、");
		return replace;
	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MyTalentBankPreviewActivity"); // 人才库预览
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MyTalentBankPreviewActivity");
		MobclickAgent.onPause(this);
	}

	@SuppressLint("NewApi")
	// 分享按钮的点击事件
	@Override
	public void onClick(int whichButton) {
		switch (whichButton) {
		// 微信
		case 1:
			new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
					.setCallback(umShareListener).withTargetUrl(content)
					.withMedia(image1).withText(content).share();
			break;
		// 朋友圈
		case 2:
			new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
					.setCallback(umShareListener).withTargetUrl(content)
					.withMedia(image1).withText(content).share();
			break;
		// qq
		case 3:
			new ShareAction(this).setPlatform(SHARE_MEDIA.QQ)
					.setCallback(umShareListener).withTargetUrl(content)
					.withMedia(image1).withText(content).share();
			break;
		// 新浪微博
		case 4:
			new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
					.setCallback(umShareListener).withText(content)
					.withMedia(image1).withText(content).share();
			break;
		// // 复制链接
		// case 5:
		// // 得到系统剪切板管理类，并把文字链接设置为剪切板的内容
		// ClipboardManager cbm = (ClipboardManager)
		// getSystemService(CLIPBOARD_SERVICE);
		// // cbm.setText("www.hishixi.com");
		// ClipData clipData = new ClipData(ClipData.newHtmlText("高顿hi实习网站",
		// content, content));
		// cbm.setPrimaryClip(clipData);
		// if (cbm.hasPrimaryClip()) {
		// Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
		// }
		// break;

		}

		umShareListener = new UMShareListener() {

			@Override
			public void onResult(SHARE_MEDIA platform) {
				// Toast.makeText(context, platform + " 分享成功啦",
				// Toast.LENGTH_SHORT)
				// .show();
			}

			@Override
			public void onError(SHARE_MEDIA platform, Throwable t) {
				// Toast.makeText(context, platform + " 分享失败啦",
				// Toast.LENGTH_SHORT)
				// .show();
			}

			@Override
			public void onCancel(SHARE_MEDIA platform) {
				// Toast.makeText(context, platform + " 分享取消了",
				// Toast.LENGTH_SHORT)
				// .show();
			}
		};
	}
}

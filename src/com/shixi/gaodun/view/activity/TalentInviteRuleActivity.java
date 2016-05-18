package com.shixi.gaodun.view.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.BitMapUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.view.ActionSheetDialog;
import com.shixi.gaodun.view.ActionSheetDialog.OnAlertSelectId;
import com.shixi.gaodun.view.MyProgressDialog;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.utils.Util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.net.utils.Base64;
import com.zxing.activity.QRCodeUtil;
import com.zxing.encoding.EncodingHandler;

/**
 * 人才库邀请规则
 * 
 * @author guolinyao
 * @date 2016年4月28日 下午2:54:10
 */
public class TalentInviteRuleActivity extends Activity implements
		OnClickListener, OnAlertSelectId {
	private UMShareListener umShareListener;
	private Context context;
	private String content;// 分享的链接
	private UMImage image1;// 链接
	private Bitmap defaultMap;
	private String hunterIdStr;
	private SendMessageToWX.Req req;
	private static final String APP_ID = "wx588f6ee07a6e0aba";// 微信appid
	private IWXAPI api;// 第三方app和微信通信的openapi接口

	private void reqToWX() {
		// 通过微信WXAPIFactory 获取IWXAPI
		api = WXAPIFactory.createWXAPI(this, APP_ID, true);
		api.registerApp(APP_ID);
	}

	// activity跳转
	public static void startAction(Context context, String shareURL) {
		Intent intent = new Intent(context, TalentInviteRuleActivity.class);
		intent.putExtra("shareURL", shareURL);
		context.startActivity(intent);

	}

	protected void getIntentData() {
		content = getIntent().getStringExtra("shareURL");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getIntentData();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_talent_invite_rule_layout);
		context = this;
		hunterIdStr = Base64.encodeBase64String(CacheUtils.get_hunter_id(this)
				.getBytes());
		initView();
		initData();

	}

	private void initView() {

		ImageView iv_title_bar_icon = (ImageView) findViewById(R.id.iv_title_bar_icon);
		iv_title_bar_icon.setImageResource(R.drawable.icon_back);
		TextView tv_title_bar_titlename = (TextView) findViewById(R.id.tv_title_bar_titlename);
		tv_title_bar_titlename.setText("邀请规则");
		findViewById(R.id.fl_title_bar_back).setOnClickListener(this);

	}

	private void initData() {
		// 开启可编辑
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
	public void onClick(View v) {
		switch (v.getId()) {
		// 分享链接
		case R.id.btn_invite:
			ActionSheetDialog.showShareAlert(this,
					TalentInviteRuleActivity.this);
			break;
		case R.id.fl_title_bar_back:
			finish();
			break;

		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	@SuppressLint("NewApi")
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("TalentInviteRuleActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("TalentInviteRuleActivity");
		MobclickAgent.onPause(this);
	}
}

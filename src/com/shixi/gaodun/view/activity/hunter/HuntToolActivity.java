package com.shixi.gaodun.view.activity.hunter;

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
 * @author guolinyao
 * @date 2016年2月26日 上午11:25:14
 */
public class HuntToolActivity extends Activity implements OnClickListener,
		OnAlertSelectId {
	private ImageView mIvQRCode;// 分享猎头二维码按钮
	private ImageView mIvLink;// 复制链接按钮
	private UMImage image;// 二维码
	private UMShareListener umShareListener;
	private Context context;
	private String content;// 分享的链接
	private Bitmap qrcodeBitmap;
	private int type = 1;// 1 分享图片 2 分享链接
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
		Intent intent = new Intent(context, HuntToolActivity.class);
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
		setContentView(R.layout.activity_hunt_tool_layout);
		context = this;
		hunterIdStr = Base64.encodeBase64String(CacheUtils.get_hunter_id(this)
				.getBytes());
		initView();
		initData();

	}

	private void initView() {

		mIvQRCode = (ImageView) findViewById(R.id.iv_QR_code);
		mIvLink = (ImageView) findViewById(R.id.iv_link);
		mIvQRCode.setOnClickListener(this);
		mIvLink.setOnClickListener(this);
		ImageView iv_title_bar_icon = (ImageView) findViewById(R.id.iv_title_bar_icon);
		iv_title_bar_icon.setImageResource(R.drawable.icon_back);
		TextView tv_title_bar_titlename = (TextView) findViewById(R.id.tv_title_bar_titlename);
		tv_title_bar_titlename.setText("猎头工具");
		findViewById(R.id.fl_title_bar_back).setOnClickListener(this);

	}

	private void initData() {
		// content = "http://www.hishixi.com/MobileAccount/login/btn/regin/hid/"
		// + hunterIdStr;

		// 根据猎头id设置二维码图片
		QRCodeUtil qrCodeUtil = new QRCodeUtil();// 生成二维码工具
		qrcodeBitmap = qrCodeUtil.createQRCode(this, mIvQRCode,
				R.drawable.icon_logo_black, content);
		
		// 置为空
		qrCodeUtil = null;
		// 初始化 WXImageObject 和 WXMediaMessage对象
		// reqToWX();
		// WXImageObject imgBbj = new WXImageObject(qrcodeBitmap);
		// WXMediaMessage msg = new WXMediaMessage();
		// msg.mediaObject = imgBbj;
		// // 设置缩略图
		// Bitmap thumpBmp = Bitmap.createScaledBitmap(qrcodeBitmap, 100, 100,
		// true);
		// msg.thumbData = BitMapUtils.bitmap2byte(thumpBmp);
		// req = new SendMessageToWX.Req();
		// req.transaction = String.valueOf(System.currentTimeMillis());
		// req.message = msg;
		// req.scene = SendMessageToWX.Req.WXSceneSession;

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

		image = new UMImage(this, qrcodeBitmap);
		Drawable drawable = getResources().getDrawable(R.drawable.icon_logo);
		defaultMap = BitMapUtils.drawableToBitmap(drawable);
		image1 = new UMImage(this, defaultMap);
	}

	/**
	 * 设置而二维码图片
	 */
	// private void setQRcode(ImageView qrcodeImageView) {
	// // 二维码内容
	// content = "www.hishixi.com/" + CacheUtils.get_hunter_id(this);
	//
	// // 判断内容是否为空
	// if (null == content || "".equals(content)) {
	// Toast.makeText(this, "生成二维码失败，请重试……", Toast.LENGTH_SHORT).show();
	// return;
	// }
	//
	// try {
	// // 生成二维码图片，第一个参数是二维码的内容，第二个参数是正方形图片的边长，单位是像素
	// qrcodeBitmap = EncodingHandler.createQRCode(content,
	// ActivityUtils.dip2px(this, 200));
	// qrcodeImageView.setImageBitmap(qrcodeBitmap);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 分享二维码
		case R.id.iv_QR_code:
			type = 1;
			// 点击转发后弹出分享页面
			ActionSheetDialog.showShareAlert(this, HuntToolActivity.this);
			break;
		// 分享链接
		case R.id.iv_link:
			type = 2;
			ActionSheetDialog.showShareAlert2(this, HuntToolActivity.this);
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
			if (type == 1) {
				// api.sendReq(req);// 发送到微信
				new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
						.setCallback(umShareListener).withMedia(image).share();
			} else if (type == 2) {
				new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
						.setCallback(umShareListener).withTargetUrl(content)
						.withMedia(image1).withText(content).share();
			}

			break;
		// 朋友圈
		case 2:
			if (type == 1) {
				new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
						.setCallback(umShareListener).withMedia(image).share();

			} else if (type == 2) {
				new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
						.setCallback(umShareListener).withTargetUrl(content)
						.withMedia(image1).withText(content).share();
			}

			break;
		// qq
		case 3:
			if (type == 1) {
				new ShareAction(this).setPlatform(SHARE_MEDIA.QQ)
						.setCallback(umShareListener).withMedia(image).share();
			} else if (type == 2) {
				new ShareAction(this).setPlatform(SHARE_MEDIA.QQ)
						.setCallback(umShareListener).withTargetUrl(content)
						.withMedia(image1).withText(content).share();
			}
			break;
		// 新浪微博
		case 4:
			if (type == 1) {
				new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
						.setCallback(umShareListener).withText(content)
						.withMedia(image).share();
			} else if (type == 2) {
				new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
						.setCallback(umShareListener).withText(content)
						.withMedia(image1).withText(content).share();
			}

			break;
		// 复制链接
		case 5:
			// 得到系统剪切板管理类，并把文字链接设置为剪切板的内容
			ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			// cbm.setText("www.hishixi.com");
			ClipData clipData = new ClipData(ClipData.newHtmlText("高顿hi实习网站",
					content, content));
			cbm.setPrimaryClip(clipData);
			if (cbm.hasPrimaryClip()) {
				Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
			}
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
		MobclickAgent.onPageStart("HuntToolActivity");
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("HuntToolActivity");
		MobclickAgent.onPause(this);
	}
}

package com.shixi.gaodun.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixi.gaodun.R;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.VolleyErrorHelper;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.Contants;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DateUtils;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.MakeCircleImg;
import com.shixi.gaodun.view.MyProgressDialog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * activity基类，不带标题,所有扩展的baseactivity都需要继承与该类
 * 
 * @author ronger 
 * @date:2015-11-10 下午1:39:56
 */
public abstract class BaseMainActivity extends FragmentActivity implements
		Listener<JSONObject>, ErrorListener, OnClickListener, ResponseCallBack {
	boolean isExit = false;
	public String TAG = "BaseNotitleActivity";
	protected MyProgressDialog myProgressDialog;
	// 是否显示进度，默认为显示
	private boolean ifShowProgress = true;
	// 是否需要请求token
	protected boolean ifRequestToken = false;
	public final int left = 1, right = 2, top = 3, bottom = 4, fade = 5;
	// 跳转的动画1向left,2right,3top,4bottom
	protected int move = 1;
	// 连接服务器失败或者无网络连接时的提示语
	protected TextView mTextErrorWarn;
	// 没有数据时的提示语
	protected TextView mTextNoneDataWarnTitle;
	protected TextView mTextNoneDataWarnDesc;
	// 非正常情况下显示的容器
	protected FrameLayout mMainNoneDataLayout;
	protected View mCustomNoneDataView, mCustomErrorView;
	// 是否执行异常时的特殊显示,默认为执行，若不执行，则需在oncrete方法中赋值为false
	protected boolean isExcute = false;
	// 通用的广播接受者
	protected BroadcastReceiver mBroadcastReceiver;
	// 第一次进
	protected boolean isFirstJoin = true;
	private long exitTime;

	@SuppressLint("HandlerLeak")
	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 2000)
				isExit = false;
		}
	};
	private Intent intent;

	/**
	 * 获取上一页面跳转时所带的参数
	 */
	protected abstract void getIntentData();

	public MyProgressDialog getMyProgressDialog() {
		return myProgressDialog;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// 去除title
		requestWindowFeature(Window.FEATURE_NO_TITLE);

//		//设置可以使用沉浸模式的通用设置
//		ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
//		View parentView = contentFrameLayout.getChildAt(0);
//		if (parentView != null && Build.VERSION.SDK_INT >= 14) {
//			parentView.setFitsSystemWindows(true);
//		}

		myProgressDialog = new MyProgressDialog(this);
		BaseApplication.mApp.mActivities.add(this);
		getClassName();
		initErrorLayout();

	}

	/**
	 * 需要注册广播接收者的页面调用,部分需要重写onReceive方法的需要单独调用
	 */
	public void setFilterRegister() {
		setBroadcastReceiver();
		baseRegisterIntentFilter();
	}

	/**
	 * 注册广播接受者
	 */
	public void baseRegisterIntentFilter() {
		ActivityUtils.getLocalBroadcastManager(this);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Contants.ALL_UPDATE_ACTION);
		BaseApplication.mApp.mLocalBroadcastManager.registerReceiver(
				mBroadcastReceiver, mFilter);
	}

	/**
	 * 注册广播接受者
	 */
	public void baseRegisterIntentFilter(String action) {
		ActivityUtils.getLocalBroadcastManager(this);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(action);
		BaseApplication.mApp.mLocalBroadcastManager.registerReceiver(
				mBroadcastReceiver, mFilter);
	}

	/**
	 * 发送通用广播
	 */
	public void sendBroadcast() {
		ActivityUtils.getLocalBroadcastManager(this);
		Intent intent = new Intent();
		intent.setAction(Contants.ALL_UPDATE_ACTION);// 更新触发当前页面的数据
		BaseApplication.mApp.mLocalBroadcastManager.sendBroadcast(intent);
	}

	/***
	 * 发送指定广播
	 * 
	 * @param action
	 */
	public void sendBroadcast(String action) {
		ActivityUtils.getLocalBroadcastManager(this);
		Intent intent = new Intent();
		intent.setAction(action);
		BaseApplication.mApp.mLocalBroadcastManager.sendBroadcast(intent);
	}

	/**
	 * 重置
	 */
	public void setBroadcastReceiver() {
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				toRequestServer(intent);
			}
		};
	}

	public void toRequestServer(Intent intent) {
		if (intent.getAction().equals(Contants.ALL_UPDATE_ACTION)) {
			setRequestParamsPre(TAG, true);
		}
	}

	@Override
	protected void onStop() {
		cancelRequests();
		super.onStop();
	}

	public void cancelRequests() {
		BaseApplication.mApp.cancelPendingRequests(TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mBroadcastReceiver) {
			BaseApplication.mApp.mLocalBroadcastManager
					.unregisterReceiver(mBroadcastReceiver);
		}
		myProgressDialog.dismiss();
		System.gc();
	}

	/**
	 * 初始化控件
	 */
	public void initView() {
	}

	public void getClassName() {
		TAG = this.getClass().getName();
	}

	/**
	 * 根据当前activity是否在运行中判断是否显示进度对话框
	 * 
	 * @param Tag
	 */
	public void setProgressDialogShow(String Tag) {
		if (ActivityUtils.isActivityRunning(this, Tag)) {
			myProgressDialog.show();
		}
	}

	// /**
	// * 异步请求失败
	// */
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// myProgressDialog.dismiss();
	// ToastUtils.showToastInCenter(VolleyErrorHelper.getMessage(error, this));
	//
	// }

	/**
	 * 异步请求成功
	 */
	@Override
	public void onResponse(JSONObject response) {
		boolean ifExistToken = getTokenSucceed(response);
		if (ifExistToken) {
			setRequestParams(TAG);
		}
	}

	/**
	 * 请求数据成功
	 * 
	 * @param response
	 */
	public void requestSucceed(JSONObject response) {

	}

	/**
	 * 设置请求时参数,带loding
	 */
	public boolean setRequestParamsPre(String className) {
		this.ifShowProgress = true;
		setProgressDialogShow(className);
		ifRequestToken = DateUtils.getTimeInterval();
		if (ifRequestToken) {
			getToken();
		} else {
			setRequestParams(className);
		}
		return true;
	}

	/**
	 * 设置请求时参数ifShowProgress是否带loding
	 */
	public boolean setRequestParamsPre(String className, boolean ifShowProgress) {
		this.ifShowProgress = ifShowProgress;
		if (ifShowProgress) {
			setProgressDialogShow(className);
		}
		ifRequestToken = DateUtils.getTimeInterval();
		if (ifRequestToken) {
			getToken();
		} else {
			setRequestParams(className);
		}
		return true;
	}

	/**
	 * 一个页面多个请求时使用 返回true则直接执行下一个请求，试情况而定,返回false则需要在请求完token后执行下一个请求
	 * 
	 * @param className
	 * @param ifShowProgress
	 */
	public boolean setRequestPrarmsByToken(String className,
			boolean ifShowProgress, Listener<JSONObject> linListener) {
		this.ifShowProgress = ifShowProgress;
		if (ifShowProgress) {
			setProgressDialogShow(className);
		}
		ifRequestToken = DateUtils.getTimeInterval();
		if (ifRequestToken) {
			getToken(linListener);
			return false;
		} else {
			return true;
		}
	}

	public void setRequestParams(String className) {
		if (ifShowProgress) {
			setProgressDialogShow(TAG);
		}

	}

	/**
	 * 获取所有命令的入口值token
	 */
	public void getToken() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("authUserAcc", GlobalContants.API_ACCOUNT_NUMER);
		map.put("authUserPwd", GlobalContants.API_PASSWORD);
		map.put("authSign", StringUtils.getMD5Str());
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.POWER_URL, map,
				this, this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	public void getToken(Listener<JSONObject> linListener) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("authUserAcc", GlobalContants.API_ACCOUNT_NUMER);
		map.put("authUserPwd", GlobalContants.API_PASSWORD);
		map.put("authSign", StringUtils.getMD5Str());
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.POWER_URL, map,
				linListener, this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取token成功
	 * 
	 * @param response
	 */
	public boolean getTokenSucceed(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0)
				return false;
			JSONObject tokenObject = new JSONObject(httpRes.getReturnData());
			if (tokenObject.has("ticket"))
				CacheUtils.saveToken(this, tokenObject.getString("ticket"));
			if (tokenObject.has("expireTime"))
				CacheUtils.saveOldGetTokenTime(this,
						tokenObject.getLong("expireTime"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		ifRequestToken = ifRequestToken(response);
	}

	public boolean ifRequestToken(JSONObject response) {
		// try {
		// HttpRes httpRes = TransFormModel.getResponseData(response);
		// // 令牌过期或失效则需要重新获取令牌
		// if (httpRes.getReturnCode() == 4 || httpRes.getReturnCode() == 5) {
		// ifRequestToken = true;
		// return ifRequestToken;
		// }
		// ifRequestToken = false;
		// } catch (Exception e) {
		// ToastUtils.showToastInCenter("数据解析错误");
		// ifRequestToken = true;
		// return ifRequestToken;
		// }
		return ifRequestToken;

	}

	/**
	 * 停留两秒跳转时调用
	 */
	public void delayedJump() {

	}

	// /**
	// * 左出右隐藏跳转动画即左向右推出
	// */
	// public void leftInRightout() {
	// this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
	// }
	//
	// /**
	// * 上出下隐动画即上往下推出
	// */
	// public void upInDownOut() {
	// this.overridePendingTransition(R.anim.slide_up_in,
	// R.anim.slide_down_out);
	// }
	//
	// /**
	// * 右进左隐即右向左推出
	// */
	// public void rightInLeftOut() {
	// this.overridePendingTransition(R.anim.push_left_in,
	// R.anim.push_left_out);
	// }
	//
	// /**
	// * 下出上隐即下往上推出
	// */
	// public void downInUpOut() {
	// this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	// }

	protected void finishProcess() {
		for (int i = 0; i < BaseApplication.mApp.mActivities.size(); i++) {
			if (BaseApplication.mApp.mActivities.get(i) != null) {
				BaseApplication.mApp.mActivities.get(i).finish();
			}
		}
		System.exit(0);
	}

	protected void finishPartProcess() {
		for (int i = 0; i < BaseApplication.mApp.mCenterTaskActivitys.size(); i++) {
			if (BaseApplication.mApp.mCenterTaskActivitys.get(i) != null) {
				BaseApplication.mApp.mCenterTaskActivitys.get(i).finish();
			}
		}
	}

	public void exit() {
		if (System.currentTimeMillis() - exitTime > 2000) {
			isExit = true;
			Toast.makeText(this.getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
			// mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent =  new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			CacheUtils.saveIndex(BaseApplication.mApp, 0);
			finishProcess();
		}
	}

	/**
	 * 走服务器成功
	 * 
	 * @param response
	 */
	public HttpRes getRequestSuccess(JSONObject response, boolean ifArray) {
		try {
			myProgressDialog.dismiss();
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				httpRes.setGoOn(false);
				return httpRes;
			}
			String resultData = httpRes.getReturnData();
			if (ifArray && StringUtils.isEmpty(resultData)
					|| resultData.length() <= 2) {
				httpRes.setGoOn(false);
				return httpRes;
			}
			httpRes.setGoOn(true);
			return httpRes;
		} catch (Exception e) {
			ToastUtils.showToastInCenter("数据解析错误");
		}

		return null;
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		pendingStartTransition();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		pendingStartTransition();
	}

	@Override
	public void finish() {
		super.finish();
		finishTranstition();
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void finishTranstition() {
		overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	public void pendingStartTransition() {
		if (move == left) {// 左进：右向左推出
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
		if (move == right) {// 右进：左向右推出
			overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
		}
		if (move == top) {// 上进：下向上推出
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		}
		if (move == bottom) {// 下进：上往下推出
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
		}
		if (move == fade) {// 淡入淡出
			overridePendingTransition(R.anim.push_fade_in, R.anim.push_fade_out);
		}
	}

	/**
	 * 根据对应的父view和id设置textview的数据
	 * 
	 * @param res
	 * @param view
	 * @param text
	 */
	public void setTextViewShow(int res, View view, String text) {
		TextView textview = (TextView) view.findViewById(res);
		textview.setText(NumberUtils.getString(text, ""));
	}

	/**
	 * 根据对应的父view和id设置textview的显示与隐藏
	 * 
	 * @param res
	 * @param view
	 * @param visibility
	 */
	public void setTextViewGon(int res, View view, int visibility) {
		TextView textview = (TextView) view.findViewById(res);
		textview.setVisibility(visibility);
	}

	/**
	 * 下载头像并显示
	 * 
	 * @param headImg
	 * @param url
	 */
	protected void setCirImage(final ImageView headImg, final String url) {
		ImageRequest imgRequest = new ImageRequest(url,
				new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						MakeCircleImg cirImg = new MakeCircleImg();
						headImg.setImageBitmap(cirImg.creatCircle(arg0));
					}
				}, 300, 200, Config.ARGB_8888, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
					}
				});
		BaseApplication.mApp.addToRequestQueue(imgRequest, TAG);
	}

	public void setTextViewShow(TextView view, String text) {
		view.setText(NumberUtils.getString(text, ""));
	}

	public void setTextViewShow(TextView view, String text, String defaultStr) {
		view.setText(NumberUtils.getString(text, defaultStr));
	}

	// public void setImageByUrl(ImageView imageView, String url, int
	// defaultDrawable, int roundPixels) {
	// try {
	// DisplayImageOptions options =
	// DisplayImageOptionsUtils.getRoundBitMap(roundPixels, defaultDrawable);
	// ImageLoader.getInstance().displayImage(url, imageView, options);
	// } catch (Exception e) {
	// imageView.setImageDrawable(BaseApplication.mApp.getResources().getDrawable(defaultDrawable));
	// }
	// }
	//
	// public void setBigImageByUrl(ImageView imageView, String url, int
	// defaultDrawable) {
	// DisplayImageOptions options =
	// DisplayImageOptionsUtils.getBigBitMap(defaultDrawable);
	// try {
	// ImageLoader.getInstance().displayImage(url, imageView, options);
	// } catch (Exception e) {
	// ImageLoader.getInstance().displayImage("drawable://" + defaultDrawable,
	// imageView, options);
	// }
	//
	// }

	public void setBigImageByUrl(ImageView imageView, String url,
			int defaultDrawable, int defaultWidth, int defaultHeight) {
		DisplayImageOptions options = DisplayImageOptionsUtils
				.getBigBitMap(defaultDrawable);
		try {
			ImageLoader.getInstance().displayImage(url, imageView, options);
		} catch (Exception e) {
			ImageLoader.getInstance().displayImage(
					"drawable://" + defaultDrawable, imageView, options);
		}

	}

	public void setImageResource(ImageView imageView, int drawableId) {
		imageView.setImageResource(drawableId);
	}

	public int getMove() {
		return move;
	}

	public void setMove(int move) {
		this.move = move;
	}

	protected void setDebugLog(Exception e) {
		if (Contants.DEBUG) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化无网络、无数据的显示布局
	 */
	@SuppressLint("InflateParams")
	public void initErrorLayout() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		mCustomNoneDataView = layoutInflater.inflate(
				R.layout.pisition_none_data_layout, null);
		mCustomErrorView = layoutInflater.inflate(R.layout.activity_base_nonet,
				null);
		mCustomErrorView.findViewById(R.id.button_again).setOnClickListener(
				this);
		mTextErrorWarn = (TextView) mCustomErrorView
				.findViewById(R.id.text_server_warn);
		mTextNoneDataWarnTitle = (TextView) mCustomNoneDataView
				.findViewById(R.id.text_title);
		mTextNoneDataWarnDesc = (TextView) mCustomNoneDataView
				.findViewById(R.id.text_desc);
	}

	/**
	 * 异常时的描述
	 * 
	 * @param errorInfo
	 */
	protected void setErrorInfoShow(String errorInfo) {
		mTextErrorWarn.setText(errorInfo);
	}

	/**
	 * 异步请求失败
	 */
	@Override
	public void onErrorResponse(VolleyError error) {
		nomalOnErrorResponse(error);
	}

	/**
	 * 不需要特殊处理的异常提示
	 * 
	 * @param error
	 */
	public void nomalOnErrorResponse(VolleyError error) {
		myProgressDialog.dismiss();
		ToastUtils.showToastInCenter(VolleyErrorHelper.getMessage(error, this));
	}

	/**
	 * 关闭progress
	 */
	public void dissMissProgress() {
		if (null != myProgressDialog)
			myProgressDialog.dismiss();
	}

	/**
	 * 获取异常详细描述
	 * 
	 * @return
	 */
	protected String getErrorDetailDesc(VolleyError error) {
		String errorDes = VolleyErrorHelper.getMessage(error, this);
		return errorDes;
	}

	/**
	 * 解析出错时的提示
	 */
	protected void exceptionToast() {
		ToastUtils.showToastInCenter("数据解析错误");
	}

}

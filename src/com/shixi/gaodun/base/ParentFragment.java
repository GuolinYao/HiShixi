package com.shixi.gaodun.base;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
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
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.MyProgressDialog;

/**
 * 该项目中所有需要用到fragment的父类
 * 
 * @author ronger
 * @date:2015-11-23 上午9:17:15
 */
public abstract class ParentFragment extends Fragment implements Listener<JSONObject>, ErrorListener, OnClickListener,
		ResponseCallBack {
	public String TAG = "BaseFragment";
	public String ACTIVITYTAG = "FragmentActivity";
	protected BaseMainActivity mContext;
	protected MyProgressDialog myProgressDialog;
	// 是否需要请求token
	protected boolean ifRequestToken = false;
	// 是否显示进度，默认为显示
	private boolean mIfShowProgress = true;

	// 连接服务器失败或者无网络连接时的提示语
	protected TextView mTextErrorWarn;
	// 没有数据时的提示语
	protected TextView mTextNoneDataWarnTitle;
	protected TextView mTextNoneDataWarnDesc;
	protected FrameLayout mMainNoneDataLayout;
	protected View mCustomNoneDataView, mCustomErrorView;
	// 通用的广播接受者
	protected BroadcastReceiver mBroadcastReceiver;
	// 是否执行异常时的特殊显示,默认为执行，若不执行，则需在oncrete方法中赋值为false
	protected boolean isExcute = false;
	// 是否是第一次执行请求
	protected boolean isFristRequest = true;
	public final int left = 1, right = 2, top = 3, bottom = 4;
	// 跳转的动画1向left,2right,3top,4bottom
	protected int move = 1;

	protected abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	protected abstract void initView(View view);

	/** 设置异常时的显示 */
	protected abstract void setErrorShow();

	/** 设置异常时显示的信息 */
	protected void setErrorInfoShow(String errorInfo) {
		mTextErrorWarn.setText(errorInfo);
	}

	public void getClassName() {
		TAG = this.getClass().getName();
	}

	/**
	 * 获取到对应的activity
	 * 
	 * @return
	 */
	public BaseMainActivity getContext() {
		return (BaseMainActivity) getActivity();
	}

	/**
	 * Not add self to back stack when removed, so only when Activity stop
	 */
	@Override
	public void onStop() {
		super.onStop();
		dissMissProgress();
		BaseApplication.mApp.cancelPendingRequests(TAG);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = (BaseMainActivity) getActivity();
		getClassName();
		ACTIVITYTAG = mContext.TAG;
		// myProgressDialog = mContext.getMyProgressDialog();
		myProgressDialog = new MyProgressDialog(mContext);
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
		ActivityUtils.getLocalBroadcastManager(mContext);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Contants.ALL_UPDATE_ACTION);
		BaseApplication.mApp.mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mFilter);
	}

	/**
	 * 发送通用广播
	 */
	public void sendBroadcast() {
		ActivityUtils.getLocalBroadcastManager(mContext);
		Intent intent = new Intent();
		intent.setAction(Contants.ALL_UPDATE_ACTION);// 更新触发当前页面的数据
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

	/**
	 * 接收到广播后刷新
	 * 
	 * @param intent
	 */
	public void toRequestServer(Intent intent) {
		if (intent.getAction().equals(Contants.ALL_UPDATE_ACTION)) {
			setRequestParamsPre(TAG, false);
		}
	}

	/**
	 * 根据当前activity是否在运行中判断是否显示进度对话框
	 * 
	 * @param Tag
	 */
	public void setProgressDialogShow(String Tag) {
		try {
			if (null != myProgressDialog && ActivityUtils.isActivityRunning(mContext, ACTIVITYTAG)) {
				myProgressDialog.show();
			}
		} catch (Exception e) {
			setDebugLog(e);
		}

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (!isExcute) {
			nomalOnErrorResponse(error);
			return;
		}
		setOnErrorResponse(error);
	}

	public void setOnErrorResponse(VolleyError error) {
		String errorInfo = VolleyErrorHelper.getMessage(error, mContext);
		if (StringUtils.isEmpty(errorInfo))
			return;
		setErrorShow();
		setErrorInfoShow(errorInfo);
	}

	/**
	 * 不需要特殊处理的异常提示
	 * 
	 * @param error
	 */
	public void nomalOnErrorResponse(VolleyError error) {
		dissMissProgress();
		ToastUtils.showToastInCenter(VolleyErrorHelper.getMessage(error, mContext));
	}

	/**
	 * 设置没有数据时的描述
	 */
	public void setNoneDataDesc(String title, String desc) {
		mTextNoneDataWarnTitle.setText(title);
		mTextNoneDataWarnDesc.setText(desc);
	}

	/**
	 * 设置无数据的layout的显示
	 */
	public void setNoneDataShow() {

	}

	/**
	 * 统一的无网络、无数据的显示布局
	 */
	@SuppressLint("InflateParams")
	public void initErrorLayout() {
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		mCustomNoneDataView = layoutInflater.inflate(R.layout.pisition_none_data_layout, null);
		mCustomErrorView = layoutInflater.inflate(R.layout.activity_base_nonet, null);
		mCustomErrorView.findViewById(R.id.button_again).setOnClickListener(this);
		mTextErrorWarn = (TextView) mCustomErrorView.findViewById(R.id.text_server_warn);
		mTextNoneDataWarnTitle = (TextView) mCustomNoneDataView.findViewById(R.id.text_title);
		mTextNoneDataWarnDesc = (TextView) mCustomNoneDataView.findViewById(R.id.text_desc);
	}

	@Override
	public void onResponse(JSONObject response) {
		boolean ifExistToken = getTokenSucceed(response);
		if (ifExistToken) {
			setRequestParams(TAG);
		}
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
				CacheUtils.saveToken(mContext, tokenObject.getString("ticket"));
			if (tokenObject.has("expireTime"))
				// CacheUtils.saveTokenTime(this, tokenObject.getString("expireTime"));
				CacheUtils.saveOldGetTokenTime(mContext, tokenObject.getLong("expireTime"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设置请求时参数
	 */
	public boolean setRequestParamsPre(String className) {
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
	 * 设置请求时参数
	 */
	public boolean setRequestParamsPre(String className, boolean isShowProgress) {
		this.mIfShowProgress = isShowProgress;
		if (this.mIfShowProgress)
			setProgressDialogShow(className);
		ifRequestToken = DateUtils.getTimeInterval();
		if (ifRequestToken) {
			getToken();
		} else {
			setRequestParams(className);
		}
		return true;
	}

	public void setRequestParams(String className) {
		if (mIfShowProgress)
			setProgressDialogShow(TAG);
	}

	/**
	 * 获取所有命令的入口值token
	 */
	public void getToken() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("authUserAcc", GlobalContants.API_ACCOUNT_NUMER);
		map.put("authUserPwd", GlobalContants.API_PASSWORD);
		map.put("authSign", StringUtils.getMD5Str());
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP() + GlobalContants.POWER_URL,
				map, this, this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	protected void setDebugLog(Exception e) {
		if (Contants.DEBUG) {
			e.printStackTrace();
		}
	}

	@Override
	public void getResponse(JSONObject response) {
	}

	/**
	 * 设置正常显示，isNomal是否有数据或获取到数据
	 * 
	 * @param isNomal
	 */
	public void setNomalShow(boolean isNomal) {

	}

	/**
	 * 判断是否有网络，若没有网络则显示无网络的界面
	 * 
	 * @param ifExcute
	 */
	public boolean initMainLayout(boolean ifExcute) {
		if (!ActivityUtils.isNetAvailable()) {
			setErrorInfoShow("网络不给力\n请稍后再试");
			setErrorShow();
			return false;
		}
		return true;
	}

	/**
	 * 关闭progress
	 */
	public void dissMissProgress() {
		if (null != myProgressDialog)
			myProgressDialog.dismiss();
	}

	public void exceptionToast() {
		ToastUtils.showToastInCenter("数据解析错误");
	}

}

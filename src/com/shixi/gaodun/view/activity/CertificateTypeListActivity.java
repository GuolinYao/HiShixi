package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.NomalListViewAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * 证书分类和证书名称列表列表和证书状态
 * 
 * @author ronger
 * @date:2015-11-4 下午4:49:54
 */
public class CertificateTypeListActivity extends TitleBaseActivity implements OnItemClickListener {
	private ListView mListView;
	private String title;
	// 证书分类ID
	private String mPid;
	// true分类列表false名称列表
	private boolean ifTypeOrName;
	private MapFormatBean mCertificateBean;
	private ArrayList<MapFormatBean> mLists;
	// 是否显示证书状态列表
	private boolean ifShowStatus;
	private String mCurrentStatus;
	private String[] strArrays;

	public static void startAction(Activity context, int requestCode, MapFormatBean mCertificateBean, String title,
			boolean ifTypeOrName, String typeId) {
		Intent intent = new Intent(context, CertificateTypeListActivity.class);
		intent.putExtra("certificate", mCertificateBean);
		intent.putExtra("title", title);
		intent.putExtra("ifTypeOrName", ifTypeOrName);
		intent.putExtra("typeId", typeId);
		context.startActivityForResult(intent, requestCode);
	}

	public static void startAction(Activity context, int requestCode, String mStatus, String title, boolean ifShowStatus) {
		Intent intent = new Intent(context, CertificateTypeListActivity.class);
		intent.putExtra("mStatus", mStatus);
		intent.putExtra("title", title);
		intent.putExtra("ifShowStatus", ifShowStatus);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mCertificateBean = (MapFormatBean) data.getSerializableExtra("certificate");
		mPid = data.getStringExtra("typeId");
		ifTypeOrName = data.getBooleanExtra("ifTypeOrName", true);
		title = data.getStringExtra("title");
		ifShowStatus = data.getBooleanExtra("ifShowStatus", false);
		mCurrentStatus = data.getStringExtra("mStatus");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = true;
		setContentView(R.layout.nomal_list_layout);
		if (ifShowStatus) {
			setCertificateStatus();
		} else {
			setRequestParamsPre(TAG);
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back)
			finish();
		if (v.getId() == R.id.button_again)
			setRetryRequest();
	}

	@SuppressWarnings("rawtypes")
	public void setCertificateStatus() {
		Resources resources = getResources();
		strArrays = resources.getStringArray(R.array.certificate_status);
		NomalListViewAdapter adapter = new NomalListViewAdapter(this, strArrays, mCurrentStatus);
		mListView.setAdapter(adapter);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText(title);
		mBackIcon.setImageResource(R.drawable.icon_back);
		mListView = (ListView) findViewById(R.id.lv_nomal_listview);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (ifTypeOrName) {
			getCertificateTypeList();
		} else {
			getCertificateNameList();
		}
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			dissMissProgress();
			isFirstJoin = false;
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setNolmalShow();
			getDataSuccess(httpRes.getReturnData());
		} catch (Exception e) {
			dissMissProgress();
			setDebugLog(e);
			exceptionToast();
		}
	}

	/**
	 * 获取数据成功
	 * 
	 * @param resultData
	 * @throws Exception
	 * @throws JSONException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDataSuccess(String resultData) throws JSONException, Exception {
		if (null != resultData && resultData.length() > 2) {
			mLists = TransFormModel.getCertificateBeanList(new JSONArray(resultData), mCertificateBean, ifTypeOrName);
			NomalListViewAdapter adapter = null;
			adapter = new NomalListViewAdapter(this, mLists, 2);
			// adapter.somalListViewAdapter2(mLists);
			mListView.setAdapter(adapter);
		}
	}

	/*
	 * 获取证书分类列表
	 */
	public void getCertificateTypeList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CERTIFICATE_LIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取证书名称列表
	 */
	public void getCertificateNameList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("pid", mPid);
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CERTIFICATENAME_LIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		if (ifShowStatus) {
			intent.putExtra("mStatus", strArrays[position]);
		} else {
			intent.putExtra("mapbean", mLists.get(position));
		}
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void setRetryRequest() {
		setRequestParamsPre(TAG);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (isExcute && isFirstJoin) {
			setOnErrorResponse(error);
			return;
		}
		nomalOnErrorResponse(error);
	}
}

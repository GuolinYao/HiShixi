package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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
import com.shixi.gaodun.model.domain.MajorClassifyBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;

/**
 * 专业分类列表
 * 
 * @author ronger
 * @date:2015-10-28 上午11:22:56
 */
public class MajorClassifyActivity extends TitleBaseActivity implements OnItemClickListener {
	private MajorClassifyBean mSelectBean;
	private ListView mListView;
	private ArrayList<MajorClassifyBean> mMajorLists;
	private String title;
	private String url;

	public static void startAction(Activity context, int requestCode, MajorClassifyBean bean, String title) {
		Intent intent = new Intent(context, MajorClassifyActivity.class);
		intent.putExtra("major", bean);
		intent.putExtra("title", title);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		mSelectBean = (MajorClassifyBean) getIntent().getSerializableExtra("major");
		title = getIntent().getStringExtra("title");
		if (StringUtils.isNotEmpty(title) && title.equals("期望岗位类型")) {
			url = StringUtils.getCommonIP() + GlobalContants.CATEGORYLIST_URL;
		} else {
			url = StringUtils.getCommonIP() + GlobalContants.MAJOR_CLASSIFY_URL;
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = true;
		setContentView(R.layout.nomal_list_layout);
		setRequestParamsPre(TAG);
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
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
		}
		if (v.getId() == R.id.button_again) {
			setRetryRequest();
		}
	}

	/**
	 * 获取专业分类列表
	 */
	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(url, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		dissMissProgress();
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			isFirstJoin = false;
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setNolmalShow();
			getMajorListSuccess(httpRes.getReturnData());
		} catch (Exception e) {
			setDebugLog(e);
			exceptionToast();
		}
	}

	/**
	 * 获取专业列表成功
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void getMajorListSuccess(String resultData) throws Exception {
		if (null != resultData && resultData.length() > 2) {
			mMajorLists = TransFormModel.getMajorClassifyLists(new JSONArray(resultData), mSelectBean);
			NomalListViewAdapter adapter = new NomalListViewAdapter(this, mMajorLists);
			mListView.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		MajorClassifyBean bean = mMajorLists.get(position);
		bean.setIfSelected(true);
		intent.putExtra("bean", bean);
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

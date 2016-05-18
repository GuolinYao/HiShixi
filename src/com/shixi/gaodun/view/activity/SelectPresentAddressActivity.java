package com.shixi.gaodun.view.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.SelectAddressAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.db.AreaDB;
import com.shixi.gaodun.inf.OnTouchingLetterChangedListener;
import com.shixi.gaodun.inf.PinyinComparator;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DateUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ClearEditTextView;
import com.shixi.gaodun.view.SideBar;

/**
 * 选择现居地
 * 
 * @author ronger
 * @date:2015-10-27 上午10:10:03
 */
public class SelectPresentAddressActivity extends TitleBaseActivity implements OnItemClickListener,
		OnTouchingLetterChangedListener, TextWatcher {
	private AreaDB mAreaDB;
	private ClearEditTextView mAddressEdit;
	private TextView mCancelText;
	private FrameLayout mContents;
	private ListView mAddressListView;
	private SideBar mSdidBar;
	private ArrayList<CityBean> mAddressList;
	private ArrayList<CityBean> mHostList;
	private boolean ifGetAllAddress = true;
	private ArrayMap<String, List<CityBean>> mapCitys;
	// private Map<String, List<CityBean>> mapCitys;
	// 区域总列表
	private ArrayList<CityBean> mCityLists;
	private CityBean selectCity;
	private SelectAddressAdapter listAdapter;
	private PinyinComparator pinyinComparator;
	// 是否添加全国在列表里面
	private boolean ifAddAll;
	// 搜索过滤后城市列表
	private ArrayList<CityBean> mFilterDateList;

	public static void startAction(BaseMainActivity context, int requestCode, CityBean selectCity) {
		Intent intent = new Intent(context, SelectPresentAddressActivity.class);
		intent.putExtra("selectCity", selectCity);
		context.setMove(3);
		context.startActivityForResult(intent, requestCode);
		context.setMove(1);
	}

	@SuppressLint("NewApi")
	@Override
	public void finishTranstition() {
		overridePendingTransition(0, R.anim.slide_down_out);
	}

	@SuppressLint("NewApi")
	public static void startActionBy(BaseMainActivity context, Fragment fragmentContext, int requestCode,
			CityBean selectCity, boolean isLaodAll) {
		Intent intent = new Intent(context, SelectPresentAddressActivity.class);
		intent.putExtra("selectCity", selectCity);
		intent.putExtra("ifAddAll", isLaodAll);
		if (null != fragmentContext) {
			fragmentContext.startActivityForResult(intent, requestCode);
			fragmentContext.getActivity().overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
		} else {
			context.setMove(3);
			context.startActivityForResult(intent, requestCode);
			context.setMove(1);
		}
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		selectCity = (CityBean) data.getSerializableExtra("selectCity");
		ifAddAll = data.getBooleanExtra("ifAddAll", false);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		mAreaDB = AreaDB.getInstance(this);
		pinyinComparator = new PinyinComparator();
		setContentView(R.layout.select_present_address_layout);
		try {
			mCityLists = mAreaDB.getCityLists();
			// 存在地址就不重新获取
			if (null != mCityLists && !mCityLists.isEmpty()
					&& !DateUtils.getTimeInterval(24, CacheUtils.getOldGetCityTime(this))) {
				if (!ifAddAll) {
					mCityLists.remove(0);
				}
				setCityListAdapter();
				return;
			}
		} catch (Exception e) {

		}
		setRequestParamsPre(TAG);
		//
		// if (null == mapCitys || mapCitys.size() <= 0) {
		// ifGetAllAddress = true;
		// } else
		// ifGetAllAddress = false;
		// setRequestParamsPre(TAG);
	}

	@Override
	public void initView() {
		super.initView();
		mBackIcon.setImageResource(R.drawable.icon_back);
		mAddressEdit = (ClearEditTextView) findViewById(R.id.et_address_editext);
		mCancelText = (TextView) findViewById(R.id.tv_search_address_cancel);
		mContents = (FrameLayout) findViewById(R.id.fl_all_address);
		mAddressListView = (ListView) findViewById(R.id.lv_address);

		mSdidBar = (SideBar) findViewById(R.id.sidrbar);
		mAddressListView.setOnItemClickListener(this);
		mSdidBar.setOnTouchingLetterChangedListener(this);
		mAddressEdit.addTextChangedListener(this);
		if (ifAddAll) {
			mTitleName.setText("选择城市");
			mAddressEdit.setHint("搜索城市");
		} else {
			mTitleName.setText("选择现居地");
			mAddressEdit.setHint("你的居住地...");
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
			overridePendingTransition(0, R.anim.slide_down_out);
		}
	}

	/**
	 * 获取热门城市
	 */
	public void getHotCity() {
		ifGetAllAddress = false;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.HOTCITY_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取区域列表
	 */
	public void getAddressList() {
		ifGetAllAddress = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.GETCITY_LIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (ifGetAllAddress) {
			getAddressList();
		} else {
			getHotCity();
		}

	}

	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		// if (ifRequestToken) {
		// getToken();
		// return;
		// }
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				dissMissProgress();
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			// 解析数据，并且获取热门城市
			if (ifGetAllAddress) {
				getAddressSuccess(httpRes.getReturnData());
				return;
			}
			getHotCitySuccess(httpRes.getReturnData());
		} catch (Exception e) {
			setDebugLog(e);
			dissMissProgress();
			exceptionToast();
		}
	}

	/**
	 * 获取热门城市成功
	 * 
	 * @param resutData
	 * @throws JSONException
	 */
	public void getHotCitySuccess(String resutData) throws JSONException {
		if (null == resutData || resutData.length() < 2) {
			return;
		}
		final JSONArray hotArray = new JSONArray(resutData);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mHostList = TransFormModel.getHotCitys(hotArray, selectCity, true);
					myHandler.sendEmptyMessage(2);
				} catch (Exception e) {
					myProgressDialog.dismiss();
				}
			}
		}).start();
	}

	/**
	 * 获取城市列表成功
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void getAddressSuccess(final String resultData) throws Exception {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mapCitys = TransFormModel.getCityListsByInitial(resultData);
					myHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					myProgressDialog.dismiss();
				}
			}
		}).start();

	}

	/**
	 * 将所有的map转换为list
	 */
	public void mapToList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mAddressList = new ArrayList<CityBean>(0);
					for (String key : mapCitys.keySet()) {
						ArrayList<CityBean> listByKeys = (ArrayList<CityBean>) mapCitys.get(key);
						for (CityBean cityBean : listByKeys) {
							cityBean.setSortLetters(key);
							cityBean.setIfSelected(false);
							if (null != selectCity && cityBean.getRegion_id().equals(selectCity.getRegion_id())) {
								cityBean.setIfSelected(selectCity.isIfSelected());
							}
							mAddressList.add(cityBean);
						}
					}

					mCityLists = new ArrayList<CityBean>();
					if (null != mHostList && mHostList.size() > 0)
						mCityLists.addAll(mHostList);
					Collections.sort(mAddressList, pinyinComparator);
					mCityLists.addAll(mAddressList);
					mAreaDB.insetrAddress(mCityLists, 1);
					if(!ifAddAll){//如果不是添加全国
						mCityLists.remove(0);//选择现居地去掉全国
					}
					myHandler.sendEmptyMessage(3);
				} catch (Exception e) {
					myProgressDialog.dismiss();
				}
			}
		}).start();

	}

	private Handler myHandler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			// 解析所有区域成功
			if (msg.what == 1) {
				ifGetAllAddress = false;
				getHotCity();
			}
			// 解析热门地区完成
			if (msg.what == 2) {
				mapToList();
			}
			// 将map转换为list
			if (msg.what == 3) {
				setCityListAdapter();
			}
		}

	};

	public void setCityListAdapter() {
		dissMissProgress();
		CacheUtils.saveOldGetCityTime(this, DateUtils.getCurrentTimeSecond());
		listAdapter = new SelectAddressAdapter(SelectPresentAddressActivity.this, mCityLists);
		mAddressListView.setAdapter(listAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mFilterDateList != null && mFilterDateList.size() > 0) {
			selectCity = mFilterDateList.get(position);
		} else {
			selectCity = mCityLists.get(position);
		}
		Intent intent = new Intent();
		selectCity.setIfSelected(true);
		intent.putExtra("selectCity", selectCity);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	/**
	 * 侧边sidebar的点击
	 */
	@Override
	public void onTouchingLetterChanged(String s) {
		try {
			// 该字母首次出现的位置
			int position = listAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				mAddressListView.setSelection(position);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		filterData(s.toString());

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 */
	private void filterData(String filterStr) {
		mFilterDateList = new ArrayList<CityBean>();
		if (TextUtils.isEmpty(filterStr)) {
			mFilterDateList = mCityLists;
		} else {
			mFilterDateList.clear();
			for (CityBean city : mCityLists) {
				String name = city.getRegion_name();
				// Log.e("gaodun", "搜索：" + characterParser.getSelling(name));
				// 拼音加汉字检索
				// if (name.indexOf(filterStr.toString()) != -1
				// || characterParser.getSelling(name).startsWith(filterStr.toString())) {
				// filterDateList.add(city);
				// }
				// 汉字检索
				if (name.indexOf(filterStr.toString()) != -1 || name.contains(filterStr.toString())) {
					mFilterDateList.add(city);
				}
			}
		}

		// 根据a-z进行排序
		// Collections.sort(filterDateList, pinyinComparator);
		listAdapter.updateListView(mFilterDateList);
	}

	@Override
	protected void setRetryRequest() {
		mMainNoneDataLayout.setVisibility(View.GONE);
		setRequestParamsPre(TAG);
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, R.anim.slide_down_out);
	}

}

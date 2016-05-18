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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.InternshipAreaAdapter;
import com.shixi.gaodun.adapter.SelectAddressAdapter;
import com.shixi.gaodun.base.BaseApplication;
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
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DateUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ClearEditTextView;
import com.shixi.gaodun.view.FlowLayout;
import com.shixi.gaodun.view.SideBar;

/**
 * 选择实习地点
 * 
 * @author ronger
 * @date:2015-11-2 上午9:17:49
 */
public class SelectInternshipAreaActivity extends TitleBaseActivity implements
		OnItemClickListener, OnTouchingLetterChangedListener, TextWatcher {
	private AreaDB mAreaDB;
	private FrameLayout selectedLayout;
	private LinearLayout mSelectedLayout;
	private FlowLayout mSelectedFlowLayout;
	private ClearEditTextView mAddressEdit;
	private ListView mAddressListView;
	private SideBar mSdidBar;
	private ArrayList<CityBean> mAddressList;
	private ArrayList<CityBean> mHostList;
	private boolean ifGetAllAddress = true;
	private Map<String, List<CityBean>> mapCitys;
	// 区域总列表
	private ArrayList<CityBean> mCityLists;
	private PinyinComparator pinyinComparator;
	// 已结选择过了的区域
	private ArrayList<CityBean> mSelectCitys;
	private InternshipAreaAdapter listAdapter;
	private TextView mSelectedDes;
	private int mType;// 0单选，1多选
	private ArrayList<CityBean> mFilterDateList;// 存放搜索过滤后的城市信息集合

	// 是否添加全部在列表里面
	// private boolean ifAddAll = false;

	public static void startAction(Activity context, int requestCode,
			ArrayList<CityBean> selectCitys, int type) {
		Intent intent = new Intent(context, SelectInternshipAreaActivity.class);
		intent.putExtra("selectCity", selectCitys);
		intent.putExtra("type", type);
		context.startActivityForResult(intent, requestCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mSelectCitys = (ArrayList<CityBean>) data
				.getSerializableExtra("selectCity");
		mType = data.getIntExtra("type", 0);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = true;
		mAreaDB = AreaDB.getInstance(this);
		setContentView(R.layout.select_internship_area_layout);
		pinyinComparator = new PinyinComparator();
		setSelectedLayoutShow();
		// setContentView(R.layout.select_present_address_layout);
		// TODO
		try {
			mCityLists = mAreaDB.getCityLists2();
			// 存在地址就不重新获取
			if (null != mCityLists
					&& !mCityLists.isEmpty()
					&& !DateUtils.getTimeInterval(24,
							CacheUtils.getOldGetCityTime(this))) {
				mCityLists.remove(0);
				setCityListAdapter();
				return;
			}
		} catch (Exception e) {

		}
	
		setRequestParamsPre(TAG);
	}

	public void setCityListAdapter() {
		dissMissProgress();
		CacheUtils.saveOldGetCityTime(this, DateUtils.getCurrentTimeSecond());
		if (null != mSelectCitys && mSelectCitys.size() > 0) {
			changeSelectdShow();
		}
		listAdapter = new InternshipAreaAdapter(
				SelectInternshipAreaActivity.this, mCityLists);
		mAddressListView.setAdapter(listAdapter);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public void initView() {
		super.initView();
		mTitleName.setText(mType == 1 ? "选择期望实习地" : "选择现居地");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.VISIBLE);
		mOtherName.setOnClickListener(this);
		mAddressEdit = (ClearEditTextView) findViewById(R.id.et_address_editext);
		mAddressListView = (ListView) findViewById(R.id.lv_address);
		selectedLayout = (FrameLayout) LayoutInflater.from(this).inflate(
				R.layout.selected_city_layout, null);
		mSelectedLayout = (LinearLayout) selectedLayout
				.findViewById(R.id.ll_selected_area_layout);
		mSelectedFlowLayout = (FlowLayout) selectedLayout
				.findViewById(R.id.fl_selected_area_layout);
		mSelectedDes = (TextView) selectedLayout
				.findViewById(R.id.tv_selected_city);
		mSelectedDes.setText("已选城市   (最多选5项)");
		mSdidBar = (SideBar) findViewById(R.id.sidrbar);
		mAddressListView.setOnItemClickListener(this);
		mSdidBar.setOnTouchingLetterChangedListener(this);
		mAddressEdit.addTextChangedListener(this);
		mOtherName
				.setEnabled(null == mSelectCitys || mSelectCitys.isEmpty() ? false
						: true);
		mOtherName.setText("保存");
		mAddressListView.addHeaderView(selectedLayout);
	}

	public void setSelectedLayoutShow() {
		if (null == mSelectCitys || mSelectCitys.isEmpty()) {
			mSelectedLayout.setVisibility(View.GONE);
			mSelectCitys = new ArrayList<CityBean>();
		} else {
			mSelectedLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
		}
		if (v.getId() == R.id.tv_title_bar_otherbtn) {
			Intent intent = new Intent();
			intent.putExtra("selectCity", mSelectCitys);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
		if (v.getId() == R.id.button_again)
			setRetryRequest();
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
		try {
			isFirstJoin = false;
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				dissMissProgress();
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			setNolmalShow();
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
					mHostList = TransFormModel.getHotCitys(hotArray,
							mSelectCitys,true);
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
					mapCitys = TransFormModel.getCitysByInitial(resultData);
					myHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					dissMissProgress();
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
					mAddressList = new ArrayList<CityBean>();
					for (String key : mapCitys.keySet()) {
						ArrayList<CityBean> listByKeys = (ArrayList<CityBean>) mapCitys
								.get(key);
						for (CityBean cityBean : listByKeys) {
							cityBean.setSortLetters(key);
							cityBean.setIfSelected(false);
							if (null == mSelectCitys || mSelectCitys.isEmpty()) {
								mAddressList.add(cityBean);
								continue;
							}
							for (CityBean selectBean : mSelectCitys) {
								if (cityBean.getRegion_id().equals(
										selectBean.getRegion_id())) {
									cityBean.setIfSelected(true);
									break;
								}
							}
							mAddressList.add(cityBean);
						}
					}
					mCityLists = new ArrayList<CityBean>();
					if (null != mHostList && mHostList.size() > 0)
						mCityLists.addAll(mHostList);
					Collections.sort(mAddressList, pinyinComparator);
					mCityLists.addAll(mAddressList);
					mAreaDB.insetrAddress2(mCityLists, 1);
					mCityLists.remove(0);//去掉全国
					myHandler.sendEmptyMessage(3);
				} catch (Exception e) {
					dissMissProgress();
				}
			}
		}).start();

	}

	@SuppressLint("HandlerLeak")
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
//				myProgressDialog.dismiss();
//				if (null != mSelectCitys && mSelectCitys.size() > 0) {
//					changeSelectdShow();
//				}
//				listAdapter = new InternshipAreaAdapter(
//						SelectInternshipAreaActivity.this, mCityLists);
//				mAddressListView.setAdapter(listAdapter);
				//TODO
				setCityListAdapter();
			}
		}

	};

	/**
	 * 获取热门城市
	 */
	public void getHotCity() {
		ifGetAllAddress = false;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.HOTCITY_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取区域列表
	 */
	public void getAddressList() {
		ifGetAllAddress = true;
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETCITY_LIST_URL,
				map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/*
	 * 设置选择了城市后的显示
	 */
	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	public void changeSelectdShow() {
		android.view.ViewGroup.LayoutParams fLayoutParams = mSelectedFlowLayout
				.getLayoutParams();
		if (null == mSelectCitys || mSelectCitys.isEmpty()) {
			mSelectedLayout.setVisibility(View.GONE);
			mSelectedFlowLayout.removeAllViews();
			fLayoutParams.width = ActivityUtils.getScreenWidth();
			fLayoutParams.height = ActivityUtils.dip2px(this, 0);
			mSelectedFlowLayout.setLayoutParams(fLayoutParams);
			mOtherName.setEnabled(false);
			return;
		}
		if (mSelectedLayout.getVisibility() == View.GONE)
			mSelectedLayout.setVisibility(View.VISIBLE);
		mSelectedFlowLayout.removeAllViews();
		for (int i = 0; i < mSelectCitys.size(); i++) {
			CityBean city = mSelectCitys.get(i);
			View selectedView = LayoutInflater.from(this).inflate(
					R.layout.selected_city_item_layout, null);
			LayoutParams layoutParams = new LayoutParams(
					ActivityUtils.getScreenWidth(), ActivityUtils.dip2px(this,
							49));
			selectedView.setLayoutParams(layoutParams);
			TextView selectedCityName = (TextView) selectedView
					.findViewById(R.id.tv_address_name);
			FrameLayout selecteDeleteBtn = (FrameLayout) selectedView
					.findViewById(R.id.fl_delect_selected);
			selectedCityName.setText(city.getRegion_name());
			selecteDeleteBtn.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					deleteSelectedCity((CityBean) v.getTag());
					return true;
				}
			});
			// setOnClickListener(this);
			selecteDeleteBtn.setTag(city);
			mSelectedFlowLayout.addView(selectedView, i);
		}
		fLayoutParams.width = ActivityUtils.getScreenWidth();
		fLayoutParams.height = ActivityUtils.dip2px(this,
				49 * mSelectCitys.size());
		mSelectedFlowLayout.setLayoutParams(fLayoutParams);
	}

	public void deleteSelectedCity(CityBean deleteCity) {
		if (mSelectCitys.contains(deleteCity)) {
			mSelectCitys.remove(deleteCity);
			changeSelectdShow();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mSelectCitys.size() == 5) {
			ToastUtils.showToastInCenter("最多选5项");
			return;
		}
		position = position - mAddressListView.getHeaderViewsCount();
		// TODO 点击城市名称
		CityBean selectCity = mCityLists.get(position);
		// Log.i("---", selectCity.getRegion_name());
		if (null != mSelectCitys) {
			boolean isGoOn = true;
			for (CityBean indexCity : mSelectCitys) {
				if (indexCity.getRegion_name().equals(
						selectCity.getRegion_name())) {
					isGoOn = false;
					break;
				}
			}
			if (!isGoOn)
				return;
		} else {
			mSelectCitys = new ArrayList<CityBean>(0);

		}

		if (mSelectCitys.contains(selectCity)) {
			return;
		}
		selectCity.setIfSelected(true);
		mSelectCitys.add(selectCity);
		mOtherName.setEnabled(true);
		// 更新已选的显示
		changeSelectdShow();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		filterData(s.toString());

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

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
				// 汉字检索
				if (name.indexOf(filterStr.toString()) != -1
						|| name.contains(filterStr.toString())) {
					mFilterDateList.add(city);
				}
			}

		}

		// 根据a-z进行排序
		Collections.sort(mFilterDateList, pinyinComparator);
		listAdapter.updateListView(mFilterDateList);
		// TODO 重新设置一下点击事件
		mAddressListView.setOnItemClickListener(this);
	}

	@Override
	public void onTouchingLetterChanged(String s) {
		try {
			// 该字母首次出现的位置
			int position = listAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				// mAddressListView.setAdapter(listAdapter);
				mAddressListView.setSelection(position);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

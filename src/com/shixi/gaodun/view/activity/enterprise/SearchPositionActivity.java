package com.shixi.gaodun.view.activity.enterprise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.Response.Listener;
import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.SeachPositionAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.db.SearchDB;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.libpull.PullToRefreshBase.OnRefreshListener;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.HotSearchBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.KeyWordBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.domain.RefersBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DateUtils;
import com.shixi.gaodun.model.utils.LayoutParamsUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.ClearEditTextView;
import com.shixi.gaodun.view.FilterDialog;
import com.shixi.gaodun.view.FilterDialog.SelectListener;
import com.shixi.gaodun.view.FlowLayout;
import com.shixi.gaodun.view.KeyWordFilterDialog;
import com.shixi.gaodun.view.KeyWordFilterDialog.onKeyWordItemClickListener;
import com.shixi.gaodun.view.activity.SelectPresentAddressActivity;

/**
 * 搜索职位
 * 
 * @author ronger guolin
 * @date:2015-11-10 下午1:23:00
 */
@SuppressLint("InflateParams")
public class SearchPositionActivity extends BaseMainActivity implements OnItemClickListener, OnRefreshListener,
		TextWatcher, onKeyWordItemClickListener, OnScrollListener {
	// 加载更多时的view
	protected View mLoadMoreLayout;
	// 是否需要加载更多
	private boolean isLoadMore;
	private LinearLayout mInitLayout;
	private FlowLayout mInitHotLayout, mInitNearLayout;
	private TextView mHistoryTitle, mHistoryDelet;
	// 已选择了的地址默认为全国
	private TextView mSelectedAddressText;
	private LinearLayout mFilterLayout, mShowLayout;
	private TextView mPositionType, mEducation, mGradeTime;
	private TextView mSearchBtn, mCancelBtn;
	private ClearEditTextView mSearchEdit;
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private RelativeLayout mNoneDataLayout;
	private FlowLayout mFlowLayout;
	private View noneLayout;
	private FilterDialog positionFilterDialog, educationFilterDialog, gradeFilterDialog;
	private ArrayList<PositionBean> mPositionLists;
	private ArrayList<HotSearchBean> mHotLists;
	private ArrayList<MapFormatBean> educationList, gradeLists, cateLists;

	private int mPage = 1;// 页码，默认从1开始
	private int mPageNumber = 5;// 每页的数量
	private int mAllCount = 0;// 总条数
	private SeachPositionAdapter mAdapter;
	// 最近搜索
	private SearchDB mSearchDB;
	private ArrayList<String> mNearLists;
	// 搜索关键词
	private ArrayList<KeyWordBean> mKeyWords;
	private KeyWordFilterDialog mkeyWordDialog;

	// 头部筛选布局
	private LinearLayout mFilterTopLayout;

	// 搜索匹配
	private LinearLayout mFilterWordLaout, mShowWordLayout;
	private boolean ifShowWordList = true;
	// 搜索条件，包括企业ID、地址、职位、地点,每周实习天数,岗位分类,企业行业,学历
	// private String enterprise_id;// 企业iD
	private String mFilterAddress, mFilterPosition, mFilterEducation, mFilterGrade, mKeyWord;
	private CityBean mSelectCity;
	private boolean isPosition, isEducation, isGrade;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, SearchPositionActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.search_position_layout);
		isExcute = false;
		initView();
		mSearchDB = new SearchDB(this);
		getInitLayout();// 热词+历史记录UI
		getNearLists();// 历史记录
		getNoneDataLayout();// 没有数据时的layout
		initFilterDialog();// 筛选分类框
		getHotList();// 获取热词
		getPostList();// 获取岗位列表
		getFilterEducationInfo();// 获取教育列表
		getFilterGradeInfo();// 获取毕业年份列表
	}

	/**
	 * 是否继续
	 * 
	 * @return
	 */
	public boolean isGoOn(JSONObject response) {
		boolean ifExistToken = getTokenSucceed(response);
		return ifExistToken;
	}

	/**
	 * 获取热词
	 */
	public void getHotList() {
		boolean ifGoOn = setRequestPrarmsByToken(TAG, true, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (isGoOn(response)) {
					getHotListByToken();
				} else {
					myProgressDialog.dismiss();
				}
			}
		});
		if (ifGoOn) {
			getHotListByToken();
		}
	}

	/**
	 * 获取热词列表
	 */
	public void getHotListByToken() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("range", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.GETHOTSEARCH_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					HttpRes httpRes = getRequestSuccess(response, true);
					if (null == httpRes || !httpRes.isGoOn()) {
						return;
					}
					String hotStr = httpRes.getReturnData();
					mHotLists = (ArrayList<HotSearchBean>) TransFormModel.getResponseResults(hotStr,
							HotSearchBean.class);
					setHotSearchShow(mInitHotLayout);
				} catch (Exception e) {

				}
			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取岗位列表
	 */
	public void getPostList() {
		boolean ifGoOn = setRequestPrarmsByToken(TAG, true, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (isGoOn(response)) {
					getPostListByToken();
				} else {
					myProgressDialog.dismiss();
				}
			}
		});
		if (ifGoOn) {
			getPostListByToken();
		}
	}

	/**
	 * 获取岗位列表
	 */
	public void getPostListByToken() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.CATEGORYLIST_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					HttpRes httpRes = getRequestSuccess(response, true);
					if (null == httpRes || !httpRes.isGoOn()) {
						return;
					}
					String resultData = httpRes.getReturnData();
					cateLists = TransFormModel.getCateLists(new JSONArray(resultData), true);
					mYHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					myProgressDialog.dismiss();
				}
			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取搜索结果
	 */
	public void getPositionList() {
		boolean ifGoOn = setRequestPrarmsByToken(TAG, true, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (isGoOn(response)) {
					getPositionListByToken();
				} else {
					myProgressDialog.dismiss();
				}
			}
		});
		if (ifGoOn) {
			getPositionListByToken();
		}
	}

	/**
	 * 获取搜索结果
	 */
	public void getPositionListByToken() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this))) {
			map.put("student_id", CacheUtils.getStudentId(this));
		}
		if (StringUtils.isNotEmpty(mFilterAddress)) {
			map.put("city_id", mFilterAddress);
		}
		if (StringUtils.isNotEmpty(mFilterEducation)) {
			map.put("edu", mFilterEducation);
		}
		if (StringUtils.isNotEmpty(mFilterGrade)) {
			map.put("grad_year", mFilterGrade);
		}
		if (StringUtils.isNotEmpty(mFilterPosition)) {
			map.put("cate_id", mFilterPosition);
		}
		if (StringUtils.isNotEmpty(mKeyWord)) {
			map.put("keyword", mKeyWord);
		}
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.GETPOSITION_FILTERLIST_URL, map, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					HttpRes httpRes = getRequestSuccess(response, false);
					if (null == httpRes || !httpRes.isGoOn())
						return;
					JSONObject object = new JSONObject(httpRes.getReturnData());
					String resultData = object.getString("post");
					mAllCount = NumberUtils.getInt(object.getString("count"), 0);
					if (mAllCount == 0) {
						mPositionLists = null;
						getResponseComplete();
						mYHandler.sendEmptyMessage(2);
						return;
					}
					setPullListViewShow();
					getResponseComplete();
					getListSuccess(resultData);
				} catch (Exception e) {
					getResponseComplete();
					// myProgressDialog.dismiss();
				}
			}
		}, this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 请求服务器完成
	 */
	public void getResponseComplete() {
		mPullListView.onRefreshComplete();
		myProgressDialog.dismiss();
		showLoadingControl(false);
	}

	/**
	 * 获取关键词对应的列表
	 */
	public void getKeyWordList() {
		boolean ifGoOn = setRequestPrarmsByToken(TAG, false, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (isGoOn(response)) {
					getKeyWordListByToken();
				} else {
					myProgressDialog.dismiss();
				}
			}
		});
		if (ifGoOn) {
			getKeyWordListByToken();
		}
	}

	/**
	 * 获取关键词列表
	 */
	public void getKeyWordListByToken() {
		// //关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("keyword", mKeyWord);
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.GETKEYWORD_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
			@Override
			public void getResponse(JSONObject response) {
				try {
					try {
						HttpRes httpRes = getRequestSuccess(response, false);
						if (null == httpRes || !httpRes.isGoOn())
							return;
						JSONObject jsonObject = new JSONObject(httpRes.getReturnData());
						String postkey = jsonObject.getString("postkey");
						String enterprisekey = jsonObject.getString("enterprisekey");
						mKeyWords = new ArrayList<KeyWordBean>();
						if (StringUtils.isNotEmpty(postkey) && postkey.length() > 2) {
							mKeyWords.addAll(TransFormModel.getResponseResults(postkey, KeyWordBean.class));
						}
						if (StringUtils.isNotEmpty(enterprisekey) && enterprisekey.length() > 2)
							mKeyWords.addAll(TransFormModel.getResponseResults(enterprisekey, KeyWordBean.class));
						if (mKeyWords.isEmpty()) {
							return;
						}
						if (null == mkeyWordDialog) {
							mkeyWordDialog = new KeyWordFilterDialog(SearchPositionActivity.this);
							mkeyWordDialog.setSelectListener(SearchPositionActivity.this);
							mShowWordLayout.removeAllViews();
							mShowWordLayout.addView(mkeyWordDialog.getResultLayout());
							mkeyWordDialog.setAdapter(mKeyWords, mKeyWord);
							return;
						}
						mkeyWordDialog.setNotify(mKeyWords);
					} catch (Exception e) {
					}

				} catch (Exception e) {
					ToastUtils.showToastInCenter("数据解析错误");
				}
			}
		}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取搜索记录数据
	 */
	public void getNearLists() {
		mNearLists = mSearchDB.selectHistoryData();
		if (null == mNearLists || mNearLists.isEmpty()) {
			setHistoryHide(false);
			return;
		}
		setHistoryHide(true);
		setHistoryDataShow();
	}

	public void setHistoryHide(boolean isShow) {
		mInitNearLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
		mHistoryTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
		mHistoryDelet.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	/**
	 * 设置搜索记录的显示
	 */
	public void setHistoryDataShow() {
		mInitNearLayout.removeAllViews();
		for (int i = 0; i < mNearLists.size(); i++) {
			LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.search_history_record_layout,
					null);
			TextView textView = (TextView) view.findViewById(R.id.tv_history_name);
			textView.setOnClickListener(this);
			String title = mNearLists.get(i);
			RefersBean bean = new RefersBean(title, i, "history");
			textView.setText(title);
			textView.setTag(bean);
			textView.setOnClickListener(this);
			mInitNearLayout.addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_position_cancel:
			finish();
			break;
		case R.id.tv_history_name:// 历史记录的点击
			ifShowWordList = false;
			RefersBean bean = (RefersBean) v.getTag();
			mKeyWord = (String) bean.getObject();
			// // 保存关键词
			// mSearchDB.insertHistory(mKeyWord, DateUtils.getCurrentTimeSecond() + "");
			mSearchEdit.setText(mKeyWord);
			// setPullListViewShow();
			mPage = 1;
			getPositionList();
			// setRequestParamsPre(TAG, true);
			break;
		case R.id.textview_flowlayout:// 热词的点击
			ifShowWordList = false;
			RefersBean hotBean = (RefersBean) v.getTag();
			HotSearchBean hot = (HotSearchBean) hotBean.getObject();
			mKeyWord = (String) hot.getTitle();
			// 保存关键词
			mSearchDB.insertHistory(mKeyWord, DateUtils.getCurrentTimeSecond() + "");
			mSearchEdit.setText(mKeyWord);
			// setPullListViewShow();
			mPage = 1;
			getPositionList();
			break;
		case R.id.tv_init_delete_nearrecord:// 清空搜索记录
			mSearchDB.dropTable();
			mNearLists.clear();
			// 设置最近搜索布局隐藏
			setHistoryHide(false);
			break;
		case R.id.tv_filter_position_type:// 选择职位类别
			if (!isPosition) {
				mFilterLayout.setVisibility(View.VISIBLE);
				mShowLayout.removeAllViews();
				mShowLayout.addView(positionFilterDialog.getResultLayout());
				positionFilterDialog.selectPosition();
			} else {
				mFilterLayout.setVisibility(View.GONE);
			}
			isPosition = !isPosition;
			isPosition = getSelectedBg(isPosition, mPositionType.getText().toString(), "职位类别");
			if (!mPositionType.isSelected())
				mPositionType.setSelected(isPosition);
			// String positionFilterStr = mPositionType.getText().toString();
			// if (positionFilterStr.equals("全部") || positionFilterStr.equals("职位类别"))
			// mPositionType.setSelected(isPosition);
			// changeTextColor(1);
			// setTextState(1);
			break;
		case R.id.tv_filter_education:// 选择学历
			if (!isEducation) {
				mFilterLayout.setVisibility(View.VISIBLE);
				mShowLayout.removeAllViews();
				mShowLayout.addView(educationFilterDialog.getResultLayout());
				educationFilterDialog.selectPosition();
			} else {
				mFilterLayout.setVisibility(View.GONE);
			}
			isEducation = !isEducation;
			isEducation = getSelectedBg(isEducation, mEducation.getText().toString(), "学历");
			if (!mEducation.isSelected())
				mEducation.setSelected(isEducation);
			// String educationFilterStr = mEducation.getText().toString();
			// if (educationFilterStr.equals("全部") || educationFilterStr.equals("学历"))
			// mEducation.setSelected(isEducation);
			break;
		case R.id.tv_filter_graducation:// 选择毕业年份
			if (!isGrade) {
				mFilterLayout.setVisibility(View.VISIBLE);
				mShowLayout.removeAllViews();
				mShowLayout.addView(gradeFilterDialog.getResultLayout());
				gradeFilterDialog.selectPosition();
			} else {
				mFilterLayout.setVisibility(View.GONE);
			}
			isGrade = !isGrade;
			isGrade = getSelectedBg(isGrade, mGradeTime.getText().toString(), "毕业年份");
			if (!mGradeTime.isSelected())
				mGradeTime.setSelected(isGrade);
			break;
		case R.id.tv_position_byaddress:// 地址
			SelectPresentAddressActivity.startActionBy(this, null, Finals.REQUESTCODE_ONE, mSelectCity, true);
			break;
		default:
			break;
		}
	}

	public boolean getSelectedBg(boolean isSelected, String textStr, String defaultStr) {
		if (textStr.equals(defaultStr))
			return false;
		return isSelected;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == Finals.REQUESTCODE_ONE) {
			mSelectCity = (CityBean) arg2.getSerializableExtra("selectCity");
			if (null != mSelectCity) {
				mFilterAddress = mSelectCity.getRegion_id();
				String addressName = mSelectCity.getRegion_name();
				if (StringUtils.isNotEmpty(addressName) && addressName.length() <= 2) {
					mSelectedAddressText.setPadding(ActivityUtils.dip2px(SearchPositionActivity.this, 15), 0, 0, 0);
				} else {
					mSelectedAddressText.setPadding(0, 0, 0, 0);
				}
				mSelectedAddressText.setText(mSelectCity.getRegion_name());
				setPullListViewShow();
				mPage = 1;
				getPositionList();
				// setRequestParamsPre(TAG, true);
			}
			return;
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void initView() {
		mLoadMoreLayout = LayoutInflater.from(this).inflate(R.layout.loadmore_layout, null);
		mFilterTopLayout = (LinearLayout) findViewById(R.id.ll_filter_layout);
		mFilterWordLaout = (LinearLayout) findViewById(R.id.filter_word_layout);
		mFilterWordLaout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setFilterKeyWordDismiss();
				setSoftWindowShow();
				return true;
			}
		});
		mShowWordLayout = (LinearLayout) findViewById(R.id.show_word_layout);
		mSelectedAddressText = (TextView) findViewById(R.id.tv_position_byaddress);
		mFilterLayout = (LinearLayout) findViewById(R.id.filter_layout);
		mShowLayout = (LinearLayout) findViewById(R.id.show_layout);
		mPositionType = (TextView) findViewById(R.id.tv_filter_position_type);
		mEducation = (TextView) findViewById(R.id.tv_filter_education);
		mGradeTime = (TextView) findViewById(R.id.tv_filter_graducation);
		mSearchBtn = (TextView) findViewById(R.id.tv_position_editcontent);
		mSearchBtn.setVisibility(View.GONE);
		mSearchEdit = (ClearEditTextView) findViewById(R.id.cet_position_editcontent);
		mSearchEdit.setVisibility(View.VISIBLE);
		mCancelBtn = (TextView) findViewById(R.id.tv_position_cancel);
		mCancelBtn.setOnClickListener(this);
		mPositionType.setOnClickListener(this);
		mEducation.setOnClickListener(this);
		mGradeTime.setOnClickListener(this);
		mSelectedAddressText.setOnClickListener(this);
		mPullListView = (PullToRefreshListView) findViewById(R.id.prlv_positionlist_pull);
		mListView = mPullListView.getRefreshableView();
		mListView.setDividerHeight(1);
		mPullListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mNoneDataLayout = (RelativeLayout) findViewById(R.id.list_null);
		mSearchEdit.addTextChangedListener(this);
		mSearchEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// IME_ACTION_SEARCH搜索
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mKeyWord = v.getText().toString();
					// 保存关键词
					mSearchDB.insertHistory(mKeyWord, DateUtils.getCurrentTimeSecond() + "");
					setFilterKeyWordDismiss();
					setSoftWindowShow();
					setPullListViewShow();
					mPage = 1;
					getPositionList();
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
		mFilterLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mFilterLayout.setVisibility(View.GONE);
				// setTextState(40);
				return true;
			}
		});
	}

	/**
	 * 获取没有搜索结果的布局
	 */
	@SuppressLint("InflateParams")
	public void getNoneDataLayout() {
		noneLayout = LayoutInflater.from(this).inflate(R.layout.none_position_layout, null);
		LayoutParams layoutParams = new LayoutParams(ActivityUtils.getScreenWidth(), ActivityUtils.getScreenHeight());
		noneLayout.setLayoutParams(layoutParams);
		mFlowLayout = (FlowLayout) noneLayout.findViewById(R.id.fl_hot_search);
		mFlowLayout.setHorizontalSpacing(ActivityUtils.dip2px(this, 7));
		mFlowLayout.setVerticalSpacing(ActivityUtils.dip2px(this, 7));
	}

	/**
	 * 获取初次进来时需要显示的界面
	 */
	public void getInitLayout() {
		mInitLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.search_initview_layout, null);
		mInitLayout.setLayoutParams(LayoutParamsUtils.geLineartLayoutParams(ActivityUtils.getScreenWidth(),
				ActivityUtils.getScreenHeight()));
		mInitHotLayout = (FlowLayout) mInitLayout.findViewById(R.id.fl_init_hot_search);
		mInitHotLayout.setHorizontalSpacing(ActivityUtils.dip2px(this, 7));
		mInitHotLayout.setVerticalSpacing(ActivityUtils.dip2px(this, 7));
		mInitNearLayout = (FlowLayout) mInitLayout.findViewById(R.id.fl_init_near_search);
		mHistoryTitle = (TextView) mInitLayout.findViewById(R.id.tv_init_near_title);
		mHistoryDelet = (TextView) mInitLayout.findViewById(R.id.tv_init_delete_nearrecord);
		mHistoryDelet.setOnClickListener(this);
		setInitLayoutShow();
	}

	/**
	 * 初始时显示
	 */
	public void setInitLayoutShow() {
		if (mFilterTopLayout.getVisibility() == View.VISIBLE)
			mFilterTopLayout.setVisibility(View.GONE);
		mNoneDataLayout.removeAllViews();
		mNoneDataLayout.addView(mInitLayout);
		if (mNoneDataLayout.getVisibility() == View.GONE)
			mNoneDataLayout.setVisibility(View.VISIBLE);
		if (mPullListView.getVisibility() == View.VISIBLE)
			mPullListView.setVisibility(View.GONE);
	}

	/**
	 * 设置没有搜索结果的布局显示
	 */
	public void setNoneLayoutShow() {
		mNoneDataLayout.removeAllViews();
		mNoneDataLayout.addView(noneLayout);
		if (mNoneDataLayout.getVisibility() == View.GONE)
			mNoneDataLayout.setVisibility(View.VISIBLE);
		if (mPullListView.getVisibility() == View.VISIBLE)
			mPullListView.setVisibility(View.GONE);
	}

	/**
	 * 设置有搜索结果的布局显示
	 */
	public void setPullListViewShow() {
		if (mFilterWordLaout.getVisibility() == View.VISIBLE)
			mFilterTopLayout.setVisibility(View.GONE);
		if (mNoneDataLayout.getVisibility() == View.VISIBLE)
			mNoneDataLayout.setVisibility(View.GONE);
		if (mPullListView.getVisibility() == View.GONE)
			mPullListView.setVisibility(View.VISIBLE);
		if (mFilterTopLayout.getVisibility() == View.GONE)
			mFilterTopLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化筛选对话框
	 */
	public void initFilterDialog() {
		positionFilterDialog = new FilterDialog(this);
		educationFilterDialog = new FilterDialog(this);
		gradeFilterDialog = new FilterDialog(this);
	}

	/**
	 * 获取学历数据
	 */
	public void getFilterEducationInfo() {
		String[] education = getResources().getStringArray(R.array.education);
		educationList = new ArrayList<MapFormatBean>(education.length + 1);
		educationList.add(new MapFormatBean("", "全部"));
		for (int i = 0; i < education.length; i++) {
			educationList.add(new MapFormatBean(String.valueOf(i + 1), education[i]));
		}
	}

	/***
	 * 获取毕业年份当前年份的前两年和后四年
	 */
	public void getFilterGradeInfo() {
		gradeLists = new ArrayList<MapFormatBean>(7);
		int currentYear = DateUtils.getCurrentYear();
		int startYear = currentYear - 2;
		gradeLists.add(new MapFormatBean("", "全部"));
		for (int i = 0; i < 7; i++) {
			gradeLists.add(new MapFormatBean(String.valueOf(i + 1), String.valueOf(startYear++)));
		}
	}

	/**
	 * 设置筛选框数据
	 */
	private void setFilterData() {
		setPositionData();
		setEducationData();
		setGradeData();
	}

	/**
	 * 改变点击筛选条件时字体的颜色
	 * 
	 * @param index
	 */
	public void changeTextColor(int index) {
		mPositionType.setSelected(index == 1 ? true : false);
		mEducation.setSelected(index == 2 ? true : false);
		mGradeTime.setSelected(index == 3 ? true : false);
	}

	/**
	 * 设置职位类别列表数据
	 */
	public void setPositionData() {
		positionFilterDialog.setAdapter(cateLists);
		positionFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				mFilterPosition = cateLists.get(Position).getKey();
				mFilterLayout.setVisibility(View.GONE);
				// setTextState(40);
				if (cateLists.get(Position).getValue().equals("全部")) {
					mPositionType.setText("职位类别");
					mPositionType.setSelected(false);
				} else {
					mPositionType.setText(cateLists.get(Position).getValue());
					mPositionType.setSelected(true);
				}
				// mPositionType.setText(cateLists.get(Position).getValue());
				// if (cateLists.get(Position).getValue().equals("全部")) {
				// mPositionType.setSelected(false);
				// } else {
				// mPositionType.setSelected(true);
				// }

				// changeTextColor(1);
				setPullListViewShow();
				mPage = 1;
				getPositionList();
			}
		});
	}

	public void setEducationData() {
		educationFilterDialog.setAdapter(educationList);
		educationFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				mFilterEducation = educationList.get(Position).getKey();
				mFilterLayout.setVisibility(View.GONE);
				if (educationList.get(Position).getValue().equals("全部")) {
					mEducation.setText("学历");
					mEducation.setSelected(false);
				} else {
					mEducation.setText(educationList.get(Position).getValue());
					mEducation.setSelected(true);
				}
				// mEducation.setText(educationList.get(Position).getValue());
				// if (educationList.get(Position).getValue().equals("全部")) {
				// mEducation.setSelected(false);
				// } else {
				// mEducation.setSelected(true);
				// }
				// changeTextColor(2);
				setPullListViewShow();
				mPage = 1;
				getPositionList();
				// setRequestParamsPre(TAG, true);
			}
		});

	}

	public void setGradeData() {
		gradeFilterDialog.setAdapter(gradeLists);
		gradeFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				mFilterGrade = gradeLists.get(Position).getValue();
				mFilterLayout.setVisibility(View.GONE);
				mGradeTime.setText(gradeLists.get(Position).getValue());
				if (gradeLists.get(Position).getValue().equals("全部")) {
					mGradeTime.setText("毕业年份");
					mGradeTime.setSelected(false);
				} else {
					mGradeTime.setText(gradeLists.get(Position).getValue());
					mGradeTime.setSelected(true);
				}
				// if (gradeLists.get(Position).getValue().equals("全部")) {
				// mGradeTime.setSelected(false);
				// } else {
				// mGradeTime.setSelected(true);
				// }
				// changeTextColor(3);
				setPullListViewShow();
				mPage = 1;
				getPositionList();
				// setRequestParamsPre(TAG, true);
			}
		});
	}

	// /**
	// * 设置筛选按钮点击后的状态
	// *
	// * @param page
	// */
	// public void setTextState(int page) {
	// switch (page) {
	// case 1:
	// positionText(true);
	// educationText(false);
	// gradeTimeText(false);
	// break;
	// case 2:
	// positionText(false);
	// educationText(true);
	// gradeTimeText(false);
	// break;
	// case 3:
	// positionText(false);
	// educationText(false);
	// gradeTimeText(true);
	// break;
	// default:
	// positionText(false);
	// educationText(false);
	// gradeTimeText(false);
	// break;
	// }
	// }

	/**
	 * 改变选中状态 mPositionType, mEducation, mGradeTime
	 * 
	 * @param isSelected
	 */
	public void positionText(boolean isSelected) {
		if (isSelected) {
			mPositionType.setTextColor(getResources().getColor(R.color.selected_font_color));
			Drawable drawable = this.getResources().getDrawable(R.drawable.selectlan);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			mPositionType.setCompoundDrawables(null, null, drawable, null);
		} else {
			mPositionType.setTextColor(getResources().getColor(R.color.form_value_color));
			Drawable drawable = this.getResources().getDrawable(R.drawable.select_search);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			mPositionType.setCompoundDrawables(null, null, drawable, null);
		}
	}

	public void educationText(boolean isSelected) {
		if (isSelected) {
			mEducation.setTextColor(getResources().getColor(R.color.selected_font_color));
			Drawable drawable = this.getResources().getDrawable(R.drawable.selectlan);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			mEducation.setCompoundDrawables(null, null, drawable, null);
		} else {
			mEducation.setTextColor(getResources().getColor(R.color.form_value_color));
			Drawable drawable = this.getResources().getDrawable(R.drawable.select_search);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			mEducation.setCompoundDrawables(null, null, drawable, null);
		}
	}

	public void gradeTimeText(boolean isSelected) {
		if (isSelected) {
			mGradeTime.setTextColor(getResources().getColor(R.color.selected_font_color));
			Drawable drawable = this.getResources().getDrawable(R.drawable.selectlan);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			mGradeTime.setCompoundDrawables(null, null, drawable, null);
		} else {
			mGradeTime.setTextColor(getResources().getColor(R.color.form_value_color));
			Drawable drawable = this.getResources().getDrawable(R.drawable.select_search);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			mGradeTime.setCompoundDrawables(null, null, drawable, null);
		}
	}

	// @Override
	// public void setRequestParams(String className) {
	// super.setRequestParams(className);
	// if (!isfirst && !isGetKeyWord) {
	// getPositionList();
	// }
	// if (!isfirst && isGetKeyWord) {
	// myProgressDialog.dismiss();
	// getKeyWordList();
	// }
	// if (isfirst && null == mHotLists) {
	// getHotSearch();
	// }
	// if (isfirst && null == cateLists) {
	// getFilterPositionInfo();
	// }
	// }

	// /**
	// * 获取职位列表
	// */
	// public void getPositionList() {
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("ticket", CacheUtils.getToken(this));
	// map.put("page", String.valueOf(mPage));
	// map.put("pageNumber", String.valueOf(mPageNumber));
	// if (StringUtils.isNotEmpty(CacheUtils.getStudentId(this))) {
	// map.put("student_id", CacheUtils.getStudentId(this));
	// }
	// if (StringUtils.isNotEmpty(mFilterAddress)) {
	// map.put("city_id", mFilterAddress);
	// }
	// if (StringUtils.isNotEmpty(mFilterEducation)) {
	// map.put("edu", mFilterEducation);
	// }
	// if (StringUtils.isNotEmpty(mFilterGrade)) {
	// map.put("grad_year", mFilterGrade);
	// }
	// if (StringUtils.isNotEmpty(mFilterPosition)) {
	// map.put("cate_id", mFilterPosition);
	// }
	// if (StringUtils.isNotEmpty(mKeyWord)) {
	// map.put("keyword", mKeyWord);
	// }
	// JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
	// + GlobalContants.GETPOSITION_FILTERLIST_URL, map, new RequestResponseLinstner(this), this);
	// BaseApplication.mApp.addToRequestQueue(request, TAG);
	// }

	// /**
	// * 获取热搜词汇
	// */
	// public void getHotSearch() {
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("ticket", CacheUtils.getToken(this));
	// map.put("range", "2");
	// JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
	// + GlobalContants.GETHOTSEARCH_URL, map, new RequestResponseLinstner(new ResponseCallBack() {
	// @Override
	// public void getResponse(JSONObject response) {
	// myProgressDialog.dismiss();
	// try {
	// HttpRes httpRes = TransFormModel.getResponseData(response);
	// if (httpRes.getReturnCode() != 0) {
	// ToastUtils.showToastInCenter(httpRes.getReturnDesc());
	// return;
	// }
	// String hotStr = httpRes.getReturnData();
	// if (StringUtils.isEmpty(hotStr) || hotStr.length() <= 2) {
	//
	// return;
	// }
	// mHotLists = (ArrayList<HotSearchBean>) TransFormModel.getResponseResults(hotStr,
	// HotSearchBean.class);
	// isfirst = false;
	// mYHandler.sendEmptyMessage(0);
	// } catch (Exception e) {
	// ToastUtils.showToastInCenter("数据解析错误");
	// }
	//
	// }
	// }), this);
	// BaseApplication.mApp.addToRequestQueue(request, TAG);
	// }

	// /**
	// * 搜索结果
	// */
	// @Override
	// public void getResponse(JSONObject response) {
	// super.getResponse(response);
	// myProgressDialog.dismiss();
	// mPullListView.onRefreshComplete();
	// try {
	// HttpRes httpRes = TransFormModel.getResponseData(response);
	// if (httpRes.getReturnCode() != 0) {
	// ToastUtils.showToastInCenter(httpRes.getReturnDesc());
	// mYHandler.sendEmptyMessage(2);
	// return;
	// }
	// JSONObject object = new JSONObject(httpRes.getReturnData());
	// String resultData = object.getString("post");
	//
	// mAllCount = NumberUtils.getInt(object.getString("count"), 0);
	// if (mAllCount == 0) {
	// mPositionLists = null;
	// mYHandler.sendEmptyMessage(2);
	// return;
	// }
	// getListSuccess(resultData);
	// } catch (Exception e) {
	// ToastUtils.showToastInCenter("数据解析错误");
	// }
	// }

	/**
	 * 搜索结果
	 * 
	 * @param resultData
	 * @throws Exception
	 */
	public void getListSuccess(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			mYHandler.sendEmptyMessage(0);
			return;
		}
		if (mPage > 1) {// 非第一次加载
			mPositionLists.addAll((ArrayList<PositionBean>) TransFormModel.getResponseResults(resultData,
					PositionBean.class));
		} else {
			mPositionLists = (ArrayList<PositionBean>) TransFormModel
					.getResponseResults(resultData, PositionBean.class);
		}
		// 判断是否有更多数据
		if (mAllCount - mPositionLists.size() > 0) {// 有更多数据
			if (mListView.getFooterViewsCount() == 0) {
				isLoadMore = true;
				mListView.addFooterView(mLoadMoreLayout);
			}
		} else {
			if (mListView.getFooterViewsCount() != 0) {
				isLoadMore = false;
				mListView.removeFooterView(mLoadMoreLayout);
			}
		}
		setDataShow();
		mPage++;
	}

	public void setDataShow() {
		if (null == mAdapter || mPage * mPageNumber <= mPageNumber) {// 第一次加载或者刷新的显示
			mAdapter = null;
			// mAdapter = new SeachPositionAdapter(this, mPositionLists);
			mAdapter = new SeachPositionAdapter(this, mPositionLists, R.layout.tab_position_item_layout);
			// mAdapter = new SeachPositionAdapter(this, mPositionLists, R.layout.search_position_item_layout);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 设置热搜词汇显示
	 */
	@SuppressLint("InflateParams")
	public void setHotSearchShow(FlowLayout flowLayout) {
		if (null == mHotLists || mHotLists.isEmpty())
			return;
		flowLayout.removeAllViews();
		for (int i = 0; i < mHotLists.size(); i++) {
			TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.flowlayout_textview_layout, null);
			textView.setOnClickListener(this);
			HotSearchBean hotBean = mHotLists.get(i);
			RefersBean bean = new RefersBean(hotBean, i, "hot");
			textView.setText(hotBean.getTitle());
			textView.setTag(bean);
			textView.setOnClickListener(this);
			flowLayout.addView(textView);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PositionBean bean = mPositionLists.get(position - mListView.getHeaderViewsCount());
		PositionDetailActivity.startAction(SearchPositionActivity.this, bean.getPkid(),1);
	}

	@SuppressLint("HandlerLeak")
	Handler mYHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == 1) {
				setFilterData();
				return;
			}
			if (msg.what == 0) {
				setHotSearchShow(mInitHotLayout);
				return;
			}
			if (msg.what == 2) {
				setNoneLayoutShow();
				setHotSearchShow(mFlowLayout);
			}
		}

	};

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 */
	private void filterData(String filterStr) {
		mKeyWord = filterStr;
		mPage = 1;
		setKeyWordLayoutShow();
		// 获取关键词
		getKeyWordList();
		// setRequestParamsPre(TAG, false);
	}

	/**
	 * 设置关键列表的显示
	 */
	public void setKeyWordLayoutShow() {
		mFilterTopLayout.setVisibility(View.GONE);
		mFilterWordLaout.setVisibility(View.VISIBLE);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.toString().trim().length() <= 0) {
			mKeyWord = "";
			setFilterKeyWordDismiss();
			return;
		}
		if (ifShowWordList) {
			filterData(s.toString());
		}
		ifShowWordList = true;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	/**
	 * 关键词匹配列表的点击
	 */
	@Override
	public void select(int Position, View view, long ViewId) {
		mKeyWord = mKeyWords.get(Position).getTitle();
		mSearchEdit.setText(mKeyWord);
		// 保存关键词
		mSearchDB.insertHistory(mKeyWord, DateUtils.getCurrentTimeSecond() + "");
		setFilterKeyWordDismiss();
		setSoftWindowShow();
		setPullListViewShow();
		mPage = 1;
		getPositionList();
		// setRequestParamsPre(TAG, true);
	}

	/**
	 * 设置筛选匹配列表的消失
	 */
	public void setFilterKeyWordDismiss() {
		mkeyWordDialog = null;
		mFilterWordLaout.setVisibility(View.GONE);
	}

	public void setSoftWindowShow() {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		mPage = 1;
		getPositionList();
		// setRequestParamsPre(TAG, false);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (null == mPositionLists || mPositionLists.isEmpty())
			return;
		boolean scrollEnd = false;// 判断是否滚动到底部
		try {
			if (view.getPositionForView(mLoadMoreLayout) == view.getLastVisiblePosition())
				scrollEnd = true;
		} catch (Exception e) {
			scrollEnd = false;
		}
		if (isLoadMore && scrollEnd) {
			if (ActivityUtils.isNetAvailable()) {
				showLoadingControl(true);
				getPositionList();
			} else {
				ToastUtils.showToastInCenter("网络未连接");
			}
		}
	}

	/**
	 * 是否显示加载更多
	 * 
	 * @param isShow
	 */
	private void showLoadingControl(boolean isShow) {
		mLoadMoreLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

}

package com.shixi.gaodun.view.activity.enterprise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.SeachPositionAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewActivity;
import com.shixi.gaodun.db.SearchDB;
import com.shixi.gaodun.inf.ResponseCallBack;
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
import com.umeng.analytics.MobclickAgent;

/**
 * 职位搜索页
 * 
 * @author ronger
 * @date:2015-12-22 上午11:12:45
 */
@SuppressLint("InflateParams")
public class PositionSearchActivity extends BaseListViewActivity<PositionBean>
		implements TextWatcher, onKeyWordItemClickListener {
	// 热搜+历史搜索控件
	private LinearLayout mInitLayout;
	private FlowLayout mInitHotLayout, mInitNearLayout;
	private TextView mHistoryTitle, mHistoryDelet;
	// 历史搜索
	private SearchDB mSearchDB;
	private ArrayList<String> mNearLists;
	// 搜索关键词, 搜索框关联匹配相关
	private LinearLayout mFilterTopLayout;
	private LinearLayout mFilterWordLayout, mShowWordLayout;
	private TextView mCancelBtn;// 取消
	private ClearEditTextView mSearchEdit;// 编辑框
	private TextView mSelectedAddressText;
	// 筛选相关
	private TextView mPositionType, mEducation, mGradeTime;
	private LinearLayout mFilterLayout, mShowLayout;
	private FilterDialog positionFilterDialog, educationFilterDialog,
			gradeFilterDialog;
	// 错误或者没有数据时的展示容器
	private RelativeLayout mNoneDataLayout;
	// 没有搜索结果时
	private FlowLayout mFlowLayout;
	private View noneLayout;
	// 筛选对话框的数据（职位分类、学历、毕业年份）
	private ArrayList<MapFormatBean> educationList, gradeLists, cateLists;
	// 热词
	private ArrayList<HotSearchBean> mHotLists;
	// 搜索关键词
	private ArrayList<KeyWordBean> mKeyWords;
	private KeyWordFilterDialog mkeyWordDialog;
	// 选择热搜词汇或者历史搜索后会改变编辑框，此处保证在选择热搜词汇和历史记录后匹配框不因编辑框文字的改变而弹出
	private boolean ifShowWordList = true;
	// 参数（地址、职位分类、学历、毕业年份、热词、历史搜索）
	private String mFilterAddress, mFilterPosition, mFilterEducation,
			mFilterGrade, mKeyWord;
	// 临时用的城市对象
	private CityBean mSelectCity;
	// 是否显示筛选对话框
	private boolean isShowFilterPosition, isShowFilterEducation,
			isShowFilterGrade;
	// 请求分类:1热词2职位类别3搜索0热词+职位类别+搜索
	private int mRequestType = 0;

	public static void startAction(Activity context) {
		Intent intent = new Intent(context, PositionSearchActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.search_position_layout);
		isExcute = false;
		mSearchDB = new SearchDB(this);
		// 初始时的显示
		initHotAndHistoryLayout();
		// 历史记录的显示
		getHistoryDBLists();
		// 初始没有搜索结果时的布局
		getNoneDataLayout();
		// 初始化筛选分类对话框
		initFilterDialog();
		// 热词数据的获取
		// 筛选数据的获取：职位类别、学历、毕业年份
		getFilterEducationInfo();
		getFilterGradeInfo();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mRequestType = 0;
		setRequestParamsPre(TAG, true);
	}

	public void refreshSearchResult(boolean isShowProgress) {
		mRequestType = 3;
		mPage = 1;
		setRequestParamsPre(TAG, isShowProgress);
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
			mSearchEdit.setText(mKeyWord);
			refreshSearchResult(true);
			break;
		case R.id.textview_flowlayout:// 热词的点击
			ifShowWordList = false;
			RefersBean hotBean = (RefersBean) v.getTag();
			HotSearchBean hot = (HotSearchBean) hotBean.getObject();
			mKeyWord = (String) hot.getTitle();
			// 保存关键词
			mSearchDB.insertHistory(mKeyWord, DateUtils.getCurrentTimeSecond()
					+ "");
			mSearchEdit.setText(mKeyWord);
			refreshSearchResult(true);
			break;
		case R.id.tv_init_delete_nearrecord:// 清空搜索记录
			mSearchDB.dropTable();
			mNearLists.clear();
			// 设置最近搜索布局隐藏
			setHistoryHide(false);
			break;
		case R.id.tv_filter_position_type:// 选择职位类别
			if (isShowFilterPosition) {
				mFilterLayout.setVisibility(View.GONE);
				isShowFilterPosition = !isShowFilterPosition;
				break;
			}
			mFilterLayout.setVisibility(View.VISIBLE);
			mShowLayout.removeAllViews();
			mShowLayout.addView(positionFilterDialog.getResultLayout());
			isShowFilterPosition = !isShowFilterPosition;
			positionFilterDialog.selectPosition();
			// if (!isShowFilterPosition) {
			// mFilterLayout.setVisibility(View.VISIBLE);
			// mShowLayout.removeAllViews();
			// mShowLayout.addView(positionFilterDialog.getResultLayout());
			// positionFilterDialog.selectPosition();
			// } else {
			// mFilterLayout.setVisibility(View.GONE);
			// }
			// isShowFilterPosition = !isShowFilterPosition;
			// if (!mPositionType.isSelected())
			// mPositionType.setSelected(isShowFilterPosition);
			break;
		case R.id.tv_filter_education:// 选择学历
			if (isShowFilterEducation) {
				isShowFilterEducation = !isShowFilterEducation;
				mFilterLayout.setVisibility(View.GONE);
				break;
			}
			mFilterLayout.setVisibility(View.VISIBLE);
			mShowLayout.removeAllViews();
			mShowLayout.addView(educationFilterDialog.getResultLayout());
			isShowFilterEducation = !isShowFilterEducation;
			educationFilterDialog.selectPosition();
			// if (!isShowFilterEducation) {
			// mFilterLayout.setVisibility(View.VISIBLE);
			// mShowLayout.removeAllViews();
			// mShowLayout.addView(educationFilterDialog.getResultLayout());
			// educationFilterDialog.selectPosition();
			// } else {
			// mFilterLayout.setVisibility(View.GONE);
			// }
			// isShowFilterEducation = !isShowFilterEducation;
			// if (!mEducation.isSelected())
			// mEducation.setSelected(isShowFilterEducation);
			break;
		case R.id.tv_filter_graducation:// 选择毕业年份
			if (isShowFilterGrade) {
				isShowFilterGrade = !isShowFilterGrade;
				mFilterLayout.setVisibility(View.GONE);
				break;
			}
			mFilterLayout.setVisibility(View.VISIBLE);
			mShowLayout.removeAllViews();
			mShowLayout.addView(gradeFilterDialog.getResultLayout());
			isShowFilterGrade = !isShowFilterGrade;
			gradeFilterDialog.selectPosition();
			// if (!isShowFilterGrade) {
			// mFilterLayout.setVisibility(View.VISIBLE);
			// mShowLayout.removeAllViews();
			// mShowLayout.addView(gradeFilterDialog.getResultLayout());
			// gradeFilterDialog.selectPosition();
			// } else {
			// mFilterLayout.setVisibility(View.GONE);
			// }
			// isShowFilterGrade = !isShowFilterGrade;
			// if (!mGradeTime.isSelected())
			// mGradeTime.setSelected(isShowFilterGrade);
			break;
		case R.id.tv_position_byaddress:// 地址
			SelectPresentAddressActivity.startActionBy(this, null,
					Finals.REQUESTCODE_ONE, mSelectCity, true);
			break;
		default:
			break;
		}
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
				// if (StringUtils.isNotEmpty(addressName) &&
				// addressName.length() <= 2) {
				// mSelectedAddressText.setPadding(ActivityUtils.dip2px(PositionSearchActivity.this,
				// 15), 0, 0, 0);
				// } else {
				// mSelectedAddressText.setPadding(0, 0, 0, 0);
				// }
				mSelectedAddressText.setText(mSelectCity.getRegion_name());
				refreshSearchResult(true);
				// setPullListViewShow();
				// mPage = 1;
				// getPositionList();
				// setRequestParamsPre(TAG, true);
			}
			return;
		}
	}

	/**
	 * 获取没有搜索结果的布局
	 */
	@SuppressLint("InflateParams")
	public void getNoneDataLayout() {
		noneLayout = LayoutInflater.from(this).inflate(
				R.layout.none_position_layout, null);
		LayoutParams layoutParams = new LayoutParams(
				ActivityUtils.getScreenWidth(), ActivityUtils.getScreenHeight());
		noneLayout.setLayoutParams(layoutParams);
		mFlowLayout = (FlowLayout) noneLayout.findViewById(R.id.fl_hot_search);
		mFlowLayout.setHorizontalSpacing(ActivityUtils.dip2px(this, 7));
		mFlowLayout.setVerticalSpacing(ActivityUtils.dip2px(this, 7));
	}

	/**
	 * 初始化筛选对话框
	 */
	public void initFilterDialog() {
		positionFilterDialog = new FilterDialog(this);
		educationFilterDialog = new FilterDialog(this);
		gradeFilterDialog = new FilterDialog(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void initView() {
		super.initView();
		mTitleLayout.setVisibility(View.GONE);
		// 关键词匹配相关
		mFilterTopLayout = (LinearLayout) findViewById(R.id.ll_filter_layout);
		mFilterWordLayout = (LinearLayout) findViewById(R.id.filter_word_layout);
		// 匹配框整体触摸后隐藏匹配框，隐藏软键盘
		mFilterWordLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setFilterKeyWordDismiss();
				// setSoftWindowShow();
				return true;
			}
		});
		mShowWordLayout = (LinearLayout) findViewById(R.id.show_word_layout);
		mSearchEdit = (ClearEditTextView) findViewById(R.id.cet_position_editcontent);
		// 地址相关
		mSelectedAddressText = (TextView) findViewById(R.id.tv_position_byaddress);
		mSelectedAddressText.setOnClickListener(this);
		// 筛选相关
		mFilterLayout = (LinearLayout) findViewById(R.id.filter_layout);
		mShowLayout = (LinearLayout) findViewById(R.id.show_layout);
		mPositionType = (TextView) findViewById(R.id.tv_filter_position_type);
		mPositionType.setEllipsize(TruncateAt.MIDDLE);
		float mFontWidth = mPositionType.getTextSize() * 5;
		mPositionType.setMaxWidth((int) mFontWidth);
		mEducation = (TextView) findViewById(R.id.tv_filter_education);
		mGradeTime = (TextView) findViewById(R.id.tv_filter_graducation);

		mPositionType.setOnClickListener(this);
		mEducation.setOnClickListener(this);
		mGradeTime.setOnClickListener(this);
		// 取消
		mCancelBtn = (TextView) findViewById(R.id.tv_position_cancel);
		mCancelBtn.setOnClickListener(this);
		// 搜索结果相关
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.prlv_positionlist_pull);
		myListView = mPullToRefreshListView.getRefreshableView();
		myListView.setDividerHeight(0);
		myListView.setOnItemClickListener(this);
		setRefreshOrLoadMoreListener();
		// 没有数据或者错误时显示的控件容器
		mNoneDataLayout = (RelativeLayout) findViewById(R.id.list_null);
		setEditFrameLinstener();
		// 筛选框的背景的触摸事件：隐藏筛选框
		mFilterLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				setFilterLayoutGone();
				return true;
			}
		});
	}

	public void setFilterLayoutGone() {
		mFilterLayout.setVisibility(View.GONE);
		isShowFilterEducation = false;
		isShowFilterGrade = false;
		isShowFilterPosition = false;
	}

	/**
	 * 1热词2职位类别3搜索4匹配列表0热词+职位类别
	 */
	@Override
	public void setRequestParams(String className) {
		super.setRequestParams(className);
		if (mRequestType == 0) {
			getHotWordList();
			// getFilterPositionList();
			return;
		}
		if (mRequestType == 1) {
			getHotWordList();
			return;
		}
		if (mRequestType == 2) {
			getFilterPositionList();
			return;
		}
		if (mRequestType == 3) {
			getSearchResultList();
			return;
		}
		if (mRequestType == 4) {
			getKeyWordList();
			return;
		}
	}

	/**
	 * 搜索结果
	 */
	@Override
	public void getResponse(JSONObject response) {
		super.getResponse(response);
		try {
			HttpRes httpRes = getRequestSuccess(response, false);
			if (null == httpRes || !httpRes.isGoOn())
				return;
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String resultData = object.getString("post");
			getResponseComplete();
			mListTotal = NumberUtils.getInt(object.getString("count"), 0);
			if (mListTotal == 0) {
				mLists = null;
				setNoneLayoutShow();
				setHotWordDataShow(mFlowLayout);
				// mYHandler.sendEmptyMessage(2);
				return;
			}
			setPullListViewShow();
			List<PositionBean> lists = TransFormModel.getResponseResults(
					resultData, PositionBean.class);
			getListSuccess(lists);
		} catch (Exception e) {
			getResponseComplete();
			exceptionToast();
		}
	}

	/**
	 * 获取搜索结果（职位列表）
	 */
	public void getSearchResultList() {
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
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.GETPOSITION_FILTERLIST_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取热词列表
	 */
	public void getHotWordList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("range", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETHOTSEARCH_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						dissMissProgress();
						try {
							if (mRequestType == 0) {
								getFilterPositionList();
							}
							HttpRes httpRes = getRequestSuccess(response, true);
							if (null == httpRes || !httpRes.isGoOn()) {
								return;
							}
							String hotStr = httpRes.getReturnData();
							mHotLists = (ArrayList<HotSearchBean>) TransFormModel
									.getResponseResults(hotStr,
											HotSearchBean.class);
							setHotWordDataShow(mInitHotLayout);
						} catch (Exception e) {
							exceptionToast();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 设置热搜词汇显示
	 */
	@SuppressLint("InflateParams")
	public void setHotWordDataShow(FlowLayout flowLayout) {
		if (null == mHotLists || mHotLists.isEmpty())
			return;
		flowLayout.removeAllViews();
		for (int i = 0; i < mHotLists.size(); i++) {
			TextView textView = (TextView) LayoutInflater.from(this).inflate(
					R.layout.flowlayout_textview_layout, null);
			textView.setOnClickListener(this);
			HotSearchBean hotBean = mHotLists.get(i);
			RefersBean bean = new RefersBean(hotBean, i, "hot");
			textView.setText(hotBean.getTitle());
			textView.setTag(bean);
			textView.setOnClickListener(this);
			flowLayout.addView(textView);
		}
	}

	/**
	 * 获取学历数据
	 */
	public void getFilterEducationInfo() {
		String[] education = getResources().getStringArray(R.array.education);
		educationList = new ArrayList<MapFormatBean>(education.length + 1);
		educationList.add(new MapFormatBean("", "全部"));
		for (int i = 0; i < education.length; i++) {
			educationList.add(new MapFormatBean(String.valueOf(i + 1),
					education[i]));
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

			gradeLists.add(new MapFormatBean(String.valueOf(i + 1), startYear
					+ "年"));
			startYear++;
		}
	}

	/**
	 * 获取职位类别列表
	 */
	public void getFilterPositionList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.CATEGORYLIST_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						dissMissProgress();
						try {
							HttpRes httpRes = getRequestSuccess(response, true);
							if (null == httpRes || !httpRes.isGoOn()) {
								return;
							}
							String resultData = httpRes.getReturnData();
							cateLists = TransFormModel.getCateLists(
									new JSONArray(resultData), true);
							// 设置筛选框数据
							setFilterData();
							// mYHandler.sendEmptyMessage(1);
						} catch (Exception e) {
							exceptionToast();
						}
					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取关键词匹配列表
	 */
	public void getKeyWordList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(this));
		map.put("keyword", mKeyWord);
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETKEYWORD_URL, map,
				new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							try {
								HttpRes httpRes = getRequestSuccess(response,
										false);
								if (null == httpRes || !httpRes.isGoOn())
									return;
								JSONObject jsonObject = new JSONObject(httpRes
										.getReturnData());
								String postkey = jsonObject
										.getString("postkey");
								String enterprisekey = jsonObject
										.getString("enterprisekey");
								mKeyWords = new ArrayList<KeyWordBean>(0);
								if (StringUtils.isNotEmpty(postkey)
										&& postkey.length() > 2) {
									mKeyWords.addAll(TransFormModel
											.getResponseResults(postkey,
													KeyWordBean.class));
								}
								if (StringUtils.isNotEmpty(enterprisekey)
										&& enterprisekey.length() > 2)
									mKeyWords.addAll(TransFormModel
											.getResponseResults(enterprisekey,
													KeyWordBean.class));
								if (mKeyWords.isEmpty()) {
									// 隐藏对话框
									setFilterKeyWordDismiss();
									return;
								}
								setKeyWordLayoutShow();
								if (null == mkeyWordDialog) {
									mkeyWordDialog = new KeyWordFilterDialog(
											PositionSearchActivity.this);
									mkeyWordDialog
											.setSelectListener(PositionSearchActivity.this);
									mShowWordLayout.removeAllViews();
									mShowWordLayout.addView(mkeyWordDialog
											.getResultLayout());
									mkeyWordDialog.setAdapter(mKeyWords,
											mKeyWord);
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
	 * 设置筛选框数据
	 */
	private void setFilterData() {
		setFilterPositionData();
		setFilterEducationData();
		setFilterGradeData();
	}

	/**
	 * 设置职位类别数据显示
	 */
	public void setFilterPositionData() {
		positionFilterDialog.setAdapter(cateLists);
		// 筛选
		positionFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				mFilterPosition = cateLists.get(Position).getKey();
				// mFilterLayout.setVisibility(View.GONE);
				// isShowFilterPosition = false;
				setFilterLayoutGone();

				if (cateLists.get(Position).getValue().equals("全部")) {
					mPositionType.setText("职位类别");
					mPositionType.setSelected(false);
				} else {
					mPositionType.setText(cateLists.get(Position).getValue());
					mPositionType.setSelected(true);
				}
				refreshSearchResult(true);
				// setPullListViewShow();
				// mPage = 1;
				// getPositionList();
			}
		});
	}

	/**
	 * 设置学历
	 */
	public void setFilterEducationData() {
		educationFilterDialog.setAdapter(educationList);
		educationFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				mFilterEducation = educationList.get(Position).getKey();
				setFilterLayoutGone();
				// mFilterLayout.setVisibility(View.GONE);
				// isShowFilterEducation = false;

				if (educationList.get(Position).getValue().equals("全部")) {
					mEducation.setText("学历");
					mEducation.setSelected(false);
				} else {
					mEducation.setText(educationList.get(Position).getValue());
					mEducation.setSelected(true);
				}
				refreshSearchResult(true);
			}
		});
	}

	/**
	 * 设置毕业年份
	 */
	public void setFilterGradeData() {
		gradeFilterDialog.setAdapter(gradeLists);
		gradeFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				mFilterGrade = gradeLists.get(Position).getValue();
				setFilterLayoutGone();
				// mFilterLayout.setVisibility(View.GONE);
				// isShowFilterGrade = false;

				if (gradeLists.get(Position).getValue().equals("全部")) {
					mGradeTime.setText("毕业年份");
					mGradeTime.setSelected(false);
				} else {
					mGradeTime.setText(gradeLists.get(Position).getValue());
					mGradeTime.setSelected(true);
				}
				refreshSearchResult(true);
			}
		});
	}

	/**
	 * 设置编辑框的各种监听
	 */
	public void setEditFrameLinstener() {
		mSearchEdit.addTextChangedListener(this);
		// 监听按键
		mSearchEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// IME_ACTION_SEARCH搜索
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					mKeyWord = v.getText().toString();
					// 保存关键词至历史记录
					mSearchDB.insertHistory(mKeyWord,
							DateUtils.getCurrentTimeSecond() + "");
					// 隐藏匹配框
					setFilterKeyWordDismiss();
					// 隐藏软键盘
					// setSoftWindowShow();
					// 搜索结果页显示
					// setPullListViewShow();
					// 刷新
					refreshSearchResult(true);
					// mPage = 1;
					// getPositionList();
				}
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	/**
	 * 设置关键词匹配框的隐藏
	 */
	public void setFilterKeyWordDismiss() {
		mkeyWordDialog = null;
		if (mFilterWordLayout.getVisibility() == View.VISIBLE)
			mFilterWordLayout.setVisibility(View.GONE);
	}

	/**
	 * 进入页面时首先显示热搜和历史搜索 初始热门搜索最近搜索的控件
	 */
	public void initHotAndHistoryLayout() {
		mInitLayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.search_initview_layout, null);
		mInitLayout
				.setLayoutParams(LayoutParamsUtils.geLineartLayoutParams(
						ActivityUtils.getScreenWidth(),
						ActivityUtils.getScreenHeight()));
		mInitHotLayout = (FlowLayout) mInitLayout
				.findViewById(R.id.fl_init_hot_search);
		mInitHotLayout.setHorizontalSpacing(ActivityUtils.dip2px(this, 7));
		mInitHotLayout.setVerticalSpacing(ActivityUtils.dip2px(this, 7));
		mInitNearLayout = (FlowLayout) mInitLayout
				.findViewById(R.id.fl_init_near_search);
		mHistoryTitle = (TextView) mInitLayout
				.findViewById(R.id.tv_init_near_title);
		mHistoryDelet = (TextView) mInitLayout
				.findViewById(R.id.tv_init_delete_nearrecord);
		mHistoryDelet.setOnClickListener(this);
		setInitLayoutShow();
	}

	/**
	 * 设置初始时的显示，确保隐藏筛选控件、搜索结果页
	 * 
	 * 显示出错、或者没有数据时的页面
	 */
	public void setInitLayoutShow() {
		if (mFilterTopLayout.getVisibility() == View.VISIBLE)
			mFilterTopLayout.setVisibility(View.GONE);
		mNoneDataLayout.removeAllViews();
		mNoneDataLayout.addView(mInitLayout);
		if (mNoneDataLayout.getVisibility() == View.GONE)
			mNoneDataLayout.setVisibility(View.VISIBLE);
		if (mPullToRefreshListView.getVisibility() == View.VISIBLE)
			mPullToRefreshListView.setVisibility(View.GONE);
	}

	/**
	 * 从本地数据库中获取到本机的历史搜索记录
	 */
	public void getHistoryDBLists() {
		mNearLists = mSearchDB.selectHistoryData();
		if (null == mNearLists || mNearLists.isEmpty()) {
			setHistoryHide(false);
			return;
		}
		setHistoryHide(true);
		setHistoryDataShow();
	}

	/**
	 * 设置历史记录控件的显示与隐藏
	 * 
	 * @param isShow
	 */
	public void setHistoryHide(boolean isShow) {
		mInitNearLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
		mHistoryTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
		mHistoryDelet.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	/**
	 * 设置没有搜索结果的布局显示
	 */
	public void setNoneLayoutShow() {
		mNoneDataLayout.removeAllViews();
		mNoneDataLayout.addView(noneLayout);
		if (mNoneDataLayout.getVisibility() == View.GONE)
			mNoneDataLayout.setVisibility(View.VISIBLE);
		if (mPullToRefreshListView.getVisibility() == View.VISIBLE)
			mPullToRefreshListView.setVisibility(View.GONE);
	}

	/**
	 * 设置有搜索结果的布局显示
	 */
	public void setPullListViewShow() {
		if (mFilterWordLayout.getVisibility() == View.VISIBLE)
			mFilterTopLayout.setVisibility(View.GONE);
		if (mNoneDataLayout.getVisibility() == View.VISIBLE)
			mNoneDataLayout.setVisibility(View.GONE);
		if (mPullToRefreshListView.getVisibility() == View.GONE)
			mPullToRefreshListView.setVisibility(View.VISIBLE);
		if (mFilterTopLayout.getVisibility() == View.GONE)
			mFilterTopLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置历史记录数据的显示
	 */
	public void setHistoryDataShow() {
		mInitNearLayout.removeAllViews();
		for (int i = 0; i < mNearLists.size(); i++) {
			LinearLayout view = (LinearLayout) LayoutInflater.from(this)
					.inflate(R.layout.search_history_record_layout, null);
			TextView textView = (TextView) view
					.findViewById(R.id.tv_history_name);
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PositionBean bean = mLists.get(position
				- myListView.getHeaderViewsCount());
		PositionDetailActivity.startAction(this, bean.getPkid(), 1);
	}

	@Override
	public void initAdapter() {
		mListAdapter = new SeachPositionAdapter(this, mLists,
				R.layout.tab_position_item_layout);
	}

	@Override
	public void refreshList() {
		mRequestType = 3;
		setRequestParamsPre(TAG, false);
	}

	@Override
	public void loadMoreList() {
		mRequestType = 3;
		setRequestParamsPre(TAG, false);
	}

	@Override
	protected void setNoneDataDesc() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void getIntentData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.toString().trim().length() <= 0) {
			mKeyWord = "";
			setFilterKeyWordDismiss();
			return;
		}
		// 若是编辑改变文本框，则显示匹配框
		if (ifShowWordList) {
			filterData(s.toString());
		}
		ifShowWordList = true;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 */
	private void filterData(String filterStr) {
		mKeyWord = filterStr;
		mPage = 1;
		// setKeyWordLayoutShow();
		// 获取关键词
		mRequestType = 4;
		setRequestParamsPre(TAG, false);
	}

	/**
	 * 设置关键词匹配列表的显示
	 */
	public void setKeyWordLayoutShow() {
		mFilterTopLayout.setVisibility(View.GONE);
		if (mFilterWordLayout.getVisibility() == View.GONE)
			mFilterWordLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 关键词匹配列表的选择
	 */
	@Override
	public void select(int Position, View view, long ViewId) {
		ifShowWordList = false;
		mKeyWord = mKeyWords.get(Position).getTitle();
		mSearchEdit.setText(mKeyWord);
		// 保存关键词
		mSearchDB
				.insertHistory(mKeyWord, DateUtils.getCurrentTimeSecond() + "");
		setFilterKeyWordDismiss();
		// 取消键盘
		setSoftWindowShow();
		refreshSearchResult(true);
		// setPullListViewShow();
		// mPage = 1;
		// getPositionList();
		//
	}

	/**
	 * 隐藏软件盘
	 */
	public void setSoftWindowShow() {

	}

	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("searchPosition"); // 职位搜索
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("searchPosition");
		MobclickAgent.onPause(this);
	}
}

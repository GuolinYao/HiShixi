package com.shixi.gaodun.view.fragment;

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
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.SeachPositionAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.BaseListViewFragment;
import com.shixi.gaodun.inf.ResponseCallBack;
import com.shixi.gaodun.libpull.PullToRefreshListView;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.HotSearchBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.domain.RefersBean;
import com.shixi.gaodun.model.global.Finals;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.ActivityUtils;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.DateUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.FilterDialog;
import com.shixi.gaodun.view.FilterDialog.SelectListener;
import com.shixi.gaodun.view.FlowLayout;
import com.shixi.gaodun.view.activity.SelectPresentAddressActivity;
import com.shixi.gaodun.view.activity.enterprise.PositionDetailActivity;
import com.shixi.gaodun.view.activity.enterprise.PositionSearchActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页职位列表
 * 
 * @author ronger
 * @date:2015-12-7 下午2:30:14
 */
public class TabHomePositionFrament extends BaseListViewFragment<PositionBean> {
	// 已选择了的地址默认为全国
	private TextView mSelectedAddressText;
	private LinearLayout mFilterLayout, mShowLayout;
	private TextView mPositionType, mEducation, mGradeTime;
	private String mFilterAddress, mFilterPosition, mFilterEducation,
			mFilterGrade;
	private RelativeLayout mNoneDataLayout;
	private FlowLayout mFlowLayout;
	private ArrayList<HotSearchBean> mHotLists;
	private View noneLayout;
	private FilterDialog positionFilterDialog, educationFilterDialog,
			gradeFilterDialog;
	private ArrayList<MapFormatBean> educationList, gradeLists, cateLists;
	// isLoadPositionListFinish = false,
	private boolean isLoadHotListFinish = false, isLoadCateListFinish = false;
	private CityBean mSelectCity;
	private TextView mSearchBtn;
	// 显示与隐藏筛选框
	private boolean isPosition, isEducation, isGrade;

	@Override
	public View getContentView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mMainLayout = inflater.inflate(getFrameLayoutId(), container,
				false);
		initView(mMainLayout);
		getNoneDataLayout();
		setFilterDiaLog();
		getFilterEducationInfo();
		getFilterGradeInfo();
		boolean ifGoOn = initMainLayout(true);
		if (!ifGoOn)
			return mMainLayout;
		setRequestParamsPre(TAG);
		return mMainLayout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = 15;
		isLoadPositionListFinish = false;
	}

	@Override
	public void setRequestParams(String className) {
		getPositionList();
		if (!isLoadHotListFinish && null == mHotLists) {
			getHotSearch();
		}
		if (!isLoadCateListFinish && null == cateLists) {
			getFilterPositionInfo();
		}
	}

	/**
	 * 获取职位列表
	 */
	public void getPositionList() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("page", String.valueOf(mPage));
		map.put("pageNumber", String.valueOf(mPageNumber));
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(mContext))) {
			map.put("student_id", CacheUtils.getToken(mContext));
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

		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP()
						+ GlobalContants.GETPOSITION_FILTERLIST_URL, map,
				new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void initView(View view) {
		mLoadMoreLayout = LayoutInflater.from(mContext).inflate(
				R.layout.loadmore_layout, null);
		mSelectedAddressText = (TextView) view
				.findViewById(R.id.tv_position_byaddress);
		mSearchBtn = (TextView) view.findViewById(R.id.tv_position_editcontent);
		mSearchBtn.setOnClickListener(this);
		mFilterLayout = (LinearLayout) view.findViewById(R.id.filter_layout);
		mShowLayout = (LinearLayout) view.findViewById(R.id.show_layout);
		mPositionType = (TextView) view
				.findViewById(R.id.tv_filter_position_type);
		float mFontWidth = mPositionType.getTextSize() * 5;
		mPositionType.setMaxWidth((int) mFontWidth);
		mEducation = (TextView) view.findViewById(R.id.tv_filter_education);
		mGradeTime = (TextView) view.findViewById(R.id.tv_filter_graducation);
		view.findViewById(R.id.layout_filter_position_type).setOnClickListener(
				this);
		view.findViewById(R.id.layout_filter_education)
				.setOnClickListener(this);
		view.findViewById(R.id.filter_graducation).setOnClickListener(this);
		// mPositionType.setOnClickListener(this);
		// mEducation.setOnClickListener(this);
		// mGradeTime.setOnClickListener(this);
		mSelectedAddressText.setOnClickListener(this);
		mPullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.prlv_positionlist_pull);
		myListView = mPullToRefreshListView.getRefreshableView();
		// myListView.setCacheColorHint(Color.TRANSPARENT);
		// myListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		// myListView.setDivider(new
		// ColorDrawable(Color.parseColor("#F2F2F5")));
		myListView.setDividerHeight(0);
		myListView.setOnScrollListener(this);
		mPullToRefreshListView.setOnRefreshListener(this);
		myListView.setOnItemClickListener(this);
		mNoneDataLayout = (RelativeLayout) view.findViewById(R.id.list_null);
		mFilterLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// mFilterLayout.setVisibility(View.GONE);
				setFilterLayoutGone();
				return true;
			}
		});

	}

	public void setFilterLayoutGone() {
		mFilterLayout.setVisibility(View.GONE);
		isPosition = false;
		isEducation = false;
		isGrade = false;
	}

	@SuppressLint("InflateParams")
	public void getNoneDataLayout() {
		noneLayout = LayoutInflater.from(mContext).inflate(
				R.layout.none_position_layout, null);
		LayoutParams layoutParams = new LayoutParams(
				ActivityUtils.getScreenWidth(), ActivityUtils.getScreenHeight());
		noneLayout.setLayoutParams(layoutParams);
		mFlowLayout = (FlowLayout) noneLayout.findViewById(R.id.fl_hot_search);
		mFlowLayout.setHorizontalSpacing(ActivityUtils.dip2px(mContext, 7));
		mFlowLayout.setVerticalSpacing(ActivityUtils.dip2px(mContext, 7));
	}

	@Override
	public void initErrorLayout() {
		super.initErrorLayout();
		LayoutParams layoutParams = new LayoutParams(
				ActivityUtils.getScreenWidth(), ActivityUtils.getScreenHeight());
		mCustomErrorView.setLayoutParams(layoutParams);
	}

	@Override
	protected void setErrorShow() {
		mNoneDataLayout.removeAllViews();
		mNoneDataLayout.addView(mCustomErrorView);
		if (mNoneDataLayout.getVisibility() == View.GONE)
			mNoneDataLayout.setVisibility(View.VISIBLE);
		if (mPullToRefreshListView.getVisibility() == View.VISIBLE)
			mPullToRefreshListView.setVisibility(View.GONE);
	}

	public void setNoneLayoutShow() {
		mNoneDataLayout.removeAllViews();
		mNoneDataLayout.addView(noneLayout);
		if (mNoneDataLayout.getVisibility() == View.GONE)
			mNoneDataLayout.setVisibility(View.VISIBLE);
		if (mPullToRefreshListView.getVisibility() == View.VISIBLE)
			mPullToRefreshListView.setVisibility(View.GONE);
	}

	public void setPullShow() {
		if (mNoneDataLayout.getVisibility() == View.VISIBLE)
			mNoneDataLayout.setVisibility(View.GONE);
		if (mPullToRefreshListView.getVisibility() == View.GONE)
			mPullToRefreshListView.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置筛选对话框
	 */
	public void setFilterDiaLog() {
		positionFilterDialog = new FilterDialog(mContext);
		educationFilterDialog = new FilterDialog(mContext);
		gradeFilterDialog = new FilterDialog(mContext);
	}

	/**
	 * 获取学历数据
	 */
	public void getFilterEducationInfo() {
		String[] education = mContext.getResources().getStringArray(
				R.array.education);
		educationList = new ArrayList<MapFormatBean>(education.length + 1);
		// educationList.add(new MapFormatBean("", "全部"));
		for (int i = 0; i < education.length; i++) {
			educationList.add(new MapFormatBean(String.valueOf(i + 1),
					education[i]));
		}
	}

	/***
	 * 获取毕业年份当前年份的前三年和后四年
	 */
	public void getFilterGradeInfo() {
		gradeLists = new ArrayList<MapFormatBean>(7);
		int currentYear = DateUtils.getCurrentYear();
		int startYear = currentYear - 3;
		// gradeLists.add(new MapFormatBean("", "全部"));
		for (int i = 0; i < 8; i++) {
			gradeLists.add(new MapFormatBean(String.valueOf(i + 1), startYear
					+ "年"));
			startYear++;
		}
	}

	/**
	 * 获取岗位列表
	 */
	public void getFilterPositionInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.CATEGORYLIST_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						try {
							isLoadCateListFinish = true;
							myProgressDialog.dismiss();
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							String resultData = httpRes.getReturnData();
							if (StringUtils.isEmpty(resultData)
									|| resultData.length() <= 2) {
								return;
							}
							cateLists = TransFormModel.getCateLists(
									new JSONArray(resultData), true);
							mYHandler.sendEmptyMessage(1);
						} catch (Exception e) {
							ToastUtils.showToastInCenter("数据解析错误");
						}

					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	/**
	 * 获取热搜词汇
	 */
	public void getHotSearch() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		map.put("range", "2");
		JsonObjectPostRequest request = new JsonObjectPostRequest(
				StringUtils.getCommonIP() + GlobalContants.GETHOTSEARCH_URL,
				map, new RequestResponseLinstner(new ResponseCallBack() {
					@Override
					public void getResponse(JSONObject response) {
						dissMissProgress();
						try {
							HttpRes httpRes = TransFormModel
									.getResponseData(response);
							if (httpRes.getReturnCode() != 0) {
								ToastUtils.showToastInCenter(httpRes
										.getReturnDesc());
								return;
							}
							String hotStr = httpRes.getReturnData();
							if (StringUtils.isEmpty(hotStr)
									|| hotStr.length() <= 2) {

								return;
							}
							mHotLists = (ArrayList<HotSearchBean>) TransFormModel
									.getResponseResults(hotStr,
											HotSearchBean.class);
							isLoadHotListFinish = true;
							mYHandler.sendEmptyMessage(0);
						} catch (Exception e) {
							ToastUtils.showToastInCenter("数据解析错误");
						}

					}
				}), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == Finals.REQUESTCODE_ONE) {
			mSelectCity = (CityBean) data.getSerializableExtra("selectCity");
			if (null != mSelectCity) {
				mFilterAddress = mSelectCity.getRegion_id();
				String addressName = mSelectCity.getRegion_name();
				// if (StringUtils.isNotEmpty(addressName) &&
				// addressName.length() <= 2) {
				// mSelectedAddressText.setPadding(ActivityUtils.dip2px(mContext,
				// 15), 0, 0, 0);
				// } else {
				// mSelectedAddressText.setPadding(0, 0, 0, 0);
				// }
				mSelectedAddressText.setText(mSelectCity.getRegion_name());
				mPage = 1;
				refreshList();
			}
			return;
		}
	}

	private void setFilterData() {
		setPositionData();
		setEducationData();
		setGradeData();
	}

	/**
	 * 设置职位类别列表数据
	 */
	public void setPositionData() {
		positionFilterDialog.setAdapter(cateLists);
		positionFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				// mFilterPosition = cateLists.get(Position).getKey();
				if (Position != -1) {
					mFilterPosition = cateLists.get(Position).getKey();
				} else {
					mFilterPosition = "";
				}
				// mFilterLayout.setVisibility(View.GONE);
				// isPosition = false;
				setFilterLayoutGone();
				// if (cateLists.get(Position).getValue().equals("全部")) {
				if (Position == -1) {
					mPositionType.setText("职位类别");
					mPositionType.setSelected(false);
				} else {
					mPositionType.setText(cateLists.get(Position).getValue());
					mPositionType.setSelected(true);
				}
				mPage = 1;
				setRequestParamsPre(TAG, true);
				// refreshList();
				// mPullToRefreshListView.setRefreshing();
			}
		});
	}

	/**
	 * 刷新列表
	 */
	public void refresh() {
		isLoadCateListFinish = true;
		isLoadHotListFinish = true;
	}

	public void setEducationData() {
		educationFilterDialog.setAdapter(educationList);
		educationFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				if (Position != -1) {
					mFilterEducation = educationList.get(Position).getKey();
				} else {
					mFilterEducation = "";
				}

				setFilterLayoutGone();
				// mFilterLayout.setVisibility(View.GONE);
				// isEducation = false;
				// if (educationList.get(Position).getValue().equals("全部")) {
				if (Position == -1) {
					mEducation.setText("学历");
					mEducation.setSelected(false);
				} else {
					mEducation.setText(educationList.get(Position).getValue());
					mEducation.setSelected(true);
				}
				mPage = 1;
				setRequestParamsPre(TAG, true);
				// refreshList();
			}
		});

	}

	public void setGradeData() {
		gradeFilterDialog.setAdapter(gradeLists);
		gradeFilterDialog.setSelectListener(new SelectListener() {
			@Override
			public void select(int Position, View view, long ViewId) {
				// mFilterGrade = gradeLists.get(Position).getValue();
				// mFilterLayout.setVisibility(View.GONE);
				// setTextState(40);
				// mGradeTime.setText(gradeLists.get(Position).getValue());
				if (Position != -1) {
					mFilterGrade = gradeLists.get(Position).getValue();
				} else {
					mFilterGrade = "";
				}
				// mFilterLayout.setVisibility(View.GONE);
				// isGrade = false;
				setFilterLayoutGone();
				// if (gradeLists.get(Position).getValue().equals("全部")) {
				if (Position == -1) {
					mGradeTime.setText("毕业年份");
					mGradeTime.setSelected(false);
				} else {
					mGradeTime.setText(gradeLists.get(Position).getValue());
					mGradeTime.setSelected(true);
				}
				mPage = 1;
				setRequestParamsPre(TAG, true);
			}
		});
	}

	/**
	 * 设置热搜词汇显示
	 */
	@SuppressLint("InflateParams")
	public void setHotSearchShow() {
		if (null == mHotLists || mHotLists.isEmpty())
			return;
		mFlowLayout.removeAllViews();
		for (int i = 0; i < mHotLists.size(); i++) {
			TextView textView = (TextView) LayoutInflater.from(mContext)
					.inflate(R.layout.flowlayout_textview_layout, null);
			HotSearchBean hotBean = mHotLists.get(i);
			RefersBean bean = new RefersBean(hotBean, i, "");
			textView.setText(hotBean.getTitle());
			textView.setTag(bean);
			textView.setOnClickListener(this);
			mFlowLayout.addView(textView);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int index = position - myListView.getHeaderViewsCount();
		if (index < 0)
			return;
		PositionBean bean = mLists.get(index);
		PositionDetailActivity.startAction(mContext, bean.getPkid(), 1);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_again:
			if (ActivityUtils.isNetAvailable()) {
				setPullShow();
				setRequestParamsPre(TAG);
			} else {
				setErrorShow();
			}
			break;
		case R.id.tv_position_editcontent:// 去搜索页
			PositionSearchActivity.startAction(mContext);
			// SearchPositionActivity.startAction(mContext);
			break;
		case R.id.layout_filter_position_type:// 选择职位类别
			if (isPosition) {
				mFilterLayout.setVisibility(View.GONE);
				isPosition = !isPosition;
				break;
			}
			if (mFilterLayout.getVisibility() == View.GONE)
				mFilterLayout.setVisibility(View.VISIBLE);
			mShowLayout.removeAllViews();
			mShowLayout.addView(positionFilterDialog.getResultLayout());
			isPosition = !isPosition;
			positionFilterDialog.selectPosition();

			// boolean isSelected = getSelectedBg(isPosition,
			// mPositionType.getText().toString(), "职位类别");
			// if (!mPositionType.isSelected())
			// mPositionType.setSelected(isSelected);
			break;
		case R.id.layout_filter_education:// 选择学历
			if (isEducation) {
				isEducation = !isEducation;
				mFilterLayout.setVisibility(View.GONE);
				break;
			}
			if (mFilterLayout.getVisibility() == View.GONE)
				mFilterLayout.setVisibility(View.VISIBLE);
			else
				educationFilterDialog.setNotify(educationList);
			mShowLayout.removeAllViews();
			mShowLayout.addView(educationFilterDialog.getResultLayout());
			isEducation = !isEducation;
			educationFilterDialog.selectPosition();

			// if (!isEducation) {
			// mFilterLayout.setVisibility(View.VISIBLE);
			// mShowLayout.removeAllViews();
			// mShowLayout.addView(educationFilterDialog.getResultLayout());
			// educationFilterDialog.selectPosition();
			// } else {
			// mFilterLayout.setVisibility(View.GONE);
			// }
			// isEducation = !isEducation;
			// isEducation = getSelectedBg(isEducation,
			// mEducation.getText().toString(), "学历");
			// if (!mEducation.isSelected())
			// mEducation.setSelected(isEducation);
			break;
		case R.id.filter_graducation:// 选择毕业年份
			if (isGrade) {
				isGrade = !isGrade;
				mFilterLayout.setVisibility(View.GONE);
				break;
			}
			if (mFilterLayout.getVisibility() == View.GONE)
				mFilterLayout.setVisibility(View.VISIBLE);
			mShowLayout.removeAllViews();
			mShowLayout.addView(gradeFilterDialog.getResultLayout());
			isGrade = !isGrade;
			gradeFilterDialog.selectPosition();
			// if (!isGrade) {
			// mFilterLayout.setVisibility(View.VISIBLE);
			// mShowLayout.removeAllViews();
			// mShowLayout.addView(gradeFilterDialog.getResultLayout());
			// gradeFilterDialog.selectPosition();
			// } else {
			// mFilterLayout.setVisibility(View.GONE);
			// }
			// isGrade = !isGrade;
			// isGrade = getSelectedBg(isGrade, mGradeTime.getText().toString(),
			// "毕业年份");
			// if (!mGradeTime.isSelected())
			// mGradeTime.setSelected(isGrade);
			break;
		case R.id.tv_position_byaddress:// 地址
			SelectPresentAddressActivity.startActionBy(mContext, this,
					Finals.REQUESTCODE_ONE, mSelectCity, true);
			// Intent intent = new Intent(mContext,
			// SelectPresentAddressActivity.class);
			// intent.putExtra("selectCity", mSelectCity);
			// intent.putExtra("ifAddAll", true);
			// startActivityForResult(intent, Finals.REQUESTCODE_ONE);
			break;
		default:
			break;
		}
	}

	public boolean getSelectedBg(boolean isSelected, String textStr,
			String defaultStr) {
		if (textStr.equals(defaultStr))
			return false;
		return isSelected;
	}

	@SuppressLint("HandlerLeak")
	Handler mYHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			if (msg.what == 1) {
				setFilterData();
				return;
			}
			if (isLoadHotListFinish && isLoadPositionListFinish
					&& (null == mLists || mLists.isEmpty())) {
				setNoneLayoutShow();
				setHotSearchShow();
			}
		}

	};

	@Override
	public void getResponse(JSONObject response) {
		getResponseComplete();
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				isLoadPositionListFinish = true;
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				mYHandler.sendEmptyMessage(0);
				return;
			}
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String resultData = object.getString("post");
			mListTotal = NumberUtils.getInt(object.getString("count"), 0);
			if (mListTotal == 0) {
				setNoneLayoutShow();
				setHotSearchShow();
				return;
			}
			setPullShow();
			List<PositionBean> lists = TransFormModel.getResponseResults(
					resultData, PositionBean.class);
			getListSuccess(lists);
			isLoadPositionListFinish = true;
		} catch (Exception e) {
			ToastUtils.showToastInCenter("数据解析错误");
			setDebugLog(e);
		}

	}

	@Override
	public void initAdapter() {
		mListAdapter = new SeachPositionAdapter(mContext, mLists,
				R.layout.tab_position_item_layout);
		// mListAdapter = new CommonAdapter<PositionBean>(mContext, mLists,
		// R.layout.tab_position_item_layout) {
		// @Override
		// public void convert(ViewHolder helper, PositionBean item, int
		// position) {
		// helper.setImageByUrl(R.id.iv_commpany_image, item.getLogo(),
		// R.drawable.default_image_icon, 3);
		// helper.setText(R.id.tv_position_name, item.getTitle());
		// helper.setText(R.id.tv_publish_time, item.getCreate_time());
		// helper.setText(R.id.tv_position_address, "[" + item.getRegion_name()
		// + "]");
		// String str = item.getSalary_range();
		// if (StringUtils.isNotEmpty(str)) {
		// int endIndex = str.indexOf("元");
		// TextView textView = helper.getView(R.id.tv_salary_range);
		// if (endIndex < 0) {
		// textView.setText(str);
		// return;
		// }
		// SpannableString spannableString = new SpannableString(str);
		// spannableString.setSpan(
		// new
		// ForegroundColorSpan(mContext.getResources().getColor(R.color.salary_range_font_color)),
		// 0, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// textView.setText(spannableString);
		// }
		// helper.setText(R.id.tv_education, item.getEducation());
		// helper.setText(R.id.tv_weekday, item.getWeek_available());
		// helper.setText(R.id.tv_commpany_name, item.getEnterprise_name());
		// helper.setText(R.id.tv_industry_name, item.getIndustry_name());
		// helper.setText(R.id.tv_commpany_number, item.getScale_name());
		// }
		// };

	}

	@Override
	public void refreshList() {
		mPage = 1;
		setRequestParamsPre(TAG, false);
	}

	@Override
	public void loadMoreList() {
		setRequestParamsPre(TAG, false);
	}

	@Override
	protected int getFrameLayoutId() {
		return R.layout.tab_position_layout;
	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 友盟统计
	 */
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("positionList"); // 统计页面，"positionList"为页面名称，可自定义
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("positionList");
	}
}

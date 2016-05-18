package com.shixi.gaodun.view.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.NomalListViewAdapter;
import com.shixi.gaodun.base.TitleBaseActivity;

/**
 * 选择学历
 * 
 * @author ronger
 * @date:2015-10-28 下午1:34:38
 */
public class EducationListActivity extends TitleBaseActivity implements OnItemClickListener {
	private String mEducationName;
	private ListView mListView;
	private ArrayList<String> mLists;
	private String[] mSelectEducation;

	public static void startAction(Activity context, int requestCode, String educationName) {
		Intent intent = new Intent(context, EducationListActivity.class);
		intent.putExtra("educationName", educationName);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mEducationName = data.getStringExtra("educationName");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.nomal_list_layout);
		initData();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("选择学历");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mListView = (ListView) findViewById(R.id.lv_nomal_listview);
		mListView.setOnItemClickListener(this);
	}

	/**
	 * 初始化数据 1:专科,2:本科,3:研究生,4:博士
	 */
	public void initData() {
		// mLists = new ArrayList<String>();
		// mLists.add("专科");
		// mLists.add("本科");
		// mLists.add("研究生");
		// mLists.add("博士");
		mSelectEducation = getResources().getStringArray(R.array.education);
		NomalListViewAdapter adapter = new NomalListViewAdapter(this, mSelectEducation, mEducationName);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		// String selectStr = mLists.get(position);
		String selectStr = mSelectEducation[position];
		intent.putExtra("selectStr", selectStr);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
		}

	}
}

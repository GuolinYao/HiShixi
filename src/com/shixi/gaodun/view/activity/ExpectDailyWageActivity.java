package com.shixi.gaodun.view.activity;

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
import com.shixi.gaodun.model.domain.MapFormatBean;

/**
 * 期望日薪
 * 
 * @author ronger
 * @date:2015-11-2 上午9:15:24
 */
public class ExpectDailyWageActivity extends TitleBaseActivity implements OnItemClickListener {
	private String[] mLists;
	private ListView mListView;
	private String mSelectStr;

	public static void startAction(Activity context, int requestCode, String selectStr) {
		Intent intent = new Intent(context, ExpectDailyWageActivity.class);
		intent.putExtra("selectStr", selectStr);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		mSelectStr = getIntent().getStringExtra("selectStr");
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
		mTitleName.setText("期望日薪");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mListView = (ListView) findViewById(R.id.lv_nomal_listview);
		mListView.setOnItemClickListener(this);
	}

	public void initData() {
		mLists = getResources().getStringArray(R.array.expectdaily_wage);
		NomalListViewAdapter adapter = new NomalListViewAdapter(this, mLists, mSelectStr);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		String selectStr = mLists[position];
		MapFormatBean bean = new MapFormatBean(String.valueOf(position + 1), selectStr);
		// intent.putExtra("selectStr", selectStr);
		intent.putExtra("wage", bean);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}
}

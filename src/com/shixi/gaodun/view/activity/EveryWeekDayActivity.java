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

/**
 * 每周可实习天数
 * 
 * @author ronger
 * @date:2015-11-2 上午9:13:38
 */
public class EveryWeekDayActivity extends TitleBaseActivity implements OnItemClickListener {
	private String[] mLists;
	private ListView mListView;
	private String mSelectDay;

	public static void startAction(Activity context, int requestCode, String day) {
		Intent intent = new Intent(context, EveryWeekDayActivity.class);
		intent.putExtra("day", day);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		mSelectDay = getIntent().getStringExtra("day");
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
		mTitleName.setText("每周可实习天数");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mListView = (ListView) findViewById(R.id.lv_nomal_listview);
		mListView.setOnItemClickListener(this);
	}

	public void initData() {
		mLists = getResources().getStringArray(R.array.week_day);
		NomalListViewAdapter adapter = new NomalListViewAdapter(this, mLists, mSelectDay);
		mListView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent();
		String selectStr = mLists[position];
		intent.putExtra("day", selectStr);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
		}
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}
}

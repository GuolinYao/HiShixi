package com.shixi.gaodun.view.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.base.BaseMainActivity;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.ViewHolder;
import com.shixi.gaodun.model.utils.DateUtils;

/**
 * 选择毕业年份
 * @author guolinyao
 * @date 2016年4月14日 上午9:19:02
 */
public class SelectGraduateYearActivity extends TitleBaseActivity implements
		OnItemClickListener {
	private String mGraduateYear;// 毕业时间
	private ArrayList<MapFormatBean> gradeLists;
	private int mSelectIndex = -1;
	private ListView mSelect_graduateyear;
	private GraduateYearAdapter adapter;

	public static void startAction(BaseMainActivity context, int requestCode,
			String graduateyear,int selectIndex) {
		Intent intent = new Intent(context, SelectGraduateYearActivity.class);
		intent.putExtra("graduateyear", graduateyear);
		intent.putExtra("selectIndex", selectIndex);
		// context.setMove(3);
		context.startActivityForResult(intent, requestCode);
		// context.setMove(1);
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mGraduateYear = data.getStringExtra("graduateyear");
		mSelectIndex = data.getIntExtra("selectIndex", 0);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		setContentView(R.layout.activity_select_graduateyear_layout);

	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("毕业年份");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mSelect_graduateyear = (ListView) findViewById(R.id.lv_select_graduateyear);
		getFilterGradeInfo();
		adapter = new GraduateYearAdapter(this, gradeLists, R.layout.item_select_graduateyear);
		mSelect_graduateyear.setAdapter(adapter);
		mSelect_graduateyear.setOnItemClickListener(this);
		
	}
	
	/**
	 * 毕业年份适配器
	 * @author guolinyao
	 * @date 2016年4月13日 下午5:35:30
	 */
	private class GraduateYearAdapter extends CommonAdapter<MapFormatBean>{

		public GraduateYearAdapter(Activity context,
				ArrayList<MapFormatBean> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
			
		}

		@Override
		public void convert(ViewHolder helper, MapFormatBean item, int position) {
			helper.setText(R.id.tv_year, item.getValue());
			ImageView iv = helper.getView(R.id.iv_check);
			if(position==mSelectIndex){
				iv.setVisibility(View.VISIBLE);
			}else{
				iv.setVisibility(View.GONE);
			}
		}
		
	}
	/***
	 * 获取毕业年份当前年份的前三年和后四年
	 */
	public void getFilterGradeInfo() {
		gradeLists = new ArrayList<MapFormatBean>(7);
		int currentYear = DateUtils.getCurrentYear();
		int startYear = currentYear - 3;
		for (int i = 0; i < 8; i++) {
			gradeLists.add(new MapFormatBean(String.valueOf(i + 1), startYear
					+ "年"));
			startYear++;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back) {
			finish();
//			overridePendingTransition(0, R.anim.slide_down_out);
			overridePendingTransition(0, R.anim.slide_right);
		}
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mSelectIndex = position;
		mGraduateYear  = gradeLists.get(position).getValue();
		mSelectIndex = position;
		adapter.notifyDataSetChanged();
		returnUpPage();
	}

	/**
	 * 返回
	 */
	public void returnUpPage() {
		Intent intent = new Intent();
		intent.putExtra("graduateyear", mGraduateYear);
		intent.putExtra("selectIndex", mSelectIndex);
		setResult(Activity.RESULT_OK, intent);
		finish();
		move = right;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void finishTranstition() {
//		overridePendingTransition(0, R.anim.slide_down_out);
		overridePendingTransition(0, R.anim.slide_right);
	}

	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		super.onBackPressed();
//		overridePendingTransition(0, R.anim.slide_down_out);
		overridePendingTransition(0, R.anim.slide_right);
	}

	@Override
	protected void setRetryRequest() {
	}

}

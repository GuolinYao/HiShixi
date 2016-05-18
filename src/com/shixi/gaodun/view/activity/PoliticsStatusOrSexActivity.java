package com.shixi.gaodun.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.TitleBaseActivity;

/**
 * 政治面貌或性别页
 * 
 * @author ronger
 * @date:2015-10-26 下午4:42:12
 */
public class PoliticsStatusOrSexActivity extends TitleBaseActivity {
	private String mSelectStr;
	private int mSelectIndex = -1;
	private int mRequestType;// 1政治面貌0性别
	private TextView mItemTextOne, mItemTextTwo;
	private ImageView mItemImgOne, mItemImgTwo;

	public static void startAction(Activity context, int requestCode, String selectStr, int selectIndex, int requestType) {
		Intent intent = new Intent(context, PoliticsStatusOrSexActivity.class);
		intent.putExtra("selectStr", selectStr);
		intent.putExtra("selectIndex", selectIndex);
		intent.putExtra("requestType", requestType);
		context.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void getIntentData() {
		Intent data = getIntent();
		mSelectIndex = data.getIntExtra("selectIndex", -1);
		mSelectStr = data.getStringExtra("selectStr");
		mRequestType = data.getIntExtra("requestType", 0);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.politics_status_sex_layout);
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText(mRequestType == 0 ? "性别" : "政治面貌");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mItemTextOne = (TextView) findViewById(R.id.tv_select_indexone);
		mItemTextTwo = (TextView) findViewById(R.id.tv_select_indextwo);
		mItemImgOne = (ImageView) findViewById(R.id.iv_select_indexone);
		mItemImgTwo = (ImageView) findViewById(R.id.iv_select_indextwo);
		mItemTextOne.setText(mRequestType == 0 ? "男" : "党员");
		mItemTextTwo.setText(mRequestType == 0 ? "女" : "非党员");
		mItemImgOne.setVisibility(View.GONE);
		mItemImgTwo.setVisibility(View.GONE);
		if (mSelectIndex == 1) {
			mItemImgOne.setVisibility(View.VISIBLE);
			mItemImgTwo.setVisibility(View.GONE);
			return;
		}
		if (mSelectIndex == 2) {
			mItemImgOne.setVisibility(View.GONE);
			mItemImgTwo.setVisibility(View.VISIBLE);
			return;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_title_bar_back:
			finish();
			break;
		case R.id.rl_select_indextwo:
			mSelectIndex = 2;
			mSelectStr = mItemTextTwo.getText().toString();
			mItemImgOne.setVisibility(View.GONE);
			mItemImgTwo.setVisibility(View.VISIBLE);
			returnUpPage();
			break;
		case R.id.rl_select_indexone:
			mSelectIndex = 1;
			mSelectStr = mItemTextOne.getText().toString();
			mItemImgOne.setVisibility(View.VISIBLE);
			mItemImgTwo.setVisibility(View.GONE);
			returnUpPage();
			break;
		default:
			break;
		}
	}

	public void returnUpPage() {
		Intent intent = new Intent();
		intent.putExtra("selectStr", mSelectStr);
		intent.putExtra("selectIndex", mSelectIndex);
		setResult(Activity.RESULT_OK, intent);
		finish();
		move = right;
	}

	@Override
	protected void setRetryRequest() {
		// TODO Auto-generated method stub

	}
}

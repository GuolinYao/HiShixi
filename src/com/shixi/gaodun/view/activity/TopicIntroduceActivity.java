package com.shixi.gaodun.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.TitleBaseActivity;
import com.shixi.gaodun.model.domain.TopicInfo;
import com.umeng.analytics.MobclickAgent;

/**
 * 话题介绍
 * 
 * @author ronger
 * @date:2015-11-27 下午2:16:28
 */
public class TopicIntroduceActivity extends TitleBaseActivity {
	private TopicInfo mTopicInfo;
	private TextView mTextTopicTitle, mTextTopicContent;
	private ImageView mImageTopic;

	public static void startAction(Activity context, TopicInfo topicInfo) {
		Intent intent = new Intent(context, TopicIntroduceActivity.class);
		intent.putExtra("topicinfo", topicInfo);
		context.startActivity(intent);
	}

	@Override
	protected void getIntentData() {
		mTopicInfo = (TopicInfo) getIntent().getSerializableExtra("topicinfo");
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getIntentData();
		isExcute = false;
		setContentView(R.layout.topic_introduce_layout);
		initData();
	}

	@Override
	public void initView() {
		super.initView();
		mTitleName.setText("话题介绍");
		mBackIcon.setImageResource(R.drawable.icon_back);
		mOtherName.setVisibility(View.GONE);
		mTextTopicTitle = (TextView) findViewById(R.id.text_topic_title);
		mTextTopicContent = (TextView) findViewById(R.id.text_topic_content);
		mImageTopic = (ImageView) findViewById(R.id.image_topicimage);
	}

	public void initData() {
		setTextViewShow(mTextTopicTitle, mTopicInfo.getTips_title());
		setTextViewShow(mTextTopicContent, mTopicInfo.getTips_content());
		BaseApplication.mApp.setRoundedImageByUrl(mImageTopic, mTopicInfo.getIcon_image(),
				R.drawable.default_image_icon, 5);
		// setImageByUrl(mImageTopic, mTopicInfo.getIcon_image(), R.drawable.default_image_icon, 10);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fl_title_bar_back)
			finish();
	}

	@Override
	protected void setRetryRequest() {
	}
	
	/**
	 * 友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("topicInfo"); //话题描述
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("topicInfo"); 
		MobclickAgent.onPause(this);
	}
}

package com.shixi.gaodun.view.activity.enterprise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.base.ParentFragment;

/**
 * 企业介绍
 * 
 * @author ronger
 * @date:2015-12-24 上午8:52:02
 */
public class EnterpriseIntroduceFragment extends ParentFragment {
	private String mEnterpriseIntroduce;
	private TextView mIntroduceText;

//	public EnterpriseIntroduceFragment(){
//
//	}
//	public EnterpriseIntroduceFragment(String introduceStr) {
//		this.mEnterpriseIntroduce = introduceStr;
//	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.textview_layout, container, false);
		initView(view);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEnterpriseIntroduce=getArguments().getString("introduce");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initView(View view) {
		mIntroduceText = (TextView) view.findViewById(R.id.text_company_desc);
		mIntroduceText.setText(mEnterpriseIntroduce);
	}

	@Override
	protected void setErrorShow() {
		// TODO Auto-generated method stub

	}

}

package com.shixi.gaodun.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.CompanyBean;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 公司介绍
 * 
 * @author ronger
 * @date:2015-11-12 下午1:37:57
 */
public class CompanyIntroductionFragment extends Fragment {
	private TextView mIntroduceText;
	// private CompanyDetailActivity mActivity;
	private CompanyBean mCompanyBean;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.company_introduce_layout, container, false);
		initView(view);
		return view;
	}

	public void initView(View view) {
		mIntroduceText = (TextView) view.findViewById(R.id.text_company_introduce);
		mIntroduceText.setText(null == mCompanyBean || StringUtils.isEmpty(mCompanyBean.getDescription()) ? ""
				: mCompanyBean.getDescription() + mCompanyBean.getDescription() + mCompanyBean.getDescription()
						+ mCompanyBean.getDescription());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		mCompanyBean = (CompanyBean) bundle.getSerializable("companyinfo");
		// mActivity = (CompanyDetailActivity) getActivity();
		// mCompanyBean = mActivity.getmCompanyBean();
	}

}

package com.shixi.gaodun.view.activity.enterprise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.shixi.gaodun.R;
import com.shixi.gaodun.adapter.CommonAdapter;
import com.shixi.gaodun.adapter.JobAdapter;
import com.shixi.gaodun.base.BaseApplication;
import com.shixi.gaodun.base.ParentFragment;
import com.shixi.gaodun.model.JsonObjectPostRequest;
import com.shixi.gaodun.model.RequestResponseLinstner;
import com.shixi.gaodun.model.TransFormModel;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.PositionBean;
import com.shixi.gaodun.model.global.GlobalContants;
import com.shixi.gaodun.model.utils.CacheUtils;
import com.shixi.gaodun.model.utils.NumberUtils;
import com.shixi.gaodun.model.utils.StringUtils;
import com.shixi.gaodun.model.utils.ToastUtils;
import com.shixi.gaodun.view.MyListView;

/**
 * 企业在招岗位
 * 
 * @author ronger
 * @date:2015-12-24 上午9:02:55
 */
public class EnterpriseJobVacancyFrament extends ParentFragment implements OnItemClickListener {
	private String mEnterprise_id;
	private MyListView myListView;
	private List<PositionBean> mList;

	/*public EnterpriseJobVacancyFrament(String enterprise_id) {
		this.mEnterprise_id = enterprise_id;
	}*/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.enterprise_job_vacancy_layout, container, false);
		initView(view);
		setRequestParamsPre(TAG, false);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEnterprise_id=getArguments().getString("companyid");
	}

	@Override
	protected void initView(View view) {
		myListView = (MyListView) view.findViewById(R.id.enterprise_job_list);
		myListView.setOnItemClickListener(this);
	}

	@Override
	public void setRequestParams(String className) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("ticket", CacheUtils.getToken(mContext));
		if (StringUtils.isNotEmpty(CacheUtils.getStudentId(mContext))) {
			map.put("student_id", CacheUtils.getStudentId(mContext));
		}
		if (StringUtils.isNotEmpty(mEnterprise_id)) {
			map.put("enterprise_id", mEnterprise_id);
		}
		JsonObjectPostRequest request = new JsonObjectPostRequest(StringUtils.getCommonIP()
				+ GlobalContants.GETPOSITION_FILTERLIST_URL, map, new RequestResponseLinstner(this), this);
		BaseApplication.mApp.addToRequestQueue(request, TAG);
	}

	@Override
	public void getResponse(JSONObject response) {
		try {
			HttpRes httpRes = TransFormModel.getResponseData(response);
			if (httpRes.getReturnCode() != 0) {
				ToastUtils.showToastInCenter(httpRes.getReturnDesc());
				return;
			}
			JSONObject object = new JSONObject(httpRes.getReturnData());
			String resultData = object.getString("post");
			int mAllCount = NumberUtils.getInt(object.getString("count"), 0);
			if (mAllCount == 0) {
				return;
			}
			getListSuccess(resultData);
		} catch (Exception e) {
			setDebugLog(e);
		}
	}

	public void getListSuccess(String resultData) throws Exception {
		if (StringUtils.isEmpty(resultData) || resultData.length() <= 2) {
			return;
		}
		List<PositionBean> mPositionLists = (ArrayList<PositionBean>) TransFormModel.getResponseResults(resultData,
				PositionBean.class);
		mList = mPositionLists;
		CommonAdapter<PositionBean> adpter = new JobAdapter(mContext, mPositionLists,
				R.layout.company_position_item_layout);
		myListView.setAdapter(adpter);
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
	protected void setErrorShow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PositionDetailActivity.startAction(mContext, mList.get(position).getPkid(),1);
	}

}

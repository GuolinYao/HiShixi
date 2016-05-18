package com.shixi.gaodun.model;

import org.json.JSONObject;

import com.android.volley.Response.Listener;
import com.shixi.gaodun.inf.ResponseCallBack;

/**
 * @author ronger
 * @date:2015-10-20 下午3:56:18
 */
public class RequestResponseLinstner implements Listener<JSONObject> {
	private ResponseCallBack mResponseCallBack;

	public RequestResponseLinstner(ResponseCallBack responseCallBack) {
		this.mResponseCallBack = responseCallBack;
	}

	@Override
	public void onResponse(JSONObject response) {
		mResponseCallBack.getResponse(response);
	}

}

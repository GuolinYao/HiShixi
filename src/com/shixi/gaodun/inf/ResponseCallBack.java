package com.shixi.gaodun.inf;

import org.json.JSONObject;

/**
 * 对服务器请求的回调
 * 
 * @author ronger
 * @date:2015-10-20 下午3:57:47
 */
public interface ResponseCallBack {
	// 请求服务器成功后调用
	public void getResponse(JSONObject response);
}

package com.shixi.gaodun.model;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class JsonObjectPostRequest extends Request<JSONObject> {
	private Map<String, String> mMap;
	private Listener<JSONObject> mListener;

	public JsonObjectPostRequest(String url, Map<String, String> map, Listener<JSONObject> listener,
			ErrorListener errorListener) {
		super(Request.Method.POST, url, errorListener);
		mMap = map;
		mListener = listener;
	}

	public JsonObjectPostRequest(String url, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(Request.Method.POST, url, errorListener);
		mListener = listener;
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mMap;
	}

	//
	// /** 请求超时设置 */
	// @Override
	// public RetryPolicy getRetryPolicy() {
	// RetryPolicy retryPolicy = new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
	// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
	// return retryPolicy;
	// }

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		} catch (JSONException e) {
			e.printStackTrace();
			return Response.error(new ParseError(e));
		}
	}

	/**
	 * 后续可在此解析数据，减少每次onResponse的解析次数
	 */
	@Override
	protected void deliverResponse(JSONObject response) {
		mListener.onResponse(response);
	}

}

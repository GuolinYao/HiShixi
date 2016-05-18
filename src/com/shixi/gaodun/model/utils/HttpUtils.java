package com.shixi.gaodun.model.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.shixi.gaodun.model.domain.HttpRes;

public class HttpUtils {
	/** 字符集 */
	private static final String ENCODING = "UTF-8";
	/** 返回值类型 */
	private static final String APPLICATION_JSON = "application/json";

	/**
	 * 上传头像及其他参数
	 * 
	 * @param url
	 * @param parames
	 * @param file
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static HttpRes postSingleFile(String url, List<BasicNameValuePair> parames, File file, String fileName)
			throws Exception {
		HttpRes httpRes = new HttpRes();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url.trim());
		// 期望上传后返回xml格式数据
		httpPost.setHeader("accept", APPLICATION_JSON);
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (parames != null) {
			for (BasicNameValuePair param : parames) {
				entity.addPart(param.getName(), new StringBody(param.getValue(), Charset.forName(ENCODING)));
			}
		}
		if (file != null && file.exists()) {
			FileBody body = new FileBody(file);
			entity.addPart(fileName, body);
		}
		httpPost.setEntity(entity);
		HttpResponse httpResponse = client.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			String str = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
			JSONObject jsonObject = new JSONObject(str);
			if (jsonObject.has("returnCode")) {
				httpRes.setReturnCode(jsonObject.getInt("returnCode"));
			}
			if (jsonObject.has("returnDesc")) {
				httpRes.setReturnDesc(jsonObject.getString("returnDesc"));
			}
			if (jsonObject.has("returnData")) {
				httpRes.setReturnData(jsonObject.getString("returnData"));
			}
			// httpRes.setReturnDataJson(EntityUtils.toString(httpResponse.getEntity(), ENCODING));
		}
		return httpRes;

	}

	/**
	 * 上传多个文件及其他参数
	 * 
	 * @param url
	 * @param parames
	 * @param file
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static HttpRes postFormFiles(String url, List<BasicNameValuePair> parames, File file, String fileName)
			throws Exception {
		HttpRes httpRes = new HttpRes();
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url.trim());
		// 期望上传后返回xml格式数据
		httpPost.setHeader("accept", APPLICATION_JSON);
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (parames != null) {
			for (BasicNameValuePair param : parames) {
				entity.addPart(param.getName(), new StringBody(param.getValue(), Charset.forName(ENCODING)));
			}
		}
		if (file != null && file.exists()) {
			File[] files = file.listFiles();
			if (null != files && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					File file2 = files[i];
					FileBody body = new FileBody(file2);
					entity.addPart(fileName + i, body);
				}
			}
		}
		httpPost.setEntity(entity);
		HttpResponse httpResponse = client.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			String str = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
			JSONObject jsonObject = new JSONObject(str);
			if (jsonObject.has("returnCode")) {
				httpRes.setReturnCode(jsonObject.getInt("returnCode"));
			}
			if (jsonObject.has("returnDesc")) {
				httpRes.setReturnDesc(jsonObject.getString("returnDesc"));
			}
			if (jsonObject.has("returnData")) {
				httpRes.setReturnData(jsonObject.getString("returnData"));
			}
		}
		return httpRes;

	}
}

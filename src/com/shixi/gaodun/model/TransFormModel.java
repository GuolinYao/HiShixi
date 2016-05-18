package com.shixi.gaodun.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.shixi.gaodun.model.domain.CertificateBean;
import com.shixi.gaodun.model.domain.CityBean;
import com.shixi.gaodun.model.domain.EducationBGBean;
import com.shixi.gaodun.model.domain.HttpRes;
import com.shixi.gaodun.model.domain.ImageInfoBean;
import com.shixi.gaodun.model.domain.InternshipIntentionBean;
import com.shixi.gaodun.model.domain.InvitationBean;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.domain.MajorClassifyBean;
import com.shixi.gaodun.model.domain.MapFormatBean;
import com.shixi.gaodun.model.domain.RecommendInfoBean;
import com.shixi.gaodun.model.domain.ResumeBean;
import com.shixi.gaodun.model.domain.TalentInfoBean;
import com.shixi.gaodun.model.domain.UserInfoBean;
import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 数据解析
 * 
 * @author ronger
 * @date:2015-10-20 下午1:24:54
 */
public class TransFormModel {
	private static Gson gson = new Gson();

	public static HttpRes getResponseData(JSONObject response) throws Exception {
		HttpRes httpRes = new HttpRes();
		if (response.has("returnCode"))
			httpRes.setReturnCode(response.getInt("returnCode"));
		if (response.has("returnDesc"))
			httpRes.setReturnDesc(response.getString("returnDesc"));
		if (response.has("returnData")) {
			httpRes.setReturnData(response.getString("returnData"));
		}
		return httpRes;
	}

	/**
	 * 解析对应的数据
	 * 
	 * @param resultData
	 * @param classBean
	 * @return
	 * @return
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getResponseResult(String resultData,
			Class<T> classBean) throws Exception {
		return (Class<T>) gson.fromJson(resultData, classBean);
	}

	public static <T> T getResponseResultObject(String resultData,
			Class<T> classBean) throws Exception {
		return gson.fromJson(resultData, classBean);
	}

	/**
	 * 解析数据列表
	 * 
	 * @param resultData
	 * @param classBean
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getResponseResults(String resultData,
			Class<T> classBean) throws Exception {
		List<T> list = new ArrayList<T>(0);
		Gson gson = new Gson();
		JsonArray array = new JsonParser().parse(resultData).getAsJsonArray();
		for (final JsonElement elem : array) {
			list.add(gson.fromJson(elem, classBean));
		}
		return list;
	}

	/**
	 * 获取热门城市列表
	 * 
	 * @param array
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<CityBean> getHotCitys(JSONArray array,
			CityBean selectBean, boolean ifAddAll) throws Exception {
		ArrayList<CityBean> lists = new ArrayList<CityBean>(0);
		if (ifAddAll) {
			boolean isSelected = false;
			if (null != selectBean && "全国".equals(selectBean.getRegion_name())) {
				isSelected = true;
			}
			lists.add(new CityBean("", "全国", "热门城市", isSelected));
			// lists.add(new CityBean("", "不限", "热", isSelected));
		}
		for (int i = 0; i < array.length(); i++) {
			CityBean bean = gson.fromJson(array.getString(i), CityBean.class);
			// bean.setSortLetters("热");
			bean.setSortLetters("热门城市");
			bean.setIfSelected(false);
			if (null != selectBean
					&& bean.getRegion_id().equals(selectBean.getRegion_id())) {
				bean.setIfSelected(selectBean.isIfSelected());
			}
			lists.add(bean);
		}
		return lists;

	}

	public static ArrayList<CityBean> getHotCitys(JSONArray array,
			ArrayList<CityBean> mSelectCitys, boolean ifAddAll)
			throws Exception {
		ArrayList<CityBean> lists = new ArrayList<CityBean>();
		if (ifAddAll) {
			boolean isSelected = true;
			// if (null != selectBean &&
			// "全国".equals(selectBean.getRegion_name())) {
			// isSelected = true;
			// }
			lists.add(new CityBean("", "全国", "热门城市", isSelected));
		}
		for (int i = 0; i < array.length(); i++) {
			CityBean bean = gson.fromJson(array.getString(i), CityBean.class);
			bean.setSortLetters("热");
			bean.setIfSelected(false);
			if (null == mSelectCitys || mSelectCitys.isEmpty()) {
				lists.add(bean);
				continue;
			}
			for (CityBean cityBean : mSelectCitys) {
				if (bean.getRegion_id().equals(cityBean.getRegion_id())) {
					bean.setIfSelected(true);
					break;
				}
			}
			lists.add(bean);
		}
		return lists;

	}

	/**
	 * 根据首字母获取城市列表
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static Map<String, List<CityBean>> getCitysByInitial(String json)
			throws Exception {
		Map<String, List<CityBean>> mapCitys = new HashMap<String, List<CityBean>>();
		mapCitys = gson.fromJson(json,
				new TypeToken<Map<String, List<CityBean>>>() {
				}.getType());
		return mapCitys;
	}

	public static ArrayMap<String, List<CityBean>> getCityListsByInitial(
			String json) throws Exception {
		ArrayMap<String, List<CityBean>> arrayMap = new ArrayMap<String, List<CityBean>>();
		arrayMap = gson.fromJson(json,
				new TypeToken<ArrayMap<String, List<CityBean>>>() {
				}.getType());
		return arrayMap;
	}

	/**
	 * 专业分类列表的解析
	 * 
	 * @param array
	 * @param selectBean
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<MajorClassifyBean> getMajorClassifyLists(
			JSONArray array, MajorClassifyBean selectBean) throws Exception {
		ArrayList<MajorClassifyBean> lists = new ArrayList<MajorClassifyBean>();
		for (int i = 0; i < array.length(); i++) {
			MajorClassifyBean bean = gson.fromJson(array.getString(i),
					MajorClassifyBean.class);
			bean.setIfSelected(false);
			if (null != selectBean
					&& bean.getPkid().equals(selectBean.getPkid())) {
				bean.setIfSelected(selectBean.isIfSelected());
			}
			lists.add(bean);
		}
		return lists;

	}

	public static ArrayList<EducationBGBean> getEducationBGList(
			String resultData) throws Exception {
		ArrayList<EducationBGBean> list = new ArrayList<EducationBGBean>();
		Gson gson = new Gson();
		JSONArray array = new JSONArray(resultData);
		// JsonArray array = new
		// JsonParser().parse(resultData).getAsJsonArray();
		for (int i = 0; i < array.length(); i++) {
			String str = array.getString(i);
			EducationBGBean educationBean = gson.fromJson(str,
					EducationBGBean.class);
			JSONObject object = new JSONObject(str);
			if (object.has("degree")) {
				JSONObject degree = new JSONObject(object.getString("degree"));
				educationBean.setDegreeName(degree.getString("value"));
				educationBean.setDegreeKey(degree.getString("key"));
			}
			list.add(educationBean);
		}
		return list;
	}

	/**
	 * 证书列表解析
	 * 
	 * @param resultData
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<CertificateBean> getCertificateBeanList(
			String resultData) throws Exception {
		ArrayList<CertificateBean> list = new ArrayList<CertificateBean>(0);
		Gson gson = new Gson();
		JSONArray array = new JSONArray(resultData);
		for (int i = 0; i < array.length(); i++) {
			String str = array.getString(i);
			CertificateBean certificateBean = gson.fromJson(str,
					CertificateBean.class);
			list.add(certificateBean);
		}
		return list;
	}

	/**
	 * 证书分类列表和证书名称列表
	 * 
	 * @param resultData
	 * @param mCertificateBean
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<MapFormatBean> getCertificateBeanList(
			JSONArray jsonArray, MapFormatBean mCertificateBean,
			boolean ifTypeOrName) throws Exception {
		ArrayList<MapFormatBean> lists = new ArrayList<MapFormatBean>(0);
		for (int i = 0; i < jsonArray.length(); i++) {
			MapFormatBean mapBean = null;
			JSONObject object = new JSONObject(jsonArray.getString(i));
			if (ifTypeOrName) {
				mapBean = new MapFormatBean(object.getString("key"),
						object.getString("name"));
			} else {

				mapBean = new MapFormatBean(object.getString("pkid"),
						object.getString("full_name"));
			}
			mapBean.setSelected(false);
			if (null == mCertificateBean) {
				lists.add(mapBean);
				continue;
			}
			if (StringUtils.isNotEmpty(mCertificateBean.getValue())
					&& mCertificateBean.getValue().equals(mapBean.getValue())) {
				mapBean.setSelected(true);
			}
			lists.add(mapBean);
		}
		return lists;

	}

	public static ArrayList<MapFormatBean> getCateLists(JSONArray jsonArray,
			boolean isExitAll) throws JSONException {
		ArrayList<MapFormatBean> lists = new ArrayList<MapFormatBean>(0);
		// if (isExitAll) {
		// lists.add(new MapFormatBean("", "全部"));
		// }
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject object = new JSONObject(jsonArray.getString(i));
			MapFormatBean mapBean = new MapFormatBean(object.getString("pkid"),
					object.getString("title"));
			lists.add(mapBean);
		}
		return lists;
	}

	/**
	 * 获取简历预览信息
	 * 
	 * @param resultData
	 * @return
	 * @throws Exception
	 */
	public static ResumeBean getResumeBean(String resultData) throws Exception {
		JSONObject jsonObject = new JSONObject(resultData);
		ResumeBean bean = new ResumeBean();
		// 基本信息
		if (jsonObject.has("basic")) {
			UserInfoBean basic = gson.fromJson(jsonObject.getString("basic"),
					UserInfoBean.class);
			bean.setBasic(basic);
		}
		// 实习意向
		if (jsonObject.has("expect")) {
			InternshipIntentionBean expect = gson.fromJson(
					jsonObject.getString("expect"),
					InternshipIntentionBean.class);
			bean.setExpect(expect);
		}
		// 教育经历
		if (jsonObject.has("education")) {
			ArrayList<EducationBGBean> education = (ArrayList<EducationBGBean>) getResponseResults(
					jsonObject.getString("education"), EducationBGBean.class);
			bean.setEducation(education);
		}
		// 实习经历
		if (jsonObject.has("practice")) {
			ArrayList<EducationBGBean> practice = (ArrayList<EducationBGBean>) getResponseResults(
					jsonObject.getString("practice"), EducationBGBean.class);
			bean.setPractice(practice);
		}
		// 课外活动
		if (jsonObject.has("activity")) {
			ArrayList<EducationBGBean> activity = (ArrayList<EducationBGBean>) getResponseResults(
					jsonObject.getString("activity"), EducationBGBean.class);
			bean.setActivity(activity);
		}
		// 校内职务
		if (jsonObject.has("school")) {
			ArrayList<EducationBGBean> school = (ArrayList<EducationBGBean>) getResponseResults(
					jsonObject.getString("school"), EducationBGBean.class);
			bean.setSchool(school);
		}
		// 证书信息
		if (jsonObject.has("cert")) {
			ArrayList<CertificateBean> cert = (ArrayList<CertificateBean>) getResponseResults(
					jsonObject.getString("cert"), CertificateBean.class);
			bean.setCert(cert);
		}
		// 荣誉
		if (jsonObject.has("prize")) {
			ArrayList<EducationBGBean> prize = (ArrayList<EducationBGBean>) getResponseResults(
					jsonObject.getString("prize"), EducationBGBean.class);
			bean.setPrize(prize);
		}
		return bean;
	}

	/**
	 * 获取人才库预览信息
	 * 
	 * @param resultData
	 * @return
	 * @throws Exception
	 */
	public static TalentInfoBean getTalentBean(String resultData)
			throws Exception {
		// 基本信息
		TalentInfoBean basic = gson.fromJson(resultData, TalentInfoBean.class);
		return basic;
	}

	public static InvitationBean getInvitationBean(String resultData)
			throws Exception {
		JSONObject jsonObject = new JSONObject(resultData);
		InvitationBean bean = new InvitationBean();
		bean = gson.fromJson(resultData, InvitationBean.class);
		return bean;
	}

	public static InvitationInfoBean getInvitationInfoBean(String resultData,
			boolean isArray) throws Exception {
		JSONObject jsonObject = new JSONObject(resultData);
		InvitationInfoBean bean = new InvitationInfoBean();
		bean = gson.fromJson(resultData, InvitationInfoBean.class);// jsonObject.getString("image")
		String image = jsonObject.has("image") ? jsonObject.getString("image")
				: "";
		if (StringUtils.isEmpty(image))
			return bean;
		if (image.length() > 2 && isArray) {
			List<ImageInfoBean> images = getResponseResults(image,
					ImageInfoBean.class);
			bean.setImages(images);
			return bean;
		}
		if (!isArray) {
			bean.setSingeimage(image);
			return bean;
		}
		return bean;
	}

	public static List<InvitationInfoBean> getInvitationInfoList(
			String resultData, boolean isArray) throws Exception {
		List<InvitationInfoBean> list = new ArrayList<InvitationInfoBean>(0);
		JSONArray array = new JSONArray(resultData);
		for (int i = 0; i < array.length(); i++) {
			InvitationInfoBean bean = getInvitationInfoBean(array.getString(i),
					isArray);
			// JSONObject object = new JSONObject(array.getString(i));
			// InvitationInfoBean bean = gson.fromJson(array.getString(i),
			// InvitationInfoBean.class);
			// if (object.has("image")) {
			// bean.setSingeimage(object.getString("image"));
			// }
			list.add(bean);
		}
		return list;
	}

	/**
	 * 获取推荐列表的信息
	 * 
	 * @param resultData
	 * @param isArray
	 * @return
	 * @throws Exception
	 */
	public static List<RecommendInfoBean> getRecommendInfoList(
			String resultData, boolean isArray) throws Exception {
		List<RecommendInfoBean> list = new ArrayList<RecommendInfoBean>(0);
		JSONArray array = new JSONArray(resultData);
		for (int i = 0; i < array.length(); i++) {
			RecommendInfoBean bean = getRecommendInfoBean(array.getString(i),
					isArray);
			list.add(bean);
		}
		return list;
	}

	public static RecommendInfoBean getRecommendInfoBean(String resultData,
			boolean isArray) throws Exception {
		// JSONObject jsonObject = new JSONObject(resultData);
		RecommendInfoBean bean = new RecommendInfoBean();
		bean = gson.fromJson(resultData, RecommendInfoBean.class);// jsonObject.getString("image")
		return bean;
	}
}

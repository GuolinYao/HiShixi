package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.ArrayList;

import com.shixi.gaodun.model.utils.StringUtils;

/**
 * 实习意向信息
 * 
 * @author ronger
 * @date:2015-11-5 下午1:21:07
 */
public class InternshipIntentionBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pkid;
	private ArrayList<CityBean> expect_city;// 实习意向城市
	private String week_available;// 每周可实习天数
	private MapFormatBean salary_range;// 期望日薪
	private MapFormatBean expect_category;// 期望实习岗位
	private String period_start;// 实习开始时间
	private String period_finish;// 实习结束时间

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public ArrayList<CityBean> getExpect_city() {
		return expect_city;
	}

	public void setExpect_city(ArrayList<CityBean> expect_city) {
		this.expect_city = expect_city;
	}

	public String getWeek_available() {
		return week_available;
	}

	public void setWeek_available(String week_available) {
		this.week_available = week_available;
	}

	public MapFormatBean getSalary_range() {
		return salary_range;
	}

	public void setSalary_range(MapFormatBean salary_range) {
		this.salary_range = salary_range;
	}

	public MapFormatBean getExpect_category() {
		return expect_category;
	}

	public void setExpect_category(MapFormatBean expect_category) {
		this.expect_category = expect_category;
	}

	public String getPeriod_start() {
		if (StringUtils.isEmpty(period_start))
			return period_start;
		// 用来显示
		period_start = period_start.replace("-", ".");
		return period_start;
	}

	public void setPeriod_start(String period_start) {
		if (StringUtils.isEmpty(period_start))
			return;
		period_start = period_start.replace("-", ".");
		this.period_start = period_start;
	}

	public String getPeriod_finish() {
		if (StringUtils.isEmpty(period_finish))
			return period_finish;
		period_finish = period_finish.replace("-", ".");
		return period_finish;
	}

	public void setPeriod_finish(String period_finish) {
		if (StringUtils.isEmpty(period_finish))
			return;
		period_finish = period_finish.replace("-", ".");
		this.period_finish = period_finish;
	}

	public String getCityListStr(String pinStr) {
		StringBuilder selectCitySB = new StringBuilder();
		for (int i = 0; i < expect_city.size(); i++) {
			CityBean cityBean = expect_city.get(i);
			selectCitySB.append(cityBean.getRegion_id());
			if (i != expect_city.size() - 1)
				selectCitySB.append(pinStr);
			// selectCitySB.append("	");
		}
		return selectCitySB.toString();
	}

	public String getToServerStartTime() {
		if (StringUtils.isEmpty(period_start))
			return "";
		String startTime = period_start.replace(".", "-");
		return startTime;
	}

	public String getToServerEndTime() {
		if (StringUtils.isEmpty(period_finish))
			return "";
		String endTime = period_finish.replace(".", "-");
		return endTime;
	}
}

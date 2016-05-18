package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 
 * @author guolinyao
 * @date 2016年3月3日 下午3:11:34
 */
public class TaskInfoBean implements Serializable {

	private String pkid;
	private String name;// 任务名称
	private String start_time;// 开始时间
	private String finish_time;// 结束时间
	private String get_total;// 已获取简历数
	private String aim_total;// 目标KPI简历数
	private String days;// 距离任务结束的天数
	private String is_urgency;// 是否开启紧急职位 1 开启 2 不开启
	private String description;// 紧急任务描述
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIs_urgency() {
		return is_urgency;
	}
	public void setIs_urgency(String is_urgency) {
		this.is_urgency = is_urgency;
	}
	public String getPkid() {
		return pkid;
	}
	public void setPkid(String pkid) {
		this.pkid = pkid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getFinish_time() {
		return finish_time;
	}
	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}
	public String getGet_total() {
		return get_total;
	}
	public void setGet_total(String get_total) {
		this.get_total = get_total;
	}
	public String getAim_total() {
		return aim_total;
	}
	public void setAim_total(String aim_total) {
		this.aim_total = aim_total;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}

}

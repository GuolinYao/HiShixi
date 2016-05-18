package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 首页广告位
 * 
 * @author ronger
 * @date:2015-11-16 上午10:40:21
 */
public class HomeBannerBean implements Serializable {
	private String pkid;// 主键
	private String title;// 广告名称
	private String link_url;// 链接地址
	private String banner_path;// 图片路径
	private String model_type;// 跳转对象 1:推荐活动,2:其它
	private String model_id;// 对象表pkid

	public String getModel_type() {
		return model_type;
	}

	public void setModel_type(String model_type) {
		this.model_type = model_type;
	}

	public String getModel_id() {
		return model_id;
	}

	public void setModel_id(String model_id) {
		this.model_id = model_id;
	}

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink_url() {
		return link_url;
	}

	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}

	public String getBanner_path() {
		return banner_path;
	}

	public void setBanner_path(String banner_path) {
		this.banner_path = banner_path;
	}

}

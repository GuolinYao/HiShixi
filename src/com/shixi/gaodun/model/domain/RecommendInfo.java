package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 推荐信息
 * 
 * @author guolinyao
 * @date:2016-01-11 上午 11：55：54
 */
public class RecommendInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pkid;// 推荐主键ID
	private String title;// 推荐标题
	private String tips_title;// 引导标题
	private String top_banner;// banner图片

	private String tips_content;// 引导内容
	private String forum_id;// 话题ID
	private String topic_id;// 帖子ID
	private String link_url;//链接地址

	public String getLink_url() {
		return link_url;
	}

	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}

	public String getPkid() {
		return pkid;
	}

	public String getForum_id() {
		return forum_id;
	}

	public void setForum_id(String forum_id) {
		this.forum_id = forum_id;
	}

	public String getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
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

	public String getTips_title() {
		return tips_title;
	}

	public void setTips_title(String tips_title) {
		this.tips_title = tips_title;
	}

	public String getTop_banner() {
		return top_banner;
	}

	public void setTop_banner(String top_banner) {
		this.top_banner = top_banner;
	}

	public String getTips_content() {
		return tips_content;
	}

	public void setTips_content(String tips_content) {
		this.tips_content = tips_content;
	}

}

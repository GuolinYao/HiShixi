package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 话题信息
 * 
 * @author ronger
 * @date:2015-11-16 下午2:25:41
 */
public class TopicInfo implements Serializable {
	private String pkid;
	private String title;
	private String index_title;
	private String banner;

	private String forum_id;// 话题id
	private String tips_title;// 引导话题
	private String tips_content;// 引导内容
	private String icon_image;// 话题icon图片

	private String topic_id;

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

	public String getIndex_title() {
		return index_title;
	}

	public void setIndex_title(String index_title) {
		this.index_title = index_title;
	}

	public String getBanner() {
		return banner;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public String getForum_id() {
		return forum_id;
	}

	public void setForum_id(String forum_id) {
		this.forum_id = forum_id;
	}

	public String getTips_title() {
		return tips_title;
	}

	public void setTips_title(String tips_title) {
		this.tips_title = tips_title;
	}

	public String getTips_content() {
		return tips_content;
	}

	public void setTips_content(String tips_content) {
		this.tips_content = tips_content;
	}

	public String getIcon_image() {
		return icon_image;
	}

	public void setIcon_image(String icon_image) {
		this.icon_image = icon_image;
	}

	public String getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}

}

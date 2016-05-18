package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 对于跟帖的评论
 * 
 * @author ronger
 * @date:2015-11-27 下午4:27:13
 */
public class CommentInvitationBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pkid;// 互动id
	private String type;
	private String content;// 帖子内容
	private String topic_id;// 跟帖ID
	private String create_time;// 发布时间
	private String reply_id;// 互动ID
	private String floor_reply_id;// 楼层贴ID
	private String anonymous;
	private String comment_status;// 评论拥有者状态 1 是 2 否
	private String nickname;
	private String g_nickname;// 被回复人名称
	private String g_anonymous;// 被回复人是否匿名 1 是 2 否
	private String student_id;
	private String gender;// 性别
	private String sns_avatar;// 头像
	private String floor;// 楼层
	private String g_content;// 目标内容
	private String g_status;// 目标状态1 已删除 2 未删除

	private List<ImageInfoBean> image;// 图片
	private String refresh_time;// 发布时间
	private String g_image;// 被评论图片

	private String g_title;// 目标对象的楼主贴的标题

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getReply_id() {
		return reply_id;
	}

	public void setReply_id(String reply_id) {
		this.reply_id = reply_id;
	}

	public String getFloor_reply_id() {
		return floor_reply_id;
	}

	public void setFloor_reply_id(String floor_reply_id) {
		this.floor_reply_id = floor_reply_id;
	}

	public String getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
	}

	public String getComment_status() {
		return comment_status;
	}

	public void setComment_status(String comment_status) {
		this.comment_status = comment_status;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getG_nickname() {
		return g_nickname;
	}

	public void setG_nickname(String g_nickname) {
		this.g_nickname = g_nickname;
	}

	public String getG_anonymous() {
		return g_anonymous;
	}

	public void setG_anonymous(String g_anonymous) {
		this.g_anonymous = g_anonymous;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSns_avatar() {
		return sns_avatar;
	}

	public void setSns_avatar(String sns_avatar) {
		this.sns_avatar = sns_avatar;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getG_content() {
		return g_content;
	}

	public void setG_content(String g_content) {
		this.g_content = g_content;
	}

	public String getG_status() {
		return g_status;
	}

	public void setG_status(String g_status) {
		this.g_status = g_status;
	}

	public List<ImageInfoBean> getImage() {
		return image;
	}

	public void setImage(List<ImageInfoBean> image) {
		this.image = image;
	}

	public String getRefresh_time() {
		return refresh_time;
	}

	public void setRefresh_time(String refresh_time) {
		this.refresh_time = refresh_time;
	}

	public String getG_image() {
		return g_image;
	}

	public void setG_image(String g_image) {
		this.g_image = g_image;
	}

	public String getG_title() {
		return g_title;
	}

	public void setG_title(String g_title) {
		this.g_title = g_title;
	}

}

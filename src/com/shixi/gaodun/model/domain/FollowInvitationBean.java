package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 跟帖
 * 
 * @author ronger
 * @date:2015-11-27 下午4:21:49
 */
public class FollowInvitationBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String pkid;// 互动id
	private String type;
	private String content;// 帖子内容
	private String topic_id;// 跟帖ID
	private String reply_id;// 互动ID
	private String floor_reply_id;// 楼层贴ID
	private String student_id;
	private String anonymous;// 是否匿名
	private String create_time;// 发布时间
	private String list_topic_status;// 跟帖所有者状态 1 是 2 否
	private String floor;// 楼层数
	private String is_topic;// 是否是楼主贴1 是 2 否
	private String nickname;// 昵称
	private String gender;// 性别
	private String sns_avatar;// 头像
	private List<ImageInfoBean> image;// 图片
	private List<CommentInvitationBean> comment;// 对于跟帖的评论
	private int commentTotal;// 评论总数
	private String reply_status;// 跟帖所有者状态

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

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public String getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getList_topic_status() {
		return list_topic_status;
	}

	public void setList_topic_status(String list_topic_status) {
		this.list_topic_status = list_topic_status;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getIs_topic() {
		return is_topic;
	}

	public void setIs_topic(String is_topic) {
		this.is_topic = is_topic;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public List<ImageInfoBean> getImage() {
		return image;
	}

	public void setImage(List<ImageInfoBean> image) {
		this.image = image;
	}

	public List<CommentInvitationBean> getComment() {
		return comment;
	}

	public void setComment(List<CommentInvitationBean> comment) {
		this.comment = comment;
	}

	public int getCommentTotal() {
		return commentTotal;
	}

	public void setCommentTotal(int commentTotal) {
		this.commentTotal = commentTotal;
	}

	public String getReply_status() {
		return reply_status;
	}

	public void setReply_status(String reply_status) {
		this.reply_status = reply_status;
	}

}

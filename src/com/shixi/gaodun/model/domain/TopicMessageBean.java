package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 消息
 * 
 * @author ronger
 * @date:2015-12-4 下午1:58:09
 */
public class TopicMessageBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message_id;// 话题消息ID
	private String topic_id;// 帖子ID
	private String read_status;// 消息阅读状态 1 已阅读 2 未阅读
	private String reply_id;// 互动ID
	private String origin_reply_id;// 楼层帖子ID
	private String operator_id;// 操作人ID
	private String receiver_id;
	private String type;// 1 点赞 2 跟帖 3 评论 4 回复
	private String create_time;
	private String nickname;
	private String gender;
	private String sns_avatar;
	private String topic_status;// 楼主贴状态 1 未删除 2 已删除
	private String topic_title;
	private String content;

	// 系统消息
	private String queue_id;// 队列ID
	private String send_time;// 发送时间
	private String status;// 阅读状态，1未阅读，2已阅读
	private String title;// 标题
	private String url;// 详情url

	private String reply_status;// 跟帖状态
	private String topic_image;// 楼主贴图片
	private String reply_image;// 跟帖图片
	private String reply_content;// 跟帖内容
	private String comment_status;// 评论状态
	private String comment;// 评论内容
	private String receve;// 回复

	private String floor;// 楼层

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}

	public String getRead_status() {
		return read_status;
	}

	public void setRead_status(String read_status) {
		this.read_status = read_status;
	}

	public String getReply_id() {
		return reply_id;
	}

	public void setReply_id(String reply_id) {
		this.reply_id = reply_id;
	}

	public String getOrigin_reply_id() {
		return origin_reply_id;
	}

	public void setOrigin_reply_id(String origin_reply_id) {
		this.origin_reply_id = origin_reply_id;
	}

	public String getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(String operator_id) {
		this.operator_id = operator_id;
	}

	public String getReceiver_id() {
		return receiver_id;
	}

	public void setReceiver_id(String receiver_id) {
		this.receiver_id = receiver_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
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

	public String getTopic_status() {
		return topic_status;
	}

	public void setTopic_status(String topic_status) {
		this.topic_status = topic_status;
	}

	public String getTopic_title() {
		return topic_title;
	}

	public void setTopic_title(String topic_title) {
		this.topic_title = topic_title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getQueue_id() {
		return queue_id;
	}

	public void setQueue_id(String queue_id) {
		this.queue_id = queue_id;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReply_status() {
		return reply_status;
	}

	public void setReply_status(String reply_status) {
		this.reply_status = reply_status;
	}

	public String getTopic_image() {
		return topic_image;
	}

	public void setTopic_image(String topic_image) {
		this.topic_image = topic_image;
	}

	public String getReply_image() {
		return reply_image;
	}

	public void setReply_image(String reply_image) {
		this.reply_image = reply_image;
	}

	public String getReply_content() {
		return reply_content;
	}

	public void setReply_content(String reply_content) {
		this.reply_content = reply_content;
	}

	public String getComment_status() {
		return comment_status;
	}

	public void setComment_status(String comment_status) {
		this.comment_status = comment_status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getReceve() {
		return receve;
	}

	public void setReceve(String receve) {
		this.receve = receve;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

}

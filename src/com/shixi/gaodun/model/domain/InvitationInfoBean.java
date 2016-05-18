package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子详细信息
 * 
 * @author ronger
 * @date:2015-11-23 下午1:58:53
 */
public class InvitationInfoBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String topic_id;
	private String title;// 帖子标题
	private String content;// 帖子内容
	private String student_id;// 学生ID
	private String anonymous;// 是否匿名
	private String official_posting;// 官方发帖
	private String favor_num;// 点赞数
	private String comment_num;// 评论数
	private String is_top;// 是否置顶
	private String refresh_time;// 发布时间
	private String nickname;// 昵称
	private String gender;// 性别
	private String sns_avatar;// 头像
	private String singeimage;// 原图
	private String width;// 缩列图宽
	private String height;// 缩略图高

	private String forum_id;// 话题Id
	private String topic_status;// 帖子所有者状态
	private String topic_collect;// 帖子收藏状态
	private String topic_favor;// 帖子点赞状态
	private List<ImageInfoBean> images;// 图片
	private List<FollowInvitationBean> list;// 跟帖列表
	private int listTotal;// 跟帖总数
	private String is_topic;// 是否是楼主贴1 是 2 否

	public String getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(String topic_id) {
		this.topic_id = topic_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public String getOfficial_posting() {
		return official_posting;
	}

	public void setOfficial_posting(String official_posting) {
		this.official_posting = official_posting;
	}

	public String getFavor_num() {
		return favor_num;
	}

	public void setFavor_num(String favor_num) {
		this.favor_num = favor_num;
	}

	public String getComment_num() {
		return comment_num;
	}

	public void setComment_num(String comment_num) {
		this.comment_num = comment_num;
	}

	public String getIs_top() {
		return is_top;
	}

	public void setIs_top(String is_top) {
		this.is_top = is_top;
	}

	public String getRefresh_time() {
		return refresh_time;
	}

	public void setRefresh_time(String refresh_time) {
		this.refresh_time = refresh_time;
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

	public String getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
	}

	public String getSingeimage() {
		return singeimage;
	}

	public void setSingeimage(String singeimage) {
		this.singeimage = singeimage;
	}

	public String getForum_id() {
		return forum_id;
	}

	public void setForum_id(String forum_id) {
		this.forum_id = forum_id;
	}

	public String getTopic_status() {
		return topic_status;
	}

	public void setTopic_status(String topic_status) {
		this.topic_status = topic_status;
	}

	public String getTopic_collect() {
		return topic_collect;
	}

	public void setTopic_collect(String topic_collect) {
		this.topic_collect = topic_collect;
	}

	public String getTopic_favor() {
		return topic_favor;
	}

	public void setTopic_favor(String topic_favor) {
		this.topic_favor = topic_favor;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public List<ImageInfoBean> getImages() {
		return images;
	}

	public void setImages(List<ImageInfoBean> images) {
		this.images = images;
	}

	public List<FollowInvitationBean> getList() {
		return list;
	}

	public void setList(List<FollowInvitationBean> list) {
		this.list = list;
	}

	public int getListTotal() {
		return listTotal;
	}

	public void setListTotal(int listTotal) {
		this.listTotal = listTotal;
	}

	public String getIs_topic() {
		return is_topic;
	}

	public void setIs_topic(String is_topic) {
		this.is_topic = is_topic;
	}

	public InvitationInfoBean(String topic_id) {
		this.topic_id = topic_id;
	}

	public InvitationInfoBean() {
	}
}

package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author ronger
 * @date:2015-11-23 下午1:55:02
 */
public class InvitationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<InvitationInfoBean> topic;
	private TopicInfo forum;
	private String nickname_status;// 昵称是否存在
	private String forbid_status;// 禁言状态

	public TopicInfo getForum() {
		return forum;
	}

	public void setForum(TopicInfo forum) {
		this.forum = forum;
	}

	public String getNickname_status() {
		return nickname_status;
	}

	public void setNickname_status(String nickname_status) {
		this.nickname_status = nickname_status;
	}

	public String getForbid_status() {
		return forbid_status;
	}

	public void setForbid_status(String forbid_status) {
		this.forbid_status = forbid_status;
	}

	public ArrayList<InvitationInfoBean> getTopic() {
		return topic;
	}

	public void setTopic(ArrayList<InvitationInfoBean> topic) {
		this.topic = topic;
	}

}

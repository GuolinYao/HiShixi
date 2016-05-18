package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * @author ronger
 * @date:2015-10-20 下午4:51:25
 */
@SuppressWarnings("serial")
public class StudentInfo implements Serializable {
	// 账户id
	private String accountId;
	// 学生Id
	private String studentId;
	// 中文简历完整度
	private String complete_rate;
	// 英文简历完整度
	private String complete_rate_en;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getComplete_rate() {
		return complete_rate;
	}

	public void setComplete_rate(String complete_rate) {
		this.complete_rate = complete_rate;
	}

	public String getComplete_rate_en() {
		return complete_rate_en;
	}

	public void setComplete_rate_en(String complete_rate_en) {
		this.complete_rate_en = complete_rate_en;
	}

}

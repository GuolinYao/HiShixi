package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 接口返回的数据对象
 * 
 * @author ronger
 * @date:2015-10-20 下午1:26:28
 */
@SuppressWarnings("serial")
public class HttpRes implements Serializable {
	private int returnCode;
	private String returnDesc;
	private String returnData;
	private String returnDataJson;
	private boolean isGoOn;// 是否继续

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnDesc() {
		return returnDesc;
	}

	public void setReturnDesc(String returnDesc) {
		this.returnDesc = returnDesc;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

	public String getReturnDataJson() {
		return returnDataJson;
	}

	public void setReturnDataJson(String returnDataJson) {
		this.returnDataJson = returnDataJson;
	}

	public boolean isGoOn() {
		return isGoOn;
	}

	public void setGoOn(boolean isGoOn) {
		this.isGoOn = isGoOn;
	}

}

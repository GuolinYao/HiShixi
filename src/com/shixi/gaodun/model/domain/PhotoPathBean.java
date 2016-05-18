package com.shixi.gaodun.model.domain;

import java.io.Serializable;

import com.shixi.gaodun.model.utils.StringUtils;

/**
 * @author ronger
 * @date:2015-11-24 下午5:38:31
 */
public class PhotoPathBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String localPath;
	private String netPath;

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public String getNetPath() {
		return netPath;
	}

	public void setNetPath(String netPath) {
		this.netPath = netPath;
	}

	public PhotoPathBean(String localPath, String netPath) {
		this.localPath = localPath;
		this.netPath = netPath;
	}

	public String getPaths() {
		if (StringUtils.isNotEmpty(localPath) && StringUtils.isNotEmpty(netPath)) {
			return localPath;
		}
		if (localPath.isEmpty() && StringUtils.isNotEmpty(netPath)) {
			return netPath;
		}
		if (StringUtils.isNotEmpty(localPath) && netPath.isEmpty()) {
			return localPath;
		}
		return "";
	}

	/**
	 * 获取是否只存在网络图片
	 * 
	 * @return
	 */
	public boolean getIfExitNetPath() {
		if (StringUtils.isNotEmpty(localPath) && StringUtils.isNotEmpty(netPath))
			return false;
		if (localPath.isEmpty() && StringUtils.isNotEmpty(netPath))
			return true;
		return false;
	}

	/**
	 * 获取是否只存在本地图片
	 * 
	 * @return
	 */
	public boolean getIfExitLocalPath() {
		if (StringUtils.isNotEmpty(localPath) && StringUtils.isNotEmpty(netPath))
			return false;
		if (netPath.isEmpty() && StringUtils.isNotEmpty(localPath))
			return true;
		return false;
	}
}

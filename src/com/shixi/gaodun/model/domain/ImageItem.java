package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * @author ronger
 * @date:2015-11-24 下午5:44:08
 */
public class ImageItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String imageId;
	public String thumbnailPath;// 缩略图路径
	public String imagePath;// 原图路径
	public boolean isSelected = false;
	public String path;

	public ImageItem(String path) {
		this.path = path;
	}
}
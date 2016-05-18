package com.shixi.gaodun.model.domain;

import java.io.Serializable; 
import java.util.ArrayList;
import java.util.List;


/**
 * 相册文件夹信息
 * 
 * @author ronger
 * @date:2015-11-25 上午8:53:45
 */
public class ImageFloder implements Serializable {
	// 图片的文件夹路径
	private String dir;
	// 第一张图片的路径
	private String firstImagePath;
	// 文件夹名称
	private String name;
	// 文件夹对应的图片
	public ArrayList<ImageItem> images = new ArrayList<ImageItem>();
	public int count = 0;
	public ArrayList<ImageItem> imageList = new ArrayList<ImageItem>();
	public String bucketName;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public ArrayList<ImageItem> getImageList() {
		return imageList;
	}

	public void setImageList(ArrayList<ImageItem> imageList) {
		this.imageList = imageList;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf);
	}

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ImageItem> getImages() {
		return images;
	}

	public void setImages(ArrayList<ImageItem> images) {
		this.images = images;
	}

}

package com.shixi.gaodun.model.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

/**
 * @author ronger
 * @date:2015-11-25 下午5:47:37
 */
public class FileModel {
	/**
	 * 将图片复制至SD卡
	 * 
	 * @param fileName
	 * @param bitmap
	 * @param quality
	 */
	public static void copyToSDCard(String fileSdPath, String fileName, Bitmap bitmap, int quality, String newPath) {
		if (!ActivityUtils.isSDCardHas()) {
			return;
		}
		if (bitmap == null) {
			return;
		}
		FileOutputStream fos = null;
		File file = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();// 可以捕获内存缓冲区的数据，转换成字节数组。
		bitmap.compress(CompressFormat.JPEG, quality, stream);
		byte[] bytes = stream.toByteArray();// 获取内存缓冲中的数据,一个字节流转换为一个字节数组
		try {
			File dir = new File(fileSdPath);
			dir.deleteOnExit();// 如果存在清除文件
			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(fileSdPath + "/", fileName + ".jpg");
			File newFile = new File(fileSdPath + "/", newPath + ".jpg");
			file.renameTo(newFile);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除目录
	 */
	public static void deleteDir(String path) {
		File dir = new File(path);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;

		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(path); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	/**
	 * 获取到指定文件夹(用来上传)
	 * 
	 */
	public static File getPostFile(String sourcePath) {
		File file = new File(sourcePath);
		File[] files = file.listFiles();
		if (null == files)
			return null;
		return file;
	}

	/**
	 * 删除指定文件
	 */
	public static void delFile(String path, String fileName) {
		File file = new File(path + "/" + fileName);
		if (file.isFile()) {
			file.delete();
		}
		file.exists();
	}
}

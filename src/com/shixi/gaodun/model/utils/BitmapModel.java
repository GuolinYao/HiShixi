package com.shixi.gaodun.model.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.shixi.gaodun.model.domain.SelectedImageBean;

/**
 * @author ronger
 * @date:2015-11-25 下午5:49:19
 */
public class BitmapModel {
	public static void saveBitmap(ArrayList<SelectedImageBean> paths, String filePath, String newName)
			throws IOException {
		Bitmap mBitmap = null;
		for (int i = 0; i < paths.size(); i++) {
			String path = paths.get(i).getPath();
			String fileName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
			mBitmap = revitionImageSize(path);// 如果图片显示较小，重新写一个由路径压缩的方法
			if (null != mBitmap) {
				FileModel.copyToSDCard(filePath, fileName, mBitmap, 100, newName + i);
			}
		}
	}

	public static Bitmap revitionImageSize(String path) throws IOException {
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
}

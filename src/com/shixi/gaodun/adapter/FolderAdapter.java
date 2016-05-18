package com.shixi.gaodun.adapter;

import java.util.ArrayList;

import android.app.Activity;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.ImageFloder;
import com.shixi.gaodun.model.domain.ViewHolder;

/**
 * 相册文件夹列表
 * 
 * @author ronger
 * @date:2015-11-25 上午8:52:58
 */
public class FolderAdapter extends CommonAdapter<ImageFloder> {
	private ImageFloder mCurrentImageFolder;

	public FolderAdapter(Activity context, ArrayList<ImageFloder> mDatas,
			int itemLayoutId, ImageFloder currentImageFolder) {
		super(context, mDatas, itemLayoutId);
		this.mCurrentImageFolder = currentImageFolder;
	}

	@Override
	public void convert(ViewHolder helper, ImageFloder item, int position) {

		helper.setText(R.id.id_dir_item_name,
				item.getBucketName() == null ? "所有照片" : item.getBucketName());
		if (item.getImageList() != null) {
			helper.setImageView(R.id.id_dir_item_image, "file://"
					+ item.getImageList().get(0).imagePath,
					R.drawable.touxiang_listdefault, null);
			helper.setText(R.id.id_dir_item_count, item.imageList.size() + "张");
		} else {// 第一个文件夹 全部图片
			helper.setImageView(
					R.id.id_dir_item_image,
					"file://"
							+ getItem(position + 1).getImageList().get(0).imagePath,
					R.drawable.touxiang_listdefault, null);
			helper.setText(R.id.id_dir_item_count, item.images.size() + "张");
		}

		helper.setImageViewVisibility(R.id.choose,
				mCurrentImageFolder == item ? 0 : 8);
	}

	public void setmCurrentImageFolder(ImageFloder mCurrentImageFolder) {
		this.mCurrentImageFolder = mCurrentImageFolder;
	}

}

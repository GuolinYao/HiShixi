package com.shixi.gaodun.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.HomeBean;
import com.shixi.gaodun.model.domain.ViewHolder;

/**
 * 首页列表适配器 author：ronger on 2016/1/15 13:29 email：dengyarong@gaodun.cn
 */
public class HomeAdapter extends CommonAdapter<HomeBean> {
	private int mCommpanyNumber;

	public HomeAdapter(Activity context, List<HomeBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	public HomeAdapter(Activity context, List<HomeBean> mDatas,
			int itemLayoutId, int commpanyNumber) {
		super(context, mDatas, itemLayoutId);
		this.mCommpanyNumber = commpanyNumber;
	}

	@Override
	public void convert(ViewHolder helper, HomeBean item, int position) {
		if (position == 0) {
			setTalentBankShow(helper, item, position);
		} else if ((position - 1) < mCommpanyNumber) {
			setCommpanyShow(helper, item, position);
		} else {
			setTopicShow(helper, item, position);
		}
	}

	/**
	 * 设置人才库
	 * 
	 * @param helper
	 * @param item
	 * @param position
	 */
	private void setTalentBankShow(ViewHolder helper, HomeBean item,
			int position) {
		helper.setRelativeLayoutVisibility(R.id.layout_hot_commpany, View.GONE);
		helper.setImageViewVisibility(R.id.iv_talent_bank, View.VISIBLE);
		// helper.setRelativeLayoutVisibility(R.id.home_item_title,
		// position == mCommpanyNumber ? View.VISIBLE : View.GONE);
		helper.setText(R.id.textview_title, "人才库");
		helper.setLeftCompoundDrawables(R.drawable.talent, R.id.textview_title);
		helper.setImageByUrl(R.id.iv_talent_bank, item.banner_path,
				R.drawable.default_image_banner, 5);
		helper.setViewVisibility(R.id.view_talent_bottom_bg, View.VISIBLE);
	}

	/**
	 * 设置热门话题
	 * 
	 * @param helper
	 * @param item
	 * @param position
	 */
	public void setTopicShow(ViewHolder helper, HomeBean item, int position) {
		helper.setRelativeLayoutVisibility(R.id.layout_hot_commpany, View.GONE);
		helper.setImageViewVisibility(R.id.image_home, View.VISIBLE);
		helper.setRelativeLayoutVisibility(R.id.home_item_title,
				(position - 1) == mCommpanyNumber ? View.VISIBLE : View.GONE);
		helper.setText(R.id.textview_title, "热门话题");
		helper.setLeftCompoundDrawables(R.drawable.remenhuati,
				R.id.textview_title);
		// Log.e("ronger","图片："+item.banner);
		helper.setImageByUrl(R.id.image_home, item.banner,
				R.drawable.default_image_banner, 5);
		helper.setViewVisibility(R.id.view_topic_bottom_bg,
				position == mLists.size() - 1 ? View.VISIBLE : View.GONE);
	}

	public void setCommpanyShow(ViewHolder helper, HomeBean item, int position) {
		helper.setRelativeLayoutVisibility(R.id.home_item_title,
				position == 1 ? View.VISIBLE : View.GONE);
		helper.setText(R.id.textview_title, "热门企业");
		helper.setLeftCompoundDrawables(R.drawable.remenqiye,
				R.id.textview_title);
		helper.setRelativeLayoutVisibility(R.id.layout_hot_commpany,
				View.VISIBLE);
		helper.setImageViewVisibility(R.id.image_home, View.GONE);
		helper.setText(R.id.text_commpany_name, item.full_name);
		helper.setText(R.id.text_commpany_desc, item.editor_note);
		helper.setImageByUrl(R.id.image_hotcommpany, item.logo,
				R.drawable.default_image_icon, 5);
		helper.setViewVisibility(R.id.view_commpany_bottom_bg,
				position == mCommpanyNumber - 1 ? View.VISIBLE : View.GONE);

	}

	class HomeViewHoler {
		TextView commPanyName;
		TextView commPanyDes;
		ImageView logo;
		RelativeLayout layout;

	}

	class HomeViewTopicHolder {
		ImageView banner;
		RelativeLayout layout;
	}
}

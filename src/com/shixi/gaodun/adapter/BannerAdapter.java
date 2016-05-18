package com.shixi.gaodun.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.HomeBannerBean;
import com.shixi.gaodun.model.domain.InvitationInfoBean;
import com.shixi.gaodun.model.utils.DisplayImageOptionsUtils;
import com.shixi.gaodun.view.activity.BannerImageDetail;
import com.shixi.gaodun.view.activity.banner.RecommendActivity;
import com.shixi.gaodun.view.activity.enterprise.EnterpriseDetailActivity;
import com.shixi.gaodun.view.activity.invitation.InvitationDetailListActivity;

import java.util.ArrayList;

/**
 * @author ronger
 * @date:2015-11-16 上午11:23:20
 */
@SuppressLint("InflateParams")
public class BannerAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HomeBannerBean> mBannerLists;
	private Context mContext;
	private int nowPosition;// 现在的位置，为了实现随意手指左右滑动
	private int count = Integer.MAX_VALUE;// banner图片总数

	public BannerAdapter(Activity context, ArrayList<HomeBannerBean> bannerLists) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		this.mBannerLists = bannerLists;
	}

	@Override
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public Object getItem(int position) {
		return mBannerLists.get(position % mBannerLists.size());
	}

	@Override
	public long getItemId(int position) {
		return position % mBannerLists.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// int oldPosition = nowPosition;
		// if (position == 0) {
		// nowPosition = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE
		// % mBannerLists.size()-1;
		// }
		nowPosition = position;
		nowPosition = nowPosition % mBannerLists.size();
		// if (nowPosition==oldPosition){
		// nowPosition--;
		// }

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.viewflow_image_item, null);
		}
		DisplayImageOptions options = DisplayImageOptionsUtils.getRoundBitMap(
				0, R.drawable.default_image_banner);
		try {
			String path = mBannerLists.get(nowPosition).getBanner_path();
			ImageLoader.getInstance().displayImage(path,
					((ImageView) convertView), options);
		} catch (Exception e) {
			ImageLoader.getInstance().displayImage(
					"drawable://" + R.drawable.default_image_banner,
					((ImageView) convertView), options);
		}
		ImageView imgView = (ImageView) convertView.findViewById(R.id.imgView);
		// 设置图片广告的点击事件
		imgView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到每周推荐
				// 如果有无链接 那么跳转到原声控件
				if (mBannerLists.get(nowPosition).getLink_url().equals("")) {
					if (mBannerLists.get(nowPosition).getModel_type()
							.equals("1")) {
						// 推荐
						RecommendActivity.startAction(mContext, mBannerLists
								.get(nowPosition).getModel_id());
					} else if (mBannerLists.get(nowPosition).getModel_type()
							.equals("2")) {
						// 话题详情
						InvitationInfoBean invitationInfo = new InvitationInfoBean();
						invitationInfo.setTopic_id(mBannerLists
								.get(nowPosition).getModel_id());
						InvitationDetailListActivity.startAction(
								(Activity) mContext, invitationInfo);
					} else if (mBannerLists.get(nowPosition).getModel_type()
							.equals("3")) {
						// 企业详情
						EnterpriseDetailActivity.startAction(
								(Activity) mContext,
								mBannerLists.get(nowPosition).getModel_id(),1);
					}
				} else {
					// 如果有链接 跳转到h5页面
					String link_url = mBannerLists.get(nowPosition)
							.getLink_url();
					BannerImageDetail.startAction(mContext, link_url);
				}

			}
		});
		return convertView;
	}
}

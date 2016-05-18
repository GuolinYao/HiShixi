package com.shixi.gaodun.adapter;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.shixi.gaodun.R;
import com.shixi.gaodun.model.domain.RecommendInfoBean;
import com.shixi.gaodun.model.domain.RecommendInfoBean.Extension;
import com.shixi.gaodun.model.domain.RecommendInfoBean.Post;
import com.shixi.gaodun.view.activity.banner.RecommendActivity;
import com.shixi.gaodun.view.activity.enterprise.EnterpriseDetailActivity;
import com.shixi.gaodun.view.activity.enterprise.PositionDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 每周推荐的适配器
 * 
 * @author guolinyao
 * @date:2016-01-11
 */
public class RecommendListAdapter extends CommonAdapter<RecommendInfoBean> {
	private RecommendActivity mContext;

	public RecommendListAdapter(Activity context,
			List<RecommendInfoBean> mDatas, int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		// TODO Auto-generated constructor stub
		this.mContext = (RecommendActivity) context;
	}

	@Override
	public void setItemView(int position) {

	}

	@Override
	public void convert(com.shixi.gaodun.model.domain.ViewHolder helper,
			RecommendInfoBean item, int position) {
		setItemShow(helper, item, position);

	}

	/**
	 * 设置item显示
	 * 
	 * @param helper
	 * @param item
	 * @param position
	 */
	public void setItemShow(com.shixi.gaodun.model.domain.ViewHolder helper,
			RecommendInfoBean item, final int position) {
//		int extend_id = NumberUtils.getInt(item.extend_id, 1);// 推荐信息详情主键ID
//		int activity_id = NumberUtils.getInt(item.activity_id, 1);// 推荐活动ID
//		String enterprise_id = item.enterprise_id;// 企业ID
		ArrayList<Post> post = (ArrayList<Post>) item.post;// 职位ID
		ArrayList<Extension> extension = (ArrayList<Extension>) item.extension;// 扩展信息
																				// key键名
		String logo_url = item.logo;// 企业logo
		String enterprise_name = item.enterprise_name;// 企业名称
		String industry_title = item.industry_title;// 企业所属行业
		String scale_title = item.scale_title;// 企业规模
		String editor_note = item.editor_note;// 企业描述
		String keep_on = item.keep_on;//是否可留用

		// 设置企业信息 圆角矩形
		helper.setImageByUrl(R.id.iv_company_logo, logo_url,
				R.drawable.default_image_icon, 5);
		helper.setText(R.id.tv_company_name, enterprise_name);// 企业名称
		helper.setText(R.id.tv_company_industry, industry_title);// 企业所属行业
		helper.setText(R.id.tv_company_scale, " | " + scale_title);// 企业规模
		if(!editor_note.equals("")){
			helper.setText(R.id.tv_company_desc, editor_note);// 企业描述
		}else {
			helper.setTextViewVisibility(R.id.tv_company_desc,View.GONE);
		}

		// 点击企业，进入企业详情
		RelativeLayout company = helper.getView(R.id.rl_company);

		company.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EnterpriseDetailActivity.startAction(mContext,
						mLists.get(position).enterprise_id,1);
			}
		});

		// 判断是否有职位
		if (null==post||post.isEmpty()) {// 如果没有职位
			// 如果没有职位信息，那么隐藏职位条目
			helper.setRelativeLayoutVisibility(R.id.rl_jobs, View.GONE);
			// 隐藏分割线
			helper.setViewVisibility(R.id.view_line1, View.GONE);

		} else {// 显示职位信息
			helper.setRelativeLayoutVisibility(R.id.rl_jobs, View.VISIBLE);
			// 显示分割线
			helper.setViewVisibility(R.id.view_line1, View.VISIBLE);
			// 设置职位信息
			helper.setText(R.id.tv_job_name, post.get(0).post_title);// 设置职位名称
			helper.setText(R.id.tv_job_city, " [" + post.get(0).region_name
					+ "]");// 职位所在城市
			helper.setText(R.id.tv_job_salary, post.get(0).salary_range);// 薪资范围
			helper.setText(R.id.tv_job_edu, " | " + post.get(0).education);// 学历
			helper.setText(R.id.tv_job_days_perweek, " | "
					+ post.get(0).week_available);// 每周实习天数
			helper.setText(R.id.tv_job_canstay, " | " + post.get(0).keep_on);// 是否可留用
			
			// 点击职位 进入职位详情
			RelativeLayout job = helper.getView(R.id.rl_jobs);
			job.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 进入职位详情
					PositionDetailActivity.startAction(mContext,
							mLists.get(position).post.get(0).pkid,1);
				}
			});
		}

		// 判断是否有扩展
		if (extension.size() == 0 || extension.isEmpty()) {// 如果扩展为空 那么隐藏扩展条目
			helper.setRelativeLayoutVisibility(R.id.rl_expand, View.GONE);
			// 隐藏分割线
			helper.setViewVisibility(R.id.view_line2, View.GONE);

		} else {// 显示扩展信息
			helper.setRelativeLayoutVisibility(R.id.rl_expand, View.VISIBLE);
			// 显示分割线
			helper.setViewVisibility(R.id.view_line2, View.VISIBLE);
			// 设置扩展信息
			int size = extension.size();
			if (size > 0) {
				helper.setText(R.id.tv_name1, extension.get(0).key);// 扩展描述
				helper.setRating(R.id.rt_rating1, extension.get(0).value);// 评分
				helper.setViewVisibility(R.id.ll_expend_1, View.VISIBLE);
			}
			if (size > 1) {
				helper.setText(R.id.tv_name2, extension.get(1).key);// 扩展描述
				helper.setRating(R.id.rt_rating2, extension.get(1).value);// 评分
				helper.setViewVisibility(R.id.ll_expend_2, View.VISIBLE);
			}
			if (size > 2) {
				helper.setText(R.id.tv_name3, extension.get(2).key);// 扩展描述
				helper.setRating(R.id.rt_rating3, extension.get(2).value);// 评分
				helper.setViewVisibility(R.id.ll_expend_3, View.VISIBLE);
			}
			if (size > 3) {
				helper.setText(R.id.tv_name4, extension.get(3).key);// 扩展描述
				helper.setRating(R.id.rt_rating4, extension.get(3).value);// 评分
				helper.setViewVisibility(R.id.ll_expend_4, View.VISIBLE);
			}



		}

	}

}

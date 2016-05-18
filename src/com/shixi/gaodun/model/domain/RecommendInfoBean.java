package com.shixi.gaodun.model.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 推荐信息（企业+职位+扩展）
 * 
 * @author guolinyao
 * @date 2016年1月8日 下午3:39:29
 */
public class RecommendInfoBean implements Serializable {

	public String extend_id;// 扩展id
	public String activity_id;// 活动id
	public String enterprise_id;// 企业id
	public String post_id;//职位id
	public String is_open;//是否公开
	public String keep_on;//是否留用

	public String logo;// 企业logo
	public String enterprise_name;// 企业名称
	public String industry_title;// 企业所属行业
	public String scale_title;// 企业规模
	public String editor_note;//企业描述

	// 职位信息
	public List<Post> post;
	
	public List<Extension> extension;// 扩展

	public class Extension {
		public String key;
		public String value;
	}

	public class Post {
		public String pkid;// 职位ID
		public String post_title;// 职位名称
		public String region_name;// 职位所在地
		public String week_available;// 每周工作天数
		public String education;// 学历
		public String salary_range;// 日薪范围
		public String keep_on;// 是否留用
		
	}

}

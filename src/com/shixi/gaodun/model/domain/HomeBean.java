package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 首页的实体 author：ronger on 2016/1/15 13:12 email：dengyarong@gaodun.cn
 */
public class HomeBean implements Serializable {
	public String topic_id;// 帖子ID
	public String title;// 帖子标题
	public String official_posting;// 是否是官方
	public String banner;// 帖子banner

	public String pkid;// 主键ID
	public String full_name;// 企业名称
	public String logo;// 企业logo
	public String editor_note;// 企业寄语
	public String link_url;// 链接地址
	public String model_type;// 跳转对象模型 1:推荐活动,2:话题,3:企业详情 4：人才库
	public String model_id;// 对象模型pkid
	public String banner_path;// 人才库图片

}

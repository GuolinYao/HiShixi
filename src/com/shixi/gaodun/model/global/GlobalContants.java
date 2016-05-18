package com.shixi.gaodun.model.global;

/**
 * 全局参数声明 (定义几个服务器的地址)
 * 
 * @author gaodun
 * 
 */
public class GlobalContants {
	// API账户
	public static final String API_ACCOUNT_NUMER = "sxandroid";
	// API密码
	public static final String API_PASSWORD = "123456";
	// API账户与密码
	public static final String API_ACCOUNT_PASSWORD = "sxandroid123456";
	// pre&正式环境
	public static final String HOST_URL = "http://shixiapipre.gaodun.cn";
	// dev
//	 public static final String HOST_URL = "http://192.168.21.251:82";
	// 令牌：所有访问的入口
	public static final String POWER_URL = "/Auth/index";
	// 登陆
	public static final String LOGIN_URL = "/Account/studentLogin";
	// 注册
	public static final String REGISTER_URL = "/Account/studentRegist";
	// 注册时获取验证码
	public static final String REGISTER_GETCODE_URL = "/Account/sendSmsCode";
	// 找回密码
	public static final String FINDPSSWORD_URL = "/Account/retrievePassword";
	// 验证邮箱
	public static final String TESTEMAIL_URL = "/Account/verifyEmailStatus";
	// 获取验证信息
	public static final String GETTESTINFO_URL = "/Account/getSetInfo";
	// 意见反馈
	public static final String OPINIONFEEDBACK_URL = "/FeedBack/addInfo";
	// 修改密码
	public static final String UPDATEPWD_URL = "/Account/changePwd";
	// 验证手机号
	public static final String TESTPHONENUMBER_URL = "/Account/verifyMobileStatus";
	// 热门城市
	public static final String HOTCITY_URL = "/Regions/getHot";
	// 区域列表
	public static final String CITYLIST_URL = "/Regions/getList";
	// 专业分类列表
	public static final String MAJOR_CLASSIFY_URL = "/MajorType/getList";
	// 上传头像
	public static final String UPLOAD_URL = "/Student/uploadImage";
	// 更新基本信息
	public static final String UPDATE_BASIINFO_URL = "/Student/updateInfo";
	// 获取用户基本信息
	public static final String GET_USERBASICINFO_URL = "/Student/getInfo";
	// 获取实习意向
	public static final String GETINTERNSHIPINTENTION = "/PostSubscribe/getInfo";
	// 获取教育背景列表
	public static final String GETEDUCATIONALBGLIST = "/Education/getList";
	// 获取实习经历列表
	public static final String GETINTERNSHIPEXPERIENCELIST = "/Practice/getList";
	// 获取课外活动列表
	public static final String GETEXTRACURRICULARACTIVITIESLIST = "/ActivityExperience/getList";
	// 获取校内职务列表
	public static final String GETSCHOOLOFFICELIST = "/JobExperience/getList";
	// 获取证书列表
	public static final String GETCERTIFICATELIST = "/StudentCertificate/getList";
	// 获取荣誉列表
	public static final String GETHONORLIST = "/PrizeExperience/getList";
	// 编辑简历接口
	public static final String EDITRESUME = "/Resume/getInfoV2";

	// 获取职位列表
	public static final String GETPOSTLISTS = "/Post/getPostLists";
	// 新增教育背景
	public static final String ADDEDUCATIONBG_URL = "/Education/addInfo";
	// 修改教育背景
	public static final String UPDATEEDUCATIONBG_URL = "/Education/updateInfo";
	// 删除教育背景
	public static final String DELETEEDUCATIONBG_URL = "/Education/delInfo";
	// 新增实习经历
	public static final String ADDINTERSHIPEXPERIENCE_URL = "/Practice/addInfo";
	// 编辑实习经历
	public static final String EDITINTERSHIPEXPERIENCE_URL = "/Practice/updateInfo";
	// 删除实习经历
	public static final String DELETETINTERSHIPEXPERIENCE_URL = "/Practice/delInfo";
	// 获取写给HR的数据
	public static final String GETHTDATA_URL = "/Student/getDearHr";
	// 新增课外活动
	public static final String ADDEXTRACURRICULAR_URL = "/ActivityExperience/addInfo";
	// 编辑课外活动
	public static final String EDITEXTRACURRICULAR_URL = "/ActivityExperience/updateInfo";
	// 删除课外活动
	public static final String DELETEEXTRACURRICULAR_URL = "/ActivityExperience/delInfo";
	// 新增证书
	public static final String ADDCERTIFICATE_URL = "/StudentCertificate/addInfo";
	// 编辑证书
	public static final String EDITCERTIFICATE_URL = "/StudentCertificate/updateInfo";
	// 删除证书
	public static final String DELETECERTIFICATE_URL = "/StudentCertificate/delInfo";
	// 证书分类列表
	public static final String CERTIFICATE_LIST_URL = "/Certificate/getCategory";
	// 证书名称列表
	public static final String CERTIFICATENAME_LIST_URL = "/Certificate/getName";
	// 新增校内职务
	public static final String ADDSCHOOLOFFICE_URL = "/JobExperience/addInfo";
	// 编辑校内职务
	public static final String EDITSCHOOLOFFICE_URL = "/JobExperience/updateInfo";
	// 删除校内职务
	public static final String DELETESCHOOLOFFICE_URL = "/JobExperience/delInfo";
	// 新增荣誉
	public static final String ADDHONOR_URL = "/PrizeExperience/addInfo";
	// 编辑荣誉
	public static final String EDITHONOR_URL = "/PrizeExperience/updateInfo";
	// 删除荣誉
	public static final String DELETEHONOR_URL = "/PrizeExperience/delInfo";
	// 删除HR
	public static final String DELETEDEAR_HR_URL = "/Student/delDearHr";
	// 更新实习意向
	public static final String CHANGEINTERSHIPINTENTION_URL = "/PostSubscribe/updateInfo";
	// 更新基本信息包含HR
	public static final String CHANGEBAISICINFO_URL = "/Student/updateInfo";
	// 岗位分类列表
	public static final String CATEGORYLIST_URL = "/PostCategory/getList";
	// 获取简历信息
	public static final String GETRESUMEINFO_URL = "/Resume/getInfo";
	// 职位列表
	public static final String GETPOSITION_FILTERLIST_URL = "/Post/getList";
	// 热搜词汇
	public static final String GETHOTSEARCH_URL = "/HotSearch/getHot";
	// 获取关键词
	public static final String GETKEYWORD_URL = "/Post/getKeyList";
	// 获取职位详情
	public static final String GETPOSITIONDETAIL_URL = "/Post/getInfo";
	// 企业详情
	public static final String GETCOMPANYDETAIL_URL = "/Enterprise/getInfo";
	// 简历投递
	public static final String HANDINRESUME_URL = "/ResumePost/deliverResume";
	// 收藏职位
	public static final String COLLECTPOSITION_URL = "/PostCollect/addInfo";
	// 获取banner
	public static final String GTEBANNER_URL = "/Adver/getList";
	// 热门企业
	public static final String GETHOTCOMMPANY_URL = "/Enterprise/getHot";
	// 热门话题
	public static final String GETHOTTOPIC_URL = "/SnsTopic/getHot";
	// 取消收藏，清空收藏
	public static final String DELETECOLLECT_URL = "/PostCollect/delList";
	// 全部话题
	public static final String GETALLTOPIC_URL = "/SnsForum/getList";
	// 帖子列表
	public static final String INVITATION_URL = "/SnsTopic/getList";
	// 发布帖子
	public static final String POSTINVITATION_URL = "/SnsTopic/addInfo";
	// 帖子详情
	public static final String INVITATIONDETAIL_URL = "/SnsTopic/getInfo";
	// 赞帖子
	public static final String PRAISE_URL = "/SnsTopicFavor/addInfo";
	// 取消赞帖子
	public static final String CANCELPRAISE_URL = "/SnsTopicFavor/cancelInfo";
	// 收藏帖子
	public static final String COLLECTINVITATION_URL = "/SnsTopicCollect/addInfo";
	// 取消收藏帖子
	public static final String CANCELCOLLECTINVITATION_URL = "/SnsTopicCollect/delList";
	// 删除帖子
	public static final String DELETEINVITATION_URL = "/SnsTopic/delInfo";
	// 举报帖子
	public static final String REPORTINVITATION_URL = "/SnsTopicReport/addInfo";
	// 评论或回复详情
	public static final String COMMENTDETAIL_URL = "/SnsTopicReply/getComment";
	// 评论楼层帖和回复评论
	public static final String COMMENTFLOOR_URL = "/SnsTopicReply/addComment";
	// 删除评论和回复
	public static final String DELETECOMMENT_URL = "/SnsTopicReply/delInfo";
	// 我的帖子和我的回复
	public static final String MYINVITATION_REPLY_URL = "/SnsTopic/getMyList";
	// 话题消息列表
	public static final String TOPICMESSAGE_URL = "/SnsTopicMessage/getList";
	// 系统消息列表
	// public static final String SYSTEM_URL = "/Message/getList";
	public static final String SYSTEM_URL = "/MessageQueue/getList";
	// 删除系统消息
	// public static final String DELTE_SYSTEM_URL = "/Message/delList";
	public static final String DELTE_SYSTEM_URL = "/MessageQueue/delList";
	// 删除消息
	public static final String DELETE_MESSAGE_URL = "/SnsTopicMessage/delList";
	// 简历动态列表
	public static final String RESUMEBYNAMIC_LIST_URL = "/ResumePost/getList";
	// 浏览记录
	public static final String BROWSELIST_URL = "/PostView/getList";
	// 清空浏览记录
	public static final String DELETE_BROWSELIST_URL = "/PostView/clearList";
	// 收藏的职位列表
	public static final String COLLECT_POSITIONLIST_URL = "/PostCollect/getList";
	// 收藏的帖子列表
	public static final String COLLECT_INVITATIONLIST_URL = "/SnsTopicCollect/getList";
	// 取消收藏的职位含清空
	public static final String CANCEL_COLLECTPOSITION_URL = "/PostCollect/delList";
	// 删除收藏帖子含清空
	public static final String CANCEL_COLLECTINVITATION_URL = "/SnsTopicCollect/delList";
	// 获取二级城市
	public static final String GETCITY_LIST_URL = "/Regions/getCityList";
	// 首页banner每周推荐
	public static final String WEEK_RECOMMEND_URL = "/RecommendActivity/getList";
	// 首页接口整合
	public static final String HOMEPAGE_URL = "/Leader/getList";
	// 猎头任务列表
	public static final String HUNTER_MISSION = "/SchoolHunterTask/getList";
	// 猎头紧急任务
	public static final String URGENCY_MISSION = "/SchoolHunterTask/getUrgencyList";
	// 猎头总kpi
	public static final String HUNTER_TOTAL_KPI = "/SchoolHunterKpi/getInfo";
	// 猎头已收集简历列表
	public static final String HUNTER_LIST_RESUME = "/SchoolHunter/getList";
	public static final String HUNTER_LIST_RESUMEList = "/SchoolHunter/getResumeList";
	// 猎头已收集简历列表
	public static final String HUNTER_RULE = "/SchoolHunterTask/getUrl";
	// 获取用户人才库状态
	public static final String TALENT_STATUS = "/Student/getTalentStatus";
	// 上传人才库信息
	public static final String UPDATE_TALENT_BANK = "/Student/addTalentInfo";
	// 获取人才库信息
	public static final String GET_TALENT_BANK = "/Student/getTalentInfo";
	// 获取引导页信息
	public static final String GET_GUIDE_INFO = "/Student/getGuideInfo";
}

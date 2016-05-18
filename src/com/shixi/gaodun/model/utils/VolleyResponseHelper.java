package com.shixi.gaodun.model.utils;

import android.content.Context;

/**
 * 接口返回错误码处理
 * 
 * @author ronger
 * @date:2015-10-29 上午8:57:06
 */
public class VolleyResponseHelper {
	public static String getMessage(int status, Context context) {
		if (status == 1001) {
			return "字数超过限制";
		}
		if (status == 2000) {
			return "证书已存在";
		}
		if (status == 2001) {
			return "手机号码已存在";
		}
		if (status == 2002) {
			return "用户账号已存在";
		}
		if (status == 2004) {
			return "职位已经收藏过";
		}
		if (status == 401) {
			return "接口必填数据未填写完整";
		}
		if (status == 402) {
			return "非法参数类型";
		}
		if (status == 30001) {
			return "页面不存在";
		}

		if (status == 30002) {
			return "不匹配，请提供正确的秀id";
		}
		if (status == 40001) {
			return "数据库异常";
		}
		if (status == 40002) {
			return "请求错误//net 返回的不祥信息";
		}
		if (status == 40003) {
			return "用户不存在";
		}
		if (status == 40004) {
			return "已存在门店名称";
		}
		if (status == 40005) {
			return "发布推广开始时间须小于结束时间，并开始时间小于等于当天时间";
		}
		if (status == 40006) {
			return "发布推广sku信息错误 ";
		}

		if (status == 50001) {
			return "发送邮件或短信等失败";
		}
		if (status == 50002) {
			return "图片上传错误 ";
		}
		if (status == 70000) {
			return "数据不存在 ";
		}
		if (status == 40007) {
			return "快递单号格式错误（8-15位字母、数字）";
		}
		return "其他错误";
	}

}

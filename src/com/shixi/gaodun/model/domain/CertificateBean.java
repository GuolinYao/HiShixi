package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 证书实体
 * 
 * @author ronger
 * @date:2015-11-4 下午3:36:37
 */
public class CertificateBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String pkid;
	private String finish_time;// 结束时间
	private String certificate_id;// 证书ID
	private String status;// 证书状态1:在读,2:通过
	private String certificate_name;// 证书名称
	private MapFormatBean certificate_type;// 证书分类

	public String getPkid() {
		return pkid;
	}

	public void setPkid(String pkid) {
		this.pkid = pkid;
	}

	public String getFinish_time() {
		finish_time = finish_time.replace("-", ".");
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		finish_time = finish_time.replace("-", ".");
		this.finish_time = finish_time;
	}

	public String getCertificate_id() {
		return certificate_id;
	}

	public void setCertificate_id(String certificate_id) {
		this.certificate_id = certificate_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCertificate_name() {
		return certificate_name;
	}

	public void setCertificate_name(String certificate_name) {
		this.certificate_name = certificate_name;
	}

	public MapFormatBean getCertificate_type() {
		return certificate_type;
	}

	public void setCertificate_type(MapFormatBean certificate_type) {
		this.certificate_type = certificate_type;
	}

}

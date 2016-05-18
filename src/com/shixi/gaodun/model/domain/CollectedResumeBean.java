package com.shixi.gaodun.model.domain;

import java.io.Serializable;

/**
 * 已收集简历信息
 *
 * @author guolinyao
 * @date 2016年3月3日 下午2:55:28
 */
public class CollectedResumeBean implements Serializable {


    private String pkid;
    private String name;//学生姓名
    private String code;//学生编号
    private String graduate_school;//毕业学校
    private String create_time;//投递时间
    private String enterprise_name;//企业名
    private String title;//职位名
    private String post_id;//职位id
    private String enterprise_id;//企业id
    private String detail_major;//专业

    public String getDetail_major() {
        return detail_major;
    }

    public void setDetail_major(String detail_major) {
        this.detail_major = detail_major;
    }

    public String getPkid() {
        return pkid;
    }

    public void setPkid(String pkid) {
        this.pkid = pkid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGraduate_school() {
        return graduate_school;
    }

    public void setGraduate_school(String graduate_school) {
        this.graduate_school = graduate_school;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(String enterprise_id) {
        this.enterprise_id = enterprise_id;
    }


}

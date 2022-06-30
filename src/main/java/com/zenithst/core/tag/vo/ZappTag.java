package com.zenithst.core.tag.vo;

import com.zenithst.core.common.extend.ZappDomain;

public class ZappTag extends ZappDomain {

    private String tagid;
    private String companyid;
    private String name;
    private String code;
    private Integer priority;
    private String isactive;

    public ZappTag() {}
    public ZappTag(String tagid) {
    	this.tagid = tagid;
    }
    public ZappTag(String companyid, String isactive) {
    	this.companyid = companyid;
    	this.isactive = isactive;
    }    
    
    public String getTagid() {
        return tagid;
    }
    public void setTagid(String tagid) {
        this.tagid = tagid == null ? null : tagid.trim();
    }
    public String getCompanyid() {
        return companyid;
    }
    public void setCompanyid(String companyid) {
        this.companyid = companyid == null ? null : companyid.trim();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }
    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    public String getIsactive() {
        return isactive;
    }
    public void setIsactive(String isactive) {
        this.isactive = isactive == null ? null : isactive.trim();
    }
}
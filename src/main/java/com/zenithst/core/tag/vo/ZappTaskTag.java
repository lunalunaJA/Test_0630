package com.zenithst.core.tag.vo;

import com.zenithst.core.common.extend.ZappDomain;

public class ZappTaskTag extends ZappDomain {

    private String tasktagid;
    private String taskid;
    private String tagid;
    private String isallowednull;
    private String iscreatedindex;
    private String isunique;
    private String defaultvalue;
    private String issearchable;
    private String datatype;
    private String querytype;
    private String seqno;
    private Integer datalength;
    private String includepk;
    private String isactive;
    
    public ZappTaskTag() {}
    public ZappTaskTag(String tasktagid) {
    	this.tasktagid = tasktagid;
    }
    public ZappTaskTag(String taskid, String tagid) {
    	this.taskid = taskid;
    	this.tagid = tagid;
    }    

    public String getTasktagid() {
        return tasktagid;
    }
    public void setTasktagid(String tasktagid) {
        this.tasktagid = tasktagid == null ? null : tasktagid.trim();
    }
    public String getTaskid() {
        return taskid;
    }
    public void setTaskid(String taskid) {
        this.taskid = taskid == null ? null : taskid.trim();
    }
    public String getTagid() {
        return tagid;
    }
    public void setTagid(String tagid) {
        this.tagid = tagid == null ? null : tagid.trim();
    }
    public String getIsallowednull() {
        return isallowednull;
    }
    public void setIsallowednull(String isallowednull) {
        this.isallowednull = isallowednull == null ? null : isallowednull.trim();
    }
    public String getIscreatedindex() {
        return iscreatedindex;
    }
    public void setIscreatedindex(String iscreatedindex) {
        this.iscreatedindex = iscreatedindex == null ? null : iscreatedindex.trim();
    }
    public String getIsunique() {
        return isunique;
    }
    public void setIsunique(String isunique) {
        this.isunique = isunique == null ? null : isunique.trim();
    }
    public String getDefaultvalue() {
        return defaultvalue;
    }
    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue == null ? null : defaultvalue.trim();
    }
    public String getIssearchable() {
        return issearchable;
    }
    public void setIssearchable(String issearchable) {
        this.issearchable = issearchable == null ? null : issearchable.trim();
    }
    public String getDatatype() {
        return datatype;
    }
    public void setDatatype(String datatype) {
        this.datatype = datatype == null ? null : datatype.trim();
    }
    public String getQuerytype() {
        return querytype;
    }
    public void setQuerytype(String querytype) {
        this.querytype = querytype == null ? null : querytype.trim();
    }
    public String getSeqno() {
        return seqno;
    }
    public void setSeqno(String seqno) {
        this.seqno = seqno == null ? null : seqno.trim();
    }
    public Integer getDatalength() {
        return datalength;
    }
    public void setDatalength(Integer datalength) {
        this.datalength = datalength;
    }
    public String getIncludepk() {
        return includepk;
    }
    public void setIncludepk(String includepk) {
        this.includepk = includepk == null ? null : includepk.trim();
    }
    public String getIsactive() {
        return isactive;
    }
    public void setIsactive(String isactive) {
        this.isactive = isactive == null ? null : isactive.trim();
    }
}
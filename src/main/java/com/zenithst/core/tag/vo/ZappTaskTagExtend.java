package com.zenithst.core.tag.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.archive.vo.ZArchTask;

public class ZappTaskTagExtend extends ZappTaskTag {

    private ZappTag zappTag;
    private ZArchTask zappTask;
    
	public ZappTag getZappTag() {
		return zappTag;
	}
	public void setZappTag(ZappTag zappTag) {
		this.zappTag = zappTag;
	}
	public ZArchTask getZappTask() {
		return zappTask;
	}
	public void setZappTask(ZArchTask zappTask) {
		this.zappTask = zappTask;
	}
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objComments;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objTblname;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objIdxname;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objFieldname;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objType;

	public String getObjComments() {
		return objComments;
	}
	public void setObjComments(String objComments) {
		this.objComments = objComments;
	}	
	public String getObjTblname() {
		return objTblname;
	}
	public void setObjTblname(String objTblname) {
		this.objTblname = objTblname;
	}
	public String getObjIdxname() {
		return objIdxname;
	}
	public void setObjIdxname(String objIdxname) {
		this.objIdxname = objIdxname;
	}
	public String getObjFieldname() {
		return objFieldname;
	}
	public void setObjFieldname(String objFieldname) {
		this.objFieldname = objFieldname;
	}
	public String getObjType() {
		return objType;
	}
	public void setObjType(String objType) {
		this.objType = objType;
	}
	
	
	
}
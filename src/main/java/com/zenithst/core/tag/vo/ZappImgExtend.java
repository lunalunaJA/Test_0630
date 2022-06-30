package com.zenithst.core.tag.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ZappImgExtend extends ZappImg {

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objTaskid;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objTaskcode;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objType;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objSchema;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objTblname;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objIdxname;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objFieldname;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objIsunique;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<ZappTaskTagExtend> objTags = new ArrayList<ZappTaskTagExtend>();
	
	public String getObjTaskid() {
		return objTaskid;
	}
	public void setObjTaskid(String objTaskid) {
		this.objTaskid = objTaskid;
	}
	public String getObjTaskcode() {
		return objTaskcode;
	}
	public void setObjTaskcode(String objTaskcode) {
		this.objTaskcode = objTaskcode;
	}
	public String getObjType() {
		return objType;
	}
	public void setObjType(String objType) {
		this.objType = objType;
	}
	public String getObjSchema() {
		return objSchema;
	}
	public void setObjSchema(String objSchema) {
		this.objSchema = objSchema;
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
	public String getObjIsunique() {
		return objIsunique;
	}
	public void setObjIsunique(String objIsunique) {
		this.objIsunique = objIsunique;
	}
	public List<ZappTaskTagExtend> getObjTags() {
		return objTags;
	}
	public void setObjTags(List<ZappTaskTagExtend> objTags) {
		this.objTags = objTags;
	}
	
 
}
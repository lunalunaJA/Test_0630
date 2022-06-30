package com.zenithst.core.classification.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for classification <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappClassification pIn = new ZappClassification();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CLASS</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>classid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>User : HASH(2+5+8)<br>Others : HASH(2+5+6+7)</td><td>Classification ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>companyid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Company ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>name</td><td></td><td></td><td></td><td>VARCHAR(150)</td><td></td><td>Name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>descpt</td><td></td><td></td><td>●</td><td>VARCHAR(500)</td><td></td><td>Desc.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>code</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Classification code</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>upid</td><td></td><td></td><td>●</td><td>VARCHAR(500)</td><td></td><td>Upper ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>holderid</td><td></td><td></td><td></td><td>VARCHAR(30)</td><td></td><td>Holder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>types</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Classification type<br>(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>priority</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Sorting order(Based on upper ID)<br></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>wfrequired</td><td></td><td></td><td></td><td>INTEGER</td><td></td><td>Workflow is required? (Y/N)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>11</td><td>wfid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Workflow ID (Group ID)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>12</td><td>affiliationid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Affiliation ID (Dept. or Group ID)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>13</td><td>retentionid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Retention ID </td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>14</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Use or not(Y/N)</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappClassification extends ZappDomain {

	/* */
    private String classid;
    private String companyid;
    private String code;
    private String name;
    private String descpt;
    private String upid;
    private String holderid;
    private String types;
    private int priority;
    private int wfrequired;
    private String wfid;
    private String affiliationid;
    private String retentionid;
    private String cpath;
    private String isactive;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappClassification() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param classid Classification ID
     */ 
    public ZappClassification(String classid) {
    	this.classid = classid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param companyid Company ID
     * @param code Classification code
     */
    public ZappClassification(String companyid, String code) {
    	this.companyid = companyid;
    	this.code = code;
    }
    /**
	 * <p><b>
	 * Additional constructor #3
	 * </b></p>
     *
     * @param classid Classification ID
     * @param priority Sorting order
     */
    public ZappClassification(String classid, Integer priority) {
    	this.classid = classid;
    	this.priority = priority;
    }
    
    /**
	 * <p><b>
	 * [OUT] Classification ID - Primary Key
	 * </b></p>
	 * 
     * @return Classification ID
     */    
	public String getClassid() {
		return classid;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification ID - Primary Key
	 * </b></p>
     *
     * @param classid Classification ID
     */  
	public void setClassid(String classid) {
		this.classid = classid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Company ID
	 * </b></p>
	 * 
     * @return Company ID
     */  
	public String getCompanyid() {
		return companyid;
	}
    
    /**
	 * <p><b>
	 * [IN] Company ID
	 * </b></p>
     *
     * @param name Company ID
     */  
	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Classification code
	 * </b></p>
	 * 
     * @return Classification code
     */  
	public String getCode() {
		return code;
	}
    
    /**
	 * <p><b>
	 * [IN] Name
	 * </b></p>
     *
     * @param name Name
     */  
	public void setCode(String code) {
		this.code = code;
	}
    
    /**
	 * <p><b>
	 * [OUT] Name
	 * </b></p>
	 * 
     * @return Name
     */  
	public String getName() {
		return name;
	}
    
    /**
	 * <p><b>
	 * [IN] Name
	 * </b></p>
     *
     * @param name Name
     */  
	public void setName(String name) {
		this.name = name;
	}
    
    /**
	 * <p><b>
	 * [OUT] Desc.
	 * </b></p>
	 * 
     * @return Desc.
     */ 
    public String getDescpt() {
		return descpt;
	}
    
    /**
	 * <p><b>
	 * [IN] Desc.
	 * </b></p>
     *
     * @param descpt Desc.
     */ 
	public void setDescpt(String descpt) {
		this.descpt = descpt;
	}
	/**
	 * <p><b>
	 * [OUT] Upper ID
	 * </b></p>
	 * 
     * @return Upper ID
     */  
	public String getUpid() {
		return upid;
	}
    
    /**
	 * <p><b>
	 * [IN] Upper ID
	 * </b></p>
     *
     * @param upid Upper ID
     */  
	public void setUpid(String upid) {
		this.upid = upid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Holder ID
	 * </b></p>
	 * 
     * @return Holder ID
     */  
	public String getHolderid() {
		return holderid;
	}
    
    /**
	 * <p><b>
	 * [IN] Holder ID
	 * </b></p>
     *
     * @param holderid Holder ID
     */  
	public void setHolderid(String holderid) {
		this.holderid = holderid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Classification type
	 * </b></p>
	 * 
     * @return Classification type
     */  
	public String getTypes() {
		return types;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification type
	 * </b></p>
     *
     * @param types Classification type
     */  
	public void setTypes(String types) {
		this.types = types;
	}
    
    /**
	 * <p><b>
	 * [OUT] Sorting order
	 * </b></p>
	 * 
     * @return Sorting order
     */  
	public int getPriority() {
		return priority;
	}
    
    /**
	 * <p><b>
	 * [IN] Sorting order
	 * </b></p>
     *
     * @param priority Sorting order
     */  
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * <p><b>
	 * [OUT] Workflow is required? (Y/N)
	 * </b></p>
	 * 
     * @return Workflow is required?
     */  
    public int getWfrequired() {
		return wfrequired;
	}

    /**
	 * <p><b>
	 * [IN] Workflow is required? (Y/N)
	 * </b></p>
     *
     * @param wfrequired Workflow is required?
     */  
	public void setWfrequired(int wfrequired) {
		this.wfrequired = wfrequired;
	}

	/**
	 * <p><b>
	 * [OUT] Workflow ID (Group ID)
	 * </b></p>
	 * 
     * @return Workflow ID
     */  
	public String getWfid() {
		return wfid;
	}

	/**
	 * <p><b>
	 * [IN] Workflow ID (Group ID)
	 * </b></p>
     *
     * @param wfid Workflow ID
     */  
	public void setWfid(String wfid) {
		this.wfid = wfid;
	}

	/**
	 * <p><b>
	 * [OUT] Affiliation ID
	 * </b></p>
	 * 
     * @return Affiliation ID
     */	
	public String getAffiliationid() {
		return affiliationid;
	}
    
    /**
	 * <p><b>
	 * [IN] Affiliation ID
	 * </b></p>
     *
     * @param affiliationid Affiliation ID
     */ 	
	public void setAffiliationid(String affiliationid) {
		this.affiliationid = affiliationid;
	}

	/**
	 * <p><b>
	 * [OUT] Retention ID
	 * </b></p>
	 * 
     * @return Retention ID
     */  
	public String getRetentionid() {
		return retentionid;
	}

	/**
	 * <p><b>
	 * [IN] Retention ID 
	 * </b></p>
     *
     * @param retentionid Retention ID
     */  
	public void setRetentionid(String retentionid) {
		this.retentionid = retentionid;
	}
	
	/**
	 * <p><b>
	 * [OUT] Class Path
	 * </b></p>
	 * 
     * @return Class Path
     */  
	public String getCpath() {
		return cpath;
	}

	/**
	 * <p><b>
	 * [IN] Class Path 
	 * </b></p>
     *
     * @param cpath Class Path
     */  
	public void setCpath(String cpath) {
		this.cpath = cpath;
	}

	/**
	 * <p><b>
	 * [OUT] Use or not(Y/N)
	 * </b></p>
	 * 
     * @return Use or not
     */  
	public String getIsactive() {
		return isactive;
	}
    
    /**
	 * <p><b>
	 * [IN] Use or not
	 * </b></p>
     *
     * @param isactive Use or not
     */  
	public void setIsactive(String isactive) {
		this.isactive = isactive;
	}
  
    
}
package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for marking <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappMarkedObject pIn = new ZappMarkedObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CLASSOBJECT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>markedobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>mobjid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Target ID (Content ID, Classification ID)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>mobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Target type<br>(00:Classification, 01:Bundle, 02:File)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>markerid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Marker ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>marktime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Mark time</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappMarkedObject extends ZappDomain {

    private String markedobjid;
    private String mobjid;
    private String mobjtype;
    private String markerid;
    private String marktime;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappMarkedObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param markedobjid pk
     */ 
    public ZappMarkedObject(String markedobjid) {
    	this.markedobjid = markedobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param mobjid Target ID
     * @param mobjtype Target type
     * @param markerid Marker ID
     * @param marktime Mark time
     */
    public ZappMarkedObject(String mobjid, String mobjtype, String markerid, String marktime) {
    	this.mobjid = mobjid;
    	this.mobjtype = mobjtype;
    	this.markerid = markerid;
    	this.marktime = marktime;
    }
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getMarkedobjid() {
		return markedobjid;
	}

	/**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param linkedobjid PK
     */ 
	public void setMarkedobjid(String markedobjid) {
		this.markedobjid = markedobjid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target ID
	 * </b></p>
	 * 
     * @return Target ID
     */
    public String getMobjid() {
		return mobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Target ID
	 * </b></p>
     *
     * @param mobjid Target ID
     */ 
	public void setMobjid(String mobjid) {
		this.mobjid = mobjid;
	}

	/**
	 * <p><b>
	 * [OUT] Target type
	 * </b></p>
	 * 
     * @return Target type
     */
    public String getMobjtype() {
		return mobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type
	 * </b></p>
     *
     * @param mobjtype Target type
     */ 
	public void setMobjtype(String mobjtype) {
		this.mobjtype = mobjtype;
	}

	/**
	 * <p><b>
	 * [OUT] Marker ID
	 * </b></p>
	 * 
     * @return Marker ID
     */
    public String getMarkerid() {
		return markerid;
	}
    
    /**
	 * <p><b>
	 * [IN] Marker ID
	 * </b></p>
     *
     * @param markerid Marker ID
     */ 
	public void setMarkerid(String markerid) {
		this.markerid = markerid;
	}

	/**
	 * <p><b>
	 * [OUT] Mark time
	 * </b></p>
	 * 
     * @return Mark time
     */
    public String getMarktime() {
		return marktime;
	}
    
    /**
	 * <p><b>
	 * [IN] Mark time
	 * </b></p>
     *
     * @param marktime Mark time
     */ 
	public void setMarktime(String marktime) {
		this.marktime = marktime;
	}

	
}
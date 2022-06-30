package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content-workflow <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappContentWorkflow pIn = new ZappContentWorkflow();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_KEYWORDS</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>cwfid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>contentid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Content ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>contenttype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Content Type</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>drafterid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Drafter ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>draftername</td><td></td><td></td><td></td><td>VARCHAR(100)</td><td></td><td>Drafter Name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>wferid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Workflower ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>wfername</td><td></td><td></td><td></td><td>VARCHAR(100)</td><td></td><td>Workflower Name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>wftime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Workflow Time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>comments</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td><td>Comments</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>status</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Status</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappContentWorkflow extends ZappDomain {

    private String cwfid;
    private String contentid;
    private String contenttype;
    private String drafterid;
    private String draftername;
    private String wferid;
    private String wfername;
    private String wftime;
    private String comments;
    private String status;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappContentWorkflow() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param cwfid pk
     */ 
    public ZappContentWorkflow(String cwfid) {
    	this.cwfid = cwfid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param contentid Keyword
     * @param contenttype Use or not
     */ 
    public ZappContentWorkflow(String contentid, String contenttype) {
    	this.contentid = contentid;
    	this.contenttype = contenttype;
    }
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
	public String getCwfid() {
		return cwfid;
	}
	
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param cwfid PK
     */ 
	public void setCwfid(String cwfid) {
		this.cwfid = cwfid;
	}
	public String getContentid() {
		return contentid;
	}
	public void setContentid(String contentid) {
		this.contentid = contentid;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	public String getDrafterid() {
		return drafterid;
	}
	public void setDrafterid(String drafterid) {
		this.drafterid = drafterid;
	}
	public String getDraftername() {
		return draftername;
	}
	public void setDraftername(String draftername) {
		this.draftername = draftername;
	}
	public String getWferid() {
		return wferid;
	}
	public void setWferid(String wferid) {
		this.wferid = wferid;
	}
	public String getWfername() {
		return wfername;
	}
	public void setWfername(String wfername) {
		this.wfername = wfername;
	}
	public String getWftime() {
		return wftime;
	}
	public void setWftime(String wftime) {
		this.wftime = wftime;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
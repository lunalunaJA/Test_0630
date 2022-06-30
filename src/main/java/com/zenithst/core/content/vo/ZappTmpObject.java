package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for temporary info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappTmpObject pIn = new ZappTmpObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_TMPOBJECT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>tmpobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>tobjid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Temp. Object ID (Bundle ID, File ID, ...)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>tobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Temp. Object Type</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>title</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td><td>Title</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>holderid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Holder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>retentionid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Retention ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>expiretime</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Expire Time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>tmptime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Temp. Time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>handlerid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Handler ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>acls</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td><td>Access Control Info.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>11</td><td>classes</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td><td>Classification Info.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>12</td><td>keywords</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td><td>Keyword Info.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>13</td><td>files</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>File Info.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>14</td><td>addinfo</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Additional Info.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>15</td><td>taskid</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td><td>Task ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>16</td><td>states</td><td></td><td></td><td></td><td>VARCHAR(10)</td><td></td><td>State Info.</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
* 
*/

public class ZappTmpObject extends ZappDomain {

    private String tmpobjid;
    private String tobjid;
    private String tobjtype;
    private String title;
    private String holderid;
    private String retentionid;
    private String expiretime;
    private String tmptime;
    private String handlerid;
    private String acls;		/* json - Access control */
    private String classes;		/* json - Classification */
    private String keywords;	/* json - Keyword */
    private String files;		/* json - File */
    private String states;		/* json - State */
    private String addinfo;		/* json - Additional Info. */
    private String taskid;
    
    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappTmpObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param tmpobjid PK
     */ 
    public ZappTmpObject(String tmpobjid) {
    	this.tmpobjid = tmpobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param tobjid Temp. Object ID
     * @param tobjtype Temp. Object Type
     */     
    public ZappTmpObject(String tobjid, String tobjtype) {
    	this.tobjid = tobjid;
    	this.tobjtype = tobjtype;
    }

    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getTmpobjid() {
        return tmpobjid;
    }
    
    /**
	 * <p><b>
	 * [IN] PK
	 * </b></p>
     *
     * @param tmpobjid PK
     */    
    public void setTmpobjid(String tmpobjid) {
        this.tmpobjid = tmpobjid == null ? null : tmpobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Temp. Object ID
	 * </b></p>
	 * 
     * @return Temp. Object ID
     */
    public String getTobjid() {
        return tobjid;
    }
    
    /**
	 * <p><b>
	 * [IN] Temp. Object ID
	 * </b></p>
     *
     * @param tobjid Temp. Object ID
     */    
    public void setTobjid(String tobjid) {
        this.tobjid = tobjid == null ? null : tobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Temp. Object Type
	 * </b></p>
	 * 
     * @return Temp. Object Type
     */
    public String getTobjtype() {
        return tobjtype;
    }
    
    /**
	 * <p><b>
	 * [IN] Temp. Object Type
	 * </b></p>
     *
     * @param objtype Temp. Object Type
     */ 
    public void setTobjtype(String objtype) {
        this.tobjtype = objtype == null ? null : objtype.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Title
	 * </b></p>
	 * 
     * @return Title
     */    
    public String getTitle() {
		return title;
	}
    
    /**
	 * <p><b>
	 * [IN] Title
	 * </b></p>
     *
     * @param title Title
     */     
	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
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
        this.holderid = holderid == null ? null : holderid.trim();
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
        this.retentionid = retentionid == null ? null : retentionid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Expire Time
	 * </b></p>
	 * 
     * @return Expire Time
     */     
    public String getExpiretime() {
        return expiretime;
    }
    
    /**
	 * <p><b>
	 * [IN] Expire Time
	 * </b></p>
     *
     * @param expiretime Expire Time
     */    
    public void setExpiretime(String expiretime) {
        this.expiretime = expiretime == null ? null : expiretime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Temp. Time
	 * </b></p>
	 * 
     * @return Temp. Time
     */     
    public String getTmptime() {
        return tmptime;
    }
    
    /**
	 * <p><b>
	 * [IN] Temp. Time
	 * </b></p>
     *
     * @param tmptime Temp. Time
     */       
    public void setTmptime(String tmptime) {
        this.tmptime = tmptime == null ? null : tmptime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Handler ID
	 * </b></p>
	 * 
     * @return Handler ID
     */    
	public String getHandlerid() {
		return handlerid;
	}
    
    /**
	 * <p><b>
	 * [IN] Handler ID
	 * </b></p>
     *
     * @param handlerid Handler ID
     */  	
	public void setHandlerid(String handlerid) {
	   this.handlerid = handlerid == null ? null : handlerid.trim();
	}
    
    /**
	 * <p><b>
	 * [OUT] ACL
	 * </b></p>
	 * 
     * @return ACL
     */   	
	public String getAcls() {
		return acls;
	}
    
    /**
	 * <p><b>
	 * [IN] ACL
	 * </b></p>
     *
     * @param acls ACL
     */  	
	public void setAcls(String acls) {
		this.acls = acls == null ? null : acls.trim();
	}
    
    /**
	 * <p><b>
	 * [OUT] Classification
	 * </b></p>
	 * 
     * @return Classification
     */  	
	public String getClasses() {
		return classes;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification
	 * </b></p>
     *
     * @param classes Classification
     */ 	
	public void setClasses(String classes) {
		this.classes = classes == null ? null : classes.trim();
	}
    
    /**
	 * <p><b>
	 * [OUT] Keyword
	 * </b></p>
	 * 
     * @return Keyword
     */  	
	public String getKeywords() {
		return keywords;
	}
    
    /**
	 * <p><b>
	 * [IN] Keyword
	 * </b></p>
     *
     * @param keywords Keyword
     */ 	
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
    
    /**
	 * <p><b>
	 * [OUT] File
	 * </b></p>
	 * 
     * @return File
     */ 	
	public String getFiles() {
		return files;
	}
    
    /**
	 * <p><b>
	 * [IN] File
	 * </b></p>
     *
     * @param files File
     */ 	
	public void setFiles(String files) {
		this.files = files == null ? null : files.trim();
	}
    
    /**
	 * <p><b>
	 * [OUT] State
	 * </b></p>
	 * 
     * @return State
     */ 	
	public String getStates() {
		return states;
	}
    
    /**
	 * <p><b>
	 * [IN] State
	 * </b></p>
     *
     * @param states State
     */	
	public void setStates(String states) {
		this.states = states;
	}
    
    /**
	 * <p><b>
	 * [OUT] Additional Info.
	 * </b></p>
	 * 
     * @return Additional Info.
     */ 	
	public String getAddinfo() {
		return addinfo;
	}
    
    /**
	 * <p><b>
	 * [IN] Additional Info.
	 * </b></p>
     *
     * @param addinfo Additional Info.
     */		
	public void setAddinfo(String addinfo) {
		this.addinfo = addinfo;
	}
    
    /**
	 * <p><b>
	 * [OUT] Task ID
	 * </b></p>
	 * 
     * @return Task ID
     */ 	
	public String getTaskid() {
		return taskid;
	}
    
    /**
	 * <p><b>
	 * [IN] Task ID
	 * </b></p>
     *
     * @param taskid Task ID
     */	
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
    
}
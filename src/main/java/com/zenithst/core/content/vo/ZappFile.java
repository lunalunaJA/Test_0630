package com.zenithst.core.content.vo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.archive.vo.ZArchExtend;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for file <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappFile pIn = new ZappFile();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_MFILE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>mfileid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>ZARCH_MFILE - mfileid</td><td>File ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>fno</td><td></td><td></td><td>●</td><td>VARCHAR(50)</td><td></td><td>File no.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>creatorname</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Creator name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>holderid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Holder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>discarderid</td><td></td><td></td><td>●</td><td>VARCHAR(64)</td><td></td><td>Discarder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>retentionid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Retention period ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>expiretime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Expire date</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>ext</td><td></td><td></td><td></td><td>VARCHAR(30)</td><td></td><td>File extension</td>
* 	 </tr>
* 	 </table>
** 	 <table width="80%" border="1">
*    <caption>ZARCH_MFILE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>mfileid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3)</td><td>File ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>linkid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Link ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>filename</td><td></td><td></td><td></td><td>TEXT</td><td></td><td>File name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>seq</td><td></td><td></td><td></td><td>SMALLINT</td><td></td><td>순서</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>creator</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Creator ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>createtime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Create time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>updatetime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Update time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>state</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>State</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappFile extends ZArchExtend {

	private String mfileid;						// Master file ID
    private String filename;					// File name
    private String linkid;						// Link ID
    private BigDecimal seq = BigDecimal.ZERO;	// Sorting order
    private String creator;						// Creator
    private String createtime;					// Create time
    private String updatetime;					// Update time
    private String expiredate;					// Expiretime
    private String state;						// State
    private String dynamic01;
    private String dynamic02;
    private String dynamic03;
    private int seclevel;						// Security Level
	
    private String fno;
    private String creatorname;					// Creator name
    private String retentionid;
    private String expiretime;
    private String holderid;
    private String discarderid;
    private String ext;
    private String workflowid;
	private String action;
    private String drafter;
    private String summary;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String versionid;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private boolean isreleased = true;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String maxhashid;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objCaller;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
	public ZappFile() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param mfileid File ID
     */ 
	public ZappFile(String mfileid) {
		this.mfileid = mfileid;
	}
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param mfileid File ID
     * @param state State
     */ 
	public ZappFile(String mfileid, String state) {
		this.mfileid = mfileid;
		this.state = state;
	}
    
    /**
	 * <p><b>
	 * [OUT] File ID - Primary Key
	 * </b></p>
	 * 
     * @return File ID
     */
	public String getMfileid() {
		return mfileid;
	}
    
    /**
	 * <p><b>
	 * [IN] File ID - Primary Key
	 * </b></p>
     *
     * @param mfileid File ID
     */
	public void setMfileid(String mfileid) {
		this.mfileid = mfileid;
	}
    
    /**
	 * <p><b>
	 * [OUT] File name
	 * </b></p>
	 * 
     * @return File name
     */ 
	public String getFilename() {
		return filename;
	}
    
    /**
	 * <p><b>
	 * [IN] File name
	 * </b></p>
     *
     * @param filename File name
     */  
	public void setFilename(String filename) {
		this.filename = filename;
	}
    
    /**
	 * <p><b>
	 * [OUT] Link ID
	 * </b></p>
	 * 
     * @return Link ID
     */ 
	public String getLinkid() {
		return linkid;
	}
    
    /**
	 * <p><b>
	 * [IN] Link ID
	 * </b></p>
     *
     * @param linkid Link ID
     */  
	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Serial number
	 * </b></p>
	 * 
     * @return Serial number
     */ 
	public BigDecimal getSeq() {
		return seq;
	}
    
    /**
	 * <p><b>
	 * [IN] Serial number
	 * </b></p>
     *
     * @param seq Serial number
     */ 
	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}
    
    /**
	 * <p><b>
	 * [OUT] Creater ID
	 * </b></p>
	 * 
     * @return Creater ID
     */ 
	public String getCreator() {
		return creator;
	}
    
    /**
	 * <p><b>
	 * [IN] Creater ID
	 * </b></p>
     *
     * @param creator Creater ID
     */ 
	public void setCreator(String creator) {
		this.creator = creator;
	}    
	
    /**
	 * <p><b>
	 * [OUT] Create time
	 * </b></p>
	 * 
     * @return Create time
     */ 
	public String getCreatetime() {
		return createtime;
	}
    
    /**
	 * <p><b>
	 * [IN] Create time
	 * </b></p>
     *
     * @param createtime Create time
     */
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
    
    /**
	 * <p><b>
	 * [OUT] Update time
	 * </b></p>
	 * 
     * @return Update time
     */ 
	public String getUpdatetime() {
		return updatetime;
	}
    
    /**
	 * <p><b>
	 * [IN] Update time
	 * </b></p>
     *
     * @param updatetime Update time
     */
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
    
    /**
	 * <p><b>
	 * [OUT] Expiretime
	 * </b></p>
	 * 
     * @return Expiretime
     */ 
	public String getExpiredate() {
		return expiredate;
	}
    
    /**
	 * <p><b>
	 * [IN] Expiretime
	 * </b></p>
     *
     * @param expiredate Expiretime
     */
	public void setExpiredate(String expiredate) {
		this.expiredate = expiredate;
	}
    
    /**
	 * <p><b>
	 * [OUT] State
	 * </b></p>
	 * 
     * @return State
     */ 
	public String getState() {
		return state;
	}
    
    /**
	 * <p><b>
	 * [IN] State
	 * </b></p>
     *
     * @param state State
     */
	public void setState(String state) {
		this.state = state;
	}
    
    /**
	 * <p><b>
	 * [OUT] Dynamic value 01
	 * </b></p>
	 * 
     * @return Dynamic value 01
     */     
    public String getDynamic01() {
        return dynamic01;
    }
    
    /**
	 * <p><b>
	 * [IN] Dynamic value 01
	 * </b></p>
     *
     * @param dynamic01 Dynamic value 01
     */  
    public void setDynamic01(String dynamic01) {
        this.dynamic01 = dynamic01 == null ? null : dynamic01.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dynamic value 02
	 * </b></p>
	 * 
     * @return Dynamic value 02
     */      
    public String getDynamic02() {
		return dynamic02;
	}
    
    /**
	 * <p><b>
	 * [IN] Dynamic value 02
	 * </b></p>
     *
     * @param dynamic02 Dynamic value 02
     */  
	public void setDynamic02(String dynamic02) {
		this.dynamic02 = dynamic02;
	}
    
    /**
	 * <p><b>
	 * [OUT] Dynamic value 03
	 * </b></p>
	 * 
     * @return Dynamic value 03
     */    	
	public String getDynamic03() {
        return dynamic03;
    }
    
    /**
	 * <p><b>
	 * [IN] Dynamic value 03
	 * </b></p>
     *
     * @param dynamic03 Dynamic value 03
     */  
    public void setDynamic03(String dynamic03) {
        this.dynamic03 = dynamic03 == null ? null : dynamic03.trim();
    }
	
	/**
	 * <p><b>
	 * [OUT] Security Level
	 * </b></p>
	 * 
     * @return Security Level
     */ 	
	public int getSeclevel() {
		return seclevel;
	}

	/**
	 * <p><b>
	 * [IN] Security Level
	 * </b></p>
     *
     * @param seclevel Security Level
     */      
	public void setSeclevel(int seclevel) {
		this.seclevel = seclevel;
	}
	
    
    /**
	 * <p><b>
	 * [OUT] File no.
	 * </b></p>
	 * 
     * @return File no.
     */ 
	public String getFno() {
		return fno;
	}
    
    /**
	 * <p><b>
	 * [IN] File no.
	 * </b></p>
     *
     * @param fno File no.
     */
	public void setFno(String fno) {
		this.fno = fno;
	}
    
    /**
	 * <p><b>
	 * [OUT] Creator name
	 * </b></p>
	 * 
     * @return Creator name
     */ 
	public String getCreatorname() {
		return creatorname;
	}
    
    /**
	 * <p><b>
	 * [IN] Creator name
	 * </b></p>
     *
     * @param creatorname Creator name
     */
	public void setCreatorname(String creatorname) {
		this.creatorname = creatorname;
	}
    
    /**
	 * <p><b>
	 * [OUT] Retention period ID
	 * </b></p>
	 * 
     * @return Retention period ID
     */ 
	public String getRetentionid() {
		return retentionid;
	}
    
    /**
	 * <p><b>
	 * [IN] Retention period ID
	 * </b></p>
     *
     * @param retentionid Retention period ID
     */
	public void setRetentionid(String retentionid) {
		this.retentionid = retentionid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Expire date
	 * </b></p>
	 * 
     * @return Expire date
     */ 
	public String getExpiretime() {
		return expiretime;
	}
    
    /**
	 * <p><b>
	 * [IN] Expire date
	 * </b></p>
     *
     * @param expiretime Expire date
     */
	public void setExpiretime(String expiretime) {
		this.expiretime = expiretime;
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
	 * [OUT] Discarder ID
	 * </b></p>
	 * 
     * @return Discarder ID
     */ 
	public String getDiscarderid() {
		return discarderid;
	}
    
    /**
	 * <p><b>
	 * [IN] Discarder ID
	 * </b></p>
     *
     * @param discarderid Discarder ID
     */
	public void setDiscarderid(String discarderid) {
		this.discarderid = discarderid;
	}
    
    /**
	 * <p><b>
	 * [OUT] File extension
	 * </b></p>
	 * 
     * @return File extension
     */ 
	public String getExt() {
		return ext;
	}
    
    /**
	 * <p><b>
	 * [IN] File extension
	 * </b></p>
     *
     * @param ext File extension
     */
	public void setExt(String ext) {
		this.ext = ext;
	}
    
	/* ************************************************************************************************************************** */
	
    /**
	 * <p><b>
	 * [OUT] Processing type
	 * </b></p>
	 * 
     * @return Processing type
     */ 
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
    
    /**
	 * <p><b>
	 * [OUT] Version ID
	 * </b></p>
	 * 
     * @return Version ID
     */ 
	public String getVersionid() {
		return versionid;
	}
	
    /**
	 * <p><b>
	 * [IN] Version ID
	 * </b></p>
     *
     * @param versionid String Version ID
     */
	public void setVersionid(String versionid) {
		this.versionid = versionid;
	}    
	
    /**
	 * <p><b>
	 * [OUT] Released ? 
	 * </b></p>
	 * 
     * @return Released ? 
     */ 
	public boolean getIsreleased() {
		return isreleased;
	}
	
    /**
	 * <p><b>
	 * [IN] Released ? 
	 * </b></p>
     *
     * @param isreleased Boolean Released ? 
     */
	public void setIsreleased(boolean isreleased) {
		this.isreleased = isreleased;
	}    
	
    /**
	 * <p><b>
	 * [OUT] Hash ID (Max version)
	 * </b></p>
	 * 
     * @return Hash ID (Max version)
     */ 
	public String getMaxhashid() {
		return maxhashid;
	}
	
    /**
	 * <p><b>
	 * [IN] Hash ID (Max version)
	 * </b></p>
     *
     * @param maxhashid String Hash ID (Max version)
     */
	public void setMaxhashid(String maxhashid) {
		this.maxhashid = maxhashid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Workflow ID
	 * </b></p>
	 * 
     * @return Workflow ID
     */
    public String getWorkflowid() {
        return workflowid;
    }
    
    /**
	 * <p><b>
	 * [IN] Workflow ID
	 * </b></p>
     *
     * @param workflowid Workflow ID
     */  
    public void setWorkflowid(String workflowid) {
        this.workflowid = workflowid == null ? null : workflowid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Caller
	 * </b></p>
	 * 
     * @return Caller
     */
	public String getObjCaller() {
		return objCaller;
	}
    
    /**
	 * <p><b>
	 * [IN] Caller
	 * </b></p>
     *
     * @param objCaller Caller
     */ 
	public void setObjCaller(String objCaller) {
		this.objCaller = objCaller;
	}
    
    /**
	 * <p><b>
	 * [OUT] Drafter
	 * </b></p>
	 * 
     * @return Drafter
     */
	public String getDrafter() {
		return drafter;
	}
    
    /**
	 * <p><b>
	 * [IN] Drafter
	 * </b></p>
     *
     * @param drafter Drafter
     */
	public void setDrafter(String drafter) {
		this.drafter = drafter;
	}    
    
    /**
	 * <p><b>
	 * [OUT] Summary
	 * </b></p>
	 * 
     * @return Summary
     */  	
	public String getSummary() {
		return summary;
	}
    
    /**
	 * <p><b>
	 * [IN] Summary
	 * </b></p>
     *
     * @param summary Summary
     */  
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
    
}

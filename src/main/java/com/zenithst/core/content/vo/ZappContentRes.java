package com.zenithst.core.content.vo;

import java.util.ArrayList;
import java.util.List;

import com.zenithst.archive.vo.ZArchMFileRes;
import com.zenithst.core.acl.vo.ZappAclExtend;
import com.zenithst.core.classification.vo.ZappClassification;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content (OUT) <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappContentRes pIn = new ZappContentRes();
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappContentRes {

	/* Common */
	private String uid;
	private String contentid;
    private String contentno;
    private String title;
    private String creatorid;
    private String creatorname;
    private String creatordeptname;
    private String holderid;
    private String holdername;
    private String holderdeptname;
    private String createtime;
    private String updatetime;
    private String retentionid;
    private String expiretime;
    private String state;
    private String summary;
    private String ctype;
    private int apporder;
    private String appname;
    
    /* Extra */
    private String contenttype;
    private int acls;
    private String version;		// Version
    private String islocked;	// Locked ?
    private String isshared;	// Shared ?
    private String islinked;	// Linked ?
    private String classname;	// Classification name
    private String classpath;	// Classification path
    private String lockername;	// Locker name
    private String lockerdeptname;	// Locker dept. name
    private Double filesize = 0.0;	// File size
    private String wfinf;

    /* Logs */
    private String contentacls;
    private String keywords;
    private String folders;
    private String files;
    private String shares;
    private String locks;
    private String marks;
    private String reasons;
	
    private String ftrResult;
	/**
	 * <p><b>
	 * [OUT] UID
	 * </b></p>
	 * 
     * @return UID
     */   
    public String getUid() {
		return uid;
	}
    
    /**
	 * <p><b>
	 * [IN] UID
	 * </b></p>
     *
     * @param uid UID
     */ 
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * <p><b>
	 * [OUT] Content ID
	 * </b></p>
	 * 
     * @return Content ID
     */
	public String getContentid() {
		return contentid;
	}
    
    /**
	 * <p><b>
	 * [IN] Content ID
	 * </b></p>
     *
     * @param contentid Content ID
     */ 
	public void setContentid(String contentid) {
		this.contentid = contentid;
	}
	
    /**
	 * <p><b>
	 * [OUT] Content No.
	 * </b></p>
	 * 
     * @return Content No.
     */
	public String getContentno() {
		return contentno;
	}
    
    /**
	 * <p><b>
	 * [IN] Content No.
	 * </b></p>
     *
     * @param contentno Content No.
     */ 
	public void setContentno(String contentno) {
		this.contentno = contentno;
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
		this.title = title;
	}
	
    /**
	 * <p><b>
	 * [OUT] Creator ID
	 * </b></p>
	 * 
     * @return Creator ID
     */
	public String getCreatorid() {
		return creatorid;
	}
    
    /**
	 * <p><b>
	 * [IN] Creator ID
	 * </b></p>
     *
     * @param creatorid Creator ID
     */
	public void setCreatorid(String creatorid) {
		this.creatorid = creatorid;
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
	 * [OUT] Creator Dept. name
	 * </b></p>
	 * 
     * @return Creator Dept. name
     */	
    public String getCreatordeptname() {
		return creatordeptname;
	}
    
    /**
	 * <p><b>
	 * [IN] Creator Dept. name
	 * </b></p>
     *
     * @param creatordeptname Creator Dept. name
     */
	public void setCreatordeptname(String creatordeptname) {
		this.creatordeptname = creatordeptname;
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
	 * [OUT] Holder name
	 * </b></p>
	 * 
     * @return Holder name
     */
	public String getHoldername() {
		return holdername;
	}
    
    /**
	 * <p><b>
	 * [IN] Holder name
	 * </b></p>
     *
     * @param holdername Holder name
     */
	public void setHoldername(String holdername) {
		this.holdername = holdername;
	}
	
    /**
	 * <p><b>
	 * [OUT] Holder dept. name
	 * </b></p>
	 * 
     * @return Holder ndept. ame
     */    
	public String getHolderdeptname() {
		return holderdeptname;
	}
    
    /**
	 * <p><b>
	 * [IN] Holder dept. name
	 * </b></p>
     *
     * @param holderdeptname Holder dept. name
     */
	public void setHolderdeptname(String holderdeptname) {
		this.holderdeptname = holderdeptname;
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
	 * [OUT] Expiretime
	 * </b></p>
	 * 
     * @return Expiretime
     */
	public String getExpiretime() {
		return expiretime;
	}
    
    /**
	 * <p><b>
	 * [IN] Expiretime
	 * </b></p>
     *
     * @param expiretime Expiretime
     */
	public void setExpiretime(String expiretime) {
		this.expiretime = expiretime;
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
	 * [OUT] Content type (01:Bundle, 02:File)
	 * 
     * @return Content type
     */
	public String getContenttype() {
		return contenttype;
	}
    
    /**
	 * <p><b>
	 * [IN] Content type (01:Bundle, 02:File)
	 * </b></p>
     *
     * @param contenttype Content type
     */
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	
    /**
	 * <p><b>
	 * [OUT] Access control value
	 * </b></p>
	 * 
     * @return Access control value
     */
	public int getAcls() {
		return acls;
	}
    
    /**
	 * <p><b>
	 * [IN] Access control value
	 * </b></p>
     *
     * @param acls Access control value
     */
	public void setAcls(int acls) {
		this.acls = acls;
	}
	
    /**
	 * <p><b>
	 * [OUT] Version
	 * </b></p>
	 * 
     * @return Version
     */
	public String getVersion() {
		return version;
	}
    
    /**
	 * <p><b>
	 * [IN] Version
	 * </b></p>
     *
     * @param version Version
     */
	public void setVersion(String version) {
		this.version = version;
	}
	
    /**
	 * <p><b>
	 * [OUT] Locked ? (Y/N)
	 * </b></p>
	 * 
     * @return Locked ?
     */
	public String getIslocked() {
		return islocked;
	}
    
    /**
	 * <p><b>
	 * [IN] Locked ? (Y/N)
	 * </b></p>
     *
     * @param islocked Locked ?
     */
	public void setIslocked(String islocked) {
		this.islocked = islocked;
	}
	
    /**
	 * <p><b>
	 * [OUT] Shared ? (Y/N)
	 * </b></p>
	 * 
     * @return Shared ?
     */
	public String getIsshared() {
		return isshared;
	}
    
    /**
	 * <p><b>
	 * [IN] Shared ? (Y/N)
	 * </b></p>
     *
     * @param isshared Shared ?
     */
	public void setIsshared(String isshared) {
		this.isshared = isshared;
	}
	
    /**
	 * <p><b>
	 * [OUT] Linked ? (Y/N)
	 * </b></p>
	 * 
     * @return Linked ?
     */
	public String getIslinked() {
		return islinked;
	}
    
    /**
	 * <p><b>
	 * [IN] Linked ? (Y/N)
	 * </b></p>
     *
     * @param islinked Linked ?
     */
	public void setIslinked(String islinked) {
		this.islinked = islinked;
	}
	
    /**
	 * <p><b>
	 * [OUT] Classification name
	 * </b></p>
	 * 
     * @return Classification name
     */
	public String getClassname() {
		return classname;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification name
	 * </b></p>
     *
     * @param classname Classification name
     */
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
    /**
	 * <p><b>
	 * [OUT] Classification path
	 * </b></p>
	 * 
     * @return Classification path
     */
	public String getClasspath() {
		return classpath;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification path
	 * </b></p>
     *
     * @param classpath Classification path
     */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
    /**
	 * <p><b>
	 * [OUT] Locker name
	 * </b></p>
	 * 
     * @return Locker name
     */
	public String getLockername() {
		return lockername;
	}
    
    /**
	 * <p><b>
	 * [IN] Locker name
	 * </b></p>
     *
     * @param lockername Locker name
     */
	public void setLockername(String lockername) {
		this.lockername = lockername;
	}
	
    /**
	 * <p><b>
	 * [OUT] Locker dept. name
	 * </b></p>
	 * 
     * @return Locker dept. name
     */    public String getLockerdeptname() {
		return lockerdeptname;
	}
     
     /**
 	 * <p><b>
 	 * [IN] Locker dept. name
 	 * </b></p>
      *
      * @param lockerdeptname Locker dept. name
      */
	public void setLockerdeptname(String lockerdeptname) {
		this.lockerdeptname = lockerdeptname;
	}

	/**
	 * <p><b>
	 * [OUT] File size
	 * </b></p>
	 * 
     * @return File size
     */
	public Double getFilesize() {
		return filesize;
	}
    
    /**
	 * <p><b>
	 * [IN] File size
	 * </b></p>
     *
     * @param filesize File size
     */
	public void setFilesize(Double filesize) {
		this.filesize = filesize;
	}
	
	public String getWfinf() {
		return wfinf;
	}

	public void setWfinf(String wfinf) {
		this.wfinf = wfinf;
	}

	/* File info. */
	private List<ZArchMFileRes> zappFiles = new ArrayList<ZArchMFileRes>();
	
    /**
	 * <p><b>
	 * [OUT] File list
	 * </b></p>
	 * 
     * @return File list
     */
	public List<ZArchMFileRes> getZappFiles() {
		return zappFiles;
	}
    
    /**
	 * <p><b>
	 * [IN] File list
	 * </b></p>
     *
     * @param zappFiles List&lt;ZArchMFileRes&gt; File list
     */
	public void setZappFiles(List<ZArchMFileRes> zappFiles) {
		this.zappFiles = zappFiles;
	}

	/* Access control */
	private ZappAclExtend zappAcl;
	private List<ZappAclExtend> zappAcls = new ArrayList<ZappAclExtend>();
	
    /**
	 * <p><b>
	 * [OUT] Extended access control info.
	 * </b></p>
	 * 
     * @return Extended access control info.
     */
	public ZappAclExtend getZappAcl() {
		return zappAcl;
	}
    
    /**
	 * <p><b>
	 * [IN] Extended access control info.
	 * </b></p>
     *
     * @param zappAcl ZappAclExtend; Extended access control info.
     */
	public void setZappAcl(ZappAclExtend zappAcl) {
		this.zappAcl = zappAcl;
	}
	
    /**
	 * <p><b>
	 * [OUT] Extended access control list
	 * </b></p>
	 * 
     * @return Extended access control list
     */

	public List<ZappAclExtend> getZappAcls() {
		return zappAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] Extended access control list
	 * </b></p>
     *
     * @param zappAcls List&lt;ZappAclExtend&gt; Extended access control list
     */
	public void setZappAcls(List<ZappAclExtend> zappAcls) {
		this.zappAcls = zappAcls;
	}
	
	/* 분류 정보 */
	private List<ZappClassification> zappClassifications = new ArrayList<ZappClassification>();
	
    /**
	 * <p><b>
	 * [OUT] Classification list
	 * </b></p>
	 * 
     * @return Classification list
     */
	public List<ZappClassification> getZappClassifications() {
		return zappClassifications;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification list
	 * </b></p>
     *
     * @param zappClassifications List&lt;ZappClassification&gt; Classification list
     */
	public void setZappClassifications(List<ZappClassification> zappClassifications) {
		this.zappClassifications = zappClassifications;
	}

	/* Keyword 정보 */
	private List<ZappKeywordObject> zappKeywords = new ArrayList<ZappKeywordObject>();
	
    /**
	 * <p><b>
	 * [OUT] Keyword list
	 * </b></p>
	 * 
     * @return Keyword list
     */
	public List<ZappKeywordObject> getZappKeywords() {
		return zappKeywords;
	}
    
    /**
	 * <p><b>
	 * [IN] Keyword list
	 * </b></p>
     *
     * @param zappKeywords List&lt;ZappKeywordObject&gt; Keyword list
     */
	public void setZappKeywords(List<ZappKeywordObject> zappKeywords) {
		this.zappKeywords = zappKeywords;
	}
	
	/* 반출 정보 */
	private List<ZappLockedObjectExtend> zappLockedObject = new ArrayList<ZappLockedObjectExtend>();

    /**
	 * <p><b>
	 * [OUT] Locked content list
	 * </b></p>
	 * 
     * @return Locked content list
     */
	public List<ZappLockedObjectExtend> getZappLockedObject() {
		return zappLockedObject;
	}

    /**
	 * <p><b>
	 * [IN] Locked content list
	 * </b></p>
     *
     * @param zappLockedObject List&lt;ZappLockedObjectExtend&gt; Locked content list
     */
	public void setZappLockedObject(List<ZappLockedObjectExtend> zappLockedObject) {
		this.zappLockedObject = zappLockedObject;
	}
	
	/* Share info. */
	private ZappSharedObjectExtend zappSharedObject;

    /**
	 * <p><b>
	 * [OUT] Shared content
	 * </b></p>
	 * 
     * @return Shared content
     */
	public ZappSharedObjectExtend getZappSharedObject() {
		return zappSharedObject;
	}

    /**
	 * <p><b>
	 * [IN] Shared content
	 * </b></p>
     *
     * @param zappSharedObject ZappSharedObject Shared content
     */
	public void setZappSharedObject(ZappSharedObjectExtend zappSharedObject) {
		this.zappSharedObject = zappSharedObject;
	}

	public String getContentacls() {
		return contentacls;
	}

	public void setContentacls(String contentacls) {
		this.contentacls = contentacls;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getFolders() {
		return folders;
	}

	public void setFolders(String folders) {
		this.folders = folders;
	}

	public String getShares() {
		return shares;
	}

	public void setShares(String shares) {
		this.shares = shares;
	}

	public String getLocks() {
		return locks;
	}

	public void setLocks(String locks) {
		this.locks = locks;
	}

	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}

	public String getReasons() {
		return reasons;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public int getApporder() {
		return apporder;
	}

	public void setApporder(int apporder) {
		this.apporder = apporder;
	}
	
	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	/* Additional Bundle */
	private ZappAdditoryBundle  zappAdditoryBundle;

	public ZappAdditoryBundle getZappAdditoryBundle() {
		return zappAdditoryBundle;
	}

	public void setZappAdditoryBundle(ZappAdditoryBundle zappAdditoryBundle) {
		this.zappAdditoryBundle = zappAdditoryBundle;
	}
	
	public String getFtrResult() {
		return ftrResult;
	}

	public void setFtrResult(String ftrResult) {
		this.ftrResult = ftrResult;
	}
	
}

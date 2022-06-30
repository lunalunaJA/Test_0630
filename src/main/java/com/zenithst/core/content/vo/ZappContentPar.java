package com.zenithst.core.content.vo;

import java.util.ArrayList;
import java.util.List;

import com.zenithst.archive.vo.ZArchExtend;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchVersion;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content (IN) <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappContentPar pIn = new ZappContentPar();
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappContentPar extends ZappDomain {
	
	private String contentid;
    
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

	/* */
    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
	public ZappContentPar() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param objType Content type
     */ 
	public ZappContentPar(String objType) {
		super.setObjType(objType);
	}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param objTaskid Content type
     * @param objType Content type
     */ 
	public ZappContentPar(String objTaskid, String objType) {
		this.objTaskid = objTaskid;
		super.setObjType(objType);
	}

	/* Archive */
	private String objTaskid;
	private ZArchExtend zArchExtend;
	private ZArchMFile zArchMFile;
	private ZappFile zappFile;
	private List<ZappFile> zappFiles = new ArrayList<ZappFile>(); 
	private ZArchVersion zArchVersion;
	private boolean isreleased = true;
	private boolean hasfile = true;

    /**
	 * <p><b>
	 * [OUT] Task ID
	 * </b></p>
	 * 
     * @return Task ID
     */
	public String getObjTaskid() {
		return objTaskid;
	}
    
    /**
	 * <p><b>
	 * [IN] Task ID
	 * </b></p>
     *
     * @param objTaskid Task ID
     */ 
	public void setObjTaskid(String objTaskid) {
		this.objTaskid = objTaskid;
	}

    /**
	 * <p><b>
	 * [OUT] Extended info. (Archive)
	 * </b></p>
	 * 
     * @return Extended info. (Archive)
     */
	public ZArchExtend getzArchExtend() {
		return zArchExtend;
	}
    
    /**
	 * <p><b>
	 * [IN] Extended info. (Archive)
	 * </b></p>
     *
     * @param zArchExtend ZArchExtend Extended info. (Archive)
     */
	public void setzArchExtend(ZArchExtend zArchExtend) {
		this.zArchExtend = zArchExtend;
	}

    /**
	 * <p><b>
	 * [OUT] File
	 * </b></p>
	 * 
     * @return File
     */
	public ZappFile getZappFile() {
		return zappFile;
	}
    
    /**
	 * <p><b>
	 * [IN] File
	 * </b></p>
     *
     * @param zappFile ZappFile File
     */
	public void setZappFile(ZappFile zappFile) {
		this.zappFile = zappFile;
	}
	
    /**
	 * <p><b>
	 * [OUT] Master file
	 * </b></p>
	 * 
     * @return Master file
     */    
	public ZArchMFile getzArchMFile() {
		return zArchMFile;
	}
     
     /**
 	 * <p><b>
 	 * [IN] Master file
 	 * </b></p>
      *
      * @param zArchMFile ZArchMFile Master file
      */
	public void setzArchMFile(ZArchMFile zArchMFile) {
		this.zArchMFile = zArchMFile;
	}

	/**
	 * <p><b>
	 * [OUT] File list
	 * </b></p>
	 * 
     * @return File list
     */
	public List<ZappFile> getZappFiles() {
		return zappFiles;
	}
    
    /**
	 * <p><b>
	 * [IN] File list
	 * </b></p>
     *
     * @param zappFiles List&lt;ZappFile&gt; File list
     */
	public void setZappFiles(List<ZappFile> zappFiles) {
		this.zappFiles = zappFiles;
	}

    /**
	 * <p><b>
	 * [OUT] Version
	 * </b></p>
	 * 
     * @return Version
     */
	public ZArchVersion getzArchVersion() {
		return zArchVersion;
	}
	
    /**
	 * <p><b>
	 * [IN] Version
	 * </b></p>
     *
     * @param zArchVersion ZArchVersion Version
     */
	public void setzArchVersion(ZArchVersion zArchVersion) {
		this.zArchVersion = zArchVersion;
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
	 * [OUT] Including file ?
	 * </b></p>
	 * 
     * @return Including file ?
     */ 	
	public boolean getHasfile() {
		return hasfile;
	}
	
    /**
	 * <p><b>
	 * [IN] Including file ?
	 * </b></p>
     *
     * @param hasfile Boolean Including file ?
     */	
	public void setHasfile(boolean hasfile) {
		this.hasfile = hasfile;
	}

	/* 4.0 */
	private ZappBundle zappBundle;
	private ZappAdditoryBundle zappAdditoryBundle;
	private ZappClassObject zappClassObject;
	private ZappClassification zappClassification;
	private ZappLinkedObject zappLinkedObject;
	private ZappSharedObject zappSharedObject;
	private ZappLockedObject zappLockedObject;
	private ZappKeyword zappKeyword;
	private ZappMarkedObject zappMarkedObject;
	private List<ZappClassObject> zappClassObjects = new ArrayList<ZappClassObject>();
	private List<ZappLinkedObject> zappLinkedObjects = new ArrayList<ZappLinkedObject>();
	private List<ZappSharedObject> zappSharedObjects = new ArrayList<ZappSharedObject>();
	private List<ZappKeywordExtend> zappKeywords = new ArrayList<ZappKeywordExtend>();
	private List<ZappKeywordObject> zappKeywordObjects = new ArrayList<ZappKeywordObject>();
	private List<ZappContentAcl> zappAcls = new ArrayList<ZappContentAcl>();
	private String objViewtype;		//View type 01 - For view, 02 - For edit
	

    /**
	 * <p><b>
	 * [OUT] Bundle
	 * </b></p>
	 * 
     * @return Bundle
     */
	public ZappBundle getZappBundle() {
		return zappBundle;
	}
	
    /**
	 * <p><b>
	 * [IN] Bundle
	 * </b></p>
     *
     * @param zappBundle ZappBundle Bundle
     */
	public void setZappBundle(ZappBundle zappBundle) {
		this.zappBundle = zappBundle;
	}
	
    /**
	 * <p><b>
	 * [OUT] AdditoryBundle
	 * </b></p>
	 * 
     * @return AdditoryBundle
     */
	public ZappAdditoryBundle getZappAdditoryBundle() {
		return zappAdditoryBundle;
	}
	
    /**
	 * <p><b>
	 * [IN] AdditoryBundle
	 * </b></p>
     *
     * @param zappAdditoryBundle ZappAdditoryBundle
     */
	public void setZappAdditoryBundle(ZappAdditoryBundle zappAdditoryBundle) {
		this.zappAdditoryBundle = zappAdditoryBundle;
	}	

	/**
	 * <p><b>
	 * [OUT] Classification
	 * </b></p>
	 * 
     * @return Classification
     */	
    public ZappClassification getZappClassification() {
		return zappClassification;
	}
	
    /**
	 * <p><b>
	 * [IN] Classification
	 * </b></p>
     *
     * @param zappClassification ZappClassification 
     */
	public void setZappClassification(ZappClassification zappClassification) {
		this.zappClassification = zappClassification;
	}

	/**
	 * <p><b>
	 * [OUT] Content-Classification
	 * </b></p>
	 * 
     * @return Content-Classification
     */
	public ZappClassObject getZappClassObject() {
		return zappClassObject;
	}
	
    /**
	 * <p><b>
	 * [IN] Content-Classification
	 * </b></p>
     *
     * @param zappClassObject ZappClassObject Content-Classification
     */
	public void setZappClassObject(ZappClassObject zappClassObject) {
		this.zappClassObject = zappClassObject;
	}
	
    /**
	 * <p><b>
	 * [OUT] 링크객체
	 * </b></p>
	 * 
     * @return 링크객체
     */
	public ZappLinkedObject getZappLinkedObject() {
		return zappLinkedObject;
	}
	
    /**
	 * <p><b>
	 * [IN] 링크객체
	 * </b></p>
     *
     * @param zappLinkedObject ZappLinkedObject 링크객체
     */
	public void setZappLinkedObject(ZappLinkedObject zappLinkedObject) {
		this.zappLinkedObject = zappLinkedObject;
	}

    /**
	 * <p><b>
	 * [OUT] 공유객체
	 * </b></p>
	 * 
     * @return 공유객체
     */
	public ZappSharedObject getZappSharedObject() {
		return zappSharedObject;
	}
	
    /**
	 * <p><b>
	 * [IN] 공유객체
	 * </b></p>
     *
     * @param zappSharedObject ZappSharedObject 공유객체
     */
	public void setZappSharedObject(ZappSharedObject zappSharedObject) {
		this.zappSharedObject = zappSharedObject;
	}

    /**
	 * <p><b>
	 * [OUT] 잠금객체
	 * </b></p>
	 * 
     * @return 잠금객체
     */
	public ZappLockedObject getZappLockedObject() {
		return zappLockedObject;
	}
	
    /**
	 * <p><b>
	 * [IN] 잠금객체
	 * </b></p>
     *
     * @param zappLockedObject ZappLockedObject 잠금객체
     */
	public void setZappLockedObject(ZappLockedObject zappLockedObject) {
		this.zappLockedObject = zappLockedObject;
	}

    /**
	 * <p><b>
	 * [OUT] Keyword 객체
	 * </b></p>
	 * 
     * @return Keyword 객체
     */
	public ZappKeyword getZappKeyword() {
		return zappKeyword;
	}
	
    /**
	 * <p><b>
	 * [IN] Keyword 객체
	 * </b></p>
     *
     * @param zappkeyword ZappKeyword Keyword 객체
     */
	public void setZappKeyword(ZappKeyword zappkeyword) {
		this.zappKeyword = zappkeyword;
	}

    /**
	 * <p><b>
	 * [OUT] Content-Classification list
	 * </b></p>
	 * 
     * @return Content-Classification list
     */
	public List<ZappClassObject> getZappClassObjects() {
		return zappClassObjects;
	}
	
    /**
	 * <p><b>
	 * [IN] Content-Classification list
	 * </b></p>
     *
     * @param zappClassObjects List&lt;ZappClassObject&gt; Content-Classification list
     */	
	public void setZappClassObjects(List<ZappClassObject> zappClassObjects) {
		this.zappClassObjects = zappClassObjects;
	}

    /**
	 * <p><b>
	 * [OUT] Link list
	 * </b></p>
	 * 
     * @return Link list
     */
	public List<ZappLinkedObject> getZappLinkedObjects() {
		return zappLinkedObjects;
	}
	
    /**
	 * <p><b>
	 * [IN] Link list
	 * </b></p>
     *
     * @param zappLinkedObjects List&lt;ZappLinkedObject&gt; Link list
     */	
	public void setZappLinkedObjects(List<ZappLinkedObject> zappLinkedObjects) {
		this.zappLinkedObjects = zappLinkedObjects;
	}

    /**
	 * <p><b>
	 * [OUT] Share list
	 * </b></p>
	 * 
     * @return Share list
     */
	public List<ZappSharedObject> getZappSharedObjects() {
		return zappSharedObjects;
	}
	
    /**
	 * <p><b>
	 * [IN] Share list
	 * </b></p>
     *
     * @param zappSharedObjects List&lt;ZappSharedObject&gt; Share list
     */	
	public void setZappSharedObjects(List<ZappSharedObject> zappSharedObjects) {
		this.zappSharedObjects = zappSharedObjects;
	}

    /**
	 * <p><b>
	 * [OUT] Extended keyword list
	 * </b></p>
	 * 
     * @return Extended keyword list
     */
	public List<ZappKeywordExtend> getZappKeywords() {
		return zappKeywords;
	}
	
    /**
	 * <p><b>
	 * [IN] Extended keyword list
	 * </b></p>
     *
     * @param zappKeywords List&lt;ZappKeywordExtend&gt; Extended keyword list
     */	
	public void setZappKeywords(List<ZappKeywordExtend> zappKeywords) {
		this.zappKeywords = zappKeywords;
	}

    /**
	 * <p><b>
	 * [OUT] Content-Keyword list
	 * </b></p>
	 * 
     * @return Content-Keyword list
     */
	public List<ZappKeywordObject> getZappKeywordObjects() {
		return zappKeywordObjects;
	}
	
    /**
	 * <p><b>
	 * [IN] Content-Keyword list
	 * </b></p>
     *
     * @param zappKeywordObjects List&lt;ZappKeywordObject&gt; Content-Keyword list
     */	
	public void setZappKeywordObjects(List<ZappKeywordObject> zappKeywordObjects) {
		this.zappKeywordObjects = zappKeywordObjects;
	}

    /**
	 * <p><b>
	 * [OUT] Content access control list
	 * </b></p>
	 * 
     * @return Content access control list
     */
	public List<ZappContentAcl> getZappAcls() {
		return zappAcls;
	}
	
    /**
	 * <p><b>
	 * [IN] Content access control list
	 * </b></p>
     *
     * @param zappAcls List&lt;ZappContentAcl&gt; Content access control list
     */	
	public void setZappAcls(List<ZappContentAcl> zappAcls) {
		this.zappAcls = zappAcls;
	}

    /**
	 * <p><b>
	 * [OUT]View type (01:For view, 02:For edit)
	 * </b></p>
	 * 
     * @returnView type
     */
	public String getObjViewtype() {
		return objViewtype;
	}
	
    /**
	 * <p><b>
	 * [IN]View type (01:For view, 02:For edit)
	 * </b></p>
     *
     * @param objViewtypeView type
     */	
	public void setObjViewtype(String objViewtype) {
		this.objViewtype = objViewtype;
	}

	/* */
	private String objClassid;			// Classification ID
	private String objClasstype;		// Classification type

    /**
	 * <p><b>
	 * [OUT] Target classification ID
	 * </b></p>
	 * 
     * @return Target classification ID
     */
	public String getObjClassid() {
		return objClassid;
	}
	
    /**
	 * <p><b>
	 * [IN] Target classification ID
	 * </b></p>
     *
     * @param objClassid Target classification ID
     */
	public void setObjClassid(String objClassid) {
		this.objClassid = objClassid;
	}

    /**
	 * <p><b>
	 * [OUT] Target classification type
	 * </b></p>
	 * 
     * @return Target classification type
     */
	public String getObjClasstype() {
		return objClasstype;
	}
	
    /**
	 * <p><b>
	 * [IN] Target classification type
	 * </b></p>
     *
     * @param objClasstype Target classification type
     */
	public void setObjClasstype(String objClasstype) {
		this.objClasstype = objClasstype;
	}

    /**
	 * <p><b>
	 * [OUT] Marked content
	 * </b></p>
	 * 
     * @return Marked content
     */
	public ZappMarkedObject getZappMarkedObject() {
		return zappMarkedObject;
	}
	
    /**
	 * <p><b>
	 * [IN] Marked content
	 * </b></p>
     *
     * @param zappMarkedObject ZappMarkedObject Marked content
     */
	public void setZappMarkedObject(ZappMarkedObject zappMarkedObject) {
		this.zappMarkedObject = zappMarkedObject;
	}
	
	/* Adding mode */
	private String addMode = ZappConts.STATES.CHECK_FILE_ONLY_ADD.state;

    /**
	 * <p><b>
	 * [OUT] Adding mode
	 * </b></p>
	 * 
     * @return Adding mode
     */
	public String getAddMode() {
		return addMode;
	}

    /**
	 * <p><b>
	 * [IN] Adding mode
	 * </b></p>
     *
     * @param addMode Adding mode
     */
	public void setAddMode(String addMode) {
		this.addMode = addMode;
	}
	
	
	/* FTR */
	private String sword;

	/**
	 * <p><b>
	 * [OUT] Search word
	 * </b></p>
	 * 
     * @return Search word
     */
	public String getSword() {
		return sword;
	}
	
    /**
	 * <p><b>
	 * [IN] Search word
	 * </b></p>
     *
     * @param sword Search word
     */
	public void setSword(String sword) {
		this.sword = sword;
	}
	
	/* Enable Options */
	private String enableOpt;
	private String enableOwid;
	private String enableName;
	
	/**
	 * <p><b>
	 * [OUT] Enable option
	 * </b></p>
	 * 
     * @return Search word
     */
	public String getEnableOpt() {
		return enableOpt;
	}
	
    /**
	 * <p><b>
	 * [IN] Enable option
	 * </b></p>
     *
     * @param enableOpt Enable option
     */
	public void setEnableOpt(String enableOpt) {
		this.enableOpt = enableOpt;
	}

	public String getEnableOwid() {
		return enableOwid;
	}

	public void setEnableOwid(String enableOwid) {
		this.enableOwid = enableOwid;
	}

	public String getEnableName() {
		return enableName;
	}

	public void setEnableName(String enableName) {
		this.enableName = enableName;
	}
	
	/* */
	private String objVerOpt;

	public String getObjVerOpt() {
		return objVerOpt;
	}

	public void setObjVerOpt(String objVerOpt) {
		this.objVerOpt = objVerOpt;
	}

}

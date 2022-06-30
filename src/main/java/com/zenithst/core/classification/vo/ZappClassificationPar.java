package com.zenithst.core.classification.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.content.vo.ZappKeywordExtend;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for classification (IN) <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappClassificationPar pIn = new ZappClassificationPar();
*    ...
*    <br>    
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappClassificationPar extends ZappClassification {

	/* 분류 기본 접근 권한 */
	private List<ZappClassAcl> zappClassAcls = new ArrayList<ZappClassAcl>();	
	/* 컨텐츠 기본 접근 권한 */
	private List<ZappContentAcl> zappContentAcls = new ArrayList<ZappContentAcl>();
	/* Keyword 객체 */
	private List<ZappKeywordExtend> zappKeywordObjects = new ArrayList<ZappKeywordExtend>();
	/* 확장 분류 */
	private ZappAdditoryClassification zappAdditoryClassification;
	
	/* 조회 레벨 */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int viewlevel;
	
    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappClassificationPar() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param classid Classification ID
     */
    public ZappClassificationPar(String classid) {
    	super.setClassid(classid);
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param companyid Company ID
     * @param code Classification code
     */
    public ZappClassificationPar(String companyid, String code) {
    	super.setCompanyid(companyid);
    	super.setCode(code);
    }
    
    /**
	 * <p><b>
	 * [OUT] 분류 접근 권한 Object 리스트 (ZAPP_CLASSACL)
	 * </b></p>
	 * 
     * @return 분류 접근 권한 Object 리스트
     */
	public List<ZappClassAcl> getZappClassAcls() {
		return zappClassAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] 분류 접근 권한 Object 리스트 (ZAPP_CLASSACL)
	 * </b></p>
     *
     * @param zappClassAcls 분류 접근 권한 Object 리스트
     */ 	
	public void setZappClassAcls(List<ZappClassAcl> zappClassAcls) {
		this.zappClassAcls = zappClassAcls;
	}
    
    /**
	 * <p><b>
	 * [OUT] 컨텐츠 접근 권한 Object 리스트 (ZAPP_CONTENTACL)
	 * </b></p>
	 * 
     * @return 컨텐츠 접근 권한 Object 리스트
     */
	public List<ZappContentAcl> getZappContentAcls() {
		return zappContentAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] 컨텐츠 접근 권한 Object 리스트 (ZAPP_CONTENTACL)
	 * </b></p>
     *
     * @param zappContentAcls 컨텐츠 접근 권한 Object 리스트
     */ 	
	public void setZappContentAcls(List<ZappContentAcl> zappContentAcls) {
		this.zappContentAcls = zappContentAcls;
	}
    
    /**
	 * <p><b>
	 * [OUT] Keyword 객체 Object 리스트 (ZAPP_KEYWORDOBJECT)
	 * </b></p>
	 * 
     * @return Keyword 객체 Object 리스트
     */
	public List<ZappKeywordExtend> getZappKeywordObjects() {
		return zappKeywordObjects;
	}
    
    /**
	 * <p><b>
	 * [IN] Keyword 객체 Object 리스트 (ZAPP_KEYWORDOBJECT)
	 * </b></p>
     *
     * @param zappKeywordObjects Keyword 객체 Object 리스트
     */ 	
	public void setZappKeywordObjects(List<ZappKeywordExtend> zappKeywordObjects) {
		this.zappKeywordObjects = zappKeywordObjects;
	}
	
    /**
	 * <p><b>
	 * [OUT] 확장분류 (ZAPP_ADDITORYCLASS)
	 * </b></p>
	 * 
     * @return 확장분류
     */
	public ZappAdditoryClassification getZappAdditoryClassification() {
		return zappAdditoryClassification;
	}
    
    /**
	 * <p><b>
	 * [IN] 확장분류 (ZAPP_ADDITORYCLASS)
	 * </b></p>
     *
     * @param zappAdditoryClassification 확장분류
     */ 
	public void setZappAdditoryClassification(ZappAdditoryClassification zappAdditoryClassification) {
		this.zappAdditoryClassification = zappAdditoryClassification;
	}
	
    /**
	 * <p><b>
	 * [OUT] View Level
	 * </b></p>
	 * 
     * @return View Level
     */
	public int getViewlevel() {
		return viewlevel;
	}
	
    /**
	 * <p><b>
	 * [IN] View Level
	 * </b></p>
     *
     * @param viewlevel View Level
     */ 
	public void setViewlevel(int viewlevel) {
		this.viewlevel = viewlevel;
	}	
	
	
    
}
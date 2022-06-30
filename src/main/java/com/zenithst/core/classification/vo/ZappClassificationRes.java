package com.zenithst.core.classification.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zenithst.core.acl.vo.ZappAclExtend;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.content.vo.ZappKeywordExtend;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for classification (OUT) <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappClassificationRes pIn = new ZappClassificationRes();
*    ...
*    <br>    
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappClassificationRes {

	private ZappClassification zappClassification;
	private ZappClassAcl zappClassAcl;
	private ZappAdditoryClassification zappAdditoryClassification;
	private List<ZappAclExtend> zappClassAcls = new ArrayList<ZappAclExtend>();
	private List<ZappAclExtend> zappContentAcls = new ArrayList<ZappAclExtend>();
	private Map<String, List<ZappAclExtend>> zappUnionAcls = new HashMap<String, List<ZappAclExtend>>();
	private List<ZappKeywordExtend> zappKeywords = new ArrayList<ZappKeywordExtend>();
	private String holdername;
	
    /**
	 * <p><b>
	 * [OUT] 분류 Object (ZAPP_CLASS)
	 * </b></p>
	 * 
     * @return 분류 Object
     */
	public ZappClassification getZappClassification() {
		return zappClassification;
	}
    
    /**
	 * <p><b>
	 * [IN] 분류 Object (ZAPP_CLASS)
	 * </b></p>
     *
     * @param zappClassification 분류 Object 
     */ 
	public void setZappClassification(ZappClassification zappClassification) {
		this.zappClassification = zappClassification;
	}
	
    public ZappClassAcl getZappClassAcl() {
		return zappClassAcl;
	}

	public void setZappClassAcl(ZappClassAcl zappClassAcl) {
		this.zappClassAcl = zappClassAcl;
	}

	/**
	 * <p><b>
	 * [OUT] 확장분류 Object (ZAPP_ADDITORYCLASS)
	 * </b></p>
	 * 
     * @return 확장분류 Object
     */
	public ZappAdditoryClassification getZappAdditoryClassification() {
		return zappAdditoryClassification;
	}
    
    /**
	 * <p><b>
	 * [IN] 확장분류 Object (ZAPP_ADDITORYCLASS)
	 * </b></p>
     *
     * @param zappAdditoryClassification 확장분류 Object 
     */ 
	public void setZappAdditoryClassification(ZappAdditoryClassification zappAdditoryClassification) {
		this.zappAdditoryClassification = zappAdditoryClassification;
	}

	/**
	 * <p><b>
	 * [OUT] 분류 권한 Object 리스트
	 * </b></p>
	 * 
     * @return 분류 권한 Object 리스트 
     */
	public List<ZappAclExtend> getZappClassAcls() {
		return zappClassAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] 분류 권한 Object 리스트
	 * </b></p>
     *
     * @param zappClassAcls 분류 권한 Object 리스트
     */ 
	public void setZappClassAcls(List<ZappAclExtend> zappClassAcls) {
		this.zappClassAcls = zappClassAcls;
	}

    /**
	 * <p><b>
	 * [OUT] 컨텐츠 권한 Object 리스트
	 * </b></p>
	 * 
     * @return 컨텐츠 권한 Object 리스트 
     */	
	public List<ZappAclExtend> getZappContentAcls() {
		return zappContentAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] 컨텐츠 권한 Object 리스트
	 * </b></p>
     *
     * @param zappContentAcls 컨텐츠 권한 Object 리스트
     */	
	public void setZappContentAcls(List<ZappAclExtend> zappContentAcls) {
		this.zappContentAcls = zappContentAcls;
	}

    /**
	 * <p><b>
	 * [OUT] 통합 권한 Object Map
	 * </b></p>
	 * 
     * @return 통합 권한 Object Map
     */	
	public Map<String, List<ZappAclExtend>> getZappUnionAcls() {
		return zappUnionAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] 통합 권한 Object Map
	 * </b></p>
     *
     * @param zappUnionAcls 통합 권한 Object Map
     */	
	public void setZappUnionAcls(Map<String, List<ZappAclExtend>> zappUnionAcls) {
		this.zappUnionAcls = zappUnionAcls;
	}

    /**
	 * <p><b>
	 * [OUT] Keyword Object 리스트
	 * </b></p>
	 * 
     * @return Keyword Object 리스트
     */
	public List<ZappKeywordExtend> getZappKeywords() {
		return zappKeywords;
	}
    
    /**
	 * <p><b>
	 * [IN] Keyword Object 리스트
	 * </b></p>
     *
     * @param zappKeywords Keyword Object 리스트
     */	
	public void setZappKeywords(List<ZappKeywordExtend> zappKeywords) {
		this.zappKeywords = zappKeywords;
	}

    /**
	 * <p><b>
	 * [OUT] Holder Name
	 * </b></p>
	 * 
     * @return Holder Name
     */
	public String getHoldername() {
		return holdername;
	}
    
    /**
	 * <p><b>
	 * [IN] Holder Name
	 * </b></p>
     *
     * @param holdername Holder Name
     */	
	public void setHoldername(String holdername) {
		this.holdername = holdername;
	}	

}

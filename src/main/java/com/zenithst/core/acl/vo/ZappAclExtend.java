package com.zenithst.core.acl.vo;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended ACL info.<br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappAclExtend pIn = new ZappAclExtend();
*    ...
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappAclExtend extends ZappContentAcl {

	/* Default */
	private String classid;
	
	/* Extend */
	private String objname;

    
    /**
   	 * <p><b>
   	 * [OUT] Classification ID (분류 ID)
   	 * </b></p>
   	 * 
     * @return Classification ID
    */
	public String getClassid() {
		return classid;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification ID 
	 * </b></p>
     *
     * @param classid Classification ID 
     */
	public void setClassid(String classid) {
		this.classid = classid;
	}
    
    /**
   	 * <p><b>
   	 * [OUT] Target AC Object Name (권한 대상 명칭)
   	 * </b></p>
   	 * 
     * @return Target AC Object Name
    */
	public String getObjname() {
		return objname;
	}
    
    /**
	 * <p><b>
	 * [IN] Target AC Object Name
	 * </b></p>
     *
     * @param objname Target AC Object Name
     */
	public void setObjname(String objname) {
		this.objname = objname;
	}

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
	public ZappAclExtend() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param aclid PK
     */  
	public ZappAclExtend(String aclid) {
		super.setAclid(aclid);
	}
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param acls ACL Value
     */ 
	public ZappAclExtend(Integer acls) {
		super.setAcls(acls);
	}
	
}

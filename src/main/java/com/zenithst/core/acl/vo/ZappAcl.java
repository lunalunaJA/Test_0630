package com.zenithst.core.acl.vo;

import java.util.List;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for ACL info.<br>
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

public class ZappAcl {

	private ZappClassAcl zappClassAcl;
	private List<ZappClassAcl> zappClassAcls;
	private ZappContentAcl zappContentAcl;
	private List<ZappContentAcl> zappContentAcls;
	
    /**
   	 * <p><b>
   	 * [OUT] Classification ACL (분류 접근 권한)
   	 * </b></p>
   	 * 
     * @return Classification ACL
    */
	public ZappClassAcl getZappClassAcl() {
		return zappClassAcl;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification ACL
	 * </b></p>
     *
     * @param zappClassAcl Classification ACL
     */
	public void setZappClassAcl(ZappClassAcl zappClassAcl) {
		this.zappClassAcl = zappClassAcl;
	}
	
    /**
   	 * <p><b>
   	 * [OUT] List of Classification ACL (분류 접근 권한 목록)
   	 * </b></p>
   	 * 
     * @return List of Classification ACL
    */
	public List<ZappClassAcl> getZappClassAcls() {
		return zappClassAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] List of Classification ACL
	 * </b></p>
     *
     * @param zappClassAcl List of Classification ACL
     */
	public void setZappClassAcls(List<ZappClassAcl> zappClassAcls) {
		this.zappClassAcls = zappClassAcls;
	}
	
    /**
   	 * <p><b>
   	 * [OUT] Content ACL (컨텐츠 접근 권한)
   	 * </b></p>
   	 * 
     * @return Content ACL
    */
	public ZappContentAcl getZappContentAcl() {
		return zappContentAcl;
	}
    
    /**
	 * <p><b>
	 * [IN] Content ACL
	 * </b></p>
     *
     * @param zappContentAcl Content ACL
     */
	public void setZappContentAcl(ZappContentAcl zappContentAcl) {
		this.zappContentAcl = zappContentAcl;
	}
	
    /**
   	 * <p><b>
   	 * [OUT] List of Content ACL (컨텐츠 접근 권한 목록)
   	 * </b></p>
   	 * 
     * @return List of Content ACL
    */
	public List<ZappContentAcl> getZappContentAcls() {
		return zappContentAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] List of Content ACL
	 * </b></p>
     *
     * @param zappContentAcls List of Content ACL
     */
	public void setZappContentAcls(List<ZappContentAcl> zappContentAcls) {
		this.zappContentAcls = zappContentAcls;
	}
	
}

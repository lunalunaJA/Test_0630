package com.zenithst.core.acl.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for classification ACL <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappClassAcl pIn = new ZappClassAcl();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CLASSACL</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>aclid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>classid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>aclobjid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>aclobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>acls</td><td></td><td></td><td></td><td>Integer</td><td></td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappClassAcl extends ZappDomain {

    private String aclid;
    private String classid;
    private String aclobjid;
    private String aclobjtype;
    private Integer acls;
    private String sacls;
    
    /* Extra */
    private String objAction;
    

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappClassAcl() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param aclid PK
     */
    public ZappClassAcl(String aclid) {
    	this.aclid = aclid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param acls ACL Value
     */
    public ZappClassAcl(Integer acls) {
    	this.acls = acls;
    }
    /**
	 * <p><b>
	 * Additional constructor #3
	 * </b></p>
     *
     * @param classid Classification ID
     * @param aclobjid Target object ID
     * @param aclobjtype Target object type
     */
    public ZappClassAcl(String classid, String aclobjid, String aclobjtype) {
    	this.classid = classid;
    	this.aclobjid = aclobjid;
    	this.aclobjtype = aclobjtype;
    }
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return Primary Key
     */
    public String getAclid() {
        return aclid;
    }
    
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param aclid Primary Key
     */
    public void setAclid(String aclid) {
        this.aclid = aclid == null ? null : aclid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Classification ID 
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
        this.classid = classid == null ? null : classid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Target ID 
	 * </b></p>
	 * 
     * @return Target ID
     */
    public String getAclobjid() {
		return aclobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Target ID
	 * </b></p>
     *
     * @param aclobjid Target ID
     */
	public void setAclobjid(String aclobjid) {
		this.aclobjid = aclobjid;
	}
	
    /**
	 * <p><b>
	 * [OUT] 권한 Target type
	 * </b></p>
	 * 
     * @return 권한 Target type
     */
	public String getAclobjtype() {
		return aclobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] 권한 Target type
	 * </b></p>
     *
     * @param aclobjtype 권한 Target type
     */
	public void setAclobjtype(String aclobjtype) {
		this.aclobjtype = aclobjtype;
	}
	
    /**
	 * <p><b>
	 * [OUT] ACL value
	 * </b></p>
	 * 
     * @return ACL value
     */
	public Integer getAcls() {
        return acls;
    }
    
    /**
	 * <p><b>
	 * [IN] ACL value
	 * </b></p>
     *
     * @param acls ACL value
     */
    public void setAcls(Integer acls) {
        this.acls = acls;
    }
    
    /**
	 * <p><b>
	 * [OUT] ACL value (String)
	 * </b></p>
	 * 
     * @return ACL value
     */
	public String getSacls() {
		return sacls;
	}
    
    /**
	 * <p><b>
	 * [IN] ACL value (String)
	 * </b></p>
     *
     * @param acls ACL value
     */
	public void setSacls(String sacls) {
		this.sacls = sacls;
	}
	
    /**
	 * <p><b>
	 * [OUT] Action (for processing)
	 * </b></p>
	 * 
     * @return Action
     */
	public String getObjAction() {
		return objAction;
	}
    
    /**
	 * <p><b>
	 * [IN] Action
	 * </b></p>
     *
     * @param objAction Action
     */
	public void setObjAction(String objAction) {
		this.objAction = objAction;
	}
    
}
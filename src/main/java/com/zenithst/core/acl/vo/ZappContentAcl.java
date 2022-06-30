package com.zenithst.core.acl.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content ACL<br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappContentAcl pIn = new ZappContentAcl();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CONTENTACL</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>aclid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4+5)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>contentid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>contenttype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>aclobjid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>aclobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>acls</td><td></td><td></td><td></td><td>Integer</td><td></td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappContentAcl extends ZappDomain {

    private String aclid;
    private String contentid;
    private String contenttype;
    private String aclobjid;
    private String aclobjtype;
    private Integer acls;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappContentAcl() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param aclid PK
     */    
    public ZappContentAcl(String aclid) {
    	this.aclid = aclid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param acls ACL Value
     */    
    public ZappContentAcl(Integer acls) {
    	this.acls = acls;
    }
    /**
	 * <p><b>
	 * Additional constructor #3
	 * </b></p>
     *
     * @param contentid Content ID
     * @param aclobjid Target object ID
     * @param aclobjtype Target object type
     */    
    public ZappContentAcl(String contentid, String aclobjid, String aclobjtype) {
    	this.contentid = contentid;
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
        this.contentid = contentid == null ? null : contentid.trim();
    }
    
    /**
   	 * <p><b>
   	 * [OUT] Content type 
   	 * </b></p>
   	 * 
     * @return Content type
     */
    public String getContenttype() {
		return contenttype;
	}
    
    /**
	 * <p><b>
	 * [IN] Content type
	 * </b></p>
     *
     * @param contenttype Content type
     */
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
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
	 * [OUT] Target type
	 * </b></p>
	 * 
     * @return Target type
     */
	public String getAclobjtype() {
		return aclobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type
	 * </b></p>
     *
     * @param aclobjtype Target type
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

	
}
package com.zenithst.core.organ.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for group <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappGroup pIn = new ZappGroup();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_GROUP</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>groupid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+4+5+6)</td><td>Group ID(PK)</td>
* 	 </tr> 	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>companyid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Company ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>name</td><td></td><td></td><td></td><td>VARCHAR(150)</td><td></td><td>Name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>upid</td><td></td><td></td><td>●</td><td>VARCHAR(64)</td><td></td><td>Upper ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>code</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Code</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>types</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Group type<br>(01:Company, 02:Department, 03:User, 04:Cooperation, 99:No access limit )</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>priority</td><td></td><td></td><td></td><td>INTEGER</td><td></td><td>Sorting order(Based on Upper ID)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Use or not(Y/N)</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappGroup extends ZappDomain {

	/* */
    private String groupid;
    private String companyid;
    private String name;
    private String upid;
    private String code;
    private String types;
    private int priority;
    private String isactive;


    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappGroup() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param groupid Group ID (PK)
     */    
    public ZappGroup(String groupid) {
    	this.groupid = groupid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param groupid Group ID (PK)
     * @param priority Sorting order
     */    
    public ZappGroup(String groupid, int priority) {
    	this.groupid = groupid;
    	this.priority = priority;
    }
    /**
	 * <p><b>
	 * Additional constructor #3
	 * </b></p>
     *
     * @param groupid Group ID (PK)
     * @param upid Upper ID
     */ 
    public ZappGroup(String groupid, String upid) {
    	this.groupid = groupid;
    	this.upid = upid;
    }    
    /**
	 * <p><b>
	 * Additional constructor #4
	 * </b></p>
     *
     * @param groupid Group ID (PK)
     * @param upid Upper ID
     * @param priority Sorting order
     */ 
    public ZappGroup(String groupid, String upid, int priority) {
    	this.groupid = groupid;
    	this.upid = upid;
    	this.priority = priority;
    } 
    
    /**
	 * <p><b>
	 * [OUT] Group ID - Primary Key
	 * </b></p>
	 * 
     * @return Group ID
     */
    public String getGroupid() {
        return groupid;
    }
    
    /**
	 * <p><b>
	 * [IN] Group ID - Primary Key
	 * </b></p>
     *
     * @param groupid Group ID
     */ 
    public void setGroupid(String groupid) {
        this.groupid = groupid == null ? null : groupid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Company ID (ZAPP_COMPANY - COMPANYID)
	 * </b></p>
	 * 
     * @return Company ID
     */
    public String getCompanyid() {
        return companyid;
    }
    
    /**
	 * <p><b>
	 * [IN] Company ID (ZAPP_COMPANY - COMPANYID)
	 * </b></p>
     *
     * @param companyid Company ID
     */ 
    public void setCompanyid(String companyid) {
        this.companyid = companyid == null ? null : companyid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Name
	 * </b></p>
	 * 
     * @return Name
     */
    public String getName() {
        return name;
    }
    
    /**
	 * <p><b>
	 * [IN] Name
	 * </b></p>
     *
     * @param name Name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Upper ID 
	 * </b></p>
	 * 
     * @return Upper ID
     */
    public String getUpid() {
        return upid;
    }
    
    /**
	 * <p><b>
	 * [IN] Upper ID 
	 * </b></p>
     *
     * @param upid Upper ID 
     */
    public void setUpid(String upid) {
        this.upid = upid == null ? null : upid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Code
	 * </b></p>
	 * 
     * @return Code
     */
    public String getCode() {
        return code;
    }
    
    /**
	 * <p><b>
	 * [IN] Code 
	 * </b></p>
     *
     * @param code Code
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Group Type (01:Company, 02:Department, 03:User, 04:Cooperation, 99:No access limit )
	 * </b></p>
	 * 
     * @return Group Type
     */
    public String getTypes() {
        return types;
    }
    
    /**
	 * <p><b>
	 * [IN] Group Type (01:Company, 02:Department, 03:User, 04:Cooperation, 99:No access limit )
	 * </b></p>
     *
     * @param types Group Type
     */
    public void setTypes(String types) {
        this.types = types == null ? null : types.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Sorting order
	 * </b></p>
	 * 
     * @return Sorting order
     */    
    public int getPriority() {
        return priority;
    }
    
    /**
	 * <p><b>
	 * [IN] Sorting order
	 * </b></p>
     *
     * @param priority Sorting order
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    /**
	 * <p><b>
	 * [OUT] Use or not (Y/N)
	 * </b></p>
	 * 
     * @return Use or not
     */
    public String getIsactive() {
        return isactive;
    }
    
    /**
	 * <p><b>
	 * [IN] Use or not (Y/N)
	 * </b></p>
     *
     * @param isactive Use or not
     */
    public void setIsactive(String isactive) {
        this.isactive = isactive == null ? null : isactive.trim();
    }
}
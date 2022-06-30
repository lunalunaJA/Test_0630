package com.zenithst.core.organ.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for department user <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappDeptUser pIn = new ZappDeptUser();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_DEPTUSER</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>deptuserid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3)</td>
* 	 </tr> 	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>deptid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>userid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>usertype</td><td></td><td></td><td>●</td><td>VARCHAR(2)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>originyn</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>positionid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>dutyid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>seclevelid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>issupervisor</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappDeptUser extends ZappDomain {

	/* */
    private String deptuserid;
    private String deptid;
    private String userid;
    private String usertype;
    private String originyn;
    private String positionid;
    private String dutyid;
    private String seclevelid;
    private String issupervisor;
    private String isactive;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappDeptUser() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param deptuserid Department User ID (PK)
     */
    public ZappDeptUser(String deptuserid) {
    	this.deptuserid = deptuserid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param deptid Dept. ID 
     * @param userid User ID
     */
    public ZappDeptUser(String deptid, String userid) {
    	this.deptid = deptid;
    	this.userid = userid;
    }
    /**
	 * <p><b>
	 * Additional constructor #3
	 * </b></p>
     *
     * @param deptid Dept. ID 
     * @param userid User ID
     * @param originyn Original job?
     */
    public ZappDeptUser(String deptid, String userid, String originyn) {
    	this.deptid = deptid;
    	this.userid = userid;
    	this.originyn = originyn;
    }
    
    
    /**
	 * <p><b>
	 * [OUT] Department User ID - Primary Key
	 * </b></p>
	 * 
     * @return Department User ID
     */
    public String getDeptuserid() {
        return deptuserid;
    }
    
    /**
	 * <p><b>
	 * [IN] Department User ID - Primary Key
	 * </b></p>
     *
     * @param deptuserid Department User ID
     */ 
    public void setDeptuserid(String deptuserid) {
        this.deptuserid = deptuserid == null ? null : deptuserid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dept. ID (ZAPP_DEPT - DEPTID)
	 * </b></p>
	 * 
     * @return Dept. ID
     */
    public String getDeptid() {
        return deptid;
    }
    
    /**
	 * <p><b>
	 * [IN] Dept. ID (ZAPP_DEPT - DEPTID)
	 * </b></p>
     *
     * @param deptid Dept. ID
     */ 
    public void setDeptid(String deptid) {
        this.deptid = deptid == null ? null : deptid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] User ID (ZAPP_USER - USERID)
	 * </b></p>
	 * 
     * @return User ID
     */
    public String getUserid() {
        return userid;
    }
    
    /**
	 * <p><b>
	 * [IN] User ID (ZAPP_USER - USERID)
	 * </b></p>
     *
     * @param userid User ID
     */
    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] User type (01:General, 02:Dept. manager, 03:Company manager, 04:System manager)
	 * </b></p>
	 * 
     * @return User type
     */
    public String getUsertype() {
        return usertype;
    }
    
    /**
	 * <p><b>
	 * [IN] User type (01:General, 02:Dept. manager, 03:Company manager, 04:System manager)
	 * </b></p>
     *
     * @param usertype User type
     */
    public void setUsertype(String usertype) {
        this.usertype = usertype == null ? null : usertype.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Original job? (Y:Original, N:Not original)
	 * </b></p>
	 * 
     * @return Original job?
     */
    public String getOriginyn() {
        return originyn;
    }
    
    /**
	 * <p><b>
	 * [IN] Original job? (Y:Original, N:Not original)
	 * </b></p>
     *
     * @param originyn Original job?
     */
    public void setOriginyn(String originyn) {
        this.originyn = originyn == null ? null : originyn.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Position ID (TYPES [ZAPP_CODE] is  '02')
	 * </b></p>
	 * 
     * @return Position ID
     */
    public String getPositionid() {
        return positionid;
    }
    
    /**
	 * <p><b>
	 * [IN] Position ID (TYPES [ZAPP_CODE] is  '02')
	 * </b></p>
     *
     * @param positionid Position ID
     */
    public void setPositionid(String positionid) {
        this.positionid = positionid == null ? null : positionid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Duty ID (TYPES [ZAPP_CODE] is  '03')
	 * </b></p>
	 * 
     * @return Duty ID
     */
    public String getDutyid() {
        return dutyid;
    }
    
    /**
	 * <p><b>
	 * [IN] Duty ID (TYPES [ZAPP_CODE] is  '03')
	 * </b></p>
     *
     * @param dutyid Duty ID
     */
    public void setDutyid(String dutyid) {
        this.dutyid = dutyid == null ? null : dutyid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Security level (TYPES [ZAPP_CODE] is  '04')
	 * </b></p>
	 * 
     * @return Security level
     */
    public String getSeclevelid() {
        return seclevelid;
    }
    
    /**
	 * <p><b>
	 * [IN] Security level (TYPES [ZAPP_CODE] is  '04')
	 * </b></p>
     *
     * @param seclevelid Security level
     */
    public void setSeclevelid(String seclevelid) {
        this.seclevelid = seclevelid == null ? null : seclevelid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Upper manager? (Y/N)
	 * </b></p>
	 * 
     * @return Upper manager?
     */
    public String getIssupervisor() {
		return issupervisor;
	}
    
    /**
	 * <p><b>
	 * [IN] Upper manager? (Y/N)
	 * </b></p>
     *
     * @param issupervisor Upper manager?
     */
	public void setIssupervisor(String issupervisor) {
		this.issupervisor = issupervisor;
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
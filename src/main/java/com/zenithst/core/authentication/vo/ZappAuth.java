package com.zenithst.core.authentication.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.archive.vo.ZArchTask;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappGroupUserExtend;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for authentication <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappAuth pIn = new ZappAuth();
*    ...
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappAuth extends ZstFwDomain {

	/* Company ID */
	private String objCompanyid;
	/* Task ID */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objTaskid;
	/* Dept. ID */
	private String objDeptid;
	/* User ID */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objLoginid;
	/* User employee number */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objEmpno;
	/* Password */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objPasswd;
	/* Mac */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objMac;
	/* IP */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objIp;
	/* JWT */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objJwt;
	/* Password */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objlang;
	/* 경로 (W:web, C:CS) */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objAccesspath = ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth;
	/* Debugging */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private boolean objDebugged = false;
	/* Type */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objType;
	/* Processing type */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objHandleType;
	/* Test or not */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objIsTest;
	/* Time */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objTime;
	/* Level */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objLevel;
	/* Whether access control apply or not */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean objSkipAcl = false;	
	/* Whether license is valid or not */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean objValidLic = false;	
	/* Whether skip querying class path */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objSkipCpath = ZappConts.USAGES.NO.use;	
	/* List of object */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> objList = new ArrayList<String>();	
	
	/* Company */
	private ZappCompany sessCompany;
	/* Department */
	private ZappDept sessDept;
	private List<String> sessAllLowerDepts = new ArrayList<String>(); 
	/* Department User */
	private ZappDeptUserExtend sessDeptUser;
	private ZappDeptUser sessOnlyDeptUser;
	/* Department User */
	private List<ZappDeptUserExtend> sessDeptUsers = new ArrayList<ZappDeptUserExtend>();
	private List<ZappDeptUser> sessOnlyDeptUsers = new ArrayList<ZappDeptUser>();
	/* Department User */
	private List<ZappGroupUserExtend> sessGroupUsers = new ArrayList<ZappGroupUserExtend>();
	private List<ZappGroupUser> sessOnlyGroupUsers = new ArrayList<ZappGroupUser>();
	/* User */
	private ZappUser sessUser;
	/* Access control info. */
	private List<String> sessAclObjList = new ArrayList<String>();
	/* Preferences */
	private Map<String, ZappEnv> sessEnv = new HashMap<String, ZappEnv>();
	/* Personal Preferences */
	private Map<String, ZappEnv> sessUserEnv = new HashMap<String, ZappEnv>();
	/* Task */
	private List<ZArchTask> sessTasks = new ArrayList<ZArchTask>();
	
    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
	public ZappAuth() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param objCompanyid String Company ID
     * @param objDeptid String Department ID
     * @param objLoginid String Login ID
     * @param objPasswd String Password
     */ 	
	public ZappAuth(String objCompanyid, String objDeptid, String objLoginid, String objPasswd) {
		this.objCompanyid = objCompanyid;
		this.objDeptid = objDeptid;
		this.objLoginid = objLoginid;
		this.objPasswd = objPasswd;
	}
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param sessCompany ZappCompany Company
     * @param sessDept ZappDept Department
     * @param sessDeptUser ZappDeptUserExtend Dept. User Object
     * @param sessDeptUsers List&lt;ZappDeptUserExtend&gt; Dept. User Object List
     * @param sessGroupUsers List&lt;ZappGroupUserExtend&gt; Group User Object List
     * @param sessUser ZappUser User Object
     */	
	public ZappAuth(ZappCompany sessCompany
				  , ZappDept sessDept
				  , ZappDeptUserExtend sessDeptUser
				  , List<ZappDeptUserExtend> sessDeptUsers
				  , List<ZappGroupUserExtend> sessGroupUsers
				  , ZappUser sessUser) {
		this.sessCompany = sessCompany;
		this.sessDept = sessDept;
		this.sessDeptUser = sessDeptUser;
		this.sessDeptUsers = sessDeptUsers;
		this.sessGroupUsers = sessGroupUsers;
		this.sessUser = sessUser;
	}
	
    
    /**
	 * <p><b>
	 * [OUT] Company ID
	 * </b></p>
	 * 
     * @return Company ID
     */ 
	public String getObjCompanyid() {
		return objCompanyid;
	}
    
    /**
	 * <p><b>
	 * [IN] Company ID
	 * </b></p>
     *
     * @param objCompanyid String Company ID
     */  
	public void setObjCompanyid(String objCompanyid) {
		this.objCompanyid = objCompanyid;
	}
	
	
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
     * @param objTaskid String Task ID
     */  
    public void setObjTaskid(String objTaskid) {
		this.objTaskid = objTaskid;
	}
	/**
	 * <p><b>
	 * [OUT] Department ID
	 * </b></p>
	 * 
     * @return Department ID
     */ 
	public String getObjDeptid() {
		return objDeptid;
	}
    
    /**
	 * <p><b>
	 * [IN] Department ID
	 * </b></p>
     *
     * @param objDeptid String Department ID
     */
	public void setObjDeptid(String objDeptid) {
		this.objDeptid = objDeptid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Login ID
	 * </b></p>
	 * 
     * @return Login ID
     */ 	
	public String getObjLoginid() {
		return objLoginid;
	}
    
    /**
	 * <p><b>
	 * [IN] Login ID
	 * </b></p>
     *
     * @param objLoginid String Login ID
     */
	public void setObjLoginid(String objLoginid) {
		this.objLoginid = objLoginid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Employee number
	 * </b></p>
	 * 
     * @return Employee number
     */ 	
	public String getObjEmpno() {
		return objEmpno;
	}
    
    /**
	 * <p><b>
	 * [IN] Employee number
	 * </b></p>
     *
     * @param objEmpno String Employee number
     */	
	public void setObjEmpno(String objEmpno) {
		this.objEmpno = objEmpno;
	}
    
    /**
	 * <p><b>
	 * [OUT] JWT
	 * </b></p>
	 * 
     * @return JWT
     */ 	
	public String getObjJwt() {
		return objJwt;
	}
    
    /**
	 * <p><b>
	 * [IN] JWT
	 * </b></p>
     *
     * @param objJwt String JWT
     */	
	public void setObjJwt(String objJwt) {
		this.objJwt = objJwt;
	}
    
    /**
	 * <p><b>
	 * [OUT] Password
	 * </b></p>
	 * 
     * @return Password
     */ 	
	public String getObjPasswd() {
		return objPasswd;
	}
    
    /**
	 * <p><b>
	 * [IN] Password
	 * </b></p>
     *
     * @param objPasswd String Password
     */	
	public void setObjPasswd(String objPasswd) {
		this.objPasswd = objPasswd;
	}
	
	/**
	 * <p><b>
	 * [OUT] Mac. Address 
	 * </b></p>
	 * 
     * @return Mac. Address 
     */ 	
    public String getObjMac() {
		return objMac;
	}

    /**
	 * <p><b>
	 * [IN] Mac. Address  
	 * </b></p>
     *
     * @param objMac String Mac. Address  
     */	
    public void setObjMac(String objMac) {
		this.objMac = objMac;
	}

	/**
	 * <p><b>
	 * [OUT] Ip Address 
	 * </b></p>
	 * 
     * @return Ip Address 
     */ 	
	public String getObjIp() {
		return objIp;
	}

    /**
	 * <p><b>
	 * [IN] Ip Address  
	 * </b></p>
     *
     * @param objIp String Ip Address  
     */	
	public void setObjIp(String objIp) {
		this.objIp = objIp;
	}
	/**
	 * <p><b>
	 * [OUT] Company 
	 * </b></p>
	 * 
     * @return Company 
     */ 	
	public ZappCompany getSessCompany() {
		return sessCompany;
	}
    
    /**
	 * <p><b>
	 * [IN] Company 
	 * </b></p>
     *
     * @param sessCompany ZappCompany Company 
     */	
	public void setSessCompany(ZappCompany sessCompany) {
		this.sessCompany = sessCompany;
	}
    
    /**
	 * <p><b>
	 * [OUT] Department 
	 * </b></p>
	 * 
     * @return Department 
     */	
	public ZappDept getSessDept() {
		return sessDept;
	}
    
    /**
	 * <p><b>
	 * [IN] Department 
	 * </b></p>
     *
     * @param sessDept ZappDept Department 
     */	
	public void setSessDept(ZappDept sessDept) {
		this.sessDept = sessDept;
	}
	
    /**
	 * <p><b>
	 * [OUT] All lower Departments 
	 * </b></p>
	 * 
     * @return List of Department 
     */	
    public List<String> getSessAllLowerDepts() {
		return sessAllLowerDepts;
	}
    
    /**
	 * <p><b>
	 * [IN] All lower Departments  
	 * </b></p>
     *
     * @param sessAllLowerDepts List of Department 
     */	
	public void setSessAllLowerDepts(List<String> sessAllLowerDepts) {
		this.sessAllLowerDepts = sessAllLowerDepts;
	}
	/**
	 * <p><b>
	 * [OUT] Department User (Extended info.)
	 * </b></p>
	 * 
     * @return Department User  (Extended info.)
     */		
	public ZappDeptUserExtend getSessDeptUser() {
		return sessDeptUser;
	}
    
    /**
	 * <p><b>
	 * [IN] Department User (Extended info.)
	 * </b></p>
     *
     * @param sessDeptUser ZappDeptUserExtend Department User (Extended info.)
     */
	public void setSessDeptUser(ZappDeptUserExtend sessDeptUser) {
		this.sessDeptUser = sessDeptUser;
	}
    
    /**
	 * <p><b>
	 * [OUT] List of Department User
	 * </b></p>
	 * 
     * @return List of Department User
     */		
	public List<ZappDeptUserExtend> getSessDeptUsers() {
		return sessDeptUsers;
	}
    
    /**
	 * <p><b>
	 * [IN] List of Department User
	 * </b></p>
     *
     * @param sessDeptUsers List&lt;ZappDeptUserExtend&gt; List of Department User
     */
	public void setSessDeptUsers(List<ZappDeptUserExtend> sessDeptUsers) {
		this.sessDeptUsers = sessDeptUsers;
	}
    
    /**
	 * <p><b>
	 * [OUT] Group User Object List
	 * </b></p>
	 * 
     * @return Group User Object List
     */			
	public List<ZappGroupUserExtend> getSessGroupUsers() {
		return sessGroupUsers;
	}
    
    /**
	 * <p><b>
	 * [IN] Group User Object List
	 * </b></p>
     *
     * @param sessGroupUsers List&lt;ZappGroupUserExtend&gt; Group User Object List
     */
	public void setSessGroupUsers(List<ZappGroupUserExtend> sessGroupUsers) {
		this.sessGroupUsers = sessGroupUsers;
	}
    
    /**
	 * <p><b>
	 * [OUT] User Object
	 * </b></p>
	 * 
     * @return User Object
     */	
	public ZappUser getSessUser() {
		return sessUser;
	}
    
    /**
	 * <p><b>
	 * [IN] User Object
	 * </b></p>
     *
     * @param sessUser ZappUser User Object
     */
	public void setSessUser(ZappUser sessUser) {
		this.sessUser = sessUser;
	}
    
    /**
	 * <p><b>
	 * [OUT] Preferences Object List
	 * </b></p>
	 * 
     * @return Preferences Object List
     */	
	public Map<String, ZappEnv> getSessEnv() {
		return sessEnv;
	}
    
    /**
	 * <p><b>
	 * [IN] Preferences Object Map
	 * </b></p>
     *
     * @param sessEnv Map&lt;String, ZappEnv&gt; Preferences Object Map
     */
	public void setSessEnv(Map<String, ZappEnv> sessEnv) {
		this.sessEnv = sessEnv;
	}
	
    /**
	 * <p><b>
	 * [OUT] Personal Preferences Object List
	 * </b></p>
	 * 
     * @return Preferences Object List
     */	
    public Map<String, ZappEnv> getSessUserEnv() {
		return sessUserEnv;
	}

    /**
	 * <p><b>
	 * [IN] Personal Preferences Object Map
	 * </b></p>
     *
     * @param sessEnv Map&lt;String, ZappEnv&gt; Preferences Object Map
     */
   public void setSessUserEnv(Map<String, ZappEnv> sessUserEnv) {
		this.sessUserEnv = sessUserEnv;
	}
	/**
	 * <p><b>
	 * [OUT] ACL List
	 * </b></p>
	 * 
     * @return ACL List
     */		
	public List<String> getSessAclObjList() {
		return sessAclObjList;
	}
    
    /**
	 * <p><b>
	 * [IN] ACL List
	 * </b></p>
     *
     * @param sessAclObjList List&lt;String&gt; ACL List
     */
	public void setSessAclObjList(List<String> sessAclObjList) {
		this.sessAclObjList = sessAclObjList;
	}
    
    /**
	 * <p><b>
	 * [OUT] Access path (W:web, C:CS)
	 * </b></p>
	 * 
     * @return Access path
     */	
	public String getObjAccesspath() {
		return objAccesspath;
	}
    
    /**
	 * <p><b>
	 * [IN] Access path (W:web, C:CS)
	 * </b></p>
     *
     * @param objAccesspath String Access path
     */
	public void setObjAccesspath(String objAccesspath) {
		this.objAccesspath = objAccesspath;
	}
    
    /**
	 * <p><b>
	 * [OUT] Language (Korean:ko, English:en)
	 * </b></p>
	 * 
     * @return Language
     */		
	public String getObjlang() {
		return objlang;
	}
    
    /**
	 * <p><b>
	 * [IN] Language (Korean:ko, English:en)
	 * </b></p>
     *
     * @param objlang String Language
     */
	public void setObjlang(String objlang) {
		this.objlang = objlang;
	}
    
    /**
	 * <p><b>
	 * [OUT] Debug or not
	 * </b></p>
	 * 
     * @return Debug or not
     */	
	public boolean getObjDebugged() {
		return objDebugged;
	}
    
    /**
	 * <p><b>
	 * [IN] Debug or not
	 * </b></p>
     *
     * @param objDebugged boolean Debug or not
     */
	public void setObjDebugged(boolean objDebugged) {
		this.objDebugged = objDebugged;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target type
	 * </b></p>
	 * 
     * @return Target type
     */	
	public String getObjType() {
		return objType;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type
	 * </b></p>
     *
     * @param objType String Target type
     */
	public void setObjType(String objType) {
		this.objType = objType;
	}
    
    /**
	 * <p><b>
	 * [OUT] Processing type
	 * </b></p>
	 * 
     * @return Processing type
     */		
	public String getObjHandleType() {
		return objHandleType;
	}
    
    /**
	 * <p><b>
	 * [IN] Processing type
	 * </b></p>
     *
     * @param objHandleType String Processing type
     */
	public void setObjHandleType(String objHandleType) {
		this.objHandleType = objHandleType;
	}
    
    /**
	 * <p><b>
	 * [OUT] Test or not (Y/N)
	 * </b></p>
	 * 
     * @return Test or not
     */		
	public String getObjIsTest() {
		return objIsTest;
	}
    
    /**
	 * <p><b>
	 * [IN] Test or not (Y/N)
	 * </b></p>
     *
     * @param objIsTest String Test or not
     */
	public void setObjIsTest(String objIsTest) {
		this.objIsTest = objIsTest;
	}
	
    
    /**
	 * <p><b>
	 * [OUT] Time
	 * </b></p>
	 * 
     * @return Time
     */	
	public String getObjTime() {
		return objTime;
	}
    
    /**
	 * <p><b>
	 * [IN] Time
	 * </b></p>
     *
     * @param objTime String Time
     */
	public void setObjTime(String objTime) {
		this.objTime = objTime;
	}

    /**
	 * <p><b>
	 * [OUT] Level
	 * </b></p>
	 * 
     * @return Level
     */
	public String getObjLevel() {
		return objLevel;
	}
    
    /**
	 * <p><b>
	 * [IN] Level
	 * </b></p>
     *
     * @param objLevel String Level
     */
	public void setObjLevel(String objLevel) {
		this.objLevel = objLevel;
	}

    /**
	 * <p><b>
	 * [OUT] Whether access control apply or not
	 * </b></p>
	 * 
     * @return Whether access control apply or not
     */    
	public boolean getObjSkipAcl() {
		return objSkipAcl;
	}
    
    /**
	 * <p><b>
	 * [IN] Whether access control apply or not
	 * </b></p>
     *
     * @param objSkipAcl boolean Whether access control apply or not
     */
	public void setObjSkipAcl(boolean objSkipAcl) {
		this.objSkipAcl = objSkipAcl;
	}
	
	/**
	 * <p><b>
	 * [OUT] Whether license is valid or not
	 * </b></p>
	 * 
     * @return Whether license is valid or not
     */		
	public boolean getObjValidLic() {
		return objValidLic;
	}
    
    /**
	 * <p><b>
	 * [IN] Whether license is valid or not
	 * </b></p>
     *
     * @param objSkipAcl boolean Whether license is valid or not
     */
	public void setObjValidLic(boolean objValidLic) {
		this.objValidLic = objValidLic;
	}
	
	/**
	 * <p><b>
	 * [OUT] Whether skip querying class path
	 * </b></p>
	 * 
     * @return Whether skip querying class path
     */		
	public String getObjSkipCpath() {
		return objSkipCpath;
	}
	
    public List<String> getObjList() {
		return objList;
	}

    public void setObjList(List<String> objList) {
		this.objList = objList;
	}

    /**
	 * <p><b>
	 * [IN] Whether skip querying class path
	 * </b></p>
     *
     * @param objSkipCpath String Whether skip querying class path
     */
	public void setObjSkipCpath(String objSkipCpath) {
		this.objSkipCpath = objSkipCpath;
	}
	
	/**
	 * <p><b>
	 * [OUT] Dept. User Only
	 * </b></p>
	 * 
     * @return Dept. User Only
     */	
	public ZappDeptUser getSessOnlyDeptUser() {
		return sessOnlyDeptUser;
	}
    
    /**
	 * <p><b>
	 * [IN] Dept. User Only
	 * </b></p>
     *
     * @param sessOnlyDeptUser ZappDeptUser Dept. User Only
     */
	public void setSessOnlyDeptUser(ZappDeptUser sessOnlyDeptUser) {
		this.sessOnlyDeptUser = sessOnlyDeptUser;
	}
    
    /**
	 * <p><b>
	 * [OUT] Dept. User List  Only
	 * </b></p>
	 * 
     * @return Dept. User List  Only
     */		
	public List<ZappDeptUser> getSessOnlyDeptUsers() {
		return sessOnlyDeptUsers;
	}
    
    /**
	 * <p><b>
	 * [IN] Dept. User List Only
	 * </b></p>
     *
     * @param sessOnlyDeptUsers List&lt;ZappDeptUser&gt; Dept. User List Only
     */
	public void setSessOnlyDeptUsers(List<ZappDeptUser> sessOnlyDeptUsers) {
		this.sessOnlyDeptUsers = sessOnlyDeptUsers;
	}
    
    /**
	 * <p><b>
	 * [OUT] Group User List  Only
	 * </b></p>
	 * 
     * @return Group User List  Only
     */	
	public List<ZappGroupUser> getSessOnlyGroupUsers() {
		return sessOnlyGroupUsers;
	}
    
    /**
	 * <p><b>
	 * [IN] Group User List Only
	 * </b></p>
     *
     * @param sessOnlyGroupUsers List&lt;ZappGroupUser&gt; Group User List Only
     */
	public void setSessOnlyGroupUsers(List<ZappGroupUser> sessOnlyGroupUsers) {
		this.sessOnlyGroupUsers = sessOnlyGroupUsers;
	}	
	
    /**
	 * <p><b>
	 * [OUT] Task List
	 * </b></p>
	 * 
     * @return Task List
     */		
	public List<ZArchTask> getSessTasks() {
		return sessTasks;
	}
	
    /**
	 * <p><b>
	 * [IN] Task List
	 * </b></p>
     *
     * @param sessTasks List&lt;ZArchTask&gt; Task List
     */
	public void setSessTasks(List<ZArchTask> sessTasks) {
		this.sessTasks = sessTasks;
	}	
	
	/* ************************************************************* */

	
	/**
	 * <p><b>
	 * Target type 아이디를 조회용으로 조합한다.<br>
	 * (세션정보에 대상 유형정보를 이용하여 조건을 자동으로 생성해준다.)
	 * </b></p>
	 * @return String - 조합된 Target ID <br>
	 */
	public String cvrtAclToString() {
		StringBuffer sb = new StringBuffer(); 
		if(this.sessAclObjList != null) {
			for(String str : this.sessAclObjList) {
				sb.append(str + ZappConts.SCHARS.DIVIDER.character);
			}
		}
		return sb.toString();
	}
}

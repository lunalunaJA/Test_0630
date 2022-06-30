package com.zenithst.core.organ.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for user <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappUser pIn = new ZappUser();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_USER</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>userid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+8)</td>
* 	 </tr> 	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>companyid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>empno</td><td></td><td></td><td></td><td>VARCHAR(30)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>loginid</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>name</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>passwd</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>passwdsalt</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>email</td><td></td><td></td><td></td><td>VARCHAR(100)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>maclimit</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>iplimit</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>11</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappUser extends ZappDomain {

	private static final long serialVersionUID = 1L;
	
	private String userid;			// User ID (HASH - Company ID + Employee number)
    private String companyid;		// Company ID
    private String empno;			// Employee number
    private String loginid;			// Login ID
    private String name;			// User name
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwd;			// Password
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwdsalt;		// Password salt
    private String email;			// E-mail
    private String maclimit;		// User Mac Address
    private String iplimit;			// User IP Address
    private String isactive;		// Use or not (Y/N)

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappUser() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param userid User ID
     */
    public ZappUser(String userid) {
    	this.userid = userid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param companyid Company ID
     * @param empno Employee number
     * @param loginid Login ID
     * @param passwd Password
     */
    public ZappUser(String companyid, String empno, String loginid, String passwd) {
    	this.companyid = companyid;
    	this.empno = empno;
    	this.loginid = loginid;
    	this.passwd = passwd;
    }
    
    /**
	 * <p><b>
	 * [OUT] User ID - Primary Key
	 * </b></p>
	 * 
     * @return User ID
     */
    public String getUserid() {
        return userid;
    }
	
    /**
	 * <p><b>
	 * [IN] User ID - Primary Key
	 * </b></p>
     *
     * @param userid User ID
     */
    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
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
	 * [OUT] Employee number
	 * </b></p>
	 * 
     * @return Employee number
     */
    public String getEmpno() {
        return empno;
    }

    /**
	 * <p><b>
	 * [IN] Employee number
	 * </b></p>
     *
     * @param empno Employee number
     */
    public void setEmpno(String empno) {
        this.empno = empno == null ? null : empno.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Login ID
	 * </b></p>
	 * 
     * @return Login ID
     */
    public String getLoginid() {
        return loginid;
    }

    /**
	 * <p><b>
	 * [IN] Login ID
	 * </b></p>
     *
     * @param loginid Login ID
     */
    public void setLoginid(String loginid) {
        this.loginid = loginid == null ? null : loginid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] User name
	 * </b></p>
	 * 
     * @return User name
     */
    public String getName() {
        return name;
    }

    /**
	 * <p><b>
	 * [IN] User name
	 * </b></p>
     *
     * @param name User name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Password
	 * </b></p>
	 * 
     * @return Password
     */
    public String getPasswd() {
        return passwd;
    }

    /**
	 * <p><b>
	 * [IN] Password
	 * </b></p>
     *
     * @param passwd Password
     */
    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Salt for password
	 * </b></p>
	 * 
     * @return Salt for password
     */
    public String getPasswdsalt() {
        return passwdsalt;
    }

    /**
	 * <p><b>
	 * [IN] Salt for password
	 * </b></p>
     *
     * @param passwdsalt Salt for password
     */
    public void setPasswdsalt(String passwdsalt) {
        this.passwdsalt = passwdsalt == null ? null : passwdsalt.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] E-mail
	 * </b></p>
	 * 
     * @return E-mail
     */
    public String getEmail() {
        return email;
    }

    /**
	 * <p><b>
	 * [IN] E-mail
	 * </b></p>
     *
     * @param email E-mail
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }
    
	/**
	 * <p><b>
	 * [OUT] Mac Address Limit
	 * </b></p>
	 * 
     * @return Mac Address Limit
     */
    public String getMaclimit() {
		return maclimit;
	}

    /**
	 * <p><b>
	 * [IN] Mac Address Limit
	 * </b></p>
     *
     * @param maclimit Mac Address Limit
     */    
	public void setMaclimit(String maclimit) {
		this.maclimit = maclimit;
	}
	
	/**
	 * <p><b>
	 * [OUT] IP Address Limit
	 * </b></p>
	 * 
     * @return IP Address Limit
     */	
	public String getIplimit() {
		return iplimit;
	}

    /**
	 * <p><b>
	 * [IN] IP Address Limit
	 * </b></p>
     *
     * @param iplimit IP Address Limit
     */	
	public void setIplimit(String iplimit) {
		this.iplimit = iplimit;
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
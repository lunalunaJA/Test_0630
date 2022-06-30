package com.zenithst.core.system.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for preferences <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappEnv pIn = new ZappEnv();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CODE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>envid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+5+9)</td><td>(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>companyid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Company ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>name</td><td></td><td></td><td></td><td>VARCHAR(150)</td><td></td><td>Code name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>setval</td><td></td><td></td><td></td><td>VARCHAR(30)</td><td></td><td>Value</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>envtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Type</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>settype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Selection type (1:Key-in, 2:Select)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>setopt</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Selection option (settype = 2)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>editable</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Edit or not(Y/N)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>envkey</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Preferences Key (for use in the program)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Use or not(Y/N)</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappEnv extends ZappDomain {

	/* */
    private String envid;
    private String companyid;
    private String userid;
    private String name;
    private String setval;
    private String envtype;
    private String settype;
    private String setopt;
    private String editable;
    private String envkey;
    private String isactive;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappEnv() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param envid Preferences ID
     */    
    public ZappEnv(String envid) {
    	this.envid = envid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param companyid Company ID
     * @param isactive Use or not
     */
    public ZappEnv(String companyid, String isactive) {
    	this.companyid = companyid;
    	this.isactive = isactive;
    }

    
    /**
	 * <p><b>
	 * [OUT] Preferences ID - Primary Key
	 * </b></p>
	 * 
     * @return Preferences ID
     */
    public String getEnvid() {
        return envid;
    }
    public void setEnvid(String envid) {
        this.envid = envid == null ? null : envid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Company ID
	 * </b></p>
	 * 
     * @return Company ID
     */     
    public String getCompanyid() {
        return companyid;
    }
    
    /**
	 * <p><b>
	 * [IN] Company ID
	 * </b></p>
     *
     * @param companyid Company ID
     */
    public void setCompanyid(String companyid) {
        this.companyid = companyid == null ? null : companyid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] User ID
	 * </b></p>
	 * 
     * @return User ID
     */     
    public String getUserid() {
		return userid;
	}

    /**
	 * <p><b>
	 * [IN] User ID
	 * </b></p>
     *
     * @param userid User ID
     */
    public void setUserid(String userid) {
		this.userid = userid;
	}
	/**
	 * <p><b>
	 * [OUT] Preferences Name
	 * </b></p>
	 * 
     * @return Preferences Name
     */     
    public String getName() {
        return name;
    }
    
    /**
	 * <p><b>
	 * [IN] Preferences Name
	 * </b></p>
     *
     * @param name Preferences Name
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Value
	 * </b></p>
	 * 
     * @return Value
     */    
    public String getSetval() {
        return setval;
    }
    
    /**
	 * <p><b>
	 * [IN] Value
	 * </b></p>
     *
     * @param setval Value
     */
    public void setSetval(String setval) {
        this.setval = setval == null ? null : setval.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Preferences Type
	 * </b></p>
	 * 
     * @return Preferences Type
     */    
    public String getEnvtype() {
        return envtype;
    }
    
    /**
	 * <p><b>
	 * [IN] Preferences Type
	 * </b></p>
     *
     * @param envtype Preferences Type
     */
    public void setEnvtype(String envtype) {
        this.envtype = envtype == null ? null : envtype.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Preferences Set type
	 * </b></p>
	 * 
     * @return Preferences Set type
     */    
    public String getSettype() {
        return settype;
    }
    
    /**
	 * <p><b>
	 * [IN] Preferences Set type
	 * </b></p>
     *
     * @param settype Preferences Set type
     */
    public void setSettype(String settype) {
        this.settype = settype == null ? null : settype.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Preferences Set option
	 * </b></p>
	 * 
     * @return Preferences Set option
     */       
    public String getSetopt() {
        return setopt;
    }
    
    /**
	 * <p><b>
	 * [IN] Preferences Set option
	 * </b></p>
     *
     * @param setopt Preferences Set option
     */
    public void setSetopt(String setopt) {
        this.setopt = setopt == null ? null : setopt.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Preferences Edit or not (Y/N)
	 * </b></p>
	 * 
     * @return Preferences Edit or not
     */    
    public String getEditable() {
        return editable;
    }
    
    /**
	 * <p><b>
	 * [IN] Preferences Edit or not (Y/N)
	 * </b></p>
     *
     * @param setopt Preferences Edit or not
     */
    public void setEditable(String editable) {
        this.editable = editable == null ? null : editable.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Preferences Key
	 * </b></p>
	 * 
     * @return Preferences Key
     */      
    public String getEnvkey() {
        return envkey;
    }
    
    /**
	 * <p><b>
	 * [IN] Preferences Key
	 * </b></p>
     *
     * @param envkey Preferences Key
     */
    public void setEnvkey(String envkey) {
        this.envkey = envkey;
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
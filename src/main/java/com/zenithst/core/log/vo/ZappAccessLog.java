package com.zenithst.core.log.vo;

import java.util.HashMap;
import java.util.Map;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for access log <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappContentLog pIn = new ZappContentLog();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_ACCESSLOG</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>logid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(ALL)</td><td>(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>companyid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Company ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>logobjid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Logging target ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>logtext</td><td></td><td></td><td></td><td>VARCHAR(150)</td><td></td><td>Name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>loggerid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Logger ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>loggername</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Logger name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>loggerdeptid</td><td></td><td></td><td></td><td>VARCHAR(30)</td><td></td><td>Logger dept. ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>loggerdeptname</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Logger dept. name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>logtime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Logging time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>logtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Logging type(01:Authentication)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>11</td><td>action</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Processing type<br>(01:Connect,02:Disconnect,03:Failed after trying to connect)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>12</td><td>logs</td><td></td><td></td><td></td><td>TEXT</td><td></td><td>Logging Info.</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/


public class ZappAccessLog extends ZappDomain {
 
	private String logid;
    private String companyid;
    private String logobjid;
    private String loggerid;
    private String loggername;
    private String loggerdeptid;
    private String loggerdeptname;
    private String logtime;
    private String logtype;
    private String logip;
    private String action;
    private String logs;
    private Map<String, Object> maplogs = new HashMap<String, Object>();	// Logging info. (Multiple)

    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getLogid() {
        return logid;
    }
	
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param logid Primary Key
     */
    public void setLogid(String logid) {
        this.logid = logid == null ? null : logid.trim();
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
	 * [OUT] Logging target ID
	 * </b></p>
	 * 
     * @return Logging target ID
     */  
    public String getLogobjid() {
		return logobjid;
	}
	
    /**
	 * <p><b>
	 * [IN] Logging target ID
	 * </b></p>
     *
     * @param logobjid Logging target ID
     */
	public void setLogobjid(String logobjid) {
		this.logobjid = logobjid;
	}

	/**
	 * <p><b>
	 * [OUT] Logger ID
	 * </b></p>
	 * 
     * @return Logger ID
     */	
	public String getLoggerid() {
        return loggerid;
    }
	
    /**
	 * <p><b>
	 * [IN] Logger ID
	 * </b></p>
     *
     * @param loggerid Logger ID
     */
    public void setLoggerid(String loggerid) {
        this.loggerid = loggerid == null ? null : loggerid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Logger name
	 * </b></p>
	 * 
     * @return Logger name
     */	
    public String getLoggername() {
        return loggername;
    }
	
    /**
	 * <p><b>
	 * [IN] Logger name
	 * </b></p>
     *
     * @param loggername Logger name
     */
    public void setLoggername(String loggername) {
        this.loggername = loggername == null ? null : loggername.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Logger dept. ID
	 * </b></p>
	 * 
     * @return Logger dept. ID
     */	
    public String getLoggerdeptid() {
        return loggerdeptid;
    }
	
    /**
	 * <p><b>
	 * [IN] Logger dept. ID
	 * </b></p>
     *
     * @param loggerdeptid Logger dept. ID
     */
    public void setLoggerdeptid(String loggerdeptid) {
        this.loggerdeptid = loggerdeptid == null ? null : loggerdeptid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Logger dept. name
	 * </b></p>
	 * 
     * @return Logger dept. name
     */	
    public String getLoggerdeptname() {
        return loggerdeptname;
    }
	
    /**
	 * <p><b>
	 * [IN] Logger dept. name
	 * </b></p>
     *
     * @param loggerdeptname Logger dept. name
     */
    public void setLoggerdeptname(String loggerdeptname) {
        this.loggerdeptname = loggerdeptname == null ? null : loggerdeptname.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Logging time
	 * </b></p>
	 * 
     * @return Logging time
     */	
    public String getLogtime() {
        return logtime;
    }
	
    /**
	 * <p><b>
	 * [IN] Logging time
	 * </b></p>
     *
     * @param loggerdeptname Logging time
     */
    public void setLogtime(String logtime) {
        this.logtime = logtime == null ? null : logtime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Logging type
	 * </b></p>
	 * 
     * @return Logging type
     */	
    public String getLogtype() {
        return logtype;
    }
	
    /**
	 * <p><b>
	 * [IN] Logging type
	 * </b></p>
     *
     * @param loggerdeptname Logging type
     */
    public void setLogtype(String logtype) {
        this.logtype = logtype == null ? null : logtype.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Log IP address
	 * </b></p>
	 * 
     * @return Log IP address
     */	
    public String getLogip() {
		return logip;
	}
	
    /**
	 * <p><b>
	 * [IN] Log IP address
	 * </b></p>
     *
     * @param logip Log IP address
     */
	public void setLogip(String logip) {
		this.logip = logip;
	}

	/**
	 * <p><b>
	 * [OUT] Processing type
	 * </b></p>
	 * 
     * @return Processing type
     */	
	public String getAction() {
		return action;
	}
	
    /**
	 * <p><b>
	 * [IN] Processing type
	 * </b></p>
     *
     * @param action Processing type
     */
	public void setAction(String action) {
		this.action = action;
	}    

    
    /**
	 * <p><b>
	 * [OUT] Logging info.
	 * </b></p>
	 * 
     * @return Logging info.
     */	
    public String getLogs() {
        return logs;
    }
	
    /**
	 * <p><b>
	 * [IN] Logging info.
	 * </b></p>
     *
     * @param logs Logging info.
     */
    public void setLogs(String logs) {
        this.logs = logs == null ? null : logs.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Logging info. map
	 * </b></p>
	 * 
     * @return Logging info. map
     */	
 	public Map<String, Object> getMaplogs() {
		return maplogs;
	}
	
    /**
	 * <p><b>
	 * [IN] Logging info. map
	 * </b></p>
     *
     * @param maplogs Logging info. map
     */
	public void setMaplogs(Map<String, Object> maplogs) {
		this.maplogs = maplogs;
	}

    
	/**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
	public ZappAccessLog() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param logid pk
     */ 
	public ZappAccessLog(String logid) {
		this.logid = logid;
	}
	
}
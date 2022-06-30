package com.zenithst.core.log.vo;

import java.util.HashMap;
import java.util.Map;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for log (OUT) <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappLog pIn = new ZappLog();
*    ...
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappLog extends ZappDomain {

    private String logid;
    private String companyid;
    private String logtext;
    private String loggerid;
    private String loggername;
    private String loggerdeptid;
    private String loggerdeptname;
    private String logtime;
    private String logtype;
    private String action;
    private String logs;
    private Map<String, Object> maplogs = new HashMap<String, Object>();	// Logging info. (Multiple)

    public String getLogid() {
        return logid;
    }
    public void setLogid(String logid) {
        this.logid = logid == null ? null : logid.trim();
    }
    public String getCompanyid() {
        return companyid;
    }
    public void setCompanyid(String companyid) {
        this.companyid = companyid == null ? null : companyid.trim();
    }
    public String getLogtext() {
		return logtext;
	}
	public void setLogtext(String logtext) {
		this.logtext = logtext;
	}
	public String getLoggerid() {
        return loggerid;
    }
    public void setLoggerid(String loggerid) {
        this.loggerid = loggerid == null ? null : loggerid.trim();
    }
    public String getLoggername() {
        return loggername;
    }
    public void setLoggername(String loggername) {
        this.loggername = loggername == null ? null : loggername.trim();
    }
    public String getLoggerdeptid() {
        return loggerdeptid;
    }
    public void setLoggerdeptid(String loggerdeptid) {
        this.loggerdeptid = loggerdeptid == null ? null : loggerdeptid.trim();
    }
    public String getLoggerdeptname() {
        return loggerdeptname;
    }
    public void setLoggerdeptname(String loggerdeptname) {
        this.loggerdeptname = loggerdeptname == null ? null : loggerdeptname.trim();
    }
    public String getLogtime() {
        return logtime;
    }
    public void setLogtime(String logtime) {
        this.logtime = logtime == null ? null : logtime.trim();
    }
    public String getLogtype() {
        return logtype;
    }
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}    
    public void setLogtype(String logtype) {
        this.logtype = logtype == null ? null : logtype.trim();
    }
    public String getLogs() {
        return logs;
    }
    public void setLogs(String logs) {
        this.logs = logs == null ? null : logs.trim();
    }
 	public Map<String, Object> getMaplogs() {
		return maplogs;
	}
	public void setMaplogs(Map<String, Object> maplogs) {
		this.maplogs = maplogs;
	}

    
}

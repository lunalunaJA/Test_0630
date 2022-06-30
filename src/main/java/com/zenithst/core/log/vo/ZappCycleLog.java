package com.zenithst.core.log.vo;

import java.util.HashMap;
import java.util.Map;

import com.zenithst.core.common.extend.ZappDomain;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for cycle log <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappCycleLog pIn = new ZappCycleLog();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CYCLELOG</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>cycleid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(ALL)</td><td>(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>cycletime</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Exe. time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>cycletype</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Type<br>(01:Change holder, 02:Change dept., 11:Discard expired content)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>12</td><td>cyclelogs</td><td></td><td></td><td></td><td>TEXT</td><td>●</td><td>content</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappCycleLog extends ZappDomain {
    

    private String cycleid;
    private String companyid;
    private String cycletime;
    private String cycletype;
    private String cyclelogs;
    private Map<String, Object> mapcyclelogs = new HashMap<String, Object>();	// Logging info. (Multiple)
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getCycleid() {
        return cycleid;
    }
	
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param cycleid Primary Key
     */
    public void setCycleid(String cycleid) {
        this.cycleid = cycleid == null ? null : cycleid.trim();
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
	 * [OUT] Exe. time
	 * </b></p>
	 * 
     * @return Exe. time
     */
    public String getCycletime() {
        return cycletime;
    }
	
    /**
	 * <p><b>
	 * [IN] Exe. time
	 * </b></p>
     *
     * @param cycletime Exe. time
     */
    public void setCycletime(String cycletime) {
        this.cycletime = cycletime == null ? null : cycletime.trim();
    }
    
	/**
	 * <p><b>
	 * [OUT] Type
	 * </b></p>
	 * 
	 * @return Type
	 */  
	public String getCycletype() {
		return cycletype;
	}
	
	/**
	 * <p><b>
	 * [IN] Type
	 * </b></p>
	 *
	 * @param cycletype Type
	 */
	public void setCycletype(String cycletype) {
		this.cycletype = cycletype;
	}
    
    /**
	 * <p><b>
	 * [OUT] Logging info.
	 * </b></p>
	 * 
     * @return Logging info.
     */	
    public String getCyclelogs() {
        return cyclelogs;
    }
	
    /**
	 * <p><b>
	 * [IN] Logging info.
	 * </b></p>
     *
     * @param cyclelogs Logging info.
     */
    public void setCyclelogs(String cyclelogs) {
        this.cyclelogs = cyclelogs == null ? null : cyclelogs.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Logging info. map
	 * </b></p>
	 * 
     * @return Logging info. map
     */	
 	public Map<String, Object> getMapcyclelogs() {
		return mapcyclelogs;
	}
	
    /**
	 * <p><b>
	 * [IN] Logging info. map
	 * </b></p>
     *
     * @param mapcyclelogs Logging info. map
     */
	public void setMapcyclelogs(Map<String, Object> mapcyclelogs) {
		this.mapcyclelogs = mapcyclelogs;
	}

	
	/**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
	public ZappCycleLog() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param cycleid pk
     */ 
	public ZappCycleLog(String cycleid) {
		this.cycleid = cycleid;
	} 
	
}
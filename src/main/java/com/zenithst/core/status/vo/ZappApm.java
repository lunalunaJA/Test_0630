package com.zenithst.core.status.vo;

import java.util.ArrayList;
import java.util.List;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for APM <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappApm pIn = new ZappApm();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_APM</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>apmid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(ALL)</td><td>(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>apm</td><td></td><td></td><td></td><td>TEXT</td><td></td><td>APM Content</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>apmtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>APM Type (01:DB, 02:DB Lock, 03:OS, 04:DISK, 05:Check)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>apmtime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>APM Time</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappApm extends ZappDomain {

    private String apmid;
    private String apmtype;
    private String apmtime;
    private String apm;
    private String apmmacadd;
    
    private List<String> objDbList = new ArrayList<String>(); 

    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getApmid() {
        return apmid;
    }
	
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param apmid Primary Key
     */    
    public void setApmid(String apmid) {
        this.apmid = apmid == null ? null : apmid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] APM Type
	 * </b></p>
	 * 
     * @return APM Type
     */
    public String getApmtype() {
        return apmtype;
    }
	
    /**
	 * <p><b>
	 * [IN] APM Type
	 * </b></p>
     *
     * @param apmtype APM Type
     */  
    public void setApmtype(String apmtype) {
        this.apmtype = apmtype == null ? null : apmtype.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] APM Time
	 * </b></p>
	 * 
     * @return APM Time
     */
    public String getApmtime() {
        return apmtime;
    }
	
    /**
	 * <p><b>
	 * [IN] APM Time
	 * </b></p>
     *
     * @param apmtime APM Time
     */     
    public void setApmtime(String apmtime) {
        this.apmtime = apmtime == null ? null : apmtime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] APM Content
	 * </b></p>
	 * 
     * @return APM Content
     */
    public String getApm() {
        return apm;
    }
	
    /**
	 * <p><b>
	 * [IN] APM Content
	 * </b></p>
     *
     * @param apm APM Content
     */  
    public void setApm(String apm) {
        this.apm = apm == null ? null : apm.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] List of DB
	 * </b></p>
	 * 
     * @return List of DB
     */    
    public List<String> getObjDbList() {
		return objDbList;
	}
	
    /**
	 * <p><b>
	 * [IN] List of DB
	 * </b></p>
     *
     * @param objDbList List of DB
     */      
	public void setObjDbList(List<String> objDbList) {
		this.objDbList = objDbList;
	}
	
    /**
	 * <p><b>
	 * [OUT] 
	 * </b></p>
	 * 
     * @return 
     */  
    public String getApmmacadd() {
		return apmmacadd;
	}
    
    /**
	 * <p><b>
	 * [IN] 
	 * </b></p>
     *
     * @param apmmacadd 
     */ 
	public void setApmmacadd(String apmmacadd) {
		this.apmmacadd = apmmacadd;
	}	
	
	/**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
	public ZappApm() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param apmid pk
     */	
    public ZappApm(String apmid) {
    	this.apmid = apmid;
    }
 
}

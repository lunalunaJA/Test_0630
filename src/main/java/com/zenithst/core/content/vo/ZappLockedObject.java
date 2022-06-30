package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for locking <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappLockedObject pIn = new ZappLockedObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_LOCKEDOBJECT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>lockobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>lobjid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Target ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>lobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Target type (01:Bundle, 02:File)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>lockerid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Locker ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>locktime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Lock time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>releasetime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Release time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>reason</td><td></td><td></td><td></td><td>VARCHAR(300)</td><td></td><td>Reason</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappLockedObject extends ZappDomain {

    private String lockobjid;
    private String lobjid;
    private String lobjtype;
    private String lockerid;
    private String locktime;
    private String releasetime;
    private String reason;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappLockedObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param lockobjid pk
     */
    public ZappLockedObject(String lockobjid) {
    	this.lockobjid = lockobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param lobjid Target ID
     * @param lobjtype Target type
     * @param locktime Lock time
     * @param releasetime Release time
     * @param reason Reason
     */
    public ZappLockedObject(String lobjid, String lobjtype, String locktime, String releasetime, String reason) {
    	this.lobjid = lobjid;
    	this.lobjtype = lobjtype;
    	this.locktime = locktime;
    	this.releasetime = releasetime;
    	this.reason = reason;
    }  
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getLockobjid() {
        return lockobjid;
    }
    
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param linkedobjid PK
     */ 
    public void setLockobjid(String lockobjid) {
        this.lockobjid = lockobjid == null ? null : lockobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Target ID
	 * </b></p>
	 * 
     * @return Target ID
     */    
	public String getLobjid() {
		return lobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Target ID
	 * </b></p>
     *
     * @param lobjid Target ID
     */ 
	public void setLobjid(String lobjid) {
		this.lobjid = lobjid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target type
	 * </b></p>
	 * 
     * @return Target type
     */
    public String getLobjtype() {
		return lobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type
	 * </b></p>
     *
     * @param lobjtype Target type
     */ 
	public void setLobjtype(String lobjtype) {
		this.lobjtype = lobjtype;
	}
    
    /**
	 * <p><b>
	 * [OUT] Locker ID
	 * </b></p>
	 * 
     * @return Locker ID
     */
	public String getLockerid() {
        return lockerid;
    }
    
    /**
	 * <p><b>
	 * [IN] Locker ID
	 * </b></p>
     *
     * @param lockerid Locker ID
     */ 
    public void setLockerid(String lockerid) {
        this.lockerid = lockerid == null ? null : lockerid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Lock time
	 * </b></p>
	 * 
     * @return Lock time
     */
    public String getLocktime() {
        return locktime;
    }
    
    /**
	 * <p><b>
	 * [IN] Lock time
	 * </b></p>
     *
     * @param locktime Lock time
     */ 
    public void setLocktime(String locktime) {
        this.locktime = locktime == null ? null : locktime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Release time
	 * </b></p>
	 * 
     * @return Release time
     */
    public String getReleasetime() {
        return releasetime;
    }
    
    /**
	 * <p><b>
	 * [IN] Release time
	 * </b></p>
     *
     * @param releasetime Release time
     */ 
    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime == null ? null : releasetime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Reason
	 * </b></p>
	 * 
     * @return Reason
     */
    public String getReason() {
        return reason;
    }
    
    /**
	 * <p><b>
	 * [IN] Reason
	 * </b></p>
     *
     * @param reason Reason
     */ 
    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }
}
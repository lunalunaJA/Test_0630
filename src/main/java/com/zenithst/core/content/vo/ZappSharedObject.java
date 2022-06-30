package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for sharing <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappSharedObject pIn = new ZappSharedObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_SHAREDOBJECT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>shareobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4+5)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>sobjid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Target ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>sobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Target type (00:Classification, 01:Bundle, 02:File)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>sharerid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Sharer ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>readerid</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Reader ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>readertype</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Reader type</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>sharetime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Share time</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappSharedObject extends ZappDomain {

    private String shareobjid;
    private String sobjid;
    private String sobjtype;
    private String sharerid;
    private String readerid;
    private String readertype;
    private String sharetime;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappSharedObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param shareobjid pk
     */
    public ZappSharedObject(String shareobjid) {
    	this.shareobjid = shareobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param sobjid Target ID
     * @param sobjtype Target type
     * @param sharerid Sharer ID
     * @param readerid Reader ID
     * @param readertype Reader type
     */
    public ZappSharedObject(String sobjid, String sobjtype, String sharerid, String readerid, String readertype) {
    	this.sobjid = sobjid;
    	this.sobjtype = sobjtype;
    	this.sharerid = sharerid;
    	this.readerid = readerid;
    	this.readertype = readertype;
    }
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getShareobjid() {
        return shareobjid;
    }
    
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param shareobjid PK
     */ 
    public void setShareobjid(String shareobjid) {
        this.shareobjid = shareobjid == null ? null : shareobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Target ID
	 * </b></p>
	 * 
     * @return Target ID
     */
    public String getSobjid() {
		return sobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Target ID
	 * </b></p>
     *
     * @param sobjid Target ID
     */ 
	public void setSobjid(String sobjid) {
		this.sobjid = sobjid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target type
	 * </b></p>
	 * 
     * @return Target type
     */
	public String getSobjtype() {
		return sobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type
	 * </b></p>
     *
     * @param sobjtype Target type
     */
	public void setSobjtype(String sobjtype) {
		this.sobjtype = sobjtype;
	}
    
    /**
	 * <p><b>
	 * [OUT] Sharer ID
	 * </b></p>
	 * 
     * @return Sharer ID
     */
	public String getSharerid() {
        return sharerid;
    }
    
    /**
	 * <p><b>
	 * [IN] Sharer ID
	 * </b></p>
     *
     * @param sharerid Sharer ID
     */
    public void setSharerid(String sharerid) {
        this.sharerid = sharerid == null ? null : sharerid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Reader ID
	 * </b></p>
	 * 
     * @return Reader ID
     */
    public String getReaderid() {
        return readerid;
    }
    
    /**
	 * <p><b>
	 * [IN] Reader ID
	 * </b></p>
     *
     * @param readerid Reader ID
     */
    public void setReaderid(String readerid) {
        this.readerid = readerid == null ? null : readerid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Reader type
	 * </b></p>
	 * 
     * @return Reader type
     */
    public String getReadertype() {
		return readertype;
	}
    
    /**
	 * <p><b>
	 * [IN] Reader type
	 * </b></p>
     *
     * @param readertype Reader type
     */
	public void setReadertype(String readertype) {
		this.readertype = readertype;
	}
    
    /**
	 * <p><b>
	 * [OUT] Share time
	 * </b></p>
	 * 
     * @return Share time
     */
	public String getSharetime() {
        return sharetime;
    }
    
    /**
	 * <p><b>
	 * [IN] Share time
	 * </b></p>
     *
     * @param sharetime Share time
     */
    public void setSharetime(String sharetime) {
        this.sharetime = sharetime == null ? null : sharetime.trim();
    }
}
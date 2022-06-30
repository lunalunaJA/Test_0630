package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for linking <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappLinkedObject pIn = new ZappLinkedObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CLASSOBJECT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>linkedobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4+5)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>sourceid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Source ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>targetid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Target ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>linkerid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Linker ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>linktime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Link time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>linktype</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Link type (01:->Bundle, 02:->File, 03:->Classification)</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappLinkedObject extends ZappDomain {

    private String linkedobjid;
    private String sourceid;
    private String targetid;
    private String linkerid;
    private String linktime;
    private String linktype;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappLinkedObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param linkedobjid pk
     */ 
    public ZappLinkedObject(String linkedobjid) {
    	this.linkedobjid = linkedobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param sourceid Source ID
     * @param targetid Target ID
     * @param linkerid Linker ID
     * @param linktype Link type
     */
    public ZappLinkedObject(String sourceid, String targetid, String linkerid, String linktype) {
    	this.sourceid = sourceid;
    	this.targetid = targetid;
    	this.linkerid = linkerid;
    	this.linktype = linktype;
    }
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getLinkedobjid() {
        return linkedobjid;
    }
    
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param linkedobjid PK
     */ 
    public void setLinkedobjid(String linkedobjid) {
        this.linkedobjid = linkedobjid == null ? null : linkedobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Source ID
	 * </b></p>
	 * 
     * @return Source ID
     */
    public String getSourceid() {
        return sourceid;
    }
    
    /**
	 * <p><b>
	 * [IN] Source ID
	 * </b></p>
     *
     * @param sourceid Source ID
     */ 
    public void setSourceid(String sourceid) {
        this.sourceid = sourceid == null ? null : sourceid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Target ID
	 * </b></p>
	 * 
     * @return Target ID
     */
    public String getTargetid() {
        return targetid;
    }
    
    /**
	 * <p><b>
	 * [IN] Target ID
	 * </b></p>
     *
     * @param targetid Target ID
     */ 
    public void setTargetid(String targetid) {
        this.targetid = targetid == null ? null : targetid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Linker ID
	 * </b></p>
	 * 
     * @return Linker ID
     */
    public String getLinkerid() {
        return linkerid;
    }
    
    /**
	 * <p><b>
	 * [IN] Linker ID
	 * </b></p>
     *
     * @param linkerid Linker ID
     */ 
    public void setLinkerid(String linkerid) {
        this.linkerid = linkerid == null ? null : linkerid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Link time
	 * </b></p>
	 * 
     * @return Link time
     */
    public String getLinktime() {
        return linktime;
    }
    
    /**
	 * <p><b>
	 * [IN] Link time
	 * </b></p>
     *
     * @param linktime Link time
     */ 
    public void setLinktime(String linktime) {
        this.linktime = linktime == null ? null : linktime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Link type
	 * </b></p>
	 * 
     * @return Link type
     */
    public String getLinktype() {
        return linktype;
    }
    
    /**
	 * <p><b>
	 * [IN] Link type
	 * </b></p>
     *
     * @param linktype Link type
     */ 
    public void setLinktype(String linktype) {
        this.linktype = linktype == null ? null : linktype.trim();
    }

}
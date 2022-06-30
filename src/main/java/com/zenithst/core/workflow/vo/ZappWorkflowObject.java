package com.zenithst.core.workflow.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content-workflow <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappWorkflowObject pIn = new ZappWorkflowObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_WORKFLOWER</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>wfobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4)</td><td>(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>wferid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Workflower ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>contentid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Content ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>contenttype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Content type</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappWorkflowObject extends ZappDomain {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String wfobjid;
    private String wferid;
    private String contentid;
    private String contenttype;


    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappWorkflowObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param wfobjid 
     */ 
    public ZappWorkflowObject(String wfobjid) {
    	this.wfobjid = wfobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param wferid Workflower ID
     * @param contentid Content ID
     * @param wfertype Content type
     */ 
    public ZappWorkflowObject(String wferid, String contentid) {
    	this.wferid = wferid;
    	this.contentid = contentid;
    }
    
    /**
	 * <p><b>
	 * [OUT]  - Primary Key
	 * </b></p>
	 * 
     * @return 
     */
    public String getWfobjid() {
        return wfobjid;
    }
    
    /**
	 * <p><b>
	 * [IN]  - Primary Key
	 * </b></p>
     *
     * @param wfobjid 
     */ 
    public void setWfobjid(String wfobjid) {
        this.wfobjid = wfobjid == null ? null : wfobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Workflower ID
	 * </b></p>
	 * 
     * @return Workflower ID
     */
    public String getWferid() {
        return wferid;
    }
    
    /**
	 * <p><b>
	 * [IN] Workflower ID
	 * </b></p>
     *
     * @param wferid Workflower ID
     */
    public void setWferid(String wferid) {
        this.wferid = wferid == null ? null : wferid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Content ID
	 * </b></p>
	 * 
     * @return Content ID
     */    
    public String getContentid() {
        return contentid;
    }
    
    /**
	 * <p><b>
	 * [IN] Content ID
	 * </b></p>
     *
     * @param contentid Content ID
     */
    public void setContentid(String contentid) {
        this.contentid = contentid == null ? null : contentid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Content type
	 * </b></p>
	 * 
     * @return Content type
     */     
    public String getContenttype() {
		return contenttype;
	}
    
    /**
	 * <p><b>
	 * [IN] Content type
	 * </b></p>
     *
     * @param contenttype Content type
     */
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
 
}
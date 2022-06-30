package com.zenithst.core.organ.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for group user <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappGroupUser pIn = new ZappGroupUser();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_GROUPUSER</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>groupuserid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4)</td>
* 	 </tr> 	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>groupid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>gobjid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>gobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>gobjseq</td><td></td><td></td><td></td><td>SMALLINT</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>editable</td><td></td><td></td><td>●</td><td>VARCHAR(2)</td><td></td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappGroupUser extends ZappDomain {

    private String groupuserid;
    private String groupid;
    private String gobjid;
    private String gobjtype;
    private int	   gobjseq;		// Target Sequence
    private String editable;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappGroupUser() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param groupuserid Group User ID (PK)
     */
    public ZappGroupUser(String groupuserid) {
    	this.groupuserid = groupuserid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param groupid Group ID
     * @param gobjid Target ID
     * @param gobjtype Target type
     */
    public ZappGroupUser(String groupid, String gobjid, String gobjtype) {
    	this.groupid = groupid;
    	this.gobjid = gobjid;
    	this.gobjtype = gobjtype;
    }

    /**
	 * <p><b>
	 * [OUT] Group User ID - Primary Key
	 * </b></p>
	 * 
     * @return Group User ID
     */
    public String getGroupuserid() {
        return groupuserid;
    }
    
    /**
	 * <p><b>
	 * [IN] Group User ID - Primary Key
	 * </b></p>
     *
     * @param groupuserid Group User ID
     */ 
    public void setGroupuserid(String groupuserid) {
        this.groupuserid = groupuserid == null ? null : groupuserid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Group ID (ZAPP_GROUP - GROUPID)
	 * </b></p>
	 * 
     * @return Group ID
     */
    public String getGroupid() {
		return groupid;
	}
    
    /**
	 * <p><b>
	 * [IN] Group ID (ZAPP_GROUP - GROUPID)
	 * </b></p>
     *
     * @param groupid Group ID
     */ 
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target ID (ZAPP_DEPT - DEPTID / ZAPP_DEPTUSER - DEPTUSERID)
	 * </b></p>
	 * 
     * @return Target ID
     */	
	public String getGobjid() {
		return gobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Target ID (ZAPP_DEPT - DEPTID / ZAPP_DEPTUSER - DEPTUSERID)
	 * </b></p>
     *
     * @param gobjid Target ID
     */
	public void setGobjid(String gobjid) {
		this.gobjid = gobjid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target type (01:User, 02:Department)
	 * </b></p>
	 * 
     * @return Target type
     */		
	public String getGobjtype() {
		return gobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type (01:User, 02:Department)
	 * </b></p>
     *
     * @param gobjtype Target type
     */
	public void setGobjtype(String gobjtype) {
		this.gobjtype = gobjtype;
	}
	
	/**
	 * <p><b>
	 * [OUT] Target sequence
	 * </b></p>
	 * 
     * @return Target sequence
     */		
    public int getGobjseq() {
		return gobjseq;
	}
    
    /**
	 * <p><b>
	 * [IN] Target sequence
	 * </b></p>
     *
     * @param gobjseq Target sequence
     */
	public void setGobjseq(int gobjseq) {
		this.gobjseq = gobjseq;
	}
	
	/**
	 * <p><b>
	 * [OUT] Editable? (Y/N)
	 * </b></p>
	 * 
     * @return Editable?
     */		
	public String getEditable() {
		return editable;
	}
	   
    /**
	 * <p><b>
	 * [IN] Editable? (Y/N)
	 * </b></p>
     *
     * @param editable Editable?
     */
	public void setEditable(String editable) {
		this.editable = editable;
	}

}
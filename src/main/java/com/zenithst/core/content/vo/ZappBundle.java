package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for bundle <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappBundle pIn = new ZappBundle();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_BUNDLE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>bundleid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+4+8)</td><td>Bundle ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>bno</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Bundle no.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>title</td><td></td><td></td><td></td><td>VARCHAR(500)</td><td></td><td>Bundle Title</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>creatorid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Creator ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>creatorname</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Creator name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>holderid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Holder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>discarderid</td><td></td><td></td><td>●</td><td>VARCHAR(64)</td><td></td><td>Discarder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>createtime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Create time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>updatetime</td><td></td><td></td><td>●</td><td>VARCHAR(25)</td><td></td><td>Update time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>retentionid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Retention period ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>11</td><td>expiretime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Expire date</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>12</td><td>btype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Bundle Type</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>13</td><td>seclevel</td><td></td><td></td><td></td><td>INT</td><td></td><td>Security Level</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>14</td><td>state</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>State</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappBundle extends ZappDomain {

    private String bundleid;
    private String bno;
    private String title;
    private String creatorid;
    private String creatorname;
    private String holderid;
    private String discarderid;
    private String createtime;
    private String updatetime;
    private String retentionid;
    private String expiretime;
    private String btype;
    private int seclevel;
    private String state;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappBundle() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param bundleid Bundle ID
     */    
    public ZappBundle(String bundleid) {
    	this.bundleid = bundleid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param bundleid Bundle ID
     * @param state State
     */    
    public ZappBundle(String bundleid, String state) {
    	this.bundleid = bundleid;
    	this.state = state;
    }
    
    /**
	 * <p><b>
	 * [OUT] Bundle ID - Primary Key
	 * </b></p>
	 * 
     * @return Bundle ID
     */
    public String getBundleid() {
        return bundleid;
    }
    
    /**
	 * <p><b>
	 * [IN] Bundle ID - Primary Key
	 * </b></p>
     *
     * @param bundleid Bundle ID
     */      
    public void setBundleid(String bundleid) {
        this.bundleid = bundleid == null ? null : bundleid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Bundle no.
	 * </b></p>
	 * 
     * @return Bundle no.
     */     
    public String getBno() {
        return bno;
    }
    
    /**
	 * <p><b>
	 * [IN] Name
	 * </b></p>
     *
     * @param name Name
     */  
    public void setBno(String bno) {
        this.bno = bno == null ? null : bno.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Bundle Title
	 * </b></p>
	 * 
     * @return Bundle Title
     */      
    public String getTitle() {
		return title;
	}
    
    /**
	 * <p><b>
	 * [IN] Bundle Title
	 * </b></p>
     *
     * @param title Bundle Title
     */  
	public void setTitle(String title) {
		this.title = title;
	}
    
    /**
	 * <p><b>
	 * [OUT] Creator ID
	 * </b></p>
	 * 
     * @return Creator ID
     */    	
	public String getCreatorid() {
        return creatorid;
    }
    
    /**
	 * <p><b>
	 * [IN] Creator ID
	 * </b></p>
     *
     * @param creatorid Creator ID
     */  
    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid == null ? null : creatorid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Creator name
	 * </b></p>
	 * 
     * @return Creator name
     */    
    public String getCreatorname() {
        return creatorname;
    }
    
    /**
	 * <p><b>
	 * [IN] Creator name
	 * </b></p>
     *
     * @param creatorname Creator name
     */  
    public void setCreatorname(String creatorname) {
        this.creatorname = creatorname == null ? null : creatorname.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Holder ID
	 * </b></p>
	 * 
     * @return Holder ID
     */       
    public String getHolderid() {
        return holderid;
    }
    
    /**
	 * <p><b>
	 * [IN] Holder ID
	 * </b></p>
     *
     * @param holderid Holder ID
     */  
    public void setHolderid(String holderid) {
        this.holderid = holderid == null ? null : holderid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Discarder ID
	 * </b></p>
	 * 
     * @return Discarder ID
     */      
    public String getDiscarderid() {
		return discarderid;
	}
    
    /**
	 * <p><b>
	 * [IN] Discarder ID
	 * </b></p>
     *
     * @param discarderid Discarder ID
     */  
	public void setDiscarderid(String discarderid) {
		this.discarderid = discarderid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Create time
	 * </b></p>
	 * 
     * @return Create time
     */ 	
	public String getCreatetime() {
        return createtime;
    }
    
    /**
	 * <p><b>
	 * [IN] Create time
	 * </b></p>
     *
     * @param createtime Create time
     */  
    public void setCreatetime(String createtime) {
        this.createtime = createtime == null ? null : createtime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Update time
	 * </b></p>
	 * 
     * @return Update time
     */     
    public String getUpdatetime() {
        return updatetime;
    }
    
    /**
	 * <p><b>
	 * [IN] Update time
	 * </b></p>
     *
     * @param updatetime Update time
     */  
    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime == null ? null : updatetime.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Retention period ID
	 * </b></p>
	 * 
     * @return Retention period ID
     */     
    public String getRetentionid() {
		return retentionid;
	}
    
    /**
	 * <p><b>
	 * [IN] Retention period ID
	 * </b></p>
     *
     * @param retentionid Retention period ID
     */  
	public void setRetentionid(String retentionid) {
		this.retentionid = retentionid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Expire date
	 * </b></p>
	 * 
     * @return Expire date
     */  	
	public String getExpiretime() {
		return expiretime;
	}
    
    /**
	 * <p><b>
	 * [IN] Expire date
	 * </b></p>
     *
     * @param expiretime Expire date
     */  
	public void setExpiretime(String expiretime) {
		this.expiretime = expiretime;
	}
	
    /**
	 * <p><b>
	 * [OUT] Bundle Type
	 * </b></p>
	 * 
     * @return Bundle Type
     */ 
    public String getBtype() {
		return btype;
	}
    
    /**
	 * <p><b>
	 * [IN] Bundle Type
	 * </b></p>
     *
     * @param btype Bundle Type
     */      
	public void setBtype(String btype) {
		this.btype = btype;
	}
	
	/**
	 * <p><b>
	 * [OUT] Security Level
	 * </b></p>
	 * 
     * @return Security Level
     */ 	
	public int getSeclevel() {
		return seclevel;
	}

	/**
	 * <p><b>
	 * [IN] Security Level
	 * </b></p>
     *
     * @param seclevel Security Level
     */      
	public void setSeclevel(int seclevel) {
		this.seclevel = seclevel;
	}
	
	/**
	 * <p><b>
	 * [OUT] State
	 * </b></p>
	 * 
     * @return State
     */ 	
	public String getState() {
        return state;
    }
    
    /**
	 * <p><b>
	 * [IN] State
	 * </b></p>
     *
     * @param state State
     */  
    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }
}
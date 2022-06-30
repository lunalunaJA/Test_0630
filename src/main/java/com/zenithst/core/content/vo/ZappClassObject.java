package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content-classification <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappClassObject pIn = new ZappClassObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CLASSOBJECT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>classobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4+5)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>classid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Classification ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>types</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Classification type<br>(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>cobjid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Target ID (Bundle / File ID)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>cobjtype</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Target type (01:Bundle, 02:File)</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappClassObject extends ZappDomain {

    private String classobjid;
    private String classid;
    private String classtype;
    private String cobjid;
    private String cobjtype;
    
    /* Extra */
    private String objAction;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappClassObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param classobjid pk
     */ 
    public ZappClassObject(String classobjid) {
    	this.classobjid = classobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param classid Classification ID
     * @param cobjid Target ID
     * @param cobjtype Target type
     */
    public ZappClassObject(String classid, String cobjid, String cobjtype) {
    	this.classid = classid;
    	this.cobjid = cobjid;
    	this.cobjtype = cobjtype;
    }
    
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
    public String getClassobjid() {
        return classobjid;
    }
    
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param classobjid PK
     */ 
    public void setClassobjid(String classobjid) {
        this.classobjid = classobjid == null ? null : classobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Classification ID
	 * </b></p>
	 * 
     * @return Classification ID
     */
    public String getClassid() {
        return classid;
    }
    
    /**
	 * <p><b>
	 * [IN] Classification ID
	 * </b></p>
     *
     * @param classid Classification ID
     */ 
    public void setClassid(String classid) {
        this.classid = classid == null ? null : classid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Classification type
	 * </b></p>
	 * 
     * @return Classification type
     */
    public String getClasstype() {
		return classtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification type
	 * </b></p>
     *
     * @param classtype Classification type
     */     
	public void setClasstype(String classtype) {
		this.classtype = classtype;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target ID
	 * </b></p>
	 * 
     * @return Target ID
     */
	public String getCobjid() {
		return cobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Target ID
	 * </b></p>
     *
     * @param cobjid Target ID
     */ 
	public void setCobjid(String cobjid) {
		this.cobjid = cobjid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target type
	 * </b></p>
	 * 
     * @return Target type
     */
	public String getCobjtype() {
		return cobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type
	 * </b></p>
     *
     * @param cobjtype Target type
     */ 
	public void setCobjtype(String cobjtype) {
		this.cobjtype = cobjtype;
	}
	
	/* ****************************************************************************** */
    
    /**
	 * <p><b>
	 * [OUT] Processing type
	 * </b></p>
	 * 
     * @return Processing type
     */
	public String getObjAction() {
		return objAction;
	}
    
    /**
	 * <p><b>
	 * [IN] Processing type
	 * </b></p>
     *
     * @param objAction Processing type
     */  
	public void setObjAction(String objAction) {
		this.objAction = objAction;
	}


}
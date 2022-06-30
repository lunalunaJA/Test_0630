package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for linking content (classification) and keywords <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappKeywordObject pIn = new ZappKeywordObject();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CLASSOBJECT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>kwobjid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>kobjid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Target ID (Classification/Bundle/File)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>kobjtype</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Target type<br>(00:Classification, 01:Bundle, 02:File)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>kwordid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Keyword ID</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappKeywordObject extends ZappDomain {

	/* KeywordObject */
    private String kwobjid;
    private String kobjid;
    private String kobjtype;
    private String kwordid;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappKeywordObject() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param kwobjid pk
     */ 
    public ZappKeywordObject(String kwobjid) {
    	this.kwobjid = kwobjid;
    }
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param kobjid Target ID
     * @param kobjtype Target type
     * @param kwordid Keyword ID
     */
    public ZappKeywordObject(String kobjid, String kobjtype, String kwordid) {
    	this.kobjid = kobjid;
    	this.kobjtype = kobjtype;
    	this.kwordid = kwordid;
    }
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
	public String getKwobjid() {
		return kwobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param kwobjid PK
     */ 
	public void setKwobjid(String kwobjid) {
		this.kwobjid = kwobjid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target ID
	 * </b></p>
	 * 
     * @return Target ID
     */
	public String getKobjid() {
		return kobjid;
	}
    
    /**
	 * <p><b>
	 * [IN] Target ID
	 * </b></p>
     *
     * @param kobjid Target ID
     */
	public void setKobjid(String kobjid) {
		this.kobjid = kobjid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target type
	 * </b></p>
	 * 
     * @return Target type
     */
	public String getKobjtype() {
		return kobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Target type
	 * </b></p>
     *
     * @param kobjtype Target type
     */
	public void setKobjtype(String kobjtype) {
		this.kobjtype = kobjtype;
	}
    
    /**
	 * <p><b>
	 * [OUT] Keyword ID
	 * </b></p>
	 * 
     * @return Keyword ID
     */
	public String getKwordid() {
		return kwordid;
	}
    
    /**
	 * <p><b>
	 * [IN] Keyword ID
	 * </b></p>
     *
     * @param kwordid Keyword ID
     */
	public void setKwordid(String kwordid) {
		this.kwordid = kwordid;
	}
    
    
}
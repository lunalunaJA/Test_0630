package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for keyword <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappKeyword pIn = new ZappKeyword();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_KEYWORDS</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>kwordid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2)</td><td>PK</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>kword</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Keyword</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Use or not (Y/N)</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappKeyword extends ZappDomain {

    private String kwordid;
    private String kword;
    private String isactive;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappKeyword() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param kwordid pk
     */ 
    public ZappKeyword(String kwordid) {
    	this.kwordid = kwordid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param kword Keyword
     * @param isactive Use or not
     */ 
    public ZappKeyword(String kword, String isactive) {
    	this.kword = kword;
    	this.isactive = isactive;
    }
    
    /**
	 * <p><b>
	 * [OUT] Primary Key
	 * </b></p>
	 * 
     * @return PK
     */
	public String getKwordid() {
		return kwordid;
	}
	
    /**
	 * <p><b>
	 * [IN] Primary Key
	 * </b></p>
     *
     * @param kwordid PK
     */ 
	public void setKwordid(String kwordid) {
		this.kwordid = kwordid;
	}
	
    /**
	 * <p><b>
	 * [OUT] Keyword
	 * </b></p>
	 * 
     * @return Keyword
     */
	public String getKword() {
		return kword;
	}
	
    /**
	 * <p><b>
	 * [IN] Keyword
	 * </b></p>
     *
     * @param kword Keyword
     */
	public void setKword(String kword) {
		this.kword = kword;
	}
	
    /**
	 * <p><b>
	 * [OUT] Use or not
	 * </b></p>
	 * 
     * @return Use or not
     */
	public String getIsactive() {
		return isactive;
	}
	
    /**
	 * <p><b>
	 * [IN] Use or not
	 * </b></p>
     *
     * @param isactive Use or not
     */
	public void setIsactive(String isactive) {
		this.isactive = isactive;
	}
    
}
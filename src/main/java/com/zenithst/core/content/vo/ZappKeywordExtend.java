package com.zenithst.core.content.vo;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended keyword <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappKeywordExtend pIn = new ZappKeywordExtend();
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappKeywordExtend extends ZappKeyword {

	/* KeywordObject */
    private String kwobjid;
    private String kobjid;
    private String kobjtype;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappKeywordExtend() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param kwobjid pk
     */ 
    public ZappKeywordExtend(String kwobjid) {
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
    public ZappKeywordExtend(String kobjid, String kobjtype, String kwordid) {
    	this.kobjid = kobjid;
    	this.kobjtype = kobjtype;
    	super.setKwordid(kwordid);
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
    
    
}
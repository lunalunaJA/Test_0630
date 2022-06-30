package com.zenithst.core.organ.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for company <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappCompany pIn = new ZappCompany();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_COMPANY</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>companyid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+5)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>name</td><td></td><td></td><td></td><td>VARCHAR(150)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>tel</td><td></td><td></td><td>●</td><td>VARCHAR(50)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>address</td><td></td><td></td><td>●</td><td>VARCHAR(500)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>code</td><td></td><td></td><td></td><td>VARCHAR(30)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>abbrname</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappCompany extends ZappDomain {

 	private static final long serialVersionUID = 1L;
	private String companyid;	
    private String name;		 
    private String tel;
    private String address;
    private String code;
    private String abbrname;
    private String isactive;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappCompany() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param companyid Company ID
     */ 
    public ZappCompany(String companyid) {
    	this.companyid = companyid;
    }
    
    /**
	 * <p><b>
	 * [OUT] Company ID - Primary Key
	 * </b></p>
	 * 
     * @return Company ID
     */
    public String getCompanyid() {
        return companyid;
    }
    
    /**
	 * <p><b>
	 * [IN] Company ID - Primary Key
	 * </b></p>
     *
     * @param companyid Company ID
     */    
    public void setCompanyid(String companyid) {
        this.companyid = companyid == null ? null : companyid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Name
	 * </b></p>
	 * 
     * @return Name
     */   
    public String getName() {
        return name;
    }
    
    /**
	 * <p><b>
	 * [IN] Name
	 * </b></p>
     *
     * @param name Name
     */    
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Tel. No
	 * </b></p>
	 * 
     * @return Tel. No
     */  
    public String getTel() {
        return tel;
    }
    
    /**
	 * <p><b>
	 * [IN] Tel. No
	 * </b></p>
     *
     * @param tel Tel. No
     */     
    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Address
	 * </b></p>
	 * 
     * @return Address
     */     
    public String getAddress() {
        return address;
    }
    
    /**
	 * <p><b>
	 * [IN] Address
	 * </b></p>
     *
     * @param address Address
     */       
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Code
	 * </b></p>
	 * 
     * @return Code
     */    
    public String getCode() {
        return code;
    }
    
    /**
	 * <p><b>
	 * [IN] Code
	 * </b></p>
     *
     * @param code Code
     */    
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Abbreviation
	 * </b></p>
	 * 
     * @return Abbreviation
     */    
    public String getAbbrname() {
        return abbrname;
    }
    
    /**
	 * <p><b>
	 * [IN] Abbreviation
	 * </b></p>
     *
     * @param abbrname Abbreviation
     */     
    public void setAbbrname(String abbrname) {
        this.abbrname = abbrname == null ? null : abbrname.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Use or not (Y/N)
	 * </b></p>
	 * 
     * @return Use or not
     */    
    public String getIsactive() {
        return isactive;
    }
    
    /**
	 * <p><b>
	 * [IN] Use or not (Y/N)
	 * </b></p>
     *
     * @param isactive Use or not
     */     
    public void setIsactive(String isactive) {
        this.isactive = isactive == null ? null : isactive.trim();
    }

}
package com.zenithst.core.system.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for code <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappCode pIn = new ZappCode();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_CODE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>codeid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+5+6+7)</td><td>Code ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>companyid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Company ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>name</td><td></td><td></td><td></td><td>VARCHAR(150)</td><td></td><td>Code name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>codevalue</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Code value</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>upid</td><td></td><td></td><td>●</td><td>VARCHAR(64)</td><td></td><td>Upper ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>types</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>Code type<br>01:Preferences, 02:Position, 03:Duty, 04:Security level<br>05:Retention period, 06:Classification access control, 07:Content  access control, 08:User type<br>09:Group type, 10:Classification type, 11:Right target type, 12:Content type</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>codekey</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Code key(for program identification)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>priority</td><td></td><td></td><td></td><td>Integer</td><td></td><td>Sorting order</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>isactive</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Use or not(Y/N)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>editable</td><td></td><td></td><td></td><td>CHAR(1)</td><td></td><td>Editable or not(Y/N)</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappCode extends ZappDomain {

	/* */
    private String codeid;
    private String companyid;
    private String name;
    private String codevalue;
    private String upid;
    private String types;
    private String codekey;		/* unique key - for program identification */
    private int priority;
    private String isactive;
    private String editable;
    
    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappCode() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param codeid Code ID
     */
    public ZappCode(String codeid) {
    	this.codeid = codeid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param companyid Company ID
     * @param isactive Use or not
     */
    public ZappCode(String companyid, String isactive) {
    	this.companyid = companyid;
    	this.isactive = isactive;
    }
    /**
	 * <p><b>
	 * Additional constructor #3
	 * </b></p>
     *
     * @param codeid Code ID
     * @param priority Sorting order
     */
    public ZappCode(String codeid, int priority) {
    	this.codeid = codeid;
    	this.priority = priority;
    }    
    
    /**
	 * <p><b>
	 * [OUT] Code ID - Primary Key
	 * </b></p>
	 * 
     * @return Code ID
     */
    public String getCodeid() {
        return codeid;
    }
    
    /**
	 * <p><b>
	 * [IN] Code ID - Primary Key
	 * </b></p>
     *
     * @param codeid Code ID
     */ 
    public void setCodeid(String codeid) {
        this.codeid = codeid == null ? null : codeid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Company ID
	 * </b></p>
	 * 
     * @return Company ID
     */ 
    public String getCompanyid() {
        return companyid;
    }
    
    /**
	 * <p><b>
	 * [IN] Company ID
	 * </b></p>
     *
     * @param companyid Company ID
     */ 
    public void setCompanyid(String companyid) {
        this.companyid = companyid == null ? null : companyid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Code Name
	 * </b></p>
	 * 
     * @return Code Name
     */
    public String getName() {
        return name;
    }
    
    /**
	 * <p><b>
	 * [IN] Code Name
	 * </b></p>
     *
     * @param name Code Name
     */    
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Value
	 * </b></p>
	 * 
     * @return Value
     */    
    public String getCodevalue() {
		return codevalue;
	}
    
    /**
	 * <p><b>
	 * [IN] Value
	 * </b></p>
     *
     * @param codevalue Value
     */
	public void setCodevalue(String codevalue) {
		this.codevalue = codevalue == null ? null : codevalue.trim();
	}
    
    /**
	 * <p><b>
	 * [OUT] Upper ID
	 * </b></p>
	 * 
     * @return Upper ID
     */	
	public String getUpid() {
        return upid;
    }
    
    /**
	 * <p><b>
	 * [IN] Upper ID
	 * </b></p>
     *
     * @param upid Upper ID
     */
    public void setUpid(String upid) {
        this.upid = upid == null ? null : upid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Code Type
	 * </b></p>
	 * 
     * @return Code Type
     */    
    public String getTypes() {
        return types;
    }
    
    /**
	 * <p><b>
	 * [IN] Code Type
	 * </b></p>
     *
     * @param types Code Type
     */
    public void setTypes(String types) {
        this.types = types == null ? null : types.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Sorting order
	 * </b></p>
	 * 
     * @return Sorting order
     */    
    public int getPriority() {
        return priority;
    }
    
    /**
	 * <p><b>
	 * [IN] Sorting order
	 * </b></p>
     *
     * @param priority Sorting order
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    /**
	 * <p><b>
	 * [OUT] Code key
	 * </b></p>
	 * 
     * @return Code key
     */     
	public String getCodekey() {
		return codekey;
	}
    
    /**
	 * <p><b>
	 * [IN] Code key
	 * </b></p>
     *
     * @param codekey Code key
     */	
	public void setCodekey(String codekey) {
		this.codekey = codekey;
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
		this.isactive = isactive;
	}
    
    /**
	 * <p><b>
	 * [OUT] Editable or not (Y/N)
	 * </b></p>
	 * 
     * @return Editable or not
     */ 	
	public String getEditable() {
		return editable;
	}
    
    /**
	 * <p><b>
	 * [IN] Editable or not (Y/N)
	 * </b></p>
     *
     * @param editable Editable or not
     */ 	
	public void setEditable(String editable) {
		this.editable = editable;
	}
    
}
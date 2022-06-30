package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for additory bundle <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappAdditoryBundle pIn = new ZappAdditoryBundle();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_ADDITORYBUNDLE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>bundleid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td></td><td>Bundle ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>dynamic01</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 01</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>dynamic02</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 03</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>dynamic03</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 04</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>dynamic04</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 05</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>dynamic05</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 06</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>dynamic06</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 07</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>dynamic07</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 08</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>9</td><td>dynamic08</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 08</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>10</td><td>dynamic09</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Dymanic value 09</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>11</td><td>dynamic10</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Dymanic value 10</td>
*    </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>12</td><td>drafter</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>Drafter ID</td>
*    </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>11</td><td>summary</td><td></td><td></td><td></td><td>VARCHAR(100)</td><td></td><td>Summary</td>
*    </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappAdditoryBundle extends ZappDomain {

    private String bundleid;
    private String dynamic01;
    private String dynamic02;
    private String dynamic03;
    private String dynamic04;
    private String dynamic05;
    private String dynamic06;
    private String dynamic07;
    private String dynamic08;
    private String dynamic09;
    private String dynamic10;
    private String drafter;
    private String summary;
    
    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappAdditoryBundle() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param bundleid Bundle ID
     */    
    public ZappAdditoryBundle(String bundleid) {
    	this.bundleid = bundleid;
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
	 * [OUT] Dymanic value 01
	 * </b></p>
	 * 
     * @return Dymanic value 01
     */     
    public String getDynamic01() {
        return dynamic01;
    }
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 01
	 * </b></p>
     *
     * @param dynamic01 Dymanic value 01
     */  
    public void setDynamic01(String dynamic01) {
        this.dynamic01 = dynamic01 == null ? null : dynamic01.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 02
	 * </b></p>
	 * 
     * @return Dymanic value 02
     */      
    public String getDynamic02() {
		return dynamic02;
	}
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 02
	 * </b></p>
     *
     * @param dynamic02 Dymanic value 02
     */  
	public void setDynamic02(String dynamic02) {
		this.dynamic02 = dynamic02;
	}
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 03
	 * </b></p>
	 * 
     * @return Dymanic value 03
     */    	
	public String getDynamic03() {
        return dynamic03;
    }
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 03
	 * </b></p>
     *
     * @param dynamic03 Dymanic value 03
     */  
    public void setDynamic03(String dynamic03) {
        this.dynamic03 = dynamic03 == null ? null : dynamic03.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 05
	 * </b></p>
	 * 
     * @return Dymanic value 05
     */    
    public String getDynamic04() {
        return dynamic04;
    }
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 05
	 * </b></p>
     *
     * @param dynamic04 Dymanic value 05
     */  
    public void setDynamic04(String dynamic04) {
        this.dynamic04 = dynamic04 == null ? null : dynamic04.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 06
	 * </b></p>
	 * 
     * @return Dymanic value 06
     */       
    public String getDynamic05() {
        return dynamic05;
    }
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 06
	 * </b></p>
     *
     * @param dynamic05 Dymanic value 06
     */  
    public void setDynamic05(String dynamic05) {
        this.dynamic05 = dynamic05 == null ? null : dynamic05.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 07
	 * </b></p>
	 * 
     * @return Dymanic value 07
     */      
    public String getDynamic06() {
		return dynamic06;
	}
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 07
	 * </b></p>
     *
     * @param dynamic06 Dymanic value 07
     */  
	public void setDynamic06(String dynamic06) {
		this.dynamic06 = dynamic06;
	}
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 08
	 * </b></p>
	 * 
     * @return Dymanic value 08
     */ 	
	public String getDynamic07() {
        return dynamic07;
    }
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 08
	 * </b></p>
     *
     * @param dynamic07 Dymanic value 08
     */  
    public void setDynamic07(String dynamic07) {
        this.dynamic07 = dynamic07 == null ? null : dynamic07.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 08
	 * </b></p>
	 * 
     * @return Dymanic value 08
     */     
    public String getDynamic08() {
        return dynamic08;
    }
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 08
	 * </b></p>
     *
     * @param dynamic08 Dymanic value 08
     */  
    public void setDynamic08(String dynamic08) {
        this.dynamic08 = dynamic08 == null ? null : dynamic08.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 09
	 * </b></p>
	 * 
     * @return Dymanic value 09
     */     
    public String getDynamic09() {
		return dynamic09;
	}
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 09
	 * </b></p>
     *
     * @param dynamic09 Dymanic value 09
     */  
	public void setDynamic09(String dynamic09) {
		this.dynamic09 = dynamic09;
	}
    
    /**
	 * <p><b>
	 * [OUT] Dymanic value 10
	 * </b></p>
	 * 
     * @return Dymanic value 10
     */  	
	public String getDynamic10() {
		return dynamic10;
	}
    
    /**
	 * <p><b>
	 * [IN] Dymanic value 10
	 * </b></p>
     *
     * @param dynamic10 Dymanic value 10
     */  
	public void setDynamic10(String dynamic10) {
		this.dynamic10 = dynamic10;
	}
    
    /**
	 * <p><b>
	 * [OUT] Drafter
	 * </b></p>
	 * 
     * @return Drafter
     */  	
	public String getDrafter() {
		return drafter;
	}
    
    /**
	 * <p><b>
	 * [IN] Drafter
	 * </b></p>
     *
     * @param drafter Drafter
     */  
	public void setDrafter(String drafter) {
		this.drafter = drafter;
	}
    
    /**
	 * <p><b>
	 * [OUT] Summary
	 * </b></p>
	 * 
     * @return Summary
     */  	
	public String getSummary() {
		return summary;
	}
    
    /**
	 * <p><b>
	 * [IN] Summary
	 * </b></p>
     *
     * @param summary Summary
     */  
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
}
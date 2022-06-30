package com.zenithst.core.status.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for statistic <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappStatus pIn = new ZappStatus();
*    ...

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappStatus extends ZappDomain {

	/* Results */
    private String stakey;
    private String staname;
    private String stasdate;
    private String staedate;
    private int stacnt;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String stayear;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String stamonth;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String staobjid;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String stacompanyid;    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String staobjtype;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String statermtype; 
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String staaction; 
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> staactions; 
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String staIncToday;     
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String staObjTerm;        
    
    /**
	 * <p><b>
	 * [OUT] PK
	 * </b></p>
	 * 
     * @return PK
     */
	public String getStakey() {
		return stakey;
	}
	
    /**
	 * <p><b>
	 * [IN] PK
	 * </b></p>
     *
     * @param stakey PK
     */	
	public void setStakey(String stakey) {
		this.stakey = stakey;
	}
	
    /**
	 * <p><b>
	 * [OUT] Start Date
	 * </b></p>
	 * 
     * @return Start Date
     */
	public String getStasdate() {
		return stasdate;
	}
	
    /**
	 * <p><b>
	 * [IN] Start Date
	 * </b></p>
     *
     * @param stasdate Start Date
     */
	public void setStasdate(String stasdate) {
		this.stasdate = stasdate;
	}
	
    /**
	 * <p><b>
	 * [OUT] End Date
	 * </b></p>
	 * 
     * @return End Date
     */
	public String getStaedate() {
		return staedate;
	}
	
    /**
	 * <p><b>
	 * [IN] End Date
	 * </b></p>
     *
     * @param staedate End Date
     */
	public void setStaedate(String staedate) {
		this.staedate = staedate;
	}
	
    /**
	 * <p><b>
	 * [OUT] Sta count
	 * </b></p>
	 * 
     * @return Sta count
     */
	public int getStacnt() {
		return stacnt;
	}
	
    /**
	 * <p><b>
	 * [IN] Sta count
	 * </b></p>
     *
     * @param stacnt Sta count
     */
	public void setStacnt(int stacnt) {
		this.stacnt = stacnt;
	}
	
    /**
	 * <p><b>
	 * [OUT] Object ID
	 * </b></p>
	 * 
     * @return Object ID
     */
	public String getStaobjid() {
		return staobjid;
	}
	
    /**
	 * <p><b>
	 * [IN] Object ID
	 * </b></p>
     *
     * @param staobjid Object ID
     */
	public void setStaobjid(String staobjid) {
		this.staobjid = staobjid;
	}

    /**
	 * <p><b>
	 * [OUT] Company ID
	 * </b></p>
	 * 
     * @return Company ID
     */
	public String getStacompanyid() {
		return stacompanyid;
	}
	
    /**
	 * <p><b>
	 * [IN] Company ID
	 * </b></p>
     *
     * @param stacompanyid Company ID
     */
	public void setStacompanyid(String stacompanyid) {
		this.stacompanyid = stacompanyid;
	}
	
    /**
	 * <p><b>
	 * [OUT] Object Type
	 * </b></p>
	 * 
     * @return Object Type
     */
	public String getStaobjtype() {
		return staobjtype;
	}
	
    /**
	 * <p><b>
	 * [IN] Object Type
	 * </b></p>
     *
     * @param staobjtype Object Type
     */
	public void setStaobjtype(String staobjtype) {
		this.staobjtype = staobjtype;
	}
	
    /**
	 * <p><b>
	 * [OUT] Term Type
	 * </b></p>
	 * 
     * @return Term Type
     */	
	public String getStatermtype() {
		return statermtype;
	}
	
    /**
	 * <p><b>
	 * [IN] Term Type
	 * </b></p>
     *
     * @param statermtype Term Type
     */
	public void setStatermtype(String statermtype) {
		this.statermtype = statermtype;
	}
	
    /**
	 * <p><b>
	 * [OUT] Year
	 * </b></p>
	 * 
     * @return Year
     */		
	public String getStayear() {
		return stayear;
	}
	
    /**
	 * <p><b>
	 * [IN] Term Type
	 * </b></p>
     *
     * @param statermtype Term Type
     */
	public void setStayear(String stayear) {
		this.stayear = stayear;
	}
	
    /**
	 * <p><b>
	 * [OUT] Month
	 * </b></p>
	 * 
     * @return Month
     */	
	public String getStamonth() {
		return stamonth;
	}
	public void setStamonth(String stamonth) {
		this.stamonth = stamonth;
	}
	
    /**
	 * <p><b>
	 * [OUT] Action
	 * </b></p>
	 * 
     * @return Action
     */		
	public String getStaaction() {
		return staaction;
	}
	public void setStaaction(String staaction) {
		this.staaction = staaction;
	}
	
    /**
	 * <p><b>
	 * [OUT] List of Action
	 * </b></p>
	 * 
     * @return List of Action
     */
	public List<String> getStaactions() {
		return staactions;
	}
	public void setStaactions(List<String> staactions) {
		this.staactions = staactions;
	}
	
    /**
	 * <p><b>
	 * [OUT] Sta. name
	 * </b></p>
	 * 
     * @return Sta. name
     */
	public String getStaname() {
		return staname;
	}
	public void setStaname(String staname) {
		this.staname = staname;
	}
	
    /**
	 * <p><b>
	 * [OUT] Inc. today?
	 * </b></p>
	 * 
     * @return Inc. today?
     */
	public String getStaIncToday() {
		return staIncToday;
	}
	public void setStaIncToday(String staIncToday) {
		this.staIncToday = staIncToday;
	}
	
    /**
	 * <p><b>
	 * [OUT] Object term
	 * </b></p>
	 * 
     * @return Object term
     */
	public String getStaObjTerm() {
		return staObjTerm;
	}
	public void setStaObjTerm(String staObjTerm) {
		this.staObjTerm = staObjTerm;
	}
  
}
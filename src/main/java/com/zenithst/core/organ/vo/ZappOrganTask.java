package com.zenithst.core.organ.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for company task <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappOrganTask pIn = new ZappOrganTask();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_ORGANTASK</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>organtaskid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3+4)</td>
* 	 </tr> 	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>companyid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>deptid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>taskid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>objtype</td><td></td><td></td><td></td><td>VACHAR(2)</td><td></td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappOrganTask extends ZappDomain {

	/* */
    private String organtaskid;
    private String companyid;
    private String deptid;
    private String taskid;
    private String tobjtype;

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */

    public ZappOrganTask() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param organtaskid Company task ID (PK)
     */
    public ZappOrganTask(String organtaskid) {
    	this.organtaskid = organtaskid;
    }
    /**
	 * <p><b>
	 * Additional constructor #2
	 * </b></p>
     *
     * @param companyid Company ID
     * @param deptid Dept. ID
     * @param taskid Task ID
     * @param objtype Target type
     */
    public ZappOrganTask(String companyid, String deptid, String taskid, String tobjtype) {
    	this.companyid = companyid;
    	this.deptid = deptid;
    	this.taskid = taskid;
    	this.tobjtype = tobjtype;
    }
    
    /**
	 * <p><b>
	 * [OUT] Company task ID - Primary Key
	 * </b></p>
	 * 
     * @return Company task ID
     */
	public String getOrgantaskid() {
		return organtaskid;
	}
	
    /**
	 * <p><b>
	 * [IN] Company task ID - Primary Key
	 * </b></p>
     *
     * @param organtaskid Company task ID
     */
	public void setOrgantaskid(String organtaskid) {
		this.organtaskid = organtaskid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Company ID (ZAPP_COMPANY - COMPANYID) 
	 * </b></p>
	 * 
     * @return Company ID
     */
	public String getCompanyid() {
		return companyid;
	}

    /**
	 * <p><b>
	 * [IN] Company ID (ZAPP_COMPANY - COMPANYID)
	 * </b></p>
     *
     * @param companyid Company ID
     */
	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}
	
    
    /**
	 * <p><b>
	 * [OUT] Dept. ID (ZAPP_DEPT - DEPTID)
	 * </b></p>
	 * 
     * @return Dept. ID
     */
	public String getDeptid() {
		return deptid;
	}

    /**
	 * <p><b>
	 * [IN] Dept. ID (ZAPP_DEPT - DEPTID)
	 * </b></p>
     *
     * @param deptid Dept. ID
     */
	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Task ID (ZARCH_TASK - TASKID) 
	 * </b></p>
	 * 
     * @return Task ID
     */
	public String getTaskid() {
		return taskid;
	}

    /**
	 * <p><b>
	 * [IN] Task ID (ZARCH_TASK - TASKID)
	 * </b></p>
     *
     * @param taskid Task ID
     */
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
    
    /**
	 * <p><b>
	 * [OUT] Target type (01:Company, 02:Department) 
	 * </b></p>
	 * 
     * @return Target type
     */
	public String getTobjtype() {
		return tobjtype;
	}

    /**
	 * <p><b>
	 * [IN] Target type (01:Company, 02:Department)
	 * </b></p>
     *
     * @param objtype Target type
     */
	public void setTobjtype(String tobjtype) {
		this.tobjtype = tobjtype;
	}
    
}
package com.zenithst.core.organ.vo;

import com.zenithst.archive.vo.ZArchTask;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended company task <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappOrganTaskExtend pIn = new ZappOrganTaskExtend();
*    ...
*    <br>    
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappOrganTaskExtend extends ZappOrganTask {

	/* */
    private ZappCompany zappCompany;	// Company
    private ZappDept zappDept;			// Department
    private ZArchTask zappTask;			// Task
    
	/**
	 * <p><b>
	 * [OUT] Company Object (ZAPP_COMPANY)
	 * </b></p>
	 * 
     * @return Company Object
     */ 
    public ZappCompany getZappCompany() {
		return zappCompany;
	}
    
    /**
	 * <p><b>
	 * [IN] Company Object (ZAPP_COMPANY)
	 * </b></p>
     *
     * @param zappCompany Company Object
     */ 
	public void setZappCompany(ZappCompany zappCompany) {
		this.zappCompany = zappCompany;
	}

	/**
	 * <p><b>
	 * [OUT] Department Object (ZAPP_DEPT)
	 * </b></p>
	 * 
     * @return Department Object
     */ 
	public ZappDept getZappDept() {
		return zappDept;
	}
    
    /**
	 * <p><b>
	 * [IN] Department Object (ZAPP_DEPT)
	 * </b></p>
     *
     * @param zappDept Department Object
     */ 
	public void setZappDept(ZappDept zappDept) {
		this.zappDept = zappDept;
	}
    
    /**
	 * <p><b>
	 * [OUT] Task Object (ZARCH_TASK)
	 * </b></p>
	 * 
     * @return Task Object
     */
	public ZArchTask getZappTask() {
		return zappTask;
	}
    
    /**
	 * <p><b>
	 * [IN] Task Object (ZARCH_TASK)
	 * </b></p>
     *
     * @param zappTask Task Object
     */ 
	public void setZappTask(ZArchTask zappTask) {
		this.zappTask = zappTask;
	}

}
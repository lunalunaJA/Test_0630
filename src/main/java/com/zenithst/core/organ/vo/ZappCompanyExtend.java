package com.zenithst.core.organ.vo;

import java.util.ArrayList;
import java.util.List;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended company <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappCompanyExtend pIn = new ZappCompanyExtend();
*    ...
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappCompanyExtend extends ZappCompany {

 	private static final long serialVersionUID = 1L;
	private ZappDept zappDept;	// Department
 	private List<ZappUser> zappCompanyUsers = new ArrayList<ZappUser>(); // Company Users
    
    /**
	 * <p><b>
	 * [OUT] Department
	 * </b></p>
	 * 
     * @return Department
     */	
 	public ZappDept getZappDept() {
		return zappDept;
	}
    
    /**
	 * <p><b>
	 * [IN] Department
	 * </b></p>
     *
     * @param zappDept Department
     */ 
	public void setZappDept(ZappDept zappDept) {
		this.zappDept = zappDept;
	}
	    
    /**
	 * <p><b>
	 * [OUT] Company Users
	 * </b></p>
	 * 
     * @return Company Users
     */	
	public List<ZappUser> getZappCompanyUsers() {
		return zappCompanyUsers;
	}
	    
    /**
	 * <p><b>
	 * [IN] Company Users
	 * </b></p>
     *
     * @param zappCompanyUsers List of Company Users
     */ 
	public void setZappCompanyUsers(List<ZappUser> zappCompanyUsers) {
		this.zappCompanyUsers = zappCompanyUsers;
	}

}
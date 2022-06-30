package com.zenithst.core.organ.vo;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended user <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappUser pIn = new ZappUser();
*    ...
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappUserExtend extends ZappUser {

	private static final long serialVersionUID = 1L;
	
	private ZappDeptUser zappDeptUser;			// Department User

	/**
	 * <p><b>
	 * [OUT] Dept. User
	 * </b></p>
	 * 
     * @return Dept. User
     */
	public ZappDeptUser getZappDeptUser() {
		return zappDeptUser;
	}

    /**
	 * <p><b>
	 * [IN] Dept. User
	 * </b></p>
     *
     * @param zappDeptUser Dept. User
     */
	public void setZappDeptUser(ZappDeptUser zappDeptUser) {
		this.zappDeptUser = zappDeptUser;
	}


}
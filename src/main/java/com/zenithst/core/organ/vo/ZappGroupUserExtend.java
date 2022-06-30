package com.zenithst.core.organ.vo;

import com.zenithst.core.system.vo.ZappCode;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended group user <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappGroupUserExtend pIn = new ZappGroupUserExtend();
*    ...
*    <br>    
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappGroupUserExtend extends ZappGroupUser {

    private ZappDept zappDept;			// Department
    private ZappUser zappUser;			// User
    private ZappCode zappPosition;		// Position
    private ZappCode zappDuty;			// Duty
    private ZappCode zappSecurity;		// Security level
    
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
	 * [OUT] User Object (ZAPP_USER)
	 * </b></p>
	 * 
     * @return User Object
     */
	public ZappUser getZappUser() {
		return zappUser;
	}
    
    /**
	 * <p><b>
	 * [IN] User Object (ZAPP_USER)
	 * </b></p>
     *
     * @param zappUser User Object
     */ 
	public void setZappUser(ZappUser zappUser) {
		this.zappUser = zappUser;
	}
    
    /**
	 * <p><b>
	 * [OUT] Position Object (ZAPP_CODE)
	 * </b></p>
	 * 
     * @return Position Object
     */
	public ZappCode getZappPosition() {
		return zappPosition;
	}
    
    /**
	 * <p><b>
	 * [IN] Position Object (ZAPP_CODE)
	 * </b></p>
     *
     * @param zappPosition Position Object
     */ 
	public void setZappPosition(ZappCode zappPosition) {
		this.zappPosition = zappPosition;
	}
    
    /**
	 * <p><b>
	 * [OUT] Duty Object (ZAPP_CODE)
	 * </b></p>
	 * 
     * @return Duty Object
     */
	public ZappCode getZappDuty() {
		return zappDuty;
	}
    
    /**
	 * <p><b>
	 * [IN] Duty Object (ZAPP_CODE)
	 * </b></p>
     *
     * @param zappDuty Duty Object
     */ 
	public void setZappDuty(ZappCode zappDuty) {
		this.zappDuty = zappDuty;
	}
    
    /**
	 * <p><b>
	 * [OUT] Security level Object (ZAPP_CODE)
	 * </b></p>
	 * 
     * @return Security level Object
     */
	public ZappCode getZappSecurity() {
		return zappSecurity;
	}
    
    /**
	 * <p><b>
	 * [IN] Security level Object (ZAPP_CODE)
	 * </b></p>
     *
     * @param zappSecurity Security level Object
     */ 
	public void setZappSecurity(ZappCode zappSecurity) {
		this.zappSecurity = zappSecurity;
	}
   
}
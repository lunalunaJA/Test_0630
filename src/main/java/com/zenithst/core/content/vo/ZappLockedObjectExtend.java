package com.zenithst.core.content.vo;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended locking <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappLockedObjectExtend pIn = new ZappLockedObjectExtend();
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappLockedObjectExtend extends ZappLockedObject {

    private String lockername;

	/**
	 * <p><b>
	 * [OUT] Locker name
	 * </b></p>
	 * 
     * @return Locker name
     */  
    public String getLockername() {
		return lockername;
	}
    
    /**
	 * <p><b>
	 * [IN] Locker name
	 * </b></p>
     *
     * @param lockername Locker name
     */ 
	public void setLockername(String lockername) {
		this.lockername = lockername;
	}


}
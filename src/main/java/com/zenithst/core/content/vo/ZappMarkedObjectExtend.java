package com.zenithst.core.content.vo;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended marking <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappMarkedObjectExtend pIn = new ZappMarkedObjectExtend();
*    ...

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappMarkedObjectExtend extends ZappMarkedObject {

    private String classname;
    private String lockername;
    

    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */
    public ZappMarkedObjectExtend() {}
 
	/**
	 * <p><b>
	 * [OUT] Classification name
	 * </b></p>
	 * 
     * @return Classification name
     */
	public String getClassname() {
		return classname;
	}

	/**
	 * <p><b>
	 * [IN] Classification name
	 * </b></p>
     *
     * @param classname Classification name
     */
	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getLockername() {
		return lockername;
	}

	public void setLockername(String lockername) {
		this.lockername = lockername;
	}
	
	
	
}
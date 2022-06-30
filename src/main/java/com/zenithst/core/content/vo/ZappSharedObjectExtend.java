package com.zenithst.core.content.vo;

import com.zenithst.core.classification.vo.ZappClassification;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended sharing <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappSharedObjectExtend pIn = new ZappSharedObjectExtend();
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/


public class ZappSharedObjectExtend extends ZappSharedObject {

	private String sharername;
	private String readername;
    
    /**
	 * <p><b>
	 * [OUT] Sharer name
	 * </b></p>
	 * 
     * @return Sharer name
     */
    public String getSharername() {
		return sharername;
	}
    
    /**
	 * <p><b>
	 * [IN] Sharer name
	 * </b></p>
     *
     * @param sharername Sharer name
     */ 
	public void setSharername(String sharername) {
		this.sharername = sharername;
	}
    
    /**
	 * <p><b>
	 * [OUT] Reader name
	 * </b></p>
	 * 
     * @return Reader name
     */
	public String getReadername() {
		return readername;
	}
    
    /**
	 * <p><b>
	 * [IN] Reader name
	 * </b></p>
     *
     * @param readername Reader name
     */ 
	public void setReadername(String readername) {
		this.readername = readername;
	}

	private ZappBundle zappBundle;
    private ZappFile zappFile;
    private ZappClassification zappClassification;
    
    
    /**
	 * <p><b>
	 * [OUT] Bundle
	 * </b></p>
	 * 
     * @return Bundle
     */
	public ZappBundle getZappBundle() {
		return zappBundle;
	}
    
    /**
	 * <p><b>
	 * [IN] Bundle
	 * </b></p>
     *
     * @param zappBundle ZappBundle Bundle
     */ 
	public void setZappBundle(ZappBundle zappBundle) {
		this.zappBundle = zappBundle;
	}
    
    /**
	 * <p><b>
	 * [OUT] File
	 * </b></p>
	 * 
     * @return File
     */
	public ZappFile getZappFile() {
		return zappFile;
	}
    
    /**
	 * <p><b>
	 * [IN] File
	 * </b></p>
     *
     * @param zappFile ZappFile File
     */
	public void setZappFile(ZappFile zappFile) {
		this.zappFile = zappFile;
	}
    
    /**
	 * <p><b>
	 * [OUT] Classification
	 * </b></p>
	 * 
     * @return Classification
     */
	public ZappClassification getZappClassification() {
		return zappClassification;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification
	 * </b></p>
     *
     * @param zappClassification ZappClassification Classification
     */
	public void setZappClassification(ZappClassification zappClassification) {
		this.zappClassification = zappClassification;
	}
    
    
}
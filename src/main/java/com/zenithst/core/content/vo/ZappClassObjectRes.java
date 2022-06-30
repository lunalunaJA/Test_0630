package com.zenithst.core.content.vo;

import java.util.ArrayList;
import java.util.List;

import com.zenithst.core.acl.vo.ZappClassAcl;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content-classification (OUT) <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappClassObjectRes pIn = new ZappClassObjectRes();
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappClassObjectRes extends ZappClassObject {
	
	private ZappClassAcl zappClassAcl;
	private List<ZappClassAcl> zappClassAcls = new ArrayList<ZappClassAcl>();

    /**
	 * <p><b>
	 * [OUT] Classification access control
	 * </b></p>
	 * 
     * @return Classification access control
     */
	public ZappClassAcl getZappClassAcl() {
		return zappClassAcl;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification access control
	 * </b></p>
     *
     * @param zappClassAcl ZappClassAcl Classification access control
     */ 
	public void setZappClassAcl(ZappClassAcl zappClassAcl) {
		this.zappClassAcl = zappClassAcl;
	}
	
    /**
	 * <p><b>
	 * [OUT] Classification access control list
	 * </b></p>
	 * 
     * @return Classification access control list
     */
	public List<ZappClassAcl> getZappClassAcls() {
		return zappClassAcls;
	}
    
    /**
	 * <p><b>
	 * [IN] Classification access control list
	 * </b></p>
     *
     * @param zappClassAcls List&lt;ZappClassAcl&gt; Classification access control list
     */ 
	public void setZappClassAcls(List<ZappClassAcl> zappClassAcls) {
		this.zappClassAcls = zappClassAcls;
	}
	
}

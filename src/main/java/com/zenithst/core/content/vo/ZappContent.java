package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for content <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappContent pIn = new ZappContent();
*    ...
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappContent extends ZappDomain {
	
	/* Whether conditions are included or not */
	private String hasconds = NO;

	public String getHasconds() {
		return hasconds;
	}

	public void setHasconds(String hasconds) {
		this.hasconds = hasconds;
	}

}

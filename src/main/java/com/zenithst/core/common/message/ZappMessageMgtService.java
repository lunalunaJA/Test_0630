package com.zenithst.core.common.message;

import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage messages. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappMessageMgtService {

	String getMessage(String key, String lang);
	String getCaption(String key, String lang);
	
	ZstFwResult getMessage(ZstFwResult zstFwResult, String lang);
}

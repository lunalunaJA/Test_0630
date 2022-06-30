package com.zenithst.core.tag.api;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappMgtService;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage tag info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappTagMgtService extends ZappMgtService {

	ZstFwResult mapTaskTag(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult createDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult dropDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult setDynamicValidator(ZappAuth pObjAuth, Object pObjImg, Object pObjTags, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult setDynamicKey(ZappAuth pObjAuth, Object pObjImg, Object pObjTags, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult setDynamicFilter(ZappAuth pObjAuth, Object pObjImg, Object pObjTags, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	
}

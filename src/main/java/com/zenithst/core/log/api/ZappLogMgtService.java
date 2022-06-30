package com.zenithst.core.log.api;

import java.sql.SQLException;
import java.util.Map;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappMgtService;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage log info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappLogMgtService extends ZappMgtService {

	ZstFwResult getLogs(ZappAuth pObjAuth, Map<String, Object> pObjMap, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult leaveLog(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZappContentRes initLogRes(Object pObjContent, ZappConts.ACTION pObjAction) throws ZappException;
	
	ZstFwResult selectObjectExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
}

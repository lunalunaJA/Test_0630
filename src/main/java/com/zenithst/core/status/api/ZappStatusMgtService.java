package com.zenithst.core.status.api;

import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.status.vo.ZappStatus;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage statistics info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappStatusMgtService {

	ZstFwResult getProcessStatusList(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult getProcessStatusListAll(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult getHoldStatusList(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/**
	 * Store APM info.
	 * @param pIn
	 * @return
	 * @throws  ZappException, SQLException
	 */
	ZstFwResult saveApms(ZappAuth pObjAuth, ZappApm pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/**
	 * Delete APM info.
	 * @param pIn
	 * @return
	 * @throws  ZappException, SQLException
	 */
	ZstFwResult deleteApms(ZappAuth pObjAuth, ZappApm pIn, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/**
	 * Inquiry APM info.
	 * @param pIn
	 * @return
	 * @throws  ZappException, SQLException
	 */
	ZstFwResult viewApmsList(ZappAuth pObjAuth, ZappApm pIn, ZstFwResult pObjRes) throws  ZappException, SQLException;
	
	
}

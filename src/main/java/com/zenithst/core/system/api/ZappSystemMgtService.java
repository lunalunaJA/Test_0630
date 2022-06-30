package com.zenithst.core.system.api;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappMgtService;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage system info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappSystemMgtService extends ZappMgtService {
	
	/**
	 * Disable
	 * @param pObjAuth
	 * @param pObjf
	 * @param pObjw
	 * @param pObjRes
	 * @return
	 * @throws ZappException
	 * @throws SQLException
	 */
	ZstFwResult disableObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/**
	 * Enable
	 * @param pObjAuth
	 * @param pObjf
	 * @param pObjw
	 * @param pObjRes
	 * @return
	 * @throws ZappException
	 * @throws SQLException
	 */
	ZstFwResult enableObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/**
	 * Discard
	 * @param pObjAuth
	 * @param pObjf
	 * @param pObjw
	 * @param pObjRes
	 * @return
	 * @throws ZappException
	 * @throws SQLException
	 */
	ZstFwResult discardObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;	
	
	/**
	 * Reorder
	 * @param pObjAuth
	 * @param pObjf
	 * @param pObjw
	 * @param pObjRes
	 * @return
	 * @throws ZappException
	 * @throws SQLException
	 */
	ZstFwResult reorderObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/**
	 * Initialization
	 * @param pObjAuth
	 * @param pObj
	 * @param pObjRes
	 * @return
	 * @throws ZappException
	 * @throws SQLException
	 */
	ZstFwResult initObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	
}

package com.zenithst.core.organ.service;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappService;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define basic processes of organization info. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappOrganService extends ZappService {

	/* Extend */
	ZstFwResult rSingleRowExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRowsAclExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw1, Object pObjw2, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult rMultiRowsDown(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRowsUp(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRowsByUser(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rNextPriority(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult upwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult downwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException, SQLException;

	/* Refresh View */
	ZstFwResult refreshView(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* */
	boolean dMultiRowsByCompany(ZappAuth pObjAuth, Object pObjw, String pCompanyid) throws ZappException, SQLException;

}

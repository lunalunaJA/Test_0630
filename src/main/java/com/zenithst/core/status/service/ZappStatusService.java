package com.zenithst.core.status.service;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappService;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.status.vo.ZappStatus;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define basic processes of statistics info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappStatusService extends ZappService {

	boolean createTmpDateTbl(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	boolean dropTmpDateTbl(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	boolean insertTmpDate(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZappStatus getWeeks(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult getProcessStatusList(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult getProcessStatusListAll(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* APM */
	ZstFwResult getDbStatus(ZappAuth pObjAuth, ZappApm pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult getDbLock(ZappAuth pObjAuth, ZappApm pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
}

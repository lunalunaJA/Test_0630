package com.zenithst.core.classification.service;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappService;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define basic processes of classification info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappClassificationService extends ZappService {

	ZstFwResult rMultiRowsDown(ZappAuth pObjAuth, Object pObjf, Object pObjw,  Object pObjwh, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult rMultiRowsUp(ZappAuth pObjAuth, Object pObjf, Object pObjw, Object pObjwh, ZstFwResult pObjRes) throws ZappException, SQLException;
	String rClassPathUp(ZappAuth pObjAuth, String pObjw, boolean pIsLast) throws ZappException, SQLException;
	
	ZstFwResult rMarkedCount(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult rMarkedList(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException;

	ZstFwResult rMultiAffiliationRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult rNextPriority(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult upwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult downwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException, SQLException;
	
}

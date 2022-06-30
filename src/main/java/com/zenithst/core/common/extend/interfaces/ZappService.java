package com.zenithst.core.common.extend.interfaces;

import java.sql.SQLException;
import java.util.List;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define basic processes. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappService {

	/* Basic */
	ZstFwResult cSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException, SQLException;
	ZstFwResult cuSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult cMultiRows(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult uSingleRow(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult dSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rExist(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	boolean cSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException, SQLException;
	boolean cuSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException, SQLException;
	boolean cMultiRows(ZappAuth pObjAuth, Object pObjs) throws ZappException, SQLException;
	Object rSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException;
	List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException;
	List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException;
	boolean uSingleRow(ZappAuth pObjAuth, Object pObj) throws ZappException, SQLException;
	boolean uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw) throws ZappException, SQLException;
	boolean uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw) throws ZappException, SQLException;
	boolean dSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException;
	boolean dMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException;
	boolean dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException;
	int rCountRows(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException;
	int rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException;
	boolean rExist(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException;
	boolean rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException;	
	
}

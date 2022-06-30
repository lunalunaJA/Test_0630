package com.zenithst.core.common.service;

import java.util.List;
import java.util.Map;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.vo.ZappCommon;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define common processes. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappCommonService {

	/* DDL */
	ZstFwResult existDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult createDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult alterDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult dropDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException;
	
	boolean existDdl(ZappAuth pObjAuth, Object pObj) throws ZappException;
	boolean createDdl(ZappAuth pObjAuth, Object pObj) throws ZappException;
	boolean alterDdl(ZappAuth pObjAuth, Object pObj) throws ZappException;
	boolean dropDdl(ZappAuth pObjAuth, Object pObj) throws ZappException;
	
	List<ZappCommon> usingOtherTable(ZappAuth pObjAuth, Map<String, Object> pMap) throws ZappException;
	
	/* Sequence */
	boolean createSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException;
	boolean updateSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException;
	int selectSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException;
	boolean deleteSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException;
	
}

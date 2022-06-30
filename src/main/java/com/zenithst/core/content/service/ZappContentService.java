package com.zenithst.core.content.service;

import java.sql.SQLException;
import java.util.List;

import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchVersion;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappService;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define basic processes of content info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappContentService extends ZappService {
	
	ZstFwResult ceSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult rContent(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;

	
	ZstFwResult rPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult rPhysicalCount(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException;

	ZstFwResult rNonPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult rNonPhysicalCount(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException;

	ZstFwResult rMultiRowsFileName(ZappAuth pObjAuth, ZArchMFile pObjFile, ZappClassification pObjClass, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult rMultiRowsMaxVersionFile(ZappAuth pObjAuth, ZArchMFile pObjFile, ZArchVersion pObjVersion, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rMultiRowsVersion(ZappAuth pObjAuth, ZArchVersion pObjVersion, ZArchFile pObjFile, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* FTR */
	ZstFwResult cFTRTbl(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult dFTRTbl(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult cFTRRows(ZappAuth pObjAuth, List<ZappContentRes> pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult rFTRCount(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult rFTRList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException;
}

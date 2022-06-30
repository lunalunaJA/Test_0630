package com.zenithst.core.acl.api;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappMgtService;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage access control info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappAclMgtService extends ZappMgtService {

	ZstFwResult selectExtendObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* *************************************************************************************************** */
	
	ZstFwResult checkObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult optimizeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult manualObject(ZappAuth pObjAuth, Object pObj, int pAcl, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* *************************************************************************************************** */
	boolean isSuperManager(ZappAuth pObjAuth);
	boolean isCompanyManager(ZappAuth pObjAuth);
	boolean isDeptManager(ZappAuth pObjAuth, String pDeptid);
	boolean isAccessFree(ZappAuth pObjAuth);
	boolean isAccessFreeContent(ZappAuth pObjAuth) throws ZappException, SQLException;
	
}

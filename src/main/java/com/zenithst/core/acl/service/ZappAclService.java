package com.zenithst.core.acl.service;

import java.util.List;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappService;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define basic processes of access control info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappAclService extends ZappService {

	/* Extended */
	ZstFwResult rMultiExtendRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException;
	ZstFwResult rMultiExtendRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException;
	List<Object> rMultiExtendRows(ZappAuth pObjAuth, Object pObjw) throws ZappException;
	List<Object> rMultiExtendRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException;
	
	/* All */
	ZstFwResult rMultiContetentRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException;
}

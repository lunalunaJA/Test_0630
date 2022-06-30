package com.zenithst.core.content.service;

import java.io.IOException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.framework.domain.ZstFwResult;


/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define basic processes of FTR info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappFtrService {
	
	ZstFwResult executeIndex(ZappAuth pObjAuth, ZappContentPar pObj, ZstFwResult pObjRes) throws ZappException, IOException;
	ZstFwResult executeSearching(ZappAuth pObjAuth, ZappContentPar pObj, ZstFwResult pObjRes) throws ZappException, IOException;
	ZstFwResult executeDeleting(ZappAuth pObjAuth, ZappContentPar pObj, ZstFwResult pObjRes) throws ZappException, IOException;
	ZstFwResult existIndex(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, IOException;
}

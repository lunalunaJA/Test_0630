package com.zenithst.core.workflow.api;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappMgtService;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage workflow info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappWorkflowMgtService extends ZappMgtService {

	ZstFwResult commenceWorkflow(ZappAuth pObjAuth, Object pObjContent, ZappClassification pObjFolder, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult proceedWorkflow(ZappAuth pObjAuth, Object pObjContent, ZappClassification pObjFolder, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult haltWorkflow(ZappAuth pObjAuth, Object pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	boolean doApply(int pWfreqired, ZappConts.ACTION pAction);
	
	
}

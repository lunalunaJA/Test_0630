package com.zenithst.core.content.api;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.framework.domain.ZstFwResult;

public interface ZappContentWorkflowMgtService {

	ZstFwResult approveContent(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult returnContent(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult undoContent(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult withdrawContent(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	String genCode(ZappContentPar pObj);
}

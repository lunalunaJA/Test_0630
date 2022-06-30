package com.zenithst.core.log.service;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappService;
import com.zenithst.framework.domain.ZstFwResult;

public interface ZappLogService extends ZappService {

	ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException;
}

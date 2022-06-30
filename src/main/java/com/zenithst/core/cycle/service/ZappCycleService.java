package com.zenithst.core.cycle.service;

import java.util.Map;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.framework.domain.ZstFwResult;

public interface ZappCycleService {

	ZstFwResult callDailyStatics(ZappAuth pObjAuth, Map<String, Object> pObjw, ZstFwResult pObjRes) throws ZappException;

}

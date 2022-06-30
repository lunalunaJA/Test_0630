package com.zenithst.core.cycle.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.cycle.mapper.ZappCycleMapper;
import com.zenithst.framework.domain.ZstFwResult;

@Service("zappCycleService")
public class ZappCycleServiceImpl extends ZappService implements ZappCycleService {

	/* Mapper */
	@Autowired
	private ZappCycleMapper cycleMapper;			// 분류권한
	
	/* Service */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/**
	 * 일별 통계 기록 프러시져를 호출한다.
	 * @param pObjAuth - 인증 Object
	 * @return pObjRes - 결과 Object
	 */
	public ZstFwResult callDailyStatics(ZappAuth pObjAuth, Map<String, Object> pObjw, ZstFwResult pObjRes) throws ZappException {

		cycleMapper.callDailyStatics(pObjw);
		
		return pObjRes;
	}

	
}

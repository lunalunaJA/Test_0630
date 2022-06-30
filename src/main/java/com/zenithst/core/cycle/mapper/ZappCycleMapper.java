package com.zenithst.core.cycle.mapper;

import java.util.Map;

import com.zenithst.core.common.extend.ZappMapper;

public interface ZappCycleMapper extends ZappMapper {

	/* */
	void callDailyStatics(Map<String, Object> params);
 
    
}
package com.zenithst.core.status.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.status.vo.ZappStatus;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map statistics data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappStatusMapper extends ZappMapper {

    int createTmpDateTbl(Map<String, Object> params);
    int dropTmpDateTbl(Map<String, Object> params);
    int insertTmpDate(Map<String, Object> params);
	ZappStatus getWeeks(Map<String, Object> params);
	List<ZappStatus> selectQuarter(Map<String, Object> params);
	List<ZappStatus> selectHalf(Map<String, Object> params);
	
	/* */
	List<ZappStatus> selectProcessStatusList(Map<String, Object> params);
	
	/* */
	List<ZappStatus> selectProcessStatusList_All(@Param("status") ZappStatus pStatus, @Param("dept") ZappDynamic ddept, @Param("user")  ZappDynamic duser);

}
package com.zenithst.core.log.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.log.vo.ZappCycleLog;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map cycle log data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappCycleLogMapper extends ZappMapper  {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappCycleLog vo);
    int insertu(ZappCycleLog vo);
    @SuppressWarnings("rawtypes")
    List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
    List selectExtendByDynamic(@Param("auth") ZappAuth auth
    						 , @Param("qryopt") ZappQryOpt qryopt			// Query options (Sorting, Paging Info.)
    						 , @Param("dynamic") ZappDynamic dynamic);
    ZappCycleLog selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappCycleLog vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappCycleLog vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
    
}
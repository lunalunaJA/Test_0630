package com.zenithst.core.workflow.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.workflow.vo.ZappWorkflowObject;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map content-workflow data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappWorkflowObjectMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappWorkflowObject vo);
    int insertu(ZappWorkflowObject vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappWorkflowObject selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappWorkflowObject vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappWorkflowObject vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
}
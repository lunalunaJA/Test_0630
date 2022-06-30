package com.zenithst.core.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.content.vo.ZappContentWorkflow;

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

public interface ZappContentWorkflowMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappContentWorkflow vo);
    int insertu(ZappContentWorkflow vo);
    int inserte(ZappContentWorkflow vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
	List selectExtendByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappContentWorkflow selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappContentWorkflow vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappContentWorkflow vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);

}
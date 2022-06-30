package com.zenithst.core.organ.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.system.vo.ZappEnv;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map group data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/


public interface ZappGroupMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappGroup vo);
    int insertu(ZappGroup vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappGroup selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappGroup vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappGroup vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);

    /* ************************************************************ */
    
    int selectNextPriority(@Param("dynamic") ZappDynamic dynamic);
    int downwardPriority(@Param("start") ZappGroup p1, @Param("end") ZappGroup p2);
    int upwardPriority(@Param("start") ZappGroup p1, @Param("end") ZappGroup p2);
    
    @SuppressWarnings("rawtypes")
	List selectDownByDynamic(@Param("auth") ZappAuth auth				// Authentication
						   , @Param("dynamic") ZappDynamic dynamic);	
    @SuppressWarnings("rawtypes")
	List selectUpByDynamic(@Param("auth") ZappAuth auth					// Authentication
					     , @Param("dynamic") ZappDynamic dynamic);
    
    List selectByUser(@Param("auth") ZappAuth auth
    				, @Param("deptRange") ZappEnv deptRange
    				, @Param("dynamic") ZappDynamic dynamic);
    
    /* ************************************************************ */
    void refreshView(ZappGroup vo);
}
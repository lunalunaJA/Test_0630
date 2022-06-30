package com.zenithst.core.organ.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappDept;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map dept. data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/


public interface ZappDeptMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappDept vo);
    int insertu(ZappDept vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappDept selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappDept vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappDept vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);

    /* ************************************************************ */
    
    int selectNextPriority(@Param("dynamic") ZappDynamic dynamic);
    int downwardPriority(@Param("start") ZappDept p1, @Param("end") ZappDept p2);
    int upwardPriority(@Param("start") ZappDept p1, @Param("end") ZappDept p2);
    
    @SuppressWarnings("rawtypes")
	List selectDownByDynamic(@Param("auth") ZappAuth auth				// Authentication
						   , @Param("dynamic") ZappDynamic dynamic);	
    @SuppressWarnings("rawtypes")
	List selectUpByDynamic(@Param("auth") ZappAuth auth					// Authentication
					     , @Param("dynamic") ZappDynamic dynamic);
}
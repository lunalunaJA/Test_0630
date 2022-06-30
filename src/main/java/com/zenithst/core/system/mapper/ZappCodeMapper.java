package com.zenithst.core.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.system.vo.ZappCode;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map code data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappCodeMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappCode vo);
    int insertu(ZappCode vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappCode selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappCode vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappCode vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
    /* ************************************************************ */
    
    int selectNextPriority(@Param("dynamic") ZappDynamic dynamic);
    int downwardPriority(@Param("start") ZappCode p1, @Param("end") ZappCode p2);
    int upwardPriority(@Param("start") ZappCode p1, @Param("end") ZappCode p2);
    int initCode(ZappCompany vo);
    
}
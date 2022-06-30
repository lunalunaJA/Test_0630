package com.zenithst.core.organ.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappOrganTaskExtend;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map company task data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappOrganTaskMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappOrganTask vo);
    int insertu(ZappOrganTask vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
	List selectExtendByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappOrganTask selectByPrimaryKey(String pk);
    ZappOrganTaskExtend selectExtendByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappOrganTask vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappOrganTask vo);
    String exists(@Param("dynamic")ZappDynamic dynamic);
}

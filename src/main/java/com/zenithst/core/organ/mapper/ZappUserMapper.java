package com.zenithst.core.organ.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappUser;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map user data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappUserMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappUser vo);
    int insertu(ZappUser vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappUser selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappUser vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappUser vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);

}
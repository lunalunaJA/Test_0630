package com.zenithst.core.tag.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.tag.vo.ZappTaskTag;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map task-tag data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappTaskTagMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappTaskTag vo);
    int insertu(ZappTaskTag vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappTaskTag selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappTaskTag vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappTaskTag vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);

}
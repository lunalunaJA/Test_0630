package com.zenithst.core.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.content.vo.ZappComment;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map comment data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappCommentMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappComment vo);
    int insertu(ZappComment vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappComment selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappComment vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappComment vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
}
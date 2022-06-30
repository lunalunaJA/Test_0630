package com.zenithst.core.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.content.vo.ZappKeyword;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map keyword data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappKeywordMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappKeyword vo);
    int insertu(ZappKeyword vo);
    int inserte(ZappKeyword vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
	List selectExtendByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappKeyword selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappKeyword vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappKeyword vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);

}
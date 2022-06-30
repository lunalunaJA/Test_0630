package com.zenithst.core.tag.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.tag.vo.ZappImg;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map image data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappImgMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappImg vo);
    int insertu(ZappImg vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappImg selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappImg vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappImg vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);

}
package com.zenithst.core.classification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.classification.vo.ZappAdditoryClassification;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map additional classification data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappAdditoryClassificationMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappAdditoryClassification vo);
    int insertu(ZappAdditoryClassification vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappAdditoryClassification selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappAdditoryClassification vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappAdditoryClassification vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
 
}
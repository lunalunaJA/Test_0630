package com.zenithst.core.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.content.vo.ZappAdditoryBundle;
import com.zenithst.core.content.vo.ZappBundle;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map additional bundle data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappAdditoryBundleMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappAdditoryBundle vo);
    int insertu(ZappAdditoryBundle vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappAdditoryBundle selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappAdditoryBundle vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappAdditoryBundle vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
}
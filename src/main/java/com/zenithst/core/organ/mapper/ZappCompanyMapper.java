package com.zenithst.core.organ.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappCompany;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map comnpany data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappCompanyMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappCompany vo);
    int insertu(ZappCompany vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappCompany selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappCompany vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappCompany vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    

}
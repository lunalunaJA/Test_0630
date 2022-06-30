package com.zenithst.core.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.vo.ZappEnv;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map preference data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappEnvMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int deleteByCompany(String companyid);
    int insert(ZappEnv vo);
    int insertu(ZappEnv vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappEnv selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappEnv vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappEnv vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
    int initEnv(ZappCompany vo);
    int initUserEnv(ZappUser vo);
    
}
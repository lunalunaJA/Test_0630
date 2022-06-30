package com.zenithst.core.acl.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map classification access control data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappClassAclMapper extends ZappMapper  {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappClassAcl vo);
    int insertu(ZappClassAcl vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
    List selectExtendByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappClassAcl selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappClassAcl vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappClassAcl vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
}
package com.zenithst.core.organ.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappGroupUserExtend;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map group user data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/


public interface ZappGroupUserMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int deleteByCompany(String companyid);
    int insert(ZappGroupUser vo);
    int insertu(ZappGroupUser vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
	List selectExtendByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
	List selectExtendAclByDynamic(@Param("dynamic") ZappDynamic dynamic1, @Param("groupUser") ZappDynamic dynamic2);
    ZappGroupUser selectByPrimaryKey(String pk);
    ZappGroupUserExtend selectExtendByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappGroupUser vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappGroupUser vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
    void refreshView(ZappGroupUser vo);
}
package com.zenithst.core.organ.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map dept. user data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappDeptUserMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int deleteByCompany(String companyid);
    int insert(ZappDeptUser vo);
    int insertu(ZappDeptUser vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
	List selectExtendByDynamic(@Param("qryopt") ZappQryOpt qryopt			// Query options (Sorting, Paging Info.)
							 , @Param("dynamic") ZappDynamic dynamic);
    @SuppressWarnings("rawtypes")
	List selectAllExtendByDynamic(@Param("qryopt") ZappQryOpt qryopt		// Query options (Sorting, Paging Info.)
							    , @Param("dept") ZappDynamic dept
							    , @Param("user") ZappDynamic user
							    , @Param("deptuser") ZappDynamic deptuser);
    ZappDeptUser selectByPrimaryKey(String pk);
    ZappDeptUserExtend selectExtendByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappDeptUser vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappDeptUser vo);
    String exists(@Param("dynamic")ZappDynamic dynamic);

}
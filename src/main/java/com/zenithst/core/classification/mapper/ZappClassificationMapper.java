package com.zenithst.core.classification.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.system.vo.ZappEnv;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map classification data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappClassificationMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappClassification vo);
    int insertu(ZappClassification vo);
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappClassification selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappClassification vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappClassification vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
    /* ************************************************************ */
    
    int selectNextPriority(@Param("dynamic") ZappDynamic dynamic);
    int downwardPriority(@Param("start") ZappClassification p1, @Param("end") ZappClassification p2);
    int upwardPriority(@Param("start") ZappClassification p1, @Param("end") ZappClassification p2);
    
    @SuppressWarnings("rawtypes")
	List selectDownByDynamic(@Param("auth") ZappAuth auth				// Authentication
						   , @Param("applyAcl") ZappEnv applyAcl		// Apply access control info. or not
						   , @Param("deptRange") ZappEnv deptRange		// Department scope
						   , @Param("viewlevel") ZappEnv viewlevel		// View level
						   , @Param("dynamic") ZappDynamic dynamic
						   , @Param("class") ZappClassification hierarchy);	
    @SuppressWarnings("rawtypes")
	List selectUpByDynamic(@Param("auth") ZappAuth auth					// Authentication
					     , @Param("applyAcl") ZappEnv applyAcl			// Apply access control info. or not
					     , @Param("deptRange") ZappEnv deptRange		// Department scope
					     , @Param("dynamic") ZappDynamic dynamic
					     , @Param("class") ZappClassification hierarchy);

	int selectMarkedCountByDynamic(@Param("auth") ZappAuth auth
						 	     , @Param("class") ZappDynamic classes
						 	     , @Param("additoryclass") ZappDynamic additoryclasses);
    @SuppressWarnings("rawtypes")
	List selectMarkedListByDynamic(@Param("auth") ZappAuth auth
						 		 , @Param("qryopt") ZappQryOpt qryopt	// Query options (Sorting, Paging Info.)
						 		 , @Param("class") ZappDynamic classes
						 		 , @Param("additoryclass") ZappDynamic additoryclasses);
    
    @SuppressWarnings("rawtypes")
	List selectAffiliation(ZappClassification vo);
    
    /* Inquiry class path (upward) */
    String selectCPath(String pk);
}
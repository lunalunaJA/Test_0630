package com.zenithst.core.content.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.system.vo.ZappEnv;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map content data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappContentMapper  {

	ZappContentRes selectContent(@Param("auth") ZappAuth auth, @Param("content") ZappContentPar content);
	
	int selectPhysicalCount(@Param("auth") ZappAuth auth
						 , @Param("applyAcl") ZappEnv applyAcl
						 , @Param("queryObj") ZappEnv queryObj
						 , @Param("deptRange") ZappEnv deptRange
						 , @Param("bundle") ZappDynamic bundle
						 , @Param("file") ZappDynamic file
//						 , @Param("mfile") ZappDynamic mfile
						 , @Param("mfile") ZArchMFile mfile
						 , @Param("class") ZappDynamic classes
//						 , @Param("classobj") ZappDynamic classobj
						 , @Param("link") ZappDynamic link
						 , @Param("share") ZappDynamic share
						 , @Param("lock") ZappDynamic lock
						 , @Param("keyword") ZappDynamic keyword
						 , @Param("mark") ZappDynamic mark
						 , @Param("additorybundle") ZappDynamic additorybundle);
    @SuppressWarnings("rawtypes")
	List selectPhysicalList(@Param("auth") ZappAuth auth
						 , @Param("qryopt") ZappQryOpt qryopt			// Query options (Sorting, Paging Info.)
						 , @Param("applyAcl") ZappEnv applyAcl
						 , @Param("queryObj") ZappEnv queryObj
						 , @Param("deptRange") ZappEnv deptRange
						 , @Param("bundle") ZappDynamic bundle
						 , @Param("file") ZappDynamic file
//						 , @Param("mfile") ZappDynamic mfile
						 , @Param("mfile") ZArchMFile mfile
						 , @Param("class") ZappDynamic classes
//						 , @Param("classobj") ZappDynamic classobj
						 , @Param("link") ZappDynamic link
						 , @Param("share") ZappDynamic share
						 , @Param("lock") ZappDynamic lock
						 , @Param("keyword") ZappDynamic keyword
						 , @Param("mark") ZappDynamic mark
						 , @Param("additorybundle") ZappDynamic additorybundle);
    
	int selectNonPhysicalCount(@Param("auth") ZappAuth auth
							 , @Param("applyAcl") ZappEnv applyAcl
							 , @Param("queryObj") ZappEnv queryObj
							 , @Param("deptRange") ZappEnv deptRange
							 , @Param("bundle") ZappDynamic bundle
							 , @Param("file") ZappDynamic file
//							 , @Param("mfile") ZappDynamic mfile
							 , @Param("mfile") ZArchMFile mfile
							 , @Param("keyword") ZappDynamic keyword
							 , @Param("additorybundle") ZappDynamic additorybundle);
;
	@SuppressWarnings("rawtypes")
	List selectNonPhysicalList(@Param("auth") ZappAuth auth
							 , @Param("qryopt") ZappQryOpt qryopt	// Query options (Sorting, Paging Info.)
							 , @Param("applyAcl") ZappEnv applyAcl
							 , @Param("queryObj") ZappEnv queryObj
							 , @Param("deptRange") ZappEnv deptRange
							 , @Param("bundle") ZappDynamic bundle
							 , @Param("file") ZappDynamic file
//							 , @Param("mfile") ZappDynamic mfile
							 , @Param("mfile") ZArchMFile mfile
							 , @Param("keyword") ZappDynamic keyword
							 , @Param("additorybundle") ZappDynamic additorybundle);
	
	int selectNonPhysicalCount_(@Param("auth") ZappAuth auth
							 , @Param("applyAcl") ZappEnv applyAcl
							 , @Param("queryObj") ZappEnv queryObj
							 , @Param("deptRange") ZappEnv deptRange
							 , @Param("bundle") ZappDynamic bundle
							 , @Param("file") ZappDynamic file
				//			 , @Param("mfile") ZappDynamic mfile
							 , @Param("mfile") ZArchMFile mfile
							 , @Param("keyword") ZappDynamic keyword
							 , @Param("additorybundle") ZappDynamic additorybundle);
	
	@SuppressWarnings("rawtypes")
	List selectNonPhysicalList_(@Param("auth") ZappAuth auth
							 , @Param("qryopt") ZappQryOpt qryopt	// Query options (Sorting, Paging Info.)
							 , @Param("applyAcl") ZappEnv applyAcl
							 , @Param("queryObj") ZappEnv queryObj
							 , @Param("deptRange") ZappEnv deptRange
							 , @Param("bundle") ZappDynamic bundle
							 , @Param("file") ZappDynamic file
//							 , @Param("mfile") ZappDynamic mfile
							 , @Param("mfile") ZArchMFile mfile
							 , @Param("keyword") ZappDynamic keyword
							 , @Param("additorybundle") ZappDynamic additorybundle);

	/* FTR */
	int selectFTRCount(@Param("auth") ZappAuth auth
					 , @Param("applyAcl") ZappEnv applyAcl
					 , @Param("queryObj") ZappEnv queryObj
					 , @Param("deptRange") ZappEnv deptRange
					 , @Param("ftr") ZappEnv ftr
					 , @Param("bundle") ZappDynamic bundle
					 , @Param("file") ZappDynamic file
					 , @Param("mfile") ZArchMFile mfile
					 , @Param("keyword") ZappDynamic keyword
					 , @Param("additorybundle") ZappDynamic additorybundle
					 , @Param("class") ZappDynamic classes);
	
	List selectFTRList(@Param("auth") ZappAuth auth
					 , @Param("qryopt") ZappQryOpt qryopt	// Query options (Sorting, Paging Info.)
					 , @Param("applyAcl") ZappEnv applyAcl
					 , @Param("queryObj") ZappEnv queryObj
					 , @Param("deptRange") ZappEnv deptRange
					 , @Param("ftr") ZappEnv ftr
					 , @Param("bundle") ZappDynamic bundle
					 , @Param("file") ZappDynamic file
					 , @Param("mfile") ZArchMFile mfile
					 , @Param("keyword") ZappDynamic keyword
					 , @Param("additorybundle") ZappDynamic additorybundle
					 , @Param("class") ZappDynamic classes);
	
	int createTmpFTRTbl(Map<String, Object> params);
	int dropTmpFTRTbl(Map<String, Object> params);
	int insertFTR(Map<String, Object> params);

}
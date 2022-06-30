package com.zenithst.core.content.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchVersion;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.extend.ZappMapper;
import com.zenithst.core.content.vo.ZappFile;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map file data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappFileMapper extends ZappMapper {

    int countByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByDynamic(@Param("dynamic") ZappDynamic dynamic);
    int deleteByPrimaryKey(String pk);
    int insert(ZappFile vo);
    int insertu(ZappFile vo);   
    @SuppressWarnings("rawtypes")
	List selectByDynamic(@Param("dynamic") ZappDynamic dynamic);
    ZappFile selectByPrimaryKey(String pk);
    int updateByDynamic(@Param("vo") ZappFile vo, @Param("dynamic") ZappDynamic dynamic);
    int updateByPrimaryKey(ZappFile vo);
    String exists(@Param("dynamic") ZappDynamic dynamic);
    
    @SuppressWarnings("rawtypes")
	List  selectByFilename(@Param("auth") ZappAuth auth, @Param("file") ZArchMFile file, @Param("class") ZappClassification cls); 
    
    @SuppressWarnings("rawtypes")
    List selectByMaxVersion(@Param("auth") ZappAuth auth, @Param("mfile") ZArchMFile mfile,  @Param("version") ZArchVersion version);
    
    @SuppressWarnings("rawtypes")
    List selectByVersion(@Param("auth") ZappAuth auth, @Param("version") ZArchVersion version, @Param("ufile") ZArchFile ufile);
    
}
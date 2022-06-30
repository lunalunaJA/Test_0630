package com.zenithst.core.common.mapper;

import java.util.List;
import java.util.Map;

import com.zenithst.core.common.vo.ZappCommon;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to map common data. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappCommonMapper {

    String existDdl(Map<String, String> map);
    int alterDdl(Map<String, String> map);
    int createIndex(Map<String, String> map);
    int dropDdl(Map<String, String> map);
    
	List<ZappCommon> usingOtherTable(Map<String, Object> map);
	
	/* Sequence */
	int createSequence(Map<String, String> map);
	int updateSequence(Map<String, String> map);
	int selectSequence(Map<String, String> map);
	int dropSequence(Map<String, String> map);

}

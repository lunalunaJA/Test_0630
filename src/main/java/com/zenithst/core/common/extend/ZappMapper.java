package com.zenithst.core.common.extend;

import java.util.Map;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage mappers. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappMapper {

	/**
     * Batch registration
     * @param params
     *        --------------------------------------------------------------------------
	 *        VARIABLE NAME       │ TYPE               │ DESCRIPTION
	 *        --------------------------------------------------------------------------
	 *        list           	  │ List<Object>       │ <b>List</b>
	 *        --------------------------------------------------------------------------<br>
     * @return
     */
    int insertb(Map<String, Object> params);
    
    /**
     * Sorting order For editing (go down)
     * @param params
     *        --------------------------------------------------------------------------
	 *        VARIABLE NAME       │ TYPE            │ DESCRIPTION
	 *        --------------------------------------------------------------------------
	 *        parentid            │ String          │ <b>Upper ID</b>
	 *        startPriority       │ Integer         │ <b>Start Priority</b>
	 *        endPriority         │ Integer         │ <b>End Priority</b>
	 *        --------------------------------------------------------------------------<br>
     * @return
     */
    int downwardPriority(Map<String, Object> params);
    
    /**
     * Sorting order For editing (go up)
     * @param params
     *        --------------------------------------------------------------------------
	 *        VARIABLE NAME       │ TYPE            │ DESCRIPTION
	 *        --------------------------------------------------------------------------
	 *        parentid            │ String          │ <b>Upper ID</b>
	 *        startPriority       │ Integer         │ <b>Start Priority</b>
	 *        endPriority         │ Integer         │ <b>End Priority</b>
	 *        --------------------------------------------------------------------------<br>
     * @return
     */
    int upwardPriority(Map<String, Object> params);    
	
}

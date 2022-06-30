package com.zenithst.core.authentication.api;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage authentication info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    '@Autowired
*	 private ZappAuthenticationMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappAuthenticationMgtService {

	ZstFwResult connect_through_web(ZappAuth pObjAuth, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult disconnect_through_web(ZappAuth pObjAuth, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult connect_through_cs(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult disconnect_through_cs(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult checkMultiDepts(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult getAuth_Test(Object pInObj, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pResult);
	
	/***************************************************************************************************************************************/
	
	ZstFwResult connect_to_otherjob_web(ZappAuth pObjAuth, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult connect_to_otherjob_cs(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException;
	
}

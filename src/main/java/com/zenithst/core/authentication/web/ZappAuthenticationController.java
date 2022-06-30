package com.zenithst.core.authentication.web;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : Authentication 처리를 한다. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.08 / khlee  / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@RestController
@RequestMapping(value = "/api/auth")
public class ZappAuthenticationController extends ZappController {

	@Autowired
	private ZappAuthenticationMgtService service;
	
	@Autowired
	private ZappAuthenticationMgtService authservice;

	
	/**
	 * <p><b>
	 * 시스템에 연결한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/auth/connect</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{"objIsTest" : "N", "objDebugged" : false, "" : ""}</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappAuth (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAuth</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>objAccesspath</td><td>String</td><td>접근경로 (Web: W, CS: C)</td><td>● (Default : Web)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objCompanyid</td><td>String</td><td>Company ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objEmpno</td><td>String</td><td>Employee number</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objLoginid</td><td>String</td><td>Login ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objDeptid</td><td>String</td><td>Department ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>objPasswd</td><td>String</td><td>Password</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>Timestamp</td><td>String</td><td>Timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>Result code</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>Error Info.</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>Result message</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>URL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>String</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/connect"
		      	  , method = RequestMethod.POST
	              , produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus connect(@RequestBody ZappAuth pIn, HttpServletRequest pRequest) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		pIn.setObjAccesspath(ZstFwValidatorUtils.fixNullString(pIn.getObjAccesspath(), ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth));
		
		try {
			
			HttpSession pSession = pRequest.getSession(true);
			
			if(pIn.getObjAccesspath().equals(ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth)) {
				result = service.connect_through_web(pIn, pSession, pRequest, result);
			}
			else if(pIn.getObjAccesspath().equals(ZappConts.AUTHENTICATION.ACCESSPATH_CS.auth)) {
				result = service.connect_through_cs(pIn, pRequest, result);
			}
			
		} catch (ZappException e) {
			result = e.getZappResult();
		}  catch (SQLException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 
		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}
	
	
	/**
	 * <p><b>
	 * 시스템에 다른 계정으로 연결한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/auth/connect</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{"objIsTest" : "N", "objDebugged" : false, "" : ""}</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappAuth (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAuth</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>objAccesspath</td><td>String</td><td>접근경로 (Web: W, CS: C)</td><td>● (Default : Web)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objDeptid</td><td>String</td><td>Department ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>Timestamp</td><td>String</td><td>Timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>Result code</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>Error Info.</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>Result message</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>URL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>String</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/connect_to_otherjob"
		      	  , method = RequestMethod.POST
	              , produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus connect_to_otherjob(@RequestBody ZappAuth pIn, HttpServletRequest pRequest) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		pIn.setObjAccesspath(ZstFwValidatorUtils.fixNullString(pIn.getObjAccesspath(), ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth));
		
		try {
			
			HttpSession pSession = pRequest.getSession(true);
			
			if(pIn.getObjAccesspath().equals(ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth)) {
				result = service.connect_to_otherjob_web(pIn, pSession, pRequest, result);
			}
			else if(pIn.getObjAccesspath().equals(ZappConts.AUTHENTICATION.ACCESSPATH_CS.auth)) {
				result = service.connect_to_otherjob_cs(pIn, pRequest, result);
			}
			
		} catch (ZappException e) {
			result = e.getZappResult();
		}  catch (SQLException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 
		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}
	
	
	
	/**
	 * <p><b>
	 * 시스템에 연결을 해제한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/auth/disconnect</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{"objIsTest" : "N", "objDebugged" : false, "objAccesspath" : "W"}</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappAuth (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAuth</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>objAccesspath</td><td>String</td><td>접근경로 (Web: W, CS: C)</td><td>● (지정하지 않은 경우 Web)</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>Timestamp</td><td>String</td><td>Timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>Result code</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>Error Info.</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>Result message</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>URL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>String</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/disconnect"
		      	  , method = RequestMethod.POST
	              , produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus disconnect(@RequestBody ZappAuth pIn, HttpSession pSession, HttpServletRequest pRequest) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		pIn.setObjAccesspath(ZstFwValidatorUtils.fixNullString(pIn.getObjAccesspath(), ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth));
		
		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if(ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			pZappAuth.setObjCompanyid("B5D0CBE66E55FF2DFB5FEEF9AED48FECBE16C9294DE9C86A75DF4759D914500D");
			pZappAuth.setObjDeptid("FC14FCD8DB1FCE33D036928D31DD8CC0AB49546C3ACAD133AA18283700C9D4C6");
			pZappAuth.setObjEmpno("U01");
			pZappAuth.setObjLoginid("U01");
			pZappAuth.setObjPasswd("U01");
			result = getAuth_Test(pZappAuth, pSession, request, result);
		} 
		pZappAuth = getAuth(pSession);
		BeanUtils.copyProperties(pZappAuth, pIn);
		
		try {
			
			if(pIn.getObjAccesspath().equals(ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth)) {
				result = service.disconnect_through_web(pIn, pSession, pRequest, result);
			}
			else if(pIn.getObjAccesspath().equals(ZappConts.AUTHENTICATION.ACCESSPATH_CS.auth)) {
				result = service.disconnect_through_cs(pIn, pRequest, result);
			}
			
		} catch (ZappException e) {
			result = e.getZappResult();
		}  catch (SQLException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 
		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}	
	
	
	/**
	 * <p><b>
	 * 해당 접속 사용자의 다중 부서정보를 조회한다. <br>
	 * (한 개 이상의 부서가 조회되는 경우 접속 사용자에게 선택하도록 함)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/auth/checkMultiDept</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{"objIsTest" : "N", "objDebugged" : false, "" : ""}</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappAuth (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAuth</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>objAccesspath</td><td>String</td><td>접근경로 (Web: W, CS: C)</td><td>● (지정하지 않은 경우 Web)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objCompanyid</td><td>String</td><td>Company ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objEmpno</td><td>String</td><td>Employee number</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objLoginid</td><td>String</td><td>Login ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>objPasswd</td><td>String</td><td>Password</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>Timestamp</td><td>String</td><td>Timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>Result code</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>Error Info.</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>Result message</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>URL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>List&lt;ZappDeptUserExtend&gt;</td><td>부서리스트</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/checkMultiDept"
		      	  , method = RequestMethod.POST
	              , produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus checkMultiDept(@RequestBody ZappAuth pIn, HttpSession pSession, HttpServletRequest pRequest) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		pIn.setObjAccesspath(ZstFwValidatorUtils.fixNullString(pIn.getObjAccesspath(), ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth));
		
		try {
			result = service.checkMultiDepts(pIn, pRequest, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		}  catch (SQLException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 
		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}	
	
	
	/**
	 * Authentication processing for testing
	 * @param pSession
	 * @return
	 */
	protected ZstFwResult getAuth_Test(ZappAuth pZappAuth, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pResult) {
		
		try {
			pResult = authservice.connect_through_web(pZappAuth, pSession, pRequest, pResult);
		} catch (ZappException e) {
			pResult = e.getZappResult();
		} catch (SQLException e) {
			if(null != e.getCause()) {
				pResult.setResCode(e.getCause().toString());
			}else {
				pResult.setResCode("ERROR");
			}
			pResult.setMessage(e.getMessage());	
		} 
		
		return pResult;
	}
	
}

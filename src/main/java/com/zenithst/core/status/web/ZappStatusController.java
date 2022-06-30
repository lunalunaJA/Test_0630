package com.zenithst.core.status.web;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zenithst.core.common.utility.ZappSystemResourceUtil;
import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.core.status.api.ZappStatusMgtService;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.status.vo.ZappStatus;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**
* ZappStatusController.java
* @Description <pre>
* 				현황을 관리한다.
*              </pre>
* @author ZenithST
* @since 2020. 07. 08.
* @version 1.0
* @see
*
* @ Date             Updator     Note
* @ -------------   ---------   -------------------------------
* @ 2020. 7. 08.  	  Daniel      New
*
* @ Copyright (C) by ZENITHST All right reserved.
*/

@RestController
@RequestMapping(value = "/api/status")
public class ZappStatusController extends ZappController {

	
	@Autowired
	private ZappStatusMgtService service;
	
	@Autowired
	private ZappAuthenticationMgtService authservice;
	
	/**
	 * <p><b>
	 * 처리 현황을 조회 한다. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/status/list/process</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"staobjid" : "", </td><td>Target ID (Department / User) </td><td>●</td></tr>
	 * 				<tr><td>"statermtype" : "", </td><td>Term type<br> D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year </td><td>●</td></tr>
	 * 			    <tr><td>"stayear" : "", </td><td>Start date </td><td>●</td></tr>
	 * 				<tr><td>"stasdate" : "", </td><td>Start date </td><td>●</td></tr>
	 * 				<tr><td>"staedate" : "", </td><td>End date </td><td></td></tr>
	 * 				<tr><td>"staaction" : "ADD" </td><td>Processing type (ADD: New, DISCARD:Discard) </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappStatus (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>staobjid</td><td>String</td><td>Target ID (Department / User) </td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>statermtype</td><td>String</td><td>Term type<br> D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>stasdate</td><td>String</td><td>Start date</td><td>●</td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>staedate</td><td>String</td><td>End date</td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objAction</td><td>String</td><td>Processing type (ADD: New, DISCARD:Discard)</td><td>●</td>
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
	 * 				<td>result</td><td>List&lt;ZappContentRes&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappStatus
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/list/process", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus list_process(@RequestBody ZappStatus pIn, HttpServletRequest request, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if(ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		} 
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}
		
		try {
			result = service.getProcessStatusList(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 
		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}	

	@RequestMapping(value = "/list/processall", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus list_processall(@RequestBody ZappStatus pIn, HttpServletRequest request, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if(ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		} 
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}
		
		try {
			result = service.getProcessStatusListAll(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
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
	 * 보유 현황을 조회 한다. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/status/list/hold</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"staobjid" : "", </td><td>Target ID (Department / User) </td><td>●</td></tr>
	 * 				<tr><td>"statermtype" : "", </td><td>Term type<br> D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year </td><td>●</td></tr>
	 * 				<tr><td>"stasdate" : "", </td><td>Start date </td><td>●</td></tr>
	 * 				<tr><td>"staedate" : "", </td><td>End date </td><td></td></tr>
	 * 				<tr><td>"objAction" : "ADD" </td><td>Processing type (ADD: New, DISCARD:Discard) </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappStatus (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>staobjid</td><td>String</td><td>Target ID (Department / User) </td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>statermtype</td><td>String</td><td>Term type<br> D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>stasdate</td><td>String</td><td>Start date</td><td>●</td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>staedate</td><td>String</td><td>End date</td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objAction</td><td>String</td><td>Processing type (ADD: New, DISCARD:Discard)</td><td>●</td>
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
	 * 				<td>result</td><td>List&lt;ZappContentRes&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappStatus
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/list/hold", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus list_hold(@RequestBody ZappStatus pIn, HttpServletRequest request, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if(ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		} 
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}
		
		try {
			result = service.getHoldStatusList(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 
		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}	

	@RequestMapping(value = "/list/apm", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus list_apm(@RequestBody ZappApm pIn, HttpServletRequest request, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if(ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		} 
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}
		
		try {
			result = service.viewApmsList(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
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
	 * 시스템 리소스 정보를 조회한다(CPU,메모리 정보)
	 * @return ZnEnvironment
	 */
    @RequestMapping(value = "/cpuMemory" )
	public ZstFwResult getSystemInfo(){
    	ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
    	ZappSystemResourceUtil.getSysteminfo();
		result.setResObj(ZappSystemResourceUtil.getSysteminfo());
		return result;
	}
}

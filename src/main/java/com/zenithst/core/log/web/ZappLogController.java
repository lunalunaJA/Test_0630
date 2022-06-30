package com.zenithst.core.log.web;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.test.ZappAuthTest;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.log.vo.ZappCycleLog;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**
* ZappLogController.java
* @Description <pre>
* 				Controller class for managing log info.
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
@RequestMapping(value = "/api/log")
public class ZappLogController extends ZappController {
	
	/* Log */
	@Autowired
	private ZappLogMgtService service;
	
	/* Authentication*/
	@Autowired
	private ZappAuthenticationMgtService authservice;

	/**
	 * <p><b>
	 * Inquire the count of access log info. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/access/count</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"logid" : "", </td><td>PK</td><td></td></tr>
	 * 				<tr><td>"logobjid" : "", </td><td>Logging target ID</td><td></td></tr>
	 * 				<tr><td>"logtext" : "", </td><td>Logging target title</td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"loggerid" : "", </td><td>Logger ID</td><td></td></tr>
	 * 				<tr><td>"loggername" : "", </td><td>Logger name</td><td></td></tr>
	 * 				<tr><td>"loggerdeptid" : ""</td><td>Logger dept. ID</td><td></td></tr>
	 * 				<tr><td>"loggerdeptname" : "", </td><td>Logger dept. name</td><td></td></tr>
	 * 				<tr><td>"logtime" : "yyyy-mm-dd：yyyy-mm-dd", </td><td>Logging time</td><td></td></tr>
	 * 				<tr><td>"logtype" : "", </td><td>Logging type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"action" : "", </td><td>Processing type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappAccessLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logid</td><td>String</td><td>PK</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logobjid</td><td>String</td><td>Logging target ID</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtext</td><td>String</td><td>Logging target title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerid</td><td>String</td><td>Logger ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggername</td><td>String</td><td>Logger name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptid</td><td>String</td><td>Logger dept. ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptname</td><td>String</td><td>Logger dept. name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtime</td><td>String</td><td>Logging time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtype</td><td>String</td><td>Logging type(01:Authentication)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>action</td><td>String</td><td>Processing type<br>(01:Connect,02:Disconnect,03:Failed after trying to connect)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td></td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td></td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappAccessLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappAccessLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/access/count", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus count_access(@RequestBody ZappAccessLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.countObject(pZappAuth, pIn, result);
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
	 * Inquire the list of access log info. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/access/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"logid" : "", </td><td>PK</td><td></td></tr>
	 * 				<tr><td>"logobjid" : "", </td><td>Logging target ID</td><td></td></tr>
	 * 				<tr><td>"logtext" : "", </td><td>Logging target title</td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"loggerid" : "", </td><td>Logger ID</td><td></td></tr>
	 * 				<tr><td>"loggername" : "", </td><td>Logger name</td><td></td></tr>
	 * 				<tr><td>"loggerdeptid" : ""</td><td>Logger dept. ID</td><td></td></tr>
	 * 				<tr><td>"loggerdeptname" : "", </td><td>Logger dept. name</td><td></td></tr>
	 * 				<tr><td>"logtime" : "yyyy-mm-dd：yyyy-mm-dd", </td><td>Logging time</td><td></td></tr>
	 * 				<tr><td>"logtype" : "", </td><td>Logging type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"action" : "", </td><td>Processing type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"objmaporder" : "", </td><td>Sorting info.</td><td></td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappAccessLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logid</td><td>String</td><td>PK</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logobjid</td><td>String</td><td>Logging target ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtext</td><td>String</td><td>Logging target title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerid</td><td>String</td><td>Logger ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggername</td><td>String</td><td>Logger name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptid</td><td>String</td><td>Logger dept. ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptname</td><td>String</td><td>Logger dept. name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtime</td><td>String</td><td>Logging time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtype</td><td>String</td><td>Logging type(01:Authentication)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>action</td><td>String</td><td>Processing type<br>(01:Connect,02:Disconnect,03:Failed after trying to connect)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td></td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td></td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappAccessLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappAccessLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/access/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_access(@RequestBody ZappAccessLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.selectObjectExtend(pZappAuth, null, pIn, result);
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
	 * Register new content log. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/content/add</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"logobjid" : "", </td><td>Logging target ID</td><td></td></tr>
	 * 				<tr><td>"logtext" : "", </td><td>Logging target title</td><td></td></tr>
	 * 				<tr><td>"logtype" : "", </td><td>Logging type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"action" : "", </td><td>Processing type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"logs" : "", </td><td>Logging Info. </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logobjid</td><td>String</td><td>Logging target ID</td><td>●</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtext</td><td>String</td><td>Logging target title</td><td>●</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtype</td><td>String</td><td>Logging type<br>(01:Bundle,02:File,03:Classification,04:Link,05:Share,06:Lock)</td><td>●</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>action</td><td>String</td><td>Processing type<br>(A1:New,B1:Edit,C1:Move,D1:Copy,E1:Delete,<br>F1:Discard,G1:View,H1:Sorting,Y1:Link,Z1:Share)</td><td>●</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logs</td><td>String</td><td>로그 내용</td><td></td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappAccessLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappAccessLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/content/add", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus add_content(@RequestBody ZappContentLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		// Set default values
		pIn.setCompanyid(pZappAuth.getObjCompanyid());
		pIn.setLoggerid(pZappAuth.getSessDeptUser().getDeptuserid());
		pIn.setLoggername(pZappAuth.getSessUser().getName());
		pIn.setLoggerdeptid(pZappAuth.getSessDeptUser().getDeptid());
		pIn.setLoggerdeptname(pZappAuth.getSessDeptUser().getZappDept().getName());
		pIn.setLogtime(ZstFwDateUtils.getNow());
		
		try {
			result = service.addObject(pZappAuth, pIn, result);
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
	 * Inquire the count of content log info. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/content/count</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"logid" : "", </td><td>PK</td><td></td></tr>
	 * 				<tr><td>"logobjid" : "", </td><td>Logging target ID</td><td></td></tr>
	 * 				<tr><td>"logtext" : "", </td><td>Logging target title</td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"loggerid" : "", </td><td>Logger ID</td><td></td></tr>
	 * 				<tr><td>"loggername" : "", </td><td>Logger name</td><td></td></tr>
	 * 				<tr><td>"loggerdeptid" : ""</td><td>Logger dept. ID</td><td></td></tr>
	 * 				<tr><td>"loggerdeptname" : "", </td><td>Logger dept. name</td><td></td></tr>
	 * 				<tr><td>"logtime" : "yyyy-mm-dd：yyyy-mm-dd", </td><td>Logging time</td><td></td></tr>
	 * 				<tr><td>"logtype" : "", </td><td>Logging type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"action" : "", </td><td>Processing type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"objmaporder" : "", </td><td>Sorting info.</td><td></td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logid</td><td>String</td><td>PK</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logobjid</td><td>String</td><td>Logging target ID</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtext</td><td>String</td><td>Logging target title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerid</td><td>String</td><td>Logger ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggername</td><td>String</td><td>Logger name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptid</td><td>String</td><td>Logger dept. ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptname</td><td>String</td><td>Logger dept. name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtime</td><td>String</td><td>Logging time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtype</td><td>String</td><td>Logging type<br>(01:Bundle,02:File,03:Classification,04:Link,05:Share,06:Lock)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>action</td><td>String</td><td>Processing type<br>(A1:New,B1:Edit,C1:Move,D1:Copy,E1:Delete,<br>F1:Discard,G1:View,H1:Sorting,Y1:Link,Z1:Share)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td></td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td></td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappContentLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/content/count", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus count_content(@RequestBody ZappContentLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.countObject(pZappAuth, pIn, result);
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
	 * Inquire the list of content log info.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/content/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"logid" : "", </td><td>PK</td><td></td></tr>
	 * 				<tr><td>"logobjid" : "", </td><td>Logging target ID</td><td></td></tr>
	 * 				<tr><td>"logtext" : "", </td><td>Logging target title</td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"loggerid" : "", </td><td>Logger ID</td><td></td></tr>
	 * 				<tr><td>"loggername" : "", </td><td>Logger name</td><td></td></tr>
	 * 				<tr><td>"loggerdeptid" : ""</td><td>Logger dept. ID</td><td></td></tr>
	 * 				<tr><td>"loggerdeptname" : "", </td><td>Logger dept. name</td><td></td></tr>
	 * 				<tr><td>"logtime" : "yyyy-mm-dd：yyyy-mm-dd", </td><td>Logging time</td><td></td></tr>
	 * 				<tr><td>"logtype" : "", </td><td>Logging type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"action" : "", </td><td>Processing type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"objmaporder" : "", </td><td>Sorting info.</td><td></td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logid</td><td>String</td><td>PK</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logobjid</td><td>String</td><td>Logging target ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtext</td><td>String</td><td>Logging target title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerid</td><td>String</td><td>Logger ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggername</td><td>String</td><td>Logger name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptid</td><td>String</td><td>Logger dept. ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptname</td><td>String</td><td>Logger dept. name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtime</td><td>String</td><td>Logging time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtype</td><td>String</td><td>Logging type<br>(01:Bundle,02:File,03:Classification,04:Link,05:Share,06:Lock)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>action</td><td>String</td><td>Processing type<br>(A1:New,B1:Edit,C1:Move,D1:Copy,E1:Delete,<br>F1:Discard,G1:View,H1:Sorting,Y1:Link,Z1:Share)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td></td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td></td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappContentLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/content/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_content(@RequestBody ZappContentLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.selectObjectExtend(pZappAuth, null, pIn, result);
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
	 * Inquire the count of system log info. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/system/count</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"logid" : "", </td><td>PK</td><td></td></tr>
	 * 				<tr><td>"logobjid" : "", </td><td>Logging target ID</td><td></td></tr>
	 * 				<tr><td>"logtext" : "", </td><td>Logging target title</td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"loggerid" : "", </td><td>Logger ID</td><td></td></tr>
	 * 				<tr><td>"loggername" : "", </td><td>Logger name</td><td></td></tr>
	 * 				<tr><td>"loggerdeptid" : ""</td><td>Logger dept. ID</td><td></td></tr>
	 * 				<tr><td>"loggerdeptname" : "", </td><td>Logger dept. name</td><td></td></tr>
	 * 				<tr><td>"logtime" : "yyyy-mm-dd：yyyy-mm-dd", </td><td>Logging time</td><td></td></tr>
	 * 				<tr><td>"logtype" : "", </td><td>Logging type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"action" : "", </td><td>Processing type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappSystemLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logid</td><td>String</td><td>PK</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logobjid</td><td>String</td><td>Logging target ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtext</td><td>String</td><td>Logging target title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerid</td><td>String</td><td>Logger ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggername</td><td>String</td><td>Logger name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptid</td><td>String</td><td>Logger dept. ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptname</td><td>String</td><td>Logger dept. name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtime</td><td>String</td><td>Logging time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtype</td><td>String</td><td>Logging type<br>(A1:Company,02:Department,03:User,04:Group,11:Preferences,12:Code)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>action</td><td>String</td><td>Processing type<br>(A1:New,B1:Edit,C1:Move,D1:Copy,E1:Delete,<br>F1:Discard,G1:View,H1:Reorder,Y1:Link,Z1:Share)</td><td></td><td>IN</td>
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
	 * 				<td>result</td><td>List&lt;ZappSystemLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappSystemLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/system/count", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus count_system(@RequestBody ZappSystemLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.countObject(pZappAuth, pIn, result);
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
	 * Inquire the list of system log info.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/content/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"logid" : "", </td><td>PK</td><td></td></tr>
	 * 				<tr><td>"logobjid" : "", </td><td>Logging target ID</td><td></td></tr>
	 * 				<tr><td>"logtext" : "", </td><td>Logging target title</td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"loggerid" : "", </td><td>Logger ID</td><td></td></tr>
	 * 				<tr><td>"loggername" : "", </td><td>Logger name</td><td></td></tr>
	 * 				<tr><td>"loggerdeptid" : ""</td><td>Logger dept. ID</td><td></td></tr>
	 * 				<tr><td>"loggerdeptname" : "", </td><td>Logger dept. name</td><td></td></tr>
	 * 				<tr><td>"logtime" : "yyyy-mm-dd：yyyy-mm-dd", </td><td>Logging time</td><td></td></tr>
	 * 				<tr><td>"logtype" : "", </td><td>Logging type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"action" : "", </td><td>Processing type (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"objmaporder" : "", </td><td>Sorting info.</td><td></td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappSystemLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logid</td><td>String</td><td>PK</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logobjid</td><td>String</td><td>Logging target ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtext</td><td>String</td><td>Logging target title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerid</td><td>String</td><td>Logger ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggername</td><td>String</td><td>Logger name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptid</td><td>String</td><td>Logger dept. ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loggerdeptname</td><td>String</td><td>Logger dept. name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtime</td><td>String</td><td>Logging time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>logtype</td><td>String</td><td>Logging type<br>(A1:Company,02:Department,03:User,04:Group,11:Preferences,12:Code)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>action</td><td>String</td><td>Processing type<br>(A1:New,B1:Edit,C1:Move,D1:Copy,E1:Delete,<br>F1:Discard,G1:View,H1:Reorder,Y1:Link,Z1:Share)</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td></td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td></td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappSystemLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappSystemLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/system/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_system(@RequestBody ZappSystemLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.selectObjectExtend(pZappAuth, null, pIn, result);
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
	 * Inquire the count of periodic job log info.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/cycle/count</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"cycleid" : "", </td><td>(PK)</td><td></td></tr>
	 * 				<tr><td>"cycletime" : "", </td><td>Exe. time</td><td></td></tr>
	 * 				<tr><td>"cycletype" : "", </td><td>Type</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappCycleLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cycleid</td><td>String</td><td>(PK)</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cycletype</td><td>String</td><td>Type</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cycletime</td><td>String</td><td>Exe. time</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
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
	 * 				<td>result</td><td>List&lt;ZappSystemLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappCycleLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/cycle/count", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus count_cycle(@RequestBody ZappCycleLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.countObject(pZappAuth, pIn, result);
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
	 * Inquire the list of periodic job log info.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/log/cycle/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td></td></tr>
	 * 				<tr><td>"cycleid" : "", </td><td>(PK)</td><td></td></tr>
	 * 				<tr><td>"cycletime" : "", </td><td>Exe. time</td><td></td></tr>
	 * 				<tr><td>"cycletype" : "", </td><td>Type</td><td></td></tr>
	 * 				<tr><td>"objmaporder" : "", </td><td>Sorting info.</td><td></td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappCycleLog (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cycleid</td><td>String</td><td>(PK)</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cycletype</td><td>String</td><td>Type</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cycletime</td><td>String</td><td>Exe. time</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td></td><td></td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td></td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappSystemLog&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappCycleLog
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/cycle/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_cycle(@RequestBody ZappCycleLog pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjDebugged(pIn.getObjDebugged());
		
		try {
			result = service.selectObjectExtend(pZappAuth, null, pIn, result);
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
	
	/**
	 * Authentication processing for testing
	 * @param pSession
	 * @return
	 */
//	protected ZstFwResult getAuth_Test(Object pInObj, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pResult) {
//		
//		ZappAuth pZappAuth = ZappAuthTest.getAuth(pInObj);
//
//		try {
//			pResult = authservice.connect_through_web(pZappAuth, pSession, pRequest, pResult);
//		} catch (ZappException e) {
//			pResult = e.getZappResult();
//		} catch (SQLException e) {
//			if(null != e.getCause()) {
//				pResult.setResCode(e.getCause().toString());
//			}else {
//				pResult.setResCode("ERROR");
//			}
//			pResult.setMessage(e.getMessage());	
//		} 
//		
//		return pResult;
//	}	
	
}

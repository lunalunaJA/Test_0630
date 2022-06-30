package com.zenithst.core.system.web;

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

import com.zenithst.archive.api.ZArchCabinetMgtService;
import com.zenithst.archive.api.ZArchFormatMgtService;
import com.zenithst.archive.api.ZArchTaskMgtService;
import com.zenithst.archive.constant.Results;
import com.zenithst.archive.vo.ZArchCabinet;
import com.zenithst.archive.vo.ZArchFormat;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.archive.vo.ZArchTask;
import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.core.system.api.ZappSystemMgtService;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**
 * <pre>
 * <b>
 * 1) Desc. (Description) : Controller class for managing system info. <br>
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
@RequestMapping(value = "/api/system")
public class ZappSystemController extends ZappController {

	/* System */
	@Autowired
	private ZappSystemMgtService service;

	/* Authentication */
	@Autowired
	private ZappAuthenticationMgtService authservice;

	@Autowired
	private ZArchFormatMgtService formatService;

	@Autowired
	private ZArchCabinetMgtService cabinetService;

	@Autowired
	private ZArchTaskMgtService taskService;
	
	/* ******* Code ********* */

	/**
	 * <p>
	 * <b> Register new code. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/add</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#cc6956">
	 *            <td>companyid</td>
	 *            <td>String</td>
	 *            <td>Company ID (Auto-input in session)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>name</td>
	 *            <td>String</td>
	 *            <td>Code name</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codevalue</td>
	 *            <td>String</td>
	 *            <td>Code value</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>upid</td>
	 *            <td>String</td>
	 *            <td>Upper ID</td>
	 *            <td></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>types</td>
	 *            <td>String</td>
	 *            <td>Code Type<br>
	 *            01:Preferences, 02:Position, 03:Duty, 04:Security level<br>
	 *            05:Retention period, 06:Classification access control,
	 *            07:Content access control, 08:User type<br>
	 *            09:Group type, 10:Classification type, 11:Right target type,
	 *            12:Content type</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codekey</td>
	 *            <td>String</td>
	 *            <td>Code key</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/add", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus add(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.addObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Edit code info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/change</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codeid</td>
	 *            <td>String</td>
	 *            <td>Code ID (PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>name</td>
	 *            <td>String</td>
	 *            <td>Code name</td>
	 *            <td></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codevalue</td>
	 *            <td>String</td>
	 *            <td>Code value</td>
	 *            <td></td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/change", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus change(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}
		pZappAuth.setObjType(YES);
		
		try {
			result = service.changeObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Disable code info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/disable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codeid</td>
	 *            <td>String</td>
	 *            <td>Code ID (PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/disable", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus disable(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());

		try {
			result = service.disableObject(pZappAuth, null, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Enable code info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/disable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codeid</td>
	 *            <td>String</td>
	 *            <td>Code ID (PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/enable", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus enable(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.enableObject(pZappAuth, null, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Discard code info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/discard</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codeid</td>
	 *            <td>String</td>
	 *            <td>Code ID (PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/discard", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus discard(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.discardObject(pZappAuth, null, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Change the order of code info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/reorder</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codeid</td>
	 *            <td>String</td>
	 *            <td>Code ID (PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>priority</td>
	 *            <td>Integer</td>
	 *            <td>Sorting order</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/reorder", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus reorder(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.reorderObject(pZappAuth, null, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Inquire code info. (based on PK) </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/get</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codeid</td>
	 *            <td>String</td>
	 *            <td>Code ID (PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/get", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus get(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.selectObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Inquire the list of code info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/code/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappCode (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappCode</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            <td><b>Default filter</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codeid</td>
	 *            <td>String</td>
	 *            <td>Code ID (PK)</td>
	 *            <td></td>
	 *            <td>EQUAL</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>companyid</td>
	 *            <td>String</td>
	 *            <td>Company ID</td>
	 *            <td></td>
	 *            <td>EQUAL</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>name</td>
	 *            <td>String</td>
	 *            <td>Code name</td>
	 *            <td></td>
	 *            <td>LIKE</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codevalue</td>
	 *            <td>String</td>
	 *            <td>Code value</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>upid</td>
	 *            <td>String</td>
	 *            <td>Upper ID</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>types</td>
	 *            <td>String</td>
	 *            <td>Code Type<br>
	 *            01:Preferences, 02:Position, 03:Duty, 04:Security level<br>
	 *            05:Retention period, 06:Classification access control,
	 *            07:Content access control, 08:User type<br>
	 *            09:Group type, 10:Classification type, 11:Right target type,
	 *            12:Content type</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>codekey</td>
	 *            <td>String</td>
	 *            <td>Code key</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>isactive</td>
	 *            <td>String</td>
	 *            <td>Use or not (Y/N)</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappCode
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/code/list", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list(@RequestBody ZappCode pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			pIn.setCompanyid(pZappAuth.getObjCompanyid()); // Company ID
			result = service.selectObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/* ******* Preferences ********* */

	/**
	 * <p>
	 * <b> Register new preference. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/env/add</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappEnv (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappEnv</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#cc6956">
	 *            <td>companyid</td>
	 *            <td>String</td>
	 *            <td>Company ID (Auto-input in session)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>name</td>
	 *            <td>String</td>
	 *            <td>Name</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>setval</td>
	 *            <td>String</td>
	 *            <td>Value</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envtype</td>
	 *            <td>String</td>
	 *            <td>Type</td>
	 *            <td></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>settype</td>
	 *            <td>String</td>
	 *            <td>Selection type (1:Key-in, 2:Selection)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>setopt</td>
	 *            <td>String</td>
	 *            <td>Selection option</td>
	 *            <td>● (When the selection type is 2 - ex.
	 *            {\"Y\":\"Used\",\"N\":\"Not used\"} )</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>editable</td>
	 *            <td>String</td>
	 *            <td>Edit or not(Y/N)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envkey</td>
	 *            <td>String</td>
	 *            <td>Key(for use in the program)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappEnv
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/env/add", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus add(@RequestBody ZappEnv pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.addObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Edit preference info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/env/change
	 * </b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappEnv (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappEnv</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envid</td>
	 *            <td>String</td>
	 *            <td>(PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>name</td>
	 *            <td>String</td>
	 *            <td>Name</td>
	 *            <td></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>setval</td>
	 *            <td>String</td>
	 *            <td>Value</td>
	 *            <td></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>editable</td>
	 *            <td>String</td>
	 *            <td>Edit or not(Y/N)</td>
	 *            <td></td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappEnv
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/env/change", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus change(@RequestBody ZappEnv pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}
		pZappAuth.setObjType(YES);
		
		try {
			result = service.changeObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Disable preference info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/env/disable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappEnv (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappEnv</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envid</td>
	 *            <td>String</td>
	 *            <td>(PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappEnv
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/env/disable", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus disable(@RequestBody ZappEnv pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.disableObject(pZappAuth, null, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Enable preference info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/env/enable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappEnv (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappEnv</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envid</td>
	 *            <td>String</td>
	 *            <td>(PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappEnv
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/env/enable", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus enable(@RequestBody ZappEnv pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.enableObject(pZappAuth, null, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Discard preference info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/env/discard</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappEnv (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappEnv</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envid</td>
	 *            <td>String</td>
	 *            <td>(PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappEnv
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/env/discard", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus discard(@RequestBody ZappEnv pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.discardObject(pZappAuth, null, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Inquire preference info. (based on PK) </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/env/get</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappEnv (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappEnv</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envid</td>
	 *            <td>String</td>
	 *            <td>(PK)</td>
	 *            <td>●</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappEnv
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/env/get", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus get(@RequestBody ZappEnv pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = service.selectObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	/**
	 * <p>
	 * <b> Inquire the list of preference info. </b>
	 * </p>
	 * 
	 * <pre>
	 * 
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/system/env/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn
	 *            ZappEnv (Not Nullable) <br>
	 *            <table width="80%" border="1">
	 *            <caption>ZappEnv</caption>
	 *            <tr bgcolor="#469CE5">
	 *            <td><b>Name</b></td>
	 *            <td><b>Type</b></td>
	 *            <td><b>Desc.</b></td>
	 *            <td><b>Required?</b></td>
	 *            <td><b>Default filter</b></td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envid</td>
	 *            <td>String</td>
	 *            <td>(PK)</td>
	 *            <td></td>
	 *            <td>EQUAL</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>companyid</td>
	 *            <td>String</td>
	 *            <td>Company ID</td>
	 *            <td></td>
	 *            <td>EQUAL</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>name</td>
	 *            <td>String</td>
	 *            <td>Name</td>
	 *            <td></td>
	 *            <td>LIKE</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>setval</td>
	 *            <td>String</td>
	 *            <td>Value</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envtype</td>
	 *            <td>String</td>
	 *            <td>Type</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>settype</td>
	 *            <td>String</td>
	 *            <td>Selection type (1:Key in, 2:Select)</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>editable</td>
	 *            <td>String</td>
	 *            <td>Edit or not(Y/N)</td>
	 *            <td></td>
	 *            <td>EQUAL</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>envkey</td>
	 *            <td>String</td>
	 *            <td>Key (for program identification)</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            <tr bgcolor="#95BEE1">
	 *            <td>isactive</td>
	 *            <td>String</td>
	 *            <td>Use or not(Y/N)</td>
	 *            <td></td>
	 *            <td>IN</td>
	 *            </tr>
	 *            </table>
	 * <br>
	 * @param request
	 *            HttpServletRequest <br>
	 * @param session
	 *            HttpSession <br>
	 * @return ZstFwStatus <br>
	 *         <table width="80%" border="1">
	 *         <caption></caption>
	 *         <tr bgcolor="#469CE5">
	 *         <td><b>Name</b></td>
	 *         <td><b>Type</b></td>
	 *         <td><b>Desc.</b></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>Timestamp</td>
	 *         <td>String</td>
	 *         <td>Timestamp</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>status</td>
	 *         <td>Object</td>
	 *         <td>Result code</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>error</td>
	 *         <td>String</td>
	 *         <td>Error Info.</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>message</td>
	 *         <td>String</td>
	 *         <td>Result message</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>trace</td>
	 *         <td>String</td>
	 *         <td></td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>path</td>
	 *         <td>String</td>
	 *         <td>URL</td>
	 *         </tr>
	 *         <tr bgcolor="#E2EAF1">
	 *         <td>result</td>
	 *         <td>String</td>
	 *         <td>Result Info.</td>
	 *         </tr>
	 *         </table>
	 * <br>
	 * @see ZappAuth
	 * @see ZappEnv
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/env/list", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list(@RequestBody ZappEnv pIn, HttpServletRequest request, HttpSession session) {

		ZstFwResult result = new ZstFwResult();
		result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if (ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			result = authservice.getAuth_Test(pIn, session, request, result);
		}
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			pIn.setCompanyid(pZappAuth.getObjCompanyid()); // Company ID
			result = service.selectObject(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if (null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			} else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());
		}

		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	@RequestMapping(value = "/format/add", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult add(@RequestBody ZArchFormat pIn, HttpServletRequest request) {
		pIn.setCheckFormat(false);
		pIn.setIsRemote(false);
		pIn.setIsDebugged(true);
		ZArchResult result = new ZArchResult();

		try {
			result = formatService.saveFormat(pIn);
			if (result.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, result.getMessage()));
			}
		} catch (Exception e) {
			if (null != e.getCause())
				result.setCode(e.getCause() == null ? null : e.getCause().toString());
			if (e.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, e.getMessage()));
			} else {
				result.setMessage(e.getMessage());
			}
		}
		return result;
	}

	@RequestMapping(value = "/format/change", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult change(@RequestBody ZArchFormat pIn, HttpServletRequest request) {
		pIn.setCheckFormat(false);
		pIn.setIsRemote(false);
		pIn.setIsDebugged(true);
		ZArchResult result = new ZArchResult();

		try {
			result = formatService.updateFormat(pIn);
			if (result.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, result.getMessage()));
			}
		} catch (Exception e) {
			if (null != e.getCause())
				result.setCode(e.getCause() == null ? null : e.getCause().toString());
			if (e.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, e.getMessage()));
			} else {
				result.setMessage(e.getMessage());
			}
		}
		return result;
	}

	@RequestMapping(value = "/format/discard", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult discard(@RequestBody ZArchFormat pIn, HttpServletRequest request) {
		pIn.setCheckFormat(true);
		pIn.setIsRemote(false);
		pIn.setIsDebugged(true);
		ZArchResult result = new ZArchResult();

		try {
			result = formatService.eraseFormat(pIn);
			if (result.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, result.getMessage()));
			}
		} catch (Exception e) {
			if (null != e.getCause())
				result.setCode(e.getCause() == null ? null : e.getCause().toString());

			if (e.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, e.getMessage()));
			} else {
				result.setMessage(e.getMessage());
			}
		}
		return result;
	}

	@RequestMapping(value = "/format/list", method = RequestMethod.POST, produces = {"application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult list(@RequestBody ZArchFormat pIn, HttpServletRequest request) {
		pIn.setCheckFormat(false);
		pIn.setIsRemote(false);
		pIn.setIsDebugged(true);
		ZArchResult result = new ZArchResult();

		try {
			result = formatService.listFormat(pIn);
			if (result.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, result.getMessage()));
			}
		} catch (Exception e) {
			if (null != e.getCause())
				result.setCode(e.getCause() == null ? null : e.getCause().toString());
			if (e.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, e.getMessage()));
			} else {
				result.setMessage(e.getMessage());
			}
		}
		return result;
	}

	@RequestMapping(value = "/cabinet/add", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult add(@RequestBody ZArchCabinet pIn, HttpServletRequest request) {
		pIn.setCheckFormat(false);
		pIn.setIsRemote(false);
		pIn.setIsDebugged(true);
		ZArchResult result = new ZArchResult();

		try {
			result = cabinetService.saveCabinet(pIn);
			if (result.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, result.getMessage()));
			}
		} catch (Exception e) {
			if (null != e.getCause())
				result.setCode(e.getCause() == null ? null : e.getCause().toString());
			if (e.getMessage().indexOf("[") > -1) {
				result.setMessage(convertMessage(request, e.getMessage()));
			} else {
				result.setMessage(e.getMessage());
			}
		}
		return result;
	}

	@RequestMapping(value = "/cabinet/change", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult change(@RequestBody ZArchCabinet pIn, HttpServletRequest request) {
	    pIn.setCheckFormat(false);
	    pIn.setIsRemote(false);
	    pIn.setIsDebugged(true);
	    ZArchResult result = new ZArchResult();

	    try {
	    	result = cabinetService.updateCabinet(pIn);
	    	if (result.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, result.getMessage()));
	    	}
	    } catch (Exception e) {
	    	if (null != e.getCause())
	    		result.setCode(e.getCause() == null ? null : e.getCause().toString());
	    	if (e.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, e.getMessage()));
	    	} else {
	    		result.setMessage(e.getMessage());
	    	}	
	    }
	    return result;
	}

	@RequestMapping(value = "/cabinet/discard", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult discard(@RequestBody ZArchCabinet pIn, HttpServletRequest request) {
	    pIn.setCheckFormat(false);
	    pIn.setIsRemote(false);
	    pIn.setIsDebugged(true);
	    ZArchResult result = new ZArchResult();

	    try {
	    	result = cabinetService.eraseCabinet(pIn);
	    	if (result.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, result.getMessage()));
	    	}
	    } catch (Exception e) {
	    	if (null != e.getCause())
	    		result.setCode(e.getCause() == null ? null : e.getCause().toString());
	    	if (e.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, e.getMessage()));
	    	} else {
	    		result.setMessage(e.getMessage());
	    	}
	    }
	    return result;
	}

		
	@RequestMapping(value = "/cabinet/list", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult list(@RequestBody ZArchCabinet pIn, HttpServletRequest request) {
		pIn.setCheckFormat(false);
	    pIn.setIsRemote(false);
	    pIn.setIsDebugged(true);
	    ZArchResult result = new ZArchResult();

	    try {
	    	result = cabinetService.listCabinet(pIn);
	    	if (result.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, result.getMessage()));
	    	}
	    } catch (Exception e) {
	    	if (null != e.getCause())
	    		result.setCode(e.getCause() == null ? null : e.getCause().toString());
	    	if (e.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, e.getMessage()));
	    	} else {
	    		result.setMessage(e.getMessage());
	    	}
	    }
	    return result;
	}

	@RequestMapping(value = "/task/add", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult add(@RequestBody ZArchTask pIn, HttpServletRequest request) {
	    pIn.setCheckFormat(false);
	    pIn.setIsRemote(false);
	    pIn.setIsDebugged(true);
	    ZArchResult result = new ZArchResult();

	    try {
	    	result = taskService.saveTask(pIn);
	    	if (result.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, result.getMessage()));
	    	}
	    } catch (Exception e) {
	    	if (null != e.getCause())
	    		result.setCode(e.getCause() == null ? null : e.getCause().toString());
	    	if (e.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, e.getMessage()));
	    	} else {
	    		result.setMessage(e.getMessage());
	    	}
	    }
	    return result;
	}

	@RequestMapping(value = "/task/change", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult change(@RequestBody ZArchTask pIn, HttpServletRequest request) {
	    pIn.setCheckFormat(false);
	    pIn.setIsRemote(false);
	    pIn.setIsDebugged(true);
	    ZArchResult result = new ZArchResult();

	    try {
	    	result = taskService.updateTask(pIn);
	    	if (result.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, result.getMessage()));
	    	}
	    } catch (Exception e) {
	    	if (null != e.getCause())
	    		result.setCode(e.getCause() == null ? null : e.getCause().toString());
	    	if (e.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, e.getMessage()));
	    	} else {
	    		result.setMessage(e.getMessage());
	    	}
	    }
	    return result;
	}

	@RequestMapping(value = "/task/discard", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult discard(@RequestBody ZArchTask pIn, HttpServletRequest request) {
	    pIn.setCheckFormat(false);
	    pIn.setIsRemote(false);
	    pIn.setIsDebugged(true);
	    ZArchResult result = new ZArchResult();

	    try {
	    	result = taskService.eraseTask(pIn);
	    	if (result.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, result.getMessage()));
	    	}
	    } catch (Exception e) {
	    	if (null != e.getCause())
	    		result.setCode(e.getCause() == null ? null : e.getCause().toString());
	    	if (e.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, e.getMessage()));
	    	} else {
	    		result.setMessage(e.getMessage());
	    	}
	    }
	    return result;
	}

	@RequestMapping(value = "/task/list", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
	@ResponseStatus(HttpStatus.CREATED)
	public ZArchResult list(@RequestBody ZArchTask pIn, HttpServletRequest request) {
	    pIn.setCheckFormat(false);
	    pIn.setIsRemote(false);
	    pIn.setIsDebugged(true);
	    ZArchResult result = new ZArchResult();

	    try {
	    	result = taskService.listTask(pIn);
	    	if (result.getMessage().indexOf("[") > -1) {
	    		convertMessage(request, result.getMessage());
	    	}
	    } catch (Exception e) {
	    	if (null != e.getCause())
	    		result.setCode(e.getCause() == null ? null : e.getCause().toString());
	    	if (e.getMessage().indexOf("[") > -1) {
	    		result.setMessage(convertMessage(request, e.getMessage()));
	    	} else {
	    		result.setMessage(e.getMessage());
	    	}
	    }
	    return result;
	}

	  
	private String checkToErrorCode(String message) {
		System.out.println("===checkToErrorCode : " + message);
		String code = null;
		if (message != null && message.indexOf("[") > -1
				&& message.indexOf("]") > -1) {
			code = message.substring(message.indexOf("[") + 1,
					message.indexOf("]"));
		}
		return code;
	}

	private String convertMessage(HttpServletRequest request, String message) {
		String convertMessage = message;
		String code = checkToErrorCode(message);
		HttpSession session = request.getSession();
		String lang = (String) session.getAttribute("sessLang");
		for (Results value : Results.values()) {
			if (value.result.equals(code)) {
				if (lang == null || lang.equals("en")) {
					convertMessage = value.comment;
					break;
				} else {
					convertMessage = value.note;
					break;
				}
			}
		}
		return convertMessage;
	}

}

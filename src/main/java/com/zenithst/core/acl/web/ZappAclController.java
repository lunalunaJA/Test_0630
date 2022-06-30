package com.zenithst.core.acl.web;

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

import com.zenithst.core.acl.api.ZappAclMgtService;
import com.zenithst.core.acl.vo.ZappAclExtend;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : Controller class for managing access control info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    '@Autowired
*	 private ZappAclMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@RestController
@RequestMapping(value = "/api/acl")
public class ZappAclController extends ZappController {
	
	/*
	* [Service]
	*/

	/* Access control */
	@Autowired
	private ZappAclMgtService service;
	
	/* Authentication */
	@Autowired
	private ZappAuthenticationMgtService authservice;
	

	/**
	 * <p><b>
	 * Inquire the list of content access control (AC) information. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/acl/list_content</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{<br> "objIsTest" : "N", "objDebugged" : false, <br>"" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentAcl Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>(PK)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01:Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappAclExtend&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentAcl
	 * @see ZappAclExtend
	 */
	@RequestMapping(value = "/list_content", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus list_content(@RequestBody ZappContentAcl pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectExtendObject(pZappAuth, null, pIn, result);
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
	 * Inquire the current user's optimized content access information. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/acl/get_content</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{<br> "objIsTest" : "N", "objDebugged" : false, <br>"" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentAcl Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID </td><td></td>
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
	 * 				<td>result</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappAclExtend
	 */
	@RequestMapping(value = "/get_content", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus get_content(@RequestBody ZappContentAcl pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.optimizeObject(pZappAuth, pIn, result);
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
	 * Inquire the list of classification access control (AC) information. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/acl/list_class</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{<br> "objIsTest" : "N", "objDebugged" : false, <br>"" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassAcl Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>(PK)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value (0:No access, 1:List, 2:View, 3:Print, 4:Download, 5:Edit) </td><td></td>
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
	 * 				<td>result</td><td>List&lt;ZappAclExtend&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappAclExtend
	 */
	@RequestMapping(value = "/list_class", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus list_class(@RequestBody ZappClassAcl pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectExtendObject(pZappAuth, null, pIn, result);
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
	 * Inquire the current user's optimized classification access information.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/acl/list_class</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{<br> "objIsTest" : "N", "objDebugged" : false, <br>"" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassAcl Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID </td><td></td>
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
	 * 				<td>result</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappAclExtend
	 */
	@RequestMapping(value = "/get_class", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwStatus get_class(@RequestBody ZappClassAcl pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.optimizeObject(pZappAuth, pIn, result);
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
	
}

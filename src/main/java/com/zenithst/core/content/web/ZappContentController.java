package com.zenithst.core.content.web;

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

import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.service.ZappCommonService;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.core.content.api.ZappContentMgtService;
import com.zenithst.core.content.api.ZappContentWorkflowMgtService;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappComment;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappKeywordExtend;
import com.zenithst.core.content.vo.ZappLinkedObject;
import com.zenithst.core.content.vo.ZappLinkedObjectExtend;
import com.zenithst.core.content.vo.ZappSharedObject;
import com.zenithst.core.content.vo.ZappSharedObjectExtend;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : Controller class for managing content info. <br>
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
@RequestMapping(value = "/api/content")
public class ZappContentController extends ZappController {
	
	/* Content */
	@Autowired
	private ZappContentMgtService service;
	
	@Autowired
	private ZappContentWorkflowMgtService cwservice;
	
	@Autowired
	private ZappAuthenticationMgtService authservice;
	
	@Autowired
	private ZappCommonService commonservice;
	
	/**
	 * <p><b>
	 * Register new content.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/add</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"objRetention" : "", </td><td>Retention period ID </td><td>●</td></tr>
	 * 				<tr><td>"zappBundle" : {"bno" : "", "title" : ""}, </td><td>Bundle (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappAdditoryBundle" : {"dynamic01" : "", ...}, </td><td>Additional bundle (when objType is 01 / See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappFiles" : [{"objFileName" : "", ... }], </td><td>List of file (when objType is 01) </td><td></td></tr>
	 * 				<tr><td>"zappFile" : {"objFileName" : "", ... }, </td><td>File (when objType is 02) </td><td></td></tr>
	 * 				<tr><td>"zappClassObjects" : [{"classid" : "", ... }], </td><td>List of classification (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"zappAcls" : [{"aclobjid" : "", ... }], </td><td>List of content access control info. (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"zappKeywords" : [{"kword" : "", ... }], </td><td>List of keyword info. (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objRetention</td><td>String</td><td>Retention period ID</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappBundle (objType 이 01)</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>bno</td><td>String</td><td>Bundle no.</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>title</td><td>String</td><td>Bundle Title</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAdditoryBundle (objType 이 01)</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic10</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 10</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileName</td><td>String</td><td>Full file path</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileExt</td><td>String</td><td>File Extension</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>filename</td><td>String</td><td>File name</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>checkFormat</td><td>Boolean</td><td>Whether to check the file type or not? (true/false)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic03</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 033</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classtype</td><td>String</td><td>Classification type<br>01:General, N1:Company, <br>N2:Department, N3:Private, <br>N4:Cooperation, 02:Classification, <br>03:Contnet type)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>권한<br>0:No access, 1:List, 2:View, 3:Print, 5:Edit</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappKeywordObject (선택)</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>kword</td><td>String</td><td>Keyword</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZappBundle
	 * @see ZappFile
	 * @see ZappClassObject
	 * @see ZappContentAcl
	 * @see ZappKeywordExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus add(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.addContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 변경한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/change</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>"objRetention" : "", </td><td>Retention period ID - Retention period 변경시 (코드에서 조회 및 선택) </td><td></td></tr>
	 * 				<tr><td>"zappBundle" : {"title" : ""}, </td><td>Bundle 수정 정보 (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappFiles" : [{"mfileid" : "", "holderid" : ""}], </td><td>File 수정 정보 (objType = 01 경우 사용) </td><td></td></tr>
	 * 				<tr><td>"zappFile" : {"holderid" : ""}, </td><td>File 수정 정보 (objType = 02 경우 사용) </td><td></td></tr>
	 * 				<tr><td>"zappClassObjects" : [<br>{"classid" : "", "classtype" : "", "objAction" : "<b>ADD</b>"}, <br>{"classobjid" : "", "objAction" : "<b>DISCARD</b>"}<br>], </td><td>분류 수정 정보(See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"zappAcls" : [<br>{"aclobjid" : "", "aclobjtype" : "", "objAction" : "<b>ADD</b>"}, <br>{"aclid" : "", "acls" : 4, "objAction" : "<b>CHANGE</b>"}, <br>{"aclid" : "", "objAction" : "<b>DISCARD</b>"}<br>], </td><td>컨텐츠 권한 수정 정보 (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"zappKeywords" : [{"kword" : "", "objAction" : "<b>ADD</b>"},<br> {"kwobjid" : "", "objAction" : "<b>DISCARD</b>"}] </td><td>Keyword 수정 정보(See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objRetention</td><td>String</td><td>Retention period ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappBundle (objType = 01)</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>title</td><td>String</td><td>Bundle Title</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classobjid</td><td>String</td><td>(PK)</td><td>● (objAction = DISCARD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classtype</td><td>String</td><td>Classification type<br>01:General, N1:Company, <br>N2:Department, N3:Private, <br>N4:Cooperation, 02:Classification, <br>03:Contnet type)</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>(PK)</td><td>● (objAction = CHANGE / DISCARD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>권한<br>0:No access, 1:List, 2:View, 3:Print, 4:Download, 5:Edit</td><td>● (objAction = CHANGE)</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappKeywordExtend</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>kwobjid</td><td>String</td><td>(PK)</td><td>● (objAction = DISCARD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>kword</td><td>String</td><td>Keyword</td><td>● (objAction = ADD)</td>
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
	 * @see ZappContentPar
	 * @see ZappBundle
	 * @see ZappFile
	 * @see ZappClassObject
	 * @see ZappContentAcl
	 * @see ZappKeywordExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus edit(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.changeContent(pZappAuth, pIn, result);
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
	 * 컨텐츠(파일)을 자동으로 버전 업 한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/edit/vu</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>"zappFile" : {"" : ""}, </td><td>File information </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileName</td><td>String</td><td>Full file path</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileExt</td><td>String</td><td>File Extension</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>filename</td><td>String</td><td>File name</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZappFile
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/edit/vu", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus edit_vu(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.replaceFile(pZappAuth, pIn, result);
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
	 * 컨텐츠를 disable 한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/disable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/disable", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus disable(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.disableContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 enable 한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/enable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/enable", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus enable(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.enableContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 폐기한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/discard</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/discard", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus discard(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.discardContent(pZappAuth, pIn, result);
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
	
	@RequestMapping(value = "/discardforcely", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus discardforcely(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.discardContentForcely(pZappAuth, pIn, result);
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
	 * 해당 컨텐츠의 최종 버전 파일을 폐기한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table style="width:80%" border="1">
	 *  	<tr style="background-color:#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr style="background-color:#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/discardversion</b></td>
	 * 		</tr>
	 * 		<tr style="background-color:#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table style="width:100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table style="width:80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr style="background-color:#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr style="background-color:#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr style="background-color:#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr style="background-color:#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table style="width:80%" border="1">

	 * 			<tr style="background-color:#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr style="background-color:#E2EAF1">
	 * 				<td>Timestamp</td><td>String</td><td>Timestamp</td>
	 * 			</tr>
	 * 			<tr style="background-color:#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>Result code</td>
	 * 			</tr>	
	 * 			<tr style="background-color:#E2EAF1">
	 * 				<td>error</td><td>String</td><td>Error Info.</td>
	 * 			</tr>
	 * 			<tr style="background-color:#E2EAF1">
	 * 				<td>message</td><td>String</td><td>Result message</td>
	 * 			</tr>
	 * 			<tr style="background-color:#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr style="background-color:#E2EAF1">
	 * 				<td>path</td><td>String</td><td>URL</td>
	 * 			</tr>
	 * 			<tr style="background-color:#E2EAF1">
	 * 				<td>result</td><td>String</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/discardversion", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus discardSpecificVersionContent(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.discardSpecificVersionContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 이동한다. (재분류)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/relocate</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>"zappClassObjects" : [<br>{"classid" : ""}, <br>{"classid" : ""}<br>], </td><td>재분류 전후 분류정보 (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/relocate", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus relocate(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.relocateContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 복사한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/replicate</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>"zappClassObjects" : [<br>{"classid" : ""}, <br>{"classid" : ""}<br>], </td><td>복사 전후 분류정보 (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/replicate", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus replicate(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.replicateContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 책갈피한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/mark</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 00: 분류, 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - 분류, Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (00: 분류, 01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Classification ID / Bundle ID / File ID)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/mark", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus mark(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.markContent(pZappAuth, pIn, result);
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
	 * 컨텐츠 책갈피를 해제한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/unmark</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 00: 분류, 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - 분류, Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (00: 분류, 01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Classification ID / Bundle ID / File ID)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/unmark", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus unmark(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.unmarkContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 조회한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/view</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>"objViewtype" : "", </td><td>조회 Type - 01: 조회용, 02:수정용 </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objViewtype</td><td>String</td><td>조회 Type - 01: 조회용, 02:수정용</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/view", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus view(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectContent(pZappAuth, pIn, result);
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
	 * 컨텐츠의 버전 정보를 조회한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/view</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - File ID </td><td>●</td></tr>
	 * 				<tr><td>"objViewtype" : "01" </td><td>조회 Type - 01: 조회용, 02:수정용 </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objViewtype</td><td>String</td><td>조회 Type - 01: 조회용, 02:수정용</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/viewversion", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus viewversion(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectVersion(pZappAuth, pIn, result);
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
	 * 컨텐츠의 물리적 목록을 조회 한다. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/list_p</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Progressing type<br> 00:NONE, 01: Classification, 02: Link, 03:Share, 04:Lock, 05:Mark </td><td>●</td></tr>
	 * 				<tr><td>"objRes" : "", </td><td>Result type (LIST / COUNT) </td><td>●</td></tr>
	 * 				<tr><td>"objIncLower" : "Y", </td><td>하위 분류 포함 여부 (Y/N) - objType : 01 일때 </td><td></td></tr>
	 * 				<tr><td>"objmaporder" : {"createtime" : "desc", "" : "", ...}, </td><td>Sorting info. </td><td></td></tr>
	 * 				<tr><td>"zappBundle" : {"" : "", ... }, </td><td>Bundle 정보 (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappAdditoryBundle" : {"dynamic01" : "", ...}, </td><td>Additional bundle (objType = 01 경우 사용 / See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappFile" : {"filename" : "", "ext" : ""}, </td><td>File 정보 (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappClassification" : {"classid" : "", "name" : "" }, </td><td>분류 정보 (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappSharedObject" : {"" : "", ... }, </td><td>Share info. (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"zappLinkedObject" : {"" : "", ... }, </td><td>연결 정보 (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"zappLockedObject" : {"" : "", ... }, </td><td>짐금 정보 (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"zappKeywords" : [{"kword" : ""},{"kword" : ""}], </td><td>Keyword 정보 (See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Progressing type<br>00:NONE, 01: Classification, 02: Linked, 03:Shared, 04:Locked, 05:Marked </td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objRes</td><td>String</td><td>Result type (LIST / COUNT)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objIncLower</td><td>String</td><td>Whether sub-classification are included or not? (Y/N) - only objType is 01</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td>●</td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappBundle</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>bundleid</td><td>String</td><td>Bundle ID (PK)</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>bno</td><td>String</td><td>Bundle no.</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>title</td><td>String</td><td>Bundle Title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorid</td><td>String</td><td>Creator ID</td><td></td><td>EQUAL</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorname</td><td>String</td><td>Creator name</td><td></td><td>LIKE</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorname</td><td>String</td><td>Createtime</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>discarderid</td><td>String</td><td>Discarder ID ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>updatetime</td><td>String</td><td>Update time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>retentionid</td><td>String</td><td>Retention period ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>expiretime</td><td>String</td><td>Expiretime</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>state</td><td>String</td><td>State</td><td></td><td>IN</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAdditoryBundle (objType 이 01)</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic10</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 10</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>filename</td><td>String</td><td>File name</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>ext</td><td>String</td><td>Extension</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic03</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 03</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Classification type<br>01:General, N1:Company, <br>N2:Department, N3:Private, <br>N4:Cooperation, 02:Classification, <br>03:Contnet type)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Classification code</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isactive</td><td>String</td><td>Use or not(Y/N)</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappSharedObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>sharerid</td><td>String</td><td>Sharer ID - If [Y] is specified, documents shared by the current user are inquired.</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>readerid</td><td>String</td><td>Reader ID (User/Department/Group) <br>- If [Y] is specified, documents shared with the current user are inquired.</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>readertype</td><td>String</td><td>Reader type (01:User, 02:Department, 03:Group)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>sharetime</td><td>String</td><td>Share time</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappLinkedObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>linkerid</td><td>String</td><td>Linker ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>linktype</td><td>String</td><td>Link type ()</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>linktime</td><td>String</td><td>Link time</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappLockedObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>lockerid</td><td>String</td><td>Locker ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>locktime</td><td>String</td><td>Lock time</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>releasetime</td><td>String</td><td>Unlock time</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappKeywordObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>kword</td><td>String</td><td>Keyword</td><td></td>
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
	 * @see ZappContentPar
	 * @see ZappBundle
	 * @see ZappFile
	 * @see ZappClassObject
	 * @see ZappContentAcl
	 * @see ZappKeywordExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/list_p", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_p(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjAccesspath(ZstFwValidatorUtils.valid(pIn.getObjCaller()) ? pIn.getObjCaller() : ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth);
		
		try {
			result = service.selectPhysicalList(pZappAuth, null, pIn, result);
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
	 * Inquire the list of content.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/list_np</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objHandleType" : "", </td><td>Progressing type<br> 01: Recently registered, 02: Registered by myself, 03: Owned, 04: Recently changed,<br>05:To be expired, 06:Expired, 07: Bin, <br>08: Discarded, 09: Expired (for manager) </td><td>●</td></tr>
	 * 				<tr><td>"objRes" : "", </td><td>Result type (LIST / COUNT) </td><td>●</td></tr>
	 * 				<tr><td>"objmaporder" : {"createtime" : "desc", "" : "", ...}, </td><td>Sorting info. </td><td></td></tr>
	 * 				<tr><td>"zappBundle" : {"" : "", ... }, </td><td>Bundle (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappAdditoryBundle" : {"dynamic01" : "", ...}, </td><td>Additional bundle (objType = 01 / See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappFile" : {"" : "", ... }, </td><td>File 정보 (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappKeywords" : [{"kword" : "", ... }], </td><td>Keyword(See details in paramater info.) </td><td>●</td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objHandleType</td><td>String</td><td>Progressing type<br> 01: Recently registered, 02: Registered by myself, 03: Owned, 04: Recently changed,<br>05:To be expired, 06:Expired, 07: Bin,<br>08: Discarded, 09: Expired (for manager)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objRes</td><td>String</td><td>Result type (LIST / COUNT)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td>●</td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappBundle</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>bundleid</td><td>String</td><td>Bundle ID (PK)</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>bno</td><td>String</td><td>Bundle no.</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>title</td><td>String</td><td>Bundle Title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorid</td><td>String</td><td>Creator ID</td><td></td><td>EQUAL</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorname</td><td>String</td><td>Creator name</td><td></td><td>LIKE</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorname</td><td>String</td><td>Createtime</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>discarderid</td><td>String</td><td>Discarder ID ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>updatetime</td><td>String</td><td>Update time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>retentionid</td><td>String</td><td>Retention period ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>expiretime</td><td>String</td><td>Expiretime</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>state</td><td>String</td><td>State</td><td></td><td>IN</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAdditoryBundle (objType 이 01)</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic10</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 10</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>mfileid</td><td>String</td><td>File ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>linkid</td><td>String</td><td>Link ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>filename</td><td>String</td><td>File name</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creator</td><td>String</td><td>Creator ID</td><td></td><td>EQUAL</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>createtime</td><td>String</td><td>Createtime</td><td></td><td>BETWEEN</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>updatetime</td><td>String</td><td>Updatetime</td><td></td><td>BETWEEN</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>expiredate</td><td>String</td><td>Expiretime</td><td></td><td>BETWEEN</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>state</td><td>String</td><td>State</td><td></td><td></td>
	 * 			</tr>
	 *   		<tr></tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>fno</td><td>String</td><td>File no.</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorname</td><td>String</td><td>Creator name</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>discarderid</td><td>String</td><td>Discarder ID ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 
	 * 				<td>retentionid</td><td>String</td><td>Retention period ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>expiretime</td><td>String</td><td>Expiretime</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>ext</td><td>String</td><td>File Extension</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic03</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 03</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappKeywordObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>kword</td><td>String</td><td>Keyword</td><td></td>
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
	 * @see ZappContentPar
	 * @see ZappBundle
	 * @see ZappFile
	 * @see ZappKeywordExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/list_np", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_np(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjAccesspath(ZstFwValidatorUtils.valid(pIn.getObjCaller()) ? pIn.getObjCaller() : ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth);
		
		try {
			result = service.selectNonPhysicalList(pZappAuth, null, pIn, result);
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
	 * Inquire the list of content from FTR.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/list_ftr</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID  </td><td>●</td></tr>
	 * 				<tr><td>"objRes" : "", </td><td>Result type (LIST / COUNT) </td><td>●</td></tr>
	 * 				<tr><td>"objmaporder" : {"createtime" : "desc", "" : "", ...}, </td><td>Sorting info. </td><td></td></tr>
	 * 				<tr><td>"zappBundle" : {"" : "", ... }, </td><td>Bundle (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"zappFile" : {"" : "", ... }, </td><td>File 정보 (See details in paramater info.) </td><td></td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>sword</td><td>String</td><td>Search Word</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objRes</td><td>String</td><td>Result type (LIST / COUNT)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td>●</td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappBundle</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Default filter</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>bundleid</td><td>String</td><td>Bundle ID (PK)</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>bno</td><td>String</td><td>Bundle no.</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>title</td><td>String</td><td>Bundle Title</td><td></td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorid</td><td>String</td><td>Creator ID</td><td></td><td>EQUAL</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorname</td><td>String</td><td>Creator name</td><td></td><td>LIKE</td>
	 * 			</td>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>creatorname</td><td>String</td><td>Createtime</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>discarderid</td><td>String</td><td>Discarder ID ID</td><td></td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>updatetime</td><td>String</td><td>Update time</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>retentionid</td><td>String</td><td>Retention period ID</td><td></td><td>IN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>expiretime</td><td>String</td><td>Expiretime</td><td></td><td>BETWEEN</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>state</td><td>String</td><td>State</td><td></td><td>IN</td>
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
	 * @see ZappContentPar
	 * @see ZappBundle
	 * @see ZappFile
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/list_ftr", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_ftr(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
		pZappAuth.setObjAccesspath(ZstFwValidatorUtils.valid(pIn.getObjCaller()) ? pIn.getObjCaller() : ZappConts.AUTHENTICATION.ACCESSPATH_WEB.auth);
		
		try {
			result = service.selectFTRList(pZappAuth, pIn, result);
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
	 * Set link information.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/link/add</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"sourceid" : "", </td><td>Source Content ID </td><td>●</td></tr>
	 * 				<tr><td>"targetid" : "", </td><td>Target Content ID </td><td>●</td></tr>
	 * 				<tr><td>"linktype" : "", </td><td>Link type<br>01: -> Bundle, 02: -> File, 03: -> Classification </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappLinkedObject (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappLinkedObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>sourceid</td><td>String</td><td>Source Content ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>targetid</td><td>String</td><td>Target Content ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>linktype</td><td>String</td><td>Link type<br>01: -> Bundle, 02: -> File, 03: -> Classification</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/link/add", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus add(@RequestBody ZappLinkedObject pIn, HttpServletRequest request, HttpSession session) {
		
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
	 * Unset link information.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/link/release</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"linkedobjid" : "", </td><td>(PK) </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappLinkedObject (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappLinkedObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>linkedobjid</td><td>String</td><td>(PK)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/link/release", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus release(@RequestBody ZappLinkedObject pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.deleteObject(pZappAuth, pIn, result);
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
	 * Inquire list of link info. (based on contents)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/link/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"sourceid" : "", </td><td>Source Content ID </td><td>●</td></tr>
	 * 				<tr><td>"objmaporder" : {"createtime" : "desc", "" : "", ...}, </td><td>Sorting info. </td><td></td></tr>
	 * 				<tr><td>"objpgnum" : 1  </td><td>Paging Info.</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappLinkedObject (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>sourceid</td><td>String</td><td>Source Content ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objmaporder</td><td>String</td><td>Sorting info.</td><td>●</td>
	 * 			</tr>
	 *			<tr bgcolor="#95BEE1">
	 * 				<td>objpgnum</td><td>String</td><td>Paging Info.</td><td>●</td>
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
	 * 				<td>result</td><td>List&lt;ZappLinkedObjectExtend&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappLinkedObject
	 * @see ZappLinkedObjectExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/link/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list(@RequestBody ZappLinkedObject pIn, HttpServletRequest request, HttpSession session) {
		
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
	 * Inquire list of shared info. (based on contents) 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/share/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID (Bundle/File ID)</td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type<br>01:Bundle, 02:File </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
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
	 * 				<td>result</td><td>List&lt;ZappLinkedObjectExtend&gt;</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappSharedObject
	 * @see ZappSharedObjectExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/share/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_share(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			ZappSharedObject pZappSharedObject = new ZappSharedObject();
			pZappSharedObject.setSobjid(pIn.getContentid());
			pZappSharedObject.setSobjtype(pIn.getObjType());
			result = service.selectExtendObject(pZappAuth, null, pZappSharedObject, result);
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
	 * Share the content.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/share</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID (Bundle/File ID) </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type<br>01:Bundle, 02:File </td><td>●</td></tr>
	 * 				<tr><td>"zappSharedObjects" : [{"readerid":"", "readertype":""}, {"readerid":"", "readertype":""}], </td><td>Share info. </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param pIn ZappSharedObject (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappSharedObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>readerid</td><td>String</td><td>Reader ID (User/Department/Group ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>readertype</td><td>String</td><td>Reader type<br>01:User, 02:Department, 03:Group</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/share", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus share(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.shareContent(pZappAuth, pIn, result);
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
	 * Edit shared info. (Adding and deleting)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/changeshare</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID (Bundle/File ID) </td><td>●</td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type<br>01:Bundle, 02:File </td><td>●</td></tr>
	 * 				<tr><td>"zappSharedObjects" : [{"shareobjid":"", "objAction":"DISCARD"}, {"readerid":"","readertype":"", "objAction":"ADD" }], </td><td>Share info. </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param pIn ZappSharedObject (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappSharedObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>shareobjid</td><td>String</td><td>(PK)</td><td>(objAction = DISCARD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>readerid</td><td>String</td><td>Reader ID (User/Department/Group ID)</td><td>(objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>readertype</td><td>String</td><td>Reader type<br>01:User, 02:Department, 03:Group</td><td>(objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objAction</td><td>String</td><td>Processing type (ADD/DISCARD)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/changeshare", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus unshare(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.changeShareContent(pZappAuth, pIn, result);
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
	 * Lock contents.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/lock</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle ID or File ID </td><td>●</td></tr>
	 * 				<tr><td>"releasetime" : "", </td><td>Lock release date </td><td>●</td></tr>
	 * 				<tr><td>"reason" : "", </td><td>Lock reason </td><td>●</td></tr>
	 * 				<tr><td>"zappFile" : {"mfileid" : "", "versionid" : ""}, </td><td>File info. </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>releasetime</td><td>String</td><td>Lock release date</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>reason</td><td>String</td><td>Lock reason</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>mfileid</td><td>String</td><td>File ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>versionid</td><td>String</td><td>Version ID</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/lock", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus lock(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.lockContent(pZappAuth, pIn, result);
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
	 * Unlock contents.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/unlock</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"objTaskid" : "", </td><td>Task ID </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle or File ID </td><td>●</td></tr>
	 * 				<tr><td>"hasFile" : true, </td><td>Whether to include files or not? (true / false)</td><td>●</td></tr>
	 * 				<tr><td>"zappFile" : {"mfileid" : "", "objFileName" : "", ... }, </td><td>File info. </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>hasFile</td><td>Boolean</td><td>Whether to include files or not? (true / false)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>mfileid</td><td>String</td><td>File ID</td><td>● (objType = 01)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileName</td><td>String</td><td>Full file path</td><td>● (hasFile = true)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileExt</td><td>String</td><td>File Extension</td><td>● (hasFile = true)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>filename</td><td>String</td><td>File name</td><td>● (hasFile = true)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isreleased</td><td>Boolean</td><td>Released? (true/false)<br>true: Upper version, false: Lower version</td>●<td></td>
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
	 * @see ZappContentPar
	 * @see ZappFile
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/unlock", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus unlock(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.unlockContent(pZappAuth, pIn, result);
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
	 * Forcely unlock contents. (for manager)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/unlockforcely</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle ID or File ID </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/unlockforcely", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus unlockForcely(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.unlockContentForcely(pZappAuth, pIn, result);
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
	 * Extend the retention period of content.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/extendretention</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>Content type - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>Content ID - Bundle ID or File ID </td><td>●</td></tr>
	 * 				<tr><td>"objRetention" : "", </td><td>Retention period ID</td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>Task ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>Content type (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle ID / File ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objRetention</td><td>Boolean</td><td>Retention period ID</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/extendretention", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus extendretention(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.extendContentRetention(pZappAuth, pIn, result);
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
	 * 컨텐츠를 승인 처리한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>항목</b></td><td><b>설명</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/approve</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>호출예제</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>테스트 Mode 여부 (Y/N) - 세션이 존재하지 않는 경우 Y, 하는 경우 N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>디버깅 여부 (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>컨텐츠 유형 - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>컨텐츠 아이디 - Bundle or File 아이디 </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td><td><b>필수여부</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>업무 아이디</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>컨텐츠 대상 유형 (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>컨텐츠 아이디 (Bundle 아이디 / FIle 아이디)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>timestamp</td><td>String</td><td>timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>처리 결과 코드</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>오류</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>오류 내용</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>호출 경로</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>String</td><td>결과</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/approve", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus approveContent(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = cwservice.approveContent(pZappAuth, pIn, result);
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
	 * 컨텐츠를 반려 처리한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>항목</b></td><td><b>설명</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/return</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>호출예제</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>테스트 Mode 여부 (Y/N) - 세션이 존재하지 않는 경우 Y, 하는 경우 N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>디버깅 여부 (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>컨텐츠 유형 - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>컨텐츠 아이디 - Bundle or File 아이디 </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td><td><b>필수여부</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>업무 아이디</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>컨텐츠 대상 유형 (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>컨텐츠 아이디 (Bundle 아이디 / FIle 아이디)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>timestamp</td><td>String</td><td>timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>처리 결과 코드</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>오류</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>오류 내용</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>호출 경로</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>String</td><td>결과</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/return", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus returnContent(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = cwservice.returnContent(pZappAuth, pIn, result);
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
	 * 승인 처리를 철회한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>항목</b></td><td><b>설명</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/discardreturned</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>호출예제</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>테스트 Mode 여부 (Y/N) - 세션이 존재하지 않는 경우 Y, 하는 경우 N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>디버깅 여부 (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>컨텐츠 유형 - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>컨텐츠 아이디 - Bundle or File 아이디 </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td><td><b>필수여부</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>업무 아이디</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>컨텐츠 대상 유형 (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>컨텐츠 아이디 (Bundle 아이디 / FIle 아이디)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>timestamp</td><td>String</td><td>timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>처리 결과 코드</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>오류</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>오류 내용</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>호출 경로</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>String</td><td>결과</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/withdraw", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus withdrawContent(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = cwservice.withdrawContent(pZappAuth, pIn, result);
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
	 * 반려 처리건을 폐기한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>항목</b></td><td><b>설명</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/discardreturned</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>호출예제</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>테스트 Mode 여부 (Y/N) - 세션이 존재하지 않는 경우 Y, 하는 경우 N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>디버깅 여부 (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>컨텐츠 유형 - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>"contentid" : "", </td><td>컨텐츠 아이디 - Bundle or File 아이디 </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>공통</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td><td><b>필수여부</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objTaskid</td><td>String</td><td>업무 아이디</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objType</td><td>String</td><td>컨텐츠 대상 유형 (01:Bundle, 02:File)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>컨텐츠 아이디 (Bundle 아이디 / FIle 아이디)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>timestamp</td><td>String</td><td>timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>처리 결과 코드</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>오류</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>오류 내용</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>호출 경로</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>String</td><td>결과</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/undo", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus undoContent(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = cwservice.undoContent(pZappAuth, pIn, result);
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
	 * Check the same filename in a folder
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>항목</b></td><td><b>설명</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/discardreturned</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>호출예제</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>테스트 Mode 여부 (Y/N) - 세션이 존재하지 않는 경우 Y, 하는 경우 N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>디버깅 여부 (true/false) </td><td></td></tr>
	 * 				<tr><td>"objType" : "", </td><td>컨텐츠 유형 - 01: Bundle, 02: File </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappContentPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappFile</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileName</td><td>String</td><td>Full file path</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objFileExt</td><td>String</td><td>File Extension</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>filename</td><td>String</td><td>File name</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classtype</td><td>String</td><td>Classification type<br>01:General, N1:Company, <br>N2:Department, N3:Private, <br>N4:Cooperation, 02:Classification, <br>03:Contnet type)</td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param request HttpServletRequest <br>
	 * @param session HttpSession <br>
	 * @return ZstFwStatus <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>변수명</b></td><td><b>유형</b></td><td><b>설명</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>timestamp</td><td>String</td><td>timestamp</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>status</td><td>Object</td><td>처리 결과 코드</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>error</td><td>String</td><td>오류</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>message</td><td>String</td><td>오류 내용</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>trace</td><td>String</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>path</td><td>String</td><td>호출 경로</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>result</td><td>boolean</td><td>true 존재함</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/checkfilename", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus checkfilename(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.existFilename(pZappAuth, pIn, result);
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
	 * Add a comment.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/comment/add</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"cobjid" : "", </td><td>Object ID </td><td>●</td></tr>
	 * 				<tr><td>"cobjtype" : "", </td><td>Object Type </td><td>●</td></tr>
	 * 				<tr><td>"comments" : "", </td><td>Comment </td><td>●</td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappComment (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappComment</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cobjid</td><td>String</td><td>Object ID</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cobjtype</td><td>String</td><td>Object Type</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>comments</td><td>String</td><td>Comments</td><td>●</td>
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
	 * @see ZappContentPar
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/comment/add", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus add(@RequestBody ZappComment pIn, HttpServletRequest request, HttpSession session) {
		
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
	 * List comments.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="80%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/content/comment/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example</td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"cobjid" : "", </td><td>Object ID </td><td></td></tr>
	 * 				<tr><td>"cobjtype" : "", </td><td>Object Type </td><td></td></tr>
	 * 				<tr><td>"comments" : "", </td><td>Comment </td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappComment (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappComment</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cobjid</td><td>String</td><td>Object ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>cobjtype</td><td>String</td><td>Object Type</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>comments</td><td>String</td><td>Comments</td><td></td>
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
	 * @see ZappComment
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/comment/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list(@RequestBody ZappComment pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectObject(pZappAuth, pIn, result);
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
	
	
	@RequestMapping(value = "/test", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public String test(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
		
		return "{\"MFILE\":{\"esproc\":null}}";
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

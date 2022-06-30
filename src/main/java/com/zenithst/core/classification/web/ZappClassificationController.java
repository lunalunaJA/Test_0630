package com.zenithst.core.classification.web;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.api.ZappClassificationMgtService;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.classification.vo.ZappClassificationPar;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.core.content.vo.ZappKeywordExtend;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;
import com.zenithst.framework.util.ZstFwValidatorUtils;


/**  
* <pre>
* <b>
* 1) Description : Controller class for managing classification info. <br>
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
@RequestMapping(value = "/api/classification")
public class ZappClassificationController extends ZappController {
	
	/* Classification */
	@Autowired
	private ZappClassificationMgtService service;
	
	/* Authentication */
	@Autowired
	private ZappAuthenticationMgtService authservice;
	
	/* ******* Classification ********* */
	
	/**
	 * <p><b>
	 * Register new classification. (including basic info., classification access control info., default content access control info. and keyword info.)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/add</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example (Company folder) </td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td>●</td></tr>
	 * 				<tr><td>"code" : "", </td><td>Classification code</td><td></td></tr>
	 * 				<tr><td>"name" : "", </td><td>Classification name</td><td>●</td></tr>
	 * 				<tr><td>"upid" : "", </td><td>Upper ID - If not specified, company ID is entered.</td><td></td></tr>
	 * 				<tr><td>"types" : "N1", </td><td>Type</td><td>●</td></tr>
	 * 				<tr><td>"zappAdditoryClassification" : {"dynamic01" : "", ...} </td><td>Additional classification information</td><td></td></tr>
	 * 				<tr><td>"zappKeywordObjects" : [{"kword" : ""}] </td><td>Keyword</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>	
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example (Department, Cooperation folder) </td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td>●</td></tr>
	 * 				<tr><td>"code" : "", </td><td>Classification code</td><td></td></tr>
	 * 				<tr><td>"name" : "", </td><td>Classification name</td><td>●</td></tr>
	 * 				<tr><td>"upid" : "", </td><td>Upper ID - If not specified, dept. ID  or company ID is entered.</td><td></td></tr>
	 * 				<tr><td>"types" : "N2 or N4", </td><td>Type</td><td>●</td></tr>
	 * 				<tr><td>"zappAdditoryClassification" : {"dynamic01" : "", ...} </td><td>Additional classification information</td><td></td></tr>
	 * 				<tr><td>"zappClassAcls" : [{"aclobjid" : "", "aclobjtype" : "", "acls" : 2}], </td><td>분류 권한</td><td>●</td></tr>
	 * 				<tr><td>"zappContentAcls" : [{"contenttype" : "", "aclobjid" : "", "aclobjtype" : "", "acls" : 2}], </td><td>컨텐츠 권한</td><td>●</td></tr>
	 * 				<tr><td>"zappKeywordObjects" : [{"kword" : ""}] </td><td>Keyword</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>	
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Example (Private folder) </td>
	 * 			<td><table width="100%" border="0">
	 * 				<tr><td>{</td><td></td><td></td></tr>
	 * 				<tr><td>"objIsTest" : "N", </td><td>Test or not (Y/N) - if the session is valid, Y otherwise N</td><td></td></tr>
	 * 				<tr><td>"objDebugged" : false, </td><td>Debug or not (true/false) </td><td></td></tr>
	 * 				<tr><td>"companyid" : "", </td><td>Company ID</td><td>●</td></tr>
	 * 				<tr><td>"name" : "", </td><td>Classification name</td><td>●</td></tr>
	 * 				<tr><td>"upid" : "", </td><td>Upper ID - If not specified, user ID is entered.</td><td></td></tr>
	 * 				<tr><td>"types" : "N3", </td><td>Type</td><td>●</td></tr>
	 * 				<tr><td>"zappAdditoryClassification" : {"dynamic01" : "", ...} </td><td>Additional classification information</td><td></td></tr>
	 * 				<tr><td>}</td><td></td></tr>
	 * 				</table>
	 *			</td>
	 * 		</tr>	 
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassificationPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>companyid</td><td>String</td><td>Company ID(Auto-input)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Classification code</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>descpt</td><td>String</td><td>Desc.</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>holderid</td><td>String</td><td>Holder ID(Auto-input)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Classification type(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAdditoryClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic10</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 10</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>classid</td><td>String</td><td>Classification ID(Auto-input)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>권한 <br>(0:Viewing not allowed + Registering not allowed, 1:Viewing allowed + Registering not allowed, 2:Viewing allowed + Registering allowed) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>contentid</td><td>String</td><td>Classification ID(Auto-input)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>contenttype</td><td>String</td><td>Content type (00:Classification, 01:Bundle, 02:File)(Auto-input)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>(0:No access, 1:List, 2:View, 3:Print, 5:Edit) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappKeywordObject</caption>
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
	 * @see ZappClassification
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 * @see ZappClassificationPar
	 * @see ZappKeywordExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus add(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.addClass(pZappAuth, pIn, result);
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
	 * Edit classification info. (including basic info., classification access control info., default content access control info. and keyword info.)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/change</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{<br> "objIsTest" : "N", "objDebugged" : false, <br>"classid" : "", "types" : "", ... 
	 * 						     <br>"zappClassAcls" : [{"aclid" : "", "aclobjid" : "", "aclobjtype" : "", "acls" : 2, "objAction" : "ADD" }], 
	 * 							 <br>"zappContentAcls" : [{"aclid" : "", "aclobjid" : "", "aclobjtype" : "", "acls" : 2 "objAction" : "DISCARD"}] 
	 * 							 <br>"zappKeywordObjects" : [{"kword" : "", "objAction" : "ADD"}, {"kwobjid" : "", "objAction" : "DISCARD"}]}</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassificationPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Classification type(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>descpt</td><td>String</td><td>Desc.</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappAdditoryClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dynamic01 ~ dynamic10</td><td>String</td><td>Dynamic value 01 ~ Dynamic value 10</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>(PK)</td><td>● (objAction = DISCARD / CHANGE) </td>
	 * 			</tr>
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>classid</td><td>String</td><td>Classification ID(시스템자동설정)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group)</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>권한 <br>(0:Viewing not allowed + Registering not allowed, 1:Viewing allowed + Registering not allowed, 2:Viewing allowed + Registering allowed) </td><td>● (objAction = ADD / CHANGE)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objAction</td><td>String</td><td>Processing type(ADD:New, CHANGE:변경, DISCARD:Discard) </td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>(PK)</td><td>● (objAction = DISCARD / CHANGE) </td>
	 * 			</tr>
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>contentid</td><td>String</td><td>Classification ID(Auto-input)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>contenttype</td><td>String</td><td>Content type (00:Classification, 01:Bundle, 02:File)(Auto-input)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group)</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>권한 <br>(0:No access, 1:List, 2:View, 3:Print, 5:Edit) </td><td>● (objAction = ADD / CHANGE)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objAction</td><td>String</td><td>Processing type(ADD:New, CHANGE:변경, DISCARD:Discard) </td><td>●</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappKeywordObject</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>kwobjid</td><td>String</td><td>(PK)</td><td>● (objAction = DISCARD)</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>kword</td><td>String</td><td>Keyword</td><td>● (objAction = ADD)</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objAction</td><td>String</td><td>Processing type(ADD:New, DISCARD:Discard) </td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 * @see ZappClassificationPar
	 * @see ZappKeywordExtend
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/change", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus change(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.changeClass(pZappAuth, pIn, result);
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
	 * 분류명를 수정한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/change/name</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{<br> "objIsTest" : "N", "objDebugged" : false, <br>"classid" : "", "name" : "" }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassificationPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td>●</td>
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
	 * @see ZappClassificationPar
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/change/name", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	
	public ZstFwStatus change_name(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
		
		/* 분류 정보만 수정 */
		pZappAuth.setObjLevel(ZappConts.ACTION.CHANGE_PK.name());
		
		try {
			result = service.changeClass(pZappAuth, pIn, result);
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
	 * 분류정보를 disable 한다.<br>
	 * (사용중지 여부를 Y->N 으로 변경한다.)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/disable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objIncLower</td><td>String</td><td>하위 분류 disable 여부 (Y/N)</td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/disable", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus disable(@RequestBody ZappClassification pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.disableClass(pZappAuth, null, pIn, result);
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
	 * 분류정보를 enable 한다.<br>
	 * (사용중지 여부를 N->Y 으로 변경한다.)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/enable</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objIncLower</td><td>String</td><td>하위 분류 disable 여부 (Y/N)</td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */
	@RequestMapping(value = "/enable", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus enable(@RequestBody ZappClassification pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.enableClass(pZappAuth, null, pIn, result);
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
	 * 분류정보를 discard 한다.<br>
	 * (실제 정보를 폐기한다. - 기본정보, Classification access control, 컨텐츠기본권한)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/discard</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/discard", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus discard(@RequestBody ZappClassification pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.discardClass(pZappAuth, null, pIn, result);
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
	 * 분류정보를 조회한다. (PK 기준)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/get</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/get", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus get(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectClass(pZappAuth, pIn, result);
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
	 * 분류 목록정보를 조회한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/list</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Classification code</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>descpt</td><td>String</td><td>Desc.</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Classification type(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isactive</td><td>String</td><td>Use or not(Y/N)</td><td></td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list(@RequestBody ZappClassification pIn, HttpServletRequest request, HttpSession session) {
		
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

	/**
	 * <p><b>
	 * 상위에서 하위 부서 정보를 분류정보에 저장하여 목록정보를 조회한다. (DOWNWARD)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/list/down</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappDept (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDept</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#cc6956">
	 * 				<td>companyid</td><td>String</td><td>Company ID (Auto-input in session)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Code</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Department Abbreviation</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isactive</td><td>String</td><td>Use or not</td><td></td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/list/down_1st", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_down_1st(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectOrganDown(pZappAuth, null, pIn, result);
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
	 * 상위에서 하위 분류 목록정보를 조회한다. (DOWNWARD)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/list/down</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassificationPar (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Classification code</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>descpt</td><td>String</td><td>Desc.</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Classification type(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isactive</td><td>String</td><td>Use or not(Y/N)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objSkipAcl</td><td>Boolean</td><td>Apply access control or not?(true = Unapplied, false = Applied)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>viewlevel</td><td>Integer</td><td>조회 지정 래벨(0 이면 전체, 0 이상이면 해당 레벨까지 조회 )</td><td></td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/list/down", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_down(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectObjectDown(pZappAuth, null, pIn, result);
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
	 * 하위에서 상위 분류 목록정보를 조회한다. (UPWRARD)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/list/up</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Classification code</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>descpt</td><td>String</td><td>Desc.</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Classification type(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isactive</td><td>String</td><td>Use or not(Y/N)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objSkipAcl</td><td>Boolean</td><td>Apply access control or not?(true = Unapplied, false = Applied)</td><td></td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/list/up", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_up(@RequestBody ZappClassification pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectObjectUp(pZappAuth, null, pIn, result);
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
	 * 책갈피 분류 목록정보를 조회한다.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/list/up</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objres</td><td>String</td><td>조회 대상 (LIST / COUNT)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Classification code</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>descpt</td><td>String</td><td>Desc.</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>holderid</td><td>String</td><td>Holder ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Classification type(01:General, N1:Company, N2:Department,<br>N3:Private, N4:Cooperation, <br>02:Classification, <br>03:Contnet type)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isactive</td><td>String</td><td>Use or not(Y/N)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>objSkipAcl</td><td>Boolean</td><td>Apply access control or not?(true = Unapplied, false = Applied)</td><td></td>
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
	 * 				<td>result</td><td>String</td><td>Result Info.</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @see ZappAuth
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/list/mark", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus list_mark(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.selectMarkedList(pZappAuth, pIn, result);
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
	 * Change the location of the classification. <br>
	 * (Moves from the current upper ID to the designated upper ID.)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/relocate</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/relocate", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus relocate(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.relocateClass(pZappAuth, null, pIn, result);
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
	 * Copy classification info.<br>
	 * (Copy the same classification info. to the designated upper ID.)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/replicate</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/replicate", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus replicate(@RequestBody ZappClassificationPar pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.replicateClass(pZappAuth, null,  pIn, result);
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
	 * Change the order of the classification info. at the same level.<br>
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  <table width="60%" border="1">
	 *  	<tr bgcolor="#CDD307">
	 * 			<td><b>Name</b></td><td><b>Desc.</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>URL</td><td><b>ROOT_CONTEXT/api/classification/reorder</b></td>
	 * 		</tr>
	 * 		<tr bgcolor="#E1E563">
	 * 			<td>Ex.</td><td>{ "objIsTest" : "N", "objDebugged" : false, "" : "", ... }</td>
	 * 		</tr>
	 *  </table>
	 * 
	 * </pre>
	 * 
	 * @param pIn ZappClassification (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassification</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID(PK)</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>priority</td><td>Interger</td><td>Sorting order</td><td>●</td>
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
	 * @see ZappClassification
	 * @see ZstFwResult
	 */	
	@RequestMapping(value = "/reorder", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus reorder(@RequestBody ZappClassification pIn, HttpServletRequest request, HttpSession session) {
		
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
			result = service.reorderClass(pZappAuth, null,  pIn, result);
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

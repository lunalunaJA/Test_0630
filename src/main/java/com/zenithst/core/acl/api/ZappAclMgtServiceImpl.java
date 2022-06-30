package com.zenithst.core.acl.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.acl.bind.ZappAclBinder;
import com.zenithst.core.acl.service.ZappAclService;
import com.zenithst.core.acl.vo.ZappAclExtend;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.service.ZappClassificationService;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.classification.vo.ZappClassificationRes;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.content.vo.ZappClassObjectRes;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.organ.service.ZappOrganService;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupUserExtend;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage access control info. <br>
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

@Service("zappAclMgtService")
public class ZappAclMgtServiceImpl extends ZappService implements ZappAclMgtService {

	/*
	* [Service]
	*/

	/* Access control */
	@Autowired
	private ZappAclService aclService;

	/* Classification */
	@Autowired
	private ZappClassificationService classService;
	
	/* Organization */
	@Autowired
	private ZappOrganService organService;

	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/*
	* [Binder]
	*/

	/* Common */
	@Autowired
	private ZappAclBinder utilBinder;
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappAclMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappAclMgtService] Service Destroy ");
	}

	/**
	 * <p><b>
	 * Register new access control information. (for classifications, contents)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.addObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObj Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:Viewing not allowed + Registering not allowed, 1:Viewing allowed + Registering not allowed, 2:Viewing allowed + Registering allowed) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01:Bundle, 02:File) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:No access, 1:List, 2:View, 3:Print, 4:Download, 5:Edit </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>String</td><td>PK</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult addObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  BLANK), BLANK);
		}
		
		/* [Multiple registration] 
		 * 
		 */
		if(pObj instanceof List) {
			pObjRes = aclService.cMultiRows(pObjAuth, pObj, pObjRes);
		} 
		else {
			
			/* Validation */
			if(utilBinder.isEmpty(pObj) == false) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  BLANK), BLANK);
			}
			
			/* Classification */
			if(pObj instanceof ZappClassAcl) {}
			
			/* Content */
			if(pObj instanceof ZappContentAcl) {}
			
			pObjRes = aclService.cSingleRow(pObjAuth, pObj, pObjRes);
		}
		
		return pObjRes;

	}
	

	/**
	 * <p><b>
	 * Edit access control information. (for classification, content) - based on PK
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	ZstFwResult result = service.changeObject(pZappAuth, pIn, );
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObj Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>(PK)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:Viewing not allowed + Registering not allowed, 1:Viewing allowed + Registering not allowed, 2:Viewing allowed + Registering allowed </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>(PK) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:No access, 1:List, 2:View, 3:Print, 4:Download, 5:Edit </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObj) || utilBinder.isEmpty(pObj)) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
		}
		
		/* Classification */
		if(pObj instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObj instanceof ZappContentAcl) {}
		
		pObjRes = aclService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}
	

	/**
	 * <p><b>
	 * Edit access control information. (for classification, content) - based on default filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	ZstFwResult result = service.changeObject(pZappAuth, pIn, );
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjs Object for Set (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:Viewing not allowed + Registering not allowed, 2:Viewing allowed + Registering not allowed, 3:Viewing allowed + Registering allowed </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:No access, 1:List, 2:View, 3:Print, 4:Download, 5:Edit </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param pObj Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	/**
	 * <p><b>
	 * Edit access control information. (for classification, content) - based on dynamic filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	ZstFwResult result = service.changeObject(pZappAuth, pIn, );
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjs Object for Set (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:Viewing not allowed + Registering not allowed, 2:Viewing allowed + Registering not allowed, 3:Viewing allowed + Registering allowed </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:No access, 1:List, 2:View, 3:Print, 5:Edit </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param pObj Object for Filter (Nullable) - If the value is null, the default filter is applied. <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @param pObj Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
		}
		
		/* Classification access control */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content  access control */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}	
	

	/**
	 * <p><b>
	 * Merge access control information. (for classification, content) - based on dynamic filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	ZstFwResult result = service.changeObject(pZappAuth, pIn, );
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjs Object for Set (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:Viewing not allowed + Registering not allowed, 2:Viewing allowed + Registering not allowed, 3:Viewing allowed + Registering allowed </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:No access, 1:List, 2:View, 3:Print, 5:Edit </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param pObj Object for Filter (Nullable) - If the value is null, the default filter is applied. <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @param pObj Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult mergeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObj) || utilBinder.isEmpty(pObj)) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[mergeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
		}
		
		/* Classification access control */
		if(pObj instanceof ZappClassAcl) {}
		
		/* Content  access control */
		if(pObj instanceof ZappContentAcl) {}
		
		pObjRes = aclService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}

	/**
	 * <p><b>
	 * Delete access control information.(for classification, content) - based on PK
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.deleteObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjw Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  BLANK), BLANK);
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = aclService.dSingleRow(pObjAuth, pObjw, pObjRes);	// Single processing
		} else {	
			pObjRes = aclService.dMultiRows(pObjAuth, pObjw, pObjRes);	// 
		}
		
		return pObjRes;
		
	}
	

	/**
	 * <p><b>
	 * Delete access control information. (for classification, content) - based on dynamic filers
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.deleteObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjf Object for Filter (Nullable) - If the value is null, the default filter is applied <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  BLANK), BLANK);
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
		
	}	


	/**
	 * <p><b>
	 * Inquire access control information. (for classification, content) - based on PK or default filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.selectObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>String</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>Object</td><td>based on PK</td>
	 * 			</tr>
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>List&lt;Object&gt;</td><td>based on not PK</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = aclService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = aclService.rMultiRows(pObjAuth, pObjw, pObjRes);
		}
		
		return pObjRes;
	}

	
	/**
	 * <p><b>
	 * Inquire access control infomation. (for classification, content) - based on dynamic filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pInf = new ZappClassAcl();
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pInf = new ZappContentAcl();
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.selectObject(pZappAuth, pInf, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjf Object for Filter (Nullable) - If the value is null, the default filter is applied <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>List&lt;Object&gt;</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}

	
	/**
	 * <p><b>
	 * Inquire the existence of access control information. (for classification, content) - based on default filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.existObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>Boolean</td><td>Exist or not</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	
	/**
	 * <p><b>
	 * Inquire the existence of access control information. (for classification, content) - based on dynamic filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pInf = new ZappClassAcl();
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pInf = new ZappContentAcl();
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.existObject(pZappAuth, pInf, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjf Object for Filter (Nullable) - If the value is null, the default filter is applied <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>Boolean</td><td>Exist or not</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}

	
	/**
	 * <p><b>
	 * Count access control information. (for classification, content) - based on default filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.countObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>Integer</td><td>Counted number</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */	
	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	
	/**
	 * <p><b>
	 * Count access control information. (for classification, content) - based on dynamic filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pInf = new ZappClassAcl();
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pInf = new ZappContentAcl();
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.countObject(pZappAuth, pInf, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjf Object for Filter (Nullable) - If the value is null, the default filter is applied <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>Integer</td><td>Counted number</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}	

	/* ************************************************************************************************* */
	
	
	/**
	 * <p><b>
	 * Check if the current user has permission or not for a specific processing. (for classification, content) 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.checkObject(pZappAuth, pInf, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObj Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value (0:Viewing not allowed + Registering not allowed, 2:Viewing allowed + Registering not allowed, 3:Viewing allowed + Registering allowed) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value (0:No access, 1:List, 2:View, 3:Print, 4:Download, 5:Edit) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>Boolean</td><td>Whether having a right or not</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult checkObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjRes.setResObj(false);
		
		if(pObjAuth != null && pObj != null) {
			
			/* [Content]
			 * 
			 */
			if(pObj instanceof ZappContentAcl) {
				
				ZappContentAcl pZappContentAcl = (ZappContentAcl) pObj;
				pZappContentAcl.setAclobjid(pObjAuth.cvrtAclToString());
				pObjRes = aclService.rMultiContetentRows(pObjAuth, pZappContentAcl, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					pObjRes.setResObj(false);
					return pObjRes;
				}
				List<ZappContentAcl> rZappContentAclList = (List<ZappContentAcl>) pObjRes.getResObj();
				if(rZappContentAclList != null) {
					if(rZappContentAclList.size() == ZERO) {
						pObjRes.setResObj(false);
						return pObjRes;
					}
					if(getOptimalAcl(pObjAuth, rZappContentAclList) >= pZappContentAcl.getAcls()) {
						pObjRes.setResObj(true);
						return pObjRes;
					} else {
						pObjRes.setResObj(false);
						return pObjRes;
					}
				} else {
					pObjRes.setResObj(false);
					return pObjRes;
				}
			
			}
			
			/* [Classification]
			 * 
			 */
			if(pObj instanceof ZappClassAcl) {
				
				ZappClassAcl pZappClassAcl = (ZappClassAcl) pObj;
				
				/* Checking Holder - In the case of holders, they have all rights. */
				ZappClassification rZappClassification = (ZappClassification) classService.rSingleRow(pObjAuth, new ZappClassification(pZappClassAcl.getClassid()));
				if(rZappClassification != null) {
					if(ZstFwValidatorUtils.isIdentical(rZappClassification.getHolderid(), pObjAuth.getSessDeptUser().getDeptuserid()) == true) {
						pObjRes.setResObj(true);
						return pObjRes;
					}
				}

				pZappClassAcl.setAclobjid(pObjAuth.cvrtAclToString());					
				pObjRes = aclService.rMultiRows(pObjAuth, pZappClassAcl, pObjRes);		
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					pObjRes.setResObj(false);
					return pObjRes;
				}
				List<ZappClassAcl> rZappClassAclList = (List<ZappClassAcl>) pObjRes.getResObj();
				if(rZappClassAclList != null) {
					if(rZappClassAclList.size() == ZERO) {
						pObjRes.setResObj(false);
						return pObjRes;
					}
					if(getOptimalAcl(pObjAuth, rZappClassAclList) >= pZappClassAcl.getAcls()) {
						pObjRes.setResObj(true);
						return pObjRes;
					} else {  
						pObjRes.setResObj(false);
						return pObjRes;
					}
				} else {
					pObjRes.setResObj(false);
					return pObjRes;
				}
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[checkObject] " + messageService.getMessage("ERR_MIS_INVAL",  BLANK), BLANK);
		}
		
		logger.info("pObjRes.getResObj() = " + pObjRes.getResObj());
		
		return pObjRes;
	}
	
	/**
	 * <p><b>
	 * After inquiring access control values corresponding to content or classification list, it is stored in the list.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  List&lt;ZappClassObjectRes&gt; pIn = new ArrayList&lt;ZappClassObjectRes&gt;();
	 *  or
	 *  List&lt;ZappContentRes&gt; pIn = new ArrayList&lt;ZappContentRes&gt;();
	 *  
	 *  // process
	 * 	result = service.optimizeObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObj List<Object> (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>List&lt;ZappClassObjectRes&gt;</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>List&lt;ZappContentRes&gt;</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>String</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>List&lt;Object&gt;</td><td>List</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassObjectRes
	 * @see ZappContentRes
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult optimizeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjAuth != null && pObj != null) {
			
			if(pObj instanceof List) {
				List<Object> objList = (List<Object>) pObj;
				boolean[] checkobj = {false, false, false};
				for(Object obj : objList) {
					if(obj instanceof ZappContentRes) {			// Content
						checkobj[0] = true; break;
					}
					if(obj instanceof ZappClassObjectRes) {		// Classification
						checkobj[1] = true; break;
					}
					if(obj instanceof ZappClassification) {		// Classification
						checkobj[2] = true; break;
					}
				}
				
				StringBuffer qsb = new StringBuffer();
				Map<String, Object> mapAcls = new HashMap<String, Object>();
				if(checkobj[0] == true) {
					List<ZappContentRes> objContentList = (List<ZappContentRes>) pObj;
					for(ZappContentRes vo : objContentList) {
						if(isHolder(pObjAuth, vo.getHolderid()) == false) {
							qsb.append(vo.getContentid() + ZappConts.SCHARS.DIVIDER.character);
						}
					}
					ZappContentAcl pZappContentAcl = new ZappContentAcl(); 
					pZappContentAcl.setContentid(qsb.toString());
					
					if(ZstFwValidatorUtils.valid(pZappContentAcl.getContentid())) {
						pObjRes = aclService.rMultiContetentRows(pObjAuth, pZappContentAcl, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							pObjRes.setResObj(pObj);
							return pObjRes;
						}
						List<ZappContentAcl> objContentAclList = (List<ZappContentAcl>) pObjRes.getResObj();
						if(objContentAclList != null) {
							for(ZappContentAcl vo : objContentAclList) {
								List<ZappContentAcl> tmpList = null;
								if(mapAcls.containsKey(vo.getContentid()) == true) {
									tmpList = (List<ZappContentAcl>) mapAcls.get(vo.getContentid());
									
								} else {
									tmpList = new ArrayList<ZappContentAcl>();
								}
								tmpList.add(vo);
								mapAcls.put(vo.getContentid(), tmpList);
							}
						}
					}
					
					/* Optimize access control values */
					for(int IDX = ZERO; IDX < objContentList.size(); IDX++) {
						if(isHolder(pObjAuth, objContentList.get(IDX).getHolderid()) == true) {		// For holder (default : edit)
							objContentList.get(IDX).setZappAcl(new ZappAclExtend(ZappConts.ACLS.CONTENT_CHANGE.acl));
						} else {																	// For not holder
							if(pObjAuth.getObjSkipAcl() == true) {
								objContentList.get(IDX).setZappAcl(new ZappAclExtend(ZappConts.ACLS.CONTENT_CHANGE.acl));
							} else {
								objContentList.get(IDX).setZappAcl(new ZappAclExtend(getOptimalAcl(pObjAuth, mapAcls.get(objContentList.get(IDX).getContentid()))));
							}
						}
					}
					pObjRes.setResObj(objContentList);
					return pObjRes; 
				}
				
				if(checkobj[1] == true) {
					StringBuffer aclsb = new StringBuffer();
					List<ZappClassObjectRes> objClassList = (List<ZappClassObjectRes>) pObj;
					for(ZappClassObjectRes vo : objClassList) {
						qsb.append(vo.getClassid() + ZappConts.SCHARS.DIVIDER.character);
					}
					for(String vo : pObjAuth.getSessAclObjList()) {
						aclsb.append(vo + ZappConts.SCHARS.DIVIDER.character);
					}
					ZappClassAcl pZappClassAcl = new ZappClassAcl();
					pZappClassAcl.setClassid(qsb.toString());
					pZappClassAcl.setAclobjid(aclsb.toString());
					pObjRes = aclService.rMultiRows(pObjAuth, pZappClassAcl, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						pObjRes.setResObj(pObj);
						return pObjRes;
					}		
					List<ZappClassAcl> objClassAclList = (List<ZappClassAcl>) pObjRes.getResObj();
					if(objClassAclList != null) {
						for(ZappClassAcl vo : objClassAclList) {
							List<ZappClassAcl> tmpList = null;
							if(mapAcls.containsKey(vo.getClassid()) == true) {
								tmpList = (List<ZappClassAcl>) mapAcls.get(vo.getClassid());
								
							} else {
								tmpList = new ArrayList<ZappClassAcl>();
							}
							tmpList.add(vo);
							mapAcls.put(vo.getClassid(), tmpList);
						}
					}
					
					/* Optimize access control values */
					for(int IDX = ZERO; IDX < objClassList.size(); IDX++) {
						objClassList.get(IDX).setZappClassAcl(new ZappClassAcl(getOptimalAcl(pObjAuth, mapAcls.get(objClassList.get(IDX).getClassid()))));
					}
					pObjRes.setResObj(objClassList);
					return pObjRes; 
				}
				
				if(checkobj[2] == true) {
					StringBuffer aclsb = new StringBuffer();
					List<ZappClassification> objClassList = (List<ZappClassification>) pObj;
					for(ZappClassification vo : objClassList) {
						qsb.append(vo.getClassid() + ZappConts.SCHARS.DIVIDER.character);
					}
//					for(String vo : pObjAuth.getSessAclObjList()) {
//						aclsb.append(vo + ZappConts.SCHARS.DIVIDER.character);
//					}
					aclsb.append(pObjAuth.getSessDeptUser().getDeptuserid() + ZappConts.SCHARS.DIVIDER.character);
					aclsb.append(pObjAuth.getSessDeptUser().getDeptid() + ZappConts.SCHARS.DIVIDER.character);
					for(ZappGroupUserExtend vo : pObjAuth.getSessGroupUsers()) {
						aclsb.append(vo.getGroupid() + ZappConts.SCHARS.DIVIDER.character);
					}
					
					ZappClassAcl pZappClassAcl = new ZappClassAcl();
					pZappClassAcl.setClassid(qsb.toString());
					pZappClassAcl.setAclobjid(aclsb.toString());
					pObjRes = aclService.rMultiRows(pObjAuth, pZappClassAcl, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						pObjRes.setResObj(pObj);
						return pObjRes;
					}		
					List<ZappClassAcl> objClassAclList = (List<ZappClassAcl>) pObjRes.getResObj();
					if(objClassAclList != null) {
						for(ZappClassAcl vo : objClassAclList) {
							List<ZappClassAcl> tmpList = null;
							if(mapAcls.containsKey(vo.getClassid()) == true) {
								tmpList = (List<ZappClassAcl>) mapAcls.get(vo.getClassid());
								
							} else {
								tmpList = new ArrayList<ZappClassAcl>();
							}
							tmpList.add(vo);
							mapAcls.put(vo.getClassid(), tmpList);
						}
					}
					
					/* Optimize access control values */
					List<ZappClassificationRes> objResClassList = new ArrayList<ZappClassificationRes>();
					for(int IDX = ZERO; IDX < objClassList.size(); IDX++) {
						ZappClassificationRes pZappClassificationRes = new ZappClassificationRes();
						pZappClassificationRes.setZappClassification(objClassList.get(IDX));
						if(ZstFwValidatorUtils.isIdentical(objClassList.get(IDX).getHolderid(), pObjAuth.getSessDeptUser().getDeptuserid())) {
							pZappClassificationRes.setZappClassAcl(new ZappClassAcl(ZappConts.ACLS.CLASS_READ_ADD.acl));
						} else {
							pZappClassificationRes.setZappClassAcl(new ZappClassAcl(getOptimalAcl(pObjAuth, mapAcls.get(objClassList.get(IDX).getClassid()))));
						}
						objResClassList.add(pZappClassificationRes);
					}
					pObjRes.setResObj(objResClassList);
					return pObjRes; 
				}

			}
			else  {

				/* [Content]
				 * 
				 */
				if(pObj instanceof ZappContentAcl) {
					
					ZappContentAcl pZappContentAcl = (ZappContentAcl) pObj;
					pZappContentAcl.setAclobjid(pObjAuth.cvrtAclToString());
					pObjRes = aclService.rMultiRows(pObjAuth, pZappContentAcl, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						pObjRes.setResObj(ZERO);
						return pObjRes;
					}
					List<ZappContentAcl> rZappContentAclList = (List<ZappContentAcl>) pObjRes.getResObj();
					if(rZappContentAclList != null) {
						pObjRes.setResObj(getOptimalAcl(pObjAuth, rZappContentAclList));
						return pObjRes;
					} else {
						pObjRes.setResObj(ZERO);
						return pObjRes;
					}
				
				}
				
				/* [Classification]
				 * 
				 */
				if(pObj instanceof ZappClassAcl) {
					
					ZappClassAcl pZappClassAcl = (ZappClassAcl) pObj;
					pZappClassAcl.setAclobjid(pObjAuth.cvrtAclToString());
					pObjRes = aclService.rMultiRows(pObjAuth, pZappClassAcl, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						pObjRes.setResObj(false);
						return pObjRes;
					}
					List<ZappClassAcl> rZappClassAclList = (List<ZappClassAcl>) pObjRes.getResObj();
					if(rZappClassAclList != null) {
						pObjRes.setResObj(getOptimalAcl(pObjAuth, rZappClassAclList));
						return pObjRes;
					} else {
						pObjRes.setResObj(ZERO);
						return pObjRes;
					}
				}
				
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[checkObject] " + messageService.getMessage("ERR_MIS_INVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	@SuppressWarnings("unchecked")
	public ZstFwResult manualObject(ZappAuth pObjAuth, Object pObj, int pAcl, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjAuth != null && pObj != null) {
			
			if(pObj instanceof List) {
				List<Object> objList = (List<Object>) pObj;
				boolean[] checkobj = {false, false, false};
				for(Object obj : objList) {
					if(obj instanceof ZappContentRes) {			// Content
						checkobj[0] = true; break;
					}
					if(obj instanceof ZappClassObjectRes) {		// Classification
						checkobj[1] = true; break;
					}
				}
				
				if(checkobj[0] == true) {
					List<ZappContentRes> objContentList = (List<ZappContentRes>) pObj;
					for(int IDX = ZERO; IDX < objContentList.size(); IDX++) {
//						objContentList.get(IDX).setZappAcl(new ZappAclExtend(ZappConts.ACLS.CONTENT_READ.acl));
						objContentList.get(IDX).setZappAcl(new ZappAclExtend(pAcl));
					}
					pObjRes.setResObj(objContentList);
					return pObjRes; 
				}
				
				if(checkobj[1] == true) {
					List<ZappClassObjectRes> objClassList = (List<ZappClassObjectRes>) pObj;
					for(int IDX = ZERO; IDX < objClassList.size(); IDX++) {
//						objClassList.get(IDX).setZappClassAcl(new ZappClassAcl(ZappConts.ACLS.CLASS_READONLY.acl));
						objClassList.get(IDX).setZappClassAcl(new ZappClassAcl(pAcl));
					}
					pObjRes.setResObj(objClassList);
					return pObjRes; 
				}
				
			}
			else  {

				/* [Content]
				 * 
				 */
				if(pObj instanceof ZappContentAcl) {
//					pObjRes.setResObj(ZappConts.ACLS.CONTENT_READ.acl);
					pObjRes.setResObj(pAcl);
					return pObjRes;
				}
				
				/* [Classification]
				 * 
				 */
				if(pObj instanceof ZappClassAcl) {
//					pObjRes.setResObj(ZappConts.ACLS.CONTENT_READ.acl);
					pObjRes.setResObj(pAcl);
					return pObjRes;				
				}
				
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[optimizeViewObject] " + messageService.getMessage("ERR_MIS_INVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}	
	
	
	/**
	 * <p><b>
	 * Inquire access control information including additional one. (for classification, content) - based on dynamic filters
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappClassAcl pInf = new ZappClassAcl();
	 *  ZappClassAcl pIn = new ZappClassAcl();
	 *  or
	 *  ZappContentAcl pInf = new ZappContentAcl();
	 *  ZappContentAcl pIn = new ZappContentAcl();
	 *  
	 *  // process
	 * 	result = service.selectExtendObject(pZappAuth, pInf, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjf Object for Filter (Nullable) - If the value is null, the default filter is applied <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @param pObjw Object for Where (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01: Bundle, 02:File) </td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @return ZstFwResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resCode</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>resMessage</td><td>String</td><td>Result message</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#E2EAF1">
	 * 				<td>resObj</td><td>List&lt;Object&gt;</td><td>List</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public ZstFwResult selectExtendObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
	
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectExtendObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification access control */
		if(pObjw instanceof ZappClassAcl) {}
		
		/* Content  access control */
		if(pObjw instanceof ZappContentAcl) {}
		
		pObjRes = aclService.rMultiExtendRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;

	}
	
	/* ********************************************************************************************* */
	
	
	/**
	 * <p><b>
	 * Check if the current user is a superuser or not.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // process
	 * 	boolean result = service.isSuperManager(pZappAuth);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @return Boolean <br>
	 * @see ZappAuth
	 */
	public boolean isSuperManager(ZappAuth pObjAuth) {
		
		boolean is = false;
		
		if(pObjAuth != null) {
			if(pObjAuth.getSessDeptUser() != null) {
				if(pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_SUPERVISOR.type)) {
					is = true;
				}
			}
		}
		
		return is;
	}	

	
	/**
	 * <p><b>
	 * Check if the current user is a company manager or not. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // process
	 * 	boolean result = service.isCompanyManager(pZappAuth);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @return Boolean <br>
	 * @see ZappAuth
	 */
	public boolean isCompanyManager(ZappAuth pObjAuth) {
		
		boolean is = false;
		
		if(pObjAuth != null) {
			if(pObjAuth.getSessDeptUser() != null) {
				if(pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_COMPANY.type)) {
					is = true;
				}
			}
		}
		
		return is;
	}
	
	
	/**
	 * <p><b>
	 * Check if the current user is a dept. manager or not. 
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // process
	 * 	boolean result = service.isDeptManager(pZappAuth);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @return Boolean <br>
	 * @see ZappAuth
	 */
	public boolean isDeptManager(ZappAuth pObjAuth, String pDeptid) {
		
		boolean is = false;
		
		if(pObjAuth != null) {
			if(pObjAuth.getSessDeptUser() != null) {
				if(pObjAuth.getSessDeptUser().getDeptid().equals(pDeptid)
					&& pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_DEPT.type)) {
					is = true;
				}
			}
		}
		
		return is;
	}
	

	/**
	 * <p><b>
	 * Check if the current user is a member of an access-free group or not.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // process
	 * 	boolean result = service.isAccessFree(pZappAuth);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @return Boolean <br>
	 * @see ZappAuth
	 */
	public boolean isAccessFree(ZappAuth pObjAuth) {
		
		boolean is = false;
		
		if(pObjAuth != null) {
			if(pObjAuth.getSessDeptUser() != null) {
				if(pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_COMPANY.type)) {
					is = true;
				}
			}
		}
		
		return is;
	}
	

	/**
	 * <p><b>
	 * Check if the current user is a member of an access-free group or not.
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  
	 *  // process
	 * 	boolean result = service.isAccessFree(pZappAuth);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @return Boolean <br>
	 * @throws SQLException 
	 * @throws ZappException 
	 * @see ZappAuth
	 */
	public boolean isAccessFreeContent(ZappAuth pObjAuth) throws ZappException, SQLException {
		
		boolean is = false;
		
		if(pObjAuth != null) {
			if(pObjAuth.getSessDeptUser() != null) {
				
				ZstFwResult pObjRes = new ZstFwResult(); pObjRes.setResCode(SUCCESS);
				
				// Inquiry access free group member
				ZappGroup pZappGroup = new ZappGroup(); pZappGroup.setIsactive(YES);
				pObjRes = organService.rMultiRowsByUser(pObjAuth, null, pZappGroup, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == true) {
					@SuppressWarnings("unchecked")
					List<ZappGroup> rZappGroupList = (List<ZappGroup>) pObjRes.getResObj();
					if(rZappGroupList != null) {
						for(ZappGroup vo : rZappGroupList) {
							if(vo.getTypes().equals(ZappConts.TYPES.GROUPTYPE_FREEACCESS.type)) {
								is = true;
								break;
							}
						}
					}
				}
			}
		}
		
		return is;
	}

	/* ********************************************************************************************* */
	
	@SuppressWarnings("unchecked")
	private int getOptimalAcl(ZappAuth pObjAuth, Object pObj) {
		
		int OPTACL = ZERO;
		int OPTACL_USER = ZappConts.ACLS.CONTENT_INACCESSIBLE.acl, OPTACL_DEPT = ZappConts.ACLS.CONTENT_INACCESSIBLE.acl, OPTACL_GROUP = ZappConts.ACLS.CONTENT_INACCESSIBLE.acl;
		int[] OPTACL_USERS = {ZappConts.ACLS.CONTENT_INACCESSIBLE.acl, ZappConts.ACLS.CONTENT_INACCESSIBLE.acl};	// 0:Folder, 1:Content	
		int[] OPTACL_DEPTS = {ZappConts.ACLS.CONTENT_INACCESSIBLE.acl, ZappConts.ACLS.CONTENT_INACCESSIBLE.acl};	// 0:Folder, 1:Content	
		int[] OPTACL_GROUPS = {ZappConts.ACLS.CONTENT_INACCESSIBLE.acl, ZappConts.ACLS.CONTENT_INACCESSIBLE.acl};	// 0:Folder, 1:Content	
		int OPTACL_OTHER = ZERO;
		boolean EXIST_USER = false;
		if(pObj == null) return ZERO;
		if(pObj instanceof List == false) return ZERO; 
		
		List<Object> objList = (List<Object>) pObj;
		boolean[] checkobj = {false, false, false};
		for(Object obj : objList) {
			if(obj instanceof ZappContentAcl) {			// Content
				checkobj[0] = true; break;
			}
			if(obj instanceof ZappClassAcl) {			// Classifiction
				checkobj[1] = true; break;
			}
		}
		
//		if(checkobj[0] == true) {
//			List<ZappContentAcl> objAclList = (List<ZappContentAcl>) pObj;
//			for(ZappContentAcl vo : objAclList) {
//				if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_USER.type)) {
//					// No access
//					if(vo.getAcls() == ZERO) {
//						if(pObjAuth.getSessDeptUser().getDeptuserid().equals(vo.getAclobjid())) {
//							return ZERO;
//						}
//					}
//					OPTACL_USER = Math.min(OPTACL_USER, vo.getAcls());
//					EXIST_USER = true;
//				} else {
//					OPTACL_OTHER = Math.max(OPTACL_OTHER, vo.getAcls());
//				}
//			}
//		}
		
		if(checkobj[0] == true) {
			List<ZappContentAcl> objAclList = (List<ZappContentAcl>) pObj;

			// User
			for(ZappContentAcl vo : objAclList) {
				if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_USER.type)) {
					if(pObjAuth.getSessDeptUser().getDeptuserid().equals(vo.getAclobjid())) {
						if(vo.getAcls() == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
							return ZappConts.ACLS.CONTENT_INACCESSIBLE.acl;
						} else {
							if(vo.getContenttype().equals(ZappConts.TYPES.CONTENT_NODE.type)) {   // Folder
								OPTACL_USERS[ZERO] = Math.max(OPTACL_USERS[ZERO], vo.getAcls());
							} else {															  // Content
								OPTACL_USERS[ONE] = Math.max(OPTACL_USERS[ONE], vo.getAcls());					
							}
							EXIST_USER = true;
						}
					}
				}
			}
			
			if(EXIST_USER == true) {
				if(OPTACL_USERS[ZERO] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_USERS[ONE] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
					OPTACL_USER = OPTACL_USERS[ONE];
				}
				if(OPTACL_USERS[ZERO] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_USERS[ONE] == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
					OPTACL_USER = OPTACL_USERS[ZERO];
				}
				if(OPTACL_USERS[ZERO] == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_USERS[ONE] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
					OPTACL_USER = OPTACL_USERS[ONE];
				}
			}
			
			if(EXIST_USER == false) {
				for(ZappContentAcl vo : objAclList) {
			
					// Department
					if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_DEPT.type)) {
						if(pObjAuth.getSessAclObjList().contains(vo.getAclobjid())) {
							if(vo.getAcls() == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
								return ZappConts.ACLS.CONTENT_INACCESSIBLE.acl;
							} else {
//								OPTACL_OTHER = Math.max(OPTACL_OTHER, vo.getAcls());
								if(vo.getContenttype().equals(ZappConts.TYPES.CONTENT_NODE.type)) {   // Folder
									OPTACL_DEPTS[ZERO] = Math.max(OPTACL_DEPTS[ZERO], vo.getAcls());
								} else {															  // Content
									OPTACL_DEPTS[ONE] = Math.max(OPTACL_DEPTS[ONE], vo.getAcls());					
								}
							}
						}
					}
			
					if(OPTACL_DEPTS[ZERO] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_DEPTS[ONE] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
						OPTACL_DEPT = OPTACL_DEPTS[ONE];
					}
					if(OPTACL_DEPTS[ZERO] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_DEPTS[ONE] == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
						OPTACL_DEPT = OPTACL_DEPTS[ZERO];
					}
					if(OPTACL_DEPTS[ZERO] == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_DEPTS[ONE] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
						OPTACL_DEPT = OPTACL_DEPTS[ONE];
					}

					// Group
					if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_GROUP.type)) {
						if(pObjAuth.getSessAclObjList().contains(vo.getAclobjid())) {
							if(vo.getAcls() == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
								return ZappConts.ACLS.CONTENT_INACCESSIBLE.acl;
							} else {
//								OPTACL_OTHER = Math.max(OPTACL_OTHER, vo.getAcls());
								if(vo.getContenttype().equals(ZappConts.TYPES.CONTENT_NODE.type)) {   // Folder
									OPTACL_GROUPS[ZERO] = Math.max(OPTACL_GROUPS[ZERO], vo.getAcls());
								} else {															  // Content
									OPTACL_GROUPS[ONE] = Math.max(OPTACL_GROUPS[ONE], vo.getAcls());					
								}
							}
						}
					}
			
					if(OPTACL_GROUPS[ZERO] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_GROUPS[ONE] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
						OPTACL_GROUP = OPTACL_GROUPS[ONE];
					}
					if(OPTACL_GROUPS[ZERO] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_GROUPS[ONE] == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
						OPTACL_GROUP = OPTACL_GROUPS[ZERO];
					}
					if(OPTACL_GROUPS[ZERO] == ZappConts.ACLS.CONTENT_INACCESSIBLE.acl && OPTACL_GROUPS[ONE] > ZappConts.ACLS.CONTENT_INACCESSIBLE.acl) {
						OPTACL_GROUP = OPTACL_GROUPS[ONE];
					}
					
					OPTACL_OTHER = Math.max(OPTACL_DEPT, OPTACL_GROUP);

				}
			}
		}		
		
		if(checkobj[1] == true) {
			List<ZappClassAcl> objAclList = (List<ZappClassAcl>) pObj;
//			for(ZappClassAcl vo : objAclList) {
//				if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_USER.type)) {
//					// No access
//					if(vo.getAcls() == ZERO) {
//						if(pObjAuth.getSessDeptUser().getDeptuserid().equals(vo.getAclobjid())) {
//							return ZERO;
//						}
//					}
//					OPTACL_USER = Math.min(OPTACL_USER, vo.getAcls());
//					EXIST_USER = true;
//				} else {
//					OPTACL_OTHER = Math.max(OPTACL_OTHER, vo.getAcls());
//				}
//			}	
			

			// User
			for(ZappClassAcl vo : objAclList) {
				if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_USER.type)) {
					if(pObjAuth.getSessDeptUser().getDeptuserid().equals(vo.getAclobjid())) {
						if(vo.getAcls() == ZappConts.ACLS.CLASS_INACCESSIBLE.acl) {
							return ZappConts.ACLS.CLASS_INACCESSIBLE.acl;
						} else {
							OPTACL_USER = Math.max(OPTACL_USER, vo.getAcls());
							EXIST_USER = true;
						}
					}
				}
			}
			
			if(EXIST_USER == false) {
				for(ZappClassAcl vo : objAclList) {
			
					// Department
					if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_DEPT.type)) {
						if(pObjAuth.getSessAclObjList().contains(vo.getAclobjid())) {
							if(vo.getAcls() == ZappConts.ACLS.CLASS_INACCESSIBLE.acl) {
								return ZappConts.ACLS.CLASS_INACCESSIBLE.acl;
							} else {
								OPTACL_OTHER = Math.max(OPTACL_OTHER, vo.getAcls());
							}
						}
					}
			
					// Group
					if(vo.getAclobjtype().equals(ZappConts.TYPES.OBJTYPE_GROUP.type)) {
						if(pObjAuth.getSessAclObjList().contains(vo.getAclobjid())) {
							if(vo.getAcls() == ZappConts.ACLS.CLASS_INACCESSIBLE.acl) {
								return ZappConts.ACLS.CLASS_INACCESSIBLE.acl;
							} else {
								OPTACL_OTHER = Math.max(OPTACL_OTHER, vo.getAcls());
							}
						}
					}
			
				}
			}
		}
		
		if(EXIST_USER == true) {
			OPTACL = OPTACL_USER;
		} else {
			OPTACL = OPTACL_OTHER;
		}
		
		return OPTACL;
	}
	
	/**
	 * Check if the current user is a holder or not.
	 * @param pObjAuth
	 * @param pHolderid
	 * @return
	 */
	private boolean isHolder(ZappAuth pObjAuth, String pHolderid) {
		
		boolean isholder = false;
		
		for(ZappDeptUserExtend vo : pObjAuth.getSessDeptUsers()) {
			if(vo.getDeptuserid().equals(pHolderid)) {
				isholder = true;
				break;
			}
		}
		
		return isholder;
		
	}

}

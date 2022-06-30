package com.zenithst.core.organ.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.constant.Results;
import com.zenithst.archive.service.ZArchTaskCabinetService;
import com.zenithst.archive.service.ZArchTaskService;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.archive.vo.ZArchTask;
import com.zenithst.archive.vo.ZArchTaskCabinetKey;
import com.zenithst.core.acl.api.ZappAclMgtService;
import com.zenithst.core.authentication.salt.ZappSalt;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.service.ZappClassificationService;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.debug.ZappDebug;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.service.ZappCommonService;
import com.zenithst.core.common.utility.ZappJSONUtils;
import com.zenithst.core.common.vo.ZappCommon;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.core.organ.bind.ZappOrganBinder;
import com.zenithst.core.organ.mapper.ZappUserMapper;
import com.zenithst.core.organ.service.ZappOrganService;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappCompanyExtend;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupPar;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappGroupUserExtend;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.organ.vo.ZappUserExtend;
import com.zenithst.core.system.api.ZappSystemMgtService;
import com.zenithst.core.system.service.ZappSystemService;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;
import com.zenithst.license.api.ZenithLicense;
import com.zenithst.license.constant.Conts;
import com.zenithst.license.vo.License;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage organization info. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>
* 
*    '@Autowired
*	 private ZappOrganMgtService service; <br>
*
*    // Authentication
*    ZappAuth pObjAuth = new ZappAuth();
*    
*    // Department 
*    ZappDept pZappDept = new ZappDept();
*    
*    // Result
*    ZstFwResult pObjRes = new ZstFwResult();
*    
*    // Call
*    pObjRes =  addObject(pObjAuth, pZappDept, pObjRes);
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappOrganMgtService")
public class ZappOrganMgtServiceImpl extends ZappService implements ZappOrganMgtService {

	/*
	* [Service]
	*/

	/* Organization */
	@Autowired
	private ZappOrganService organService;
	@Autowired
	private ZappUserMapper userMapper;				// User
	
	/* System */
	@Autowired
	private ZappSystemMgtService systemService;
	@Autowired
	private ZappSystemService systemService_;

	/* Access control */
	@Autowired
	private ZappAclMgtService aclService;
	
	/* Log */
	@Autowired
	private ZappLogMgtService logService;	

	/* Class */
	@Autowired
	private ZappClassificationService classService_;	

	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Common */
	@Autowired
	private ZappCommonService commonService;

	/* Tesk (Archive) */
	@Autowired
	private ZArchTaskService taskService;
	
	/* Cabinet (Archive) */
	@Autowired
	private ZArchTaskCabinetService taskcabinetService;
	
	
	/*
	* [Binder]
	*/

	/* Organization */
	@Autowired
	private ZappOrganBinder utilBinder;
	
	/* License path */
	@Value("#{archiveconfig['LIC_PATH']}")
	private String LIC_PATH;	
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappOrganMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappOrganMgtService] Service Destroy ");
	}		

	/* ******************************************************************************************************************** */
	
	/**
	 * <p><b>
	 * Register new organization information. (Company, Department, User, Group, Company task)
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappCompany pIn = new ZappCompany();
	 *  or
	 *  ZappDept pIn = new ZappDept();
	 *  or
	 *  ZappDeptUser pIn = new ZappDeptUser();
	 *  or
	 *  ZappGroup pIn = new ZappGroup();
	 *  or
	 *  ZappGroupUser pIn = new ZappGroupUser();
	 *  or
	 *  ZappUser pIn = new ZappUser();
	 *  or
	 *  ZappOrganTask pIn = new ZappOrganTask();
	 *  
	 *  // process
	 * 	result = service.addObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObj Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappCompany</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Code</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Company Abbreviation</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>tel</td><td>String</td><td>Tel. No</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>address</td><td>String</td><td>Address</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDept</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID (Auto-input in session)</td><td style="color: white; background:cyan;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Code</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Department Abbreviation</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDeptUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>deptid</td><td>String</td><td>Dept. ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>userid</td><td>String</td><td>User ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>usertype</td><td>String</td><td>User type (01:General, 02:Dept. manager, 03:Company manager, 04:System manager)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>originyn</td><td>String</td><td>Original job? (Y:Original, N:Not original)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>positionid</td><td>String</td><td>Position ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dutyid</td><td>String</td><td>Duty ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>seclevelid</td><td>String</td><td>Security level</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>issupervisor</td><td>String</td><td>Upper manager? (Y/N)</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroup</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID (Auto-input in session)</td><td style="color: white; background:cyan;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Group Name</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Group Code</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Group Type (01:Company, 02:Department, 03:User, 04:Cooperation, 99:No access limit )</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroupUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>groupid</td><td>String</td><td>Group ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>gobjid</td><td>String</td><td>Target ID (User/Department)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>gobjtype</td><td>String</td><td>Target type (01:User, 02:Department)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID (Auto-input in session)</td><td style="color: white; background:cyan;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>empno</td><td>String</td><td>Employee number</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loginid</td><td>String</td><td>Login ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>User name</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>passwd</td><td>String</td><td>Password</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>email</td><td>String</td><td>E-mail</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappOrganTask</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>deptid</td><td>String</td><td>Dept. ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>taskid</td><td>String</td><td>Task ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>tobjtype</td><td>String</td><td>Target type (01:Company, 02:Department)</td><td style="color: white; background:red;" >Required</td>
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
	 * @see ZappCompany
	 * @see ZappDept
	 * @see ZappDeptUser
	 * @see ZappGroup
	 * @see ZappGroupUser
	 * @see ZappUser
	 * @see ZappOrganTask
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Multiple registration] 
		 * 
		 */
		if(pObj instanceof List) {
			pObjRes = organService.cMultiRows(pObjAuth, pObj, pObjRes);
		} 
		else {
			
			/* Validation */
			pObjRes = utilBinder.isEmpty(pObjAuth, pObj, pObjRes);
			
			/* Company */
			if(pObj instanceof ZappCompany) {}
			
			/* Department */
			if(pObj instanceof ZappDept) {}
			
			/* Department User */
			if(pObj instanceof ZappDeptUser) {}
			
			/* Group */
			if(pObj instanceof ZappGroup) {}
			
			/* Group User */
			if(pObj instanceof ZappGroupUser) {}
			
			/* User */
			if(pObj instanceof ZappUser) {}
			
			/* Company Task */
			if(pObj instanceof ZappOrganTask) {}
			
			pObjRes = organService.cSingleRow(pObjAuth, pObj, pObjRes);
		}
				
//		pObjRes.setResObj(BLANK);
		
		return pObjRes;

	}
	
	
	/**
	 * <p><b>
	 * Edit organization information. (Company, Department, User, Group, Company task) - Based on PK
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappCompany pIn = new ZappCompany();
	 *  or
	 *  ZappDept pIn = new ZappDept();
	 *  or
	 *  ZappDeptUser pIn = new ZappDeptUser();
	 *  or
	 *  ZappGroup pIn = new ZappGroup();
	 *  or
	 *  ZappGroupUser pIn = new ZappGroupUser();
	 *  or
	 *  ZappUser pIn = new ZappUser();
	 *  or
	 *  ZappOrganTask pIn = new ZappOrganTask();
	 *  
	 *  // process
	 * 	result = service.changeObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObj Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappCompany</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#CED1D4">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td>●</td><td>●</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Company Abbreviation</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>tel</td><td>String</td><td>Tel. No</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>address</td><td>String</td><td>Address</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDept</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#CED1D4">
	 * 				<td>deptid</td><td>String</td><td>Department ID</td><td>●</td><td>●</td>
	 * 			</tr>		
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Department Abbreviation</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDeptUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#CED1D4">
	 * 				<td>deptuserid</td><td>String</td><td>Dept. user ID</td><td>●</td><td>●</td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>usertype</td><td>String</td><td>User type (01:General, 02:Dept. manager, 03:Company manager, 04:System manager)</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>originyn</td><td>String</td><td>Original job? (Y:Original, N:Not original)</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>positionid</td><td>String</td><td>Position ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dutyid</td><td>String</td><td>Duty ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>seclevelid</td><td>String</td><td>Security level</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>issupervisor</td><td>String</td><td>Upper manager? (Y/N)</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroup</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#CED1D4">
	 * 				<td>groupid</td><td>String</td><td>Group ID</td><td>●</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Group Name</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroupUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>	
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#CED1D4">
	 * 				<td>userid</td><td>String</td><td>사용자아이디</td><td>●</td><td>●</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loginid</td><td>String</td><td>Login ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>User name</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>passwd</td><td>String</td><td>Password</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappOrganTask</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
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
	 * 				<td>resObj</td><td>String</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappCompany
	 * @see ZappDept
	 * @see ZappDeptUser
	 * @see ZappGroup
	 * @see ZappGroupUser
	 * @see ZappUser
	 * @see ZappOrganTask
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* Validation */
		if(utilBinder.isEmptyPk(pObj) == true || utilBinder.isEmpty(pObj) == true) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObj instanceof ZappCompany) {}
		
		/* Department */
		if(pObj instanceof ZappDept) {}
		
		/* Department User */
		if(pObj instanceof ZappDeptUser) {}

		/* Group */
		if(pObj instanceof ZappGroup) {}
		
		/* Group User */
		if(pObj instanceof ZappGroupUser) {}
		
		/* User */
		if(pObj instanceof ZappUser) {}
		
		/* Company Task */
		if(pObj instanceof ZappOrganTask) {}
		
		pObjRes = organService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}

	
	/**
	 * <p><b>
	 * Edit organization information. (Company, Department, User, Group, Company task) - based on default filter
	 * </b></p>
	 * 
	 * <pre>
	 *  
	 *  ZappAuth pZappAuth = new ZappAuth();
	 *  ZstFwResult result = new ZstFwResult();
	 *  
	 *  // vo
	 *  ZappCompany pIn = new ZappCompany();
	 *  or
	 *  ZappDept pIn = new ZappDept();
	 *  or
	 *  ZappDeptUser pIn = new ZappDeptUser();
	 *  or
	 *  ZappGroup pIn = new ZappGroup();
	 *  or
	 *  ZappGroupUser pIn = new ZappGroupUser();
	 *  or
	 *  ZappUser pIn = new ZappUser();
	 *  or
	 *  ZappOrganTask pIn = new ZappOrganTask();
	 *  
	 *  // process
	 * 	result = service.changeObject(pZappAuth, pIn, result);
	 * 
	 * </pre>
	 * 
	 * @param pObjAuth ZappAuth Object (Not Nullable) <br>
	 * @param pObjs Set Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappCompany</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Company Abbreviation</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>tel</td><td>String</td><td>Tel. No</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>address</td><td>String</td><td>Address</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDept</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Department Abbreviation</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDeptUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>usertype</td><td>String</td><td>User type (01:General, 02:Dept. manager, 03:Company manager, 04:System manager)</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>originyn</td><td>String</td><td>Original job? (Y:Original, N:Not original)</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>positionid</td><td>String</td><td>Position ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dutyid</td><td>String</td><td>Duty ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>seclevelid</td><td>String</td><td>Security level</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>issupervisor</td><td>String</td><td>Upper manager? (Y/N)</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroup</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Group Name</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroupUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td colspan="5">N/A</td>
	 * 			</tr>	
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loginid</td><td>String</td><td>Login ID</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>User name</td><td></td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>passwd</td><td>String</td><td>Password</td><td></td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappOrganTask</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td><td><b>Uneditable</b></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td colspan="5">N/A</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param pObjw Where Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappCompany</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Code</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Company Abbreviation</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>tel</td><td>String</td><td>Tel. No</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>address</td><td>String</td><td>Address</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDept</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID (Auto-input in session)</td><td style="color: white; background:cyan;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Name</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Code</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>abbrname</td><td>String</td><td>Department Abbreviation</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappDeptUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>deptid</td><td>String</td><td>Dept. ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>userid</td><td>String</td><td>User ID</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>usertype</td><td>String</td><td>User type (01:General, 02:Dept. manager, 03:Company manager, 04:System manager)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>originyn</td><td>String</td><td>Original job? (Y:Original, N:Not original)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>positionid</td><td>String</td><td>Position ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>dutyid</td><td>String</td><td>Duty ID</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>seclevelid</td><td>String</td><td>Security level</td><td></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>issupervisor</td><td>String</td><td>Upper manager? (Y/N)</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroup</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID (Auto-input in session)</td><td style="color: white; background:cyan;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>Group Name</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>code</td><td>String</td><td>Group Code</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>types</td><td>String</td><td>Group Type (01:Company, 02:Department, 03:User, 04:Cooperation, 99:No access limit )</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>upid</td><td>String</td><td>Upper ID</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappGroupUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td colspan="4">N/A</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappUser</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Default filter</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>userid</td><td>String</td><td>User ID</td><td>EQUAL</td><td rowspan="8"></td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>companyid</td><td>String</td><td>Company ID</td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>empno</td><td>String</td><td>Employee number</td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>loginid</td><td>String</td><td>Login ID</td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>name</td><td>String</td><td>User name</td><td>LIKE</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>passwd</td><td>String</td><td>Password</td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>email</td><td>String</td><td>E-mail</td><td>EQUAL</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>isactive</td><td>String</td><td>Use or not</td><td>IN</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappOrganTask</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td colspan="4">N/A</td>
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
	 * 				<td>resObj</td><td>String</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @throws ZappException
	 * @throws SQLException
	 * @see ZappAuth
	 * @see ZappCompany
	 * @see ZappDept
	 * @see ZappDeptUser
	 * @see ZappGroup
	 * @see ZappGroupUser
	 * @see ZappUser
	 * @see ZappOrganTask
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* Validation */
		if(utilBinder.isEmpty(pObjs) == true || (utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true)) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}	
	
	
	/**
	 * [Merge]
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult mergeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObj) || utilBinder.isEmpty(pObj)) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[mergeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObj instanceof ZappCompany) {}
		
		/* Department */
		if(pObj instanceof ZappDept) {}
		
		/* Department User */
		if(pObj instanceof ZappDeptUser) {}

		/* Group */
		if(pObj instanceof ZappGroup) {}
		
		/* Group User */
		if(pObj instanceof ZappGroupUser) {}
		
		/* User */
		if(pObj instanceof ZappUser) {}
		
		/* Company Task */
		if(pObj instanceof ZappOrganTask) {}
		
		pObjRes = organService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}		
	
	/**
	 * [Delete]
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* Validation */
		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		if(utilBinder.isEmptyPk(pObjw) == false && utilBinder.isEmpty(pObjw) == true) {
			pObjRes = organService.dSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = organService.dMultiRows(pObjAuth, pObjw, pObjRes);
		}
		
		return pObjRes;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* Validation */
		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
		
	}	

	/**
	 *  [Inquire]
	 */
	
	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		if(utilBinder.isEmptyPk(pObjw) == false && utilBinder.isEmpty(pObjw) == true) {
			pObjRes = organService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = organService.rMultiRows(pObjAuth, pObjw, pObjRes);
		}
		
		return pObjRes;
	}
	
	public ZstFwResult selectObjectExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
		if(pObjw instanceof ZappDeptUserExtend == false) {
			if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
				return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObjectExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		if(utilBinder.isEmptyPk(pObjw) == false && utilBinder.isEmpty(pObjw) == true) {
			pObjRes = organService.rSingleRowExtend(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = organService.rMultiRowsExtend(pObjAuth, pObjw, pObjRes);
		}
		
		return pObjRes;
	}	

	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}


	public ZstFwResult selectObjectExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
		if(pObjw instanceof ZappDeptUserExtend == false) {
			if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
				return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObjectExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}
		
		/* Department User (통합) */
		if(pObjw instanceof ZappDeptUserExtend) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rMultiRowsExtend(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult selectAclObjectExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw1, Object pObjw2, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
		if(utilBinder.isEmptyPk(pObjw1) == true && utilBinder.isEmpty(pObjw1) == true) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectAclObjectExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(utilBinder.isEmptyPk(pObjw2) == true && utilBinder.isEmpty(pObjw2) == true) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectAclObjectExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Company */
		if(pObjw1 instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw1 instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw1 instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw1 instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw1 instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw1 instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw1 instanceof ZappOrganTask) {}
		
		pObjRes = organService.rMultiRowsAclExtend(pObjAuth, pObjf, pObjw1, pObjw2, pObjRes);
		
		return pObjRes;
	}	
	
	/**
	 * [Exist or not]
	 */
	
	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
//		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[existObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
//		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[existObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}

	/**
	 * [Counting]
	 */
	
	
	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
//		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[countObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Validation */
//		if(utilBinder.isEmptyPk(pObjw) == true && utilBinder.isEmpty(pObjw) == true) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[countObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}	
	
	/* ******************************************************************************************************************** */
	
	
	/**
	 * <p><b>
	 * 하위 계층 정보를 조회한다.
	 * </b></p>
	 * 
	 * <pre>
	 *    
	 *    // User 정보
	 *    ZappSession pInSession = new ZappSession();
	 *    pInSession.setObjUserid("사용자아이디");
	 *    
	 *    // 문서 권한 정보
	 *    List<ZappDocAclExtend> pZappDocAclList = new ArrayList<ZappDocAclExtend>();
	 *    ZappDocAclExtend pZappDocAclExtend = new ZappDocAclExtend();
	 *    pZappDocAclExtend.setDocid("문서아이디");
	 *    pZappDocAclExtend.setObjectid("권한대상아이디");
	 *    pZappDocAclExtend.setObjecttype("Right target type");
	 *    pZappDocAclExtend.setAcl("1：2：4");
	 *    pZappDocAclList.add(pZappDocAclExtend);
	 *    
	 *    ZappResult result = saveDocAcl(pInSession
	 *					   		       , pZappDocAclList);
	 * 
	 * </pre>
	 * 
	 * @param pInSession ZappSession Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor=darkkhaki>
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor=khaki>
	 * 				<td>objUserid</td><td>String</td><td>접속사용자아이디</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @param pIn List of ZappDocAclExtend (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor=darkkhaki>
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor=khaki>
	 * 				<td>docid</td><td>String</td><td>문서아이디</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor=khaki>
	 * 				<td>objectid</td><td>String</td><td>권한대상아이디</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor=khaki>
	 * 				<td>objecttype</td><td>String</td><td>Right target type - Conts.TYPE 참조<br>(1:User, 2:Department, 3:Group)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 *          <tr bgcolor=khaki>
	 * 				<td>acl</td><td>String</td><td>권한 (ZstFwConst.SCHARS.DIVIDER.character 구분자로 멀티 지정) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return ZappResult <br>
	 * 		  <table width="80%" border="1">
	 *          <caption></caption>
	 * 			<tr bgcolor=darkkhaki>
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor=khaki>
	 * 				<td>code</td><td>String</td><td>Result code</td>
	 * 			</tr>
	 * 			<tr bgcolor=khaki>
	 * 				<td>message</td><td>Object</td><td>Result message</td>
	 * 			</tr>	
	 * 		  </table><br>
	 * @throws  ZappException, SQLException Exception
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	public ZstFwResult selectObjectDown(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}		
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rMultiRowsDown(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult selectObjectUp(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rMultiRowsUp(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	public ZstFwResult selectObjectByUser(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Company */
		if(pObjw instanceof ZappCompany) {}
		
		/* Department */
		if(pObjw instanceof ZappDept) {}
		
		/* Department User */
		if(pObjw instanceof ZappDeptUser) {}

		/* Group */
		if(pObjw instanceof ZappGroup) {}
		
		/* Group User */
		if(pObjw instanceof ZappGroupUser) {}
		
		/* User */
		if(pObjw instanceof ZappUser) {}
		
		/* Company Task */
		if(pObjw instanceof ZappOrganTask) {}
		
		pObjRes = organService.rMultiRowsByUser(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}
	
	
	/**
	 * 이동
	 */
	public ZstFwResult relocateObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// Preferences
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* Department */
		if(pObjw instanceof ZappDept) {
						
			/* [Check access control info.]
			 * 처리자 권한을 체크한다.
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[relocateObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Department 정보 조회
			ZappDept pvo = (ZappDept) pObjw;
			ZappDept rvo = (ZappDept) organService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_R_DEPT", "[relocateObject] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// 대상 부모 Department 정보 조회
			pObjRes = selectObject(pObjAuth, new ZappDept(pvo.getUpid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_DEPT", "[relocateObject] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			ZappDept rvo_up = (ZappDept) pObjRes.getResObj();
			if(rvo_up == null) {
				return ZappFinalizing.finalising("ERR_R_DEPT", "[relocateObject] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}		

			// 대상 Department 정렬 순서 조회
			pObjRes = organService.rNextPriority(pObjAuth, null, new ZappDept(null, pvo.getUpid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_ORDER", "[relocateObject] " + messageService.getMessage("ERR_R_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pvo.setPriority((Integer) pObjRes.getResObj());
			
			// Department 수정
			pObjRes = changeObject(pObjAuth, new ZappDept(pvo.getDeptid(), pvo.getUpid(), pvo.getPriority()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_DEPT", "[relocateObject] " + messageService.getMessage("ERR_E_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getDeptid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_DEPT.log
								 , ZappConts.LOGS.ACTION_RELOCATE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}
		
		/* Group */
		if(pObjw instanceof ZappGroup) {
						
			/* [Check access control info.]
			 * 처리자 권한을 체크한다.
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[relocateObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// 정렬 대상 체크
			ZappGroup pvo = (ZappGroup) pObjw;
			ZappGroup rvo = (ZappGroup) organService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[relocateObject] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getPriority() == pvo.getPriority()) {
				return ZappFinalizing.finalising("ERR_DUP_ORDER", "[relocateObject] " + messageService.getMessage("ERR_DUP_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// 대상 부모 Department 정보 조회
			pObjRes = selectObject(pObjAuth, new ZappGroup(pvo.getUpid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_GROUP", "[relocateObject] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			ZappGroup rvo_up = (ZappGroup) pObjRes.getResObj();
			if(rvo_up == null) {
				return ZappFinalizing.finalising("ERR_R_GROUP", "[relocateObject] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}		

			// 대상 Department 정렬 순서 조회
			pObjRes = organService.rNextPriority(pObjAuth, null, new ZappGroup(null, pvo.getUpid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_ORDER", "[relocateObject] " + messageService.getMessage("ERR_R_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pvo.setPriority((Integer) pObjRes.getResObj());
			
			// Department 수정
			pObjRes = changeObject(pObjAuth, new ZappGroup(pvo.getGroupid(), pvo.getUpid(), pvo.getPriority()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_GROUP", "[relocateObject] " + messageService.getMessage("ERR_E_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getGroupid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_GROUP.log
								 , ZappConts.LOGS.ACTION_RELOCATE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}		
		
		return pObjRes;
		
	}
	
	
	/**
	 * Change the order
	 */
	public ZstFwResult reorderObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// Preferences
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* Department */
		if(pObjw instanceof ZappDept) {
						
			/* [Check access control info.]
			 * 처리자 권한을 체크한다.
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[reorderObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get info.
			ZappDept pvo = (ZappDept) pObjw;
			ZappDept rvo = (ZappDept) organService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_R_DEPT", "[reorderObject] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getPriority() == pvo.getPriority()) {
				return ZappFinalizing.finalising("ERR_DUP_ORDER", "[reorderObject] " + messageService.getMessage("ERR_DUP_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Change order
			ZappDept pvos = new ZappDept();
			pvos.setCompanyid(rvo.getCompanyid());
			pvos.setUpid(rvo.getUpid());
			ZappDept pvoe = new ZappDept();
			if(rvo.getPriority() > pvo.getPriority()) {	// When the order moves up
				pvos.setPriority(pvo.getPriority());
				pvoe.setPriority(rvo.getPriority() - ONE);
				pObjRes = organService.upwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(rvo.getPriority() < pvo.getPriority()) {	// When the order moves down
				pvos.setPriority(rvo.getPriority() + ONE);
				pvoe.setPriority(pvo.getPriority());
				pObjRes = organService.downwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_ORDER", "[reorderObject] " + messageService.getMessage("ERR_E_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update department
			pObjRes = changeObject(pObjAuth, new ZappDept(pvo.getDeptid(), pvo.getPriority()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CLASS", "[reorderObject] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getDeptid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_DEPT.log
								 , ZappConts.LOGS.ACTION_REORDER.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}
		
		/* Group */
		if(pObjw instanceof ZappGroup) {
						
			/* [Check access control info.]
			 * 처리자 권한을 체크한다.
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[reorderObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// 정렬 대상 체크
			ZappGroup pvo = (ZappGroup) pObjw;
			ZappGroup rvo = (ZappGroup) organService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[reorderObject] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getPriority() == pvo.getPriority()) {
				return ZappFinalizing.finalising("ERR_DUP_ORDER", "[reorderObject] " + messageService.getMessage("ERR_DUP_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Change order
			ZappGroup pvos = new ZappGroup();
			pvos.setCompanyid(rvo.getCompanyid());
			pvos.setUpid(rvo.getUpid());
			ZappGroup pvoe = new ZappGroup();
			if(rvo.getPriority() > pvo.getPriority()) {	// When the order moves up
				pvos.setPriority(pvo.getPriority());
				pvoe.setPriority(rvo.getPriority() - ONE);
				pObjRes = organService.upwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(rvo.getPriority() < pvo.getPriority()) {	// When the order moves down
				pvos.setPriority(rvo.getPriority() + ONE);
				pvoe.setPriority(pvo.getPriority() - ONE);
				pObjRes = organService.downwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_ORDER", "[reorderObject] " + messageService.getMessage("ERR_E_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			pObjRes = changeObject(pObjAuth, new ZappGroup(pvo.getGroupid(), pvo.getPriority()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_GROUP", "[reorderObject] " + messageService.getMessage("ERR_E_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getGroupid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_GROUP.log
								 , ZappConts.LOGS.ACTION_REORDER.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}		
		
		return pObjRes;
		
	}
	
	/* **************************************************************************************************************************************** */
	/* Company 																																	*/
	/* **************************************************************************************************************************************** */
	
	/**
	 * Register new company.
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addCompany(ZappAuth pObjAuth, ZappCompanyExtend pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObj);
		
		/* Validation */
		if(pObj.getObjSkipAcl() == false) {
			pObjRes = validParams(pObjAuth, pObj, pObjRes, "addCompany", ZappConts.ACTION.ADD);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
		}		
		/* [Inquire preferences]
		 * 세션에 저장된 시스템 설정 정보를 조회한다.
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = new ZappEnv();
		if(pObj.getObjSkipAcl() == false) {		
			SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log	
		}
		
		/* [Check access control info.]
		 * 처리자 권한을 체크한다.
		 */
		if(pObj.getObjSkipAcl() == false) {
			if(aclService.isSuperManager(pObjAuth) == false) { 
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addCompany] " + messageService.getMessage("ERR_NO_ACL", pObjAuth.getObjlang()), pObjAuth.getObjlang()); 
			}
		}
		
		/* [Company 생성]
		 * 
		 */
		ZappCompany pZappCompany = new ZappCompany();
		BeanUtils.copyProperties(pObj, pZappCompany);
		pObjRes = addObject(pObjAuth, pZappCompany, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_COMPANY", "[addCompany] " + messageService.getMessage("ERR_C_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		String COMPANYID = (String) pObjRes.getResObj();
		logger.info("신규 Company ID : " + COMPANYID);
		
		/* [기본 데이타 구축]
		 * 
		 */
		pObjRes = systemService.initObject(pObjAuth, new ZappCompany(COMPANYID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_COMPANY", "[addCompany] " + messageService.getMessage("ERR_C_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Company Manager Department 등록]
		 * 
		 */
		String DEPTID = BLANK;
		pObj.setZappDept(new ZappDept());
		pObj.getZappDept().setCompanyid(COMPANYID);
		pObj.getZappDept().setUpid(COMPANYID);
		pObj.getZappDept().setName(pZappCompany.getName());
		pObj.getZappDept().setCode(pZappCompany.getCode());
		pObj.getZappDept().setAbbrname(pZappCompany.getAbbrname());
		pObj.getZappDept().setObjSkipAcl(pObj.getObjSkipAcl()); //jwjang
		pObjRes = addObject(pObjAuth, pObj.getZappDept(), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_DEPT", "[addCompany] " + messageService.getMessage("ERR_C_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		DEPTID = (String) pObjRes.getResObj();
		
		/* [Company Manager 등록]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(DEPTID) == true) {
			
			// Unspecified Code
			ZappCode pZappCode = new ZappCode(COMPANYID, YES);
			pZappCode.setUpid("ROOT");
			pZappCode.setTypes(ZappConts.TYPES.CODE_UNKNOWN.type);
			pZappCode.setCodevalue("UNKNOWN");
			pZappCode.setCodekey("UNKNOWN");
			String UNKNOWN = ZappKey.getPk(pZappCode);
			
			// User 등록
			ZappUserExtend pZappUserExtend = new ZappUserExtend();
			pZappUserExtend.setCompanyid(COMPANYID);
			pZappUserExtend.setEmpno(pZappCompany.getCode());
			pZappUserExtend.setLoginid(pZappCompany.getCode());
			pZappUserExtend.setName(ZappConts.TYPES.USERTYPE_COMPANY.comment);
			pZappUserExtend.setPasswd(pZappCompany.getCode());
			pZappUserExtend.setEmail(pZappCompany.getCode());
			pZappUserExtend.setObjSkipAcl(pObj.getObjSkipAcl()); //jwjang
			
			ZappDeptUser pZappDeptUser = new ZappDeptUser();
			pZappDeptUser.setUsertype(ZappConts.TYPES.USERTYPE_COMPANY.type);	// Company Manager
			pZappDeptUser.setDeptid(DEPTID);
			pZappDeptUser.setPositionid(UNKNOWN);
			pZappDeptUser.setDutyid(UNKNOWN);
			pZappDeptUser.setSeclevelid(UNKNOWN);
			pZappDeptUser.setIssupervisor(NO);
			pZappDeptUser.setOriginyn(YES);
			pZappDeptUser.setIsactive(YES);
			pZappUserExtend.setZappDeptUser(pZappDeptUser);
			
			pObjRes = addUser(pObjAuth, pZappUserExtend, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_USER", "[addCompany] " + messageService.getMessage("ERR_C_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}					
		}
		
		/* [Create Sequence]
		 * Generating sequence for content no.
		 */
		commonService.createSeq(pObjAuth, "C", pZappCompany.getCode());
		
		/*  [Logging]
		 * 
		 */
		if(pObj.getObjSkipAcl() == false) {
			if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, COMPANYID);
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_COMPANY.log
								 , ZappConts.LOGS.ACTION_ADD.log
						         , LOGMAP, PROCTIME, pObjRes);
			}		
		}
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult disableCompany(ZappAuth pObjAuth, ZappCompany pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObj);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObj, pObjRes, "disableCompany", ZappConts.ACTION.DISABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 세션에 저장된 시스템 설정 정보를 조회한다.
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 처리자 권한을 체크한다.
		 */
		
		  if(aclService.isSuperManager(pObjAuth) == false) { return
		  ZappFinalizing.finalising("ERR_NO_ACL", "[disableDept] " +
		  messageService.getMessage("ERR_NO_ACL", pObjAuth.getObjlang()), pObjAuth.getObjlang()); }
		 
		
		/* [Company 삭제]
		 * 
		 */
		pObj.setIsactive(NO);
		pObjRes = changeObject(pObjAuth, pObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_COMPANY", "[disableCompany] " + messageService.getMessage("ERR_E_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObj.getCompanyid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_COMPANY.log
							 , ZappConts.LOGS.ACTION_DISABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult enableCompany(ZappAuth pObjAuth, ZappCompany pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObj);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObj, pObjRes, "enableCompany", ZappConts.ACTION.ENABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 세션에 저장된 시스템 설정 정보를 조회한다.
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 처리자 권한을 체크한다.
		 */
		
	   if(aclService.isSuperManager(pObjAuth) == false) { return
	     ZappFinalizing.finalising("ERR_NO_ACL", "[disableDept] " +
		  messageService.getMessage("ERR_NO_ACL", pObjAuth.getObjlang()), pObjAuth.getObjlang()); }
		 
		
		/* [Company 삭제]
		 * 
		 */
		pObj.setIsactive(YES);
		pObjRes = changeObject(pObjAuth, pObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_COMPANY", "[enableCompany] " + messageService.getMessage("ERR_E_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObj.getCompanyid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_COMPANY.log
							 , ZappConts.LOGS.ACTION_ENABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardCompany(ZappAuth pObjAuth, ZappCompany pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObj);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObj, pObjRes, "discardCpmpany", ZappConts.ACTION.DISCARD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
	    if(aclService.isSuperManager(pObjAuth) == false) { 
			return ZappFinalizing.finalising("ERR_NO_ACL", "[discardCpmpany] " + messageService.getMessage("ERR_NO_ACL", pObjAuth.getObjlang()), pObjAuth.getObjlang()); 
		}
		
		/* Inquiry company info.
		 * 
		 */
		ZappCompany rZappCompany = null;
		pObjRes = selectObject(pObjAuth, new ZappCompany(pObj.getCompanyid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_COMPANY", "[discardCpmpany] " + messageService.getMessage("ERR_R_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappCompany = (ZappCompany) pObjRes.getResObj();
		if(rZappCompany == null) {
			return ZappFinalizing.finalising("ERR_R_COMPANY", "[discardCpmpany] " + messageService.getMessage("ERR_R_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Use or not]
		 * 
		 */
		pObjRes = checkUsingInOtherObject(pObjAuth, "discardDept", pObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Delete other info.]
		 * 
		 */
		// [01] Code
		ZappCode pZappCode = new ZappCode();
		pZappCode.setCompanyid(pObj.getCompanyid());
		systemService_.dMultiRows(pObjAuth, pZappCode);
		
		// [02] Preferences
		ZappEnv pZappEnv = new ZappEnv();
		pZappEnv.setCompanyid(pObj.getCompanyid());
		systemService_.dMultiRowsByCompany(pObjAuth, new ZappEnv(), pObj.getCompanyid());
		
		// [03] Classification
		ZappClassification pZappClassification = new ZappClassification();
		pZappClassification.setCompanyid(pObj.getCompanyid());
		classService_.dMultiRows(pObjAuth, null, pZappClassification);
		
		// [04] Dept. User
		organService.dMultiRowsByCompany(pObjAuth, new ZappDeptUser(), pObj.getCompanyid());
		
		// [05] Grouo. User
		organService.dMultiRowsByCompany(pObjAuth, new ZappGroupUser(), pObj.getCompanyid());
		
		// [06] Dept.
		ZappDept pZappDept = new ZappDept();
		pZappDept.setCompanyid(pObj.getCompanyid());
		organService.dMultiRows(pObjAuth, pZappDept);

		// [07] User
		ZappUser pZappUser = new ZappUser();
		pZappUser.setCompanyid(pObj.getCompanyid());
		organService.dMultiRows(pObjAuth, pZappUser);
		
		// [08] Group
		ZappGroup pZappGroup = new ZappGroup();
		pZappGroup.setCompanyid(pObj.getCompanyid());
		organService.dMultiRows(pObjAuth, pZappGroup);

		// [09] Organ. Task
		ZappOrganTask pZappOrganTask = new ZappOrganTask();
		pZappOrganTask.setCompanyid(pObj.getCompanyid());
		organService.dMultiRows(pObjAuth, pZappOrganTask);
		
		/* [Delete company]
		 * 
		 */
		pObj.setIsactive(NO);
		pObjRes = deleteObject(pObjAuth, pObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_COMPANY", "[discardCpmpany] " + messageService.getMessage("ERR_D_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Drop Sequence]
		 * Dropping sequence for content no.
		 */
		commonService.deleteSeq(pObjAuth, "C", rZappCompany.getCode());
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObj.getCompanyid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_COMPANY.log
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}		
	
	/* **************************************************************************************************************************************** */
	/* Department 																																	*/
	/* **************************************************************************************************************************************** */
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addDept(ZappAuth pObjAuth, ZappDept pObjDept, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String DEPTID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjDept);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjDept, pObjRes, "addDept", ZappConts.ACTION.ADD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[addDept] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Add dept.]
		 * 
		 */
		pObjRes = addObject(pObjAuth, pObjDept, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_DEPT", "[addDept] " + messageService.getMessage("ERR_C_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		DEPTID = (String) pObjRes.getResObj();
		
		/* [Add dept. group user]
		 * 
		 */
//		ZappGroupUser pZappGroupUser = new ZappGroupUser(DEPTID, DEPTID, ZappConts.TYPES.OBJTYPE_DEPT.type);
//		pObjRes = addObject(pObjAuth, pZappGroupUser, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_C_GROUPUSER", "[addDept] " + messageService.getMessage("ERR_C_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}

		/* [Create Sequence]
		 * Generating sequence for content no.
		 */
		commonService.createSeq(pObjAuth, "D", pObjDept.getCode());		
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjDept);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, DEPTID);
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_DEPT.log
							 , ZappConts.LOGS.ACTION_ADD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult disableDept(ZappAuth pObjAuth, ZappDept pObjDept, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String DEPTID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjDept);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjDept, pObjRes, "disableDept", ZappConts.ACTION.DISABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[disableDept] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Change dept.]
		 * 
		 */
		pObjDept.setIsactive(NO);
		pObjRes = changeObject(pObjAuth, pObjDept, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_DEPT", "[disableDept] " + messageService.getMessage("ERR_E_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Disable lower dept.]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjDept.getObjIncLower())) {
			if(pObjDept.getObjIncLower().equals(YES)) {
				pObjRes = selectObjectDown(pObjAuth, null, new ZappDept(pObjDept.getDeptid()), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_DEPT", "[disableDept] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				List<ZappDept> rZappDeptDownList = (List<ZappDept>) pObjRes.getResObj();
				StringBuffer sbids = new StringBuffer();
				if(rZappDeptDownList != null) {
					for(ZappDept vo : rZappDeptDownList) {
						if(vo.getDeptid().equals(pObjDept.getDeptid())) { continue; }
						sbids.append(vo.getDeptid() + DIVIDER);
					}
					if(sbids.length() > ZERO) {
						ZappDept pvo_set = new ZappDept();
						pvo_set.setIsactive(NO);
						ZappDept pvo_filter = new ZappDept();
						pvo_filter.setDeptid(Operators.IN.operator);
						ZappDept pvo_where = new ZappDept();
						pvo_where.setDeptid(sbids.toString());
						pObjRes = changeObject(pObjAuth, pvo_set, pvo_filter, pvo_where, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_DEPT", "[disableDept] " + messageService.getMessage("ERR_E_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				}
			}
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjDept);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjDept.getDeptid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_DEPT.log
							 , ZappConts.LOGS.ACTION_DISABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult enableDept(ZappAuth pObjAuth, ZappDept pObjDept, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjDept);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjDept, pObjRes, "enableDept", ZappConts.ACTION.ENABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[enableDept] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Change dept.]
		 * 
		 */
		pObjDept.setIsactive(YES);
		pObjRes = changeObject(pObjAuth, pObjDept, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_DEPT", "[enableDept] " + messageService.getMessage("ERR_E_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Disable lower dept.]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjDept.getObjIncLower())) {
			if(pObjDept.getObjIncLower().equals(YES)) {
				pObjRes = selectObjectDown(pObjAuth, null, new ZappDept(pObjDept.getDeptid()), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_DEPT", "[enableDept] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				List<ZappDept> rZappDeptDownList = (List<ZappDept>) pObjRes.getResObj();
				StringBuffer sbids = new StringBuffer();
				if(rZappDeptDownList != null) {
					for(ZappDept vo : rZappDeptDownList) {
						if(vo.getDeptid().equals(pObjDept.getDeptid())) { continue; }
						sbids.append(vo.getDeptid() + DIVIDER);
					}
					if(sbids.length() > ZERO) {
						ZappDept pvo_set = new ZappDept();
						pvo_set.setIsactive(YES);
						ZappDept pvo_filter = new ZappDept();
						pvo_filter.setDeptid(Operators.IN.operator);
						ZappDept pvo_where = new ZappDept();
						pvo_where.setDeptid(sbids.toString());
						pObjRes = changeObject(pObjAuth, pvo_set, pvo_filter, pvo_where, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_DEPT", "[enableDept] " + messageService.getMessage("ERR_E_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				}
			}
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjDept);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjDept.getDeptid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_DEPT.log
							 , ZappConts.LOGS.ACTION_ENABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardDept(ZappAuth pObjAuth, ZappDept pObjDept, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String DEPTID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjDept);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjDept, pObjRes, "discardDept", ZappConts.ACTION.DISCARD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[deleteDept] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Inquiry dept. info.
		 * 
		 */
		ZappDept rZappDept = null;
		pObjRes = selectObject(pObjAuth, new ZappDept(pObjDept.getDeptid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_DEPT", "[deleteDept] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappDept = (ZappDept) pObjRes.getResObj();
		if(rZappDept == null) {
			return ZappFinalizing.finalising("ERR_R_DEPT", "[deleteDept] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Use or not]
		 * 
		 */
		pObjRes = checkUsingInOtherObject(pObjAuth, "discardDept", pObjDept, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Delete dept.]
		 * 
		 */
		pObjDept.setIsactive(NO);
		pObjRes = deleteObject(pObjAuth, pObjDept, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_DEPT", "[deleteDept] " + messageService.getMessage("ERR_D_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Delete dept. group user]
		 * 
		 */
//		ZappGroupUser pZappGroupUser = new ZappGroupUser(DEPTID, DEPTID, ZappConts.TYPES.OBJTYPE_DEPT.type);
//		pObjRes = deleteObject(pObjAuth, pZappGroupUser, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_D_GROUPUSER", "[deleteDept] " + messageService.getMessage("ERR_D_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}

		/* [Drop Sequence]
		 * Dropping sequence for content no.
		 */
		commonService.deleteSeq(pObjAuth, "D", rZappDept.getCode());				
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjDept);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjDept.getDeptid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_DEPT.log
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
	
	/* **************************************************************************************************************************************** */
	/* User 																																	*/
	/* **************************************************************************************************************************************** */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addUser(ZappAuth pObjAuth, ZappUserExtend pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String USERID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		ZappEnv SYS_LOG_SYSTEM_YN = null;
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		if(pObjUser.getObjSkipAcl() == false) {
			pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "addUser", ZappConts.ACTION.ADD);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
		}
		/* [Inquire preferences]
		 * 
		 */
		if(pObjUser.getObjSkipAcl() == false) {
			SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(pObjUser.getObjSkipAcl() == false) {
			if(aclService.isSuperManager(pObjAuth) == false 
					&& aclService.isCompanyManager(pObjAuth) == false 
					&& aclService.isDeptManager(pObjAuth, pObjAuth.getObjDeptid()) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addUser] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}		
		/* [Checking License]
		 * 01. Get current no. of users
		 * 02. Get 
		 */
		// 02.
		if(pObjUser.getObjSkipAcl() == false) {
			License pLicense = new License();
			pLicense.setFilepath(LIC_PATH);
			String CHECK_TYPE = BLANK;
			try {
				CHECK_TYPE = ZenithLicense.verifyCheckType(pLicense);
			} catch (Exception e1) {
				return ZappFinalizing.finalising("ERR_R_LIC", "[addUser] " + messageService.getMessage("ERR_R_LIC",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(CHECK_TYPE.equals(Conts.Lic.Check_User.key)) {	// User
				// 01.
				ZappUser pZappUser = new ZappUser();
				pZappUser.setCompanyid(pObjAuth.getObjCompanyid());
				pObjRes = countObject(pObjAuth, pZappUser, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_USER", "[addUser] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				int CNT_CURRENT = (Integer) pObjRes.getResObj();
				// 02
				int CNT_LICENSE = ZERO;
				try {
					CNT_LICENSE = ZenithLicense.verifyUserCount(pLicense);
				} catch (Exception e) {
					return ZappFinalizing.finalising("ERR_R_LIC", "[addUser] " + messageService.getMessage("ERR_R_LIC",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if((CNT_CURRENT + ONE) > CNT_LICENSE) {
					return ZappFinalizing.finalising("ERR_EXCEED_NO_USER", "[addUser] " + messageService.getMessage("ERR_EXCEED_NO_USER",  pObjAuth.getObjlang()) + " (LICENSE "+CNT_LICENSE+", USER "+CNT_CURRENT+")", pObjAuth.getObjlang());
				}
			}
		}
		/* [Add user]
		 * 
		 */
		ZappUser pZappUser = new ZappUser();
		BeanUtils.copyProperties(pObjUser, pZappUser);
		if(ZstFwValidatorUtils.valid(pZappUser.getMaclimit()) == true) {	// Limit Mac Address
			String[] macs = pZappUser.getMaclimit().split(DIVIDER);
			if(macs != null) {
				pZappUser.setMaclimit(ZappJSONUtils.cvrtObjToJson(macs));
			}
		}
		if(ZstFwValidatorUtils.valid(pZappUser.getIplimit()) == true) {		// Limit IP Address
			String[] ips = pZappUser.getIplimit().split(DIVIDER);
			if(ips != null) {
				pZappUser.setIplimit(ZappJSONUtils.cvrtObjToJson(ips));
			}
		}
		pObjRes = addObject(pObjAuth, pZappUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_USER", "[addUser] " + messageService.getMessage("ERR_C_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		USERID = (String) pObjRes.getResObj();
		
		/* [Add dept. user]
		 * 
		 */
		ZappDeptUser pZappDeptUser = new ZappDeptUser();
		if(pObjUser.getZappDeptUser() != null) {
			BeanUtils.copyProperties(pObjUser.getZappDeptUser(), pZappDeptUser);
			pZappDeptUser.setUserid(USERID);
			pObjRes = addObject(pObjAuth, pZappDeptUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_DEPTUSER", "[addUser] " + messageService.getMessage("ERR_C_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/*  [Logging]
		 * 
		 */
		if(pObjUser.getObjSkipAcl() == false) {
			if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjUser.setUserid(USERID);
				pObjUser.setZappDeptUser(pZappDeptUser);
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, USERID);
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_USER.log, ZappConts.LOGS.ACTION_ADD.log
						         , LOGMAP, PROCTIME, pObjRes);
			}		
		}
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}

	/**
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeUser(ZappAuth pObjAuth, ZappUser pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String USERID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "changeUser", ZappConts.ACTION.CHANGE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isSuperManager(pObjAuth) == false 
				&& aclService.isCompanyManager(pObjAuth) == false 
				&& aclService.isDeptManager(pObjAuth, pObjAuth.getObjDeptid()) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[changeUser] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Change user]
		 * 
		 */
		ZappUser pZappUser = new ZappUser();
		BeanUtils.copyProperties(pObjUser, pZappUser);
		if(ZstFwValidatorUtils.valid(pZappUser.getMaclimit()) == true) {	// Limit Mac Address
			String[] macs = pZappUser.getMaclimit().split(DIVIDER);
			if(macs != null) {
				pZappUser.setMaclimit(ZappJSONUtils.cvrtObjToJson(macs));
			}
		}
		if(ZstFwValidatorUtils.valid(pZappUser.getIplimit()) == true) {		// Limit IP Address
			String[] ips = pZappUser.getIplimit().split(DIVIDER);
			if(ips != null) {
				pZappUser.setIplimit(ZappJSONUtils.cvrtObjToJson(ips));
			}
		}
		pObjRes = changeObject(pObjAuth, pZappUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_USER", "[addUser] " + messageService.getMessage("ERR_C_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		USERID = (String) pObjRes.getResObj();
	
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjUser.setUserid(USERID);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, USERID);
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	/**
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeUserPwd(ZappAuth pObjAuth, ZappUser pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String USERID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "changeUserPwd", ZappConts.ACTION.CHANGE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log
		
		/* [Check user]
		 * 
		 */
		if(ZstFwValidatorUtils.isIdentical(pObjUser.getUserid(), pObjAuth.getSessDeptUser().getUserid()) == false) {
			return ZappFinalizing.finalising("ERR_R_USER", "[changeUserPwd] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Update password]
		 * 
		 */
		ZappUser pZappUser = new ZappUser();
		pZappUser = ZappSalt.generatePasswd(pObjUser);
		pZappUser.setUserid(pObjUser.getUserid());
//		pObjRes = changeObject(pObjAuth, pZappUser, pObjRes);
		if(userMapper.updateByPrimaryKey(pZappUser) < ONE) {
			return ZappFinalizing.finalising("ERR_R_USER", "[changeUserPwd] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
	
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjUser.setUserid(USERID);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, USERID);
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	

	/**
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeUsers(ZappAuth pObjAuth, ZappUserExtend pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String USERID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "changeUsers", ZappConts.ACTION.CHANGE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isSuperManager(pObjAuth) == false 
				&& aclService.isCompanyManager(pObjAuth) == false 
				&& aclService.isDeptManager(pObjAuth, pObjAuth.getObjDeptid()) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[changeUsers] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Change user]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjUser.getUserid()) == true) {
			ZappUser pZappUser = new ZappUser();
			BeanUtils.copyProperties(pObjUser, pZappUser);
			if(ZstFwValidatorUtils.valid(pZappUser.getMaclimit()) == true) {	// Limit Mac Address
				String[] macs = pZappUser.getMaclimit().split(DIVIDER);
				if(macs != null) {
					pZappUser.setMaclimit(ZappJSONUtils.cvrtObjToJson(macs));
				}
			}
			if(ZstFwValidatorUtils.valid(pZappUser.getIplimit()) == true) {		// Limit IP Address
				String[] ips = pZappUser.getIplimit().split(DIVIDER);
				if(ips != null) {
					pZappUser.setIplimit(ZappJSONUtils.cvrtObjToJson(ips));
				}
			}
			pObjRes = changeObject(pObjAuth, pZappUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_USER", "[changeUsers] " + messageService.getMessage("ERR_E_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
//			USERID = (String) pObjRes.getResObj();
		}
		
		/* [Change Dept. User]
		 * 
		 */
		if(pObjUser.getZappDeptUser() != null) {
			if(ZstFwValidatorUtils.valid(pObjUser.getZappDeptUser().getDeptuserid()) == true) {
				ZappDeptUser pZappDeptUser = new ZappDeptUser();
				BeanUtils.copyProperties(pObjUser.getZappDeptUser(), pZappDeptUser);
				pObjRes = changeObject(pObjAuth, pZappDeptUser, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_DEPTUSER", "[changeUsers] " + messageService.getMessage("ERR_E_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjUser.setUserid(USERID);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, USERID);
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult disableUser(ZappAuth pObjAuth, ZappUser pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String DEPTID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "disableUser", ZappConts.ACTION.DISABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[disableUser] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Change user]
		 * 
		 */
		pObjUser.setIsactive(NO);
		pObjRes = changeObject(pObjAuth, pObjUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_USER", "[disableUser] " + messageService.getMessage("ERR_E_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjUser.getUserid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_DISABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult enableUser(ZappAuth pObjAuth, ZappUser pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "enableUser", ZappConts.ACTION.ENABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[enableUser] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Change user]
		 * 
		 */
		pObjUser.setIsactive(YES);
		pObjRes = changeObject(pObjAuth, pObjUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_USER", "[enableUser] " + messageService.getMessage("ERR_E_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		ZappDeptUser pZappDeptUser_Set = new ZappDeptUser();
		ZappDeptUser pZappDeptUser_Where = new ZappDeptUser();
		pZappDeptUser_Where.setUserid(pObjUser.getUserid());
		pZappDeptUser_Set.setIsactive(YES);
		pObjRes = changeObject(pObjAuth, pZappDeptUser_Set, pZappDeptUser_Where, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_DEPTUSER", "[enableUser] " + messageService.getMessage("ERR_E_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjUser.getUserid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_ENABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
	/**
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult disableUsers(ZappAuth pObjAuth, ZappUserExtend pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "disableUsers", ZappConts.ACTION.DISABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[disableUsers] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Options]
		 * 
		 */
		boolean[] OPTS = {false, false}; ZappUser rZappUser = null; ZappDeptUser rZappDeptUser = null; boolean _EXISTDU = false;
		if(ZstFwValidatorUtils.valid(pObjUser.getUserid()) == true) {
			pObjRes = selectObject(pObjAuth, new ZappUser(pObjUser.getUserid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) { return pObjRes; }
			rZappUser = (ZappUser) pObjRes.getResObj();
			if(rZappUser.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_STATE", "[disableUsers - USER] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			OPTS[ZERO] = true;
		}
		if(pObjUser.getZappDeptUser() != null) {
			if(ZstFwValidatorUtils.valid(pObjUser.getZappDeptUser().getDeptuserid()) == true) {
				pObjRes = selectObject(pObjAuth, new ZappDeptUser(pObjUser.getZappDeptUser().getDeptuserid()), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) { return pObjRes; }
				rZappDeptUser = (ZappDeptUser) pObjRes.getResObj();
				if(rZappDeptUser.getIsactive().equals(NO)) {
					return ZappFinalizing.finalising("ERR_STATE", "[disableUsers - DEPTUSER] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
				OPTS[ONE] = true;
			}
		}		
		
		/* [Checking Info.]
		 * Userid 만 입력 된 경우 Deptuser 체크
		 */
		if(OPTS[ZERO] == true && OPTS[ONE] == false) {
//			ZappDeptUser pZappDeptUser = new ZappDeptUser();
//			pZappDeptUser.setUserid(pObjUser.getUserid());
//			pZappDeptUser.setIsactive(YES);
//			_EXISTDU = organService.rExist(pObjAuth, pZappDeptUser);
//			if(_EXISTDU == true) {
//				return ZappFinalizing.finalising("ERR_EXIST_DEPTUSER", "[disableUsers] " + messageService.getMessage("ERR_EXIST_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
			ZappDeptUser pZappDeptUser_Where = new ZappDeptUser(null, pObjUser.getUserid());
			ZappDeptUser pZappDeptUser_Value = new ZappDeptUser();
			pZappDeptUser_Value.setIsactive(NO);
			pObjRes = changeObject(pObjAuth, pZappDeptUser_Value, pZappDeptUser_Where, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_DEPTUSER", "[disableUsers] " + messageService.getMessage("ERR_E_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}		
		
		/* [Change user]
		 * 
		 */
		if(OPTS[ZERO] == true) {
			ZappUser pZappUser = new ZappUser(pObjUser.getUserid());
			pZappUser.setIsactive(NO);
			pObjRes = changeObject(pObjAuth, pZappUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_USER", "[disableUsers] " + messageService.getMessage("ERR_E_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Change Dept. User]
		 * 
		 */
		if(OPTS[ONE] == true) {
			ZappDeptUser pZappDeptUser = new ZappDeptUser(pObjUser.getZappDeptUser().getDeptuserid());
			pZappDeptUser.setIsactive(NO);
			pObjRes = changeObject(pObjAuth, pZappDeptUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_DEPTUSER", "[disableUsers] " + messageService.getMessage("ERR_E_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}		
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjUser.getUserid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_DISABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
//	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardUser(ZappAuth pObjAuth, ZappUser pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String DEPTID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "discardUser", ZappConts.ACTION.DISCARD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[discardUser] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Use or not]
		 * 
		 */
		pObjRes = checkUsingInOtherObject(pObjAuth, "discardUser", pObjUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Delete user]
		 * 
		 */
		pObjUser.setIsactive(NO);
		pObjRes = deleteObject(pObjAuth, pObjUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_USER", "[discardUser] " + messageService.getMessage("ERR_D_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjUser.getUserid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}		
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardUsers(ZappAuth pObjAuth, ZappUserExtend pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjUser);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjUser, pObjRes, "discardUsers", ZappConts.ACTION.DISCARD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[discardUsers] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Options]
		 * 
		 */
		boolean[] OPTS = {false, false}; ZappUser rZappUser = null; ZappDeptUser rZappDeptUser = null; boolean _EXISTDU = false;
		if(ZstFwValidatorUtils.valid(pObjUser.getUserid()) == true) {
			pObjRes = selectObject(pObjAuth, new ZappUser(pObjUser.getUserid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) { return pObjRes; }
			rZappUser = (ZappUser) pObjRes.getResObj();
			if(!rZappUser.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_STATE", "[discardUsers] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			OPTS[ZERO] = true;
		}
		if(pObjUser.getZappDeptUser() != null) {
			if(ZstFwValidatorUtils.valid(pObjUser.getZappDeptUser().getDeptuserid()) == true) {
				pObjRes = selectObject(pObjAuth, new ZappDeptUser(pObjUser.getZappDeptUser().getDeptuserid()), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) { return pObjRes; }
				rZappDeptUser = (ZappDeptUser) pObjRes.getResObj();
				if(!rZappDeptUser.getIsactive().equals(NO)) {
					return ZappFinalizing.finalising("ERR_STATE", "[discardUsers] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
				OPTS[ONE] = true;
			}
		}
		
		/* [Checking Info.]
		 * 01. Discard an user by deptuserid
		 * 02. Discard an user by userid (including depeusers)
		 */
		// 01.
		if(OPTS[ZERO] == false && OPTS[ONE] == true) {
			
			// Checking 
			pObjRes = checkUsingInOtherObject(pObjAuth, "discardUser", new ZappDeptUser(pObjUser.getZappDeptUser().getDeptuserid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Delete
			pObjRes = deleteObject(pObjAuth, new ZappDeptUser(pObjUser.getZappDeptUser().getDeptuserid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_DEPTUSER", "[discardUsers] " + messageService.getMessage("ERR_D_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			
		}
		
		// 02.
		if(OPTS[ZERO] == true && OPTS[ONE] == false) {
			
			// Checking dept. user
			ZappDeptUser pZappDeptUser = new ZappDeptUser();
			pZappDeptUser.setUserid(pObjUser.getUserid());
			pObjRes = selectObject(pObjAuth, pZappDeptUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[discardUsers] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			List<ZappDeptUser> rZappDeptUserList = (List<ZappDeptUser>) pObjRes.getResObj();
			if(rZappDeptUserList != null) {
				for(ZappDeptUser vo : rZappDeptUserList) {
					pObjRes = checkUsingInOtherObject(pObjAuth, "discardUsers", vo, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return pObjRes;
					}
				}
			}
			
			// Delete dept. users
			pZappDeptUser = new ZappDeptUser();
			pZappDeptUser.setUserid(pObjUser.getUserid());
			pObjRes = deleteObject(pObjAuth, pZappDeptUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_DEPTUSER", "[discardUsers] " + messageService.getMessage("ERR_D_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Checking user
			pObjRes = checkUsingInOtherObject(pObjAuth, "discardUsers", new ZappUser(pObjUser.getUserid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Delete user
			pObjRes = deleteObject(pObjAuth, new ZappUser(pObjUser.getUserid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_USER", "[discardUsers] " + messageService.getMessage("ERR_D_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
		}
		
		/* [Checking Info.]
		 * 01. Userid 와 Deptuserid 를 동시에 삭제 하는 경우
		 * 02. Userid 만 삭제하는 경우
		 * 03. Deptuserid 만 
		 */
		// 01.
//		if(OPTS[ZERO] == true && OPTS[ONE] == true) {
//			ZappDeptUser pZappDeptUser_Filter = new ZappDeptUser();
//			pZappDeptUser_Filter.setDeptuserid(Operators.NOT_IN.operator);
//			ZappDeptUser pZappDeptUser = new ZappDeptUser();
//			pZappDeptUser.setDeptuserid(pObjUser.getZappDeptUser().getDeptuserid());
//			pZappDeptUser.setUserid(pObjUser.getUserid());
//			_EXISTDU = organService.rExist(pObjAuth, pZappDeptUser_Filter, pZappDeptUser);
//		}
//		// 02.
//		if(OPTS[ZERO] == true && OPTS[ONE] == false) {
//			ZappDeptUser pZappDeptUser = new ZappDeptUser();
//			pZappDeptUser.setUserid(pObjUser.getUserid());
//			_EXISTDU = organService.rExist(pObjAuth, pZappDeptUser);
//			if(_EXISTDU == true) {
//				return ZappFinalizing.finalising("ERR_EXIST_DEPTUSER", "[discardUsers] " + messageService.getMessage("ERR_EXIST_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
//		}
//		// 03.
//		if(OPTS[ZERO] == false && OPTS[ONE] == true) {
//			ZappDeptUser pZappDeptUser = new ZappDeptUser();
//			pZappDeptUser.setUserid(pObjUser.getUserid());
//			_EXISTDU = organService.rExist(pObjAuth, pZappDeptUser);
//			if(_EXISTDU == true) {
//				return ZappFinalizing.finalising("ERR_EXIST_DEPTUSER", "[discardUsers] " + messageService.getMessage("ERR_EXIST_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
//		}		
//		
//		/* [Use or not]
//		 * 
//		 */
//		if(OPTS[ONE] == true) {
//			ZappDeptUser pZappDeptUser = new ZappDeptUser();
//			BeanUtils.copyProperties(pObjUser.getZappDeptUser(), pZappDeptUser);
//			pObjRes = deleteObject(pObjAuth, pZappDeptUser, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_D_DEPTUSER", "[discardUsers] " + messageService.getMessage("ERR_D_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
//		}
//		
//		/* [Delete a user]
//		 * Deptuser 가 존재하지 않는 경우 삭제
//		 */
//		if(OPTS[ZERO] == true && _EXISTDU == false) {
//			pObjRes = deleteObject(pObjAuth, new ZappUser(pObjUser.getUserid()), pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_D_USER", "[discardUsers] " + messageService.getMessage("ERR_D_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}			
//		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjUser);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjUser.getUserid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_USER.log
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}			
	
	/* **************************************************************************************************************************************** */
	/* Group 																																	*/
	/* **************************************************************************************************************************************** */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addGroup(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String GROUPID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Initialization] ##
		if(pObjGroup.getZappGroup() == null) { pObjGroup.setZappGroup(new ZappGroup()); }
		if(pObjGroup.getZappGroupUsers() == null) { pObjGroup.setZappGroupUsers(new ArrayList<ZappGroupUser>()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjGroup);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjGroup, pObjRes, "addGroup", ZappConts.ACTION.ADD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 1. Company group -> Company manager
		 */
		if(pObjGroup.getZappGroup().getTypes().equals(ZappConts.TYPES.GROUPTYPE_COMPANY.type)) {		// Company
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		if(pObjGroup.getZappGroup().getTypes().equals(ZappConts.TYPES.GROUPTYPE_DEPT.type)) {			// Department
			if(aclService.isDeptManager(pObjAuth, pObjGroup.getZappGroup().getGroupid()) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			GROUPID = pObjAuth.getSessDeptUser().getDeptid();
		}
		if(pObjGroup.getZappGroup().getTypes().equals(ZappConts.TYPES.GROUPTYPE_PERSONAL.type)) {		// User
			if(!pObjGroup.getZappGroup().getUpid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}	
		if(pObjGroup.getZappGroup().getTypes().equals(ZappConts.TYPES.GROUPTYPE_COLLABORATION.type)) {	// Cooperation
			
		}
		if(pObjGroup.getZappGroup().getTypes().equals(ZappConts.TYPES.GROUPTYPE_WORKFLOW.type)) {	    // Workflow
			if(aclService.isCompanyManager(pObjAuth) == false 
					&& aclService.isDeptManager(pObjAuth, pObjAuth.getSessDeptUser().getDeptid()) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(aclService.isCompanyManager(pObjAuth) == true) {
				pObjGroup.getZappGroup().setUpid(pObjAuth.getObjCompanyid());
			} else {
				if(aclService.isDeptManager(pObjAuth, pObjAuth.getSessDeptUser().getDeptid()) == true) {
					pObjGroup.getZappGroup().setUpid(pObjAuth.getSessDeptUser().getDeptid());
				}
			}
		}
		
		/* [Add a group]
		 * 
		 */
		pObjRes = addObject(pObjAuth, pObjGroup.getZappGroup(), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_GROUP", "[addGroup] " + messageService.getMessage("ERR_C_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(!pObjGroup.getZappGroup().getTypes().equals(ZappConts.TYPES.GROUPTYPE_DEPT.type)) {
			GROUPID = (String) pObjRes.getResObj();
		}
		
		/* [Add new group users]
		 * 
		 */
		for(int IDX = ZERO; IDX < pObjGroup.getZappGroupUsers().size(); IDX++) {
			pObjGroup.getZappGroupUsers().get(IDX).setGroupid(GROUPID);
			pObjGroup.getZappGroupUsers().get(IDX).setGroupuserid(ZappKey.getPk(pObjGroup.getZappGroupUsers().get(IDX)));
		}
		pObjRes = addObject(pObjAuth, pObjGroup.getZappGroupUsers(), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_GROUPUSER", "[addGroup] " + messageService.getMessage("ERR_C_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Refresh View]
		 * 
		 */
		rview(pObjAuth, pObjRes);
		
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjGroup);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjGroup.getZappGroup().getGroupid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_GROUP.log
							 , ZappConts.LOGS.ACTION_ADD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeGroup(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String GROUPID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Initialization] ##
		if(pObjGroup.getZappGroup() == null) { pObjGroup.setZappGroup(new ZappGroup()); }
		if(pObjGroup.getZappGroupUsers() == null) { pObjGroup.setZappGroupUsers(new ArrayList<ZappGroupUser>()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjGroup);
		GROUPID = pObjGroup.getZappGroup().getGroupid();
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjGroup, pObjRes, "changeGroup", ZappConts.ACTION.CHANGE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Inquire group info.]
		 * 
		 */
		pObjRes = selectObject(pObjAuth, new ZappGroup(GROUPID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUP", "[changeGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappGroup rZappGroup = (ZappGroup) pObjRes.getResObj(); 
		if(rZappGroup == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[changeGroup] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Check access control info.]
		 * 1. Company group -> Company manager
		 * 2. Department Group -> Department manager
		 * 2. User Group -> User
		 * 4. Cooperation Group -> 
		 */
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_COMPANY.type)) {		// Company
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_DEPT.type)) {			// Department
			if(aclService.isDeptManager(pObjAuth, rZappGroup.getGroupid()) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			GROUPID = pObjAuth.getSessDeptUser().getDeptid();
		}
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_PERSONAL.type)) {		// User
			if(!rZappGroup.getUpid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[addGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}	
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_COLLABORATION.type)) {	// Cooperation
			
		}
		
		/* [Checking approval process]
		 * 
		 */
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_WORKFLOW.type)) {
			Map<String, Object> pMap = new HashMap<String, Object>();
			pMap.put("objType", "WORKFLOW");
			pMap.put("objid", rZappGroup.getGroupid());
			
			List<ZappCommon> rUsingList = commonService.usingOtherTable(pObjAuth, pMap);
			if(rUsingList != null) {
				if(rUsingList.size() > 0) {
					return ZappFinalizing.finalising("ERR_ALREADY_USED", "[checkUsingInOtherObject] " + messageService.getMessage("ERR_ALREADY_USED",  pObjAuth.getObjlang()), pObjAuth.getObjlang(), rUsingList);
				}
			}
		}
		
		/* [Edit group member info.]
		 * 1. Add new group members
		 * 2. Delete group members
		 */
		List<ZappGroupUser> pZappGroupUser_ADD = new ArrayList<ZappGroupUser>();
		List<ZappGroupUser> pZappGroupUser_DISCARD = new ArrayList<ZappGroupUser>();
		List<ZappGroupUser> pZappGroupUser_CHANGE = new ArrayList<ZappGroupUser>();
		for(ZappGroupUser vo : pObjGroup.getZappGroupUsers()) {
			if(vo.getObjAction().equals(ZappConts.ACTION.ADD.toString())) {
				pZappGroupUser_ADD.add(vo);
			} else if(vo.getObjAction().equals(ZappConts.ACTION.CHANGE.toString())) {
				pZappGroupUser_CHANGE.add(vo);
			} else if(vo.getObjAction().equals(ZappConts.ACTION.DISCARD.toString())) {
				pZappGroupUser_DISCARD.add(vo);
			}
		}
		for(int IDX = ZERO; IDX < pZappGroupUser_ADD.size(); IDX++) {
			pZappGroupUser_ADD.get(IDX).setGroupid(GROUPID);
			pZappGroupUser_ADD.get(IDX).setGroupuserid(ZappKey.getPk(pZappGroupUser_ADD.get(IDX)));
		}

		
		// 1. Delete
		ZappGroupUser pZappGroupUser_Filter = new ZappGroupUser();
		ZappGroupUser pZappGroupUser_Value = new ZappGroupUser();
		pZappGroupUser_Filter.setGroupuserid(Operators.IN.operator);
		StringBuffer pk = new StringBuffer();
		if(pZappGroupUser_DISCARD.size() > 0) {
			for(int IDX = ZERO; IDX < pZappGroupUser_DISCARD.size(); IDX++) {
				pk.append(pZappGroupUser_DISCARD.get(IDX).getGroupuserid() + DIVIDER);
			}
			pZappGroupUser_Value.setGroupuserid(pk.toString());
			pObjRes = deleteObject(pObjAuth, pZappGroupUser_Filter, pZappGroupUser_Value, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_GROUPUSER", "[changeGroup] " + messageService.getMessage("ERR_D_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}		
		}
		
		// 2. Add
		pObjRes = addObject(pObjAuth, pZappGroupUser_ADD, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_GROUPUSER", "[changeGroup] " + messageService.getMessage("ERR_C_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// 3. Update
		if(pZappGroupUser_CHANGE.size() > 0) {
			for(int IDX = ZERO; IDX < pZappGroupUser_CHANGE.size(); IDX++) {
				pObjRes = changeObject(pObjAuth, pZappGroupUser_CHANGE.get(IDX), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_GROUPUSER", "[changeGroup] " + messageService.getMessage("ERR_E_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		
		/* [Update group info.]
		 * Except for dept. group / access-free group
		 */
		if(!rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_DEPT.type)
				&& !rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_FREEACCESS.type)) {
			pObjRes = changeObject(pObjAuth, pObjGroup.getZappGroup(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_GROUP", "[changeGroup] " + messageService.getMessage("ERR_E_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
		}
		
		/* [Refresh View]
		 * 
		 */
		rview(pObjAuth, pObjRes);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjGroup);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjGroup.getZappGroup().getGroupid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_GROUP.log
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);		
		
		return pObjRes;		
	}
	
	
	@SuppressWarnings("unchecked")
	public ZstFwResult selectGroup(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String GROUPID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Initialization] ##
		if(pObjGroup.getZappGroup() == null) { pObjGroup.setZappGroup(new ZappGroup()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjGroup);
		GROUPID = pObjGroup.getZappGroup().getGroupid();
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjGroup, pObjRes, "selectGroup", ZappConts.ACTION.VIEW_PK);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Inquire group info.]
		 * 
		 */
		pObjRes = selectObject(pObjAuth, new ZappGroup(GROUPID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUP", "[changeGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappGroup rZappGroup = (ZappGroup) pObjRes.getResObj(); 
		if(rZappGroup == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[changeGroup] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		pObjGroup.setZappGroup(rZappGroup);
		
		/* [Inquire group members]
		 * 
		 */
		pObjRes = selectObjectExtend(pObjAuth, new ZappGroupUser(GROUPID, null, null), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUPUSER", "[changeGroup] " + messageService.getMessage("ERR_R_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		pObjGroup.setZappGroupUserExtends((List<ZappGroupUserExtend>) pObjRes.getResObj());
		
		/*  [Logging]
		 * 
		 */
//		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
//			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
//			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjGroup);
//			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjGroup.getZappGroup().getGroupid());
//			pObjRes = leaveLog(pObjAuth
//					         , ZappConts.LOGS.TYPE_GROUP.log
//							 , ZappConts.LOGS.ACTION_VIEW.log
//					         , LOGMAP
//					         , PROCTIME
//					         , pObjRes);
//		}
		
		pObjRes.setResObj(pObjGroup);	
		
		return pObjRes;		
	}	

	
	@SuppressWarnings("unchecked")
	public ZstFwResult selectGroupByUser(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Initialization] ##
		if(pObjGroup.getZappGroup() == null) { pObjGroup.setZappGroup(new ZappGroup()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjGroup);
		
		/* [Inquire group info.]
		 * 
		 */
		pObjRes = selectObjectByUser(pObjAuth, null, pObjGroup.getZappGroup(), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUP", "[changeGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappGroup> rZappGroup = (List<ZappGroup>) pObjRes.getResObj(); 
//		if(rZappGroup == null) {
//			return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[changeGroup] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		pObjRes.setResObj(rZappGroup);	
		
		return pObjRes;		
	}	

	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult disableGroup(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjGroup);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjGroup, pObjRes, "disableGroup", ZappConts.ACTION.DISABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 처리자 권한을 체크한다.
		 */
//		if(aclService.isCompanyManager(pObjAuth) == false) {
//			return ZappFinalizing.finalising("ERR_NO_ACL", "[disableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* [Inquire group info.]
		 * 
		 */
		pObjRes = selectObject(pObjAuth, new ZappGroup(pObjGroup.getZappGroup().getGroupid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUP", "[discardGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappGroup rZappGroup = (ZappGroup) pObjRes.getResObj(); 
		if(rZappGroup == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[discardGroup] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(rZappGroup.getIsactive().equals(NO)) {
			return ZappFinalizing.finalising("ERR_STATE", "[discardGroup] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}	
		
		/* [Check access control info. by type]
		 * 
		 */
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_COMPANY.type)) {
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[disableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		} else if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_PERSONAL.type)) {
			if(!rZappGroup.getUpid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[disableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Group disable]
		 * 
		 */
		pObjGroup.getZappGroup().setIsactive(NO);
		pObjRes = changeObject(pObjAuth, pObjGroup.getZappGroup(), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_GROUP", "[disableGroup] " + messageService.getMessage("ERR_E_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Disable lower groups]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjGroup.getObjIncLower())) {
			if(pObjGroup.getObjIncLower().equals(YES)) {
				pObjRes = selectObjectDown(pObjAuth, null, new ZappGroup(pObjGroup.getZappGroup().getGroupid()), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_GROUP", "[discardGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				List<ZappGroup> rZappGroupDownList = (List<ZappGroup>) pObjRes.getResObj();
				StringBuffer sbids = new StringBuffer();
				if(rZappGroupDownList != null) {
					for(ZappGroup vo : rZappGroupDownList) {
						if(vo.getGroupid().equals(pObjGroup.getZappGroup().getGroupid())) { continue; }
						sbids.append(vo.getGroupid() + DIVIDER);
					}
					if(sbids.length() > ZERO) {
						ZappGroup pvo_set = new ZappGroup();
						pvo_set.setIsactive(NO);
						ZappGroup pvo_filter = new ZappGroup();
						pvo_filter.setGroupid(Operators.IN.operator);
						ZappGroup pvo_where = new ZappGroup();
						pvo_where.setGroupid(sbids.toString());
						pObjRes = changeObject(pObjAuth, pvo_set, pvo_filter, pvo_where, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_GROUP", "[discardGroup] " + messageService.getMessage("ERR_E_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				}
			}
		}
		
		/* [Refresh View]
		 * 
		 */
		rview(pObjAuth, pObjRes);			
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjGroup);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjGroup.getZappGroup().getGroupid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_GROUP.log
							 , ZappConts.LOGS.ACTION_DISABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult enableGroup(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjGroup);
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjGroup, pObjRes, "enableGroup", ZappConts.ACTION.ENABLE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
//		if(aclService.isCompanyManager(pObjAuth) == false) {
//			return ZappFinalizing.finalising("ERR_NO_ACL", "[disableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* [Inquire group info.]
		 * 
		 */
		pObjRes = selectObject(pObjAuth, new ZappGroup(pObjGroup.getZappGroup().getGroupid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUP", "[enableGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappGroup rZappGroup = (ZappGroup) pObjRes.getResObj(); 
		if(rZappGroup == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[enableGroup] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(rZappGroup.getIsactive().equals(YES)) {
			return ZappFinalizing.finalising("ERR_STATE", "[enableGroup] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}	
		
		/* [Check access control info. by type]
		 * 
		 */
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_COMPANY.type)) {
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[enableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		} else if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_PERSONAL.type)) {
			if(!rZappGroup.getUpid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[enableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Group disable]
		 * 
		 */
		pObjGroup.getZappGroup().setIsactive(YES);
		pObjRes = changeObject(pObjAuth, pObjGroup.getZappGroup(), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_E_GROUP", "[enableGroup] " + messageService.getMessage("ERR_E_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Disable lower groups]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjGroup.getObjIncLower())) {
			if(pObjGroup.getObjIncLower().equals(YES)) {
				pObjRes = selectObjectDown(pObjAuth, null, new ZappGroup(pObjGroup.getZappGroup().getGroupid()), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_GROUP", "[enableGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				List<ZappGroup> rZappGroupDownList = (List<ZappGroup>) pObjRes.getResObj();
				StringBuffer sbids = new StringBuffer();
				if(rZappGroupDownList != null) {
					for(ZappGroup vo : rZappGroupDownList) {
						if(vo.getGroupid().equals(pObjGroup.getZappGroup().getGroupid())) { continue; }
						sbids.append(vo.getGroupid() + DIVIDER);
					}
					if(sbids.length() > ZERO) {
						ZappGroup pvo_set = new ZappGroup();
						pvo_set.setIsactive(YES);
						ZappGroup pvo_filter = new ZappGroup();
						pvo_filter.setGroupid(Operators.IN.operator);
						ZappGroup pvo_where = new ZappGroup();
						pvo_where.setGroupid(sbids.toString());
						pObjRes = changeObject(pObjAuth, pvo_set, pvo_filter, pvo_where, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_GROUP", "[enableGroup] " + messageService.getMessage("ERR_E_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				}
			}
		}
		
		/* [Refresh View]
		 * 
		 */
		rview(pObjAuth, pObjRes);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjGroup);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjGroup.getZappGroup().getGroupid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_GROUP.log
							 , ZappConts.LOGS.ACTION_ENABLE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardGroup(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String GROUPID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## [Initialization] ##
		if(pObjGroup.getZappGroup() == null) { pObjGroup.setZappGroup(new ZappGroup()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjGroup);
		GROUPID = pObjGroup.getZappGroup().getGroupid();
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjGroup, pObjRes, "discardGroup", ZappConts.ACTION.DISCARD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Inquire group info.]
		 * 
		 */
		pObjRes = selectObject(pObjAuth, new ZappGroup(GROUPID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUP", "[discardGroup] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappGroup rZappGroup = (ZappGroup) pObjRes.getResObj(); 
		if(rZappGroup == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUP", "[discardGroup] " + messageService.getMessage("ERR_NEXIST_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(!rZappGroup.getIsactive().equals(NO)) {
			return ZappFinalizing.finalising("ERR_STATE", "[discardGroup] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Check access control info. by type]
		 * 
		 */
		if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_COMPANY.type)) {
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[disableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		} else if(rZappGroup.getTypes().equals(ZappConts.TYPES.GROUPTYPE_PERSONAL.type)) {
			if(!rZappGroup.getUpid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[disableGroup] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Use or not]
		 * 
		 */
		pObjRes = checkUsingInOtherObject(pObjAuth, "discardGroup", new ZappGroup(GROUPID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire group members]
		 * 
		 */
//		pObjRes = selectObjectExtend(pObjAuth, new ZappGroupUser(GROUPID, null, null), pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_R_GROUPUSER", "[discardGroup] " + messageService.getMessage("ERR_R_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		pObjGroup.setZappGroupUserExtends((List<ZappGroupUserExtend>) pObjRes.getResObj());
		
		/* [Delete group members]
		 * 
		 */
		pObjRes = deleteObject(pObjAuth, new ZappGroupUser(GROUPID, null, null), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_GROUPUSER", "[discardGroup] " + messageService.getMessage("ERR_D_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Delete a group]
		 * 
		 */
		pObjRes = deleteObject(pObjAuth, new ZappGroup(GROUPID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_GROUP", "[discardGroup] " + messageService.getMessage("ERR_D_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Refresh View]
		 * 
		 */
		rview(pObjAuth, pObjRes);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjGroup);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjGroup.getZappGroup().getGroupid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_GROUP.log
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);	
		
		return pObjRes;		
	}
	
	public ZstFwResult addTask(ZappAuth pObjAuth, ZArchTask pObjTask, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log

		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjTask);		
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjTask, pObjRes, "addTask", ZappConts.ACTION.ADD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log	
		
		// Check Task
		ZArchTask pZArchTask_Check = new ZArchTask();
//		pZArchTask_Check.setName(pObjTask.getName());
		pZArchTask_Check.setCode(pObjAuth.getSessCompany().getCode() + PERIOD + pObjTask.getCode());	// Combine company code
		boolean EXIST_TASK = false;
		try {
			EXIST_TASK = taskService.exists(pZArchTask_Check);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(EXIST_TASK == true) {
			return ZappFinalizing.finalising_Archive(Results.EXIST_TASK.result, pObjAuth.getObjlang());
		}
		
		// Add a task
		String TASKID = BLANK;
		pObjTask.setCode(pObjAuth.getSessCompany().getCode() + PERIOD + pObjTask.getCode());	// Combine company code
		try {
			TASKID = taskService.cSingleRow_Pk(pObjTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(ZstFwValidatorUtils.valid(TASKID) == false) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_CREATE_TASK.result, pObjAuth.getObjlang());
		}
		
		// Add OrganTask
		ZappOrganTask pZappOrganTask = new ZappOrganTask();
		pZappOrganTask.setCompanyid(pObjAuth.getObjCompanyid());
		pZappOrganTask.setTaskid(TASKID);
		pZappOrganTask.setTobjtype(ZappConts.TYPES.TASK_COMPANY.type);
		pObjRes = addObject(pObjAuth, pZappOrganTask, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_ORGANTASK", "[addTask] " + messageService.getMessage("ERR_C_ORGANTASK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjTask);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, TASKID);
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_TASK.log
							 , ZappConts.LOGS.ACTION_ADD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);	
		
		return pObjRes;
	}
	
	public ZstFwResult changeTask(ZappAuth pObjAuth, ZArchTask pObjTask, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log

		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjTask);		
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjTask, pObjRes, "changeTask", ZappConts.ACTION.CHANGE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log	
		
		// Check Task
		ZArchTask pZArchTask_Check = new ZArchTask();
		pZArchTask_Check.setTaskid(pObjTask.getTaskid());
		boolean EXIST_TASK = false;
		try {
			EXIST_TASK = taskService.exists(pZArchTask_Check);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(EXIST_TASK == false) {
			return ZappFinalizing.finalising_Archive(Results.NOT_EXIST_TASK.result, pObjAuth.getObjlang());
		}
		
		// Check code
		if(ZstFwValidatorUtils.valid(pObjTask.getCode()) == true) {
			
			EXIST_TASK = false;
			ZArchTask pZArchTask_Filter = new ZArchTask();
			pZArchTask_Filter.setTaskid(Operators.NOT_IN.operator);
			
			pZArchTask_Check = new ZArchTask();
			pZArchTask_Check.setTaskid(pObjTask.getTaskid());
			pZArchTask_Check.setCode(pObjAuth.getSessCompany().getCode() + PERIOD + pObjTask.getCode());
			
			try {
				EXIST_TASK = taskService.exists(pZArchTask_Filter, pZArchTask_Check);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(EXIST_TASK == true) {
				return ZappFinalizing.finalising_Archive(Results.DUPLICATE_TASK.result, pObjAuth.getObjlang());
			}			
			
			pObjTask.setCode(pObjAuth.getSessCompany().getCode() + PERIOD + pObjTask.getCode());	// Combine company code

		}
		
		// Change a task
		ZArchResult rZArchResult = new ZArchResult();
		try {
			rZArchResult = taskService.uSingleRow(pObjTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!rZArchResult.getCode().equals(SUCCESS)) {
			return ZappFinalizing.finalising_Archive(rZArchResult.getCode(), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjTask);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjTask.getTaskid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_TASK.log
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);	
		
		return pObjRes;
		
	}
	
	public ZstFwResult discardTask(ZappAuth pObjAuth, ZArchTask pObjTask, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log

		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjTask);		
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjTask, pObjRes, "discardTask", ZappConts.ACTION.DISCARD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log	
		
		/* [Use or not]
		 * 
		 */
		pObjRes = checkUsingInOtherObject(pObjAuth, "discardTask", new ZArchTask(pObjTask.getTaskid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		// Check cabinets
		boolean EXIST_CABINET = false;
		ZArchTaskCabinetKey pZArchTaskCabinetKey = new ZArchTaskCabinetKey();
		pZArchTaskCabinetKey.setTaskid(pObjTask.getTaskid());
		try {
			EXIST_CABINET = taskcabinetService.exists(pZArchTaskCabinetKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(EXIST_CABINET == true) {
			return ZappFinalizing.finalising_Archive(Results.EXIST_TASKCABINET.result, pObjAuth.getObjlang());
		}
		
		// Delete OrganTask
		ZappOrganTask pZappOrganTask = new ZappOrganTask();
		pZappOrganTask.setCompanyid(pObjAuth.getObjCompanyid());
		pZappOrganTask.setTaskid(pObjTask.getTaskid());
		pObjRes = deleteObject(pObjAuth, pZappOrganTask, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_ORGANTASK", "[discardTask] " + messageService.getMessage("ERR_D_ORGANTASK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		// Delete a Task
		ZArchResult rZArchResult = new ZArchResult();
		try {
			rZArchResult = taskService.dSingleRow(pObjTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!rZArchResult.getCode().equals(SUCCESS)) {
			return ZappFinalizing.finalising_Archive(rZArchResult.getCode(), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjTask);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjTask.getTaskid());
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_TASK.log
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);	
		
		return pObjRes;
	}	
	
	// ### Use or not ###
	private ZstFwResult checkUsingInOtherObject(ZappAuth pObjAuth, String pCaller, Object pObj, ZstFwResult pObjRes) {
	
		if(pObj != null) {
			
			Map<String, Object> pMap = new HashMap<String, Object>();
			
			if(pObj instanceof ZappCompany) {
				ZappCompany pvo = (ZappCompany) pObj;
				pMap.put("objType", "COMPANY");
				pMap.put("objid", pvo.getCompanyid());
			}
			if(pObj instanceof ZappDept) {
				ZappDept pvo = (ZappDept) pObj;
				pMap.put("objType", "DEPT");
				pMap.put("objid", pvo.getDeptid());
			}
			if(pObj instanceof ZappDeptUser) {
				ZappDeptUser pvo = (ZappDeptUser) pObj;
				pMap.put("objType", "DEPTUSER");
				pMap.put("objid", pvo.getDeptuserid());
			}			
			if(pObj instanceof ZappUser) {
				ZappUser pvo = (ZappUser) pObj;
				pMap.put("objType", "USER");
				pMap.put("objid", pvo.getUserid());
			}
			if(pObj instanceof ZappGroup) {
				ZappGroup pvo = (ZappGroup) pObj;
				pMap.put("objType", "GROUP");
				pMap.put("objid", pvo.getGroupid());
			}
			if(pObj instanceof ZArchTask) {
				ZArchTask pvo = (ZArchTask) pObj;
				pMap.put("objType", "TASK");
				pMap.put("objid", pvo.getTaskid());
			}
			
			List<ZappCommon> rUsingList = commonService.usingOtherTable(pObjAuth, pMap);
			if(rUsingList != null) {
				if(rUsingList.size() > 0) {
					return ZappFinalizing.finalising("ERR_ALREADY_USED", "[" + pCaller + "][checkUsingInOtherObject] " + messageService.getMessage("ERR_ALREADY_USED",  pObjAuth.getObjlang()), pObjAuth.getObjlang(), rUsingList);
				}
			}
		}
		
		return pObjRes;
	}
	
	// ### Refresh Mview ###
	private void rview(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		organService.refreshView(pObjAuth, new ZappGroup(), pObjRes);
		organService.refreshView(pObjAuth, new ZappGroupUser(), pObjRes);
	}
	
	// ### Logging ###
	private ZstFwResult leaveLog(ZappAuth pObjAuth, String pLogType, String pLogAction, Map<String, Object> pLogMap, String pLogTime, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		List<ZappSystemLog> pLogObjectList = new ArrayList<ZappSystemLog>();
		ZappSystemLog pLogObject = new ZappSystemLog();
		pLogObject.setLogobjid((String) pLogMap.get(ZappConts.LOGS.ITEM_CONTENT_ID.log));
		pLogObject.setLogtype(pLogType);
		pLogObject.setAction(pLogAction);
		pLogObject.setMaplogs(pLogMap);
		pLogObject.setLogtime(pLogTime);												// Logging time
		pLogObjectList.add(pLogObject);
		pObjRes = logService.leaveLog(pObjAuth, pLogObjectList, pObjRes);
		
		return pObjRes;
	}
	
	// ### Validation ###
	
	private ZstFwResult validParams(ZappAuth pObjAuth
								  , Object pObjPar
								  , ZstFwResult pObjRes
								  , String pCaller
								  , ZappConts.ACTION pAct) {
		
		/* [Authentication Info.]
		 * 
		 */
		if(pObjAuth == null) {
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Input value]
		 * 
		 */
		if(pObjPar == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		if(pObjPar instanceof ZappCompany) {
			
			ZappCompany pObj = (ZappCompany) pObjPar;
			
			// Company ID
			switch(pAct) {
				case CHANGE: case DISABLE: case DISCARD:
					if(!ZstFwValidatorUtils.valid(pObj.getCompanyid())) {
						return ZappFinalizing.finalising("ERR_MIS_ID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_ID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Company name
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pObj.getName())) {
						return ZappFinalizing.finalising("ERR_MIS_NAME", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Code
			switch(pAct) {
				case ADD:
					if(!ZstFwValidatorUtils.valid(pObj.getCode())) {
						return ZappFinalizing.finalising("ERR_MIS_CODE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}			
			
			// Abbreviation
			switch(pAct) {
				case ADD:
					if(!ZstFwValidatorUtils.valid(pObj.getAbbrname())) {
						return ZappFinalizing.finalising("ERR_MIS_ABBR", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_ABBR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}				
		}
		
		if(pObjPar instanceof ZappDept) {
			
			ZappDept pObjDept = (ZappDept) pObjPar;
			
			// Department ID
			switch(pAct) {
				case CHANGE: case REARRANGE:
					if(!ZstFwValidatorUtils.valid(pObjDept.getDeptid())) {
						return ZappFinalizing.finalising("ERR_MIS_ID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_ID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Department name
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pObjDept.getName())) {
						return ZappFinalizing.finalising("ERR_MIS_NAME", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Sorting order
			switch(pAct) {
				case REARRANGE:
					if(!ZstFwValidatorUtils.valid(pObjDept.getPriority())) {
						return ZappFinalizing.finalising("ERR_MIS_ORDER", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}			
			
		}
		
		if(pObjPar instanceof ZappGroupPar) {
	
			ZappGroupPar pObjGroup = (ZappGroupPar) pObjPar;
			
			// Group ID
			switch(pAct) {
				case CHANGE: case DISCARD: case VIEW_PK:
					if(!ZstFwValidatorUtils.valid(pObjGroup.getZappGroup().getGroupid())) {
						return ZappFinalizing.finalising("ERR_MIS_ID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_ID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Group name
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pObjGroup.getZappGroup().getName())) {
						return ZappFinalizing.finalising("ERR_MIS_NAME", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}	
			
			// Upper ID
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pObjGroup.getZappGroup().getUpid())) {
						return ZappFinalizing.finalising("ERR_MIS_UPID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_UPID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}		
			
			// Type
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pObjGroup.getZappGroup().getTypes())) {
						return ZappFinalizing.finalising("ERR_MIS_TYPE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
	
			// Group users
			switch(pAct) {
				case ADD: 
					if(pObjGroup.getZappGroupUsers().size() == ZERO) {
						return ZappFinalizing.finalising("ERR_NEXIST_GROUPUSER", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_NEXIST_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
		}
		
		//
		if(pObjPar instanceof ZappUserExtend) {
			
			ZappUserExtend pObj = (ZappUserExtend) pObjPar;
			
			switch(pAct) {
				case ADD: break;
				case CHANGE: 
					if(ZstFwValidatorUtils.valid(pObj.getUserid()) == true) {
						ZappUser pZappUser = new ZappUser();
						BeanUtils.copyProperties(pObj, pZappUser);
						if(utilBinder.isEmpty(pZappUser) == true) {
							return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[" + pCaller + "][USER] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
					if(pObj.getZappDeptUser() != null) {
						if(ZstFwValidatorUtils.valid(pObj.getZappDeptUser().getDeptuserid()) == true) {
							if(utilBinder.isEmpty(pObj.getZappDeptUser()) == true) {
								return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[" + pCaller + "][DEPTUSER] " + messageService.getMessage("ERR_MIS_DEPTUSERID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
						}					
					}
					if(ZstFwValidatorUtils.valid(pObj.getUserid()) == false) {
						if(pObj.getZappDeptUser() != null) {
							if(ZstFwValidatorUtils.valid(pObj.getZappDeptUser().getDeptuserid()) == false) {
								return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
						}
					}
				break;
				case DISABLE: case DISCARD: 
				break;
				default:break;
			}			
		}
		
		// Task (2022-04-05)
		if(pObjPar instanceof ZArchTask) {
			
			ZArchTask pObj = (ZArchTask) pObjPar;
			
			switch(pAct) {
				case ADD: 
					if(ZstFwValidatorUtils.valid(pObj.getName()) == false) {
						return ZappFinalizing.finalising("ERR_MIS_NAME", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(ZstFwValidatorUtils.valid(pObj.getCode()) == false) {
						return ZappFinalizing.finalising("ERR_MIS_CODE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}				
				break;
				case CHANGE: case DISCARD:
					if(ZstFwValidatorUtils.valid(pObj.getTaskid()) == false) {
						return ZappFinalizing.finalising("ERR_MIS_ID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_ID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}	
			
		}
		
		return pObjRes;
		
	}
		
	
}

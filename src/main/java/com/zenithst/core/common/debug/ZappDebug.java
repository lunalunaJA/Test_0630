package com.zenithst.core.common.debug;

import org.slf4j.Logger;

import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.vo.ZappClassificationPar;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.content.vo.ZappLinkedObject;
import com.zenithst.core.content.vo.ZappSharedObject;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to debug. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappDebug {
	
	static public void debug(Logger pLogger, ZappAuth pObjAuth, Object pObj) {
		
		if(pLogger != null && pObjAuth != null && pObj != null) {
			
			if(pObjAuth.getObjDebugged()) {
				
				pLogger.info("<<>> [Valiables] <<>>");
				pLogger.info("Company (objCompanyid) : [" + pObjAuth.getObjCompanyid() + "]");
				pLogger.info("Department (objDeptid) : [" + pObjAuth.getObjDeptid() + "]");
				pLogger.info("Login ID (objLoginid) : [" + pObjAuth.getObjLoginid() + "]");
				pLogger.info("Employee number (objEmpno) : [" + pObjAuth.getObjEmpno() + "]");
				pLogger.info("Password (objPasswd) : [" + ((ZstFwValidatorUtils.valid(pObjAuth.getObjPasswd())) ? "Y" : "N") + "]");
				
				pLogger.info("<<>> [Authentication] <<>>");
				if(pObjAuth.getSessCompany() != null) {
					pLogger.info("Company : [" + pObjAuth.getSessCompany().getCompanyid() + " - " + pObjAuth.getSessCompany().getName() + "]");
				}
				if(pObjAuth.getSessDeptUser() != null) {
					pLogger.info("Department : [" + pObjAuth.getSessDeptUser().getDeptid() + " - " + pObjAuth.getSessDept().getName() + "]");
				}
				if(pObjAuth.getSessDeptUser() != null) {
					pLogger.info("User : [" + pObjAuth.getSessDeptUser().getDeptuserid() + " - " + pObjAuth.getSessDeptUser().getZappUser().getName() + " ]");
				}
				
				pLogger.info("<<>> [Setting] <<>>");
				pLogger.info("Approval? : [" + ((ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_YN.env)).getSetval() + "]");
				// ...
				
				/* Content */
				if(pObj instanceof ZappContentPar) {
				
					ZappContentPar pObjLog = (ZappContentPar) pObj;
					
					pLogger.info("<<>> [Contents] <<>>");
					if(pObjLog.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						pLogger.info("1> Bundle");
						pLogger.info("BundleID : [" + pObjLog.getZappBundle().getBundleid() + "]");
						pLogger.info("No. : [" + pObjLog.getZappBundle().getBno() + "]");
						pLogger.info("Title : [" + pObjLog.getZappBundle().getTitle() + "]");
						pLogger.info("Retention period : [" + pObjLog.getObjRetention() + "]");
					}
					if(pObjLog.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						pLogger.info("1> File");
						for(ZappFile vo : pObjLog.getZappFiles()) {
							pLogger.info("Full file path : [" + vo.getObjFileName() + "]");
						}
					}
					
					pLogger.info("2> Classification");
					if(pObjLog.getZappClassObjects() != null) {
						for(ZappClassObject vo : pObjLog.getZappClassObjects()) {
							pLogger.info("(Classid) [" + vo.getClassid() + "] - (Objectid) [" + vo.getCobjid() + "] - (Objecttype) [" + vo.getCobjtype() + "]");
						}
					}
					
					pLogger.info("2-1> Classification");
					if(pObjLog.getZappClassification() != null) {
						pLogger.info("(Classid) [" + pObjLog.getZappClassification().getClassid() + "] - (Name) [" + pObjLog.getZappClassification().getName() + "]");
					}
					
					pLogger.info("3> Link");
					if(pObjLog.getZappLinkedObjects() != null) {
						for(ZappLinkedObject vo : pObjLog.getZappLinkedObjects()) {
							pLogger.info("(Source) [" + vo.getSourceid() + "] - (Target) [" + vo.getTargetid() + "] - (Type) [" + vo.getLinktype() + "]");
						}
					}
					
					pLogger.info("4> Share");
					if(pObjLog.getZappSharedObjects() != null) {
						for(ZappSharedObject vo : pObjLog.getZappSharedObjects()) {
							pLogger.info("(Object) [" + vo.getSobjid() + "] - (Type) [" + vo.getSobjtype() + "] - (Reader) [" + vo.getReaderid() + "]");
						}
					}
					
					pLogger.info("5> ACL ");
					if(pObjLog.getZappAcls() != null) {
						for(ZappContentAcl vo : pObjLog.getZappAcls()) {
							pLogger.info("(Contentid) [" + pObjLog.getZappBundle().getBundleid() + "] - (Object) [" + vo.getAclobjid() + "] - (Type) " + vo.getObjType() + " - (Acls) [" + vo.getAcls() + "]");
						}
					}
					
					pLogger.info("6> Workflow ");
//					if(ZstFwValidatorUtils.valid(pObjLog.getZappWorkflow().getWorkflowid())) {
//						pLogger.info("(Workflowid) [" + pObjLog.getZappWorkflow().getWorkflowid()+ "]");
//					} else {
//						for(ZappWorkflower vo : pObjLog.getZappWorkflowers()) {
//							pLogger.info("(Userid) [" + vo.getUserid() + "]");
//						}
//					}
					
					pLogger.info("7> Keyword ");
					if(pObjLog.getZappKeywords() != null) {
						for(ZappKeyword vo : pObjLog.getZappKeywords()) {
							pLogger.info("(Keyword) [" + vo.getKword() + "]");
						}
					}
				}
				
				if(pObj instanceof ZappClassificationPar) {
					
					ZappClassificationPar pObjLog = (ZappClassificationPar) pObj;
					pLogger.info("<<>> [Classification] <<>>");
					pLogger.info("> Classid : [" + pObjLog.getClassid() + "]");
					pLogger.info("> Companyid : [" + pObjLog.getCompanyid() + "]");
					pLogger.info("> Code : [" + pObjLog.getCode() + "]");
					pLogger.info("> Name : [" + pObjLog.getName() + "]");
					pLogger.info("> Upid : [" + pObjLog.getUpid() + "]");
					pLogger.info("> Holderid : [" + pObjLog.getHolderid() + "]");
					pLogger.info("> Types : [" + pObjLog.getTypes() + "]");
					pLogger.info("> Priority : [" + pObjLog.getPriority() + "]");
					pLogger.info("> Isactive : [" + pObjLog.getIsactive() + "]");
					
					if(pObjLog.getZappClassAcls() != null) {
						for(ZappClassAcl vo : pObjLog.getZappClassAcls()) {
							pLogger.info("(Object) [" + vo.getAclobjid() + "] - (Type) " + vo.getObjType() + " - (Acls) [" + vo.getAcls() + "]");
						}
					}
					
					if(pObjLog.getZappContentAcls() != null) {
						for(ZappContentAcl vo : pObjLog.getZappContentAcls()) {
							pLogger.info("(Contenttype) [" + vo.getContenttype() + "] - (Object) [" + vo.getAclobjid() + "] - (Type) " + vo.getObjType() + " - (Acls) [" + vo.getAcls() + "]");
						}
					}
				}
			
			}
			
		}
		
	}

}

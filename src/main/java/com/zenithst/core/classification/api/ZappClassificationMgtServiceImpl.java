package com.zenithst.core.classification.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zenithst.archive.constant.Operators;
import com.zenithst.core.acl.api.ZappAclMgtService;
import com.zenithst.core.acl.vo.ZappAclExtend;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.bind.ZappClassificationBinder;
import com.zenithst.core.classification.service.ZappClassificationService;
import com.zenithst.core.classification.vo.ZappAdditoryClassification;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.classification.vo.ZappClassificationPar;
import com.zenithst.core.classification.vo.ZappClassificationRes;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.debug.ZappDebug;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.service.ZappCommonService;
import com.zenithst.core.common.vo.ZappCommon;
import com.zenithst.core.content.api.ZappContentMgtService;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.content.vo.ZappKeywordExtend;
import com.zenithst.core.content.vo.ZappKeywordObject;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupPar;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage classification info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappClassificationMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappClassificationMgtService")
public class ZappClassificationMgtServiceImpl extends ZappService implements ZappClassificationMgtService {

	/*
	* [Service]
	*/

	/* Classification */
	@Autowired
	private ZappClassificationService classificationService;
	
	/* Access control info. */
	@Autowired
	private ZappAclMgtService aclService;	
	
	/* Keyword */
	@Autowired
	private ZappContentMgtService contentService;	
	
	/* Organization */
	@Autowired
	private ZappOrganMgtService organService;	
	
	/* Log */
	@Autowired
	private ZappLogMgtService logService;	
	
	
	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Common */
	@Autowired
	private ZappCommonService commonService;
	
	/*
	* [Binder]
	*/

	/* Classification */
	@Autowired
	private ZappClassificationBinder utilBinder;
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappClassificationMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappClassificationMgtService] Service Destroy ");
	}	
	
	/*
	 *  [New] 
	 */
	
	
	@SuppressWarnings("unchecked")
	public ZstFwResult addObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* [ Validation ]
		 * 
		 */
		pObjRes = validParams(pObjAuth, pObj, pObjRes, ZappConts.ACTION.ADD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObj);	
		
		/* [Preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* [Multiple registration] 
		 * 
		 */
		if(pObj instanceof List) {
			pObjRes = classificationService.cMultiRows(pObjAuth, pObj, pObjRes);
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				List<Object> objs = (List<Object>) pObj;
				for(Object obj : objs) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, obj);
				}
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_ADD.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
		} 
		else {
			
			/* [ Validation ]
			 * 
			 */
			pObjRes = validParams(pObjAuth, pObj, pObjRes, ZappConts.ACTION.ADD);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Classification */
			if(pObj instanceof ZappClassification) {}
			
			pObjRes = classificationService.cSingleRow(pObjAuth, pObj, pObjRes);
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_ADD.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
		}
		
		return pObjRes;

	}
	
	/*
	 *  [Edit] 
	 */
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObj) || utilBinder.isEmpty(pObj)) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObj instanceof ZappClassification) {
			
			// 
			
		}
		
		pObjRes = classificationService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}	
	
	
	/**
	 * [Merge]
	 */
	
	public ZstFwResult mergeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObj) || utilBinder.isEmpty(pObj)) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[mergeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObj instanceof ZappClassification) {}
		
		pObjRes = classificationService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}	
	
	/**
	 * [Delete]
	 */

	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = classificationService.dSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = classificationService.dMultiRows(pObjAuth, pObjw, pObjRes);
		}
		
		return pObjRes;
		
	}
	
	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
		
	}	

	/**
	 *  [Inquire]
	 */
	
	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {
			ZappClassification pZappClassification  = (ZappClassification) pObjw;
			logger.info("[selectObject] codeid = " + pZappClassification.getClassid());
		}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = classificationService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = classificationService.rMultiRows(pObjAuth, pObjw, pObjRes);
		}
		
		return pObjRes;
	}

	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}

	/**
	 * [Exist or not]
	 */
	
	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}

	/**
	 * [Counting]
	 */
	
	
	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {}
		
		pObjRes = classificationService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}	


	/* ********************************************************************************************* */
	
	/**
	 * Register new classification info.
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addClass(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String CLASSID = BLANK;										// Classification ID
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Classification */
		if(pObjw instanceof ZappClassificationPar) {
			
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;

			// Set upper ID or check
			if(ZstFwValidatorUtils.valid(pvo.getUpid()) == false) {
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
					pvo.setUpid(pObjAuth.getObjCompanyid());
				}
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)) {
					return ZappFinalizing.finalising("ERR_MIS_UPID", "[addClass] " + messageService.getMessage("ERR_MIS_UPID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)) {
					return ZappFinalizing.finalising("ERR_MIS_UPID", "[addClass] " + messageService.getMessage("ERR_MIS_UPID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
					pvo.setUpid(pObjAuth.getSessDeptUser().getUserid());
				}
			}
			// Set holder ID
			if(ZstFwValidatorUtils.valid(pvo.getHolderid()) == false) {
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)) {
					pvo.setHolderid(pObjAuth.getSessDeptUser().getDeptuserid());
				}
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
					pvo.setHolderid(pObjAuth.getSessDeptUser().getUserid());
				}
			}
			// Set affiliation ID
			if(ZstFwValidatorUtils.valid(pvo.getAffiliationid()) == false) {
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)) {
					pObjRes = classificationService.rMultiAffiliationRows(pObjAuth, new ZappClassification(pvo.getUpid()), pObjRes);
					@SuppressWarnings("unchecked")
					List<ZappClassification> rZappClassification = (List<ZappClassification>) pObjRes.getResObj();
					if(rZappClassification != null) {
						for(ZappClassification vo : rZappClassification) {
							pvo.setAffiliationid(vo.getClassid());
						}
					} 
				}
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)) {
					ZappClassification pvo_up = (ZappClassification) classificationService.rSingleRow(pObjAuth, new ZappClassification(pvo.getUpid()));
					if(pvo_up == null) { // Top
						pvo.setAffiliationid(pvo.getUpid());
					} else {
						pvo.setAffiliationid(pvo_up.getAffiliationid());
					}
				}
			}
			// Set company ID
			pvo.setCompanyid(pObjAuth.getObjCompanyid());
			BeanUtils.copyProperties(pvo, pObjw);
		}
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjw, pObjRes, ZappConts.ACTION.ADD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjw);
		
		/* Preferences */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* Classification */
		if(pObjw instanceof ZappClassificationPar) {
			
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;
			
			// Checking access control values
			pObjRes = canExec(pObjAuth, pObjw, ZappConts.ACTION.ADD, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Register classification info.
			ZappClassification pvo_insert = new ZappClassification();
			BeanUtils.copyProperties(pvo, pvo_insert);
			pvo_insert.setCpath(classificationService.rClassPathUp(pObjAuth, pvo.getUpid(), false) + pvo.getName());	// Class Path
			pObjRes = classificationService.cSingleRow(pObjAuth, pvo_insert, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_CLASS", "[addClass] " + messageService.getMessage("ERR_C_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pvo.setClassid((String) pObjRes.getResObj());
			CLASSID = pvo.getClassid();
			
			// Register additional classification info.
			if(pvo.getZappAdditoryClassification() != null) {
				pvo.getZappAdditoryClassification().setClassid(pvo.getClassid());
				pObjRes = classificationService.cSingleRow(pObjAuth, pvo.getZappAdditoryClassification(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_ADDCLASS", "[addClass] " + messageService.getMessage("ERR_C_ADDCLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			// Folder
			if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER.type)
					|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)
					|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)) {
				
				// Classification ACL
				for(int IDX = ZERO; IDX < pvo.getZappClassAcls().size() ; IDX++) {
					pvo.getZappClassAcls().get(IDX).setClassid(pvo.getClassid());	// Classification ID
				}
				pObjRes = aclService.addObject(pObjAuth, pvo.getZappClassAcls(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_CLASSACL", "[addClass] " + messageService.getMessage("ERR_C_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Basic content ACL
				for(int IDX = ZERO; IDX < pvo.getZappContentAcls().size() ; IDX++) {
					pvo.getZappContentAcls().get(IDX).setContentid(pvo.getClassid());						// Classification ID
					pvo.getZappContentAcls().get(IDX).setContenttype(ZappConts.TYPES.CONTENT_NODE.type);	// Folder
				}
				pObjRes = aclService.addObject(pObjAuth, pvo.getZappContentAcls(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_CONTENTACL", "[addClass] " + messageService.getMessage("ERR_C_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
			}
			
			// Keyword 
			if(pvo.getZappKeywordObjects() != null) {
				if(pvo.getZappKeywordObjects().size() > ZERO) {
					List<ZappKeywordObject> pZappKeywordObjectList = new ArrayList<ZappKeywordObject>();
					for(int IDX = ZERO; IDX < pvo.getZappKeywordObjects().size(); IDX++) {
						ZappKeyword pZappKeyword = new ZappKeyword();
						pZappKeyword.setIsactive(YES);
						pZappKeyword.setKword(pvo.getZappKeywordObjects().get(IDX).getKword());
						pZappKeyword.setKwordid(ZappKey.getPk(pZappKeyword));
						pObjRes = contentService.addObjectExist(pObjAuth, pZappKeyword, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_C_KEYWORD", "[addClass] " + messageService.getMessage("ERR_C_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						
						ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
						pZappKeywordObject.setKobjid(pvo.getClassid());							// Classification ID
						pZappKeywordObject.setKobjtype(ZappConts.TYPES.CONTENT_NODE.type);		// Target type
						pZappKeywordObject.setKwordid(pZappKeyword.getKwordid());
						pZappKeywordObject.setKwobjid(ZappKey.getPk(pvo.getZappKeywordObjects().get(IDX)));
						pZappKeywordObjectList.add(pZappKeywordObject);
						
					}
					
					pObjRes = contentService.addObject(pObjAuth, pZappKeywordObjectList, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_KEYWORDOBJ", "[addClass] " + messageService.getMessage("ERR_C_KEYWORDOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					
				}
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getClassid());
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, pvo.getName());
			}
		}
		
		// Log
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
							 , ZappConts.LOGS.ACTION_ADD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		pObjRes.setResObj(CLASSID);
		
		return pObjRes;
		
	}
	
	
	/**
	 * Edit
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeClass(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log

		/* Validation */
		pObjRes = validParams(pObjAuth, pObjw, pObjRes, ZappConts.ACTION.CHANGE_PK);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjw);
		
		/* Preferences */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env); // [LOG] Content Logging or not
		
		/* Classification */
		if(pObjw instanceof ZappClassificationPar) {
			
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;

			// Get info.
			pObjRes = classificationService.rSingleRow(pObjAuth, new ZappClassification(pvo.getClassid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASS", "[changeClass] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			ZappClassification rvo = (ZappClassification) pObjRes.getResObj();
			if(rvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
				pvo.setUpid(rvo.getUpid());
			}
			pvo.setTypes(rvo.getTypes());
			pvo.setHolderid(rvo.getHolderid());
			
			// Check AC 
			pObjRes = canExec(pObjAuth, pvo, ZappConts.ACTION.CHANGE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Update classification info.
			ZappClassification pvo_update = new ZappClassification();
			BeanUtils.copyProperties(pvo, pvo_update);
			pvo_update.setCompanyid(null);
			pvo_update.setCode(null);
			pvo_update.setTypes(null);
			pvo_update.setUpid(null);
			pvo_update.setAffiliationid(null);
			pvo_update.setPriority(pvo.getPriority());
			pvo_update.setCpath(classificationService.rClassPathUp(pObjAuth, rvo.getUpid(), false) + (ZstFwValidatorUtils.valid(pvo.getName()) == true ? pvo.getName() : rvo.getName()));	// Class Path
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CLASS", "[changeClass] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Classification editing only */
			if(ZstFwValidatorUtils.valid(pObjAuth.getObjLevel())) {
				if(pObjAuth.getObjLevel().equals(ZappConts.ACTION.CHANGE_PK.name())) {
					
					// Log
					if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
						LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
						LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
						LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getClassid());
						LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, pvo.getName());
						LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
						pObjRes = leaveLog(pObjAuth
								         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
										 , ZappConts.LOGS.ACTION_CHANGE.log
								         , LOGMAP
								         , PROCTIME
								         , pObjRes);
					}					
					//return pObjRes; jwjang: What's this? why return here
				}
			}
			
			// Additional
			if(pvo.getZappAdditoryClassification() != null) {
				pvo.getZappAdditoryClassification().setClassid(pvo.getClassid());
				pObjRes = changeObject(pObjAuth, pvo.getZappAdditoryClassification(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_ADDCLASS", "[changeClass] " + messageService.getMessage("ERR_E_ADDCLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			// Folders (Company, Department, Cooperation)
			if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
					|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)
					|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)) {
				
				// Classification access control info.
				if(pvo.getZappClassAcls() != null ) {
					logger.debug("====== pvo.getZappClassAcls() != null");
					List<ZappClassAcl> CA_ADD = new ArrayList<ZappClassAcl>();
					List<ZappClassAcl> CA_CHANGE = new ArrayList<ZappClassAcl>();
					List<ZappClassAcl> CA_DISCARD = new ArrayList<ZappClassAcl>();
					logger.debug("====== pvo.getZappClassAcls size: " + pvo.getZappClassAcls().size());
					for(ZappClassAcl vo : pvo.getZappClassAcls()) {
						if(vo.getObjAction().equals(ZappConts.ACTION.ADD.name())) { CA_ADD.add(vo); }
						if(vo.getObjAction().equals(ZappConts.ACTION.CHANGE.name())) { CA_CHANGE.add(vo); }
						if(vo.getObjAction().equals(ZappConts.ACTION.DISCARD.name())) { CA_DISCARD.add(vo); }
					}
					
					// Add
					for(int IDX = ZERO; IDX < CA_ADD.size(); IDX++) {
						CA_ADD.get(IDX).setClassid(pvo.getClassid());
						CA_ADD.get(IDX).setAclid(ZappKey.getPk(CA_ADD.get(IDX)));
					}
					pObjRes = aclService.addObject(pObjAuth, CA_ADD, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_CLASSACL", "[changeClass] " + messageService.getMessage("ERR_C_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}	
					
					// Edit
					logger.debug("====== CA_CHANGE.size(): " + CA_CHANGE.size());
					for(int IDX = ZERO; IDX < CA_CHANGE.size(); IDX++) {
						pObjRes = aclService.changeObject(pObjAuth, CA_CHANGE.get(IDX), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_CLASSACL", "[changeClass] " + messageService.getMessage("ERR_E_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}	
					}
					
					// Delete
					for(int IDX = ZERO; IDX < CA_DISCARD.size(); IDX++) {
						pObjRes = aclService.deleteObject(pObjAuth, CA_DISCARD.get(IDX), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_CLASSACL", "[changeClass] " + messageService.getMessage("ERR_D_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}	
					}					
					
				}
				
				// Content access control info.
				if(pvo.getZappContentAcls() != null ) {
					logger.debug("====== pvo.getZappContentAcls() != null");
					List<ZappContentAcl> CA_ADD = new ArrayList<ZappContentAcl>();
					List<ZappContentAcl> CA_CHANGE = new ArrayList<ZappContentAcl>();
					List<ZappContentAcl> CA_DISCARD = new ArrayList<ZappContentAcl>();
					for(ZappContentAcl vo : pvo.getZappContentAcls()) {
						if(vo.getObjAction().equals(ZappConts.ACTION.ADD.name())) { CA_ADD.add(vo); }
						if(vo.getObjAction().equals(ZappConts.ACTION.CHANGE.name())) { CA_CHANGE.add(vo); }
						if(vo.getObjAction().equals(ZappConts.ACTION.DISCARD.name())) { CA_DISCARD.add(vo); }
					}
					
					// Add
					for(int IDX = ZERO; IDX < CA_ADD.size(); IDX++) {
						CA_ADD.get(IDX).setContentid(pvo.getClassid());
						CA_ADD.get(IDX).setContenttype(ZappConts.TYPES.CONTENT_NODE.type);
						CA_ADD.get(IDX).setAclid(ZappKey.getPk(CA_ADD.get(IDX)));
					}
					pObjRes = aclService.addObject(pObjAuth, CA_ADD, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_CONTENTACL", "[changeClass] " + messageService.getMessage("ERR_C_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}	
					
					// Edit
					for(int IDX = ZERO; IDX < CA_CHANGE.size(); IDX++) {
						pObjRes = aclService.changeObject(pObjAuth, CA_CHANGE.get(IDX), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_CONTENTACL", "[changeClass] " + messageService.getMessage("ERR_E_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}	
					}
					
					// Delete
					for(int IDX = ZERO; IDX < CA_DISCARD.size(); IDX++) {
						pObjRes = aclService.deleteObject(pObjAuth, CA_DISCARD.get(IDX), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_CONTENTACL", "[changeClass] " + messageService.getMessage("ERR_D_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}	
					}					
					
				}
				
			}
			
			// Keyword
			if(pvo.getZappKeywordObjects() != null) {
				if(pvo.getZappKeywordObjects().size() > ZERO) {
					List<ZappKeywordExtend> ADDs = new ArrayList<ZappKeywordExtend>();
					List<ZappKeywordObject> ADDObjects = new ArrayList<ZappKeywordObject>();
					List<ZappKeywordExtend> DISCARDs = new ArrayList<ZappKeywordExtend>();
					for(ZappKeywordExtend vo : pvo.getZappKeywordObjects()) {
						if(vo.getObjAction().equals(ZappConts.ACTION.ADD.name())) {
							vo.setIsactive(YES);
							vo.setKwordid(ZappKey.getPk(vo));
							vo.setKobjid(pvo.getClassid());							// Classification ID
							vo.setKobjtype(ZappConts.TYPES.CONTENT_NODE.type);		// Target type
							vo.setKwordid(vo.getKwordid());
							vo.setKwobjid(ZappKey.getPk(vo));
							ADDs.add(vo);
							
							ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
							BeanUtils.copyProperties(vo, pZappKeywordObject);
							ADDObjects.add(pZappKeywordObject);
							
						}
						if(vo.getObjAction().equals(ZappConts.ACTION.DISCARD.name())) {
							DISCARDs.add(vo);
						}
					}
					
					// Add
					if(ADDs.size() > ZERO) {
						for(ZappKeywordExtend vo : ADDs) {
							ZappKeyword pZappKeyword = new ZappKeyword();
							BeanUtils.copyProperties(vo, pZappKeyword);
							pObjRes = contentService.addObjectExist(pObjAuth, pZappKeyword, pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_C_KEYWORD", "[changeClass] " + messageService.getMessage("ERR_C_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
						}
						pObjRes = contentService.addObject(pObjAuth, ADDObjects, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_C_KEYWORDOBJ", "[changeClass] " + messageService.getMessage("ERR_C_KEYWORDOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
					
					
					// Delete
					if(DISCARDs.size() > ZERO) {
						ZappKeywordObject pZappKeywordObject_Filter = new ZappKeywordObject();
						pZappKeywordObject_Filter.setKwobjid(Operators.IN.operator);
						ZappKeywordObject pZappKeywordObject_Where = new ZappKeywordObject();
						StringBuffer sbids = new StringBuffer();
						for(ZappKeywordExtend vo : DISCARDs) {
							sbids.append(vo.getKwobjid() + DIVIDER);
						}
						pZappKeywordObject_Where.setKwobjid(sbids.toString());
						pObjRes = contentService.deleteObject(pObjAuth, pZappKeywordObject_Filter, pZappKeywordObject_Where, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_KEYWORDOBJ", "[changeClass] " + messageService.getMessage("ERR_D_KEYWORDOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
					
				}
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
			}
		}
		
		// Log
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			pObjRes = leaveLog(pObjAuth
					         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public ZstFwResult selectClass(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjw, pObjRes, ZappConts.ACTION.VIEW);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjw);
		
		/* Preferences */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* Classification */
		if(pObjw instanceof ZappClassificationPar) {
			
			ZappClassificationRes rZappClassificationRes = new ZappClassificationRes();
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;
			
			// Get info.
			pObjRes = selectObject(pObjAuth, pObjw, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASS", "[selectClass] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappClassificationRes.setZappClassification((ZappClassification) pObjRes.getResObj());
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, rZappClassificationRes.getZappClassification().getClassid());
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappClassificationRes.getZappClassification().getName());
			
			// Additional classification information 조회
			pObjRes = selectObject(pObjAuth, new ZappAdditoryClassification(pvo.getClassid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_ADDCLASS", "[selectClass] " + messageService.getMessage("ERR_R_ADDCLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappClassificationRes.setZappAdditoryClassification((ZappAdditoryClassification) pObjRes.getResObj());
			
			// Check access control
			if(!rZappClassificationRes.getZappClassification().getTypes().equals(ZappConts.TYPES.CLASS_CLASS.type)
					&& !rZappClassificationRes.getZappClassification().getTypes().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
				// Classification
				ZappClassAcl pZappClassAcl = new ZappClassAcl();
				pZappClassAcl.setClassid(pvo.getClassid());
				pObjRes = aclService.selectExtendObject(pObjAuth, null, pZappClassAcl, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_CLASSACL", "[selectClass] " + messageService.getMessage("ERR_R_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				rZappClassificationRes.setZappClassAcls((List<ZappAclExtend>) pObjRes.getResObj());
				
				// Content
				ZappContentAcl pZappContentAcl = new ZappContentAcl();
				pZappContentAcl.setContentid(pvo.getClassid());
				pZappContentAcl.setContenttype(ZappConts.TYPES.CONTENT_NODE.type);
				pObjRes = aclService.selectExtendObject(pObjAuth, null, pZappContentAcl, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[selectClass] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				rZappClassificationRes.setZappContentAcls((List<ZappAclExtend>) pObjRes.getResObj());
				
				// All
				Map<String, List<ZappAclExtend>> mapUnion = new HashMap<String, List<ZappAclExtend>>();
				for(ZappAclExtend clsvo : rZappClassificationRes.getZappClassAcls()) {
					for(ZappAclExtend convo : rZappClassificationRes.getZappContentAcls()) {
						String clskey = clsvo.getAclobjid() + clsvo.getAclobjtype();
						String conkey = convo.getAclobjid() + convo.getAclobjtype();
						if(clskey.equals(conkey)) {
							List<ZappAclExtend> tmpList = null;
							if(mapUnion.containsKey(clskey) == true) {
								tmpList = (List<ZappAclExtend>) mapUnion.get(clskey);
								tmpList.add(convo);
							} else {
								tmpList = new ArrayList<ZappAclExtend>();
								tmpList.add(clsvo);
								tmpList.add(convo);
							}
							mapUnion.put(conkey, tmpList);
						}
					}
				}
				rZappClassificationRes.setZappUnionAcls(mapUnion);
			}
			
			// Holder Name 
			if(ZstFwValidatorUtils.valid(rZappClassificationRes.getZappClassification().getHolderid()) == true) {
				ZappDeptUser pZappDeptUser = new ZappDeptUser(rZappClassificationRes.getZappClassification().getHolderid());
				pObjRes = organService.selectObjectExtend(pObjAuth, pZappDeptUser, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[selectClass] " + messageService.getMessage("ERR_R_DEPTUSER",   pObjAuth.getObjlang()), BLANK);
				}			
				ZappDeptUserExtend rZappDeptUserExtend = (ZappDeptUserExtend) pObjRes.getResObj();
				if(rZappDeptUserExtend != null) {
					rZappClassificationRes.setHoldername(rZappDeptUserExtend.getZappDept().getName() + DIVIDER + rZappDeptUserExtend.getZappUser().getName());
				}
			}
			
			pObjRes.setResObj(rZappClassificationRes);
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pvo);
			}
		}
		
		// Log
//		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
//			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
//			pObjRes = leaveLog(pObjAuth
//					         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
//							 , ZappConts.LOGS.ACTION_VIEW.log
//					         , LOGMAP
//					         , PROCTIME
//					         , pObjRes);
//		}
		
		return pObjRes;
		
	}

	public ZstFwResult selectObjectDown(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjAuth.setObjSkipAcl(false);
		
		/* Classification */
		boolean isMngMode = false;
		String TYPES = BLANK;
		if(pObjw instanceof ZappClassification) {
			ZappClassification pvo = (ZappClassification) pObjw;
			isMngMode = pvo.getObjIsMngMode();
			TYPES = pvo.getTypes();
		}
		
		/* [Check access control info.]
		 * 
		 */
//		if(aclService.isCompanyManager(pObjAuth) == true && isMngMode == true) {
		pObjAuth.setObjSkipAcl(true);
//		}
		
		/* [Normal mode]
		 * 
		 */
		ZappClassification pZappClassification = null;
		if(isMngMode == false) {
			pZappClassification = new ZappClassification();
			pZappClassification.setIsactive(YES);
		}
		
		pObjRes = classificationService.rMultiRowsDown(pObjAuth, pObjf, pObjw, pZappClassification, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[selectObjectDown] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		@SuppressWarnings("unchecked")
		List<ZappClassification> zappClassificationList = (List<ZappClassification>) pObjRes.getResObj();
		if(zappClassificationList == null) {
			return pObjRes;
		}
		if(zappClassificationList.size() == ZERO) {
			return pObjRes;
		}
		
		/* [Inquiry access info.]
		 * 
		 */
		if(!TYPES.equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
				&& !TYPES.equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			/* Check access free member or not */
			if(aclService.isAccessFreeContent(pObjAuth) == false) {
				pObjRes = aclService.optimizeObject(pObjAuth, zappClassificationList, pObjRes);
			} else {
				List<ZappClassificationRes> objResClassList = new ArrayList<ZappClassificationRes>();
				for(int IDX = ZERO; IDX < zappClassificationList.size(); IDX++) {
					ZappClassificationRes pZappClassificationRes = new ZappClassificationRes();
					pZappClassificationRes.setZappClassification(zappClassificationList.get(IDX));
					pZappClassificationRes.setZappClassAcl(new ZappClassAcl(ZappConts.ACLS.CLASS_READ_ADD.acl));
					objResClassList.add(pZappClassificationRes);
				}
				pObjRes.setResObj(objResClassList);
			}
		} else {
			
			int ACLS = ZERO;
			if(TYPES.equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
				if(aclService.isCompanyManager(pObjAuth) == true) {
					ACLS = ZappConts.ACLS.CLASS_READ_ADD.acl;
				} else {
					ACLS = ZappConts.ACLS.CLASS_READONLY.acl;
				}
			}
			if(TYPES.equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
				ACLS = ZappConts.ACLS.CLASS_READ_ADD.acl;
			}
			
			List<ZappClassificationRes> objResClassList = new ArrayList<ZappClassificationRes>();
			for(int IDX = ZERO; IDX < zappClassificationList.size(); IDX++) {
				ZappClassificationRes pZappClassificationRes = new ZappClassificationRes();
				pZappClassificationRes.setZappClassification(zappClassificationList.get(IDX));
				pZappClassificationRes.setZappClassAcl(new ZappClassAcl(ACLS));
				objResClassList.add(pZappClassificationRes);
			}
			pObjRes.setResObj(objResClassList);
		}
		
		
		return pObjRes;
		
	}

	public ZstFwResult selectOrganDown(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		// Filter
		ZappDept pZappDept_Filter = null;
		if(pObjf instanceof ZappClassificationPar) {
		
		}		
		
		// Values
		if(pObjw instanceof ZappClassificationPar) {
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;

			if(!pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)) {	// Belonged cooperation groups
				
				ZappDept pZappDept_Value = new ZappDept();
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) { 			// All department
					if(ZstFwValidatorUtils.valid(pvo.getUpid()) == false) {
						pZappDept_Value.setUpid(pObjAuth.getObjCompanyid());
					} else {
						pZappDept_Value.setUpid(pvo.getUpid());
					}
				}
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)) {		// Belonged dept. and lower depts.
					pZappDept_Value.setDeptid(pObjAuth.getObjDeptid());	
				}
	
				pObjRes = organService.selectObjectDown(pObjAuth, pZappDept_Filter, pZappDept_Value, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_DEPT", "[selectOrganDown] " + messageService.getMessage("ERR_R_DEPT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				@SuppressWarnings("unchecked")
				List<ZappDept> rZappDeptList = (List<ZappDept>) pObjRes.getResObj();
				if(rZappDeptList == null) {
					pObjRes.setResObj(new ArrayList<ZappClassification>());
				} else {
					List<ZappClassification> rZappClsList = new ArrayList<ZappClassification>();
					for(ZappDept vo : rZappDeptList) {
						ZappClassification rvo = new ZappClassification();
						BeanUtils.copyProperties(vo, rvo);
						rvo.setClassid(vo.getDeptid());
						rvo.setTypes(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type);	// Department
						rvo.setObjAction(NO);
						rvo.setObjIncLower(vo.getObjIncLower());  						// Including folders
						rZappClsList.add(rvo);
					}
					pObjRes.setResObj(rZappClsList);
				}
			}
			else {
				ZappGroupPar pZappGroupPar = new ZappGroupPar();
				ZappGroup pZappGroup = new ZappGroup();
				pZappGroup.setCompanyid(pObjAuth.getObjCompanyid());
				pZappGroup.setTypes(ZappConts.TYPES.GROUPTYPE_COLLABORATION.type);
				pZappGroupPar.setZappGroup(pZappGroup);
				/* Check access free member or not */
				if(aclService.isAccessFreeContent(pObjAuth) == false) {
					pObjRes = organService.selectGroupByUser(pObjAuth, pZappGroupPar, pObjRes);
				} else {
					pObjRes = organService.selectObject(pObjAuth, pZappGroup, pObjRes);
				}
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_GROUP", "[selectOrganDown] " + messageService.getMessage("ERR_R_GROUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				@SuppressWarnings("unchecked")
				List<ZappGroup> rZappGroupList = (List<ZappGroup>) pObjRes.getResObj();
				if(rZappGroupList == null) {
					pObjRes.setResObj(new ArrayList<ZappGroup>());
				} else {
					List<ZappClassification> rZappClsList = new ArrayList<ZappClassification>();
					for(ZappGroup vo : rZappGroupList) {
						ZappClassification rvo = new ZappClassification();
						BeanUtils.copyProperties(vo, rvo);
						rvo.setClassid(vo.getGroupid());
						rvo.setTypes(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type);	// Group
						rvo.setObjAction(NO);						
						rvo.setObjIncLower(vo.getObjIncLower());  						// Including folders
						rZappClsList.add(rvo);
					}
					pObjRes.setResObj(rZappClsList);
				}		
			}
		} else {
			logger.debug("Invalid instance of pObjw");
		}
		return pObjRes;
	}

	
	public ZstFwResult selectObjectUp(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Classification */
		boolean isMngMode = false;
		if(pObjw instanceof ZappClassification) {
			ZappClassification pvo = (ZappClassification) pObjw;
			isMngMode = pvo.getObjIsMngMode();
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == true && isMngMode == true) {
			pObjAuth.setObjSkipAcl(true);
		}
		
		/* [Normal mode]
		 * 
		 */
		ZappClassification pZappClassification = null;
		if(isMngMode == false) {
			pZappClassification = new ZappClassification();
			pZappClassification.setIsactive(YES);
		}
		
		pObjRes = classificationService.rMultiRowsUp(pObjAuth, pObjf, pObjw, pZappClassification, pObjRes);
		
		return pObjRes;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult relocateClass(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Classification */
		if(pObjw instanceof ZappClassificationPar) {
			
			// Preferences
			ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
			
			// Get source info.
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;
			ZappClassification pvo_single = new ZappClassification();
			BeanUtils.copyProperties(pvo, pvo_single);
			ZappClassification rvo = (ZappClassification) classificationService.rSingleRow(pObjAuth, pvo_single);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateObject] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getUpid().equals(pvo.getUpid())) {
				return ZappFinalizing.finalising("ERR_DUP_UPID", "[relocateObject] " + messageService.getMessage("ERR_DUP_UPID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get target info.
//			ZappClassification pvo_up = new ZappClassification();
//			pvo_up.setClassid(pvo_single.getUpid());
//			ZappClassification rvo_up = (ZappClassification) classificationService.rSingleRow(pObjAuth, pvo_up);
//			if(rvo_up == null) {
//				if(rvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)) {
//				
//				}
//				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateObject] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
//			if(!rvo.getTypes().equals(rvo_up.getTypes())) {
//				return ZappFinalizing.finalising("ERR_NIDENT_TYPE", "[relocateObject] " + messageService.getMessage("ERR_NIDENT_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
			
			// Check access control info.
			pvo.setTypes(rvo.getTypes());
			pvo.setHolderid(rvo.getHolderid());
			
			pObjRes = canExec(pObjAuth, pvo, ZappConts.ACTION.RELOCATE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}			
			
			// Get next priority
			ZappClassification pvo_up = new ZappClassification();
			pvo_up.setUpid(pvo.getUpid());
			pObjRes = classificationService.rNextPriority(pObjAuth, null, pvo_up, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_ORDER", "[relocateObject] " + messageService.getMessage("ERR_R_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pvo.setPriority((Integer) pObjRes.getResObj());
			
			// Update
			ZappClassification pvo_update = new ZappClassification();
			pvo_update.setClassid(pvo.getClassid());
			pvo_update.setUpid(pvo.getUpid());
			pvo_update.setPriority(pvo.getPriority());
			pvo_update.setCpath(classificationService.rClassPathUp(pObjAuth, pvo.getUpid(), false) + rvo.getName());	// Class Path
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CLASS", "[relocateObject] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, rvo.getClassid());
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rvo.getName());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_RELOCATE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
		}

		pObjRes.setResObj(BLANK);
		
		return pObjRes;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult replicateClass(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {
			
			// Preferences
			ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
			
			// Get source info.
			ZappClassification pvo = (ZappClassification) pObjw;
			ZappClassification pvo_single = new ZappClassification();
			BeanUtils.copyProperties(pvo, pvo_single);
			ZappClassification rvo = (ZappClassification) classificationService.rSingleRow(pObjAuth, pvo_single);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[replicateObject] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getUpid().equals(pvo.getUpid())) {
				return ZappFinalizing.finalising("ERR_DUP_UPID", "[replicateObject] " + messageService.getMessage("ERR_DUP_UPID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get upper info.
			ZappClassification pvo_up = new ZappClassification();
			pvo_up.setClassid(pvo_single.getUpid());
			ZappClassification rvo_up = (ZappClassification) classificationService.rSingleRow(pObjAuth, pvo_up);
			if(rvo_up == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateObject] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getUpid().equals(pvo.getUpid())) {
				return ZappFinalizing.finalising("ERR_DUP_UPID", "[relocateObject] " + messageService.getMessage("ERR_DUP_UPID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(!rvo.getTypes().equals(rvo_up.getTypes())) {
				return ZappFinalizing.finalising("ERR_NIDENT_TYPE", "[relocateObject] " + messageService.getMessage("ERR_NIDENT_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			if(!rvo.getTypes().equals(rvo_up.getTypes())) {
				return ZappFinalizing.finalising("ERR_NIDENT_TYPE", "[relocateObject] " + messageService.getMessage("ERR_NIDENT_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Check access control info.
			pvo.setTypes(rvo.getTypes());
			pObjRes = canExec(pObjAuth, pvo, ZappConts.ACTION.REPLICATE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}	
			
			// Get next priority
			pvo_up = new ZappClassification();
			pvo_up.setUpid(pvo.getUpid());
			pObjRes = classificationService.rNextPriority(pObjAuth, null, pvo_up, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_ORDER", "[replicateObject] " + messageService.getMessage("ERR_R_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pvo.setPriority((Integer) pObjRes.getResObj());
			pvo.setCpath(classificationService.rClassPathUp(pObjAuth, pvo.getUpid(), false) + rvo.getName());	// Class Path

			// Update
			pObjRes = changeObject(pObjAuth, pvo, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CLASS", "[replicateObject] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, rvo.getClassid());
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rvo.getName());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_REPLICATE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
		}

		pObjRes.setResObj(BLANK);
		
		return pObjRes;
		
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult reorderClass(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {
			
			// Preferences
			ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
						
			// Check access control info.
			pObjRes = canExec(pObjAuth, pObjw, ZappConts.ACTION.REARRANGE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Get info.
			ZappClassification pvo = (ZappClassification) pObjw;
			ZappClassification rvo = (ZappClassification) classificationService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[reorderClass] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getPriority() == pvo.getPriority()) {
				return ZappFinalizing.finalising("ERR_DUP_ORDER", "[reorderClass] " + messageService.getMessage("ERR_DUP_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Change order
			ZappClassification pvos = new ZappClassification();
			pvos.setCompanyid(rvo.getCompanyid());
			pvos.setUpid(rvo.getUpid());
			ZappClassification pvoe = new ZappClassification();
			if(rvo.getPriority() > pvo.getPriority()) {	// When the order moves up
				pvos.setPriority(pvo.getPriority());
				pvoe.setPriority(rvo.getPriority() - ONE);
				pObjRes = classificationService.upwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(rvo.getPriority() < pvo.getPriority()) {	// When the order moves down
				pvos.setPriority(rvo.getPriority() + ONE);
				pvoe.setPriority(pvo.getPriority());
				pObjRes = classificationService.downwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_ORDER", "[reorderClass] " + messageService.getMessage("ERR_E_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			pObjRes = changeObject(pObjAuth, new ZappClassification(pvo.getClassid(), pvo.getPriority()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CLASS", "[reorderClass] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, rvo.getClassid());
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rvo.getName());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_REORDER.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
		}

		pObjRes.setResObj(BLANK);
		
		return pObjRes;
		
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult disableClass(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {
			
			// Preferences
			ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
						
			//  Check access control info.
			pObjRes = canExec(pObjAuth, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Get source info.
			ZappClassification pvo = (ZappClassification) pObjw;
			ZappClassification rvo = (ZappClassification) classificationService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[disableClass] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			if(rvo.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[disableClass] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* [Use or not]
			 * 
			 */
			pObjRes = checkUsingInOtherObject(pObjAuth, "disableClass", pvo, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}	
			
			/* [Check lower folders]
			 * 
			 */
			ZappClassification pvo_check = new ZappClassification();
			pvo_check.setUpid(pvo.getClassid());
			pObjRes = existObject(pObjAuth, pvo_check, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}	
			boolean EXIST_LOWER = (Boolean) pObjRes.getResObj();
			if(EXIST_LOWER == true) {
				return ZappFinalizing.finalising("EXIST_LOWER_FOLDER", "[disableClass] " + messageService.getMessage("EXIST_LOWER_FOLDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			ZappClassification pvo_update = new ZappClassification(pvo.getClassid()); 
			pvo_update.setIsactive(NO);
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CLASS", "[disableClass] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Disable lower classifications
//			if(ZstFwValidatorUtils.valid(pvo.getObjIncLower())) {
//				if(pvo.getObjIncLower().equals(YES)) {
//					ZappClassification pZappClassification_Down = new ZappClassification(pvo.getClassid());
//					pZappClassification_Down.setTypes(rvo.getTypes());
//					pZappClassification_Down.setObjSkipAcl(true);
//					pObjRes = selectObjectDown(pObjAuth, null, pZappClassification_Down, pObjRes);
//					if(ZappFinalizing.isSuccess(pObjRes) == false) {
//						return ZappFinalizing.finalising("ERR_R_CLASS", "[disableClass] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//					}
//					List<ZappClassification> rZappClassDownList = (List<ZappClassification>) pObjRes.getResObj();
//					StringBuffer sbids = new StringBuffer();
//					if(rZappClassDownList != null) {
//						for(ZappClassification vo : rZappClassDownList) {
//							if(vo.getClassid().equals(pvo.getClassid())) { continue; }
//							sbids.append(vo.getClassid() + DIVIDER);
//						}
//						if(sbids.length() > ZERO) {
//							ZappClassification pvo_set = new ZappClassification();
//							pvo_set.setIsactive(NO);
//							ZappClassification pvo_filter = new ZappClassification();
//							pvo_filter.setClassid(Operators.IN.operator);
//							ZappClassification pvo_where = new ZappClassification();
//							pvo_where.setClassid(sbids.toString());
//							pObjRes = changeObject(pObjAuth, pvo_set, pvo_filter, pvo_where, pObjRes);
//							if(ZappFinalizing.isSuccess(pObjRes) == false) {
//								return ZappFinalizing.finalising("ERR_E_CLASS", "[disableClass] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//							}
//						}
//					}
//				}
//			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, rvo.getClassid());
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rvo.getName());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_DISABLE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
		}
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult enableClass(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {
			
			// Preferences
			ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
						
			// Check access control info. 
			pObjRes = canExec(pObjAuth, pObjw, ZappConts.ACTION.ENABLE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Get source info.
			ZappClassification pvo = (ZappClassification) pObjw;
			ZappClassification rvo = (ZappClassification) classificationService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[disableClass] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			if(!rvo.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[disableClass] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			ZappClassification pvo_update = new ZappClassification(pvo.getClassid()); 
			pvo_update.setIsactive(YES);
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CLASS", "[disableClass] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Disable lower classifications
			if(ZstFwValidatorUtils.valid(pvo.getObjIncLower())) {
				if(pvo.getObjIncLower().equals(YES)) {
					ZappClassification pZappClassification_Down = new ZappClassification(pvo.getClassid());
					pZappClassification_Down.setTypes(rvo.getTypes());
					pZappClassification_Down.setObjSkipAcl(true);
					pObjRes = selectObjectDown(pObjAuth, null, pZappClassification_Down, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_CLASS", "[disableClass] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					List<ZappClassification> rZappClassDownList = (List<ZappClassification>) pObjRes.getResObj();
					StringBuffer sbids = new StringBuffer();
					if(rZappClassDownList != null) {
						for(ZappClassification vo : rZappClassDownList) {
							if(vo.getClassid().equals(pvo.getClassid())) { continue; }
							sbids.append(vo.getClassid() + DIVIDER);
						}
						if(sbids.length() > ZERO) {
							ZappClassification pvo_set = new ZappClassification();
							pvo_set.setIsactive(YES);
							ZappClassification pvo_filter = new ZappClassification();
							pvo_filter.setClassid(Operators.IN.operator);
							ZappClassification pvo_where = new ZappClassification();
							pvo_where.setClassid(sbids.toString());
							pObjRes = changeObject(pObjAuth, pvo_set, pvo_filter, pvo_where, pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_E_CLASS", "[disableClass] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
						}
					}
				}
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, rvo.getClassid());
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rvo.getName());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_ENABLE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
		}
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
		

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardClass(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* Classification */
		if(pObjw instanceof ZappClassification) {
			
			// Preferences
			ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
						
			/* [Check access control info.]
			 * 
			 */
			pObjRes = canExec(pObjAuth, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
 
			/* [Get upper info.]
			 * 
			 */
			ZappClassification pvo = (ZappClassification) pObjw;
			ZappClassification rvo = (ZappClassification) classificationService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[discardClass] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			if(rvo.getIsactive().equals(YES)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[discardClass] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* [Use or not]
			 * 
			 */
			pObjRes = checkUsingInOtherObject(pObjAuth, "discardClass", pvo, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* [Delete access control info.]
			 * 
			 */
			ZappClassAcl pacl = new ZappClassAcl();
			pacl.setClassid(pvo.getClassid());
			pObjRes = aclService.deleteObject(pObjAuth, pacl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CLASSACL", "[discardClass] " + messageService.getMessage("ERR_D_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			ZappContentAcl paclc = new ZappContentAcl();
			paclc.setContentid(pvo.getClassid());
			paclc.setContenttype(ZappConts.TYPES.CONTENT_NODE.type);
			pObjRes = aclService.deleteObject(pObjAuth, paclc, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CONTENTACL", "[discardClass] " + messageService.getMessage("ERR_D_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* [Delete additional info.]
			 * 
			 */
			pObjRes = deleteObject(pObjAuth, new ZappAdditoryClassification(pvo.getClassid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_ADDCLASS", "[discardClass] " + messageService.getMessage("ERR_D_ADDCLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* [Delete classification]
			 * 
			 */
			pObjRes = deleteObject(pObjAuth, new ZappClassification(pvo.getClassid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CLASS", "[discardClass] " + messageService.getMessage("ERR_D_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* [Delete keyword]
			 * 
			 */
			ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
			pZappKeywordObject.setKobjid(pvo.getClassid());
			pZappKeywordObject.setKobjtype(ZappConts.TYPES.CONTENT_NODE.type);
			pObjRes = contentService.deleteObject(pObjAuth, pZappKeywordObject, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_D_KEYWORD", "[discardClass] " + messageService.getMessage("ERR_D_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, rvo.getClassid());
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rvo.getName());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CLASSIFICATION.log
								 , ZappConts.LOGS.ACTION_DISCARD.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
		}
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}	
	
	public ZstFwResult selectMarkedList(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Classification */
		if(pObjw instanceof ZappClassificationPar) {
			
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;
		
			if(ZstFwValidatorUtils.valid(pvo.getObjRes()) == false) {
				pvo.setObjRes(ZappConts.TYPES.RESULT_LIST.type);
			}
			if(pvo.getObjRes().equals(ZappConts.TYPES.RESULT_COUNT.type)) {		// Counting
				pObjRes = classificationService.rMarkedCount(pObjAuth, pObjw, pObjRes);
			} else if(pvo.getObjRes().equals(ZappConts.TYPES.RESULT_LIST.type)) {	// List
				pObjRes = classificationService.rMarkedList(pObjAuth, pObjw, pObjRes);
			}
		}
		
		return  pObjRes;
	}
	
	/* *********************************************************************************************** */
	
	private ZstFwResult checkClassBeforeChange(ZappAuth pObjAuth, ZappClassification pObj, ZstFwResult pObjRes) {
		
		
		return pObjRes;
	}
	
	private ZstFwResult checkClassBeforeMove(ZappAuth pObjAuth, ZappClassification pObj, ZstFwResult pObjRes) {
		
		
		return pObjRes;
	}
	
	private ZstFwResult checkClassBeforeDelete(ZappAuth pObjAuth, ZappClassification pObj, ZstFwResult pObjRes) {
		
	
		return pObjRes;
	}

	// ### Check access control info. ###
	private ZstFwResult canExec(ZappAuth pObjAuth, Object pObj, ZappConts.ACTION pObjType, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObj instanceof ZappClassificationPar) {
		
			ZappClassificationPar pvo = (ZappClassificationPar) pObj;
			
			switch(pObjType) {
				case ADD:
					/* Company (Company manager) */
					if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
						
						// Inquire preference (Whether general users can register company folders)
						ZappEnv SYS_CAN_REG_COMPANY_FOLDER = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CAN_REG_COMPANY_FOLDER.env);
						if(SYS_CAN_REG_COMPANY_FOLDER == null) {
							SYS_CAN_REG_COMPANY_FOLDER = new ZappEnv(); SYS_CAN_REG_COMPANY_FOLDER.setSetval(NO);
						}
						if(SYS_CAN_REG_COMPANY_FOLDER.getSetval().equals(NO)) {
							// Only company managers can register folders
							if(!pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_COMPANY.type)) {
								return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
						} else {
							// All user can register folders.
						}
					}
					
					/* Department (Dept. manager) */
					if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)) {	
	
						// Inquire preference (Whether dept. users can register dept. folders)
						ZappEnv SYS_CAN_REG_DEPT_FOLDER = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CAN_REG_DEPT_FOLDER.env);
						if(SYS_CAN_REG_DEPT_FOLDER == null) {
							SYS_CAN_REG_DEPT_FOLDER = new ZappEnv(); SYS_CAN_REG_DEPT_FOLDER.setSetval(YES);
						}
						// Inquire preference (Whether parent dept. users can register folders in sub-departments)
						ZappEnv SYS_CAN_REG_FOLDER_IN_LOWER = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CAN_REG_FOLDER_IN_LOWER.env);
						if(SYS_CAN_REG_FOLDER_IN_LOWER == null) {
							SYS_CAN_REG_FOLDER_IN_LOWER = new ZappEnv(); SYS_CAN_REG_FOLDER_IN_LOWER.setSetval(YES);
						}
						
						// Inquiry affiliation dept.
						pObjRes = classificationService.rMultiAffiliationRows(pObjAuth, new ZappClassification(pvo.getUpid()), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						@SuppressWarnings("unchecked")
						List<ZappClassification> rZappClassification = (List<ZappClassification>) pObjRes.getResObj();
						if(rZappClassification != null) {
							for(ZappClassification vo : rZappClassification) {
								pvo.setAffiliationid(vo.getClassid());
							}
						} else {
							return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						
						// Check lower dept.
						if(pObjAuth.getSessAllLowerDepts().contains(pvo.getAffiliationid()) == false) {
							return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
	
						if(SYS_CAN_REG_DEPT_FOLDER.getSetval().equals(NO)) {
							// Only dept. managers can register dept. folders. 
							if(!pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_DEPT.type)) {
								return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
						} else {
							// All dept. users can register folders. 
						}
					}
					
					/* User (User) */
					if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
						ZappClassification pvo_up = null;
						// When this folder is not a top-level one
						if(!pvo.getUpid().equals(pObjAuth.getSessDeptUser().getUserid())) { 
							pvo_up = (ZappClassification) classificationService.rSingleRow(pObjAuth, new ZappClassification(pvo.getUpid()));
							if(pvo_up == null) {
								return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
							
							// Check a holder
							if(!pvo_up.getHolderid().equals(pObjAuth.getSessDeptUser().getUserid())) {
								return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
						}
	
					}
					
					/* Cooperation (Group) */
					if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)) {
						
						String GROUPID = BLANK;
						ZappClassification pvo_up = (ZappClassification) classificationService.rSingleRow(pObjAuth, new ZappClassification(pvo.getUpid()));
						if(pvo_up == null) { // Top
							GROUPID = pvo.getUpid();
						} else {
							GROUPID = pvo_up.getAffiliationid();
						}
							
						// Checks whether the current user belongs to the collaboration group of this folder
						ZappGroupUser pZappGroupUser_Filter = new ZappGroupUser();
						pZappGroupUser_Filter.setGobjid(Operators.IN.operator);
						ZappGroupUser pZappGroupUser_Value = new ZappGroupUser();
						pZappGroupUser_Value.setGroupid(GROUPID);
						pZappGroupUser_Value.setGobjid(pObjAuth.getSessDeptUser().getDeptid() + DIVIDER + pObjAuth.getSessDeptUser().getDeptuserid());	// Department + User
						pObjRes = organService.existObject(pObjAuth, pZappGroupUser_Filter, pZappGroupUser_Value, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						boolean belongs = (Boolean) pObjRes.getResObj();
						if(belongs == false) {
							return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
	
					}
					
				break;
				
				case CHANGE: case DISABLE: case ENABLE: case DISCARD: case RELOCATE: case REPLICATE: case REARRANGE:
					
					/* Company, Department, Cooperation */
					if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
							|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)
							|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type)) {
						if(pObjAuth.getSessAclObjList().contains(pvo.getHolderid()) == false) {
							return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
					
					/* Personal */
					if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
						if(!pvo.getHolderid().equals(pObjAuth.getSessDeptUser().getUserid())) { 
							return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
					
				break;
				
				default:
				break;
			
			}
			
			
			/*
			 * [Classification] (Company manager)
			 */
			if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_CLASS.type)) {
				if(!pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_COMPANY.type)) {
					return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/*
			 * [Contgent type]  (Company manager)
			 */
			if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
				if(!pObjAuth.getSessDeptUser().getUsertype().equals(ZappConts.TYPES.USERTYPE_COMPANY.type)) {
					return ZappFinalizing.finalising("ERR_NO_ACL", "[canExec] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
		}
		
		return pObjRes;
	}
	
	// ### Logging ###
	private ZstFwResult leaveLog(ZappAuth pObjAuth, String pLogType, String pLogAction, Map<String, Object> pLogMap, String pLogTime, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		List<ZappContentLog> pLogObjectList = new ArrayList<ZappContentLog>();
		ZappContentLog pLogObject = new ZappContentLog();
		pLogObject.setLogobjid((String) pLogMap.get(ZappConts.LOGS.ITEM_CONTENT_ID.log));
		pLogObject.setLogtext((String) pLogMap.get(ZappConts.LOGS.ITEM_CONTENT_TITLE.log));
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
								  , Object pObj
								  , ZstFwResult pObjRes
								  , ZappConts.ACTION pAct) {
		
		/* [Authentication Info.]
		 * 
		 */
		if(pObjAuth == null) {
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[validParams] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Input value]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[validParams] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		if(pObj instanceof ZappClassificationPar) {
			
			ZappClassificationPar pVo = (ZappClassificationPar) pObj;
			
			// Classification ID
			switch(pAct) {
				case CHANGE_PK: case CHANGE: case DISCARD: case RELOCATE: case REPLICATE: case VIEW: 
					if(!ZstFwValidatorUtils.valid(pVo.getClassid())) {
						return ZappFinalizing.finalising("ERR_MIS_ID", "[validParams] " + messageService.getMessage("ERR_MIS_ID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Company ID
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pVo.getCompanyid())) {
						return ZappFinalizing.finalising("ERR_MIS_COMPANYID", "[validParams] " + messageService.getMessage("ERR_MIS_COMPANYID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Upper ID
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pVo.getUpid())) {
						return ZappFinalizing.finalising("ERR_MIS_UPID", "[validParams] " + messageService.getMessage("ERR_MIS_UPID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}		
			
			// Code
			switch(pAct) {
				case ADD: case REPLICATE: case RELOCATE:
					if(!pVo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
						if(!ZstFwValidatorUtils.valid(pVo.getCode())) {
							return ZappFinalizing.finalising("ERR_MIS_CODE", "[validParams] " + messageService.getMessage("ERR_MIS_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				break;
				default:break;
			}
	
			// Name
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pVo.getName())) {
						return ZappFinalizing.finalising("ERR_MIS_NAME", "[validParams] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Type
			switch(pAct) {
				case ADD: 
					if(!ZstFwValidatorUtils.valid(pVo.getTypes())) {
						return ZappFinalizing.finalising("ERR_MIS_TYPE", "[validParams] " + messageService.getMessage("ERR_MIS_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
			// Classification access control info.
			switch(pAct) {
				case ADD:
					if(containAclFreeType(pVo.getTypes()) == false) {
						if(pVo.getZappClassAcls() == null) {
							return ZappFinalizing.finalising("ERR_MIS_CLASSACL", "[validParams] " + messageService.getMessage("ERR_MIS_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						if(pVo.getZappClassAcls().size() == ZERO) {
							return ZappFinalizing.finalising("ERR_MIS_CLASSACL", "[validParams] " + messageService.getMessage("ERR_MIS_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				break;
				default:break;
			}		
			
			// Default content access control info.
			switch(pAct) {
				case ADD:	
					if(containAclFreeType(pVo.getTypes()) == false) {
						if(pVo.getZappContentAcls() == null) {
							return ZappFinalizing.finalising("ERR_MIS_CONTENTACL", "[validParams] " + messageService.getMessage("ERR_MIS_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						if(pVo.getZappContentAcls().size() == ZERO) {
							return ZappFinalizing.finalising("ERR_MIS_CONTENTACL", "[validParams] " + messageService.getMessage("ERR_MIS_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				break;
				default:break;
			}	
			
			// Workflow
			switch(pAct) {
				case ADD:
					if(pVo.getWfrequired() > ZERO) {
						if(ZstFwValidatorUtils.valid(pVo.getWfid()) == false) {
							return ZappFinalizing.finalising("ERR_MIS_WFOBJ", "[validParams] " + messageService.getMessage("ERR_MIS_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				break;
				default: break;
			}
			
			// Affiliationid
			switch(pAct) {
				case ADD:
					if(pVo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type)) {
						if(ZstFwValidatorUtils.valid(pVo.getAffiliationid()) == false) {
							return ZappFinalizing.finalising("ERR_MIS_AFFILIATIONID", "[validParams] " + messageService.getMessage("ERR_MIS_AFFILIATIONID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				break;
				default: break;
			}			
			
		}
		
		return pObjRes;
		
	}
	
	// ### Use or not ###
	private ZstFwResult checkUsingInOtherObject(ZappAuth pObjAuth, String pCaller, Object pObj, ZstFwResult pObjRes) {
	
		if(pObj != null) {
			
			Map<String, Object> pMap = new HashMap<String, Object>();
			
			if(pObj instanceof ZappClassification) {
				ZappClassification pvo = (ZappClassification) pObj;
				pMap.put("objType", "CLASS");
				pMap.put("objid", pvo.getClassid());
			}
			
			List<ZappCommon> rUsingList = commonService.usingOtherTable(pObjAuth, pMap);
			if(rUsingList != null) {
				if(rUsingList.size() > 0) {
					return ZappFinalizing.finalising("ERR_ALREADY_USED", "[" + pCaller + "][checkUsingInOtherObject] " + messageService.getMessage("ERR_ALREADY_USED",  BLANK), BLANK, rUsingList);
				}
			}
		}
		
		return pObjRes;
	}	
	
	// ### 
	private boolean containAclFreeType(String checkType) {
		if(getAclFreeTypes().contains(checkType)) {
			return true;
		} else {
			return false;
		}
	}
	private String getAclFreeTypes() {
		StringBuffer types = new StringBuffer();
		types.append(ZappConts.TYPES.CLASS_FOLDER.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_CLASS.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_DOCTYPE.type); 
		return types.toString();
	}	
	
}

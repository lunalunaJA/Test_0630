package com.zenithst.core.content.api;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.zenithst.archive.api.ZArchFileMgtService;
import com.zenithst.archive.api.ZArchMFileMgtService;
import com.zenithst.archive.api.ZArchVersionMgtService;
import com.zenithst.archive.constant.Characters;
import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.constant.Results;
import com.zenithst.archive.exception.ZArchApiException;
import com.zenithst.archive.key.FormKey;
import com.zenithst.archive.service.ZArchFileService;
import com.zenithst.archive.service.ZArchMFileService;
import com.zenithst.archive.service.ZArchVersionService;
import com.zenithst.archive.util.CryptoNUtil;
import com.zenithst.archive.util.FileUtil;
import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchMFileRes;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.archive.vo.ZArchVersion;
import com.zenithst.core.acl.api.ZappAclMgtService;
import com.zenithst.core.acl.vo.ZappAclExtend;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.api.ZappClassificationMgtService;
import com.zenithst.core.classification.bind.ZappClassificationBinder;
import com.zenithst.core.classification.service.ZappClassificationService;
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
import com.zenithst.core.common.utility.ZappJSONUtils;
import com.zenithst.core.content.bind.ZappContentBinder;
import com.zenithst.core.content.service.ZappContentService;
import com.zenithst.core.content.service.ZappFtrService;
import com.zenithst.core.content.vo.ZappAdditoryBundle;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.content.vo.ZappContentWorkflow;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.content.vo.ZappKeywordExtend;
import com.zenithst.core.content.vo.ZappKeywordObject;
import com.zenithst.core.content.vo.ZappLinkedObject;
import com.zenithst.core.content.vo.ZappLockedObject;
import com.zenithst.core.content.vo.ZappLockedObjectExtend;
import com.zenithst.core.content.vo.ZappMarkedObject;
import com.zenithst.core.content.vo.ZappSharedObject;
import com.zenithst.core.content.vo.ZappTmpObject;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.system.api.ZappSystemMgtService;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.core.workflow.api.ZappWorkflowMgtService;
import com.zenithst.core.workflow.vo.ZappWorkflowObject;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwEncodeUtils;
import com.zenithst.framework.util.ZstFwFileUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage content info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappContentMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappContentMgtService")
public class ZappContentMgtServiceImpl extends ZappService implements ZappContentMgtService {

	/*
	* [Service]
	*/

	/* Content */
	@Autowired
	private ZappContentService contentService;

	/* Content-Workflow */
	@Autowired
	private ZappContentWorkflowMgtService contentWorkflowoService;
	
	/* FTR */
	@Autowired
	private ZappFtrService ftrService;
	
	/* Access control */
	@Autowired
	private ZappAclMgtService aclService;
	
	/* Classification */
	@Autowired
	private ZappClassificationMgtService classService;
	@Autowired
	private ZappClassificationService classService_;
	
	/* Log */
	@Autowired
	private ZappLogMgtService logService;
	
	/* System */
	@Autowired
	private ZappSystemMgtService systemService;
	
	/* Workflow */
	@Autowired
	private ZappWorkflowMgtService workflowService;
	
	/* Common */
	@Autowired
	private ZappCommonService commonService;
	
	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/*
	* [Binder]
	*/

	/* Content */
	@Autowired
	private ZappContentBinder utilBinder;
	
	/* Classification */
	@Autowired
	private ZappClassificationBinder utilClassBinder;
	
	/*  
	* [Archive]
	*/

	/* Master file */
	@Autowired
	private ZArchMFileMgtService zarchMfileMgtService;
	
	/* Master file */
	@Autowired
	private ZArchMFileService zarchMfileService;
	
	/* Unique file */
	@Autowired
	private ZArchFileMgtService zarchfileMgtService;
	@Autowired
	private ZArchFileService zarchfileService;
	
	/* Version */
	@Autowired
	private ZArchVersionService zarchVersionService;

	/* Version */
	@Autowired
	private ZArchVersionMgtService zarchVersionMgtService;
	
	/* Temporary Mounting Path  */
	@Value("#{archiveconfig['TEMP_PATH']}")
	protected String TEMP_PATH;
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappContentMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappContentMgtService] Service Destroy ");
	}		
	
	/*
	 *  [New] 
	 */
	
	
	public ZstFwResult addObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* Variable */
		pObjAuth.setObjTime(ZstFwDateUtils.getNow());					// Processing time
		
		/* [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [LOG] Whether to leave content log
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);			
		
		/* [Multiple registration] 
		 * 
		 */
		if(pObj instanceof List) {
			pObjRes = contentService.cMultiRows(pObjAuth, pObj, pObjRes);
		} 
		else {
			
			/* Validation */
			if(utilBinder.isEmpty(pObj) == true) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Bundle */
			if(pObj instanceof ZappBundle) {}
			
			/* File */
			if(pObj instanceof ZappFile) {}
			
			/* Content-Clasification */
			if(pObj instanceof ZappClassObject) {}
			
			/* Linked content */
			if(pObj instanceof ZappLinkedObject) {}
			
			/* Shared content  */
			if(pObj instanceof ZappSharedObject) {}
			
			/* Locked content  */
			if(pObj instanceof ZappLockedObject) {}
			
			/* Temporay info.  */
			if(pObj instanceof ZappTmpObject) {}
			
			pObjRes = contentService.cSingleRow(pObjAuth, pObj, pObjRes);
			
			/* ## Log ## */
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use) && ZappFinalizing.isSuccess(pObjRes) == true) {
				
				Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
				String LOGTYPE = BLANK; String LOGACTION = BLANK;
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
				
				/* Bundle */
				if(pObj instanceof ZappBundle) { 
					LOGTYPE = ZappConts.LOGS.TYPE_BUNDLE.log;
					LOGACTION = ZappConts.LOGS.ACTION_ADD.log; 
				}
				
				/* Additional Bundle */
				if(pObj instanceof ZappAdditoryBundle) { 
					LOGTYPE = ZappConts.LOGS.TYPE_ADDBUNDLE.log;
					LOGACTION = ZappConts.LOGS.ACTION_ADD.log; 
				}				
				
				/* File */
				if(pObj instanceof ZappFile) { 
					LOGTYPE = ZappConts.LOGS.TYPE_FILE.log;
					LOGACTION = ZappConts.LOGS.ACTION_ADD.log; 
				}
				
				/* Content-Clasification */
				if(pObj instanceof ZappClassObject) { 
					LOGTYPE = ZappConts.LOGS.TYPE_CLASSIFICATION.log;
					LOGACTION = ZappConts.LOGS.ACTION_ADD.log; 
				}
				
				/* Linked content */
				if(pObj instanceof ZappLinkedObject) { 
					LOGTYPE = ZappConts.LOGS.TYPE_LINK.log;
					LOGACTION = ZappConts.LOGS.ACTION_LINK.log; 
				}
				
				/* Shared content  */
				if(pObj instanceof ZappSharedObject) { 
					LOGTYPE = ZappConts.LOGS.TYPE_SHARE.log;
					LOGACTION = ZappConts.LOGS.ACTION_SHARE.log; 
				}
				
				/* Locked content  */
				if(pObj instanceof ZappLockedObject) { 
					LOGTYPE = ZappConts.LOGS.TYPE_LOCK.log;
					LOGACTION = ZappConts.LOGS.ACTION_LOCK.log; 
				}
				
				/* Temporay info.  */
				if(pObj instanceof ZappTmpObject) { 
					LOGTYPE = ZappConts.LOGS.TYPE_SHARE.log;
					LOGACTION = ZappConts.LOGS.ACTION_ADD.log; 
				}
				
				/* Content Workflow  */
				if(pObj instanceof ZappContentWorkflow) { 
					LOGTYPE = ZappConts.LOGS.TYPE_WORKFLOWCONTENT.log;
					LOGACTION = ZappConts.LOGS.ACTION_ADD.log; 
				}
				
				pObjRes = leaveLog(pObjAuth
						         , LOGTYPE
								 , LOGACTION
						         , LOGMAP
						         , pObjAuth.getObjTime()
						         , pObjRes);
				pObjRes.setResObj(BLANK);
			}
			
		}
		
		return pObjRes;

	}
	
	/*
	 * []
	 */
	public ZstFwResult mergeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[mergeObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Validation */
		if(utilBinder.isEmpty(pObj) == true) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[mergeObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObj instanceof ZappBundle) {}
		
		/* File */
		if(pObj instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObj instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObj instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObj instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObj instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObj instanceof ZappTmpObject) {}
		
		pObjRes = contentService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
		
	}
	
	/**
	 * 
	 */
	public ZstFwResult addObjectExist(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		/* [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObjectExist] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Validation */
		if(utilBinder.isEmpty(pObj) == true) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObjectExist] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObj instanceof ZappBundle) {}
		
		/* File */
		if(pObj instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObj instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObj instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObj instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObj instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObj instanceof ZappTmpObject) {}
		
		/* Temporay info.  */
		if(pObj instanceof ZappKeyword) {}
		
		/* Temporay info.  */
		if(pObj instanceof ZappKeywordObject) {}
		
		pObjRes = contentService.ceSingleRow(pObjAuth, pObj, pObjRes);
		
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
		
		/* Bundle */
		if(pObj instanceof ZappBundle) {}
		
		/* File */
		if(pObj instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObj instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObj instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObj instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObj instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObj instanceof ZappTmpObject) {}
		
		pObjRes = contentService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		pObjRes = contentService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		pObjRes = contentService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
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
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = contentService.dSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = contentService.dMultiRows(pObjAuth, pObjw, pObjRes);
		}
		
		/* ## Log ## */
		
		return pObjRes;
		
	}
	
	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		/*  */
		if(pObjw instanceof ZappKeyword) {}
		
		/*  */
		if(pObjw instanceof ZappKeywordObject) {}
		
		pObjRes = contentService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
		
	}	

	/**
	 *  [OUT]
	 */
	
	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* Additional Bundle */
		if(pObjw instanceof ZappAdditoryBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = contentService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = contentService.rMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		pObjRes = contentService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	/**
	 *  [OUT]
	 */
	
	public ZstFwResult selectExtendObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = contentService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = contentService.rMultiRowsExtend(pObjAuth, pObjw, pObjRes);
		}
		
		return pObjRes;
	}

	public ZstFwResult selectExtendObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		pObjRes = contentService.rMultiRowsExtend(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		pObjRes = contentService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		pObjRes = contentService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}

		pObjRes = contentService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Bundle */
		if(pObjw instanceof ZappBundle) {}
		
		/* File */
		if(pObjw instanceof ZappFile) {}
		
		/* Content-Clasification */
		if(pObjw instanceof ZappClassObject) {}
		
		/* Linked content */
		if(pObjw instanceof ZappLinkedObject) {}
		
		/* Shared content  */
		if(pObjw instanceof ZappSharedObject) {}
		
		/* Locked content  */
		if(pObjw instanceof ZappLockedObject) {}
		
		/* Temporay info.  */
		if(pObjw instanceof ZappTmpObject) {}
		
		pObjRes = contentService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}
	
	/* ****************************************************************************************************************** */
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String CONTENTID = BLANK, CLASSID = BLANK, CLASS_TYPE = BLANK, DOCTYPEID = BLANK;									// Content ID
		Object[] EXIST_FILENAME = {ZappConts.ACTION.IGNORE, ZappConts.ACTION.IGNORE};	// Whether the same file name exists								
		boolean IN_EXPIRETIME = false;								// Whether expiretime is specified
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		ZappContentRes pObjContentLog = null;
		
		// ## Initialization
		if(pObjContent.getZappAdditoryBundle() == null) {pObjContent.setZappAdditoryBundle(new ZappAdditoryBundle());}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);			
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "addContent", ZappConts.ACTION.ADD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_ADD_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_ADD_YN.env);					// Approval for registration or not
		ZappEnv SYS_APPROVAL_ADD_YN = new ZappEnv(); SYS_APPROVAL_ADD_YN.setSetval(NO);
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether content access control info. are applied
		ZappEnv SYS_CLASSACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CLASSACL_YN.env);							// Whether classification access control info. are applied
		ZappEnv SYS_REMOTE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.REMOTE_YN.env);								// Whether remote file
		ZappEnv SYS_ENCRYPTION_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.ENCRYPTION_YN.env);						// [FILE] Encrypted or not
		ZappEnv SYS_VERSION_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_YN.env);							// [VERSION] Apply version or not
		ZappEnv SYS_VERSION_UPONLYHIGH_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_UPONLYHIGH_YN.env);		// [VERSION] Whether to increase the higher version
		ZappEnv SYS_VERSION_UPWITHNOSAMEHASH_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_UPWITHNOSAMEHASH_YN.env);		// [VERSION] Whether to upgrade version if the file hash value is the same
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log

		/* [Check folder access control info.]
		 * Check if the user has permission to add to the current folder.
		 */
		if(SYS_CLASSACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			ZappClassAcl pZappClassAcl = new ZappClassAcl(); 
			for(ZappClassObject vo : pObjContent.getZappClassObjects()) {
				if(getNodeTypes().contains(vo.getClasstype())) {	
					pZappClassAcl.setClassid(vo.getClassid());
					CLASSID = vo.getClassid(); CLASS_TYPE = vo.getClasstype();
				} else {
					if(vo.getClasstype().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
						DOCTYPEID = vo.getClassid();
					}
				}
			}
			if(!CLASS_TYPE.equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type) && !CLASS_TYPE.equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
				pZappClassAcl.setAcls(ZappConts.ACLS.CLASS_READ_ADD.acl);
				pObjRes = aclService.checkObject(pObjAuth, pZappClassAcl, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_CLASSACL", "[addContent] " + messageService.getMessage("ERR_R_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				boolean CANDO = (Boolean) pObjRes.getResObj();
				if(CANDO == false) {
					return ZappFinalizing.finalising("ERR_NO_ACL", "[addContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} else {
				if(CLASS_TYPE.equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
					pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pZappClassAcl.getClassid()), pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
					if(rZappClassification == null) {
						return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(!rZappClassification.getHolderid().equals(pObjAuth.getSessDeptUser().getUserid())) {
						return ZappFinalizing.finalising("ERR_NO_ACL", "[addContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			}
		} else {
			for(ZappClassObject vo : pObjContent.getZappClassObjects()) {
				if(getNodeTypes().contains(vo.getClasstype())) {	
					CLASSID = vo.getClassid(); CLASS_TYPE = vo.getClasstype();
				} else {
					if(vo.getClasstype().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
						DOCTYPEID = vo.getClassid();
					}
				}
			}
		}
		
		/** [Get classification info.]
		 * 
		 */
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(CLASSID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification == null) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if((rZappClassification.getWfrequired() | ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt) == ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt
				|| rZappClassification.getWfrequired() == ZappConts.WORKFLOWS.WF_OBJECT_ALL.iwt) {
			SYS_APPROVAL_ADD_YN.setSetval(YES); // Apply approval process
		}
		// Apply approval process
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			SYS_APPROVAL_ADD_YN.setSetval(workflowService.doApply(rZappClassification.getWfrequired(), ZappConts.ACTION.ADD) == true ? YES : NO);
		} else {
			SYS_APPROVAL_ADD_YN.setSetval(NO);
		}
		
		/* Validation
		 * 1. Content access control info., 2. Info. for approval
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjRes = validMultiple(pObjAuth, pObjContent, pObjRes, "addContent", ZappConts.ACTION.ACL);
		}
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjRes = validMultiple(pObjAuth, pObjContent, pObjRes, "addContent", ZappConts.ACTION.APPROVAL);
		}
		
		/* [Bundle and file]
		 * Get info. by content type
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* Bundle */
			pObjContent.getZappBundle().setCreatorid(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getCreatorid()) ? pObjContent.getZappBundle().getCreatorid() : pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappBundle().setCreatorname(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getCreatorname()) ? pObjContent.getZappBundle().getCreatorname() : pObjAuth.getSessUser().getName());
			pObjContent.getZappBundle().setHolderid(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getHolderid()) ? pObjContent.getZappBundle().getHolderid() : pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappBundle().setCreatetime(PROCTIME);
			pObjContent.getZappBundle().setUpdatetime(PROCTIME);
			pObjContent.getZappBundle().setRetentionid(!ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getRetentionid()) ? pObjContent.getObjRetention() : pObjContent.getZappBundle().getRetentionid());
			
			/* Expired time (Key-In) */
			if(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getExpiretime()) == true) {
				if(isDateFormat(pObjContent.getZappBundle().getExpiretime()) == true) {
					IN_EXPIRETIME = true;
				} else {
					return ZappFinalizing.finalising("ERR_NIDENT_DATEFORMAT", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* State */
			if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjContent.getZappBundle().setState(ZappConts.STATES.BUNDLE_ADD_REQUEST.state);		// Request for adding
			} else {
				pObjContent.getZappBundle().setState(ZappConts.STATES.BUNDLE_NORMAL.state);				// Normal
			}
			pObjContent.getZappBundle().setBundleid(ZappKey.getPk(pObjContent.getZappBundle()));		// PK
			CONTENTID = pObjContent.getZappBundle().getBundleid();
			pObjContent.setContentid(CONTENTID); 	// for FTR
			
			/* File */
			if(pObjContent.getZappFiles().size() > ZERO) {
				for(int IDX = ZERO; IDX < pObjContent.getZappFiles().size(); IDX++) {
					pObjContent.getZappFiles().get(IDX).getObjFileName();											// Entire pathway for uploading
					pObjContent.getZappFiles().get(IDX).getObjFileExt();											// File extension
					pObjContent.getZappFiles().get(IDX).getFilename();												// File name
					pObjContent.getZappFiles().get(IDX).setObjTaskid(pObjContent.getObjTaskid());					// Task ID
					pObjContent.getZappFiles().get(IDX).setCreator(ZstFwValidatorUtils.valid(pObjContent.getZappFiles().get(IDX).getCreator()) ? pObjContent.getZappFiles().get(IDX).getCreator() : pObjAuth.getSessDeptUser().getDeptuserid());		// Creator ID
					pObjContent.getZappFiles().get(IDX).setLinkid(CONTENTID);										// Content ID (Bundle ID)			
					pObjContent.getZappFiles().get(IDX).setIsEncrypted(SYS_ENCRYPTION_YN.getSetval().equals(ZappConts.USAGES.YES.use) ? true : false); // Encrypted or not
					if(SYS_VERSION_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
						// Whether to increase the version when the file hash values ​​are the same
						pObjContent.getZappFiles().get(IDX).setIsVersionedUpwithNoSameHash((SYS_VERSION_UPWITHNOSAMEHASH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
						// Whether to increase the higher version
						if(ZstFwValidatorUtils.valid(pObjContent.getObjVerOpt()) == true) {
							if(pObjContent.getObjVerOpt().equals(ZappConts.OPTIONS.UP_HIGH_VERSION.opt)) {
								SYS_VERSION_UPONLYHIGH_YN.setSetval(YES);
							} else {
								SYS_VERSION_UPONLYHIGH_YN.setSetval(NO);
							}
						}
						pObjContent.getZappFiles().get(IDX).setIsHighVer((SYS_VERSION_UPONLYHIGH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
					}
				}
			}
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
		
			pObjContent.getZappFile().getObjFileName();											// Entire pathway for uploading
			pObjContent.getZappFile().getObjTaskid();											// Task ID
			pObjContent.getZappFile().getObjFileExt();											// File extension
			pObjContent.getZappFile().getFilename();											// File name
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getObjFileExt())) {
				pObjContent.getZappFile().setExt(ZstFwFileUtils.getExtension(pObjContent.getZappFile().getObjFileExt()));
			} else {
				pObjContent.getZappFile().setExt(ZstFwFileUtils.getExtension(pObjContent.getZappFile().getObjFileName()));
			}
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());					// Task ID
			pObjContent.getZappFile().setCreator(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getCreator()) ? pObjAuth.getSessDeptUser().getDeptuserid() : pObjContent.getZappFile().getCreator());	// Creator ID
//			pObjContent.getZappFile().setLinkid(ZappConts.TYPES.CONTENT_FILE.type);				// File				
			pObjContent.getZappFile().setLinkid(ZstFwEncodeUtils.encodeString_SHA256(ZstFwDateUtils.getNow()));		// 동적 값으로 수정 2021-09-28				
			pObjContent.getZappFile().setIsEncrypted(SYS_ENCRYPTION_YN.getSetval().equals(ZappConts.USAGES.YES.use) ? true : false); // Encrypted or not
			if(SYS_VERSION_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				// Whether to increase the version when the file hash values ​​are the same
				pObjContent.getZappFile().setIsVersionedUpwithNoSameHash((SYS_VERSION_UPWITHNOSAMEHASH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
				// Whether to increase the higher version
				if(ZstFwValidatorUtils.valid(pObjContent.getObjVerOpt()) == true) {
					if(pObjContent.getObjVerOpt().equals(ZappConts.OPTIONS.UP_HIGH_VERSION.opt)) {
						SYS_VERSION_UPONLYHIGH_YN.setSetval(YES);
					} else {
						SYS_VERSION_UPONLYHIGH_YN.setSetval(NO);
					}
				}
				pObjContent.getZappFile().setIsHighVer((SYS_VERSION_UPONLYHIGH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
			}

			/* Expired date (Key-In) */
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getExpiretime()) == true) {
				if(isDateFormat(pObjContent.getZappFile().getExpiretime()) == true) {
					IN_EXPIRETIME = true;
				} else {
					return ZappFinalizing.finalising("ERR_NIDENT_DATEFORMAT", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

			/* State */
			if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjContent.getZappFile().setState(ZappConts.STATES.BUNDLE_ADD_REQUEST.state);		// Request for adding
				pObjContent.getZappFile().setDrafter(pObjAuth.getSessDeptUser().getDeptuserid() + DIVIDER + pObjAuth.getSessUser().getName());			// Drafter
			} else {
				pObjContent.getZappFile().setState(ZappConts.STATES.BUNDLE_NORMAL.state);			// Normal
			}
			
			/* Check the same file name
			 * If the content type is FILE, check whether the same file name exists in the folder.
			 */
			pObjRes = checkFile(pObjAuth, pObjContent, pObjRes);
			EXIST_FILENAME = (Object[]) pObjRes.getResObj();
			if(EXIST_FILENAME != null) {
				if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.IGNORE) {
					return pObjRes;
				}
			} 
			logger.info("EXIST_FILENAME[0] : " + EXIST_FILENAME[ZERO]);
			logger.info("EXIST_FILENAME[1] : " + EXIST_FILENAME[ONE]);
			
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.ADD) {					// New
				ZArchMFile pk = new ZArchMFile();
				pk.setFilename(pObjContent.getZappFile().getFilename());
				pk.setLinkid(ZstFwEncodeUtils.encodeString_SHA256(PROCTIME));
				CONTENTID = FormKey.getKey(pk);
			} else if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.VERSION_UP) {	// Version-Up
				CONTENTID = (String) EXIST_FILENAME[ONE];
			}
		}
		
		/* [Expire time] 
		 * 
		 */
		if(IN_EXPIRETIME == false && ZstFwValidatorUtils.valid(pObjContent.getObjRetention()) == true) {
			ZappCode pZappCode = new ZappCode(); 
			pZappCode.setCodeid(pObjContent.getObjRetention());
			pZappCode.setTypes(ZappConts.TYPES.CODE_RETENTION.type);
			pObjRes = systemService.selectObject(pObjAuth, pZappCode, pObjRes);	
			if(ZappFinalizing.isSuccess(pObjRes) == true) {
				List<ZappCode> rZappCodeList = (List<ZappCode>) pObjRes.getResObj();
				if(rZappCodeList == null) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(rZappCodeList.size() == ZERO) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				ZappCode rZappCode = null;
				for(ZappCode vo : rZappCodeList) {
					rZappCode = vo;
				}
				try {
					if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						pObjContent.getZappBundle().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode != null ? rZappCode.getCodevalue() : String.valueOf(ZERO))));
					}
					else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						pObjContent.getZappFile().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode != null ? rZappCode.getCodevalue() : String.valueOf(ZERO))));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		/* [Classification] 
		 * Folder, Content type, Classification system 
		 */
		for(int IDX = ZERO; IDX < pObjContent.getZappClassObjects().size(); IDX++ ) {
			if(pObjContent.getZappClassObjects().get(IDX).getClasstype().equals(ZappConts.TYPES.CLASS_FOLDER.type)) {				// Folder
			}
			if(pObjContent.getZappClassObjects().get(IDX).getClasstype().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {			// Content type
				
			}
			if(pObjContent.getZappClassObjects().get(IDX).getClasstype().equals(ZappConts.TYPES.CLASS_CLASS.type)) {			// Classification system 
				
			}
			pObjContent.getZappClassObjects().get(IDX).setCobjid(CONTENTID);
			pObjContent.getZappClassObjects().get(IDX).setCobjtype(pObjContent.getObjType());
			pObjContent.getZappClassObjects().get(IDX).setClassobjid(ZappKey.getPk(pObjContent.getZappClassObjects().get(IDX)));
		}
		
		/* [Link] 
		 * 
		 */
		for(int IDX = ZERO; IDX < pObjContent.getZappLinkedObjects().size(); IDX++ ) {
			pObjContent.getZappLinkedObjects().get(IDX).setSourceid(CONTENTID);
			pObjContent.getZappLinkedObjects().get(IDX).setLinkerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappLinkedObjects().get(IDX).setLinktime(PROCTIME);
			pObjContent.getZappLinkedObjects().get(IDX).setLinkedobjid(ZappKey.getPk(pObjContent.getZappLinkedObjects().get(IDX)));
		}
		
		/* [Share info.] 
		 * 
		 */
		for(int IDX = ZERO; IDX < pObjContent.getZappSharedObjects().size(); IDX++ ) {
			pObjContent.getZappSharedObjects().get(IDX).setSobjid(CONTENTID);
			pObjContent.getZappSharedObjects().get(IDX).setSobjtype(pObjContent.getObjType());		// Bundle , File
			pObjContent.getZappSharedObjects().get(IDX).setSharerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappSharedObjects().get(IDX).setSharetime(PROCTIME);
			pObjContent.getZappSharedObjects().get(IDX).setShareobjid(ZappKey.getPk(pObjContent.getZappSharedObjects().get(IDX)));
		}
		
		/* [Access control info.] 
		 *  
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			for(int IDX = ZERO; IDX < pObjContent.getZappAcls().size(); IDX++ ) {
				pObjContent.getZappAcls().get(IDX).setContentid(CONTENTID);
				pObjContent.getZappAcls().get(IDX).setContenttype(pObjContent.getObjType());
				pObjContent.getZappAcls().get(IDX).setAclid(ZappKey.getPk(pObjContent.getZappAcls().get(IDX)));
			}
		}
		
		/* [Keyword]
		 * 
		 */
		if(pObjContent.getZappKeywords() != null) {
			if(pObjContent.getZappKeywords().size() > ZERO) {
				List<ZappKeywordObject> pZappKeywordObjectList = new ArrayList<ZappKeywordObject>();
				for(int IDX = ZERO; IDX < pObjContent.getZappKeywords().size(); IDX++ ) {
					ZappKeyword pZappKeyword = new ZappKeyword();
					pZappKeyword.setIsactive(YES);
					pZappKeyword.setKword(pObjContent.getZappKeywords().get(IDX).getKword());
					pZappKeyword.setKwordid(ZappKey.getPk(pZappKeyword));
					pObjRes = addObjectExist(pObjAuth, pZappKeyword, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_KEYWORD", "[addContent] " + messageService.getMessage("ERR_C_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					
					ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
					pZappKeywordObject.setKobjid(CONTENTID);							// Content ID
					pZappKeywordObject.setKobjtype(pObjContent.getObjType());			// Target type
					pZappKeywordObject.setKwordid(pZappKeyword.getKwordid());
					pZappKeywordObject.setKwobjid(ZappKey.getPk(pObjContent.getZappKeywords().get(IDX)));		
					pZappKeywordObjectList.add(pZappKeywordObject);
				}
				pObjContent.setZappKeywordObjects(pZappKeywordObjectList);
			}
		}
		
		
		/* [Approval]
		 * 
		 */
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			
		}
		
		/* [Sub-info.]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjContent.getZappAdditoryBundle().setBundleid(CONTENTID);
		} 
		
		/* ***************************************************************************************************************** */
		
		/* [Bundle and file]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* Bundle */
			pObjRes = addObject(pObjAuth, pObjContent.getZappBundle(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_BUNDLE", "[addContent] " + messageService.getMessage("ERR_C_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* File */
			if(pObjContent.getZappFiles().size() > ZERO) {
				if(pObjContent.getZappBundle().getBtype().equals(ZappConts.TYPES.BUNDLE_NORMAL.type)) {
					for(ZappFile vo : pObjContent.getZappFiles()) {
						vo.setObjTaskid(pObjContent.getObjTaskid());	// Task ID	
						vo.setLinkid(CONTENTID);						// Bundle ID
						vo.setObjCaller(pObjContent.getObjCaller());	// Caller
						pObjRes = addFile(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_C_FILE", "[addContent] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				} else if(pObjContent.getZappBundle().getBtype().equals(ZappConts.TYPES.BUNDLE_VIRTUAL.type)) {
					List<ZappLinkedObject> pLinkList = new ArrayList<ZappLinkedObject>();
					for(ZappFile vo : pObjContent.getZappFiles()) {
						ZappLinkedObject pZappLinkedObject = new ZappLinkedObject(); 
						if(ZstFwValidatorUtils.valid(vo.getAction()) == false) {
							vo.setAction(ZappConts.TYPES.CONTENT_FILE.type);
						}
						pZappLinkedObject.setSourceid(CONTENTID);
						pZappLinkedObject.setTargetid(vo.getMfileid());
						pZappLinkedObject.setLinkerid(pObjAuth.getSessDeptUser().getDeptuserid());
						pZappLinkedObject.setLinktime(PROCTIME);
						pZappLinkedObject.setLinktype(vo.getAction().equals(ZappConts.TYPES.CONTENT_BUNDLE.type) ? ZappConts.TYPES.LINK_BTOB.type : ZappConts.TYPES.LINK_BTOF.type);
						pZappLinkedObject.setLinkedobjid(ZappKey.getPk(pZappLinkedObject));
						pLinkList.add(pZappLinkedObject);
					}
					pObjRes = addObject(pObjAuth, pLinkList, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_LINK", "[addContent] " + messageService.getMessage("ERR_C_LINK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			}
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			/* File */
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.VERSION_UP) {	// Version-Up
				pObjContent.getZappFile().setMfileid((String) EXIST_FILENAME[ONE]);
			}
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());						// Task ID	
			pObjContent.getZappFile().setLinkid(ZstFwEncodeUtils.encodeString_SHA256(PROCTIME));	
			pObjContent.getZappFile().setCreatetime(PROCTIME);
			pObjContent.getZappFile().setObjCaller(pObjContent.getObjCaller());						// Caller
			pObjRes = addFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_FILE", "[addContent] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			CONTENTID = (String) pObjRes.getResObj();
			pObjContent.setContentid(CONTENTID); 	// for FTR
			if(ZstFwValidatorUtils.valid(CONTENTID) == false) {
				return ZappFinalizing.finalising("ERR_MIS_FILEID", "[addContent] " + messageService.getMessage("ERR_MIS_FILEID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pObjRes.setResCode(SUCCESS);
			
			logger.info("[MFILEID] = " + CONTENTID);
			
			/* Sub-file info. */
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.ADD) {
				pObjContent.getZappFile().setMfileid(CONTENTID);
				pObjContent.getZappFile().setRetentionid(pObjContent.getObjRetention());
				pObjContent.getZappFile().setExpiretime(pObjContent.getZappFile().getExpiretime());
				pObjRes = addFileExtension(pObjAuth, pObjContent.getZappFile(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_FILEEXT", "[addContent] " + messageService.getMessage("ERR_C_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		pObjRes.setResCode(SUCCESS);
		
		/* [Classification]
		 * 
		 */
		if(pObjContent.getZappClassObjects().size() > ZERO  && EXIST_FILENAME[ZERO] != ZappConts.ACTION.VERSION_UP) {
			
			// File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappClassObjects().size(); IDX++ ) {
					pObjContent.getZappClassObjects().get(IDX).setCobjid(CONTENTID);
					pObjContent.getZappClassObjects().get(IDX).setCobjtype(pObjContent.getObjType());
					pObjContent.getZappClassObjects().get(IDX).setClassobjid(ZappKey.getPk(pObjContent.getZappClassObjects().get(IDX)));
				}
			}
			
			pObjRes = addObject(pObjAuth, pObjContent.getZappClassObjects(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_CLASSOBJ", "[addContent] " + messageService.getMessage("ERR_C_CLASSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Link]
		 * 
		 */
		if(pObjContent.getZappLinkedObjects().size() > ZERO) {
			
			// File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappLinkedObjects().size(); IDX++ ) {
					pObjContent.getZappLinkedObjects().get(IDX).setSourceid(CONTENTID);
					pObjContent.getZappLinkedObjects().get(IDX).setLinkedobjid(ZappKey.getPk(pObjContent.getZappLinkedObjects().get(IDX)));
				}
			}			
			
			pObjRes = addObject(pObjAuth, pObjContent.getZappLinkedObjects(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_LINK", "[addContent] " + messageService.getMessage("ERR_C_LINK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}		
		
		/* [Share info.]
		 * 
		 */
		if(pObjContent.getZappSharedObjects().size() > ZERO) {

			// File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappSharedObjects().size(); IDX++ ) {
					pObjContent.getZappSharedObjects().get(IDX).setSobjid(CONTENTID);
					pObjContent.getZappSharedObjects().get(IDX).setSobjtype(pObjContent.getObjType());		// Bundle , File
					pObjContent.getZappSharedObjects().get(IDX).setShareobjid(ZappKey.getPk(pObjContent.getZappSharedObjects().get(IDX)));
				}
			}
			
			pObjRes = addObject(pObjAuth, pObjContent.getZappSharedObjects(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_SHARE", "[addContent] " + messageService.getMessage("ERR_C_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}	
		
		/* [Access control info.]
		 * 
		 */
		if(pObjContent.getZappAcls().size() > ZERO) {
			
			//File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappAcls().size(); IDX++ ) {
					pObjContent.getZappAcls().get(IDX).setContentid(CONTENTID);
					pObjContent.getZappAcls().get(IDX).setContenttype(pObjContent.getObjType());
					pObjContent.getZappAcls().get(IDX).setAclid(ZappKey.getPk(pObjContent.getZappAcls().get(IDX)));
				}
			}			
			
			pObjRes = aclService.addObject(pObjAuth, pObjContent.getZappAcls(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_CONTENTACL", "[addContent] " + messageService.getMessage("ERR_C_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}	
		
		/* [Keyword]
		 * 
		 */
		if(pObjContent.getZappKeywordObjects().size() > ZERO) {
			
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappKeywordObjects().size(); IDX++) {
					pObjContent.getZappKeywordObjects().get(IDX).setKobjid(CONTENTID);
					pObjContent.getZappKeywordObjects().get(IDX).setKwobjid(ZappKey.getPk(pObjContent.getZappKeywordObjects().get(IDX)));
				}
			}
			
			for(ZappKeywordObject vo : pObjContent.getZappKeywordObjects()) {
				pObjRes = addObjectExist(pObjAuth, vo, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_KEYWORDOBJ", "[addContent] " + messageService.getMessage("ERR_C_KEYWORDOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		
		/* [Approval]
		 * 
		 */
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, pObjContent.getZappBundle(), rZappClassification, pObjRes);
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, pObjContent.getZappFile(), rZappClassification, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[addContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Sub-Bundle info.]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			if(pObjContent.getZappAdditoryBundle() != null) {
				pObjContent.getZappAdditoryBundle().setBundleid(CONTENTID);
				if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
					pObjContent.getZappAdditoryBundle().setDrafter(pObjAuth.getSessDeptUser().getDeptuserid() + DIVIDER + pObjAuth.getSessUser().getName());
				} else {
					pObjContent.getZappAdditoryBundle().setDrafter(ZstFwConst.SCHARS.UNDERSCORE.character);
				}
				pObjRes = addObject(pObjAuth, pObjContent.getZappAdditoryBundle(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_BUNDLEEXT", "[addContent] " + messageService.getMessage("ERR_C_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} 
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.ADD);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, CONTENTID);
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, pObjContent.getZappBundle().getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, pObjContent.getZappFile().getFilename());
			}
			pObjRes = leaveLog(pObjAuth
					         , pObjContent.getObjType()
							 , (SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.LOGS.ACTION_ADD_REQUEST.log : ZappConts.LOGS.ACTION_ADD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		/* [FTR]
		 * 
		 */
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(NO)) {
			if(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getBtype()) == false) {
				pObjContent.getZappBundle().setBtype(BLANK);
			}
			if(!pObjContent.getZappBundle().getBtype().equals(ZappConts.TYPES.BUNDLE_VIRTUAL.type)) {
				pObjContent.setContentid(CONTENTID);
				pObjRes.setResCode(SUCCESS);
				try {
					
					pObjContent.setObjHandleType(ZappConts.ACTION.ADD.name());
					
					pObjRes = ftrService.executeIndex(pObjAuth, pObjContent, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_INDEX", "[addContent] " + messageService.getMessage("ERR_C_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				} catch (IOException e) {
					return ZappFinalizing.finalising("ERR_C_INDEX", "[addContent] " + e.getMessage(), pObjAuth.getObjlang());
				}
			}			
		}
		
		/* [Result]
		 * 
		 */
		pObjRes.setResObj(CONTENTID);

		return pObjRes;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult addContentNoFile(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String CONTENTID = BLANK, CLASSID = BLANK, CLASS_TYPE = BLANK, DOCTYPEID = BLANK;									// Content ID
		Object[] EXIST_FILENAME = {ZappConts.ACTION.IGNORE, ZappConts.ACTION.IGNORE};	// Whether the same file name exists								
		boolean IN_EXPIRETIME = false;								// Whether expiretime is specified
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		ZappContentRes pObjContentLog = null;
		
		// ## Initialization
		if(pObjContent.getZappAdditoryBundle() == null) {pObjContent.setZappAdditoryBundle(new ZappAdditoryBundle());}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);			
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "addContent", ZappConts.ACTION.ADD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_ADD_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_ADD_YN.env);					// Approval for registration or not
		ZappEnv SYS_APPROVAL_ADD_YN = new ZappEnv(); SYS_APPROVAL_ADD_YN.setSetval(NO);
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether content access control info. are applied
		ZappEnv SYS_CLASSACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CLASSACL_YN.env);							// Whether classification access control info. are applied
		ZappEnv SYS_REMOTE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.REMOTE_YN.env);								// Whether remote file
		ZappEnv SYS_ENCRYPTION_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.ENCRYPTION_YN.env);						// [FILE] Encrypted or not
		ZappEnv SYS_VERSION_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_YN.env);							// [VERSION] Apply version or not
		ZappEnv SYS_VERSION_UPONLYHIGH_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_UPONLYHIGH_YN.env);		// [VERSION] Whether to increase the higher version
		ZappEnv SYS_VERSION_UPWITHNOSAMEHASH_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_UPWITHNOSAMEHASH_YN.env);		// [VERSION] Whether to upgrade version if the file hash value is the same
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log

		/* [Check folder access control info.]
		 * Check if the user has permission to add to the current folder.
		 */
		if(SYS_CLASSACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			ZappClassAcl pZappClassAcl = new ZappClassAcl(); 
			for(ZappClassObject vo : pObjContent.getZappClassObjects()) {
				if(getNodeTypes().contains(vo.getClasstype())) {	
					pZappClassAcl.setClassid(vo.getClassid());
					CLASSID = vo.getClassid(); 
					CLASS_TYPE = vo.getClasstype();
				} else {
					if(vo.getClasstype().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
						DOCTYPEID = vo.getClassid();
					}
				}
			}
			
			if(!CLASS_TYPE.equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type) && !CLASS_TYPE.equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
				pZappClassAcl.setAcls(ZappConts.ACLS.CLASS_READ_ADD.acl);
				pObjRes = aclService.checkObject(pObjAuth, pZappClassAcl, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_CLASSACL", "[addContent] " + messageService.getMessage("ERR_R_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				boolean CANDO = (Boolean) pObjRes.getResObj();
				if(CANDO == false) {
					return ZappFinalizing.finalising("ERR_NO_ACL", "[addContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} else {
				if(CLASS_TYPE.equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
					pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pZappClassAcl.getClassid()), pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
					if(rZappClassification == null) {
						return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(!rZappClassification.getHolderid().equals(pObjAuth.getSessDeptUser().getUserid())) {
						return ZappFinalizing.finalising("ERR_NO_ACL", "[addContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			}
		} else {
			for(ZappClassObject vo : pObjContent.getZappClassObjects()) {
				if(getNodeTypes().contains(vo.getClasstype())) {	
					CLASSID = vo.getClassid(); CLASS_TYPE = vo.getClasstype();
				} else {
					if(vo.getClasstype().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
						DOCTYPEID = vo.getClassid();
					}
				}
			}
		}
		
		/** [Get classification info.]
		 * 
		 */
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(CLASSID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification == null) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[addContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if((rZappClassification.getWfrequired() | ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt) == ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt
				|| rZappClassification.getWfrequired() == ZappConts.WORKFLOWS.WF_OBJECT_ALL.iwt) {
			SYS_APPROVAL_ADD_YN.setSetval(YES); // Apply approval process
		}
		// Apply approval process
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			SYS_APPROVAL_ADD_YN.setSetval(workflowService.doApply(rZappClassification.getWfrequired(), ZappConts.ACTION.ADD) == true ? YES : NO);
		} else {
			SYS_APPROVAL_ADD_YN.setSetval(NO);
		}
		
		/* Validation
		 * 1. Content access control info., 2. Info. for approval
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjRes = validMultiple(pObjAuth, pObjContent, pObjRes, "addContent", ZappConts.ACTION.ACL);
		}
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjRes = validMultiple(pObjAuth, pObjContent, pObjRes, "addContent", ZappConts.ACTION.APPROVAL);
		}
		
		/* [Bundle and file]
		 * Get info. by content type
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* Bundle */
			pObjContent.getZappBundle().setCreatorid(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getCreatorid()) ? pObjContent.getZappBundle().getCreatorid() : pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappBundle().setCreatorname(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getCreatorname()) ? pObjContent.getZappBundle().getCreatorname() : pObjAuth.getSessUser().getName());
			pObjContent.getZappBundle().setHolderid(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getHolderid()) ? pObjContent.getZappBundle().getHolderid() : pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappBundle().setCreatetime(PROCTIME);
			pObjContent.getZappBundle().setRetentionid(!ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getRetentionid()) ? pObjContent.getObjRetention() : pObjContent.getZappBundle().getRetentionid());
			
			/* Expired time (Key-In) */
			if(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getExpiretime()) == true) {
				if(isDateFormat(pObjContent.getZappBundle().getExpiretime()) == true) {
					IN_EXPIRETIME = true;
				} else {
					return ZappFinalizing.finalising("ERR_NIDENT_DATEFORMAT", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* State */
			if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjContent.getZappBundle().setState(ZappConts.STATES.BUNDLE_ADD_REQUEST.state);		// Request for adding
			} else {
				pObjContent.getZappBundle().setState(ZappConts.STATES.BUNDLE_NORMAL.state);				// Normal
			}
			pObjContent.getZappBundle().setBundleid(ZappKey.getPk(pObjContent.getZappBundle()));		// PK
			CONTENTID = pObjContent.getZappBundle().getBundleid();
			pObjContent.setContentid(CONTENTID); 	// for FTR
			
			/* File */
			if(pObjContent.getZappFiles().size() > ZERO) {
				for(int IDX = ZERO; IDX < pObjContent.getZappFiles().size(); IDX++) {
					pObjContent.getZappFiles().get(IDX).getObjFileName();											// Entire pathway for uploading
					pObjContent.getZappFiles().get(IDX).getObjFileExt();											// File extension
					pObjContent.getZappFiles().get(IDX).getFilename();												// File name
					pObjContent.getZappFiles().get(IDX).setObjTaskid(pObjContent.getObjTaskid());					// Task ID
					pObjContent.getZappFiles().get(IDX).setCreator(ZstFwValidatorUtils.valid(pObjContent.getZappFiles().get(IDX).getCreator()) ? pObjContent.getZappFiles().get(IDX).getCreator() : pObjAuth.getSessDeptUser().getDeptuserid());		// Creator ID
					pObjContent.getZappFiles().get(IDX).setLinkid(CONTENTID);										// Content ID (Bundle ID)			
					pObjContent.getZappFiles().get(IDX).setIsEncrypted(SYS_ENCRYPTION_YN.getSetval().equals(ZappConts.USAGES.YES.use) ? true : false); // Encrypted or not
					if(SYS_VERSION_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
						// Whether to increase the version when the file hash values ​​are the same
						pObjContent.getZappFiles().get(IDX).setIsVersionedUpwithNoSameHash((SYS_VERSION_UPWITHNOSAMEHASH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
						// Whether to increase the higher version
						pObjContent.getZappFiles().get(IDX).setIsHighVer((SYS_VERSION_UPONLYHIGH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
					}
				}
			}
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
		
			pObjContent.getZappFile().getObjFileName();											// Entire pathway for uploading
			pObjContent.getZappFile().getObjTaskid();											// Task ID
			pObjContent.getZappFile().getObjFileExt();											// File extension
			pObjContent.getZappFile().getFilename();											// File name
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getObjFileExt())) {
				pObjContent.getZappFile().setExt(ZstFwFileUtils.getExtension(pObjContent.getZappFile().getObjFileExt()));
			} else {
				pObjContent.getZappFile().setExt(ZstFwFileUtils.getExtension(pObjContent.getZappFile().getObjFileName()));
			}
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());					// Task ID
			pObjContent.getZappFile().setCreator(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getCreator()) ? pObjAuth.getSessDeptUser().getDeptuserid() : pObjContent.getZappFile().getCreator());	// Creator ID
//			pObjContent.getZappFile().setLinkid(ZappConts.TYPES.CONTENT_FILE.type);				// File				
			pObjContent.getZappFile().setLinkid(ZstFwEncodeUtils.encodeString_SHA256(ZstFwDateUtils.getNow()));		// 동적 값으로 수정 2021-09-28				
			pObjContent.getZappFile().setIsEncrypted(SYS_ENCRYPTION_YN.getSetval().equals(ZappConts.USAGES.YES.use) ? true : false); // Encrypted or not
			if(SYS_VERSION_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				// Whether to increase the version when the file hash values ​​are the same
				pObjContent.getZappFile().setIsVersionedUpwithNoSameHash((SYS_VERSION_UPWITHNOSAMEHASH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
				// Whether to increase the higher version
				pObjContent.getZappFile().setIsHighVer((SYS_VERSION_UPONLYHIGH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
			}

			/* Expired date (Key-In) */
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getExpiretime()) == true) {
				if(isDateFormat(pObjContent.getZappFile().getExpiretime()) == true) {
					IN_EXPIRETIME = true;
				} else {
					return ZappFinalizing.finalising("ERR_NIDENT_DATEFORMAT", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

			/* State */
			if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjContent.getZappFile().setState(ZappConts.STATES.BUNDLE_ADD_REQUEST.state);		// Request for adding
				pObjContent.getZappFile().setDrafter(pObjAuth.getSessDeptUser().getDeptuserid() + DIVIDER + pObjAuth.getSessUser().getName());			// Drafter
			} else {
				pObjContent.getZappFile().setState(ZappConts.STATES.BUNDLE_NORMAL.state);			// Normal
			}
			
			/* Check the same file name
			 * If the content type is FILE, check whether the same file name exists in the folder.
			 */
			pObjRes = checkFile(pObjAuth, pObjContent, pObjRes);
			EXIST_FILENAME = (Object[]) pObjRes.getResObj();
			if(EXIST_FILENAME != null) {
				if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.IGNORE) {
					return pObjRes;
				}
			} 
			logger.info("EXIST_FILENAME[0] : " + EXIST_FILENAME[ZERO]);
			logger.info("EXIST_FILENAME[1] : " + EXIST_FILENAME[ONE]);
			
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.ADD) {					// New
				ZArchMFile pk = new ZArchMFile();
				pk.setFilename(pObjContent.getZappFile().getFilename());
				pk.setLinkid(ZstFwEncodeUtils.encodeString_SHA256(PROCTIME));
				CONTENTID = FormKey.getKey(pk);
			} else if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.VERSION_UP) {	// Version-Up
				CONTENTID = (String) EXIST_FILENAME[ONE];
			}
		}
		
		/* [Expire time] 
		 * 
		 */
		if(IN_EXPIRETIME == false && ZstFwValidatorUtils.valid(pObjContent.getObjRetention()) == true) {
			ZappCode pZappCode = new ZappCode(); 
			pZappCode.setCodeid(pObjContent.getObjRetention());
			pZappCode.setTypes(ZappConts.TYPES.CODE_RETENTION.type);
			pObjRes = systemService.selectObject(pObjAuth, pZappCode, pObjRes);	
			if(ZappFinalizing.isSuccess(pObjRes) == true) {
				List<ZappCode> rZappCodeList = (List<ZappCode>) pObjRes.getResObj();
				if(rZappCodeList == null) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(rZappCodeList.size() == ZERO) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				ZappCode rZappCode = null;
				for(ZappCode vo : rZappCodeList) {
					rZappCode = vo;
				}
				try {
					if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						pObjContent.getZappBundle().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode != null ? rZappCode.getCodevalue() : String.valueOf(ZERO))));
					}
					else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						pObjContent.getZappFile().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode != null ? rZappCode.getCodevalue() : String.valueOf(ZERO))));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		/* [Classification] 
		 * Folder, Content type, Classification system 
		 */
		for(int IDX = ZERO; IDX < pObjContent.getZappClassObjects().size(); IDX++ ) {
			if(pObjContent.getZappClassObjects().get(IDX).getClasstype().equals(ZappConts.TYPES.CLASS_FOLDER.type)) {				// Folder
			}
			if(pObjContent.getZappClassObjects().get(IDX).getClasstype().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {			// Content type
				
			}
			if(pObjContent.getZappClassObjects().get(IDX).getClasstype().equals(ZappConts.TYPES.CLASS_CLASS.type)) {			// Classification system 
				
			}
			pObjContent.getZappClassObjects().get(IDX).setCobjid(CONTENTID);
			pObjContent.getZappClassObjects().get(IDX).setCobjtype(pObjContent.getObjType());
			pObjContent.getZappClassObjects().get(IDX).setClassobjid(ZappKey.getPk(pObjContent.getZappClassObjects().get(IDX)));
		}

		/* [Link] 
		 * 
		 */
		for(int IDX = ZERO; IDX < pObjContent.getZappLinkedObjects().size(); IDX++ ) {
			pObjContent.getZappLinkedObjects().get(IDX).setSourceid(CONTENTID);
			pObjContent.getZappLinkedObjects().get(IDX).setLinkerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappLinkedObjects().get(IDX).setLinktime(PROCTIME);
			pObjContent.getZappLinkedObjects().get(IDX).setLinkedobjid(ZappKey.getPk(pObjContent.getZappLinkedObjects().get(IDX)));
		}
		
		/* [Share info.] 
		 * 
		 */
		for(int IDX = ZERO; IDX < pObjContent.getZappSharedObjects().size(); IDX++ ) {
			pObjContent.getZappSharedObjects().get(IDX).setSobjid(CONTENTID);
			pObjContent.getZappSharedObjects().get(IDX).setSobjtype(pObjContent.getObjType());		// Bundle , File
			pObjContent.getZappSharedObjects().get(IDX).setSharerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjContent.getZappSharedObjects().get(IDX).setSharetime(PROCTIME);
			pObjContent.getZappSharedObjects().get(IDX).setShareobjid(ZappKey.getPk(pObjContent.getZappSharedObjects().get(IDX)));
		}
		
		/* [Access control info.] 
		 *  
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			for(int IDX = ZERO; IDX < pObjContent.getZappAcls().size(); IDX++ ) {
				pObjContent.getZappAcls().get(IDX).setContentid(CONTENTID);
				pObjContent.getZappAcls().get(IDX).setContenttype(pObjContent.getObjType());
				pObjContent.getZappAcls().get(IDX).setAclid(ZappKey.getPk(pObjContent.getZappAcls().get(IDX)));
			}
		}

		/* [Keyword]
		 * 
		 */
		if(pObjContent.getZappKeywords() != null) {
			if(pObjContent.getZappKeywords().size() > ZERO) {
				List<ZappKeywordObject> pZappKeywordObjectList = new ArrayList<ZappKeywordObject>();
				for(int IDX = ZERO; IDX < pObjContent.getZappKeywords().size(); IDX++ ) {
					ZappKeyword pZappKeyword = new ZappKeyword();
					pZappKeyword.setIsactive(YES);
					pZappKeyword.setKword(pObjContent.getZappKeywords().get(IDX).getKword());
					pZappKeyword.setKwordid(ZappKey.getPk(pZappKeyword));
					pObjRes = addObjectExist(pObjAuth, pZappKeyword, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_KEYWORD", "[addContent] " + messageService.getMessage("ERR_C_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					
					ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
					pZappKeywordObject.setKobjid(CONTENTID);							// Content ID
					pZappKeywordObject.setKobjtype(pObjContent.getObjType());			// Target type
					pZappKeywordObject.setKwordid(pZappKeyword.getKwordid());
					pZappKeywordObject.setKwobjid(ZappKey.getPk(pObjContent.getZappKeywords().get(IDX)));		
					pZappKeywordObjectList.add(pZappKeywordObject);
				}
				pObjContent.setZappKeywordObjects(pZappKeywordObjectList);
			}
		}
		
		
		/* [Approval]
		 * 
		 */
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			
		}
		
		/* [Sub-info.]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjContent.getZappAdditoryBundle().setBundleid(CONTENTID);
		} 

		/* ***************************************************************************************************************** */
		
		/* [Bundle and file]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* Bundle */
			pObjRes = addObject(pObjAuth, pObjContent.getZappBundle(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_BUNDLE", "[addContent] " + messageService.getMessage("ERR_C_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* File */
			if(pObjContent.getZappFiles().size() > ZERO) {
				if(pObjContent.getZappBundle().getBtype().equals(ZappConts.TYPES.BUNDLE_NORMAL.type)) {
					for(ZappFile vo : pObjContent.getZappFiles()) {
						vo.setObjTaskid(pObjContent.getObjTaskid());	// Task ID	
						vo.setLinkid(CONTENTID);						// Bundle ID
						vo.setObjCaller(pObjContent.getObjCaller());	// Caller
						pObjRes = addFile(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_C_FILE", "[addContent] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				} else if(pObjContent.getZappBundle().getBtype().equals(ZappConts.TYPES.BUNDLE_VIRTUAL.type)) {
					List<ZappLinkedObject> pLinkList = new ArrayList<ZappLinkedObject>();
					for(ZappFile vo : pObjContent.getZappFiles()) {
						ZappLinkedObject pZappLinkedObject = new ZappLinkedObject(); 
						if(ZstFwValidatorUtils.valid(vo.getAction()) == false) {
							vo.setAction(ZappConts.TYPES.CONTENT_FILE.type);
						}
						pZappLinkedObject.setSourceid(CONTENTID);
						pZappLinkedObject.setTargetid(vo.getMfileid());
						pZappLinkedObject.setLinkerid(pObjAuth.getSessDeptUser().getDeptuserid());
						pZappLinkedObject.setLinktime(PROCTIME);
						pZappLinkedObject.setLinktype(vo.getAction().equals(ZappConts.TYPES.CONTENT_BUNDLE.type) ? ZappConts.TYPES.LINK_BTOB.type : ZappConts.TYPES.LINK_BTOF.type);
						pZappLinkedObject.setLinkedobjid(ZappKey.getPk(pZappLinkedObject));
						pLinkList.add(pZappLinkedObject);
					}
					pObjRes = addObject(pObjAuth, pLinkList, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_LINK", "[addContent] " + messageService.getMessage("ERR_C_LINK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			}
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			/* File */
			/* jwjang 20220110
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.VERSION_UP) {	// Version-Up
				pObjContent.getZappFile().setMfileid((String) EXIST_FILENAME[ONE]);
			}
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());						// Task ID	
			pObjContent.getZappFile().setLinkid(ZstFwEncodeUtils.encodeString_SHA256(PROCTIME));	
			pObjContent.getZappFile().setCreatetime(PROCTIME);
			pObjContent.getZappFile().setUpdatetime(PROCTIME);
			pObjContent.getZappFile().setObjCaller(pObjContent.getObjCaller());						// Caller
			pObjRes = addFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_FILE", "[addContent] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			CONTENTID = (String) pObjRes.getResObj();
			pObjContent.setContentid(CONTENTID); 	// for FTR
			if(ZstFwValidatorUtils.valid(CONTENTID) == false) {
				return ZappFinalizing.finalising("ERR_MIS_FILEID", "[addContent] " + messageService.getMessage("ERR_MIS_FILEID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pObjRes.setResCode(SUCCESS);
			
			logger.info("[MFILEID] = " + CONTENTID);
			*/
			
			/* Sub-file info. */
			/* jwjang 20220110
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.ADD) {
				pObjContent.getZappFile().setMfileid(CONTENTID);
				pObjContent.getZappFile().setRetentionid(pObjContent.getObjRetention());
				pObjContent.getZappFile().setExpiretime(pObjContent.getZappFile().getExpiretime());
				pObjContent.getZappFile().setCreatorname(pObjAuth.getSessDeptUser().getZappUser().getName());
				pObjRes = addFileExtension(pObjAuth, pObjContent.getZappFile(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_FILEEXT", "[addContent] " + messageService.getMessage("ERR_C_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			*/
		}
		pObjRes.setResCode(SUCCESS);
		
		/* [Classification]
		 * 
		 */
		if(pObjContent.getZappClassObjects().size() > ZERO  && EXIST_FILENAME[ZERO] != ZappConts.ACTION.VERSION_UP) {
			
			// File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappClassObjects().size(); IDX++ ) {
					pObjContent.getZappClassObjects().get(IDX).setCobjid(CONTENTID);
					pObjContent.getZappClassObjects().get(IDX).setCobjtype(pObjContent.getObjType());
					pObjContent.getZappClassObjects().get(IDX).setClassobjid(ZappKey.getPk(pObjContent.getZappClassObjects().get(IDX)));
				}
			}
			
			pObjRes = addObject(pObjAuth, pObjContent.getZappClassObjects(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_CLASSOBJ", "[addContent] " + messageService.getMessage("ERR_C_CLASSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Link]
		 * 
		 */
		if(pObjContent.getZappLinkedObjects().size() > ZERO) {
			
			// File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappLinkedObjects().size(); IDX++ ) {
					pObjContent.getZappLinkedObjects().get(IDX).setSourceid(CONTENTID);
					pObjContent.getZappLinkedObjects().get(IDX).setLinkedobjid(ZappKey.getPk(pObjContent.getZappLinkedObjects().get(IDX)));
				}
			}			
			
			pObjRes = addObject(pObjAuth, pObjContent.getZappLinkedObjects(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_LINK", "[addContent] " + messageService.getMessage("ERR_C_LINK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}		
		
		/* [Share info.]
		 * 
		 */
		if(pObjContent.getZappSharedObjects().size() > ZERO) {

			// File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappSharedObjects().size(); IDX++ ) {
					pObjContent.getZappSharedObjects().get(IDX).setSobjid(CONTENTID);
					pObjContent.getZappSharedObjects().get(IDX).setSobjtype(pObjContent.getObjType());		// Bundle , File
					pObjContent.getZappSharedObjects().get(IDX).setShareobjid(ZappKey.getPk(pObjContent.getZappSharedObjects().get(IDX)));
				}
			}
			
			pObjRes = addObject(pObjAuth, pObjContent.getZappSharedObjects(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_SHARE", "[addContent] " + messageService.getMessage("ERR_C_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}	
		
		/* [Access control info.]
		 * 
		 */
		if(pObjContent.getZappAcls().size() > ZERO) {
			
			//File
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				for(int IDX = ZERO; IDX < pObjContent.getZappAcls().size(); IDX++ ) {
					pObjContent.getZappAcls().get(IDX).setContentid(CONTENTID);
					pObjContent.getZappAcls().get(IDX).setContenttype(pObjContent.getObjType());
					pObjContent.getZappAcls().get(IDX).setAclid(ZappKey.getPk(pObjContent.getZappAcls().get(IDX)));
				}
			}			
			
			pObjRes = aclService.addObject(pObjAuth, pObjContent.getZappAcls(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_CONTENTACL", "[addContent] " + messageService.getMessage("ERR_C_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}	

		/* [Approval]
		 * 
		 */
		/* jwjang 20220428
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, pObjContent.getZappBundle(), rZappClassification, pObjRes);
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, pObjContent.getZappFile(), rZappClassification, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[addContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		*/
		
		/* [Result]
		 * 
		 */
		pObjRes.setResObj(CONTENTID);

		return pObjRes;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow(), STATE_NOW = BLANK, CTYPE = BLANK;
		boolean SKIP_CONTENTACL = false, IN_EXPIRETIME = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		ZappContentRes pObjContentLog = logService.initLogRes(null, ZappConts.ACTION.CHANGE);
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);

		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle( new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappAcls() == null) { pObjContent.setZappAcls(new ArrayList<ZappContentAcl>()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappFiles() == null) { pObjContent.setZappFiles(new ArrayList<ZappFile>()); }
		if(pObjContent.getZappKeywords() == null) { pObjContent.setZappKeywords(new ArrayList<ZappKeywordExtend>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "changeContent", ZappConts.ACTION.CHANGE_PK);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_CHANGE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_CHANGE_YN.env);			// Approval or not
		ZappEnv SYS_APPROVAL_CHANGE_YN = new ZappEnv(); SYS_APPROVAL_CHANGE_YN.setSetval(NO);
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether content access control info. are applied
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
				
		/* [Default]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjContent.getZappBundle().setBundleid(pObjContent.getContentid());
			pObjContent.getZappBundle().setUpdatetime(PROCTIME);
			if(ZstFwValidatorUtils.valid(pObjContent.getObjRetention())) {
				pObjContent.getZappBundle().setRetentionid(pObjContent.getObjRetention());
			}
			if(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getExpiretime()) == true) {	// Key-In
				if(isDateFormat(pObjContent.getZappBundle().getExpiretime()) == true) {
					IN_EXPIRETIME = true;
				} else {
					return ZappFinalizing.finalising("ERR_NIDENT_DATEFORMAT", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			pObjContent.getZappFile().setMfileid(pObjContent.getContentid());
			if(ZstFwValidatorUtils.valid(pObjContent.getObjRetention())) {
				pObjContent.getZappFile().setRetentionid(pObjContent.getObjRetention());
			}
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getExpiretime()) == true) {	// Key-In
				if(isDateFormat(pObjContent.getZappFile().getExpiretime()) == true) {
					IN_EXPIRETIME = true;
				} else {
					return ZappFinalizing.finalising("ERR_NIDENT_DATEFORMAT", "[addContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
	
		/** [Get classification info.]
		 * 
		 */
//		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getZappClassObject().getClassid()), pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_R_CLASS", "[changeContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
//		if(rZappClassification == null) {
//			return ZappFinalizing.finalising("ERR_R_CLASS", "[changeContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		// Apply approval process
//		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
//			SYS_APPROVAL_CHANGE_YN.setSetval(workflowService.doApply(rZappClassification.getWfrequired(), ZappConts.ACTION.CHANGE) == true ? YES : NO);
//		} else {
//			if(validAcsRoute(pObjContent) == true) {
//				SYS_APPROVAL_CHANGE_YN.setSetval(NO);
//			} else {
//				return ZappFinalizing.finalising("ERR_NIDENT_CODE", "[changeContent] " + messageService.getMessage("ERR_NIDENT_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
//		}
		
		ZappClassification rZappFolder = null, rZappDoctype = null, rZappClass = null;
		ZappClassObject pZappClassObject = new ZappClassObject();
		pZappClassObject.setCobjid(pObjContent.getContentid());
		pZappClassObject.setCobjtype(pObjContent.getObjType());
		pObjRes = selectExtendObject(pObjAuth, pZappClassObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[changeContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappClassification> rZappClassList = (List<ZappClassification>) pObjRes.getResObj();
		if(rZappClassList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[changeContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappClassification vo : rZappClassList) {
			if(isNodeType(vo.getTypes()) == true) {
				rZappFolder = vo;
			}
			if(vo.getTypes().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
				rZappDoctype = vo;
			}
			if(vo.getTypes().equals(ZappConts.TYPES.CLASS_CLASS.type)) {
				rZappClass = vo;
			}
		}

		if(rZappFolder == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[changeContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		// Apply approval process
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			SYS_APPROVAL_CHANGE_YN.setSetval(workflowService.doApply(rZappFolder.getWfrequired(), ZappConts.ACTION.CHANGE) == true ? YES : NO);
		} else {
			if(validAcsRoute(pObjContent) == true) {
				SYS_APPROVAL_CHANGE_YN.setSetval(NO);
			} else {
				return ZappFinalizing.finalising("ERR_NIDENT_CODE", "[changeContent] " + messageService.getMessage("ERR_NIDENT_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}

		
		/* [Get info. by type]
		 * If the current user is a holder, bypass permission checks.
		 */
		ZappBundle rZappBundle = null; ZappFile rZappFile = null; ZArchMFileRes rZArchMFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[changeContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj(); 
			if(rZappBundle == null) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[changeContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rZappBundle.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
			STATE_NOW = rZappBundle.getState();
			pObjContentLog.setContentno(rZappBundle.getBno());
			
			/* Bundle Type */
			CTYPE = rZappBundle.getBtype();
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			/* File */
			ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
			pZappFile.setObjTaskid(pObjContent.getObjTaskid());
			pObjRes = getFile(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFileRes) pObjRes.getResObj(); 
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			STATE_NOW = rZArchMFile.getState();
			
			/* Sub-file info. */
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappFile = (ZappFile) pObjRes.getResObj(); 
			if(rZappFile == null) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
			pObjContentLog.setContentno(rZappFile.getFno());
			
		}
		
		/* [Check state]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			if(!STATE_NOW.equals(ZappConts.STATES.BUNDLE_NORMAL.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[changeContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		} else {
			if(!STATE_NOW.equals(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[changeContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			SKIP_CONTENTACL = true;
		}
		
		/* [Exclude permission check]
		 * 1. When the folder type is USER, the current user matches
		 * 2. When the folder type is COMPANY, the current user is the holder
		 */
		if(rZappFolder.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			if(rZappFolder.getHolderid().equals(pObjAuth.getSessDeptUser().getUserid())) {
				SKIP_CONTENTACL = true;
			}
		}
		if(rZappFolder.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
			if(rZappFolder.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
		}
		
		/* [Manager Mode]
		 * In case of administrator function, permission check is excluded. 
		 */
		if(pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Check content access control info.]
		 *
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid() + DIVIDER + pObjContent.getZappClassObject().getClassid());		// Classification ID + Content ID
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[changeContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 수정할 수 있는 권한이 없습니다.
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[changeContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 수정할 수 있는 권한이 없습니다.
			}			
		}
		
		/* [Temporary info.]
		 * 
		 */
		ZappTmpObject pZappTmpObject = null;
		if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pZappTmpObject = new ZappTmpObject();
			pZappTmpObject.setTobjid(pObjContent.getContentid());
			pZappTmpObject.setTobjtype(pObjContent.getObjType());
			pZappTmpObject.setTmptime(PROCTIME);
			pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pZappTmpObject.setTaskid(pObjContent.getObjTaskid());	// 2021-09-16
		}
		
		/* Title  */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			if(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getTitle())) {
				if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getTitle(), rZappBundle.getTitle()) == false) {
					pObjContentLog.setTitle(rZappBundle.getTitle() + " -> " + pObjContent.getZappBundle().getTitle());
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
						pZappTmpObject.setTitle(pObjContent.getZappBundle().getTitle());
					} 
				} else {
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.NO.use)) {
						if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getTitle(), rZappBundle.getTitle()) == true) {
							pObjContent.getZappBundle().setTitle(null); // None
						}
					}
				}
			}
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {	
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getFilename())) {
				if(ZstFwValidatorUtils.isIdentical(rZArchMFile.getzArchVersion().getFilename(), pObjContent.getZappFile().getFilename()) == false) {
					pObjContentLog.setTitle(rZArchMFile.getzArchVersion().getFilename() + " -> " + pObjContent.getZappFile().getFilename());
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
						pZappTmpObject.setTitle(pObjContent.getZappFile().getFilename());
					} 
				} else {
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.NO.use)) {
						pObjContent.getZappFile().setFilename(null); // None
					}
				}
			}
		}
		
		/* Holder */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			if(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getHolderid())) {
				if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getHolderid(), rZappBundle.getHolderid()) == false) {
					pObjContentLog.setHolderid(rZappBundle.getHolderid() + " -> " + pObjContent.getZappBundle().getHolderid());
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
						pZappTmpObject.setHolderid(pObjContent.getZappBundle().getHolderid());
					}
				} else {
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.NO.use)) {
						if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getHolderid(), rZappBundle.getHolderid()) == true) {
							pObjContent.getZappBundle().setHolderid(null); // None
						}
					}
				}
			}
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getHolderid())) {
				if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappFile().getHolderid(), rZappFile.getHolderid()) == false) {
					pObjContentLog.setHolderid(rZappFile.getHolderid() + " -> " + pObjContent.getZappFile().getHolderid());
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
						pZappTmpObject.setHolderid(pObjContent.getZappFile().getHolderid());
					}
				} else {
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.NO.use)) {
						if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappFile().getHolderid(), rZappFile.getHolderid()) == true) {
							pObjContent.getZappFile().setHolderid(null); // 처리 안함
						}
					}
				}
			}			
		} // End of if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
		
		/* Retention period */
		boolean CHANGED_RETENTION = false; String EXPTIME = BLANK;
		if(ZstFwValidatorUtils.valid(pObjContent.getObjRetention()) || IN_EXPIRETIME == true) {
			if(IN_EXPIRETIME == true) {
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					EXPTIME = ZstFwValidatorUtils.fixNullString(pObjContent.getZappBundle().getExpiretime());
					if(!rZappBundle.getExpiretime().equals(EXPTIME)) { 
						CHANGED_RETENTION = true;
						pObjContent.getZappBundle().setRetentionid(BLANK); pObjContent.getZappBundle().setExpiretime(EXPTIME);
						pObjContentLog.setExpiretime(rZappBundle.getExpiretime() + " -> " + EXPTIME);
					}
				} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					EXPTIME = ZstFwValidatorUtils.fixNullString(pObjContent.getZappFile().getExpiretime());
					if(!rZappFile.getExpiretime().equals(EXPTIME)) { 
						CHANGED_RETENTION = true;
						pObjContent.getZappFile().setRetentionid(BLANK); pObjContent.getZappFile().setExpiretime(EXPTIME);
						pObjContentLog.setExpiretime(rZappFile.getExpiretime() + " -> " + EXPTIME);
					}
				}
				if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(YES)) {
					pZappTmpObject.setRetentionid(BLANK); pZappTmpObject.setExpiretime(EXPTIME);
				}
			} else {
				if(ZstFwValidatorUtils.valid(pObjContent.getObjRetention())) {
					if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						if(!rZappBundle.getRetentionid().equals(pObjContent.getObjRetention())) { 
							CHANGED_RETENTION = true;
							pObjContent.getZappBundle().setRetentionid(pObjContent.getObjRetention());
							pObjContentLog.setRetentionid(rZappBundle.getRetentionid() + " -> " + pObjContent.getObjRetention());
						}
					} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						if(!rZappFile.getRetentionid().equals(pObjContent.getObjRetention())) { 
							CHANGED_RETENTION = true; 
							pObjContent.getZappFile().setRetentionid(pObjContent.getObjRetention());
							pObjContentLog.setRetentionid(rZappFile.getRetentionid() + " -> " + pObjContent.getObjRetention());
						}
					}
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(YES)) {
						pZappTmpObject.setRetentionid(pObjContent.getObjRetention()); pZappTmpObject.setExpiretime(BLANK);
					}
				}
			}
		}
		if(CHANGED_RETENTION == true && ZstFwValidatorUtils.valid(EXPTIME) == false) {
			pObjRes = systemService.selectObject(pObjAuth, new ZappCode(pObjContent.getObjRetention()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == true) {
				ZappCode rZappCode = (ZappCode) pObjRes.getResObj();
				if(rZappCode == null) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[changeContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				try {
					
					String NEW_CRTTIME = BLANK;
					if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						NEW_CRTTIME = ZstFwValidatorUtils.valid(rZappBundle.getCreatetime()) ? rZappBundle.getCreatetime().substring(0, 10) : BLANK;
					} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						NEW_CRTTIME = ZstFwValidatorUtils.valid(rZArchMFile.getCreatetime()) ? rZArchMFile.getCreatetime().substring(0, 10) : BLANK;
					}
					
					if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(YES)) {
//						pZappTmpObject.setRetentionid(pObjContent.getObjRetention());
						if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
							pZappTmpObject.setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode.getCodevalue()), NEW_CRTTIME));
						} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
							pZappTmpObject.setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode.getCodevalue()), NEW_CRTTIME));
						}						
					} else {
						if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
							pObjContent.getZappBundle().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode.getCodevalue()), NEW_CRTTIME));
						} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
							pObjContent.getZappFile().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode.getCodevalue()), NEW_CRTTIME));
						}
					}
					
				} catch (NumberFormatException e) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[changeContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				} catch (ParseException e) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[changeContent] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		} // End of if(ZstFwValidatorUtils.valid(pObjContent.getObjRetention())) {
		
		/* Additional Info. */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			if(utilBinder.isEmpty(pObjContent.getZappAdditoryBundle()) == false) { 
				if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
					pZappTmpObject.setAddinfo(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappAdditoryBundle()));
				}
			}
		}
		
		/* Content Type */
		if(pObjContent.getZappClassObject() != null) {
			if(pObjContent.getZappClassObject().getClasstype() != null && 
					pObjContent.getZappClassObject().getClasstype().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
				boolean CHANGED_DOCTYPE = false;
		
				if(rZappDoctype == null) {
					CHANGED_DOCTYPE = true;
				} else {
					if(ZstFwValidatorUtils.isIdentical(rZappDoctype.getClassid(), pObjContent.getZappClassObject().getClassid()) == false) {
						if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(YES)) {
							pZappTmpObject.setClasses(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappClassObject()));
						} else {
							CHANGED_DOCTYPE = true;
						}
					}
				}
				if(CHANGED_DOCTYPE == true) {

					ZappClassObject p_IN = new ZappClassObject();
					p_IN.setClassid(pObjContent.getZappClassObject().getClassid());
					p_IN.setClasstype(ZappConts.TYPES.CLASS_DOCTYPE.type);
					p_IN.setCobjid(pObjContent.getContentid());
					p_IN.setCobjtype(pObjContent.getObjType());
					p_IN.setClassobjid(ZappKey.getPk(p_IN));
					pObjRes = addObject(pObjAuth, p_IN, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_CLSOBJ", "[changeContent] " + messageService.getMessage("ERR_C_CLSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}

					if(rZappDoctype != null) {
						ZappClassObject p_OUT = new ZappClassObject();
						p_OUT.setClassid(rZappDoctype.getClassid());
						p_OUT.setClasstype(ZappConts.TYPES.CLASS_DOCTYPE.type);
						p_OUT.setCobjid(pObjContent.getContentid());
						p_OUT.setCobjtype(pObjContent.getObjType());
						pObjRes = deleteObject(pObjAuth, new ZappClassObject(ZappKey.getPk(p_OUT)), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_CLSOBJ", "[changeContent] " + messageService.getMessage("ERR_D_CLSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					} 
				}
			}
		}
		
		/* Update basic info. */
		if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.NO.use)) {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = changeObject(pObjAuth, pObjContent.getZappBundle(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[changeContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Additional Info.
				if(pObjContent.getZappAdditoryBundle() != null && SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.NO.use)) {
					if(utilBinder.isEmpty(pObjContent.getZappAdditoryBundle()) == false) { 
						pObjContent.getZappAdditoryBundle().setBundleid(pObjContent.getZappBundle().getBundleid());
						pObjRes = changeObject(pObjAuth, pObjContent.getZappAdditoryBundle(), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_BUNDLEEXT", "[changeContent] " + messageService.getMessage("ERR_E_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				}
				
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				if(utilBinder.isEmpty_File(pObjContent.getZappFile()) == false) {
					pObjRes = changeObject(pObjAuth, pObjContent.getZappFile(), pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_E_FILE", "[changeContent] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
				// Change File Name
				if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getFilename())) {
					ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getZappFile().getMfileid()); pObjAuth.setObjType(ZappConts.TYPES.VIEWTYPE_EDIT.type);
					ZArchVersion pZArchMVersion = new ZArchVersion(); pZArchMVersion.setMfileid(pObjContent.getZappFile().getMfileid());
					
					List<String> objMfileList = new ArrayList<String>();
					try {
						ZArchMFile tmpList = zarchMfileService.rSingleRow_Vo(pZArchMFile);
						if(tmpList != null) {
							objMfileList.add(tmpList.getMfileid());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					pObjAuth.setObjList(objMfileList);
					
					pObjRes = contentService.rMultiRowsMaxVersionFile(pObjAuth, pZArchMFile, pZArchMVersion, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					List<ZArchMFileRes> rZappFileMax = (List<ZArchMFileRes>) pObjRes.getResObj();
					if(rZappFileMax == null) {
						return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					
					for(ZArchMFileRes vo : rZappFileMax) {
						ZArchResult zArchResult = new ZArchResult();
						pZArchMVersion = new ZArchVersion();
						pZArchMVersion.setVersionid(vo.getzArchVersion().getVersionid());
						pZArchMVersion.setFilename(pObjContent.getZappFile().getFilename());
						pZArchMVersion.setHver(null);
						pZArchMVersion.setLver(null);
						try {
							zArchResult = zarchVersionService.uSingleRow(pZArchMVersion);
						} catch (Exception e) {
							return ZappFinalizing.finalising("ERR_E_VERSION", "[changeContent] " + messageService.getMessage("ERR_E_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						if(ZappFinalizing.isSuccess(zArchResult) == false) {
							return ZappFinalizing.finalising("ERR_E_VERSION", "[changeContent] " + messageService.getMessage("ERR_E_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						
					}

				}
				
			} // End of if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
		}

		/* Access control info. */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pZappTmpObject.setAcls(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappAcls()));
			} else {
			
				if(pObjContent.getZappAcls().size() > ZERO) {
					List<ZappContentAcl> ADDs = new ArrayList<ZappContentAcl>();
					List<ZappContentAcl> CHANGEs = new ArrayList<ZappContentAcl>();
					List<ZappContentAcl> DISCARDs = new ArrayList<ZappContentAcl>();
					pObjContentLog.setContentacls(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappAcls()));
					for(int IDX = ZERO; IDX < pObjContent.getZappAcls().size(); IDX++ ) {
						if(pObjContent.getZappAcls().get(IDX).getObjAction().equals(ZappConts.ACTION.ADD.toString())) {				// New
							pObjContent.getZappAcls().get(IDX).setContentid(pObjContent.getContentid());
							pObjContent.getZappAcls().get(IDX).setContenttype(pObjContent.getObjType());
							pObjContent.getZappAcls().get(IDX).setAclid(ZappKey.getPk(pObjContent.getZappAcls().get(IDX)));
							ADDs.add(pObjContent.getZappAcls().get(IDX));
						}
						else if(pObjContent.getZappAcls().get(IDX).getObjAction().equals(ZappConts.ACTION.CHANGE.toString())) {		// Edit
							CHANGEs.add(pObjContent.getZappAcls().get(IDX));
						}
						else if(pObjContent.getZappAcls().get(IDX).getObjAction().equals(ZappConts.ACTION.DISCARD.toString())) {	// Delete
							DISCARDs.add(pObjContent.getZappAcls().get(IDX));
						}
					}
					
					// Add
					pObjRes = aclService.addObject(pObjAuth, ADDs, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_CONTENTACL", "[changeContent] " + messageService.getMessage("ERR_C_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						//# 컨텐츠 권한을 등록하는데 실패했습니다.
					}
					
					// Edit
					for(ZappContentAcl vo : CHANGEs) {
						vo.setContentid(null); vo.setContenttype(null); vo.setAclobjid(null); vo.setAclobjtype(null); 
						pObjRes = aclService.changeObject(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_E_CONTENTACL", "[changeContent] " + messageService.getMessage("ERR_E_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							//# 컨텐츠 권한을 변경하는데 실패했습니다.
						}
					}
					
					// Delete
					if(DISCARDs.size() > ZERO) {
						StringBuffer pks = new StringBuffer();
						for(ZappContentAcl vo : DISCARDs) {
							pks.append(vo.getAclid() + DIVIDER);
						}
						ZappContentAcl pZappContentAcl = new ZappContentAcl(pks.toString());
						pZappContentAcl.setContentid(pObjContent.getContentid());
						pObjRes = aclService.deleteObject(pObjAuth, null, pZappContentAcl, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_CONTENTACL", "[changeContent] " + messageService.getMessage("ERR_D_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							//# 컨텐츠 권한을 삭제하는데 실패했습니다.
						}
					}
				}
			}
		}
		
		/* Classification */
		if(pObjContent.getZappClassObjects().size() > ZERO) {
			
			if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pZappTmpObject.setClasses(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappClassObjects()));
			} else {
				// Get info.
//				ZappClassObject pZappClassObject = new ZappClassObject();
//				pZappClassObject.setCobjid(pObjContent.getContentid());
//				pZappClassObject.setCobjtype(pObjContent.getObjType());
//				pObjRes = selectObject(pObjAuth, pZappClassObject, pObjRes);
//				if(ZappFinalizing.isSuccess(pObjRes) == false) {
//					return ZappFinalizing.finalising("ERR_R_CLASS", "[changeContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
//				List<ZappClassObject> rZappClassObjectList = (List<ZappClassObject>) pObjRes.getResObj();
				
				List<ZappClassObject> pZCO_ADD = new ArrayList<ZappClassObject>();
				List<ZappClassObject> pZCO_DELETE = new ArrayList<ZappClassObject>();
				for(ZappClassObject vo : pObjContent.getZappClassObjects()) {
					if(!vo.getClasstype().equals(ZappConts.TYPES.CLASS_FOLDER.type)) {
						if(vo.getObjAction().equals(ZappConts.ACTION.ADD.toString())) {			
							pZCO_ADD.add(vo);
						}
						if(vo.getObjAction().equals(ZappConts.ACTION.DISCARD.toString())) {	
							pZCO_DELETE.add(vo);
						}
					}
				}
				
				// Add
				if(pZCO_ADD.size() > ZERO) {
					for(int IDX = ZERO; IDX < pZCO_ADD.size(); IDX++) {
						pZCO_ADD.get(IDX).setCobjid(pObjContent.getContentid());
						pZCO_ADD.get(IDX).setCobjtype(pObjContent.getObjType());
						pZCO_ADD.get(IDX).setClassobjid(ZappKey.getPk(pZCO_ADD.get(IDX)));
					}
					pObjRes = addObject(pObjAuth, pZCO_ADD, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_CLASS", "[changeContent] " + messageService.getMessage("ERR_C_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						//# 분류를 등록하는데 실패했습니다.
					}
				}
				
				// Delete
				if(pZCO_DELETE.size() > ZERO) {
					StringBuffer pks = new StringBuffer();
					for(ZappClassObject vo : pZCO_DELETE) {
						pks.append(vo.getClassid() + DIVIDER);
					}
					pZappClassObject = new ZappClassObject(pks.toString(), null, null);
					pZappClassObject.setCobjid(pObjContent.getContentid());
					pZappClassObject.setCobjtype(pObjContent.getObjType());
					pObjRes = deleteObject(pObjAuth, pZappClassObject, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_D_CLASS", "[changeContent] " + messageService.getMessage("ERR_D_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						//# 분류를 삭제하는데 실패했습니다.
					}
				}
			}
		}	
		
		/* Keyword */
		if(pObjContent.getZappKeywords().size() > ZERO) {
			if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pZappTmpObject.setKeywords(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappKeywords()));
			} else {
				List<ZappKeyword> ADDs = new ArrayList<ZappKeyword>();
				List<ZappKeywordObject> ADDObjects = new ArrayList<ZappKeywordObject>();
				List<ZappKeywordExtend> DISCARDs = new ArrayList<ZappKeywordExtend>();
				pObjContentLog.setKeywords(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappKeywords()));
				for(ZappKeywordExtend vo : pObjContent.getZappKeywords()) {
					if(vo.getObjAction().equals(ZappConts.ACTION.ADD.name())) {
						// Keyword
						ZappKeyword pZappKeyword = new ZappKeyword();
						pZappKeyword.setKword(vo.getKword());
						pZappKeyword.setIsactive(YES);
						pZappKeyword.setKwordid(ZappKey.getPk(pZappKeyword));
						ADDs.add(pZappKeyword);
						
						// Keyword Object
						ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
						pZappKeywordObject.setKobjid(pObjContent.getContentid());
						pZappKeywordObject.setKobjtype(pObjContent.getObjType());
						pZappKeywordObject.setKwordid(pZappKeyword.getKwordid());
						pZappKeywordObject.setKwobjid(ZappKey.getPk(pZappKeywordObject));
						ADDObjects.add(pZappKeywordObject);
					}
					if(vo.getObjAction().equals(ZappConts.ACTION.DISCARD.name())) {
						DISCARDs.add(vo);
					}
				}
				
				// Add
				if(ADDObjects.size() > ZERO) {
					for(ZappKeyword vo : ADDs) {
						pObjRes = addObjectExist(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_C_KEYWORD", "[changeContent] " + messageService.getMessage("ERR_C_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
					pObjRes = addObject(pObjAuth, ADDObjects, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_C_KEYWORDOBJ", "[changeContent] " + messageService.getMessage("ERR_C_KEYWORDOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
					pObjRes = deleteObject(pObjAuth, pZappKeywordObject_Filter, pZappKeywordObject_Where, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_D_KEYWORDOBJ", "[changeContent] " + messageService.getMessage("ERR_D_KEYWORDOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			}
		}
		
		/* File */
		List<ZappFile> pFTRFileList = new ArrayList<ZappFile>();	// For FTR
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type) && pObjContent.getZappFiles().size() > ZERO) {
			if(CTYPE.equals(ZappConts.TYPES.BUNDLE_NORMAL.type)) {
				if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
					
					// Real file
					for(int IDX = ZERO; IDX < pObjContent.getZappFiles().size(); IDX++) {
						if(pObjContent.getZappFiles().get(IDX).getAction().equals(ZappConts.ACTION.ADD.toString())) {
							pObjContent.getZappFiles().get(IDX).setObjWorkplaceid(ZappKey.getPk(pZappTmpObject)); 	// Temp. ID
							pObjRes = addTmpFile(pObjAuth, pObjContent.getZappFiles().get(IDX), pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_C_FILE", "[changeContent] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
							ZappFile rtZappFile = (ZappFile) pObjRes.getResObj();
							if(rtZappFile != null) {
								pObjContent.getZappFiles().get(IDX).setObjFileName(rtZappFile.getObjFileName());
							}
						}
					}
					pZappTmpObject.setFiles(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappFiles()));
					
				} else {
					
					List<ZappFile> ADDs = new ArrayList<ZappFile>();
					List<ZappFile> DISCARDs = new ArrayList<ZappFile>();
					pObjContentLog.setFiles(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappFiles()));
					for(int IDX = ZERO; IDX < pObjContent.getZappFiles().size(); IDX++ ) {
						pObjContent.getZappFiles().get(IDX).setObjTaskid(pObjContent.getObjTaskid());	// Task ID
						pObjContent.getZappFiles().get(IDX).setLinkid(pObjContent.getContentid());
						if(pObjContent.getZappFiles().get(IDX).getAction().equals(ZappConts.ACTION.ADD.toString())) {		// Adding
							ADDs.add(pObjContent.getZappFiles().get(IDX));
						}
						if(pObjContent.getZappFiles().get(IDX).getAction().equals(ZappConts.ACTION.DISCARD.toString())) {	// Deleting
							DISCARDs.add(pObjContent.getZappFiles().get(IDX));
						}
					}
		
					// Add
					for(ZappFile vo : ADDs) {
						
						// File
						pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());				// Task ID	
						pObjContent.getZappFile().setLinkid(pObjContent.getContentid());				// Bundle ID
						pObjRes = addFile(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_C_FILE", "[changeContent] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						pObjRes.setResCode(SUCCESS);
						
						// Sub-Bundle info.
						/*
						vo.setMfileid((String) pObjRes.getResObj());
						if(ZstFwValidatorUtils.valid(vo.getObjFileExt())) {
							vo.setExt(ZstFwFileUtils.getExtension(vo.getObjFileExt()));
						} else {
							vo.setExt(ZstFwFileUtils.getExtension(vo.getObjFileName()));
						}
						vo.setRetentionid(ZstFwValidatorUtils.valid(pObjContent.getObjRetention()) ? pObjContent.getObjRetention() : rZappBundle.getRetentionid());
						pObjRes = addFileExtension(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_C_FILEEXT", "[changeContent] " + messageService.getMessage("ERR_C_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						*/
						
						// FTR
						try {
							ZArchVersion rZArchVersion = zarchVersionService.rSingleRow_Vo(new ZArchVersion((String) pObjRes.getPath()));
							if(rZArchVersion != null) {
								ZappFile pZappFile = new ZappFile(rZArchVersion.getUfileid());
								pZappFile.setAction(ZappConts.ACTION.ADD.name());
								pFTRFileList.add(pZappFile);
							}
						} catch (Exception e) {
							return ZappFinalizing.finalising("ERR_R_VERSION", "[changeContent] " + messageService.getMessage("ERR_R_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
	
					}
		
					// Delete
					for(ZappFile vo : DISCARDs) {
						
						// Sub-Bundle info.
						pObjRes = deleteFileExtension(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_FILEEXT", "[changeContent] " + messageService.getMessage("ERR_D_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						
						// File
						pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());				// Task ID	
						pObjRes = deleteFile(pObjAuth, vo, pObjRes);
						if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_FILE", "[changeContent] " + messageService.getMessage("ERR_D_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						pObjRes.setResCode(SUCCESS);
	
						// FTR
						try {
							ZArchVersion pZArchVersion = new ZArchVersion();
							pZArchVersion.setMfileid(vo.getMfileid());
							List<ZArchVersion> rZArchVersion = zarchVersionService.rMultiRows_List(pZArchVersion);
							if(rZArchVersion != null) {
								for(ZArchVersion vvo : rZArchVersion) {
									ZappFile pZappFile = new ZappFile(vvo.getUfileid());
									pZappFile.setAction(ZappConts.ACTION.DISCARD.name());
									pFTRFileList.add(pZappFile);
								}
							}
						} catch (Exception e) {
							return ZappFinalizing.finalising("ERR_R_VERSION", "[changeContent] " + messageService.getMessage("ERR_R_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}			
				}
				
				
			} else if(CTYPE.equals(ZappConts.TYPES.BUNDLE_VIRTUAL.type)) {
				
				if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(YES)) {
					pZappTmpObject.setFiles(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappFiles()));
				} else {
					List<String> ADDs = new ArrayList<String>();
					List<String> DISCARDs = new ArrayList<String>();
					pObjContentLog.setFiles(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappFiles()));
					for(int IDX = ZERO; IDX < pObjContent.getZappFiles().size(); IDX++ ) {
						if(pObjContent.getZappFiles().get(IDX).getAction().equals(ZappConts.ACTION.ADD.toString())) {		// Adding
							ADDs.add(pObjContent.getZappFiles().get(IDX).getMfileid());
						}
						if(pObjContent.getZappFiles().get(IDX).getAction().equals(ZappConts.ACTION.DISCARD.toString())) {	// Deleting
							DISCARDs.add(pObjContent.getZappFiles().get(IDX).getMfileid());
						}
					}
					
					// Adding
					for(String id : ADDs) {
						
					}
					
					// Deleting
					for(String targetid : DISCARDs) {
						ZappLinkedObject pZappLinkedObject = new ZappLinkedObject();
						pZappLinkedObject.setSourceid(pObjContent.getContentid());
						pZappLinkedObject.setTargetid(targetid);
						pObjRes = deleteObject(pObjAuth, pZappLinkedObject, pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_D_LINKOBJ", "[changeContent] " + messageService.getMessage("ERR_D_LINKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
					}
				}
			}
		}		
		
		/* [Temporary info.]
		 * 
		 */
		if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			// Register temp. info.
			pObjRes = addObject(pObjAuth, pZappTmpObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_TEMP", "[changeContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Commence workflow
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, pObjContent.getZappBundle(), rZappFolder, pObjRes);
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, pObjContent.getZappFile(), rZappFolder, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[changeContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Change state
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
				pZappBundle.setState(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state);
				pObjRes = changeObject(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[changeContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}			
				
				// Drafter
				ZappAdditoryBundle pZappAdditoryBundle = new ZappAdditoryBundle(pObjContent.getContentid());
				pZappAdditoryBundle.setDrafter(pObjAuth.getSessDeptUser().getDeptuserid() + DIVIDER + pObjAuth.getSessUser().getName());	
				pObjRes = changeObject(pObjAuth, pZappAdditoryBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLEEXT", "[changeContent] " + messageService.getMessage("ERR_E_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}			
				
				
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				ZArchResult zArchResult = new ZArchResult();
				try {
					ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getContentid());
					pZArchMFile.setState(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state);
					zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}
				
				// Drafter
				ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
				pZappFile.setDrafter(pObjAuth.getSessDeptUser().getDeptuserid() + DIVIDER + pObjAuth.getSessUser().getName());	
				pObjRes = changeObject(pObjAuth, pZappFile, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_FILEEXT", "[changeContent] " + messageService.getMessage("ERR_E_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
			}
			
			
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getTitle(), rZappBundle.getTitle()) == false) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getTitle()) ? pObjContent.getZappBundle().getTitle() : rZappBundle.getTitle());
				} else {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , (SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.LOGS.ACTION_CHANGE_REQUEST.log : ZappConts.LOGS.ACTION_CHANGE.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}	
		
		/* [FTR]
		 * Updating FTR indexes
		 */
		if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(NO)) {
			pObjContent.setContentid(pObjContent.getContentid());
			pObjContent.setZappFiles(pFTRFileList);
			try {
				
				pObjContent.setObjHandleType(ZappConts.ACTION.CHANGE.name());
				
				pObjRes = ftrService.executeIndex(pObjAuth, pObjContent, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_INDEX", "[changeContent] " + messageService.getMessage("ERR_E_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} catch (IOException e) {
				return ZappFinalizing.finalising("ERR_E_INDEX", "[changeContent] " + messageService.getMessage("ERR_E_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}

		
		return pObjRes;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult disableContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String CONTENTID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		boolean SKIP_CONTENTACL = false;
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);		
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "disableContent", ZappConts.ACTION.DISABLE_PK);
		
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_DISABLE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_DISABLE_YN.env);			// Approval for deleting or not
		ZappEnv SYS_APPROVAL_DISABLE_YN = new ZappEnv(); SYS_APPROVAL_DISABLE_YN.setSetval(NO);
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		SYS_APPROVAL_DISABLE_YN.setSetval(NO);
		
		// Content ID
		CONTENTID = pObjContent.getContentid();
		
		/** [Get classification info.]
		 * 
		 */
//		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getZappClassObject().getClassid()), pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_R_CLASS", "[changeContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
//		if(rZappClassification == null) {
//			return ZappFinalizing.finalising("ERR_R_CLASS", "[changeContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		if((rZappClassification.getWfrequired() | ZappConts.WORKFLOWS.WF_OBJECT_DELETE.iwt) == ZappConts.WORKFLOWS.WF_OBJECT_DELETE.iwt
//				|| rZappClassification.getWfrequired() == ZappConts.WORKFLOWS.WF_OBJECT_ALL.iwt) {
//			SYS_APPROVAL_DISABLE_YN.setSetval(YES); // Apply approval process
//		}
//		SYS_APPROVAL_DISABLE_YN.setSetval(workflowService.doApply(rZappClassification.getWfrequired(), ZappConts.ACTION.DISABLE) == true ? YES : NO);
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			pObjRes = selectObject(pObjAuth, new ZappBundle(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[disableContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj(); 

		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
			pZappFile.setObjTaskid(pObjContent.getObjTaskid());
			pObjRes = getFile(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[disableContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); 
			
			// Sub-Bundle info.
			pObjRes = selectObject(pObjAuth, new ZappFile(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[disableContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj(); 
			
		}
		
		/* [Manage mode]
		 * In case of administrator function, permission check is excluded.
		 */
		if(pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			if(rZappBundle.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
//				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[disableContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
			}
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
//				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[disableContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
			}			
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid() + DIVIDER + pObjContent.getZappClassObject().getClassid());		// Classification ID + Content ID
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CONTENTACL", "[disableContent] " + messageService.getMessage("ERR_D_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 수정할 수 있는 권한이 없습니다.
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[changeContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 수정할 수 있는 권한이 없습니다.
			}			
		}

		
		/* [삭제 처리]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			ZappBundle pZappBundle = new ZappBundle(CONTENTID);
			
			/* Check state */
			if(!rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[disableContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Disable */
			if(SYS_APPROVAL_DISABLE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				
				// Temporary info.
				ZappTmpObject pZappTmpObject = new ZappTmpObject();
				pZappTmpObject.setTobjid(CONTENTID);
				pZappTmpObject.setTobjtype(pObjContent.getObjType());
				pZappTmpObject.setTmptime(PROCTIME);
				pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
				List<String> STATES = new ArrayList<String>(Arrays.asList(new String[]{ rZappBundle.getState(), ZappConts.STATES.BUNDLE_DISCARD_WAIT.state }));
				pZappTmpObject.setStates(ZappJSONUtils.cvrtObjToJson(STATES));
				pObjRes = addObject(pObjAuth, pZappTmpObject, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_TEMP", "[disableContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pZappBundle.setState(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state);	// Request for deleting
				
			} else {
				pZappBundle.setState(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state);		// Waiting for discarding
			}
			
			// Change state
			pZappBundle.setDiscarderid(pObjAuth.getSessDeptUser().getDeptuserid());
			pZappBundle.setUpdatetime(PROCTIME);
			pObjRes = changeObject(pObjAuth, pZappBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[disableContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
		
			/* Variable */
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile(CONTENTID);
			
			/* Check state */
			if(!rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[disableContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Disable */
			if(SYS_APPROVAL_DISABLE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				
				// Temporary info.
				ZappTmpObject pZappTmpObject = new ZappTmpObject();
				pZappTmpObject.setTobjid(CONTENTID);
				pZappTmpObject.setTobjtype(pObjContent.getObjType());
				pZappTmpObject.setTmptime(PROCTIME);
				pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
				List<String> STATES = new ArrayList<String>(Arrays.asList(new String[]{ rZArchMFile.getState(), ZappConts.STATES.BUNDLE_DISCARD_WAIT.state }));
				pZappTmpObject.setStates(ZappJSONUtils.cvrtObjToJson(STATES));
				pObjRes = contentService.uSingleRow(pObjAuth, pZappTmpObject, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_TEMP", "[disableContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state);	// Request for deleting
				
			} else {
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state);	    // Waiting for discarding
			}
			
			// Change state
			try {
//				zArchResult = zarchMfileMgtService.updateMFile(pZArchMFile);
				zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			
			// Sub-Bundle info.
			ZappFile pZappFile = new ZappFile(CONTENTID);
			pZappFile.setDiscarderid(pObjAuth.getSessDeptUser().getDeptuserid());
			pZappFile.setUpdatetime(PROCTIME);
			pObjRes = changeObject(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[disableContent] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.DISABLE);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, CONTENTID);
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
				     , pObjContent.getObjType()
				     , (SYS_APPROVAL_DISABLE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.LOGS.ACTION_DISABLE_REQUEST.log : ZappConts.LOGS.ACTION_DISABLE.log
				     , LOGMAP
				     , PROCTIME
				     , pObjRes);			
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult enableContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String CONTENTID = BLANK;
		boolean SKIP_CONTENTACL = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);		
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "enableContent", ZappConts.ACTION.ENABLE_PK);
		
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_YN.env);							// Approval or not
		ZappEnv SYS_APPROVAL_DISABLE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_DISABLE_YN.env);			// Approval for deleting or not
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		SYS_APPROVAL_DISABLE_YN.setSetval(NO);
		
		// Content ID
		CONTENTID = pObjContent.getContentid();
		
		/* [Check access control info.]
		 * 
		 */
//		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
//			ZappContentAcl pZappContentAcl = new ZappContentAcl();
//			pZappContentAcl.setContentid(CONTENTID + DIVIDER + pObjContent.getZappClassObject().getClassid());
//			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
//			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[disableContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
//			}
//			boolean CANDO = (Boolean) pObjRes.getResObj();
//			if(CANDO == false) {
//				return ZappFinalizing.finalising("ERR_NO_ACL", "[disableContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
//			}			
//		}
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			pObjRes = selectObject(pObjAuth, new ZappBundle(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[disableContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj(); 

		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
			pZappFile.setObjTaskid(pObjContent.getObjTaskid());
			pObjRes = getFile(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[disableContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); 
			
			// Additionnal info.
			pObjRes = selectObject(pObjAuth, new ZappFile(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[disableContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj(); 
		}		
		
		/* [Manage mode]
		 * In case of administrator function, permission check is excluded.
		 */
		if(pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(SKIP_CONTENTACL == false) {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				if(!rZappBundle.getDiscarderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
					return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[disableContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
	//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				if(!rZappFile.getDiscarderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
					return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[disableContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
	//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
				}			
			}		
		}
		
		/* [Restore]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* Variable */
			ZappBundle pZappBundle = new ZappBundle(CONTENTID);
			
			/* Check state */
			if(!rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[disableContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Enable */
			if(SYS_APPROVAL_DISABLE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				
				// Temporary info.
				ZappTmpObject pZappTmpObject = new ZappTmpObject();
				pZappTmpObject.setTobjid(CONTENTID);
				pZappTmpObject.setTobjtype(pObjContent.getObjType());
				pZappTmpObject.setTmptime(PROCTIME);
				pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
				List<String> STATES = new ArrayList<String>(Arrays.asList(new String[]{ rZappBundle.getState(), ZappConts.STATES.BUNDLE_NORMAL.state }));
				pZappTmpObject.setStates(ZappJSONUtils.cvrtObjToJson(STATES));
				pObjRes = addObject(pObjAuth, pZappTmpObject, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_TEMP", "[disableContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pZappBundle.setState(ZappConts.STATES.BUNDLE_ENABLE_REQUEST.state);	// Request for deleting
				
			} else {
				pZappBundle.setState(ZappConts.STATES.BUNDLE_NORMAL.state);		// Waiting for discarding
			}
			
			pZappBundle.setDiscarderid(BLANK);		// Discarder ID 
			pZappBundle.setUpdatetime(PROCTIME);	// Update time
			
			// Change state
			pObjRes = changeObject(pObjAuth, pZappBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[disableContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
		
			/* Variable */
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile(CONTENTID);
//			BeanUtils.copyProperties(pObjContent.getZappFile(), pZArchMFile);
			
			/* Check state */
			if(!rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[disableContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Disable 처리 */
			if(SYS_APPROVAL_DISABLE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				
				// Temporary info.
				ZappTmpObject pZappTmpObject = new ZappTmpObject();
				pZappTmpObject.setTobjid(CONTENTID);
				pZappTmpObject.setTobjtype(pObjContent.getObjType());
				pZappTmpObject.setTmptime(PROCTIME);
				pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
				List<String> STATES = new ArrayList<String>(Arrays.asList(new String[]{ rZArchMFile.getState(), ZappConts.STATES.BUNDLE_NORMAL.state }));
				pZappTmpObject.setStates(ZappJSONUtils.cvrtObjToJson(STATES));
				pObjRes = contentService.uSingleRow(pObjAuth, pZappTmpObject, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_TEMP", "[disableContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_ENABLE_REQUEST.state);	// 복구 요청
				
			} else {
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_NORMAL.state);	    	// 정상
			}
			
			// Change state
			try {
//				zArchResult = zarchMfileMgtService.updateMFile(pZArchMFile);
				zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			
			ZappFile pZappFile = new ZappFile(CONTENTID);
			pZappFile.setDiscarderid(BLANK);		// Discarder ID 
			pZappFile.setUpdatetime(PROCTIME);		// Update time
			pObjRes = changeObject(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[disableContent] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.ENABLE);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, CONTENTID);
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
				     , pObjContent.getObjType()
				     , (SYS_APPROVAL_DISABLE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.LOGS.ACTION_ENABLE_REQUEST.log : ZappConts.LOGS.ACTION_ENABLE.log
				     , LOGMAP
				     , PROCTIME
				     , pObjRes);			
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String CONTENTID = BLANK, CTYPE = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile());  }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "discardContent", ZappConts.ACTION.DISCARD_PK);
		
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_YN.env);							// Approval or not
		ZappEnv SYS_APPROVAL_DISCARD_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_DISCARD_YN.env);			// Approval for discarding or not
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		// Content ID
		CONTENTID = pObjContent.getContentid();
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* Variable */
			pObjRes = selectObject(pObjAuth, new ZappBundle(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[discardContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			
			/* Check state */
			if(!rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state)
					&& !rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_RETURN.state)
					&& !rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state)
					&& !rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_ADD_RETURN.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[discardContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* 권한 체크 */
			if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
				if(!rZappBundle.getDiscarderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
					return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[discardContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					// 컨테츠를 삭제할 수 있는 권한이 없습니다.				
				}
			}
			
			/* */
			CTYPE = rZappBundle.getBtype();
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			/* Variable */
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile();
			BeanUtils.copyProperties(new ZappFile(CONTENTID), pZArchMFile);
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());
			
			try {
				zArchResult = zarchMfileMgtService.loadMFile(pZArchMFile);
			} catch (ZArchApiException e) {
				return ZappFinalizing.finalising_Archive(e.getZArchResult().getCode(), pObjAuth.getObjlang());
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) zArchResult.getResult();
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_FILE", "[discardContent] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Check state */
			if(!rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state)
					&& !rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_RETURN.state)
					&& !rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state)
					&& !rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_ADD_RETURN.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[discardContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Get info. */
			pObjRes = selectObject(pObjAuth, new ZappFile(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[discardContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
			/* Check access control info. */
			if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
				if(!rZappFile.getDiscarderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
					return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[discardContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					// 컨테츠를 삭제할 수 있는 권한이 없습니다.				
				}
			}
			
		}		
		
		/* [Get current folder info.]
		 * 
		 */
		ZappClassification rZappClassification_Original = null;
		ZappClassObject pZappClassObject = new ZappClassObject(); pZappClassObject.setCobjid(CONTENTID); pZappClassObject.setCobjtype(pObjContent.getObjType());
		ZappClassObject rZappClassObject = null;
		pObjRes = selectObject(pObjAuth, pZappClassObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASSOBJ", "[discardContent] " + messageService.getMessage("ERR_NEXIST_CLASSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		@SuppressWarnings("unchecked")
		List<ZappClassObject> rZappClassObjectList = (List<ZappClassObject>) pObjRes.getResObj();
		if(rZappClassObjectList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASSOBJ", "[discardContent] " + messageService.getMessage("ERR_NEXIST_CLASSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappClassObject vo : rZappClassObjectList) {
			rZappClassObject = vo;
		}
		if(rZappClassObject == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASSOBJ", "[discardContent] " + messageService.getMessage("ERR_NEXIST_CLASSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(rZappClassObject.getClassid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[discardContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappClassification_Original = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification_Original == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[discardContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			SYS_APPROVAL_DISCARD_YN.setSetval(workflowService.doApply(rZappClassification_Original.getWfrequired(), ZappConts.ACTION.DISCARD) == true ? YES : NO);
		} else {
			if(validAcsRoute(pObjContent) == true) {
				SYS_APPROVAL_DISCARD_YN.setSetval(NO);
			} else {
				return ZappFinalizing.finalising("ERR_NIDENT_CODE", "[discardContent] " + messageService.getMessage("ERR_NIDENT_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}		
		
		/* [Check access control info.]
		 * 
		 */
//		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
//			ZappContentAcl pZappContentAcl = new ZappContentAcl();
//			pZappContentAcl.setContentid(CONTENTID + DIVIDER + pObjContent.getZappClassObject().getClassid());
//			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
//			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[discardContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
//			}
//			boolean CANDO = (Boolean) pObjRes.getResObj();
//			if(CANDO == false) {
//				return ZappFinalizing.finalising("ERR_NO_ACL", "[discardContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 삭제할 수 있는 권한이 없습니다.
//			}				
//		}
		
		
		/* [Approval]
		 * 
		 */
		if(SYS_APPROVAL_DISCARD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			
			ZappTmpObject pZappTmpObject = new ZappTmpObject();
			pZappTmpObject.setTobjid(pObjContent.getContentid());
			pZappTmpObject.setTobjtype(pObjContent.getObjType());
			pZappTmpObject.setTmptime(PROCTIME);
			pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pZappTmpObject.setTaskid(pObjContent.getObjTaskid());
			
			/* Temporary info. */
			pObjRes = contentService.cSingleRow(pObjAuth, pZappTmpObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_TEMP", "[discardContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Bundle */
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
				pZappBundle.setState(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state);					// Waiting for moving
				pObjRes = contentService.uSingleRow(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[discardContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				ZArchResult zArchResult = new ZArchResult();
				ZArchMFile pZArchMFile = new ZArchMFile();
				pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());	// Task ID
				pZArchMFile.setMfileid(pObjContent.getContentid());
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state);					// Waiting for moving
				try {
//					zArchResult = zarchMfileMgtService.updateMFile(pZArchMFile);
					zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}
			}

			/* */
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth
													    , new ZappBundle(pObjContent.getContentid())
													    , rZappClassification_Original
													    , pObjRes);
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth
														 , new ZappFile(pObjContent.getContentid())
													     , rZappClassification_Original
														 , pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[discardContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			
			/* Log */
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjContent.setZappBundle(rZappBundle);
				pObjContent.setZappFile(rZappFile);
				ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.DISCARD);
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
				} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
				}
				pObjRes = leaveLog(pObjAuth
							     , pObjContent.getObjType()
							     , ZappConts.LOGS.ACTION_DISCARD_REQUEST.log
							     , LOGMAP
							     , PROCTIME
							     , pObjRes);
			}
			
			return pObjRes;
			
		}
		
		/* Link 
		 * 
		 */
		ZappLinkedObject pZappLinkedObject = new ZappLinkedObject();
		pZappLinkedObject.setSourceid(CONTENTID);
		pObjRes = deleteObject(pObjAuth, pZappLinkedObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_링크객체", "[discardContent] " + messageService.getMessage("ERR_삭제_링크객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 링크 객체를 삭제하는데 실패했습니다.
//		}
		
		/* Share info.
		 * 
		 */
		ZappSharedObject pZappSharedObject = new ZappSharedObject();
		pZappSharedObject.setSobjid(CONTENTID);
		pObjRes = deleteObject(pObjAuth, pZappSharedObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_공유객체", "[discardContent] " + messageService.getMessage("ERR_삭제_공유객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 공유 객체를 삭제하는데 실패했습니다.
//		}
		
		/* Classification 
		 * 
		 */
		pZappClassObject = new ZappClassObject();
		pZappClassObject.setCobjid(CONTENTID);
		pObjRes = deleteObject(pObjAuth, pZappClassObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_CLASSOBJ", "[discardContent] " + messageService.getMessage("ERR_D_CLASSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 분류 객체를 삭제하는데 실패했습니다.
		}
		
		/* Locked info.
		 * 
		 */
		ZappLockedObject pZappLockedObject = new ZappLockedObject();
		pZappLockedObject.setLobjid(CONTENTID);							// Target ID
		pZappLockedObject.setLobjtype(pObjContent.getObjType());		// Target type
		pObjRes = deleteObject(pObjAuth, pZappLockedObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_잠금객체", "[discardContent] " + messageService.getMessage("ERR_삭제_잠금객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 잠금 객체를 삭제하는데 실패했습니다.
//		}		
		
		/* Keyword 
		 * 
		 */
		ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
		pZappKeywordObject.setKobjid(CONTENTID);							// Target ID
		pZappKeywordObject.setKobjtype(pObjContent.getObjType());		// Target type
		pObjRes = deleteObject(pObjAuth, pZappKeywordObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_잠금객체", "[discardContent] " + messageService.getMessage("ERR_삭제_잠금객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# Keyword 객체를 삭제하는데 실패했습니다.
//		}	
		
		/* Bundle and file
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* File */
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());		// Task ID
			pObjContent.getZappFile().setLinkid(CONTENTID);
			pObjRes = deleteFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_FILE", "[discardContent] " + messageService.getMessage("ERR_D_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 삭제하는데 실패했습니다.
			}
			pObjRes.setResCode(SUCCESS);
			
			/* Bundle */
			pObjRes = deleteObject(pObjAuth, new ZappBundle(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_BUNDLE", "[discardContent] " + messageService.getMessage("ERR_D_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 삭제하는데 실패했습니다.
			}
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			/* Sub-Bundle info. */
			pObjRes = deleteObject(pObjAuth, new ZappFile(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_FILEEXT", "[discardContent] " + messageService.getMessage("ERR_D_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일확장 정보를 삭제하는데 실패했습니다.
			}
			
			/* File */
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());		// Task ID
			pObjContent.getZappFile().setMfileid(CONTENTID);
			pObjRes = deleteFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_FILE", "[discardContent] " + messageService.getMessage("ERR_D_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 삭제하는데 실패했습니다.
			}
			pObjRes.setResCode(SUCCESS);
			
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.DISCARD);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, CONTENTID);
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}			
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_DISCARD.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);			
		}
		
		/* [FTR]
		 * Deleting FTR index
		 */
		if(SYS_APPROVAL_DISCARD_YN.getSetval().equals(NO)) {
			pObjContent.setContentid(CONTENTID);
			try {
				pObjRes = ftrService.executeDeleting(pObjAuth, pObjContent, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_D_INDEX", "[addContent] " + messageService.getMessage("ERR_D_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} catch (IOException e) {
				return ZappFinalizing.finalising("ERR_D_INDEX", "[addContent] " + messageService.getMessage("ERR_D_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}
	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardContentForcely(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String CONTENTID = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();		
		
		// ## [Initialization] ##
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile());  }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "discardContentForcely", ZappConts.ACTION.DISCARD_PK);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		if(SYS_LOG_CONTENT_YN == null) {
			SYS_LOG_CONTENT_YN = new ZappEnv();
			SYS_LOG_CONTENT_YN.setSetval(NO);
		}
		
		// Content ID
		CONTENTID = pObjContent.getContentid();
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			pObjRes = selectObject(pObjAuth, new ZappBundle(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_NEXIST_BUNDLE", "[discardContent] " + messageService.getMessage("ERR_NEXIST_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			/* Variable */
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile();
			BeanUtils.copyProperties(new ZappFile(CONTENTID), pZArchMFile);
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());
			
			try {
				zArchResult = zarchMfileMgtService.loadMFile(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[discardContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[discardContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) zArchResult.getResult();
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_FILE", "[discardContent] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Get info. */
			pObjRes = selectObject(pObjAuth, new ZappFile(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[discardContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
		}				

		/* Link
		 * 
		 */
		ZappLinkedObject pZappLinkedObject = new ZappLinkedObject();
		pZappLinkedObject.setSourceid(CONTENTID);
		pObjRes = deleteObject(pObjAuth, pZappLinkedObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_링크객체", "[discardContentForcely] " + messageService.getMessage("ERR_삭제_링크객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 링크 객체를 삭제하는데 실패했습니다.
//		}
		
		/* Share info.
		 * 
		 */
		ZappSharedObject pZappSharedObject = new ZappSharedObject();
		pZappSharedObject.setSobjid(CONTENTID);
		pObjRes = deleteObject(pObjAuth, pZappSharedObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_공유객체", "[discardContentForcely] " + messageService.getMessage("ERR_삭제_공유객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 공유 객체를 삭제하는데 실패했습니다.
//		}
		
		/* Classification
		 * 
		 */
		ZappClassObject pZappClassObject = new ZappClassObject();
		pZappClassObject.setCobjid(CONTENTID);
		pObjRes = deleteObject(pObjAuth, pZappClassObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_CLASSOBJ", "[discardContentForcely] " + messageService.getMessage("ERR_D_CLASSOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 분류 객체를 삭제하는데 실패했습니다.
		}
		
		/* Locked info.
		 * 
		 */
		ZappLockedObject pZappLockedObject = new ZappLockedObject();
		pZappLockedObject.setLobjid(CONTENTID);							// Target ID
		pZappLockedObject.setLobjtype(pObjContent.getObjType());		// Target type
		pObjRes = deleteObject(pObjAuth, pZappLockedObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_잠금객체", "[discardContentForcely] " + messageService.getMessage("ERR_삭제_잠금객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 잠금 객체를 삭제하는데 실패했습니다.
//		}		
		
		/* Keyword
		 * 
		 */
		ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
		pZappKeywordObject.setKobjid(CONTENTID);							// Target ID
		pZappKeywordObject.setKobjtype(pObjContent.getObjType());		// Target type
		pObjRes = deleteObject(pObjAuth, pZappKeywordObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_삭제_잠금객체", "[discardContentForcely] " + messageService.getMessage("ERR_삭제_잠금객체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# Keyword 객체를 삭제하는데 실패했습니다.
//		}	
		
		/* Bundle and file
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			/* File */
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());		// Task ID
			pObjContent.getZappFile().setLinkid(CONTENTID);
			pObjRes = deleteFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_FILE", "[discardContentForcely] " + messageService.getMessage("ERR_D_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 삭제하는데 실패했습니다.
			}
			pObjRes.setResCode(SUCCESS);
			
			/* Bundle */
			pObjRes = deleteObject(pObjAuth, new ZappBundle(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_BUNDLE", "[discardContentForcely] " + messageService.getMessage("ERR_D_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 삭제하는데 실패했습니다.
			}
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			/* Sub-Bundle info. */
			pObjRes = deleteObject(pObjAuth, new ZappFile(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_FILEEXT", "[discardContentForcely] " + messageService.getMessage("ERR_D_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일확장 정보를 삭제하는데 실패했습니다.
			}
			
			/* 파일 정보 삭제 */
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());		// Task ID
			pObjContent.getZappFile().setMfileid(CONTENTID);
			pObjRes = deleteFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_FILE", "[discardContentForcely] " + messageService.getMessage("ERR_D_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 삭제하는데 실패했습니다.
			}
			pObjRes.setResCode(SUCCESS);
			
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.DISCARD);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, CONTENTID);
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}			
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_DISCARD_FORCELY.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);			
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}	
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult discardSpecificVersionContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow(); String CONTENTID = BLANK;
		boolean SKIP_CONTENTACL = false, SKIP_CLASSACL = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();

		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "discardSpecificVersionContent", ZappConts.ACTION.DISCARD_VERSION);
	
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_YN.env);							// Approval or not
		ZappEnv SYS_APPROVAL_DISCARD_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_DISCARD_YN.env);			// Approval for discarding or not
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		// Content ID
		CONTENTID = pObjContent.getContentid();
		
		/** [Get classification info.]
		 * 
		 */
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getZappClassObject().getClassid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification == null) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if((rZappClassification.getWfrequired() | ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt) == ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt
				|| rZappClassification.getWfrequired() == ZappConts.WORKFLOWS.WF_OBJECT_ALL.iwt) {
			SYS_APPROVAL_DISCARD_YN.setSetval(YES); // Apply approval process
		}
		// Apply approval process
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			SYS_APPROVAL_DISCARD_YN.setSetval(workflowService.doApply(rZappClassification.getWfrequired(), ZappConts.ACTION.ADD) == true ? YES : NO);
		} else {
			SYS_APPROVAL_DISCARD_YN.setSetval(NO);
		}
		
		/*
		 * 
		 */
		ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			/* Variable */
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile();
			BeanUtils.copyProperties(new ZappFile(CONTENTID), pZArchMFile);
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());
			
			try {
				zArchResult = zarchMfileMgtService.loadMFile(pZArchMFile);
			} catch (ZArchApiException e) {
				return ZappFinalizing.finalising_Archive(e.getZArchResult().getCode(), pObjAuth.getObjlang());
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) zArchResult.getResult();
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_FILE", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Check state */
			if(!rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			// Sub-Bundle info.
			pObjRes = selectObject(pObjAuth, new ZappFile(CONTENTID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappFile = (ZappFile) pObjRes.getResObj(); 			
		}
		
		/* [Manage mode]
		 * In case of administrator function, permission check is excluded.
		 */
		if(pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(SKIP_CONTENTACL == false) {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
					SKIP_CONTENTACL = true;
				}
			}		
		}	
		
		/* [Check access control info.]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(YES) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid() + DIVIDER + pObjContent.getZappClassObject().getClassid());		// Classification ID + Content ID
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CONTENTACL", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_D_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}		
		
		if(SYS_APPROVAL_DISCARD_YN.getSetval().equals(YES)) {
			ZappTmpObject pZappTmpObject = new ZappTmpObject();
			pZappTmpObject.setTobjid(pObjContent.getContentid());
			pZappTmpObject.setTobjtype(pObjContent.getObjType());
			pZappTmpObject.setTmptime(PROCTIME);
			pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pZappTmpObject.setClasses(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappClassObjects())); // Folder info.
			pZappTmpObject.setTaskid(pObjContent.getObjTaskid());
			pZappTmpObject.setFiles(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappFile()));
			
			/* Temporary info. */
			pObjRes = contentService.cSingleRow(pObjAuth, pZappTmpObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_TEMP", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			pObjRes = workflowService.commenceWorkflow(pObjAuth
					 , new ZappFile(pObjContent.getContentid())
				     , rZappClassification 
					 , pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Update state */
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile();
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());	// Task ID
			pZArchMFile.setMfileid(pObjContent.getContentid());
			pZArchMFile.setState(ZappConts.STATES.BUNDLE_DISCARD_VERSION_REQUEST.state);					// Waiting for discarding version
			try {
				zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
			} catch (ZArchApiException e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, e.getMessage(), pObjAuth.getObjlang());
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, e.getMessage(), pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			
		} else {
			
			// 최종 버전 정보 조회
			ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getContentid());
			pObjAuth.setObjType("01");
			
			List<String> objMfileList = new ArrayList<String>();
			try {
				ZArchMFile tmpList = zarchMfileService.rSingleRow_Vo(pZArchMFile);
				if(tmpList != null) {
					objMfileList.add(tmpList.getMfileid());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			pObjAuth.setObjList(objMfileList);
			
			pObjRes = contentService.rMultiRowsMaxVersionFile(pObjAuth, pZArchMFile, null, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_VERSION", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_R_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			List<ZArchMFileRes> rZArchMfileList = (List<ZArchMFileRes>) pObjRes.getResObj();
			if(rZArchMfileList == null) {
				return ZappFinalizing.finalising("ERR_R_VERSION", "[discardSpecificVersionContent] " + messageService.getMessage("ERR_R_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// 최종 버전 삭제
			for(ZArchMFileRes vo: rZArchMfileList) {
				ZArchResult zArchResult = new ZArchResult();
				try {
					zArchResult = zarchVersionService.dSingleRow(new ZArchVersion(vo.getzArchVersion().getVersionid()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}
			}
			
			// 최종 버전 파일 삭제
			for(ZArchMFileRes vo: rZArchMfileList) {
				ZArchVersion pZArchVersion_Filter = new ZArchVersion();
				pZArchVersion_Filter.setVersionid(Operators.NOT_IN.operator);
				ZArchVersion pZArchVersion = new ZArchVersion(vo.getzArchVersion().getVersionid());
				pZArchVersion.setUfileid(vo.getzArchVersion().getUfileid());
				boolean EXISTS = true;
				try {
					EXISTS = zarchVersionService.exists(pZArchVersion_Filter, pZArchVersion);
				} catch (ZArchApiException e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_VERSION.result, e.getMessage(), pObjAuth.getObjlang());
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_VERSION.result, e.getMessage(), pObjAuth.getObjlang());
				}
				
				if(EXISTS == false) {
					
					// Delete a file
					ZArchResult zArchResult = new ZArchResult();
					try {
						zArchResult = zarchfileMgtService.getExistingArchivePath(vo.getzArchFile());
					} catch (ZArchApiException e) {
						return ZappFinalizing.finalising_Archive(Results.FAIL_TO_GET_STORE_PATH.result, e.getMessage(), pObjAuth.getObjlang());
					} catch (Exception e) {
						return ZappFinalizing.finalising_Archive(Results.FAIL_TO_GET_STORE_PATH.result, e.getMessage(), pObjAuth.getObjlang());
					}
					
					if(ZappFinalizing.isSuccess(zArchResult) == false) {
						return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
					}
					String FILEPATH = (String) zArchResult.getResult();
					if(ZstFwValidatorUtils.valid(FILEPATH) == true) {
					
						File desFile = new File(ZstFwFileUtils.addSeperator(FILEPATH) + vo.getzArchFile().getUfileid());
						if(desFile.exists()) {
							if(FileUtil.deleteFile(ZstFwFileUtils.addSeperator(FILEPATH) + vo.getzArchFile().getUfileid()) == false) {
								return ZappFinalizing.finalising_Archive(Results.FAIL_TO_DELETE_FILE.result, pObjAuth.getObjlang());
							}
						}
						
						// Delete info.
						ZArchFile pZArchUFile = new ZArchFile(vo.getzArchFile().getUfileid());
						try {
							zArchResult = zarchfileService.dSingleRow(pZArchUFile);
						} catch (ZArchApiException e) {
							return ZappFinalizing.finalising_Archive(Results.FAIL_TO_DELETE_UFILE.result, e.getMessage(), pObjAuth.getObjlang());
						} catch (Exception e) {
							return ZappFinalizing.finalising_Archive(Results.FAIL_TO_DELETE_UFILE.result, e.getMessage(), pObjAuth.getObjlang());
						}
						if(ZappFinalizing.isSuccess(zArchResult) == false) {
							return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
						}
						
					}
				}
			}
			
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(YES) && SYS_APPROVAL_DISCARD_YN.getSetval().equals(NO)) {
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.DISCARD);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, CONTENTID);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_DISCARD.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);			
		}
		
		return pObjRes;
		
	}
	
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult relocateContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		boolean SKIP_CONTENTACL = false, SKIP_CLASSACL = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "relocateContent", ZappConts.ACTION.RELOCATE);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_APPROVAL_DISCARD_YN = new ZappEnv(); SYS_APPROVAL_DISCARD_YN.setSetval(NO);	
		ZappEnv SYS_APPROVAL_ADD_YN = new ZappEnv(); SYS_APPROVAL_ADD_YN.setSetval(NO);	
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_CLASSACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CLASSACL_YN.env);							// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log

		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* [Get info. by type]
		 * 1. Check state
		 * 2. Holder or not
		 */
		ZappBundle rZappBundle = null; ZappFile rZappFile = null; ZArchMFile rZArchMFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
			pObjRes = contentService.rSingleRow(pObjAuth, pZappBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_NEXIST_BUNDLE", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			
			/* Check state */
			if(!rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)
					&& !rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state)
					&& !rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[relocateContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Excluded (Holder) */
			if(rZappBundle.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			/* Variable */
			pObjContent.getZappFile().setMfileid(pObjContent.getContentid());
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile();
			BeanUtils.copyProperties(pObjContent.getZappFile(), pZArchMFile);
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());	// Task ID
			
			try {
				zArchResult = zarchMfileMgtService.loadMFile(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) zArchResult.getResult();
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_FILE", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Check state */
			if(!rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)
					&& !rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state)
					&& !rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[relocateContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Sub-Bundle info. */
			pObjRes = contentService.rSingleRow(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[relocateContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
			/* Excluded (Holder) */
			if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}			
			
		}		
		
		/* [Checking Source Classification]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjContent.getZappClassObjects().get(ZERO).getClassid()) == false) {
			ZappClassObject pZappClassObject = new ZappClassObject();
			pZappClassObject.setClasstype(getNodeTypes());
			pZappClassObject.setCobjid(pObjContent.getContentid());
			pZappClassObject.setCobjtype(pObjContent.getObjType());
			pObjRes = selectObject(pObjAuth, pZappClassObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			List<ZappClassObject> rZappClassObject = (List<ZappClassObject>) pObjRes.getResObj();
			if(rZappClassObject == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappClassObject vo : rZappClassObject) {
				pObjContent.getZappClassObjects().get(ZERO).setClassid(vo.getClassid());
			}
		}
		
		
		/* [Get current folder info.]
		 * 
		 */
		ZappClassification rZappClassification_Original = null;
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getZappClassObjects().get(ZERO).getClassid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappClassification_Original = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification_Original == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			SYS_APPROVAL_DISCARD_YN.setSetval(workflowService.doApply(rZappClassification_Original.getWfrequired(), ZappConts.ACTION.DISCARD) == true ? YES : NO);
		} else {
			if(validAcsRoute(pObjContent) == true) {
				SYS_APPROVAL_DISCARD_YN.setSetval(NO); SKIP_CONTENTACL = true; SKIP_CLASSACL = true;
			} else {
				return ZappFinalizing.finalising("ERR_NIDENT_CODE", "[relocateContent] " + messageService.getMessage("ERR_NIDENT_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}

		
		/* [Get target folder info.]
		 * 
		 */
		ZappClassification rZappClassification_Target = null;
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getZappClassObjects().get(ONE).getClassid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappClassification_Target = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification_Target == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {
			SYS_APPROVAL_ADD_YN.setSetval(workflowService.doApply(rZappClassification_Target.getWfrequired(), ZappConts.ACTION.ADD) == true ? YES : NO);
		} else {
			if(validAcsRoute(pObjContent) == true) {
				SYS_APPROVAL_ADD_YN.setSetval(NO); SKIP_CONTENTACL = true; SKIP_CLASSACL = true;
			} else {
				return ZappFinalizing.finalising("ERR_NIDENT_CODE", "[relocateContent] " + messageService.getMessage("ERR_NIDENT_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Exclude access control info.]
		 * For Department and cooperation folders
		 */
		if(isSkipAclNodes(rZappClassification_Target.getTypes()) == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Company Manager]
		 * Enables copying from the company manager's personal folder
		 */
		if(aclService.isCompanyManager(pObjAuth) == true) {
			if(rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)
					&& rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
					SKIP_CONTENTACL = true; SKIP_CLASSACL = true;
			}
			if(rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
					&& rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
					SKIP_CONTENTACL = true; SKIP_CLASSACL = true;
			}
		}
	
		/* [Check by classification type]
		 * Depending on the type of source classification and target classification,
		 * 1. Basically, it is possible to move between the same classification types.
		 * 2. Content in personal folders can be moved to other types. 
		 */
		if(!rZappClassification_Original.getTypes().equals(rZappClassification_Target.getTypes())) {
			if(!rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
				return ZappFinalizing.finalising("ERR_NIDENT_TYPE", "[relocateContent] " + messageService.getMessage("ERR_NIDENT_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# User 노드 분류에서만 다른 유형의 노드 분류로 이동이 가능합니다.
			}
		}
		if(rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)
				&& rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			if(!rZappClassification_Target.getHolderid().equals(pObjAuth.getSessUser().getUserid())) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[relocateContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			SKIP_CLASSACL = true;
		}
		
		/* [Manager mode]
		 * In case of administrator function, permission check is excluded.
		 */
		if(pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [ Check content access control info. ]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid() + DIVIDER + rZappClassification_Original.getClassid());
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[relocateContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 이동할 수 있는 권한이 없습니다.
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[relocateContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 이동할 수 있는 권한이 없습니다.
			}				
		}

		/* [Check classification access control info.]
		 * 
		 */
		if(SYS_CLASSACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) 
				&& SKIP_CLASSACL == false 
				&& !rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			ZappClassAcl pZappClassAcl = new ZappClassAcl();
			pZappClassAcl.setClassid(rZappClassification_Target.getClassid());
			pZappClassAcl.setAcls(ZappConts.ACLS.CLASS_READ_ADD.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappClassAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASSACL", "[relocateContent] " + messageService.getMessage("ERR_R_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 이동할 수 있는 권한이 없습니다.
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[relocateContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 이동할 수 있는 권한이 없습니다.
			}				
		}

		
//		/* 링크 정보 */
//		ZappLinkedObject pZappLinkedObject = new ZappLinkedObject();
//		pZappLinkedObject.setSourceid(pObjContent.getContentid());
//		if(contentService.rExist(pObjAuth, pZappLinkedObject) == true) {
//			return ZappFinalizing.finalising("ERR_체크_링크겍체", "[relocateContent] " + messageService.getMessage("ERR_체크_링크겍체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 링크객체가 지정되어 있습니다. 
//		}
//		
//		/* Share info. */
//		ZappSharedObject pZappSharedObject = new ZappSharedObject();
//		pZappSharedObject.setTobjid(pObjContent.getContentid());
//		if(contentService.rExist(pObjAuth, pZappSharedObject) == true) {
//			return ZappFinalizing.finalising("ERR_체크_공유겍체", "[relocateContent] " + messageService.getMessage("ERR_체크_공유겍체",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			//# 공유객체가 지정되어 있습니다.
//		}	
		
		/* [Approval]
		 * 
		 */
		if(SYS_APPROVAL_DISCARD_YN.getSetval().equals(ZappConts.USAGES.YES.use)
				|| SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			
			ZappTmpObject pZappTmpObject = new ZappTmpObject();
			pZappTmpObject.setTobjid(pObjContent.getContentid());
			pZappTmpObject.setTobjtype(pObjContent.getObjType());
			pZappTmpObject.setTmptime(PROCTIME);
			pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pZappTmpObject.setClasses(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappClassObjects())); // Folder info.
			pZappTmpObject.setTaskid(pObjContent.getObjTaskid());
			
			/* Temporary info. */
			pObjRes = contentService.cSingleRow(pObjAuth, pZappTmpObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_TEMP", "[relocateContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Bundle */
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
				pZappBundle.setState(SYS_APPROVAL_DISCARD_YN.getSetval().equals(YES) ? ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state : ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state);					// Waiting for moving
				pObjRes = contentService.uSingleRow(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[relocateContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				ZArchResult zArchResult = new ZArchResult();
				ZArchMFile pZArchMFile = new ZArchMFile();
				pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());	// Task ID
				pZArchMFile.setMfileid(pObjContent.getContentid());
				pZArchMFile.setState(SYS_APPROVAL_DISCARD_YN.getSetval().equals(YES) ? ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state : ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state);					// Waiting for moving
				try {
//					zArchResult = zarchMfileMgtService.updateMFile(pZArchMFile);
					zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}
			}

			/* */
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth
													    , new ZappBundle(pObjContent.getContentid())
													    , SYS_APPROVAL_DISCARD_YN.getSetval().equals(YES) ? rZappClassification_Original : rZappClassification_Target 
													    , pObjRes);
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth
														 , new ZappFile(pObjContent.getContentid())
													     , SYS_APPROVAL_DISCARD_YN.getSetval().equals(YES) ? rZappClassification_Original : rZappClassification_Target 
														 , pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[addContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			
			/* Log */
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjContent.setZappBundle(rZappBundle);
				pObjContent.setZappFile(rZappFile);
				ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.RELOCATE);
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
				} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
				}
				pObjRes = leaveLog(pObjAuth
							     , pObjContent.getObjType()
							     , ZappConts.LOGS.ACTION_RELOCATE_REQUEST.log
							     , LOGMAP
							     , PROCTIME
							     , pObjRes);
			}
			
			return pObjRes;
			
		}
		
		/* [Reclassification]
		 * 1. Delete current classification info.
		 * 2. Create target classification info.
		 */
		// 1. Delete current classification info.
		ZappClassObject pZappClassObject = new ZappClassObject();
		pZappClassObject.setClassid(rZappClassification_Original.getClassid());		// Current classification ID
		pZappClassObject.setCobjid(pObjContent.getContentid());						// Content ID
		pZappClassObject.setCobjtype(pObjContent.getObjType());						// Content type
		pObjRes = contentService.dMultiRows(pObjAuth, pZappClassObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_CLASS", "[discardContent] " + messageService.getMessage("ERR_D_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 분류 정보를 삭제하는데 실패했습니다.
		}
		// 2. Create target classification info.
		pZappClassObject = new ZappClassObject();
		pZappClassObject.setClassid(rZappClassification_Target.getClassid());		// Teget classification ID
		pZappClassObject.setClasstype(rZappClassification_Target.getTypes());		// Teget classification type
		pZappClassObject.setCobjid(pObjContent.getContentid());						// Content ID
		pZappClassObject.setCobjtype(pObjContent.getObjType());						// Content type
		pZappClassObject.setClassobjid(ZappKey.getPk(pZappClassObject));
		pObjRes = contentService.cSingleRow(pObjAuth, pZappClassObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_CLASS", "[discardContent] " + messageService.getMessage("ERR_C_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 분류 정보를 등록하는데 실패했습니다.
		}
		
		/*  [Logging]
		 * 1. 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.RELOCATE);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_RELOCATE.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult replicateContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		boolean SKIP_CONTENTACL = false; boolean SKIP_CLASSACL = false; boolean ENCRYPTION_YN = false; boolean APPROVAL_REPLICATE_YN = false;
		String CTYPE = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		ZappContentPar pObjContent_NEW = new ZappContentPar(pObjContent.getObjTaskid(), pObjContent.getObjType());
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "replicateContent", ZappConts.ACTION.REPLICATE);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_APPROVAL_ADD_YN = new ZappEnv(); SYS_APPROVAL_ADD_YN.setSetval(NO);
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_CLASSACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CLASSACL_YN.env);							// Whether to apply content access info.
		ZappEnv SYS_ENCRYPTION_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.ENCRYPTION_YN.env);						// [FILE] Apply encryption or not
		if(SYS_ENCRYPTION_YN != null) {
			ENCRYPTION_YN = SYS_ENCRYPTION_YN.getSetval().equals(ZappConts.USAGES.YES.use) ? true : false;
		}
		ZappEnv SYS_CHECKFORMAT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CHECKFORMAT_YN.env);								// [FILE] Check file format?
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* [Get info. by type]
		 * 1. Check state
		 * 2. Holder
		 */
		ZappBundle rZappBundle = null; ZappFile rZappFile = null; ZArchMFile rZArchMFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
			pObjRes = contentService.rSingleRow(pObjAuth, pZappBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_NEXIST_BUNDLE", "[replicateContent] " + messageService.getMessage("ERR_NEXIST_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			pObjContent_NEW.setZappBundle(rZappBundle); pObjContent_NEW.getZappBundle().setState(ZappConts.STATES.BUNDLE_NORMAL.state);
			
			/* Check state */
			if(!rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)
					&& !rZappBundle.getState().equals(ZappConts.STATES.BUNDLE_REPLICATE_REQUEST.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[replicateContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Excluded (Holder) */
			if(rZappBundle.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
			
			/* Bundle Type */
			CTYPE = rZappBundle.getBtype();
			
		}
		else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			pObjContent.getZappFile().setMfileid(pObjContent.getContentid());
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile();
			BeanUtils.copyProperties(pObjContent.getZappFile(), pZArchMFile);
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());	// Task ID
			
			try {
				zArchResult = zarchMfileMgtService.loadMFile(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) zArchResult.getResult();
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_FILE", "[replicateContent] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Check state */
			if(!rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)
					&& !rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_REPLICATE_REQUEST.state)) {
				return ZappFinalizing.finalising("ERR_STATE", "[replicateContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 상태가 현재 처리를 할 수 없습니다. 
			}
			
			/* Sub-Bundle info. */
			pObjRes = contentService.rSingleRow(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[replicateContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			pObjContent_NEW.setZappFile(rZappFile); pObjContent_NEW.getZappFile().setState(ZappConts.STATES.BUNDLE_NORMAL.state);
			
			/* Excluded (Holder) */
			if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}			
			
		}		
		
		/* [Checking Source Classification]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjContent.getZappClassObjects().get(ZERO).getClassid()) == false) {
			ZappClassObject pZappClassObject = new ZappClassObject();
			pZappClassObject.setClasstype(getNodeTypes());
			pZappClassObject.setCobjid(pObjContent.getContentid());
			pZappClassObject.setCobjtype(pObjContent.getObjType());
			pObjRes = selectObject(pObjAuth, pZappClassObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			List<ZappClassObject> rZappClassObject = (List<ZappClassObject>) pObjRes.getResObj();
			if(rZappClassObject == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappClassObject vo : rZappClassObject) {
				pObjContent.getZappClassObjects().get(ZERO).setClassid(vo.getClassid());
			}
		}
		
		/* [Get current classification info.]
		 * 
		 */
		ZappClassification rZappClassification_Original = null;
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getZappClassObjects().get(ZERO).getClassid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[relocateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappClassification_Original = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification_Original == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[replicateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		/* [Get target classification info.]
		 * 
		 */
		ZappClassification rZappClassification_Target = null;
		pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getZappClassObjects().get(ONE).getClassid()), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[replicateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappClassification_Target = (ZappClassification) pObjRes.getResObj();
		if(rZappClassification_Target == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[replicateContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(ZstFwValidatorUtils.valid(pObjContent.getObjAcsRoute()) == false) {	// Initial Process
			SYS_APPROVAL_ADD_YN.setSetval(workflowService.doApply(rZappClassification_Target.getWfrequired(), ZappConts.ACTION.ADD) == true ? YES : NO);
		} else {																// Approval Process
			if(validAcsRoute(pObjContent) == true) {
				SYS_APPROVAL_ADD_YN.setSetval(NO);
			} else {
				return ZappFinalizing.finalising("ERR_NIDENT_CODE", "[relocateContent] " + messageService.getMessage("ERR_NIDENT_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Check by classification type]
		 * 
		 */
		if(isSkipAclNodes(rZappClassification_Target.getTypes()) == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Company Manager]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == true) {
			if(rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)
					&& rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
					SKIP_CONTENTACL = true;  SKIP_CLASSACL = true;
			}
			if(rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
					&& rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
					SKIP_CONTENTACL = true;  SKIP_CLASSACL = true;
			}
		}
		
		/* [Check by classification type]
		 * Depending on the type of source classification and target classification,
		 * 1. Basically, it is possible to move between the same classification types.
		 * 2. Content in personal folders can be moved to other types. 
		 */
		if(!rZappClassification_Original.getTypes().equals(rZappClassification_Target.getTypes())) {
			if(!rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
				return ZappFinalizing.finalising("ERR_NIDENT_TYPE", "[replicateContent] " + messageService.getMessage("ERR_NIDENT_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# User 노드 분류에서만 다른 유형의 노드 분류로 이동이 가능합니다.
			}
		}
		if(rZappClassification_Original.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)
				&& rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			if(!rZappClassification_Target.getHolderid().equals(pObjAuth.getSessUser().getUserid())) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[replicateContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			SKIP_CLASSACL = true;
		}

		/* [Manager mode]
		 * In case of administrator function, permission check is excluded.
		 */
		if(pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [ Check content access control info. ]
		 * 1. Checking whether the current user has editing permission for the current content. 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid() + DIVIDER + rZappClassification_Original.getClassid());
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[replicateContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 이동할 수 있는 권한이 없습니다.
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[replicateContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 이동할 수 있는 권한이 없습니다.
			}				
		}
		
		/* [Exclude applying access control info. ]
		 * 
		 */
		if(rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			if(rZappClassification_Target.getHolderid().equals(pObjAuth.getSessDeptUser().getUserid())) {
				SKIP_CONTENTACL = true;
			}
		}
//		if(rZappClassification_Target.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
//			if(rZappClassification_Target.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
//				SKIP_CONTENTACL = true;
//			}
//		}

		/* [Check classification access control info.]
		 * 
		 */
		if(SYS_CLASSACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CLASSACL == false) {
			ZappClassAcl pZappClassAcl = new ZappClassAcl();
			pZappClassAcl.setClassid(rZappClassification_Target.getClassid());
			pZappClassAcl.setAcls(ZappConts.ACLS.CLASS_READ_ADD.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappClassAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASSACL", "[replicateContent] " + messageService.getMessage("ERR_R_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[replicateContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}				
		}		
		
		/* **************************************************************************************************************************** */
		/* Source */
		/* **************************************************************************************************************************** */
		
		/* [Temporary info.]
		 * 
		 */
		ZappTmpObject pZappTmpObject = null;
		if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pZappTmpObject = new ZappTmpObject();
			pZappTmpObject.setTobjid(pObjContent.getContentid());
			pZappTmpObject.setTobjtype(pObjContent.getObjType());
			pZappTmpObject.setTmptime(PROCTIME);
			pZappTmpObject.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pZappTmpObject.setClasses(ZappJSONUtils.cvrtObjToJson(pObjContent.getZappClassObjects()));	// Folder info.
			pZappTmpObject.setTaskid(pObjContent.getObjTaskid());
			pObjRes = addObject(pObjAuth, pZappTmpObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_TEMP", "[replicateContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Bundle */
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
				pZappBundle.setState(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state);					// Waiting for copying
				pObjRes = contentService.uSingleRow(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[replicateContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				ZArchResult zArchResult = new ZArchResult();
				ZArchMFile pZArchMFile = new ZArchMFile();
				pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());	// Task ID
				pZArchMFile.setMfileid(pObjContent.getContentid());
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state);					// Waiting for copying
				try {
					zArchResult = zarchMfileMgtService.updateMFile(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}
			}
			
			// Commence workflow
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, new ZappBundle(pObjContent.getContentid()), rZappClassification_Target, pObjRes);
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjRes = workflowService.commenceWorkflow(pObjAuth, new ZappFile(pObjContent.getContentid()), rZappClassification_Target, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[replicateContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/*  [Logging]
			 * 
			 */
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				pObjContent.setZappBundle(rZappBundle);
				pObjContent.setZappFile(rZappFile);
				ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.REPLICATE);
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
				} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
				}
				pObjRes = leaveLog(pObjAuth
							     , pObjContent.getObjType()
							     , ZappConts.LOGS.ACTION_REPLICATE_REQUEST.log
							     , LOGMAP
							     , PROCTIME
							     , pObjRes);
			}

			return pObjRes;
		}

		
		/* [Access control info.]
		 * 
		 */
//		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
//			ZappContentAcl pZappContentAcl = new ZappContentAcl();
//			pZappContentAcl.setContentid(pObjContent.getContentid());
//			pObjRes = aclService.selectObject(pObjAuth, pZappContentAcl, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("누락_조회_권한", "[replicateContent] " + messageService.getMessage("누락_조회_권한",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				//# 권한 정보를 조회하는데 실패했습니다.
//			}
//			List<ZappContentAcl> rZappContentAclList = (List<ZappContentAcl>) pObjRes.getResObj();
//			if(rZappContentAclList != null) {
//				for(int IDX = ZERO; IDX < rZappContentAclList.size(); IDX++) {
//					rZappContentAclList.get(IDX).setAclid(null);
//					rZappContentAclList.get(IDX).setContentid(null);
//				}
//			}
//			
//			pObjContent_NEW.setZappAcls(rZappContentAclList);
//		}
		
		/* [Sub-Bundle info.]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			ZappAdditoryBundle rZappAdditoryBundle = null;
			ZappAdditoryBundle pZappAdditoryBundle = new ZappAdditoryBundle(pObjContent.getContentid());
			pObjRes = contentService.rSingleRow(pObjAuth, pZappAdditoryBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_NEXIST_BUNDLEEXT", "[replicateContent] " + messageService.getMessage("ERR_NEXIST_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappAdditoryBundle = (ZappAdditoryBundle) pObjRes.getResObj();
			rZappAdditoryBundle.setBundleid(null);
			if(utilBinder.isEmpty(rZappAdditoryBundle) == true) {
				rZappAdditoryBundle.setDynamic10(YES);
			}
			pObjContent_NEW.setZappAdditoryBundle(rZappAdditoryBundle);
		}
		
		/* [File]
		 * 
		 */
		List<ZappFile> pZappFileList = new ArrayList<ZappFile>();
		if(!CTYPE.equals(ZappConts.TYPES.BUNDLE_VIRTUAL.type)) {		
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile();
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());	// Task ID
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pZArchMFile.setLinkid(pObjContent.getContentid());
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pZArchMFile.setMfileid(pObjContent.getContentid());
			}
			
			
			try {
				zArchResult = zarchMfileMgtService.listMFileAll(pZArchMFile, null, null);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			List<ZArchMFileRes> rZArchMFileList = (List<ZArchMFileRes>) zArchResult.getResult();
			if(rZArchMFileList == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_FILE", "[replicateContent] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZArchMFileRes vo : rZArchMFileList) {
				ZappFile pZappFile = new ZappFile();
				try {
					zArchResult = zarchfileMgtService.getExistingArchivePath(vo.getzArchFile());
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_GET_STORE_PATH.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}				
	//			pZappFile.setObjFileName(zArchResult.getResult() + vo.getzArchFile().getUfileid());		// Currently saved file path
				if(ENCRYPTION_YN == true) {
					CryptoNUtil cryptoNUtil = new CryptoNUtil();
					try {
						cryptoNUtil.doDecrypt(new File(zArchResult.getResult() + vo.getzArchFile().getUfileid())
											, new File(zArchResult.getResult() + vo.getFilename())
//											, new File(zArchResult.getResult() + vo.getzArchFile().getUfileid() + "_tmp")
											 );
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						ZstFwFileUtils.copyFile(zArchResult.getResult() + vo.getzArchFile().getUfileid()
											  , zArchResult.getResult() + vo.getFilename()
//											  , zArchResult.getResult() + vo.getzArchFile().getUfileid() + "_tmp"
											  );
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
//				pZappFile.setObjFileName(zArchResult.getResult() + vo.getzArchFile().getUfileid() + "_tmp");	// Entire pathway for the saved file
				pZappFile.setObjFileName(zArchResult.getResult() + vo.getFilename());	// Entire pathway for the saved file
				pZappFile.setFilename(vo.getFilename());														// Real file name
				pZappFile.setObjFileExt(ZstFwFileUtils.getExtension(vo.getFilename()));							// File extension
				
				// Sub-file info.
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					pObjRes = selectObject(pObjAuth, new ZappFile(vo.getMfileid()), pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_FILEEXT", "[replicateContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					ZappFile rZappFile_ = (ZappFile) pObjRes.getResObj();
					if(rZappFile_ != null) {
						pZappFile.setFno(rZappFile_.getFno());
						pZappFile.setDynamic01(rZappFile_.getDynamic01());
						pZappFile.setDynamic02(rZappFile_.getDynamic02());
						pZappFile.setDynamic03(rZappFile_.getDynamic03());
						pZappFile.setExt(rZappFile_.getExt());
						pZappFile.setCheckFormat((SYS_CHECKFORMAT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);
					}
				}
				
				pZappFileList.add(pZappFile);
			}
			
		} else {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				
				ZappLinkedObject pZappLinkedObject = new ZappLinkedObject();
				pZappLinkedObject.setSourceid(pObjContent.getContentid());
				pObjRes = selectObject(pObjAuth, pZappLinkedObject, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_LINKEDOBJ", "[replicateContent] " + messageService.getMessage("ERR_R_LINKEDOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				List<ZappLinkedObject> rZappLinkedObjList = (List<ZappLinkedObject>) pObjRes.getResObj();
				if(rZappLinkedObjList != null) {
					for(ZappLinkedObject vo : rZappLinkedObjList) {
						ZappFile pTmp = new ZappFile();
						pTmp.setMfileid(vo.getTargetid());
						if(vo.getLinktype().equals(ZappConts.TYPES.LINK_BTOB.type)) {
							pTmp.setAction(ZappConts.TYPES.CONTENT_BUNDLE.type);
						} else if(vo.getLinktype().equals(ZappConts.TYPES.LINK_BTOF.type)) {
							pTmp.setAction(ZappConts.TYPES.CONTENT_FILE.type);
						}
						pZappFileList.add(pTmp);
					}
				}
			}
		}			
		
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjContent_NEW.setZappFiles(pZappFileList);
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			for(ZappFile vo : pZappFileList) {
				pObjContent_NEW.setZappFile(vo);
			}
		}
		
		/* [Classification]
		 * 
		 */
		List<ZappClassObject> pZappClassObjectList = new ArrayList<ZappClassObject>();
		ZappClassObject pZappClassObject = new ZappClassObject();
		pZappClassObject.setClassid(pObjContent.getZappClassObjects().get(ONE).getClassid());
		pZappClassObject.setClasstype(rZappClassification_Target.getTypes());
		pZappClassObjectList.add(pZappClassObject);
		
		// Classification, Doctype
		pZappClassObject = new ZappClassObject();
		pZappClassObject.setClasstype(ZappConts.TYPES.CLASS_CLASS.type + DIVIDER + ZappConts.TYPES.CLASS_DOCTYPE.type);
		pZappClassObject.setCobjid(pObjContent.getContentid());
		pObjRes = selectObject(pObjAuth, pZappClassObject, pObjRes);
		List<ZappClassObject> rZappClassObjectList = (List<ZappClassObject>) pObjRes.getResObj();
		if(rZappClassObjectList != null) {
			for(ZappClassObject vo : rZappClassObjectList) {
				pZappClassObject = new ZappClassObject();
				pZappClassObject.setClassid(vo.getClassid());
				pZappClassObject.setClasstype(vo.getClasstype());
				pZappClassObjectList.add(pZappClassObject);
			}
		}
		pObjContent_NEW.setZappClassObjects(pZappClassObjectList);
		
		/* [Keyword]
		 * 2021-04-12
		 */
		ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
		pZappKeywordObject.setKobjid(pObjContent.getContentid());
		pZappKeywordObject.setKobjtype(pObjContent.getObjType());
		pObjRes = selectExtendObject(pObjAuth, pZappKeywordObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_KEYWORD", "[replicateContent] " + messageService.getMessage("ERR_R_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 컨텐츠 정보를 등록하는데 실패했습니다.
		}	
		List<ZappKeywordExtend> keywords = (List<ZappKeywordExtend>) pObjRes.getResObj();
		if(keywords != null) {
			if(keywords.size() > ZERO) {
				pObjContent_NEW.setZappKeywords(keywords);
			}
		}
		
		/* [Retention period]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjContent_NEW.setObjRetention(rZappBundle.getRetentionid());
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			pObjContent_NEW.setObjRetention(rZappFile.getRetentionid());
		}		
		
		/* [Content]
		 * 
		 */
		pObjContent_NEW.setObjCaller(ZappConts.ACTION.RELOCATE.name());	// Caller
		pObjContent_NEW.setObjAcsRoute(pObjContent.getObjAcsRoute());   // Approval Process
		pObjRes = addContent(pObjAuth, pObjContent_NEW, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_CONTENT", "[replicateContent] " + messageService.getMessage("ERR_C_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 컨텐츠 정보를 등록하는데 실패했습니다.
		}
		String CONTENTID = (String) pObjRes.getResObj();  // Registed Content ID
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.REPLICATE);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_REPLICATE.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(CONTENTID);
		return pObjRes;
	}	
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult lockContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String STATE_NOW = BLANK;
		boolean SKIP_CONTENTACL = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappLockedObject() == null) { pObjContent.setZappLockedObject(new ZappLockedObject()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "lockContent", ZappConts.ACTION.LOCK);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_APPROVAL_CHANGE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_CHANGE_YN.env);			// Approval for editing or not
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);			
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; List<ZArchMFileRes> rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			// Bundle
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[lockContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			STATE_NOW = rZappBundle.getState();
			
			// Holder 
			if(rZappBundle.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
			
			// File
			pObjContent.getZappFile().setLinkid(pObjContent.getContentid());
			pObjRes = getFilesAll(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[lockContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZArchMFile = (List<ZArchMFileRes>) pObjRes.getResObj(); 
			
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			// Archive
			pObjRes = getFilesAll(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[lockContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZArchMFile = (List<ZArchMFileRes>) pObjRes.getResObj(); 
			for(ZArchMFile vo : rZArchMFile) {
				STATE_NOW = vo.getState();
			}
			
			// Sub-Bundle info.
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[lockContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
			// Holder 
			if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
		}
		pObjRes.setResCode(SUCCESS);
		
		/* [Check state]
		 * Only contents in the normal state can be locked.
		 */
		if(!STATE_NOW.equals(ZappConts.STATES.BUNDLE_NORMAL.state)) {
			return ZappFinalizing.finalising("ERR_STATE", "[lockContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid() + DIVIDER + pObjContent.getZappClassObjects().get(ZERO).getClassid());		// Classification ID + Content ID
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[lockContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 수정할 수 있는 권한이 없습니다.
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[lockContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠를 수정할 수 있는 권한이 없습니다.
			}			
		}		
		
		/* [Approval]
		 * 1. In case of approval processing, it is stored in a temporary object.
		 * 2. If approval processing is not performed, lock processing is performed immediately.
		 */
		if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			
			/* [Temporary info.]
			 * 
			 */
			ZappTmpObject pZappTmpObject = new ZappTmpObject();
			pZappTmpObject.setTobjid(pObjContent.getContentid());	// Content ID
			pZappTmpObject.setTobjtype(pObjContent.getObjType());	// Content type
			pZappTmpObject.setTmptime(PROCTIME);					// Processing time
			List<String> STATES = new ArrayList<String>(Arrays.asList(new String[]{ ZappConts.STATES.BUNDLE_LOCK_REQUEST.state, ZappConts.STATES.BUNDLE_LOCK.state }));
			pZappTmpObject.setStates(ZappJSONUtils.cvrtObjToJson(STATES));
			pObjRes = addObject(pObjAuth, pZappTmpObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_TEMP", "[lockContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 임시객체 정보를 등록하는데 실패했습니다.
			}
			
		} else {
			
			pObjContent.getZappLockedObject().setLobjid(pObjContent.getContentid());
			pObjContent.getZappLockedObject().setLobjtype(pObjContent.getObjType());
			pObjContent.getZappLockedObject().setLocktime(PROCTIME);
			pObjContent.getZappLockedObject().setLockerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjRes = addObject(pObjAuth, pObjContent.getZappLockedObject(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_LOCK", "[lockContent] " + messageService.getMessage("ERR_C_LOCK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 잠금 정보를 등록하는데 실패했습니다.
			}
			
		}
		
		/* [Change state]
		 * 1. Bundle
		 * 2. File
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			// Bundle
			pObjRes = changeObject(pObjAuth
					            , (SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? new ZappBundle(pObjContent.getContentid(), ZappConts.STATES.BUNDLE_LOCK_REQUEST.state)
																									    : new ZappBundle(pObjContent.getContentid(), ZappConts.STATES.BUNDLE_LOCK.state)
								, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[lockContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 수정하는데 실패했습니다.
			}
			
			// File
			pObjRes = changeFile(pObjAuth, new ZappFile(pObjContent.getZappFile().getMfileid()
							      					 , (SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.STATES.BUNDLE_LOCK_REQUEST.state
																										  					 : ZappConts.STATES.BUNDLE_LOCK.state)
								  					 , pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[lockContent] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 수정하는데 실패했습니다.
			}
			
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			pObjRes = changeFile(pObjAuth, new ZappFile(pObjContent.getContentid()
												      , (SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.STATES.BUNDLE_LOCK_REQUEST.state
																															  : ZappConts.STATES.BUNDLE_LOCK.state)
													  , pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[lockContent] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 수정하는데 실패했습니다.
			}
		}		
		pObjRes.setResCode(SUCCESS);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.LOCK);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.get(ZERO).getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , (SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.LOGS.ACTION_LOCK_REQUEST.log : ZappConts.LOGS.ACTION_LOCK.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(rZArchMFile);
		
		return pObjRes;
		
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult unlockContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String STATE = BLANK;
		boolean WITHOUT_FILE = pObjContent.getHasfile() == true ? false : true;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappLockedObject() == null) { pObjContent.setZappLockedObject(new ZappLockedObject()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "unlockContent", ZappConts.ACTION.UNLOCK);
		
		/* [Inquire preferences]
		 * 
		 */
//		ZappEnv SYS_APPROVAL_REPLICATE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_REPLICATE_YN.env);		// Approval or not
//		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
//		ZappEnv SYS_CLASSACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CLASSACL_YN.env);							// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		/* [Check access control info.]
		 * 
		 */
//		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
//			ZappContentAcl pZappContentAcl = new ZappContentAcl();
//			pZappContentAcl.setContentid(CONTENTID);
//			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
//			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[releaseLockedContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 수정할 수 있는 권한이 없습니다.
//			}
//			boolean CANDO = (Boolean) pObjRes.getResObj();
//			if(CANDO == false) {
//				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[releaseLockedContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				// 컨테츠를 수정할 수 있는 권한이 없습니다.
//			}			
//		}
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[unlockContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			STATE = rZappBundle.getState();
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
			pZappFile.setObjTaskid(pObjContent.getObjTaskid());
			pObjRes = getFile(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[unlockContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); 
			STATE = rZArchMFile.getState();
			
			/* Get info. */
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[unlockContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
		}
		
		/* [Check stte]
		 * Only locked contents can be unlocked.
		 */
		if(!STATE.equals(ZappConts.STATES.BUNDLE_LOCK.state)) {
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_STATE", "[unlockContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠 상태가 해당 처리를 할 수 없습니다.
			}
		}
		
		/* [Locked info.]
		 * 
		 */
		ZappLockedObject rZappLockedObject = null;
		pObjRes = selectObject(pObjAuth, new ZappLockedObject(pObjContent.getContentid(), pObjContent.getObjType(), null, null, null), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_LOCKOBJ", "[unlockContent] " + messageService.getMessage("ERR_R_LOCKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 잠금 정보를 조회하는데 실패했습니다.
		}
		List<ZappLockedObject> rZappLockedObjectList = (List<ZappLockedObject>) pObjRes.getResObj();
		for(ZappLockedObject vo : rZappLockedObjectList) {
			if(!vo.getLockerid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				return ZappFinalizing.finalising("ERR_IDENTICAL_LOCKER", "[unlockContent] " + messageService.getMessage("ERR_IDENTICAL_LOCKER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 잠금자가 일치하지 않습니다.
			}
			rZappLockedObject = vo;
		}
		
		/* [Version-Up]
		 * 
		 */
		if(WITHOUT_FILE == false) {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjContent.getZappFile().setMfileid(pObjContent.getZappFile().getMfileid());
				pObjContent.getZappFile().setLinkid(pObjContent.getContentid());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjContent.getZappFile().setLinkid(ZstFwEncodeUtils.encodeString_SHA256(PROCTIME));	// None
				pObjContent.getZappFile().setMfileid(pObjContent.getContentid());
			}
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());						// Task ID
//			pObjContent.getZappFile().setIsreleased(pObjContent.getIsreleased());					// Released? (true -> High, false -> Low)
			pObjRes = addFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_FILE", "[unlockContent] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 잠금 정보를 조회하는데 실패했습니다.
			}
			pObjRes.setResCode(SUCCESS);
		}
		
		/* [Delete locked info.]
		 * 
		 */
		ZappLockedObject pZappLockedObject	= new ZappLockedObject();
		pZappLockedObject.setLobjid(pObjContent.getContentid());
		pZappLockedObject.setLobjtype(pObjContent.getObjType());
		pObjRes = deleteObject(pObjAuth, pZappLockedObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_LOCKOBJ", "[unlockContent] " + messageService.getMessage("ERR_D_LOCKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 임시객체 정보를 삭제하는데 실패했습니다.
		}
		
		/* [Chage state]
		 * 1. Bundle
		 * 2. File 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			// Bundle
			pObjRes = changeObject(pObjAuth
					            , new ZappBundle(pObjContent.getContentid(), ZappConts.STATES.BUNDLE_NORMAL.state)
								, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[unlockContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 수정하는데 실패했습니다.
			}
			
			// File
			pObjRes = changeFile(pObjAuth, new ZappFile(pObjContent.getZappFile().getMfileid()
							      					 , ZappConts.STATES.BUNDLE_NORMAL.state)
								  					 , pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[unlockContent] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 수정하는데 실패했습니다.
			}			
			
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			pObjRes = changeFile(pObjAuth, new ZappFile(pObjContent.getContentid(), ZappConts.STATES.BUNDLE_NORMAL.state)
													  , pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[unlockContent] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 수정하는데 실패했습니다.
			}
		}	
		pObjRes.setResCode(SUCCESS);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.UNLOCK);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_UNLOCK.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}

	/**
	 * Unlock forcely
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult unlockContentForcely(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String STATE = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappLockedObject() == null) { pObjContent.setZappLockedObject(new ZappLockedObject()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "unlockContentForcely", ZappConts.ACTION.UNLOCK);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		/* [Check access control info.]
		 * Check if the user is a company manager.
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[unlockContentForcely] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[unlockContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			STATE = rZappBundle.getState();
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
			pZappFile.setObjTaskid(pObjContent.getObjTaskid());
			pObjRes = getFile(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[unlockContentForcely] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 조회하는데 실패했습니다.
			}
			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); 
			STATE = rZArchMFile.getState();
			
			/* Get info. */
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[unlockContentForcely] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();

		}
		
		/* [Check state]
		 * Only locked contents can be unlocked.
		 */
		if(!STATE.equals(ZappConts.STATES.BUNDLE_LOCK.state)) {
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_STATE", "[unlockContentForcely] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				// 컨테츠 상태가 해당 처리를 할 수 없습니다.
			}
		}
		
		/* [Delete locked info.]
		 * 
		 */
		ZappLockedObject pZappLockedObject	= new ZappLockedObject();
		pZappLockedObject.setLobjid(pObjContent.getContentid());
		pZappLockedObject.setLobjtype(pObjContent.getObjType());
		pObjRes = deleteObject(pObjAuth, pZappLockedObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_LOCKOBJ", "[unlockContentForcely] " + messageService.getMessage("ERR_D_LOCKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 잠금 정보를 삭제하는데 실패했습니다.
		}
		
		/* [Chage state]
		 * 1. Bundle
		 * 2. File 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			// Bundle
			pObjRes = changeObject(pObjAuth
					            , new ZappBundle(pObjContent.getContentid(), ZappConts.STATES.BUNDLE_NORMAL.state)
								, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[unlockContentForcely] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 수정하는데 실패했습니다.
			}
			
			// File
			ZappFile pZappFile_Set = new ZappFile(); ZappFile pZappFile_Where = new ZappFile();
			pZappFile_Set.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
			pZappFile_Where.setLinkid(pObjContent.getContentid());
			pObjRes = changeFiles(pObjAuth, pZappFile_Set, pZappFile_Where, pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[unlockContentForcely] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 수정하는데 실패했습니다.
			}			
			
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			pObjRes = changeFile(pObjAuth, new ZappFile(pObjContent.getContentid(), ZappConts.STATES.BUNDLE_NORMAL.state)
													  , pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[unlockContentForcely] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# 파일 정보를 수정하는데 실패했습니다.
			}
		}	
		pObjRes.setResCode(SUCCESS);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.UNLOCK);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_UNLOCK_FORCELY.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}

	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult markContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappLockedObject() == null) { pObjContent.setZappLockedObject(new ZappLockedObject()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "markContent", ZappConts.ACTION.MARK);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		/* [Check marked info.]
		 * 
		 */
		pObjRes = existObject(pObjAuth, new ZappMarkedObject(pObjContent.getContentid(), pObjContent.getObjType(), pObjAuth.getSessDeptUser().getUserid(), null), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_MARKOBJ", "[markContent] " + messageService.getMessage("ERR_R_MARKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		boolean EXIST = (Boolean) pObjRes.getResObj();
		if(EXIST == true) {
			return ZappFinalizing.finalising("ERR_ALREADY_MARK", "[markContent] " + messageService.getMessage("ERR_ALREADY_MARK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappClassification rZappClassification = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_NODE.type)) {
			pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASS", "[markContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappClassification = (ZappClassification) pObjRes.getResObj();
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[markContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
			pZappFile.setObjTaskid(pObjContent.getObjTaskid());
			pObjRes = getFile(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[markContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); 
			
			/* Get info. */
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[markContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();

		}
		
		/* [Mark]
		 * 
		 */
		ZappMarkedObject pZappMarkedObject = new ZappMarkedObject(pObjContent.getContentid(), pObjContent.getObjType(), pObjAuth.getSessDeptUser().getUserid(), PROCTIME);
		pObjRes = addObject(pObjAuth, pZappMarkedObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_MARKOBJ", "[markContent] " + messageService.getMessage("ERR_C_MARKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.MARK);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_NODE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappClassification.getName());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_MARK.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}

	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult unmarkContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [�ʱ�ȭ] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappLockedObject() == null) { pObjContent.setZappLockedObject(new ZappLockedObject()); }
		
		// ## [Initialization] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "markContent", ZappConts.ACTION.MARK);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		/* [Check marked info.]
		 * 
		 */
		pObjRes = existObject(pObjAuth, new ZappMarkedObject(pObjContent.getContentid(), pObjContent.getObjType(), pObjAuth.getSessDeptUser().getUserid(), null), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_MARK", "[unmarkContent] " + messageService.getMessage("ERR_R_MARK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		boolean EXIST = (Boolean) pObjRes.getResObj();
		if(EXIST == false) {
			return ZappFinalizing.finalising("ERR_NEXIST_MARK", "[unmarkContent] " + messageService.getMessage("ERR_NEXIST_MARK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZArchMFile rZArchMFile = null; ZappClassification rZappClassification = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_NODE.type)) {
			pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASS", "[unmarkContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappClassification = (ZappClassification) pObjRes.getResObj();
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[unmarkContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
			pZappFile.setObjTaskid(pObjContent.getObjTaskid());
			pObjRes = getFile(pObjAuth, pZappFile, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[unmarkContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); 

			/* Get info. */
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[unmarkContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();

		}
		
		/* [Unmark]
		 * 
		 */
		ZappMarkedObject pZappMarkedObject = new ZappMarkedObject(pObjContent.getContentid(), pObjContent.getObjType(), pObjAuth.getSessDeptUser().getUserid(), null);
		pObjRes = deleteObject(pObjAuth, pZappMarkedObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_MARK", "[unmarkContent] " + messageService.getMessage("ERR_D_MARK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.UNMARK);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_NODE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappClassification.getName());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_UNMARK.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		return pObjRes;
		
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult shareContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		String STATE_NOW = BLANK;
		boolean SKIP_CONTENTACL = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappSharedObjects() == null) { pObjContent.setZappSharedObjects(new ArrayList<ZappSharedObject>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "shareContent", ZappConts.ACTION.SHARE);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_APPROVAL_CHANGE_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_CHANGE_YN.env);			// Approval or not
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);			
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; List<ZArchMFileRes> rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			// Bundle
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[lockContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			STATE_NOW = rZappBundle.getState();
			
			// Holder 
			if(rZappBundle.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			// Archive
			pObjRes = getFilesAll(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[lockContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZArchMFile = (List<ZArchMFileRes>) pObjRes.getResObj(); 
			for(ZArchMFile vo : rZArchMFile) {
				STATE_NOW = vo.getState();
			}
			
			// Sub-File info.
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[lockContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
			// Holder 
			if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
		}
		pObjRes.setResCode(SUCCESS);

		/* [Check state]
		 * Only content with a normal state can be shared.
		 */
		if(!STATE_NOW.equals(ZappConts.STATES.BUNDLE_NORMAL.state)) {
			return ZappFinalizing.finalising("ERR_STATE", "[shareContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid());
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[shareContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[shareContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}		
		
		/* [Approval]
		 * 1. In case of approval processing, it is stored in a temporary object.
		 * 2. If approval processing is not performed, share processing is performed immediately.
		 */
		if(SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			
			/* [Temporary info.]
			 * 
			 */
//			ZappTmpObject pZappTmpObject = new ZappTmpObject();
//			pZappTmpObject.setTobjid(pObjContent.getContentid());	// Content ID
//			pZappTmpObject.setTobjtype(pObjContent.getObjType());	// Content type
//			pZappTmpObject.setTmptime(PROCTIME);					// Processing time
//			List<String> STATES = new ArrayList<String>(Arrays.asList(new String[]{ ZappConts.STATES.BUNDLE_LOCK_REQUEST.state, ZappConts.STATES.BUNDLE_LOCK.state }));
//			pZappTmpObject.setStates(ZappJSONUtils.cvrtObjToJson(STATES));
//			pObjRes = addObject(pObjAuth, pZappTmpObject, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_C_TEMP", "[lockContent] " + messageService.getMessage("ERR_C_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
			
		} else {
			
			/* [Share info.]
			 * 1. Check if it is specified as a share target.
			 * 2. Register in bulk.
			 */
			for(int IDX = ZERO; IDX < pObjContent.getZappSharedObjects().size(); IDX++) {
				pObjContent.getZappSharedObjects().get(IDX).setSobjid(pObjContent.getContentid());
				pObjContent.getZappSharedObjects().get(IDX).setSobjtype(pObjContent.getObjType());
				pObjContent.getZappSharedObjects().get(IDX).setSharerid(pObjAuth.getSessDeptUser().getDeptuserid());
				pObjContent.getZappSharedObjects().get(IDX).setSharetime(PROCTIME);
				pObjContent.getZappSharedObjects().get(IDX).setShareobjid(ZappKey.getPk(pObjContent.getZappSharedObjects().get(IDX)));
			}
			
			// 01.
			StringBuffer pks = new StringBuffer();
			for(ZappSharedObject vo : pObjContent.getZappSharedObjects()) {
				pks.append(vo.getShareobjid() + DIVIDER);
			}
			if(pks.length() > ZERO) {
				ZappSharedObject pZappSharedObject_Check = new ZappSharedObject();
				pZappSharedObject_Check.setShareobjid(pks.toString());
				pObjRes = existObject(pObjAuth, pZappSharedObject_Check, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_SHARE", "[shareContent] " + messageService.getMessage("ERR_R_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				boolean EXIST = (Boolean) pObjRes.getResObj();
				if(EXIST == true) {
					return ZappFinalizing.finalising("ERR_DUP_SHARE", "[shareContent] " + messageService.getMessage("ERR_DUP_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			// 02.
			if(pObjContent.getZappSharedObjects().size() > ZERO) {
				pObjRes = addObject(pObjAuth, pObjContent.getZappSharedObjects(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_C_SHARE", "[shareContent] " + messageService.getMessage("ERR_C_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
		}
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.SHARE);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.get(ZERO).getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , (SYS_APPROVAL_CHANGE_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.LOGS.ACTION_SHARE_REQUEST.log : ZappConts.LOGS.ACTION_SHARE.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
		
	}

	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult changeShareContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappSharedObjects() == null) { pObjContent.setZappSharedObjects(new ArrayList<ZappSharedObject>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "unshareContent", ZappConts.ACTION.UNSHARE);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* [ Get info. by type ]
		 * 
		 */
		ZappBundle rZappBundle = null; List<ZArchMFileRes> rZArchMFile = null; ZappFile rZappFile = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			// Bundle
			pObjRes = selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[lockContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj();
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			// Archive
			pObjRes = getFilesAll(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[lockContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZArchMFile = (List<ZArchMFileRes>) pObjRes.getResObj(); 

		}
		pObjRes.setResCode(SUCCESS);
		
		/* [Processing type]
		 * 
		 */
		List<ZappSharedObject> ADD_ = new ArrayList<ZappSharedObject>();
		List<ZappSharedObject> DISCARD_ = new ArrayList<ZappSharedObject>();
		for(ZappSharedObject vo : pObjContent.getZappSharedObjects()) {
			if(vo.getObjAction().equals(ZappConts.ACTION.ADD.name())) {
				ADD_.add(vo);
			}
			if(vo.getObjAction().equals(ZappConts.ACTION.DISCARD.name())) {
				DISCARD_.add(vo);
			}
		}
		
		/* [Delete share info.]
		 * 1. Inquire share info.
		 * 2. Check share info.
		 * 3. Delete share info.
		 */
		if(DISCARD_.size() > ZERO) {
			// 01.
			StringBuffer pks = new StringBuffer();
			for(ZappSharedObject vo : DISCARD_) {
				pks.append(vo.getShareobjid() + DIVIDER);
			}
			ZappSharedObject pZappSharedObject = new ZappSharedObject();	
			pZappSharedObject.setShareobjid(pks.toString());
			pZappSharedObject.setSharerid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjRes = selectObject(pObjAuth, pZappSharedObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_SHARE", "[unshareContent] " + messageService.getMessage("ERR_R_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			@SuppressWarnings("unchecked")
			List<ZappSharedObject> rZappSharedObjectList_Discard = (List<ZappSharedObject>) pObjRes.getResObj();
			
			// 02.
			if(rZappSharedObjectList_Discard != null) {
				if(DISCARD_.size() != rZappSharedObjectList_Discard.size()) {
					return ZappFinalizing.finalising("ERR_MIS_SHARE", "[unshareContent] " + messageService.getMessage("ERR_MIS_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			// 03.
			for(ZappSharedObject vo : DISCARD_) {
				pObjRes = deleteObject(pObjAuth, vo, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_D_SHARE", "[unshareContent] " + messageService.getMessage("ERR_D_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}		
			}
		}
		
		/* [Add share info.]
		 * 1. Set share info.
		 * 2. Check share info.
		 * 3. Add share info.
		 */
		// 01.
		for(int IDX = ZERO; IDX < ADD_.size(); IDX++) {
			ADD_.get(IDX).setSobjid(pObjContent.getContentid());
			ADD_.get(IDX).setSobjtype(pObjContent.getObjType());
			ADD_.get(IDX).setSharerid(pObjAuth.getSessDeptUser().getDeptuserid());
			ADD_.get(IDX).setSharetime(PROCTIME);
			ADD_.get(IDX).setShareobjid(ZappKey.getPk(ADD_.get(IDX)));
		}
		
		// 02.
		StringBuffer pks = new StringBuffer();
		for(ZappSharedObject vo : ADD_) {
			pks.append(vo.getShareobjid() + DIVIDER);
		}
		if(pks.length() > ZERO) {
			ZappSharedObject pZappSharedObject_Check = new ZappSharedObject();
			pZappSharedObject_Check.setShareobjid(pks.toString());
			pObjRes = existObject(pObjAuth, pZappSharedObject_Check, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_SHARE", "[unshareContent] " + messageService.getMessage("ERR_R_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			boolean EXIST = (Boolean) pObjRes.getResObj();
			if(EXIST == true) {
				return ZappFinalizing.finalising("ERR_DUP_SHARE", "[unshareContent] " + messageService.getMessage("ERR_DUP_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		// 03.
		if(ADD_.size() > ZERO) {
			pObjRes = addObject(pObjAuth, ADD_, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_SHARE", "[unshareContent] " + messageService.getMessage("ERR_C_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}		
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle);
			pObjContent.setZappFile(rZappFile);
			ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.UNSHARE);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.get(ZERO).getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_CHANGE_SHARE.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		
		
		return pObjRes;
	}
	
	// ### Extend retention period 
	public ZstFwResult extendContentRetention(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();						// Processing time
		ZappContentRes rZappContentRes = new ZappContentRes();			// Content Result	
		boolean SKIP_CONTENTACL = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();		// Log
		
		if(pObjContent == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[extendContentRetention] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// ## Initialization
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);			
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "extendContentRetention", ZappConts.ACTION.VIEW_PK);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// [ACL] Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		/* [Get info. by type]
		 * 
		 */
		pObjContent.setContentid(pObjContent.getContentid());
		pObjRes = contentService.rContent(pObjAuth, pObjContent, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CONTENT", "[extendContentRetention] " + messageService.getMessage("ERR_R_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappContentRes = (ZappContentRes) pObjRes.getResObj();
		if(rZappContentRes == null) {
			return ZappFinalizing.finalising("ERR_R_CONTENT", "[extendContentRetention] " + messageService.getMessage("ERR_R_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		if(rZappContentRes.getRetentionid().equals(pObjContent.getObjRetention())) {
			return ZappFinalizing.finalising("ERR_IDENTICAL_EXPERIOD", "[extendContentRetention] " + messageService.getMessage("ERR_IDENTICAL_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		if(rZappContentRes.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Check whether the company manager or manager mode]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == true && pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid());
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[extendContentRetention] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[extendContentRetention] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}
		
		/* [Get retention info.]
		 * 
		 */
		ZappCode pZappCode = new ZappCode();
		pZappCode.setCodeid(pObjContent.getObjRetention());
		pZappCode.setTypes(ZappConts.TYPES.CODE_RETENTION.type);
		pObjRes = systemService.selectObject(pObjAuth, pZappCode, pObjRes);	logger.info("[extendContentRetention] codeid = " + pZappCode.getCodeid() );
		if(ZappFinalizing.isSuccess(pObjRes) == true) {
			List<ZappCode> rZappCodeList = (List<ZappCode>) pObjRes.getResObj();
			if(rZappCodeList == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[extendContentRetention] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rZappCodeList.size() == ZERO) {
				return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[extendContentRetention] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			ZappCode rZappCode = null;
			for(ZappCode vo : rZappCodeList) {
				rZappCode = vo;
			}
			try {
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					pObjContent.getZappBundle().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode != null ? rZappCode.getCodevalue() : String.valueOf(ZERO))));
				}
				else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					pObjContent.getZappFile().setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode != null ? rZappCode.getCodevalue() : String.valueOf(ZERO))));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/* [Extend]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			if(ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getExpiretime()) == false) {
				return ZappFinalizing.finalising("ERR_MIS_EXPERIOD", "[extendContentRetention] " + messageService.getMessage("ERR_MIS_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}

			pObjContent.getZappBundle().setBundleid(pObjContent.getContentid());
			pObjContent.getZappBundle().setRetentionid(pObjContent.getObjRetention());
			
			// Bundle
			pObjRes = changeObject(pObjAuth, pObjContent.getZappBundle(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[extendContentRetention] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			if(ZstFwValidatorUtils.valid(pObjContent.getZappFile().getExpiretime()) == false) {
				return ZappFinalizing.finalising("ERR_MIS_EXPERIOD", "[extendContentRetention] " + messageService.getMessage("ERR_MIS_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			pObjContent.getZappFile().setMfileid(pObjContent.getContentid());
			pObjContent.getZappFile().setRetentionid(pObjContent.getObjRetention());
			
			pObjRes = changeFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_FILE", "[extendContentRetention] " + messageService.getMessage("ERR_E_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}	
		pObjRes.setResCode(SUCCESS);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContent);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappContentRes.getTitle());
			pObjRes = leaveLog(pObjAuth
					         , pObjContent.getObjType()
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		/* [Result]
		 * 
		 */
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	// ### Inquire content
	@SuppressWarnings("unchecked")
	public ZstFwResult selectContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();						// Processing time
		String CONTENTID = BLANK;										// Content ID
		boolean SKIP_CONTENTACL = false;
		ZappContentRes rZappContentRes = new ZappContentRes();			// Content Result
		Map<String, Object> LOGMAP = new HashMap<String, Object>();		// Log
		
		if(pObjContent == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[selectContent] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		// ## Initialization
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "selectContent", ZappConts.ACTION.VIEW_PK);
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_APPROVAL_VIEW_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.APPROVAL_VIEW_YN.env);				// [APPROVAL] Whether to apply approval process
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// [ACL] Whether to apply content access info.
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		/* [Get info. by type]
		 * 
		 */
		CONTENTID = pObjContent.getContentid();
		pObjContent.setContentid(CONTENTID);
		pObjAuth.setObjSkipCpath(NO);
		pObjRes = contentService.rContent(pObjAuth, pObjContent, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CONTENT", "[selectContent] " + messageService.getMessage("ERR_R_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappContentRes = (ZappContentRes) pObjRes.getResObj();
		if(rZappContentRes == null) {
			return ZappFinalizing.finalising("ERR_R_CONTENT", "[selectContent] " + messageService.getMessage("ERR_R_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		if(rZappContentRes.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Checking access free member]
		 * 
		 */
		if(aclService.isAccessFreeContent(pObjAuth) == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Classification]
		 * 
		 */
		ZappClassification rZappClassification = null;
		ZappClassObject pZappClassObject = new ZappClassObject(null, CONTENTID, pObjContent.getObjType()); 
//		pZappClassObject.setClasstype(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type + DIVIDER + ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type + DIVIDER + ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type + DIVIDER + ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type);
		pObjRes = selectExtendObject(pObjAuth, null, pZappClassObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[selectContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappClassification> rZappClassificationList = (List<ZappClassification>) pObjRes.getResObj();
		if(rZappClassificationList == null) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[selectContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappClassification vo : rZappClassificationList) {
			if(isNodeType(vo.getTypes()) == true) {
				rZappClassification = vo;
			}
		}
		if(rZappClassification == null) {
			return ZappFinalizing.finalising("ERR_R_CLASS", "[selectContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		// Company
		if(rZappClassification.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
			SKIP_CONTENTACL = true;
		}
		// Personal
		if(rZappClassification.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			if(rZappClassification.getHolderid().equals(pObjAuth.getSessDeptUser().getUserid())) {
				SKIP_CONTENTACL = true;
			}
		}
		
		/* [Check whether the company manager or manager mode]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == true && pObjContent.getObjIsMngMode() == true) {
			SKIP_CONTENTACL = true;
		}
		
		/* [Approval]
		 * 
		 */
		if(rZappClassification.getWfrequired() > ZERO && SKIP_CONTENTACL == false) {
			ZappWorkflowObject pZappWorkflowObject = new ZappWorkflowObject();
			pZappWorkflowObject.setContentid(pObjContent.getContentid());
			pZappWorkflowObject.setContenttype(pObjContent.getObjType());
			pZappWorkflowObject.setWferid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjRes = workflowService.existObject(pObjAuth, pZappWorkflowObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_WFOBJ", "[selectContent] " + messageService.getMessage("ERR_R_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == true) {
				SKIP_CONTENTACL = true;
			}
		}
		
		/* [Check shared doc.]
		 * 
		 */
		if(SKIP_CONTENTACL == false) {
			ZappSharedObject pZappSharedObject = new ZappSharedObject();
			pZappSharedObject.setSobjid(pObjContent.getContentid());
			pZappSharedObject.setSobjtype(pObjContent.getObjType());
			pZappSharedObject.setReaderid(pObjAuth.getSessDeptUser().getDeptuserid());
			pObjRes = existObject(pObjAuth, pZappSharedObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_SHARE", "[selectContent] " + messageService.getMessage("ERR_R_SHARE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == true) {
				SKIP_CONTENTACL = true;
			}
		}
		
		/* [Check access control info.]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(pObjContent.getContentid());
			pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_READ.acl);
			pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[selectContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			boolean CANDO = (Boolean) pObjRes.getResObj();
			if(CANDO == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[selectContent] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}	
		
		/* [File]
		 * 
		 */
		pObjAuth.setObjType(pObjContent.getObjViewtype());	// View type
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			
			if(rZappContentRes.getCtype().equals(ZappConts.TYPES.BUNDLE_NORMAL.type)) {
				if(pObjContent.getZappFile() == null) pObjContent.setZappFile(new ZappFile());
				pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());		// Task ID
				pObjContent.getZappFile().setLinkid(CONTENTID);							// Content ID (Bundle)
				pObjAuth.setObjType(pObjContent.getObjViewtype());						// View
				pObjRes = getMaxVersionFiles(pObjAuth, pObjContent.getObjType(), pObjContent.getZappFile(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_FILE", "[selectContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				rZappContentRes.setZappFiles((List<ZArchMFileRes>) pObjRes.getResObj());
	//			if(pObjContent.getObjViewtype().equals(ZappConts.TYPES.VIEWTYPE_VIEW.type)) {			// Normal
	//				rZappContentRes.setZappFiles(extractMajorVersions((List<ZArchMFileRes>) pObjRes.getResObj()));
	//			} else if(pObjContent.getObjViewtype().equals(ZappConts.TYPES.VIEWTYPE_EDIT.type)) {	// Edit
	//				rZappContentRes.setZappFiles((List<ZArchMFileRes>) pObjRes.getResObj());
	//			}
			} else if(rZappContentRes.getCtype().equals(ZappConts.TYPES.BUNDLE_VIRTUAL.type)) {
				// 01. Link Info.
				ZappLinkedObject pZappLinkedObject = new ZappLinkedObject();
				pZappLinkedObject.setSourceid(CONTENTID);
//				pZappLinkedObject.setLinktype(ZappConts.TYPES.LINK_BTOF.type);
				pObjRes = selectObject(pObjAuth, pZappLinkedObject, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_LINKOBJ", "[selectContent] " + messageService.getMessage("ERR_R_LINKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				List<ZappLinkedObject> rZappLinkedObjList = (List<ZappLinkedObject>) pObjRes.getResObj();
				// 02. File Info.
				if(rZappLinkedObjList != null) {
					List<ZArchMFileRes> rZArchMFileList = new ArrayList<ZArchMFileRes>();
					for(ZappLinkedObject vo : rZappLinkedObjList) {
						ZappFile pZappFile = new ZappFile();	// Master File ID
						pZappFile.setObjTaskid(pObjContent.getObjTaskid());		// Task ID
						if(vo.getLinktype().equals(ZappConts.TYPES.LINK_BTOF.type)) {
							pZappFile.setMfileid(vo.getTargetid());
							pObjAuth.setObjType(pObjContent.getObjViewtype());				// View
							pObjRes = getMaxVersionFiles(pObjAuth, ZappConts.TYPES.CONTENT_FILE.type, pZappFile, pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_R_FILE", "[selectContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
							rZArchMFileList.addAll((List<ZArchMFileRes>) pObjRes.getResObj());
						} else if(vo.getLinktype().equals(ZappConts.TYPES.LINK_BTOB.type)) {
							// Bundle
							pObjRes = selectObject(pObjAuth, new ZappBundle(vo.getTargetid()), pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_R_BUNDLE", "[selectContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}			
							ZappBundle rZappBundle = (ZappBundle) pObjRes.getResObj();
							ZArchMFileRes pZArchMFileRes = new ZArchMFileRes();
							pZArchMFileRes.setMfileid(vo.getTargetid());
							pZArchMFileRes.setFilename(rZappBundle.getTitle());
							pZArchMFileRes.setState(BLANK);
							rZArchMFileList.add(pZArchMFileRes);
							
							// File
							pZappFile.setLinkid(vo.getTargetid());
							pObjAuth.setObjType(pObjContent.getObjViewtype());				// View
							pObjRes = getMaxVersionFiles(pObjAuth, ZappConts.TYPES.CONTENT_BUNDLE.type, pZappFile, pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_R_FILE", "[selectContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
							rZArchMFileList.addAll((List<ZArchMFileRes>) pObjRes.getResObj());
						}
					}
					rZappContentRes.setZappFiles(rZArchMFileList);
				}
			}
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());		// Task ID
			pObjContent.getZappFile().setMfileid(CONTENTID);						// Content ID (File)
			pObjAuth.setObjType(pObjContent.getObjViewtype());				// View
			pObjRes = getMaxVersionFiles(pObjAuth, pObjContent.getObjType(), pObjContent.getZappFile(), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[selectContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappContentRes.setZappFiles((List<ZArchMFileRes>) pObjRes.getResObj());
//			rZappContentRes.setZappFiles(new ArrayList<ZArchMFileRes>(Arrays.asList(new ZArchMFileRes[]{ (ZArchMFileRes) pObjRes.getResObj() })));
//			if(pObjContent.getObjViewtype().equals(ZappConts.TYPES.VIEWTYPE_VIEW.type)) {			// Normal
//				rZappContentRes.setZappFiles(extractMajorVersion((List<ZArchMFileRes>) pObjRes.getResObj()));
//			} else if(pObjContent.getObjViewtype().equals(ZappConts.TYPES.VIEWTYPE_EDIT.type)) {	// Edit
//				rZappContentRes.setZappFiles((List<ZArchMFileRes>) pObjRes.getResObj());
//			}
			
		}
		pObjRes.setResCode(SUCCESS);
		
		/* [Access control info.]
		 * 
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			ZappContentAcl pZappContentAcl = new ZappContentAcl();
			pZappContentAcl.setContentid(CONTENTID);
			pObjRes = aclService.selectExtendObject(pObjAuth, null, pZappContentAcl, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_R_CONTENTACL", "[selectContent] " + messageService.getMessage("ERR_R_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
			rZappContentRes.setZappAcls((List<ZappAclExtend>) pObjRes.getResObj());
		}
		
		/* [Classification]
		 * 
		 */
//		ZappClassObject pZappClassObject = new ZappClassObject(null, CONTENTID, pObjContent.getObjType()); 
//		pObjRes = selectExtendObject(pObjAuth, null, pZappClassObject, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_R_CLASS", "[selectContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		rZappContentRes.setZappClassifications((List<ZappClassification>) pObjRes.getResObj());
		rZappContentRes.setZappClassifications(rZappClassificationList);
		
		/* [Keyword]
		 * 
		 */
		ZappKeywordObject pZappKeywordObject = new ZappKeywordObject();
		pZappKeywordObject.setKobjid(CONTENTID);
		pZappKeywordObject.setKobjtype(pObjContent.getObjType());
		pObjRes = selectExtendObject(pObjAuth, null, pZappKeywordObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_KEYWORD", "[selectContent] " + messageService.getMessage("ERR_R_KEYWORD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappContentRes.setZappKeywords((List<ZappKeywordObject>) pObjRes.getResObj());
		
		/* [Locked info.]
		 * 
		 */
		if(rZappContentRes.getState().equals(ZappConts.STATES.BUNDLE_LOCK.state)) {
			ZappLockedObject pZappLockedObject = new ZappLockedObject();
			pZappLockedObject.setLobjid(CONTENTID);
			pZappLockedObject.setLobjtype(pObjContent.getObjType());
			pObjRes = selectExtendObject(pObjAuth, null, pZappLockedObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASS", "[selectContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappContentRes.setZappLockedObject((List<ZappLockedObjectExtend>) pObjRes.getResObj());
		}
		
		/** [Additional Info.]
		 * 
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			ZappAdditoryBundle pZappAdditoryBundle = new ZappAdditoryBundle();
			pZappAdditoryBundle.setBundleid(CONTENTID);
			pObjRes = selectObject(pObjAuth, pZappAdditoryBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLEEXT", "[selectContent] " + messageService.getMessage("ERR_R_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappContentRes.setZappAdditoryBundle((ZappAdditoryBundle) pObjRes.getResObj());
		}
		
		/* [ACL]
		 * 2022-05-10
		 */
		if(SYS_CONTENTACL_YN.getSetval().equals(YES)) {
			List<ZappContentRes> aclList = new ArrayList<ZappContentRes>();
			aclList.add(rZappContentRes);
			pObjRes = aclService.optimizeObject(pObjAuth, aclList, pObjRes);
			List<ZappContentRes> rZappContentResList = (List<ZappContentRes>) pObjRes.getResObj();
			for(ZappContentRes vo : rZappContentResList) {
				rZappContentRes.getZappAcl().setAcls(vo.getZappAcl().getAcls());
			}
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			ZappContentRes pObjContentLog = logService.initLogRes(rZappContentRes, ZappConts.ACTION.VIEW);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
//			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, rZappContentRes);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, CONTENTID);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappContentRes.getTitle());
			pObjRes = leaveLog(pObjAuth
					         , pObjContent.getObjType()
							 , (SYS_APPROVAL_VIEW_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? ZappConts.LOGS.ACTION_VIEW_REQUEST.log : ZappConts.LOGS.ACTION_VIEW.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		/* [Result]
		 * 
		 */
		pObjRes.setResObj(rZappContentRes);
		
		return pObjRes;
	}
	
	/**
	 * Inquire version list. 
	 */
	public ZstFwResult selectVersion(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();						// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();				// Log
		
		if(pObjContent == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[selectVersion] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# 인자값이 누락되었습니다.
		}		
		
		// ## Initialization
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle(new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		
		/* Validation */
//		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "selectVersion", ZappConts.ACTION.VIEW_PK);
		
		/* Get info. */
		ZArchVersion pZArchVersion = new ZArchVersion();
		pZArchVersion.setMfileid(pObjContent.getContentid());
		pObjAuth.setObjType(pObjContent.getObjViewtype());	// View type
		
		pObjRes = contentService.rMultiRowsVersion(pObjAuth, pZArchVersion, null, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_VERSION", "[selectVersion] " + messageService.getMessage("ERR_R_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			//# Keyword 정보를 조회하는데 실패했습니다.
		}

		return pObjRes;
	}
	
	// ### Inquire physical content list  ###
	
	@SuppressWarnings("unchecked")
	public ZstFwResult selectPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		boolean REQ_ACL_YN = true; int REQ_ACL = ZERO;
		String CLASSID = BLANK;
		
		if(pObjw == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[selectPhysicalList] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Init]
		 * 
		 */
		if(pObjw.getzArchMFile() == null) { pObjw.setzArchMFile(new ZArchMFile()); }
		
		/* [Filter]
		 * 
		 */
		if(pObjf == null) {
			pObjf = new ZappContentPar();
		}
		pObjAuth.setObjSkipAcl(false);
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjw);			
		
		/* [Bundle]
		 * 
		 */
		if(pObjw.getZappBundle() == null) {
			pObjw.setZappBundle(new ZappBundle());
		} 
		pObjw.getZappBundle().setState(getViewableStates());
		
		/* [Sub-Bundle]
		 * 
		 */
		if(pObjw.getZappAdditoryBundle() != null) {
			if(utilBinder.isEmptyPk(pObjw.getZappAdditoryBundle()) == true && utilBinder.isEmpty(pObjw.getZappAdditoryBundle()) == true) {
				pObjw.setZappAdditoryBundle(null);
			}
		} 
		
		/* [File]
		 * 
		 */
		if(pObjw.getZappFile() != null) {
			ZArchMFile pZArchMFile = new ZArchMFile();
			pZArchMFile.setState(getViewableStates());
			if(ZstFwValidatorUtils.valid(pObjw.getZappFile().getFilename()) == true) {		// File name 
				pZArchMFile.setFilename(pObjw.getZappFile().getFilename());
				pObjw.getZappFile().setFilename(null);
			} 
			if(ZstFwValidatorUtils.valid(pObjw.getZappFile().getCreatetime()) == true) {	// File name 
//				String[] ctimes = pObjw.getZappFile().getCreatetime().split(DIVIDER);
//				if(ctimes != null) {
//					if(ctimes.length == TWO) {
						pZArchMFile.setCreatetime(pObjw.getZappFile().getCreatetime());
						pObjw.getZappFile().setCreatetime(null);
//					}
//				}
			}
			if(utilBinder.isEmptyPk(pObjw.getZappFile()) == true && utilBinder.isEmpty_File(pObjw.getZappFile()) == true) {
				pObjw.setZappFile(null);
			}
			pObjw.setzArchMFile(pZArchMFile);
		} else {
			ZArchMFile pZArchMFile = new ZArchMFile();
			pZArchMFile.setState(getViewableStates());
			if(pObjw.getzArchMFile().getCreatetime() != null) {
				pZArchMFile.setCreatetime(pObjw.getzArchMFile().getCreatetime());
			}
			pObjw.setzArchMFile(pZArchMFile);
		}
		
		/* [Keyword]
		 * 
		 */
		if(pObjw.getZappKeywords() != null) {
			if(pObjw.getZappKeywords().size() > ZERO) {
				StringBuffer kwords = new StringBuffer();
				for(ZappKeywordExtend vo : pObjw.getZappKeywords()) {
					kwords.append(vo.getKword() + DIVIDER);
				}
				pObjw.setZappKeyword(new ZappKeyword());
				pObjw.getZappKeyword().setKword(kwords.toString());
			}
		}		
		
		/* [Processing by type]
		 * 1. Classification - Folder, Classification system and Content type
		 * 2. Link - Linked content list
		 * 3. Share - Shared content list
		 * 4. Lock - Locked content list
		 * 5. Mark - Marked content list
		 */
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_CLASS.type)) {	// Classification
			if(pObjw.getZappClassification() != null) {
				if(utilClassBinder.isEmptyPk(pObjw.getZappClassification()) == true && utilClassBinder.isEmpty(pObjw.getZappClassification()) == true) {
					pObjw.setZappClassification(null);
				} 
			}
			
			/* Disable conditions */
//			pObjw.setZappClassification(null);
			pObjw.setZappLinkedObject(null);	
			pObjw.setZappSharedObject(null); 
			pObjw.setZappLockedObject(null); 
			CLASSID = pObjw.getZappClassification().getClassid();
			
			/* Whether to include sub-classification */
			if(ZstFwValidatorUtils.valid(pObjw.getObjIncLower())) {
				if(pObjw.getObjIncLower().equals(YES)) {
//					ZappClassificationPar pZappClassification = new ZappClassificationPar(pObjw.getZappClassification().getClassid());
					ZappClassificationPar pZappClassification = new ZappClassificationPar();
					pZappClassification.setUpid(pObjw.getZappClassification().getClassid());
					pZappClassification.setTypes(pObjw.getZappClassification().getTypes());
					pObjRes = classService.selectObjectDown(pObjAuth, null, pZappClassification, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_CLASS", "[selectPhysicalList] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					List<ZappClassificationRes> rZappClassDownList = (List<ZappClassificationRes>) pObjRes.getResObj();
					StringBuffer sbids = new StringBuffer();
					if(rZappClassDownList != null) {
						for(ZappClassificationRes vo : rZappClassDownList) {
							sbids.append(vo.getZappClassification().getClassid() + DIVIDER);
						}
						sbids.append(pObjw.getZappClassification().getClassid());
					} else {
						sbids.append(pObjw.getZappClassification().getClassid());
					}
					if(sbids.length() > ZERO) {
						pObjw.getZappClassification().setClassid(sbids.toString());
					}
				}
				
				// Filter
				ZappClassification pZappClassification_Filter = new ZappClassification();
				pZappClassification_Filter.setClassid(Operators.IN.operator);
				if(ZstFwValidatorUtils.valid(pObjw.getZappClassification().getName())) {
					pZappClassification_Filter.setName(Operators.EQUAL.operator);
				}
				pObjf.setZappClassification(pZappClassification_Filter);
			}
			
		}
		
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_LINK.type)) {		// Linked

			/* Disable conditions */
			if(pObjw.getZappLinkedObject() != null) {
				pObjw.setZappClassification(null);
				pObjw.setZappClassObject(null);
//				pObjw.setZappLinkedObject(null);	
				pObjw.setZappSharedObject(null); 
				pObjw.setZappLockedObject(null);
			}
			
		}
		
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_SHARE.type)) {	// Shared

			/* Disable conditions */
			if(pObjw.getZappSharedObject() != null) {
				pObjw.setZappClassification(null);
				pObjw.setZappClassObject(null);
				pObjw.setZappLinkedObject(null);	
//				pObjw.setZappSharedObject(null); 
				pObjw.setZappLockedObject(null); 	
				
				// 
				if(ZstFwValidatorUtils.valid(pObjw.getZappSharedObject().getReaderid())) {
					pObjw.getZappSharedObject().setObjHasconds(YES);
				} else {
					pObjw.getZappSharedObject().setObjHasconds(NO);
				}
				
				if(ZstFwValidatorUtils.valid(pObjw.getZappSharedObject().getSharerid())) {
					pObjw.getZappSharedObject().setSharerid(pObjAuth.getSessDeptUser().getDeptuserid());
				}
				if(ZstFwValidatorUtils.valid(pObjw.getZappSharedObject().getReaderid())) {
					pObjw.getZappSharedObject().setReaderid(pObjAuth.getSessDeptUser().getDeptuserid());
					REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_DOWNLOAD.acl;
				}
			}
			
		}
		
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_LOCK.type)) {		// Locked

			/* Disable conditions */
			if(pObjw.getZappLockedObject() != null) {
				pObjw.setZappClassification(null);
				pObjw.setZappClassObject(null);
				pObjw.setZappLinkedObject(null);	
				pObjw.setZappSharedObject(null); 
//				pObjw.setZappLockedObject(null); 			
			}

		}
		
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_MARK.type)) {		// Marked

			/* Disable conditions */
			if(pObjw.getZappMarkedObject() != null) {
				pObjw.setZappClassification(null);
				pObjw.setZappClassObject(null);
				pObjw.setZappLinkedObject(null);	
				pObjw.setZappSharedObject(null); 
				pObjw.setZappLockedObject(null); 			
			} else {
				pObjw.setZappMarkedObject(new ZappMarkedObject());
			}

		}		
		
		/* [Check classification access control info.]
		 * 
		 */
		boolean CANDO = false;
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_CLASS.type)) {	// Classification
			if(ZstFwValidatorUtils.valid(pObjw.getZappClassification().getClassid()) && isAclFreeNodeType(pObjw.getZappClassification().getTypes()) == false) {
				
				/* Check access free member or not */
				if(aclService.isAccessFreeContent(pObjAuth) == false) {
					ZappClassAcl pZappClassAcl = new ZappClassAcl();
					pZappClassAcl.setClassid(pObjw.getZappClassification().getClassid());
					pZappClassAcl.setAcls(ZappConts.ACLS.CLASS_READONLY.acl);
					pObjRes = aclService.checkObject(pObjAuth, pZappClassAcl, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_CLASSACL", "[selectPhysicalList] " + messageService.getMessage("ERR_R_CLASSACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					CANDO = (Boolean) pObjRes.getResObj();
					if(CANDO == false) {
						return ZappFinalizing.finalising("ERR_NO_ACL", "[selectPhysicalList] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				} else {
					pObjw.setObjSkipAcl(true); pObjAuth.setObjSkipAcl(true);
					REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_DOWNLOAD.acl;
				}
			}
			if(pObjw.getZappClassification().getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)) {
				pObjw.setObjSkipAcl(true);
				pObjAuth.setObjSkipAcl(true);
				if(aclService.isCompanyManager(pObjAuth) == true) {
					REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_CHANGE.acl;
				} else {
					REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_DOWNLOAD.acl;
				}
			}
			// 
			if(pObjw.getZappClassification().getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
				pObjRes = classService.selectObject(pObjAuth, new ZappClassification(CLASSID), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_CLASS", "[selectPhysicalList] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				ZappClassification rZappClassification = (ZappClassification) pObjRes.getResObj();
				if(rZappClassification == null) {
					return ZappFinalizing.finalising("ERR_R_CLASS", "[selectPhysicalList] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				boolean OWNED = false;
				for(ZappDeptUserExtend vo : pObjAuth.getSessDeptUsers()) {
					if(vo.getZappUser().getUserid().equals(rZappClassification.getHolderid())) {
						OWNED = true; break;
					}
				}
				if(OWNED == true) {
					pObjw.setObjSkipAcl(true);
					pObjAuth.setObjSkipAcl(true);
//					REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_CHANGE.acl;
				}
			}
			
			pObjAuth.setObjSkipCpath(YES);
		}
		
		/* [Check whether the company manager or manager mode]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == true && pObjw.getObjIsMngMode() == true) {
			pObjw.setObjSkipAcl(true);
			pObjAuth.setObjSkipAcl(true);
		}
		
		/* [Result]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjw.getObjRes()) == false) {
			pObjw.setObjRes(ZappConts.TYPES.RESULT_LIST.type);
		}
		if(pObjw.getObjRes().equals(ZappConts.TYPES.RESULT_COUNT.type)) {			// Counting
			pObjRes = contentService.rPhysicalCount(pObjAuth, pObjf, pObjw, pObjRes);
		} else if(pObjw.getObjRes().equals(ZappConts.TYPES.RESULT_LIST.type)) {	// List
			pObjRes = contentService.rPhysicalList(pObjAuth, pObjf, pObjw, pObjRes);
			
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
			}
//			if(SYS_CONTENTACL_YN.getSetval().equals(YES)) {
//				pObjRes = aclService.optimizeObject(pObjAuth, (List<ZappContentRes>) pObjRes.getResObj(), pObjRes);
//			}
			
			if(SYS_CONTENTACL_YN.getSetval().equals(YES)) {
				if(REQ_ACL_YN == true) {
					pObjRes = aclService.optimizeObject(pObjAuth, (List<ZappContentRes>) pObjRes.getResObj(), pObjRes);
				} else {
					pObjRes = aclService.manualObject(pObjAuth, (List<ZappContentRes>) pObjRes.getResObj(), REQ_ACL, pObjRes);
				}
			} else {
				List<ZappContentRes> rTmpList = (List<ZappContentRes>) pObjRes.getResObj();
				if(rTmpList != null) {
					for(int IDX = ZERO; IDX < rTmpList.size(); IDX++) {
						if(rTmpList.get(IDX).getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
							rTmpList.get(IDX).setZappAcl(new ZappAclExtend(ZappConts.ACLS.CONTENT_CHANGE.acl));
						} else {
							rTmpList.get(IDX).setZappAcl(new ZappAclExtend(ZappConts.ACLS.CONTENT_READ.acl));
						}
					}
				}
				pObjRes.setResObj(rTmpList);
			}
			
		}
		
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_CLASS.type)) {
			pObjRes.setTrace(CANDO == true ? YES : NO);
			
			 // Class Path
			if(pObjw.getObjRes().equals(ZappConts.TYPES.RESULT_LIST.type)) {
				if(pObjRes.getResObj() != null) {
					List<ZappContentRes> tmpList = (List<ZappContentRes>) pObjRes.getResObj();
					if(tmpList != null) {
		
						// Query Class Path
	//					String CPATH = classService_.rClassPathUp(pObjAuth, pObjw.getZappClassification().getClassid(), true);
						ZappClassification rZappClassification = (ZappClassification) classService_.rSingleRow(pObjAuth, pObjw.getZappClassification());
						String CPATH = BLANK;
						if(rZappClassification != null) {
						   CPATH = rZappClassification.getCpath();
						}
						for(int IDX = ZERO; IDX < tmpList.size(); IDX++) {
							tmpList.get(IDX).setClasspath(CPATH);
						}
						pObjRes.setResObj(tmpList);
					}
				}
			}
		}
		
		return pObjRes;

	}
	
	// ### Inquire Non-Physical list  ###
	
	@SuppressWarnings("unchecked")
	public ZstFwResult selectNonPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		// Valiables
		boolean REQ_ACL_YN = true; int REQ_ACL = ZappConts.ACLS.CONTENT_READ.acl;
		
		// Validation
		if(pObjw == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[selectNonPhysicalList] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// ## Initialization
		if(pObjw.getZappFile() == null) {pObjw.setZappFile(new ZappFile());}
				
		/* [Filter]
		 * 
		 */
		if(pObjf == null) {
			pObjf = new ZappContentPar();
		}
		pObjAuth.setObjSkipAcl(false);
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjw);	
		
		/* [Bundle]
		 * 
		 */
		if(pObjw.getZappBundle() == null) {
			pObjw.setZappBundle(new ZappBundle());
			if(!pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_BIN.type) 
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD.type)
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD_ADMIN.type)
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type)
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_OBJECT.type)) {
				pObjw.getZappBundle().setState(getViewableStates());
			} else {
				if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type)) {
					pObjw.getZappBundle().setState(getRequestStates());
					pObjw.setObjSkipAcl(true); pObjAuth.setObjSkipAcl(true);
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_APPROVED.type)) {
					pObjw.getZappBundle().setState(getViewableStates());
					pObjw.setObjSkipAcl(true); pObjAuth.setObjSkipAcl(true);
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_RETURNED.type)) {
					pObjw.getZappBundle().setState(getReturnStates());
					pObjw.setObjSkipAcl(true); pObjAuth.setObjSkipAcl(true);
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_OBJECT.type)) {
					pObjw.getZappBundle().setState(getAllRequestStates());
					pObjw.setObjSkipAcl(true); pObjAuth.setObjSkipAcl(true);
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_BIN.type)) {
					pObjw.getZappBundle().setState(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state);
					pObjw.setObjSkipAcl(true); pObjAuth.setObjSkipAcl(true);
				} else {
					pObjw.setZappBundle(null);
				}
			}
		} 
		if(pObjw.getZappBundle() != null) {
			if(utilBinder.isEmptyPk(pObjw.getZappBundle()) == true && utilBinder.isEmpty(pObjw.getZappBundle()) == true) {
				pObjw.setZappBundle(null);
			}
		}
		
		/* [Sub-Bundle]
		 * 
		 */
		if(pObjw.getZappAdditoryBundle() != null) {
			if(utilBinder.isEmptyPk(pObjw.getZappAdditoryBundle()) == true && utilBinder.isEmpty(pObjw.getZappAdditoryBundle()) == true) {
				pObjw.setZappAdditoryBundle(null);
			}
		} 
		
		/* [File]
		 * 
		 */
		if(pObjw.getZappFile() != null) {
			ZArchMFile pZArchMFile = new ZArchMFile();
			BeanUtils.copyProperties(pObjw.getZappFile(), pZArchMFile);
			if(!pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_BIN.type) 
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD.type)
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD_ADMIN.type)
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type)
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_OBJECT.type)) {
				pZArchMFile.setState(getViewableStates());
			} else {
				if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type)) {
					pZArchMFile.setState(getRequestStates());
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_APPROVED.type)) {
					pZArchMFile.setState(getViewableStates());
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_RETURNED.type)) {
					pZArchMFile.setState(getReturnStates());
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_OBJECT.type)) {
					pZArchMFile.setState(getAllRequestStates());
				} else if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_BIN.type)) {
					pZArchMFile.setState(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state);
					pObjw.setObjSkipAcl(true); pObjAuth.setObjSkipAcl(true);
				}
			}
			if(ZstFwValidatorUtils.valid(pObjw.getZappFile().getFilename()) == true) {		// File name
				pZArchMFile.setFilename(pObjw.getZappFile().getFilename());
				pObjw.getZappFile().setFilename(null);
			} 
			if(ZstFwValidatorUtils.valid(pObjw.getZappFile().getCreatetime()) == true) {	// Create time
				String[] ctimes = pObjw.getZappFile().getCreatetime().split(DIVIDER);
				if(ctimes != null) {
					if(ctimes.length == TWO) {
						pZArchMFile.setCreatetime(pObjw.getZappFile().getCreatetime());
					}
				}
			}
			if(utilBinder.isEmptyPk(pObjw.getZappFile()) == true && utilBinder.isEmpty_File(pObjw.getZappFile()) == true) {
				pObjw.setZappFile(null);
			}
			
			if(ZstFwValidatorUtils.valid(pZArchMFile.getState()) == false
					&& ZstFwValidatorUtils.valid(pZArchMFile.getFilename()) == false) {		
				pObjw.setzArchMFile(null);
			} else {
				pObjw.setzArchMFile(pZArchMFile);
			}
		}
		
		
		/* [Keyword]
		 * 
		 */
		if(pObjw.getZappKeywords() != null) {
			if(pObjw.getZappKeywords().size() > ZERO) {
				StringBuffer kwords = new StringBuffer();
				for(ZappKeywordExtend vo : pObjw.getZappKeywords()) {
					kwords.append(vo.getKword() + DIVIDER);
				}
				pObjw.setZappKeyword(new ZappKeyword());
				pObjw.getZappKeyword().setKword(kwords.toString());
			}
		}		
		
		/* [Processing by type]
		 * 1. Recently registered
		 * 2. Registered by myself
		 * 3. Owned
		 * 4. Recently Updated 
		 * 5. Coming to expiry
		 * 6. Expired
		 */
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_RECENT_ADD.type)) {		// Recently registered
			
		}		
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_SELF_ADD.type)) {		// Registered by myself
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_CHANGE.acl;
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_OWN.type)) {			// Owned
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_CHANGE.acl;
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_RECENT_CHANGE.type)) {	// Recently Updated 
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_COMING_EXPIRE.type)) {	// Coming to expiry
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_EXPIRE.type)) {			// Expired
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_BIN.type)) {			// Delete
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_CHANGE.acl;
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD.type)) {		// Discard
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_CHANGE.acl;
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD_ADMIN.type)) {		// Discard - Manager
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_EXPIRE_ADMIN.type)) {		// Expired - Manager
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type)) {		// Requested
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_READ.acl;
			if(pObjw.getZappAdditoryBundle() == null) {
				pObjw.setZappAdditoryBundle(new ZappAdditoryBundle());
			}
			pObjw.getZappBundle().setState(getRequestStates());
			
			/* File */
			if(pObjw.getzArchMFile() == null) {
				pObjw.setzArchMFile(new ZArchMFile());
			}
			pObjw.getzArchMFile().setState(getRequestStates());
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_APPROVED.type)) {		// Approved
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_READ.acl;
			pObjw.getZappBundle().setState(getViewableStates());
			
			/* File */
			if(pObjw.getzArchMFile() == null) {
				pObjw.setzArchMFile(new ZArchMFile());
			}
			pObjw.getzArchMFile().setState(getViewableStates());
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_RETURNED.type)) {	// Returned
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_READ.acl;
			pObjw.getZappBundle().setState(getViewableStates());
			
			/* File */
			if(pObjw.getzArchMFile() == null) {
				pObjw.setzArchMFile(new ZArchMFile());
			}
			pObjw.getzArchMFile().setState(getViewableStates() + getReturnStates());
			
		}
		if(pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_OBJECT.type)) {	// To be approved
			REQ_ACL_YN = false; REQ_ACL = ZappConts.ACLS.CONTENT_READ.acl;
			pObjw.getZappBundle().setState(getAllRequestStates());
			
			/* File */
			if(pObjw.getzArchMFile() == null) {
				pObjw.setzArchMFile(new ZArchMFile());
			}
			pObjw.getzArchMFile().setState(getAllRequestStates());
			
		}
		pObjAuth.setObjHandleType(pObjw.getObjHandleType());
		
		/* [Result]
		 * 
		 */
		if(pObjw.getObjRes().equals(ZappConts.TYPES.RESULT_COUNT.type)) {			// Counting
			pObjRes = contentService.rNonPhysicalCount(pObjAuth, pObjf, pObjw, pObjRes);
		} else if(pObjw.getObjRes().equals(ZappConts.TYPES.RESULT_LIST.type)) {	// List
			pObjRes = contentService.rNonPhysicalList(pObjAuth, pObjf, pObjw, pObjRes);
			
			if(!pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD_ADMIN.type)
					&& !pObjw.getObjHandleType().equals(ZappConts.TYPES.LIST_EXPIRE_ADMIN.type)) {
				ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
				if(SYS_CONTENTACL_YN == null) {
					SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
				}
				if(SYS_CONTENTACL_YN.getSetval().equals(YES)) {
					if(REQ_ACL_YN == true) {
						pObjRes = aclService.optimizeObject(pObjAuth, (List<ZappContentRes>) pObjRes.getResObj(), pObjRes);
					} else {
						pObjRes = aclService.manualObject(pObjAuth, (List<ZappContentRes>) pObjRes.getResObj(), REQ_ACL, pObjRes);
					}
				} else {
					List<ZappContentRes> rTmpList = (List<ZappContentRes>) pObjRes.getResObj();
					if(rTmpList != null) {
						for(int IDX = ZERO; IDX < rTmpList.size(); IDX++) {
							if(rTmpList.get(IDX).getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
								rTmpList.get(IDX).setZappAcl(new ZappAclExtend(ZappConts.ACLS.CONTENT_CHANGE.acl));
							} else {
								rTmpList.get(IDX).setZappAcl(new ZappAclExtend(ZappConts.ACLS.CONTENT_READ.acl));
							}
						}
					}
					pObjRes.setResObj(rTmpList);
				}
			}
		}
		
		return pObjRes;

	}
	
	@SuppressWarnings("unchecked")
	public ZstFwResult selectFTRList(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		// ### Validation
		if(pObjw == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[selectFTRList] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
//		if(ZstFwValidatorUtils.valid(pObjw.getSword()) == false) {
//			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[selectFTRList] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* [Init]
		 * 
		 */
		if(pObjw.getZappBundle() == null) { pObjw.setZappBundle(new ZappBundle()); }
		pObjw.getZappBundle().setState(getViewableStates());	// State
		if(pObjw.getZappFile() != null) { 
			if(utilBinder.isEmptyPk(pObjw.getZappFile()) == true && utilBinder.isEmpty(pObjw.getZappFile()) == true) {
				pObjw.setZappFile(null);
			}
		}
		if(pObjw.getZappClassObject() == null) { pObjw.setZappClassObject(new ZappClassObject()); }
		
//		if(pObjw.getzArchMFile() == null) { pObjw.setzArchMFile(new ZArchMFile()); }
//		pObjw.getzArchMFile().setState(getViewableStates());	// State
		
		/* [FTR Searching]
		 * 
		 */
		Map<String, List<ZappContentRes>> rFTRList = new HashMap<String, List<ZappContentRes>>();
		if(ZstFwValidatorUtils.valid(pObjw.getSword()) == true) {
			try {
				pObjRes = ftrService.executeSearching(pObjAuth, pObjw, pObjRes);
			} catch (IOException e) {
				return ZappFinalizing.finalising("ERR_R_FTR", "[selectFTRList] " + messageService.getMessage("ERR_R_FTR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FTR", "[selectFTRList] " + messageService.getMessage("ERR_R_FTR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rFTRList = (Map<String, List<ZappContentRes>>) pObjRes.getResObj();
			if(rFTRList == null) {
				return pObjRes;
			}
			if(rFTRList.size() == ZERO) {
				return pObjRes;
			}
			
			/* [Handling Temporary Info.]
			 * 
			 */
			// ### Create Temporary Table
			pObjRes = contentService.cFTRTbl(pObjAuth, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FTR", "[selectFTRList] " + messageService.getMessage("ERR_R_FTR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// ### Insert Temporary Info.
			List<ZappContentRes> pContentids = new ArrayList<ZappContentRes>();
			for (Map.Entry<String, List<ZappContentRes>> entry : rFTRList.entrySet()) {
				String[] keys = entry.getKey().split(ZstFwConst.SCHARS.UNDERSCORE.character);
				if(keys != null) {
					ZappContentRes pZappContentRes = new ZappContentRes();
					pZappContentRes.setContentid(keys[ZERO]);
					pZappContentRes.setContenttype(keys[ONE]);
					pContentids.add(pZappContentRes);
				}
			}
			pObjRes = contentService.cFTRRows(pObjAuth, pContentids, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FTR", "[selectFTRList] " + messageService.getMessage("ERR_R_FTR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}		
		
		/* [Combined Searching]
		 * 
		 */
		// Bundle
		if(pObjw.getZappBundle() == null) {
			pObjw.setZappBundle(new ZappBundle());
		}
		pObjw.getZappBundle().setState(getViewableStates());
		// Sub-Bundle
		if(pObjw.getZappAdditoryBundle() != null) {
			if(utilBinder.isEmptyPk(pObjw.getZappAdditoryBundle()) == true && utilBinder.isEmpty(pObjw.getZappAdditoryBundle()) == true) {
				pObjw.setZappAdditoryBundle(null);
			}
		} 		
		
		// File
		if(pObjw.getZappFile() != null) {
			ZArchMFile pZArchMFile = new ZArchMFile();
			pZArchMFile.setState(getViewableStates());
			if(ZstFwValidatorUtils.valid(pObjw.getZappFile().getFilename()) == true) {		// File name 
				pZArchMFile.setFilename(pObjw.getZappFile().getFilename());
				pObjw.getZappFile().setFilename(null);
			} 
			if(ZstFwValidatorUtils.valid(pObjw.getZappFile().getCreatetime()) == true) {	// File name 
				String[] ctimes = pObjw.getZappFile().getCreatetime().split(DIVIDER);
				if(ctimes != null) {
					if(ctimes.length == TWO) {
						pZArchMFile.setCreatetime(pObjw.getZappFile().getCreatetime());
					}
				}
				pObjw.getZappFile().setCreatetime(null);
			}
			if(utilBinder.isEmptyPk(pObjw.getZappFile()) == true && utilBinder.isEmpty_File(pObjw.getZappFile()) == true) {
				pObjw.setZappFile(null);
			}
			pObjw.setzArchMFile(pZArchMFile);
		} else {
			ZArchMFile pZArchMFile = new ZArchMFile();
			pZArchMFile.setState(getViewableStates());
			if(pObjw.getzArchMFile().getCreatetime() != null) {
				pZArchMFile.setCreatetime(pObjw.getzArchMFile().getCreatetime());
			}
			pObjw.setzArchMFile(pZArchMFile);
		}
		
		// Keyword
		if(pObjw.getZappKeywords() != null) {
			if(pObjw.getZappKeywords().size() > ZERO) {
				StringBuffer kwords = new StringBuffer();
				for(ZappKeywordExtend vo : pObjw.getZappKeywords()) {
					kwords.append(vo.getKword() + DIVIDER);
				}
				pObjw.setZappKeyword(new ZappKeyword());
				pObjw.getZappKeyword().setKword(kwords.toString());
			}
		}
		
		// Classification
		ZappContentPar pObjf = null;
		if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_CLASS.type)) {
			
			pObjf = new ZappContentPar();
			
			if(pObjw.getZappClassification() != null) {
				if(utilClassBinder.isEmptyPk(pObjw.getZappClassification()) == true && utilClassBinder.isEmpty(pObjw.getZappClassification()) == true) {
					pObjw.setZappClassification(null);
				} 
			}
			
			//String CLASSID = pObjw.getZappClassification().getClassid();
			
			/* Whether to include sub-classification */
			if(ZstFwValidatorUtils.valid(pObjw.getObjIncLower())) {
				if(pObjw.getObjIncLower().equals(YES)) {
//					ZappClassificationPar pZappClassification = new ZappClassificationPar(pObjw.getZappClassification().getClassid());
					ZappClassificationPar pZappClassification = new ZappClassificationPar();
					pZappClassification.setUpid(pObjw.getZappClassification().getClassid());
					pZappClassification.setTypes(pObjw.getZappClassification().getTypes());
					pObjRes = classService.selectObjectDown(pObjAuth, null, pZappClassification, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_R_CLASS", "[selectFTRList] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					List<ZappClassificationRes> rZappClassDownList = (List<ZappClassificationRes>) pObjRes.getResObj();
					StringBuffer sbids = new StringBuffer();
					if(rZappClassDownList != null) {
						for(ZappClassificationRes vo : rZappClassDownList) {
							sbids.append(vo.getZappClassification().getClassid() + DIVIDER);
						}
						sbids.append(pObjw.getZappClassification().getClassid());
					} else {
						sbids.append(pObjw.getZappClassification().getClassid());
					}
					if(sbids.length() > ZERO) {
						if(pObjw.getZappClassObject() == null) {
							pObjw.setZappClassObject(new ZappClassObject());
						}
						pObjw.getZappClassObject().setClassid(sbids.toString());
					}
				} else {
					pObjw.getZappClassObject().setClassid(pObjw.getZappClassification().getClassid());
				}
				
				// Filter
				ZappClassObject ZappClassObject_Filter = new ZappClassObject();
				ZappClassObject_Filter.setClassid(Operators.IN.operator);
				pObjf.setZappClassObject(ZappClassObject_Filter);
			} else {
				pObjw.getZappClassObject().setClassid(pObjw.getZappClassification().getClassid());
			}
			
		}		

		List<ZappContentRes> rTmpList = null;
		if(pObjw.getObjRes().equals(ZappConts.TYPES.RESULT_COUNT.type)) {			// Counting
			pObjRes = contentService.rFTRCount(pObjAuth, pObjf, pObjw, pObjRes);
		} else if(pObjw.getObjRes().equals(ZappConts.TYPES.RESULT_LIST.type)) {	// List
			pObjRes = contentService.rFTRList(pObjAuth, pObjf, pObjw, pObjRes);
			// ACL
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
			}
			if(SYS_CONTENTACL_YN.getSetval().equals(YES)) {
				pObjRes = aclService.optimizeObject(pObjAuth, (List<ZappContentRes>) pObjRes.getResObj(), pObjRes);
			}
			
			// FTR Content
			rTmpList = (List<ZappContentRes>) pObjRes.getResObj();
			if(ZstFwValidatorUtils.valid(pObjw.getSword()) == true) {
				for(int IDX = ZERO; IDX < rTmpList.size() ; IDX++) {
					String key = rTmpList.get(IDX).getContentid() + ZstFwConst.SCHARS.UNDERSCORE.character + rTmpList.get(IDX).getContenttype();
					List<ZappContentRes> rFTRMap = (List<ZappContentRes>) rFTRList.get(key);
					for(ZappContentRes vo : rFTRMap) {
						rTmpList.get(IDX).setFiles(vo.getSummary());
					}
				}
			}
		}		
		
		// ### Drop Temporary Info.
		if(ZstFwValidatorUtils.valid(pObjw.getSword()) == true) {
			pObjRes = contentService.dFTRTbl(pObjAuth, pObjRes);
		}
		
		pObjRes.setResObj(rTmpList);
		
		return pObjRes;
	}
	
	/**
	 * Forcedly increase version
	 */
	public ZstFwResult replaceFile(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(!ZstFwValidatorUtils.valid(pObjContent.getObjTaskid())) {
			return ZappFinalizing.finalising("ERR_MIS_TASKID", messageService.getMessage("ERR_MIS_TASKID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(!ZstFwValidatorUtils.valid(pObjContent.getContentid())) {
			return ZappFinalizing.finalising("ERR_MIS_CONTENTID", messageService.getMessage("ERR_MIS_CONTENTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getObjFileName())) {
			return ZappFinalizing.finalising("ERR_MIS_WFILENAME", messageService.getMessage("ERR_MIS_WFILENAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getObjFileExt())) {
			return ZappFinalizing.finalising("ERR_MIS_FILEEXTN", messageService.getMessage("ERR_MIS_FILEEXTN",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getFilename())) {
			return ZappFinalizing.finalising("ERR_MIS_FILENAME", messageService.getMessage("ERR_MIS_FILENAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		

		pObjContent.getZappFile().setObjTaskid(pObjContent.getObjTaskid());			// Task ID
		pObjContent.getZappFile().setMfileid(pObjContent.getContentid());			// File ID
		pObjContent.getZappFile().setIsreleased(true);								// Upper version Up
		
		pObjRes = addFile(pObjAuth, pObjContent.getZappFile(), pObjRes);
		if(ZappFinalizing.isSuccess_Archive(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_FILE", "[replaceFile] " + messageService.getMessage("ERR_C_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	/**
	 * Check the same filename in a folder
	 */
	public ZstFwResult existFilename(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		// ## Initialization
		if(pObjContent.getZappFile() == null) {pObjContent.setZappFile(new ZappFile());}
		if(pObjContent.getZappClassObjects() == null) {pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>());}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);	
		ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);						// Whether content access control info. are applied

		//
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			// Check file name
			Object[] EXIST_FILENAME = new Object[TWO];
			pObjRes = checkFile(pObjAuth, pObjContent, pObjRes);
			EXIST_FILENAME = (Object[]) pObjRes.getResObj();
			if(EXIST_FILENAME == null) {
				pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_ONLY_ADD.state);	// New
				return pObjRes;				
			}
				
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.ADD) {					// When the same file does not exist in the folder
				pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_ONLY_ADD.state);	// New
				return pObjRes;
			}
			
			// Inquiry file info.
			ZArchMFile rZArchMFile = null;
			pObjContent.setContentid((String) EXIST_FILENAME[ONE]);
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.IGNORE || EXIST_FILENAME[ZERO] == ZappConts.ACTION.VERSION_UP) {
				try {
					rZArchMFile = zarchMfileService.rSingleRow_Vo(new ZArchMFile(pObjContent.getContentid()));
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
				}
			}
			if(rZArchMFile == null) { 											// (When the same name file does not exist in the folder)
				pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_ONLY_ADD.state);	// New
				return pObjRes;					
			}
			if(!rZArchMFile.getState().equals(ZappConts.STATES.BUNDLE_NORMAL.state)) {
				pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_ONLY_RENAME.state);	// Rename
				return pObjRes;					
			}
			
			boolean SKIP_CONTENTACL = false;
			pObjRes = selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			ZappFile rZappFile = (ZappFile) pObjRes.getResObj(); 
			if(rZappFile == null) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[changeContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rZappFile.getHolderid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				SKIP_CONTENTACL = true;
			}
			
			logger.info(">>> CONTENTID : " + ZstFwValidatorUtils.fixNullString(pObjContent.getContentid()));
			
			/* [Check content access control info.]
			 *
			 */
			if(SYS_CONTENTACL_YN.getSetval().equals(ZappConts.USAGES.YES.use) && SKIP_CONTENTACL == false) {
				ZappContentAcl pZappContentAcl = new ZappContentAcl();
				pZappContentAcl.setContentid(pObjContent.getContentid() + DIVIDER + pObjContent.getZappClassObjects().get(ZERO).getClassid());		// Classification ID + Content ID
				pZappContentAcl.setAcls(ZappConts.ACLS.CONTENT_CHANGE.acl);
				pObjRes = aclService.checkObject(pObjAuth, pZappContentAcl, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_ONLY_RENAME.state);	// Rename
					return pObjRes;		
				}
				boolean CANDO = (Boolean) pObjRes.getResObj();
				if(CANDO == false) {
					pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_ONLY_RENAME.state);	// Rename
					return pObjRes;		
				}			
			}
			
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.IGNORE) {								// Version-Up ( When the same file name exists in the folder and hash value is same )
				pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_RENAME_VERSIONUP_WOFILE.state);	// Forcedly version-up but keep file
				return pObjRes;
			}
			
			if(EXIST_FILENAME[ZERO] == ZappConts.ACTION.VERSION_UP) {  					// Version-Up ( When the same file name exists in the folder but hash value is different )
				pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_RENAME_VERSIONUP.state);	// Forcedly version-up
				return pObjRes;				
			}
			
		} else {
			pObjRes.setResObj(ZappConts.STATES.CHECK_FILE_ONLY_ADD.state);	// New
		}
		
		return pObjRes;
	}
	
	// ### Generating content no.
	public String getContentNo(ZappAuth pObjAuth) throws ZappException {
		
		// Preference 
		ZappEnv SYS_DOC_CONTENTNO_RULE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DOC_CONTENTNO_RULE.env); // Rules for generating content no.	
		if(SYS_DOC_CONTENTNO_RULE == null) {
			SYS_DOC_CONTENTNO_RULE = new ZappEnv(); SYS_DOC_CONTENTNO_RULE.setSetval("1");	// Company
		}
		
		/* Generating Content No.
		 * 
		 */
		StringBuffer cno = new StringBuffer();
		
		// Code
		switch(Integer.parseInt(SYS_DOC_CONTENTNO_RULE.getSetval())) {
			case 1:	// Company
				cno.append(pObjAuth.getSessCompany().getCode() + "-");
			break;
			case 2:	// Department
				cno.append(pObjAuth.getSessDeptUser().getZappDept().getCode() + "-");
			break;
			case 3:	// Folder
			break;
			default: break;
		}

		// Year
		cno.append(ZstFwDateUtils.getYear() + "-");	

		// Serial No.
		switch(Integer.parseInt(SYS_DOC_CONTENTNO_RULE.getSetval())) {
			case 1:	// Company
				cno.append(String.format("%08d", commonService.selectSeq(pObjAuth, "C", pObjAuth.getSessCompany().getCode())));
			break;
			case 2:	// Department
				cno.append(String.format("%08d", commonService.selectSeq(pObjAuth, "D", pObjAuth.getSessDeptUser().getZappDept().getCode())));
			break;
			case 3:	// Folder
//				cno.append(commonService.selectSeq(pObjAuth, "F", ""));
			break;
			default: break;
		}
		
		return cno.toString();
	}
	
	// ### Check file ###
	@SuppressWarnings("unchecked")
	private ZstFwResult checkFile(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		List<ZappFile> existZappFileList = null;
		Object[] resObj = new Object[TWO];
		
		/* [Classification]
		 * 
		 */
		String CLASSID = BLANK;
		if(pObjContent.getZappClassObjects() != null) {
			for(int IDX = ZERO; IDX < pObjContent.getZappClassObjects().size(); IDX++ ) {
				if(isNodeType(pObjContent.getZappClassObjects().get(IDX).getClasstype()) == true) {
					CLASSID = pObjContent.getZappClassObjects().get(IDX).getClassid();
				}
			}
		}

		/* [Classification]
		 * If no classification info. is entered, the classification info. of the file is searched.
		 */
		if(!ZstFwValidatorUtils.valid(CLASSID)) {
			
			ZappClassObject pZappClassObject = new ZappClassObject();
			pZappClassObject.setCobjid(pObjContent.getContentid());		// File ID
			pZappClassObject.setCobjtype(pObjContent.getObjType());		// File type
			pZappClassObject.setClasstype(getNodeTypes());				// Classification type
			pObjRes = selectObject(pObjAuth, pZappClassObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASS", "[checkFile] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			List<ZappClassObject> rZappClassObjectList = (List<ZappClassObject>) pObjRes.getResObj();
			if(rZappClassObjectList == null) {
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_R_CLASS", "[checkFile] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
			}
			for(ZappClassObject vo : rZappClassObjectList) {
				CLASSID = vo.getClassid();
			}
			
		}

		/* [Check file name ]
		 * 
		 */
		ZArchMFile pZArchMFile = new ZArchMFile();
		pZArchMFile.setFilename(pObjContent.getZappFile().getFilename());	// File name
		pObjRes = contentService.rMultiRowsFileName(pObjAuth, pZArchMFile, new ZappClassification(CLASSID), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_FILE", "[checkFile] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		existZappFileList = (List<ZappFile>) pObjRes.getResObj();
		if(existZappFileList == null) {			// 
			resObj[ZERO] = ZappConts.ACTION.ADD;
			pObjRes.setResObj(resObj);
			return pObjRes;
		}
		if(existZappFileList.size() == ZERO) {	// 
			resObj[ZERO] = ZappConts.ACTION.ADD;
			pObjRes.setResObj(resObj);
			return pObjRes;
		}
		
		/* [Compare file hash values]
		 * 
		 */
		for(ZappFile vo : existZappFileList) {
			String HASHID = ZstFwEncodeUtils.encodeFile_SHA256(pObjContent.getZappFile().getObjFileName());
			if(ZstFwValidatorUtils.valid(vo.getMaxhashid()) && ZstFwValidatorUtils.valid(HASHID)) { 
				if(vo.getMaxhashid().equals(HASHID)) {
					resObj[ZERO] = ZappConts.ACTION.IGNORE;
					resObj[ONE] = vo.getMfileid();
					pObjRes.setResObj(resObj);	
					return pObjRes;
				}
				resObj[ONE] = vo.getMfileid();
			}
		}
		
		resObj[ZERO] = ZappConts.ACTION.VERSION_UP;
		pObjRes.setResObj(resObj);
		
		return pObjRes;
	}
	
	// ### Inquire file ###
	public ZstFwResult getFile(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) {
		
		/* Variable */
		ZArchResult zArchResult = new ZArchResult();
		ZArchMFile pZArchMFile = new ZArchMFile();
		BeanUtils.copyProperties(pObjContent, pZArchMFile);
		pZArchMFile.setIsDebugged(true);
		
		try {
			zArchResult = zarchMfileMgtService.loadMFile(pZArchMFile);
		} catch (Exception e) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
		}			
		if(ZappFinalizing.isSuccess(zArchResult) == false) {
			return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
		}
		ZArchMFileRes rZArchMFile = (ZArchMFileRes) zArchResult.getResult();
		if(rZArchMFile == null) {
			return ZappFinalizing.finalising_Archive("ERR_NEXIST_FILE", "[getFile] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes.setResCode((String) zArchResult.getCode());
		pObjRes.setResMessage((String) zArchResult.getMessage());
		pObjRes.setResObj(rZArchMFile);
		
		return pObjRes;
	}
	private ZstFwResult getFiles(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) {
		
		/* Variable */
		ZArchResult zArchResult = new ZArchResult();
		ZArchMFile pZArchMFile = new ZArchMFile();
		BeanUtils.copyProperties(pObjContent, pZArchMFile);
		pZArchMFile.setIsDebugged(true);
		
		try {
			zArchResult = zarchMfileMgtService.listMFileAll(pZArchMFile, null, null);
		} catch (Exception e) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
		}			
		if(ZappFinalizing.isSuccess(zArchResult) == false) {
			return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
		}
		List<ZArchMFileRes> rZArchMFile = (List<ZArchMFileRes>) zArchResult.getResult();
		if(rZArchMFile == null) {
			return ZappFinalizing.finalising_Archive("ERR_NEXIST_FILE", "[getFiles] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes.setResCode((String) zArchResult.getCode());
		pObjRes.setResMessage((String) zArchResult.getMessage());
		pObjRes.setResObj(rZArchMFile);
		
		return pObjRes;
	}	
	private ZstFwResult getMaxVersionFiles(ZappAuth pObjAuth, String pObjType, ZappFile pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZArchMFile pZArchMFile = new ZArchMFile();
		ZArchVersion pZArchVersion = null;
		List<String> objMfileList = new ArrayList<String>();
		
		if(pObjType.equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			pZArchMFile.setLinkid(pObjContent.getLinkid());
			
			try {
				List<ZArchMFile> tmpList = zarchMfileService.rMultiRows_List(pZArchMFile);
				if(tmpList != null) {
					for(ZArchMFile vo : tmpList) {
						objMfileList.add(vo.getMfileid());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else if(pObjType.equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			pZArchMFile.setMfileid(pObjContent.getMfileid());
			try {
				ZArchMFile tmpList = zarchMfileService.rSingleRow_Vo(pZArchMFile);
				if(tmpList != null) {
					objMfileList.add(tmpList.getMfileid());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		pObjAuth.setObjList(objMfileList);
		
		pObjRes = contentService.rMultiRowsMaxVersionFile(pObjAuth, pZArchMFile, pZArchVersion, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising_Archive("ERR_R_FILE", "[getMaxVersionFiles] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		return pObjRes;
	}	
	private ZstFwResult getFilesAll(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) {
		
		/* Variable */
		ZArchResult zArchResult = new ZArchResult();
		ZArchMFile pZArchMFile = new ZArchMFile();
		BeanUtils.copyProperties(pObjContent, pZArchMFile);
		pZArchMFile.setIsDebugged(true);
		
		try {
			zArchResult = zarchMfileMgtService.listMFileAll(pZArchMFile
														  , null
														  , ZstFwValidatorUtils.valid(pObjContent.getVersionid()) ? new ZArchVersion(pObjContent.getVersionid()) : null);
		} catch (Exception e) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
		}			
		if(ZappFinalizing.isSuccess(zArchResult) == false) {
			return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
		}
		List<ZArchMFileRes> rZArchMFile = (List<ZArchMFileRes>) zArchResult.getResult();
		if(rZArchMFile == null) {
			return ZappFinalizing.finalising_Archive("ERR_NEXIST_FILE", "[getFilesAll] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes.setResCode((String) zArchResult.getCode());
		pObjRes.setResMessage((String) zArchResult.getMessage());
		pObjRes.setResObj(rZArchMFile);
		
		return pObjRes;
	}		
	
	// ### Add file ###
	private ZstFwResult addFile(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) {

		ZArchResult zArchResult = new ZArchResult();
		ZArchMFile pZArchMFile = new ZArchMFile();
		BeanUtils.copyProperties(pObjContent, pZArchMFile);
		pZArchMFile.setObjTaskid(pObjContent.getObjTaskid()); 					// Task ID
//		pZArchMFile.setIsDebugged(pObjAuth.getObjDebugged());					// Debug or not
		pZArchMFile.setCreator(pObjAuth.getSessDeptUser().getDeptuserid());		// Creator ID
		pZArchMFile.setIsDebugged(true);										// Debug or not
		pZArchMFile.setState(ZstFwValidatorUtils.valid(pObjContent.getState()) ? pObjContent.getState() : ZappConts.STATES.BUNDLE_NORMAL.state);				
		pZArchMFile.setUpdatetime(ZstFwValidatorUtils.valid(pObjContent.getUpdatetime()) ? pObjContent.getUpdatetime() : ZstFwDateUtils.getNow());				
		
		// Preferences
		ZappEnv SYS_VERSION_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_YN.env);										// [VERSION] Apply version or not
		ZappEnv SYS_VERSION_UPONLYHIGH_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_UPONLYHIGH_YN.env);					// [VERSION] Whether to increase the higher version
		ZappEnv SYS_VERSION_UPWITHNOSAMEHASH_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.VERSION_UPWITHNOSAMEHASH_YN.env);		// [VERSION] // Whether to increase the version when the file hash values ​​are the same
		ZappEnv SYS_CHECKFORMAT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CHECKFORMAT_YN.env);								// [FILE] Check file format?
		ZappEnv SYS_ENCRYPTION_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.ENCRYPTION_YN.env);									// [FILE] Apply encryption or not
		if(SYS_VERSION_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			// Whether to increase the version when the file hash values ​​are the same
			pZArchMFile.setIsVersionedUpwithNoSameHash((SYS_VERSION_UPWITHNOSAMEHASH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
			// Whether to increase the higher version
			pZArchMFile.setIsHighVer((SYS_VERSION_UPONLYHIGH_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);		
			pZArchMFile.setIsHighVer(pObjContent.getIsreleased() == true ? true : false);													// ### Change settings according to user
		}
		// Check file type
		if(SYS_CHECKFORMAT_YN != null) {
			pZArchMFile.setCheckFormat((SYS_CHECKFORMAT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);
		} else {
			pZArchMFile.setCheckFormat(false);
		}
		// Encryption
		if(SYS_ENCRYPTION_YN != null) {
			pZArchMFile.setIsEncrypted((SYS_ENCRYPTION_YN.getSetval().equals(ZappConts.USAGES.YES.use)) ? true : false);
		} else {
			pZArchMFile.setIsEncrypted(false);
		}
		// Delete temp. file
		if(ZstFwValidatorUtils.valid(pObjContent.getObjCaller())) {
			if(pObjContent.getObjCaller().equals(ZappConts.ACTION.RELOCATE.name())) {
				pZArchMFile.setIsDelTmpFile(YES);
			}
		}
		
		logger.info("addFile ============================================");
		logger.info("[E] pObjContent.getIsreleased() - " + pObjContent.getIsreleased());
		logger.info("[E] ObjTaskid - " + pZArchMFile .getObjTaskid());
		logger.info("[E] ObjFileName - " + pZArchMFile .getObjFileName());
		logger.info("[E] ObjFileExt - " + pZArchMFile .getObjFileExt());
		logger.info("[O] mfileid - " + pZArchMFile .getMfileid());
		logger.info("[E] Filename - " + pZArchMFile .getFilename());
		logger.info("[E] Linkid - " + pZArchMFile .getLinkid());
		logger.info("[E] Creator - " + pZArchMFile .getCreator());
		logger.info("[O] Createtime - " + pZArchMFile .getCreatetime());
		logger.info("[O] Updatetime - " + pZArchMFile .getUpdatetime());
		logger.info("[O] State - " + pZArchMFile .getState());
		logger.info("[E] CheckFormat - " + pZArchMFile .getCheckFormat());
		logger.info("[E] IsRemote - " + pZArchMFile .getIsRemote());
		logger.info("[E] IsHighVer - " + pZArchMFile .getIsHighVer());
		logger.info("[E] IsVersionedUpwithNoSameHash - " + pZArchMFile .getIsVersionedUpwithNoSameHash());
		logger.info("[E] IsEncrypted - " + pZArchMFile .getIsEncrypted());
		logger.info("[E] IsDelTmpFile - " + pZArchMFile .getIsDelTmpFile());	
		
		try {
			zArchResult = zarchMfileMgtService.saveMFile_v1(pZArchMFile);
		} catch (Exception e) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_CREATE_MFILE.result, e.getMessage(), pObjAuth.getObjlang());
		}			
		if(ZappFinalizing.isSuccess(zArchResult) == false) {
			return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
		}
		
		pObjRes.setResCode((String) zArchResult.getCode());
		pObjRes.setResMessage((String) zArchResult.getMessage());
		pObjRes.setResObj((String) zArchResult.getResFileid());					// Master File ID
		pObjRes.setPath((String) zArchResult.getResVersionid()); 				// Version ID
		
		return pObjRes;
	}
	// ### Add temp. file ###
	private ZstFwResult addTmpFile(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) {

		StringBuffer TMPFILE = new StringBuffer();
		TMPFILE.append(ZstFwFileUtils.addSeperator(TEMP_PATH));							// Temp. Mounting Path (in configuration xml file)
		TMPFILE.append(ZstFwFileUtils.addSeperator(pObjContent.getObjWorkplaceid()));	// Temp. ID	
		TMPFILE.append(pObjContent.getFilename());
		
		if(ZstFwValidatorUtils.valid(pObjContent.getObjFileName()) == true) {
			if(ZstFwFileUtils.existFile(pObjContent.getObjFileName()) == true) {
				try {
					ZstFwFileUtils.moveFile(pObjContent.getObjFileName(), TMPFILE.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				return ZappFinalizing.finalising("ERR_MIS_FILE", "[addTmpFile] " + messageService.getMessage("ERR_MIS_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		} else {
			return ZappFinalizing.finalising("ERR_MIS_FILE", "[addTmpFile] " + messageService.getMessage("ERR_MIS_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjContent.setObjFileName(TMPFILE.toString());
		pObjRes.setResObj(pObjContent);
		
		return pObjRes;
	}
	
	private ZstFwResult addFileExtension(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {

		// File No. 
		// Retention period ID
		pObjContent.setHolderid(pObjAuth.getSessDeptUser().getDeptuserid());
		pObjContent.setCreatorname(pObjAuth.getSessDeptUser().getZappUser().getName());
		// Expiretime
		if(ZstFwValidatorUtils.valid(pObjContent.getRetentionid()) == true) {
			pObjRes = systemService.selectObject(pObjAuth, new ZappCode(pObjContent.getRetentionid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes)) {
				ZappCode rZappCode = (ZappCode) pObjRes.getResObj();
				if(rZappCode == null) {
					return ZappFinalizing.finalising("ERR_NEXIST_EXPERIOD", "[addFileExtension] " + messageService.getMessage("ERR_NEXIST_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				try {
					pObjContent.setExpiretime(ZstFwDateUtils.addYears(Integer.parseInt(rZappCode.getCodevalue())));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		pObjRes = contentService.cSingleRow(pObjAuth, pObjContent, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_FILEEXTN", "[addFileExtension] " + messageService.getMessage("ERR_C_FILEEXTN",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes.setResObj(true);
		
		return pObjRes;
	}	
	
//	private ZstFwResult addTmpObject(ZappAuth pObjAuth, ZappTmpObject pObjContent, ZstFwResult pObjRes) {
//
//		pObjContent.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
//		pObjRes = contentService.uSingleRow(pObjAuth, pObjContent, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_C_LOCK", "[addLockedObject] " + messageService.getMessage("ERR_C_LOCK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		
//		pObjRes.setResObj(true);
//		
//		return pObjRes;
//	}
	
	private ZstFwResult addLockedObject(ZappAuth pObjAuth, ZappLockedObject pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjContent.setLockerid(pObjAuth.getSessDeptUser().getDeptuserid());
		pObjContent.setLockobjid(ZappKey.getPk(pObjContent));
		pObjRes = contentService.uSingleRow(pObjAuth, pObjContent, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_LOCKOBJ", "[addLockedObject] " + messageService.getMessage("ERR_C_LOCKOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes.setResObj(true);
		
		return pObjRes;
	}	
	
	// ### Edit ###
	/**
	 * Change by Mater File ID
	 * @param pObjAuth
	 * @param pObjContent
	 * @param pObjRes
	 * @return
	 */
	private ZstFwResult changeFile(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) {
		
		ZArchResult zArchResult = new ZArchResult();
		ZArchMFile pZArchMFile = new ZArchMFile();
		BeanUtils.copyProperties(pObjContent, pZArchMFile);
		pZArchMFile.setMfileid(pObjContent.getMfileid());
		
		try {
//			zArchResult = zarchMfileMgtService.updateMFile(pZArchMFile);
			zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
		} catch (Exception e) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
		}			
		if(ZappFinalizing.isSuccess(zArchResult) == false) {
			return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
		}
		
		pObjRes.setResCode((String) zArchResult.getCode());
		pObjRes.setResMessage((String) zArchResult.getMessage());
		pObjRes.setResObj(true);
		
		return pObjRes;
	}	
	/**
	 * Master Change by others except for Mater File ID
	 * @param pObjAuth
	 * @param pObjContent
	 * @param pObjRes
	 * @return
	 */
	private ZstFwResult changeFiles(ZappAuth pObjAuth, ZappFile pObjSet, ZappFile pObjWhere, ZstFwResult pObjRes) {
		
		ZArchResult zArchResult = new ZArchResult();
		ZArchMFile pZArchMFile_Set = new ZArchMFile();
		ZArchMFile pZArchMFile_Where = new ZArchMFile();
		BeanUtils.copyProperties(pObjSet, pZArchMFile_Set);
		BeanUtils.copyProperties(pObjWhere, pZArchMFile_Where);
		
		try {
//			zArchResult = zarchMfileMgtService.updateMFile(pZArchMFile);
			zArchResult = zarchMfileService.uMultiRows(pZArchMFile_Set, null, pZArchMFile_Where);
		} catch (Exception e) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
		}			
		if(ZappFinalizing.isSuccess(zArchResult) == false) {
			return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
		}
		
		pObjRes.setResCode((String) zArchResult.getCode());
		pObjRes.setResMessage((String) zArchResult.getMessage());
		pObjRes.setResObj(true);
		
		return pObjRes;
	}
	
	// ### Delete ###
	private ZstFwResult deleteFile(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) {
		
		ZArchResult zArchResult = new ZArchResult();
		ZArchMFile pZArchMFile = new ZArchMFile();
		BeanUtils.copyProperties(pObjContent, pZArchMFile);
		pZArchMFile.setIsDebugged(true);
		
		// Get Master File Info.
		try {
			zArchResult = zarchMfileMgtService.listMFile(pZArchMFile);
		} catch (Exception e) {
			return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
		}			
		if(ZappFinalizing.isSuccess(zArchResult) == false) {
			return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
		}
		List<ZArchMFile> rZArchMFile = (List<ZArchMFile>) zArchResult.getResult();
		if(rZArchMFile == null) {
			return ZappFinalizing.finalising_Archive("ERR_NEXIST_FILE", "[deleteFile] " + messageService.getMessage("ERR_NEXIST_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// Delete files
		for(ZArchMFile vo : rZArchMFile) {
			pZArchMFile = new ZArchMFile();
			pZArchMFile.setObjTaskid(pObjContent.getObjTaskid());
			pZArchMFile.setMfileid(vo.getMfileid());
			try {
				zArchResult = zarchMfileMgtService.eraseMFile(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_DELETE_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
		}
		
		pObjRes.setResCode((String) zArchResult.getCode());
		pObjRes.setResMessage((String) zArchResult.getMessage());
		pObjRes.setResObj(true);
		
		return pObjRes;
	}
	
	private ZstFwResult deleteFileExtension(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjRes = contentService.dSingleRow(pObjAuth, pObjContent, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_FILEEXT", "[changeContent] " + messageService.getMessage("ERR_D_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes.setResObj(true);
		
		return pObjRes;
	}	

	/**
	 * Inquire file info. of major version.
	 * @param pObjContent
	 * @return
	 */
	private List<ZArchMFileRes> extractMajorVersions(List<ZArchMFileRes> pObjContent) {
		
		List<ZArchMFileRes> rList = new ArrayList<ZArchMFileRes>();
		Map<String, List<ZArchMFileRes>> mapGroup = new HashMap<String, List<ZArchMFileRes>>();
		
		
		if(pObjContent != null) {
			List<ZArchMFileRes> pList = null;
			for(ZArchMFileRes vo : pObjContent) {
				if(mapGroup.containsKey(vo.getMfileid())) {
					pList = (List<ZArchMFileRes>)  mapGroup.get(vo.getMfileid());
				} else {
					pList = new ArrayList<ZArchMFileRes>();
				}
				pList.add(vo);
				mapGroup.put(vo.getMfileid(), pList);
			}
			for (Map.Entry<String, List<ZArchMFileRes>> entry : mapGroup.entrySet()) {
				pList = (List<ZArchMFileRes>)  entry.getValue();
				int maxver = ZERO;
				for(ZArchMFileRes vo : pList) {
					if(vo.getzArchVersion().getLver().intValue() == ZERO) {
						maxver = Math.max(maxver, vo.getzArchVersion().getHver().intValue());
					}
				}
				for(ZArchMFileRes vo : pList) {
					if(vo.getzArchVersion().getLver().intValue() == ZERO) {
						if(vo.getzArchVersion().getHver().intValue() == maxver) {
							rList.add(vo);
							break;
						}
					}
				}
	        }
		}
		
		return rList;
	}
	
	/**
	 * Inquire file info. of major version.
	 * @param pObjContent
	 * @return
	 */
	private List<ZArchMFileRes> extractMajorVersion(List<ZArchMFileRes> pObjContent) {
		
		List<ZArchMFileRes> rList = new ArrayList<ZArchMFileRes>();
		int maxver = ZERO;
		
		if(pObjContent != null) {
			for(ZArchMFileRes vo : pObjContent) {
				if(vo.getzArchVersion().getLver().intValue() == ZERO) {
					maxver = Math.max(maxver, vo.getzArchVersion().getHver().intValue());
				}
			}
			for(ZArchMFileRes vo : pObjContent) {
				if(vo.getzArchVersion().getLver().intValue() == ZERO) {
					if(vo.getzArchVersion().getHver().intValue() == maxver) {
						rList.add(vo);
						break;
					}
				}
			}
		}
		
		return rList;
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
								  , ZappContentPar pObjContent
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
		if(pObjContent == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// Object type
		if(!ZstFwValidatorUtils.valid(pObjContent.getObjType())) {
			return ZappFinalizing.finalising("ERR_MIS_CONTENTTYPE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CONTENTTYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// Task ID
		switch(pAct) {
			case ADD: case DISCARD: case REPLICATE: case UNLOCK:
				if(!ZstFwValidatorUtils.valid(pObjContent.getObjTaskid())) {
					return ZappFinalizing.finalising("ERR_MIS_TASKID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_TASKID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:break;
		}		
		
		// View Type 
		switch(pAct) {
			case VIEW_PK: 
				if(!ZstFwValidatorUtils.valid(pObjContent.getObjViewtype())) {
					return ZappFinalizing.finalising("ERR_MIS_VIEWTYPE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_VIEWTYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:break;
		}		
		
		// Classification ID
		switch(pAct) {
			case ADD: 
				if(pObjContent.getZappClassObjects().size() == ZERO) {
					return ZappFinalizing.finalising("ERR_MIS_CLASS", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
//			case LOCK:		
//				if(pObjContent.getZappClassObjects().size() < ONE) {
//					return ZappFinalizing.finalising("ERR_MIS_CLASS", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
//			break;
			case REPLICATE: case RELOCATE: 	
				if(pObjContent.getZappClassObjects().size() < TWO) {
					return ZappFinalizing.finalising("ERR_MIS_CLASS", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:break;
		}

		// Content ID
		switch(pAct) {
			case CHANGE: case DISABLE: case DISCARD: 
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getBundleid())) {
						return ZappFinalizing.finalising("ERR_MIS_BUNDLEID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_BUNDLEID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getMfileid())) {
						return ZappFinalizing.finalising("ERR_MIS_FILEID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_FILEID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			break;
			default:break;
		}
		
		// Content ID
		switch(pAct) {
			case VIEW_PK: case CHANGE_PK: case DISABLE_PK: case ENABLE_PK: case DISCARD_PK: case RELOCATE: case REPLICATE: case LOCK: case UNLOCK:
				if(!ZstFwValidatorUtils.valid(pObjContent.getContentid())) {
						return ZappFinalizing.finalising("ERR_MIS_CONTENTID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CONTENTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
			break;
			default:break;
		}
		
		// Title
		switch(pAct) {
			case ADD:	
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getTitle())) {
						return ZappFinalizing.finalising("ERR_MIS_CONTENTTITLE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CONTENTTITLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			break;
			default:break;
		}
		
		// Content No.
		switch(pAct) {
			case ADD:	
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getBno())) {
						return ZappFinalizing.finalising("ERR_MIS_CONTENTNO", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_CONTENTNO",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			break;
			default:break;
		}
		
		// Expiration period
		switch(pAct) {
			case ADD:
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getObjRetention()) && !ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getExpiretime())) {
						return ZappFinalizing.finalising("ERR_MIS_EXPERIOD", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getObjRetention()) && !ZstFwValidatorUtils.valid(pObjContent.getZappFile().getExpiretime())) {
						return ZappFinalizing.finalising("ERR_MIS_EXPERIOD", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_EXPERIOD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			break;
			default:break;
		}		
		
		// Locked info.
		switch(pAct) {
			case LOCK:	
				if(!ZstFwValidatorUtils.valid(pObjContent.getZappLockedObject().getReleasetime())) {
					return ZappFinalizing.finalising("ERR_MIS_RTIME", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_RTIME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObjContent.getZappLockedObject().getReason())) {
					return ZappFinalizing.finalising("ERR_MIS_REASON", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_REASON",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:break;
		}
		
		// Files
		switch(pAct) {
			case LOCK:
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getMfileid())) {
						return ZappFinalizing.finalising("ERR_MIS_FILEID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_FILEID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getVersionid())) {
						return ZappFinalizing.finalising("ERR_MIS_VERSIONID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_VERSIONID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				} 
			break;
			case UNLOCK:	
				if(pObjContent.getHasfile() == true) {
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getObjFileName())) {
						return ZappFinalizing.finalising("ERR_MIS_WFILENAME", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_WFILENAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getObjFileExt())) {
						return ZappFinalizing.finalising("ERR_MIS_FILEEXT", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
					if(!ZstFwValidatorUtils.valid(pObjContent.getZappFile().getFilename())) {
						return ZappFinalizing.finalising("ERR_MIS_FILENAME", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_FILENAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			break;
			default:break;
		}		
		
		return pObjRes;
		
	}
	
	
	private ZstFwResult validMultiple(ZappAuth pObjAuth
								   , ZappContentPar pObjContent
								   , ZstFwResult pObjRes
								   , String pCaller
								   , ZappConts.ACTION pAct) {
		
		switch(pAct) {
			case ACL:
//				if(pObjContent.getZappAcls() == null) {
//					return ZappFinalizing.finalising("ERR_MIS_CONTENTACL", "[" + pCaller + "][validMultiple] " + messageService.getMessage("ERR_MIS_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
//				if(pObjContent.getZappAcls().size() == ZERO) {
//					return ZappFinalizing.finalising("ERR_MIS_CONTENTACL", "[" + pCaller + "][validMultiple] " + messageService.getMessage("ERR_MIS_CONTENTACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
			break;
//			case APPROVAL:	
//				if(pObjContent.getZappAprovPaths() == null && pObjContent.getZappApprover() == null) {
//					return ZappFinalizing.finalising("ERR_MIS_APPROVAL", "[validMultiple] " + messageService.getMessage("ERR_MIS_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
//				if(pObjContent.getZappAprovPaths().size() == ZERO && pObjContent.getZappApprover().size() == ZERO) {
//					return ZappFinalizing.finalising("ERR_MIS_APPROVAL", "[validMultiple] " + messageService.getMessage("ERR_MIS_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
//			break;
			default:
		}
		
		return pObjRes;
	}	

	/**
	 * Combination of states
	 * @param pTypes
	 * @param pViewable
	 * @return
	 */
	private String getStates(int pKind) {
		
		StringBuffer sbstate = new StringBuffer();
		String pTypes = "BUNDLE_"; boolean pViewable = false;
		
		switch(pKind) {
			case 0: pTypes="BUNDLE_"; pViewable = true; break;		// All viewable states
			case 1: pTypes="_REQUEST"; pViewable = false;  break;	// All request states
			case 2: pTypes="_RETURN"; pViewable = false;  break;	// All return states
			default: break;
		}
		
		for(ZappConts.STATES states : ZappConts.STATES.values()) {
			if(states.name().startsWith(pTypes)) {
				if(pViewable == true) {
					if(states.viewable.equals(ZappConts.USAGES.YES.use)) {
						sbstate.append(states.state + DIVIDER);
					}
				} else {
					sbstate.append(states.state + DIVIDER);
				}
			}
		}
		
		return sbstate.toString();

	}
	
	/**
	 * Combination of viewable states
	 * @return
	 */
	private String getViewableStates() {
		
		StringBuffer sbstate = new StringBuffer();
		
		for(ZappConts.STATES states : ZappConts.STATES.values()) {
			if(states.name().startsWith("BUNDLE_")) {
				if(states.viewable.equals(ZappConts.USAGES.YES.use)) {
					sbstate.append(states.state + DIVIDER);
				}
			}
		}
		
		return sbstate.toString();
		
	}
	
	/**
	 * Combination of request and return states 
	 * @return
	 */
	private String getRequestReturnStates() {
		
		StringBuffer sbstate = new StringBuffer();
		
		for(ZappConts.STATES states : ZappConts.STATES.values()) {
			if(states.name().contains("_REQUEST") || states.name().contains("_RETURN")) {
				sbstate.append(states.state + DIVIDER);
			}
		}
		
		return sbstate.toString();
		
	}
	
	/**
	 * Combination of request states 
	 * @return
	 */
	private String getRequestStates() {
		
		StringBuffer sbstate = new StringBuffer();
		
		for(ZappConts.STATES states : ZappConts.STATES.values()) {
			if(states.name().contains("_REQUEST")) {
				sbstate.append(states.state + DIVIDER);
			}
		}
		
		return sbstate.toString();
		
	}
	private String getReturnStates() {
		
		StringBuffer sbstate = new StringBuffer();
		
		for(ZappConts.STATES states : ZappConts.STATES.values()) {
			if(states.name().contains("_RETURN")) {
				sbstate.append(states.state + DIVIDER);
			}
		}
		
		return sbstate.toString();
		
	}	
	private String getAllRequestStates() {
		
		StringBuffer sbstate = new StringBuffer();
		
		for(ZappConts.STATES states : ZappConts.STATES.values()) {
			if(states.name().contains("_REQUEST")) {
				sbstate.append(states.state + Characters.DEFAULT.character);
			}
		}
		
		return sbstate.toString();
		
	}	
	
	/**
	 * Folder Type
	 * @return
	 */
	private String getNodeTypes() {
		
		StringBuffer types = new StringBuffer();
		
		types.append(ZappConts.TYPES.CLASS_FOLDER.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type); 
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type);
		
		return types.toString();
	}
	private boolean isNodeType(String checkType) {
		if(getNodeTypes().contains(checkType)) {
			return true;
		} else {
			return false;
		}
	}
	private String getNonNodeTypes() {
		
		StringBuffer types = new StringBuffer();
		
		types.append(ZappConts.TYPES.CLASS_CLASS.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_DOCTYPE.type); 
		
		return types.toString();
	}	
	private boolean isNoneNodeType(String checkType) {
		if(getNonNodeTypes().contains(checkType)) {
			return true;
		} else {
			return false;
		}
	}
	private boolean isAclFreeNodeType(String checkType) {
		if(checkType.equals(ZappConts.TYPES.CLASS_CLASS.type) 
				|| checkType.equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
				|| checkType.equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)) {
			return true;
		} else {
			return false;
		}
	}
	private String getSkipAclNodeTypes() {
		
		StringBuffer types = new StringBuffer();
		
		types.append(ZappConts.TYPES.CLASS_FOLDER.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type); 
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_CLASS.type);
		types.append(DIVIDER);
		types.append(ZappConts.TYPES.CLASS_DOCTYPE.type);
		
		return types.toString();
	}
	private boolean isSkipAclNodes(String checkType) {
		if(getSkipAclNodeTypes().contains(checkType)) {
			return true;
		} else {
			return false;
		}		
	}
	/**
	 * Validate access route
	 * @param pObjContent
	 * @return
	 */
	private boolean validAcsRoute(ZappContentPar pObjContent) {
		if(contentWorkflowoService.genCode(pObjContent).equals(pObjContent.getObjAcsRoute())) { 
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checking date format
	 * @param pdate
	 * @return
	 */
	private boolean isDateFormat(String pdate) {
		
		try {
		    SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyy-MM-dd"); //검증할 날짜 포맷 설정
		    dateFormatParser.setLenient(false); //false일경우 처리시 입력한 값이 잘못된 형식일 시 오류가 발생
		    dateFormatParser.parse(pdate); //대상 값 포맷에 적용되는지 확인
		    return true;
		} catch (Exception e) {
		    return false;
		}
		
	}	
	
}

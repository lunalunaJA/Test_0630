package com.zenithst.core.system.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.acl.api.ZappAclMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.service.ZappCommonService;
import com.zenithst.core.common.vo.ZappCommon;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.bind.ZappSystemBinder;
import com.zenithst.core.system.service.ZappSystemService;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage system info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappSystemMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappSystemMgtService")
public class ZappSystemMgtServiceImpl extends ZappService implements ZappSystemMgtService {

	/*
	* [Service]
	*/

	/* System */
	@Autowired
	private ZappSystemService systemService;

	/* Access control */
	@Autowired
	private ZappAclMgtService aclService;
	
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

	/* System */
	@Autowired
	private ZappSystemBinder utilBinder;
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappSystemMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappSystemMgtService] Service Destroy ");
	}	
	
	/*
	 *  [New] 
	 */
	
	
	public ZstFwResult addObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String LOG_TYPE = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[addObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Multiple registration] 
		 * 
		 */
		if(pObj instanceof List) {
			pObjRes = systemService.cMultiRows(pObjAuth, pObj, pObjRes);
		} 
		else {
			
			/* Validation */
			if(utilBinder.isEmpty(pObj) == true) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Code */
			if(pObj instanceof ZappCode) { LOG_TYPE = ZappConts.LOGS.TYPE_CODE.log; }
			
			/* Preferences */
			if(pObj instanceof ZappEnv) { LOG_TYPE = ZappConts.LOGS.TYPE_ENV.log; }
			
			pObjRes = systemService.cSingleRow(pObjAuth, pObj, pObjRes);
			
			/*  [Logging]
			 * 
			 */
			if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, (String) pObjRes.getResObj());
				pObjRes = leaveLog(pObjAuth
						         , LOG_TYPE
								 , ZappConts.LOGS.ACTION_ADD.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);

			}
		}
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;

	}
	
	/*
	 *  [Edit] 
	 */
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String LOG_TYPE = BLANK, PK = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObj) || utilBinder.isEmpty(pObj)) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log		
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[addObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Code */
		if(pObj instanceof ZappCode) { 
			ZappCode pvo = (ZappCode) pObj;
			LOG_TYPE = ZappConts.LOGS.TYPE_CODE.log; 
			PK = pvo.getCodeid();
		}
		
		/* Preferences */
		if(pObj instanceof ZappEnv) { 
			ZappEnv pvo = (ZappEnv) pObj;
			LOG_TYPE = ZappConts.LOGS.TYPE_ENV.log; 
			PK = pvo.getEnvid();
		}
		
		pObjRes = systemService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use) && !pObjAuth.getObjType().equals(NO)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObj);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, PK);
			pObjRes = leaveLog(pObjAuth
					         , LOG_TYPE
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String LOG_TYPE = BLANK, PK = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[addObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Code */
		if(pObjw instanceof ZappCode) { 
			ZappCode pvo = (ZappCode) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_CODE.log; 
			PK = pvo.getCodeid();
		}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) { 
			ZappEnv pvo = (ZappEnv) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_ENV.log; 
			PK = pvo.getEnvid();
		}
		
		pObjRes = systemService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjs);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, PK);
			pObjRes = leaveLog(pObjAuth
					         , LOG_TYPE
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
	}
	

	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String LOG_TYPE = BLANK, PK = BLANK;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[changeObject] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[addObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Code */
		if(pObjw instanceof ZappCode) { 
			ZappCode pvo = (ZappCode) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_CODE.log; 
			PK = pvo.getCodeid();
		}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) { 
			ZappEnv pvo = (ZappEnv) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_ENV.log; 
			PK = pvo.getEnvid();
		}
		
		pObjRes = systemService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjs);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, PK);
			pObjRes = leaveLog(pObjAuth
					         , LOG_TYPE
							 , ZappConts.LOGS.ACTION_CHANGE.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);		
		
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
		
		/* Code */
		if(pObj instanceof ZappCode) {}
		
		/* Preferences */
		if(pObj instanceof ZappEnv) {}
		
		pObjRes = systemService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}		
		
	
	/**
	 * [Delete]
	 */

	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String LOG_TYPE = BLANK, PK = BLANK;
		Object logbj = null;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[addObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Code */
		if(pObjw instanceof ZappCode) { 
			ZappCode pvo = (ZappCode) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_CODE.log; PK = pvo.getCodeid();
		}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) { 
			ZappEnv pvo = (ZappEnv) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_ENV.log; PK = pvo.getEnvid();
		}
		logbj = systemService.rSingleRow(pObjAuth, pObjw);
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = systemService.dSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = systemService.dMultiRows(pObjAuth, pObjw, pObjRes);
		}
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, logbj);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, PK);
			pObjRes = leaveLog(pObjAuth
					         , LOG_TYPE
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);	
		
		return pObjRes;
		
	}
	
	public ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		String LOG_TYPE = BLANK, PK = BLANK;
		List<Object> logbj = null;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[deleteObject] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_SYSTEM_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_SYSTEM_YN.env);					// [LOG] Whether to leave system log
		
		/* [Check access control info.]
		 * 
		 */
		if(aclService.isCompanyManager(pObjAuth) == false) {
			return ZappFinalizing.finalising("ERR_NO_ACL", "[addObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Code */
		if(pObjw instanceof ZappCode) { 
			ZappCode pvo = (ZappCode) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_CODE.log; PK = pvo.getCodeid();
		}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) { 
			ZappEnv pvo = (ZappEnv) pObjw;
			LOG_TYPE = ZappConts.LOGS.TYPE_ENV.log; PK = pvo.getEnvid();
		}
		logbj = systemService.rMultiRows(pObjAuth, pObjw);
		
		pObjRes = systemService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		/*  [Logging]
		 * 
		 */
		if(SYS_LOG_SYSTEM_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, logbj);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, PK);
			pObjRes = leaveLog(pObjAuth
					         , LOG_TYPE
							 , ZappConts.LOGS.ACTION_DISCARD.log
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);

		}		
		
		pObjRes.setResObj(BLANK);
		
		return pObjRes;
		
	}	

	/**
	 *  [OUT]
	 */
	
	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		logger.info("utilBinder.isEmptyPk(pObjw) = " + utilBinder.isEmptyPk(pObjw));
		logger.info("utilBinder.isEmpty(pObjw) = " + utilBinder.isEmpty(pObjw));
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Code */
		if(pObjw instanceof ZappCode) {}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = systemService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = systemService.rMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Code */
		if(pObjw instanceof ZappCode) {}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {}
		
		pObjRes = systemService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Code */
		if(pObjw instanceof ZappCode) {}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {}
		
		pObjRes = systemService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Code */
		if(pObjw instanceof ZappCode) {}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {}
		
		pObjRes = systemService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Code */
		if(pObjw instanceof ZappCode) {}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {}
		
		pObjRes = systemService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Code */
		if(pObjw instanceof ZappCode) {}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {}
		
		pObjRes = systemService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}	

	/**
	 * Disable
	 */
	public ZstFwResult disableObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// Preferences
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* Code */
		if(pObjw instanceof ZappCode) {
						
			/* [Check access control info.]
			 * 
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[disableObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get info.
			ZappCode pvo = (ZappCode) pObjw;
			ZappCode rvo = (ZappCode) systemService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CODE", "[disableObject] " + messageService.getMessage("누락_조회_부서",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[disableObject] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			ZappCode pvo_update = new ZappCode(pvo.getCodeid());
			pvo_update.setIsactive(NO); pObjAuth.setObjType(NO);
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CODE", "[disableObject] " + messageService.getMessage("ERR_E_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getCodeid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CODE.log
								 , ZappConts.LOGS.ACTION_DISABLE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {
						
			/* [Check access control info.]
			 * 
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[disableObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get info.
			ZappEnv pvo = (ZappEnv) pObjw;
			ZappEnv rvo = (ZappEnv) systemService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_ENV", "[disableObject] " + messageService.getMessage("ERR_NEXIST_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[disableObject] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			ZappEnv pvo_update = new ZappEnv(pvo.getEnvid());
			pvo_update.setIsactive(NO); pObjAuth.setObjType(NO);
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_ENV", "[disableObject] " + messageService.getMessage("ERR_E_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getEnvid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_ENV.log
								 , ZappConts.LOGS.ACTION_DISABLE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}		
		
		return pObjRes;
		
	}	

	/**
	 * Enable
	 */
	public ZstFwResult enableObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// Preferences
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* Code */
		if(pObjw instanceof ZappCode) {
						
			/* [Check access control info.]
			 * 
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[enableObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get info.
			ZappCode pvo = (ZappCode) pObjw;
			ZappCode rvo = (ZappCode) systemService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CODE", "[enableObject] " + messageService.getMessage("누락_조회_부서",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getIsactive().equals(YES)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[enableObject] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			ZappCode pvo_update = new ZappCode(pvo.getCodeid());
			pvo_update.setIsactive(YES); pObjAuth.setObjType(NO);
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CODE", "[enableObject] " + messageService.getMessage("ERR_E_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getCodeid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CODE.log
								 , ZappConts.LOGS.ACTION_ENABLE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {
						
			/* [Check access control info.]
			 * 
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[enableObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get info.
			ZappEnv pvo = (ZappEnv) pObjw;
			ZappEnv rvo = (ZappEnv) systemService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_ENV", "[enableObject] " + messageService.getMessage("ERR_NEXIST_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getIsactive().equals(YES)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[enableObject] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			ZappEnv pvo_update = new ZappEnv(pvo.getEnvid());
			pvo_update.setIsactive(YES); pObjAuth.setObjType(NO);
			pObjRes = changeObject(pObjAuth, pvo_update, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_ENV", "[enableObject] " + messageService.getMessage("ERR_E_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getEnvid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_ENV.log
								 , ZappConts.LOGS.ACTION_ENABLE.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}		
		
		return pObjRes;
		
	}	

	/**
	 * Discard
	 */
	public ZstFwResult discardObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// Preferences
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Content Logging or not
		
		/* Code */
		if(pObjw instanceof ZappCode) {
						
			/* [Check access control info.]
			 * 
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[discardObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			
			/* [Use or not]
			 * 
			 */
			pObjRes = checkUsingInOtherObject(pObjAuth, "discardObject", pObjw, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			//Get info.
			ZappCode pvo = (ZappCode) pObjw;
			ZappCode rvo = (ZappCode) systemService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CODE", "[discardObject] " + messageService.getMessage("누락_조회_부서",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(!rvo.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[discardObject] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Delete
			ZappCode pvo_update = new ZappCode(pvo.getCodeid());
			pObjRes = deleteObject(pObjAuth, new ZappCode(pvo.getCodeid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CODE", "[discardObject] " + messageService.getMessage("ERR_D_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getCodeid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CODE.log
								 , ZappConts.LOGS.ACTION_DISCARD.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}
		
		/* Preferences */
		if(pObjw instanceof ZappEnv) {
						
			/* [Check access control info.]
			 * 
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[discardObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* [Use or not]
			 * 
			 */
			pObjRes = checkUsingInOtherObject(pObjAuth, "discardObject", pObjw, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			// Get info.
			ZappEnv pvo = (ZappEnv) pObjw;
			ZappEnv rvo = (ZappEnv) systemService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_ENV", "[discardObject] " + messageService.getMessage("ERR_NEXIST_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(!rvo.getIsactive().equals(NO)) {
				return ZappFinalizing.finalising("ERR_ACTIVE", "[discardObject] " + messageService.getMessage("ERR_ACTIVE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Delete
			pObjRes = deleteObject(pObjAuth, new ZappEnv(pvo.getEnvid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_ENV", "[discardObject] " + messageService.getMessage("ERR_D_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getEnvid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_ENV.log
								 , ZappConts.LOGS.ACTION_DISCARD.log
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
		
		/* Code */
		if(pObjw instanceof ZappCode) {
						
			/* [Check access control info.]
			 * 
			 */
			if(aclService.isCompanyManager(pObjAuth) == false) {
				return ZappFinalizing.finalising("ERR_NO_ACL", "[reorderObject] " + messageService.getMessage("ERR_NO_ACL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Get info.
			ZappCode pvo = (ZappCode) pObjw;
			ZappCode rvo = (ZappCode) systemService.rSingleRow(pObjAuth, pObjw);
			if(rvo == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CODE", "[reorderObject] " + messageService.getMessage("ERR_NEXIST_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rvo.getPriority() == pvo.getPriority()) {
				return ZappFinalizing.finalising("ERR_DUP_ORDER", "[reorderObject] " + messageService.getMessage("ERR_DUP_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Change order
			ZappCode pvos = new ZappCode();
			pvos.setCompanyid(rvo.getCompanyid());
			pvos.setUpid(rvo.getUpid());
			ZappCode pvoe = new ZappCode();
			if(rvo.getPriority() > pvo.getPriority()) {	// When the order moves up
				pvos.setPriority(pvo.getPriority());
				pvoe.setPriority(rvo.getPriority() - ONE);
				pObjRes = systemService.upwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(rvo.getPriority() < pvo.getPriority()) {	// When the order moves down
				pvos.setPriority(rvo.getPriority() + ONE);
				pvoe.setPriority(pvo.getPriority());
				pObjRes = systemService.downwardPriority(pObjAuth, pvos, pvoe, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_ORDER", "[reorderObject] " + messageService.getMessage("ERR_E_ORDER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Update
			pObjRes = changeObject(pObjAuth, new ZappCode(pvo.getCodeid(), pvo.getPriority()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_CODE", "[reorderObject] " + messageService.getMessage("ERR_E_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Log
			if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_BEFORE.log, rvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_AFTER.log, pvo);
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pvo.getCodeid());
				pObjRes = leaveLog(pObjAuth
						         , ZappConts.LOGS.TYPE_CODE.log
								 , ZappConts.LOGS.ACTION_REORDER.log
						         , LOGMAP
						         , PROCTIME
						         , pObjRes);
			}
			
			pObjRes.setResObj(BLANK);
		}
		
		return pObjRes;
		
	}	
	
	/**
	 * Initialization
	 */
	public ZstFwResult initObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObj instanceof ZappCompany) {
			
			// Code initialization
			pObjRes = systemService.initSystem(pObjAuth, new ZappCode(), pObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_CODE", "[initObject] " + messageService.getMessage("ERR_C_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			// Preferences initialization
			pObjRes = systemService.initSystem(pObjAuth, new ZappEnv(), pObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_ENV", "[initObject] " + messageService.getMessage("ERR_C_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}
		
		if(pObj instanceof ZappUser) {
			
			// Code initialization
//			pObjRes = systemService.initSystem(pObjAuth, new ZappCode(), pObj, pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_C_CODE", "[initObject] " + messageService.getMessage("ERR_C_CODE",  BLANK), BLANK);
//			}
			
			// Preferences initialization
			pObjRes = systemService.initSystem(pObjAuth, new ZappEnv(), pObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_ENV", "[initObject] " + messageService.getMessage("ERR_C_ENV",  BLANK), BLANK);
			}			
		}

		return pObjRes;
	}

	/* ********************************************************************************************* */

	
	// ### Use or not ###
	private ZstFwResult checkUsingInOtherObject(ZappAuth pObjAuth, String pCaller, Object pObj, ZstFwResult pObjRes) {
	
		if(pObj != null) {
			
			Map<String, Object> pMap = new HashMap<String, Object>();
			
			if(pObj instanceof ZappCode) {
				ZappCode pvo = (ZappCode) pObj;
				pMap.put("objType", "CODE");
				pMap.put("objid", pvo.getCodeid());
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
	
}

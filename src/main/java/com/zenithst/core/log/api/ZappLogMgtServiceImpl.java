package com.zenithst.core.log.api;

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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappJSONUtils;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.log.bind.ZappLogBinder;
import com.zenithst.core.log.service.ZappLogService;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.log.vo.ZappCycleLog;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;
import com.zenithst.framework.view.ZstFwViews;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage log info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappLogMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappLogMgtService")
public class ZappLogMgtServiceImpl extends ZappService implements ZappLogMgtService {

	/*
	* [Service]
	*/

	/* Log */
	@Autowired
	private ZappLogService logService;
	
	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;

	/*
	* [Binder]
	*/

	/* Log */
	@Autowired
	private ZappLogBinder utilBinder;
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappLogMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappLogMgtService] Service Destroy ");
	}		
	
	/*
	 *  [New] 
	 */
	
	
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
			pObjRes = logService.cMultiRows(pObjAuth, pObj, pObjRes);
		} 
		else {
			
			/* Validation */
			if(utilBinder.isEmpty(pObj) == true) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Access Log */
			if(pObj instanceof ZappAccessLog) {}
			
			/* Content Log */
			if(pObj instanceof ZappContentLog) {}
			
			/* System Log */
			if(pObj instanceof ZappSystemLog) {}
			
			pObjRes = logService.cSingleRow(pObjAuth, pObj, pObjRes);
		}
		
		pObjRes.setResObj(BLANK);
		
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
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Access Log */
		if(pObj instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObj instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObj instanceof ZappSystemLog) {}
		
		pObjRes = logService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
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
		
		/* Access Log */
		if(pObj instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObj instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObj instanceof ZappSystemLog) {}
		
		pObjRes = logService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
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
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = logService.dSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = logService.dMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = logService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = logService.rMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}
	
	/**
	 * 
	 * @param pObjAuth
	 * @param pObjf
	 * @param pObjw
	 * @param pObjRes
	 * @return
	 * @throws ZappException
	 * @throws SQLException
	 */
	public ZstFwResult selectObjectExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObjectExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.rMultiRowsExtend(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Access Log */
		if(pObjw instanceof ZappAccessLog) {}
		
		/* Content Log */
		if(pObjw instanceof ZappContentLog) {}
		
		/* System Log */
		if(pObjw instanceof ZappSystemLog) {}
		
		pObjRes = logService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}	

	
	/* *************************************************************************************************************************************** */
	
	public ZstFwResult getLogs(ZappAuth pObjAuth, Map<String, Object> pObjMap, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> mapLogs = new HashMap<String, Object>();
		
		/* Authentication info.
		 * 
		 */
		pObjAuth.setObjPasswd(BLANK);
		ZappAuth pZappAuth = new ZappAuth();
		BeanUtils.copyProperties(pObjAuth, pZappAuth);
		pZappAuth.setSessEnv(null);
		mapLogs.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
		
		/* Additional info.
		 * 
		 */
		if(pObjMap != null) {
			mapLogs.putAll(pObjMap);
		}
		
		/* JSON
		 * 
		 */
		pObjRes.setResObj(mapToJson(mapLogs));
		
		return pObjRes;
	}
	
	
	@SuppressWarnings("unchecked")
	public ZstFwResult leaveLog(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[leaveLog] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Multiple registration] 
		 * 
		 */
		if(pObj instanceof List) {
			boolean[] checkObj = {false, false, false, false};
			List<Object> checkObjList = (List<Object>) pObj;
			for(Object obj : checkObjList) {
				if(obj instanceof ZappAccessLog) {
					checkObj[0] = true; break;
				}
				if(obj instanceof ZappContentLog) {
					checkObj[1] = true; break;
				}
				if(obj instanceof ZappSystemLog) {
					checkObj[2] = true; break;
				}
				if(obj instanceof ZappCycleLog) {
					checkObj[3] = true; break;
				}
			}
			
			/* Access Log */
			if(checkObj[0] == true) {
				List<ZappAccessLog> pLogList = new ArrayList<ZappAccessLog>();
				for(Object obj : checkObjList) {
					ZappAccessLog pLog = (ZappAccessLog) obj;
					pLog.setCompanyid(pObjAuth.getObjCompanyid());
					pLog.setLoggerid(pObjAuth.getSessDeptUser().getDeptuserid());
					pLog.setLoggername(pObjAuth.getSessDeptUser().getZappUser().getName());
					pLog.setLoggerdeptid(pObjAuth.getSessDeptUser().getDeptid());
					pLog.setLoggerdeptname(pObjAuth.getSessDeptUser().getZappDept().getName());
					pLog.setLogtime(ZstFwDateUtils.getNow());
	//				pLog.setLogtype(BLANK); 	// External input
	//				pLog.setAction(BLANK); 		// External input
					if(pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log) != null) {
						ZappAuth pZappAuth = new ZappAuth();
//						BeanUtils.copyProperties((ZappAuth) pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log), pZappAuth);
						pZappAuth.setSessEnv(null);
						pLog.getMaplogs().put(ZappConts.LOGS.ITEM_LOGGER.log, pZappAuth);
					}
					pLog.setLogs(mapToJson(pLog.getMaplogs()));
					pLog.setLogid(ZappKey.getPk(pLog));
					pLogList.add(pLog);
				}
				pObjRes = logService.cMultiRows(pObjAuth, pLogList, pObjRes);
			}
			
			/* Content Log */
			if(checkObj[1] == true) {
				List<ZappContentLog> pLogList = new ArrayList<ZappContentLog>();
				for(Object obj : checkObjList) {
					ZappContentLog pLog = (ZappContentLog) obj;
					pLog.setCompanyid(pObjAuth.getObjCompanyid());
					pLog.setLoggerid(pObjAuth.getSessDeptUser().getDeptuserid());
					pLog.setLoggername(pObjAuth.getSessDeptUser().getZappUser().getName());
					pLog.setLoggerdeptid(pObjAuth.getSessDeptUser().getDeptid());
					pLog.setLoggerdeptname(pObjAuth.getSessDeptUser().getZappDept().getName());
					pLog.setLogtime(ZstFwDateUtils.getNow());
	//				pLog.setLogtype(BLANK); 	// External input
	//				pLog.setAction(BLANK); 	// External input		 
					if(pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log) != null) {
						ZappAuth pZappAuth = new ZappAuth();
//						BeanUtils.copyProperties((ZappAuth) pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log), pZappAuth);
						pZappAuth.setSessEnv(null);
						pLog.getMaplogs().put(ZappConts.LOGS.ITEM_LOGGER.log, pZappAuth);
					}				
					pLog.setLogs(mapToJson(pLog.getMaplogs()));
					pLog.setLogid(ZappKey.getPk(pLog));
					pLogList.add(pLog);
				}
				pObjRes = logService.cMultiRows(pObjAuth, pLogList, pObjRes);
			}
			
			/* System Log */
			if(checkObj[2] == true) {
				List<ZappSystemLog> pLogList = new ArrayList<ZappSystemLog>();
				for(Object obj : checkObjList) {
					ZappSystemLog pLog = (ZappSystemLog) obj;
					pLog.setCompanyid(pObjAuth.getObjCompanyid());
					pLog.setLoggerid(pObjAuth.getSessDeptUser().getDeptuserid());
					pLog.setLoggername(pObjAuth.getSessDeptUser().getZappUser().getName());
					pLog.setLoggerdeptid(pObjAuth.getSessDeptUser().getDeptid());
					pLog.setLoggerdeptname(pObjAuth.getSessDeptUser().getZappDept().getName());
					pLog.setLogtime(ZstFwDateUtils.getNow());
	//				pLog.setLogtype(BLANK); 	// External input
	//				pLog.setAction(BLANK); 	// External input		
					if(pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log) != null) {
						ZappAuth pZappAuth = new ZappAuth();
//						BeanUtils.copyProperties((ZappAuth) pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log), pZappAuth);
						pZappAuth.setSessEnv(null);
						pLog.getMaplogs().put(ZappConts.LOGS.ITEM_LOGGER.log, pZappAuth);
					}
					pLog.setLogs(mapToJson(pLog.getMaplogs()));
					pLog.setLogid(ZappKey.getPk(pLog));
					pLogList.add(pLog);
				}
				pObjRes = logService.cMultiRows(pObjAuth, pLogList, pObjRes);
			}
			
			/* Cycle  Log */
			if(checkObj[3] == true) {
				List<ZappCycleLog> pLogList = new ArrayList<ZappCycleLog>();
				for(Object obj : checkObjList) {
					ZappCycleLog pLog = (ZappCycleLog) obj;
					pLog.setCyclelogs(mapToJson(pLog.getMapcyclelogs()));
					pLog.setCycleid(ZappKey.getPk(pLog));
					pLogList.add(pLog);
				}
				pObjRes = logService.cMultiRows(pObjAuth, pLogList, pObjRes);
			}
			
		} 
		else {
			
			/* Validation */
			if(utilBinder.isEmpty(pObj) == true) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[leaveLog] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Access Log */
			if(pObj instanceof ZappAccessLog) {
				ZappAccessLog pLog = (ZappAccessLog) pObj;
				pLog.setCompanyid(pObjAuth.getObjCompanyid());
				pLog.setLoggerid(pObjAuth.getSessDeptUser().getDeptuserid());
				pLog.setLoggername(pObjAuth.getSessDeptUser().getZappUser().getName());
				pLog.setLoggerdeptid(pObjAuth.getSessDeptUser().getDeptid());
				pLog.setLoggerdeptname(pObjAuth.getSessDeptUser().getZappDept().getName());
				pLog.setLogtime(ZstFwValidatorUtils.valid(pLog.getLogtime()) == false ? ZstFwDateUtils.getNow() : pLog.getLogtime());
//				pLog.setLogtype(BLANK); 	// External input
//				pLog.setAction(BLANK); 	// External input
				if(pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log) != null) {
					ZappAuth pZappAuth = new ZappAuth();
//					BeanUtils.copyProperties((ZappAuth) pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log), pZappAuth);
					pZappAuth.setSessEnv(null);
					pLog.getMaplogs().put(ZappConts.LOGS.ITEM_LOGGER.log, pZappAuth);
				}
				pLog.setLogs(mapToJson(pLog.getMaplogs()));
				pObjRes = logService.cSingleRow(pObjAuth, pLog, pObjRes);
			}
			
			/* Content Log */
			if(pObj instanceof ZappContentLog) {
				ZappContentLog pLog = (ZappContentLog) pObj;
				pLog.setCompanyid(pObjAuth.getObjCompanyid());
				pLog.setLoggerid(pObjAuth.getSessDeptUser().getDeptuserid());
				pLog.setLoggername(pObjAuth.getSessDeptUser().getZappUser().getName());
				pLog.setLoggerdeptid(pObjAuth.getSessDeptUser().getDeptid());
				pLog.setLoggerdeptname(pObjAuth.getSessDeptUser().getZappDept().getName());
				pLog.setLogtime(ZstFwValidatorUtils.valid(pLog.getLogtime()) == false ? ZstFwDateUtils.getNow() : pLog.getLogtime());
//				pLog.setLogtype(BLANK); 	// External input
//				pLog.setAction(BLANK); 	// External input		
				if(pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log) != null) {
					ZappAuth pZappAuth = new ZappAuth();
//					BeanUtils.copyProperties((ZappAuth) pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log), pZappAuth);
					pZappAuth.setSessEnv(null);
					pLog.getMaplogs().put(ZappConts.LOGS.ITEM_LOGGER.log, pZappAuth);
				}				
				pLog.setLogs(mapToJson(pLog.getMaplogs()));
				pObjRes = logService.cSingleRow(pObjAuth, pLog, pObjRes);
				
			}
			
			/* System Log */
			if(pObj instanceof ZappSystemLog) {
				ZappSystemLog pLog = (ZappSystemLog) pObj;
				pLog.setCompanyid(pObjAuth.getObjCompanyid());
				pLog.setLoggerid(pObjAuth.getSessDeptUser().getDeptuserid());
				pLog.setLoggername(pObjAuth.getSessDeptUser().getZappUser().getName());
				pLog.setLoggerdeptid(pObjAuth.getSessDeptUser().getDeptid());
				pLog.setLoggerdeptname(pObjAuth.getSessDeptUser().getZappDept().getName());
				pLog.setLogtime(ZstFwValidatorUtils.valid(pLog.getLogtime()) == false ? ZstFwDateUtils.getNow() : pLog.getLogtime());
//				pLog.setLogtype(BLANK); 	// External input
//				pLog.setAction(BLANK); 	// External input		
				if(pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log) != null) {
					ZappAuth pZappAuth = new ZappAuth();
//					BeanUtils.copyProperties((ZappAuth) pLog.getMaplogs().get(ZappConts.LOGS.ITEM_LOGGER.log), pZappAuth);
					pZappAuth.setSessEnv(null);
					pLog.getMaplogs().put(ZappConts.LOGS.ITEM_LOGGER.log, pZappAuth);
				}				
				pLog.setLogs(mapToJson(pLog.getMaplogs()));
				pObjRes = logService.cSingleRow(pObjAuth, pLog, pObjRes);				
			}
			
			/* Cycle  Log */
			if(pObj instanceof ZappCycleLog) {
				ZappCycleLog pLog = (ZappCycleLog) pObj;
				pLog.setCyclelogs(mapToJson(pLog.getMapcyclelogs()));
				pLog.setCycleid(ZappKey.getPk(pLog));
				pObjRes = logService.cSingleRow(pObjAuth, pLog, pObjRes);				
			}			

		}
		
		return pObjRes;

	}
	
	/**
	 * Log for contents
	 * @param pObjContent
	 * @param pObjAction
	 * @return
	 */
	public ZappContentRes initLogRes(Object pObjContent, ZappConts.ACTION pObjAction) throws ZappException {
		
		ZappContentRes rZappContentRes = new ZappContentRes();
		rZappContentRes.setZappAcl(null);
		rZappContentRes.setZappAcls(null);
		rZappContentRes.setZappClassifications(null);
		rZappContentRes.setZappFiles(null);
		rZappContentRes.setZappKeywords(null);
		rZappContentRes.setZappLockedObject(null);
		rZappContentRes.setZappSharedObject(null);
		
		ZappContentPar pObjContentPar = null;
		ZappContentRes pObjContentRes = null;
		if(pObjContent != null) {
			if(pObjContent instanceof ZappContentPar) pObjContentPar = (ZappContentPar) pObjContent;
			if(pObjContent instanceof ZappContentRes) pObjContentRes = (ZappContentRes) pObjContent;
			if(pObjContentPar != null) {
				if(pObjContentPar.getZappBundle() == null) pObjContentPar.setZappBundle(new ZappBundle());
				if(pObjContentPar.getZappFile() == null) pObjContentPar.setZappFile(new ZappFile());
			}
		}
		
		switch(pObjAction) {
		
			case ADD:
				if(pObjContentPar != null) {
					if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						BeanUtils.copyProperties(pObjContentPar.getZappBundle(), rZappContentRes);
						rZappContentRes.setContentid(pObjContentPar.getZappBundle().getBundleid());
						rZappContentRes.setContentno(pObjContentPar.getZappBundle().getBno());
						rZappContentRes.setFiles(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappFile()));
					} else if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						BeanUtils.copyProperties(pObjContentPar.getZappFile(), rZappContentRes);
						rZappContentRes.setContentid(pObjContentPar.getZappFile().getMfileid());
						rZappContentRes.setContentno(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getFno()));
						rZappContentRes.setFiles(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappFiles()));
					}
					rZappContentRes.setRetentionid(pObjContentPar.getObjRetention());
					rZappContentRes.setFolders(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappClassObjects()));
					rZappContentRes.setContentacls(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappAcls()));
					rZappContentRes.setKeywords(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappKeywordObjects()));
				}
			break;
			
			case CHANGE:
				// self-processing
			break;
			
			case VIEW:
				if(pObjContentRes != null) {
					rZappContentRes.setContentid(pObjContentRes.getContentid());
					rZappContentRes.setContentno(pObjContentRes.getContentno());
					rZappContentRes.setHolderid(pObjContentRes.getHolderid());
				}
			break;
			
			case DISABLE: case ENABLE: case DISCARD: 
				if(pObjContentPar != null) {
					rZappContentRes.setContentid(pObjContentPar.getContentid());
					if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						rZappContentRes.setContentno(pObjContentPar.getZappBundle().getBno());
						rZappContentRes.setHolderid(pObjContentPar.getZappBundle().getHolderid());
					} else if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						rZappContentRes.setContentno(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getFno()));
						rZappContentRes.setHolderid(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getHolderid()));
					}
				}
			break;
			
			case RELOCATE: case REPLICATE:
				if(pObjContentPar != null) {
					rZappContentRes.setContentid(pObjContentPar.getContentid());
					if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						rZappContentRes.setContentno(pObjContentPar.getZappBundle().getBno());
						rZappContentRes.setHolderid(pObjContentPar.getZappBundle().getHolderid());
					} else if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						rZappContentRes.setContentno(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getFno()));
						rZappContentRes.setHolderid(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getHolderid()));
					}
					rZappContentRes.setFolders(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappClassObjects()));
				}
			break;

			case LOCK: case UNLOCK:
				if(pObjContentPar != null) {
					rZappContentRes.setContentid(pObjContentPar.getContentid());
					if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						rZappContentRes.setContentno(pObjContentPar.getZappBundle().getBno());
						rZappContentRes.setHolderid(pObjContentPar.getZappBundle().getHolderid());
					} else if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						rZappContentRes.setContentno(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getFno()));
						rZappContentRes.setHolderid(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getHolderid()));
					}
					rZappContentRes.setLocks(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappLockedObject()));
				}
			break;
			
			case SHARE: case UNSHARE: 
				if(pObjContentPar != null) {
					rZappContentRes.setContentid(pObjContentPar.getContentid());
					if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						rZappContentRes.setContentno(pObjContentPar.getZappBundle().getBno());
						rZappContentRes.setHolderid(pObjContentPar.getZappBundle().getHolderid());
					} else if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						rZappContentRes.setContentno(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getFno()));
						rZappContentRes.setHolderid(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getHolderid()));
					}
					rZappContentRes.setShares(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappSharedObjects()));
				}
			break;
			
			case MARK: case UNMARK: 
				if(pObjContentPar != null) {
					rZappContentRes.setContentid(pObjContentPar.getContentid());
					if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						rZappContentRes.setContentno(pObjContentPar.getZappBundle().getBno());
						rZappContentRes.setHolderid(pObjContentPar.getZappBundle().getHolderid());
					} else if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						rZappContentRes.setContentno(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getFno()));
						rZappContentRes.setHolderid(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getHolderid()));
					}
					rZappContentRes.setMarks(ZappJSONUtils.cvrtObjToJson(pObjContentPar.getZappMarkedObject()));
				}
			break;
			
			case APPROVE: case RETURN:
				if(pObjContentPar != null) {
					rZappContentRes.setContentid(pObjContentPar.getContentid());
					if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						rZappContentRes.setContentno(pObjContentPar.getZappBundle().getBno());
						rZappContentRes.setHolderid(pObjContentPar.getZappBundle().getHolderid());
					} else if(pObjContentPar.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						rZappContentRes.setContentno(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getFno()));
						rZappContentRes.setHolderid(ZstFwValidatorUtils.fixNullString(pObjContentPar.getZappFile().getHolderid()));
					}
					if(pObjContentPar.getZappLockedObject() != null) {
						rZappContentRes.setReasons(pObjContentPar.getZappLockedObject().getReason());
					}
				}
			break;
			
			default:
		}
		
		return rZappContentRes;
		
	}
	
	
	/**
	 * 
	 * @param pObj
	 * @return
	 */
	private String mapToJson(Map<String, Object> pObj) {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		String JSON = BLANK;
		
		if(pObj != null) {
	        try {
	        	JSON = mapper.writerWithView(ZstFwViews.Normal.class).writeValueAsString(pObj);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
		return JSON;
	}
}

package com.zenithst.core.log.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.bind.ZappDynamicBinder;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.log.bind.ZappLogBinder;
import com.zenithst.core.log.mapper.ZappAccessLogMapper;
import com.zenithst.core.log.mapper.ZappContentLogMapper;
import com.zenithst.core.log.mapper.ZappCycleLogMapper;
import com.zenithst.core.log.mapper.ZappSystemLogMapper;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.log.vo.ZappCycleLog;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

@Service("zappLogService")
public class ZappLogServiceImpl extends ZappService implements ZappLogService {

	/* Mapper */
	@Autowired
	private ZappAccessLogMapper accessLogMapper;		// Access Log
	@Autowired
	private ZappContentLogMapper contentLogMapper;		// Content Log
	@Autowired
	private ZappSystemLogMapper systemLogMapper;		// System Log
	@Autowired
	private ZappCycleLogMapper cycleLogMapper;			// Cycle  Log
	
	/* Binder */
	@Autowired
	private ZappDynamicBinder dynamicBinder;
	@Autowired
	private ZappLogBinder utilBinder;
	
	/* Service */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/**
	 * 로그 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				pvo.setCompanyid(ZstFwValidatorUtils.valid(pvo.getCompanyid()) == true ? pvo.getCompanyid() : pObjAuth.getSessCompany().getCompanyid());
				pvo.setLoggerid(ZstFwValidatorUtils.valid(pvo.getLoggerid()) == true ? pvo.getLoggerid() : pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setLoggername(ZstFwValidatorUtils.valid(pvo.getLoggername()) == true ? pvo.getLoggername() : pObjAuth.getSessUser().getName());
				pvo.setLoggerdeptid(ZstFwValidatorUtils.valid(pvo.getLoggerdeptid()) == true ? pvo.getLoggerdeptid() : pObjAuth.getSessDeptUser().getDeptid());
				pvo.setLoggerdeptname(ZstFwValidatorUtils.valid(pvo.getLoggerdeptname()) == true ? pvo.getLoggerdeptname() : pObjAuth.getSessDept().getName());
				pvo.setLogtime(ZstFwValidatorUtils.valid(pvo.getLogtime()) == true ? pvo.getLogtime() : ZstFwDateUtils.getNow());
				pvo.setLogid(ZappKey.getPk(pvo));
				if(accessLogMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ACCESSLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				pvo.setCompanyid(ZstFwValidatorUtils.valid(pvo.getCompanyid()) == true ? pvo.getCompanyid() : pObjAuth.getSessCompany().getCompanyid());
				pvo.setLoggerid(ZstFwValidatorUtils.valid(pvo.getLoggerid()) == true ? pvo.getLoggerid() : pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setLoggername(ZstFwValidatorUtils.valid(pvo.getLoggername()) == true ? pvo.getLoggername() : pObjAuth.getSessUser().getName());
				pvo.setLoggerdeptid(ZstFwValidatorUtils.valid(pvo.getLoggerdeptid()) == true ? pvo.getLoggerdeptid() : pObjAuth.getSessDeptUser().getDeptid());
				pvo.setLoggerdeptname(ZstFwValidatorUtils.valid(pvo.getLoggerdeptname()) == true ? pvo.getLoggerdeptname() : pObjAuth.getSessDept().getName());
				pvo.setLogtime(ZstFwValidatorUtils.valid(pvo.getLogtime()) == true ? pvo.getLogtime() : ZstFwDateUtils.getNow());
				pvo.setLogid(ZappKey.getPk(pvo));
				if(contentLogMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CONTENTLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				pvo.setCompanyid(ZstFwValidatorUtils.valid(pvo.getCompanyid()) == true ? pvo.getCompanyid() : pObjAuth.getSessCompany().getCompanyid());
				pvo.setLoggerid(ZstFwValidatorUtils.valid(pvo.getLoggerid()) == true ? pvo.getLoggerid() : pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setLoggername(ZstFwValidatorUtils.valid(pvo.getLoggername()) == true ? pvo.getLoggername() : pObjAuth.getSessUser().getName());
				pvo.setLoggerdeptid(ZstFwValidatorUtils.valid(pvo.getLoggerdeptid()) == true ? pvo.getLoggerdeptid() : pObjAuth.getSessDeptUser().getDeptid());
				pvo.setLoggerdeptname(ZstFwValidatorUtils.valid(pvo.getLoggerdeptname()) == true ? pvo.getLoggerdeptname() : pObjAuth.getSessDept().getName());
				pvo.setLogtime(ZstFwValidatorUtils.valid(pvo.getLogtime()) == true ? pvo.getLogtime() : ZstFwDateUtils.getNow());
				pvo.setLogid(ZappKey.getPk(pvo));
				if(systemLogMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][SYSTEMLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				pvo.setCycleid(ZappKey.getPk(pvo));
				if(cycleLogMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CYCLELOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}

	
	/**
	 * 로그 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cuSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(accessLogMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ACCESSLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(contentLogMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CONTENTLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(systemLogMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][SYSTEMLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				pvo.setCycleid(ZappKey.getPk(pvo));
				if(cycleLogMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CYCLELOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 저장한다. (다건)
	 * @param pObjs - Object to be registered (List<Object>)
	 * @return pObjRes - Result Object
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult cMultiRows(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {

		if(pObjs != null) {
			
			if(pObjs instanceof List == false) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			boolean[] checkobj = {false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappAccessLog) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappContentLog) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappSystemLog) {
					checkobj[2] = true;
					break;
				}
				if(obj instanceof ZappCycleLog) {
					checkobj[3] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Access Log
				List<ZappAccessLog> list = new ArrayList<ZappAccessLog>();
				for(Object obj : oObjs) {
					ZappAccessLog pvo = (ZappAccessLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLogid())) {
						pvo.setLogid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(accessLogMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][ACCESSLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[1] == true) {	// Content Log
				List<ZappContentLog> list = new ArrayList<ZappContentLog>();
				for(Object obj : oObjs) {
					ZappContentLog pvo = (ZappContentLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLogid())) {
						pvo.setLogid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(contentLogMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][CONTENTLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[2] == true) {	// System Log
				List<ZappSystemLog> list = new ArrayList<ZappSystemLog>();
				for(Object obj : oObjs) {
					ZappSystemLog pvo = (ZappSystemLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLogid())) {
						pvo.setLogid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(systemLogMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][SYSTEMLOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[3] == true) {	// Cycle  Log
				List<ZappCycleLog> list = new ArrayList<ZappCycleLog>();
				for(Object obj : oObjs) {
					ZappCycleLog pvo = (ZappCycleLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getCycleid())) {
						pvo.setCycleid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(cycleLogMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][CYCLELOG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	
	/**
	 * 로그 정보를 조회한다. (단건)
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.selectByPrimaryKey(pvo.getLogid()));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.selectByPrimaryKey(pvo.getLogid()));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.selectByPrimaryKey(pvo.getLogid()));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.selectByPrimaryKey(pvo.getCycleid()));
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rSingleRow] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 조회한다. (다건) 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				
				// Ordering
				pvo = (ZappAccessLog) mapOrders(pvo);

				pObjRes.setResObj(accessLogMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				
				// Ordering
				pvo = (ZappContentLog) mapOrders(pvo);

				pObjRes.setResObj(contentLogMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				
				// Ordering
				pvo = (ZappSystemLog) mapOrders(pvo);

				pObjRes.setResObj(systemLogMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				
				// Ordering
				pvo = (ZappCycleLog) mapOrders(pvo);

				pObjRes.setResObj(cycleLogMapper.selectByDynamic(getWhere(null, pvo)));
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 조회한다. (다건) 
	 * @param pObjf - Filter Object 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				
				// Ordering
				pvo = (ZappAccessLog) mapOrders(pvo);

				pObjRes.setResObj(accessLogMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				
				// Ordering
				pvo = (ZappContentLog) mapOrders(pvo);

				pObjRes.setResObj(contentLogMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				
				// Ordering
				pvo = (ZappSystemLog) mapOrders(pvo);

				pObjRes.setResObj(systemLogMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				
				// Ordering
				pvo = (ZappCycleLog) mapOrders(pvo);

				pObjRes.setResObj(cycleLogMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	
	/**
	 * 로그 정보를 조회한다. (다건) 
	 * @param pObjf - Filter Object 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("LOGTIME", "DESC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}
				pObjRes.setResObj(accessLogMapper.selectExtendByDynamic(pObjAuth
																	  , new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																	  , getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("LOGTIME", "DESC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}				
				pObjRes.setResObj(contentLogMapper.selectExtendByDynamic(pObjAuth
																	   , new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																	   , getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("LOGTIME", "DESC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}				
				pObjRes.setResObj(systemLogMapper.selectExtendByDynamic(pObjAuth
																	  , new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																	  , getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappCycleLog) {				// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("LOGTIME", "DESC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}
				pObjRes.setResObj(cycleLogMapper.selectExtendByDynamic(pObjAuth
																	  , new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																	  , getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRowsExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	/**
	 * 로그 정보를 수정한다. (단건) 
	 * @param pObj - Values to edit (PK and others)
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uSingleRow(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {

		if(pObj != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObj, null, ZappConts.ACTION.CHANGE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObj instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObj;
				if(accessLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][ACCESSLOG] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObj;
				if(contentLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][CONTENTLOG] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObj;
				if(systemLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][SYSTEMLOG] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObj;
				if(cycleLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][CYCLELOG] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uSingleRow] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 수정한다. (다건) 
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				pObjRes.setResObj(accessLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				pObjRes.setResObj(contentLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				pObjRes.setResObj(systemLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				pObjRes.setResObj(cycleLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 수정한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				pObjRes.setResObj(accessLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				pObjRes.setResObj(contentLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				pObjRes.setResObj(systemLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				pObjRes.setResObj(cycleLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}


		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 삭제한다. (단건) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.deleteByPrimaryKey(pvo.getLogid()));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.deleteByPrimaryKey(pvo.getLogid()));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.deleteByPrimaryKey(pvo.getLogid()));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.deleteByPrimaryKey(pvo.getCycleid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dSingleRow] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 삭제한다. (다건) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 삭제한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보 건수를 조회한다. (다건) 
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.countByDynamic(getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보 건수를 조회한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보 존재여부를 조회한다.
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보 존재여부를 조회한다. 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				pObjRes.setResObj(accessLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				pObjRes.setResObj(contentLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				pObjRes.setResObj(systemLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				pObjRes.setResObj(cycleLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 로그 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(accessLogMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(contentLogMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(systemLogMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappCycleLog) {				// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				pvo.setCycleid(ZappKey.getPk(pvo));
				if(cycleLogMapper.insert(pvo) < ONE) {
					result = false;
				}
			}

		}
		else {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * 로그 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cuSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(accessLogMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(contentLogMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				pvo.setLogid(ZappKey.getPk(pvo));
				if(systemLogMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				pvo.setCycleid(ZappKey.getPk(pvo));
				if(cycleLogMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}

		}
		else {
			result = false;
		}
		
		return result;
	}	
	
	/**
	 * 로그 정보를 저장한다. (다건)
	 * @param pObjs - Object to be registered (List<Object>)
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean cMultiRows(ZappAuth pObjAuth, Object pObjs) throws ZappException {
		

		if(pObjs != null) {
			
			if(pObjs instanceof List == false) {
				return false;
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			boolean[] checkobj = {false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappAccessLog) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappContentLog) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappSystemLog) {
					checkobj[2] = true;
					break;
				}
				if(obj instanceof ZappCycleLog) {
					checkobj[3] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Access Log
				List<ZappAccessLog> list = new ArrayList<ZappAccessLog>();
				for(Object obj : oObjs) {
					ZappAccessLog pvo = (ZappAccessLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLogid())) {
						pvo.setLogid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(accessLogMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[1] == true) {	// Content Log
				List<ZappContentLog> list = new ArrayList<ZappContentLog>();
				for(Object obj : oObjs) {
					ZappContentLog pvo = (ZappContentLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLogid())) {
						pvo.setLogid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(contentLogMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[2] == true) {	// System Log
				List<ZappSystemLog> list = new ArrayList<ZappSystemLog>();
				for(Object obj : oObjs) {
					ZappSystemLog pvo = (ZappSystemLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLogid())) {
						pvo.setLogid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(systemLogMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[3] == true) {	// Cycle  Log
				List<ZappCycleLog> list = new ArrayList<ZappCycleLog>();
				for(Object obj : oObjs) {
					ZappCycleLog pvo = (ZappCycleLog) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getCycleid())) {
						pvo.setCycleid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(cycleLogMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * 로그 정보를 조회한다. (단건)
	 * @param pObjs - Search Criteria Object
	 * @return Object
	 */
	public Object rSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				return accessLogMapper.selectByPrimaryKey(pvo.getLogid());
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				return contentLogMapper.selectByPrimaryKey(pvo.getLogid());
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				return systemLogMapper.selectByPrimaryKey(pvo.getLogid());
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				return cycleLogMapper.selectByPrimaryKey(pvo.getCycleid());
			}
		}

		return null;
	}
	
	/**
	 * 로그 정보를 조회한다. (다건)
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				return accessLogMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				return contentLogMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				return systemLogMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				return cycleLogMapper.selectByDynamic(getWhere(null, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * 로그 정보를 조회한다. (다건)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				return accessLogMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				return contentLogMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				return systemLogMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				return cycleLogMapper.selectByDynamic(getWhere(pObjf, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * 로그 정보를 수정한다. (PK)
	 * @param pObj - Object to search
	 * @return boolean
	 */
	public boolean uSingleRow(ZappAuth pObjAuth, Object pObj) throws ZappException {

		if(pObj != null) {
			
			if(pObj instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObj;
				if(accessLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObj;
				if(contentLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObj;
				if(systemLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObj;
				if(cycleLogMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 로그 정보를 수정한다. (다건)
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				if(accessLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				if(contentLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				if(systemLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				if(cycleLogMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 로그 정보를 수정한다. (다건)
	 * @param pObjs - Values to edit
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjs;
				if(accessLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjs;
				if(contentLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjs;
				if(systemLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjs;
				if(cycleLogMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 로그 정보를 삭제한다. (단건)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				if(accessLogMapper.deleteByPrimaryKey(pvo.getLogid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				if(contentLogMapper.deleteByPrimaryKey(pvo.getLogid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				if(systemLogMapper.deleteByPrimaryKey(pvo.getLogid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				if(cycleLogMapper.deleteByPrimaryKey(pvo.getCycleid()) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 로그 정보를 삭제한다. (다건)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				if(accessLogMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
				ZappContentLog pvo = (ZappContentLog) pObjw;
				if(contentLogMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				if(systemLogMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				if(cycleLogMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 로그 정보를 삭제한다. (다건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				if(accessLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				if(contentLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				if(systemLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				if(cycleLogMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 로그 정보 건수를 조회한다. (단건)
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				return accessLogMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				return contentLogMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				return systemLogMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				return cycleLogMapper.countByDynamic(getWhere(null, pObjw));
			}
		}
		
		return ZERO;
	}
	
	/**
	 * 로그 정보 건수를 조회한다. (단건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				return accessLogMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				return contentLogMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				return systemLogMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				return cycleLogMapper.countByDynamic(getWhere(pObjf, pObjw));
			}

		}
		
		return ZERO;
	}
	
	/**
	 * 로그 정보 존재여부를 조회한다.
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				return accessLogMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				return contentLogMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				return systemLogMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				return cycleLogMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
		}
		
		return false;
	}
	
	/**
	 * 로그 정보 존재여부를 조회한다.
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappAccessLog) {			// Access Log
//				ZappAccessLog pvo = (ZappAccessLog) pObjw;
				return accessLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappContentLog) {				// Content Log
//				ZappContentLog pvo = (ZappContentLog) pObjw;
				return contentLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappSystemLog) {			// System Log
//				ZappSystemLog pvo = (ZappSystemLog) pObjw;
				return systemLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappCycleLog) {			// Cycle  Log
//				ZappCycleLog pvo = (ZappCycleLog) pObjw;
				return cycleLogMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
		}
		
		return false;
	}

	/**
	 * Create dynamic conditions.
	 * @param pobjf - Filter
	 * @param pobjw - Values
	 * @return
	 */
	protected ZappDynamic getWhere(Object pobjf, Object pobjw) {
		
		dynamicBinder = new ZappDynamicBinder();
		
		String ALIAS = BLANK;
		ZappDynamic dynamic = null;
		if(pobjw instanceof ZappAccessLog) {
			ALIAS = ZappConts.ALIAS.ACCESSLOG.alias;
		}
		if(pobjw instanceof ZappContentLog) {
			ALIAS = ZappConts.ALIAS.CONTENTLOG.alias;
		}
		if(pobjw instanceof ZappSystemLog) {
			ALIAS = ZappConts.ALIAS.SYSTEMLOG.alias;
		}
		if(pobjw instanceof ZappCycleLog) {
			ALIAS = ZappConts.ALIAS.CYCLELOG.alias;
		}
		
		try {
			if(pobjw instanceof ZappAccessLog) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappAccessLog()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappContentLog) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappContentLog()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappSystemLog) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappSystemLog()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappCycleLog) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappCycleLog()) : pobjf, pobjw, ALIAS);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return dynamic;
	}
	
	/**
	 * Validation
	 * @param pObjs
	 * @param pObjw
	 * @param pObjAct
	 * @param pObjRes
	 * @return
	 */
	private ZstFwResult valid(ZappAuth pObjAuth,  Object pObjs, Object pObjw, ZappConts.ACTION pObjAct, ZstFwResult pObjRes) {
		
		switch(pObjAct) {
			case ADD: 
				if(utilBinder.isEmpty(pObjs) == true) {
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[Log] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Log] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Log] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Log] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Log] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Log] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Log] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:
		}

		return pObjRes;
	}	
	
	
	/**
	 * Convert ordering
	 * @param pIn
	 * @return
	 */
	private Object mapOrders(Object pIn) {
		
		if(pIn instanceof ZappAccessLog) {
			ZappAccessLog pvo = (ZappAccessLog) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}
			
			return pvo;
		}
		if(pIn instanceof ZappContentLog) {
			ZappContentLog pvo = (ZappContentLog) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}
		if(pIn instanceof ZappSystemLog) {
			ZappSystemLog pvo = (ZappSystemLog) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}
		if(pIn instanceof ZappCycleLog) {
			ZappCycleLog pvo = (ZappCycleLog) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}		
		
		return pIn;
	}
}

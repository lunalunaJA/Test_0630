package com.zenithst.core.status.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.status.bind.ZappStatusBinder;
import com.zenithst.core.status.mapper.ZappApmMapper;
import com.zenithst.core.status.mapper.ZappStatusMapper;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.status.vo.ZappStatus;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of statistics info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappStatusService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappStatusService")
public class ZappStatusServiceImpl extends ZappService implements ZappStatusService {

	/* Mapper */
	@Autowired
	private ZappApmMapper apmMapper;					// Apm
	@Autowired
	private ZappStatusMapper statusMapper;				// Status
	
	/* Service */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Binder */
	@Autowired
	private ZappDynamicBinder dynamicBinder;
	@Autowired
	private ZappStatusBinder utilBinder;
	

	/**
	 * 시스템 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappApm) {				// Apm
				ZappApm pvo = (ZappApm) pObjs;
				
				// Insert
				if(apmMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][APM] " + messageService.getMessage("ERR_C",  BLANK), BLANK);
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}

	/**
	 * 시스템 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cuSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappApm) {				// Apm
				ZappApm pvo = (ZappApm) pObjs;
				if(apmMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][APM] " + messageService.getMessage("ERR_C",  BLANK), BLANK);
				}
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}	
	
	
	/**
	 * 시스템 정보를 저장한다. (다건)
	 * @param pObjs - Object to be registered (List<Object>)
	 * @return pObjRes - Result Object
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult cMultiRows(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {

		if(pObjs != null) {
			
			Map<String, Object> params = new HashMap<String, Object>();
			boolean[] checkobj = {false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappApm) {
					checkobj[0] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Apm
				List<ZappApm> list = new ArrayList<ZappApm>();
				for(Object obj : oObjs) {
					ZappApm pvo = (ZappApm) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(apmMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][APM] " + messageService.getMessage("ERR_C",  BLANK), BLANK);
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	
	/**
	 * 시스템 정보를 조회한다. (단건)
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.VIEW_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				pObjRes.setResObj(apmMapper.selectByPrimaryKey(pvo.getApmid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rSingleRow] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 조회한다. (다건) 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				pObjRes.setResObj(apmMapper.selectByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder()), getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 조회한다. (다건) 
	 * @param pObjf - Filter Object 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				pObjRes.setResObj(apmMapper.selectByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder()), getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 수정한다. (단건) 
	 * @param pObj - Values to edit (PK and others)
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uSingleRow(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {

		if(pObj != null) {
			
			/* Validation */
			pObjRes = valid(pObj, null, ZappConts.ACTION.CHANGE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObj instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObj;
				if(apmMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][COMPANY] " + messageService.getMessage("ERR_E",  BLANK), BLANK);
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uSingleRow] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 수정한다. (다건) 
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjs;
				pObjRes.setResObj(apmMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 수정한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjs;
				pObjRes.setResObj(apmMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 삭제한다. (단건) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.DISABLE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				pObjRes.setResObj(apmMapper.deleteByPrimaryKey(pvo.getApmid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dSingleRow] " + messageService.getMessage("ERR_MIS_DELVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 삭제한다. (다건) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				pObjRes.setResObj(apmMapper.deleteByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 삭제한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
//				ZappApm pvo = (ZappApm) pObjw;
				pObjRes.setResObj(apmMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보 건수를 조회한다. (다건) 
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
//				ZappApm pvo = (ZappApm) pObjw;
				pObjRes.setResObj(apmMapper.countByDynamic(getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보 건수를 조회한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				pObjRes.setResObj(apmMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보 존재여부를 조회한다.
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				pObjRes.setResObj(apmMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보 존재여부를 조회한다. 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappApm) {			// Apm
				pObjRes.setResObj(apmMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
		}
		
		return pObjRes;
	}
	
	/**
	 * 시스템 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjs;
				pvo.setApmid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappApm(pvo.getApmid())) == true) {
					result = false;
				}
				if(apmMapper.insert(pvo) < ONE) {
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
	 * 시스템 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cuSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjs;
				pvo.setApmid(ZappKey.getPk(pvo));
				if(apmMapper.insertu(pvo) < ONE) {
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
	 * 시스템 정보를 저장한다. (다건)
	 * @param pObjs - Object to be registered (List<Object>)
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean cMultiRows(ZappAuth pObjAuth, Object pObjs) throws ZappException {
		

		if(pObjs != null) {
			
			Map<String, Object> params = new HashMap<String, Object>();
			boolean[] checkobj = {false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappApm) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappStatus) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappDeptUser) {
					checkobj[2] = true;
					break;
				}
				if(obj instanceof ZappGroupUser) {
					checkobj[3] = true;
					break;
				}
				if(obj instanceof ZappUser) {
					checkobj[4] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Apm
				List<ZappApm> list = new ArrayList<ZappApm>();
				for(Object obj : oObjs) {
					ZappApm pvo = (ZappApm) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getApmid())) {
						pvo.setApmid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(apmMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 시스템 정보를 조회한다. (단건)
	 * @param pObjs - Search Criteria Object
	 * @return Object
	 */
	public Object rSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				return apmMapper.selectByPrimaryKey(pvo.getApmid());
			}

		}

		return null;
	}
	
	/**
	 * 시스템 정보를 조회한다. (다건)
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				return apmMapper.selectByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder()), getWhere(null, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * 시스템 정보를 조회한다. (다건)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				return apmMapper.selectByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder()), getWhere(pObjf, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * 시스템 정보를 수정한다. (PK)
	 * @param pObj - Object to search
	 * @return boolean
	 */
	public boolean uSingleRow(ZappAuth pObjAuth, Object pObj) throws ZappException {

		if(pObj != null) {
			
			if(pObj instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObj;
				if(apmMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 시스템 정보를 수정한다. (다건)
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjs;
				if(apmMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 시스템 정보를 수정한다. (다건)
	 * @param pObjs - Values to edit
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjs;
				if(apmMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 시스템 정보를 삭제한다. (단건)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				if(apmMapper.deleteByPrimaryKey(pvo.getApmid()) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 시스템 정보를 삭제한다. (다건)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				ZappApm pvo = (ZappApm) pObjw;
				if(apmMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 시스템 정보를 삭제한다. (다건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
//				ZappApm pvo = (ZappApm) pObjw;
				if(apmMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 시스템 정보 건수를 조회한다. (단건)
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				return apmMapper.countByDynamic(getWhere(null, pObjw));
			}

		}
		
		return ZERO;
	}
	
	/**
	 * 시스템 정보 건수를 조회한다. (단건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
				return apmMapper.countByDynamic(getWhere(pObjf, pObjw));
			}

		}
		
		return ZERO;
	}
	
	/**
	 * 시스템 정보 존재여부를 조회한다.
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {			// Apm
//				ZappApm pvo = (ZappApm) pObjw;
				return apmMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}

		}
		
		return false;
	}
	
	/**
	 * 시스템 정보 존재여부를 조회한다.
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappApm) {					// Apm
				return apmMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}

		}
		
		return false;
	}
	
	public ZstFwResult getDbStatus(ZappAuth pObjAuth, ZappApm pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjRes.setResObj(apmMapper.selectDbStatus(pObjs));
		return pObjRes;
	}
	
	public ZstFwResult getDbLock(ZappAuth pObjAuth, ZappApm pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjRes.setResObj(apmMapper.selectLockList(pObjs));
		return pObjRes;
	}

	
	/**
	 * 임시 테이블 생성
	 */
	public boolean createTmpDateTbl(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		statusMapper.createTmpDateTbl(params);
		
		return true;
	}

	/**
	 * 임시 테이블 삭제
	 */
	public boolean dropTmpDateTbl(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		statusMapper.dropTmpDateTbl(params);
		
		return true;
	}

	/**
	 * Week 조회
	 */
	public ZappStatus getWeeks(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("stasdate", pObjs.getStasdate());
		params.put("staedate", pObjs.getStaedate());
		return statusMapper.getWeeks(params);
	}	

	/**
	 * Create dynamic conditions.
	 * @param pobjf - Filter
	 * @param pobjw - Values
	 * @return
	 */
	protected ZappDynamic getWhere(Object pobjf, Object pobjw) {
		
		String ALIAS = BLANK;
		ZappDynamic dynamic = null;
		if(pobjw instanceof ZappApm) {
			ALIAS = ZappConts.ALIAS.APM.alias;
		}
		
		try {
			if(pobjw instanceof ZappApm) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappApm()) : pobjf, pobjw, ALIAS);
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
	private ZstFwResult valid(Object pObjs, Object pObjw, ZappConts.ACTION pObjAct, ZstFwResult pObjRes) {
		
		switch(pObjAct) {
			case ADD: 
				if(utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[Status] " + messageService.getMessage("ERR_MIS_REGVAL",  BLANK), BLANK);
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Status] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Status] " + messageService.getMessage("ERR_MIS_EDTVAL",  BLANK), BLANK);
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Status] " + messageService.getMessage("ERR_MIS_DELVAL",  BLANK), BLANK);
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Status] " + messageService.getMessage("ERR_MIS_DELVAL",  BLANK), BLANK);
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Status] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
				}
			break;
			case VIEW: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Status] " + messageService.getMessage("ERR_MIS_QRYVAL",  BLANK), BLANK);
				}
			break;
			default:
		}

		return pObjRes;
	}	

	
	/**
	 * 임시 정보 저장
	 */
	public boolean insertTmpDate(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		if(pObjs.getStatermtype().equals(ZappConts.TYPES.STATUS_DAY.type)) {	// Day
			params.put("objTerm", ZappConts.TYPES.STATUS_DAY.type);
			params.put("stasdate", pObjs.getStasdate());	// yyyy-mm-dd
			params.put("staedate", pObjs.getStaedate());	// yyyy-mm-dd	
		}
		
		if(pObjs.getStatermtype().equals(ZappConts.TYPES.STATUS_WEEK.type)) {	// Week
			params.put("objTerm", ZappConts.TYPES.STATUS_WEEK.type);
			
			ZappStatus winfo = getWeeks(pObjAuth, pObjs, pObjRes);
			if(winfo != null) {
				params.put("dates", makeWeekList(winfo));
			} else {
				return false;
			}
		}
		
		if(pObjs.getStatermtype().equals(ZappConts.TYPES.STATUS_MONTH.type)) {		// Month
			params.put("objTerm", ZappConts.TYPES.STATUS_MONTH.type);
			// pObjs.getStasdate() --> yyyy
			params.put("dates", makeMonthList(pObjs));
		}	
		
		if(pObjs.getStatermtype().equals(ZappConts.TYPES.STATUS_QUARTER.type)) {	// QUARTER
			params.put("objTerm", ZappConts.TYPES.STATUS_QUARTER.type);
			// pObjs.getStasdate() --> yyyy
			params.put("stayear", pObjs.getStasdate());
			List<ZappStatus> qinfo = statusMapper.selectQuarter(params);
			if(qinfo != null) {
				params.put("dates", qinfo);
			} else {
				return false;
			}
		}
		
		if(pObjs.getStatermtype().equals(ZappConts.TYPES.STATUS_HALF.type)) {	// HALF
			params.put("objTerm", ZappConts.TYPES.STATUS_HALF.type);
			// pObjs.getStasdate() --> yyyy
			params.put("stayear", pObjs.getStasdate());
			List<ZappStatus> qinfo = statusMapper.selectHalf(params);
			if(qinfo != null) {
				params.put("dates", qinfo);
			} else {
				return false;
			}
		}
		
		if(pObjs.getStatermtype().equals(ZappConts.TYPES.STATUS_YEAR.type)) {	// YEAR
			params.put("objTerm", ZappConts.TYPES.STATUS_HALF.type);
			// pObjs.getStasdate(), pObjs.getStaedate() --> yyyy
			params.put("dates", makeYearList(pObjs));
		}
		
		return (statusMapper.insertTmpDate(params) > ZERO) ? true : false;

	}

	private List<ZappStatus> makeWeekList(ZappStatus pWeek) {
		int wy1 = Integer.parseInt(pWeek.getStasdate());
		int wy2 = Integer.parseInt(pWeek.getStaedate());
		List<ZappStatus> list = new ArrayList<ZappStatus>();
		for(int idx = wy1; idx <= wy2; idx++) {
			ZappStatus vo = new ZappStatus();
			vo.setStasdate(pWeek.getStakey() + fillStr(String.valueOf(idx), "0", 2));
			list.add(vo);
		}
		return list;
	}

	private List<ZappStatus> makeMonthList(ZappStatus pWeek) {
		String year = pWeek.getStasdate();
		List<ZappStatus> list = new ArrayList<ZappStatus>();
		for(int idx = 1; idx <= 12; idx++) {
			ZappStatus vo = new ZappStatus();
			vo.setStasdate(year + "-" + fillStr(String.valueOf(idx), "0", 2));
			list.add(vo);
		}
		return list;
	}
	
	private List<ZappStatus> makeYearList(ZappStatus pWeek) {
		int y1 = Integer.parseInt(pWeek.getStasdate());
		int y2 = Integer.parseInt(pWeek.getStaedate());
		List<ZappStatus> list = new ArrayList<ZappStatus>();
		for(int idx = y1; idx <= y2; idx++) {
			ZappStatus vo = new ZappStatus();
			vo.setStasdate(String.valueOf(idx));
			list.add(vo);
		}
		return list;
	}
	
	private static String fillStr(String str, String fillstr, int digit) {
		StringBuffer sb = new StringBuffer();
		if(str.length() <= digit) {
			for(int i=0; i < digit-String.valueOf(str).length(); i++) {
				sb.append(fillstr);
			}
			sb.append(str);
		}
		return sb.toString();
	}  
	
	
	public ZstFwResult getProcessStatusList(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("staObjId", pObjs.getStaobjid());
		params.put("staObjType", pObjs.getStaobjtype());
		params.put("staObjTerm", pObjs.getStatermtype());	
		params.put("staAction", pObjs.getStaaction());	
		params.put("staSdate", pObjs.getStasdate());	
		params.put("staEdate", pObjs.getStaedate());
		params.put("staIncToday", checkToday(pObjs.getStaedate()) == true ? YES : NO);	
		
		pObjRes.setResObj(statusMapper.selectProcessStatusList(params));
		
		return pObjRes;
	}
	
	public ZstFwResult getProcessStatusListAll(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjs.setStaIncToday(checkToday(pObjs.getStaedate()) == true ? YES : NO);
		pObjRes.setResObj(statusMapper.selectProcessStatusList_All(pObjs, null, null));
		
		return pObjRes;
	}	
	
	private boolean checkToday(String cdate) {

		boolean check = false;
		
		SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        try {
			Date cpdate = sdformat.parse(cdate);
			Date today = sdformat.parse(ZstFwDateUtils.getToday());
			
			if (cpdate.equals(today)) {
				check = true;
			}
			if (cpdate.after(today)) {
				check = true;
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
		return check;
	}
	
}

package com.zenithst.core.system.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
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
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.bind.ZappSystemBinder;
import com.zenithst.core.system.mapper.ZappCodeMapper;
import com.zenithst.core.system.mapper.ZappEnvMapper;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of system info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappSystemService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappSystemService")
public class ZappSystemServiceImpl extends ZappService implements ZappSystemService {
	
	/* Mapper */
	@Autowired
	private ZappEnvMapper envMapper;
	@Autowired
	private ZappCodeMapper codeMapper;

	/* Service */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Binder */
	@Autowired
	private ZappDynamicBinder dynamicBinder;
	@Autowired
	private ZappSystemBinder utilBinder;

	/**
	 * 시스템 정보를 저장한다. (단건)
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
			if(pObjs instanceof ZappCode) {				// Code
				ZappCode pvo = (ZappCode) pObjs;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());	
				pvo.setCodeid(ZappKey.getPk(pvo));
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) ? pvo.getIsactive() : YES);
				pvo.setEditable(ZstFwValidatorUtils.valid(pvo.getEditable()) ? pvo.getEditable() : YES);
				
				// Exist
				if(rExist(pObjAuth, new ZappCode(pvo.getCodeid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][CODE] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Priority
				ZappCode pZappCode_Priority = new ZappCode();
				pZappCode_Priority.setUpid(pvo.getUpid());
				pvo.setPriority(codeMapper.selectNextPriority(getWhere(null, pZappCode_Priority)));
				
				// Insert
				if(codeMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CODE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getCodeid());	// Pk
			}
			if(pObjs instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());		
				pvo.setEnvid(ZappKey.getPk(pvo));
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) ? pvo.getIsactive() : YES);
				
				// Exist
				if(rExist(pObjAuth, new ZappEnv(pvo.getEnvid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][ENV] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				// Insert
				if(envMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ENV] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getEnvid());	// Pk
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappCode) {				// Code
				ZappCode pvo = (ZappCode) pObjs;
				pvo.setCodeid(ZappKey.getPk(pvo));
				if(codeMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CODE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				pvo.setEnvid(ZappKey.getPk(pvo));
				if(envMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ENV] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
				if(obj instanceof ZappCode) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappEnv) {
					checkobj[1] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Code
				List<ZappCode> list = new ArrayList<ZappCode>();
				for(Object obj : oObjs) {
					ZappCode pvo = (ZappCode) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getCodeid())) {
						pvo.setCompanyid(pObjAuth.getSessCompany().getCompanyid());	
						pvo.setCodeid(ZappKey.getPk(pvo));
						pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) ? pvo.getIsactive() : YES);
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(codeMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][CODE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[1] == true) {	// Preferences
				List<ZappEnv> list = new ArrayList<ZappEnv>();
				for(Object obj : oObjs) {
					ZappEnv pvo = (ZappEnv) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getEnvid())) {
						pvo.setCompanyid(pObjAuth.getSessCompany().getCompanyid());	
						pvo.setEnvid(ZappKey.getPk(pvo));
						pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) ? pvo.getIsactive() : YES);
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(envMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][ENV] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.selectByPrimaryKey(pvo.getCodeid()));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.selectByPrimaryKey(pvo.getEnvid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rSingleRow] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
					
				pvo.setCompanyid(pObjAuth.getObjCompanyid());	
				
				if(utilBinder.isEmptyPk(pObjw) && !utilBinder.isEmpty(pObjw)) {	// pk 조건이 아닌 경우
					
					if(ZstFwValidatorUtils.valid(pvo.getTypes())) {
						ZappCode pZappCode_Root = new ZappCode(); 
						pZappCode_Root.setCompanyid(pObjAuth.getObjCompanyid());	
						pZappCode_Root.setUpid("ROOT");						
						pZappCode_Root.setTypes(pvo.getTypes());			// Code type
						List<ZappCode> rZappCodeList = codeMapper.selectByDynamic(getWhere(null, pZappCode_Root));
						if(rZappCodeList != null) {
							for(ZappCode vo : rZappCodeList) {
								pvo.setUpid(vo.getCodeid());
							}
							
						}
					}
				}

				// Ordering
				pvo = (ZappCode) mapOrders(pvo);
				
				pObjRes.setResObj(codeMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());	

				// Ordering
				pvo = (ZappEnv) mapOrders(pvo);
				
				pObjRes.setResObj(envMapper.selectByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());	
				if(utilBinder.isEmptyPk(pObjw) && !utilBinder.isEmpty(pObjw)) {	// pk 조건이 아닌 경우
					if(ZstFwValidatorUtils.valid(pvo.getTypes())) {
						ZappCode pZappCode_Root = new ZappCode(); 
						pZappCode_Root.setUpid("ROOT");						
						pZappCode_Root.setTypes(pvo.getTypes());			// Code type
						List<ZappCode> rZappCodeList = codeMapper.selectByDynamic(getWhere(null, pZappCode_Root));
						if(rZappCodeList != null) {
							for(ZappCode vo : rZappCodeList) {
								pvo.setUpid(vo.getCodeid());
							}
							
						}
					}
				}

				// Ordering
				pvo = (ZappCode) mapOrders(pvo);
				
				pObjRes.setResObj(codeMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;

				// Ordering
				pvo = (ZappEnv) mapOrders(pvo);
				
				pObjRes.setResObj(envMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, pObj, null, ZappConts.ACTION.CHANGE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObj instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObj;
				if(codeMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][COMPANY] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObj;
				if(envMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][DEPT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uSingleRow] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjs;
				pObjRes.setResObj(codeMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				pObjRes.setResObj(envMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjs;
				pObjRes.setResObj(codeMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				pObjRes.setResObj(envMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.deleteByPrimaryKey(pvo.getCodeid()));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.deleteByPrimaryKey(pvo.getEnvid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dSingleRow] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.deleteByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.countByDynamic(getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				pObjRes.setResObj(envMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			
			if(pObjs instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjs;
				pvo.setCodeid(ZappKey.getPk(pvo));
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) ? pvo.getIsactive() : YES);
				pvo.setEditable(ZstFwValidatorUtils.valid(pvo.getEditable()) ? pvo.getEditable() : YES);
				
				// Exist
				if(rExist(pObjAuth, new ZappCode(pvo.getCodeid())) == true) {
					result = false;
				}
				if(codeMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				pvo.setEnvid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappEnv(pvo.getEnvid())) == true) {
					result = false;
				}
				if(envMapper.insert(pvo) < ONE) {
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
			
			if(pObjs instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjs;
				pvo.setCodeid(ZappKey.getPk(pvo));
				if(codeMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				pvo.setEnvid(ZappKey.getPk(pvo));
				if(envMapper.insertu(pvo) < ONE) {
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
				if(obj instanceof ZappCode) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappEnv) {
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
			
			if(checkobj[0] == true) {	// Code
				List<ZappCode> list = new ArrayList<ZappCode>();
				for(Object obj : oObjs) {
					ZappCode pvo = (ZappCode) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getCodeid())) {
						pvo.setCodeid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(codeMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[1] == true) {	// Preferences
				List<ZappEnv> list = new ArrayList<ZappEnv>();
				for(Object obj : oObjs) {
					ZappEnv pvo = (ZappEnv) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getEnvid())) {
						pvo.setEnvid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(envMapper.insertb(params) != oObjs.size()) {
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
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				return codeMapper.selectByPrimaryKey(pvo.getCodeid());
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				return envMapper.selectByPrimaryKey(pvo.getEnvid());
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
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				return codeMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				return envMapper.selectByDynamic(getWhere(null, pvo));
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
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				return codeMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				return envMapper.selectByDynamic(getWhere(pObjf, pvo));
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
			
			if(pObj instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObj;
				if(codeMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObj;
				if(envMapper.updateByPrimaryKey(pvo) < ONE) {
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
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjs;
				if(codeMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				if(envMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
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
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjs;
				if(codeMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjs;
				if(envMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
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
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				if(codeMapper.deleteByPrimaryKey(pvo.getCodeid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				if(envMapper.deleteByPrimaryKey(pvo.getEnvid()) < ONE) {
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
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				if(codeMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
				ZappEnv pvo = (ZappEnv) pObjw;
				if(envMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
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
			
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				if(codeMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				if(envMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	public boolean dMultiRowsByCompany(ZappAuth pObjAuth, Object pObjw, String pCompanyid) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappEnv) {				// Preferences
				if(envMapper.deleteByCompany(pCompanyid) < ONE) {
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
			
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				return codeMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				return envMapper.countByDynamic(getWhere(null, pObjw));
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
			
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				return codeMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				return envMapper.countByDynamic(getWhere(pObjf, pObjw));
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
			
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				return codeMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				return envMapper.exists(getWhere(null, pObjw)) == null ? false : true;
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
			
			if(pObjw instanceof ZappCode) {			// Code
//				ZappCode pvo = (ZappCode) pObjw;
				return codeMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappEnv) {				// Preferences
//				ZappEnv pvo = (ZappEnv) pObjw;
				return envMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}

		}
		
		return false;
	}
	
	/**
	 * 다음 우선순위를 조회한다.
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult rNextPriority(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappCode) {			// Code
				ZappCode pvo = (ZappCode) pObjw;
				pObjRes.setResObj(codeMapper.selectNextPriority(getWhere(pObjf, pvo)));
			}
		}
		
		return pObjRes;
	}	

	
	/**
	 * 다음 우선순위를 조회한다.
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult upwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			if(pObjs instanceof ZappCode) {			// Code
				ZappCode pvos = (ZappCode) pObjs;
				ZappCode pvoe = (ZappCode) pObje;
				pObjRes.setResObj(codeMapper.upwardPriority(pvos, pvoe));
			}

		}
		
		return pObjRes;
	}
	
	/**
	 * 다음 우선순위를 조회한다.
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult downwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			if(pObjs instanceof ZappCode) {			// Code
				ZappCode pvos = (ZappCode) pObjs;
				ZappCode pvoe = (ZappCode) pObje;
				pObjRes.setResObj(codeMapper.downwardPriority(pvos, pvoe));
			}

		}
		
		return pObjRes;
	}
	
	/**
	 * 데이타를 초기화한다.
	 */
	public ZstFwResult initSystem(ZappAuth pObjAuth, Object pObj, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObj != null && pObjs != null) {
			
			int cnt = ZERO;
			if(pObjs instanceof ZappCompany) {
				ZappCompany pvo =  (ZappCompany) pObjs;
			
				logger.info("(initSystem) Company ID : " + pvo.getCompanyid());
				
				if(pObj instanceof ZappCode) {
					cnt = codeMapper.initCode(pvo);
				}
				
				if(pObj instanceof ZappEnv) {
					cnt = envMapper.initEnv(pvo);
				}
			
			}
			if(pObjs instanceof ZappUser) {
				ZappUser pvo =  (ZappUser) pObjs;
			
				logger.info("(initSystem) Company ID : " + pvo.getCompanyid());
				logger.info("(initSystem) User ID : " + pvo.getUserid());
				
//				if(pObj instanceof ZappCode) {
//					cnt = codeMapper.initUserCode(pvo);
//				}
				
				if(pObj instanceof ZappEnv) {
					cnt = envMapper.initUserEnv(pvo);
				}
			
			}

			pObjRes.setResObj(cnt > ZERO ? true : false);
			
		}
		
		return pObjRes;
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
		if(pobjw instanceof ZappCode) {
			ALIAS = ZappConts.ALIAS.CODE.alias;
		}
		if(pobjw instanceof ZappEnv) {
			ALIAS = ZappConts.ALIAS.ENV.alias;
		}
		
		try {
			if(pobjw instanceof ZappCode) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappCode()) : pobjf, pobjw, ALIAS);
			} 
			if(pobjw instanceof ZappEnv) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappEnv()) : pobjf, pobjw, ALIAS);
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
				if(utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[System] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[System] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[System] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[System] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[System] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[System] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[System] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
		
		if(pIn instanceof ZappCode) {
			ZappCode pvo = (ZappCode) pIn;
			
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
		if(pIn instanceof ZappEnv) {
			ZappEnv pvo = (ZappEnv) pIn;
			
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

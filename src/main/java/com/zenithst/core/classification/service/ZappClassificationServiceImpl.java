package com.zenithst.core.classification.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.bind.ZappClassificationBinder;
import com.zenithst.core.classification.mapper.ZappAdditoryClassificationMapper;
import com.zenithst.core.classification.mapper.ZappClassificationMapper;
import com.zenithst.core.classification.vo.ZappAdditoryClassification;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.classification.vo.ZappClassificationPar;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.bind.ZappDynamicBinder;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwEncodeUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of classification info. <br>
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

@Service("zappClassificationService")
public class ZappClassificationServiceImpl extends ZappService implements ZappClassificationService {
	
	/* Mapper */
	@Autowired
	private ZappClassificationMapper classificationMapper;
	@Autowired
	private ZappAdditoryClassificationMapper additoryClassificationMapper;

	/* Service */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Binder */
	@Autowired
	private ZappDynamicBinder dynamicBinder;
	@Autowired
	private ZappClassificationBinder utilBinder;

	/**
	 * Register new classification. (Single)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappClassification) {				// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				pvo.setCode(ZstFwValidatorUtils.valid(pvo.getCode()) ? pvo.getCode() : getNewCode(pObjAuth, pvo));
				pvo.setClassid(ZappKey.getPk(pvo));
				pvo.setIsactive(ZappConts.USAGES.YES.use);
				pObjRes.setResObj(pvo.getClassid());
				
				// Check if the same code exists 
				ZappClassification pZappClassification_Check = new ZappClassification();
				pZappClassification_Check.setClassid(pvo.getClassid()); //kdh 20220127
				//pZappClassification_Check.setCode(pvo.getCode());
				//pZappClassification_Check.setTypes(pvo.getTypes()); //jwjang 20210804
				if(rExist(pObjAuth, null, pZappClassification_Check) == true) {
					return ZappFinalizing.finalising("ERR_DUP_CODE", "[cSingleRow][CLASSIFICATION] " + messageService.getMessage("ERR_DUP_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
				
				// Check if the same code name exists
				ZappClassification pZappClassification_Filter = new ZappClassification();
				pZappClassification_Filter.setName(Operators.EQUAL.operator);
				pZappClassification_Check = new ZappClassification();
				pZappClassification_Check.setUpid(pvo.getUpid());
				pZappClassification_Check.setName(pvo.getName());
				pZappClassification_Check.setTypes(pvo.getTypes()); //jwjang 20210804
				if(rExist(pObjAuth, pZappClassification_Filter, pZappClassification_Check) == true) {
					return ZappFinalizing.finalising("ERR_DUP_NAME", "[cSingleRow][CLASSIFICATION] " + messageService.getMessage("ERR_DUP_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Exist
				if(rExist(pObjAuth, new ZappClassification(pvo.getClassid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][CLASSIFICATION] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Next Priority
				ZappClassification pvo_priority = new ZappClassification();
				pvo_priority.setUpid(pvo.getUpid());
				pvo.setPriority(classificationMapper.selectNextPriority(getWhere(null, pvo_priority)));
				
				// Insert
				if(classificationMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CLASSIFICATION] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			if(pObjs instanceof ZappAdditoryClassification) {				// Additional classification
				
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappAdditoryClassification(pvo.getClassid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][ADDITORYCLASSIFICATION] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Insert
				if(additoryClassificationMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ADDITORYCLASSIFICATION] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}

	/**
	 * Classifiction 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cuSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappClassification) {				// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				pvo.setClassid(ZappKey.getPk(pvo));
				if(classificationMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CLASSIFICATION] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappAdditoryClassification) {				// Classifiction
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				if(additoryClassificationMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ADDITORYCLASSIFICATION] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	
	/**
	 * Classifiction 정보를 저장한다. (다건)
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
				if(obj instanceof ZappClassification) {
					checkobj[0] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Classifiction
				List<ZappClassification> list = new ArrayList<ZappClassification>();
				for(Object obj : oObjs) {
					ZappClassification pvo = (ZappClassification) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getClassid())) {
						pvo.setClassid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(classificationMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][CLASSIFICATION] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			if(checkobj[1] == true) {	// Additional classification
				List<ZappAdditoryClassification> list = new ArrayList<ZappAdditoryClassification>();
				for(Object obj : oObjs) {
					ZappAdditoryClassification pvo = (ZappAdditoryClassification) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(additoryClassificationMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][ADDITORYCLASSIFICATION] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	
	/**
	 * Classifiction 정보를 조회한다. (단건)
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW_PK, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.selectByPrimaryKey(pvo.getClassid()));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Classifiction
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.selectByPrimaryKey(pvo.getClassid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rSingleRow] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 조회한다. (다건) 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				
				// Ordering
				pvo = (ZappClassification) mapOrders(pvo);
		
				pObjRes.setResObj(classificationMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				
				// Ordering
				pvo = (ZappAdditoryClassification) mapOrders(pvo);
		
				pObjRes.setResObj(additoryClassificationMapper.selectByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 조회한다. (다건) 
	 * @param pObjf - Filter Object 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				
				// Ordering
				pvo = (ZappClassification) mapOrders(pvo);
		
				pObjRes.setResObj(classificationMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				
				// Ordering
				pvo = (ZappAdditoryClassification) mapOrders(pvo);
		
				pObjRes.setResObj(additoryClassificationMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 수정한다. (단건) 
	 * @param pObj - Values to edit (PK and others)
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uSingleRow(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {

		if(pObj != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObj, null, ZappConts.ACTION.CHANGE_PK, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObj instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObj;
				if(classificationMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][CLASSIFICATION] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObj;
				if(additoryClassificationMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][ADDITORYCLASSIFICATION] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uSingleRow] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 수정한다. (다건) 
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				pObjRes.setResObj(classificationMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				pObjRes.setResObj(additoryClassificationMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 수정한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				pObjRes.setResObj(classificationMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				pObjRes.setResObj(additoryClassificationMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 삭제한다. (단건) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE_PK, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.deleteByPrimaryKey(pvo.getClassid()));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.deleteByPrimaryKey(pvo.getClassid()));
			}			

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dSingleRow] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 삭제한다. (다건) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.deleteByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 삭제한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보 건수를 조회한다. (다건) 
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.countByDynamic(getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보 건수를 조회한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보 존재여부를 조회한다.
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보 존재여부를 조회한다. 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				pObjRes.setResObj(additoryClassificationMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				pvo.setClassid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappClassification(pvo.getClassid())) == true) {
					result = false;
				}
				if(classificationMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappClassification(pvo.getClassid())) == true) {
					result = false;
				}
				if(additoryClassificationMapper.insert(pvo) < ONE) {
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
	 * Classifiction 정보를 저장한다. (단건)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cuSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				pvo.setClassid(ZappKey.getPk(pvo));
				if(classificationMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				if(additoryClassificationMapper.insertu(pvo) < ONE) {
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
	 * Classifiction 정보를 저장한다. (다건)
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
				if(obj instanceof ZappClassification) {
					checkobj[0] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Classifiction
				List<ZappClassification> list = new ArrayList<ZappClassification>();
				for(Object obj : oObjs) {
					ZappClassification pvo = (ZappClassification) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getClassid())) {
						pvo.setClassid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(classificationMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			
			if(checkobj[1] == true) {	// Classifiction
				List<ZappAdditoryClassification> list = new ArrayList<ZappAdditoryClassification>();
				for(Object obj : oObjs) {
					ZappAdditoryClassification pvo = (ZappAdditoryClassification) obj;
					list.add(pvo);
				}
				if(additoryClassificationMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보를 조회한다. (단건)
	 * @param pObjs - Search Criteria Object
	 * @return Object
	 */
	public Object rSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				return classificationMapper.selectByPrimaryKey(pvo.getClassid());
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Classifiction
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				return additoryClassificationMapper.selectByPrimaryKey(pvo.getClassid());
			}

		}

		return null;
	}
	
	/**
	 * Classifiction 정보를 조회한다. (다건)
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				return classificationMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Classifiction
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				return additoryClassificationMapper.selectByDynamic(getWhere(null, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * Classifiction 정보를 조회한다. (다건)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				return classificationMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				return additoryClassificationMapper.selectByDynamic(getWhere(pObjf, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * Classifiction 정보를 수정한다. (PK)
	 * @param pObj - Object to search
	 * @return boolean
	 */
	public boolean uSingleRow(ZappAuth pObjAuth, Object pObj) throws ZappException {

		if(pObj != null) {
			
			if(pObj instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObj;
				if(classificationMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObj;
				if(additoryClassificationMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보를 수정한다. (다건)
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				if(classificationMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				if(additoryClassificationMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보를 수정한다. (다건)
	 * @param pObjs - Values to edit
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjs;
				if(classificationMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjs;
				if(additoryClassificationMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보를 삭제한다. (단건)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				if(classificationMapper.deleteByPrimaryKey(pvo.getClassid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Classifiction
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				if(additoryClassificationMapper.deleteByPrimaryKey(pvo.getClassid()) < ONE) {
					return false;
				}
			}
			
		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보를 삭제한다. (다건)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				if(classificationMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				if(additoryClassificationMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보를 삭제한다. (다건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				if(classificationMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				if(additoryClassificationMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}			

		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보 건수를 조회한다. (단건)
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				return classificationMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				return additoryClassificationMapper.countByDynamic(getWhere(null, pObjw));
			}
		}
		
		return ZERO;
	}
	
	/**
	 * Classifiction 정보 건수를 조회한다. (단건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				return classificationMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				return additoryClassificationMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			
		}
		
		return ZERO;
	}
	
	/**
	 * Classifiction 정보 존재여부를 조회한다.
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				return classificationMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				return additoryClassificationMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
		}
		
		return false;
	}
	
	/**
	 * Classifiction 정보 존재여부를 조회한다.
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
//				ZappClassification pvo = (ZappClassification) pObjw;
				return classificationMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappAdditoryClassification) {			// Additional classification
//				ZappAdditoryClassification pvo = (ZappAdditoryClassification) pObjw;
				return additoryClassificationMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}

		}
		
		return false;
	}
	
	/* *************************************************************************************************** */
	
	/**
	 * Classifiction 정보를 조회한다. (하위)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult rMultiRowsDown(ZappAuth pObjAuth, Object pObjf, Object pObjw, Object pObjwh, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			ZappEnv SYS_CLASSACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CLASSACL_YN.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
		
			/* Default */
			if(SYS_CLASSACL_YN == null) {
				SYS_CLASSACL_YN = new ZappEnv(); SYS_CLASSACL_YN.setSetval(YES);
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			if(pObjw instanceof ZappClassificationPar) {			// Classifiction
				ZappClassificationPar pvo_par = (ZappClassificationPar) pObjw;
				ZappClassification pvo = new ZappClassification();
				ZappClassification pvoh = (ZappClassification) pObjwh;
				BeanUtils.copyProperties(pvo_par, pvo);
				
				boolean SKIP_CLASSACL = false;
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_CLASS.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
					SKIP_CLASSACL = true;
				} else {
					SKIP_CLASSACL = pObjAuth.getObjSkipAcl();
				}
				
				// 조회 레벨
				ZappEnv SYS_CLASS_VIEWLEVEL = new ZappEnv();
				if(pvo_par.getViewlevel() > ZERO) {
					SYS_CLASS_VIEWLEVEL.setObjenum(pvo_par.getViewlevel());
				} else {
					SYS_CLASS_VIEWLEVEL.setObjenum(ZERO);
				}
				
				//
				if(ZstFwValidatorUtils.valid(pvo_par.getIsactive()) == true) {
					if(pvoh == null) {
						pvoh = new ZappClassification();
					}
					pvoh.setIsactive(pvo_par.getIsactive());
				}
				
				logger.info("CLS================================================================");
				logger.info("SKIP_CLASSACL = [" + SKIP_CLASSACL + "]");
				logger.info("SYS_CLASSACL_YN = [" + SYS_CLASSACL_YN.getSetval() + "]");
				logger.info("getObjSkipAcl() = [" + pvo.getObjSkipAcl() + "]");
				logger.info("==================================================================");
				
				pObjRes.setResObj(classificationMapper.selectDownByDynamic(pObjAuth
																		  , SKIP_CLASSACL == true ? null : SYS_CLASSACL_YN
																		  , SYS_DEPT_RANGE
																		  , SYS_CLASS_VIEWLEVEL
																		  , getWhere(pObjf, pvo)
																		  , pvoh == null ? null : pvoh));
			}

		}
		
		return pObjRes;
	}
	
	/**
	 * Classifiction 정보를 조회한다. (상위)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult rMultiRowsUp(ZappAuth pObjAuth, Object pObjf, Object pObjw, Object pObjwh, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			ZappEnv SYS_CLASSACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CLASSACL_YN.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
		
			/* Default */
			if(SYS_CLASSACL_YN == null) {
				SYS_CLASSACL_YN = new ZappEnv(); SYS_CLASSACL_YN.setSetval(YES);
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				ZappClassification pvoh = (ZappClassification) pObjwh;

				/* 유형별 권한 체크 여부 */
				boolean SKIP_CLASSACL = false;
				if(pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_COMPANY.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_CLASS.type)
						|| pvo.getTypes().equals(ZappConts.TYPES.CLASS_DOCTYPE.type)) {
					SKIP_CLASSACL = true; 
				} else {
					SKIP_CLASSACL = pObjAuth.getObjSkipAcl();
				}
				
				pObjRes.setResObj(classificationMapper.selectUpByDynamic(pObjAuth
																	    , SKIP_CLASSACL == true ? null : SYS_CLASSACL_YN
																		, SYS_DEPT_RANGE
																		, getWhere(pObjf, pvo)
																		, pvoh == null ? null : pvoh));
			}

		}
		
		return pObjRes;
	}

	/**
	 * 폴더 경로를 조회한다.
	 * @param pObjAuth ZappAuth Obj.
	 * @paramp Obj Classid or Upid
	 */
	public String rClassPathUp(ZappAuth pObjAuth, String pObj, boolean pIsLast) throws ZappException, SQLException {

		StringBuffer path = new StringBuffer();
		path.append(classificationMapper.selectCPath(pObj));
		if(path.length() > ZERO && pIsLast == false) {
			path.append(ZappConts.SCHARS.SPACE.character + ZappConts.SCHARS.GREATER_THAN.character + ZappConts.SCHARS.SPACE.character);
		}
		
		return (path.toString() == null ? BLANK : path.toString());
	
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
			
			if(pObjw instanceof ZappClassification) {			// Classifiction
				ZappClassification pvo = (ZappClassification) pObjw;
				pObjRes.setResObj(classificationMapper.selectNextPriority(getWhere(pObjf, pvo)));
			}

		}
		
		return pObjRes;
	}	

	/**
	 * 책갈피 Classifiction 건수
	 */
	public ZstFwResult rMarkedCount(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw instanceof ZappClassificationPar) {
			
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;
			ZappClassification pvo_class = new ZappClassification();
			ZappAdditoryClassification pvo_additoryclass = new ZappAdditoryClassification();
			BeanUtils.copyProperties(pvo, pvo_class);
			pvo_class.setIsactive(YES);
			pvo_additoryclass = pvo.getZappAdditoryClassification();
			
			pObjRes.setResObj(classificationMapper.selectMarkedCountByDynamic(pObjAuth
																		   , (utilBinder.isEmptyPk(pvo_class) && utilBinder.isEmpty(pvo_class)) ? null : getWhere(null, pvo_class)
																		   , (utilBinder.isEmptyPk(pvo_additoryclass) && utilBinder.isEmpty(pvo_additoryclass)) ? null : getWhere(null, pvo_additoryclass)));
			
		}
		
		return pObjRes;
	}
	
	/**
	 * 책갈피 Classifiction 목록
	 */
	public ZstFwResult rMarkedList(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {
	
		if(pObjw instanceof ZappClassification) {
			
			ZappClassificationPar pvo = (ZappClassificationPar) pObjw;
			ZappClassification pvo_class = new ZappClassification();
			ZappAdditoryClassification pvo_additoryclass = new ZappAdditoryClassification();
			BeanUtils.copyProperties(pvo, pvo_class);
			pvo_class.setIsactive(YES);
			pvo_additoryclass = pvo.getZappAdditoryClassification();
			
			/* Sorting */
			if(pvo.getObjmaporder().size() == ZERO) {
				pvo.getObjmaporder().put("MARKOBJ_marktime", "desc");
			}
			
			pObjRes.setResObj(classificationMapper.selectMarkedListByDynamic(pObjAuth
																		   , new ZappQryOpt(pObjAuth, pvo.getObjpgnum(), pvo.getObjmaporder())
																		   , (utilBinder.isEmptyPk(pvo_class) && utilBinder.isEmpty(pvo_class)) ? null : getWhere(null, pvo_class)
																		   , (utilBinder.isEmptyPk(pvo_additoryclass) && utilBinder.isEmpty(pvo_additoryclass)) ? null : getWhere(null, pvo_additoryclass)));
			
		}
		
		return pObjRes;
	}

	/**
	 * Inquiry Affiliation ID
	 */
	public ZstFwResult rMultiAffiliationRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjw instanceof ZappClassification) {
			ZappClassification pvo = (ZappClassification) pObjw;
			pObjRes.setResObj(classificationMapper.selectAffiliation(pvo));
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
			
			if(pObjs instanceof ZappClassification) {			// Classifiction
				ZappClassification pvos = (ZappClassification) pObjs;
				ZappClassification pvoe = (ZappClassification) pObje;
				pObjRes.setResObj(classificationMapper.upwardPriority(pvos, pvoe));
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
			
			if(pObjs instanceof ZappClassification) {			// Classifiction
				ZappClassification pvos = (ZappClassification) pObjs;
				ZappClassification pvoe = (ZappClassification) pObje;
				pObjRes.setResObj(classificationMapper.downwardPriority(pvos, pvoe));
			}

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
		if(pobjw instanceof ZappClassification) {
			ALIAS = ZappConts.ALIAS.CLASS.alias;
		}
		if(pobjw instanceof ZappAdditoryClassification) {
			ALIAS = ZappConts.ALIAS.ADDITORYCLASS.alias;
		}
		
		try {
			if(pobjw instanceof ZappClassification) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappClassification()) : pobjf, pobjw, ALIAS);
			} 
			if(pobjw instanceof ZappAdditoryClassification) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappAdditoryClassification()) : pobjf, pobjw, ALIAS);
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
	 * Code 자동 생성
	 * @param pObjAuth
	 * @param pObj
	 * @return
	 */
	private String getNewCode(ZappAuth pObjAuth, ZappClassification pObj) {
		
		StringBuffer code = new StringBuffer();
		code.append(pObjAuth.getObjCompanyid());
		code.append(pObjAuth.getSessDeptUser().getDeptuserid());
		code.append(ZstFwDateUtils.getNow());
		code.append(pObj.getUpid());
		code.append(pObj.getTypes());
		
		return ZstFwEncodeUtils.encodeString_SHA256(code.toString());
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
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[Classification] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Classification] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Classification] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Classification] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Classification] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Classification] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Classification] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
		
		if(pIn instanceof ZappClassification) {
			ZappClassification pvo = (ZappClassification) pIn;
			
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
		if(pIn instanceof ZappAdditoryClassification) {
			ZappAdditoryClassification pvo = (ZappAdditoryClassification) pIn;
			
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

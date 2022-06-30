package com.zenithst.core.tag.service;

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
import com.zenithst.core.tag.bind.ZappTagBinder;
import com.zenithst.core.tag.mapper.ZappImgMapper;
import com.zenithst.core.tag.mapper.ZappTagMapper;
import com.zenithst.core.tag.mapper.ZappTaskTagMapper;
import com.zenithst.core.tag.vo.ZappImg;
import com.zenithst.core.tag.vo.ZappTag;
import com.zenithst.core.tag.vo.ZappTaskTag;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of tag info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappTagService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappTagService")
public class ZappTagServiceImpl extends ZappService implements ZappTagService {

	/* Mapper */
	@Autowired
	private ZappImgMapper imgMapper;			// Image
	@Autowired
	private ZappTagMapper tagMapper;			// Tag
	@Autowired
	private ZappTaskTagMapper taskTagMapper;	// TaskTag
	
	/* Binder */
	@Autowired
	private ZappDynamicBinder dynamicBinder;
	@Autowired
	private ZappTagBinder utilBinder;
	
	/* Service */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/**
	 * Register new access control info. (Single)
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
			if(pObjs instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				pvo.setAppid(ZappKey.getPk(pvo));
				if(imgMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][IMG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				pvo.setTagid(ZappKey.getPk(pvo));
				if(tagMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][Tag] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappTaskTag) {				// Tag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				pvo.setTasktagid(ZappKey.getPk(pvo));
				if(taskTagMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][TaskTag] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Register new access control info. (Single)
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
			if(pObjs instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				pvo.setAppid(ZappKey.getPk(pvo));
				if(imgMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cuSingleRow][IMG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				pvo.setTagid(ZappKey.getPk(pvo));
				if(tagMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cuSingleRow][Tag] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappTaskTag) {				// Tag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				pvo.setTasktagid(ZappKey.getPk(pvo));
				if(taskTagMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cuSingleRow][TaskTag] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	
	/**
	 * 조직 정보를 저장한다. (다건)
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
				if(obj instanceof ZappImg) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappTag) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappTaskTag) {
					checkobj[2] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Image
				List<ZappImg> list = new ArrayList<ZappImg>();
				for(Object obj : oObjs) {
					ZappImg pvo = (ZappImg) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getAppid())) {
						pvo.setAppid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(imgMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][IMG] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[1] == true) {	// Tag
				List<ZappTag> list = new ArrayList<ZappTag>();
				for(Object obj : oObjs) {
					ZappTag pvo = (ZappTag) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getTagid())) {
						pvo.setTagid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(tagMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][Tag] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[2] == true) {	// TaskTag
				List<ZappTaskTag> list = new ArrayList<ZappTaskTag>();
				for(Object obj : oObjs) {
					ZappTaskTag pvo = (ZappTaskTag) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getTasktagid())) {
						pvo.setTasktagid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(tagMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][TaskTag] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	
	/**
	 * Inquire access control info. (Single)
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
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.selectByPrimaryKey(pvo.getAppid()));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.selectByPrimaryKey(pvo.getTagid()));
			}
			if(pObjw instanceof ZappTaskTag) {				// Tag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(tagMapper.selectByPrimaryKey(pvo.getTasktagid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rSingleRow] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire access control info. (Multiple) 
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
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.selectByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire access control info. (Multiple) 
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
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappTaskTag) {				// ZappTaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보를 수정한다. (단건) 
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
			if(pObj instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObj;
				if(imgMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][Image] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObj;
				if(tagMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][Tag] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObj;
				if(taskTagMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][TaskTag] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uSingleRow] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Edit access control info. (Multiple) 
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
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				pObjRes.setResObj(imgMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				pObjRes.setResObj(tagMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				pObjRes.setResObj(taskTagMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Edit access control info. (Multiple) 
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
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				pObjRes.setResObj(imgMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				pObjRes.setResObj(tagMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				pObjRes.setResObj(taskTagMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Discard access control info. (Single) 
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
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.deleteByPrimaryKey(pvo.getAppid()));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.deleteByPrimaryKey(pvo.getTagid()));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(tagMapper.deleteByPrimaryKey(pvo.getTasktagid()));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dSingleRow] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Discard access control info. (Multiple) 
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
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.deleteByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Discard access control info. (Multiple) 
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
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTaskTag) {				// Tag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (다건) 
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
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTaskTag) {				// Tag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.countByDynamic(getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (다건) 
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
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTaskTag) {				// Tag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다.
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
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.exists(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.exists(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTaskTag) {				// Tag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.exists(getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다. 
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
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				pObjRes.setResObj(imgMapper.exists(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				pObjRes.setResObj(tagMapper.exists(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				pObjRes.setResObj(taskTagMapper.exists(getWhere(pObjf, pObjw)));
			}
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Register new access control info. (Single)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				pvo.setAppid(ZappKey.getPk(pvo));
				if(imgMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				pvo.setTagid(ZappKey.getPk(pvo));
				if(tagMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				pvo.setTasktagid(ZappKey.getPk(pvo));
				if(taskTagMapper.insert(pvo) < ONE) {
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
	 * Register new access control info. (Single)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cuSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				pvo.setAppid(ZappKey.getPk(pvo));
				if(imgMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				pvo.setTagid(ZappKey.getPk(pvo));
				if(tagMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				pvo.setTasktagid(ZappKey.getPk(pvo));
				if(taskTagMapper.insertu(pvo) < ONE) {
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
	 * 조직 정보를 저장한다. (다건)
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
				if(obj instanceof ZappImg) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappTag) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappTaskTag) {
					checkobj[2] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Image
				List<ZappImg> list = new ArrayList<ZappImg>();
				for(Object obj : oObjs) {
					ZappImg pvo = (ZappImg) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getAppid())) {
						pvo.setAppid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(imgMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[1] == true) {	// Tag
				List<ZappTag> list = new ArrayList<ZappTag>();
				for(Object obj : oObjs) {
					ZappTag pvo = (ZappTag) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getTagid())) {
						pvo.setTagid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(tagMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[2] == true) {	// TaskTag
				List<ZappTaskTag> list = new ArrayList<ZappTaskTag>();
				for(Object obj : oObjs) {
					ZappTaskTag pvo = (ZappTaskTag) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getTasktagid())) {
						pvo.setTasktagid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(taskTagMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Inquire access control info. (Single)
	 * @param pObjs - Search Criteria Object
	 * @return Object
	 */
	public Object rSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				return imgMapper.selectByPrimaryKey(pvo.getAppid());
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				return tagMapper.selectByPrimaryKey(pvo.getTagid());
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				return taskTagMapper.selectByPrimaryKey(pvo.getTasktagid());
			}

		}

		return null;
	}
	
	/**
	 * Inquire access control info. (Multiple)
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				return imgMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				return tagMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				return taskTagMapper.selectByDynamic(getWhere(null, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * Inquire access control info. (Multiple)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				return imgMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				return tagMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				return taskTagMapper.selectByDynamic(getWhere(pObjf, pvo));
			}

		}
		
		return null;
	}
	
	/**
	 * Edit access control info. (PK)
	 * @param pObj - Object to search
	 * @return boolean
	 */
	public boolean uSingleRow(ZappAuth pObjAuth, Object pObj) throws ZappException {

		if(pObj != null) {
			
			if(pObj instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObj;
				if(imgMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObj;
				if(tagMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObj;
				if(taskTagMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Edit access control info. (Multiple)
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				if(imgMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				if(tagMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				if(taskTagMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Edit access control info. (Multiple)
	 * @param pObjs - Values to edit
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjs;
				if(imgMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjs;
				if(tagMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjs;
				if(taskTagMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Discard access control info. (Single)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				if(imgMapper.deleteByPrimaryKey(pvo.getAppid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				if(tagMapper.deleteByPrimaryKey(pvo.getTagid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				if(taskTagMapper.deleteByPrimaryKey(pvo.getTasktagid()) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Discard access control info. (Multiple)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
				ZappImg pvo = (ZappImg) pObjw;
				if(imgMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTag) {				// Tag
				ZappTag pvo = (ZappTag) pObjw;
				if(tagMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				if(taskTagMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Discard access control info. (Multiple)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				if(imgMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				if(tagMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				if(taskTagMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (단건)
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				return imgMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				return tagMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				return taskTagMapper.countByDynamic(getWhere(null, pObjw));
			}

		}
		
		return ZERO;
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (단건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				return imgMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				return tagMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				return taskTagMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
		}
		
		return ZERO;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다.
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				return imgMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				return tagMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				return taskTagMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
		}
		
		return false;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다.
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappImg) {			// Image
//				ZappImg pvo = (ZappImg) pObjw;
				return imgMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappTag) {				// Tag
//				ZappTag pvo = (ZappTag) pObjw;
				return tagMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappTaskTag) {				// TaskTag
//				ZappTaskTag pvo = (ZappTaskTag) pObjw;
				return taskTagMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
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
		
		String ALIAS = BLANK;
		ZappDynamic dynamic = null;
		if(pobjw instanceof ZappImg) {
			ALIAS = ZappConts.ALIAS.IMG.alias;
		}
		if(pobjw instanceof ZappTag) {
			ALIAS = ZappConts.ALIAS.TAG.alias;
		}
		if(pobjw instanceof ZappTaskTag) {
			ALIAS = ZappConts.ALIAS.TASKTAG.alias;
		}
		
		try {
			dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(pobjf) : pobjf, pobjw, ALIAS);
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
	private ZstFwResult valid(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZappConts.ACTION pObjAct, ZstFwResult pObjRes) {
		
		switch(pObjAct) {
			case ADD: 
				if(utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cOrgan] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[cOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[cOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[cOrgan] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[cOrgan] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[cOrgan] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[cOrgan] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:
		}

		return pObjRes;
	}	
	
}

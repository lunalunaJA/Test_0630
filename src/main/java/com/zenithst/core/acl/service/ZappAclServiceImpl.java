package com.zenithst.core.acl.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.acl.bind.ZappAclBinder;
import com.zenithst.core.acl.mapper.ZappClassAclMapper;
import com.zenithst.core.acl.mapper.ZappContentAclMapper;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.bind.ZappDynamicBinder;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.content.bind.ZappContentBinder;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of access control info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    '@Autowired
*	 private ZappAclMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappAclService")
public class ZappAclServiceImpl extends ZappService implements ZappAclService {

	/* 
		[Mapper] 
	*/
	
	/* Classification access control */
	@Autowired
	private ZappClassAclMapper classAclMapper;			

	/* Content access control */
	@Autowired
	private ZappContentAclMapper contentAclMapper;		
	
	/* 
		[Binder] 
	*/

	@Autowired
	private ZappDynamicBinder dynamicBinder;
	@Autowired
	private ZappAclBinder utilBinder;
	@Autowired
	private ZappContentBinder utilBinder_content;
	
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
			if(pObjs instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(classAclMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][COMPANY] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(contentAclMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][DEPT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Register new organization. If it aready exists, it is edited. (Single)
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
			if(pObjs instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(classAclMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][COMPANY] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(contentAclMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][DEPT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	
	/**
	 * Register new organization. (One more)
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
				if(obj instanceof ZappClassAcl) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappContentAcl) {
					checkobj[1] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Classification access control
				List<ZappClassAcl> list = new ArrayList<ZappClassAcl>();
				for(Object obj : oObjs) {
					ZappClassAcl pvo = (ZappClassAcl) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getAclid())) {
						pvo.setAclid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(classAclMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][COMPANY] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[1] == true) {	// Content  access control
				List<ZappContentAcl> list = new ArrayList<ZappContentAcl>();
				for(Object obj : oObjs) {
					ZappContentAcl pvo = (ZappContentAcl) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getAclid())) {
						pvo.setAclid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(contentAclMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][DEPT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.selectByPrimaryKey(pvo.getAclid()));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.selectByPrimaryKey(pvo.getAclid()));
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.selectByDynamic(getWhere(null, pvo)));
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire extended access control info. (Multiple) 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiExtendRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiExtendRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire extended access control info. (Multiple) 
	 * @param pObjf - Filter Object 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiExtendRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiExtendRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	/**
	 * Inquire all access control info. of the corresponding content.
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiContetentRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				ZappClassObject pvo_class = new ZappClassObject();
				pvo_class.setCobjid(pvo.getContentid());
				pObjRes.setResObj(contentAclMapper.selectAllByContent(getWhere(null, pvo), getWhere(null, pvo_class)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiContetentRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Edit access control info. (Single) 
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
			if(pObj instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObj;
				if(classAclMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][COMPANY] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObj;
				if(contentAclMapper.updateByPrimaryKey(pvo) < ONE) {
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				pObjRes.setResObj(classAclMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				pObjRes.setResObj(contentAclMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				pObjRes.setResObj(classAclMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				pObjRes.setResObj(contentAclMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.deleteByPrimaryKey(pvo.getAclid()));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.deleteByPrimaryKey(pvo.getAclid()));
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.deleteByDynamic(getWhere(null, pvo)));
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire the count of access control info. (Multiple) 
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.countByDynamic(getWhere(null, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire the count of access control info. (Multiple)
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire whether to exist access control info.
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
			if(pObjw instanceof ZappClassAcl) {					// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire whether to exist access control info. 
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
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				pObjRes.setResObj(classAclMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				pObjRes.setResObj(contentAclMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
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
			
			if(pObjs instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(classAclMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(contentAclMapper.insert(pvo) < ONE) {
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
			
			if(pObjs instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(classAclMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				pvo.setAclid(ZappKey.getPk(pvo));
				if(contentAclMapper.insertu(pvo) < ONE) {
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
	 * Register new access control info. (Multiple)
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
				if(obj instanceof ZappClassAcl) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappContentAcl) {
					checkobj[1] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Classification access control
				List<ZappClassAcl> list = new ArrayList<ZappClassAcl>();
				for(Object obj : oObjs) {
					ZappClassAcl pvo = (ZappClassAcl) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getAclid())) {
						pvo.setAclid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(classAclMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[1] == true) {	// Content  access control
				List<ZappContentAcl> list = new ArrayList<ZappContentAcl>();
				for(Object obj : oObjs) {
					ZappContentAcl pvo = (ZappContentAcl) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getAclid())) {
						pvo.setAclid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(contentAclMapper.insertb(params) != oObjs.size()) {
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.selectByPrimaryKey(pvo.getAclid());
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.selectByPrimaryKey(pvo.getAclid());
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.selectByDynamic(getWhere(null, pvo));
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.selectByDynamic(getWhere(pObjf, pvo));
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
	public List<Object> rMultiExtendRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.selectExtendByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.selectExtendByDynamic(getWhere(null, pvo));
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
	public List<Object> rMultiExtendRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.selectExtendByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.selectExtendByDynamic(getWhere(pObjf, pvo));
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
			
			if(pObj instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObj;
				if(classAclMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObj;
				if(contentAclMapper.updateByPrimaryKey(pvo) < ONE) {
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				if(classAclMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				if(contentAclMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjs;
				if(classAclMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjs;
				if(contentAclMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				if(classAclMapper.deleteByPrimaryKey(pvo.getAclid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				if(contentAclMapper.deleteByPrimaryKey(pvo.getAclid()) < ONE) {
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				if(classAclMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				if(contentAclMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
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
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				if(classAclMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				if(contentAclMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Inquire the count of access control info.
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.countByDynamic(getWhere(null, pObjw));
			}

		}
		
		return ZERO;
	}
	
	/**
	 * Inquire the count of access control info.
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.countByDynamic(getWhere(pObjf, pObjw));
			}

		}
		
		return ZERO;
	}
	
	/**
	 * Inquire whether to exist access control info.
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}

		}
		
		return false;
	}
	
	/**
	 * Inquire whether to exist access control info.
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappClassAcl) {			// Classification access control
//				ZappClassAcl pvo = (ZappClassAcl) pObjw;
				return classAclMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappContentAcl) {				// Content  access control
//				ZappContentAcl pvo = (ZappContentAcl) pObjw;
				return contentAclMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
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
		if(pobjw instanceof ZappClassAcl) {
			ALIAS = ZappConts.ALIAS.CLASSACL.alias;
		}
		if(pobjw instanceof ZappContentAcl) {
			ALIAS = ZappConts.ALIAS.CONTENTACL.alias;
		}
		if(pobjw instanceof ZappClassObject) {
			ALIAS = ZappConts.ALIAS.CLASSOBJECT.alias;
		}
		
		try {
			if(pobjw instanceof ZappClassAcl) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappClassAcl()) : pobjf, pobjw, ALIAS);
			} 
			if(pobjw instanceof ZappContentAcl) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappContentAcl()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappClassObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder_content.getFilter(new ZappClassObject()) : pobjf, pobjw, ALIAS);
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
			case ADD: logger.info("[ADD] = " + pObjAct);
				if(utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[valid] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[valid] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[valid] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[valid] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[valid] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[valid] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW:  
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[valid] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:
		}

		return pObjRes;
	}	
	
}

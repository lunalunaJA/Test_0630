package com.zenithst.core.tag.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.archive.api.ZArchTaskMgtService;
import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.archive.vo.ZArchTask;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.service.ZappCommonService;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.core.tag.bind.ZappTagBinder;
import com.zenithst.core.tag.hash.ZappImgKey;
import com.zenithst.core.tag.service.ZappTagService;
import com.zenithst.core.tag.vo.ZappImg;
import com.zenithst.core.tag.vo.ZappImgExtend;
import com.zenithst.core.tag.vo.ZappTag;
import com.zenithst.core.tag.vo.ZappTaskTag;
import com.zenithst.core.tag.vo.ZappTaskTagExtend;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage tag info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappTagMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappTagMgtService")
public class ZappTagMgtServiceImpl extends ZappService implements ZappTagMgtService {


	@Autowired
	private ZappTagService tagService;
	
	@Autowired
	private ZappCommonService commonService;
	
	@Autowired
	private ZappMessageMgtService messageService;
	
	@Autowired
	private ZArchTaskMgtService zarchTaskMgtService;
	
	@Autowired
	private ZappTagBinder utilBinder;
	
//	@Value("#{zenithconfig['DYNAMIC_DB_SCHEME']}")
	private String DYNAMIC_DB_SCHEME;
	
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
			pObjRes = tagService.cMultiRows(pObjAuth, pObj, pObjRes);
		} 
		else {
			
			/* Validation */
			pObjRes = utilBinder.isEmpty(pObjAuth, pObj, pObjRes);
			
			/* Image */
			if(pObj instanceof ZappImg) {}
			
			/* Tag */
			if(pObj instanceof ZappTag) {}
			
			pObjRes = tagService.cSingleRow(pObjAuth, pObj, pObjRes);
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
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Image */
		if(pObj instanceof ZappImg) {}
		
		/* Tag */
		if(pObj instanceof ZappTag) {}
		
		pObjRes = tagService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
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
		
		/* Image */
		if(pObj instanceof ZappImg) {}
		
		/* Tag */
		if(pObj instanceof ZappTag) {}
		
		pObjRes = tagService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
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
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = tagService.dSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = tagService.dMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = tagService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = tagService.rMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Image */
		if(pObjw instanceof ZappImg) {}
		
		/* Tag */
		if(pObjw instanceof ZappTag) {}
		
		pObjRes = tagService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}	

	/* *********************************************************************************************** */
	
	public ZstFwResult mapTaskTag(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		return pObjRes;
		
	}
	
	@SuppressWarnings("unchecked")
	public ZstFwResult createDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/*  [ Validation ]
		 * 
		 */
		if(pObj == null) {
			return ZappFinalizing.finalising("누락_입력값", "[createDdl] " + messageService.getMessage("누락_입력값",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappImgExtend pInDdl = (ZappImgExtend) pObj;
		if(!ZstFwValidatorUtils.valid(pInDdl.getObjTaskid())) {
			return ZappFinalizing.finalising("누락_업무아이디", "[createDdl] " + messageService.getMessage("누락_업무아이디",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [기본 정보]
		 * 
		 */
		Map<String, Object> pObjParamMap = new HashMap<String, Object>();
		pObjParamMap.put("", ZappConts.TAGS.OBJTYPE_TABLE.tag);
		
		pInDdl.setObjSchema(DYNAMIC_DB_SCHEME);
		
		/* [업무정보조회]
		 * 
		 */
		pInDdl.setObjTaskcode(getTaskcode(pInDdl.getObjTaskid()));	// 업무코드
		
		/* [테이블 체크]
		 * 1. IMG table
		 * 2. IMGFILE table
		 * 3. IMGNOTE table
		 * 4. IMGORIGINAL table 
		 */
		
		boolean[] _EXIST_TABLE = {false, false, false, false};	// 0 : imgdoc, 1:imgfile, 2:imgnote, 3:imgoriginal
		pObjParamMap.put("", ZappConts.TAGS.TABLE_IMG.tag + "_" + pInDdl.getObjTaskcode());
		pObjRes = commonService.existDdl(pObjAuth, pObjParamMap, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) && ((Boolean) pObjRes.getResObj()) == true) {
			_EXIST_TABLE[0] = true;
		}
//		pObjParamMap.put("", ZappConts.TAGS.TABLE_IMGFILE.tag + "_" + pInDdl.getObjTaskcode());
//		pObjRes = commonService.existDdl(pObjAuth, pObjParamMap, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) && ((Boolean) pObjRes.getResObj()) == true) {
//			_EXIST_TABLE[1] = true;
//		}
//		pObjParamMap.put("", ZappConts.TAGS.TABLE_IMGNOTE.tag + "_" + pInDdl.getObjTaskcode());
//		pObjRes = commonService.existDdl(pObjAuth, pObjParamMap, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) && ((Boolean) pObjRes.getResObj()) == true) {
//			_EXIST_TABLE[2] = true;
//		}
//		pObjParamMap.put("", ZappConts.TAGS.TABLE_IMGORIGINAL.tag + "_" + pInDdl.getObjTaskcode());
//		pObjRes = commonService.existDdl(pObjAuth, pObjParamMap, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) && ((Boolean) pObjRes.getResObj()) == true) {
//			_EXIST_TABLE[3] = true;
//		}
		
		/* [테그 정보 조회]
		 * 
		 */
		ZappTaskTag pZappTaskTag = new ZappTaskTag();
		pZappTaskTag.setTaskid(pInDdl.getObjTaskid());		// Task ID
		List<ZappTaskTagExtend> rZappTaskTagList = null; 
		pObjRes = tagService.rMultiRows(pObjAuth, pZappTaskTag, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_TASKTAG", "[createDdl] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(rZappTaskTagList == null) {
			return ZappFinalizing.finalising("ERR_R_TASKTAG", "[createDdl] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappTaskTagList = (List<ZappTaskTagExtend>) pObjRes.getResObj();
		
		/* [동적컬럼생성]
		 * 
		 */
		List<ZappTaskTagExtend> pTagList = new ArrayList<ZappTaskTagExtend>();
		for(ZappTaskTagExtend vo : rZappTaskTagList) {
			vo.setObjComments(vo.getZappTag().getName());
			pTagList.add(vo);
		}
		pInDdl.setObjTags(popOthers(pTagList, pTagList.size()));	// 컬럼정보
		
		/* [테이블 생성]
		 * 1. IMG table
		 * 2. IMGFILE table 
		 * 3. IMGNOTE table
		 * 4. IMGORIGINAL table 
		 */
		if(_EXIST_TABLE[ZERO] == false) {
			pObjParamMap.put("", ZappConts.TAGS.TABLE_IMG.tag + "_" + pInDdl.getObjTaskcode());
			pObjRes = commonService.createDdl(pObjAuth, pObjParamMap, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) && ((Boolean) pObjRes.getResObj()) == false) {
				return ZappFinalizing.finalising("ERR_DDL", "[createDdl] " + messageService.getMessage("ERR_DDL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [인덱스 생성] - Img
		 * 1. Create time
		 * 2. 상태
		 * 3. 동적 컬럼
		 */
		pInDdl.setObjType(ZappConts.TAGS.OBJTYPE_INDEX.tag);	// Processing type
		
		// #1
		pInDdl.setObjTblname(ZappConts.TAGS.TABLE_IMG.tag + "_" + pInDdl.getObjTaskcode());
		pInDdl.setObjIdxname("I_" + ZappConts.TAGS.TABLE_IMG_ABBREVIATION.tag + "_" + pInDdl.getObjTaskcode() + "_A");
		if(commonService.existDdl(pObjAuth, pInDdl) == false) {
			pInDdl.setObjIsunique(ZappConts.USAGES.NO.use);
			pInDdl.setObjFieldname("CREATETIME");
			pInDdl.setObjType("IDX");
			if(commonService.createDdl(pObjAuth, pInDdl) == false) {
				return ZappFinalizing.finalising("ERR_C_INDEX", "[createDdl] " + messageService.getMessage("ERR_C_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		// #2
		pInDdl.setObjTblname(ZappConts.TAGS.TABLE_IMG.tag + "_" + pInDdl.getObjTaskcode());
		pInDdl.setObjIdxname("I_" + ZappConts.TAGS.TABLE_IMG_ABBREVIATION.tag + "_" + pInDdl.getObjTaskcode() + "_B");
		if(commonService.existDdl(pObjAuth, pInDdl) == false) {
			pInDdl.setObjIsunique(ZappConts.USAGES.NO.use);
			pInDdl.setObjFieldname("STATE");
			pInDdl.setObjType("IDX");
			if(commonService.createDdl(pObjAuth, pInDdl) == false) {
				return ZappFinalizing.finalising("ERR_C_INDEX", "[createDdl] " + messageService.getMessage("ERR_C_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		// #3
		for(ZappTaskTagExtend vo : rZappTaskTagList) {
			if(vo.getIscreatedindex().equals(ZappConts.USAGES.YES.use)) {
				vo.setObjTblname(ZappConts.TAGS.TABLE_IMG.tag + "_" + pInDdl.getObjTaskcode());
				if(vo.getIsunique().equals(ZappConts.USAGES.YES.use)) {
					vo.setObjIdxname("UI_" + ZappConts.TAGS.TABLE_IMG_ABBREVIATION.tag + "_" + pInDdl.getObjTaskcode() + "_" + vo.getSeqno());
				} else {
					vo.setObjIdxname("I_" + ZappConts.TAGS.TABLE_IMG_ABBREVIATION.tag + "_" + pInDdl.getObjTaskcode() + "_" + vo.getSeqno());
				}
				vo.setObjFieldname("IDX" + vo.getSeqno());
				
				pInDdl.setObjTblname(vo.getObjTblname());
				pInDdl.setObjIdxname(vo.getObjIdxname());
				pInDdl.setObjType("IDX");
				if(commonService.existDdl(pObjAuth, pInDdl) == false) {
					if(commonService.createDdl(pObjAuth, vo) == false) {
						return ZappFinalizing.finalising("ERR_C_INDEX", "[createDdl] " + messageService.getMessage("ERR_C_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				}
			}
		}
		
		/* [인덱스 생성] - ImgFile
		 * 1. APPID + DOCTYPE
		 * 2. ORIGINALAPPFILEID
		 */
		// #1
//		pInDdl.setObjTblname(ZappConts.TAGS.TABLE_IMGFILE.tag + "_" + pInDdl.getObjTaskcode());
//		pInDdl.setObjIdxname("I_" + ZappConts.TAGS.TABLE_IMGFILE_ABBREVIATION.tag + "_" + pInDdl.getObjTaskcode() + "_A");
//		if(commonService.existDdl(pInDdl) == false) {
//			pInDdl.setObjIsunique(ZappConts.STATE.SUSPENDING.state);
//			pInDdl.setObjFieldname("APPID, DOCTYPEID");
//			if(commonService.createIndex(pInDdl) == false) {
//				return ZappFinalizing.finalising(ZappConts.RESULTS.USER_DEFINITION_ERROR.result, "[FAIL] It is failed to create index. [" + pInDdl.getObjIdxname() + "]", BLANK);
//			}
//		}	
//		// #2
//		pInDdl.setObjTblname(ZappConts.TAGS.TABLE_IMGFILE.tag + "_" + pInDdl.getObjTaskcode());
//		pInDdl.setObjIdxname("I_" + ZappConts.TAGS.TABLE_IMGFILE_ABBREVIATION.tag + "_" + pInDdl.getObjTaskcode() + "_B");
//		if(commonService.existDdl(pInDdl) == false) {
//			pInDdl.setObjIsunique(ZappConts.STATE.SUSPENDING.state);
//			pInDdl.setObjFieldname("ORIGINALAPPFILEID");
//			if(commonService.createIndex(pInDdl) == false) {
//				return ZappFinalizing.finalising(ZappConts.RESULTS.USER_DEFINITION_ERROR.result, "[FAIL] It is failed to create index. [" + pInDdl.getObjIdxname() + "]", BLANK);
//			}
//		}		

		return pObjRes;
	}
	
	public ZstFwResult dropDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZappImgExtend pZappImgExtend = (ZappImgExtend) pObj;
		
		/* [데이타 체크]
		 * IMG 테이블에 데이타가 존재하는지 체크한다.
		 */
		if(tagService.rExist(pObjAuth, new ZappImg()) == true) {
			return ZappFinalizing.finalising("ERR_ALREADY_TAG", "[dropDdl] " + messageService.getMessage("ERR_ALREADY_TAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [테이블 삭제]
		 * 1. IMG table
		 * 2. IMGFILE table
		 * 3. IMGNOTE table
		 * 4. IMGORIGINAL table 
		 */
		ZappImgExtend pInDdl = new ZappImgExtend();
		pInDdl.setObjTaskcode(getTaskcode(pZappImgExtend.getObjTaskid()));		// 업무코드
		pInDdl.setObjTblname(ZappConts.TAGS.TABLE_IMG.tag + "_" + pInDdl.getObjTaskcode());			// img
		pInDdl.setObjType("TBL");
		if(commonService.existDdl(pObjAuth, pInDdl) == true) {
			if(commonService.dropDdl(pObjAuth, pInDdl) == false) {
				return ZappFinalizing.finalising("ERR_D_DDL", "[dropDdl] " + messageService.getMessage("ERR_D_DDL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		return pObjRes;
	}
	
	@SuppressWarnings("unchecked")
	public ZstFwResult setDynamicValidator(ZappAuth pObjAuth, Object pObjImg, Object pObjTags,  ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjImg == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[setDynamicValidator] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		ZappImgExtend pZappImgExtend = (ZappImgExtend) pObjImg;
		
		/* [테그 정보 조회]
		 * 
		 */
		List<ZappTaskTagExtend> rZappTaskTagList = null; 
		if(pObjTags == null) {
			ZappTaskTag pZappTaskTag = new ZappTaskTag();
			pZappTaskTag.setTaskid(pZappImgExtend.getObjTaskid());		// Task ID
			pObjRes = tagService.rMultiRows(pObjAuth, pZappTaskTag, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_TASKTAG", "[setDynamicValidator] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappTaskTagList = (List<ZappTaskTagExtend>) pObjRes.getResObj();
		} else {
			rZappTaskTagList = (List<ZappTaskTagExtend>) pObjTags;
		}
		if(rZappTaskTagList == null) {
			return ZappFinalizing.finalising("ERR_R_TASKTAG", "[setDynamicValidator] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [ Validation ]
		 * 
		 */
		for(ZappTaskTagExtend vo : rZappTaskTagList) {
			if(vo.getIsallowednull().equals(ZappConts.USAGES.NO.use)) {
				int _IDX_SEQ = ZstFwValidatorUtils.fixNullInt(vo.getSeqno(), ZERO);
				switch(_IDX_SEQ) {
					case 1: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx01())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 01 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 2: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx02())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 02 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 3: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx03())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 03 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 4: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx04())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 04 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 5: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx05())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 05 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 6: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx06())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 06 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 7: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx07())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 07 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 8: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx08())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 08 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 9: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx09())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 09 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 10: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx10())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 10 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 11: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx11())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 11 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 12: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx12())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 12 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 13: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx13())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 13 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 14: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx14())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 14 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 15: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx15())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 15 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 16: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx16())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 16 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 17: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx17())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 17 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 18: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx18())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 18 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 19: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx19())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 19 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					case 20: if(!ZstFwValidatorUtils.valid(pZappImgExtend.getIdx20())) 
						return ZappFinalizing.finalising("ERR_MIS_INDEX", "[setDynamicValidator] 20 " + messageService.getMessage("ERR_MIS_INDEX",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					break;
					default:
				}
			}
		}		
		
		return pObjRes;
	}
	
	@SuppressWarnings("unchecked")
	public ZstFwResult setDynamicKey(ZappAuth pObjAuth, Object pObjImg, Object pObjTags, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		if(pObjImg == null && pObjTags == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[setDynamicValidator] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		ZappImgExtend pZappImgExtend = null;
		if(pObjImg != null) {
			pZappImgExtend = (ZappImgExtend) pObjImg;
		}
		
		/* [테그 정보 조회]
		 * 
		 */
		List<ZappTaskTagExtend> rZappTaskTagList = null; 
		if(pObjTags == null) {
			ZappTaskTag pZappTaskTag = new ZappTaskTag();
			pZappTaskTag.setTaskid(pZappImgExtend.getObjTaskid());		// Task ID
			pObjRes = tagService.rMultiRows(pObjAuth, pZappTaskTag, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_TASKTAG", "[setDynamicKey] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappTaskTagList = (List<ZappTaskTagExtend>) pObjRes.getResObj();
		} else {
			rZappTaskTagList = (List<ZappTaskTagExtend>) pObjTags;
		}
		if(rZappTaskTagList == null) {
			return ZappFinalizing.finalising("ERR_R_TASKTAG", "[setDynamicKey] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [결과]
		 * 
		 */
		pObjRes.setResObj(ZappImgKey.getPk(pZappImgExtend, rZappTaskTagList));
		
		return pObjRes;
	}
	
	@SuppressWarnings("unchecked")
	public ZstFwResult setDynamicFilter(ZappAuth pObjAuth, Object pObjImg, Object pObjTags, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, ZappImg> mFilter = new HashMap<String, ZappImg>();
		
		ZappImgExtend pZappImgExtend = null;
		if(pObjImg != null) {
			pZappImgExtend = (ZappImgExtend) pObjImg;
		}
		
		/* [테그 정보 조회]
		 * 
		 */
		List<ZappTaskTagExtend> rZappTaskTagList = null; 
		if(pObjTags == null) {
			ZappTaskTag pZappTaskTag = new ZappTaskTag();
			pZappTaskTag.setTaskid(pZappImgExtend.getObjTaskid());		// Task ID
			pObjRes = tagService.rMultiRows(pObjAuth, pZappTaskTag, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_TASKTAG", "[setDynamicKey] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappTaskTagList = (List<ZappTaskTagExtend>) pObjRes.getResObj();
		} else {
			rZappTaskTagList = (List<ZappTaskTagExtend>) pObjTags;
		}
		if(rZappTaskTagList == null) {
			return ZappFinalizing.finalising("ERR_R_TASKTAG", "[setDynamicKey] " + messageService.getMessage("ERR_R_TASKTAG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [필터]
		 * 
		 */
		ZappImg pZappImg_Filter = new ZappImg();
		for(ZappTaskTagExtend vo : rZappTaskTagList) {
			int _IDX_SEQ = ZstFwValidatorUtils.fixNullInt(vo.getSeqno(), ZERO);
			String _QRY_TYPE = mapFilter(ZstFwValidatorUtils.fixNullString(vo.getQuerytype(), BLANK));
			switch(_IDX_SEQ) {
				case 1: pZappImg_Filter.setIdx01(_QRY_TYPE); break;
				case 2: pZappImg_Filter.setIdx02(_QRY_TYPE); break;
				case 3: pZappImg_Filter.setIdx03(_QRY_TYPE); break;
				case 4: pZappImg_Filter.setIdx04(_QRY_TYPE); break;
				case 5: pZappImg_Filter.setIdx05(_QRY_TYPE); break;
				case 6: pZappImg_Filter.setIdx06(_QRY_TYPE); break;
				case 7: pZappImg_Filter.setIdx07(_QRY_TYPE); break;
				case 8: pZappImg_Filter.setIdx08(_QRY_TYPE); break;
				case 9: pZappImg_Filter.setIdx09(_QRY_TYPE); break;
				case 10: pZappImg_Filter.setIdx10(_QRY_TYPE); break;
				case 11: pZappImg_Filter.setIdx11(_QRY_TYPE); break;
				case 12: pZappImg_Filter.setIdx12(_QRY_TYPE); break;
				case 13: pZappImg_Filter.setIdx13(_QRY_TYPE); break;
				case 14: pZappImg_Filter.setIdx14(_QRY_TYPE); break;
				case 15: pZappImg_Filter.setIdx15(_QRY_TYPE); break;
				case 16: pZappImg_Filter.setIdx16(_QRY_TYPE); break;
				case 17: pZappImg_Filter.setIdx17(_QRY_TYPE); break;
				case 18: pZappImg_Filter.setIdx18(_QRY_TYPE); break;
				case 19: pZappImg_Filter.setIdx19(_QRY_TYPE); break;
				case 20: pZappImg_Filter.setIdx20(_QRY_TYPE); break;
				default:
			}
		}
		
		/* [조회값]
		 * 
		 */
		ZappImg pZappImg_Value = new ZappImg();
		BeanUtils.copyProperties(pZappImgExtend, pZappImg_Value);
		
		/* [값설정]
		 * 
		 */
		mFilter.put(String.valueOf(ONE), pZappImg_Filter);
		mFilter.put(String.valueOf(TWO), pZappImg_Value);
		
		pObjRes.setResObj(mFilter);
		
		return pObjRes;
	}
	
	/* ********************************************************************************************************** */
	
	/**
	 * 
	 * @param qrytype
	 * @return
	 */
	private String mapFilter(String qrytype) {
		
		String filter = BLANK;
		for(Operators op : Operators.values()) {
			if(qrytype.equals(op.operator)) {
				filter = op.operator;
				break;
			}
		}
		
		return filter;
		
	}
	
	private List<ZappTaskTagExtend> popOthers(List<ZappTaskTagExtend> pIn, int pLen) {
		
		if(pIn != null) {
			
			for(int idx = (pLen+1); idx < 21; idx++) {
				ZappTaskTagExtend vo = new ZappTaskTagExtend();
				vo.setSeqno(String.format("%02d", idx));
				vo.setDatalength(ONE);
				vo.setIsallowednull(ZappConts.USAGES.YES.use);
				vo.setDefaultvalue(null);
				vo.setObjComments(BLANK);
				pIn.add(vo);
			}
		}
		
		return pIn;
	}
	
	/**
	 * 업무코를 조회한다.
	 * @param pTaskid
	 * @return
	 */
	public String getTaskcode(String pTaskid) {
		
		ZArchTask rZArchTask = null;
		try {
			ZArchResult rZArchResult = zarchTaskMgtService.loadTask(new ZArchTask(pTaskid));
			if(rZArchResult != null) {
				rZArchTask = (ZArchTask) rZArchResult.getResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(rZArchTask == null) {
			return BLANK;
		}

		return rZArchTask.getCode();
	}
	

}

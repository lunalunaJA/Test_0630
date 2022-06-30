package com.zenithst.core.content.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchVersion;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.bind.ZappClassificationBinder;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.bind.ZappDynamicBinder;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.content.bind.ZappContentBinder;
import com.zenithst.core.content.mapper.ZappAdditoryBundleMapper;
import com.zenithst.core.content.mapper.ZappBundleMapper;
import com.zenithst.core.content.mapper.ZappClassObjectMapper;
import com.zenithst.core.content.mapper.ZappCommentMapper;
import com.zenithst.core.content.mapper.ZappContentMapper;
import com.zenithst.core.content.mapper.ZappContentWorkflowMapper;
import com.zenithst.core.content.mapper.ZappFileMapper;
import com.zenithst.core.content.mapper.ZappKeywordMapper;
import com.zenithst.core.content.mapper.ZappKeywordObjectMapper;
import com.zenithst.core.content.mapper.ZappLinkedObjectMapper;
import com.zenithst.core.content.mapper.ZappLockedObjectMapper;
import com.zenithst.core.content.mapper.ZappMarkedObjectMapper;
import com.zenithst.core.content.mapper.ZappSharedObjectMapper;
import com.zenithst.core.content.mapper.ZappTmpObjectMapper;
import com.zenithst.core.content.vo.ZappAdditoryBundle;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappComment;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.content.vo.ZappContentWorkflow;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.content.vo.ZappKeywordObject;
import com.zenithst.core.content.vo.ZappLinkedObject;
import com.zenithst.core.content.vo.ZappLockedObject;
import com.zenithst.core.content.vo.ZappMarkedObject;
import com.zenithst.core.content.vo.ZappSharedObject;
import com.zenithst.core.content.vo.ZappTmpObject;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of content info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappContentService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappContentService")
public class ZappContentServiceImpl extends ZappService implements ZappContentService {


	/* Mapper */
	@Autowired
	private ZappBundleMapper bundleMapper;				// Bundle
	@Autowired
	private ZappAdditoryBundleMapper additoryBundleMapper;		// 추가 Bundle
	@Autowired
	private ZappClassObjectMapper classObjectMapper;	// 분류객체
	@Autowired
	private ZappLinkedObjectMapper linkedObjectMapper;	// 링크객체
	@Autowired
	private ZappSharedObjectMapper sharedObjectMapper;	// 공유객체
	@Autowired
	private ZappLockedObjectMapper lockedObjectMapper;	// 잠금객체
	@Autowired
	private ZappTmpObjectMapper tmpObjectMapper;		// 임시객체
	@Autowired
	private ZappFileMapper mFileMapper;					// 파일확장
	@Autowired
	private ZappContentMapper contentMapper;			// 컨텐츠
	@Autowired
	private ZappKeywordMapper keywordMapper;			// Keyword
	@Autowired
	private ZappKeywordObjectMapper keywordObjectMapper;		// 키워드객체
	@Autowired
	private ZappMarkedObjectMapper markedObjectMapper;		// 책갈피
	@Autowired
	private ZappContentWorkflowMapper contentWorkflowMapper;		// Content-Workflow
	@Autowired
	private ZappCommentMapper commentMapper;		// Comment
	
	/* Binder */
	@Autowired
	private ZappDynamicBinder dynamicBinder;
	@Autowired
	private ZappContentBinder utilBinder;
	@Autowired
	private ZappClassificationBinder utilClassBinder;
	
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
			if(pObjs instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				pvo.setBundleid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappBundle(pvo.getBundleid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][BUNDLE] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				if(bundleMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][BUNDLE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
//				pvo.setBundleid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappAdditoryBundle(pvo.getBundleid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][ADDITORYBUNDLE] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				if(additoryBundleMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ADDITORYBUNDLE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}			
			if(pObjs instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				pvo.setClassobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappClassObject(pvo.getClassobjid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][CLASSOBJECT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				if(classObjectMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CLASSOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				pvo.setLinkerid(pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setLinktime(pObjAuth.getObjTime());
				pvo.setLinkedobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappLinkedObject(pvo.getLinkedobjid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][LINKEDOBJECT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				if(linkedObjectMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][LINKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				pvo.setSharerid(pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setSharetime(pObjAuth.getObjTime());
				pvo.setShareobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappSharedObject(pvo.getShareobjid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][SHAREDOBJECT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				if(sharedObjectMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][SHAREDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				pvo.setLockerid(pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setLocktime(pObjAuth.getObjTime());
				pvo.setLockobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappLockedObject(pvo.getLockobjid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][LOCKEDOBJECT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				if(lockedObjectMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][LOCKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				pvo.setTmpobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappTmpObject(pvo.getTmpobjid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][SHAREDOBJECT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				if(tmpObjectMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][SHAREDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappFile) {			// 파일확장
				ZappFile pvo = (ZappFile) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappFile(pvo.getMfileid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][MFILE] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
				
				if(mFileMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][MFILE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappKeyword) {			// Keyword
				ZappKeyword pvo = (ZappKeyword) pObjs;
				pvo.setKwordid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappKeyword(pvo.getKwordid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][KEYWORD] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
				
				if(keywordMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][KEYWORD] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappKeywordObject) {			// Keyword 객체
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				pvo.setKwobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappKeywordObject(pvo.getKwobjid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][KEYWORDOBJECT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
				
				if(keywordObjectMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][KEYWORDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappMarkedObject) {			// Keyword 객체
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				pvo.setMarkedobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappMarkedObject(pvo.getMarkedobjid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][MARKEDOBJECT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}				
				
				if(markedObjectMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][MARKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappContentWorkflow) {	// Content-Workflow
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjs;
				pvo.setWftime(ZstFwValidatorUtils.valid(pvo.getWftime()) ? pvo.getWftime() : ZstFwDateUtils.getNow());
				pvo.setCwfid(ZappKey.getPk(pvo));

				if(contentWorkflowMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CONTENTWORKFLOW] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappComment) {	// Comment
				ZappComment pvo = (ZappComment) pObjs;
				pvo.setCommenttime(ZstFwValidatorUtils.valid(pvo.getCommenttime()) ? pvo.getCommenttime() : ZstFwDateUtils.getNow());
				pvo.setCommenterid(pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setCommenter(pObjAuth.getSessDeptUser().getZappUser().getName() + " [" + pObjAuth.getSessDeptUser().getZappDept().getName() + "]");
				pvo.setCommentid(ZappKey.getPk(pvo));

				if(commentMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][COMMENT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보를 저장한다. (단건 중복 처리)
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
			if(pObjs instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				pvo.setBundleid(ZappKey.getPk(pvo));
				if(bundleMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][BUNDLE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
				if(additoryBundleMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][BUNDLE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				pvo.setClassobjid(ZappKey.getPk(pvo));
				if(classObjectMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][CLASSOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				pvo.setLinkedobjid(ZappKey.getPk(pvo));
				if(linkedObjectMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][LINKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				pvo.setShareobjid(ZappKey.getPk(pvo));
				if(sharedObjectMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][SHAREDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				pvo.setLockobjid(ZappKey.getPk(pvo));
				if(lockedObjectMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][LOCKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				pvo.setTmpobjid(ZappKey.getPk(pvo));
				if(tmpObjectMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][SHAREDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappFile) {			// 파일확장
				ZappFile pvo = (ZappFile) pObjs;
				if(mFileMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][MFILE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappKeyword) {			// Keyword
				ZappKeyword pvo = (ZappKeyword) pObjs;
				if(keywordMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][KEYWORD] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappKeywordObject) {			// Keyword 객체
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				if(keywordObjectMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][KEYWORDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappMarkedObject) {			// 책갈피
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				if(markedObjectMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][MARKEDBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappContentWorkflow) {	// Content-Workflow
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjs;
				pvo.setWftime(ZstFwValidatorUtils.valid(pvo.getWftime()) ? pvo.getWftime() : ZstFwDateUtils.getNow());
				pvo.setCwfid(ZappKey.getPk(pvo));

				if(contentWorkflowMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cuSingleRow][CONTENTWORKFLOW] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappComment) {	// Comment
				ZappComment pvo = (ZappComment) pObjs;
				pvo.setCommenttime(ZstFwValidatorUtils.valid(pvo.getCommenttime()) ? pvo.getCommenttime() : ZstFwDateUtils.getNow());
				pvo.setCommenterid(pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setCommenter(pObjAuth.getSessDeptUser().getZappUser().getName() + " [" + pObjAuth.getSessDeptUser().getZappDept().getName() + "]");
				pvo.setCommentid(ZappKey.getPk(pvo));

				if(commentMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cuSingleRow][COMMENT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}			

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	
	/**
	 * 조직 정보를 저장한다. (단건 중복 처리)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult ceSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}

			if(pObjs instanceof ZappKeyword) {			// Keyword
				ZappKeyword pvo = (ZappKeyword) pObjs;
				if(keywordMapper.inserte(pvo) < ZERO) {
					return ZappFinalizing.finalising("ERR_C", "[ceSingleRow][KEYWORD] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}

			if(pObjs instanceof ZappKeywordObject) {			// Keyword 객체
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				if(keywordObjectMapper.inserte(pvo) < ZERO) {
					return ZappFinalizing.finalising("ERR_C", "[ceSingleRow][KEYWORDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			boolean[] checkobj = {false, false, false, false, false, false, false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappBundle) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappClassObject) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappLinkedObject) {
					checkobj[2] = true;
					break;
				}
				if(obj instanceof ZappSharedObject) {
					checkobj[3] = true;
					break;
				}
				if(obj instanceof ZappTmpObject) {
					checkobj[4] = true;
					break;
				}
				if(obj instanceof ZappFile) {
					checkobj[5] = true;
					break;
				}
				if(obj instanceof ZappLockedObject) {
					checkobj[6] = true;
					break;
				}
				if(obj instanceof ZappKeyword) {
					checkobj[7] = true;
					break;
				}
				if(obj instanceof ZappKeywordObject) {
					checkobj[8] = true;
					break;
				}
				if(obj instanceof ZappMarkedObject) {
					checkobj[9] = true;
					break;
				}
				if(obj instanceof ZappAdditoryBundle) {
					checkobj[10] = true;
					break;
				}
				if(obj instanceof ZappContentWorkflow) {
					checkobj[11] = true;
					break;
				}
				if(obj instanceof ZappComment) {
					checkobj[12] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Bundle
				List<ZappBundle> list = new ArrayList<ZappBundle>();
				for(Object obj : oObjs) {
					ZappBundle pvo = (ZappBundle) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getBundleid())) {
						pvo.setBundleid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(bundleMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][BUNDLE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[1] == true) {	// 분류객체
				List<ZappClassObject> list = new ArrayList<ZappClassObject>();
				for(Object obj : oObjs) {
					ZappClassObject pvo = (ZappClassObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getClassobjid())) {
						pvo.setClassobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(classObjectMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][CLASSOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[2] == true) {	// 링크객체
				List<ZappLinkedObject> list = new ArrayList<ZappLinkedObject>();
				for(Object obj : oObjs) {
					ZappLinkedObject pvo = (ZappLinkedObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLinkedobjid())) {
						pvo.setLinkedobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(linkedObjectMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][LINKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[3] == true) {	// 공유객체
				List<ZappSharedObject> list = new ArrayList<ZappSharedObject>();
				for(Object obj : oObjs) {
					ZappSharedObject pvo = (ZappSharedObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getShareobjid())) {
						pvo.setShareobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(sharedObjectMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][SHAREDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[4] == true) {	// 임시객체
				List<ZappTmpObject> list = new ArrayList<ZappTmpObject>();
				for(Object obj : oObjs) {
					ZappTmpObject pvo = (ZappTmpObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getTmpobjid())) {
						pvo.setTmpobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(tmpObjectMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][TMPOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[5] == true) {	// 임시객체
				List<ZappFile> list = new ArrayList<ZappFile>();
				for(Object obj : oObjs) {
					ZappFile pvo = (ZappFile) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(mFileMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][MFILE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[6] == true) {	// 잠금객체
				List<ZappLockedObject> list = new ArrayList<ZappLockedObject>();
				for(Object obj : oObjs) {
					ZappLockedObject pvo = (ZappLockedObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLockobjid())) {
						pvo.setLockobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(lockedObjectMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][LOCKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[7] == true) {	
				List<ZappKeyword> list = new ArrayList<ZappKeyword>();
				for(Object obj : oObjs) {
					ZappKeyword pvo = (ZappKeyword) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getKwordid())) {
						pvo.setIsactive(YES);
						pvo.setKwordid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(keywordMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][KEYWORD] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[8] == true) {	
				List<ZappKeywordObject> list = new ArrayList<ZappKeywordObject>();
				for(Object obj : oObjs) {
					ZappKeywordObject pvo = (ZappKeywordObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getKwobjid())) {
						pvo.setKwobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(keywordObjectMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][KEYWORDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[9] == true) {	
				List<ZappMarkedObject> list = new ArrayList<ZappMarkedObject>();
				for(Object obj : oObjs) {
					ZappMarkedObject pvo = (ZappMarkedObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getMarkedobjid())) {
						pvo.setMarkedobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(markedObjectMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][MARKEDOBJECT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[10] == true) {	
				List<ZappAdditoryBundle> list = new ArrayList<ZappAdditoryBundle>();
				for(Object obj : oObjs) {
					ZappAdditoryBundle pvo = (ZappAdditoryBundle) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(additoryBundleMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][ADDITORYBUNDLE] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[11] == true) {	// Content-Workflow
				List<ZappContentWorkflow> list = new ArrayList<ZappContentWorkflow>();
				for(Object obj : oObjs) {
					ZappContentWorkflow pvo = (ZappContentWorkflow) obj;
					pvo.setWftime(ZstFwValidatorUtils.valid(pvo.getWftime()) ? pvo.getWftime() : ZstFwDateUtils.getNow());
					if(!ZstFwValidatorUtils.valid(pvo.getCwfid())) {
						pvo.setCwfid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(contentWorkflowMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][CONTENTWORKFLOW] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[12] == true) {	// Comment
				List<ZappComment> list = new ArrayList<ZappComment>();
				for(Object obj : oObjs) {
					ZappComment pvo = (ZappComment) obj;
					pvo.setCommenttime(ZstFwValidatorUtils.valid(pvo.getCommenttime()) ? pvo.getCommenttime() : ZstFwDateUtils.getNow());
					pvo.setCommenterid(pObjAuth.getSessDeptUser().getDeptuserid());
					pvo.setCommenter(pObjAuth.getSessDeptUser().getZappUser().getName() + " [" + pObjAuth.getSessDeptUser().getZappDept().getName() + "]");
					if(!ZstFwValidatorUtils.valid(pvo.getCommentid())) {
						pvo.setCommentid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(commentMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][COMMENT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.selectByPrimaryKey(pvo.getBundleid()));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.selectByPrimaryKey(pvo.getBundleid()));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.selectByPrimaryKey(pvo.getClassobjid()));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.selectByPrimaryKey(pvo.getLinkedobjid()));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.selectByPrimaryKey(pvo.getShareobjid()));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.selectByPrimaryKey(pvo.getLockobjid()));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.selectByPrimaryKey(pvo.getTmpobjid()));
			}
			if(pObjw instanceof ZappFile) {			// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.selectByPrimaryKey(pvo.getMfileid()));
			}
			if(pObjw instanceof ZappKeyword) {			
				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.selectByPrimaryKey(pvo.getKwordid()));
			}
			if(pObjw instanceof ZappKeywordObject) {			
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.selectByPrimaryKey(pvo.getKwobjid()));
			}
			if(pObjw instanceof ZappMarkedObject) {			
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.selectByPrimaryKey(pvo.getMarkedobjid()));
			}
			if(pObjw instanceof ZappContentWorkflow) {			
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.selectByPrimaryKey(pvo.getCwfid()));
			}
			if(pObjw instanceof ZappComment) {			
				ZappComment pvo = (ZappComment) pObjw;
				pObjRes.setResObj(commentMapper.selectByPrimaryKey(pvo.getCommentid()));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappFile) {		// 파일
				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.selectByDynamic(getWhere(null, pvo)));
			}			
			if(pObjw instanceof ZappContentWorkflow) {			
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappComment) {			
				ZappComment pvo = (ZappComment) pObjw;
				pObjRes.setResObj(commentMapper.selectByDynamic(getWhere(null, pvo)));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}			
			if(pObjw instanceof ZappContentWorkflow) {			
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappComment) {			
				ZappComment pvo = (ZappComment) pObjw;
				pObjRes.setResObj(commentMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}				
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	
	/**
	 * Inquire access control info. (Multiple) 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException {

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappKeywordObject) {	
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappMarkedObject) {	
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappContentWorkflow) {			
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
//			if(pObjw instanceof ZappComment) {			
//				ZappComment pvo = (ZappComment) pObjw;
//				pObjRes.setResObj(commentMapper.selectExtendByDynamic(getWhere(null, pvo)));
//			}		
			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRowsExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Inquire access control info. (Multiple) 
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
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappKeywordObject) {	
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappMarkedObject) {	
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}			
			if(pObjw instanceof ZappContentWorkflow) {			
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRowsExtend] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			if(pObj instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObj;
				if(bundleMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][BUNDLE] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObj;
				if(additoryBundleMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][ADDITORYBUNDLE] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObj;
				if(classObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][CLASSOBJECT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObj;
				if(linkedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][LINKEDOBJECT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObj;
				if(sharedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][SHAREDOBJECT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappLockedObject) {		// 공유객체
				ZappLockedObject pvo = (ZappLockedObject) pObj;
				if(lockedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][LOCKEDOBJECT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappTmpObject) {		//  임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObj;
				if(tmpObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][TMPOBJECT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappFile) {		//  임시객체
				ZappFile pvo = (ZappFile) pObj;
				if(mFileMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][MFILE] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObj;
				if(keywordMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][KEYWORD] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObj;
				if(keywordObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][KEYWORDOBJECT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}	
			if(pObj instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObj;
				if(markedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][MARKEDOBJECT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObj;
				if(contentWorkflowMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][CONTENTWORKFLOW] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObj;
				if(commentMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][COMMENT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				pObjRes.setResObj(bundleMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
				pObjRes.setResObj(additoryBundleMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				pObjRes.setResObj(classObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				pObjRes.setResObj(linkedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				pObjRes.setResObj(sharedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				pObjRes.setResObj(lockedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				pObjRes.setResObj(tmpObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjs;
				pObjRes.setResObj(mFileMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjs;
				pObjRes.setResObj(keywordMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappKeywordObject) {	
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				pObjRes.setResObj(keywordObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappMarkedObject) {	
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				pObjRes.setResObj(markedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}			
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				pObjRes.setResObj(commentMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				pObjRes.setResObj(bundleMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
				pObjRes.setResObj(additoryBundleMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				pObjRes.setResObj(classObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				pObjRes.setResObj(linkedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				pObjRes.setResObj(sharedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				pObjRes.setResObj(lockedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				pObjRes.setResObj(tmpObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjs;
				pObjRes.setResObj(mFileMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjs;
				pObjRes.setResObj(keywordMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				pObjRes.setResObj(keywordObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				pObjRes.setResObj(markedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				pObjRes.setResObj(commentMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.deleteByPrimaryKey(pvo.getBundleid()));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.deleteByPrimaryKey(pvo.getBundleid()));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.deleteByPrimaryKey(pvo.getClassobjid()));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.deleteByPrimaryKey(pvo.getLinkedobjid()));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.deleteByPrimaryKey(pvo.getShareobjid()));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.deleteByPrimaryKey(pvo.getLockobjid()));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.deleteByPrimaryKey(pvo.getTmpobjid()));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.deleteByPrimaryKey(pvo.getMfileid()));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.deleteByPrimaryKey(pvo.getKwordid()));
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.deleteByPrimaryKey(pvo.getKwobjid()));
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.deleteByPrimaryKey(pvo.getMarkedobjid()));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.deleteByPrimaryKey(pvo.getCwfid()));
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				pObjRes.setResObj(commentMapper.deleteByPrimaryKey(pvo.getCommentid()));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				pObjRes.setResObj(commentMapper.deleteByDynamic(getWhere(null, pvo)));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappKeyword) {	
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappKeywordObject) {	
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappMarkedObject) {	
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.deleteByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappComment) {		
//				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				pObjRes.setResObj(commentMapper.deleteByDynamic(getWhere(null, pObjw)));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappKeyword) {		
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappKeywordObject) {		
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappMarkedObject) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappComment) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(commentMapper.countByDynamic(getWhere(null, pObjw)));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappKeyword) {		
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappKeywordObject) {		
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappMarkedObject) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappComment) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(commentMapper.countByDynamic(getWhere(pObjf, pObjw)));
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
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappKeyword) {		
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappKeywordObject) {		
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappMarkedObject) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappComment) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(commentMapper.exists(getWhere(null, pObjw)) == null ? false : true);
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
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				pObjRes.setResObj(bundleMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				pObjRes.setResObj(additoryBundleMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				pObjRes.setResObj(classObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				pObjRes.setResObj(linkedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				pObjRes.setResObj(sharedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				pObjRes.setResObj(lockedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				pObjRes.setResObj(tmpObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				pObjRes.setResObj(mFileMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappKeyword) {	
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				pObjRes.setResObj(keywordMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappKeywordObject) {	
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				pObjRes.setResObj(keywordObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappMarkedObject) {	
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(markedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappContentWorkflow) {	
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(contentWorkflowMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappComment) {	
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				pObjRes.setResObj(commentMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
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
			
			if(pObjs instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				pvo.setBundleid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappBundle(pvo.getBundleid())) == true) {
					return false;
				}
				
				if(bundleMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappBundle(pvo.getBundleid())) == true) {
					return false;
				}
				
				if(additoryBundleMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				pvo.setClassobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappClassObject(pvo.getClassobjid())) == true) {
					return false;
				}
				
				if(classObjectMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				pvo.setLinkedobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappLinkedObject(pvo.getLinkedobjid())) == true) {
					return false;
				}
				
				if(linkedObjectMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				pvo.setShareobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappSharedObject(pvo.getShareobjid())) == true) {
					return false;
				}
				
				if(sharedObjectMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				pvo.setLockobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappLockedObject(pvo.getLockobjid())) == true) {
					return false;
				}
				
				if(lockedObjectMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				pvo.setTmpobjid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappTmpObject(pvo.getTmpobjid())) == true) {
					return false;
				}
				
				if(tmpObjectMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappFile(pvo.getMfileid())) == true) {
					return false;
				}
				
				if(mFileMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappKeyword(pvo.getKwordid())) == true) {
					return false;
				}
				
				if(keywordMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappKeywordObject(pvo.getKwobjid())) == true) {
					return false;
				}
				
				if(keywordObjectMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				
				// Exist
				if(rExist(pObjAuth, new ZappMarkedObject(pvo.getMarkedobjid())) == true) {
					return false;
				}
				
				if(markedObjectMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjs;
				pvo.setWftime(ZstFwValidatorUtils.valid(pvo.getWftime()) ? pvo.getWftime() : ZstFwDateUtils.getNow());
				pvo.setCwfid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappContentWorkflow(pvo.getCwfid())) == true) {
					return false;
				}
				
				if(contentWorkflowMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjs;
				pvo.setCommenttime(ZstFwValidatorUtils.valid(pvo.getCommenttime()) ? pvo.getCommenttime() : ZstFwDateUtils.getNow());
				pvo.setCommenterid(pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setCommenter(pObjAuth.getSessDeptUser().getZappUser().getName() + " [" + pObjAuth.getSessDeptUser().getZappDept().getName() + "]");
				pvo.setCommentid(ZappKey.getPk(pvo));

				
				// Exist
				if(rExist(pObjAuth, new ZappComment(pvo.getCommentid())) == true) {
					return false;
				}
				
				if(commentMapper.insert(pvo) < ONE) {
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
	 * 조직 정보를 저장한다. (단건 중복 처리)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cuSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException {

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				pvo.setBundleid(ZappKey.getPk(pvo));
				if(bundleMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
				if(additoryBundleMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				pvo.setClassobjid(ZappKey.getPk(pvo));
				if(classObjectMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				pvo.setLinkedobjid(ZappKey.getPk(pvo));
				if(linkedObjectMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				pvo.setShareobjid(ZappKey.getPk(pvo));
				if(sharedObjectMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				pvo.setLockobjid(ZappKey.getPk(pvo));
				if(lockedObjectMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				pvo.setTmpobjid(ZappKey.getPk(pvo));
				if(tmpObjectMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjs;
				if(mFileMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappKeyword) {	
				ZappKeyword pvo = (ZappKeyword) pObjs;
				if(keywordMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappKeywordObject) {	
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				if(keywordObjectMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappMarkedObject) {	
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				if(markedObjectMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}			
			if(pObjs instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjs;
				pvo.setWftime(ZstFwValidatorUtils.valid(pvo.getWftime()) ? pvo.getWftime() : ZstFwDateUtils.getNow());
				pvo.setCwfid(ZappKey.getPk(pvo));
				
				if(contentWorkflowMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjs;
				pvo.setCommenttime(ZstFwValidatorUtils.valid(pvo.getCommenttime()) ? pvo.getCommenttime() : ZstFwDateUtils.getNow());
				pvo.setCommenterid(pObjAuth.getSessDeptUser().getDeptuserid());
				pvo.setCommenter(pObjAuth.getSessDeptUser().getZappUser().getName() + " [" + pObjAuth.getSessDeptUser().getZappDept().getName() + "]");
				pvo.setCommentid(ZappKey.getPk(pvo));
				
				if(commentMapper.insertu(pvo) < ONE) {
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
			boolean[] checkobj = {false, false, false, false, false, false, false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappBundle) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappClassObject) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappLinkedObject) {
					checkobj[2] = true;
					break;
				}
				if(obj instanceof ZappSharedObject) {
					checkobj[3] = true;
					break;
				}
				if(obj instanceof ZappTmpObject) {
					checkobj[4] = true;
					break;
				}
				if(obj instanceof ZappFile) {
					checkobj[5] = true;
					break;
				}
				if(obj instanceof ZappLockedObject) {
					checkobj[6] = true;
					break;
				}
				if(obj instanceof ZappKeyword) {
					checkobj[7] = true;
					break;
				}
				if(obj instanceof ZappKeywordObject) {
					checkobj[8] = true;
					break;
				}
				if(obj instanceof ZappMarkedObject) {
					checkobj[9] = true;
					break;
				}
				if(obj instanceof ZappAdditoryBundle) {
					checkobj[10] = true;
					break;
				}
				if(obj instanceof ZappContentWorkflow) {
					checkobj[11] = true;
					break;
				}
				if(obj instanceof ZappComment) {
					checkobj[12] = true;
					break;
				}				
			}
			
			if(checkobj[0] == true) {	// Bundle
				List<ZappBundle> list = new ArrayList<ZappBundle>();
				for(Object obj : oObjs) {
					ZappBundle pvo = (ZappBundle) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getBundleid())) {
						pvo.setBundleid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(bundleMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[1] == true) {	// 분류객체
				List<ZappClassObject> list = new ArrayList<ZappClassObject>();
				for(Object obj : oObjs) {
					ZappClassObject pvo = (ZappClassObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getClassobjid())) {
						pvo.setClassobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(classObjectMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[2] == true) {	// 링크객체
				List<ZappLinkedObject> list = new ArrayList<ZappLinkedObject>();
				for(Object obj : oObjs) {
					ZappLinkedObject pvo = (ZappLinkedObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getLinkedobjid())) {
						pvo.setLinkedobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(linkedObjectMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[3] == true) {	// 공유객체
				List<ZappSharedObject> list = new ArrayList<ZappSharedObject>();
				for(Object obj : oObjs) {
					ZappSharedObject pvo = (ZappSharedObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getShareobjid())) {
						pvo.setShareobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(sharedObjectMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[4] == true) {	// 임시객체
				List<ZappTmpObject> list = new ArrayList<ZappTmpObject>();
				for(Object obj : oObjs) {
					ZappTmpObject pvo = (ZappTmpObject) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getTmpobjid())) {
						pvo.setTmpobjid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(tmpObjectMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[5] == true) {	// 임시객체
				List<ZappFile> list = new ArrayList<ZappFile>();
				for(Object obj : oObjs) {
					ZappFile pvo = (ZappFile) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(mFileMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[6] == true) {	// 잠금객체
				List<ZappLockedObject> list = new ArrayList<ZappLockedObject>();
				for(Object obj : oObjs) {
					ZappLockedObject pvo = (ZappLockedObject) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(lockedObjectMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[7] == true) {	
				List<ZappKeyword> list = new ArrayList<ZappKeyword>();
				for(Object obj : oObjs) {
					ZappKeyword pvo = (ZappKeyword) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(keywordMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[8] == true) {	
				List<ZappKeywordObject> list = new ArrayList<ZappKeywordObject>();
				for(Object obj : oObjs) {
					ZappKeywordObject pvo = (ZappKeywordObject) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(keywordObjectMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[9] == true) {	
				List<ZappMarkedObject> list = new ArrayList<ZappMarkedObject>();
				for(Object obj : oObjs) {
					ZappMarkedObject pvo = (ZappMarkedObject) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(markedObjectMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[10] == true) {	
				List<ZappAdditoryBundle> list = new ArrayList<ZappAdditoryBundle>();
				for(Object obj : oObjs) {
					ZappAdditoryBundle pvo = (ZappAdditoryBundle) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(additoryBundleMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[11] == true) {	
				List<ZappContentWorkflow> list = new ArrayList<ZappContentWorkflow>();
				for(Object obj : oObjs) {
					ZappContentWorkflow pvo = (ZappContentWorkflow) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(contentWorkflowMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[12] == true) {	
				List<ZappComment> list = new ArrayList<ZappComment>();
				for(Object obj : oObjs) {
					ZappComment pvo = (ZappComment) obj;
					list.add(pvo);
				}
				params.put("batch", list);
				if(commentMapper.insertb(params) != oObjs.size()) {
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				return bundleMapper.selectByPrimaryKey(pvo.getBundleid());
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				return additoryBundleMapper.selectByPrimaryKey(pvo.getBundleid());
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				return classObjectMapper.selectByPrimaryKey(pvo.getClassobjid());
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				return linkedObjectMapper.selectByPrimaryKey(pvo.getLinkedobjid());
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				return sharedObjectMapper.selectByPrimaryKey(pvo.getShareobjid());
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				return lockedObjectMapper.selectByPrimaryKey(pvo.getLockobjid());
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				return tmpObjectMapper.selectByPrimaryKey(pvo.getTmpobjid());
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				return mFileMapper.selectByPrimaryKey(pvo.getMfileid());
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				return keywordMapper.selectByPrimaryKey(pvo.getKwordid());
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				return keywordObjectMapper.selectByPrimaryKey(pvo.getKwobjid());
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return markedObjectMapper.selectByPrimaryKey(pvo.getMarkedobjid());
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				return contentWorkflowMapper.selectByPrimaryKey(pvo.getCwfid());
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				return commentMapper.selectByPrimaryKey(pvo.getCommentid());
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				return bundleMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				return additoryBundleMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				return classObjectMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				return linkedObjectMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				return sharedObjectMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				return lockedObjectMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				return tmpObjectMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				return mFileMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				return keywordMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				return keywordObjectMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return markedObjectMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				return contentWorkflowMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				return commentMapper.selectByDynamic(getWhere(null, pvo));
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				return bundleMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				return additoryBundleMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				return classObjectMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				return linkedObjectMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				return sharedObjectMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				return lockedObjectMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				return tmpObjectMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappFile) {		// 임시시객체
				ZappFile pvo = (ZappFile) pObjw;
				return mFileMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				return keywordMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				return keywordObjectMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return markedObjectMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				return contentWorkflowMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				return commentMapper.selectByDynamic(getWhere(pObjf, pvo));
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
			
			if(pObj instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObj;
				if(bundleMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObj;
				if(additoryBundleMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObj;
				if(classObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObj;
				if(linkedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObj;
				if(sharedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObj;
				if(lockedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObj;
				if(tmpObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObj;
				if(mFileMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObj;
				if(keywordMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObj;
				if(keywordObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObj;
				if(markedObjectMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObj;
				if(contentWorkflowMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObj;
				if(commentMapper.updateByPrimaryKey(pvo) < ONE) {
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				if(bundleMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
				if(additoryBundleMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				if(classObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				if(linkedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				if(sharedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				if(lockedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				if(tmpObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjs;
				if(mFileMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeyword) {	
				ZappKeyword pvo = (ZappKeyword) pObjs;
				if(keywordMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeywordObject) {	
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				if(keywordObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappMarkedObject) {	
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				if(markedObjectMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentWorkflow) {	
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjs;
				if(contentWorkflowMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappComment) {	
				ZappComment pvo = (ZappComment) pObjs;
				if(commentMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjs;
				if(bundleMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjs;
				if(additoryBundleMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjs;
				if(classObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjs;
				if(linkedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjs;
				if(sharedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjs;
				if(lockedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjs;
				if(tmpObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjs;
				if(mFileMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjs;
				if(keywordMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjs;
				if(keywordObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjs;
				if(markedObjectMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjs;
				if(contentWorkflowMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjs;
				if(commentMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				if(bundleMapper.deleteByPrimaryKey(pvo.getBundleid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				if(additoryBundleMapper.deleteByPrimaryKey(pvo.getBundleid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				if(classObjectMapper.deleteByPrimaryKey(pvo.getClassobjid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				if(linkedObjectMapper.deleteByPrimaryKey(pvo.getLinkedobjid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				if(sharedObjectMapper.deleteByPrimaryKey(pvo.getShareobjid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				if(lockedObjectMapper.deleteByPrimaryKey(pvo.getLockobjid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				if(tmpObjectMapper.deleteByPrimaryKey(pvo.getTmpobjid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				if(mFileMapper.deleteByPrimaryKey(pvo.getMfileid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				if(keywordMapper.deleteByPrimaryKey(pvo.getKwordid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				if(keywordObjectMapper.deleteByPrimaryKey(pvo.getKwobjid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				if(markedObjectMapper.deleteByPrimaryKey(pvo.getMarkedobjid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				if(contentWorkflowMapper.deleteByPrimaryKey(pvo.getCwfid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				if(commentMapper.deleteByPrimaryKey(pvo.getCommentid()) < ONE) {
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
				ZappBundle pvo = (ZappBundle) pObjw;
				if(bundleMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				if(additoryBundleMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
				ZappClassObject pvo = (ZappClassObject) pObjw;
				if(classObjectMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				if(linkedObjectMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				if(sharedObjectMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				if(lockedObjectMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				if(tmpObjectMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
				ZappFile pvo = (ZappFile) pObjw;
				if(mFileMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeyword) {		
				ZappKeyword pvo = (ZappKeyword) pObjw;
				if(keywordMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeywordObject) {		
				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				if(keywordObjectMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappMarkedObject) {		
				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				if(markedObjectMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentWorkflow) {		
				ZappContentWorkflow pvo = (ZappContentWorkflow) pObjw;
				if(contentWorkflowMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappComment) {		
				ZappComment pvo = (ZappComment) pObjw;
				if(commentMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				if(bundleMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				if(additoryBundleMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				if(classObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				if(linkedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				if(sharedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				if(lockedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				if(tmpObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				if(mFileMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeyword) {		
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				if(keywordMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappKeywordObject) {		
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				if(keywordObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappMarkedObject) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				if(markedObjectMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				if(contentWorkflowMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappComment) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				if(commentMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				return bundleMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				return additoryBundleMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				return classObjectMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				return linkedObjectMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				return sharedObjectMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				return lockedObjectMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				return tmpObjectMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				return mFileMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappKeyword) {		
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				return keywordMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappKeywordObject) {		
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				return keywordObjectMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappMarkedObject) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return markedObjectMapper.countByDynamic(getWhere(null, pObjw));
			}			
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return contentWorkflowMapper.countByDynamic(getWhere(null, pObjw));
			}	
			if(pObjw instanceof ZappComment) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return commentMapper.countByDynamic(getWhere(null, pObjw));
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				return bundleMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				return additoryBundleMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				return classObjectMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				return linkedObjectMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				return sharedObjectMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				return lockedObjectMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				return tmpObjectMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				return mFileMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappKeyword) {	
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				return keywordMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappKeywordObject) {	
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				return keywordObjectMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappMarkedObject) {	
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return markedObjectMapper.countByDynamic(getWhere(pObjf, pObjw));
			}			
			if(pObjw instanceof ZappContentWorkflow) {	
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return contentWorkflowMapper.countByDynamic(getWhere(pObjf, pObjw));
			}			
			if(pObjw instanceof ZappComment) {	
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return commentMapper.countByDynamic(getWhere(pObjf, pObjw));
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				return bundleMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				return additoryBundleMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				return classObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				return linkedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				return sharedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				return lockedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				return tmpObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				return mFileMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappKeyword) {		
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				return keywordMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappKeywordObject) {		
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				return keywordObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappMarkedObject) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return markedObjectMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}			
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return contentWorkflowMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}		
			if(pObjw instanceof ZappComment) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return commentMapper.exists(getWhere(null, pObjw)) == null ? false : true;
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
			
			if(pObjw instanceof ZappBundle) {			// Bundle
//				ZappBundle pvo = (ZappBundle) pObjw;
				return bundleMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappAdditoryBundle) {			// Bundle
//				ZappAdditoryBundle pvo = (ZappAdditoryBundle) pObjw;
				return additoryBundleMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappClassObject) {				// 분류객체
//				ZappClassObject pvo = (ZappClassObject) pObjw;
				return classObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappLinkedObject) {			// 링크객체
//				ZappLinkedObject pvo = (ZappLinkedObject) pObjw;
				return linkedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappSharedObject) {		// 공유객체
//				ZappSharedObject pvo = (ZappSharedObject) pObjw;
				return sharedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappLockedObject) {		// 잠금객체
//				ZappLockedObject pvo = (ZappLockedObject) pObjw;
				return lockedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappTmpObject) {		// 임시객체
//				ZappTmpObject pvo = (ZappTmpObject) pObjw;
				return tmpObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappFile) {		// 임시객체
//				ZappFile pvo = (ZappFile) pObjw;
				return mFileMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}		
			if(pObjw instanceof ZappKeyword) {		
//				ZappKeyword pvo = (ZappKeyword) pObjw;
				return keywordMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappKeywordObject) {		
//				ZappKeywordObject pvo = (ZappKeywordObject) pObjw;
				return keywordObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappMarkedObject) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return markedObjectMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappContentWorkflow) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return contentWorkflowMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappComment) {		
//				ZappMarkedObject pvo = (ZappMarkedObject) pObjw;
				return commentMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}			
		}
		
		return false;
	}
	
	/* ******************************************************************************** */
	
	public ZstFwResult rContent(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(ZstFwValidatorUtils.valid(pObjw.getObjType()) && ZstFwValidatorUtils.valid(pObjw.getContentid())) {
			pObjRes.setResObj(contentMapper.selectContent(pObjAuth, pObjw));
		} else {
			pObjRes.setResObj(null);
		}
		
		return pObjRes;
	}

	public ZstFwResult rPhysicalCount(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException {
		
		int CNT = ZERO;
		
		if(pObjw != null) {
		
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			ZappEnv SYS_LIST_QUERY_OBJECT = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_QUERY_OBJECT.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
			
			/* Default */
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);			// 권한적용함
			}
			if(SYS_LIST_QUERY_OBJECT == null) {
				SYS_LIST_QUERY_OBJECT = new ZappEnv(); SYS_LIST_QUERY_OBJECT.setSetval("A");	// 전체
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			/* 조회 범위 */
			boolean MANUAL_TYPE = false;
			ZappEnv SYS_LIST_QUERY_OBJECT_MANUAL = null;
			if(ZstFwValidatorUtils.valid(pObjw.getObjQueryType()) == true) {
				MANUAL_TYPE = true;
				SYS_LIST_QUERY_OBJECT_MANUAL = new ZappEnv();
				SYS_LIST_QUERY_OBJECT_MANUAL.setSetval(pObjw.getObjQueryType());
			}
			
			/* Acl */
			boolean SKIP_CONTENTACL = false;
			if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_LINK.type) 
					|| pObjw.getObjType().equals(ZappConts.TYPES.LIST_SHARE.type) 
					|| pObjw.getObjType().equals(ZappConts.TYPES.LIST_LOCK.type)
					|| pObjw.getObjSkipAcl() == true) {
				SKIP_CONTENTACL = true;
			}
		
			CNT = contentMapper.selectPhysicalCount(pObjAuth
													 , SKIP_CONTENTACL == true ? null : SYS_CONTENTACL_YN
													 , MANUAL_TYPE == true ? SYS_LIST_QUERY_OBJECT_MANUAL : SYS_LIST_QUERY_OBJECT
													 , SYS_DEPT_RANGE
													 , (pObjw.getZappBundle() != null) ? getWhere(pObjf == null ? new ZappBundle() : pObjf.getZappBundle(), pObjw.getZappBundle()) : null
													 , (pObjw.getZappFile() != null) ? getWhere(pObjf == null ? new ZappFile() : pObjf.getZappFile(), pObjw.getZappFile()) : null
//													 , (pObjw.getzArchMFile() != null) ? getWhere(pObjf == null ? new ZArchMFile() : pObjf.getzArchMFile(), pObjw.getzArchMFile()) : null
													 , (pObjw.getzArchMFile() != null) ? pObjw.getzArchMFile() : null
													 , (pObjw.getZappClassification() != null) ? getWhere(pObjf == null ? new ZappClassification() : pObjf.getZappClassification(), pObjw.getZappClassification()) : null
//													 , (pObjw.getZappClassObject() != null) ? getWhere(pObjf == null ? new ZappClassObject() : pObjf.getZappClassObject(), pObjw.getZappClassObject()) : null
													 , (pObjw.getZappLinkedObject() != null) ? getWhere(pObjf == null ? new ZappLinkedObject() : pObjf.getZappLinkedObject(), pObjw.getZappLinkedObject()) : null
													 , (pObjw.getZappSharedObject() != null) ? getWhere(pObjf == null ? new ZappSharedObject() : pObjf.getZappSharedObject(), pObjw.getZappSharedObject()) : null
													 , (pObjw.getZappLockedObject() != null) ? getWhere(pObjf == null ? new ZappLockedObject() : pObjf.getZappLockedObject(), pObjw.getZappLockedObject()) : null
													 , (pObjw.getZappKeyword() != null) ? getWhere(pObjf == null ? new ZappKeyword() : pObjf.getZappKeyword(), pObjw.getZappKeyword()) : null
													 , (pObjw.getZappMarkedObject() != null) ? getWhere(pObjf == null ? new ZappMarkedObject() : pObjf.getZappMarkedObject(), pObjw.getZappMarkedObject()) : null
													 , (pObjw.getZappAdditoryBundle() != null) ? getWhere(pObjf == null ? new ZappAdditoryBundle() : pObjf.getZappAdditoryBundle(), pObjw.getZappAdditoryBundle()) : null		 
													  );
		
		}
		
		pObjRes.setResObj(CNT);
		
		return pObjRes;
	}
	
	public ZstFwResult rPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			ZappEnv SYS_LIST_QUERY_OBJECT = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_QUERY_OBJECT.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
		
			/* Default */
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
			}
			if(SYS_LIST_QUERY_OBJECT == null) {
				SYS_LIST_QUERY_OBJECT = new ZappEnv(); SYS_LIST_QUERY_OBJECT.setSetval("A");	// 전체
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			/* 조회 범위 */
			boolean MANUAL_TYPE = false;
			ZappEnv SYS_LIST_QUERY_OBJECT_MANUAL = null;
			if(ZstFwValidatorUtils.valid(pObjw.getObjQueryType()) == true) {
				MANUAL_TYPE = true;
				SYS_LIST_QUERY_OBJECT_MANUAL = new ZappEnv();
				SYS_LIST_QUERY_OBJECT_MANUAL.setSetval(pObjw.getObjQueryType());
			}
			
			/* Acl */
			boolean SKIP_CONTENTACL = false;
			if(pObjw.getObjType().equals(ZappConts.TYPES.LIST_LINK.type) 
					|| pObjw.getObjType().equals(ZappConts.TYPES.LIST_SHARE.type) 
					|| pObjw.getObjType().equals(ZappConts.TYPES.LIST_LOCK.type)
					|| pObjw.getObjSkipAcl() == true) {
				SKIP_CONTENTACL = true;
			}
			
			/* Sorting */
			if(pObjw.getObjmaporder() == null) {
				pObjw.setObjmaporder(new HashMap<String, String>());
				pObjw.getObjmaporder().put("CREATETIME", "DESC");
			}
			if(pObjw.getObjmaporder().size() == ZERO) {
				pObjw.setObjmaporder(new HashMap<String, String>());
				pObjw.getObjmaporder().put("CREATETIME", "DESC");
			}
			
			pObjRes.setResObj(contentMapper.selectPhysicalList(pObjAuth
															 , new ZappQryOpt(pObjAuth, pObjw.getObjnumperpg(), pObjw.getObjpgnum(), pObjw.getObjmaporder())
															 , SKIP_CONTENTACL == true ? null : SYS_CONTENTACL_YN
															 , MANUAL_TYPE == true ? SYS_LIST_QUERY_OBJECT_MANUAL : SYS_LIST_QUERY_OBJECT
															 , SYS_DEPT_RANGE
															 , (pObjw.getZappBundle() != null) ? getWhere(pObjf == null ? new ZappBundle() : pObjf.getZappBundle(), pObjw.getZappBundle()) : null
															 , (pObjw.getZappFile() != null) ? getWhere(pObjf == null ? new ZappFile() : pObjf.getZappFile(), pObjw.getZappFile()) : null
//															 , (pObjw.getzArchMFile() != null) ? getWhere(pObjf == null ? new ZArchMFile() : pObjf.getzArchMFile(), pObjw.getzArchMFile()) : null
															 , (pObjw.getzArchMFile() != null) ? pObjw.getzArchMFile() : null
															 , (pObjw.getZappClassification() != null) ? getWhere(pObjf == null ? new ZappClassification() : pObjf.getZappClassification(), pObjw.getZappClassification()) : null
//															 , (pObjw.getZappClassObject() != null) ? getWhere(pObjf == null ? new ZappClassObject() : pObjf.getZappClassObject(), pObjw.getZappClassObject()) : null
															 , (pObjw.getZappLinkedObject() != null) ? getWhere(pObjf == null ? new ZappLinkedObject() : pObjf.getZappLinkedObject(), pObjw.getZappLinkedObject()) : null
															 , (pObjw.getZappSharedObject() != null) ? getWhere(pObjf == null ? new ZappSharedObject() : pObjf.getZappSharedObject(), pObjw.getZappSharedObject()) : null
															 , (pObjw.getZappLockedObject() != null) ? getWhere(pObjf == null ? new ZappLockedObject() : pObjf.getZappLockedObject(), pObjw.getZappLockedObject()) : null
															 , (pObjw.getZappKeyword() != null) ? getWhere(pObjf == null ? new ZappKeyword() : pObjf.getZappKeyword(), pObjw.getZappKeyword()) : null
															 , (pObjw.getZappMarkedObject() != null) ? getWhere(pObjf == null ? new ZappMarkedObject() : pObjf.getZappMarkedObject(), pObjw.getZappMarkedObject()) : null				 
															 , (pObjw.getZappAdditoryBundle() != null) ? getWhere(pObjf == null ? new ZappAdditoryBundle() : pObjf.getZappAdditoryBundle(), pObjw.getZappAdditoryBundle()) : null		 

														  ));
		
		}
		
		return pObjRes;
	}
	
	public ZstFwResult rNonPhysicalCount(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException {
		
		int CNT = ZERO;
		
		if(pObjw != null) {
		
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			ZappEnv SYS_LIST_QUERY_OBJECT = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_QUERY_OBJECT.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
		
			/* Default */
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
			}
			if(SYS_LIST_QUERY_OBJECT == null) {
				SYS_LIST_QUERY_OBJECT = new ZappEnv(); SYS_LIST_QUERY_OBJECT.setSetval("A");	// 전체
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			/* 조회 범위 */
			boolean MANUAL_TYPE = false;
			ZappEnv SYS_LIST_QUERY_OBJECT_MANUAL = null;
			if(ZstFwValidatorUtils.valid(pObjw.getObjQueryType()) == true) {
				MANUAL_TYPE = true;
				SYS_LIST_QUERY_OBJECT_MANUAL = new ZappEnv();
				SYS_LIST_QUERY_OBJECT_MANUAL.setSetval(pObjw.getObjQueryType());
			}			
			
			/* 권한 제외 */
			boolean SKIP_CONTENTACL = false;
			if(pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_OWN.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_SELF_ADD.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_COMING_EXPIRE.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_EXPIRE.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_BIN.type) 
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_EXPIRE_ADMIN.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_APPROVED.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_RETURNED.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_OBJECT.type)) {
				SKIP_CONTENTACL = true;
			}
			
			CNT = contentMapper.selectNonPhysicalCount_(pObjAuth
													 , SKIP_CONTENTACL == true ? null : SYS_CONTENTACL_YN
													 , MANUAL_TYPE == true ? SYS_LIST_QUERY_OBJECT_MANUAL : SYS_LIST_QUERY_OBJECT
													 , SYS_DEPT_RANGE
													 , (pObjw.getZappBundle() != null) ? getWhere(pObjf == null ? new ZappBundle() : pObjf.getZappBundle(), pObjw.getZappBundle()) : null
													 , (pObjw.getZappFile() != null) ? getWhere(pObjf == null ? new ZappFile() : pObjf.getZappFile(), pObjw.getZappFile()) : null
													 , (pObjw.getzArchMFile() != null) ? pObjw.getzArchMFile() : null
//													 , (pObjw.getzArchMFile() != null) ? getWhere(pObjf == null ? new ZArchMFile() : pObjf.getzArchMFile(), pObjw.getzArchMFile()) : null
													 , (pObjw.getZappKeyword() != null) ? getWhere(pObjf == null ? new ZappKeyword() : pObjf.getZappKeyword(), pObjw.getZappKeyword()) : null
													 , (pObjw.getZappAdditoryBundle() != null) ? getWhere(pObjf == null ? new ZappAdditoryBundle() : pObjf.getZappAdditoryBundle(), pObjw.getZappAdditoryBundle()) : null		 
													  );
		
		}
		
		pObjRes.setResObj(CNT);
		
		return pObjRes;
	}
	
	public ZstFwResult rNonPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException {
		
	
		if(pObjw != null) {
		
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			ZappEnv SYS_LIST_QUERY_OBJECT = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_QUERY_OBJECT.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
			pObjAuth.setObjHandleType(pObjw.getObjHandleType());	// Processing type					
		
			/* Default */
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
			}
			if(SYS_LIST_QUERY_OBJECT == null) {
				SYS_LIST_QUERY_OBJECT = new ZappEnv(); SYS_LIST_QUERY_OBJECT.setSetval("A");	// 전체
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			/* 조회 범위 */
			boolean MANUAL_TYPE = false;
			ZappEnv SYS_LIST_QUERY_OBJECT_MANUAL = null;
			if(ZstFwValidatorUtils.valid(pObjw.getObjQueryType()) == true) {
				MANUAL_TYPE = true;
				SYS_LIST_QUERY_OBJECT_MANUAL = new ZappEnv();
				SYS_LIST_QUERY_OBJECT_MANUAL.setSetval(pObjw.getObjQueryType());
			}			
			
			/* 권한 제외 */
			boolean SKIP_CONTENTACL = false;
			if(pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_OWN.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_SELF_ADD.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_COMING_EXPIRE.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_EXPIRE.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_BIN.type) 
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_DISCARD_ADMIN.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_EXPIRE_ADMIN.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_APPROVED.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_RETURNED.type)
					|| pObjAuth.getObjHandleType().equals(ZappConts.TYPES.LIST_APPROVAL_OBJECT.type)) {
				SKIP_CONTENTACL = true;
			}
			
			/* Sorting */
			if(pObjw.getObjmaporder() == null) {
				pObjw.setObjmaporder(new HashMap<String, String>());
				pObjw.getObjmaporder().put("CREATETIME", "DESC");
			}
			if(pObjw.getObjmaporder().size() == ZERO) {
				pObjw.setObjmaporder(new HashMap<String, String>());
				pObjw.getObjmaporder().put("CREATETIME", "DESC");
			}
			
			logger.info("NP================================================================");
			logger.info("SKIP_CONTENTACL = [" + SKIP_CONTENTACL + "]");
			logger.info("SYS_CONTENTACL_YN = [" + SYS_CONTENTACL_YN.getSetval() + "]");
			logger.info("getObjSkipAcl() = [" + pObjw.getObjSkipAcl() + "]");
			logger.info("==================================================================");
			
			pObjRes.setResObj(contentMapper.selectNonPhysicalList_(pObjAuth
																 , new ZappQryOpt(pObjAuth, pObjw.getObjnumperpg(), pObjw.getObjpgnum(), pObjw.getObjmaporder())
																 , SKIP_CONTENTACL == true ? null : SYS_CONTENTACL_YN
																 , MANUAL_TYPE == true ? SYS_LIST_QUERY_OBJECT_MANUAL : SYS_LIST_QUERY_OBJECT
																 , SYS_DEPT_RANGE
																 , (pObjw.getZappBundle() != null) ? getWhere(pObjf == null ? new ZappBundle() : pObjf.getZappBundle(), pObjw.getZappBundle()) : null
																 , (pObjw.getZappFile() != null) ? getWhere(pObjf == null ? new ZappFile() : pObjf.getZappFile(), pObjw.getZappFile()) : null
																 , (pObjw.getzArchMFile() != null) ? pObjw.getzArchMFile() : null
//																 , (pObjw.getzArchMFile() != null) ? getWhere(pObjf == null ? new ZArchMFile() : pObjf.getzArchMFile(), pObjw.getzArchMFile()) : null
																 , (pObjw.getZappKeyword() != null) ? getWhere(pObjf == null ? new ZappKeyword() : pObjf.getZappKeyword(), pObjw.getZappKeyword()) : null
																 , (pObjw.getZappAdditoryBundle() != null) ? getWhere(pObjf == null ? new ZappAdditoryBundle() : pObjf.getZappAdditoryBundle(), pObjw.getZappAdditoryBundle()) : null		 
																  ));
		
		}
		
		return pObjRes;
	}	
	
	public ZstFwResult rMultiRowsFileName(ZappAuth pObjAuth, ZArchMFile pObjFile, ZappClassification pObjClass, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjFile != null && pObjClass != null) {
			pObjRes.setResObj(mFileMapper.selectByFilename(pObjAuth, pObjFile, pObjClass));
		} else {
			return ZappFinalizing.finalising("ERR_MIS_FILENAME", "[rMultiRowsFileName][FILE] " + messageService.getMessage("ERR_MIS_FILENAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	public ZstFwResult rMultiRowsMaxVersionFile(ZappAuth pObjAuth, ZArchMFile pObjFile, ZArchVersion pObjVersion, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjVersion != null || pObjFile != null) {
			pObjRes.setResObj(mFileMapper.selectByMaxVersion(pObjAuth, pObjFile, pObjVersion));
		} else {
			return ZappFinalizing.finalising("ERR_R_VERSION", "[rMultiRowsMaxVersionFile][FILE] " + messageService.getMessage("ERR_R_VERSION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	public ZstFwResult rMultiRowsVersion(ZappAuth pObjAuth, ZArchVersion pObjVersion, ZArchFile pObjFile, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjVersion != null || pObjFile != null) {
			pObjRes.setResObj(mFileMapper.selectByVersion(pObjAuth, pObjVersion, pObjFile));
		} else {
			return ZappFinalizing.finalising("오류_조회_버전", "[rMultiRowsVersion][FILE] " + messageService.getMessage("오류_조회_버전",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/* ******************************************************************************** */
	/* FTR 																				*/
	/* ******************************************************************************** */
	/**
	 * <pre>
	 * Create FTR Temporary Table
	 * </pre>
	 * @param pObjAuth
	 * @param pObjRes
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	public ZstFwResult cFTRTbl(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		contentMapper.createTmpFTRTbl(params);
		
		return pObjRes;
	}

	/**
	 * <pre>
	 * Drop FTR Temporary Table
	 * </pre>
	 * @param pObjAuth
	 * @param pObjRes
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	public ZstFwResult dFTRTbl(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		contentMapper.dropTmpFTRTbl(params);
		
		return pObjRes;
	}

	/**
	 * <pre>
	 * Insert FTR Data
	 * </pre>
	 * @param pObjAuth
	 * @param pObj
	 * @param pObjRes
	 * @see ZappAuth
	 * @see ZstFwResult
	 */
	public ZstFwResult cFTRRows(ZappAuth pObjAuth, List<ZappContentRes> pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObj == null) {
			return ZappFinalizing.finalising("오류_조회_버전", "[cFTRRows] " + messageService.getMessage("오류_조회_버전",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(pObj.size() == ZERO) {
			return ZappFinalizing.finalising("오류_조회_버전", "[cFTRRows] " + messageService.getMessage("오류_조회_버전",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("batch", pObj);
		 
		if(contentMapper.insertFTR(params) < ONE) {
			return ZappFinalizing.finalising("오류_조회_버전", "[cFTRRows] " + messageService.getMessage("오류_조회_버전",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	public ZstFwResult rFTRCount(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			ZappEnv SYS_LIST_QUERY_OBJECT = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_QUERY_OBJECT.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
			ZappEnv SYS_FTR = new ZappEnv(); SYS_FTR.setSetval(NO);
		
			/* Default */
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
			}
			if(SYS_LIST_QUERY_OBJECT == null) {
				SYS_LIST_QUERY_OBJECT = new ZappEnv(); SYS_LIST_QUERY_OBJECT.setSetval("A");	// 전체
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			/* 조회 범위 */
			boolean MANUAL_TYPE = false;
			ZappEnv SYS_LIST_QUERY_OBJECT_MANUAL = null;
			if(ZstFwValidatorUtils.valid(pObjw.getObjQueryType()) == true) {
				MANUAL_TYPE = true;
				SYS_LIST_QUERY_OBJECT_MANUAL = new ZappEnv();
				SYS_LIST_QUERY_OBJECT_MANUAL.setSetval(pObjw.getObjQueryType());
			}		
			if(ZstFwValidatorUtils.valid(pObjw.getSword()) == true) {
				SYS_FTR.setSetval(YES);
			}
			
			pObjRes.setResObj(contentMapper.selectFTRCount(pObjAuth
														, SYS_CONTENTACL_YN
														, MANUAL_TYPE == true ? SYS_LIST_QUERY_OBJECT_MANUAL : SYS_LIST_QUERY_OBJECT
														, SYS_DEPT_RANGE
														, SYS_FTR
														, (pObjw.getZappBundle() != null) ? getWhere(pObjf == null ? new ZappBundle() : pObjf.getZappBundle(), pObjw.getZappBundle()) : null
														, (pObjw.getZappFile() != null) ? getWhere(pObjf == null ? new ZappFile() : pObjf.getZappFile(), pObjw.getZappFile()) : null
														, (pObjw.getzArchMFile() != null) ? pObjw.getzArchMFile() : null
														, (pObjw.getZappKeyword() != null) ? getWhere(pObjf == null ? new ZappKeyword() : pObjf.getZappKeyword(), pObjw.getZappKeyword()) : null
														, (pObjw.getZappAdditoryBundle() != null) ? getWhere(pObjf == null ? new ZappAdditoryBundle() : pObjf.getZappAdditoryBundle(), pObjw.getZappAdditoryBundle()) : null			
														, (pObjw.getZappClassObject() != null) ? getWhere(pObjf == null ? new ZappClassObject() : pObjf.getZappClassObject(), pObjw.getZappClassObject()) : null				
														));
		
		}
		
		return pObjRes;
	}
	
	
	public ZstFwResult rFTRList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			ZappEnv SYS_CONTENTACL_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.CONTENTACL_YN.env);
			ZappEnv SYS_LIST_QUERY_OBJECT = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_QUERY_OBJECT.env);
			ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
			ZappEnv SYS_FTR = new ZappEnv(); SYS_FTR.setSetval(NO);
			
			/* Default */
			if(SYS_CONTENTACL_YN == null) {
				SYS_CONTENTACL_YN = new ZappEnv(); SYS_CONTENTACL_YN.setSetval(YES);
			}
			if(SYS_LIST_QUERY_OBJECT == null) {
				SYS_LIST_QUERY_OBJECT = new ZappEnv(); SYS_LIST_QUERY_OBJECT.setSetval("A");	// 전체
			}
			if(SYS_DEPT_RANGE == null) {
				SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");					// 소속부서
			}
			
			/* 조회 범위 */
			boolean MANUAL_TYPE = false;
			ZappEnv SYS_LIST_QUERY_OBJECT_MANUAL = null;
			if(ZstFwValidatorUtils.valid(pObjw.getObjQueryType()) == true) {
				MANUAL_TYPE = true;
				SYS_LIST_QUERY_OBJECT_MANUAL = new ZappEnv();
				SYS_LIST_QUERY_OBJECT_MANUAL.setSetval(pObjw.getObjQueryType());
			}			
			if(ZstFwValidatorUtils.valid(pObjw.getSword()) == true) {
				SYS_FTR.setSetval(YES);
			}
			
			/* Sorting */
			if(pObjw.getObjmaporder() == null) {
				pObjw.setObjmaporder(new HashMap<String, String>());
				pObjw.getObjmaporder().put("CREATETIME", "DESC");
			}
			if(pObjw.getObjmaporder().size() == ZERO) {
				pObjw.setObjmaporder(new HashMap<String, String>());
				pObjw.getObjmaporder().put("CREATETIME", "DESC");
			}
			
			pObjRes.setResObj(contentMapper.selectFTRList(pObjAuth
													    , new ZappQryOpt(pObjAuth, pObjw.getObjnumperpg(), pObjw.getObjpgnum(), pObjw.getObjmaporder())
														, SYS_CONTENTACL_YN
														, MANUAL_TYPE == true ? SYS_LIST_QUERY_OBJECT_MANUAL : SYS_LIST_QUERY_OBJECT
														, SYS_DEPT_RANGE
														, SYS_FTR
														, (pObjw.getZappBundle() != null) ? getWhere(pObjf == null ? new ZappBundle() : pObjf.getZappBundle(), pObjw.getZappBundle()) : null
														, (pObjw.getZappFile() != null) ? getWhere(pObjf == null ? new ZappFile() : pObjf.getZappFile(), pObjw.getZappFile()) : null
														, (pObjw.getzArchMFile() != null) ? pObjw.getzArchMFile() : null
														, (pObjw.getZappKeyword() != null) ? getWhere(pObjf == null ? new ZappKeyword() : pObjf.getZappKeyword(), pObjw.getZappKeyword()) : null
														, (pObjw.getZappAdditoryBundle() != null) ? getWhere(pObjf == null ? new ZappAdditoryBundle() : pObjf.getZappAdditoryBundle(), pObjw.getZappAdditoryBundle()) : null				
														, (pObjw.getZappClassObject() != null) ? getWhere(pObjf == null ? new ZappClassObject() : pObjf.getZappClassObject(), pObjw.getZappClassObject()) : null				
														));
		
		}
		
		return pObjRes;
	}
	
	/* ******************************************************************************** */
	
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
		if(pobjw instanceof ZappBundle) {
			ALIAS = ZappConts.ALIAS.BUNDLE.alias;
		}
		if(pobjw instanceof ZappAdditoryBundle) {
			ALIAS = ZappConts.ALIAS.ADDITORYBUNDLE.alias;
		}
		if(pobjw instanceof ZappClassObject) {
			ALIAS = ZappConts.ALIAS.CLASSOBJECT.alias;
		}
		if(pobjw instanceof ZappLinkedObject) {
			ALIAS = ZappConts.ALIAS.LINKEDOBJECT.alias;
		}
		if(pobjw instanceof ZappSharedObject) {
			ALIAS = ZappConts.ALIAS.SHAREDOBJECT.alias;
		}
		if(pobjw instanceof ZappLockedObject) {
			ALIAS = ZappConts.ALIAS.LOCKEDOBJECT.alias;
		}
		if(pobjw instanceof ZappTmpObject) {
			ALIAS = ZappConts.ALIAS.TMPOBJECT.alias;
		}
		if(pobjw instanceof ZappFile) {
			ALIAS = ZappConts.ALIAS.MFILE.alias;
		}
		if(pobjw instanceof ZArchMFile) {
			ALIAS = ZappConts.ALIAS.ARCHMFILE.alias;
		}
		if(pobjw instanceof ZappKeyword) {
			ALIAS = ZappConts.ALIAS.KEYWORD.alias;
		}
		if(pobjw instanceof ZappKeywordObject) {
			ALIAS = ZappConts.ALIAS.KEYWORDOBJECT.alias;
		}	
		if(pobjw instanceof ZappClassification) {
			ALIAS = ZappConts.ALIAS.CLASS.alias;
		}
		if(pobjw instanceof ZappMarkedObject) {
			ALIAS = ZappConts.ALIAS.MARKEDOBJECT.alias;
		}
		if(pobjw instanceof ZappContentWorkflow) {
			ALIAS = ZappConts.ALIAS.CONWF.alias;
		}		
		if(pobjw instanceof ZappComment) {
			ALIAS = ZappConts.ALIAS.COMMENT.alias;
		}
		
		try {
			if(pobjw instanceof ZappBundle) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappBundle()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappAdditoryBundle) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappAdditoryBundle()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappClassObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappClassObject()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappClassification) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilClassBinder.getFilter(new ZappClassification()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappLinkedObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappLinkedObject()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappSharedObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappSharedObject()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappLockedObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappLockedObject()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappTmpObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappTmpObject()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappFile) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappFile()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZArchMFile) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZArchMFile()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappKeyword) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappKeyword()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappKeywordObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappKeywordObject()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappMarkedObject) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappMarkedObject()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappContentWorkflow) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappContentWorkflow()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappComment) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappComment()) : pobjf, pobjw, ALIAS);
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
	private ZstFwResult valid(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZappConts.ACTION pObjAct, ZstFwResult pObjRes) {
		
		switch(pObjAct) {
			case ADD: 
				if(utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[Content] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Content] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[Content] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Content] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[Content] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Content] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[Content] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:
		}

		return pObjRes;
	}	

	
}

package com.zenithst.core.content.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.zenithst.archive.constant.Results;
import com.zenithst.archive.service.ZArchMFileService;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.api.ZappClassificationMgtService;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.debug.ZappDebug;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappJSONUtils;
import com.zenithst.core.content.vo.ZappAdditoryBundle;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.content.vo.ZappContentWorkflow;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.content.vo.ZappKeywordExtend;
import com.zenithst.core.content.vo.ZappLockedObject;
import com.zenithst.core.content.vo.ZappTmpObject;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.core.workflow.api.ZappWorkflowMgtService;
import com.zenithst.core.workflow.service.ZappWorkflowService;
import com.zenithst.core.workflow.vo.ZappWorkflowObject;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwEncodeUtils;
import com.zenithst.framework.util.ZstFwFileUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage workflow info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappContentWorkflowMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappContentWorkflowMgtService")
public class ZappContentWorkflowMgtServiceImpl extends ZappService implements ZappContentWorkflowMgtService {

	/*
	 * [Service]
	 */
	
	/* Content */
	@Autowired
	private ZappContentMgtService contentMgtService;
	
	/* Classification */
	@Autowired
	private ZappClassificationMgtService classService;
	
	/* Log */
	@Autowired
	private ZappOrganMgtService organService;
	
	/* Workflow */
	@Autowired
	private ZappWorkflowMgtService workflowMgtService;
	@Autowired
	private ZappWorkflowService workflowService;
	
	/* Log */
	@Autowired
	private ZappLogMgtService logService;

	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/*
	 * [Binder] 
	 */
	

	/* Archive */
	@Autowired
	private ZArchMFileService zarchMfileService;
	
	/* Temporary Mounting Path  */
	@Value("#{archiveconfig['TEMP_PATH']}")
	protected String TEMP_PATH;
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappContentWorkflowMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappContentWorkflowMgtService] Service Destroy ");
	}		
	
	/* ****************************************************************************************************************** */
	
	/**
	 * 컨텐츠를 승인 처리한다.
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult approveContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing Time
//		String CONTENTID = BLANK, CONTENTTYPE = BLANK;				// Content
		String ACTION = BLANK;										// Processing Type
		String ACTION_REQUEST = BLANK;								// Requesting Type
		String STATE = BLANK;										// State
		boolean ISLASTAPPROVER = false, ISLASTAPPROVER_RELOCATE = true;		// The last approver ?
		boolean ISDIRECTSTATE = false;
		Map<String, Object> LOGMAP = new HashMap<String, Object>();	// Log
		
		// ## 초기화
		if(pObjContent.getZappAdditoryBundle() == null) {pObjContent.setZappAdditoryBundle(new ZappAdditoryBundle());}
		if(pObjContent.getZappLockedObject() == null) {pObjContent.setZappLockedObject(new ZappLockedObject());}
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);			
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "approveContent", ZappConts.ACTION.APPROVE);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Inquire preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] Whether to leave content log
		
		/* [Get info. by type]
		 * 
		 */
		ZappBundle rZappBundle = null; ZappAdditoryBundle rZappAdditoryBundle = null; 
		ZArchMFile rZArchMFile = null; ZappFile rZappFile = null; 
		String rState = BLANK; String[] DRAFTER = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[approveContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj(); 
			rState = rZappBundle.getState();

			// Additory Bundle
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappAdditoryBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLEEXT", "[approveContent] " + messageService.getMessage("ERR_R_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappAdditoryBundle = (ZappAdditoryBundle) pObjRes.getResObj(); 
			if(rZappAdditoryBundle != null) {
				DRAFTER = rZappAdditoryBundle.getDrafter().split(DIVIDER);
			}
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			try {
				rZArchMFile = zarchMfileService.rSingleRow_Vo(new ZArchMFile(pObjContent.getContentid()));
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
			}			
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[approveContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rState = rZArchMFile.getState();
			
			/* Get info. */
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[approveContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			rZappFile.setFilename(rZArchMFile.getFilename());
			if(rZappFile != null) {
				DRAFTER = rZappFile.getDrafter().split(DIVIDER);
			}
			
		}
		
		/* [Temporary Info.]
		 * 
		 */
		ZappClassification rZappCls = null;
		if(rState.equals(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state) 
				|| rState.equals(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state)) {		// Replicate or Relocate 
		
			pObjRes = getExp(pObjAuth, pObjContent, rState, pObjRes);
			rZappCls = (ZappClassification) pObjRes.getResObj();
		
		} else {
		
			/* [Get folder info.] 
			 * 1. Inquire folder info. of this content.
			 * 2. When the folder is not required workflow process, return. 
			 */
			ZappClassObject pZappClassObject = new ZappClassObject();
			pZappClassObject.setCobjid(pObjContent.getContentid());
			pZappClassObject.setCobjtype(pObjContent.getObjType());
			pObjRes = contentMgtService.selectExtendObject(pObjAuth, pZappClassObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_CLASS", "[approveContent] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			List<ZappClassification> rZappClsList = (List<ZappClassification>) pObjRes.getResObj();
			if(rZappClsList == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[approveContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappClassification vo : rZappClsList) {
				rZappCls = vo;
			}
		}
		if(rZappCls == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[approveContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(ZstFwValidatorUtils.valid(rZappCls.getWfid()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_WFID", "[approveContent] " + messageService.getMessage("ERR_MIS_WFID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Check state]
		 * 
		 */
		if(isRequestState(rState) == false) {
			return ZappFinalizing.finalising("ERR_STATE", "[approveContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ISDIRECTSTATE = isDirectState(rState);
		
		/* [Check approval]
		 * 
		 */
		if(rState.equals(ZappConts.STATES.BUNDLE_ADD_REQUEST.state)) {
			if(workflowMgtService.doApply(rZappCls.getWfrequired(), ZappConts.ACTION.ADD) == false) {
				return ZappFinalizing.finalising("ERR_STATE", "[approveContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		if(rState.equals(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state)						// Edit
				|| rState.equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state)			// Move
				|| rState.equals(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state)) {	// Copy
			if(workflowMgtService.doApply(rZappCls.getWfrequired(), ZappConts.ACTION.CHANGE) == false) {
				return ZappFinalizing.finalising("ERR_STATE", "[approveContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		if(rState.equals(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state)) {
			if(workflowMgtService.doApply(rZappCls.getWfrequired(), ZappConts.ACTION.DISABLE) == false) {
				return ZappFinalizing.finalising("ERR_STATE", "[approveContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Get workflow info.]
		 * Check whether the current is the assigned approver of this content.
		 */
		ZappWorkflowObject pZappWfObj = new ZappWorkflowObject();
		ZappWorkflowObject rZappWfObj = null;
		pZappWfObj.setContentid(pObjContent.getContentid());
		pZappWfObj.setContenttype(pObjContent.getObjType());
		pZappWfObj.setWferid(pObjAuth.getSessDeptUser().getDeptuserid());
		pObjRes = workflowMgtService.selectObject(pObjAuth, pZappWfObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_R_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}	
		List<ZappWorkflowObject> rZappWfObjList = (List<ZappWorkflowObject>) pObjRes.getResObj();
		if(rZappWfObjList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(rZappWfObjList.size() == ZERO) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappWorkflowObject vo : rZappWfObjList) {
			rZappWfObj = vo;
		}
		if(rZappWfObj == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Get temporary info.]
		 * 등록 요청을 제외한 요청 상태의 경우 임시 정보를 조회한다.
		 */
		List<ZappTmpObject> rZappTmpObjList = null; ZappTmpObject rZappTmpObj = null;
		ZappContentPar pZappContentPar = new ZappContentPar();
		if(!rState.equals(ZappConts.STATES.BUNDLE_ADD_REQUEST.state)) {	// 등록 요청이 아닌 경우 
			ZappTmpObject pZappTmpObj = new ZappTmpObject();
			pZappTmpObj.setTobjid(pObjContent.getContentid());
			pObjRes = contentMgtService.selectObject(pObjAuth, pZappTmpObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_TEMP", "[approveContent] " + messageService.getMessage("ERR_R_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			rZappTmpObjList = (List<ZappTmpObject>) pObjRes.getResObj();
			if(rZappTmpObjList == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_TEMP", "[approveContent] " + messageService.getMessage("ERR_NEXIST_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappTmpObject vo : rZappTmpObjList) {
				rZappTmpObj = vo;
			}
			pZappContentPar = extractTmpObj(rZappTmpObj);
		}
		
		/* [Get group users info. for approval]
		 * 
		 */
		ZappGroupUser pZappGroupUser = new ZappGroupUser();
		pZappGroupUser.setGroupid(rZappCls.getWfid());		// Workflow Group ID
		pObjRes = organService.selectObject(pObjAuth, null, pZappGroupUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUPUSER", "[approveContent] " + messageService.getMessage("ERR_R_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappGroupUser> rZappGroupUserList = (List<ZappGroupUser>)  pObjRes.getResObj();
		if(rZappGroupUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUPUSER", "[approveContent] " + messageService.getMessage("ERR_NEXIST_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
//		ZappGroupUser pCurrentUser = null;			// The first approver
		int MAX_SEQ = ZERO;
		for(ZappGroupUser vo : rZappGroupUserList) {
			MAX_SEQ = Math.max(vo.getGobjseq(), MAX_SEQ);
		}
		for(ZappGroupUser vo : rZappGroupUserList) {
			if(vo.getGobjid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				if(vo.getGobjseq() == MAX_SEQ) {
					ISLASTAPPROVER = true;
				}
//				pCurrentUser = vo;
				break;
			}
		}
		
		/* 최종 승인처리가 이동의 원본 승인인 경우
		 * 대상 폴더에 다시 승인 프로세스를 태운다
		 */
		ZappClassification rZappClassification_Target = null;
		if(ISLASTAPPROVER == true) {
			if(rState.equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state)) {
				if(pZappContentPar.getZappClassObjects() != null) {
					if(!rZappCls.getClassid().equals(pZappContentPar.getZappClassObjects().get(ONE).getClassid())) {	// Target Classification
						pObjRes = classService.selectObject(pObjAuth, new ZappClassification(pZappContentPar.getZappClassObjects().get(ONE).getClassid()), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[approveContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						rZappClassification_Target = (ZappClassification) pObjRes.getResObj();
						if(rZappClassification_Target == null) {
							return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[approveContent] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						if(workflowMgtService.doApply(rZappClassification_Target.getWfrequired(), ZappConts.ACTION.ADD) == true) {
							
							STATE = ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state;
							ISLASTAPPROVER_RELOCATE = false;
							ISDIRECTSTATE = true;
//							if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
//								pObjRes = workflowMgtService.commenceWorkflow(pObjAuth, rZappBundle, rZappClassification_Target, pObjRes);
//							}
//							if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
//								pObjRes = workflowMgtService.commenceWorkflow(pObjAuth, rZappFile, rZappClassification_Target, pObjRes);
//							}
//							if(ZappFinalizing.isSuccess(pObjRes) == false) {
//								return ZappFinalizing.finalising("ERR_C_APPROVAL", "[approveContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//							}
						}
					}
				}
			}
		}
		
		/* [Approval process by states]
		 * 1. Request for registering :  + 로그  
		 * 2. Request for editing : 변경 처리 호출
		 * 3. Request for deleting : 상태 변경 + 로그
		 * 4. Request for recovering : 상태 변경 + 로그
		 */
		if(ISLASTAPPROVER == true) {

			if(rState.equals(ZappConts.STATES.BUNDLE_ADD_REQUEST.state)) {			// Request for registering
				STATE = ZappConts.STATES.BUNDLE_NORMAL.state;
				ACTION = ZappConts.LOGS.ACTION_ADD.log;
				ACTION_REQUEST = ZappConts.LOGS.ACTION_ADD_APPROVE.log;
			}
			
			pZappContentPar.setObjAcsRoute(genCode(pObjContent)); 				// Generate processing code
			if(rState.equals(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state)) {		// Request for editing
			
				pZappContentPar.setZappClassObject(new ZappClassObject(rZappCls.getClassid(), null, null));
				pObjRes = contentMgtService.changeContent(pObjAuth, pZappContentPar, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_CONTENT", "[approveContent] " + messageService.getMessage("ERR_E_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				STATE = ZappConts.STATES.BUNDLE_NORMAL.state;
				ACTION_REQUEST = ZappConts.LOGS.ACTION_CHANGE_APPROVE.log;
				ISDIRECTSTATE = true;
			}
			
			if(rState.equals(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state)) {		// Request for deleting
				
//				pObjRes = contentMgtService.disableContent(pObjAuth, pZappContentPar, pObjRes);
//				if(ZappFinalizing.isSuccess(pObjRes) == false) {
//					return ZappFinalizing.finalising("ERR_D_CONTENT", "[approveContent] " + messageService.getMessage("ERR_D_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}		
				
				STATE = ZappConts.STATES.BUNDLE_DISCARD_WAIT.state;
				ACTION = ZappConts.LOGS.ACTION_DISABLE.log;
				ACTION_REQUEST = ZappConts.LOGS.ACTION_DISABLE_APPROVE.log;
			}
			
			if(rState.equals(ZappConts.STATES.BUNDLE_ENABLE_REQUEST.state)) {		// Request for recovering
				
//				pObjRes = contentMgtService.enableContent(pObjAuth, pZappContentPar, pObjRes);
//				if(ZappFinalizing.isSuccess(pObjRes) == false) {
//					return ZappFinalizing.finalising("ERR_복구_컨텐츠", "[approveContent] " + messageService.getMessage("ERR_복구_컨텐츠",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}			
				
				STATE = ZappConts.STATES.BUNDLE_NORMAL.state;
				ACTION = ZappConts.LOGS.ACTION_ENABLE.log;
				ACTION_REQUEST = ZappConts.LOGS.ACTION_ENABLE_APPROVE.log;
			}
			
			if(rState.equals(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state)) {		// Request for discarding
				
				pObjRes = contentMgtService.discardContent(pObjAuth, pZappContentPar, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_D_CONTENT", "[approveContent] " + messageService.getMessage("ERR_D_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}			
				
				ACTION_REQUEST = ZappConts.LOGS.ACTION_DISCARD_APPROVE.log;
			}	
			
			if(rState.equals(ZappConts.STATES.BUNDLE_DISCARD_VERSION_REQUEST.state)) {		// Request for discarding a version
				
				pObjRes = contentMgtService.discardSpecificVersionContent(pObjAuth, pZappContentPar, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_D_CONTENT", "[approveContent] " + messageService.getMessage("ERR_D_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}			
				
				ACTION_REQUEST = ZappConts.LOGS.ACTION_DISCARD_APPROVE.log;
			}				
			
			if((rState.equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state) || rState.equals(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state))
					&& ISLASTAPPROVER_RELOCATE == true) {							// Request for moving
				
				pObjRes = contentMgtService.relocateContent(pObjAuth, pZappContentPar, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_RL_CONTENT", "[approveContent] " + messageService.getMessage("ERR_RL_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}	

				STATE = ZappConts.STATES.BUNDLE_NORMAL.state;
				ACTION_REQUEST = ZappConts.LOGS.ACTION_RELOCATE_APPROVE.log;
				ISDIRECTSTATE = true;
			}	
			
			if(rState.equals(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state)) {	// Request for copying
				
				pObjRes = contentMgtService.replicateContent(pObjAuth, pZappContentPar, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_RP_CONTENT", "[approveContent] " + messageService.getMessage("ERR_RP_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}					

				STATE = ZappConts.STATES.BUNDLE_NORMAL.state;
				ACTION_REQUEST = ZappConts.LOGS.ACTION_REPLICATE_APPROVE.log;
				ISDIRECTSTATE = true;
			}	
			
			if(rState.equals(ZappConts.STATES.BUNDLE_LOCK_REQUEST.state)) {		// Request for locking
				
				pObjRes = contentMgtService.lockContent(pObjAuth, pZappContentPar, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_LOCK_CONTENT", "[approveContent] " + messageService.getMessage("ERR_LOCK_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}	
				
				ACTION_REQUEST = ZappConts.LOGS.ACTION_LOCK_APPROVE.log;
			}	
			
			/* [직접 처리]
			 * 1. 상태변경
			 * 2. 로그기록
			 */
			if(ISDIRECTSTATE == true) {
				
				//
				if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
					pZappBundle.setState(STATE);
					pObjRes = contentMgtService.changeObject(pObjAuth, pZappBundle, pObjRes);
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_E_BUNDLE", "[approveContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
					ZArchResult rZArchResult = new ZArchResult();
					ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getContentid());
					pZArchMFile.setState(STATE);
					try {
						rZArchResult = zarchMfileService.uSingleRow(pZArchMFile);
					} catch (Exception e) {
						return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
					}			
					if(ZappFinalizing.isSuccess(rZArchResult) == false) {
						return ZappFinalizing.finalising_Archive(rZArchResult.getCode(), pObjAuth.getObjlang());
					}
				}
				
				// 로그
//				if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
//					
//					ZappAuth pZappAuth = cvrtAuth(pObjAuth);
//					LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pZappAuth);
//					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContent);
//					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
//					if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
//						LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
//					} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
//						LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
//					}
//					pObjRes = leaveLog(pZappAuth
//							        , pObjContent.getObjType()
//							        , ACTION
//							        , LOGMAP
//							        , PROCTIME
//							        , pObjRes);
//				}
				
			}
			
			/* [임시 정보 삭제]
			 * 
			 */
			if(!rState.equals(ZappConts.STATES.BUNDLE_ADD_REQUEST.state) && ISLASTAPPROVER_RELOCATE == true) {
				pObjRes = contentMgtService.deleteObject(pObjAuth, new ZappTmpObject(rZappTmpObj.getTmpobjid()), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_D_TEMP", "[approveContent] " + messageService.getMessage("ERR_D_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				/* File */
				if(rState.equals(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state)) {
					StringBuffer TMPFILE = new StringBuffer();
					TMPFILE.append(ZstFwFileUtils.addSeperator(TEMP_PATH));						// Temp. Mounting Path (in configuration xml file)
					TMPFILE.append(ZstFwFileUtils.addSeperator(rZappTmpObj.getTmpobjid()));		// Temp. ID	
					if(ZstFwFileUtils.existFile(TMPFILE.toString()) == true) {
						try {
							ZstFwFileUtils.delDir(TMPFILE.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
			
			/* [Clear workflow info.]
			 * 
			 */
			ZappWorkflowObject pZappWfObject = new ZappWorkflowObject();
			pZappWfObject.setContentid(pObjContent.getContentid());					// Content ID
			pZappWfObject.setContenttype(pObjContent.getObjType());					// Content Type
//			pZappWfObject.setWferid(pObjAuth.getSessDeptUser().getDeptuserid());	// Approver ID
			pObjRes = workflowService.dMultiRows(pObjAuth, pZappWfObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_D_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			
			/* [Clear drafter info.]
			 * 
			 */
			if(ISLASTAPPROVER_RELOCATE == true) {
				if(!rState.equals(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state)) {
					if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
						ZappAdditoryBundle pZappAdditoryBundle = new ZappAdditoryBundle(pObjContent.getContentid());
						pZappAdditoryBundle.setDrafter(ZstFwConst.SCHARS.UNDERSCORE.character);
						pObjRes = contentMgtService.changeObject(pObjAuth, pZappAdditoryBundle, pObjRes);
					} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
						pZappFile.setDrafter(ZstFwConst.SCHARS.UNDERSCORE.character);
						pZappFile.setUpdatetime(ZstFwDateUtils.getNow());
						pObjRes = contentMgtService.changeObject(pObjAuth, pZappFile, pObjRes);
					}
					if(ZappFinalizing.isSuccess(pObjRes) == false) {
						return ZappFinalizing.finalising("ERR_E_EXT", "[approveContent] " + messageService.getMessage("ERR_E_EXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}			
				}
			}
			
		} else {

			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				pObjRes = workflowMgtService.proceedWorkflow(pObjAuth, rZappBundle, rZappCls, pObjRes);
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				pObjRes = workflowMgtService.proceedWorkflow(pObjAuth, rZappFile, rZappCls, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_ASSIGN_NEXTAPPROVER", "[approveContent] " + messageService.getMessage("ERR_ASSIGN_NEXTAPPROVER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}
		
		/* [Approval History]
		 * 
		 */
		ZappContentWorkflow pZappContentWorkflow = new ZappContentWorkflow(pObjContent.getContentid(), pObjContent.getObjType());
		pZappContentWorkflow.setDrafterid(DRAFTER != null ? DRAFTER[ZERO] : BLANK);
		pZappContentWorkflow.setDraftername(DRAFTER != null ? DRAFTER[ONE] : BLANK);
		pZappContentWorkflow.setWferid(pObjAuth.getSessDeptUser().getDeptuserid());
		pZappContentWorkflow.setWfername("[" + pObjAuth.getSessDeptUser().getZappDept().getName() + "]" + pObjAuth.getSessDeptUser().getZappUser().getName());
		pZappContentWorkflow.setComments(ZstFwValidatorUtils.fixNullString(pObjContent.getZappLockedObject().getReason(), BLANK));
		pZappContentWorkflow.setStatus(getCounterApprovedState(rState));
		pObjRes = contentMgtService.addObject(pObjAuth, pZappContentWorkflow, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_WFHIS", "[approveContent] " + messageService.getMessage("ERR_C_WFHIS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [로그]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle); pObjContent.setZappFile(rZappFile);
		    ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.APPROVE);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
					         , pObjContent.getObjType()
							 , ACTION_REQUEST
					         , LOGMAP
					         , PROCTIME
					         , pObjRes);
		}
		
		/**
		 * Relocation
		 */
		if(rState.equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state) 
				&& ISLASTAPPROVER_RELOCATE == false 
				&& ISLASTAPPROVER == true) {
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				rZappBundle.setState(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state);
				pObjRes = workflowMgtService.commenceWorkflow(pObjAuth, rZappBundle, rZappClassification_Target, pObjRes);
			}
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				rZappFile.setState(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state);
				pObjRes = workflowMgtService.commenceWorkflow(pObjAuth, rZappFile, rZappClassification_Target, pObjRes);
			}
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_C_APPROVAL", "[approveContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}

		return pObjRes;
	}
	
	/**
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor={Exception.class})
	public ZstFwResult returnContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);

		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle( new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappAcls() == null) { pObjContent.setZappAcls(new ArrayList<ZappContentAcl>()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappFiles() == null) { pObjContent.setZappFiles(new ArrayList<ZappFile>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "returnContent", ZappConts.ACTION.RETURN);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] 컨텐츠 로그 기록 여부

		/* [Get info. by types]
		 * 
		 */
		ZappBundle rZappBundle = null; ZappAdditoryBundle rZappAdditoryBundle = null;
		ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		String rState = BLANK; String[] DRAFTER = null;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[returnContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj(); 
			rState = rZappBundle.getState();
			
			// 확장 정보
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappAdditoryBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLEEXT", "[returnContent] " + messageService.getMessage("ERR_R_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappAdditoryBundle = (ZappAdditoryBundle) pObjRes.getResObj(); 
			if(rZappAdditoryBundle != null) {
				DRAFTER = rZappAdditoryBundle.getDrafter().split(DIVIDER);
			}
			

		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			try {
				rZArchMFile = zarchMfileService.rSingleRow_Vo(new ZArchMFile(pObjContent.getContentid()));
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_READ_MFILE.result, pObjAuth.getObjlang());
			}			
			if(rZArchMFile == null) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[returnContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rState = rZArchMFile.getState();
			
			// Get info.
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[returnContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				//# Bundle 정보를 조회하는데 실패했습니다.
			}
			rZappFile = (ZappFile) pObjRes.getResObj(); 
			if(rZappFile != null) {
				DRAFTER = rZappFile.getDrafter().split(DIVIDER);
			}
			
		}

		/* [Check state]
		 * 
		 */
		if(isRequestState(rState) == false) {
			return ZappFinalizing.finalising("ERR_STATE", "[returnContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Get workflow info.]
		 * 현재 처리자가 해당 요청 처리에 설정된 승인자인지 체크한다.
		 */
		ZappWorkflowObject pZappWorkflowObject = new ZappWorkflowObject();
		ZappWorkflowObject rZappWorkflowObject = null;
		pZappWorkflowObject.setContentid(pObjContent.getContentid());				// Content ID
		pZappWorkflowObject.setContenttype(pObjContent.getObjType());				// Content Type
		pZappWorkflowObject.setWferid(pObjAuth.getSessDeptUser().getDeptuserid());	// Approver
		pObjRes = workflowMgtService.selectObject(pObjAuth, pZappWorkflowObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_R_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}	
		@SuppressWarnings("unchecked")
		List<ZappWorkflowObject> rZappWorkflowObjectList = (List<ZappWorkflowObject>) pObjRes.getResObj();
		if(rZappWorkflowObjectList == null) {
			return ZappFinalizing.finalising("ERR_R_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_R_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(rZappWorkflowObjectList.size() == ZERO) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappWorkflowObject vo : rZappWorkflowObjectList) {
			rZappWorkflowObject = vo;
		}
		if(rZappWorkflowObject == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}	
		if(!rZappWorkflowObject.getWferid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[approveContent] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Change info.]
		 * 1. Change state
		 * 2. Write a comment
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			// State
			ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
			pZappBundle.setState(getCounterState(rState));
//			pZappBundle.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
			pObjRes = contentMgtService.changeObject(pObjAuth, pZappBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[returnContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			
			// State
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getContentid());
			pZArchMFile.setState(getCounterState(rState));
//			pZArchMFile.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
			
			try {
				zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}

		}
		
		/* [Temporary]
		 * 
		 */
		if(!rState.equals(ZappConts.STATES.BUNDLE_ADD_REQUEST.state)) {
			
			/* Info. */
			ZappTmpObject pZappTmpObject = new ZappTmpObject();
			pZappTmpObject.setTobjid(pObjContent.getContentid());
			pZappTmpObject.setTobjtype(pObjContent.getObjType());
			pObjRes = contentMgtService.deleteObject(pObjAuth, new ZappTmpObject(ZappKey.getPk(pZappTmpObject)), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_TMPOBJ", "[returnContent] " + messageService.getMessage("ERR_E_TMPOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* File */
			if(rState.equals(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state)) {
				StringBuffer TMPFILE = new StringBuffer();
				TMPFILE.append(ZstFwFileUtils.addSeperator(TEMP_PATH));						// Temp. Mounting Path (in configuration xml file)
				TMPFILE.append(ZstFwFileUtils.addSeperator(ZappKey.getPk(pZappTmpObject)));	// Temp. ID	
				if(ZstFwFileUtils.existFile(TMPFILE.toString()) == true) {
					try {
						ZstFwFileUtils.delDir(TMPFILE.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		/* [Clear Workflow info.]
		 * 
		 */
		ZappWorkflowObject pZappWfObject = new ZappWorkflowObject();
		pZappWfObject.setContentid(pObjContent.getContentid());					// Content ID
		pZappWfObject.setContenttype(pObjContent.getObjType());					// Content Type
		pObjRes = workflowService.dMultiRows(pObjAuth, pZappWfObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_WFOBJ", "[returnContent] " + messageService.getMessage("ERR_D_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		/* [Approval History]
		 * 
		 */
		ZappContentWorkflow pZappContentWorkflow = new ZappContentWorkflow(pObjContent.getContentid(), pObjContent.getObjType());
		pZappContentWorkflow.setDrafterid(DRAFTER != null ? DRAFTER[ZERO] : BLANK);
		pZappContentWorkflow.setDraftername(DRAFTER != null ? DRAFTER[ONE] : BLANK);
		pZappContentWorkflow.setWferid(pObjAuth.getSessDeptUser().getDeptuserid());
		pZappContentWorkflow.setWfername("[" + pObjAuth.getSessDeptUser().getZappDept().getName() + "]" + pObjAuth.getSessDeptUser().getZappUser().getName());
		pZappContentWorkflow.setComments(ZstFwValidatorUtils.fixNullString(pObjContent.getZappLockedObject().getReason(), BLANK));
		pZappContentWorkflow.setStatus(getCounterState(rState));
		pObjRes = contentMgtService.addObject(pObjAuth, pZappContentWorkflow, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_WFHIS", "[returnContent] " + messageService.getMessage("ERR_C_WFHIS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/*  [LOG]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			pObjContent.setZappBundle(rZappBundle); pObjContent.setZappFile(rZappFile);
		    ZappContentRes pObjContentLog = logService.initLogRes(pObjContent, ZappConts.ACTION.RETURN);
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContentLog);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getTitle(), rZappBundle.getTitle()) == false) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getTitle()) ? pObjContent.getZappBundle().getTitle() : rZappBundle.getTitle());
				} else {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_RETURN.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}	
		
		return pObjRes;
		
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult undoContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);

		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle( new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappAcls() == null) { pObjContent.setZappAcls(new ArrayList<ZappContentAcl>()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappFiles() == null) { pObjContent.setZappFiles(new ArrayList<ZappFile>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "discardContent", ZappConts.ACTION.DISCARD);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] 컨텐츠 로그 기록 여부

		/* [Get content info. by types]
		 * 
		 */
		ZappBundle rZappBundle = null; 
		ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		String rState = BLANK;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[discardContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj(); 
			rState = rZappBundle.getState();

		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

//			pObjRes = contentMgtService.getFile(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
//				return ZappFinalizing.finalising("ERR_R_FILE", "[discardContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//			}
//			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); --
//			rState = rZArchMFile.getState();
			
			try {
				rZArchMFile = zarchMfileService.rSingleRow_Vo(new ZArchMFile(pObjContent.getContentid()));
			} catch (Exception e) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[discardContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rState = rZArchMFile.getState();
			
			/* Get info. */
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[discardContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
		}
		
		/* [Check state]
		 * Check if the status is returned 
		 */
		if(isReturnState(rState) == false) {
			return ZappFinalizing.finalising("ERR_STATE", "[discardContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		/* [Undo]
		 * 
		 */
		if(rState.equals(ZappConts.STATES.BUNDLE_ADD_RETURN.state)) {
			
			pObjContent.setObjAcsRoute(genCode(pObjContent));
			pObjRes = contentMgtService.discardContent(pObjAuth, pObjContent, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CONTENT", "[discardContent] " + messageService.getMessage("ERR_D_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
		} else {
			
			// Temp. info
			ZappTmpObject pZappTmpObj = new ZappTmpObject(); ZappTmpObject rZappTmpObj = null; List<ZappTmpObject> rZappTmpObjList = null;
			pZappTmpObj.setTobjid(pObjContent.getContentid());
			pObjRes = contentMgtService.selectObject(pObjAuth, pZappTmpObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_TEMP", "[approveContent] " + messageService.getMessage("ERR_R_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			rZappTmpObjList = (List<ZappTmpObject>) pObjRes.getResObj();
			if(rZappTmpObjList == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_TEMP", "[approveContent] " + messageService.getMessage("ERR_NEXIST_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappTmpObject vo : rZappTmpObjList) {
				rZappTmpObj = vo;
			}
			
			// Temp. info
			pZappTmpObj = new ZappTmpObject(pObjContent.getContentid(), pObjContent.getObjType());
			pObjRes = contentMgtService.deleteObject(pObjAuth, pZappTmpObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_TMPOBJ", "[discardContent] " + messageService.getMessage("ERR_D_TMPOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}

			// Workflow info
			ZappWorkflowObject pZappWorkflowObject = new ZappWorkflowObject(null, pObjContent.getContentid());
			pObjRes = workflowMgtService.deleteObject(pObjAuth, pZappWorkflowObject, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_WFOBJ", "[discardContent] " + messageService.getMessage("ERR_D_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}

			// State
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

				ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
				pZappBundle.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				pObjRes = contentMgtService.changeObject(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[returnContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				
				ZArchResult zArchResult = new ZArchResult();
				ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getContentid());
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				
				try {
					zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}

			}
			
			// File
			if(rState.equals(ZappConts.STATES.BUNDLE_CHANGE_RETURN.state)) {
				pObjRes = deleteTmpFile(pObjAuth, rZappTmpObj.getTmpobjid(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[returnContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
		}

		/*  [LOG]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContent);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getTitle(), rZappBundle.getTitle()) == false) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getTitle()) ? pObjContent.getZappBundle().getTitle() : rZappBundle.getTitle());
				} else {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_DISCARD_RETURN.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}	
		
		return pObjRes;
	}
	
	
	/**
	 * Withdrawing approval process
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult withdrawContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();
		Map<String, Object> LOGMAP = new HashMap<String, Object>();
		
		// ## [Debugging] ##
		ZappDebug.debug(logger, pObjAuth, pObjContent);

		// ## [Initialization] ##
		if(pObjContent.getZappBundle() == null) { pObjContent.setZappBundle( new ZappBundle()); }
		if(pObjContent.getZappFile() == null) { pObjContent.setZappFile(new ZappFile()); }
		if(pObjContent.getZappAcls() == null) { pObjContent.setZappAcls(new ArrayList<ZappContentAcl>()); }
		if(pObjContent.getZappClassObjects() == null) { pObjContent.setZappClassObjects(new ArrayList<ZappClassObject>()); }
		if(pObjContent.getZappFiles() == null) { pObjContent.setZappFiles(new ArrayList<ZappFile>()); }
		
		/* Validation */
		pObjRes = validParams(pObjAuth, pObjContent, pObjRes, "withdrawContent", ZappConts.ACTION.WITHDRAW);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return pObjRes;
		}
		
		/* [Preferences]
		 * 
		 */
		ZappEnv SYS_LOG_CONTENT_YN = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LOG_CONTENT_YN.env);					// [LOG] 컨텐츠 로그 기록 여부

		/* [Get content info. by types]
		 * 
		 */
		ZappBundle rZappBundle = null; ZappAdditoryBundle rZappAdditoryBundle = null; 
		ZArchMFile rZArchMFile = null; ZappFile rZappFile = null;
		String rState = BLANK;
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

			/* Bundle */
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLE", "[withdrawContent] " + messageService.getMessage("ERR_R_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappBundle = (ZappBundle) pObjRes.getResObj(); 
			rState = rZappBundle.getState();
			
			/* AdditoryBundle */
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappAdditoryBundle(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_BUNDLEEXT", "[withdrawContent] " + messageService.getMessage("ERR_R_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappAdditoryBundle = (ZappAdditoryBundle) pObjRes.getResObj(); 

		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {

			pObjRes = contentMgtService.getFile(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILE", "[withdrawContent] " + messageService.getMessage("ERR_R_FILE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZArchMFile = (ZArchMFile) pObjRes.getResObj(); 
			rState = rZArchMFile.getState();

			/* Get info. */
			pObjRes = contentMgtService.selectObject(pObjAuth, new ZappFile(pObjContent.getContentid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_FILEEXT", "[withdrawContent] " + messageService.getMessage("ERR_R_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			rZappFile = (ZappFile) pObjRes.getResObj();
			
		}
		
		/*
		 * Checking a withdrawer
		 */
		if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
			if(rZappAdditoryBundle.getDrafter().contains(pObjAuth.getSessDeptUser().getDeptuserid()) == false) {
				return ZappFinalizing.finalising("ERR_NIDEN_WITHDRAWER", "[withdrawContent] " + messageService.getMessage("ERR_NIDEN_WITHDRAWER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
			if(rZappFile.getDrafter().contains(pObjAuth.getSessDeptUser().getDeptuserid()) == false) {
				return ZappFinalizing.finalising("ERR_NIDEN_WITHDRAWER", "[withdrawContent] " + messageService.getMessage("ERR_NIDEN_WITHDRAWER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}			
		}
		
		/* [Check state]
		 * Check if the status is returned 
		 */
		if(isRequestState(rState) == false) {
			return ZappFinalizing.finalising("ERR_STATE", "[withdrawContent] " + messageService.getMessage("ERR_STATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		/* [Withdraw]
		 * 
		 */
		if(rState.equals(ZappConts.STATES.BUNDLE_ADD_REQUEST.state)) {

			// Changing state
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

				ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
				pZappBundle.setState(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state);
				pZappBundle.setDiscarderid(pObjAuth.getSessDeptUser().getDeptuserid());
				pObjRes = contentMgtService.changeObject(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[withdrawContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				
				ZArchResult zArchResult = new ZArchResult();
				ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getContentid());
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_DISCARD_WAIT.state);
				
				try {
					zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}

				ZappFile pZappFile = new ZappFile(pObjContent.getContentid());
				pZappFile.setDiscarderid(pObjAuth.getSessDeptUser().getDeptuserid());
				pObjRes = contentMgtService.changeObject(pObjAuth, pZappFile, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_FILEEXT", "[withdrawContent] " + messageService.getMessage("ERR_E_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}

			}

			pObjContent.setObjAcsRoute(genCode(pObjContent));	// Except for approval process
			pObjRes = contentMgtService.discardContent(pObjAuth, pObjContent, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_CONTENT", "[withdrawContent] " + messageService.getMessage("ERR_D_CONTENT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
		} else {
			
			// Temp. info
			ZappTmpObject pZappTmpObj = new ZappTmpObject(); ZappTmpObject rZappTmpObj = null; List<ZappTmpObject> rZappTmpObjList = null;
			pZappTmpObj.setTobjid(pObjContent.getContentid());
			pObjRes = contentMgtService.selectObject(pObjAuth, pZappTmpObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_TEMP", "[withdrawContent] " + messageService.getMessage("ERR_R_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			rZappTmpObjList = (List<ZappTmpObject>) pObjRes.getResObj();
			if(rZappTmpObjList == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_TEMP", "[withdrawContent] " + messageService.getMessage("ERR_NEXIST_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappTmpObject vo : rZappTmpObjList) {
				rZappTmpObj = vo;
			}
			
			// Temp. info
			pZappTmpObj = new ZappTmpObject(pObjContent.getContentid(), pObjContent.getObjType());
			pObjRes = contentMgtService.deleteObject(pObjAuth, pZappTmpObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_D_TMPOBJ", "[withdrawContent] " + messageService.getMessage("ERR_D_TMPOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}

			// Changing state
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {

				ZappBundle pZappBundle = new ZappBundle(pObjContent.getContentid());
				pZappBundle.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				pObjRes = contentMgtService.changeObject(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[withdrawContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				
				ZArchResult zArchResult = new ZArchResult();
				ZArchMFile pZArchMFile = new ZArchMFile(pObjContent.getContentid());
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				
				try {
					zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}

			}
			
			// File
			if(rState.equals(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state)) {
				pObjRes = deleteTmpFile(pObjAuth, rZappTmpObj.getTmpobjid(), pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[withdrawContent] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
		}

		// Workflow info
		ZappWorkflowObject pZappWorkflowObject = new ZappWorkflowObject(null, pObjContent.getContentid());
		pObjRes = workflowMgtService.deleteObject(pObjAuth, pZappWorkflowObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_WFOBJ", "[withdrawContent] " + messageService.getMessage("ERR_D_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/*  [LOG]
		 * 
		 */
		if(SYS_LOG_CONTENT_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
			LOGMAP.put(ZappConts.LOGS.ITEM_LOGGER.log, pObjAuth);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT.log, pObjContent);
			LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_ID.log, pObjContent.getContentid());
			if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				if(ZstFwValidatorUtils.isIdentical(pObjContent.getZappBundle().getTitle(), rZappBundle.getTitle()) == false) {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, ZstFwValidatorUtils.valid(pObjContent.getZappBundle().getTitle()) ? pObjContent.getZappBundle().getTitle() : rZappBundle.getTitle());
				} else {
					LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZappBundle.getTitle());
				}
			} else if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				LOGMAP.put(ZappConts.LOGS.ITEM_CONTENT_TITLE.log, rZArchMFile.getFilename());
			}
			pObjRes = leaveLog(pObjAuth
						     , pObjContent.getObjType()
						     , ZappConts.LOGS.ACTION_WITHDRAW.log
						     , LOGMAP
						     , PROCTIME
						     , pObjRes);
		}	
		
		return pObjRes;
	}
	
	
	// ### Extract temp. info ###
	@SuppressWarnings("unchecked")
	private ZappContentPar extractTmpObj(ZappTmpObject pTmpObj) {
		
		ZappContentPar rZappContentPar = null;
		
		if(pTmpObj != null) {
			rZappContentPar = new ZappContentPar();
			rZappContentPar.setObjType(pTmpObj.getTobjtype());	// Content type
			rZappContentPar.setContentid(pTmpObj.getTobjid());	// Content ID
			if(ZstFwValidatorUtils.valid(pTmpObj.getRetentionid())) { rZappContentPar.setObjRetention(pTmpObj.getRetentionid()); }
			if(ZstFwValidatorUtils.valid(pTmpObj.getTaskid())) { rZappContentPar.setObjTaskid(pTmpObj.getTaskid()); }
			
			ZappBundle pZappBundle = new ZappBundle();
			ZappFile pZappFile = new ZappFile();
			if(pTmpObj.getTobjtype().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				if(ZstFwValidatorUtils.valid(pTmpObj.getTitle())) { pZappBundle.setTitle(pTmpObj.getTitle()); }
				if(ZstFwValidatorUtils.valid(pTmpObj.getHolderid())) { pZappBundle.setHolderid(pTmpObj.getHolderid()); }
				if(ZstFwValidatorUtils.valid(pTmpObj.getExpiretime())) { pZappBundle.setExpiretime(pTmpObj.getExpiretime()); }
				rZappContentPar.setZappBundle(pZappBundle);
			} else if(pTmpObj.getTobjtype().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				if(ZstFwValidatorUtils.valid(pTmpObj.getTitle())) { pZappFile.setFilename(pTmpObj.getTitle()); }
				if(ZstFwValidatorUtils.valid(pTmpObj.getHolderid())) { pZappFile.setHolderid(pTmpObj.getHolderid()); }
				if(ZstFwValidatorUtils.valid(pTmpObj.getExpiretime())) { pZappFile.setExpiretime(pTmpObj.getExpiretime()); }				
				rZappContentPar.setZappFile(pZappFile);
			}
			
			// Additional Info.
			if(ZstFwValidatorUtils.valid(pTmpObj.getAddinfo())) {
				logger.info("pTmpObj.getAddinfo() = " + pTmpObj.getAddinfo());
				rZappContentPar.setZappAdditoryBundle((ZappAdditoryBundle) ZappJSONUtils.cvrtJsonToObj(new ZappAdditoryBundle(), pTmpObj.getAddinfo()));
			}
			
			// Access control info.
			if(ZstFwValidatorUtils.valid(pTmpObj.getAcls())) {
				logger.info("pTmpObj.getAcls() = " + pTmpObj.getAcls());
				List<ZappContentAcl> formlist = new ArrayList<ZappContentAcl>(); formlist.add(new ZappContentAcl());
				rZappContentPar.setZappAcls((List<ZappContentAcl>) ZappJSONUtils.cvrtJsonToObj(formlist, pTmpObj.getAcls()));
			}
			
			// Classification
			if(ZstFwValidatorUtils.valid(pTmpObj.getClasses())) {
				logger.info("pTmpObj.getClasses() = " + pTmpObj.getClasses());
				List<ZappClassObject> formlist = new ArrayList<ZappClassObject>(); formlist.add(new ZappClassObject());
				List<ZappClassObject> reslist = (List<ZappClassObject>) ZappJSONUtils.cvrtJsonToObj(formlist, pTmpObj.getClasses());
				if(reslist != null) {
					for(ZappClassObject vo : reslist) {
						rZappContentPar.setZappClassObject(vo);
					}
				}
				rZappContentPar.setZappClassObjects(reslist);
			}		
			
			// Keyword
			if(ZstFwValidatorUtils.valid(pTmpObj.getKeywords())) {
				logger.info("pTmpObj.getKeywords() = " + pTmpObj.getKeywords());
				List<ZappKeyword> formlist = new ArrayList<ZappKeyword>(); formlist.add(new ZappKeyword());
				List<ZappKeyword> reslist = (List<ZappKeyword>) ZappJSONUtils.cvrtJsonToObj(formlist, pTmpObj.getKeywords()); 
				if(reslist != null) {
					List<ZappKeywordExtend> reslist_ = new ArrayList<ZappKeywordExtend>();
					for(ZappKeyword vo : reslist) {
						ZappKeywordExtend pZappKeywordExtend = new ZappKeywordExtend();
						BeanUtils.copyProperties(vo, pZappKeywordExtend);
						reslist_.add(pZappKeywordExtend);
					}
					rZappContentPar.setZappKeywords(reslist_);
				}
			}		
			
			// File
			if(pTmpObj.getTobjtype().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				if(ZstFwValidatorUtils.valid(pTmpObj.getFiles())) {
					logger.info("pTmpObj.getFiles() = " + pTmpObj.getFiles());
					List<ZappFile> formlist = new ArrayList<ZappFile>(); formlist.add(new ZappFile());
					rZappContentPar.setZappFiles((List<ZappFile>) ZappJSONUtils.cvrtJsonToObj(formlist, pTmpObj.getFiles()));
					if(rZappContentPar.getZappFiles() != null) {
						for(int IDX = ZERO; IDX < rZappContentPar.getZappFiles().size(); IDX++) {
							rZappContentPar.getZappFiles().get(IDX).setObjFileName(ZstFwFileUtils.addSeperator(TEMP_PATH) 
																			     + ZstFwFileUtils.addSeperator(pTmpObj.getTmpobjid()) 
																			     + rZappContentPar.getZappFiles().get(IDX).getFilename());
							rZappContentPar.getZappFiles().get(IDX).setObjFileExt(ZstFwFileUtils.getExtension(rZappContentPar.getZappFiles().get(IDX).getFilename()));
						}
					}
				}
			} 
			
			// State
			if(ZstFwValidatorUtils.valid(pTmpObj.getStates())) {
				List<ZappFile> formlist = new ArrayList<ZappFile>(); formlist.add(new ZappFile());
				rZappContentPar.setZappFiles((List<ZappFile>) ZappJSONUtils.cvrtJsonToObj(formlist, pTmpObj.getStates()));
			}

		}
		
		return rZappContentPar;
	}
	
	// ### Log ###
	private ZstFwResult leaveLog(ZappAuth pObjAuth, String pLogType, String pLogAction, Map<String, Object> pLogMap, String pLogTime, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		List<ZappContentLog> pLogObjectList = new ArrayList<ZappContentLog>();
		ZappContentLog pLogObject = new ZappContentLog();
		pLogObject.setLogobjid((String) pLogMap.get(ZappConts.LOGS.ITEM_CONTENT_ID.log));
		pLogObject.setLogtext((String) pLogMap.get(ZappConts.LOGS.ITEM_CONTENT_TITLE.log));
		pLogObject.setLogtype(pLogType);
		pLogObject.setAction(pLogAction);
		pLogObject.setMaplogs(pLogMap);
		pLogObject.setLogtime(pLogTime);												// Log time
		pLogObjectList.add(pLogObject);
		pObjRes = logService.leaveLog(pObjAuth, pLogObjectList, pObjRes);
		
		return pObjRes;
	}
	
	// ### States (Requesting) ###
	private String getRequestStates() {
		StringBuffer states = new StringBuffer();
		states.append(ZappConts.STATES.BUNDLE_ADD_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_ENABLE_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_DISCARD_VERSION_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_LOCK_REQUEST.state + DIVIDER);
		return states.toString();
	}
	// ### States (Returned) ###
	private String getReturnStates() {
		StringBuffer states = new StringBuffer();
		states.append(ZappConts.STATES.BUNDLE_ADD_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_CHANGE_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_DISABLE_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_ENABLE_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_DISCARD_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_DISCARD_VERSION_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_RELOCATE_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_REPLICATE_RETURN.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_LOCK_RETURN.state + DIVIDER);
		return states.toString();
	}	
	private String getDirectStates() {
		StringBuffer states = new StringBuffer();
		states.append(ZappConts.STATES.BUNDLE_ADD_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state + DIVIDER);
		states.append(ZappConts.STATES.BUNDLE_ENABLE_REQUEST.state + DIVIDER);
		return states.toString();
	}	
	private boolean isRequestState(String checkstate) {
		if(getRequestStates().contains(checkstate)) {
			return true;
		} else {
			return false;
		}
	}
	private boolean isReturnState(String checkstate) {
		if(getReturnStates().contains(checkstate)) {
			return true;
		} else {
			return false;
		}
	}	
	private boolean isDirectState(String checkstate) {
		if(getDirectStates().contains(checkstate)) {
			return true;
		} else {
			return false;
		}
	}
	
	// ### Requesting / Return Mapping ###
	private Map<String, String> mapStates() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(ZappConts.STATES.BUNDLE_ADD_REQUEST.state, ZappConts.STATES.BUNDLE_ADD_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state, ZappConts.STATES.BUNDLE_CHANGE_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state, ZappConts.STATES.BUNDLE_DISABLE_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_ENABLE_REQUEST.state, ZappConts.STATES.BUNDLE_ENABLE_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state, ZappConts.STATES.BUNDLE_DISCARD_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state, ZappConts.STATES.BUNDLE_RELOCATE_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state, ZappConts.STATES.BUNDLE_REPLICATE_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state, ZappConts.STATES.BUNDLE_RELOCATE_RETURN.state);
		map.put(ZappConts.STATES.BUNDLE_REPLICATE_REQUEST.state, ZappConts.STATES.BUNDLE_REPLICATE_RETURN.state);		
		map.put(ZappConts.STATES.BUNDLE_LOCK_REQUEST.state, ZappConts.STATES.BUNDLE_LOCK_RETURN.state);
		return map;
	}
	private String getCounterState(String state) {
		return mapStates().get(state);
	}
	private Map<String, String> mapApprovedStates() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(ZappConts.STATES.BUNDLE_ADD_REQUEST.state, ZappConts.LOGS.ACTION_ADD_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_CHANGE_REQUEST.state, ZappConts.LOGS.ACTION_CHANGE_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_DISABLE_REQUEST.state, ZappConts.LOGS.ACTION_DISABLE_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_ENABLE_REQUEST.state, ZappConts.LOGS.ACTION_ENABLE_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_DISCARD_REQUEST.state, ZappConts.LOGS.ACTION_DISCARD_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state, ZappConts.LOGS.ACTION_RELOCATE_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state, ZappConts.LOGS.ACTION_REPLICATE_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state, ZappConts.LOGS.ACTION_RELOCATE_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_REPLICATE_REQUEST.state, ZappConts.LOGS.ACTION_REPLICATE_APPROVE.log);
		map.put(ZappConts.STATES.BUNDLE_LOCK_REQUEST.state, ZappConts.LOGS.ACTION_LOCK_APPROVE.log);
		return map;
	}	
	private String getCounterApprovedState(String state) {
		return mapApprovedStates().get(state);
	}	
	
	// ###
	public String genCode(ZappContentPar pObj) {
		StringBuffer rawcode = new StringBuffer(); 
		rawcode.append(pObj.getContentid());
		rawcode.append(pObj.getObjType());
		rawcode.append(ZstFwDateUtils.getToday());
		return ZstFwEncodeUtils.encodeString_SHA512(rawcode.toString());
	}
	// ### Delete temp. file ###
	private ZstFwResult deleteTmpFile(ZappAuth pObjAuth, String pObjTmpobjid, ZstFwResult pObjRes) {

		StringBuffer TMPFILE = new StringBuffer();
		TMPFILE.append(ZstFwFileUtils.addSeperator(TEMP_PATH));							// Temp. Mounting Path (in configuration xml file)
		TMPFILE.append(ZstFwFileUtils.addSeperator(pObjTmpobjid));						// Temp. ID	

		try {
			ZstFwFileUtils.delDir(TMPFILE.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pObjRes;
	}
	// ### 
	@SuppressWarnings("unchecked")
	private ZstFwResult getExp(ZappAuth pObjAuth, ZappContentPar pObjContent, String pState, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZappClassification rZappClassification = null;
		List<ZappClassObject> rZappTmpClassList = null;

		/* [Inquiry Temp. Info.]
		 * 
		 */
		ZappTmpObject pZappTmpObj = new ZappTmpObject(); ZappTmpObject rZappTmpObj = null; 
		pZappTmpObj.setTobjid(pObjContent.getContentid()); pZappTmpObj.setTobjtype(pObjContent.getObjType());
		pObjRes = contentMgtService.selectObject(pObjAuth, new ZappTmpObject(ZappKey.getPk(pZappTmpObj)), pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_TEMP", "[getExp] " + messageService.getMessage("ERR_R_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}	
		rZappTmpObj = (ZappTmpObject) pObjRes.getResObj();
		if(rZappTmpObj == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_TEMP", "[getExp] " + messageService.getMessage("ERR_NEXIST_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappClassObject> formlist = new ArrayList<ZappClassObject>(); formlist.add(new ZappClassObject());
		rZappTmpClassList = (List<ZappClassObject>) ZappJSONUtils.cvrtJsonToObj(formlist, rZappTmpObj.getClasses());
		if(rZappTmpClassList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_TEMP", "[getExp] " + messageService.getMessage("ERR_NEXIST_TEMP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// Replicate
		if(pState.equals(ZappConts.STATES.BUNDLE_REPLICATE_ADD_REQUEST.state)) {
			if(ZstFwValidatorUtils.valid(rZappTmpObj.getClasses())) {
				for(int IDX = ZERO; IDX < rZappTmpClassList.size(); IDX++) {
					if(IDX == ONE) {	// Target
						pObjRes = classService.selectObject(pObjAuth, new ZappClassification(rZappTmpClassList.get(IDX).getClassid()), pObjRes);
						if(ZappFinalizing.isSuccess(pObjRes) == false) {
							return ZappFinalizing.finalising("ERR_R_CLASS", "[getExp] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
						}
						rZappClassification = (ZappClassification) pObjRes.getResObj();
					}
				}
			}
		}
		
		// Relocate
		if(pState.equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state) || pState.equals(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state)) {
			if(ZstFwValidatorUtils.valid(rZappTmpObj.getClasses())) {
				for(int IDX = ZERO; IDX < rZappTmpClassList.size(); IDX++) {
					switch (IDX) {
						case 0: // Source
							pObjRes = classService.selectObject(pObjAuth, new ZappClassification(rZappTmpClassList.get(IDX).getClassid()), pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_R_CLASS", "[getExp] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
							rZappClassification = (ZappClassification) pObjRes.getResObj();
							if(pState.equals(ZappConts.STATES.BUNDLE_RELOCATE_REQUEST.state) 
									&& workflowMgtService.doApply(rZappClassification.getWfrequired(), ZappConts.ACTION.RELOCATE) == true) break;
						break;	
						case 1: // Target
							pObjRes = classService.selectObject(pObjAuth, new ZappClassification(rZappTmpClassList.get(IDX).getClassid()), pObjRes);
							if(ZappFinalizing.isSuccess(pObjRes) == false) {
								return ZappFinalizing.finalising("ERR_R_CLASS", "[getExp] " + messageService.getMessage("ERR_R_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
							}
							rZappClassification = (ZappClassification) pObjRes.getResObj();
							if(pState.equals(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state)) break;
						break;	
					}
					
					// Check approver
//					if(rZappClassification.getWfrequired() > ZERO) {
//						ZappGroupUser pZappGroupUser = new ZappGroupUser();
//						pZappGroupUser.setGroupid(rZappClassification.getWfid());
//						pZappGroupUser.setGobjid(pObjAuth.getSessDeptUser().getDeptuserid());	// Approver
//						pObjRes = organService.existObject(pObjAuth, pZappGroupUser, pObjRes);
//						boolean exists = (Boolean) pObjRes.getResObj();
//						if(exists == true) break;
//					}
				}
			}
			
		}
		
		pObjRes.setResObj(rZappClassification);
		
		return pObjRes;
	}
	
	
	// ### Validation ###
	private ZstFwResult validParams(ZappAuth pObjAuth
								  , Object pObjPar
								  , ZstFwResult pObjRes
								  , String pCaller
								  , ZappConts.ACTION pAct) {
		
		/* [Authentication]
		 * 
		 */
		if(pObjAuth == null) {
			return ZappFinalizing.finalising("MISSING_AUTHENTICATION", "[" + pCaller + "][validParams] " + messageService.getMessage("MISSING_AUTHENTICATION",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Input]
		 * 
		 */
		if(pObjPar == null) {
			return ZappFinalizing.finalising("MISSING_INPUT", "[" + pCaller + "][validParams] " + messageService.getMessage("MISSING_INPUT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Content]
		 * 
		 */
		if(pObjPar instanceof ZappContentPar) {
			
			ZappContentPar pvo = (ZappContentPar) pObjPar;
			
			// Content ID
			switch(pAct) {
				case APPROVE: case RETURN: case WITHDRAW: 
					if(!ZstFwValidatorUtils.valid(pvo.getContentid())) {
						return ZappFinalizing.finalising("MISSING_CONTENTID", "[" + pCaller + "][validParams] " + messageService.getMessage("MISSING_CONTENTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}			
			
			// Content type
			switch(pAct) {
				case APPROVE: case RETURN: case WITHDRAW:
					if(!ZstFwValidatorUtils.valid(pvo.getObjType())) {
						return ZappFinalizing.finalising("MISSING_CONTENTTYPE", "[" + pCaller + "][validParams] " + messageService.getMessage("MISSING_CONTENTTYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}

			// Reason
			switch(pAct) {
				case RETURN: 
					if(!ZstFwValidatorUtils.valid(pvo.getZappLockedObject().getReason())) {
						return ZappFinalizing.finalising("ERR_MIS_RETURNREASON", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_RETURNREASON",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					}
				break;
				default:break;
			}
			
		}
		
		return pObjRes;
	}
	
}

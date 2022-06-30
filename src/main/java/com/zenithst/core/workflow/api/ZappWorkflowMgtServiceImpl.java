package com.zenithst.core.workflow.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Results;
import com.zenithst.archive.service.ZArchMFileService;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.service.ZappClassificationService;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.content.api.ZappContentMgtService;
import com.zenithst.core.content.service.ZappContentService;
import com.zenithst.core.content.vo.ZappAdditoryBundle;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappTmpObject;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.organ.service.ZappOrganService;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.workflow.bind.ZappWorkflowBinder;
import com.zenithst.core.workflow.service.ZappWorkflowService;
import com.zenithst.core.workflow.vo.ZappWorkflowObject;
import com.zenithst.framework.domain.ZstFwResult;
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
*	 private ZappWorkflowMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappWorkflowMgtService")
public class ZappWorkflowMgtServiceImpl extends ZappService implements ZappWorkflowMgtService {


	/* Workflow */
	@Autowired
	private ZappWorkflowService workflowService;
	
	/* Content */
	@Autowired
	private ZappContentService contentService;
	@Autowired
	private ZappContentMgtService contentMgtService;	
	
	/* Classification */
	@Autowired
	private ZappClassificationService classificationService;	
	
	/* Organization */
	@Autowired
	private ZappOrganService organService;	
	
	/* Log */
	@Autowired
	private ZappLogMgtService zappLogMgtService;
	
	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Master file */
	@Autowired
	private ZArchMFileService zarchMfileService;	
	
	/* Binder */
	@Autowired
	private ZappWorkflowBinder utilBinder;
	
	
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
			pObjRes = workflowService.cMultiRows(pObjAuth, pObj, pObjRes);
		} 
		else {
			
			/* Validation */
			if(utilBinder.isEmpty(pObj) == false) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[addObject] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			/* Content-Workflow */
			if(pObj instanceof ZappWorkflowObject) {}
			
			pObjRes = workflowService.cSingleRow(pObjAuth, pObj, pObjRes);
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
		
		/* Content-Workflow */
		if(pObj instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.uSingleRow(pObjAuth, pObj, pObjRes);
		
		return pObjRes;
	}
	
	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.uMultiRows(pObjAuth, pObjs, pObjw, pObjRes);
		
		return pObjRes;
	}
	

	public ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {

		/* [ Validation ]
		 * 
		 */
		if((utilBinder.isEmptyPk(pObjs) && utilBinder.isEmpty(pObjs)) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uOrgan] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.uMultiRows(pObjAuth, pObjf, pObjs, pObjw, pObjRes);
		
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
		
		/* Content-Workflow */
		if(pObj instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.cuSingleRow(pObjAuth, pObj, pObjRes);
		
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
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = workflowService.dSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = workflowService.dMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.dMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
		
	}	

	/**
	 *  [Inquire]
	 */
	
	public ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		if(!utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
			pObjRes = workflowService.rSingleRow(pObjAuth, pObjw, pObjRes);
		} else {
			pObjRes = workflowService.rMultiRows(pObjAuth, pObjw, pObjRes);
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
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.rMultiRows(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.rExist(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
		
	}

	public ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.rExist(pObjAuth, pObjf, pObjw, pObjRes);
		
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
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.rCountRows(pObjAuth, pObjw, pObjRes);
		
		return pObjRes;
	}

	public ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [ Validation ]
		 * 
		 */
//		if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
//			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[selectObject] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Content-Workflow */
		if(pObjw instanceof ZappWorkflowObject) {}
		
		pObjRes = workflowService.rCountRows(pObjAuth, pObjf, pObjw, pObjRes);
		
		return pObjRes;
	}	

	/* ************************************************************************************************ */
	
	
	@SuppressWarnings("unchecked")
	public ZstFwResult commenceWorkflow(ZappAuth pObjAuth, Object pObjContent, ZappClassification pObjFolder, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		String CONTENTID = BLANK, CONTENTTYPE = BLANK, STATE = BLANK;
		
		/* [Validation]
		 * 
		 */
		if(pObjContent == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[commenceWorkflow] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(pObjFolder == null) {
			return ZappFinalizing.finalising("ERR_MIS_CLASS", "[commenceWorkflow] " + messageService.getMessage("ERR_MIS_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Get info. by content type]
		 * 
		 */
		if(pObjContent instanceof ZappBundle) {
			ZappBundle pZappBundle = (ZappBundle) pObjContent;
			CONTENTID = pZappBundle.getBundleid();
			CONTENTTYPE = ZappConts.TYPES.CONTENT_BUNDLE.type;
			STATE = ZstFwValidatorUtils.valid(pZappBundle.getState()) ? pZappBundle.getState() : BLANK;
		}
		if(pObjContent instanceof ZappFile) {
			ZappFile pZappFile = (ZappFile) pObjContent;
			CONTENTID = pZappFile.getMfileid();
			CONTENTTYPE = ZappConts.TYPES.CONTENT_FILE.type;
			STATE = ZstFwValidatorUtils.valid(pZappFile.getState()) ? pZappFile.getState() : BLANK;
		}
		
		/* [Get classification info.]
		 * When pObjFolder does not have workflow info, inquiry folder info. again.
		 * When this folder is not required workflow process, return.
		 */
		if(ZstFwValidatorUtils.valid(pObjFolder.getWfid()) == false 
				|| ZstFwValidatorUtils.valid(pObjFolder.getWfrequired()) == false) {
			ZappClassification rZappClassification = (ZappClassification) classificationService.rSingleRow(pObjAuth, new ZappClassification(pObjFolder.getClassid()));
			if(rZappClassification == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[commenceWorkflow] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rZappClassification.getWfrequired() == ZERO) {	// Not applied
				return ZappFinalizing.finalising("ERR_NOSUBJECT_REG", "[commenceWorkflow] " + messageService.getMessage("ERR_NOSUBJECT_REG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pObjFolder.setWfid(rZappClassification.getWfid());
			if(!ZstFwValidatorUtils.valid(pObjFolder.getWfid())) {
				return ZappFinalizing.finalising("ERR_MIS_WFID", "[commenceWorkflow] " + messageService.getMessage("ERR_MIS_WFID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Get group users info. for approval]
		 * 
		 */
		ZappGroupUser pZappGroupUser = new ZappGroupUser();
		pZappGroupUser.setGroupid(pObjFolder.getWfid());
		pObjRes = organService.rMultiRows(pObjAuth, pZappGroupUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUPUSER", "[commenceWorkflow] " + messageService.getMessage("ERR_R_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		List<ZappGroupUser> rZappGroupUserList = (List<ZappGroupUser>) pObjRes.getResObj();
		if(rZappGroupUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUPUSER", "[commenceWorkflow] " + messageService.getMessage("ERR_NEXIST_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappGroupUser> pFirstUserList = null;			// The first approvers
		for(ZappGroupUser vo : rZappGroupUserList) {
			if(vo.getGobjseq() == ONE) {
				if(pFirstUserList == null) {
					pFirstUserList = new ArrayList<ZappGroupUser>();
				}
				pFirstUserList.add(vo);
			}
		}
		if(pFirstUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_1STAPPROVER", "[commenceWorkflow] " + messageService.getMessage("ERR_NEXIST_1STAPPROVER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Assign the first approver to this content]
		 * 
		 */
		List<ZappWorkflowObject> pZappWfObjList = new ArrayList<ZappWorkflowObject>();
		for(ZappGroupUser vo : pFirstUserList) {
			ZappWorkflowObject pZappWfObj = new ZappWorkflowObject();
			pZappWfObj.setContentid(CONTENTID);
			pZappWfObj.setContenttype(CONTENTTYPE);
			pZappWfObj.setWferid(vo.getGobjid());
			pZappWfObj.setWfobjid(ZappKey.getPk(pZappWfObj));
			pZappWfObjList.add(pZappWfObj);
		}
		pObjRes = workflowService.cMultiRows(pObjAuth, pZappWfObjList, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_WFOBJ", "[commenceWorkflow] " + messageService.getMessage("ERR_C_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}	
		
		/* Drafter */
		if(!STATE.equals(ZappConts.STATES.BUNDLE_RELOCATE_ADD_REQUEST.state)) {
			if(CONTENTTYPE.equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
				ZappAdditoryBundle pZappAdditoryBundle = new ZappAdditoryBundle(CONTENTID);
				pZappAdditoryBundle.setDrafter(pObjAuth.getSessDeptUser().getDeptuserid() + DIVIDER + pObjAuth.getSessUser().getName());	
	//			pObjRes = contentMgtService.changeObject(pObjAuth, pZappAdditoryBundle, pObjRes);
	//			if(ZappFinalizing.isSuccess(pObjRes) == false) {
	//				return ZappFinalizing.finalising("ERR_E_BUNDLEEXT", "[changeContent] " + messageService.getMessage("ERR_E_BUNDLEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
	//			}
				contentService.uSingleRow(pObjAuth, pZappAdditoryBundle);
				
			} else if(CONTENTTYPE.equals(ZappConts.TYPES.CONTENT_FILE.type)) {
				ZappFile pZappFile = new ZappFile(CONTENTID);
				pZappFile.setDrafter(pObjAuth.getSessDeptUser().getDeptuserid() + DIVIDER + pObjAuth.getSessUser().getName());	
				pObjRes = contentMgtService.changeObject(pObjAuth, pZappFile, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_FILEEXT", "[changeContent] " + messageService.getMessage("ERR_E_FILEEXT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}			
			}
		}
		
		return pObjRes;
		
	}
	
	public ZstFwResult proceedWorkflow(ZappAuth pObjAuth, Object pObjContent, ZappClassification pObjFolder, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		String CONTENTID = BLANK; String CONTENTTYPE = BLANK;
		
		/* [Validation]
		 * 
		 */
		if(pObjContent == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[proceedWorkflow] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
//		if(pObjFolder == null) {
//			return ZappFinalizing.finalising("ERR_MIS_CLASS", "[proceedWorkflow] " + messageService.getMessage("ERR_MIS_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* [Get info. by content type]
		 * 
		 */
		if(pObjContent instanceof ZappBundle) {
			ZappBundle pZappBundle = (ZappBundle) pObjContent;
			CONTENTID = pZappBundle.getBundleid();
			CONTENTTYPE = ZappConts.TYPES.CONTENT_BUNDLE.type;
		}
		if(pObjContent instanceof ZappFile) {
			ZappFile pZappFile = (ZappFile) pObjContent;
			CONTENTID = pZappFile.getMfileid();
			CONTENTTYPE = ZappConts.TYPES.CONTENT_FILE.type;
		}
		
		/* [Get workflow info.]
		 * 
		 */			
		ZappWorkflowObject pZappWfObject = new ZappWorkflowObject();
		ZappWorkflowObject rZappWfObject = null;
		pZappWfObject.setContentid(CONTENTID);
		pZappWfObject.setContenttype(CONTENTTYPE);
		pObjRes = workflowService.rMultiRows(pObjAuth, pZappWfObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_WFOBJ", "[proceedWorkflow] " + messageService.getMessage("ERR_R_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		@SuppressWarnings("unchecked")
		List<ZappWorkflowObject> rZappWfObjectList = (List<ZappWorkflowObject>) pObjRes.getResObj();
		if(rZappWfObjectList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[proceedWorkflow] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappWorkflowObject vo : rZappWfObjectList) {
			if(vo.getWferid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
				rZappWfObject = vo; break;
			}
		}
		if(rZappWfObject == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_WFOBJ", "[proceedWorkflow] " + messageService.getMessage("ERR_NEXIST_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(!rZappWfObject.getWferid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
			return ZappFinalizing.finalising("ERR_NIDEN_WFID", "[proceedWorkflow] " + messageService.getMessage("ERR_NIDEN_WFID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Get classification info.]
		 * When pObjFolder does not have workflow info, inquiry folder info. again.
		 * When this folder is not required workflow process, return.
		 */
		boolean VALID_FOLDER = true;
		if(pObjFolder == null) {
			VALID_FOLDER = false;
		} else {
			if(!ZstFwValidatorUtils.valid(pObjFolder.getWfid()) || !ZstFwValidatorUtils.valid(pObjFolder.getWfrequired())) {
				VALID_FOLDER = false;
			}
		}
		if(VALID_FOLDER == false) {
			ZappClassification rZappClassification = (ZappClassification) classificationService.rSingleRow(pObjAuth, new ZappClassification(pObjFolder.getClassid()));
			if(rZappClassification == null) {
				return ZappFinalizing.finalising("ERR_NEXIST_CLASS", "[proceedWorkflow] " + messageService.getMessage("ERR_NEXIST_CLASS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			if(rZappClassification.getWfrequired() == ZERO) {	// Not applied
				return ZappFinalizing.finalising("ERR_NOSUBJECT_REG", "[proceedWorkflow] " + messageService.getMessage("ERR_NOSUBJECT_REG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			pObjFolder.setWfid(rZappClassification.getWfid());
			if(!ZstFwValidatorUtils.valid(pObjFolder.getWfid())) {
				return ZappFinalizing.finalising("ERR_MIS_WFID", "[proceedWorkflow] " + messageService.getMessage("ERR_MIS_WFID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* [Get group users info. for approval]
		 * 
		 */
		ZappGroupUser pZappGroupUser = new ZappGroupUser();
		pZappGroupUser.setGroupid(pObjFolder.getWfid());
		pObjRes = organService.rMultiRows(pObjAuth, pZappGroupUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_GROUPUSER", "[proceedWorkflow] " + messageService.getMessage("ERR_R_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		@SuppressWarnings("unchecked")
		List<ZappGroupUser> rZappGroupUserList = (List<ZappGroupUser>) pObjRes.getResObj();
		if(rZappGroupUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_GROUPUSER", "[proceedWorkflow] " + messageService.getMessage("ERR_NEXIST_GROUPUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		ZappGroupUser pCurrentUser = null;					// The current approver
		List<ZappGroupUser> pNextUserList = null;			// The next approver
		for(ZappGroupUser vo : rZappGroupUserList) {
			if(rZappWfObject.getWferid().equals(vo.getGobjid())) {
				pCurrentUser = vo; break;
			}
		}
		for(ZappGroupUser vo : rZappGroupUserList) {
			if(vo.getGobjseq() == (pCurrentUser.getGobjseq() + ONE) ) {
				if(pNextUserList == null) {
					pNextUserList = new ArrayList<ZappGroupUser>();
				}
				pNextUserList.add(vo);
			}
		}

		/* [Remove the current approvers to this content]
		 * 
		 */
		pZappWfObject = new ZappWorkflowObject();
		pZappWfObject.setContentid(CONTENTID);
		pZappWfObject.setContenttype(CONTENTTYPE);
//		pZappWfObject.setWferid(pCurrentUser.getGobjid());
		pObjRes = workflowService.dMultiRows(pObjAuth, pZappWfObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_WFOBJ", "[proceedWorkflow] " + messageService.getMessage("ERR_D_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		

		/* [When the current user is not the final approver]
		 * 
		 */
		if(pNextUserList != null) {

			/* [Assign the next approver to this content]
			 * 
			 */
			List<ZappWorkflowObject> pZappWfObjList = new ArrayList<ZappWorkflowObject>(); 
			for(ZappGroupUser vo : pNextUserList) {
				pZappWfObject = new ZappWorkflowObject();
				pZappWfObject.setContentid(CONTENTID);
				pZappWfObject.setContenttype(CONTENTTYPE);
				pZappWfObject.setWferid(vo.getGobjid());
				pZappWfObject.setWfobjid(ZappKey.getPk(pZappWfObject));
				pZappWfObjList.add(pZappWfObject);
			}
			pObjRes = workflowService.cMultiRows(pObjAuth, pZappWfObjList, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_WFOBJ", "[proceedWorkflow] " + messageService.getMessage("ERR_R_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}	
			
		}
		else {
			
			if(pObjContent instanceof ZappBundle) {
				ZappBundle pZappBundle = new ZappBundle(CONTENTID);
				pZappBundle.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				pObjRes = changeObject(pObjAuth, pZappBundle, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == false) {
					return ZappFinalizing.finalising("ERR_E_BUNDLE", "[continueWorkflow] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjContent instanceof ZappFile) {
				ZArchResult zArchResult = new ZArchResult();
				ZArchMFile pZArchMFile = new ZArchMFile(CONTENTID);
				pZArchMFile.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				try {
					zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
				} catch (Exception e) {
					return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
				}			
				if(ZappFinalizing.isSuccess(zArchResult) == false) {
					return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
				}
				pObjRes.setResCode((String) zArchResult.getCode());
				pObjRes.setResMessage((String) zArchResult.getMessage());
			}			
		}
		
		return pObjRes;

	}

	/**
	 * 
	 */
	public ZstFwResult haltWorkflow(ZappAuth pObjAuth, Object pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		String CONTENTID = BLANK; String CONTENTTYPE = BLANK;
		
		/* [Validation]
		 * 
		 */
		if(pObjContent == null) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[haltWorkflow] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		/* [Get info. by content type]
		 * 
		 */
		if(pObjContent instanceof ZappBundle) {
			ZappBundle pZappBundle = (ZappBundle) pObjContent;
			CONTENTID = pZappBundle.getBundleid();
			CONTENTTYPE = ZappConts.TYPES.CONTENT_BUNDLE.type;
		}
		if(pObjContent instanceof ZappFile) {
			ZappFile pZappFile = (ZappFile) pObjContent;
			CONTENTID = pZappFile.getMfileid();
			CONTENTTYPE = ZappConts.TYPES.CONTENT_FILE.type;
		}
		
		/* [Delete workflow info.]
		 * 
		 */
		ZappWorkflowObject pZappWfObject = new ZappWorkflowObject();
		pZappWfObject.setContentid(CONTENTID);
		pZappWfObject.setContenttype(CONTENTTYPE);
//		pZappWfObject.setWferid(pCurrentUser.getGobjid());
		pObjRes = workflowService.dMultiRows(pObjAuth, pZappWfObject, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_D_WFOBJ", "[haltWorkflow] " + messageService.getMessage("ERR_D_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		
		
		/* [Delete temp. info.]
		 * 
		 */
		ZappTmpObject pZappTmpObj = new ZappTmpObject();
		pZappTmpObj.setTobjid(CONTENTID);
		pZappTmpObj.setTobjtype(CONTENTTYPE);
		pZappTmpObj.setHandlerid(pObjAuth.getSessDeptUser().getDeptuserid());
		pObjRes = contentService.dMultiRows(pObjAuth, pZappTmpObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_D_WFOBJ", "[haltWorkflow] " + messageService.getMessage("ERR_D_WFOBJ",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}		

		/* [Update states]
		 * 
		 */
		if(pObjContent instanceof ZappBundle) {
			ZappBundle pZappBundle = new ZappBundle(CONTENTID);
			pZappBundle.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
			pObjRes = changeObject(pObjAuth, pZappBundle, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_E_BUNDLE", "[continueWorkflow] " + messageService.getMessage("ERR_E_BUNDLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		if(pObjContent instanceof ZappFile) {
			ZArchResult zArchResult = new ZArchResult();
			ZArchMFile pZArchMFile = new ZArchMFile(CONTENTID);
			pZArchMFile.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
			try {
				zArchResult = zarchMfileService.uSingleRow(pZArchMFile);
			} catch (Exception e) {
				return ZappFinalizing.finalising_Archive(Results.FAIL_TO_UPDATE_MFILE.result, pObjAuth.getObjlang());
			}			
			if(ZappFinalizing.isSuccess(zArchResult) == false) {
				return ZappFinalizing.finalising_Archive(zArchResult.getCode(), pObjAuth.getObjlang());
			}
			pObjRes.setResCode((String) zArchResult.getCode());
			pObjRes.setResMessage((String) zArchResult.getMessage());
		}			

		
		return pObjRes;
	}
	
	/**
	 * 
	 */
	public boolean doApply(int pWfreqired, ZappConts.ACTION pAction) {
		
		boolean APPLIED = false;
		
		if(pWfreqired == ZappConts.WORKFLOWS.WF_OBJECT_NONE.iwt) {
			return false;
		}
		if(pWfreqired == ZappConts.WORKFLOWS.WF_OBJECT_ALL.iwt) {
			return true;
		}
		
		switch(pAction) {
			case ADD:
				if((pWfreqired & ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt) == ZappConts.WORKFLOWS.WF_OBJECT_ADD.iwt) {
					return true;
				}
			break;
			case CHANGE: case RELOCATE: case REPLICATE:
				if((pWfreqired & ZappConts.WORKFLOWS.WF_OBJECT_EDIT.iwt) == ZappConts.WORKFLOWS.WF_OBJECT_EDIT.iwt) {
					return true;
				}
			break;
			case DISABLE: case DISCARD:
				if((pWfreqired & ZappConts.WORKFLOWS.WF_OBJECT_DELETE.iwt) == ZappConts.WORKFLOWS.WF_OBJECT_DELETE.iwt) {
					return true;
				}
			break;
			default:
		}
		
		return APPLIED;
	}
	
	
	// ### Logging ###
	private ZstFwResult leaveLog(ZappAuth pObjAuth, String pLogType, String pLogAction, Map<String, Object> pLogMap, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZappContentLog pLogObject = new ZappContentLog();
		pLogObject.setLogtype(pLogType);
		pLogObject.setAction(pLogAction);
		pLogObject.setMaplogs(pLogMap);
		pObjRes = zappLogMgtService.leaveLog(pObjAuth, pLogObject, pObjRes);
		
		return pObjRes;
	}

}

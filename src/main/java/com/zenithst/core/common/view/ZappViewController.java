package com.zenithst.core.common.view;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.api.ZappClassificationMgtService;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.content.api.ZappContentMgtService;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.system.api.ZappSystemMgtService;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

@Controller
public class ZappViewController extends ZappController {
	
	@Autowired
	private ZappContentMgtService zappContentMgtService;

	@Autowired
	private ZappOrganMgtService zappOrganMgtService;
	
	/* System */
	@Autowired
	private ZappSystemMgtService zappSystemService;
	
	/* Authentication */
	@Autowired
	private ZappClassificationMgtService zappClassMgtService;
	
	/**
	 * Location info.
	 */
	private enum LOCATION {
		
		/* Authentication */
		login("login", "/auth/ZecmLogin"),
		
		/* Content */
		docMain("docMain", "/user/ZecmDocMain"),
		nodeTree("nodeTree", "/user/ZecmNodeTree"),
		fileReg("fileReg", "/user/ZecmFileReg"),
		fileEdit("fileEdit", "/user/ZecmFileEdit"),
		fileInfo("fileInfo", "/user/ZecmFileInfo"),
		linkReg("linkReg", "/user/ZecmLinkReg"),
		linkEdit("linkEdit", "/user/ZecmLinkEdit"),
		linkInfo("linkInfo", "/user/ZecmLinkInfo"),
		downTest("downTest", "/user/downTest"),
		fldTreePop("fldTreePop", "/user/ZecmFldTree"),
		authPop("authPop", "/user/ZecmAuthMng"),
		folderRegN4("folderRegN2", "/user/ZecmCFolderReg"),			// 협업문서함
		folderEditN4("folderEditN2", "/user/ZecmCFolderEdit"),		// 협업 문서함
		folderReg("folderReg", "/user/ZecmFolderReg"),
		folderEdit("folderEdit", "/user/ZecmFolderEdit"),
		reasonReg("reasonReg", "/user/ZecmReasonReg"),
		docComparePop("docComparePop", "/user/ZecmDocComparePop"),
		cmtRegPop("cmtRegPop", "/user/ZecmCmtPop"),
		
		pdfView("pdfView", "/user/ZecmPdfViewer"),
		pdfManual("pdfManual", "/user/ZecmPdfManual"),

		/* ClassMng */
		ClassMng("ClassMng", "/admin/ZecmClassMng"),
		NodeMng("NodeMng", "/admin/ZecmNodeMng"),
		NodeDeptMng("NodeDeptMng", "/admin/ZecmNodeDeptMng"),
		NodeDocMng("NodeDocMng", "/admin/ZecmNodeDocMng"),
		DocTypeMng("DocTypeMng", "/admin/ZecmDocTypeMng"),

		/* Organ */
		OrganMng("OrganMng", "/admin/ZecmOrganMng"),
		DeptMng("DeptMng", "/admin/ZecmDeptUserMng"),
		GroupUserMng("GroupUserMng", "/admin/ZecmGroupUserMng"),
		PerGroupMng("PerGroupMng", "/admin/ZecmPerGroupMng"),
		
		/* System */
		CodeMng("CodeMng", "/admin/ZecmCodeMng"),
		CodeMng2("CodeMng2", "/admin/ZecmCodeMng2"),
		EnvMng("EnvMng", "/admin/ZecmEnvMng"),
		FormatMng("FormatMng", "/admin/ZecmFormatMng"),
		TaskMng("TaskMng", "/admin/ZecmTaskMng"),
		CabinetMng("CabinetMng", "/admin/ZecmCabinetMng"),

		LogMng("LogMng", "/admin/ZecmLogMng"),
		StatMng("StatMng", "/admin/ZecmStatMng"),
		ProcMon("ProcMon", "/admin/ZecmProcMon"),
		
		Error("Error", "/cmnns/error")
		
		;
		
		public final String vid;
		public final String location;
		
		
		LOCATION(String vid, String location) {
			this.vid = vid;
			this.location = location;
		}
	}
	
	/**
	 * Welcome Page (Index)
	 */
	@RequestMapping(value = "/")
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(LOCATION.login.location);
		return mav;
	}	

	/**
	 * 
	 * @param vid
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/go/{vid}", method = RequestMethod.GET)
	public ModelAndView cmnt(@PathVariable String vid, HttpServletRequest request, HttpSession session) {

		logger.info("**** VID = [" + vid + "]");
		
		ModelAndView mav = new ModelAndView();
		ZappAuth pObjAuth = getAuth(session);
		
		if(pObjAuth == null && !vid.equals(LOCATION.login.vid)) {
			mav.addObject("code", "SESSION_EXPIRED");
			mav.setViewName(LOCATION.Error.location);//세션 없을때 로그인 페이지로 전환
			return mav;
		}

		logger.info("=== request.getParameter(lang): " + request.getParameter("lang"));
		
		if(request.getParameter("lang") != null && !request.getParameter("lang").equals("")) {
			session.setAttribute("sessLang", request.getParameter("lang"));
		}
		
		logger.info("=== session.getAttribute(sessLang): " + session.getAttribute("sessLang"));		

		for(LOCATION loc : LOCATION.values()) {
			if(loc.vid.equals(vid)) {
				mav.setViewName(loc.location);
				break;
			}
		}
		
		// 로그인
		if(vid.equals(LOCATION.login.vid)) {
			//logger.debug("=== request.getParameter(lang): " + request.getParameter("lang"));
			//session.setAttribute("sessLang", request.getParameter("lang"));
			//logger.debug("=== session.getAttribute(sessLang): " + session.getAttribute("sessLang"));
			return mav;
		}

		// 컨텐츠 등록
		if(vid.equals(LOCATION.fileReg.vid) || vid.equals(LOCATION.linkReg.vid)) {
			mav.addObject("docNo", zappContentMgtService.getContentNo(pObjAuth));
			return mav;
		}
		
		// 폴더 등록
		if(vid.equals(LOCATION.folderReg.vid)) {
			String type = ZstFwValidatorUtils.fixNullString(request.getParameter("type"), "");
			logger.debug("=== type:" + type);
			if(type.equals("N4")) { //협업문서함
				mav.setViewName(LOCATION.folderRegN4.location);
			} else {
				mav.setViewName(LOCATION.folderReg.location);
			}
			return mav;
		}
		
		// 폴더 수정
		if(vid.equals(LOCATION.folderEdit.vid)) {
			String type = ZstFwValidatorUtils.fixNullString(request.getParameter("type"), "");
			if(type.equals("N4")) { //협업문서함
				mav.setViewName(LOCATION.folderEditN4.location);
			} else {
				mav.setViewName(LOCATION.folderEdit.location);
			}
			return mav;
		}
		
		// 메인화면
		if(vid.equals(LOCATION.docMain.vid)) {
			
			ZstFwResult pObjRes = new ZstFwResult();
				pObjRes.setResCode(SUCCESS);
			
			try {
				
				ZappCode codevo = new ZappCode();
					codevo.setCompanyid(pObjAuth.getObjCompanyid());
					codevo.setCodekey("RETENTION");//보존년한
					codevo.setCodevalue("999");//영구
				
					pObjRes = zappSystemService.selectObject(pObjAuth, codevo, pObjRes);
					
				ZappCode rZappCode = null;
					if(ZappFinalizing.isSuccess(pObjRes) == true) {
						List<ZappCode> rZappCodeList = (List<ZappCode>) pObjRes.getResObj();
						
						for(ZappCode vo : rZappCodeList) {
							rZappCode = vo;
						}
						
					}
					
					mav.addObject("RETENTION_DEFAULT", rZappCode.getCodeid());
					
					
					
				ZappClassification pZappClass = new ZappClassification();
					pZappClass.setCompanyid(pObjAuth.getObjCompanyid());
					pZappClass.setTypes("03"); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
					pZappClass.setCode("GENERAL");//일반문서
					
					pObjRes = zappClassMgtService.selectObject(pObjAuth, pZappClass, pObjRes);
					
				ZappClassification rZappClassification = null;
					if(ZappFinalizing.isSuccess(pObjRes) == true) {
						
						List<ZappClassification> rZappClassificationList = (List<ZappClassification>) pObjRes.getResObj();
						
						for(ZappClassification vo : rZappClassificationList) {
							rZappClassification = vo;
						}
						
					}	
					
					mav.addObject("DOCTYPE_DEFAULT", rZappClassification.getClassid());
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			mav.setViewName(LOCATION.docMain.location);
			
			return mav;
		}		
		return mav;
	}
	
	/**
	 * Admin
	 * @param vid
	 * @param request
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/admin/{vid}", method = RequestMethod.GET)
	public ModelAndView admin(@PathVariable String vid, HttpServletRequest request, HttpSession session) {

		logger.info("**** VID = [" + vid + "]");
		
		ModelAndView mav = new ModelAndView();
		ZappAuth pObjAuth = getAuth(session);

		if(pObjAuth == null && !vid.equals(LOCATION.login.vid)) {
			mav.addObject("code", "SESSION_EXPIRED");
			mav.setViewName(LOCATION.Error.location);//세션 없을때 로그인 페이지로 전환
			return mav;
		}
		
		for(LOCATION loc : LOCATION.values()) {
			if(loc.vid.equals(vid)) {
				mav.setViewName(loc.location);
				break;
			}
		}
		
		return mav;

	}
	
	@RequestMapping(value = "/cmnns/error")
	public ModelAndView error(HttpServletRequest request) {		
		logger.info("Error Page");
		String code = request.getParameter("code");
		logger.info("Error code : {}",code);
		
		ModelAndView mav = new ModelAndView();	
		mav.addObject("code", code);
		mav.setViewName("/common/error");
		
		return mav;
	}	
}

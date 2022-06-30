package com.zenithst.core.central.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenithst.archive.api.ZArchFileMgtService;
import com.zenithst.archive.service.ZArchFileService;
import com.zenithst.archive.service.ZArchVersionService;
import com.zenithst.archive.util.CryptoNUtil;
import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.archive.vo.ZArchTask;
import com.zenithst.archive.vo.ZArchVersion;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.api.ZappAuthenticationMgtService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.central.vo.ZappCentral;
import com.zenithst.core.classification.api.ZappClassificationMgtService;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.classification.vo.ZappClassificationPar;
import com.zenithst.core.classification.vo.ZappClassificationRes;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.core.content.api.ZappContentMgtService;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappLockedObject;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.core.workflow.api.ZappWorkflowMgtService;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;

/**  
* <pre>
* <b>
* 1) Description : 권한 정보를 관리한다. <br>
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

@RestController
@RequestMapping(value ="")
public class ZappCentralController extends ZappController {
	
	/* Authentication */
	@Autowired
	private ZappAuthenticationMgtService authMgtService;
	
	/* Authentication */
	@Autowired
	private ZappClassificationMgtService classMgtService;

	@Autowired
	private ZappContentMgtService contentMgtService;

	@Resource
	private ZArchFileService zArchFileService;
	
	@Autowired
	private ZArchVersionService zArchVersionService;
	
	@Resource
	private ZArchFileMgtService zArchFileMgtService;

	@Autowired
	private ZappOrganMgtService organService;
	
	@Autowired
	private ZappWorkflowMgtService workflowService;

	@Value("#{archiveconfig['UPLOAD_TEMP_PATH']}")
	private String upTempPath ;

//	private String testCompanyId = "B5D0CBE66E55FF2DFB5FEEF9AED48FECBE16C9294DE9C86A75DF4759D914500D";
//	private String testDeptId = "FC14FCD8DB1FCE33D036928D31DD8CC0AB49546C3ACAD133AA18283700C9D4C6";
//	private String testEmpNo = "U01";
//	private String testLoginId = "U01";
//	private String testPasswd = "U01";
	
	//private String testCompanyId = "FB7EE86AA31D87723A5BD09E6A20AF1F1A63E287321EF4F5DE410246EDF4C242";
	//private String testDeptId = "8782E478691C87DC343F75B7E18D47D1480258F823C06CCD8D4F2BFAF24C448A"; //연구소
	//private String testTaskId = "94EE059335E587E501CC4BF90613E0814F00A7B08BC7C648FD865A2AF6A22CC2";
	//private String testEmpNo = "10012";
	//private String testLoginId = "jwj";
	//private String testPasswd = "jwj!@#";
	//private String testUserId = "65E910A4667B12FE1A54F704468815BB16503695CF061359EFD533516EE62301";
	static private String gTaskId = "";
	
	private String gDeptUserId = "";
	private String gDeptId = "";

	/**
	 * <p><b>
	 * 로그인
	 * </b></p>
	 */
	@RequestMapping(value = "/main/login/login.do", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> login(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model) {
	public String login(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		logger.debug("=== /main/login/login.do");
		
		byte[] decodedBytes = Base64.getDecoder().decode(pIn.getUserId());
		String loginId = new String(decodedBytes);

		logger.debug("=== pIn.getUserId [" + pIn.getUserId() + "], pIn.getPassword [" + pIn.getPassword() + "]");
		logger.debug("=== pIn.getCompanycode(): " + pIn.getCompanycode());

		if (pIn.getPassword() != null) {
			decodedBytes = Base64.getDecoder().decode(pIn.getPassword());
			String password = new String(decodedBytes);
			logger.info("=== loginId [" + loginId + "], password [" + password + "]");
		}
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		/*
		ZappAuth pZappAuth = new ZappAuth();
//		if(ZstFwValidatorUtils.fixNullString(pIn.getObjIsTest(), NO).equals(YES)) {
			pZappAuth.setObjCompanyid(testCompanyId);
			pZappAuth.setObjDeptid(testDeptId);
			pZappAuth.setObjEmpno(testEmpNo);
			pZappAuth.setObjLoginid(testLoginId);
			pZappAuth.setObjPasswd(testPasswd);
			result = getAuth_Test(pZappAuth, session, request, result);
//		} 
		*/
		ZappAuth pZappAuth = new ZappAuth();
		pZappAuth = getAuth(session);
		
		gTaskId = getTaskId(session);
		
		//if (pIn.getUserId() != null && pIn.getUserId() != "") {
		if (pZappAuth == null) {
			// 로그인 처리
			loginpass(pIn, bindingResult, model, session);
		} else {
		}
				
		//System.out.println("=== pZappAuth.getObjLoginid(): " + pZappAuth.getObjLoginid());
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> rows = new HashMap<String, String>();

		rows.put("password_ticket", "DM_TICKET\u003dT0JKIE5VTEwgMAoxMwp2ZXJzaW9uIElOVCBTIDAKMwpmbGFncyBJTlQgUyAwCjEKc2VxdWVuY2VfbnVtIElOVCBTIDAKMTUKY3JlYXRlX3RpbWUgSU5UIFMgMAoxNjA5NzI0Mzg0CmV4cGlyZV90aW1lIElOVCBTIDAKMTYwOTcyNDY4NApkb21haW4gSU5UIFMgMAowCnVzZXJfbmFtZSBTVFJJTkcgUyAwCkEgMyB5d3MKcGFzc3dvcmQgSU5UIFMgMAowCmRvY2Jhc2VfbmFtZSBTVFJJTkcgUyAwCkEgNiBFQ01SRVAKaG9zdF9uYW1lIFNUUklORyBTIDAKQSAxMCBkcmVhbWludGVrCnNlcnZlcl9uYW1lIFNUUklORyBTIDAKQSA2IEVDTVJFUApzaWduYXR1cmVfbGVuIElOVCBTIDAKMTEyCnNpZ25hdHVyZSBTVFJJTkcgUyAwCkEgMTEyIEFBQUFFSlIraUQzTHhGL1VQbjFYM2N0WitCdXVadVNLSDVlUXdoVi9ZUXVwYy9iR2t1MEl2ak5zc0VaSEhVR1ZmV3hPQWprL1gvdURVNjNqOEwxTDJmSW9tWnpINmJWZEJVNUYxM1FPcFBjaDh3dksK");
		rows.put("description", "ECM팀");
		rows.put("userGroupName", "00001000");
		rows.put("userId", loginId);
		rows.put("userName", "양우석");

		list.add(rows);

		resMap.put("totalCount", 1);
		resMap.put("list", list);
		resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
		resMap.put("errcode", "0");			

		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		logger.info("========= FINISH: main/login/login.do ==========");
		
		return retStr;
	}	
	
	
	@RequestMapping(value = "/main/login/loginpass.do", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> loginpass(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model) {
	public String loginpass(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> rows = new HashMap<String, String>();
		String retStr = "";

		ZappAuth pZappAuth = new ZappAuth();
		ZstFwResult pObjRes = new ZstFwResult();
		String companyCode = "";
		String empNo = "";
		String userId = "";
		String deptUserId = "";
		String passwd = "";

		logger.debug("=================================");
		logger.debug("======= login/loginpass.do ======");
		
		try {
			logger.debug("=== pIn.getCompanycode(): " + pIn.getCompanycode());
			logger.debug("=== pIn.getUserId(): " + pIn.getUserId());
			logger.debug("=== pIn.getPassword(): " + pIn.getPassword());
			
			byte[] decodedBytes = Base64.getDecoder().decode(pIn.getUserId());
			String loginId = new String(decodedBytes);
			logger.debug("=== Decode userId: " + loginId);
			
			if (pIn.getPassword() != null) {
				decodedBytes = Base64.getDecoder().decode(pIn.getPassword());
				passwd = new String(decodedBytes);
				logger.debug("=== Decode password: " + passwd);
			}		

			//if (pIn.getCompanycode() != null) {
			//	decodedBytes = Base64.getDecoder().decode(pIn.getCompanycode());
			//	companyCode = new String(decodedBytes);
			//	logger.debug("=== Decode companyCode: " + companyCode);
			//}
			companyCode = pIn.getCompanycode();

			//초기화
			pObjRes.setResCode(SUCCESS);

			/* User */
			ZappUser rZappUser = null;
			ZappUser pZappUser = new ZappUser(null, null, pZappAuth.getObjLoginid(), null);
			pZappUser.setIsactive(YES);
			pZappUser.setCompanyid(companyCode);
			pZappUser.setLoginid(loginId);
			
			pObjRes = organService.selectObject(pZappAuth, pZappUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				System.out.println("=== organService.selectObject Error");
			}
			System.out.println("=== organService.selectObject END");
			
			List<ZappUser> rZappUserList = (List<ZappUser>) pObjRes.getResObj();
			if(rZappUserList == null) {
				System.out.println("=== organService.selectObject Error");
			}
			System.out.println("=== rZappUserList size: " + rZappUserList.size());
			for(ZappUser vo : rZappUserList) {
				rZappUser = vo;
				
				empNo = rZappUser.getEmpno();
				//System.out.println("=== rZappUser.getEmpno(): " + rZappUser.getEmpno());
				System.out.println("=== rZappUser.getCompanyid(): " + rZappUser.getCompanyid());
				//System.out.println("=== rZappUser.getName(): " + rZappUser.getName());
				//System.out.println("=== rZappUser.getUserid(): " + rZappUser.getUserid());
				pZappAuth.setSessUser(vo);
			}

			/* Department User */
			pZappAuth.setObjLoginid(loginId);
			//pZappAuth.setObjPasswd(passwd);
			
			List<ZappDeptUserExtend> rZappDeptUserList = null;
			List<ZappDeptUser> rZappOnlyDeptUserList = new ArrayList<ZappDeptUser>();
			ZappDeptUser pZappDeptUser = new ZappDeptUser(null, rZappUser.getUserid());
			pZappDeptUser.setIsactive(YES);
			pObjRes = organService.selectObjectExtend(pZappAuth, pZappDeptUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				System.out.println("=== organService.selectObjectExtend Error");
			}
			System.out.println("=== organService.selectObjectExtend END");
			
			rZappDeptUserList = (List<ZappDeptUserExtend>) pObjRes.getResObj();
			if(rZappDeptUserList == null) {
				System.out.println("=== organService.selectObjectExtend Error");
			}
			System.out.println("=== rZappDeptUserList size: " + rZappDeptUserList.size());
			
			for(ZappDeptUserExtend vo : rZappDeptUserList) {
				pZappAuth.setSessDeptUser(vo);
				ZappDeptUser ivo = new ZappDeptUser();
				BeanUtils.copyProperties(vo, ivo);
				
				gDeptUserId = ivo.getDeptuserid();
				
				pZappAuth.setSessOnlyDeptUser(ivo);
				break;
			}
			for(ZappDeptUserExtend vo : rZappDeptUserList) {
				ZappDeptUser ivo = new ZappDeptUser();
				BeanUtils.copyProperties(vo, ivo);
				rZappOnlyDeptUserList.add(ivo);
				//rSessAclObjList.add(vo.getDeptuserid());
				//rSessAclObjList.add(vo.getDeptid());
			}
			pZappAuth.setSessDeptUsers(rZappDeptUserList);
			pZappAuth.setSessOnlyDeptUsers(rZappOnlyDeptUserList);

			System.out.println("=== pZappAuth.getSessUser().getCompanyid(): " + pZappAuth.getSessUser().getCompanyid());
			System.out.println("=== companyCode: " + companyCode);

			//ZappAuth pZappAuth = new ZappAuth();
			//pZappAuth.setObjCompanyid(pZappAuth.getSessUser().getCompanyid());
			pZappAuth.setObjCompanyid(companyCode);			
			pZappAuth.setObjDeptid(gDeptUserId);
			pZappAuth.setObjEmpno(empNo);
			pZappAuth.setObjLoginid(loginId);
			pZappAuth.setObjPasswd(passwd);

			//result = getAuth_Test(pZappAuth, session, request, result);
			//result = authMgtService.connect_through_cs(pZappAuth, session, request, result);
			result = authMgtService.connect_through_web(pZappAuth, session, request, result);

			logger.debug("=== resObj:" + result.getResObj().toString());
			//ZappAuth rZappAuth = new ZappAuth();
			//rZappAuth = (ZappAuth)session.getAttribute("Authentication");  			// Storing authentication information in session
			
			
			pZappAuth = getAuth(session);
			
			List<ZArchTask> taskList = pZappAuth.getSessTasks();
			logger.debug("== taskList size:" + taskList.size());
			for (ZArchTask archTask : taskList) {
				logger.debug("=== taskcode:" + archTask.getCode() + ", taskId:" + archTask.getTaskid());
				if (archTask.getCode().equals("EDMS")) {
					gTaskId = archTask.getTaskid();
				}
			}
			
			//String _JWT = pZappAuth.getObjJwt();
			//logger.debug("=== loginpass, _JWT: " + _JWT);
			logger.debug("=== pZappAuth.getSessUser().getName(): " + pZappAuth.getSessUser().getName());
	
			// {"totalCount":"1","list":[{"password_ticket":"1234","description":"ECM팀","userGroupName":"00001000","userId":"yws","userName":"양우석"}],"errmsg":"정상 처리 되었습니다.\n(The operation is completed.)","errcode":"0"}
			rows.put("password_ticket", "1234");
			//rows.put("token", _JWT);
			rows.put("description", "ECM팀");
			rows.put("userGroupName", "00001000");
			rows.put("userId", loginId);
			rows.put("userName", pZappAuth.getSessUser().getName());
	
			list.add(rows);
	
			resMap.put("totalCount", 1);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
			resMap.put("errcode", "0");
			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug("===== loginpass.do retStr:" + retStr);
		return retStr;
		
	}	

	@RequestMapping(value = "/main/login/logout.do", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String logout(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		String retStr = "";

		try {
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			result = authMgtService.disconnect_through_web(pZappAuth, session, request, result);
	
			resMap.put("totalCount", 1);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
			resMap.put("errcode", "0");			
			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			logger.debug("===== logout.do retStr:" + retStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retStr;		
	}	

	@RequestMapping(value = "/cmnns/error", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> cmnError(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model) {
	public String cmnError(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();

		logger.info("=================================================================================");
		logger.info("========= RequestURI: cmnns/error =========");
		logger.info("=================================================================================");

		resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
		resMap.put("errcode", "0");			
		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		return retStr;
		
	}	
	
	/**
	 * <p><b>
	 * 최신문서 조회
	 * </b></p>
	 */
	@RequestMapping(value = "/folder/common/menuFolder.do", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> menuFolder(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String menuFolder(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		ZappAuth pZappAuth = new ZappAuth();

		byte[] decodedBytes = Base64.getDecoder().decode(pIn.getUser_id());
		String loginId = new String(decodedBytes);
		logger.info("=== loginId : " + loginId);
		
		pZappAuth = getAuth(session);
		
		// 01: 일반,02: 부서관리자,03: 기업관리자,04: 시스템관리자) 
		String userType = pZappAuth.getSessDeptUser().getUsertype();
		logger.debug("=== userType:" +  userType);
		
		// 폴더권한 (0 : 조회X+등록X, 1 : 조회O+등록X, 2 : 조회O+등록O) 
		String permitLevel = pIn.getPermit_level();
		
		logger.info("=== UserId : " + pIn.getUserId());
		logger.info("=== user_id : " + pIn.getUser_id());
		logger.info("=== rid: " + pIn.getRid());
		logger.info("=== r_folder_id: " + pIn.getR_folder_id());
		logger.info("=== doc_box_name: " + pIn.getDoc_box_name());
		logger.info("=== folder_level: " + pIn.getFolder_level());
		logger.info("=== permit_level: " + permitLevel);
	
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> rows = new HashMap<String, String>();

		if (pIn.getR_folder_id().equals("eim_qlink")) { // 바로가기
			rows.put("FOLDER_RENAME_YN", 		"0");	//012:폴더명변경
			rows.put("FOLDER_DEL_YN", 			"0");	//013:삭제	
		} else if (pIn.getR_folder_id().equals("오프라인 문서")) {
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("approval_mgt")) { // 승인관리
			rows.put("FOLDER_RENAME_YN", 		"0");	//012:폴더명변경
			rows.put("FOLDER_DEL_YN", 			"0");	//013:삭제	
		} else if (pIn.getR_folder_id().equals("export_approval_request_doc")) {
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("export_approval_complete_doc")) {
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("export_approval_return_doc")) { // 반출승인 반려문서
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("export_approval_doc")) { // 반출승인할 문서
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("export_req_history_doc")) { // 반출요청 이력조회
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("export_history_doc")) { // 반출현황 조회
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("security_lv_change_request_doc")) { // 보안등급변경 요청문서
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("security_lv_aporoval_request_doc")) { // 보안등급변경 승인대상문서
			//list.add(rows);
		} else if (pIn.getR_folder_id().equals("read_approval_doc")) { // 열람권한 승인할문서
			//list.add(rows);
		}
		else if (pIn.getR_folder_id().equals("common_doc") 		// 전사문서함
				|| pIn.getR_folder_id().equals("department_doc") // 부서문서함
				|| pIn.getR_folder_id().equals("public_doc") 	// 협업문서함
				|| pIn.getR_folder_id().equals("personal_doc")	// 개인문서함
				|| pIn.getR_folder_id().equals("favorite_doc") 	// 즐겨찾기업무함
				) {
			rows.put("FOLDER_RENAME_YN", 		"0");	//012:폴더명변경
			rows.put("FOLDER_DEL_YN", 			"0");	//013:삭제	
			rows.put("FAVORITES_YN", 			"0");	//022:즐겨찾기에 추가		
		}
		else if (pIn.getR_folder_id().equals("eim_qlink_own_doc") 
				|| pIn.getR_folder_id().equals("eim_qlink_recent_checkout_doc") 
				|| pIn.getR_folder_id().equals("eim_qlink_imp_doc") 
				|| pIn.getR_folder_id().equals("eim_qlink_teamwork_doc")
				|| pIn.getR_folder_id().equals("eim_qlink_coaching_doc") 
				|| pIn.getR_folder_id().equals("eim_qlink_post_doc")
				|| pIn.getR_folder_id().equals("eim_qlink_recycle_doc") 
				|| pIn.getR_folder_id().equals("eim_qlink_rec_doc")
				|| pIn.getR_folder_id().equals("qlink_report_mgt")
				|| pIn.getR_folder_id().equals("qlink_report_set_doc") 
				|| pIn.getR_folder_id().equals("qlink_reported_doc")
				|| pIn.getR_folder_id().equals("qlink_editing_doc") 
				) {
			
			if (pIn.getR_folder_id().equals("eim_qlink_own_doc")) {
				rows.put("DESCRIPTION", "소유문서");				
			} else if (pIn.getR_folder_id().equals("eim_qlink_recent_checkout_doc")) {
				rows.put("DESCRIPTION", "최근열람문서");				
			} else if (pIn.getR_folder_id().equals("eim_qlink_imp_doc")) {
				rows.put("DESCRIPTION", "중요문서");				
			} else if (pIn.getR_folder_id().equals("eim_qlink_teamwork_doc")) {
				rows.put("DESCRIPTION", "편집권한문서");				
			} else if (pIn.getR_folder_id().equals("eim_qlink_coaching_doc")) {
				rows.put("DESCRIPTION", "열람지정문서");				
			} else if (pIn.getR_folder_id().equals("qlink_report_mgt")) {
				rows.put("DESCRIPTION", "보고서관리");				
			} else if (pIn.getR_folder_id().equals("qlink_report_set_doc")) {
				rows.put("DESCRIPTION", "보고서설정문서");				
			} else if (pIn.getR_folder_id().equals("qlink_reported_doc")) {
				rows.put("DESCRIPTION", "보고받은문서");				
			} else if (pIn.getR_folder_id().equals("qlink_editing_doc")) {
				rows.put("DESCRIPTION", "편집중문서");				
			} else if (pIn.getR_folder_id().equals("eim_qlink_post_doc")) {
				rows.put("DESCRIPTION", "개인메일첨부함");				
			} else if (pIn.getR_folder_id().equals("eim_qlink_recycle_doc")) {
				rows.put("DESCRIPTION", "휴지통");				
				rows.put("DOC_RENAME_YN", "0");			// 이름바꾸기		
			} else if (pIn.getR_folder_id().equals("eim_qlink_rec_doc")) {
				rows.put("text", "최신문서");
				rows.put("rid", "eim_qlink_rec_doc");
				rows.put("r_menu_type", "dm_recent_doc");
				rows.put("r_link_type", "1");
				rows.put("order_code", "10");
				
				rows.put("DESCRIPTION", "최신문서");
			}
			
			
			rows.put("DOC_BOX_NAME", pIn.getR_folder_id());
		
			rows.put("SEARCH_YN", 				"0");	//001:검색			
			rows.put("PUBLIC_SEARCH_YN", 		"0");	//002:공용업무함검색
			rows.put("PUBLIC_ADD_YN", 			"0");	//003:공용업무함 개설
			rows.put("PUBLIC_USER_ADD_YN", 		"0");	//004:공용업무함 사용자관리
			rows.put("INVISIBLE_PROJECT_YN", 	"0");	//005:공용업무함 숨기기
			rows.put("PERSONAL_INFO_YN", 		"0");	//006:개인업무함 폴더관리
			rows.put("PUBLIC_INFO_YN", 			"0");	//007:공용업무함 속성관리
			rows.put("PUBLIC_FOLDER_YN", 		"0");	//008:공용업무함 폴더관리
			rows.put("DOC_SEARCH_REC_YN", 		"1");	//009:최신문서조회설정
			rows.put("DOC_SEARCH_OWN_YN", 		"0");	//010:소유문서조회설정
			rows.put("FOLDER_RENAME_YN", 		"0");	//012:폴더명변경
			rows.put("FOLDER_DEL_YN", 			"0");	//013:삭제	
			rows.put("FOLDER_INFO_YN", 			"0");	//014:공용업무함 폴더관리
			rows.put("FOLDER_DOC_DEL_YN", 		"0");	//015:영구삭제(폴더+파일)
			rows.put("PERSONAL_FOLDER_INFO_YN", "0");	//016:개인업무함 폴더관리
			rows.put("IMAGE_VIEW_YN", 			"1");	//020:이미지 미리보기
			rows.put("DESKTOP_ICON_YN", 		"1");	//021:바탕화면에 바로 가기 만들기
			rows.put("FAVORITES_YN", 			"0");	//022:즐겨찾기에 추가
			rows.put("FOLDER_DRAG_YN", 			"0");	//023:Drag&Drop 보내기 설정
			rows.put("FAVORITES_DEL_YN", 		"0");	//024:즐겨찾기 해제
														//025:SEPARATOR
			rows.put("TRASH_EMPTY_YN", 			"0");	//026:휴지통 비우기
			rows.put("PARENT_OUT_YN", 			"1");	//027:현재 경로 복사
														//028:SEPARATOR
			rows.put("REFRESH_YN", 				"1");	//029:새로고침
			
			rows.put("COMPRESS_YN", 			"0");
			rows.put("EXPANSION_YN", 			"0");
			rows.put("FOLDER_COPY_YN", 			"0");
			rows.put("FOLDER_MOVE_YN", 			"0");
			rows.put("FOLDER_YN", 				"N");
			rows.put("FOLDER_ADD_YN", 			"0");
			rows.put("FOLDER_DOWN_YN", 			"0");
			rows.put("FOLDER_MGT_YN", 			"0");
			rows.put("NEW_DOC_YN", 				"0");
			rows.put("PROJECT_YN", 				"N");

			//list.add(rows);
			
		} else { // 하위 일반폴더
			logger.debug("===== Invalid r_folder_id [" + pIn.getR_folder_id() + "]");
			
			if (pIn.getDoc_box_name().equals("common_doc")) { // 전사문서함
				if (userType.equals("03")) { // 기업관리자
					rows.put("FOLDER_RENAME_YN", 		"1");	//012:폴더명변경
					rows.put("FOLDER_DEL_YN", 			"1");	//013:삭제
					rows.put("FOLDER_INFO_YN", 			"1");	//014:폴더속성관리
				} else {
					rows.put("FOLDER_RENAME_YN", 		"0");	//012:폴더명변경
					rows.put("FOLDER_DEL_YN", 			"0");	//013:삭제
					rows.put("FOLDER_INFO_YN", 			"0");	//014:폴더속성관리					
				}
				rows.put("FAVORITES_YN", 			"1");	//022:즐겨찾기에 추가
			} else if (pIn.getDoc_box_name().equals("personal_doc")) { // 개인문서함
				rows.put("FOLDER_RENAME_YN", 		"1");	//012:폴더명변경
				rows.put("FOLDER_DEL_YN", 			"1");	//013:삭제	
			} else { // 부서, 협업
				if (permitLevel.equals("2")) { // 조회가능, 등록가능
					rows.put("FOLDER_RENAME_YN", 		"1");	//012:폴더명변경
					rows.put("FOLDER_DEL_YN", 			"1");	//013:삭제	
					rows.put("FOLDER_INFO_YN", 			"1");	//014:폴더속성관리					
				} else {
					rows.put("FOLDER_RENAME_YN", 		"0");	//012:폴더명변경
					rows.put("FOLDER_DEL_YN", 			"0");	//013:삭제	
					rows.put("FOLDER_INFO_YN", 			"0");	//014:폴더속성관리
				}
				rows.put("FAVORITES_YN", 			"1");	//022:즐겨찾기에 추가
			}
			//list.add(rows);
		}
		
		list.add(rows);			
		resMap.put("totalCount", 1);
		resMap.put("list", list);
		resMap.put("errmsg", "정상 처리 되었습니다.\n(menuFolder)");
		resMap.put("errcode", "0");			
		
		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		logger.debug("===== menuFolder.do retStr:" + retStr);
		return retStr;

	}			
	

	@RequestMapping(value = "/doc/common/menuDoc.do", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> menuFolder(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String menuDoc(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.info("=== User_id: " + pIn.getUser_id());
		logger.info("=== DocBoxName: " + pIn.getDoc_box_name());
		logger.info("=== Rid: " + pIn.getRid());
		logger.info("=== RfolderId: " + pIn.getR_folder_id());		
		logger.info("=== PermitLevel: " + pIn.getPermit_level());		
		logger.info("=== Status: " + pIn.getStatus());		
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> rows = new HashMap<String, String>();

		if (pIn.getPermit_level().equals("삭제권한") && pIn.getStatus().equals("정상")) {
			rows.put("EDIT_YN", 			"1"); 	//001:편집		
		} else {
			rows.put("EDIT_YN", 			"0"); 	//001:편집
		}

		if (pIn.getPermit_level().equals("접근불가") || pIn.getPermit_level().equals("목록조회")) {
			rows.put("READ_YN", 			"0"); 	//002:읽기
			rows.put("URL_COPY_YN", 		"0");	//004:문서URL복사
			rows.put("PROPERTIES_YN", 		"0");	//025:문서속성보기
			rows.put("HISTORY_YN", 			"0");	//026:문서이력보기
		} else { // 조회, 인쇄, 다운로드, 편집
			rows.put("READ_YN", 			"1"); 	//002:읽기
			rows.put("URL_COPY_YN", 		"1");	//004:문서URL복사
			rows.put("PROPERTIES_YN", 		"1");	//025:문서속성보기
			rows.put("HISTORY_YN", 			"1");	//026:문서이력보기			
		}
		rows.put("CANCLE_EDIT_YN", 		"0");	//003:편집 취소
		rows.put("PC_VER_UP", 			"0");	//005:PC파일로 버전업		
		rows.put("EXPORT_YN", 			"0");	//008:임시드라이브 다운로드
		rows.put("EXPORT_W_DOWN_YN", 	"0");	//009:반출드라이브 다운로드
												//027:설정 및 보내기
		rows.put("DRAG_YN", 			"0");	//030:Drag&Drop보내기 설정
		rows.put("EXPORT_OUT_YN", 		"0");	//031:반출신청
		
		if (pIn.getDoc_box_name().equals("export_approval_doc") // 승인 관련	 (승인,반려,취소)
		 || pIn.getDoc_box_name().equals("export_approval_request_doc")
		 || pIn.getDoc_box_name().equals("export_approval_return_doc")
		 || pIn.getDoc_box_name().equals("eim_qlink_recycle_doc") //휴지통
		 
		 ) { 		
			rows.put("COPY_YN", 			"0");	//011:복사
			rows.put("CUT_YN", 				"0");	//012:잘라내기
			rows.put("RENAME_YN", 			"0");	//013:이름바꾸기
													//015:삭제추가기능
			rows.put("DEL_YN", 				"0");	//016:삭제
			rows.put("PARENT_DEL_YN", 		"0");	//017:현재버전삭제
			rows.put("DEL_ALL_YN", 			"0");	//018:완전삭제
			rows.put("DEL_CANCLE_YN", 		"0");	//019:복원
			rows.put("TRASH_YN", 			"0");	//021:삭제
													//024:속성 및 이력
			rows.put("IMPORT_YN", 			"0");	//028:중요문서설정
			rows.put("IMPORT_CANCLE_YN", 	"0");	//029:중요문서해제
			
			if (pIn.getDoc_box_name().equals("export_approval_doc")) { // 반출승인할문서 (내가 승인할 문서)
				rows.put("EXPORT_APPR_YN", 		"1");	//032:반출승인
				rows.put("EXPORT_REJECT_YN", 	"1");	//034:반출반려
			} else {
				rows.put("EXPORT_APPR_YN", 		"0");	//032:반출승인			
				rows.put("EXPORT_REJECT_YN", 	"0");	//034:반출반려			
			}
			if (pIn.getDoc_box_name().equals("export_approval_request_doc")) { // 승인요청문서
				rows.put("EXPORT_CANCLE_YN", 	"1");	//033:반출신청 취소
			} else {
				rows.put("EXPORT_CANCLE_YN", 	"0");	//033:반출신청 취소			
			}			
			if (pIn.getDoc_box_name().equals("eim_qlink_recycle_doc")) { // 휴지통
				rows.put("URL_COPY_YN", 	"0");	//004:문서URL복사
				rows.put("DEL_CANCLE_YN", 	"1");	//019:복원
				rows.put("TRASH_DEL_YN", 	"1");	//022:삭제			
			}
		} else {
			if (pIn.getPermit_level().equals("삭제권한") && pIn.getStatus().equals("정상")) {
				rows.put("COPY_YN", 			"1");	//011:복사
				rows.put("CUT_YN", 				"1");	//012:잘라내기
				rows.put("RENAME_YN", 			"1");	//013:이름바꾸기
														//015:삭제추가기능
				rows.put("DEL_YN", 				"1");	//016:삭제
				rows.put("PARENT_DEL_YN", 		"1");	//017:현재버전삭제
				rows.put("DEL_ALL_YN", 			"0");	//018:완전삭제
				rows.put("DEL_CANCLE_YN", 		"0");	//019:복원
				rows.put("TRASH_YN", 			"0");	//021:삭제
														//024:속성 및 이력
				rows.put("IMPORT_YN", 			"1");	//028:중요문서설정
				rows.put("IMPORT_CANCLE_YN", 	"1");	//029:중요문서해제
			}
		}
		
		rows.put("REPORT_YN", 			"0");	//036:보고받는분 지정
		rows.put("PUBLIC_MOVE_YN", 		"0");	//037:공용업무함으로 이동
												//039:엑셀 데이터 연동 복사
		rows.put("REFRESH_YN", 			"1");	//040:새로고침
		rows.put("SECRET_REJECT_YN", 	"0");
		rows.put("SECRET_CHANGE_YN", 	"0");
		rows.put("MULTI_EXPORT_OFF_YN", "0");
		rows.put("REPORT_DEL_YN", 		"0");
		rows.put("READ_REQ_APPR_YN", 	"0");
		rows.put("MULTI_ATTR_YN", 		"0");
		rows.put("MULTI_COPY_YN", 		"0");
		rows.put("REPORT_ADD_YN", 		"0");
		rows.put("PDF_RETRY_YN", 		"0");
		rows.put("PDF_URL_COPY_YN", 	"0");
		rows.put("PARENT_CONFIG_YN", 	"0");
		rows.put("MULTI_APPR_YN", 		"0");
		rows.put("PUBLIC_COPY_YN", 		"0");
		rows.put("MULTI_CUT_YN", 		"0");
		rows.put("MULTI_CHANGE_OWNER_YN", "0");
		rows.put("PDF_READ_YN", 		"0");
		rows.put("SECRET_CANCLE_YN", 	"0");
		rows.put("PASTE_YN", 			"1");
		rows.put("READ_REQ_REJECT_YN", 	"0");
		rows.put("SUBSCRIBE_YN", 		"0");
		rows.put("SECRET_KEEP_YN", 		"0");
		rows.put("PARENT_HISTORY_YN", 	"1");
		rows.put("LASTEST", 			"1");
		rows.put("MULTI_PASTE_YN", 		"0");
		rows.put("PDF_CHANGE_YN", 		"0");
		rows.put("SECRET_APPR_YN", 		"0");
		rows.put("EXPORT_OFF_YN", 		"0");
		rows.put("CHANGE_OWNER_YN", 	"0");
		rows.put("MAIL_DEL_YN", 		"0");
		rows.put("SECRET_DOWN_YN", 		"0");
		rows.put("CHECK_IN_YN", 		"0");
		rows.put("MULTI_APPR_CHANGE_YN","0");
		rows.put("DOWN", 				"0");
		rows.put("READ_REQ_CANCLE_YN", 	"0");
		rows.put("PDF_EXPORT_YN", 		"0");
		rows.put("EXPORT_MAIL_HISTORY_YN", "0");
		rows.put("CES_ATT_YN", 			"0");
		rows.put("MULTI_EXPORT_YN", 	"0");
		rows.put("REQ_COM_YN", 			"0");
		/*
		if (pIn.getDoc_box_name().equals("export_approval_doc")) { // 반출승인할문서 (내가 승인할 문서)
			//rows.put("EXPORT_APPR_YN", 		"1");	//032:반출승인
			//rows.put("EXPORT_CANCLE_YN", 	"0");	//033:반출신청 취소
			//rows.put("EXPORT_REJECT_YN", 	"1");	//034:반출반려
		} else if (pIn.getDoc_box_name().equals("export_approval_request_doc")) { // 승인요청문서
			rows.put("EXPORT_APPR_YN", 		"0");	//032:반출승인
			rows.put("EXPORT_CANCLE_YN", 	"1");	//033:반출신청 취소
			rows.put("EXPORT_REJECT_YN", 	"0");	//034:반출반려
		} else {
		}
		*/
		
		list.add(rows);
		
		resMap.put("totalCount", 1);
		resMap.put("list", list);
		resMap.put("errmsg", "정상 처리 되었습니다.\n(menuDoc)");
		resMap.put("errcode", "0");			

		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		logger.debug("===== menuDoc.do retStr:" + retStr);
		return retStr;
	}
	
	/**
	 * <p><b>
	 * 폴더 조회
	 * </b></p>
	 */
	@RequestMapping(value = "/main/menu/callAgentFolderMenu.do", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8" )
	//public Map<String, Object> callAgentFolderMenu(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String callAgentFolderMenu(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		byte[] decodedBytes = Base64.getDecoder().decode(pIn.getUser_id());
		String loginId = new String(decodedBytes);
		logger.info("=== loginId [" + loginId + "]");

		logger.info("==== userId [" + pIn.getUserId() + "]");
		logger.info("==== user_id [" + pIn.getUser_id() + "]");
		logger.info("==== rid [" + pIn.getRid() + "]");
			
		if (pIn.getRid().equals("TOP_ROOT_ID")) { // 최상위 문서함
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			
			if (pIn.getUser_id() != null && pIn.getUser_id() != "") {
				logger.debug("=== loginId is not null?? ");
				rows.put("json_call_url", "/main/menu/eimLeftQuickLink.do");
				rows.put("text", "바로가기");
				rows.put("rid", "eim_qlink");
				rows.put("r_menu_type", "dm_qlink");
				rows.put("r_link_type", "1");
				rows.put("order_code", "1");
				list.add(rows);
				rows = new HashMap<String, String>();
				rows.put("json_call_url", "/main/menu/eimLeftApprManage.do");
				rows.put("text", "승인관리");
				rows.put("rid", "approval_mgt");
				rows.put("r_menu_type", "dm_approval_mgt");
				rows.put("r_link_type", "1");
				rows.put("order_code", "2");
				list.add(rows);
				rows = new HashMap<String, String>();
				rows.put("json_call_url", "0");
				rows.put("text", "전사문서함");
				rows.put("rid", "common_doc");
				rows.put("r_menu_type", "dm_common_doc");
				rows.put("r_link_type", "1");
				rows.put("order_code", "3");
				list.add(rows);
				rows = new HashMap<String, String>();
				rows.put("json_call_url", "0");
				rows.put("text", "부서문서함");
				rows.put("rid", "department_doc");
				rows.put("r_menu_type", "dm_department_doc");
				rows.put("r_link_type", "1");
				rows.put("order_code", "4");
				rows.put("aaa", "bbb");
				list.add(rows);
				rows = new HashMap<String, String>();
				rows.put("json_call_url", "0");
				rows.put("text", "협업문서함");
				rows.put("rid", "public_doc");
				rows.put("r_menu_type", "dm_public_doc");
				rows.put("r_link_type", "1");
				rows.put("order_code", "5");
				list.add(rows);
				rows = new HashMap<String, String>();
				rows.put("json_call_url", "0");
				rows.put("text", "개인문서함");
				rows.put("rid", "personal_doc");
				rows.put("r_menu_type", "dm_personal_doc");
				rows.put("r_link_type", "1");
				rows.put("order_code", "6");
				list.add(rows);
				rows = new HashMap<String, String>();
				rows.put("json_call_url", "fav");
				rows.put("text", "즐겨찾기 문서");
				rows.put("rid", "favorite_doc");
				rows.put("r_menu_type", "dm_favorite_doc");
				rows.put("r_link_type", "1");
				rows.put("order_code", "7");
				list.add(rows);
				
				resMap.put("totalCount", 7);
				resMap.put("list", list);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
				resMap.put("errcode", "0");
			} else { // Not logged in
				logger.debug("=== loginId is empty ");
				resMap.put("totalCount", 0);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
				resMap.put("errcode", "0");				
			}
		} 
		else if (pIn.getRid().equals("eim_qlink")) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			rows.put("text", "최신문서");
			rows.put("rid", "eim_qlink_rec_doc");
			rows.put("r_menu_type", "dm_recent_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "10");
			list.add(rows);
			rows = new HashMap<String, String>();
			rows.put("text", "소유문서");
			rows.put("rid", "eim_qlink_own_doc");
			rows.put("r_menu_type", "dm_ownership_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "20");
			list.add(rows);
			rows = new HashMap<String, String>();
			rows.put("text", "최근열람문서");
			rows.put("rid", "eim_qlink_recent_checkout_doc");
			rows.put("r_menu_type", "dm_recent_checkout_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "30");
			list.add(rows);
			rows = new HashMap<String, String>();
			rows.put("text", "휴지통");
			rows.put("rid", "eim_qlink_recycle_doc");
			rows.put("r_menu_type", "dm_recycle_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "40");
			list.add(rows);
			
			resMap.put("totalCount", 4);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
			resMap.put("errcode", "0");			
		}		
		else if (pIn.getRid().equals("qlink_report_mgt")) { // 보고서 관리
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			rows.put("json_call_url", "/pcexp");
			rows.put("text", "보고중문서");
			rows.put("rid", "qlink_report_set_doc");
			rows.put("r_menu_type", "dm_report_set_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "1");
			list.add(rows);
			rows = new HashMap<String, String>();
			rows.put("json_call_url", "/pcexp");
			rows.put("text", "보고받은문서");
			rows.put("rid", "qlink_reported_doc");
			rows.put("r_menu_type", "dm_reported_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "2");
			list.add(rows);

			resMap.put("totalCount", 2);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
			resMap.put("errcode", "0");			
		} 
		else if (pIn.getRid().equals("approval_mgt")) { // 승인관리
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			rows.put("text", "승인요청문서");
			rows.put("rid", "export_approval_request_doc");
			rows.put("r_menu_type", "dm_export_approval_request_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "10");
			list.add(rows);
			rows = new HashMap<String, String>();
			rows.put("text", "승인완료문서");
			rows.put("rid", "export_approval_complete_doc");
			rows.put("r_menu_type", "dm_export_approval_complete_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "20");
			list.add(rows);
			rows = new HashMap<String, String>();
			rows.put("text", "승인반려문서");
			rows.put("rid", "export_approval_return_doc");
			rows.put("r_menu_type", "dm_export_approval_return_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "30");
			list.add(rows);
			rows = new HashMap<String, String>();
			rows.put("text", "내가 승인할 문서");
			rows.put("rid", "export_approval_doc");
			rows.put("r_menu_type", "dm_export_approval_doc");
			rows.put("r_link_type", "1");
			rows.put("order_code", "40");
			list.add(rows);
//			rows = new HashMap<String, String>();
//			rows.put("text", "반출요청이력조회");
//			rows.put("rid", "export_req_history_doc");
//			rows.put("r_menu_type", "dm_export_req_history_doc");
//			rows.put("r_link_type", "1");
//			rows.put("order_code", "50");
//			list.add(rows);
//			rows = new HashMap<String, String>();
//			rows.put("text", "반출현황조회");
//			rows.put("rid", "export_history_doc");
//			rows.put("r_menu_type", "dm_export_history_doc");
//			rows.put("r_link_type", "1");
//			rows.put("order_code", "60");
//			list.add(rows);
//			rows = new HashMap<String, String>();
//			rows.put("text", "보안등급변경요청문서");
//			rows.put("rid", "security_lv_change_request_doc");
//			rows.put("r_menu_type", "dm_security_lv_change_request_doc");
//			rows.put("r_link_type", "1");
//			rows.put("order_code", "70");
//			list.add(rows);
//			rows = new HashMap<String, String>();
//			rows.put("text", "보안등급변경승인대상문서");
//			rows.put("rid", "security_lv_aporoval_request_doc");
//			rows.put("r_menu_type", "dm_security_lv_change_doc");
//			rows.put("r_link_type", "1");
//			rows.put("order_code", "80");
//			list.add(rows);
//			rows = new HashMap<String, String>();
//			rows.put("text", "열람권한신청한문서");
//			rows.put("rid", "read_approval_request_doc");
//			rows.put("r_menu_type", "dm_read_approval_request_doc");
//			rows.put("r_link_type", "1");
//			rows.put("order_code", "90");
//			list.add(rows);
//			rows = new HashMap<String, String>();
//			rows.put("text", "열람권한승인할문서");
//			rows.put("rid", "read_approval_doc");
//			rows.put("r_menu_type", "dm_read_approval_doc");
//			rows.put("r_link_type", "1");
//			rows.put("order_code", "100");
//			list.add(rows);

			resMap.put("totalCount", 4);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentFolderMenu)");
			resMap.put("errcode", "0");			
		} else {
			logger.debug("===== Invalid RID [" + pIn.getRid() + "]");
		}
		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		logger.debug("===== callAgentFolderMenu.do retStr:" + retStr);
		return retStr;

	}		
	
	
	/**
	 * <p><b>
	 * 최근열람문서 조회
	 * </b></p>
	 */
	@RequestMapping(value = "/main/menu/callAgentDocMenu.do", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> callAgentDocMenu(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String callAgentDocMenu(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		String handleType = ""; // 01: 최근등록, 02: 본인등록, 03:본인소유, 04:최근변경,
									  // 05:만료도래, 06:만료, 07:휴지통, 
									  // 08:폐기, 09:만료(관리자)

		try {
		logger.info("사용자아이디 : " + pIn.getUserId());
		logger.info("rid : " + pIn.getRid());
		
		if (pIn.getRid().equals("eim_qlink_rec_doc")  // 최근등록문서
				|| pIn.getRid().equals("eim_qlink_recent_checkout_doc") // 최근열람문서
				|| pIn.getRid().equals("eim_qlink_recycle_doc") // 휴지통
				|| pIn.getRid().equals("eim_qlink_own_doc") 	// 소유문서 
		) {
			 // 01: 최근등록, 02: 본인등록, 03:본인소유, 04:최근변경, 05:만료도래, 06:만료, 07:휴지통, 08:폐기, 09:만료(관리자)
			if (pIn.getRid().equals("eim_qlink_rec_doc")) { // 최근 등록 문서
				handleType = "01";
			} else if (pIn.getRid().equals("eim_qlink_recent_checkout_doc")) { // 내가 등록한 문서
				handleType = "02";
			} else if (pIn.getRid().equals("eim_qlink_own_doc")) { // 소유문서
				handleType = "03";
			} else if (pIn.getRid().equals("eim_XXXX")) { // 만료도래
				handleType = "05";
			} else if (pIn.getRid().equals("eim_qlink_recycle_doc")) { // 휴지동
				handleType = "07";
			}
			
			
			// ECM40 API 호출 (list_np)
			result = callECM40_NPList (handleType, session);

			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
			List<ZappContentRes> res = (List<ZappContentRes>) result.getResObj();
			for (int i=0; i<res.size(); i++) {
				ZappContentRes contentRes = new ZappContentRes();
				contentRes = res.get(i);
				//logger.debug("=== contentRes.className : " + contentRes.getClassname());
				//logger.debug("=== contentRes.contentNo : " + contentRes.getContentno());
							
				String lockerName = "";
				if (contentRes.getLockername() != null) {
					lockerName = "[" + contentRes.getLockerdeptname() + "] " + contentRes.getLockername();
				}
				long dFileSize = contentRes.getFilesize().longValue();					
				String sVer = contentRes.getVersion();
				//logger.debug("=== sVer:" + sVer + ", dFileSize:" + dFileSize + ", lockerName:" + lockerName);
				String[] titles = contentRes.getTitle().split("：");

				String acls = "";
				String rState = ""; //승인상태
				String apprDiv = ""; //신청유형
				//ACL 0:접근불가, 1:목록조회, 2:조회, 3:인쇄, 4:다운로드, 5:편집
				if (contentRes.getZappAcl().getAcls() == 0)
					acls = "접근불가";
				else if (contentRes.getZappAcl().getAcls() == 1)
					acls = "목록조회";
				else if (contentRes.getZappAcl().getAcls() == 2)
					acls = "조회";
				else if (contentRes.getZappAcl().getAcls() == 3)
					acls = "인쇄";
				else if (contentRes.getZappAcl().getAcls() == 4)
					acls = "다운로드";
				else if (contentRes.getZappAcl().getAcls() == 5)
					acls = "삭제권한";

				Map<String, String> rows = new HashMap<String, String>();
				rows.put("a_content_type", "excel12book");
				rows.put("creator_org_nm", "ECM팀(TBD)");
				rows.put("creator_emp_nm", contentRes.getCreatorname());
				rows.put("dm_rnum", "eim_doc");
				rows.put("dos_ext", "xlsx");
				rows.put("doc_folderPath", contentRes.getHoldername());
				rows.put("i_chronicle_id", "0912d6878000d982");  // object_id: 문서의 버전마다의 고유id, chronicle_id: 버전 무관한 문서 자체의 고유id
				rows.put("inspt_date", contentRes.getUpdatetime());
				rows.put("object_name", titles[0]);
				rows.put("open_yn", "Y");
				//rows.put("owner_name", contentRes.getCreatorid());
				rows.put("owner_name", "[" + contentRes.getCreatordeptname() + "] " + contentRes.getCreatorname());
				rows.put("permit_name", acls);
				rows.put("r_creation_date", contentRes.getCreatetime());
				rows.put("r_secu_level", "부서비(TBD)");
				//rows.put("r_modify_date", contentRes.getCreatetime().substring(0, 10));
				if (contentRes.getUpdatetime() != null && contentRes.getUpdatetime() != "") {
					rows.put("r_modify_date", contentRes.getUpdatetime().substring(0, 19));
				} else {
					rows.put("r_modify_date", "");							
				}
				rows.put("r_object_id", contentRes.getContentid());
				rows.put("r_lock_type", "0"); //아이콘에 체크 표시
				rows.put("r_modifier", "[" + contentRes.getCreatordeptname() + "] " + contentRes.getCreatorname());
				rows.put("r_object_type", "di_doc");
				rows.put("r_version_label", sVer);
				rows.put("r_state", "Working");
				rows.put("r_link_type", "0");
				rows.put("r_lock_owner_kor", lockerName);
				rows.put("r_lock_owner", lockerName);
				rows.put("r_content_size", Long.toString(dFileSize));
				retList.add(rows);
			}
		
			resMap.put("totalCount", res.size());
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}
		else if (pIn.getRid().equals("eim_qlink_imp_doc")) { // 중요문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			Map<String, String> rows = new HashMap<String, String>();
			rows.put("a_content_type", "pdftext");
			rows.put("creator_org_nm", "ECM팀");
			rows.put("creator_emp_nm", "김동진[ECM팀]");
			rows.put("dm_rnum", "eim_doc");
			rows.put("dos_ext", "txt");
			rows.put("doc_folderPath", "테스트1/v2");
			rows.put("i_chronicle_id", "0912d6878000d848");  // object_id: 문서의 버전마다의 고유id, chronicle_id: 버전 무관한 문서 자체의 고유id
			rows.put("object_name", "김용훈_테스트.txt");
			rows.put("open_yn", "Y");
			rows.put("owner_name", "yws");
			rows.put("permit_name", "삭제권한");
			rows.put("r_creation_date", "2017/08/29 15:14:16");
			rows.put("r_version_label", "0.3");
			rows.put("r_object_type", "di_doc");
			rows.put("r_secu_level", "부서비");
			rows.put("r_state", "Working");
			rows.put("r_link_type", "0");
			rows.put("r_lock_owner", "");
			rows.put("r_object_id", "0912d6878000d925");
			rows.put("r_modify_date", "2017/08/29 20:16:05");
			rows.put("r_lock_type", "0");
			rows.put("r_content_size", "28");
			rows.put("r_modifier", "yws");
			retList.add(rows);
			
			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}		
		else if (pIn.getRid().equals("eim_qlink_teamwork_doc")) { // 편집권한 문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}		
		else if (pIn.getRid().equals("eim_qlink_coaching_doc")) { // 열람지정 문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			Map<String, String> rows = new HashMap<String, String>();
			rows.put("a_content_type", "pdftext");
			rows.put("creator_org_nm", "ECM팀");
			rows.put("creator_emp_nm", "김동진[ECM팀]");
			rows.put("dm_rnum", "eim_doc");
			rows.put("dos_ext", "txt");
			rows.put("doc_folderPath", "김동진[kdj]/새 폴더");
			rows.put("i_chronicle_id", "0912d68780009d02"); // object_id: 문서의 버전마다의 고유id, chronicle_id: 버전 무관한 문서 자체의 고유id
			rows.put("open_yn", "Y");
			rows.put("object_name", "새 텍스트 문서.txt");
			rows.put("owner_name", "kdj");
			rows.put("permit_name", "읽기권한");
			rows.put("r_creation_date", "2017/04/17 16:54:00");
			rows.put("r_version_label", "2.0");
			rows.put("r_object_type", "di_doc");
			rows.put("r_secu_level", "부서비");
			rows.put("r_state", "Complete");
			rows.put("r_link_type", "0");
			rows.put("r_lock_owner", "");
			rows.put("r_object_id", "0912d6878000bf76");
			rows.put("r_modify_date", "2017/07/28 10:53:44");
			rows.put("r_lock_type", "0");
			rows.put("r_content_size", "115");
			rows.put("r_modifier", "dmadmin");
			retList.add(rows);

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}		
		else if (pIn.getRid().equals("qlink_report_set_doc")) { // 보고중 문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}				
		else if (pIn.getRid().equals("qlink_reported_doc")) { // 보고받은 문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}				
		else if (pIn.getRid().equals("qlink_editing_doc")) { // 편집중인 문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("eim_qlink_post_doc")) { // 개인메일첨부함
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("export_approval_request_doc")) { // 승인요청 문서
			handleType = ZappConts.TYPES.LIST_APPROVAL_REQUESTED.type;
			
			// ECM40 API 호출 (list_np)
			result = callECM40_NPList (handleType, session);
			
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
			List<ZappContentRes> res = (List<ZappContentRes>) result.getResObj();
			for (int i=0; i<res.size(); i++) {
				ZappContentRes contentRes = new ZappContentRes();
				contentRes = res.get(i);
				//logger.debug("=== contentRes.className : " + contentRes.getClassname());
				//logger.debug("=== contentRes.contentNo : " + contentRes.getContentno());

				String lockerName = contentRes.getLockername();					
				Double dFileSize = contentRes.getFilesize();					
				String sVer = contentRes.getVersion();
				//logger.debug("=== sVer:" + sVer + ", dFileSize:" + dFileSize + ", lockerName:" + lockerName);
				String[] titles = contentRes.getTitle().split("：");
				String[] wfInfos = contentRes.getWfinf().split("_");
				String rState = ""; //승인상태
				String apprDiv = ""; //신청유형
				logger.debug("=== title [" + titles[0] + "], contentRes.getWfinf [" + contentRes.getWfinf() + "]");
				logger.debug("=== appOrder [" + contentRes.getApporder() + "], state [" + contentRes.getState() + "]");
				
				rState = contentRes.getApporder() + "차 승인대기";

				// 승인코드값 조회
				apprDiv = getApproveType(contentRes.getState());			
				
				Map<String, String> rows = new HashMap<String, String>();
				if (wfInfos.length > 1) {
					rows.put("approver_name", wfInfos[1]);
				} else {
					rows.put("approver_name", "");
				}
				
				rows.put("appr_div", apprDiv);
				rows.put("dos_ext", "txt");
				rows.put("expired_yn", "만료(TBD)");
				rows.put("expired_date", contentRes.getExpiretime());
				rows.put("export_reason", contentRes.getReasons());
				rows.put("object_name", titles[0]);
				rows.put("permit_name", "" + contentRes.getAcls());
				rows.put("request_date", contentRes.getCreatetime().substring(0, 10));
				rows.put("request_seq", "578ead26-a49b-44f4-8ea6-8ecb1499bc1a");
				rows.put("r_version_label", contentRes.getVersion());
				rows.put("r_object_type", "di_doc");
				rows.put("r_secu_level", "부서비(TBD)");
				rows.put("r_state", rState);				
				rows.put("r_object_id", contentRes.getContentid());
				retList.add(rows);
			}
		
			resMap.put("totalCount", res.size());
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("export_approval_complete_doc")) { // 승인 완료문서
			handleType = ZappConts.TYPES.LIST_APPROVAL_APPROVED.type;
			
			// ECM40 API 호출 (list_np)
			result = callECM40_NPList (handleType, session);
			
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
			List<ZappContentRes> res = (List<ZappContentRes>) result.getResObj();
			for (int i=0; i<res.size(); i++) {
				ZappContentRes contentRes = new ZappContentRes();
				contentRes = res.get(i);
				//logger.debug("=== contentRes.className : " + contentRes.getClassname());
				//logger.debug("=== contentRes.contentNo : " + contentRes.getContentno());

				String lockerName = contentRes.getLockername();					
				Double dFileSize = contentRes.getFilesize();					
				String sVer = contentRes.getVersion();
				//logger.debug("=== sVer:" + sVer + ", dFileSize:" + dFileSize + ", lockerName:" + lockerName);
				String[] titles = contentRes.getTitle().split("：");
				String[] wfInfos = contentRes.getWfinf().split("_");
				logger.debug("=== title [" + titles[0] + "], contentRes.getWfinf [" + contentRes.getWfinf() + "]");

				Map<String, String> rows = new HashMap<String, String>();
				if (wfInfos.length > 1) {
					rows.put("approver_name", wfInfos[1]);
					rows.put("appr_date", wfInfos[0].substring(0, 10));
				} else {
					rows.put("approver_name", "");
					rows.put("appr_date", "");
				}
				rows.put("appr_div", "전결(TBD)");
				rows.put("appr_comments", contentRes.getReasons());				
				rows.put("dos_ext", "txt");
				rows.put("expired_yn", "만료(TBD)");
				rows.put("expired_date", contentRes.getExpiretime());
				rows.put("export_reason", "");
				rows.put("object_name", titles[0]);
				rows.put("permit_name", "" + contentRes.getAcls());
				rows.put("request_date", contentRes.getCreatetime().substring(0, 10));
				rows.put("request_seq", "578ead26-a49b-44f4-8ea6-8ecb1499bc1a");
				rows.put("r_version_label", contentRes.getVersion());
				rows.put("r_object_type", "di_doc");
				rows.put("r_secu_level", "부서비(TBD)");
				rows.put("r_state", "승인");
				rows.put("r_object_id", contentRes.getContentid());
				retList.add(rows);
			}
		
			resMap.put("totalCount", res.size());
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("export_approval_return_doc")) { // 승인 반려문서
			handleType = ZappConts.TYPES.LIST_APPROVAL_RETURNED.type;
			
			// ECM40 API 호출 (list_np)
			result = callECM40_NPList (handleType, session);
			
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
			List<ZappContentRes> res = (List<ZappContentRes>) result.getResObj();
			for (int i=0; i<res.size(); i++) {
				ZappContentRes contentRes = new ZappContentRes();
				contentRes = res.get(i);
				//logger.debug("=== contentRes.className : " + contentRes.getClassname());
				//logger.debug("=== contentRes.contentNo : " + contentRes.getContentno());

				String lockerName = contentRes.getLockername();					
				Double dFileSize = contentRes.getFilesize();					
				String sVer = contentRes.getVersion();
				//logger.debug("=== sVer:" + sVer + ", dFileSize:" + dFileSize + ", lockerName:" + lockerName);
				String[] titles = contentRes.getTitle().split("：");
				String[] wfInfos = contentRes.getWfinf().split("_");
				String rState = ""; //승인상태
				String apprDiv = ""; //신청유형

				logger.debug("=== title [" + titles[0] + "], contentRes.getWfinf [" + contentRes.getWfinf() + "]");
				logger.debug("=== appOrder [" + contentRes.getApporder() + "], state [" + contentRes.getState() + "]");

				rState = contentRes.getApporder() + "차 승인대기";
				// 승인코드값 조회
				apprDiv = getApproveType(contentRes.getState());

				Map<String, String> rows = new HashMap<String, String>();
				if (wfInfos.length > 1) {
					rows.put("appr_date", wfInfos[0].substring(0, 10)); // 반려일자
					rows.put("approver_name", wfInfos[1]);
				} else {
					rows.put("approver_name", "");
				}
				
				rows.put("appr_div", "전결(TBD)");
				rows.put("appr_comments", contentRes.getReasons());				
				rows.put("dos_ext", "txt");
				rows.put("expired_yn", "만료(TBD)");
				rows.put("expired_date", contentRes.getExpiretime());
				rows.put("export_reason", "");
				rows.put("object_name", titles[0]);
				rows.put("permit_name", "" + contentRes.getAcls());
				rows.put("request_date", contentRes.getCreatetime().substring(0, 10));
				rows.put("request_seq", "578ead26-a49b-44f4-8ea6-8ecb1499bc1a");
				rows.put("r_version_label", contentRes.getVersion());
				rows.put("r_object_type", "di_doc");
				rows.put("r_secu_level", "부서비(TBD)");
				rows.put("r_state", apprDiv);
				rows.put("r_object_id", contentRes.getContentid());
				retList.add(rows);
			}
		
			resMap.put("totalCount", res.size());
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("export_approval_doc")) { // 내가 승인할 문서
			handleType = ZappConts.TYPES.LIST_APPROVAL_OBJECT.type;
			
			// ECM40 API 호출 (list_np)
			result = callECM40_NPList (handleType, session);
			
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
			List<ZappContentRes> res = (List<ZappContentRes>) result.getResObj();
			for (int i=0; i<res.size(); i++) {
				ZappContentRes contentRes = new ZappContentRes();
				contentRes = res.get(i);
				//logger.debug("=== contentRes.className : " + contentRes.getClassname());
				//logger.debug("=== contentRes.contentNo : " + contentRes.getContentno());

				String lockerName = contentRes.getLockername();					
				Double dFileSize = contentRes.getFilesize();					
				String sVer = contentRes.getVersion();
				//logger.debug("=== sVer:" + sVer + ", dFileSize:" + dFileSize + ", lockerName:" + lockerName);
				String[] titles = contentRes.getTitle().split("：");
				String[] wfInfos = contentRes.getWfinf().split("_");
				String rState = ""; //승인상태
				String apprDiv = ""; //신청유형
				logger.debug("=== title [" + titles[0] + "], contentRes.getWfinf [" + contentRes.getWfinf() + "]");
				
				rState = contentRes.getApporder() + "차 승인대기";
				// 승인코드값 조회
				apprDiv = getApproveType(contentRes.getState());

				Map<String, String> rows = new HashMap<String, String>();
				if (wfInfos.length > 1) {
					rows.put("appr_date", wfInfos[0].substring(0, 10)); // 반려일자
					//rows.put("approver_name", wfInfos[1]);
				} else {
					//rows.put("approver_name", "");
				}

				rows.put("appr_div", apprDiv);
				rows.put("appr_comments", contentRes.getReasons());				
				rows.put("dos_ext", "txt");
				rows.put("expired_yn", "만료(TBD)");
				rows.put("expired_date", "2017-08-13(TBD)");
				rows.put("export_reason", "");
				rows.put("object_name", titles[0]);
				rows.put("request_date", contentRes.getCreatetime().substring(0, 10));
				rows.put("request_seq", "a34423cb-3708-4d5c-b853-dbc4598bc688");
				rows.put("requester_name", "[" + contentRes.getCreatordeptname() + "]" + contentRes.getCreatorname());
				rows.put("r_version_label", sVer);
				rows.put("r_object_type", "di_doc");
				rows.put("r_secu_level", "부서비(TBD)");
				rows.put("r_state", rState);
				rows.put("r_state_code", "R");
				rows.put("r_object_id", contentRes.getContentid());
				retList.add(rows);
			}
		
			resMap.put("totalCount", res.size());
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("export_req_history_doc")) { // 반출요청 이력조회
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			Map<String, String> rows = new HashMap<String, String>();
			rows.put("appr_div", "전결");
			rows.put("approver_name", "김동진");
			rows.put("dos_ext", "txt");
			rows.put("expired_yn", "만료");
			rows.put("expired_date", "2021-01-01");
			rows.put("export_reason", "000");
			rows.put("object_name", "test.txt");
			rows.put("request_date", "2020-12-29");
			rows.put("request_seq", "578ead26-a49b-44f4-8ea6-8ecb1499bc1a");
			rows.put("requester_name", "양우석");
			rows.put("r_version_label", "0.1");
			rows.put("r_object_type", "di_doc");
			rows.put("r_secu_level", "부서비");
			rows.put("r_state", "미결");
			rows.put("r_object_id", "0912d6878000dedd");
			retList.add(rows);

			rows = new HashMap<String, String>();
			rows.put("appr_div", "전결");
			rows.put("approver_name", "김동진");
			rows.put("dos_ext", "txt");
			rows.put("expired_yn", "만료");
			rows.put("expired_date", "2021-01-01");
			rows.put("export_reason", "000");
			rows.put("object_name", "test.txt");
			rows.put("request_date", "2020-12-29");
			rows.put("request_seq", "43543675-ecaa-45d0-bd0f-b828631ecff8");
			rows.put("requester_name", "양우석");
			rows.put("r_version_label", "0.1");
			rows.put("r_object_type", "di_doc");
			rows.put("r_secu_level", "부서비(export_req_history_doc)");
			rows.put("r_state", "미결");
			rows.put("r_object_id", "0912d6878000dedd");
			retList.add(rows);
			
			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("export_history_doc")) { // 반출현황조회
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			Map<String, String> rows = new HashMap<String, String>();
			rows.put("approver_name", "양우석");
			rows.put("appr_date", "2017-08-10");
			rows.put("appr_comments", "ds");
			rows.put("appr_div", "전결");
			rows.put("dos_ext", "txt");
			rows.put("export_reason", "ㅅㄷㄴㅅ");
			rows.put("exp_date", "2017-08-10");
			rows.put("exp_div", "반출_다운로드");
			rows.put("requester_name", "김동진");
			rows.put("request_seq", "7dfdb5a4-af4c-4224-9090-8738c56fbc0e");
			rows.put("request_date", "2017-08-10");
			rows.put("r_version_label", "2.0");
			rows.put("r_object_type", "di_doc");
			rows.put("r_secu_level", "부서비(export_history_doc)");
			rows.put("r_object_id", "0912d6878000bf76");
			retList.add(rows);

			rows = new HashMap<String, String>();
			rows.put("approver_name", "양우석");
			rows.put("appr_date", "2017-07-28");
			rows.put("appr_comments", "ㅁㄴ");
			rows.put("appr_div", "전결");
			rows.put("dos_ext", "txt");
			rows.put("export_reason", "ㅁㅇㄴㄻㄴㅇㄹ");
			rows.put("exp_date", "2017-08-03");
			rows.put("exp_div", "반출_메일발송");
			rows.put("object_name", "새 텍스트 문서.txt"); //
			rows.put("requester_name", "김동진");
			rows.put("request_seq", "6ef02811-1ca9-4193-b673-2d3418b7aa31");
			rows.put("request_date", "2017-07-28");
			rows.put("r_version_label", "2.0");
			rows.put("r_object_type", "di_doc");
			rows.put("r_secu_level", "부서비");
			rows.put("r_object_id", "0912d6878000bf76");
			retList.add(rows);
			
			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("security_lv_change_request_doc")) { // 보안등급변경요청문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}
		else if (pIn.getRid().equals("security_lv_aporoval_request_doc")) { // 보안등급변경 승인대상문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}						
		else if (pIn.getRid().equals("read_approval_doc")) { // 열람권한승인할문서
			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			resMap.put("totalCount", 0);
			resMap.put("list", retList);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}	
		else if (pIn.getRid().equals("favorite_doc")) { // 즐겨찾기 문서
			
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

			handleType = "05"; // Marked List

			//ZappClassificationPar zappClassPar = new ZappClassificationPar();
			//zappClassPar.setObjIsTest("N");
			//zappClassPar.setObjRes("LIST");
			//zappClassPar.setObjpgnum(0);			
			//result = classMgtService.selectMarkedList(pZappAuth, zappClassPar, result);
			
			result = callECM40_PList (handleType, "", "", session);

			logger.debug("=== callECM40_NPList");
			logger.debug("=== result.getResCode(): " + result.getResCode());
			logger.debug("=== result.getResMessage(): " + result.getResMessage());
			
			//List<ZappClassification> res = (List<ZappClassification>) result.getResObj();
			List<ZappContentRes> res = (List<ZappContentRes>) result.getResObj();
			logger.debug("==== res.size():" + res.size());
					
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			for (int i=0; i<res.size(); i++) {
				//ZappClassification classInfo = new ZappClassification();
				//classInfo = (ZappClassification)res.get(i);
				//System.out.println("=== favorite name:" + classInfo.getName());
				//rows = new HashMap<String, String>();
				//rows.put("text", classInfo.getName());
				//rows.put("r_object_type", "pna_myfavorite_doc");
				//rows.put("rid", classInfo.getClassid());
				//rows.put("r_link_type", "0");
				//rows.put("order_code", "0");
				
				ZappContentRes contentRes = new ZappContentRes();
				contentRes = res.get(i);

				String lockerName = contentRes.getLockername();					
				Double dFileSize = contentRes.getFilesize();					
				String sVer = contentRes.getVersion();
				logger.info("=== title:" + contentRes.getTitle());
				logger.info("=== getCreatordeptname:" + contentRes.getCreatordeptname());

				String[] titles = contentRes.getTitle().split("：");
				String rState = ""; //승인상태

				if (contentRes.getState().equals("00")) { //정상
					rState = "정상";
				} else { //??
					rState = contentRes.getState();
				}
				
				if (contentRes.getContenttype().equals ("02")) {
					rows = new HashMap<String, String>();
					//rows.put("a_content_type", "pdftext");
					//rows.put("creator_org_nm", "ECM팀(TBD)");
					rows.put("creator_emp_nm", "[" + contentRes.getCreatordeptname() + "]" + contentRes.getHoldername());
					//rows.put("dm_rnum", "eim_doc");
					rows.put("doc_folderPath", contentRes.getClasspath());
					rows.put("dos_ext", "txt");
					rows.put("object_name", titles[0]);
					rows.put("open_yn","Y");
					rows.put("owner_name", "[" + contentRes.getCreatordeptname() + "]" + contentRes.getHoldername());
//					rows.put("permit_name","읽기권한");
					rows.put("r_creation_date", contentRes.getCreatetime().substring(0, 10));
					rows.put("r_version_label", sVer);
					rows.put("r_object_type", "di_doc");
					//rows.put("r_secu_level", "부서비(TBD)");
					rows.put("r_state", rState);
					rows.put("r_link_type", "0");
					rows.put("r_lock_owner", lockerName);
					rows.put("r_object_id", contentRes.getContentid());
					//rows.put("r_modify_date", contentRes.getCreatetime().substring(0, 10));
					if (contentRes.getUpdatetime() != null && contentRes.getUpdatetime() != "") {
						rows.put("r_modify_date", contentRes.getUpdatetime().substring(0, 19));
					} else {
						rows.put("r_modify_date", "");							
					}
					rows.put("r_lock_type", "0");
					rows.put("r_content_size", dFileSize.toString());
					//rows.put("r_modifier", "yws");
					rows.put("i_chronicle_id", "0912d6878000bf47");  // object_id: 문서의 버전마다의 고유id, chronicle_id: 버전 무관한 문서 자체의 고유id
					list.add(rows);
				}
				
			}
			
			resMap.put("totalCount", res.size());
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocMenu)");
			resMap.put("errcode", "0");
		}
		else {
			logger.debug("===== Invalid RID [" + pIn.getRid() + "]");
		}
		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		logger.debug("===== callAgentDocMenu.do retStr:" + retStr);
		return retStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/main/menu/callAgentDocFolderMenu.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> callAgentDocFolderMenu(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String callAgentDocFolderMenu(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult
					,  Model model, HttpSession session) {

		ZappAuth pZappAuth = new ZappAuth();
		String retStr = "";

		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			String classType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			String upId = "";
	
			logger.info("=== callAgentDocFolderMenu ===");
/*
			byte[] decodedBytes = Base64.getDecoder().decode(pIn.getUser_id());
			String loginId = new String(decodedBytes);
			decodedBytes = Base64.getDecoder().decode(pIn.getPasswd());
			String password = new String(decodedBytes);
			logger.info("=== loginId : " + loginId + ", password: " + password);
			*/
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("password : " + pIn.getPassword());
			logger.info("rid : " + pIn.getRid());
			
			classType = pIn.getFolder_type();
			logger.info("classType:" + classType);

			pZappAuth = getAuth(session);

			//jwjang 20220311 - 
			if (pZappAuth == null) {
				System.out.println("=== pZappAuth is null");
			} else {
				System.out.println("=== pZappAuth is OK, deptid:" + pZappAuth.getObjDeptid());
			}
				
			
			if (pIn.getRid().equals("qlink_report_mgt")) {
			}
			else if (pIn.getRid().equals("common_doc")) { // 전사문서함 하위폴더 조회
				List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
				classType = "N1";
				//upId = testCompanyId;
				upId = pZappAuth.getSessUser().getCompanyid();

				logger.debug("==== call callECM40_list(common_doc) upId:" + upId);
				
				result = callECM40_list(classType, upId, session);
				
				System.out.println("==== callECM40_list resCode:" + result.getResCode());
				
				List<ZappClassification> res = (List<ZappClassification>) result.getResObj();
				Map<String, String> rows = new HashMap<String, String>();
				logger.debug("==== res.size():" + res.size());
				
				int classCnt = 0;
				if (res.size() > 10)
					classCnt = 10;
				else
					classCnt = res.size();
				
				for (int i=0; i<classCnt; i++) {
					ZappClassification classInfo = new ZappClassification();
					classInfo = (ZappClassification)res.get(i);
					
					//ZappClassification classInfo = classRes.getZappClassification();
					rows = new HashMap<String, String>();
					rows.put("text", classInfo.getName());
					rows.put("r_object_type", "dm_folder");
					rows.put("rid", classInfo.getClassid());
					rows.put("folder_type", classType);
					rows.put("prj_code", classInfo.getCode());
					rows.put("r_link_type", "0");
					rows.put("order_code", "0");
					rows.put("class_type", classType);
					retList.add(rows);
								
				}
				resMap.put("totalCount", retList.size());
				resMap.put("list", retList);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocFolderMenu)");
				resMap.put("errcode", "0");				
			}
			else if (pIn.getRid().equals("department_doc")) { // 부서문서함 하위폴더 조회
				List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
				classType = "N2";
				//upId = testDeptId;
				upId = pZappAuth.getObjDeptid();
				upId = pZappAuth.getSessDeptUser().getDeptid();
				logger.debug("==== pZappAuth.getObjDeptid():" + pZappAuth.getObjDeptid() + ", pZappAuth.getSessDeptUser().getDeptid():" + pZappAuth.getSessDeptUser().getDeptid());

				logger.debug("==== call callECM40_listDown(department_doc) upId:" + upId);
				
				result = callECM40_listDown(classType, upId, session);
				
				logger.debug("==== callECM40_listDown resCode:" + result.getResCode());
				
				List<ZappClassificationRes> classRes = (List<ZappClassificationRes>) result.getResObj();
				Map<String, String> rows = new HashMap<String, String>();
				System.out.println("==== res.size():" + classRes.size());
				
				int classCnt = classRes.size();
				
				for (int i=0; i<classCnt; i++) {
					ZappClassification classInfo = (ZappClassification)classRes.get(i).getZappClassification();
					ZappClassAcl classAcl = (ZappClassAcl)classRes.get(i).getZappClassAcl();
					
					logger.debug("=== class ACL["+classAcl.getAcls()+"], code["+classInfo.getCode()+"], name["+classInfo.getName()+"], id["+classInfo.getClassid()+"]");
					
					if (classInfo.getUpid().equals(upId)) {
						rows = new HashMap<String, String>();
						rows.put("text", classInfo.getName());
						rows.put("r_object_type", "dm_folder");
						rows.put("rid", classInfo.getClassid());
						rows.put("folder_type", classType);
						rows.put("prj_code", classInfo.getCode());
						rows.put("permit_level", Integer.toString(classAcl.getAcls()));
						rows.put("r_link_type", "0");
						rows.put("order_code", "0");
						rows.put("class_type", classType);
						retList.add(rows);
					}	
				}
				resMap.put("totalCount", retList.size());
				resMap.put("list", retList);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocFolderMenu)");
				resMap.put("errcode", "0");				
			}
			else if (pIn.getRid().equals("public_doc")) { // 협업문서함 하위폴더 조회
				
				List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
				classType = "N4";
				//upId = "N4";
				
				logger.debug("==== call callECM40_groupList");
				
				result = callECM40_groupList(classType, upId, session);
				
				logger.debug("==== callECM40_groupList resCode:" + result.getResCode());
				
				List<ZappClassification> res = (List<ZappClassification>) result.getResObj();
				Map<String, String> rows = new HashMap<String, String>();
				logger.debug("==== res.size():" + res.size());
				
				int classCnt = 0;
				if (res.size() > 10)
					classCnt = 10;
				else
					classCnt = res.size();
				
				for (int i=0; i<classCnt; i++) {
					ZappClassification classRes = new ZappClassification();
					classRes = res.get(i);
					logger.debug("=== classRes.className : " + classRes.getName());
					logger.debug("=== classRes.getCode : " + classRes.getCode());
					logger.debug("=== classRes.getClassid : " + classRes.getClassid());				
					
					rows = new HashMap<String, String>();
					rows.put("text", classRes.getName());
					rows.put("r_object_type", "dm_folder");
					rows.put("rid", classRes.getClassid());
					rows.put("folder_type", classType);
					rows.put("prj_code", classRes.getCode());
					rows.put("r_link_type", "0");
					rows.put("order_code", "0");
					rows.put("class_type", classType);
					retList.add(rows);
								
				}
				resMap.put("totalCount", retList.size());
				resMap.put("list", retList);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocFolderMenu)");
				resMap.put("errcode", "0");				
			}			
			else if (pIn.getRid().equals("personal_doc")) { // 개인업무함 하위폴더 조회
				List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
				classType = "N3";
				//upId = testUserId;
				upId = pZappAuth.getSessUser().getUserid();
				
				logger.debug("==== call callECM40_list(personal_doc) upId:" + upId);
				
				result = callECM40_list(classType, upId, session);
				
				logger.debug("==== callECM40_list resCode:" + result.getResCode());
				
				List<ZappClassification> res = (List<ZappClassification>) result.getResObj();
				Map<String, String> rows = new HashMap<String, String>();
				logger.debug("==== res.size():" + res.size());
				
				int classCnt = 0;
				if (res.size() > 10)
					classCnt = 10;
				else
					classCnt = res.size();
				
				for (int i=0; i<classCnt; i++) {
					ZappClassification classRes = (ZappClassification)res.get(i);
					logger.debug("=== classRes.className : " + classRes.getName());
					logger.debug("=== classRes.getCode : " + classRes.getCode());
					logger.debug("=== classRes.getClassid : " + classRes.getClassid());				
					
					rows = new HashMap<String, String>();
					rows.put("text", classRes.getName());
					rows.put("r_object_type", "dm_folder");
					rows.put("rid", classRes.getClassid());
					rows.put("folder_type", classType);
					rows.put("prj_code", classRes.getCode());
					rows.put("r_link_type", "0");
					rows.put("order_code", "0");
					rows.put("class_type", classType);
					retList.add(rows);
								
				}
				resMap.put("totalCount", retList.size());
				resMap.put("list", retList);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocFolderMenu)");
				resMap.put("errcode", "0");				
			}
			else if (!pIn.getRid().equals("")) { // 하위폴더 조회
				List<Map<String, String>> retList = new ArrayList<Map<String, String>>();

				upId = pIn.getRid();
				
				logger.debug("==== callECM40_listDown classType [" + classType + "], upId [" + upId + "]");

				result = callECM40_listDown(classType, upId, session);
				logger.debug("==== callECM40_listDown resCode:" + result.getResCode());
				
				List<ZappClassificationRes> classRes = (List<ZappClassificationRes>) result.getResObj();
				Map<String, String> rows = new HashMap<String, String>();
				logger.debug("==== res.size():" + classRes.size());
				
				int classCnt = classRes.size();
				
				for (int i=0; i<classCnt; i++) {
					ZappClassification zappClass = new ZappClassification();
					ZappClassAcl zappClassAcl = new ZappClassAcl();
					
					zappClass = classRes.get(i).getZappClassification();
					zappClassAcl = classRes.get(i).getZappClassAcl();
					
					//logger.debug("=== types [" + zappClass.getTypes() + "], ACL [" + zappClassAcl.getAcls() + "], className [" + zappClass.getName() + "]");
				
					if (zappClass.getUpid().equals(upId)) {
						rows = new HashMap<String, String>();
						rows.put("r_object_type", "dm_folder");
						rows.put("text", 		zappClass.getName());
						rows.put("rid", 		zappClass.getClassid());
						rows.put("folder_type", zappClass.getTypes());
						rows.put("prj_code", 	zappClass.getCode());
						rows.put("permit_level", 	Integer.toString(zappClassAcl.getAcls())); // 폴더권한 (0 : 조회X+등록X, 1 : 조회O+등록X, 2 : 조회O+등록O) 
						rows.put("r_link_type", "0");
						rows.put("order_code", 	"0");
						retList.add(rows);
					}
								
				}
				resMap.put("totalCount", retList.size());
				resMap.put("list", retList);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(callAgentDocFolderMenu)");
				resMap.put("errcode", "0");				
			
			}

			
			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("===== callAgentDocFolderMenu.do retStr:" + retStr);
		return retStr;
		
	}
	
	@RequestMapping(value = "/doc/mydoc/myDocList.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> myDocList (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String myDocList (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		String retStr = "";
		
		logger.error("사용자아이디 : " + pIn.getUserId());
		logger.error("rid : " + pIn.getRid());
		logger.error("R_folder_id : " + pIn.getR_folder_id());
		logger.error("classType : " + pIn.getClassType());
		logger.error("getDoc_box_name : " + pIn.getDoc_box_name());
		logger.error("folder_type : " + pIn.getFolder_type());

		// handleType
		 // 1. Classification - Folder, Classification system and Content type
		 // 2. Link - Linked content list
		 // 3. Share - Shared content list
		 // 4. Lock - Locked content list
		 // 5. Mark - Marked content list
		String handleType = "01"; 
		String classId = pIn.getR_folder_id();
		String fldType = pIn.getFolder_type(); // N1, N2, ...
		
		// 01: 일반 , N1: 전사, N2: 부서, N3: 개인, N4: 협업, 02: 분류체계, 03: 문서유형 
		if (classId.equals("common_doc"))
			fldType = "N1";
		else if (classId.equals("department_doc"))
			fldType = "N2";
		else if (classId.equals("personal_doc"))
			fldType = "N3";
		else if (classId.equals("public_doc"))
			fldType = "N4";
		
		logger.error("fldType : " + fldType);
		
		try {
			if (classId == null || classId.equals("")) {
				resMap.put("totalCount", 0);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(myDocList)");
				resMap.put("errcode", "0");
			} 
			else if (classId.equals("eim_qlink") 		//바로가기
				  || classId.equals("common_doc")		//전사문서함 최상위
				  || classId.equals("department_doc")	//부서문서함 최상위
				  || classId.equals("personal_doc")		//개인문서함 최상위
				  || classId.equals("public_doc")		//협업문서함 최상위
				  )	 
			{ 
				resMap.put("totalCount", 0);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(myDocList)");
				resMap.put("errcode", "0");				
			}
			else {
				result = callECM40_PList (handleType, classId, fldType, session);
		
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				Map<String, String> rows = new HashMap<String, String>();
		
				List<ZappContentRes> res = (List<ZappContentRes>) result.getResObj();
		
				ZappAuth pZappAuth = new ZappAuth();
//				pZappAuth.setObjCompanyid(testCompanyId);
//				pZappAuth.setObjDeptid(testDeptId);
//				pZappAuth.setObjEmpno(testEmpNo);
//				pZappAuth.setObjLoginid(testLoginId);
//				pZappAuth.setObjPasswd(testPasswd);
//				result = getAuth_Test(pZappAuth, session, request, result);
				pZappAuth = getAuth(session);
	
				for (int i=0; i<res.size(); i++) {
					ZappContentRes contentRes = new ZappContentRes();
					contentRes = res.get(i);

					String lockerName = "";
					if (contentRes.getLockername() != null) {
						lockerName = "[" + contentRes.getLockerdeptname() + "] " + contentRes.getLockername();
					}
					Double dFileSize = contentRes.getFilesize();					
					String sVer = contentRes.getVersion();
					//logger.info("=== title:" + contentRes.getTitle());
					String[] titles = contentRes.getTitle().split("：");
					
					logger.info("=== title:" + contentRes.getTitle());

					String acls = "";
					String rState = ""; //승인상태
					String apprDiv = ""; //신청유형
					//ACL 0:접근불가, 1:목록조회, 2:조회, 3:인쇄, 4:다운로드, 5:편집
					if (contentRes.getZappAcl().getAcls() == 0)
						acls = "접근불가";
					else if (contentRes.getZappAcl().getAcls() == 1)
						acls = "목록조회";
					else if (contentRes.getZappAcl().getAcls() == 2)
						acls = "조회";
					else if (contentRes.getZappAcl().getAcls() == 3)
						acls = "인쇄";
					else if (contentRes.getZappAcl().getAcls() == 4)
						acls = "다운로드";
					else if (contentRes.getZappAcl().getAcls() == 5)
						acls = "삭제권한";

					if (contentRes.getState().equals("00")) { //정상
						rState = "정상";
					} else { //??
						rState = contentRes.getState();
					}
					
					if (contentRes.getContenttype().equals ("02")) {
						rows = new HashMap<String, String>();
						rows.put("a_content_type", "pdftext");
						rows.put("creator_org_nm", "ECM팀(TBD)");
						rows.put("creator_emp_nm", contentRes.getCreatorname());
						rows.put("dm_rnum", "eim_doc");
						rows.put("doc_folderPath", contentRes.getClasspath());
						rows.put("dos_ext", "txt");
						rows.put("object_name", titles[0]);
						rows.put("open_yn","Y");
						rows.put("owner_name", "[" + contentRes.getHolderdeptname() + "] " + contentRes.getHoldername());
	//					rows.put("permit_name","읽기권한");
						rows.put("permit_name", acls);
						rows.put("r_creation_date", contentRes.getCreatetime().substring(0, 10));
						rows.put("r_version_label", sVer);
						rows.put("r_object_type", "di_doc");
						rows.put("r_secu_level", "부서비(TBD)");
						rows.put("r_state", rState);
						rows.put("r_link_type", "0");
						rows.put("r_lock_owner", lockerName);
						rows.put("r_object_id", contentRes.getContentid());
						//rows.put("r_modify_date", contentRes.getCreatetime().substring(0, 10));
						if (contentRes.getUpdatetime() != null && contentRes.getUpdatetime() != "") {
							rows.put("r_modify_date", contentRes.getUpdatetime().substring(0, 19));
						} else {
							rows.put("r_modify_date", "");							
						}
						if (lockerName != null && !lockerName.equals("")) {
							rows.put("r_lock_type", "1"); // 잠김 표시
						} else {
							rows.put("r_lock_type", "0");
						}
						rows.put("r_content_size", dFileSize.toString());
						rows.put("r_modifier", "yws");
						rows.put("i_chronicle_id", "0912d6878000bf47"); // object_id: 문서의 버전마다의 고유id, chronicle_id: 버전 무관한 문서 자체의 고유id
						list.add(rows);
					}
				}
		
				resMap.put("totalCount", list.size());
				resMap.put("list", list);
				resMap.put("errmsg", "정상 처리 되었습니다.\n(myDocList)");
				resMap.put("errcode", "0");
			}
			
			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		//System.out.println("===== myDocList.do retStr:" + retStr);
		return retStr;
		
	}
/*
	@RequestMapping(value = "/doc/hook/EDMSOpenW.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String EDMSOpenW (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.debug("================== doc/hook/EDMSOpenW.do");
		logger.debug("=== User_id: " + pIn.getUser_id());
		logger.debug("=== DocBoxName: " + pIn.getDoc_box_name());
		logger.debug("=== Rid: " + pIn.getRid());
		logger.debug("=== RfolderId: " + pIn.getR_folder_id());		
		logger.debug("=== option: " + pIn.getOption());		
		logger.debug("=== target: " + pIn.getTarget());		
		logger.debug("=== target_url: " + pIn.getTarget_url());		

		resMap.put("totalCount", 0);
		resMap.put("errmsg", "정상 처리 되었습니다.\n(The operation is completed.)");
		resMap.put("errcode", "0");

		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		return retStr;
	}
*/

	@RequestMapping(value = "/doc/hook/newDocAttributePc.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String newDocAttributePc (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.info("=== User_id: " + pIn.getUser_id());
		logger.info("=== DocBoxName: " + pIn.getDoc_box_name());
		logger.info("=== Rid: " + pIn.getRid());
		logger.info("=== RfolderId: " + pIn.getR_folder_id());		
		
		String retStr = "";
		return retStr;
	}

	// 바로가기
	@RequestMapping(value = "/main/menu/eimLeftQuickLink.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> eimLeftQuickLink (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String eimLeftQuickLink (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.info("사용자아이디 : " + pIn.getUserId());
		logger.info("rid : " + pIn.getRid());

		resMap.put("errmsg", "정상 처리 되었습니다.\n(eimLeftQuickLink)");
		resMap.put("errcode", "0");

		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		return retStr;
		
	}

	// 승인관리
	@RequestMapping(value = "/main/menu/eimLeftApprManage.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	//public Map<String, Object> eimLeftApprManage (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
	public String eimLeftApprManage (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.info("사용자아이디 : " + pIn.getUserId());
		logger.info("rid : " + pIn.getRid());

		resMap.put("errmsg", "정상 처리 되었습니다.\n(eimLeftApprManage)");
		resMap.put("errcode", "0");

		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		return retStr;

	}

	/**
	 * 세션 정보를 Object 에 저장한다. (테스트용)
	 * @param pSession
	 * @return
	 */
	protected ZstFwResult getAuth_Test(ZappAuth pZappAuth, HttpSession session, HttpServletRequest pRequest, ZstFwResult pResult) {
		
		try {
			//pResult = authMgtService.connect_through_cs(pZappAuth, pRequest, pResult);
			pResult = authMgtService.connect_through_web(pZappAuth, session, pRequest, pResult);
		} catch (ZappException e) {
			pResult = e.getZappResult();
		} catch (SQLException e) {
			if(null != e.getCause()) {
				pResult.setResCode(e.getCause().toString());
			}else {
				pResult.setResCode("ERROR");
			}
			pResult.setMessage(e.getMessage());	
		} 
		
		return pResult;
	}

	@RequestMapping(value = "/doc/hook/newDocAttributePc")
	public ModelAndView taskMngMainView(HttpServletRequest request) {

		ModelAndView mav = new ModelAndView();

		mav.setViewName("/admin/ZecmTaskMng");

		return mav;
	}

	@RequestMapping(value = "/doc/hook/chkDupFolderObjectName.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String chkDupFolderObjectName(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("folder_r_object_id : " + pIn.getFolder_r_object_id());
			logger.info("object_name : " + pIn.getObject_name());
			logger.info("r_folder_name : " + pIn.getR_folder_name());
			
			String upId = pIn.getParent_r_folder_id();
			String HandleType = "N1";
			String folderName = pIn.getR_folder_name();

			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			ZappClassification pZappClass = new ZappClassification();
			pZappClass.setCompanyid(pZappAuth.getSessUser().getCompanyid());
			pZappClass.setUpid(upId);
			//pZappClass.setName(folderName);
			pZappClass.setName(pIn.getObject_name());
			//pZappClass.setTypes(HandleType); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			
			result = classMgtService.selectObject(pZappAuth, pZappClass, result);
			logger.debug("=== classMgtService.selectObject");
			logger.debug("=== result.getResCode(): " + result.getResCode());
			logger.debug("=== result.getResMessage(): " + result.getResMessage());

			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			rows = new HashMap<String, String>();
			rows.put("chkFolderName", "F");
			list.add(rows);
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(chkDupFolderObjectName)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== chkDupFolderObjectName.do retStr:" + retStr);
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/doc/hook/chkDupDocObjectName.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String chkDupDocObjectName(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("folder_r_object_id : " + pIn.getFolder_r_object_id());
			logger.info("object_name : " + pIn.getObject_name());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			String handleType = "02"; // bundle:01, file:02

			ZappContentPar pZappContentPar = new ZappContentPar();
			ZappFile zappFile = new ZappFile();
			zappFile.setFilename(pIn.getObject_name());
			pZappContentPar.setObjType(handleType);
			pZappContentPar.setObjHandleType(handleType);
			pZappContentPar.setObjViewtype(handleType);
			pZappContentPar.setObjRes("COUNT");
			pZappContentPar.setZappFile(zappFile);

			//result = contentMgtService.selectObject(pZappAuth, pZappContentPar, result);
			result = contentMgtService.selectNonPhysicalList(pZappAuth, null, pZappContentPar, result);
			logger.info("======= selectNonPhysicalList Result");
			logger.info("=== Error: " + result.getError());
			logger.info("=== Message: " + result.getMessage());
			logger.info("=== ResCode: " + result.getResCode());
			logger.info("=== ResMessage: " + result.getResMessage());
			
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			rows = new HashMap<String, String>();
			rows.put("chkDocName", "F");
			list.add(rows);
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(chkDupDocObjectName)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			logger.info("===== chkDupDocObjectName.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@RequestMapping(value = "/doc/insNewDocAgent.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String insNewDocAgent(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		
		try {
			logger.debug("=== insNewDocAgent");
			
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("=== insNewDocAgent ===");
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("getRid : " + pIn.getRid());
			logger.info("folder_r_object_id : " + pIn.getFolder_r_object_id());
			logger.info("object_name : " + pIn.getObject_name());
			logger.info("old_r_object_id : " + pIn.getOld_r_object_id());
			logger.info("new_name_doc_yn : " + pIn.getNew_name_doc_yn());
			logger.info("getFileName : " + pIn.getFileName());
			logger.info("getR_folder_name : " + pIn.getR_folder_name());
			logger.info("getSzFilePath : " + pIn.getSzFilePath());

			logger.info("getClassType : " + pIn.getClassType());
			logger.info("getDoc_box_name : " + pIn.getDoc_box_name());
			logger.info("getFolder_level : " + pIn.getFolder_level());
			logger.info("getR_object_id : " + pIn.getR_object_id());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			gTaskId = getTaskId(session);

			logger.info("==== aft gTaskId : " + gTaskId);
			/***

			String contentType = "02"; // bundle:01, file:02

			// Class Type 조회
			handleType = getClassType(pZappAuth, pIn.getFolder_r_object_id());
			
			// 컨텐츠 속성정보
			ZappContentPar pZappContentPar = new ZappContentPar();			
			pZappContentPar.setObjType(contentType);
			pZappContentPar.setObjHandleType(contentType);
			pZappContentPar.setObjViewtype(contentType);
			pZappContentPar.setObjRes("COUNT");
			pZappContentPar.setObjTaskid(gTaskId);
			
			// 파일 정보
			ZappFile zappFile = new ZappFile();
			zappFile.setFilename(pIn.getObject_name());
			zappFile.setObjFileName(pIn.getObject_name());
			String extName = pIn.getObject_name().substring(pIn.getObject_name().lastIndexOf('.')+1);
			zappFile.setObjFileExt(extName);
			pZappContentPar.setZappFile(zappFile);

			// 분류 정보 (폴더, 문서유형,...)
			List<ZappClassObject> pZappClassifications = new ArrayList<ZappClassObject>();

			// 폴더 정보
			ZappClassObject pZappClassObject = new ZappClassObject();
			pZappClassObject.setClassid(pIn.getFolder_r_object_id()); //folder id
			//jwjang 폴더타입 정보가 없어서 N으로 넘김
			pZappClassObject.setClasstype(handleType); // 폴더 타입 (N1:Company, N2:Department, N3:Personal, N4:Cooperation)
			pZappClassifications.add(pZappClassObject);
			
			// 문서유형 정보
			pZappClassObject = new ZappClassObject();
			pZappClassObject.setClassid("33C922F4A64AF53594682E6C5AAF26C596A72B3387FFD6EE30FB62432BE9A1C5"); //일반문서
			pZappClassObject.setClasstype("03"); // 문서유형
			pZappClassifications.add(pZappClassObject);
			
			pZappContentPar.setZappClassObjects(pZappClassifications);

			// 보존년한
			pZappContentPar.setObjRetention("C718B4AB1C9C824375D0C7464E2CEEB5CE9DBF0A2B58DDC2273B832AFF5625A3"); //영구
			
			// Set of addObject
			result = contentMgtService.addContentNoFile(pZappAuth, pZappContentPar, result);

				
			logger.info("======= addContent Result");
			logger.info("=== Error: " + result.getError());
			logger.info("=== Message: " + result.getMessage());
			logger.info("=== ResCode: " + result.getResCode());
			logger.info("=== ResMessage: " + result.getResMessage());
			*/
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> rows = new HashMap<String, String>();
			rows = new HashMap<String, String>();
			
			rows.put("r_object_id", pIn.getFolder_r_object_id()); //////////////////
			
			list.add(rows);
			
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(insNewDocAgent)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}		
			logger.info("===== insNewDocAgent.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

	// 현재버전 삭제
	@RequestMapping(value = "/doc/common/docDel.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docDel(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());
			logger.info("r_folder_id : " + pIn.getR_folder_id());
			
			String mfileid = pIn.getR_object_id();

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			String handleType = "02"; // bundle: 01, file: 02

			ZappClassObject classObj = new ZappClassObject();
			classObj.setClassid(pIn.getR_folder_id()); // folder id
			
			// 특정버전 삭제
			ZappContentPar contentPar = new ZappContentPar();
			contentPar.setObjType(handleType); //파일
			contentPar.setContentid(mfileid);
			contentPar.setObjTaskid(gTaskId);
			contentPar.setZappClassObject(classObj);

			result = contentMgtService.discardSpecificVersionContent(pZappAuth, contentPar, result);
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docDel)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docDelAllVersiont.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	// 전체버전 삭제
	@RequestMapping(value = "/doc/common/docDelAllVersion.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docDelAllVersion(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		String mFileId = "";

		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());
			logger.info("doc_empty : " + pIn.getNew_name_doc_yn());

			mFileId = pIn.getR_object_id();
			
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			if (gTaskId == null || gTaskId.equals(""))
				gTaskId = getTaskId(session);

			String handleType = "02"; // bundle : 01, file : 02

			//ZappFile zappFile = new ZappFile();
			//zappFile.setMfileid(mFileId);			
			//contentMgtService.deleteObject(pZappAuth, zappFile, result);			
			
			// 휴지통
			ZappContentPar contentPar = new ZappContentPar();
			contentPar.setObjType(handleType); //파일
			contentPar.setObjTaskid(gTaskId);
			contentPar.setContentid(mFileId);
			result = contentMgtService.disableContent(pZappAuth, contentPar, result);
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docDelAllVersion)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docDelAllVersiont.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	// 휴지통 복원
	@RequestMapping(value = "/doc/common/docDelCancel.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docDelCancel(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		String mFileId = "";

		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());
			logger.info("doc_empty : " + pIn.getNew_name_doc_yn());

			mFileId = pIn.getR_object_id();
			
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			String handleType = "02"; // bundle : 01, file : 02

			//ZappFile zappFile = new ZappFile();
			//zappFile.setMfileid(mFileId);			
			//contentMgtService.deleteObject(pZappAuth, zappFile, result);			
			
			// 휴지통
			ZappContentPar contentPar = new ZappContentPar();
			contentPar.setObjType(handleType); //파일
			contentPar.setObjTaskid(gTaskId);
			contentPar.setContentid(mFileId);
			result = contentMgtService.enableContent(pZappAuth, contentPar, result);
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docDelCancel)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docDelAllVersiont.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	// 휴지통 폐기
	@RequestMapping(value = "/doc/common/docDiscard.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docDiscard(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		String mFileId = "";

		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());
			logger.info("doc_empty : " + pIn.getNew_name_doc_yn());

			mFileId = pIn.getR_object_id();
			
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			if (gTaskId == null || gTaskId.equals(""))
				gTaskId = getTaskId(session);

			String handleType = "02"; // bundle : 01, file : 02

			//ZappFile zappFile = new ZappFile();
			//zappFile.setMfileid(mFileId);			
			//contentMgtService.deleteObject(pZappAuth, zappFile, result);			
			
			// 휴지통
			ZappContentPar contentPar = new ZappContentPar();
			contentPar.setObjType(handleType); //파일
			contentPar.setObjTaskid(gTaskId);
			contentPar.setContentid(mFileId);
			logger.debug("===== contentMgtService.discardContent Start");
			result = contentMgtService.discardContent(pZappAuth, contentPar, result);
			logger.debug("===== contentMgtService.discardContent End");
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docDiscard)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			logger.debug("===== docDelAllVersiont.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	@RequestMapping(value = "/doc/common/docUrlCopyNew.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docUrlCopyNew(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session, HttpServletRequest request) {

		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());

			System.out.println("=== getSzFilePath: " +  pIn.getSzFilePath());
			System.out.println("=== getTarget_url: " +  pIn.getTarget_url());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			String handleType = "02"; // bundle : 01, file : 02

			System.out.println("=== Error: " + result.getError());
			System.out.println("=== Message: " + result.getMessage());
			System.out.println("=== ResCode: " + result.getResCode());
			System.out.println("=== ResMessage: " + result.getResMessage());		
			
			String ufileid = "";
			String fileName = "";
			String versionid = "";

			ZArchVersion pZArchVersion = new ZArchVersion();
			pZArchVersion.setMfileid(pIn.getR_object_id()); //jwjang 버전이 1개일 경우에는 상관없으나 여러개일 경우 버전 아이디를 넘겨야 함.. 수정 필요
			List<ZArchVersion> versionList =  zArchVersionService.rMultiRows_List(pZArchVersion);
					
			for (int i=0; i<versionList.size(); i++) {
				ZArchVersion zarchVersion = (ZArchVersion)versionList.get(i);
				ufileid = zarchVersion.getUfileid();
				fileName = zarchVersion.getFilename();
				versionid = zarchVersion.getVersionid();
				logger.debug("=== ufileid:" + ufileid);				
				logger.debug("=== fileName:" + fileName);				
				logger.debug("=== versionid:" + versionid);				
			}

			// 결과 리턴
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();

			Map<String, String> rows = new HashMap<String, String>();
			rows = new HashMap<String, String>();

			String remoteAddr = request.getRemoteAddr();
			String localAddr = request.getLocalAddr();
			logger.debug("=== remoteAddr:" + remoteAddr);			
			logger.debug("=== localAddr:" + localAddr);			
			int port = request.getServerPort();
			
			String contextPath = request.getContextPath(); // /ecm40
			//logger.debug("=== contextPath:" + contextPath);
			
			//http://localhost:8080/ecm40/api/file/fileDown/29E763E047B564CFDF4E5C5589662886684C0BE25CC0C92B2F8EA1C652D09801
			String docUrl = "http://" + localAddr + ":" + port + contextPath + "/api/file/fileDown/" + versionid;
			logger.debug("=== docUrl:" + docUrl);
			
			rows.put("docurl", docUrl);
			
			list.add(rows);
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docUrlCopyNew)");
			resMap.put("errcode", "0");
			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docUrlCopyNew.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	@RequestMapping(value = "/doc/common/docMove.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docMove(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		//http://127.0.0.1:8080/ecm40/doc/common/docMove.do?user_id=eXdz&src_r_object_id=40F409CE66CE54161903A5CCD41766FAAF5CA542C9E6E580DFA1E940905885C3&tgt_r_folder_id=C6A67ABC003CA6A1179A5086D4D168BB5B1606FAF2E6978DCA1938991B1E6F4D)

		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("==== /doc/common/docMove.do");
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("src_r_object_id : " + pIn.getSrc_r_object_id()); // mfileid
			logger.info("tgt_r_folder_id : " + pIn.getTgt_r_folder_id());
			
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			try {
				ZappContentPar contentPar = new ZappContentPar();
				contentPar.setObjTaskid(gTaskId);
				contentPar.setObjType("02"); //파일
				contentPar.setContentid(pIn.getSrc_r_object_id());
				
				List<ZappClassObject> classObjects = new ArrayList<ZappClassObject>();
				ZappClassObject classObject = new ZappClassObject();
				classObject.setClassid(""); // Src ClassID
				classObjects.add(classObject);
				classObject = new ZappClassObject();
				classObject.setClassid(pIn.getTgt_r_folder_id()); // Tgt ClassID
				classObjects.add(classObject);				
				contentPar.setZappClassObjects(classObjects);

				result = contentMgtService.relocateContent(pZappAuth, contentPar, result);
			} catch (ZappException e) {
				result = e.getZappResult();
			} catch (SQLException e) {
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
			resMap.put("totalCount", 0);
			//resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docDel)");
			resMap.put("errcode", "0");
			
			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docCopy.do retStr:" + retStr);
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return retStr;
		}
	}

	@RequestMapping(value = "/doc/common/docCopy.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docCopy(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		// http://127.0.0.1:8080/ecm40/doc/common/docCopy.do?user_id=eXdz&src_r_object_id=40F409CE66CE54161903A5CCD41766FAAF5CA542C9E6E580DFA1E940905885C3&tgt_r_folder_id=C6A67ABC003CA6A1179A5086D4D168BB5B1606FAF2E6978DCA1938991B1E6F4D)

		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("==== /doc/common/docCopy.do");
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("src_r_object_id : " + pIn.getSrc_r_object_id()); // mfileid
			logger.info("tgt_r_folder_id : " + pIn.getTgt_r_folder_id());
			
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			try {
				ZappContentPar contentPar = new ZappContentPar();
				contentPar.setObjTaskid(gTaskId);
				contentPar.setObjType("02"); //파일
				contentPar.setContentid(pIn.getSrc_r_object_id());
				
				List<ZappClassObject> classObjects = new ArrayList<ZappClassObject>();
				ZappClassObject classObject = new ZappClassObject();
				classObject.setClassid(""); // Src ClassID
				classObjects.add(classObject);
				classObject = new ZappClassObject();
				classObject.setClassid(pIn.getTgt_r_folder_id()); // Tgt ClassID
				classObjects.add(classObject);				
				contentPar.setZappClassObjects(classObjects);

				result = contentMgtService.replicateContent(pZappAuth, contentPar, result);
			} catch (ZappException e) {
				result = e.getZappResult();
			} catch (SQLException e) {
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
			resMap.put("totalCount", 0);
			//resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docDel)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docCopy.do retStr:" + retStr);
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return retStr;
		}
	}
	
	// 중요문서 설정
	@RequestMapping(value = "/doc/common/docInsKey.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docInsKey(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			String handleType = "02"; // bundle:01, file:02

			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			ZappContentPar contentPar = new ZappContentPar();
			contentPar.setObjTaskid(gTaskId);
			contentPar.setObjType(handleType);
			contentPar.setContentid(pIn.getR_object_id());

			// 즐겨찾기 등록
			result = contentMgtService.markContent(pZappAuth, contentPar, result);
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docInsKey)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docInsKey.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	// 즐겨찾기 해제
	@RequestMapping(value = "/doc/common/docDelKey.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docDelKey(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			String handleType = "02"; // bundle:01, file:02

			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			ZappContentPar contentPar = new ZappContentPar();
			contentPar.setObjTaskid(gTaskId);
			contentPar.setObjType(handleType);
			contentPar.setContentid(pIn.getR_object_id());

			// 즐겨찾기  해제
			result = contentMgtService.unmarkContent(pZappAuth, contentPar, result);

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docDelKey)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docDelKey.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}

	// 문서명 변경
	@RequestMapping(value = "/doc/common/docRename.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docRename(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {

		String retStr = "";
		
		//user_id=eXdz&r_object_id=FB61F09767EB7A06E764DCB8753926E8A7F8368D0CC0482165C10F1E73817E7B&object_name=%5b%eb%b3%84%ed%91%9c%201%5d%20%ea%b8%b0%eb%a1%9d%eb%ac%bc%ec%9d%98%20%eb%b3%b4%ec%a1%b4%ea%b8%b0%ea%b0%84%eb%b3%84%20%ec%b1%85%ec%a0%95%20%ea%b8%b0%ec%a4%80%28%ec%a0%9c26%ec%a1%b0%ec%a0%9c1%ed%95%ad%20%ea%b4%80%eb%a0%a8%29.pdf

		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			//String handleType = ""; // 01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
	
			logger.info("userId : " + pIn.getUserId());
			logger.info("user_id : " + pIn.getUser_id());
			logger.info("r_object_id : " + pIn.getR_object_id());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			String handleType = "02"; // bundle:01, file:02

			ZappFile zappFile = new ZappFile();
			zappFile.setFilename(pIn.getObject_name());
			
			ZappContentPar contentPar = new ZappContentPar();
			contentPar.setObjTaskid(gTaskId);
			contentPar.setObjType(handleType);
			contentPar.setContentid(pIn.getR_object_id());
			contentPar.setZappFile(zappFile);
			result = contentMgtService.changeContent(pZappAuth, contentPar, result);
			
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			
			resMap.put("totalCount", 0);
			resMap.put("list", list);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docRename)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			System.out.println("===== docDelKey.do retStr:" + retStr);
			return retStr;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@RequestMapping(value = "/doc/common/isCheckout.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String isCheckout(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			logger.info("사용자아이디 : " + pIn.getUserId());
			logger.info("r_object_id : " + pIn.getR_object_id());

			//ZappAuth pZappAuth = new ZappAuth();
			//pZappAuth = getAuth(session);

			
			resMap.put("errmsg", "정상 처리 되었습니다.\n(isCheckout)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/doc/hook/checkout.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public ResponseEntity checkout(@ModelAttribute @Valid ZappCentral pIn,  HttpServletResponse response, BindingResult bindingResult
							,  Model model, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		ResponseEntity entity = null;

		try {
			logger.info("사용자아이디 : " + pIn.getUserId());
			logger.info("rid : " + pIn.getRid());
			logger.info("getFileName : " + pIn.getFileName());
			logger.info("getFolder_r_object_id : " + pIn.getFolder_r_object_id());
			logger.info("getR_object_id : " + pIn.getR_object_id());
			logger.info("getSzFilePath : " + pIn.getSzFilePath());
			
			String mFileId = pIn.getR_object_id();

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			String handleType = "02"; // bundle : 01, file : 02
			String ufileid = "";
			String fileName = "";

			ZArchVersion pZArchVersion = new ZArchVersion();
			pZArchVersion.setMfileid(mFileId); //jwjang 버전이 1개일 경우에는 상관없으나 여러개일 경우 버전 아이디를 넘겨야 함.. 수정 필요
			List<ZArchVersion> versionList =  zArchVersionService.rMultiRows_List(pZArchVersion);
					
			for (int i=0; i<versionList.size(); i++) {
				ZArchVersion zarchVersion = (ZArchVersion)versionList.get(i);
				ufileid = zarchVersion.getUfileid();
				fileName = zarchVersion.getFilename();
				logger.debug("=== ufileid:" + ufileid);				
				logger.debug("=== fileName:" + fileName);				
			}

			ZArchFile zArchFile = new ZArchFile();
			zArchFile.setUfileid(ufileid);
			zArchFile = zArchFileService.rSingleRow_Vo(zArchFile);

			ZArchResult zArchResult = zArchFileMgtService.getExistingArchivePath(zArchFile); 
			String filePath = (String)zArchResult.getResult()+zArchFile.getUfileid();						

			logger.debug("=== filePath: " + filePath);

			String mimeType ="application/octet-stream";
			FileInputStream fis = null;

			fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
			File regFile = new File(filePath);
			if(regFile.exists()){
				int contentlength = (int)regFile.length();					
				response.setContentType(mimeType.toString());
				//inline : 
				response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
				
				fis = FileUtils.openInputStream(regFile);							

				//암호화 여부 확인
				if(zArchFile.getIsencrypted().toString().trim().equals("Y")){
					logger.info("=== encrypt file  decrypt down: {} ",fileName);
					//원본 파일 사이즈를 지정한다..
					response.setContentLength(Integer.parseInt(String.valueOf(Math.round(zArchFile.getFilesize()))));								
					CryptoNUtil enc = new CryptoNUtil();								
					enc.doDecrypt(fis, response.getOutputStream());
				}else {
					response.setContentLength(contentlength);//물리파일의 파일 크기를 지정한다.(원본이므로)
					logger.info("=== NonEncrypt file down: {} ", fileName);
					IOUtils.copy(fis, response.getOutputStream());
				}

				entity = new ResponseEntity("SUCCESS", HttpStatus.CREATED);
			}			

		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("Content-Disposition", "attachment; filename=\" Internal Server Error \"");
			//response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			entity = new ResponseEntity("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return entity;
		
	}

	@RequestMapping(value = "/doc/common/docCheckOut.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docCheckOut(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			logger.info("CompanyCode : " + pIn.getCompanycode());
			logger.info("UserId : " + pIn.getUserId());
			logger.info("User_id : " + pIn.getUser_id());
			logger.info("Password : " + pIn.getPassword());
			logger.info("r_object_id : " + pIn.getR_object_id());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			if (pZappAuth == null) {
				if (pIn.getUserId() == null)
					pIn.setUserId(pIn.getUser_id());
				
				// 로그인 처리
				loginpass(pIn, bindingResult, model, session);
				pZappAuth = getAuth(session);
			}

			Calendar cal = Calendar.getInstance();
			String format = "yyyy-MM-dd";
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			cal.add(cal.DATE, +1); //날짜를 하루 뺀다.
			String releaseDate = sdf.format(cal.getTime());
			System.out.println("=== releaseDate:" + releaseDate);
			
			ZappLockedObject pZappLockedObject = new ZappLockedObject();
			pZappLockedObject.setReleasetime(releaseDate);
			pZappLockedObject.setReason("CENTRAL_AUTO_CHECKOUT");

			ZappContentPar pZappContentPar = new ZappContentPar();
			pZappContentPar.setObjIsTest("N");
			pZappContentPar.setObjDebugged(false);
			pZappContentPar.setObjType("02");//00:분류,01:번들,02:파일
			pZappContentPar.setContentid(pIn.getR_object_id());
			pZappContentPar.setObjTaskid(gTaskId);
			pZappContentPar.setZappLockedObject(pZappLockedObject);

			result = contentMgtService.lockContent(pZappAuth, pZappContentPar, result);
			resMap.put("errmsg", "정상 처리 되었습니다.\n(docCheckOut)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 편집으로 열기 후 저장하지 않고 종료할 경우
	@RequestMapping(value = "/doc/common/docCheckOutCancel.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String docCheckOutCancel(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			logger.info("UserId : " + pIn.getUserId());
			logger.info("User_id : " + pIn.getUser_id());
			logger.info("Password : " + pIn.getPassword());
			logger.info("r_object_id : " + pIn.getR_object_id());

			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			ZappContentPar pZappContentPar = new ZappContentPar();
			pZappContentPar.setObjIsTest("N");
			pZappContentPar.setObjDebugged(false);
			pZappContentPar.setObjType("02");//00:분류,01:번들,02:파일
			pZappContentPar.setContentid(pIn.getR_object_id());
			pZappContentPar.setObjTaskid(gTaskId);
			pZappContentPar.setHasfile(false);

			result = contentMgtService.unlockContent(pZappAuth, pZappContentPar, result);

			resMap.put("errmsg", "정상 처리 되었습니다.\n(docCheckOutCancel)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/folder/common/folderAdd.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String folderAdd(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			logger.debug("사용자아이디 : " + pIn.getUserId());
			logger.debug("pIn.getUser_id : " + pIn.getUser_id());	
			logger.debug("parent_r_folder_id : " + pIn.getParent_r_folder_id());
			logger.debug("r_folder_name : " + pIn.getR_folder_name());

			String upId = pIn.getParent_r_folder_id();
			String folderName = pIn.getR_folder_name();
			
			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			logger.debug("=== sessDeptUser.userid: " + pZappAuth.getSessDeptUser().getUserid());
			logger.debug("=== sessDeptUser.deptid: " + pZappAuth.getSessDeptUser().getDeptid());

			Calendar cal = Calendar.getInstance();
			String format = "yyyy-MM-dd-HHmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			String currDate = sdf.format(cal.getTime());
			String classCodeDept = pZappAuth.getSessCompany().getAbbrname() + currDate;
			//ZSTTT2022-06-28-174859
			logger.debug("=== classCodeDept: " + classCodeDept);
			
			// Class Type 조회
			String HandleType = "";
			if (pIn.getParent_r_folder_id().equals("common_doc")) {
				HandleType = ZappConts.TYPES.CLASS_FOLDER_COMPANY.type; // N1
			} else if (pIn.getParent_r_folder_id().equals("department_doc")) {
				HandleType = ZappConts.TYPES.CLASS_FOLDER_DEPARTMENT.type; // N2
			} else if (pIn.getParent_r_folder_id().equals("personal_doc")) {
				HandleType = ZappConts.TYPES.CLASS_FOLDER_PERSONAL.type; // N3
			} else if (pIn.getParent_r_folder_id().equals("public_doc")) {
				HandleType = ZappConts.TYPES.CLASS_FOLDER_COLLABORATION.type; // N4
			} else {
				HandleType = getClassType(pZappAuth, pIn.getParent_r_folder_id());
			}
			logger.debug("HandleType : " + HandleType);

			ZappClassificationPar pZappClassPar = new ZappClassificationPar();
			if (upId.equals("personal_doc")) {
				pZappClassPar.setUpid(pZappAuth.getSessDeptUser().getUserid());				
				//jwjang 20220628 Code는 자동생성
				//pZappClassPar.setCode(folderName);			
			} else if (upId.equals("department_doc")) {
				pZappClassPar.setUpid(pZappAuth.getSessDeptUser().getDeptid());				
				pZappClassPar.setCode(classCodeDept);
			} else if (upId.equals("common_doc")) { // 전사문서함
				pZappClassPar.setUpid(upId);
				pZappClassPar.setCode(folderName);
			} else {
				pZappClassPar.setUpid(upId);
			}			
			pZappClassPar.setName(folderName);
			pZappClassPar.setTypes(HandleType); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			pZappClassPar.setHolderid(pIn.getUser_id());
			
			// 노드 권한  - 상위권한 상속받아서 입력하도록 수정 필요 
			List<ZappClassAcl> classAclList = new ArrayList<ZappClassAcl>();
			ZappClassAcl zappClassAcl = new ZappClassAcl();
			//zappClassAcl.setAclobjid(testDeptId); //연구소
			zappClassAcl.setAclobjid(pZappAuth.getObjDeptid());
			zappClassAcl.setAclobjtype("02"); 	// 권한대상유형(01:사용자,02:부서,03:그룹)
			zappClassAcl.setAcls(2); 			// 권한(0:조회불가 + 등록불가, 1:조회가능 + 등록불가, 2:조회가능 + 등록가능)
			classAclList.add(zappClassAcl);
			pZappClassPar.setZappClassAcls(classAclList);

			// 컨텐츠 권한 - 상위권한 상속받아서 입력하도록 수정 필요 
			List<ZappContentAcl> contentAclList = new ArrayList<ZappContentAcl>();
			ZappContentAcl zappContentAcl = new ZappContentAcl();
			//zappContentAcl.setAclobjid(testDeptId); //연구소
			zappContentAcl.setAclobjid(pZappAuth.getObjDeptid());	
			zappContentAcl.setAclobjtype("02");
			zappContentAcl.setAcls(2);
			contentAclList.add(zappContentAcl);
			pZappClassPar.setZappContentAcls(contentAclList);
			
			try {
				logger.debug("=== call classMgtService.addClass");
				// Node 생성
				//result = classMgtService.addObject(pZappAuth, pZappClassPar, result);
				result = classMgtService.addClass(pZappAuth, pZappClassPar, result);
			} catch (ZappException e) {
				result = e.getZappResult();
			} catch (SQLException e) {
			}
			
			logger.debug("=== classMgtService.addClass");
			logger.debug("=== result.getResCode(): " + result.getResCode());
			logger.debug("=== result.getResMessage(): " + result.getResMessage());
			logger.debug("=== resObj: " + result.getResObj());
			
			// 결과 리턴
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();

			Map<String, String> rows = new HashMap<String, String>();
			rows = new HashMap<String, String>();
			
			rows.put("r_folder_id", (String)result.getResObj());
			rows.put("object_name", folderName);
			
			list.add(rows);

			
			if (result.getResCode().equals("0000")) {				
				resMap.put("errmsg", "정상 처리 되었습니다.\n(folderAdd)");
				resMap.put("totalCount", 1);
				resMap.put("list", list);
				resMap.put("errcode", "0");
			} else {
				resMap.put("errmsg", result.getResMessage());
				resMap.put("errcode", result.getResCode());
			}

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/folder/common/folderRename.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String folderRename(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			logger.info("사용자아이디 : " + pIn.getUserId());
			logger.info("r_folder_id : " + pIn.getR_folder_id());
			logger.info("object_name : " + pIn.getObject_name());

			String folderId = pIn.getR_folder_id();
			String HandleType = "N4"; //협업노드
			String folderName = pIn.getObject_name();
			
			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			ZappClassification pZappClass = new ZappClassification();
			pZappClass.setCompanyid(pZappAuth.getSessUser().getCompanyid());
			pZappClass.setClassid(folderId);
			pZappClass.setName(folderName);
			//pZappClass.setTypes(HandleType); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			
			result = classMgtService.changeObject(pZappAuth, pZappClass, result);
			logger.debug("=== classMgtService.changeObject");
			logger.debug("=== result.getResCode(): " + result.getResCode());
			logger.debug("=== result.getResMessage(): " + result.getResMessage());
					
			resMap.put("errmsg", "정상 처리 되었습니다.\n(folderRename)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/folder/common/folderDel.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String folderDel(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			logger.info("사용자아이디 : " + pIn.getUserId());
			logger.info("r_folder_id : " + pIn.getR_folder_id());
			logger.info("object_name : " + pIn.getObject_name());

			String folderId = pIn.getR_folder_id();
			String HandleType = "N2"; //
			String folderName = pIn.getObject_name();
			
			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			ZappClassification pZappClass = new ZappClassification();
			pZappClass.setCompanyid(pZappAuth.getSessUser().getCompanyid());
			pZappClass.setClassid(folderId);
			pZappClass.setName(folderName);
			//pZappClass.setTypes(HandleType); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			
			try {
				// isActive를 N으로 설정
				result = classMgtService.disableClass(pZappAuth, null, pZappClass, result);
				logger.debug("=== classMgtService.disableClass");
				logger.debug("=== result.getResCode(): " + result.getResCode());
				logger.debug("=== result.getResMessage(): " + result.getResMessage());
				
				if (result.getResCode().equals("0000")) {
					// 폐기 처리
					result = classMgtService.deleteObject(pZappAuth, pZappClass, result);
				}
						
				resMap.put("errmsg", "정상 처리 되었습니다.\n(folderDel)");
				resMap.put("errcode", "0");
			} catch (ZappException e) {
				logger.debug("=== result.getResCode(): " + result.getResCode());
				logger.debug("=== result.getResMessage(): " + result.getResMessage());
				logger.debug("=== e.getMessage(): " + e.getMessage());
				
				resMap.put("errmsg", e.getMessage());
				resMap.put("errcode", result.getResCode());			
				
				e.printStackTrace();
			}

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(value = "/folder/dept/insFolderFavorite.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String insFolderFavorite(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		try {
			ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
			Map<String, Object> resMap = new HashMap<String, Object>();
			
			logger.info("사용자아이디 : " + pIn.getUserId());
			logger.info("r_folder_id : " + pIn.getR_folder_id());
			logger.info("code : " + pIn.getCode());

			String folderId = pIn.getR_folder_id();
			String HandleType = "N2"; //
			String folderName = pIn.getObject_name();
			
			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			ZappContentPar pZappContentPar = new ZappContentPar();
			pZappContentPar.setObjIsTest("N");
			pZappContentPar.setObjDebugged(false);
			pZappContentPar.setObjType("00");//00:분류,01:번들,02:파일
			pZappContentPar.setContentid(folderId);
			pZappContentPar.setObjTaskid(gTaskId);
			//pZappClass.setTypes(HandleType); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			
			result = contentMgtService.markContent(pZappAuth, pZappContentPar, result);
			//result = classMgtService.deleteObject(pZappAuth, pZappClass, result);
			//logger.debug("=== classMgtService.chadeleteObjectngeObject");
			logger.debug("=== result.getResCode(): " + result.getResCode());
			logger.debug("=== result.getResMessage(): " + result.getResMessage());
					
			resMap.put("errmsg", "정상 처리 되었습니다.\n(folderRename)");
			resMap.put("errcode", "0");

			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 스마트 검색
	@RequestMapping(value = "/doc/hook/searchPermitYn.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String searchPermitYn(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.info("=== User_id: " + pIn.getUser_id());

		ZappAuth pZappAuth = new ZappAuth();
		
		session.setAttribute("sessLang", "ko");
		pZappAuth = getAuth(session);

		//logger.info("==== auth.lang:" + pZappAuth.getObjlang());

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> rows = new HashMap<String, String>();

		rows.put("searchPermitYn", "Y");

		list.add(rows);

		resMap.put("totalCount", list.size());
		resMap.put("list", list);
		resMap.put("errmsg", "정상 처리 되었습니다.\n(searchPermitYn)");
		resMap.put("errcode", "0");

		//return resMap;
		ObjectMapper mapper = new ObjectMapper(); 
		String retStr = "";
		try {
			retStr = mapper.writeValueAsString(resMap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		
		return retStr;		
	}

	// 반입 (버전업)
	@RequestMapping(value = "/doc/hook/insEimCompMultiReqAnswer.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public String insEimCompMultiReqAnswer(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.info("=== User_id: " + pIn.getUser_id());
		logger.info("=== Userid: " + pIn.getUserId());
		logger.info("=== r_object_id: " + pIn.getR_object_id());

		try {
			//ZappAuth pZappAuth = new ZappAuth();
			//pZappAuth = getAuth(session);
	
			/*
			// Already processed in EDMSSaveW
			ZappContentPar pZappContentPar = new ZappContentPar();
			pZappContentPar.setObjIsTest("N");
			pZappContentPar.setObjDebugged(false);
			pZappContentPar.setObjType("02");//00:분류,01:번들,02:파일
			pZappContentPar.setContentid(pIn.getR_object_id());
			pZappContentPar.setObjTaskid(testTaskId);
			pZappContentPar.setHasfile(false);
	
			result = contentMgtService.unlockContent(pZappAuth, pZappContentPar, result);
			*/
			
			resMap.put("errmsg", "정상 처리 되었습니다.\n(insEimCompMultiReqAnswer)");
			resMap.put("errcode", "0");
	
			//return resMap;
			ObjectMapper mapper = new ObjectMapper(); 
			String retStr = "";
			try {
				retStr = mapper.writeValueAsString(resMap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}		
			return retStr;		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	@RequestMapping(value = "/doc/content/view", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)
	public ZstFwStatus contentViewExt(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		
		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		logger.debug("== doc/content/view session: " + session);

		/*
		pZappAuth.setObjCompanyid(testCompanyId);
		pZappAuth.setObjDeptid(testDeptId);
		pZappAuth.setObjEmpno(testEmpNo);
		pZappAuth.setObjLoginid(testLoginId);
		pZappAuth.setObjPasswd(testPasswd);
		result = getAuth_Test(pZappAuth, session, request, result);
		*/
		pZappAuth = getAuth(session);
		
		try {
			result = contentMgtService.selectContent(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (SQLException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 
		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}	
	
	
	@RequestMapping(value = "/doc/hook/EDMSOpenW.do", method ={RequestMethod.POST, RequestMethod.GET}, produces = "application/json; charset=utf-8")
	public ModelAndView EDMSOpenW (@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		logger.info("=== CompanyCode: " + pIn.getCompanycode());
		logger.info("=== User_id: " + pIn.getUser_id());
		logger.info("=== UserId: " + pIn.getUserId());
		logger.info("=== password: " + pIn.getPassword());
		logger.info("=== DocBoxName: " + pIn.getDoc_box_name());
		logger.info("=== Rid: " + pIn.getRid());
		logger.info("=== RfolderId: " + pIn.getR_folder_id());		
		logger.info("=== option: " + pIn.getOption());		
		logger.info("=== target: " + pIn.getTarget());		
		logger.info("=== target_url: " + pIn.getTarget_url());		

		if (pIn.getUserId() == null || pIn.getUserId().equals(""))
			pIn.setUserId(pIn.getUser_id());
		
		//login(pIn, bindingResult, model, session);
		
		ZappAuth pZappAuth = new ZappAuth();
		/*
		String testCompanyId = "FB7EE86AA31D87723A5BD09E6A20AF1F1A63E287321EF4F5DE410246EDF4C242";
		String testDeptId = "FC14FCD8DB1FCE33D036928D31DD8CC0AB49546C3ACAD133AA18283700C9D4C6";
		String testEmpNo = "10012";
		String testLoginId = "jwj";
		String testPasswd = "jwj!@#";

		pZappAuth.setObjCompanyid(testCompanyId);
		pZappAuth.setObjDeptid(testDeptId);
		pZappAuth.setObjEmpno(testEmpNo);
		pZappAuth.setObjLoginid(testLoginId);
		pZappAuth.setObjPasswd(testPasswd);
		result = getAuth_Test(pZappAuth, session, request, result);
		*/
		
		//jwjang 20220401 세션이 없을 경우 로그인처리
		//pZappAuth = (ZappAuth) session.getAttribute("Authentication");
		pZappAuth = getAuth(session);
		if (pZappAuth == null) {
			String base64EncodeUserId = new String(Base64.getEncoder().encode(pIn.getUserId().getBytes()));
			logger.error("=== Org UserId [" + pIn.getUserId() + "], Base64Encode [" + base64EncodeUserId + "]");
			pIn.setUserId(base64EncodeUserId);
			//pIn.setPassword(pIn.getPassword());
			
			if (pIn.getUserId() != null && pIn.getUserId() != "") {
				// 로그인 처리
				loginpass(pIn, bindingResult, model, session);
			} else {
				logger.error("=== pIn.getUserId is NULL....");
			}
		}
		session.setAttribute("sessLang", "ko");

		ModelAndView mav = new ModelAndView();
		
		mav.addObject("contentid", pIn.getTarget());
		mav.addObject("taskid", gTaskId);
		mav.addObject("opType", pIn.getOption());
/*
		if (pIn.getOption().equals("exportappr")) {  // 승인
			mav.addObject("option", pIn.getOption());
			mav.addObject("selectReasonType", "approve");
			//mav.setViewName("/user/ZecmReasonPopExt");			
			mav.setViewName("/user/ZecmReasonRegExt");			
		} else {
			//mav.setViewName("/pcexplorer/doc/hook/EDMSOpenWin");
			mav.setViewName("/user/ZecmFileInfoExt");
		}
*/
		if (pIn.getOption().equals("docAttribute")) { // 문서속성보기			
			mav.setViewName("/user/ZecmFileInfoExt");			
		} else if (pIn.getOption().equals("docFolderAttr")) { // 폴더속성보기			
			mav.setViewName("/user/ZecmFolderEditExt");			
		} else if (pIn.getOption().equals("newdocworkhistoryagent")) { // 문서이력보기			
			mav.setViewName("/user/ZecmFileInfoExt");			
		} else if (pIn.getOption().equals("exportappr")) { // 승인			
			mav.setViewName("/user/ZecmReasonRegExt");			
		} else if (pIn.getOption().equals("SEARCH")) { // 검색		
			mav.setViewName("/user/ZecmSmartSearch");			
		} else {
			mav.setViewName("/user/ZecmReasonRegExt");
		}
		return mav;
	}

	
	@RequestMapping(value = "/doc/hook/EDMSSaveW.do", method = {RequestMethod.POST}, consumes = "multipart/form-data")
	public String  EDMSSaveW(@ModelAttribute @Valid ZappCentral pIn,  BindingResult bindingResult,  Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) {		
	//public String  EDMSSaveW(MultipartHttpServletRequest request, HttpServletResponse response, HttpSession session) {		
	//public String  EDMSSaveW(MultipartHttpServletRequest request) {		
		String savedFileName = "";	
		Random random = null;
		//HashMap<String,Object> result = new HashMap<String,Object>();
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		ZappFile zappFile = new ZappFile();
		String r_object_id = "";
		String classId = "";
		String mfileId = "";
		String option = ""; // newversion, overwrite
		String newDocYn = "";
		String szFilePath = "";
		String objVerOpt = ""; // H, L
		
		logger.debug("=== EDMSSaveW pIn.getUser_id: " + pIn.getUser_id());
		logger.debug("=== EDMSSaveW pIn.getUserId: " + pIn.getUserId());
		logger.debug("=== EDMSSaveW pIn.getPassword: " + pIn.getPassword());
		
		try {
			
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			if (pZappAuth == null) {
				String base64EncodeUserId = new String(Base64.getEncoder().encode(pIn.getUser_id().getBytes()));
				logger.error("=== Org UserId [" + pIn.getUser_id() + "], Base64Encode [" + base64EncodeUserId + "]");
				pIn.setUserId(base64EncodeUserId);
				//pIn.setPassword(pIn.getPassword());
				
				if (pIn.getUserId() != null && pIn.getUserId() != "") {
					// 로그인 처리
					loginpass(pIn, bindingResult, model, session);

					pZappAuth = getAuth(session);
					
				} else {
					logger.error("=== pIn.getUserId is NULL....");
				}
			}
			
			Enumeration e = request.getParameterNames();
			while (e.hasMoreElements()) {
				String pname = (String)e.nextElement();
				String pvalue = request.getParameter(pname);				
				logger.debug("=== Parameter pname: " + pname + ", pvalue:" + pvalue);
				if (pname.equals("r_object_id")) {
					r_object_id = pvalue;
				} else if (pname.equals("option")) {
					option = pvalue;
				} else if (pname.equals("new_doc_yn")) {
					newDocYn = pvalue;
				} else if (pname.equals("szFilePath")) {
					szFilePath = pvalue;
				}
			}

			if (option.equals("overwrite")) {
				if (newDocYn.equals("Y")) { //신규 등록
					classId = r_object_id;
				} else { // Major 버전업
					mfileId = r_object_id;
					objVerOpt = "H";
				}
			} else if (option.equals("newversion")) { // Minor 버전업
				mfileId = r_object_id;
				objVerOpt = "L";
			}
			
			if (classId.equals("")) {
				// mfileid로 classid 조회
				ZappClassObject classObjPar = new ZappClassObject();
				List<ZappClassObject> rZappClassObjList = null;

				classObjPar.setCobjid(mfileId);
				ZstFwResult pObjRes = contentMgtService.selectObject(pZappAuth, classObjPar, result);		
				rZappClassObjList = (List<ZappClassObject>) pObjRes.getResObj();
				if(rZappClassObjList == null) {
					logger.error("=== class not exist");
				}
				for(ZappClassObject vo : rZappClassObjList) {
					if (!vo.getClasstype().equals("03") && !vo.getClasstype().equals("02"))
						classId = vo.getClassid();
				}
				logger.debug("=== return classid : " + classId);
			}				
			
			logger.info("fileSend upTempPath : {} ",upTempPath);
			if (upTempPath.equals("")){
				logger.error("temp file Path not Exist ");
			} else {
				if (!ServletFileUpload.isMultipartContent(request)){
					logger.error("======= Is Not MultipartContent ");

				} else {					
					File uploadDir = new File(upTempPath);
					if(!uploadDir.exists()){
						uploadDir.mkdirs();
					}
					List<FileItem> multiparts;
					logger.info("fileSend request: ", request);
					multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
					logger.info("fileSend multiparts: ", multiparts);
					ArrayList<HashMap<String,String>> array = new ArrayList<HashMap<String,String>>();
					for(FileItem item : multiparts){
						logger.info("fileSend upTempPath1 : {} ",upTempPath);
						if(!item.isFormField()){
							logger.info("fileSend upTempPath2 : {} ",upTempPath);
							HashMap<String,String> filedata = new HashMap<String,String>();
							savedFileName = new File(item.getName()).getName();
							String ext = savedFileName.substring(savedFileName.lastIndexOf('.')+1);

							String savedFileFullPath  = upTempPath +File.separator+System.currentTimeMillis()+"_"+ savedFileName;
							File savedFile = new File(savedFileFullPath);
							if(savedFile.exists()){
								random = new Random();
								savedFileFullPath += random.nextInt(100);
							}
							item.write( new File(savedFileFullPath));
							filedata.put("objFileName", savedFileFullPath);
							filedata.put("objFileExt", ext);
							filedata.put("filename", savedFileName);//checkFormat
							filedata.put("checkFormat", "false");
							filedata.put("action", "ADD");
							array.add(filedata);		
							
							zappFile.setObjFileName(savedFileFullPath);
							zappFile.setObjFileExt(ext);
							zappFile.setFilename(savedFileName);
							zappFile.setCheckFormat(false);
							zappFile.setAction("ADD");
							
							zappFile.setFno(contentMgtService.getContentNo(pZappAuth)); // 문서번호
							
							// 상위/하위 버전업 여부
							if (objVerOpt.equals("H")) {
								zappFile.setIsreleased(true);
							} else if (objVerOpt.equals("L")) {
								zappFile.setIsreleased(false);								
							}
							
						}
					}// end for		
//					result.put("zappFiles", array);	
//					result.put("result", 0);
					
					// ECM40 API 호출 (addContent)
					result = callECM40_addContent (zappFile, classId, session);

					logger.debug("=== callECM40_addContent, result.getResCode(): " + result.getResCode());
					logger.debug("=== callECM40_addContent, result.getResMessage(): " + result.getResMessage());
					
					if (result.getResCode().equals("0000") && result.getResMessage().equals("Success") && !objVerOpt.equals("")) {
						ZappContentPar pZappContentPar = new ZappContentPar();
						pZappContentPar.setObjIsTest("N");
						pZappContentPar.setObjDebugged(false);
						pZappContentPar.setObjType("02");//00:분류,01:번들,02:파일
						pZappContentPar.setContentid(pIn.getR_object_id());
						pZappContentPar.setObjTaskid(gTaskId);
						pZappContentPar.setHasfile(false);
				
						result = contentMgtService.unlockContent(pZappAuth, pZappContentPar, result);
					}
				}
			}			

		} catch (FileUploadException e) {	
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("fileSend return 1 : {} ",upTempPath);

		return "OK";
	}
	
	private ZstFwResult callECM40_NPList(String HandleType, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		try {
			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			/* HandleType
			 * 1. 최근 등록
			 * 2. 본인 등록
			 * 3. 본인 소유
			 * 4. 최근 변경
			 * 5. 만료 도래
			 * 6. 만료
			 * */
			ZappContentPar pZappContentPar = new ZappContentPar();
			pZappContentPar.setObjTaskid(gTaskId);
			pZappContentPar.setObjHandleType(HandleType);
			pZappContentPar.setObjQueryType("F"); // A:All, F:File, B:Bundle
			pZappContentPar.setObjRes("LIST");
			pZappContentPar.setObjpgnum(0);
			//{"objIsTest":"Y","objDebugged":true,"objTaskid":"17B811791BF5E2A176D5A5A865ABE21CB31E829A51383A372342CE7E3999B9B0","objHandleType":"02","objRes":"LIST","objpgnum":1}

			// Skip get_classpath_upward_direct
			pZappAuth.setObjAccesspath("C");

			try {
				result = contentMgtService.selectNonPhysicalList(pZappAuth, null, pZappContentPar, result);
			} catch (ZappException e) {
				result = e.getZappResult();
			} catch (SQLException e) {
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private ZstFwResult callECM40_PList(String handleType, String classId, String fldType, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		try {
			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);

			// handleType
			 // 1. Classification - Folder, Classification system and Content type
			 // 2. Link - Linked content list
			 // 3. Share - Shared content list
			 // 4. Lock - Locked content list
			 // 5. Mark - Marked content list

			ZappContentPar pZappContentPar = new ZappContentPar();
			
			if (!classId.equals("")) {
				ZappClassification zappClass = new ZappClassification();
				zappClass.setClassid(classId);
				if (fldType != null && !fldType.equals(""))
					zappClass.setTypes(fldType);
				pZappContentPar.setZappClassification(zappClass);
			}
			pZappContentPar.setObjTaskid(gTaskId);
			pZappContentPar.setObjType(handleType);
			pZappContentPar.setObjQueryType("F"); // A:All, F:File, B:Bundle
			pZappContentPar.setObjRes("LIST");
			pZappContentPar.setObjIncLower("N");
			pZappContentPar.setObjpgnum(0); // 0: No Paging
			//{"objIsTest":"Y","objDebugged":true,"objTaskid":"17B811791BF5E2A176D5A5A865ABE21CB31E829A51383A372342CE7E3999B9B0","objHandleType":"02","objRes":"LIST","objpgnum":1}

			// Skip get_classpath_upward_direct
			pZappAuth.setObjAccesspath("C");

			logger.debug("=== callECM40_PList pZappAuth.getObjAccesspath:" + pZappAuth.getObjAccesspath());
			try {
				result = contentMgtService.selectPhysicalList(pZappAuth, null, pZappContentPar, result);
			} catch (ZappException e) {
				result = e.getZappResult();
			} catch (SQLException e) {
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private ZstFwResult callECM40_list(String HandleType, String upId, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		System.out.println("=== callECM40_list HandleType:" + HandleType + ", upId:" + upId);
		
		try {
//			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
						
			ZappClassification pZappClass = new ZappClassification();
			pZappClass.setCompanyid(pZappAuth.getSessUser().getCompanyid());
			pZappClass.setUpid(upId);
			pZappClass.setTypes(HandleType); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			pZappClass.setIsactive("Y");
			
			try {
				//result = contentMgtService.selectNonPhysicalList(pZappAuth, null, pZappContentPar, result);
				//result = classMgtService.selectObjectDown(pZappAuth, null, pZappClass, result);
				
				System.out.println("==== call classMgtService.selectObject");
				
				result = classMgtService.selectObject(pZappAuth, null, pZappClass, result); // 해당 하위만 나옴
				
			} catch (ZappException e) {
				logger.error("=== ZappException:" + e.toString());
				result = e.getZappResult();
			} catch (SQLException e) {
				logger.error("=== SQLException:" + e.toString());
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private ZstFwResult callECM40_listDown(String HandleType, String upId, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		System.out.println("=== callECM40_listDown HandleType:" + HandleType + ", upId:" + upId);
		
		try {
//			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			ZappClassification pZappClass = new ZappClassification();
			pZappClass.setCompanyid(pZappAuth.getSessUser().getCompanyid());
			pZappClass.setUpid(upId);
			pZappClass.setTypes(HandleType); //01:일반노드분류, N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류, 02:분류체계,03:문서유형
			pZappClass.setIsactive("Y");
			
			ZappClassificationPar zappClassPar = new ZappClassificationPar();
			zappClassPar.setCompanyid(pZappAuth.getSessUser().getCompanyid());
			zappClassPar.setUpid(upId);
			zappClassPar.setTypes(HandleType);
			zappClassPar.setIsactive("Y");
			
			try {
				//result = contentMgtService.selectNonPhysicalList(pZappAuth, null, pZappContentPar, result);
				//result = classMgtService.selectObjectDown(pZappAuth, null, pZappClass, result);
				
				System.out.println("==== call classMgtService.selectObjectDown");
				
				result = classMgtService.selectObjectDown(pZappAuth, null, zappClassPar, result); // 전체 하위가 다 나옴
				
				
			} catch (ZappException e) {
				logger.error("=== ZappException:" + e.toString());
				result = e.getZappResult();
			} catch (SQLException e) {
				logger.error("=== SQLException:" + e.toString());
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private ZstFwResult callECM40_groupList(String HandleType, String upId, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		System.out.println("=== callECM40_groupList HandleType:" + HandleType + ", upId:" + upId);
		
		try {
//			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth = getAuth(session);
			
			ZappClassificationPar pZappClass = new ZappClassificationPar();
			pZappClass.setCompanyid(pZappAuth.getSessUser().getCompanyid());
			pZappClass.setTypes(HandleType); //04:협업그룹
			pZappClass.setIsactive("Y");
			
			try {
				System.out.println("==== call classMgtService.selectOrganDown");
				// 협업그룹 목록 조회 (N4)
				result = classMgtService.selectOrganDown(pZappAuth, null, pZappClass, result);

			} catch (ZappException e) {
				logger.error("=== ZappException:" + e.toString());
				result = e.getZappResult();
			} catch (SQLException e) {
				logger.error("=== SQLException:" + e.toString());
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private ZstFwResult callECM40_addContent(ZappFile zappFile, String classId, HttpSession session) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		logger.info("=============================================");
		logger.info("=== callECM40_addContent");
		try {
			/* Test */
			ZappAuth pZappAuth = new ZappAuth();
			ZstFwResult pObjRes = new ZstFwResult(); 
			pObjRes.setResCode(SUCCESS);
			
			pZappAuth = getAuth(session);
			logger.debug("=== pZappAuth.getObjDeptid(): " + pZappAuth.getObjDeptid());
						
			logger.debug("=== deptuserName: " + pZappAuth.getSessDeptUser().getZappUser().getName());
			logger.debug("=== userName: " + pZappAuth.getSessUser().getName());
			logger.debug("=== classId: " + classId);
			
			ZappContentPar pContentPar = new ZappContentPar();

			
			ZappClassification zappClass = new ZappClassification();
			zappClass.setClassid(classId);
			result = classMgtService.selectObject(pZappAuth, zappClass, result);
			
			logger.debug("=== callECM40_addContent query class, result.getMessage(): " + result.getMessage());
			logger.debug("=== callECM40_addContent query class, result.getResCode(): " + result.getResCode());

			zappClass = (ZappClassification)result.getResObj();
			logger.debug("=== class type: " + zappClass.getTypes());
			String classType = zappClass.getTypes();
			
			List<ZappClassObject> classList = new ArrayList<ZappClassObject>();
			ZappClassObject zappClassObject = new ZappClassObject();
			zappClassObject.setClassid(classId);
			zappClassObject.setClasstype(classType);
			classList.add(zappClassObject);

			pContentPar.setObjTaskid(gTaskId);
			String handleType = "02"; // bundle : 01, file : 02
			pContentPar.setObjType(handleType);
			pContentPar.setObjRetention("C718B4AB1C9C824375D0C7464E2CEEB5CE9DBF0A2B58DDC2273B832AFF5625A3"); // 영구
			pContentPar.setZappFile(zappFile);
			pContentPar.setZappClassObjects(classList);
			
			logger.debug("=== class Size: " + pContentPar.getZappClassObjects().size());
			
			try {
				logger.debug("=== bef fwResult.getResCode(): " + result.getResCode());
				
				// 컨텐츠 등록 API 호출
				result = contentMgtService.addContent(pZappAuth, pContentPar, result);
				
				logger.debug("=== result.getError(): " + result.getError());
				logger.debug("=== result.getMessage(): " + result.getMessage());
				logger.debug("=== result.getResCode(): " + result.getResCode());
				logger.debug("=== result.getResMessage(): " + result.getResMessage());
				logger.debug("=== result.getResObj(): " + result.getResObj());
				
				
				//jwjang moved from addContentNoFile
				ZappEnv SYS_APPROVAL_ADD_YN = new ZappEnv(); SYS_APPROVAL_ADD_YN.setSetval(NO);
				if(SYS_APPROVAL_ADD_YN.getSetval().equals(ZappConts.USAGES.YES.use)) {
					//if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_BUNDLE.type)) {
					//	pObjRes = workflowService.commenceWorkflow(pObjAuth, pObjContent.getZappBundle(), rZappClassification, pObjRes);
					//}
					//if(pObjContent.getObjType().equals(ZappConts.TYPES.CONTENT_FILE.type)) {
						pObjRes = workflowService.commenceWorkflow(pZappAuth, zappFile, zappClass, pObjRes);
					//}
					//if(ZappFinalizing.isSuccess(pObjRes) == false) {
					//	return ZappFinalizing.finalising("ERR_C_APPROVAL", "[addContent] " + messageService.getMessage("ERR_C_APPROVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
					//}
				}

			} catch (ZappException e) {
				logger.error("=== ZappException:" + e.toString());
				result = e.getZappResult();
			} catch (SQLException e) {
				logger.error("=== SQLException:" + e.toString());
				if(null != e.getCause()) {
					result.setResCode(e.getCause().toString());
				}else {
					result.setResCode("ERROR");
				}
				result.setMessage(e.getMessage());	
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// ClassID를 이용한 ClassType 조회
	private String getClassType(ZappAuth pZappAuth, String classId) {
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);
		ZappClassification classification = new ZappClassification();
		ZappClassificationPar pClassPar = new ZappClassificationPar();
		
		try {
			logger.debug("=== getClassType === classId: " + classId);
			pClassPar.setClassid(classId);
			result = classMgtService.selectClass(pZappAuth, pClassPar, result);
			ZappClassificationRes classRes = new ZappClassificationRes();
			classRes = (ZappClassificationRes)result.getResObj();
			classification = classRes.getZappClassification();
			logger.debug("=== getClassType === className: " + classification.getName() + ", classType: " + classification.getTypes());
		} catch (ZappException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return classification.getTypes();
	}
	
	private String getApproveType(String state) {
		// 00:정상, 01:삭제대기, 03:잠김, 
		// A0:수정요청, A1:삭제요청, A2:복구요청, A3:이동요청, A4:복사요청, A5:잠금요청, A6:이동요청, 
		// B1:등록요청, B2:파기요청, B3:이관요청, 
		// C0:수정반려, C1:삭제반려, C2:복구반려, C4:이동반려, C5:복사반려, C6:반출반려, 
		// D1:등록반려, D2:파기반려
		String apprDiv = "";
		
		if (state.equals("A0")) { //수정요청
			apprDiv = "수정";
		} else if (state.equals("A1")) {
			apprDiv = "삭제";
		} else if (state.equals("A2")) {
			apprDiv = "복구";
		} else if (state.equals("A3")) {
			apprDiv = "이동";
		} else if (state.equals("A4")) {
			apprDiv = "복사";
		} else if (state.equals("A5")) {
			apprDiv = "반출";
		} else if (state.equals("A6")) {
			apprDiv = "이동";
		} else if (state.equals("B1")) {
			apprDiv = "등록";
		} else if (state.equals("B2")) {
			apprDiv = "폐기";
		} else if (state.equals("B3")) {
			apprDiv = "이관";
		} else if (state.equals("C0")) {
			apprDiv = "수정반려";
		} else if (state.equals("C1")) {
			apprDiv = "삭제반려";
		} else if (state.equals("C2")) {
			apprDiv = "복구반려";
		} else if (state.equals("C4")) {
			apprDiv = "이동반려";
		} else if (state.equals("C5")) {
			apprDiv = "복사반려";
		} else if (state.equals("C6")) {
			apprDiv = "반출반려";
		} else if (state.equals("D1")) {
			apprDiv = "등록반려";
		} else if (state.equals("D2")) {
			apprDiv = "파기반려";
		}
		return apprDiv;
	}
	
	private String getTaskId(HttpSession session) {
		if (gTaskId.length() > 0)
			return gTaskId;

		ZappAuth pZappAuth = new ZappAuth();
		pZappAuth = getAuth(session);
		if (pZappAuth != null) {
			List<ZArchTask> taskList = pZappAuth.getSessTasks();
			logger.debug("== taskList size:" + taskList.size());
			for (ZArchTask archTask : taskList) {
				logger.debug("=== taskcode:" + archTask.getCode() + ", taskId:" + archTask.getTaskid());
				if (archTask.getCode() != null && archTask.getCode().equals("EDMS")) {
					gTaskId = archTask.getTaskid();
					logger.debug("=== gTaskId:" + gTaskId);
					break;
				}
			}
		}
		
		return gTaskId;
	}
}

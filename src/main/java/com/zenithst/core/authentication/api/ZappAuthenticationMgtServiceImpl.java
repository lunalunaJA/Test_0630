package com.zenithst.core.authentication.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.vo.ZArchTask;
import com.zenithst.core.authentication.service.ZappAuthenticationService;
import com.zenithst.core.authentication.service.ZappJWTService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.debug.ZappDebug;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.test.ZappAuthTest;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappGroupUserExtend;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappOrganTaskExtend;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.api.ZappSystemMgtService;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwEncodeUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;
import com.zenithst.license.api.ZenithLicense;
import com.zenithst.license.api.ZenithLicense.UsagePurpose;
import com.zenithst.license.exception.ZenithLicenseException;
import com.zenithst.license.vo.License;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage authentication info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    '@Autowired
*	 private ZappAuthenticationMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappAuthenticationMgtService")
public class ZappAuthenticationMgtServiceImpl extends ZappService implements ZappAuthenticationMgtService {

	/*
		[Service]
	*/

	// Authentication
	@Autowired
	private ZappAuthenticationService authenticationService;
	
	// JWT
	@Autowired
	private ZappJWTService jwtService;
	
	// Log
	@Autowired
	private ZappLogMgtService logService;
	
	// Organization
	@Autowired
	private ZappOrganMgtService organService;
	
	// System
	@Autowired
	private ZappSystemMgtService systemService;
	
	// Message
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* License path */
	@Value("#{archiveconfig['LIC_PATH']}")
	private String LIC_PATH;	

	@SuppressWarnings("unchecked")
	public ZstFwResult connect_through_web(ZappAuth pObjAuth, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time
		
		/* Validation */
		if(pObjAuth == null)
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[connect_through_web] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		if(pObjRes == null) {
			pObjRes = new ZstFwResult(); pObjRes.setResCode(SUCCESS);
		}
		
		/* Debugging */
		ZappDebug.debug(logger, pObjAuth, null);
		
		/* Value Check */
		pObjRes = validation(ZappConts.ACTION.CONNECT_WEB, pObjAuth, pObjRes);
		
		/* [Check License]
		 * 
		 */
		License pLicense = new License();
		pLicense.setFilepath(LIC_PATH);
		UsagePurpose CHECK_USAGE;
		try {
			CHECK_USAGE = ZenithLicense.verifyUsagePurpose(pLicense);
		} catch (Exception e) {
			return ZappFinalizing.finalising("ERR_R_LIC", "[connect_through_web] " + messageService.getMessage("ERR_R_LIC",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(CHECK_USAGE == null) {
			return ZappFinalizing.finalising("ERR_R_LIC", "[connect_through_web] " + messageService.getMessage("ERR_R_LIC",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(CHECK_USAGE.equals(UsagePurpose.DEVELOPMENT)) {
			pLicense.setCheckMac(false);
		} else {
			pLicense.setCheckMac(true);
		}
    	try {
    		ZenithLicense.checkLic(pLicense);
    	} catch(Exception e) {
			return ZappFinalizing.finalising("ERR_R_LIC", "[connect_through_web] " + e.getMessage(), pObjAuth.getObjlang());
    	}
    	pSession.setAttribute("validLic", true);
		
		/* [User]
		 * Inquire and check the current user information including the company and dept. 
		 */
		pObjRes = checkUser(pObjAuth, pObjRes);
		ZappAuth rZappAuth = (ZappAuth) pObjRes.getResObj();
		pObjAuth.setObjCompanyid(rZappAuth.getObjCompanyid());
		BeanUtils.copyProperties(pObjAuth, rZappAuth);
		
		/* [Task]
		 * Set task info.
		 */
		ZappOrganTask pZappOrganTask = new ZappOrganTask(pObjAuth.getObjCompanyid(), BLANK, pObjAuth.getObjTaskid(), ZappConts.TYPES.GROUPTYPE_COMPANY.type);
		pZappOrganTask.setOrgantaskid(ZappKey.getPk(pZappOrganTask));
		pSession.setAttribute("sessTask", rZappAuth.getSessTasks());	// Task Info.
		
		/* [Language]
		 * If there is no specified language, English is applied as default language.
		 */
		if(!ZstFwValidatorUtils.valid(rZappAuth.getObjlang())) {
			rZappAuth.setObjlang(ZappConts.LANGS.KOREAN.lang);	
//			rZappAuth.setObjlang(ZappConts.LANGS.ENGLISH.lang);	
		}
		
		/* [Preferences] 
		 * Inquire preferences and store it in the authentication info.
		 */
		pObjRes = getEnv(pObjAuth, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_ENV", "[connect_through_web] " + messageService.getMessage("ERR_R_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		rZappAuth.setSessEnv(((Map<String, ZappEnv>) pObjRes.getResObj()) == null ? new HashMap<String, ZappEnv>() : (Map<String, ZappEnv>) pObjRes.getResObj());

		/* [Personal Preferences]
		 * 
		 */
//		pObjRes = getUserEnv(pObjAuth, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_R_ENV", "[connect_through_web] " + messageService.getMessage("ERR_R_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		} 
//		rZappAuth.setSessUserEnv(((Map<String, ZappEnv>) pObjRes.getResObj()) == null ? new HashMap<String, ZappEnv>() : (Map<String, ZappEnv>) pObjRes.getResObj());
		
		/* [Session] 
		 * By default, the session duration is set by preference. If not, it is set to one hour. 
		 */
		if(rZappAuth.getSessEnv().containsKey("SYS_SESSION_TIME")) {
			pSession.setMaxInactiveInterval(ZstFwValidatorUtils.fixNullInt(rZappAuth.getSessEnv().get("SYS_SESSION_TIME").getSetval(), 3600));		// 세션 유지 시간
		} else {
			pSession.setMaxInactiveInterval(3600);																									// 세션 유지 시간
		}
		pSession.setAttribute("Authentication", rZappAuth);  			// Storing authentication information in session
		
		/* [Logging]
		 * 
		 */
		ZappAccessLog pZappAccessLog = new ZappAccessLog();
		pZappAccessLog.setLogobjid(rZappAuth.getSessDeptUser().getDeptuserid());
		pZappAccessLog.setLogtype(ZappConts.TYPES.LOG_ATHENTICATION.type);					// Logging type
		Map<String, Object> mapExtra = new HashMap<String, Object>();
		mapExtra.put(ZappConts.LOGS.ITEM_CLIENT.log, new ArrayList<String>(Arrays.asList(pRequest.getRemoteAddr())));
		pObjRes = logService.getLogs(pObjAuth, null, pObjRes);
		pZappAccessLog.setLogs(pObjRes != null ? (String) pObjRes.getResObj() : BLANK);		// Logging content
		pZappAccessLog.setAction(ZappConts.LOGS.ACTION_CONNECT.log);						// Processing type
		pZappAccessLog.setLogtime(PROCTIME);												// Logging time
		pZappAccessLog.setLogip(pRequest.getRemoteAddr());							    	// Ip address
		pObjRes = logService.leaveLog(pObjAuth, pZappAccessLog, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_LOG", "[connect_through_web] " + messageService.getMessage("ERR_C_LOG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		// JWT
//		rZappAuth.setObjJwt(jwtService.createJWT_Simple(rZappAuth, pRequest));
		
		// # 리턴 정보
		pObjRes.setResObj(rZappAuth.getSessDeptUser());
		
		return pObjRes;
	}

	public ZstFwResult disconnect_through_web(ZappAuth pObjAuth, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		/* Null Check */
		if(pObjAuth == null)
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[disconnect_through_web] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		if(pObjRes == null) pObjRes = new ZstFwResult();
		
		/* Value Check */
		pObjRes = validation(ZappConts.ACTION.DISCONNECT_WEB, pObjAuth, pObjRes);

		/* 로그 기록
		 * 접속자 정보, 클라이언트 정보를 저장한다.
		 */
		ZappAccessLog pZappAccessLog = new ZappAccessLog();
		pZappAccessLog.setAction(ZappConts.LOGS.ACTION_DISCONNECT.log);		// 접속 종료
		pZappAccessLog.setLogtype(ZappConts.LOGS.TYPE_AUTHENTICATION.log);	// Authentication
		Map<String, Object> mapExtra = new HashMap<String, Object>();
		mapExtra.put(ZappConts.LOGS.ITEM_CLIENT.log, new ArrayList<String>(Arrays.asList(pRequest.getRemoteAddr())));
		pObjRes = logService.getLogs(pObjAuth, null, pObjRes);
		pZappAccessLog.setLogs(pObjRes != null ? (String) pObjRes.getResObj() : BLANK);
		pZappAccessLog.setLogip(pRequest.getRemoteAddr());							    	// Ip address
		pObjRes = logService.leaveLog(pObjAuth, pZappAccessLog, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_LOG", "[disconnect_through_web] " + messageService.getMessage("ERR_C_LOG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		pObjRes.setResObj(BLANK);
		
		/* 세션 초기화
		 * 
		 */
		pSession.invalidate();
		
		return pObjRes;
		
	}
	
	@SuppressWarnings("unchecked")
	public ZstFwResult connect_through_cs(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		/* Null Check */
		if(pObjAuth == null)
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[connect_through_cs] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		if(pObjRes == null) pObjRes = new ZstFwResult();
		
		/* Value Check */
		pObjRes = validation(ZappConts.ACTION.CONNECT_CS, pObjAuth, pObjRes);
		
		/* User 체크 및 접속자 정보 저장  
		 * Company, Department, User 관련 정보를 조회하여 저장한다.
		 */
		pObjRes = checkUser(pObjAuth, pObjRes);
		ZappAuth rZappAuth = (ZappAuth) pObjRes.getResObj();
		BeanUtils.copyProperties(pObjAuth, rZappAuth);
		
		/* 언어 지정
		 * 
		 */
		if(!ZstFwValidatorUtils.valid(rZappAuth.getObjlang())) {
			rZappAuth.setObjlang(ZappConts.LANGS.KOREAN.lang);	
		}
		
		/* Preferences 
		 * Preferences 정보를 저장한다.
		 */
		pObjRes = getEnv(pObjAuth, pObjRes);
		rZappAuth.setSessEnv((Map<String, ZappEnv>) pObjRes.getResObj());
		
		/* JSON Web Token 생성
		 * 
		 */
		String JWT = jwtService.createJWT_Obj(rZappAuth, pRequest);
		if(!ZstFwValidatorUtils.valid(JWT)) {
			return ZappFinalizing.finalising("ERR_JWT", "[connect_through_cs] " + messageService.getMessage("ERR_JWT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* 로그 기록
		 * 
		 */
		ZappAccessLog pZappAccessLog = new ZappAccessLog();
		pZappAccessLog.setLogtype(ZappConts.LOGS.ACTION_CONNECT.log);		// 접속
		Map<String, Object> mapExtra = new HashMap<String, Object>();
		mapExtra.put(ZappConts.LOGS.ITEM_CLIENT.log, new ArrayList<String>(Arrays.asList(pRequest.getRemoteAddr())));
		pObjRes = logService.getLogs(pObjAuth, null, pObjRes);
		pZappAccessLog.setLogs(pObjRes != null ? (String) pObjRes.getResObj() : BLANK);
		pObjRes = logService.leaveLog(pObjAuth, pZappAccessLog, pObjRes);
		
		if(ZappFinalizing.isSuccess(pObjRes)) {
			pObjRes.setResObj(JWT);
		} else {
			return ZappFinalizing.finalising("ERR_CONNECT", "[disconnect_through_cs] " + messageService.getMessage("ERR_CONNECT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}

	public ZstFwResult disconnect_through_cs(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		/* Null Check */
		if(pObjAuth == null)
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[disconnect_through_cs] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		if(pObjRes == null) pObjRes = new ZstFwResult();
		
		/* Value Check */
		pObjRes = validation(ZappConts.ACTION.DISCONNECT_CS, pObjAuth, pObjRes);
		
		/* JSON Web Token 체크
		 * 
		 */
		if(jwtService.checkJWT(pObjAuth, pRequest) == false) {
			return ZappFinalizing.finalising("ERR_JWT", "[disconnect_through_cs] " + messageService.getMessage("ERR_JWT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* 로그 기록
		 * 
		 */
		ZappAccessLog pZappAccessLog = new ZappAccessLog();
		pZappAccessLog.setLogtype(ZappConts.LOGS.ACTION_DISCONNECT.log);		// 접속 종료
		Map<String, Object> mapExtra = new HashMap<String, Object>();
		mapExtra.put(ZappConts.LOGS.ITEM_CLIENT.log, new ArrayList<String>(Arrays.asList(pRequest.getRemoteAddr())));
		pObjRes = logService.getLogs(pObjAuth, null, pObjRes);
		pZappAccessLog.setLogs(pObjRes != null ? (String) pObjRes.getResObj() : BLANK);
		pObjRes = logService.leaveLog(pObjAuth, pZappAccessLog, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes)) {
			return ZappFinalizing.finalising("ERR_C_LOG", "[disconnect_through_cs] " + messageService.getMessage("ERR_C_LOG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
		
	}
	
	/**
	 * Checks if the user belongs to multiple departments. 
	 * If it belong to one more departments, returns a list of departments.
	 * @param pObjAuth
	 * @param pRequest
	 * @param pObjRes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult checkMultiDepts(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		/* Null Check */
		if(pObjAuth == null)
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[checkMultiDepts] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		if(pObjRes == null) pObjRes = new ZstFwResult();
		
		/* Value Check */
		pObjRes = validation(ZappConts.ACTION.CHECK_MULTI_DEPTS, pObjAuth, pObjRes);

		/* User  */
		ZappUser rZappUser = null;
		ZappUser pZappUser = new ZappUser(pObjAuth.getObjCompanyid(), pObjAuth.getObjEmpno(), pObjAuth.getObjLoginid(), null);
		pObjRes = organService.selectObject(pObjAuth, pZappUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_USER", "[checkMultiDepts] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappUser> rZappUserList = (List<ZappUser>) pObjRes.getResObj();
		if(rZappUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_USER", "[checkMultiDepts] " + messageService.getMessage("ERR_NEXIST_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappUser vo : rZappUserList) {
			rZappUser = vo;
		}
		if(rZappUser == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_USER", "[checkMultiDepts] " + messageService.getMessage("ERR_NEXIST_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Department User 정보 */
		List<ZappDeptUserExtend> rZappDeptUserList = null;
		ZappDeptUser pZappDeptUser = new ZappDeptUser(null, rZappUser.getUserid());
		pZappDeptUser.setIsactive(YES);
		pObjRes = organService.selectObjectExtend(pObjAuth, pZappDeptUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[checkMultiDepts] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		 rZappDeptUserList = (List<ZappDeptUserExtend>) pObjRes.getResObj();
		if(rZappDeptUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_DEPTUSER", "[checkMultiDepts] " + messageService.getMessage("ERR_NEXIST_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(rZappDeptUserList.size() == ZERO) {
			return ZappFinalizing.finalising("ERR_NEXIST_DEPTUSER", "[checkMultiDepts] " + messageService.getMessage("ERR_NEXIST_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		pObjRes.setResObj(rZappDeptUserList);
		
		return pObjRes;
	}	
	
	/**
	 * 
	 */
	public ZstFwResult getAuth_Test(Object pInObj, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pResult) {
		
		ZappAuth pZappAuth = ZappAuthTest.getAuth(pInObj);

		try {
			pResult = connect_through_web(pZappAuth, pSession, pRequest, pResult);
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
	
	public ZstFwResult connect_to_otherjob_web(ZappAuth pObjAuth, HttpSession pSession, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time

		/* [Validation]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjAuth.getObjDeptid()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[connect_to_otherjob] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Inquiry current user info. from session]
		 * 
		 */
		ZappAuth rZappAuth_Current = new ZappAuth();
		rZappAuth_Current = (ZappAuth) pSession.getAttribute("Authentication");
		if(rZappAuth_Current == null) {
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[connect_to_otherjob] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* [Checking User]
		 * 
		 */
//		if(rZappAuth_Current.getSessDeptUser().getDeptuserid().equals(pObjAuth.getSessDeptUser().getDeptuserid())) {
//			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[connect_to_otherjob] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* [Inquiry Target User Info.]
		 * 
		 */ 
//		ZappDeptUser pZappDeptUser = new ZappDeptUser();
//		pZappDeptUser.setUserid(rZappAuth_Current.getSessDeptUser().getUserid());	// User ID
//		pZappDeptUser.setDeptid(pObjAuth.getObjDeptid());							// To be changed dept. ID	
//		ZappDeptUserExtend rZappDeptUser_Target = null;
//		pObjRes = organService.selectObjectExtend(pObjAuth, pZappDeptUser, pObjRes);
//		if(ZappFinalizing.isSuccess(pObjRes) == false) {
//			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[connect_to_otherjob] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		@SuppressWarnings("unchecked")
//		List<ZappDeptUserExtend> rZappDeptUserList_Target = (List<ZappDeptUserExtend>)  pObjRes.getResObj();
//		if(rZappDeptUserList_Target == null) {
//			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[connect_to_otherjob] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		for(ZappDeptUserExtend vo : rZappDeptUserList_Target) {
//			rZappDeptUser_Target = vo;
//		}
//		if(rZappDeptUser_Target == null) {
//			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[connect_to_otherjob] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		/* Changing Session Info.
		 * 
		 */
		for(ZappDeptUserExtend vo : rZappAuth_Current.getSessDeptUsers()) {
			if(vo.getDeptid().equals(pObjAuth.getObjDeptid())) {
				rZappAuth_Current.setSessDeptUser(vo);
				ZappDeptUser ivo = new ZappDeptUser();
				BeanUtils.copyProperties(vo, ivo);
				rZappAuth_Current.setSessOnlyDeptUser(ivo);
				// Acl String
				String[] tmpAcl = {rZappAuth_Current.getSessDeptUser().getDeptuserid(), rZappAuth_Current.getSessDeptUser().getDeptid()};
				rZappAuth_Current.setSessAclObjList(Arrays.asList(tmpAcl));
				break;
			}
		}		
		
		pSession.setAttribute("Authentication", rZappAuth_Current);
		
		/* [Logging]
		 * 
		 */
		ZappAccessLog pZappAccessLog = new ZappAccessLog();
		pZappAccessLog.setLogobjid(rZappAuth_Current.getSessDeptUser().getDeptuserid());
		pZappAccessLog.setLogtype(ZappConts.TYPES.LOG_ATHENTICATION.type);					// Logging type
		Map<String, Object> mapExtra = new HashMap<String, Object>();
		mapExtra.put(ZappConts.LOGS.ITEM_CLIENT.log, new ArrayList<String>(Arrays.asList(pRequest.getRemoteAddr())));
		pObjRes = logService.getLogs(rZappAuth_Current, null, pObjRes);
		pZappAccessLog.setLogs(pObjRes != null ? (String) pObjRes.getResObj() : BLANK);		// Logging content
		pZappAccessLog.setAction(ZappConts.LOGS.ACTION_CONNECT.log);						// Processing type
		pZappAccessLog.setLogtime(PROCTIME);												// Logging time
		pObjRes = logService.leaveLog(rZappAuth_Current, pZappAccessLog, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_LOG", "[connect_through_web] " + messageService.getMessage("ERR_C_LOG",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		
		return pObjRes;
	}

	public ZstFwResult connect_to_otherjob_cs(ZappAuth pObjAuth, HttpServletRequest pRequest, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Variable */
		String PROCTIME = ZstFwDateUtils.getNow();					// Processing time

		/* [Validation]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(pObjAuth.getObjDeptid()) == false) {
			return ZappFinalizing.finalising("ERR_MIS_AUTH", "[connect_to_otherjob] " + messageService.getMessage("ERR_MIS_AUTH",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* JSON Web Token 생성
		 * 
		 */
		String JWT = jwtService.createJWT(pObjAuth, pRequest);
		if(!ZstFwValidatorUtils.valid(JWT)) {
			return ZappFinalizing.finalising("ERR_JWT", "[connect_through_cs] " + messageService.getMessage("ERR_JWT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}

	
	/**
	 * Validation
	 * @param pAct
	 * @param pObjAuth
	 * @param pObjRes
	 * @return
	 */
	private ZstFwResult validation(ZappConts.ACTION pAct, ZappAuth pObjAuth, ZstFwResult pObjRes) {

		/* Company ID */
		if(!ZstFwValidatorUtils.valid(pObjAuth.getObjCompanyid())) {
			return ZappFinalizing.finalising("ERR_MIS_COMPANYID", "[connect_through_web] " + messageService.getMessage("ERR_MIS_COMPANYID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		/* Login ID */
		if(!ZstFwValidatorUtils.valid(pObjAuth.getObjLoginid()) && !ZstFwValidatorUtils.valid(pObjAuth.getObjEmpno())) {
			return ZappFinalizing.finalising("ERR_MIS_LOGINID", "[connect_through_web] " + messageService.getMessage("ERR_MIS_LOGINID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		switch(pAct) {
			case CONNECT_WEB:
				/* Dept. ID */
//				if(!ZstFwValidatorUtils.valid(pObjAuth.getObjDeptid())) {
//					return ZappFinalizing.finalising("ERR_MIS_DEPTID", "[connect_through_web] " + messageService.getMessage("ERR_MIS_DEPTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
				/* Password */
				if(!ZstFwValidatorUtils.valid(pObjAuth.getObjPasswd())) {
					return ZappFinalizing.finalising("ERR_MIS_PWD", "[connect_through_web] " + messageService.getMessage("ERR_MIS_PWD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CONNECT_CS:
				/* Dept. ID */
//				if(!ZstFwValidatorUtils.valid(pObjAuth.getObjDeptid())) {
//					return ZappFinalizing.finalising("ERR_MIS_DEPTID", "[connect_through_web] " + messageService.getMessage("ERR_MIS_DEPTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
				/* Password */
				if(!ZstFwValidatorUtils.valid(pObjAuth.getObjPasswd())) {
					return ZappFinalizing.finalising("ERR_MIS_DEPTID", "[connect_through_web] " + messageService.getMessage("ERR_MIS_DEPTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}			
			break;
			case DISCONNECT_WEB:
			break;
			case DISCONNECT_CS:
				/* JWT */
				if(!ZstFwValidatorUtils.valid(pObjAuth.getObjJwt())) {
					return ZappFinalizing.finalising("ERR_MIS_JWT", "[connect_through_web] " + messageService.getMessage("ERR_MIS_JWT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHECK_CONNECT_WEB:

			break;
			case CHECK_CONNECT_CS:
				/* JWT */
				if(!ZstFwValidatorUtils.valid(pObjAuth.getObjJwt())) {
					return ZappFinalizing.finalising("ERR_MIS_JWT", "[connect_through_web] " + messageService.getMessage("ERR_MIS_JWT",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHECK_MULTI_DEPTS:
			break;
			default:
		}
		
		return pObjRes;
	}

	
	@SuppressWarnings("unchecked")
	private ZstFwResult checkUser(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		List<String> rSessAclObjList = new ArrayList<String>();
		
		/* Company */
//		pObjRes = organService.selectObject(pObjAuth, new ZappCompany(pObjAuth.getObjCompanyid()), pObjRes);
		ZappCompany pZappCompany = new ZappCompany();
		pZappCompany.setCode(pObjAuth.getObjCompanyid());
		pObjRes = organService.selectObject(pObjAuth, pZappCompany, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_COMPANY", "[checkUser] " + messageService.getMessage("ERR_R_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(pObjRes.getResObj() != null) {
			List<ZappCompany> tmpList = (List<ZappCompany>) pObjRes.getResObj();
			if(tmpList == null) {
				return ZappFinalizing.finalising("ERR_R_COMPANY", "[checkUser] " + messageService.getMessage("ERR_R_COMPANY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappCompany vo : tmpList) {
				pObjAuth.setSessCompany(vo);	
			}
		}
		pObjAuth.setObjCompanyid(pObjAuth.getSessCompany().getCompanyid());
		
		/* Task */
		List<ZArchTask> rZArchTaskList = new ArrayList<ZArchTask>();
		ZappOrganTask pZappOrganTask = new ZappOrganTask();
		pZappOrganTask.setCompanyid(pObjAuth.getSessCompany().getCompanyid());
		pZappOrganTask.setTobjtype(ZappConts.TYPES.TASK_COMPANY.type);
		pObjRes = organService.selectObjectExtend(pObjAuth, pZappOrganTask, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_ORGANTASK", "[checkUser] " + messageService.getMessage("ERR_R_ORGANTASK",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappOrganTaskExtend> rZappOrganTaskList = (List<ZappOrganTaskExtend>) pObjRes.getResObj();
		if(rZappOrganTaskList != null) {
			for(ZappOrganTaskExtend vo : rZappOrganTaskList) {
				rZArchTaskList.add(vo.getZappTask());
			}
		}
		pObjAuth.setSessTasks(rZArchTaskList);
		
		/* User */
		ZappUser rZappUser = null;
		ZappUser pZappUser = new ZappUser(pObjAuth.getObjCompanyid(), pObjAuth.getObjEmpno(), pObjAuth.getObjLoginid(), null);
		pZappUser.setIsactive(YES);
		pObjRes = organService.selectObject(pObjAuth, pZappUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_USER", "[checkUser] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappUser> rZappUserList = (List<ZappUser>) pObjRes.getResObj();
		if(rZappUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_USER", "[checkUser] " + messageService.getMessage("ERR_NEXIST_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappUser vo : rZappUserList) {
			rZappUser = vo;
			pObjAuth.setSessUser(vo);
		}
		if(rZappUser == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_USER", "[checkUser] " + messageService.getMessage("ERR_NEXIST_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Original Dept. */
		ZappDeptUser pZappDeptUser_Check = new ZappDeptUser();
		pZappDeptUser_Check.setUserid(rZappUser.getUserid());
		pZappDeptUser_Check.setOriginyn(YES);
		pZappDeptUser_Check.setIsactive(YES);
		pObjRes = organService.selectObject(pObjAuth, pZappDeptUser_Check, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[checkUser] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		List<ZappDeptUser> rZappDeptUserList_Check = (List<ZappDeptUser>) pObjRes.getResObj(); 
		if(rZappDeptUserList_Check != null) {
			for(ZappDeptUser vo : rZappDeptUserList_Check) {
				pObjAuth.setObjDeptid(vo.getDeptid()); // Original Job
			}
		} else {
			return ZappFinalizing.finalising("ERR_MIS_DEPTID", "[checkUser] " + messageService.getMessage("ERR_MIS_DEPTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Password */
		if(!rZappUser.getPasswd().equals(ZstFwEncodeUtils.encodeString_SHA256(pObjAuth.getObjPasswd() + rZappUser.getPasswdsalt()))) {
			return ZappFinalizing.finalising("ERR_NIDENT_PWD", "[checkUser] " + messageService.getMessage("ERR_NIDENT_PWD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* Checking Mac or Ip address */
		if(ZstFwValidatorUtils.valid(rZappUser.getMaclimit()) == true) {
			if(rZappUser.getMaclimit().contains(pObjAuth.getObjMac()) == false) {
				return ZappFinalizing.finalising("ERR_NIDENT_MAC", "[checkUser] " + messageService.getMessage("ERR_NIDENT_MAC",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		if(ZstFwValidatorUtils.valid(rZappUser.getIplimit()) == true) {
			if(rZappUser.getIplimit().contains(pObjAuth.getObjIp()) == false) {
				return ZappFinalizing.finalising("ERR_NIDENT_IP", "[checkUser] " + messageService.getMessage("ERR_NIDENT_IP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
		}
		
		/* Department User */
		List<ZappDeptUserExtend> rZappDeptUserList = null;
		List<ZappDeptUser> rZappOnlyDeptUserList = new ArrayList<ZappDeptUser>();
		ZappDeptUser pZappDeptUser = new ZappDeptUser(null, rZappUser.getUserid());
		pZappDeptUser.setIsactive(YES);
		pObjRes = organService.selectObjectExtend(pObjAuth, pZappDeptUser, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_DEPTUSER", "[checkUser] " + messageService.getMessage("ERR_R_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		rZappDeptUserList = (List<ZappDeptUserExtend>) pObjRes.getResObj();
		if(rZappDeptUserList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_DEPTUSER", "[checkUser] " + messageService.getMessage("ERR_NEXIST_DEPTUSER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappDeptUserExtend vo : rZappDeptUserList) {
			if(vo.getDeptid().equals(pObjAuth.getObjDeptid())) {
				pObjAuth.setSessDeptUser(vo);
				ZappDeptUser ivo = new ZappDeptUser();
				BeanUtils.copyProperties(vo, ivo);
				pObjAuth.setSessOnlyDeptUser(ivo);
				break;
			}
		}
		for(ZappDeptUserExtend vo : rZappDeptUserList) {
			ZappDeptUser ivo = new ZappDeptUser();
			BeanUtils.copyProperties(vo, ivo);
			rZappOnlyDeptUserList.add(ivo);
//			rSessAclObjList.add(vo.getDeptuserid());
//			rSessAclObjList.add(vo.getDeptid());
		}
		if(pObjAuth.getSessDeptUser() != null) {
			rSessAclObjList.add(pObjAuth.getSessDeptUser().getDeptid());
			rSessAclObjList.add(pObjAuth.getSessDeptUser().getDeptuserid());
		}
		pObjAuth.setSessDeptUsers(rZappDeptUserList);
		pObjAuth.setSessOnlyDeptUsers(rZappOnlyDeptUserList);
		
		/* Group User */
		StringBuffer sbdeptid = new StringBuffer();
		List<ZappGroupUserExtend> rZappGroupUserList = null;
		List<ZappGroupUser> rZappOnlyGroupUserList = new ArrayList<ZappGroupUser>();
		for(ZappDeptUserExtend vo : rZappDeptUserList) {
			sbdeptid.append(vo.getDeptid() + DIVIDER);
		}
		ZappGroupUser pZappGroupUser_Dept = new ZappGroupUser(null, sbdeptid.toString(), ZappConts.TYPES.OBJTYPE_DEPT.type);
		ZappGroupUser pZappGroupUser_User = new ZappGroupUser(null, pObjAuth.getSessDeptUser().getDeptuserid(), ZappConts.TYPES.OBJTYPE_USER.type);
//		pObjRes = organService.selectObjectExtend(pObjAuth, pZappGroupUser, pObjRes);
		pObjRes = organService.selectAclObjectExtend(pObjAuth, null, pZappGroupUser_Dept, pZappGroupUser_User, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == true) {
			rZappGroupUserList = (List<ZappGroupUserExtend>) pObjRes.getResObj();
			pObjAuth.setSessGroupUsers(rZappGroupUserList);	
			for(ZappGroupUserExtend vo : rZappGroupUserList) {
				ZappGroupUser ivo = new ZappGroupUser();
				BeanUtils.copyProperties(vo, ivo);
				rZappOnlyGroupUserList.add(ivo);
				rSessAclObjList.add(vo.getGroupid());
			}
			pObjAuth.setSessOnlyGroupUsers(rZappOnlyGroupUserList);	
			
		}
		
		/* 권한 대상 정보 */
		HashSet<String> onlySessAclObjList = new HashSet<String>(rSessAclObjList);
		List<String> rSessAclObjList_ = new ArrayList<String>(onlySessAclObjList);
		pObjAuth.setSessAclObjList(rSessAclObjList_);
		
		/* [Inquiry all lower dept. Info]
		 * 
		 */
		if(rZappOnlyDeptUserList.size() > ZERO) {
			ZappDept pZappDept_Filter = new ZappDept();
			pZappDept_Filter.setDeptid(Operators.IN.operator);
			ZappDept pZappDept_Value = new ZappDept();
			sbdeptid.setLength(ZERO);
			for(ZappDeptUser vo : rZappOnlyDeptUserList) {
				sbdeptid.append(vo.getDeptid() + DIVIDER);
			}
			pZappDept_Value.setDeptid(sbdeptid.toString());
			pObjRes = organService.selectObjectDown(pObjAuth, pZappDept_Filter, pZappDept_Value, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == true) {
				if(pObjRes.getResObj() != null) {
					List<ZappDept> tmpDeptList = (List<ZappDept>) pObjRes.getResObj();
					List<String> allLowerDeptList = new ArrayList<String>();
					for(ZappDept vo : tmpDeptList) {
						allLowerDeptList.add(vo.getDeptid());
					}
					pObjAuth.setSessAllLowerDepts(allLowerDeptList);
				} else {
					pObjAuth.setSessAllLowerDepts(new ArrayList<String>());
				}
			}
		}
		
		/* Authentication info. */
		pObjRes.setResObj(pObjAuth);
		
		return pObjRes;
	}
	
	/**
	 * Inquire the preferences information of the user's organization.
	 * @param pObjAuth
	 * @param pObjRes
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 */
	@SuppressWarnings("unchecked")
	private ZstFwResult getEnv(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
	
		/* Preferences */
		Map<String, ZappEnv> sessEnv = new HashMap<String, ZappEnv>();
		ZappEnv pZappEnv = new ZappEnv(pObjAuth.getObjCompanyid(), YES);
		pZappEnv.setUserid(pObjAuth.getObjCompanyid());  // Adding user id
		pObjRes = systemService.selectObject(pObjAuth, pZappEnv, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_ENV", "[getEnv] " + messageService.getMessage("ERR_R_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		List<ZappEnv> rZappEnvList = (List<ZappEnv>) pObjRes.getResObj();
		if(rZappEnvList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_ENV", "[getEnv] " + messageService.getMessage("ERR_NEXIST_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		if(rZappEnvList.size() == ZERO) {
			return ZappFinalizing.finalising("ERR_NEXIST_ENV", "[getEnv] " + messageService.getMessage("ERR_NEXIST_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappEnv vo : rZappEnvList) {
			sessEnv.put(vo.getEnvkey(), vo);
		}
		
		/* 최종 정보 저장 */
		pObjRes.setResObj(sessEnv);
		
		return pObjRes;
	}
	private ZstFwResult getUserEnv(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* Preferences */
		Map<String, ZappEnv> sessEnv = new HashMap<String, ZappEnv>();
		ZappEnv pZappEnv = new ZappEnv(pObjAuth.getObjCompanyid(), YES);
		pZappEnv.setUserid(pObjAuth.getSessDeptUser().getUserid());  // Adding user id
		pObjRes = systemService.selectObject(pObjAuth, pZappEnv, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_ENV", "[getEnv] " + messageService.getMessage("ERR_R_ENV",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		@SuppressWarnings("unchecked")
		List<ZappEnv> rZappEnvList = (List<ZappEnv>) pObjRes.getResObj();
		if(rZappEnvList == null) {
			pObjRes.setResObj(sessEnv);
			return pObjRes;
		}
		if(rZappEnvList.size() == ZERO) {
			pObjRes.setResObj(sessEnv);
			return pObjRes;
		}
		for(ZappEnv vo : rZappEnvList) {
			sessEnv.put(vo.getEnvkey(), vo);
		}
		
		/* 최종 정보 저장 */
		pObjRes.setResObj(sessEnv);
		
		return pObjRes;
	}
	
	
	/**
	 * 세션 저장용 Company 사용 Code 정보를 조회한다.
	 * @param pObjAuth
	 * @param pObjRes
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 */
	@SuppressWarnings("unchecked")
	private ZstFwResult getCode(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
	
		/* Preferences */
		Map<String, ZappCode> sessCode = new HashMap<String, ZappCode>();
		ZappCode pZappCode = new ZappCode(pObjAuth.getObjCompanyid(), YES);
		pObjRes = systemService.selectObject(pObjAuth, pZappCode, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_R_CODE", "[getCode] " + messageService.getMessage("ERR_R_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		List<ZappCode> rZappCodeList = (List<ZappCode>) pObjRes.getResObj();
		if(rZappCodeList == null) {
			return ZappFinalizing.finalising("ERR_NEXIST_CODE", "[getCode] " + messageService.getMessage("ERR_NEXIST_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		for(ZappCode vo : rZappCodeList) {
			if(ZstFwValidatorUtils.valid(vo.getCodekey())) {
				sessCode.put(vo.getCodekey(), vo);
			}
		}
		
		/* 최종 정보 저장 */
		pObjRes.setResObj(sessCode);
		
		return pObjRes;
	}	
	



}

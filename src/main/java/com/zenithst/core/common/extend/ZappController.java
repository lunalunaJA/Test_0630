package com.zenithst.core.common.extend;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import com.zenithst.core.authentication.service.ZappJWTService;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;
import com.zenithst.framework.web.controller.ZstFwController;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to extend controller classes. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappController extends ZstFwController {
	
	@Autowired
	private ZappJWTService jwtservice;
	
	/* Meessage */
	@Autowired
	protected ZappMessageMgtService msgservice;

	/* Separators */
	public static final String BLANK = ZstFwConst.SCHARS.BLANK.character;
	public static final String DIVIDER = ZstFwConst.SCHARS.DIVIDER.character;
	public static final String COLON = ZstFwConst.SCHARS.COLON.character;
	public static final String EQUAL = ZstFwConst.SCHARS.EQUAL.character;
	public static final String PERIOD = ZstFwConst.SCHARS.PERIOD.character;
	
	/* Numbers */
	public static final int ZERO = ZstFwConst.NUMS.ZERO.num;
	public static final int ONE = ZstFwConst.NUMS.ONE.num;
	
	/* Flags */
	public static final String YES = ZstFwConst.USAGES.YES.use;
	public static final String NO = ZstFwConst.USAGES.NO.use; 
	
	/* Result */
	public static final String SUCCESS = ZstFwConst.RESULTS.SUCCESS.result;
	public static final String FAILURE = ZstFwConst.RESULTS.FAILURE.result;
	
	
	/**
	 * 세션 정보를 Object 에 저장한다.
	 * @param pSession
	 * @return
	 */
	protected ZappAuth getAuth(HttpSession pSession) {
		
		ZappAuth pZappAuth = new ZappAuth();
		if(pSession != null) {
			pZappAuth = (ZappAuth) pSession.getAttribute("Authentication");
			if (pZappAuth != null) {
				if(pSession.getAttribute("sessLang") == null) {
				   String lange = "en";
				   pZappAuth.setObjlang(lange);
				}else {
				   pZappAuth.setObjlang(pSession.getAttribute("sessLang").toString());
				}
			} else {
				System.out.println("=== session.Authentication is null");
				return null;
			}
		} else {
			System.out.println("=== HttpSession is null");
			return null;
		}
		
		return pZappAuth;
	}

	/**
	 * For Token
	 * @param pAccessRoute
	 * @param pJwt
	 * @param pSession
	 * @return
	 */
	protected ZappAuth getAuth(HttpSession pSession, String pJwt) {
		
		ZappAuth rZappAuth = null;
		
		if(ZstFwValidatorUtils.valid(pJwt) == false) {	// Through Web
			if(pSession != null) {
				rZappAuth = (ZappAuth) pSession.getAttribute("Authentication");
			}
		} else {
			ZappAuth pZappAuth = new ZappAuth();
			pZappAuth.setObjJwt(pJwt);
			try {
				rZappAuth = jwtservice.checkJWT_Simple(pZappAuth);
			} catch (ZappException e) {
				return null;
			} catch (SQLException e) {
				return null;
			}
		}
		
		return rZappAuth;
	}

	/**
	 * 
	 * @param result
	 * @param lang
	 * @return
	 */
	protected ZstFwResult sessionOut(ZstFwResult result, String lang) {
		
		result.setResCode("SESSION_EXPIRED");
		result.setMessage(msgservice.getMessage("SESSION_EXPIRED", lang));
		return result;
		
	}
	
}

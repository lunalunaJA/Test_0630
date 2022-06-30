package com.zenithst.core.authentication.service;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;

public interface ZappJWTService {

	boolean checkJWT(ZappAuth pZappAuth, HttpServletRequest pRequest);
	ZappAuth checkJWT_Obj(ZappAuth pZappAuth);
	ZappAuth checkJWT_Simple(ZappAuth pZappAuth) throws ZappException, SQLException;
	
	String createJWT(ZappAuth pZappAuth, HttpServletRequest pRequest);
	String createJWT_Obj(ZappAuth pZappAuth, HttpServletRequest pRequest);
	String createJWT_Simple(ZappAuth pZappAuth, HttpServletRequest pRequest);

}

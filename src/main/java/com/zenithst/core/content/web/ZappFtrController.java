package com.zenithst.core.content.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappController;
import com.zenithst.core.common.utility.ZappMappingUtils;
import com.zenithst.core.content.api.ZappContentMgtService;
import com.zenithst.core.content.service.ZappFtrService;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappContentRes;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;

@RestController
@RequestMapping(value = "/api/ftr")
public class ZappFtrController extends ZappController {

	@Autowired
	private ZappFtrService ftrService;
	
	@Autowired
	private ZappContentMgtService service;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus add(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			result = ftrService.executeIndex(pZappAuth, pIn, result);
		} catch (ZappException e) {
			result = e.getZappResult();
		} catch (IOException e) {
			if(null != e.getCause()) {
				result.setResCode(e.getCause().toString());
			}else {
				result.setResCode("ERROR");
			}
			result.setMessage(e.getMessage());	
		} 		
		return ZappMappingUtils.mapResultToStatus(result, request.getRequestURI());
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST, produces = {"application/json", "application/xml"})
	@ResponseStatus(HttpStatus.CREATED)	
	public ZstFwStatus list(@RequestBody ZappContentPar pIn, HttpServletRequest request, HttpSession session) {
		
		ZstFwResult result = new ZstFwResult(); result.setResCode(SUCCESS);

		/* Test */
		ZappAuth pZappAuth = new ZappAuth();
		pZappAuth = getAuth(session, pIn.getObjJwt());
		if(pZappAuth == null) {
			return ZappMappingUtils.mapResultToStatus(sessionOut(result, pIn.getObjlang()), request.getRequestURI());
		}

		try {
			logger.debug("=== pIn.getSword():" + pIn.getSword());
			result = service.selectFTRList(pZappAuth, pIn, result);
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
	
}

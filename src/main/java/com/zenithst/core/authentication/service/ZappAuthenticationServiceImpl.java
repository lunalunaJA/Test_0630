package com.zenithst.core.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.system.api.ZappSystemMgtService;

@Service("zappAuthenticationService")
public class ZappAuthenticationServiceImpl extends ZappService implements ZappAuthenticationService {

	/* Orgnization */
	@Autowired
	private ZappOrganMgtService zappOrganService;
	
	/* System */
	@Autowired
	private ZappSystemMgtService zappSystemService;
	
	public ZappAuth getAccessorInfo(ZappAuth pZappAuth) {
		
		ZappAuth rZappAuth = new ZappAuth();
		
		return rZappAuth;
	}
	
	public ZappAuth getEnvInfo(ZappAuth pZappAuth) {
		
		ZappAuth rZappAuth = new ZappAuth();
		
		return rZappAuth;
	}

}

package com.zenithst.core.authentication.service;

import com.zenithst.core.authentication.vo.ZappAuth;

public interface ZappAuthenticationService {

	ZappAuth getAccessorInfo(ZappAuth pObjAuth); 
	
	ZappAuth getEnvInfo(ZappAuth pObjAuth);
	
}

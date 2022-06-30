package com.zenithst.core.common.utility;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : Utility class for paging. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappPaging extends ZappDomain {
	
	public ZappPaging() {}
	public ZappPaging(ZappAuth pObjAuth, int pPgnum) {
		int[] vals = setPaging(pObjAuth, pPgnum);
		super.setObjsnum(vals[ZERO]);
		super.setObjenum(vals[ONE]);
	}

	private int[] setPaging(ZappAuth pObjAuth, int pPgnum) {
		
		int[] pgnums = {10, 0}; 
		
		if(pObjAuth != null && pPgnum > 0) {
			
			int cntPerPg = 10;	// 기본 Paging Info. 수
			
			// Paging Info. 목록 수
			ZappEnv SYS_LIST_CNT_PER_PAGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_CNT_PER_PAGE.env);
			if(SYS_LIST_CNT_PER_PAGE != null) {
				cntPerPg = ZstFwValidatorUtils.fixNullInt(SYS_LIST_CNT_PER_PAGE.getSetval(), 10);
			}
			
			// 값 세팅
			pgnums[ZERO] = cntPerPg;
			pgnums[ONE] = (cntPerPg * pPgnum) - cntPerPg; 
			
		}
		
		return pgnums;
		
	}
	
}

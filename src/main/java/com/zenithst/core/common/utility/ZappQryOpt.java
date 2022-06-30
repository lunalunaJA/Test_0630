package com.zenithst.core.common.utility;

import java.util.Map;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : Utility class for listing. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappQryOpt extends ZappDomain {
	
	public ZappQryOpt() {}
	public ZappQryOpt(ZappAuth pObjAuth, int pPgnum, Map<String, String> pOrds) {
		int[] vals = setPaging(pObjAuth, pPgnum);
		super.setObjsnum(vals[ZERO]);
		super.setObjenum(vals[ONE]);
		super.setObjpgnum(pPgnum);
		super.setObjnumperpg(vals[ZERO]);
		super.setObjmaporder(pOrds);				// Sorting Info.
	}
	public ZappQryOpt(ZappAuth pObjAuth, int pNumperpg, int pPgnum, Map<String, String> pOrds) {
		int[] vals = setPaging(pObjAuth, pNumperpg, pPgnum);
		super.setObjsnum(vals[ZERO]);
		super.setObjenum(vals[ONE]);
		super.setObjpgnum(pPgnum);
		super.setObjnumperpg(vals[ZERO]);
		super.setObjmaporder(pOrds);				// Sorting Info.
	}

	private int[] setPaging(ZappAuth pObjAuth, int pNumperpg, int pPgnum) {
		
		int[] pgnums = {10, 0}; 
		
		if(pObjAuth != null && pPgnum > 0) {
			
			int cntPerPg = 10;	// 기본 Paging Info. 수
			
			// Paging Info. 목록 수 (Company)
			ZappEnv SYS_LIST_CNT_PER_PAGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.LIST_CNT_PER_PAGE.env);
			if(SYS_LIST_CNT_PER_PAGE != null) {
				cntPerPg = ZstFwValidatorUtils.fixNullInt(SYS_LIST_CNT_PER_PAGE.getSetval(), 10);
			}

			// Paging Info. 목록 수 (Company)
			if(pObjAuth.getSessUserEnv() != null) {
				ZappEnv SYS_LIST_CNT_PER_PAGE_USER = (ZappEnv) pObjAuth.getSessUserEnv().get(ZappConts.ENVS.LIST_CNT_PER_PAGE.env);
				if(SYS_LIST_CNT_PER_PAGE_USER != null) {
					cntPerPg = ZstFwValidatorUtils.fixNullInt(SYS_LIST_CNT_PER_PAGE.getSetval(), 10);
				}
			}
			
			if(pNumperpg > ZERO) {
				cntPerPg = pNumperpg;
			}
			
			// 값 세팅
			pgnums[ZERO] = cntPerPg;
			pgnums[ONE] = (cntPerPg * pPgnum) - cntPerPg; 
			
		} else {
			pgnums[ZERO] = -1;
			pgnums[ONE] = -1;
		}
		
		return pgnums;
		
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
			
		} else {
			pgnums[ZERO] = -1;
			pgnums[ONE] = -1;
		}
		
		return pgnums;
		
	}

}

package com.zenithst.core.common.extend;

import org.springframework.beans.factory.annotation.Autowired;

import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.service.ZstFwServiceBase;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to extend service classes. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappService extends ZstFwServiceBase {
	
	/* Separators */
	public static final String BLANK = ZstFwConst.SCHARS.BLANK.character;
	public static final String DIVIDER = ZstFwConst.SCHARS.DIVIDER.character;
	public static final String COLON = ZstFwConst.SCHARS.COLON.character;
	public static final String EQUAL = ZstFwConst.SCHARS.EQUAL.character;
	public static final String PERIOD = ZstFwConst.SCHARS.PERIOD.character;
	
	/* Numbers */
	public static final int ZERO = ZstFwConst.NUMS.ZERO.num;
	public static final int ONE = ZstFwConst.NUMS.ONE.num;
	public static final int TWO = ZstFwConst.NUMS.TWO.num;
	
	/* Flags */
	public static final String YES = ZstFwConst.USAGES.YES.use;
	public static final String NO = ZstFwConst.USAGES.NO.use; 
	
	/* Result */
	public static final String SUCCESS = ZstFwConst.RESULTS.SUCCESS.result;
	public static final String FAILURE = ZstFwConst.RESULTS.FAILURE.result;
	
	
	
}

package com.zenithst.core.common.utility;

import org.springframework.beans.BeanUtils;

import com.zenithst.archive.util.DateUtil;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.core.common.extend.ZappStatus;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.domain.ZstFwStatus;

/**  
* <pre>
* <b>
* 1) Description : Utility class for mapping. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappMappingUtils {

	
	/**
	 * ZstFwResult -> ZstFwStatus
	 * @param pvo
	 * @param path
	 * @return ZstFwStatus
	 */
	public static ZstFwStatus mapResultToStatus(ZstFwResult pvo, String path) {
		
		ZstFwStatus rZstFwStatus = mapResultToStatus(pvo);
		rZstFwStatus.setPath(path);
		return rZstFwStatus;
		
	}
	
	/**
	 * ZArchResult -> ZstFwStatus
	 * @param pvo
	 * @param path
	 * @return ZstFwStatus
	 */
	public static ZstFwStatus mapResultToStatus(ZArchResult pvo, String path) {
		
		ZstFwStatus rZstFwStatus = mapResultToStatus(pvo);
		rZstFwStatus.setPath(path);
		return rZstFwStatus;
		
	}
	
	/**
	 * ZstFwResult -> ZstFwStatus
	 * @param pvo
	 * @return ZstFwStatus
	 */
	public static ZstFwStatus mapResultToStatus(ZstFwResult pvo) {
		
		ZappStatus rZstFwStatus = new ZappStatus();
		
		if(pvo != null) {
			BeanUtils.copyProperties(pvo, rZstFwStatus);
			rZstFwStatus.setTimestamp(DateUtil.getCurrentDateTime());
			rZstFwStatus.setStatus(pvo.getResCode());
			rZstFwStatus.setMessage(pvo.getMessage());
			rZstFwStatus.setError(pvo.getError());
			rZstFwStatus.setTrace(pvo.getTrace());
			rZstFwStatus.setResult(pvo.getResObj());
		}
		
		return rZstFwStatus;
		
	}
	
	/**
	 * ZArchResult -> ZstFwStatus
	 * @param pvo
	 * @return ZstFwStatus
	 */
	public static ZstFwStatus mapResultToStatus(ZArchResult pvo) {
		
		ZstFwStatus rZstFwStatus = new ZstFwStatus();
		
		if(pvo != null) {
			BeanUtils.copyProperties(pvo, rZstFwStatus);
			rZstFwStatus.setTimestamp(DateUtil.getCurrentDateTime());
			rZstFwStatus.setStatus(pvo.getCode());
			rZstFwStatus.setMessage(pvo.getMessage());
			rZstFwStatus.setError("");
			rZstFwStatus.setTrace("");
		}
		
		return rZstFwStatus;
		
	}
}

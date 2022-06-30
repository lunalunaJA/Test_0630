package com.zenithst.core.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zenithst.archive.constant.Results;
import com.zenithst.archive.vo.ZArchResult;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to finalize. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappFinalizing {

	/* Log */
	protected final static Logger logger = LoggerFactory.getLogger(ZappFinalizing.class);
	
	/**
	 * <p><b>
	 * Finalising process.
	 * </b></p>
	 * 
	 * <pre>
	 * 
	 * if(result == false) {
	 *    return ZappFinalizing.finalising("result code");
	 * }
	 * 
	 * </pre>
	 * 
	 * @param code Result code <br>
	 * @return ZstFwResult Result Object <br>
	 * @see ZstFwResult
	 */
	public static ZstFwResult finalising(String code) {
		
		ZstFwResult result = new ZstFwResult();
		result.setResCode(code);

		if(!code.equals(ZstFwConst.RESULTS.SUCCESS.result)){
			throw new ZappException(code);
		}

		return result;
	}

	
	public static ZstFwResult finalising_Archive(String code, String lang) {
		
		ZstFwResult result = new ZstFwResult();
		result.setResCode(code);
		result.setMessage(getMessage_Archive(code, ZstFwValidatorUtils.valid(lang) ? lang : ZstFwConst.LANGS.KOREAN.lang));
		
		logger.info("=========================================================");
		logger.info("[Archive] ERROR CODE : " + code);
		logger.info("[Archive] ERROR MESSAGE : " + result.getMessage());
		logger.info("=========================================================");
		
		if(!code.equals(ZstFwConst.RESULTS.SUCCESS.result)){
			throw new ZappException(result.getMessage(), result);
		}

		return result;
	}
	public static ZstFwResult finalising_Archive_kor(String code, String lang) {
		
		ZstFwResult result = new ZstFwResult();
		result.setResCode(code);
		result.setMessage(getMessage_Archive(code, ZstFwValidatorUtils.valid(lang) ? lang : ZstFwConst.LANGS.KOREAN.lang));

		if(!code.equals(ZstFwConst.RESULTS.SUCCESS.result)){
			throw new ZappException(result.getMessage(), result);
		}

		return result;
	}	
	
	/**
	 * <p><b>
	 * Finalising process.
	 * </b></p>
	 * 
	 * <pre>
	 * 
	 * if(result == false) {
	 *    return ZappFinalizing.finalising("result code", "result message");
	 * }
	 * 
	 * </pre>
	 * 
	 * @param code Result code <br>
	 * @param message Result message <br>
	 * @return ZstFwResult Result Object <br>
	 * @see ZstFwResult
	 */
	public static ZstFwResult finalising(String code, String message, String lang) {
		
		ZstFwResult result = new ZstFwResult();
		result.setResCode(code);
		result.setMessage(message);
		//error Class line trace
		try {
			StackTraceElement[] ste = new Throwable().getStackTrace();		
			logger.error(ste[1].getClassName()+"."+ste[1].getMethodName()+"() line "+ste[1].getLineNumber());
		}catch(Exception e) {			
		}
		
		logger.info("=========================================================");
		logger.info("ERROR CODE : " + code);
		logger.info("ERROR MESSAGE : " + message);
		logger.info("=========================================================");

		if(!code.equals(ZstFwConst.RESULTS.SUCCESS.result)){
			throw new ZappException(result.getMessage(), result);
		}

		return result;
	}
	public static ZstFwResult finalising_Archive(String code, String message, String lang) {
		
		ZstFwResult result = new ZstFwResult();
		result.setResCode(code);
		result.setMessage(message);
		//error Class line trace
		try {
			StackTraceElement[] ste = new Throwable().getStackTrace();		
			logger.error(ste[1].getClassName()+"."+ste[1].getMethodName()+"() line "+ste[1].getLineNumber());
		}catch(Exception e) {			
		}
		
		logger.info("=========================================================");
		logger.info("ERROR CODE : " + code);
		logger.info("ERROR MESSAGE : " + message);
		logger.info("=========================================================");

		if(!code.equals(Results.SUCCESS.result)){
			throw new ZappException(result.getMessage(), result);
		}

		return result;
	}	
	public static ZstFwResult finalising(String code, String message, String lang, Object res) {
		
		ZstFwResult result = new ZstFwResult();
		result.setResCode(code);
		result.setMessage(message);
		result.setResObj(res);
		
		logger.info("=========================================================");
		logger.info("ERROR CODE : " + code);
		logger.info("ERROR MESSAGE : " + message);
		logger.info("ERROR RESOBJ : " + res);
		logger.info("=========================================================");

		if(!code.equals(ZstFwConst.RESULTS.SUCCESS.result)){
			throw new ZappException(result.getMessage(), result);
		}

		return result;
	}	

	
	private static String getMessage_Archive(String code, String lang) {
		
		for(Results results : Results.values()) {
			if(results.result.equals(code)) {
				if(lang.equals(ZstFwConst.LANGS.KOREAN.lang)) {
					return results.comment;
				}
				if(lang.equals(ZstFwConst.LANGS.ENGLISH.lang)) {
					return results.note;
				}
			}
		}
		
		return ZstFwConst.SCHARS.BLANK.character;
	}
	
	
	public static boolean isSuccess(ZstFwResult pObjRes) {
	
		boolean isSuccess = false;
		
		if(pObjRes != null) {
			if(pObjRes.getResCode().equals(ZstFwConst.RESULTS.SUCCESS.result)) {
				isSuccess = true;
			}
		} 
		
		return isSuccess;
		
	}
	
	public static boolean isSuccess_Archive(ZstFwResult pObjRes) {
	
		boolean isSuccess = false;
		
		if(pObjRes != null) {
			if(pObjRes.getResCode().equals(Results.SUCCESS.result)) {
				isSuccess = true;
			}
		} 
		
		return isSuccess;
		
	}	
	
	public static boolean isSuccess(ZArchResult pObjRes) {
		
		boolean isSuccess = false;
		
		if(pObjRes != null) {
			logger.info("=========================================================");
			logger.info("Archive");
			logger.info("=========================================================");
			logger.info("ERROR CODE : " + pObjRes.getCode());
			logger.info("=========================================================");
			
			if(pObjRes.getCode().equals(Results.SUCCESS.result)) {
				isSuccess = true;
			}
		} 
		
		return isSuccess;
		
	}	
	
}

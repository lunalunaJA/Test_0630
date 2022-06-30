package com.zenithst.core.common.extend.interfaces;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to define managing processes. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappMgtService {

	/* 기본 기능 */
	
	/* 등록 */
	
	/**
	 * 등록
	 * @param pObjAuth - 접속자 정보
	 * @param pObj - 등록 정보 ( Single - Object / Multiple - List<Object> )
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult addObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* 수정 */
	
	/**
	 * 수정 (PK 기준 단건 처리)
	 * @param pObjAuth - 접속자 정보
	 * @param pObj - 수정 정보 (PK and Others)
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult changeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	/**
	 * 수정 (Default filter 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjs - 수정 정보
	 * @param pObjw - 수정 조건 정보
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	/**
	 * 수정 (지정 필터 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjs - 수정 정보
	 * @param pObjf - 수정 조건 필터 정보
	 * @param pObjw - 수정 조건 정보
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult changeObject(ZappAuth pObjAuth, Object pObjs, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;

	/* 등록 및 수정 */

	/**
	 * 수정 (PK 기준 단건 처리)
	 * @param pObjAuth - 접속자 정보
	 * @param pObj - 수정 정보 (PK and Others)
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult mergeObject(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException;

	
	/* 삭제 */
	
	/**
	 * 삭제 (Default filter 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjw - 삭제 조건 정보 (PK and Others)
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	/**
	 * 삭제 (지정 필터 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjf - 삭제 조건 필터 정보
	 * @param pObjw - 삭제 조건 정보
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult deleteObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* 조회 */
	
	/**
	 * 조회 (Default filter 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjw - 조회 정보 (PK and Others)
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/**
	 * 조회 (지정 필터 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjf - 조회 조건 필터 정보
	 * @param pObjw - 조회 조건 정보
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult selectObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	
	/* Exist or not */
	
	/**
	 * Exist or not (Default filter 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjw - 조회 정보 (PK and Others)
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult existObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	/**
	 * Exist or not (지정 필터 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjf - 조회 조건 필터 정보
	 * @param pObjw - 조회 조건 정보
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult existObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* Counting */
	
	/**
	 * Counting (Default filter 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjw - 조회 정보 (PK and Others)
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult countObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	/**
	 * Counting (지정 필터 사용)
	 * @param pObjAuth - 접속자 정보
	 * @param pObjf - 조회 조건 필터 정보
	 * @param pObjw - 조회 조건 정보
	 * @param pObjRes - 처리 결과
	 * @return ZstFwResult
	 * @throws ZappException, SQLException
	 */
	ZstFwResult countObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;	
	
}

package com.zenithst.core.content.api;

import java.sql.SQLException;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappMgtService;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage content info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappContentMgtService extends ZappMgtService {

	ZstFwResult addObjectExist(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* 통합 기능 */
	ZstFwResult selectExtendObject(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult selectExtendObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	// 신규 컨텐츠 등록
	ZstFwResult addContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult addContentNoFile(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 수정
	ZstFwResult changeContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 삭제
	ZstFwResult disableContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 복원
	ZstFwResult enableContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 폐기
	ZstFwResult discardContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 폐기 (배치용)
	ZstFwResult discardContentForcely(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠의 특정 버전 폐기
	ZstFwResult discardSpecificVersionContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 이동
	ZstFwResult relocateContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 복사
	ZstFwResult replicateContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 잠금
	ZstFwResult lockContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 잠금 해제
	ZstFwResult unlockContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 강제 잠금 해제 (관리자용)
	ZstFwResult unlockContentForcely(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 책갈피
	ZstFwResult markContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 책갈피 해제
	ZstFwResult unmarkContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 공유
	ZstFwResult shareContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 기존 컨텐츠 공유 해제
	ZstFwResult changeShareContent(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 보존 기간 연장
	ZstFwResult extendContentRetention(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 파일 강제 버전 업
	ZstFwResult replaceFile(ZappAuth pObjAuth, ZappContentPar pObjContent, ZstFwResult pObjRes) throws ZappException, SQLException;
		
	/* ************************************************************************************************************ */
	// 컨텐츠 정보 조회
	ZstFwResult selectContent(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	// 컨텐츠 버전 정보 조회
	ZstFwResult selectVersion(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	// 물리적 목록 조회
	ZstFwResult selectPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	// 비물리적 목록 조회
	ZstFwResult selectNonPhysicalList(ZappAuth pObjAuth, ZappContentPar pObjf, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	// FTR 목록 조회
	ZstFwResult selectFTRList(ZappAuth pObjAuth, ZappContentPar pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	/* ************************************************************************************************************ */
	ZstFwResult getFile(ZappAuth pObjAuth, ZappFile pObjContent, ZstFwResult pObjRes);
	
	// 동일 파일명 체크
	ZstFwResult existFilename(ZappAuth pObjAuth, ZappContentPar pObj, ZstFwResult pObjRes) throws ZappException, SQLException;

	
	// 문서 번호 
	String getContentNo(ZappAuth pObjAuth) throws ZappException;
}

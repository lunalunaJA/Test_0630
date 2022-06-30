package com.zenithst.core.common.extend;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwDomain;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to extend wapper classes. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappDomain extends ZstFwDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* Separators */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String BLANK = ZstFwConst.SCHARS.BLANK.character;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String DIVIDER = ZstFwConst.SCHARS.DIVIDER.character;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String COLON = ZstFwConst.SCHARS.COLON.character;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String EQUAL = ZstFwConst.SCHARS.EQUAL.character;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String PERIOD = ZstFwConst.SCHARS.PERIOD.character;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String PERCENT = ZstFwConst.SCHARS.PERCENT.character;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String SPACE = ZstFwConst.SCHARS.SPACE.character;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String COMMA = ZstFwConst.SCHARS.COMMA.character;
	
	/* Numbers */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final int MINUS_ONE = ZstFwConst.NUMS.MINUS_ONE.num;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final int ZERO = ZstFwConst.NUMS.ZERO.num;	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final int ONE = ZstFwConst.NUMS.ONE.num;
	
	/* Flags */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String YES = ZstFwConst.USAGES.YES.use;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String NO = ZstFwConst.USAGES.NO.use;
	
	/* Result */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String SUCCESS = ZstFwConst.RESULTS.SUCCESS.result;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public static final String FAILURE = ZstFwConst.RESULTS.FAILURE.result;	
	

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int objpgnum;	// Paging Info. 번호
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int objnumperpg;	// Paging Info.당 건수
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int objsnum;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private int objenum;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String[] objorder;		// Sorting order
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String[] objorderfield;	// 정렬컬럼
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Map<String, String> objmaporder = new HashMap<String, String>(); // Sorting info.
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objlang;			// 메세지 언어
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objRetention;	// Retention period ID (Code)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objType;			// Target type
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objHandleType;	// Processing type
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objRes;			// 대상결과
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objIsTest = NO;	// 테스트 여부
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objHasconds = NO; // 조건 지정 여부
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private boolean objDebugged = false; // 디버깅
//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objAction;			// 처리종류
//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objIncLower;			// 하위 계츧 포함여부
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean objSkipAcl = false;	// 권한 적용 여부
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean objIsMngMode = false;	// 관리자 모드 여부
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String objAcsRoute = BLANK;		// 승인 강제 처리 여부
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objQueryType;			// 대상조회유형
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objCaller;			// 호출대상	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String objJwt;				// JWT	
	
    /* Test */
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private ZappAuth objTestUser;			// 테스트사용자   

	public ZappAuth getObjTestUser() {
		return objTestUser;
	}

	public void setObjTestUser(ZappAuth objTestUser) {
		this.objTestUser = objTestUser;
	}
	
	public int getObjpgnum() {
		return objpgnum;
	}
	public void setObjpgnum(int objpgnum) {
		this.objpgnum = objpgnum;
	}
	
	public int getObjnumperpg() {
		return objnumperpg;
	}

	public void setObjnumperpg(int objnumperpg) {
		this.objnumperpg = objnumperpg;
	}

	public int getObjsnum() {
		return objsnum;
	}
	public void setObjsnum(int objsnum) {
		this.objsnum = objsnum;
	}
	public int getObjenum() {
		return objenum;
	}
	public void setObjenum(int objenum) {
		this.objenum = objenum;
	}
	public String[] getObjorder() {
		return objorder;
	}
	public String[] getObjorderfield() {
		return objorderfield;
	}
	public void setObjorder(String[] objorder) {
		this.objorder = objorder;
	}
	public void setObjorderfield(String[] objorderfield) {
		this.objorderfield = objorderfield;
	}
	public Map<String, String> getObjmaporder() {
		return objmaporder;
	}
	public void setObjmaporder(Map<String, String> objmaporder) {
		this.objmaporder = objmaporder;
	}
	public String getObjlang() {
		return objlang;
	}
	public void setObjlang(String objlang) {
		this.objlang = objlang;
	}
	public String getObjRetention() {
		return objRetention;
	}
	public void setObjRetention(String objRetention) {
		this.objRetention = objRetention;
	}
	public String getObjType() {
		return objType;
	}
	public void setObjType(String objType) {
		this.objType = objType;
	}
	public String getObjHandleType() {
		return objHandleType;
	}
	public void setObjHandleType(String objHandleType) {
		this.objHandleType = objHandleType;
	}
	public String getObjRes() {
		return objRes;
	}
	public void setObjRes(String objRes) {
		this.objRes = objRes;
	}
	public String getObjIsTest() {
		return objIsTest;
	}
	public void setObjIsTest(String objIsTest) {
		this.objIsTest = objIsTest;
	}
	public String getObjHasconds() {
		return objHasconds;
	}
	public void setObjHasconds(String objHasconds) {
		this.objHasconds = objHasconds;
	}
	public boolean getObjDebugged() {
		return objDebugged;
	}
	public void setObjDebugged(boolean objDebugged) {
		this.objDebugged = objDebugged;
	}
	public String getObjAction() {
		return objAction;
	}
	public void setObjAction(String objAction) {
		this.objAction = objAction;
	}
	public String getObjIncLower() {
		return objIncLower;
	}
	public void setObjIncLower(String objIncLower) {
		this.objIncLower = objIncLower;
	}
	public boolean getObjSkipAcl() {
		return objSkipAcl;
	}
	public void setObjSkipAcl(boolean objSkipAcl) {
		this.objSkipAcl = objSkipAcl;
	}
	public boolean getObjIsMngMode() {
		return objIsMngMode;
	}
	public void setObjIsMngMode(boolean objIsMngMode) {
		this.objIsMngMode = objIsMngMode;
	}
	public String getObjAcsRoute() {
		return objAcsRoute;
	}
	public void setObjAcsRoute(String objAcsRoute) {
		this.objAcsRoute = objAcsRoute;
	}
	public String getObjQueryType() {
		return objQueryType;
	}
	public void setObjQueryType(String objQueryType) {
		this.objQueryType = objQueryType;
	}
	public String getObjCaller() {
		return objCaller;
	}
	public void setObjCaller(String objCaller) {
		this.objCaller = objCaller;
	}

	public String getObjJwt() {
		return objJwt;
	}

	public void setObjJwt(String objJwt) {
		this.objJwt = objJwt;
	}
	
	
}

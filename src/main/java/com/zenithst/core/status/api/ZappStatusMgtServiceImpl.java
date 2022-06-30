package com.zenithst.core.status.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zenithst.archive.constant.Characters;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappJSONUtils;
import com.zenithst.core.organ.service.ZappOrganService;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.status.service.ZappStatusService;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.status.vo.ZappStatus;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage statistics info. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappStatusMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappStatusMgtService")
public class ZappStatusMgtServiceImpl extends ZappService implements ZappStatusMgtService {


	/* 형황 */
	@Autowired
	private ZappStatusService statusService;
	
	/* Organisation */
	@Autowired
	private ZappOrganService organService;
	
	/* Message */
	@Autowired
	private ZappMessageMgtService messageService;	
	
	@Value("#{archiveconfig['APM_DB_SCHEME']}")
	private String APM_DB_SCHEME;
	
	@Value("#{archiveconfig['APM_DISK']}")
	private String APM_DISK;
	
	
	/**
	 * 
	 */
	public ZstFwResult getProcessStatusList(ZappAuth pObjAuth, ZappStatus pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		pObjRes = validParams(pObjAuth, pObj, "getListOnLog", pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_MIS_INVAL", "[getListOnLog] " + messageService.getMessage("ERR_MIS_INVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* 임시 테이블 생성 */
		if(statusService.createTmpDateTbl(pObjAuth, pObj, pObjRes) == false) {
			return ZappFinalizing.finalising("ERR_C_TABLE", "[getListOnLog] " + messageService.getMessage("ERR_C_TABLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* 임시 날짜 정보 등록 */
		if(statusService.insertTmpDate(pObjAuth, pObj, pObjRes) == false) {
			statusService.dropTmpDateTbl(pObjAuth, pObj, pObjRes);
			return ZappFinalizing.finalising("ERR_C_TABLE", "[getListOnLog] " + messageService.getMessage("ERR_C_TABLE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		
		/* */
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_MONTH.type)) {		// Month
			pObj.setStasdate(pObj.getStayear() + "-01-01");
			pObj.setStaedate(pObj.getStayear() + "-12-31");
		}
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_QUARTER.type)) {		// QUARTER
			pObj.setStasdate(pObj.getStayear() + "-01-01");
			pObj.setStaedate(pObj.getStayear() + "-12-31");
		}
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_HALF.type)) {	// HALF
			pObj.setStasdate(pObj.getStayear() + "-01-01");
			pObj.setStaedate(pObj.getStayear() + "-12-31");
		}
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_YEAR.type)) {	// YEAR
			pObj.setStasdate(pObj.getStasdate() + "-01-01");
			pObj.setStaedate(pObj.getStaedate() + "-12-31");
		}
		
		/* 정보 조회 */
//		List<String> objActons = new ArrayList<String>();
//		if(pObj.getObjAction().equals(ZappConts.ACTION.ADD.name())) {
//			objActons.add(ZappConts.LOGS.ACTION_ADD.log);
//		}
//		pObj.setStaaction(objActons);
		pObjRes = statusService.getProcessStatusList(pObjAuth, pObj, pObjRes);
		
		/* 임시 테이블 삭제 */
		if(statusService.dropTmpDateTbl(pObjAuth, pObj, pObjRes) == false) {

		}
		
		return pObjRes;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult getProcessStatusListAll(ZappAuth pObjAuth, ZappStatus pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* */
		Map<String, List<ZappStatus>> rSingleMap = new HashMap<String, List<ZappStatus>>();
		Map<String, Map<String, List<ZappStatus>>> rMultipleMap = new HashMap<String, Map<String, List<ZappStatus>>>();
		
		pObjRes = validParams(pObjAuth, pObj, "getListOnLog", pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("누락_입력값", "[getListOnLog] " + messageService.getMessage("누락_입력값",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		/* 임시 테이블 생성 */
		if(statusService.createTmpDateTbl(pObjAuth, pObj, pObjRes) == false) {
			return ZappFinalizing.finalising("오류_임시테이블생성", "[getListOnLog] " + messageService.getMessage("오류_임시테이블생성",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}

		
		/* 임시 날짜 정보 등록 */
		if(statusService.insertTmpDate(pObjAuth, pObj, pObjRes) == false) {
			statusService.dropTmpDateTbl(pObjAuth, pObj, pObjRes);
			return ZappFinalizing.finalising("오류_임시데이타생성", "[getListOnLog] " + messageService.getMessage("오류_임시데이타생성",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		} 
		
		/* */
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_MONTH.type)) {		// Month
			pObj.setStayear(pObj.getStasdate());
			pObj.setStasdate(pObj.getStayear() + "-01-01");
			pObj.setStaedate(pObj.getStayear() + "-12-31");
		}
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_QUARTER.type)) {		// QUARTER
			pObj.setStasdate(pObj.getStayear() + "-01-01");
			pObj.setStaedate(pObj.getStayear() + "-12-31");
		}
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_HALF.type)) {	// HALF
			pObj.setStasdate(pObj.getStayear() + "-01-01");
			pObj.setStaedate(pObj.getStayear() + "-12-31");
		}
		if(pObj.getStatermtype().equals(ZappConts.TYPES.STATUS_YEAR.type)) {	// YEAR
			pObj.setStasdate(pObj.getStasdate() + "-01-01");
			pObj.setStaedate(pObj.getStaedate() + "-12-31");
		}
		
		logger.info("pObj.getStasdate() = " + pObj.getStasdate());
		logger.info("pObj.getStaedate() = " + pObj.getStaedate());

		/* Inquiry user info. */
		Map<String, String> rUMap = new HashMap<String, String>();
		if(pObj.getObjType().equals(ZappConts.TYPES.OBJTYPE_USER.type)) {
			ZappDeptUserExtend pZappDeptUser = new ZappDeptUserExtend(); 
			ZappDept pZappDept = new ZappDept(); pZappDept.setCompanyid(pObjAuth.getObjCompanyid());
			pZappDeptUser.setZappDept(pZappDept);
			pZappDeptUser.setObjpgnum(-1);
			if(ZstFwValidatorUtils.valid(pObj.getStaobjid()) == true) {
				pZappDeptUser.setDeptuserid(pObj.getStaobjid());
			}
			pObjRes = organService.rMultiRowsExtend(pObjAuth, pZappDeptUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_USER", "[getProcessStatusListAll] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			List<ZappDeptUserExtend> rZappDeptUser = (List<ZappDeptUserExtend>) pObjRes.getResObj();
			if(rZappDeptUser == null) {
				return ZappFinalizing.finalising("ERR_R_USER", "[getProcessStatusListAll] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			for(ZappDeptUserExtend vo : rZappDeptUser) {
				if(rUMap.containsKey(vo.getDeptuserid()) == false) {
					rUMap.put(vo.getDeptuserid(), vo.getZappUser().getName() + " [" + vo.getZappDept().getName() + "]");
				} 
			}
		}
		
		/* Actions */
		String[] actions = null;
		if(ZstFwValidatorUtils.valid(pObj.getStaaction()) == true) {
			actions = pObj.getStaaction().split("-");
		}
		if(actions == null) {
			return ZappFinalizing.finalising("ERR_R_USER", "[getProcessStatusListAll] " + messageService.getMessage("ERR_R_USER",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		for(String action : actions) {
			rSingleMap = new HashMap<String, List<ZappStatus>>();
			pObj.setStaaction(action);
			pObj.setStacompanyid(pObjAuth.getObjCompanyid());
			pObjRes = statusService.getProcessStatusListAll(pObjAuth, pObj, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return ZappFinalizing.finalising("ERR_R_STATUS", "[getProcessStatusListAll] " + messageService.getMessage("ERR_R_STATUS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			Map<String, List<ZappStatus>> rtMap = new HashMap<String, List<ZappStatus>>();
			List<ZappStatus> rZappStatusList = (List<ZappStatus>) pObjRes.getResObj();
			if(rZappStatusList != null) {
				for(ZappStatus vo : rZappStatusList) {
					List<ZappStatus> rTmpList = null;
					if(rtMap.containsKey(vo.getStaname())) {
						rTmpList = (List<ZappStatus>) rtMap.get(vo.getStaname());
					} else {
						rTmpList = new ArrayList<ZappStatus>();
					}
					rTmpList.add(vo);
					rtMap.put(vo.getStaname(), rTmpList);
				}
			}
			
			/*  */
			if(pObj.getObjType().equals(ZappConts.TYPES.OBJTYPE_USER.type)) {
				if(rtMap.size() > ZERO) {
					for (Map.Entry<String, List<ZappStatus>> set : rtMap.entrySet()) {
						if(rUMap.get(set.getKey()) != null) {
							rSingleMap.put(rUMap.get(set.getKey()), set.getValue());
						}
					}
				}
			} else {
				rSingleMap = rtMap;
			}
			
			/* */
			rMultipleMap.put(action, rSingleMap);
		}
		
		/* 임시 테이블 삭제 */
		if(statusService.dropTmpDateTbl(pObjAuth, pObj, pObjRes) == false) {

		}
		
		/* Result */
		pObjRes.setResObj(rMultipleMap);	
		
		return pObjRes;
	}	

	public ZstFwResult getHoldStatusList(ZappAuth pObjAuth, ZappStatus pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {

		return pObjRes;
	}
	
	
	/**
	 * Store APM info.
	 * @param pIn
	 * @return
	 * @throws  ZappException, SQLException
	 */
	public ZstFwResult saveApms(ZappAuth pObjAuth, ZappApm pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [APM 유형에 따른 정보 설정]
		 * 
		 */
		StringBuffer apms = new StringBuffer();
		if(pObj.getApmtype().equals(ZappConts.TYPES.APM_DB.type)) {
			apms.append(getDbList(pObjAuth, pObjRes));
		}
		if(pObj.getApmtype().equals(ZappConts.TYPES.APM_DB_Lock.type)) {
			apms.append(getDbLockList(pObjAuth, pObjRes));
		}
		if(pObj.getApmtype().equals(ZappConts.TYPES.APM_OS.type)) {
			apms.append(getOSList(pObjAuth, pObjRes));
		}
		if(pObj.getApmtype().equals(ZappConts.TYPES.APM_DISK.type)) {
			apms.append(getDiskList(pObjAuth, pObjRes));
		}
		if(pObj.getApmtype().equals(ZappConts.TYPES.APM_CHECK.type)) {
			
		}
		
		/* [Store]
		 * 
		 */
		pObjRes = statusService.cSingleRow(pObjAuth, pObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("누락_입력값", "[saveApms] " + messageService.getMessage("누락_입력값",  BLANK), BLANK);
		}
		
		return pObjRes; 
	}

	/**
	 * Delete APM info.
	 * @param pIn
	 * @return
	 * @throws  ZappException, SQLException
	 */
	public ZstFwResult deleteApms(ZappAuth pObjAuth, ZappApm pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [Store]
		 * 
		 */
		pObjRes = statusService.dMultiRows(pObjAuth, pObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("누락_입력값", "[deleteApms] " + messageService.getMessage("누락_입력값",  BLANK), BLANK);
		}

		return pObjRes; 
	}

	/**
	 * Inquiry APM info.
	 * @param pIn
	 * @return
	 * @throws  ZappException, SQLException
	 */
	public ZstFwResult viewApmsList(ZappAuth pObjAuth, ZappApm pObj, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		/* [Store]
		 * 
		 */
		pObjRes = statusService.rMultiRows(pObjAuth, pObj, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return ZappFinalizing.finalising("누락_입력값", "[viewApmsList] " + messageService.getMessage("누락_입력값",  BLANK), BLANK);
		}

		return pObjRes; 
	}
	
	
	private ZstFwResult validParams(ZappAuth pObjAuth, ZappStatus pObj, String pCaller, ZstFwResult pObjRes) {
		
		/* */
//		if(ZstFwValidatorUtils.valid(pObj.getStaobjid()) == false) {
//			return ZappFinalizing.finalising("ERR_MIS_TARID", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_TARID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		if(ZstFwValidatorUtils.valid(pObj.getStatermtype()) == false) {
			return ZappFinalizing.finalising("누락_기간유형", "[" + pCaller + "][validParams] " + messageService.getMessage("누락_기간유형",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
//		if(ZstFwValidatorUtils.valid(pObj.getStasdate()) == false) {
//			return ZappFinalizing.finalising("ERR_MIS_SDATE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_SDATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
//		if(ZstFwValidatorUtils.valid(pObj.getStaedate()) == false) {
//			return ZappFinalizing.finalising("ERR_MIS_EDATE", "[" + pCaller + "][validParams] " + messageService.getMessage("ERR_MIS_EDATE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//		}
		
		return pObjRes;
	}
	
	/**
	 * DB List
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 * @throws JsonProcessingException 
	 */
	private String getDbList(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZappApm pZappApm = new ZappApm();
		
		if(!ZstFwValidatorUtils.valid(APM_DB_SCHEME)) {
			return BLANK;
		}
		
		String[] objId = APM_DB_SCHEME.split(Characters.COMMA.character);
		if(objId != null) {
			pZappApm.setObjDbList(Arrays.asList(objId));
		}
		
		pObjRes = statusService.getDbStatus(pObjAuth, pZappApm, pObjRes);
		
		return ZappJSONUtils.cvrtObjToJson(pObjRes.getResObj());
		
	}

	/**
	 * DB Lock List
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 * @throws JsonProcessingException 
	 */
	private String getDbLockList(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZappApm pIn = new ZappApm();
		
		pObjRes = statusService.getDbLock(pObjAuth, pIn, pObjRes);
		
		return ZappJSONUtils.cvrtObjToJson(pObjRes.getResObj());
		
	}	

	/**
	 * OS List
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 * @throws JsonProcessingException 
	 */
	private String getOSList(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		Map<String, Object> osmap = new HashMap<String, Object>();
		
		OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
			method.setAccessible(true);
		    if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
		            Object value = null;
		        try {
		            value = method.invoke(operatingSystemMXBean);
		        } catch (Exception e) {

		        } 
		        osmap.put(method.getName(), value);
		    } 
		} 
		
		return ZappJSONUtils.cvrtObjToJson(osmap);
		
	}	

	/**
	 * Disk List
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 * @throws JsonProcessingException 
	 */
	private String getDiskList(ZappAuth pObjAuth, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		
		String OS = System.getProperty("os.name").toLowerCase();
		
		/** [Unix]
		 * 
		 */
		if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 || OS.indexOf("sunos") > 0) {
			
			Map<String, String> dmap = new HashMap<String, String>();
			
			if(ZstFwValidatorUtils.valid(APM_DISK)) {
				
				Process proc = null;
				String str = "";
				StringBuffer sb = new StringBuffer();
				BufferedReader br = null;
			
				// Command
				String cmd_t = "df " + APM_DISK;
				String cmd_f = "du -sh " + APM_DISK;
				
				try {
				
					proc = Runtime.getRuntime().exec(cmd_t);
					br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					while((str = br.readLine()) != null) {
						sb.append(str).append("\n");
					}
					
					proc.waitFor();
					int ex = proc.exitValue();
					if(ex != 0) {
						
					}
				
				} catch(IOException e) {
					
				} catch(InterruptedException e) {
					
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				dmap.put("Total", sb.toString());
				
				try {
					
					sb.setLength(0);
					proc = Runtime.getRuntime().exec(cmd_f);
					br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					while((str = br.readLine()) != null) {
						sb.append(str).append("\n");
					}
					
					proc.waitFor();
					int ex = proc.exitValue();
					if(ex != 0) {
						
					}
				
				} catch(IOException e) {
					
				} catch(InterruptedException e) {
					
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				dmap.put("Free", sb.toString());
			
			}
			
			return ZappJSONUtils.cvrtObjToJson(dmap);
		}
		
		/** [Windows]
		 * 
		 */
		if(OS.indexOf("win") >= 0) {
			
			Map<String, Map<String, String>> dmap = new HashMap<String, Map<String, String>>();
			double  totalSize, freeSize, useSize;   
			File[] roots = File.listRoots();	// root list
			
			if(roots != null) {
				for (File root : roots) {
					Map<String, String> tmap = new HashMap<String, String>();
					
					totalSize = Math.round(root.getTotalSpace() / Math.pow(1024, 3));
					useSize = Math.round(root.getUsableSpace() / Math.pow(1024, 3));
					freeSize = Math.round(totalSize - useSize);
					
					tmap.put("Total", String.valueOf(totalSize) + " GB");
					tmap.put("Used", String.valueOf(useSize) + " GB");
					tmap.put("Free", String.valueOf(freeSize) + " GB");
					dmap.put(root.getAbsolutePath(), tmap);
				}
			}
			
			return ZappJSONUtils.cvrtObjToJson(dmap);
		}
		
		return BLANK;
		
	}
	
	
	
}

package com.zenithst.core.cycle.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenithst.archive.constant.Characters;
import com.zenithst.archive.constant.Operators;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.service.ZappCommonService;
import com.zenithst.core.content.api.ZappContentMgtService;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.cycle.service.ZappCycleService;
import com.zenithst.core.log.api.ZappLogMgtService;
import com.zenithst.core.log.vo.ZappCycleLog;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.status.api.ZappStatusMgtService;
import com.zenithst.core.status.service.ZappStatusService;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.status.vo.ZappApmExtend;
import com.zenithst.core.system.api.ZappSystemMgtService;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) 셜명 (Description) : 반복 처리를 관리한다. <br>
* 2) 이력 (History) : <br>
*         - v1.0 / 2020.11.14 / 이경호 / 신규작성 (New)
* 
* 3) 사용법 (Usage or Example) : <br>
* 
*    @Autowired
*	 private ZappCycleMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappCycleMgtService")
public class ZappCycleMgtServiceImpl extends ZappService implements ZappCycleMgtService {

	/* */
	@Autowired
	private ZappCycleService cycleService;
	
	/* 조직 */
	@Autowired
	private ZappOrganMgtService organService;
	
	/* 컨텐츠 */
	@Autowired
	private ZappContentMgtService contentService;
	
	/* 시스템 */
	@Autowired
	private ZappSystemMgtService systemService;
	
	/* 현황 */
	@Autowired
	private ZappStatusMgtService statusMgtService;
	@Autowired
	private ZappStatusService statusService;

	/* 로그 */
	@Autowired
	private ZappLogMgtService logService;
	
	/* 메세지 */
	@Autowired
	private ZappMessageMgtService messageService;
	
	/* Common */
	@Autowired
	private ZappCommonService commonService;

	/* APM */
	@Value("#{archiveconfig['APM_DB_SCHEME']}")
	private String APM_DB_SCHEME;
	@Value("#{archiveconfig['APM_DISK_PATH']}")
	private String APM_DISK_PATH;
	
	@PostConstruct
	public void initService() {
		logger.info("[ZappCycleMgtService] Service Start ");
	}
	@PreDestroy
	public void destroy(){
		logger.info("[ZappCycleMgtService] Service Destroy ");
	}		
	
	@Scheduled(cron = "#{archiveconfig['BATCH_DISCARDDOC']}")	// 매 2분마다
	@SuppressWarnings("unchecked")
	public void discardExpiredContent() throws ZappException, SQLException {
		
		ZstFwResult rObjRes = new ZstFwResult(); rObjRes.setResCode(SUCCESS);
		ZappAuth pZappAuth = new ZappAuth();
		String PROC_TIME = ZstFwDateUtils.getNow();
		Map<String, Object> pLogMap = new HashMap<String, Object>();
		
		/* [기관 정보 조회]
		 * 
		 */
		List<ZappCompany> companylist = getCompanyList(pZappAuth, rObjRes);
		for(ZappCompany vo_company : companylist) {
			pZappAuth.setObjCompanyid(vo_company.getCompanyid());
			
			// 환결 설정 정보 조회 (통계정보생성유형)
			ZappEnv pZappEnv = new ZappEnv(); ZappEnv rZappEnv = null;
			pZappEnv.setCompanyid(vo_company.getCompanyid());
			pZappEnv.setEnvtype(ZappConts.ENVS.CYCLE_STATUS_DISCARD_EXPIRED_YN.type);
			pZappEnv.setEnvkey(ZappConts.ENVS.CYCLE_STATUS_DISCARD_EXPIRED_YN.env);
//			pZappEnv.setEnvid(ZappKey.getPk(pZappEnv));
			rObjRes = systemService.selectObject(pZappAuth, pZappEnv, rObjRes);		
			if(ZappFinalizing.isSuccess(rObjRes) == false) {
				rZappEnv = new ZappEnv();
				rZappEnv.setSetval(NO);
			}
			List<ZappEnv> envlist = (List<ZappEnv>) rObjRes.getResObj();
			if(envlist != null) {
				for(ZappEnv vo : envlist) {
					rZappEnv = vo; break;
				}
			}
			if(rZappEnv == null) {
				rZappEnv = new ZappEnv();
				rZappEnv.setSetval(NO);
			}			
			
			if(rZappEnv.getSetval().equals(YES)) {
				
				Map<String, String> pContentMap = new HashMap<String, String>();
				Map<String, String> pResMap = new HashMap<String, String>();
				
				// 보존 기간 만료 문서 조회 (Bundle)
				ZappBundle pZappBundle_Filter = new ZappBundle();
				pZappBundle_Filter.setExpiretime(Operators.LESS_THAN.operator);
				ZappBundle pZappBundle_Value = new ZappBundle();
				pZappBundle_Value.setExpiretime(ZstFwDateUtils.getToday());
				pZappBundle_Value.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				rObjRes = contentService.selectObject(pZappAuth, pZappBundle_Filter, pZappBundle_Value, rObjRes);
				if(ZappFinalizing.isSuccess(rObjRes) == false) {
					
				}
				List<ZappBundle> rZappBundleList = (List<ZappBundle>) rObjRes.getResObj();
				if(rZappBundleList != null) {
					for(ZappBundle vo : rZappBundleList) {
						pContentMap.put(vo.getBundleid(), ZappConts.TYPES.CONTENT_BUNDLE.type);
					}
				}
				
				// 보존 기간 만료 문서 조회 (File)
				ZappFile pZappFile_Filter = new ZappFile();
				pZappFile_Filter.setExpiretime(Operators.LESS_THAN.operator);
				ZappFile pZappFile_Value = new ZappFile();
				pZappFile_Value.setExpiretime(ZstFwDateUtils.getToday());
				pZappFile_Value.setState(ZappConts.STATES.BUNDLE_NORMAL.state);
				rObjRes = contentService.selectObject(pZappAuth, pZappFile_Filter, pZappFile_Value, rObjRes);
				if(ZappFinalizing.isSuccess(rObjRes) == false) {
					
				}
				List<ZappFile> rZappFileList = (List<ZappFile>) rObjRes.getResObj();
				if(rZappFileList != null) {
					for(ZappFile vo : rZappFileList) {
						pContentMap.put(vo.getMfileid(), ZappConts.TYPES.CONTENT_FILE.type);
					}
				}
				
				// 만료 문서 폐기
				for (Map.Entry<String, String> entry : pContentMap.entrySet()) {
					
					rObjRes.setResCode(SUCCESS);
					ZappContentPar pIn = new ZappContentPar();
					pIn.setContentid(entry.getKey());
					pIn.setObjType(entry.getValue());
					pIn.setObjTaskid("94EE059335E587E501CC4BF90613E0814F00A7B08BC7C648FD865A2AF6A22CC2");
					rObjRes = contentService.discardContentForcely(pZappAuth, pIn, rObjRes);
					if(ZappFinalizing.isSuccess(rObjRes) == false) {
						pResMap.put(pIn.getObjType() + "-" + pIn.getContentid(), "FAIL");
						continue;
					}
					pResMap.put(pIn.getObjType() + "-" + pIn.getContentid(), "SUCCESS");
					
					// Log
					Map<String, Object> cmap = new HashMap<String, Object>();
					cmap.put("ID", entry.getKey()); 
					cmap.put("BUNDLE", rZappBundleList); 
					cmap.put("FILE", rZappFileList);
					ZappCycleLog pZappCycleLog = new ZappCycleLog();
					pZappCycleLog.setCompanyid(vo_company.getCompanyid());
					pZappCycleLog.setCycletype(ZappConts.LOGS.TYPE_DISCARD_EXPIRED_CONTENT.log);
					pZappCycleLog.setCycletime(ZstFwDateUtils.getToday());
					pZappCycleLog.setMapcyclelogs(cmap);
//					pZappCycleLog.setCycleid(ZappKey.getPk(pZappCycleLog));
					logService.leaveLog(pZappAuth, pZappCycleLog, rObjRes);
				}	
				
				pLogMap.put("[OBJECT]", pResMap);
				rObjRes = leaveLog(pZappAuth, ZappConts.LOGS.TYPE_DISCARD_EXPIRED_CONTENT.log, PROC_TIME, pLogMap, rObjRes);

			}
		}
		
	}	
	
	@SuppressWarnings("unchecked")
	@Scheduled(cron = "0 0 1 ? * *")	// 매일 1시
	public void buildDailyStatics() throws ZappException, SQLException {
		
		ZstFwResult rObjRes = new ZstFwResult(); rObjRes.setResCode(SUCCESS);
		ZappAuth pZappAuth = new ZappAuth();
		String PROC_TIME = ZstFwDateUtils.getNow();
		Map<String, Object> pLogMap = new HashMap<String, Object>();
		
		/* [기관 정보 조회]
		 * 
		 */
		List<ZappCompany> companylist = getCompanyList(pZappAuth, rObjRes);
		for(ZappCompany vo_company : companylist) {
			pZappAuth.setObjCompanyid(vo_company.getCompanyid());
			
			// 환결 설정 정보 조회 (통계정보생성유형)
			ZappEnv pZappEnv = new ZappEnv(); ZappEnv rZappEnv = null; List<ZappEnv> rZappEnvList = null;
			pZappEnv.setCompanyid(vo_company.getCompanyid());
			pZappEnv.setEnvtype(ZappConts.ENVS.CYCLE_STATUS_BUILD_TYPE.type);
			pZappEnv.setEnvkey(ZappConts.ENVS.CYCLE_STATUS_BUILD_TYPE.env);
//			pZappEnv.setEnvid(ZappKey.getPk(pZappEnv));
			rObjRes = systemService.selectObject(pZappAuth, pZappEnv, rObjRes);		
			if(ZappFinalizing.isSuccess(rObjRes) == false) {
				rZappEnv = new ZappEnv();
				rZappEnv.setSetval(ZappConts.TYPES.JOBTYPE_REALTIME.type);
			}
			rZappEnvList = (List<ZappEnv>) rObjRes.getResObj();
			if(rZappEnvList == null) {
				rZappEnv = new ZappEnv();
				rZappEnv.setSetval(ZappConts.TYPES.JOBTYPE_REALTIME.type);
			}
			for(ZappEnv vo : rZappEnvList) {
				rZappEnv = vo;
			}
			
			// 일자별 통계 정보 호출 (프로시져)
			if(rZappEnv.getSetval().equals(ZappConts.TYPES.JOBTYPE_BATCH.type)) {
				
				Map<String, Object> pObjw = new HashMap<String, Object>();
				pObjw.put("pCompanyid", vo_company.getCompanyid());
				pObjw.put("pDate", getPrevDate());
				rObjRes = cycleService.callDailyStatics(pZappAuth, pObjw, rObjRes);
				
				rObjRes = leaveLog(pZappAuth, ZappConts.LOGS.TYPE_BUILD_DAILY_STATICS.log, PROC_TIME, pLogMap, rObjRes);

			}
		}
		
	}	
	
	/**
	 * 매년 1월 1일에 시쿼스를 리셋한다.
	 */
//	@Scheduled(cron = "0 0 1 1 1 ? ")	// 매년 1월 1일 1시에 실행
	public void resetSeq() throws ZappException, SQLException {
	
		ZappAuth pZappAuth = new ZappAuth();
		ZstFwResult pObjRes = new ZstFwResult();
		
		// Inquiry company list
		List<ZappCompany> rZappCompanyList = getCompanyList(pZappAuth, pObjRes);
		
		if(rZappCompanyList != null) {
			for(ZappCompany vo : rZappCompanyList) {
				commonService.updateSeq(pZappAuth, "C", vo.getCode());
			}
		}
		 
	}
	
	// ### 일자 구하기 ###
	private String getPrevDate() {

	    Calendar day = Calendar.getInstance();
	    day.add(Calendar.DATE , -1);
	    return  new java.text.SimpleDateFormat("yyyy-MM-dd").format(day.getTime());

	}
	
	
	// ### 기관 정보 조회 ###
	@SuppressWarnings("unchecked")
	private List<ZappCompany> getCompanyList(ZappAuth pZappAuth, ZstFwResult pObjRes) {
		
		List<ZappCompany> list = new ArrayList<ZappCompany>();
		
		ZappCompany pvo = new ZappCompany();
		pvo.setIsactive(YES);
		try {
			pObjRes = organService.selectObject(pZappAuth, pvo, pObjRes);
		} catch (ZappException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(ZappFinalizing.isSuccess(pObjRes) == false) {
			return list;
		}
		
		list = (List<ZappCompany>) pObjRes.getResObj(); 
		
		return list;
		
	}
	
	// ### 로그 ###
	private ZstFwResult leaveLog(ZappAuth pObjAuth, String pLogType, String pLogTime, Map<String, Object> pLogMap, ZstFwResult pObjRes) {
		
		List<ZappCycleLog> pLogObjectList = new ArrayList<ZappCycleLog>();
		ZappCycleLog pLogObject = new ZappCycleLog();
		pLogObject.setCompanyid(pObjAuth.getObjCompanyid());
		pLogObject.setCycletype(pLogType);
		pLogObject.setMapcyclelogs(pLogMap);
		pLogObject.setCycletime(pLogTime);												// 로그시간
		pLogObjectList.add(pLogObject);
		try {
			pObjRes = logService.leaveLog(pObjAuth, pLogObjectList, pObjRes);
		} catch (ZappException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return pObjRes;
	}
	
	/* ************************************************************************************************** */
	
	/**
	 * APM 관련 정보를 수집하여 db 에 등록한다.
	 * @throws SQLException 
	 * @throws ZappException 
	 */
	//@Scheduled(cron = "#{archiveconfig['APM_CRON']}")	// 매 2분마다
	public void collectApm() throws ZappException, SQLException {
		
		////String NOW = ZstFwDateUtils.getNow();
		ZappAuth pZappAuth = new ZappAuth();
		ZstFwResult rZstFwResult = new ZstFwResult(); rZstFwResult.setResCode(ZstFwConst.RESULTS.SUCCESS.result);

		logger.info("[APM 정보 수집 배치 시작] - " + ZstFwDateUtils.getNow());
		
		
		logger.info("MacAddress : " + getLocalMacAddress());
		
		String macAddr = getLocalMacAddress();
		
		
		List<ZappApm> pList = new ArrayList<ZappApm>();
		
		/* [DB 현황]
		 * 
		 */
		ZappApm pZappApm = new ZappApm();
		pZappApm.setApmtype(ZappConts.TYPES.APM_DB.type);
		pZappApm.setApm(getDbStatus(rZstFwResult));
		pZappApm.setApmtime(ZstFwDateUtils.getNow());
		pZappApm.setApmmacadd(macAddr);
		
		String apmidDB = ZappKey.getPk(pZappApm);
		logger.info("apmidDB : " + apmidDB);
		
		pZappApm.setApmid(apmidDB);	
		pList.add(pZappApm);
		
		/* [DB Lock]
		 * 
		 */
		if(ZstFwValidatorUtils.valid(getDbLockStatus(rZstFwResult))) {
			pZappApm = new ZappApm();
			pZappApm.setApmtype(ZappConts.TYPES.APM_DB_Lock.type);
			pZappApm.setApm(getDbLockStatus(rZstFwResult));
			pZappApm.setApmtime(ZstFwDateUtils.getNow());
			pZappApm.setApmmacadd(macAddr);
			
			String apmidDBLock = ZappKey.getPk(pZappApm);
			logger.info("apmidDBLock : " + apmidDBLock);
			
			pZappApm.setApmid(apmidDBLock);
			
			pList.add(pZappApm);
		}
		
		/* [OS 현황]
		 * 
		 */
		pZappApm = new ZappApm();
		pZappApm.setApmtype(ZappConts.TYPES.APM_OS.type);
		pZappApm.setApm(getOsStatus());
		pZappApm.setApmtime(ZstFwDateUtils.getNow());
		pZappApm.setApmmacadd(macAddr);

		
		String apmidOS = ZappKey.getPk(pZappApm);
		logger.info("apmidOS : " + apmidOS);
		
		pZappApm.setApmid(apmidOS);
		
		pList.add(pZappApm);
		
		/* [DISK 현황]
		 * 
		 */
		pZappApm = new ZappApm();
		pZappApm.setApmtype(ZappConts.TYPES.APM_DISK.type);
		pZappApm.setApm(getDiskStatus());
		pZappApm.setApmtime(ZstFwDateUtils.getNow());
		pZappApm.setApmmacadd(macAddr);
		
		String apmidDISK = ZappKey.getPk(pZappApm);
		logger.info("apmidDISK : " + apmidDISK);
		
		pZappApm.setApmid(apmidDISK);
		
		pList.add(pZappApm);
		
		/*
		 * 
		 */
		if(pList.size() > 0) {
			
//			for(ZappApm vo : pList) {
//				try {
////					Map<String, Object> map = vo.toMap();
////					for (Map.Entry<String, Object> entry : map.entrySet())  
////			            System.out.println("Key = " + entry.getKey() + 
////			                             ", Value = " + (String) entry.getValue()); 
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
			
			try {
				ZstFwResult rst =  statusService.cMultiRows(pZappAuth, pList, rZstFwResult);
				//logger.info("rst:" + rst.getStatus());
				//logger.info("rst:" + rst.getMessage());
				
			} catch (ZappException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		logger.info("[APM 정보 수집 배치 종료] - " + ZstFwDateUtils.getNow());
		
	}
	
	/**
	 * 데이터베이스 현황 정보를 조회한다
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 */
	@SuppressWarnings("unchecked")
	private String getDbStatus(ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZappApm pIn = new ZappApm();
		String result = Characters.BLANK.character;
		ZappAuth pZappAuth = new ZappAuth();
		
		String[] objId = APM_DB_SCHEME.split(Characters.COMMA.character);
		if(objId != null) {
			pIn.setObjDbList(Arrays.asList(objId));
		}
		
		List<ZappApmExtend> list = null;
		
		pObjRes = statusService.getDbStatus(pZappAuth, pIn, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == true) {
			list = (List<ZappApmExtend>) pObjRes.getResObj();
		}

		if(list != null) {
			ObjectMapper Obj = new ObjectMapper();
			
			try {
				result = Obj.writeValueAsString(list);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
		return result;
		
	}
	
	/**
	 * Lock 쿼리 정보를 조회한다
	 * @return
	 * @throws SQLException 
	 * @throws ZappException 
	 */
	@SuppressWarnings("unchecked")
	private String getDbLockStatus(ZstFwResult pObjRes) throws ZappException, SQLException {
		
		ZappApm pIn = new ZappApm();
		String result = Characters.BLANK.character;
		ZappAuth pZappAuth = new ZappAuth();
		
		String[] objId = APM_DB_SCHEME.split(Characters.COMMA.character);
		if(objId != null) {
			pIn.setObjDbList(Arrays.asList(objId));
		}
		
		List<Object> list = null;
		pObjRes = statusService.getDbLock(pZappAuth, pIn, pObjRes);
		if(ZappFinalizing.isSuccess(pObjRes) == true) {
			list = (List<Object>) pObjRes.getResObj();
		}

		if(list != null) {
			ObjectMapper Obj = new ObjectMapper();
			
			try {
				result = Obj.writeValueAsString(list);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
		return result;
		
	}
	
	/**
	 * OS 현황 정보를 조회한다.
	 * @return
	 */
	private String getOsStatus() {
		
		String result = Characters.BLANK.character;
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
		
		if(osmap != null) {
			ObjectMapper Obj = new ObjectMapper();
			
			try {
				result = Obj.writeValueAsString(osmap);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
		return result;
		
	}
	
	/**
	 * 디스크 상태를 조회한다.
	 * @return
	 */
	private String getDiskStatus() {
		
		String result = Characters.BLANK.character; 
		String OS = System.getProperty("os.name").toLowerCase();
		
		/** [Unix]
		 * 
		 */
		if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 || OS.indexOf("sunos") > 0) {
			
			Map<String, String> dmap = new HashMap<String, String>();
			
			if(ZstFwValidatorUtils.valid(APM_DISK_PATH)) {
				
				Process proc = null;
				String str = "";
				StringBuffer sb = new StringBuffer();
				BufferedReader br = null;
			
				// Command
				String cmd_t = "df " + APM_DISK_PATH;
				String cmd_f = "du -sh " + APM_DISK_PATH;
				
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
			
			if(dmap != null) {
				ObjectMapper Obj = new ObjectMapper();
				
				try {
					result = Obj.writeValueAsString(dmap);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			
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
			
			if(dmap != null) {
				ObjectMapper Obj = new ObjectMapper();
				
				try {
					result = Obj.writeValueAsString(dmap);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return result;
	}
	
	//맥 주소를 가져오는 메소드
	public String getLocalMacAddress() {
		String result = "";
		InetAddress ip;

		try {
			ip = InetAddress.getLocalHost();

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			byte[] mac = network.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			result = sb.toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		return result;
	}
	
}

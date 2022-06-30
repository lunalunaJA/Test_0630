package com.zenithst.core.common.utility;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import com.sun.management.OperatingSystemMXBean;
import com.zenithst.core.status.vo.ZappSystemResource;
import com.zenithst.archive.util.DateUtil;

/**
 * 시스템 정보를 조회한다.
 * @author jerido
 *
 */
public class ZappSystemResourceUtil {
	
	private static List<ZappSystemResource> cacheSystemResourceList;
	
	/**
	 * 캐쉬에 조회 정보를 등록한다.
	 * @return
	 */
	public static synchronized void addSysteminfo(){
		if(null==cacheSystemResourceList) cacheSystemResourceList = new ArrayList<ZappSystemResource>();
		if(cacheSystemResourceList.size()<60){
			cacheSystemResourceList.add(getSysteminfo());
		}else{
			//먼저 입력된 값을 삭제
			cacheSystemResourceList.remove(cacheSystemResourceList.get(0));
			cacheSystemResourceList.add(getSysteminfo());
		}
	}
	
	public static synchronized List<ZappSystemResource> getSystemInfoList(){
		if(null==cacheSystemResourceList) addSysteminfo();
		return cacheSystemResourceList;
	}
	
	/**
	 * 시스템 사용정보를 조회한다.
	 * @return
	 */
	public static ZappSystemResource getSysteminfo(){

		ZappSystemResource resource = new ZappSystemResource();
		long totalMemory = Runtime.getRuntime().totalMemory()/1024/1024;
		long freeMemory = Runtime.getRuntime().freeMemory()/1024/1024;
		long maxMemory = Runtime.getRuntime().maxMemory()/1024/1024;
		int processor =Runtime.getRuntime().availableProcessors();
		


		OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
		double load = 0 ;
		while(true){	     
			load = osBean.getSystemCpuLoad();
			try { 
				Thread.sleep(10); 
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			if( load*100.0 > 0.0) break;
		}	

		String cpuUsagePercent = Math.round(load*100.0) + "";
		String cpuIdlePercent  = (100 - Math.round(load*100.0)) + "";

		resource.setTotalMemory(totalMemory);
		resource.setFreeMemory(freeMemory);
		resource.setMaxMemory(maxMemory);
		resource.setProcessor(processor);
		resource.setUsedMemory(totalMemory-freeMemory);
		resource.setCpuUsagePercent(cpuUsagePercent);
		resource.setCpuIdlePercent(cpuIdlePercent);
		resource.setCheckTime(DateUtil.getCurrentTime());
		return resource;
	}

}

package com.zenithst.core.status.vo;

public class ZappSystemResource {

	private long totalMemory;
	
	private long freeMemory;
	
	private long maxMemory;
	
	private int processor;
	
	private String cpuUsagePercent;
	
	private String cpuIdlePercent;
	
	private String memoryUsagePercent;
	
	private String memoryIdlepercent;
	
	private String checkTime;
	
	private long usedMemory;

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public int getProcessor() {
		return processor;
	}

	public void setProcessor(int processor) {
		this.processor = processor;
	}

	public String getCpuUsagePercent() {
		return cpuUsagePercent;
	}

	public void setCpuUsagePercent(String cpuUsagePercent) {
		this.cpuUsagePercent = cpuUsagePercent;
	}

	public String getCpuIdlePercent() {
		return cpuIdlePercent;
	}

	public void setCpuIdlePercent(String cpuIdlePercent) {
		this.cpuIdlePercent = cpuIdlePercent;
	}

	public String getMemoryUsagePercent() {
		return memoryUsagePercent;
	}

	public void setMemoryUsagePercent(String memoryUsagePercent) {
		this.memoryUsagePercent = memoryUsagePercent;
	}

	public String getMemoryIdlepercent() {
		return memoryIdlepercent;
	}

	public void setMemoryIdlepercent(String memoryIdlepercent) {
		this.memoryIdlepercent = memoryIdlepercent;
	}

	public String getCheckTime() {
		return checkTime;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}
	
	
	
	
}


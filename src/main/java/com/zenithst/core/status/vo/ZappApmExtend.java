package com.zenithst.core.status.vo;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for extended APM <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappApmExtend pIn = new ZappApmExtend();
*    ...

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappApmExtend {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * Operating System
	 */
	private String ostarget;	/* 조회 대상 */
	private double osused;		/* 사용량 */
	private double osfree;		/* 남은량 */
	private Integer osratio;	/* 사용 비율 */
	
	/*
	 * Database
	 */
	private String dbscheme;		/* 스키마 */
    private String dbused;			/* 사용량 */
    private String dbtotal;			/* 전체량 */
    private Integer dbratio;		/* 사용 비율 */
    
    /*
     * Disk
     */
    private String diskpath;
    
    /**
	 * <p><b>
	 * [OUT] Target OS
	 * </b></p>
	 * 
     * @return Target OS
     */
	public String getOstarget() {
		return ostarget;
	}
	
    /**
	 * <p><b>
	 * [IN] Target OS
	 * </b></p>
     *
     * @param ostarget Target OS
     */
	public void setOstarget(String ostarget) {
		this.ostarget = ostarget;
	}
	
    /**
	 * <p><b>
	 * [OUT] Used OS
	 * </b></p>
	 * 
     * @return Used OS
     */
	public double getOsused() {
		return osused;
	}
	
    /**
	 * <p><b>
	 * [IN] Used OS
	 * </b></p>
     *
     * @param osused Used OS
     */	
	public void setOsused(double osused) {
		this.osused = osused;
	}
	
    /**
	 * <p><b>
	 * [OUT] Free OS
	 * </b></p>
	 * 
     * @return Free OS
     */
	public double getOsfree() {
		return osfree;
	}
	
    /**
	 * <p><b>
	 * [IN] Free OS
	 * </b></p>
     *
     * @param osfree Free OS
     */	
	public void setOsfree(double osfree) {
		this.osfree = osfree;
	}
	
    /**
	 * <p><b>
	 * [OUT] OS Ratio
	 * </b></p>
	 * 
     * @return OS Ratio
     */
	public Integer getOsratio() {
		return osratio;
	}
	
    /**
	 * <p><b>
	 * [IN] OS Ratio
	 * </b></p>
     *
     * @param osratio OS Ratio
     */	
	public void setOsratio(Integer osratio) {
		this.osratio = osratio;
	}
	
    /**
	 * <p><b>
	 * [OUT] DB Scheme
	 * </b></p>
	 * 
     * @return DB Scheme
     */	
	public String getDbscheme() {
		return dbscheme;
	}
	
    /**
	 * <p><b>
	 * [IN] DB Scheme
	 * </b></p>
     *
     * @param dbscheme DB Scheme
     */		
	public void setDbscheme(String dbscheme) {
		this.dbscheme = dbscheme;
	}
	
    /**
	 * <p><b>
	 * [OUT] Used DB
	 * </b></p>
	 * 
     * @return Used DB
     */	
	public String getDbused() {
		return dbused;
	}
	
    /**
	 * <p><b>
	 * [IN] Used DB
	 * </b></p>
     *
     * @param dbused Used DB
     */		
	public void setDbused(String dbused) {
		this.dbused = dbused;
	}
	
    /**
	 * <p><b>
	 * [OUT] Total DB
	 * </b></p>
	 * 
     * @return Total DB
     */	
	public String getDbtotal() {
		return dbtotal;
	}
	
    /**
	 * <p><b>
	 * [IN] Total DB
	 * </b></p>
     *
     * @param dbtotal Total DB
     */	
	public void setDbtotal(String dbtotal) {
		this.dbtotal = dbtotal;
	}
	
    /**
	 * <p><b>
	 * [OUT] DB Ratio
	 * </b></p>
	 * 
     * @return DB Ratio
     */
	public Integer getDbratio() {
		return dbratio;
	}
	
    /**
	 * <p><b>
	 * [IN] DB Ratio
	 * </b></p>
     *
     * @param dbratio DB Ratio
     */		
	public void setDbratio(Integer dbratio) {
		this.dbratio = dbratio;
	}
	
    /**
	 * <p><b>
	 * [OUT] Disk Path
	 * </b></p>
	 * 
     * @return Disk Path
     */	
	public String getDiskpath() {
		return diskpath;
	}
	
    /**
	 * <p><b>
	 * [IN] Disk Path
	 * </b></p>
     *
     * @param diskpath Disk Path
     */		
	public void setDiskpath(String diskpath) {
		this.diskpath = diskpath;
	}
	
}

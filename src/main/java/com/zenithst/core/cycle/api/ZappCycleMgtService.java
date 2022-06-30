package com.zenithst.core.cycle.api;

import java.sql.SQLException;

import com.zenithst.core.common.exception.ZappException;


public interface ZappCycleMgtService {
	
	void discardExpiredContent() throws ZappException, SQLException;
	
	void buildDailyStatics() throws ZappException, SQLException;
	
	void collectApm() throws ZappException, SQLException;
	
	void resetSeq() throws ZappException, SQLException;
	
}

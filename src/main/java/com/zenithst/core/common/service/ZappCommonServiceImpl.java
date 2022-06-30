package com.zenithst.core.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.mapper.ZappCommonMapper;
import com.zenithst.core.common.vo.ZappCommon;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define common processes. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappCommonService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappCommonService")
public class ZappCommonServiceImpl extends ZappService implements ZappCommonService {

	/* Mapper */
	@Autowired
	private ZappCommonMapper commonMapper;		// 공통

	public ZstFwResult existDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {

		return pObjRes;
	}
	
	public ZstFwResult createDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {

		return pObjRes;
	}
	
	public ZstFwResult alterDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {

		return pObjRes;
	}
	
	public ZstFwResult dropDdl(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {

		return pObjRes;
	}

	public boolean existDdl(ZappAuth pObjAuth, Object pObj)
			throws ZappException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean createDdl(ZappAuth pObjAuth, Object pObj)
			throws ZappException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean alterDdl(ZappAuth pObjAuth, Object pObj)
			throws ZappException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean dropDdl(ZappAuth pObjAuth, Object pObj) throws ZappException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public List<ZappCommon> usingOtherTable(ZappAuth pObjAuth, Map<String, Object> pMap) throws ZappException {
		return commonMapper.usingOtherTable(pMap);
	}
	
	public boolean createSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("objType", pObjType);
		map.put("objCode", pObjCode);
		return commonMapper.createSequence(map) > ZERO ? true : false;
	}
	public boolean updateSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("objType", pObjType);
		map.put("objCode", pObjCode);
		return commonMapper.updateSequence(map) > ZERO ? true : false;
	}	
	public boolean deleteSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("objType", pObjType);
		map.put("objCode", pObjCode);
		return commonMapper.dropSequence(map) > ZERO ? true : false;
	}	
	public int selectSeq(ZappAuth pObjAuth, String pObjType, String pObjCode) throws ZappException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("objType", pObjType);
		map.put("objCode", pObjCode);
		return commonMapper.selectSequence(map);
	}		
}

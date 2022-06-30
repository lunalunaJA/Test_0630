package com.zenithst.core.organ.api;

import java.sql.SQLException;

import com.zenithst.archive.vo.ZArchTask;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.extend.interfaces.ZappMgtService;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappCompanyExtend;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappGroupPar;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.organ.vo.ZappUserExtend;
import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to manage organization info.  <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public interface ZappOrganMgtService extends ZappMgtService {

	ZstFwResult selectObjectExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult selectObjectExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult selectAclObjectExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw1, Object pObjw2, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult selectObjectDown(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult selectObjectUp(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult reorderObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult relocateObject(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult addCompany(ZappAuth pObjAuth, ZappCompanyExtend pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult disableCompany(ZappAuth pObjAuth, ZappCompany pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult discardCompany(ZappAuth pObjAuth, ZappCompany pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult enableCompany(ZappAuth pObjAuth, ZappCompany pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult addDept(ZappAuth pObjAuth, ZappDept pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult disableDept(ZappAuth pObjAuth, ZappDept pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult discardDept(ZappAuth pObjAuth, ZappDept pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult enableDept(ZappAuth pObjAuth, ZappDept pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	
	ZstFwResult addUser(ZappAuth pObjAuth, ZappUserExtend pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult changeUser(ZappAuth pObjAuth, ZappUser pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException;	
	ZstFwResult changeUserPwd(ZappAuth pObjAuth, ZappUser pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException;	
	ZstFwResult disableUser(ZappAuth pObjAuth, ZappUser pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult discardUser(ZappAuth pObjAuth, ZappUser pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult enableUser(ZappAuth pObjAuth, ZappUser pObj, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult changeUsers(ZappAuth pObjAuth, ZappUserExtend pObjUser, ZstFwResult pObjRes) throws ZappException, SQLException;	
	ZstFwResult disableUsers(ZappAuth pObjAuth, ZappUserExtend pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult discardUsers(ZappAuth pObjAuth, ZappUserExtend pObj, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult addGroup(ZappAuth pObjAuth, ZappGroupPar pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult changeGroup(ZappAuth pObjAuth, ZappGroupPar pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult selectGroup(ZappAuth pObjAuth, ZappGroupPar pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult selectGroupByUser(ZappAuth pObjAuth, ZappGroupPar pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult disableGroup(ZappAuth pObjAuth, ZappGroupPar pObj, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult enableGroup(ZappAuth pObjAuth, ZappGroupPar pObjGroup, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult discardGroup(ZappAuth pObjAuth, ZappGroupPar pObj, ZstFwResult pObjRes) throws ZappException, SQLException;

	ZstFwResult addTask(ZappAuth pObjAuth, ZArchTask pObjTask, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult changeTask(ZappAuth pObjAuth, ZArchTask pObjTask, ZstFwResult pObjRes) throws ZappException, SQLException;
	ZstFwResult discardTask(ZappAuth pObjAuth, ZArchTask pObjTask, ZstFwResult pObjRes) throws ZappException, SQLException;
	
}

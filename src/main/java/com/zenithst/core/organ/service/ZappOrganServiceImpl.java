package com.zenithst.core.organ.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.core.authentication.salt.ZappSalt;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.bind.ZappDynamicBinder;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.hash.ZappKey;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappQryOpt;
import com.zenithst.core.organ.bind.ZappOrganBinder;
import com.zenithst.core.organ.mapper.ZappCompanyMapper;
import com.zenithst.core.organ.mapper.ZappDeptMapper;
import com.zenithst.core.organ.mapper.ZappDeptUserMapper;
import com.zenithst.core.organ.mapper.ZappGroupMapper;
import com.zenithst.core.organ.mapper.ZappGroupUserMapper;
import com.zenithst.core.organ.mapper.ZappOrganTaskMapper;
import com.zenithst.core.organ.mapper.ZappUserMapper;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to define basic processes of organization info. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>
* 
*    '@Autowired
*	 private ZappOrganService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappOrganService")
public class ZappOrganServiceImpl extends ZappService implements ZappOrganService {

	/* Mapper */
	@Autowired
	private ZappCompanyMapper companyMapper;		// Company
	@Autowired
	private ZappDeptMapper deptMapper;				// Department
	@Autowired
	private ZappDeptUserMapper deptUserMapper;		// Dept. User
	@Autowired
	private ZappGroupMapper groupMapper;			// Group
	@Autowired
	private ZappGroupUserMapper groupUserMapper;	// Group User
	@Autowired
	private ZappUserMapper userMapper;				// User
	@Autowired
	private ZappOrganTaskMapper organTaskMapper;	// Company Task
	
	/* Binder */
	@Autowired
	private ZappDynamicBinder dynamicBinder;		// Common
	@Autowired
	private ZappOrganBinder utilBinder;				// Organization
	
	/* Service */
	@Autowired
	private ZappMessageMgtService messageService;	// Message
	
	/**
	 * Register new access control info. (Single)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException {
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setCompanyid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappCompany(pvo.getCompanyid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][COMPANY] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				// Insert
				if(companyMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][COMPANY] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getCompanyid());
			}
			if(pObjs instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				pvo.setCompanyid(ZstFwValidatorUtils.valid(pvo.getCompanyid()) ? pvo.getCompanyid() : pObjAuth.getObjCompanyid());
				pvo.setUpid(ZstFwValidatorUtils.valid(pvo.getUpid()) == false ? pObjAuth.getObjCompanyid() : pvo.getUpid());	// Upper ID
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());				// Use or not
				pvo.setDeptid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappDept(pvo.getDeptid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][DEPT] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Priority
				ZappDept pvo_priority = new ZappDept();
				pvo_priority.setUpid(pvo.getUpid());
				pvo.setPriority(deptMapper.selectNextPriority(getWhere(null, pvo_priority)));
				
				// Insert
				if(deptMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][DEPT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getDeptid());
				
			}
			if(pObjs instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				pvo.setUsertype(ZstFwValidatorUtils.valid(pvo.getUsertype()) == false ? ZappConts.TYPES.USERTYPE_NORMAL.type : pvo.getUsertype());	// User type
				pvo.setOriginyn(ZstFwValidatorUtils.valid(pvo.getOriginyn()) == false ? YES : pvo.getOriginyn());	// Original job?
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setIssupervisor(ZstFwValidatorUtils.valid(pvo.getIssupervisor()) == false ? YES : pvo.getIssupervisor());
				pvo.setDeptuserid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappDeptUser(pvo.getDeptuserid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][DEPTUSER] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Exist (Original job?)
				if(rExist(pObjAuth, new ZappDeptUser(null, pvo.getUserid(), pvo.getOriginyn())) == true) {
					return ZappFinalizing.finalising("ERR_DUP_ORGJOB", "[cSingleRow][DEPTUSER] " + messageService.getMessage("ERR_DUP_ORGJOB",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Insert
				if(deptUserMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][DEPTUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getDeptuserid());
			}
			if(pObjs instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setGroupid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappGroup(pvo.getGroupid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][GROUP] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				// Priority
				ZappGroup pvo_priority = new ZappGroup();
				pvo_priority.setUpid(pvo.getUpid());
				pvo.setPriority(groupMapper.selectNextPriority(getWhere(null, pvo_priority)));
				
				// Insert
				if(groupMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][GROUP] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getGroupid());
			}
			if(pObjs instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				pvo.setGroupuserid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappGroupUser(pvo.getGroupuserid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][GROUPUSER] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				// Insert
				if(groupUserMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][GROUPUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getGroupuserid());
			}
			if(pObjs instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				pvo.setCompanyid(ZstFwValidatorUtils.valid(pvo.getCompanyid()) ? pvo.getCompanyid() : pObjAuth.getObjCompanyid());
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo = ZappSalt.generatePasswd(pvo);
				pvo.setUserid(ZappKey.getPk(pvo));
				
				// Exist
				ZappUser pcheckvo = new ZappUser();
				pcheckvo.setCompanyid(pvo.getCompanyid());
				pcheckvo.setEmpno(pvo.getEmpno());
				if(rExist(pObjAuth, pcheckvo) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][USER] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				// Insert
				if(userMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][USER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getUserid());
			}
			if(pObjs instanceof ZappOrganTask) {			// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				pvo.setOrgantaskid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappOrganTask(pvo.getOrgantaskid())) == true) {
					return ZappFinalizing.finalising("ERR_DUP", "[cSingleRow][ORGANTASK] " + messageService.getMessage("ERR_DUP",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				// Insert
				if(organTaskMapper.insert(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ORGANTASK] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				
				pObjRes.setResObj(pvo.getOrgantaskid());
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	
	/**
	 * Register new access control info. (Single)
	 * @param pObjs - Object to be registered
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult cuSingleRow(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException{
		
		if(pObjs != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, null, ZappConts.ACTION.ADD, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjs instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setCompanyid(ZappKey.getPk(pvo));
				if(companyMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][COMPANY] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setDeptid(ZappKey.getPk(pvo));
				if(deptMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][DEPT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setDeptuserid(ZappKey.getPk(pvo));
				if(deptUserMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][DEPTUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setGroupid(ZappKey.getPk(pvo));
				if(groupMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][GROUP] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				pvo.setGroupuserid(ZappKey.getPk(pvo));
				if(groupUserMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][GROUPUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setUserid(ZappKey.getPk(pvo));
				if(userMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][USER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObjs instanceof ZappOrganTask) {			// Dept. User
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				pvo.setOrgantaskid(ZappKey.getPk(pvo));
				if(organTaskMapper.insertu(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_C", "[cSingleRow][ORGANUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cSingleRow] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보를 저장한다. (다건)
	 * @param pObjs - Object to be registered (List<Object>)
	 * @return pObjRes - Result Object
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult cMultiRows(ZappAuth pObjAuth, Object pObjs, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjs != null) {
			
			if(pObjs instanceof List == false) {
				return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			boolean[] checkobj = {false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappCompany) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappDept) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappDeptUser) {
					checkobj[2] = true;
					break;
				}
				if(obj instanceof ZappGroupUser) {
					checkobj[3] = true;
					break;
				}
				if(obj instanceof ZappUser) {
					checkobj[4] = true;
					break;
				}
				if(obj instanceof ZappOrganTask) {
					checkobj[5] = true;
					break;
				}
				if(obj instanceof ZappGroup) {
					checkobj[6] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Company
				List<ZappCompany> list = new ArrayList<ZappCompany>();
				for(Object obj : oObjs) {
					ZappCompany pvo = (ZappCompany) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getCompanyid())) {
						pvo.setCompanyid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(companyMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][COMPANY] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[1] == true) {	// Department
				List<ZappDept> list = new ArrayList<ZappDept>();
				for(Object obj : oObjs) {
					ZappDept pvo = (ZappDept) obj;
					pvo.setCompanyid(pObjAuth.getObjCompanyid());
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getDeptid())) {
						pvo.setDeptid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(deptMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][DEPT] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[2] == true) {	// Dept. User
				List<ZappDeptUser> list = new ArrayList<ZappDeptUser>();
				for(Object obj : oObjs) {
					ZappDeptUser pvo = (ZappDeptUser) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getDeptuserid())) {
						pvo.setDeptuserid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(deptUserMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][DEPTUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[3] == true) {	// Group User
				List<ZappGroupUser> list = new ArrayList<ZappGroupUser>();
				for(Object obj : oObjs) {
					ZappGroupUser pvo = (ZappGroupUser) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getGroupuserid())) {
						pvo.setGroupuserid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(groupUserMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][GROUPUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[4] == true) {	// User
				List<ZappUser> list = new ArrayList<ZappUser>();
				for(Object obj : oObjs) {
					ZappUser pvo = (ZappUser) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getUserid())) {
						pvo.setUserid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(userMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][USER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[5] == true) {	// Company Task
				List<ZappOrganTask> list = new ArrayList<ZappOrganTask>();
				for(Object obj : oObjs) {
					ZappOrganTask pvo = (ZappOrganTask) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getOrgantaskid())) {
						pvo.setOrgantaskid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(organTaskMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][ORGANUSER] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(checkobj[6] == true) {	// Group
				List<ZappGroup> list = new ArrayList<ZappGroup>();
				for(Object obj : oObjs) {
					ZappGroup pvo = (ZappGroup) obj;
					pvo.setCompanyid(pObjAuth.getObjCompanyid());
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getGroupid())) {
						pvo.setGroupid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				params.put("batch", list);
				if(groupMapper.insertb(params) != oObjs.size()) {
					return ZappFinalizing.finalising("ERR_C", "[cMultiRows][GROUP] " + messageService.getMessage("ERR_C",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}			

		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[cMultiRows] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	
	/**
	 * Inquire access control info. (Single)
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.selectByPrimaryKey(pvo.getCompanyid()));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.selectByPrimaryKey(pvo.getDeptid()));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.selectByPrimaryKey(pvo.getDeptuserid()));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.selectByPrimaryKey(pvo.getGroupid()));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.selectByPrimaryKey(pvo.getGroupuserid()));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.selectByPrimaryKey(pvo.getUserid()));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.selectByPrimaryKey(pvo.getOrgantaskid()));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rSingleRow] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	public ZstFwResult rSingleRowExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
//			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
//				pObjRes.setResObj(companyMapper.selectByPrimaryKey(pvo.getCompanyid()));
//			}
//			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
//				pObjRes.setResObj(deptMapper.selectByPrimaryKey(pvo.getDeptid()));
//			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.selectExtendByPrimaryKey(pvo.getDeptuserid()));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.selectExtendByPrimaryKey(pvo.getGroupuserid()));
			}
//			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
//				pObjRes.setResObj(userMapper.selectByPrimaryKey(pvo.getUserid()));
//			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.selectExtendByPrimaryKey(pvo.getOrgantaskid()));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rSingleRow] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	/**
	 * Inquire access control info. (Multiple) 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				
				// Ordering
				pvo = (ZappCompany) mapOrders(pvo);
				
				pObjRes.setResObj(companyMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				
				// Ordering
				pvo = (ZappDept) mapOrders(pvo);
				
				pObjRes.setResObj(deptMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				
				// Ordering
				pvo = (ZappDeptUser) mapOrders(pvo);
				
				pObjRes.setResObj(deptUserMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				
				// Ordering
				pvo = (ZappGroup) mapOrders(pvo);
				
				pObjRes.setResObj(groupMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				
				// Ordering
				pvo = (ZappGroupUser) mapOrders(pvo);
				
				pObjRes.setResObj(groupUserMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				
				// Ordering
				pvo = (ZappUser) mapOrders(pvo);
				
				pObjRes.setResObj(userMapper.selectByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				
				// Ordering
				pvo = (ZappOrganTask) mapOrders(pvo);
				
				pObjRes.setResObj(organTaskMapper.selectByDynamic(getWhere(null, pvo)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	public ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			if(pObjw instanceof ZappDeptUserExtend == false) {
				pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
				if(pObjRes.getResCode().equals(SUCCESS) == false) {
					return pObjRes;
				}
			}
			
			/* Processing */
//			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
//				pObjRes.setResObj(companyMapper.selectByDynamic(getWhere(null, pvo)));
//			}
//			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
//				pObjRes.setResObj(deptMapper.selectByDynamic(getWhere(null, pvo)));
//			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("USERS.EMPNO", "ASC");
					orders.put("POS.PRIORITY", "ASC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}
				pObjRes.setResObj(deptUserMapper.selectExtendByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																	 , getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappDeptUserExtend) {			// Dept. User (통합)
				ZappDeptUserExtend pvo = (ZappDeptUserExtend) pObjw;
				ZappDeptUser pZappDeptUser = new ZappDeptUser();
				BeanUtils.copyProperties(pvo, pZappDeptUser);
				if(utilBinder.isEmptyPk(pZappDeptUser) == true && utilBinder.isEmpty(pZappDeptUser) == true) {
					pZappDeptUser = null;
				}
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("USERS.EMPNO", "ASC");
					orders.put("POS.PRIORITY", "ASC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}
				pObjRes.setResObj(deptUserMapper.selectAllExtendByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																	    , getWhere(null, pvo.getZappDept())
																	    , getWhere(null, pvo.getZappUser())
																	    , getWhere(null, pZappDeptUser)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
//			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
//				pObjRes.setResObj(userMapper.selectByDynamic(getWhere(null, pvo)));
//			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.selectExtendByDynamic(getWhere(null, pvo)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	/**
	 * Inquire access control info. (Multiple) 
	 * @param pObjf - Filter Object 
	 * @param pObjw - Search Criteria Object 
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				
				// Ordering
				pvo = (ZappCompany) mapOrders(pvo);
				
				pObjRes.setResObj(companyMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				
				// Ordering
				pvo = (ZappDept) mapOrders(pvo);
				
				pObjRes.setResObj(deptMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				
				// Ordering
				pvo = (ZappDeptUser) mapOrders(pvo);
				
				pObjRes.setResObj(deptUserMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				
				// Ordering
				pvo = (ZappGroup) mapOrders(pvo);
				
				pObjRes.setResObj(groupMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				
				// Ordering
				pvo = (ZappGroupUser) mapOrders(pvo);
				
				pObjRes.setResObj(groupUserMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				
				// Ordering
				pvo = (ZappUser) mapOrders(pvo);
				
				pObjRes.setResObj(userMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				
				// Ordering
				pvo = (ZappOrganTask) mapOrders(pvo);
				
				pObjRes.setResObj(organTaskMapper.selectByDynamic(getWhere(pObjf, pvo)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	public ZstFwResult rMultiRowsExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			if(pObjw instanceof ZappDeptUserExtend == false) {
				pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
				if(pObjRes.getResCode().equals(SUCCESS) == false) {
					return pObjRes;
				}
			}
			
			/* Processing */
//			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
//				pObjRes.setResObj(companyMapper.selectByDynamic(getWhere(pObjf, pvo)));
//			}
//			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
//				pObjRes.setResObj(deptMapper.selectByDynamic(getWhere(pObjf, pvo)));
//			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("USERS.EMPNO", "ASC");
					orders.put("POS.PRIORITY", "ASC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}
				pObjRes.setResObj(deptUserMapper.selectExtendByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																     , getWhere(pObjf, pvo)));
			}
			if(pObjw instanceof ZappDeptUserExtend) {			// Dept. User (통합)
				ZappDeptUserExtend pvo = (ZappDeptUserExtend) pObjw;
				ZappDeptUser pZappDeptUser = new ZappDeptUser();
				BeanUtils.copyProperties(pvo, pZappDeptUser);
				if(utilBinder.isEmptyPk(pZappDeptUser) == true && utilBinder.isEmpty(pZappDeptUser) == true) {
					pZappDeptUser = null;
				}
				if(pvo.getObjmaporder().size() == ZERO) {
					Map<String, String> orders = new HashMap<String, String>();
					orders.put("USERS.EMPNO", "ASC");
					orders.put("POS.PRIORITY", "ASC");
					pvo.setObjmaporder(orders);
				}
				if(pvo.getObjpgnum() == ZERO) {
					pvo.setObjpgnum(ONE);
				}
				pObjRes.setResObj(deptUserMapper.selectAllExtendByDynamic(new ZappQryOpt(pObjAuth, pvo.getObjnumperpg(), pvo.getObjpgnum(), pvo.getObjmaporder())
																	    , getWhere(null, pvo.getZappDept())
																	    , getWhere(null, pvo.getZappUser())
																	    , getWhere(null, utilBinder.isEmptyPk(pZappDeptUser) == true && utilBinder.isEmpty(pZappDeptUser) == true ? null : pZappDeptUser)));
			}			
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
//			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
//				pObjRes.setResObj(userMapper.selectByDynamic(getWhere(pObjf, pvo)));
//			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}	
	
	
	public ZstFwResult rMultiRowsAclExtend(ZappAuth pObjAuth, Object pObjf, Object pObjw1, Object pObjw2, ZstFwResult pObjRes) throws ZappException, SQLException {

		if(pObjw1 != null && pObjw2 != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw1, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
//			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
//				pObjRes.setResObj(companyMapper.selectByDynamic(getWhere(pObjf, pvo)));
//			}
//			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
//				pObjRes.setResObj(deptMapper.selectByDynamic(getWhere(pObjf, pvo)));
//			}
//			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
//				pObjRes.setResObj(deptUserMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
//			}
			if(pObjw1 instanceof ZappGroupUser && pObjw2 instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo1 = (ZappGroupUser) pObjw1;
				ZappGroupUser pvo2 = (ZappGroupUser) pObjw2;
				pObjRes.setResObj(groupUserMapper.selectExtendAclByDynamic(getWhere(pObjf, pvo1), getWhere(pObjf, pvo2)));
			}
//			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
//				pObjRes.setResObj(userMapper.selectByDynamic(getWhere(pObjf, pvo)));
//			}
//			if(pObjw instanceof ZappOrganTask) {		// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
//				pObjRes.setResObj(organTaskMapper.selectExtendByDynamic(getWhere(pObjf, pvo)));
//			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rMultiRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}		
	
	/**
	 * 조직 정보를 수정한다. (단건) 
	 * @param pObj - Values to edit (PK and others)
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uSingleRow(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObj != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObj, null, ZappConts.ACTION.CHANGE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObj instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObj;
				if(companyMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][COMPANY] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObj;
				if(deptMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][DEPT] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObj;
				if(deptUserMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][DEPTUSER] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObj;
				if(groupMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][GROUP] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObj;
				if(groupUserMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][GROUPUSER] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObj;
				if(ZstFwValidatorUtils.valid(pvo.getPasswd()) == true) {
					pvo = ZappSalt.generatePasswd(pvo);
					pvo.setUserid(ZappKey.getPk(pvo));
				}
				if(userMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][USER] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			if(pObj instanceof ZappOrganTask) {			// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObj;
				if(organTaskMapper.updateByPrimaryKey(pvo) < ONE) {
					return ZappFinalizing.finalising("ERR_E", "[uSingleRow][ORGANTASK] " + messageService.getMessage("ERR_E",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uSingleRow] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Edit access control info. (Multiple) 
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				pObjRes.setResObj(companyMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				pObjRes.setResObj(deptMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				pObjRes.setResObj(deptUserMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				pObjRes.setResObj(groupMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				pObjRes.setResObj(groupUserMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				pObjRes.setResObj(userMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				pObjRes.setResObj(organTaskMapper.updateByDynamic(pvo, getWhere(null, pObjw)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Edit access control info. (Multiple) 
	 * @param pObjf - Filter Object
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to be edited
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, pObjs, pObjw, ZappConts.ACTION.CHANGE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				pObjRes.setResObj(companyMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				pObjRes.setResObj(deptMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				pObjRes.setResObj(deptUserMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				pObjRes.setResObj(groupMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				pObjRes.setResObj(groupUserMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				pObjRes.setResObj(userMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				pObjRes.setResObj(organTaskMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[uMultiRows] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Discard access control info. (Single) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dSingleRow(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE_PK, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.deleteByPrimaryKey(pvo.getCompanyid()));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.deleteByPrimaryKey(pvo.getDeptid()));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.deleteByPrimaryKey(pvo.getDeptuserid()));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.deleteByPrimaryKey(pvo.getGroupid()));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.deleteByPrimaryKey(pvo.getGroupuserid()));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.deleteByPrimaryKey(pvo.getUserid()));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.deleteByPrimaryKey(pvo.getOrgantaskid()));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dSingleRow] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Discard access control info. (Multiple) 
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.deleteByDynamic(getWhere(null, pvo)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.deleteByDynamic(getWhere(null, pvo)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Discard access control info. (Multiple) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Object to be discarded
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.DISABLE, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}			
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.deleteByDynamic(getWhere(pObjf, pObjw)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[dMultiRows] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (다건) 
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.countByDynamic(getWhere(null, pObjw)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.countByDynamic(getWhere(null, pObjw)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (다건) 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.countByDynamic(getWhere(pObjf, pObjw)));
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rCountRows] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다.
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.exists(getWhere(null, pObjw)) == null ? false : true);
			}			
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다. 
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return pObjRes - Result Object
	 */
	public ZstFwResult rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException, SQLException{

		if(pObjw != null) {
			
			/* Validation */
			pObjRes = valid(pObjAuth, null, pObjw, ZappConts.ACTION.VIEW, pObjRes);
			if(pObjRes.getResCode().equals(SUCCESS) == false) {
				return pObjRes;
			}
			
			/* Processing */
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				pObjRes.setResObj(companyMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				pObjRes.setResObj(deptUserMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				pObjRes.setResObj(groupUserMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				pObjRes.setResObj(userMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				pObjRes.setResObj(organTaskMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true);
			}
		}
		else {
			return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[rExist] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
	}
	
	/**
	 * Register new access control info. (Single)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException, SQLException{

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setCompanyid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappCompany(pvo.getCompanyid())) == true) {
					result = false;
				}
				if(companyMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setDeptid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappDept(pvo.getDeptid())) == true) {
					result = false;
				}
				if(deptMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setDeptuserid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappDeptUser(pvo.getDeptuserid())) == true) {
					result = false;
				}
				if(deptUserMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				pvo.setCompanyid(pObjAuth.getObjCompanyid());
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setGroupid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappDept(pvo.getGroupid())) == true) {
					result = false;
				}
				if(groupMapper.insert(pvo) < ONE) {
					result = false;
				}
			}			
			if(pObjs instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				pvo.setGroupuserid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappGroupUser(pvo.getGroupuserid())) == true) {
					result = false;
				}
				if(groupUserMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setUserid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappUser(pvo.getUserid())) == true) {
					result = false;
				}
				if(userMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				pvo.setOrgantaskid(ZappKey.getPk(pvo));
				
				// Exist
				if(rExist(pObjAuth, new ZappOrganTask(pvo.getOrgantaskid())) == true) {
					result = false;
				}
				if(organTaskMapper.insert(pvo) < ONE) {
					result = false;
				}
			}
		}
		else {
			result = false;
		}
		
		return result;
	}
	
	
	/**
	 * Register new access control info. (Single)
	 * @param pObjs - Object to be registered
	 * @return boolean
	 */
	public boolean cuSingleRow(ZappAuth pObjAuth, Object pObjs) throws ZappException, SQLException{

		boolean result = false;
		if(pObjs != null) {
			
			if(pObjs instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setCompanyid(ZappKey.getPk(pvo));
				if(companyMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setDeptid(ZappKey.getPk(pvo));
				if(deptMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setDeptuserid(ZappKey.getPk(pvo));
				if(deptUserMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setGroupid(ZappKey.getPk(pvo));
				if(groupMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				pvo.setGroupuserid(ZappKey.getPk(pvo));
				if(groupUserMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
				pvo.setUserid(ZappKey.getPk(pvo));
				if(userMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
			if(pObjs instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				pvo.setOrgantaskid(ZappKey.getPk(pvo));
				if(organTaskMapper.insertu(pvo) < ONE) {
					result = false;
				}
			}
		}
		else {
			result = false;
		}
		
		return result;
	}	
	
	/**
	 * 조직 정보를 저장한다. (다건)
	 * @param pObjs - Object to be registered (List<Object>)
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public boolean cMultiRows(ZappAuth pObjAuth, Object pObjs) throws ZappException, SQLException{
		

		if(pObjs != null) {
			
			if(pObjs instanceof List == false) {
				return false;
			}
			
			Map<String, Object> params = new HashMap<String, Object>();
			boolean[] checkobj = {false, false, false, false, false, false, false};
			List<Object> oObjs = (List<Object>) pObjs;
			
			for(Object obj : oObjs) {
				if(obj instanceof ZappCompany) {
					checkobj[0] = true;
					break;
				}
				if(obj instanceof ZappDept) {
					checkobj[1] = true;
					break;
				}
				if(obj instanceof ZappDeptUser) {
					checkobj[2] = true;
					break;
				}
				if(obj instanceof ZappGroupUser) {
					checkobj[3] = true;
					break;
				}
				if(obj instanceof ZappUser) {
					checkobj[4] = true;
					break;
				}
				if(obj instanceof ZappOrganTask) {
					checkobj[5] = true;
					break;
				}
				if(obj instanceof ZappGroup) {
					checkobj[6] = true;
					break;
				}
			}
			
			if(checkobj[0] == true) {	// Company
				List<ZappCompany> list = new ArrayList<ZappCompany>();
				for(Object obj : oObjs) {
					ZappCompany pvo = (ZappCompany) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getCompanyid())) {
						pvo.setCompanyid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(companyMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[1] == true) {	// Department
				List<ZappDept> list = new ArrayList<ZappDept>();
				for(Object obj : oObjs) {
					ZappDept pvo = (ZappDept) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getDeptid())) {
						pvo.setDeptid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(deptMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[2] == true) {	// Dept. User
				List<ZappDeptUser> list = new ArrayList<ZappDeptUser>();
				for(Object obj : oObjs) {
					ZappDeptUser pvo = (ZappDeptUser) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getDeptuserid())) {
						pvo.setDeptuserid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(deptUserMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[3] == true) {	// Group User
				List<ZappGroupUser> list = new ArrayList<ZappGroupUser>();
				for(Object obj : oObjs) {
					ZappGroupUser pvo = (ZappGroupUser) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getGroupuserid())) {
						pvo.setGroupuserid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(groupUserMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[4] == true) {	// User
				List<ZappUser> list = new ArrayList<ZappUser>();
				for(Object obj : oObjs) {
					ZappUser pvo = (ZappUser) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getUserid())) {
						pvo.setUserid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(userMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[5] == true) {	// Company Task
				List<ZappOrganTask> list = new ArrayList<ZappOrganTask>();
				for(Object obj : oObjs) {
					ZappOrganTask pvo = (ZappOrganTask) obj;
					if(!ZstFwValidatorUtils.valid(pvo.getOrgantaskid())) {
						pvo.setOrgantaskid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(organTaskMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}
			if(checkobj[6] == true) {	// Group
				List<ZappGroup> list = new ArrayList<ZappGroup>();
				for(Object obj : oObjs) {
					ZappGroup pvo = (ZappGroup) obj;
					pvo.setIsactive(ZstFwValidatorUtils.valid(pvo.getIsactive()) == false ? YES : pvo.getIsactive());
					if(!ZstFwValidatorUtils.valid(pvo.getGroupid())) {
						pvo.setGroupid(ZappKey.getPk(pvo));
					}
					list.add(pvo);
				}
				if(groupMapper.insertb(params) != oObjs.size()) {
					return false;
				}
			}

		}
		
		return false;
	}
	
	/**
	 * Inquire access control info. (Single)
	 * @param pObjs - Search Criteria Object
	 * @return Object
	 */
	public Object rSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				return companyMapper.selectByPrimaryKey(pvo.getCompanyid());
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				return deptMapper.selectByPrimaryKey(pvo.getDeptid());
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				return deptUserMapper.selectByPrimaryKey(pvo.getDeptuserid());
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				return groupMapper.selectByPrimaryKey(pvo.getGroupid());
			}			
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				return groupUserMapper.selectByPrimaryKey(pvo.getGroupuserid());
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				return userMapper.selectByPrimaryKey(pvo.getUserid());
			}
			if(pObjw instanceof ZappOrganTask) {		// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				return organTaskMapper.selectByPrimaryKey(pvo.getOrgantaskid());
			}
		}

		return null;
	}
	
	/**
	 * Inquire access control info. (Multiple)
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				return companyMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				return deptMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				return deptUserMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				return groupMapper.selectByDynamic(getWhere(null, pvo));
			}			
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				return groupUserMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				return userMapper.selectByDynamic(getWhere(null, pvo));
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				return organTaskMapper.selectByDynamic(getWhere(null, pvo));
			}
		}
		
		return null;
	}
	
	/**
	 * Inquire access control info. (Multiple)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public List<Object> rMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				return companyMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				return deptMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				return deptUserMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				return groupMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				return groupUserMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				return userMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				return organTaskMapper.selectByDynamic(getWhere(pObjf, pvo));
			}
		}
		
		return null;
	}
	
	/**
	 * Edit access control info. (PK)
	 * @param pObj - Object to search
	 * @return boolean
	 */
	public boolean uSingleRow(ZappAuth pObjAuth, Object pObj) throws ZappException, SQLException{

		if(pObj != null) {
			
			if(pObj instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObj;
				if(companyMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObj;
				if(deptMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObj;
				if(deptUserMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObj;
				if(groupMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObj;
				if(groupUserMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObj;
				if(userMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
			if(pObj instanceof ZappOrganTask) {				// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObj;
				if(organTaskMapper.updateByPrimaryKey(pvo) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Edit access control info. (Multiple)
	 * @param pObjs - Values to edit
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjs, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				if(companyMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				if(deptMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				if(deptUserMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				if(groupMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				if(groupUserMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				if(userMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				if(organTaskMapper.updateByDynamic(pvo, getWhere(null, pObjw)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Edit access control info. (Multiple)
	 * @param pObjs - Values to edit
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean uMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjs, Object pObjw) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjs;
				if(companyMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjs;
				if(deptMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjs;
				if(deptUserMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjs;
				if(groupMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjs;
				if(groupUserMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjs;
				if(userMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjs;
				if(organTaskMapper.updateByDynamic(pvo, getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Discard access control info. (Single)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dSingleRow(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				if(companyMapper.deleteByPrimaryKey(pvo.getCompanyid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				if(deptMapper.deleteByPrimaryKey(pvo.getDeptid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				if(deptUserMapper.deleteByPrimaryKey(pvo.getDeptuserid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				if(groupMapper.deleteByPrimaryKey(pvo.getGroupid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				if(groupUserMapper.deleteByPrimaryKey(pvo.getGroupuserid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				if(userMapper.deleteByPrimaryKey(pvo.getUserid()) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				if(organTaskMapper.deleteByPrimaryKey(pvo.getOrgantaskid()) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Discard access control info. (Multiple)
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
				ZappCompany pvo = (ZappCompany) pObjw;
				if(companyMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDept) {				// Department
				ZappDept pvo = (ZappDept) pObjw;
				if(deptMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				if(deptUserMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroup) {				// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				if(groupMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				if(groupUserMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappUser) {				// User
				ZappUser pvo = (ZappUser) pObjw;
				if(userMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				if(organTaskMapper.deleteByDynamic(getWhere(null, pvo)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Discard access control info. (Multiple)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to discard
	 * @return boolean
	 */
	public boolean dMultiRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException{
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				if(companyMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				if(deptMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				if(deptUserMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				if(groupMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}			
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				if(groupUserMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				if(userMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				if(organTaskMapper.deleteByDynamic(getWhere(pObjf, pObjw)) < ONE) {
					return false;
				}
			}
		}
		
		return false;
	}
	
	public boolean dMultiRowsByCompany(ZappAuth pObjAuth, Object pObjw, String pCompanyid) throws ZappException, SQLException {

		if(pObjw != null) {
			
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
				if(deptUserMapper.deleteByCompany(pCompanyid) < ONE) {
					return false;
				}
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
				if(groupUserMapper.deleteByCompany(pCompanyid) < ONE) {
					return false;
				}
			}
		}
		
		return false;
		
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (단건)
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				return companyMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				return deptMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				return deptUserMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				return groupMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				return groupUserMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				return userMapper.countByDynamic(getWhere(null, pObjw));
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				return organTaskMapper.countByDynamic(getWhere(null, pObjw));
			}
		}
		
		return ZERO;
	}
	
	/**
	 * 조직 정보 건수를 조회한다. (단건)
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return int
	 */
	public int rCountRows(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				return companyMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				return deptMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				return deptUserMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				return groupMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				return groupUserMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				return userMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				return organTaskMapper.countByDynamic(getWhere(pObjf, pObjw));
			}
		}
		
		return ZERO;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다.
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				return companyMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				return deptMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				return deptUserMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				return groupMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				return groupUserMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				return userMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				return organTaskMapper.exists(getWhere(null, pObjw)) == null ? false : true;
			}
		}
		
		return false;
	}
	
	/**
	 * 조직 정보 존재여부를 조회한다.
	 * @param pObjf - Filter object
	 * @param pObjw - Object to search
	 * @return boolean
	 */
	public boolean rExist(ZappAuth pObjAuth, Object pObjf, Object pObjw) throws ZappException, SQLException{

		if(pObjw != null) {
			
			if(pObjw instanceof ZappCompany) {			// Company
//				ZappCompany pvo = (ZappCompany) pObjw;
				return companyMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappDept) {				// Department
//				ZappDept pvo = (ZappDept) pObjw;
				return deptMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappDeptUser) {			// Dept. User
//				ZappDeptUser pvo = (ZappDeptUser) pObjw;
				return deptUserMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappGroup) {				// Group
//				ZappGroup pvo = (ZappGroup) pObjw;
				return groupMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappGroupUser) {		// Group User
//				ZappGroupUser pvo = (ZappGroupUser) pObjw;
				return groupUserMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappUser) {				// User
//				ZappUser pvo = (ZappUser) pObjw;
				return userMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
			if(pObjw instanceof ZappOrganTask) {				// Company Task
//				ZappOrganTask pvo = (ZappOrganTask) pObjw;
				return organTaskMapper.exists(getWhere(pObjf, pObjw)) == null ? false : true;
			}
		}
		
		return false;
	}
	
	/* *************************************************************************************************** */
	
	/**
	 * 조직 정보를 조회한다. (하위)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	public ZstFwResult rMultiRowsDown(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappDept) {			// Department
				ZappDept pvo = (ZappDept) pObjw;
				if(ZstFwValidatorUtils.valid(pvo.getDeptid()) == false
						&& ZstFwValidatorUtils.valid(pvo.getUpid()) == false) {
					pvo.setUpid(pObjAuth.getObjCompanyid());	// All dept.
				}
				pObjRes.setResObj(deptMapper.selectDownByDynamic(pObjAuth, getWhere(pObjf, pvo)));
			}
			
			if(pObjw instanceof ZappGroup) {			// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.selectDownByDynamic(pObjAuth, getWhere(pObjf, pvo)));
			}

		}
		
		return pObjRes;
	}
	
	/**
	 * 조직 정보를 조회한다. (상위)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	public ZstFwResult rMultiRowsUp(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappDept) {			// Department
				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.selectUpByDynamic(pObjAuth, getWhere(pObjf, pvo)));
			}
			
			if(pObjw instanceof ZappGroup) {			// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.selectUpByDynamic(pObjAuth, getWhere(pObjf, pvo)));
			}
		}
		
		return pObjRes;
	}

	/**
	 * 조직 정보를 조회한다. (상위)
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	public ZstFwResult rMultiRowsByUser(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappGroup) {			// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				// [LOG] The scope of the department to be searched when checking access control info.		
				ZappEnv SYS_DEPT_RANGE = (ZappEnv) pObjAuth.getSessEnv().get(ZappConts.ENVS.DEPT_RANGE.env);
				if(SYS_DEPT_RANGE == null) {
					SYS_DEPT_RANGE = new ZappEnv(); SYS_DEPT_RANGE.setSetval("2");
				}
				pObjRes.setResObj(groupMapper.selectByUser(pObjAuth, SYS_DEPT_RANGE, getWhere(pObjf, pvo)));
			}
		}
		
		return pObjRes;
	}
	
	/**
	 * 다음 우선순위를 조회한다.
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult rNextPriority(ZappAuth pObjAuth, Object pObjf, Object pObjw, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjw != null) {
			
			if(pObjw instanceof ZappDept) {			// Department
				ZappDept pvo = (ZappDept) pObjw;
				pObjRes.setResObj(deptMapper.selectNextPriority(getWhere(pObjf, pvo)));
			}
			
			if(pObjw instanceof ZappGroup) {			// Group
				ZappGroup pvo = (ZappGroup) pObjw;
				pObjRes.setResObj(groupMapper.selectNextPriority(getWhere(pObjf, pvo)));
			}
		}
		
		return pObjRes;
	}		

	
	/**
	 * 다음 우선순위를 조회한다.
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult upwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			if(pObjs instanceof ZappDept) {			// Department
				ZappDept pvos = (ZappDept) pObjs;
				ZappDept pvoe = (ZappDept) pObje;
				pObjRes.setResObj(deptMapper.upwardPriority(pvos, pvoe));
			}
			
			if(pObjs instanceof ZappGroup) {			// Group
				ZappGroup pvos = (ZappGroup) pObjs;
				ZappGroup pvoe = (ZappGroup) pObje;
				pObjRes.setResObj(groupMapper.upwardPriority(pvos, pvoe));
			}			

		}
		
		return pObjRes;
	}
	
	/**
	 * 다음 우선순위를 조회한다.
	 * @param pObjf - Filter Object
	 * @param pObjw - Search Criteria Object
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	public ZstFwResult downwardPriority(ZappAuth pObjAuth, Object pObjs, Object pObje, ZstFwResult pObjRes) throws ZappException {
		
		if(pObjs != null) {
			
			if(pObjs instanceof ZappDept) {			// Classifiction
				ZappDept pvos = (ZappDept) pObjs;
				ZappDept pvoe = (ZappDept) pObje;
				pObjRes.setResObj(deptMapper.downwardPriority(pvos, pvoe));
			}
			
			if(pObjs instanceof ZappGroup) {			// Group
				ZappGroup pvos = (ZappGroup) pObjs;
				ZappGroup pvoe = (ZappGroup) pObje;
				pObjRes.setResObj(groupMapper.downwardPriority(pvos, pvoe));
			}	
		}
		
		return pObjRes;
	}
	/**
	 * 
	 */
	public ZstFwResult refreshView(ZappAuth pObjAuth, Object pObj, ZstFwResult pObjRes) throws ZappException {
		
		if(pObj != null) {
			
			if(pObj instanceof ZappGroup) {			// Group
				ZappGroup pvo = (ZappGroup) pObj;
				groupMapper.refreshView(pvo);
			}
			
			if(pObj instanceof ZappGroupUser) {			// GroupUser
				ZappGroupUser pvo = (ZappGroupUser) pObj;
				groupUserMapper.refreshView(pvo);
			}	
		}
		
		return pObjRes;
	}	
	
	/**
	 * Create dynamic conditions.
	 * @param pobjf - Filter
	 * @param pobjw - Values
	 * @return
	 */
	protected ZappDynamic getWhere(Object pobjf, Object pobjw) {
		
		dynamicBinder = new ZappDynamicBinder();
		
		String ALIAS = BLANK;
		ZappDynamic dynamic = null;
		if(pobjw instanceof ZappCompany) {
			ALIAS = ZappConts.ALIAS.COMPANY.alias;
		}
		if(pobjw instanceof ZappDept) {
			ALIAS = ZappConts.ALIAS.DEPT.alias;
		}
		if(pobjw instanceof ZappDeptUser) {
			ALIAS = ZappConts.ALIAS.DEPTUSER.alias;
		}
		if(pobjw instanceof ZappGroup) {
			ALIAS = ZappConts.ALIAS.GROUP.alias;
		}
		if(pobjw instanceof ZappGroupUser) {
			ALIAS = ZappConts.ALIAS.GROUPUSER.alias;
		}
		if(pobjw instanceof ZappUser) {
			ALIAS = ZappConts.ALIAS.USER.alias;
		}
		if(pobjw instanceof ZappOrganTask) {
			ALIAS = ZappConts.ALIAS.ORGANTASK.alias;
		}
		
		try {
			if(pobjw instanceof ZappCompany) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappCompany()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappDept) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappDept()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappDeptUser) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappDeptUser()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappGroup) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappGroup()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappGroupUser) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappGroupUser()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappUser) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappUser()) : pobjf, pobjw, ALIAS);
			}
			if(pobjw instanceof ZappOrganTask) {
				dynamic = dynamicBinder.getWhere(pobjf == null ? utilBinder.getFilter(new ZappOrganTask()) : pobjf, pobjw, ALIAS);
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return dynamic;
	}
	
	/**
	 * Validation
	 * @param pObjs
	 * @param pObjw
	 * @param pObjAct
	 * @param pObjRes
	 * @return
	 */
	private ZstFwResult valid(ZappAuth pObjAuth,  Object pObjs, Object pObjw, ZappConts.ACTION pObjAct, ZstFwResult pObjRes) {
		
		switch(pObjAct) {
			case ADD: 
				if(utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[valid] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE_PK: 
				if(utilBinder.isEmptyPk(pObjs) || utilBinder.isEmpty(pObjs)) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[valid] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case CHANGE: 
				if(utilBinder.isEmpty(pObjs) || (utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw))) {
					return ZappFinalizing.finalising("ERR_MIS_EDTVAL", "[valid] " + messageService.getMessage("ERR_MIS_EDTVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[valid] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case DISABLE: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_DELVAL", "[valid] " + messageService.getMessage("ERR_MIS_DELVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW_PK: 
				if(utilBinder.isEmptyPk(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[valid] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			case VIEW: 
				if(utilBinder.isEmptyPk(pObjw) && utilBinder.isEmpty(pObjw)) {
					return ZappFinalizing.finalising("ERR_MIS_QRYVAL", "[valid] " + messageService.getMessage("ERR_MIS_QRYVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			break;
			default:
		}
		
		return pObjRes;
	}	
	
	
	/**
	 * Convert ordering
	 * @param pIn
	 * @return
	 */
	private Object mapOrders(Object pIn) {
		
		if(pIn instanceof ZappCompany) {
			ZappCompany pvo = (ZappCompany) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}
			
			return pvo;
		}
		if(pIn instanceof ZappDept) {
			ZappDept pvo = (ZappDept) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}
		if(pIn instanceof ZappDeptUser) {
			ZappDeptUser pvo = (ZappDeptUser) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}
		if(pIn instanceof ZappGroupUser) {
			ZappGroupUser pvo = (ZappGroupUser) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}
		if(pIn instanceof ZappUser) {
			ZappUser pvo = (ZappUser) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}
		if(pIn instanceof ZappGroup) {
			ZappGroup pvo = (ZappGroup) pIn;
			
			if(pvo.getObjmaporder() != null) {
				List<String> orders = new ArrayList<String>();
				List<String> orderFields = new ArrayList<String>();
				for (Map.Entry<String,String> entry : pvo.getObjmaporder().entrySet()) {
					orders.add(entry.getValue()); orderFields.add(entry.getKey());
				}
				String[] aorders = new String[orders.size()];
				String[] aorderfields = new String[orderFields.size()];
				pvo.setObjorder(orders.toArray(aorders));
				pvo.setObjorderfield(orderFields.toArray(aorderfields));
			}			
			
			return pvo;			
		}
		
		return pIn;
	}	

}

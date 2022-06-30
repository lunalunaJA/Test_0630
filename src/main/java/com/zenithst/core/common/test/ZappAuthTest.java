package com.zenithst.core.common.test;

import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.classification.vo.ZappClassificationPar;
import com.zenithst.core.content.vo.ZappContentPar;
import com.zenithst.core.content.vo.ZappLinkedObject;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupPar;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.status.vo.ZappStatus;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;

/**  
* <pre>
* <b>
* 1) Description : Utility class for authentication (Test). <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappAuthTest {

	public static ZappAuth getAuth(Object pInObj) {
		
		ZappAuth rZappAuth = new ZappAuth();

		if(pInObj instanceof ZappContentAcl) {
			ZappContentAcl pIn = (ZappContentAcl) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappClassAcl) {
			ZappClassAcl pIn = (ZappClassAcl) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}

		if(pInObj instanceof ZappClassificationPar) {
			ZappClassificationPar pIn = (ZappClassificationPar) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappClassification) {
			ZappClassification pIn = (ZappClassification) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}

		if(pInObj instanceof ZappContentPar) {
			ZappContentPar pIn = (ZappContentPar) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappLinkedObject) {
			ZappLinkedObject pIn = (ZappLinkedObject) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappLinkedObject) {
			ZappLinkedObject pIn = (ZappLinkedObject) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}

		if(pInObj instanceof ZappCompany) {
			ZappCompany pIn = (ZappCompany) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappDept) {
			ZappDept pIn = (ZappDept) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappUser) {
			ZappUser pIn = (ZappUser) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappDeptUser) {
			ZappDeptUser pIn = (ZappDeptUser) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappGroupPar) {
			ZappGroupPar pIn = (ZappGroupPar) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappGroup) {
			ZappGroup pIn = (ZappGroup) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappGroupUser) {
			ZappGroupUser pIn = (ZappGroupUser) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappOrganTask) {
			ZappOrganTask pIn = (ZappOrganTask) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}

		if(pInObj instanceof ZappStatus) {
			ZappStatus pIn = (ZappStatus) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}

		if(pInObj instanceof ZappCode) {
			ZappCode pIn = (ZappCode) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappEnv) {
			ZappEnv pIn = (ZappEnv) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		
		if(pInObj instanceof ZappAccessLog) {
			ZappAccessLog pIn = (ZappAccessLog) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappContentLog) {
			ZappContentLog pIn = (ZappContentLog) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}
		if(pInObj instanceof ZappSystemLog) {
			ZappSystemLog pIn = (ZappSystemLog) pInObj;
			rZappAuth.setObjCompanyid(pIn.getObjTestUser().getObjCompanyid());
			rZappAuth.setObjDeptid(pIn.getObjTestUser().getObjDeptid());
			rZappAuth.setObjEmpno(pIn.getObjTestUser().getObjEmpno());
			rZappAuth.setObjLoginid(pIn.getObjTestUser().getObjLoginid());
			rZappAuth.setObjPasswd(pIn.getObjTestUser().getObjPasswd());
		}

		return rZappAuth;
	}
}

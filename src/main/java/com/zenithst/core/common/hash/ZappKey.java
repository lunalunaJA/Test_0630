package com.zenithst.core.common.hash;

import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappComment;
import com.zenithst.core.content.vo.ZappContentWorkflow;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.content.vo.ZappKeywordObject;
import com.zenithst.core.content.vo.ZappLinkedObject;
import com.zenithst.core.content.vo.ZappLockedObject;
import com.zenithst.core.content.vo.ZappMarkedObject;
import com.zenithst.core.content.vo.ZappSharedObject;
import com.zenithst.core.content.vo.ZappTmpObject;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.log.vo.ZappCycleLog;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.status.vo.ZappApm;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.core.workflow.vo.ZappWorkflowObject;
import com.zenithst.framework.util.ZstFwEncodeUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to generate unique keys. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappKey {

public static String getPk(Object pObj) {
		
		StringBuffer key = new StringBuffer();
		
		/* Company */
		if(pObj instanceof ZappCompany) { 
			ZappCompany vo = (ZappCompany) pObj;
			key.append(vo.getName());		// 1. Company name
			key.append(vo.getCode());		// 2. Code
		}
		
		/* Department */
		if(pObj instanceof ZappDept) { 
			ZappDept vo = (ZappDept) pObj;
			key.append(vo.getCompanyid());	// 1. Company ID
			key.append(vo.getUpid());		// 2. Upper ID
//			key.append(vo.getName());		// 3. Department name
			key.append(vo.getCode());		// 4. Code
		}
		
		/* Dept. User */
		if(pObj instanceof ZappDeptUser) { 
			ZappDeptUser vo = (ZappDeptUser) pObj;
			key.append(vo.getDeptid());		// 1. Department ID
			key.append(vo.getUserid());		// 2. 사용자아이디
//			key.append(vo.getPositionid());	// 3. 직위아이디
//			key.append(vo.getDutyid());		// 4. 직무아이디
//			key.append(vo.getSeclevelid());	// 5. Security level
		}
		
		/* Group */
		if(pObj instanceof ZappGroup) { 
			ZappGroup vo = (ZappGroup) pObj;
			key.append(vo.getCompanyid());	// 1. Company ID
			key.append(vo.getUpid());		// 2. Upper ID
			key.append(vo.getCode());		// 3. Code
			key.append(vo.getTypes());		// 4. Type
		}
		
		/* Group User */
		if(pObj instanceof ZappGroupUser) { 
			ZappGroupUser vo = (ZappGroupUser) pObj;
			key.append(vo.getGroupid());	// 1. Group ID
			key.append(vo.getGobjid());		// 2. 대상아아디
			key.append(vo.getGobjtype());	// 3. Target type
		}
		
		/* User */
		if(pObj instanceof ZappUser) { 
			ZappUser vo = (ZappUser) pObj;
			key.append(vo.getCompanyid());	// 1. Company ID
			key.append(vo.getEmpno());		// 2. Employee number
			key.append(vo.getEmail());		// 3. E-mail
		}
		
		/* Company Task */
		if(pObj instanceof ZappOrganTask) { 
			ZappOrganTask vo = (ZappOrganTask) pObj;
			key.append(vo.getCompanyid());	// 1. Company ID
			key.append(vo.getDeptid());		// 2. Department ID
			key.append(vo.getTaskid());		// 3. Task ID
		}		
		
		/* Classification */
		if(pObj instanceof ZappClassification) { 
			ZappClassification vo = (ZappClassification) pObj;
			key.append(vo.getCompanyid());		// 1. Company
			key.append(vo.getUpid());			// 2. Upper ID
			key.append(vo.getCode());			// 3. Code
			key.append(vo.getTypes());			// 4. Type
		}
		
		/* Bundle */
		if(pObj instanceof ZappBundle) { 
			ZappBundle vo = (ZappBundle) pObj;
			key.append(vo.getBno());			// 1. Bundle no. (문서번호)
			key.append(vo.getCreatorid());		// 2. Creator ID
			key.append(vo.getCreatetime());		// 3. Createtime
		}
		
		/* Comment */
		if(pObj instanceof ZappComment) { 
			ZappComment vo = (ZappComment) pObj;
			key.append(vo.getCobjid());			// 1. Object ID
			key.append(vo.getCobjtype());		// 2. Object Type
			key.append(vo.getCommenttime());	// 3. Comment Time
		}
		
		/* ZappClassObject */
		if(pObj instanceof ZappClassObject) { 
			ZappClassObject vo = (ZappClassObject) pObj;
			key.append(vo.getClassid());			// 1. Classification ID
			key.append(vo.getClasstype());			// 2. Classification type
			key.append(vo.getCobjid());				// 3. Target ID
			key.append(vo.getCobjtype());			// 4. Target type
		}
		
		/* ZappLinkedObject */
		if(pObj instanceof ZappLinkedObject) { 
			ZappLinkedObject vo = (ZappLinkedObject) pObj;
			key.append(vo.getSourceid());			// 1. 소스아이디
			key.append(vo.getTargetid());			// 2. Target ID
			key.append(vo.getLinkerid());			// 3. 링커아이디
			key.append(vo.getLinktype());			// 4. 링크유형
		}	
		
		/* ZappSharedObject */
		if(pObj instanceof ZappSharedObject) { 
			ZappSharedObject vo = (ZappSharedObject) pObj;
			key.append(vo.getSobjid());				// 1. Target ID
			key.append(vo.getSobjtype());			// 2. Target type
			key.append(vo.getSharerid());			// 3. Sharer ID
			key.append(vo.getReaderid());			// 4. Reader ID
		}		
		
		/* ZappLockedObject */
		if(pObj instanceof ZappLockedObject) { 
			ZappLockedObject vo = (ZappLockedObject) pObj;
			key.append(vo.getLobjid());				// 1. Target ID
			key.append(vo.getLobjtype());			// 2. Target type
			key.append(vo.getLockerid());			// 2. Locker ID
		}
		
		if(pObj instanceof ZappKeyword) { 
			ZappKeyword vo = (ZappKeyword) pObj;
			key.append(vo.getKword());				// 1. Keyword
		}
		
		if(pObj instanceof ZappKeywordObject) { 
			ZappKeywordObject vo = (ZappKeywordObject) pObj;
			key.append(vo.getKobjid());				// 1. Target ID
			key.append(vo.getKobjtype());			// 2. Target type
			key.append(vo.getKwordid());			// 3. 키워드아이디
		}
		
		/* ZappMarkedObject */
		if(pObj instanceof ZappMarkedObject) { 
			ZappMarkedObject vo = (ZappMarkedObject) pObj;
			key.append(vo.getMobjid());				// 1. Target ID
			key.append(vo.getMobjtype());			// 2. Target type
			key.append(vo.getMarkerid());			// 3. 마커아이디
		}		
		
		/* ZappTmpObject */
		if(pObj instanceof ZappTmpObject) { 
			ZappTmpObject vo = (ZappTmpObject) pObj;
			key.append(vo.getTobjid());				// 1. Target ID
			key.append(vo.getTobjtype());			// 2. Target type
		}	
		
		/* ZappClassAcl */
		if(pObj instanceof ZappClassAcl) { 
			ZappClassAcl vo = (ZappClassAcl) pObj;
			key.append(vo.getClassid());			// 1. Classification ID
			key.append(vo.getAclobjid());				// 2. Target ID
			key.append(vo.getAclobjtype());			// 3. Target type
		}	
		
		/* ZappContentAcl */
		if(pObj instanceof ZappContentAcl) { 
			ZappContentAcl vo = (ZappContentAcl) pObj;
			key.append(vo.getContentid());			// 1. Content ID
			key.append(vo.getAclobjid());			// 2. Target ID
			key.append(vo.getAclobjtype());			// 3. Target type
		}	
		
		/* ZappContentWorkflow */
		if(pObj instanceof ZappContentWorkflow) { 
			ZappContentWorkflow vo = (ZappContentWorkflow) pObj;
			key.append(vo.getContentid());			// 1. Content ID
			key.append(vo.getContentid());			// 2. Content Type
			key.append(vo.getDrafterid());			// 3. Drafter ID
			key.append(vo.getWferid());				// 4. Approver ID
			key.append(vo.getWftime());				// 5. Execution time
			key.append(vo.getStatus());				// 6. Status
		}

		/*
		 * [시스템]
		 */
		
		/* ZappCode */
		if(pObj instanceof ZappCode) { 
			ZappCode vo = (ZappCode) pObj;
			key.append(vo.getCompanyid());			// 0. Company ID
			key.append(vo.getUpid());				// 1. Upper ID
			key.append(vo.getTypes());				// 3. Type
			key.append(vo.getCodevalue());			// 4. Value
			key.append(vo.getCodekey());			// 5. Code key
		}

		/* ZappEnv */
		if(pObj instanceof ZappEnv) { 
			ZappEnv vo = (ZappEnv) pObj;
			key.append(vo.getCompanyid());			// 1. Company ID
			key.append(vo.getUserid());				// 2. User ID
			key.append(vo.getEnvtype());			// 3. Type
			key.append(vo.getEnvkey());				// 4. Key
		}
		
		/*
		 * [Logging]
		 */
		if(pObj instanceof ZappAccessLog) { 
			ZappAccessLog vo = (ZappAccessLog) pObj;
			key.append(vo.getCompanyid());			// 0. Company ID
			key.append(vo.getLoggerid());			// 1. 로거
			key.append(vo.getLoggername());			// 2. Logger name
			key.append(vo.getLoggerdeptid());		// 3. 로거부서
			key.append(vo.getLoggerdeptname());		// 4. Logger dept. name
			key.append(vo.getLogtype());			// 5. Logging type
			key.append(vo.getAction());				// 6. 처리
			key.append(vo.getLogtime());			// 7. Logging time
			key.append(vo.getLogs());				// 8. Logging info.
		}
		if(pObj instanceof ZappContentLog) { 
			ZappContentLog vo = (ZappContentLog) pObj;
			key.append(vo.getCompanyid());			// 0. Company ID
			key.append(vo.getLoggerid());			// 1. 로거
			key.append(vo.getLoggername());			// 2. Logger name
			key.append(vo.getLoggerdeptid());		// 3. 로거부서
			key.append(vo.getLoggerdeptname());		// 4. Logger dept. name
			key.append(vo.getLogtype());			// 5. Logging type
			key.append(vo.getAction());				// 6. 처리
			key.append(vo.getLogtime());			// 7. Logging time
			key.append(vo.getLogs());				// 8. Logging info.
		}
		if(pObj instanceof ZappSystemLog) { 
			ZappSystemLog vo = (ZappSystemLog) pObj;
			key.append(vo.getCompanyid());			// 0. Company ID
			key.append(vo.getLoggerid());			// 1. 로거
			key.append(vo.getLoggername());			// 2. Logger name
			key.append(vo.getLoggerdeptid());		// 3. 로거부서
			key.append(vo.getLoggerdeptname());		// 4. Logger dept. name
			key.append(vo.getLogtype());			// 5. Logging type
			key.append(vo.getAction());				// 6. 처리
			key.append(vo.getLogtime());			// 7. Logging time
			key.append(vo.getLogs());				// 8. Logging info.
		}
		if(pObj instanceof ZappCycleLog) {
			ZappCycleLog vo = (ZappCycleLog) pObj;
			key.append(vo.getCompanyid());			// 0. Company ID
			key.append(vo.getCycletime());			// 0. Company ID
			key.append(vo.getCycletype());			// 0. Company ID
			key.append(vo.getCyclelogs());			// 0. Company ID
		}
		
		if(pObj instanceof ZappWorkflowObject) {
			ZappWorkflowObject vo = (ZappWorkflowObject) pObj;
			key.append(vo.getContentid());
			key.append(vo.getContenttype());
			key.append(vo.getWferid());
		}
		
		if(pObj instanceof ZappApm) { 
			ZappApm vo = (ZappApm) pObj;
			key.append(vo.getApmtype());		// 1. Type
			key.append(vo.getApmtime());		// 2. Time
			key.append(vo.getApm());			// 3. Content
			key.append(vo.getApmmacadd());      // 4. MacAddress
		}
		
		return ZstFwEncodeUtils.encodeString_SHA256(key.toString());
		
	}
}

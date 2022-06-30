/**
 *
 */
package com.zenithst.core.common.bind;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.DbOperators;
import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.archive.vo.ZArchMFile;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.bind.ZappDynamic.Criteria;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.content.vo.ZappAdditoryBundle;
import com.zenithst.core.content.vo.ZappBundle;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappComment;
import com.zenithst.core.content.vo.ZappContentWorkflow;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.content.vo.ZappKeywordObject;
import com.zenithst.core.content.vo.ZappLinkedObject;
import com.zenithst.core.content.vo.ZappLockedObject;
import com.zenithst.core.content.vo.ZappSharedObject;
import com.zenithst.core.content.vo.ZappTmpObject;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.log.vo.ZappSystemLog;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.core.tag.vo.ZappImg;
import com.zenithst.core.tag.vo.ZappTag;
import com.zenithst.core.tag.vo.ZappTaskTag;
import com.zenithst.core.workflow.vo.ZappWorkflowObject;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to bind to all object <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service
public class ZappDynamicBinder extends ZappDomain {
	
	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/* Fields info. */
	private ConcurrentHashMap<String, String> FIELDS = new ConcurrentHashMap<String, String>();
	
	/**
	 * Bind the input object to ZappDynamic object.
	 * @param Object pFilter - filter
	 * @param Object pValue - values
	 * @return ZappDynamic Bound object
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("rawtypes")
	public ZappDynamic getWhere(Object pFilter, Object pValue, String pAlias) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{

		ZappDynamic dynamic = new ZappDynamic();

		DbOperators filter = null;
		Criteria criteria = dynamic.createCriteria();
		criteria.setAlias(pAlias); 
		
		/* [Fields info.]
		 * Get field information included in the object.
		 */
		getFielMap(pValue);
		
		/* [Processing according to object]
		 * 
		 */
		// Code
		if(pFilter instanceof ZappCode && pValue instanceof ZappCode) {
			ZappCode rFilter = (ZappCode) pFilter; pFilter = rFilter;
			ZappCode rValue = (ZappCode) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"PRIORITY", "NAME"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"ASC", "ASC"};
					rValue.setObjorder(order);
				}
			}
			//else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			//}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}
		// Preferences
		if(pFilter instanceof ZappEnv && pValue instanceof ZappEnv) {
			ZappEnv rFilter = (ZappEnv) pFilter; pFilter = rFilter;
			ZappEnv rValue = (ZappEnv) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"name"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"asc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		
		// Company
		if(pFilter instanceof ZappCompany && pValue instanceof ZappCompany) {
			ZappCompany rFilter = (ZappCompany) pFilter; pFilter = rFilter;
			ZappCompany rValue = (ZappCompany) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"name"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"asc"};
					rValue.setObjorder(order);
				}
			}
//			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
//			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}
		// Department
		if(pFilter instanceof ZappDept && pValue instanceof ZappDept) {
			ZappDept rFilter = (ZappDept) pFilter; pFilter = rFilter;
			ZappDept rValue = (ZappDept) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"PRIORITY", "NAME"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"ASC", "ASC"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}
		// User
		if(pFilter instanceof ZappUser && pValue instanceof ZappUser) {
			ZappUser rFilter = (ZappUser) pFilter; pFilter = rFilter;
			ZappUser rValue = (ZappUser) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"EMPNO", "NAME"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"ASC", "ASC"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}
		
		// Group
		if(pFilter instanceof ZappGroup && pValue instanceof ZappGroup) {
			ZappGroup rFilter = (ZappGroup) pFilter; pFilter = rFilter;
			ZappGroup rValue = (ZappGroup) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"PRIORITY", "NAME"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"ASC", "ASC"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}
		
		// Access Log
		if(pFilter instanceof ZappAccessLog && pValue instanceof ZappAccessLog) {
			ZappAccessLog rFilter = (ZappAccessLog) pFilter; pFilter = rFilter;
			ZappAccessLog rValue = (ZappAccessLog) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"logtime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}
		// Content Log
		if(pFilter instanceof ZappContentLog && pValue instanceof ZappContentLog) {
			ZappContentLog rFilter = (ZappContentLog) pFilter; pFilter = rFilter;
			ZappContentLog rValue = (ZappContentLog) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"logtime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}
		// System Log
		if(pFilter instanceof ZappSystemLog && pValue instanceof ZappSystemLog) {
			ZappSystemLog rFilter = (ZappSystemLog) pFilter; pFilter = rFilter;
			ZappSystemLog rValue = (ZappSystemLog) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"logtime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		
		// Classification access control
		if(pFilter instanceof ZappClassAcl && pValue instanceof ZappClassAcl) {
			ZappClassAcl rFilter = (ZappClassAcl) pFilter; pFilter = rFilter;
			ZappClassAcl rValue = (ZappClassAcl) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"objtype"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"asc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		// Content  access control
		if(pFilter instanceof ZappContentAcl && pValue instanceof ZappContentAcl) {
			ZappContentAcl rFilter = (ZappContentAcl) pFilter; pFilter = rFilter;
			ZappContentAcl rValue = (ZappContentAcl) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"objtype"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"asc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		
		// Classification
		if(pFilter instanceof ZappClassification && pValue instanceof ZappClassification) {
			ZappClassification rFilter = (ZappClassification) pFilter; pFilter = rFilter;
			ZappClassification rValue = (ZappClassification) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"objtype"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"asc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}	
		
		// Bundle
		if(pFilter instanceof ZappBundle && pValue instanceof ZappBundle) {
			ZappBundle rFilter = (ZappBundle) pFilter; pFilter = rFilter;
			ZappBundle rValue = (ZappBundle) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"createtime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}	
		// Comment
		if(pFilter instanceof ZappComment && pValue instanceof ZappComment) {
			ZappComment rFilter = (ZappComment) pFilter; pFilter = rFilter;
			ZappComment rValue = (ZappComment) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"commenttime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		// Content-Classification
		if(pFilter instanceof ZappClassObject && pValue instanceof ZappClassObject) {
			ZappClassObject rFilter = (ZappClassObject) pFilter; pFilter = rFilter;
			ZappClassObject rValue = (ZappClassObject) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"classtype"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"asc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}	
		// Linked content
		if(pFilter instanceof ZappLinkedObject && pValue instanceof ZappLinkedObject) {
			ZappLinkedObject rFilter = (ZappLinkedObject) pFilter; pFilter = rFilter;
			ZappLinkedObject rValue = (ZappLinkedObject) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"linktime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}	
		// Locked content
		if(pFilter instanceof ZappLockedObject && pValue instanceof ZappLockedObject) {
			ZappLockedObject rFilter = (ZappLockedObject) pFilter; pFilter = rFilter;
			ZappLockedObject rValue = (ZappLockedObject) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"locktime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		// Shared content
		if(pFilter instanceof ZappSharedObject && pValue instanceof ZappSharedObject) {
			ZappSharedObject rFilter = (ZappSharedObject) pFilter; pFilter = rFilter;
			ZappSharedObject rValue = (ZappSharedObject) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"sharetime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
	
			// Extra
			dynamic.setObjHasconds(rValue.getObjHasconds());
			
		}		
		// Temporary Info.
		if(pFilter instanceof ZappTmpObject && pValue instanceof ZappTmpObject) {
			ZappTmpObject rFilter = (ZappTmpObject) pFilter; pFilter = rFilter;
			ZappTmpObject rValue = (ZappTmpObject) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"tmptime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		// Keyword
		if(pFilter instanceof ZappKeyword && pValue instanceof ZappKeyword) {
			ZappKeyword rFilter = (ZappKeyword) pFilter; pFilter = rFilter;
			ZappKeyword rValue = (ZappKeyword) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"kword"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		// Content-Keyword
		if(pFilter instanceof ZappKeywordObject && pValue instanceof ZappKeywordObject) {
			ZappKeywordObject rFilter = (ZappKeywordObject) pFilter; pFilter = rFilter;
			ZappKeywordObject rValue = (ZappKeywordObject) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"kwordid"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}		
		
		// Content - Workflow
		if(pFilter instanceof ZappContentWorkflow && pValue instanceof ZappContentWorkflow) {
			ZappContentWorkflow rFilter = (ZappContentWorkflow) pFilter; pFilter = rFilter;
			ZappContentWorkflow rValue = (ZappContentWorkflow) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"wftime"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"desc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}

		
		// Workflow
		if(pFilter instanceof ZappWorkflowObject && pValue instanceof ZappWorkflowObject) {
			ZappWorkflowObject rFilter = (ZappWorkflowObject) pFilter; pFilter = rFilter;
			ZappWorkflowObject rValue = (ZappWorkflowObject) pValue; pValue = rValue;
			
			// Order Clause
			StringBuffer orderClause = new StringBuffer();
			if(rValue.getObjorderfield() == null || rValue.getObjorder() == null) {
				if(rValue.getObjorderfield() == null) {
					String[] orderf = {"wferid"};
					rValue.setObjorderfield(orderf);
				}
				if(rValue.getObjorder() == null) {
					String[] order = {"asc"};
					rValue.setObjorder(order);
				}
			}
			else {
				for(int i=0; i < rValue.getObjorderfield().length; i++) {
					orderClause.append(rValue.getObjorderfield()[i]);
					orderClause.append(SPACE);
					orderClause.append(rValue.getObjorder()[i]);
					if(i < (rValue.getObjorderfield().length-1)) {
						orderClause.append(COMMA);
					}
				}
			}
			if(ZstFwValidatorUtils.valid(orderClause.toString())) {
				dynamic.setOrderByClause(orderClause.toString());
			}
			
			// Paging
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjsnum(rValue.getObjsnum());
			}
			if(rValue.getObjsnum() > ZERO && rValue.getObjsnum() <= rValue.getObjenum()) {
				dynamic.setObjenum(rValue.getObjenum());
			}
		}

		
		// Method list
		Class<?> clsFilter = pFilter.getClass();
		Class<?> clsValue = pValue.getClass();  
		
		for (Map.Entry field : FIELDS.entrySet()) {
			
			//logger.info("vo fields = " + field);
			
			Method invokeMethod_Filter = clsFilter.getMethod((String) field.getValue());		// Filter
			Method invokeMethod_Value = clsValue.getMethod((String) field.getValue());			// Value
			
			if(ZstFwValidatorUtils.valid(invokeMethod_Value.invoke(pValue))) {

				//logger.info("invokeMethod_Value.invoke(pValue) = " + invokeMethod_Value.invoke(pValue));
				
				// String 타입
				if(invokeMethod_Value.invoke(pValue) instanceof String) {
				
					String value = (String) invokeMethod_Value.invoke(pValue);
					filter = BindFilter.mapOperator(ZstFwValidatorUtils.fixNullString((String) invokeMethod_Filter.invoke(pFilter), Operators.EQUAL.operator));
					
					//logger.info("(String) invokeMethod_Filter.invoke(pValue) = " + (String) invokeMethod_Filter.invoke(pFilter));
					//logger.info("value = " + value);
					//logger.info("filter = " + filter);
					
					criteria.setColumn(ZstFwValidatorUtils.fixNullString((String) field.getKey(), BLANK));  
					//logger.info("column = " + ZstFwValidatorUtils.fixNullString((String) field.getKey(), BLANK));
					switch(filter) {
						case BETWEEN :
							String[] between = value.split(DIVIDER);
							criteria.andBetween(between[ZERO], between[ONE]);
						break;
						case EQUAL :
							criteria.andEqualTo(value);
						break;
						case IN :
							criteria.andIn(new ArrayList<String>(Arrays.asList(value.split(DIVIDER))));
						break;
						case GREATER_THAN :
							criteria.andGreaterThan(value);
						break;
						case GREATER_THAN_OR_EQUAL :
							criteria.andGreaterThanOrEqualTo(value);
						break;
						case IS_NOT_NULL :
							criteria.andIsNotNull();
						break;
						case IS_NULL :
							criteria.andIsNull();
						break;
						case LESS_THAN :
							criteria.andLessThan(value);
						break;
						case LESS_THAN_OR_EQUAL :
							criteria.andLessThanOrEqualTo(value);
						break;
						case LIKE :
							criteria.andLike(PERCENT + value + PERCENT);
						break;
						case NOT_BETWEEN :
							between = value.split(DIVIDER);
							criteria.andNotBetween(between[ZERO], between[ONE]);
						break;
						case NOT_EQUAL :
							criteria.andNotEqualTo(value);
						break;
						case NOT_IN :
							criteria.andNotIn(new ArrayList<String>(Arrays.asList(value.split(DIVIDER))));
						break;
						case NOT_LIKE :
							criteria.andNotLike(PERCENT + value + PERCENT);
						break;
						default:
							criteria.andEqualTo(value);
					}
				
				}
				
				// Integer type
//				if(invokeMethod_Value.invoke(pValue) instanceof Integer) {
//					
//					Integer value_Integer = (Integer) invokeMethod_Value.invoke(pValue);
//					Method invokeMethod_String = clsValue.getMethod((String) "s" + field.getValue());			// String
//					String value_String = (String) invokeMethod_String.invoke(pValue);
//					
//					filter = BindFilter.mapOperator(ZstFwValidatorUtils.fixNullString((String) invokeMethod_Filter.invoke(pValue), Operators.EQUAL.operator));
//					
//					criteria.setColumn(ZstFwValidatorUtils.fixNullString((String) field.getKey(), BLANK)); 
//					switch(filter) {
//						case BETWEEN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								Integer[] between = ConvertUtil.convertStringArrayToIntegerArray(value_String.split(DIVIDER));
//								criteria.andBetween(between[ZERO], between[ONE]);
//							}
//						break;
//						case EQUAL :
//							criteria.andEqualTo(value_Integer);
//						break;
//						case IN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								criteria.andIn(new ArrayList<String>(Arrays.asList(value_String.split(DIVIDER))));
//							}
//						break;
//						case GREATER_THAN :
//							criteria.andGreaterThan(value_Integer);
//						break;
//						case GREATER_THAN_OR_EQUAL :
//							criteria.andGreaterThanOrEqualTo(value_Integer);
//						break;
//						case IS_NOT_NULL :
//							criteria.andIsNotNull();
//						break;
//						case IS_NULL :
//							criteria.andIsNull();
//						break;
//						case LESS_THAN :
//							criteria.andLessThan(value_Integer);
//						break;
//						case LESS_THAN_OR_EQUAL :
//							criteria.andLessThanOrEqualTo(value_Integer);
//						break;
//						case LIKE :
//							criteria.andLike(PERCENT + value_Integer + PERCENT);
//						break;
//						case NOT_BETWEEN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								Integer[] between = ConvertUtil.convertStringArrayToIntegerArray(value_String.split(DIVIDER));
//								criteria.andNotBetween(between[ZERO], between[ONE]);
//							}
//						break;
//						case NOT_EQUAL :
//							criteria.andNotEqualTo(value_Integer);
//						break;
//						case NOT_IN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								criteria.andNotIn(new ArrayList<String>(Arrays.asList(value_String.split(DIVIDER))));
//							}
//						break;
//						case NOT_LIKE :
//							criteria.andNotLike(PERCENT + value_Integer + PERCENT);
//						break;
//						default:
//							criteria.andEqualTo(value_Integer);
//					}
//				}
//
//				// BigDecimal 타입
//				if(invokeMethod_Value.invoke(pValue) instanceof BigDecimal) {
//					
//					BigDecimal value_BigDecimal = (BigDecimal) invokeMethod_Value.invoke(pValue);
//					Method invokeMethod_String = clsValue.getMethod((String) "s" + field.getValue());			// 스트링 타입값
//					String value_String = (String) invokeMethod_String.invoke(pValue);
//					
//					filter = BindFilter.mapOperator(ZstFwValidatorUtils.fixNullString((String) invokeMethod_Filter.invoke(pValue), Operators.EQUAL.operator));
//					
//					criteria.setColumn(ZstFwValidatorUtils.fixNullString((String) field.getKey(), BLANK)); 
//					switch(filter) {
//						case BETWEEN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								BigDecimal[] between = ConvertUtil.convertStringArrayToDecimalArray(value_String.split(DIVIDER));
//								criteria.andBetween(between[ZERO], between[ONE]);
//							}
//						break;
//						case EQUAL :
//							criteria.andEqualTo(value_BigDecimal);
//						break;
//						case IN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								criteria.andIn(new ArrayList<String>(Arrays.asList(value_String.split(DIVIDER))));
//							}
//						break;
//						case GREATER_THAN :
//							criteria.andGreaterThan(value_BigDecimal);
//						break;
//						case GREATER_THAN_OR_EQUAL :
//							criteria.andGreaterThanOrEqualTo(value_BigDecimal);
//						break;
//						case IS_NOT_NULL :
//							criteria.andIsNotNull();
//						break;
//						case IS_NULL :
//							criteria.andIsNull();
//						break;
//						case LESS_THAN :
//							criteria.andLessThan(value_BigDecimal);
//						break;
//						case LESS_THAN_OR_EQUAL :
//							criteria.andLessThanOrEqualTo(value_BigDecimal);
//						break;
//						case LIKE :
//							criteria.andLike(PERCENT + value_BigDecimal + PERCENT);
//						break;
//						case NOT_BETWEEN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								BigDecimal[] between = ConvertUtil.convertStringArrayToDecimalArray(value_String.split(DIVIDER));
//								criteria.andNotBetween(between[ZERO], between[ONE]);
//							}
//						break;
//						case NOT_EQUAL :
//							criteria.andNotEqualTo(value_BigDecimal);
//						break;
//						case NOT_IN :
//							if(ZstFwValidatorUtils.valid(value_String)) {
//								criteria.andNotIn(new ArrayList<String>(Arrays.asList(value_String.split(DIVIDER))));
//							}
//						break;
//						case NOT_LIKE :
//							criteria.andNotLike(PERCENT + value_BigDecimal + PERCENT);
//						break;
//						default:
//							criteria.andEqualTo(value_BigDecimal);
//					}
//				}
				
			}
		}


		return dynamic;
	}

	/**
	 * Extract field info. of the object
	 * @param pObj
	 * @return
	 */
	private void getFielMap(Object pObj) {
		
		FIELDS = new ConcurrentHashMap<String, String>();
		
		if(pObj != null) {
			
			if(pObj instanceof ZappContentAcl) pObj = new ZappContentAcl(); 			// Content  access control
			if(pObj instanceof ZappClassAcl) pObj = new ZappClassAcl(); 				// Classification access control
			
			if(pObj instanceof ZappClassification) pObj = new ZappClassification(); 	// Classification
			
			if(pObj instanceof ZappBundle) pObj = new ZappBundle(); 					// Bundle
			if(pObj instanceof ZappAdditoryBundle) pObj = new ZappAdditoryBundle(); 	// Additional Bundle
			if(pObj instanceof ZappFile) pObj = new ZappFile(); 						// File
			if(pObj instanceof ZArchMFile) pObj = new ZArchMFile(); 					// Master File
			if(pObj instanceof ZappClassObject) pObj = new ZappClassObject(); 			// Content-Classification
			if(pObj instanceof ZappLinkedObject) pObj = new ZappLinkedObject(); 		// Linked Content
			if(pObj instanceof ZappSharedObject) pObj = new ZappSharedObject(); 		// Shared Content
			if(pObj instanceof ZappLockedObject) pObj = new ZappLockedObject(); 		// Locked Content
			if(pObj instanceof ZappTmpObject) pObj = new ZappTmpObject(); 				// Temporary Info.
			if(pObj instanceof ZappKeyword) pObj = new ZappKeyword(); 					// Keyword
			if(pObj instanceof ZappKeywordObject) pObj = new ZappKeywordObject(); 		// Content-Keyword
			if(pObj instanceof ZappComment) pObj = new ZappComment(); 					// Comment
			
			if(pObj instanceof ZappAccessLog) pObj = new ZappAccessLog(); 				// Access Log
			if(pObj instanceof ZappContentLog) pObj = new ZappContentLog(); 			// Content Log
			if(pObj instanceof ZappSystemLog) pObj = new ZappSystemLog(); 				// System Log
			
			if(pObj instanceof ZappCompany) pObj = new ZappCompany(); 					// Company
			if(pObj instanceof ZappDept) pObj = new ZappDept(); 						// Department
			if(pObj instanceof ZappUser) pObj = new ZappUser();							// User
			if(pObj instanceof ZappDeptUser) pObj = new ZappDeptUser(); 				// Dept. User
			if(pObj instanceof ZappGroupUser) pObj = new ZappGroupUser(); 				// Group User	
			if(pObj instanceof ZappOrganTask) pObj = new ZappOrganTask(); 				// Company Task
			
			if(pObj instanceof ZappCode) pObj = new ZappCode(); 						// Code
			if(pObj instanceof ZappEnv) pObj = new ZappEnv(); 							// Preferences
			
			if(pObj instanceof ZappImg) pObj = new ZappImg(); 							// Code
			if(pObj instanceof ZappTag) pObj = new ZappTag(); 							// Tag
			if(pObj instanceof ZappTaskTag) pObj = new ZappTaskTag(); 					// Task Tag
			
			if(pObj instanceof ZappWorkflowObject) pObj = new ZappWorkflowObject(); 	// Workflow
			if(pObj instanceof ZappContentWorkflow) pObj = new ZappContentWorkflow(); 	// Content-Workflow
			
			for(Field field : pObj.getClass().getDeclaredFields()) {
				if(!field.getName().startsWith("obj") && !field.getName().startsWith("serial")) {
					field.setAccessible(true);
					StringBuilder sb = new StringBuilder(field.getName().toLowerCase());
					sb.setCharAt(ZERO, Character.toUpperCase(field.getName().charAt(ZERO)));
					FIELDS.put(field.getName(), "get" + sb.toString());
					//logger.info(FIELDS.get(field.getName()));
				}
			}
			
		}
		
	}
	


}

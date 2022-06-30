/**
 *
 */
package com.zenithst.core.workflow.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.workflow.vo.ZappWorkflowObject;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to check and bind values. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service
public class ZappWorkflowBinder extends ZappDomain {

	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Check if PK is entered or not.
	 * @param pValue
	 * @return
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappWorkflowObject) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappWorkflowObject) pValue).getWfobjid(), BLANK);
		}

		if(pk.equals(BLANK)){
			return true;
		}
		
		return false;
	}	
	
	/**
	 * Checks for missing values ​​except for the PK.
	 * @param pValue
	 * @return
	 */
	public boolean isEmpty(Object pValue) {
		
		StringBuffer strtype = new StringBuffer();
		int inttype = ZERO;
		
		if(pValue instanceof ZappWorkflowObject) {
			ZappWorkflowObject pvo = (ZappWorkflowObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {

				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("wfobjid")) continue;
	            
	            if (field.getType().equals(Boolean.TYPE)) {
	            	
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
	            	try {
						inttype += (Integer) field.get(pvo);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
	            	try {
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
		
		if(strtype.toString().equals(BLANK) && inttype == ZERO) {
			return true;
		}
		
		return false;
	}

	/**
	 * Set default filtering options.
	 * @param Object
	 * @return Object
	 * @sample
	 */
	public Object getFilter(Object pFilter) {
		
		if(pFilter instanceof ZappWorkflowObject) {
			ZappWorkflowObject oFilter = new ZappWorkflowObject();
			oFilter.setWfobjid(Operators.EQUAL.operator);
			oFilter.setWferid(Operators.IN.operator);
			oFilter.setContentid(Operators.IN.operator);
			return oFilter;
		}
		
		return null;
		
	}
	
	/**
	 * Set dynamic filtering options. (Reference: Filter setting is specified only for string type methods)
	 * @param pFilter
	 * @return
	 */
	public Object getDynamicFilter(Object pObjFilter) {
		
		if(pObjFilter instanceof ZappWorkflowObject) {
			ZappWorkflowObject pFilter = (ZappWorkflowObject) pObjFilter;
			ZappWorkflowObject oFilter = new ZappWorkflowObject();
			oFilter.setWfobjid(BindFilter.setFilter(BLANK, pFilter.getWfobjid()));
			oFilter.setWferid(BindFilter.setFilter(Operators.IN.operator, pFilter.getWferid()));
			oFilter.setContentid(BindFilter.setFilter(Operators.IN.operator, pFilter.getContentid()));
			return oFilter;
		}
		
		return null;

	}
}

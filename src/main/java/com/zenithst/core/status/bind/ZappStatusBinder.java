/**
 *
 */
package com.zenithst.core.status.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.status.vo.ZappApm;
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
public class ZappStatusBinder extends ZappDomain {

	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Check if PK is entered or not.
	 * @param pValue
	 * @return
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappApm) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappApm) pValue).getApmid(), BLANK);
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
		
		if(pValue instanceof ZappApm) {
			ZappApm pvo = (ZappApm) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {

				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("apmid")) continue;
	            
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

		if(pFilter instanceof ZappApm) {
			ZappApm oFilter = new ZappApm();
			oFilter.setApmid(Operators.EQUAL.operator);
			oFilter.setApmtype(Operators.IN.operator);
			oFilter.setApmtime(Operators.BETWEEN.operator);
			oFilter.setApm(Operators.LIKE.operator);
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
		
		if(pObjFilter instanceof ZappApm) {
			ZappApm pFilter = (ZappApm) pObjFilter;
			ZappApm oFilter = new ZappApm();
			oFilter.setApmid(BindFilter.setFilter(BLANK, pFilter.getApmid()));
			oFilter.setApmtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getApmtype()));
			oFilter.setApmtime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getApmtime()));
			oFilter.setApm(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getApm()));
			return oFilter;
		}
	
		return null;

	}

}

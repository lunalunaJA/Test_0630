/**
 *
 */
package com.zenithst.core.system.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.system.vo.ZappCode;
import com.zenithst.core.system.vo.ZappEnv;
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
public class ZappSystemBinder extends ZappDomain {

	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Check if PK is entered or not.
	 * @param pValue
	 * @return
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappCode) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappCode) pValue).getCodeid(), BLANK);
		}
		if(pValue instanceof ZappEnv) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappEnv) pValue).getEnvid(), BLANK);
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
		
		if(pValue instanceof ZappCode) {
			ZappCode pvo = (ZappCode) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("codeid")) continue;
//				if(field.getName().startsWith("obj")) continue;
	            //logger.debug("[isEmpty] Field = " + field.getName());
	            if (field.getType().equals(Boolean.TYPE)) {
	            	//logger.debug("[isEmpty] Type = Boolean");
	            	try {
	            		//logger.debug("[isEmpty] Value = " + (Boolean) field.get(pvo));
						boolean booltype = (Boolean) field.get(pvo);
						if(booltype == true) strtype.append("B");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
	            	//logger.debug("[isEmpty] Type = Integer");
	            	try {
	            		//logger.debug("[isEmpty] Value = " + (Integer) field.get(pvo));
						int inttype = field.get(pvo) != null ? (Integer) field.get(pvo) : ZERO;
						if(inttype > ZERO) strtype.append(inttype);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
	            	//logger.debug("[isEmpty] Type = String");
	            	try {
	            		//logger.debug("[isEmpty] Value = " + (String) field.get(pvo));
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
		
		if(pValue instanceof ZappEnv) {
			ZappEnv pvo = (ZappEnv) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("envid")) continue;
	            //logger.debug("[isEmpty] Field = " + field.getName());
	            if (field.getType().equals(Boolean.TYPE)) {
	            	//logger.debug("[isEmpty] Type = Boolean");
	            	try {
	            		//logger.debug("[isEmpty] Value = " + (Boolean) field.get(pvo));
						boolean booltype = (Boolean) field.get(pvo);
						if(booltype == true) strtype.append("B");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
	            	//logger.debug("[isEmpty] Type = Integer");
	            	try {
	            		//logger.debug("[isEmpty] Value = " + (Integer) field.get(pvo));
						int inttype = field.get(pvo) != null ? (Integer) field.get(pvo) : ZERO;
						if(inttype > ZERO) strtype.append(inttype);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
	            	//logger.debug("[isEmpty] Type = String");
	            	try {
	            		//logger.debug("[isEmpty] Value = " + (String) field.get(pvo));
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
		
		//logger.debug("[isEmpty] Check String = " + strtype.toString());
		
		if(strtype.toString().equals(BLANK)) {
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

		if(pFilter instanceof ZappCode) {
			ZappCode oFilter = new ZappCode();
			oFilter.setCodeid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setCodevalue(Operators.IN.operator);
			oFilter.setUpid(Operators.IN.operator);
			oFilter.setTypes(Operators.IN.operator);
			oFilter.setCodekey(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappEnv) {
			ZappEnv oFilter = new ZappEnv();
			oFilter.setEnvid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setSetval(Operators.IN.operator);
			oFilter.setEnvtype(Operators.IN.operator);
			oFilter.setSettype(Operators.IN.operator);
			oFilter.setSetopt(Operators.IN.operator);
			oFilter.setEditable(Operators.EQUAL.operator);
			oFilter.setEnvkey(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
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
		
		if(pObjFilter instanceof ZappCode) {
			ZappCode pFilter = (ZappCode) pObjFilter;
			ZappCode oFilter = new ZappCode();
			oFilter.setCodeid(BindFilter.setFilter(BLANK, pFilter.getCodeid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setCodevalue(BindFilter.setFilter(Operators.IN.operator, pFilter.getCodevalue()));
			oFilter.setUpid(BindFilter.setFilter(Operators.IN.operator, pFilter.getUpid()));
			oFilter.setCodekey(BindFilter.setFilter(Operators.IN.operator, pFilter.getCodekey()));
			oFilter.setTypes(BindFilter.setFilter(Operators.IN.operator, pFilter.getTypes()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappEnv) {
			ZappEnv pFilter = (ZappEnv) pObjFilter;
			ZappEnv oFilter = new ZappEnv();
			oFilter.setEnvid(BindFilter.setFilter(BLANK, pFilter.getEnvid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setSetval(BindFilter.setFilter(Operators.IN.operator, pFilter.getSetval()));
			oFilter.setEnvtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getEnvtype()));
			oFilter.setSettype(BindFilter.setFilter(Operators.IN.operator, pFilter.getSettype()));
			oFilter.setSetopt(BindFilter.setFilter(Operators.IN.operator, pFilter.getSetopt()));
			oFilter.setEditable(BindFilter.setFilter(BLANK, pFilter.getEditable()));
			oFilter.setEnvkey(BindFilter.setFilter(Operators.IN.operator, pFilter.getEnvkey()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		return null;

	}

}

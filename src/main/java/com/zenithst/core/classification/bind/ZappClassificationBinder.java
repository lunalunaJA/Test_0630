/**
 *
 */
package com.zenithst.core.classification.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.core.classification.vo.ZappAdditoryClassification;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this interface is to check and bind values. <br>
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
public class ZappClassificationBinder extends ZappDomain {

	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Check if PK is entered or not.
	 * @param pValue
	 * @return
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappClassification) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappClassification) pValue).getClassid(), BLANK);
		}
		if(pValue instanceof ZappAdditoryClassification) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappAdditoryClassification) pValue).getClassid(), BLANK);
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
		
		if(pValue instanceof ZappClassification) {
			ZappClassification pvo = (ZappClassification) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("classid")) continue;
//				if(field.getName().startsWith("obj")) continue;
//	            logger.info("[isEmpty] Field = " + field.getName());
	            if (field.getType().equals(Boolean.TYPE)) {
//	            	logger.info("[isEmpty] Type = Boolean");
	            	try {
//	            		logger.info("[isEmpty] Value = " + (Boolean) field.get(pvo));
						boolean booltype = (Boolean) field.get(pvo);
						if(booltype == true) strtype.append("B");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
//	            	logger.info("[isEmpty] Type = Integer");
	            	try {
//	            		logger.info("[isEmpty] Value = " + (Integer) field.get(pvo));
						int inttype = field.get(pvo) != null ? (Integer) field.get(pvo) : ZERO;
						if(inttype > ZERO) strtype.append(inttype);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
//	            	logger.info("[isEmpty] Type = String");
	            	try {
//	            		logger.info("[isEmpty] Value = " + (String) field.get(pvo));
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
		
		if(pValue instanceof ZappAdditoryClassification) {
			ZappAdditoryClassification pvo = (ZappAdditoryClassification) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("classid")) continue;
//	            logger.info("[isEmpty] Field = " + field.getName());
	            if (field.getType().equals(Boolean.TYPE)) {
//	            	logger.info("[isEmpty] Type = Boolean");
	            	try {
//	            		logger.info("[isEmpty] Value = " + (Boolean) field.get(pvo));
						boolean booltype = (Boolean) field.get(pvo);
						if(booltype == true) strtype.append("B");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
//	            	logger.info("[isEmpty] Type = Integer");
	            	try {
//	            		logger.info("[isEmpty] Value = " + (Integer) field.get(pvo));
						int inttype = field.get(pvo) != null ? (Integer) field.get(pvo) : ZERO;
						if(inttype > ZERO) strtype.append(inttype);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
//	            	logger.info("[isEmpty] Type = String");
	            	try {
//	            		logger.info("[isEmpty] Value = " + (String) field.get(pvo));
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
								
		logger.info("[isEmpty] Check String = " + strtype.toString());
		
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

		if(pFilter instanceof ZappClassification) {
			ZappClassification oFilter = new ZappClassification();
			oFilter.setClassid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setCode(Operators.IN.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setDescpt(Operators.LIKE.operator);
			oFilter.setUpid(Operators.IN.operator);
			oFilter.setHolderid(Operators.IN.operator);
			oFilter.setPriority(Operators.EQUAL.ioperator);
			oFilter.setTypes(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappAdditoryClassification) {
			ZappAdditoryClassification oFilter = new ZappAdditoryClassification();
			oFilter.setClassid(Operators.EQUAL.operator);
			oFilter.setDynamic01(Operators.IN.operator);
			oFilter.setDynamic02(Operators.IN.operator);
			oFilter.setDynamic03(Operators.IN.operator);
			oFilter.setDynamic04(Operators.IN.operator);
			oFilter.setDynamic05(Operators.IN.operator);
			oFilter.setDynamic06(Operators.IN.operator);
			oFilter.setDynamic07(Operators.IN.operator);
			oFilter.setDynamic08(Operators.IN.operator);
			oFilter.setDynamic09(Operators.IN.operator);
			oFilter.setDynamic10(Operators.IN.operator);
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
		
		if(pObjFilter instanceof ZappClassification) {
			ZappClassification pFilter = (ZappClassification) pObjFilter;
			ZappClassification oFilter = new ZappClassification();
			oFilter.setClassid(BindFilter.setFilter(BLANK, pFilter.getClassid()));
			oFilter.setCode(BindFilter.setFilter(Operators.IN.operator, pFilter.getCode()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setDescpt(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getDescpt()));
			oFilter.setUpid(BindFilter.setFilter(Operators.IN.operator, pFilter.getUpid()));
			oFilter.setPriority(BindFilter.setFilter(Operators.IN.ioperator, pFilter.getPriority()));
			oFilter.setTypes(BindFilter.setFilter(Operators.IN.operator, pFilter.getTypes()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappAdditoryClassification) {
			ZappAdditoryClassification pFilter = (ZappAdditoryClassification) pObjFilter;
			ZappAdditoryClassification oFilter = new ZappAdditoryClassification();
			oFilter.setClassid(BindFilter.setFilter(BLANK, pFilter.getClassid()));
			oFilter.setDynamic01(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic01()));
			oFilter.setDynamic02(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic02()));
			oFilter.setDynamic03(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic03()));
			oFilter.setDynamic04(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic04()));
			oFilter.setDynamic05(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic05()));
			oFilter.setDynamic06(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic06()));
			oFilter.setDynamic07(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic07()));
			oFilter.setDynamic08(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic08()));
			oFilter.setDynamic09(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic09()));
			oFilter.setDynamic10(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic10()));			
		}
		
		return null;

	}

}

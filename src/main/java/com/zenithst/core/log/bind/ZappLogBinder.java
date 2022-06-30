/**
 *
 */
package com.zenithst.core.log.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.log.vo.ZappAccessLog;
import com.zenithst.core.log.vo.ZappContentLog;
import com.zenithst.core.log.vo.ZappSystemLog;
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
public class ZappLogBinder extends ZappDomain {

	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Check if PK is entered or not.
	 * @param pValue
	 * @return
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappAccessLog) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappAccessLog) pValue).getLogid(), BLANK);
		}
		if(pValue instanceof ZappContentLog) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappContentLog) pValue).getLogid(), BLANK);
		}
		if(pValue instanceof ZappSystemLog) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappSystemLog) pValue).getLogid(), BLANK);
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
		
		if(pValue instanceof ZappAccessLog) {
			ZappAccessLog pvo = (ZappAccessLog) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("logid")) continue;
	            //logger.info("[isEmpty] Field = " + field.getName());
	            if (field.getType().equals(Boolean.TYPE)) {
	            	//logger.info("[isEmpty] Type = Boolean");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (Boolean) field.get(pvo));
						boolean booltype = (Boolean) field.get(pvo);
						if(booltype == true) strtype.append("B");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
	            	//logger.info("[isEmpty] Type = Integer");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (Integer) field.get(pvo));
						int inttype = field.get(pvo) != null ? (Integer) field.get(pvo) : ZERO;
						if(inttype > ZERO) strtype.append(inttype);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
	            	//logger.info("[isEmpty] Type = String");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (String) field.get(pvo));
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
		
		if(pValue instanceof ZappContentLog) {
			ZappContentLog pvo = (ZappContentLog) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("logid")) continue;
	            //logger.info("[isEmpty] Field = " + field.getName());
	            if (field.getType().equals(Boolean.TYPE)) {
	            	//logger.info("[isEmpty] Type = Boolean");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (Boolean) field.get(pvo));
						boolean booltype = (Boolean) field.get(pvo);
						if(booltype == true) strtype.append("B");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
	            	//logger.info("[isEmpty] Type = Integer");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (Integer) field.get(pvo));
						int inttype = field.get(pvo) != null ? (Integer) field.get(pvo) : ZERO;
						if(inttype > ZERO) strtype.append(inttype);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
	            	//logger.info("[isEmpty] Type = String");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (String) field.get(pvo));
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
		
		if(pValue instanceof ZappSystemLog) {
			ZappSystemLog pvo = (ZappSystemLog) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("logid")) continue;
	            //logger.info("[isEmpty] Field = " + field.getName());
	            if (field.getType().equals(Boolean.TYPE)) {
	            	//logger.info("[isEmpty] Type = Boolean");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (Boolean) field.get(pvo));
						boolean booltype = (Boolean) field.get(pvo);
						if(booltype == true) strtype.append("B");
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == Integer.TYPE || field.getType() == Integer.class) {
	            	//logger.info("[isEmpty] Type = Integer");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (Integer) field.get(pvo));
						int inttype = field.get(pvo) != null ? (Integer) field.get(pvo) : ZERO;
						if(inttype > ZERO) strtype.append(inttype);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	            if (field.getType() == String.class) {
	            	//logger.info("[isEmpty] Type = String");
	            	try {
	            		//logger.info("[isEmpty] Value = " + (String) field.get(pvo));
						strtype.append(ZstFwValidatorUtils.fixNullString((String) field.get(pvo), BLANK));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
	            }
	        } 
		}
		
		//logger.info("[isEmpty] Check String = " + strtype.toString());
		
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

		if(pFilter instanceof ZappAccessLog) {
			ZappAccessLog oFilter = new ZappAccessLog();
			oFilter.setLogid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setLogobjid(Operators.IN.operator);
			oFilter.setLoggerid(Operators.IN.operator);
			oFilter.setLoggername(Operators.LIKE.operator);
			oFilter.setLoggerdeptid(Operators.IN.operator);
			oFilter.setLoggerdeptname(Operators.LIKE.operator);
			oFilter.setLogtime(Operators.BETWEEN.operator);
			oFilter.setLogtype(Operators.IN.operator);
			oFilter.setLogip(Operators.IN.operator);
			oFilter.setLogs(Operators.LIKE.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappContentLog) {
			ZappContentLog oFilter = new ZappContentLog();
			oFilter.setLogid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setLogobjid(Operators.IN.operator);
			oFilter.setLogtext(Operators.LIKE.operator);
			oFilter.setLoggerid(Operators.IN.operator);
			oFilter.setLoggername(Operators.LIKE.operator);
			oFilter.setLoggerdeptid(Operators.IN.operator);
			oFilter.setLoggerdeptname(Operators.LIKE.operator);
			oFilter.setLogtime(Operators.BETWEEN.operator);
			oFilter.setLogtype(Operators.IN.operator);
			oFilter.setLogs(Operators.LIKE.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappSystemLog) {
			ZappSystemLog oFilter = new ZappSystemLog();
			oFilter.setLogid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setLogobjid(Operators.IN.operator);
			oFilter.setLoggerid(Operators.IN.operator);
			oFilter.setLoggername(Operators.LIKE.operator);
			oFilter.setLoggerdeptid(Operators.IN.operator);
			oFilter.setLoggerdeptname(Operators.LIKE.operator);
			oFilter.setLogtime(Operators.BETWEEN.operator);
			oFilter.setLogtype(Operators.IN.operator);
			oFilter.setLogs(Operators.LIKE.operator);
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
		
		if(pObjFilter instanceof ZappAccessLog) {
			ZappAccessLog pFilter = (ZappAccessLog) pObjFilter;
			ZappAccessLog oFilter = new ZappAccessLog();
			oFilter.setLogid(BindFilter.setFilter(BLANK, pFilter.getLogid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setLogobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLogobjid()));
			oFilter.setLoggerid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLoggerid()));
			oFilter.setLoggername(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLoggername()));
			oFilter.setLoggerdeptid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLoggerdeptid()));
			oFilter.setLoggerdeptname(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLoggerdeptname()));
			oFilter.setLogtime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getLogtime()));
			oFilter.setLogtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getLogtype()));
			oFilter.setLogip(BindFilter.setFilter(Operators.IN.operator, pFilter.getLogip()));
			oFilter.setLogs(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLogs()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappContentLog) {
			ZappContentLog pFilter = (ZappContentLog) pObjFilter;
			ZappContentLog oFilter = new ZappContentLog();
			oFilter.setLogid(BindFilter.setFilter(BLANK, pFilter.getLogid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setLogobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLogobjid()));
			oFilter.setLogtext(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLogtext()));
			oFilter.setLoggerid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLoggerid()));
			oFilter.setLoggername(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLoggername()));
			oFilter.setLoggerdeptid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLoggerdeptid()));
			oFilter.setLoggerdeptname(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLoggerdeptname()));
			oFilter.setLogtime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getLogtime()));
			oFilter.setLogtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getLogtype()));
			oFilter.setLogs(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLogs()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappSystemLog) {
			ZappSystemLog pFilter = (ZappSystemLog) pObjFilter;
			ZappSystemLog oFilter = new ZappSystemLog();
			oFilter.setLogid(BindFilter.setFilter(BLANK, pFilter.getLogid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setLogobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLogobjid()));
			oFilter.setLoggerid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLoggerid()));
			oFilter.setLoggername(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLoggername()));
			oFilter.setLoggerdeptid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLoggerdeptid()));
			oFilter.setLoggerdeptname(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLoggerdeptname()));
			oFilter.setLogtime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getLogtime()));
			oFilter.setLogtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getLogtype()));
			oFilter.setLogs(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getLogs()));
			return oFilter;
		}
		
		return null;

	}

}

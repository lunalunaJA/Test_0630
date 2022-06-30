/**
 *
 */
package com.zenithst.core.organ.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.bind.ZappDynamic;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappDomain;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappGroup;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappOrganTask;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.framework.domain.ZstFwResult;
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
public class ZappOrganBinder extends ZappDomain {
	
	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ZappMessageMgtService messageService;
	
	/**
	 * Check if PK is entered or not.
	 * @param pValue
	 * @return
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappCompany) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappCompany) pValue).getCompanyid(), BLANK);
		}
		if(pValue instanceof ZappDept) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappDept) pValue).getDeptid(), BLANK);
		}
		if(pValue instanceof ZappDeptUser) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappDeptUser) pValue).getDeptuserid(), BLANK);
		}
		if(pValue instanceof ZappGroup) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappGroup) pValue).getGroupid(), BLANK);
		}
		if(pValue instanceof ZappGroupUser) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappGroupUser) pValue).getGroupuserid(), BLANK);
		}
		if(pValue instanceof ZappUser) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappUser) pValue).getUserid(), BLANK);
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

		if(pValue instanceof ZappCompany) {
			ZappCompany pvo = (ZappCompany) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("companyid")) continue;
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
		

		if(pValue instanceof ZappDept) {
			ZappDept pvo = (ZappDept) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("deptid")) continue;
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

		if(pValue instanceof ZappDeptUser) {
			ZappDeptUser pvo = (ZappDeptUser) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("deptuserid")) continue;
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

		if(pValue instanceof ZappGroup) {
			//logger.info("[isEmpty] Class = ZappGroup");
			ZappGroup pvo = (ZappGroup) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("groupid")) continue;
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

		if(pValue instanceof ZappGroupUser) {
			//logger.info("[isEmpty] Class = ZappGroupUser");
			ZappGroupUser pvo = (ZappGroupUser) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("groupuserid")) continue;
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

		if(pValue instanceof ZappUser) {
			ZappUser pvo = (ZappUser) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("userid")) continue;
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
		
		if(pValue instanceof ZappOrganTask) {
			ZappOrganTask pvo = (ZappOrganTask) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("organtaskid")) continue;
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
		
		logger.info("[isEmpty] Check String = " + strtype.toString());
		
		if(strtype.toString().equals(BLANK)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 등록을 위한 필수 정보를 체크한다.
	 * @param pValue
	 * @return
	 */
	public ZstFwResult isEmpty(ZappAuth pObjAuth, Object pValue, ZstFwResult pObjRes) {
		
		if(pValue != null) {
			
			/* Company */
			if(pValue instanceof ZappCompany) {
				ZappCompany pObj = (ZappCompany) pValue;
				if(!ZstFwValidatorUtils.valid(pObj.getName())) {
					return ZappFinalizing.finalising("ERR_MIS_NAME", "[isEmpty] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getCode())) {
					return ZappFinalizing.finalising("ERR_MIS_CODE", "[isEmpty] " + messageService.getMessage("ERR_MIS_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getAbbrname())) {
					return ZappFinalizing.finalising("ERR_MIS_ABBR", "[isEmpty] " + messageService.getMessage("ERR_MIS_ABBR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* Department */
			if(pValue instanceof ZappDept) {
				ZappDept pObj = (ZappDept) pValue;
//				if(!ZstFwValidatorUtils.valid(pObj.getCompanyid())) {
//					return ZappFinalizing.finalising("ERR_MIS_COMPANYID", "[isEmpty] " + messageService.getMessage("ERR_MIS_COMPANYID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
				if(!ZstFwValidatorUtils.valid(pObj.getName())) {
					return ZappFinalizing.finalising("ERR_MIS_NAME", "[isEmpty] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getCode())) {
					return ZappFinalizing.finalising("ERR_MIS_CODE", "[isEmpty] " + messageService.getMessage("ERR_MIS_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getAbbrname())) {
					return ZappFinalizing.finalising("ERR_MIS_ABBR", "[isEmpty] " + messageService.getMessage("ERR_MIS_ABBR",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* Department User */
			if(pValue instanceof ZappDeptUser) {
				ZappDeptUser pObj = (ZappDeptUser) pValue;
				if(!ZstFwValidatorUtils.valid(pObj.getDeptid())) {
					return ZappFinalizing.finalising("ERR_MIS_DEPTID", "[isEmpty] " + messageService.getMessage("ERR_MIS_DEPTID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getUserid())) {
					return ZappFinalizing.finalising("ERR_MIS_USERID", "[isEmpty] " + messageService.getMessage("ERR_MIS_USERID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getUsertype())) {
					return ZappFinalizing.finalising("ERR_MIS_UTYPE", "[isEmpty] " + messageService.getMessage("ERR_MIS_UTYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getOriginyn())) {
					return ZappFinalizing.finalising("ERR_MIS_ORGJOB", "[isEmpty] " + messageService.getMessage("ERR_MIS_ORGJOB",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getPositionid())) {
					return ZappFinalizing.finalising("ERR_MIS_POS", "[isEmpty] " + messageService.getMessage("ERR_MIS_POS",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getDutyid())) {
					return ZappFinalizing.finalising("ERR_MIS_DUTY", "[isEmpty] " + messageService.getMessage("ERR_MIS_DUTY",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getSeclevelid())) {
					return ZappFinalizing.finalising("ERR_MIS_SECLVL", "[isEmpty] " + messageService.getMessage("ERR_MIS_SECLVL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* Group */
			if(pValue instanceof ZappGroup) {
				ZappGroup pObj = (ZappGroup) pValue;
//				if(!ZstFwValidatorUtils.valid(pObj.getCompanyid())) {
//					return ZappFinalizing.finalising("ERR_MIS_COMPANYID", "[isEmpty] " + messageService.getMessage("ERR_MIS_COMPANYID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
				if(!ZstFwValidatorUtils.valid(pObj.getName())) {
					return ZappFinalizing.finalising("ERR_MIS_NAME", "[isEmpty] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getCode())) {
					return ZappFinalizing.finalising("ERR_MIS_CODE", "[isEmpty] " + messageService.getMessage("ERR_MIS_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getTypes())) {
					return ZappFinalizing.finalising("ERR_MIS_TYPE", "[isEmpty] " + messageService.getMessage("ERR_MIS_TYPE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}			
			
			/* Group User */
			if(pValue instanceof ZappGroupUser) {
				ZappGroupUser pObj = (ZappGroupUser) pValue;
				if(!ZstFwValidatorUtils.valid(pObj.getGobjid())) {
					return ZappFinalizing.finalising("ERR_MIS_TARID", "[isEmpty] " + messageService.getMessage("ERR_MIS_TARID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getGobjtype())) {
					return ZappFinalizing.finalising("누락_대상유형", "[isEmpty] " + messageService.getMessage("누락_대상유형",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getGroupid())) {
					return ZappFinalizing.finalising("누락_그룹아이디", "[isEmpty] " + messageService.getMessage("누락_그룹아이디",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* User */
			if(pValue instanceof ZappUser) {
				ZappUser pObj = (ZappUser) pValue;
//				if(!ZstFwValidatorUtils.valid(pObj.getCompanyid())) {
//					return ZappFinalizing.finalising("ERR_MIS_COMPANYID", "[isEmpty] " + messageService.getMessage("ERR_MIS_COMPANYID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
//				}
				if(!ZstFwValidatorUtils.valid(pObj.getEmpno())) {
					return ZappFinalizing.finalising("누락_사원번호", "[isEmpty] " + messageService.getMessage("누락_사원번호",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getLoginid())) {
					return ZappFinalizing.finalising("누락_로그인아이디", "[isEmpty] " + messageService.getMessage("누락_로그인아이디",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getName())) {
					return ZappFinalizing.finalising("ERR_MIS_NAME", "[isEmpty] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getPasswd())) {
					return ZappFinalizing.finalising("ERR_MIS_PWD", "[isEmpty] " + messageService.getMessage("ERR_MIS_PWD",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getEmail())) {
					return ZappFinalizing.finalising("누락_이메일", "[isEmpty] " + messageService.getMessage("누락_이메일",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
		} else {
			return ZappFinalizing.finalising("ERR_MIS_REGVAL", "[isEmpty] " + messageService.getMessage("ERR_MIS_REGVAL",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
		}
		
		return pObjRes;
		
	}

	/**
	 * Set default filtering options.
	 * @param Object
	 * @return Object
	 * @sample
	 */
	public Object getFilter(Object pFilter) {

		if(pFilter instanceof ZappCompany) {
			ZappCompany oFilter = new ZappCompany();
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setTel(Operators.LIKE.operator);
			oFilter.setAddress(Operators.LIKE.operator);
			oFilter.setCode(Operators.IN.operator);
			oFilter.setAbbrname(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappDept) {
			ZappDept oFilter = new ZappDept();
			oFilter.setDeptid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setUpid(Operators.IN.operator);
			oFilter.setCode(Operators.IN.operator);
			oFilter.setPriority(Operators.IN.ioperator);
			oFilter.setAbbrname(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappDeptUser) {
			ZappDeptUser oFilter = new ZappDeptUser();
			oFilter.setDeptuserid(Operators.EQUAL.operator);
			oFilter.setDeptid(Operators.IN.operator);
			oFilter.setUserid(Operators.IN.operator);
			oFilter.setUsertype(Operators.IN.operator);
			oFilter.setOriginyn(Operators.IN.operator);
			oFilter.setPositionid(Operators.IN.operator);
			oFilter.setDutyid(Operators.IN.operator);
			oFilter.setSeclevelid(Operators.IN.operator);
			oFilter.setIssupervisor(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappGroup) {
			ZappGroup oFilter = new ZappGroup();
			oFilter.setGroupid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setUpid(Operators.IN.operator);
			oFilter.setCode(Operators.IN.operator);
			oFilter.setPriority(Operators.IN.ioperator);
			oFilter.setTypes(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}		
		
		if(pFilter instanceof ZappGroupUser) {
			ZappGroupUser oFilter = new ZappGroupUser();
			oFilter.setGroupuserid(Operators.EQUAL.operator);
			oFilter.setGobjid(Operators.IN.operator);
			oFilter.setGroupid(Operators.IN.operator);
			oFilter.setGobjtype(Operators.IN.operator);
			oFilter.setEditable(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappUser) {
			ZappUser oFilter = new ZappUser();
			oFilter.setUserid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setLoginid(Operators.EQUAL.operator);
			oFilter.setPasswd(Operators.EQUAL.operator);
			oFilter.setEmpno(Operators.EQUAL.operator);
			oFilter.setEmail(Operators.EQUAL.operator);
			oFilter.setPasswdsalt(Operators.EQUAL.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappOrganTask) {
			ZappOrganTask oFilter = new ZappOrganTask();
			oFilter.setOrgantaskid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setDeptid(Operators.IN.operator);
			oFilter.setTaskid(Operators.IN.operator);
			oFilter.setTobjtype(Operators.IN.operator);
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
		
		if(pObjFilter instanceof ZappCompany) {
			ZappCompany pFilter = (ZappCompany) pObjFilter;
			ZappCompany oFilter = new ZappCompany();
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setTel(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getTel()));
			oFilter.setAddress(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getAddress()));
			oFilter.setCode(BindFilter.setFilter(Operators.IN.operator, pFilter.getCode()));
			oFilter.setAbbrname(BindFilter.setFilter(Operators.IN.operator, pFilter.getAbbrname()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappDept) {
			ZappDept pFilter = (ZappDept) pObjFilter;
			ZappDept oFilter = new ZappDept();
			oFilter.setDeptid(BindFilter.setFilter(BLANK, pFilter.getDeptid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setUpid(BindFilter.setFilter(Operators.IN.operator, pFilter.getUpid()));
			oFilter.setCode(BindFilter.setFilter(Operators.IN.operator, pFilter.getCode()));
			oFilter.setPriority(BindFilter.setFilter(Operators.IN.ioperator, pFilter.getPriority()));
			oFilter.setAbbrname(BindFilter.setFilter(Operators.IN.operator, pFilter.getAbbrname()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappDeptUser) {
			ZappDeptUser pFilter = (ZappDeptUser) pObjFilter;
			ZappDeptUser oFilter = new ZappDeptUser();
			oFilter.setDeptuserid(BindFilter.setFilter(BLANK, pFilter.getDeptuserid()));
			oFilter.setDeptid(BindFilter.setFilter(Operators.IN.operator, pFilter.getDeptid()));
			oFilter.setUserid(BindFilter.setFilter(Operators.IN.operator, pFilter.getUserid()));
			oFilter.setUsertype(BindFilter.setFilter(Operators.IN.operator, pFilter.getUsertype()));
			oFilter.setOriginyn(BindFilter.setFilter(Operators.IN.operator, pFilter.getOriginyn()));
			oFilter.setPositionid(BindFilter.setFilter(Operators.IN.operator, pFilter.getPositionid()));
			oFilter.setDutyid(BindFilter.setFilter(Operators.IN.operator, pFilter.getDutyid()));
			oFilter.setSeclevelid(BindFilter.setFilter(Operators.IN.operator, pFilter.getSeclevelid()));
			oFilter.setIssupervisor(BindFilter.setFilter(Operators.IN.operator, pFilter.getIssupervisor()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappGroup) {
			ZappGroup pFilter = (ZappGroup) pObjFilter;
			ZappGroup oFilter = new ZappGroup();
			oFilter.setGroupid(BindFilter.setFilter(BLANK, pFilter.getGroupid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setUpid(BindFilter.setFilter(Operators.IN.operator, pFilter.getUpid()));
			oFilter.setCode(BindFilter.setFilter(Operators.IN.operator, pFilter.getCode()));
			oFilter.setPriority(BindFilter.setFilter(Operators.IN.ioperator, pFilter.getPriority()));
			oFilter.setTypes(BindFilter.setFilter(Operators.IN.operator, pFilter.getTypes()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappGroupUser) {
			ZappGroupUser pFilter = (ZappGroupUser) pObjFilter;
			ZappGroupUser oFilter = new ZappGroupUser();
			oFilter.setGroupuserid(BindFilter.setFilter(BLANK, pFilter.getGroupuserid()));
			oFilter.setGobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getGobjid()));
			oFilter.setGroupid(BindFilter.setFilter(Operators.IN.operator, pFilter.getGroupid()));
			oFilter.setGobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getGobjtype()));
			oFilter.setEditable(BindFilter.setFilter(Operators.IN.operator, pFilter.getEditable()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappUser) {
			ZappUser pFilter = (ZappUser) pObjFilter;
			ZappUser oFilter = new ZappUser();
			oFilter.setUserid(BindFilter.setFilter(BLANK, pFilter.getUserid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setLoginid(BindFilter.setFilter(BLANK, pFilter.getLoginid()));
			oFilter.setPasswd(BindFilter.setFilter(BLANK, pFilter.getPasswd()));
			oFilter.setEmpno(BindFilter.setFilter(BLANK, pFilter.getEmpno()));
			oFilter.setEmail(BindFilter.setFilter(BLANK, pFilter.getEmail()));
			oFilter.setPasswdsalt(BindFilter.setFilter(BLANK, pFilter.getPasswdsalt()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappOrganTask) {
			ZappOrganTask pFilter = (ZappOrganTask) pObjFilter;
			ZappOrganTask oFilter = new ZappOrganTask();
			oFilter.setOrgantaskid(BindFilter.setFilter(BLANK, pFilter.getOrgantaskid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setDeptid(BindFilter.setFilter(Operators.IN.operator, pFilter.getDeptid()));
			oFilter.setTaskid(BindFilter.setFilter(Operators.IN.operator, pFilter.getTaskid()));
			oFilter.setTobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getTobjtype()));
			return oFilter;
		}
		
		return null;

	}

}

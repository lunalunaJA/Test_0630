/**
 *
 */
package com.zenithst.core.tag.bind;

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
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.tag.vo.ZappImg;
import com.zenithst.core.tag.vo.ZappTag;
import com.zenithst.core.tag.vo.ZappTaskTag;
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
public class ZappTagBinder extends ZappDomain {
	
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
		if(pValue instanceof ZappImg) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappImg) pValue).getAppid(), BLANK);
		}
		if(pValue instanceof ZappTag) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappTag) pValue).getTagid(), BLANK);
		}
		if(pValue instanceof ZappTaskTag) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappTaskTag) pValue).getTasktagid(), BLANK);
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
		
		if(pValue instanceof ZappImg) {
			ZappImg pvo = (ZappImg) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {

				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("appid")) continue;
	            
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
		if(pValue instanceof ZappTag) {
			ZappTag pvo = (ZappTag) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {

				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("tagid")) continue;
	            
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
		if(pValue instanceof ZappTaskTag) {
			ZappTaskTag pvo = (ZappTaskTag) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {

				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("tasktagid")) continue;
	            
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
	 * 등록을 위한 필수 정보를 체크한다.
	 * @param pValue
	 * @return
	 */
	public ZstFwResult isEmpty(ZappAuth pObjAuth, Object pValue, ZstFwResult pObjRes) {
		
		if(pValue != null) {
			
			/* Company */
			if(pValue instanceof ZappImg) {
				ZappImg pObj = (ZappImg) pValue;
				if(!ZstFwValidatorUtils.valid(pObj.getState())) {
					return ZappFinalizing.finalising("누락_상태", "[isEmpty] " + messageService.getMessage("누락_상태",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* Tag */
			if(pValue instanceof ZappTag) {
				ZappTag pObj = (ZappTag) pValue;
				if(!ZstFwValidatorUtils.valid(pObj.getCompanyid())) {
					return ZappFinalizing.finalising("ERR_MIS_COMPANYID", "[isEmpty] " + messageService.getMessage("ERR_MIS_COMPANYID",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getName())) {
					return ZappFinalizing.finalising("ERR_MIS_NAME", "[isEmpty] " + messageService.getMessage("ERR_MIS_NAME",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getCode())) {
					return ZappFinalizing.finalising("ERR_MIS_CODE", "[isEmpty] " + messageService.getMessage("ERR_MIS_CODE",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
			}
			
			/* Task Tag */
			if(pValue instanceof ZappTaskTag) {
				ZappTaskTag pObj = (ZappTaskTag) pValue;
				if(!ZstFwValidatorUtils.valid(pObj.getTagid())) {
					return ZappFinalizing.finalising("누락_태그아이디", "[isEmpty] " + messageService.getMessage("누락_태그아이디",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
				}
				if(!ZstFwValidatorUtils.valid(pObj.getTaskid())) {
					return ZappFinalizing.finalising("누락_업무아이디", "[isEmpty] " + messageService.getMessage("누락_업무아이디",  pObjAuth.getObjlang()), pObjAuth.getObjlang());
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

		if(pFilter instanceof ZappImg) {
			ZappImg oFilter = new ZappImg();
			oFilter.setAppid(Operators.EQUAL.operator);
			oFilter.setCreatorid(Operators.EQUAL.operator);
			oFilter.setCreatetime(Operators.BETWEEN.operator);
			oFilter.setState(Operators.IN.operator);
			oFilter.setIdx01(Operators.EQUAL.operator);
			oFilter.setIdx02(Operators.EQUAL.operator);
			oFilter.setIdx03(Operators.EQUAL.operator);
			oFilter.setIdx04(Operators.EQUAL.operator);
			oFilter.setIdx05(Operators.EQUAL.operator);
			oFilter.setIdx06(Operators.EQUAL.operator);
			oFilter.setIdx07(Operators.EQUAL.operator);
			oFilter.setIdx08(Operators.EQUAL.operator);
			oFilter.setIdx09(Operators.EQUAL.operator);
			oFilter.setIdx10(Operators.EQUAL.operator);
			oFilter.setIdx11(Operators.EQUAL.operator);
			oFilter.setIdx12(Operators.EQUAL.operator);
			oFilter.setIdx13(Operators.EQUAL.operator);
			oFilter.setIdx14(Operators.EQUAL.operator);
			oFilter.setIdx15(Operators.EQUAL.operator);
			oFilter.setIdx16(Operators.EQUAL.operator);
			oFilter.setIdx17(Operators.EQUAL.operator);
			oFilter.setIdx18(Operators.EQUAL.operator);
			oFilter.setIdx19(Operators.EQUAL.operator);
			oFilter.setIdx20(Operators.EQUAL.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappTag) {
			ZappTag oFilter = new ZappTag();
			oFilter.setTagid(Operators.EQUAL.operator);
			oFilter.setCompanyid(Operators.EQUAL.operator);
			oFilter.setName(Operators.LIKE.operator);
			oFilter.setCode(Operators.IN.operator);
			oFilter.setIsactive(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappTaskTag) {
			ZappTaskTag oFilter = new ZappTaskTag();
			oFilter.setTasktagid(Operators.EQUAL.operator);
			oFilter.setTaskid(Operators.IN.operator);
			oFilter.setTagid(Operators.IN.operator);
			oFilter.setIsallowednull(Operators.EQUAL.operator);
			oFilter.setIscreatedindex(Operators.EQUAL.operator);
			oFilter.setIsunique(Operators.EQUAL.operator);
			oFilter.setDefaultvalue(Operators.EQUAL.operator);
			oFilter.setIssearchable(Operators.EQUAL.operator);
			oFilter.setDatatype(Operators.IN.operator);
			oFilter.setQuerytype(Operators.IN.operator);
			oFilter.setSeqno(Operators.IN.operator);
			oFilter.setIncludepk(Operators.EQUAL.operator);
			oFilter.setIsactive(Operators.EQUAL.operator);
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
		
		if(pObjFilter instanceof ZappImg) {
			ZappImg pFilter = (ZappImg) pObjFilter;
			ZappImg oFilter = new ZappImg();
			oFilter.setAppid(BindFilter.setFilter(BLANK, pFilter.getAppid()));
			oFilter.setCreatorid(BindFilter.setFilter(BLANK, pFilter.getCreatorid()));
			oFilter.setCreatetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getCreatetime()));
			oFilter.setState(BindFilter.setFilter(Operators.IN.operator, pFilter.getState()));
			oFilter.setIdx01(BindFilter.setFilter(BLANK, pFilter.getIdx01()));
			oFilter.setIdx02(BindFilter.setFilter(BLANK, pFilter.getIdx02()));
			oFilter.setIdx03(BindFilter.setFilter(BLANK, pFilter.getIdx03()));
			oFilter.setIdx04(BindFilter.setFilter(BLANK, pFilter.getIdx04()));
			oFilter.setIdx05(BindFilter.setFilter(BLANK, pFilter.getIdx05()));
			oFilter.setIdx06(BindFilter.setFilter(BLANK, pFilter.getIdx06()));
			oFilter.setIdx07(BindFilter.setFilter(BLANK, pFilter.getIdx07()));
			oFilter.setIdx08(BindFilter.setFilter(BLANK, pFilter.getIdx08()));
			oFilter.setIdx09(BindFilter.setFilter(BLANK, pFilter.getIdx09()));
			oFilter.setIdx10(BindFilter.setFilter(BLANK, pFilter.getIdx10()));
			oFilter.setIdx11(BindFilter.setFilter(BLANK, pFilter.getIdx11()));
			oFilter.setIdx12(BindFilter.setFilter(BLANK, pFilter.getIdx12()));
			oFilter.setIdx13(BindFilter.setFilter(BLANK, pFilter.getIdx13()));
			oFilter.setIdx14(BindFilter.setFilter(BLANK, pFilter.getIdx14()));
			oFilter.setIdx15(BindFilter.setFilter(BLANK, pFilter.getIdx15()));
			oFilter.setIdx16(BindFilter.setFilter(BLANK, pFilter.getIdx16()));
			oFilter.setIdx17(BindFilter.setFilter(BLANK, pFilter.getIdx17()));
			oFilter.setIdx18(BindFilter.setFilter(BLANK, pFilter.getIdx18()));
			oFilter.setIdx19(BindFilter.setFilter(BLANK, pFilter.getIdx19()));
			oFilter.setIdx20(BindFilter.setFilter(BLANK, pFilter.getIdx20()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappTag) {
			ZappTag pFilter = (ZappTag) pObjFilter;
			ZappTag oFilter = new ZappTag();
			oFilter.setTagid(BindFilter.setFilter(BLANK, pFilter.getTagid()));
			oFilter.setCompanyid(BindFilter.setFilter(BLANK, pFilter.getCompanyid()));
			oFilter.setName(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getName()));
			oFilter.setCode(BindFilter.setFilter(Operators.IN.operator, pFilter.getCode()));
			oFilter.setIsactive(BindFilter.setFilter(Operators.IN.operator, pFilter.getIsactive()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappTaskTag) {
			ZappTaskTag pFilter = (ZappTaskTag) pObjFilter;
			ZappTaskTag oFilter = new ZappTaskTag();
			oFilter.setTasktagid(BindFilter.setFilter(BLANK, pFilter.getTasktagid()));
			oFilter.setTaskid(BindFilter.setFilter(Operators.IN.operator, pFilter.getTaskid()));
			oFilter.setTagid(BindFilter.setFilter(Operators.IN.operator, pFilter.getTagid()));
			oFilter.setIsallowednull(BindFilter.setFilter(BLANK, pFilter.getIsallowednull()));
			oFilter.setIscreatedindex(BindFilter.setFilter(BLANK, pFilter.getIscreatedindex()));
			oFilter.setIsunique(BindFilter.setFilter(BLANK, pFilter.getIsunique()));
			oFilter.setDefaultvalue(BindFilter.setFilter(BLANK, pFilter.getDefaultvalue()));
			oFilter.setIssearchable(BindFilter.setFilter(BLANK, pFilter.getIssearchable()));
			oFilter.setDatatype(BindFilter.setFilter(Operators.IN.operator, pFilter.getDatatype()));
			oFilter.setQuerytype(BindFilter.setFilter(Operators.IN.operator, pFilter.getQuerytype()));
			oFilter.setSeqno(BindFilter.setFilter(Operators.IN.operator, pFilter.getSeqno()));
			oFilter.setIncludepk(BindFilter.setFilter(BLANK, pFilter.getIncludepk()));
			oFilter.setIsactive(BindFilter.setFilter(BLANK, pFilter.getIsactive()));
			return oFilter;
		}
				
		return null;

	}

}

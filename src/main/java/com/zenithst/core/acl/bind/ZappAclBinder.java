/**
 *
 */
package com.zenithst.core.acl.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.core.acl.vo.ZappClassAcl;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.common.extend.ZappDomain;
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
public class ZappAclBinder extends ZappDomain {
	
	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());


	/**
	 * <p><b>
	 * Check if PK is entered or not.
	 * </b></p>
	 * 
	 * @param pValue Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclid</td><td>String</td><td>PK</td><td></td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return Boolean <br>
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappClassAcl) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappClassAcl) pValue).getAclid(), BLANK);
		}
		if(pValue instanceof ZappContentAcl) {
			pk = ZstFwValidatorUtils.fixNullString(((ZappContentAcl) pValue).getAclid(), BLANK);
		}

		if(pk.equals(BLANK)){
			return true;
		}
		
		return false;
	}	

	
	/**
	 * <p><b>
	 * Checks for missing values ​​except for the PK. 
	 * </b></p>
	 * 
	 * @param pValue Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>classid</td><td>String</td><td>Classification ID (Folder)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:Viewing not allowed + Registering not allowed, 1:Viewing allowed + Registering not allowed, 2:Viewing allowed + Registering allowed) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Type</b></td><td><b>Desc.</b></td><td><b>Required?</b></td>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contentid</td><td>String</td><td>Content ID (Bundle / File) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>contenttype</td><td>String</td><td>Content type (01:Bundle, 02:File) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjid</td><td>String</td><td>Target ID (Dept. user ID / Department ID / Group ID)</td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>aclobjtype</td><td>String</td><td>Target type (01:User, 02:Department, 03:Group) </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 			<tr bgcolor="#95BEE1">
	 * 				<td>acls</td><td>Integer</td><td>Access control value <br>0:No access, 1:List, 2:View, 3:Print, 5:Edit </td><td style="color: white; background:red;" >Required</td>
	 * 			</tr>
	 * 		  </table><br>
	 * @return Boolean <br>
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */	
	public boolean isEmpty(Object pValue) {
		
		StringBuffer strtype = new StringBuffer();
		
		if(pValue instanceof ZappClassAcl) {
			ZappClassAcl pvo = (ZappClassAcl) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("aclid")) continue;
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
						if(inttype > MINUS_ONE) strtype.append(inttype);
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
		
		if(pValue instanceof ZappContentAcl) {
			ZappContentAcl pvo = (ZappContentAcl) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("aclid")) continue;
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
						if(inttype > MINUS_ONE) strtype.append(inttype);
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
	 * <p><b>
	 * Set default filtering options.
	 * </b></p>
	 * 
	 * @param pFilter Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @return Boolean <br>
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */	
	public Object getFilter(Object pFilter) {

		if(pFilter instanceof ZappClassAcl) {
			ZappClassAcl oFilter = new ZappClassAcl();
			oFilter.setAclid(Operators.IN.operator);
			oFilter.setClassid(Operators.IN.operator);
			oFilter.setAclobjid(Operators.IN.operator);
			oFilter.setAclobjtype(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappContentAcl) {
			ZappContentAcl oFilter = new ZappContentAcl();
			oFilter.setAclid(Operators.IN.operator);
			oFilter.setContentid(Operators.IN.operator);
			oFilter.setContenttype(Operators.IN.operator);
			oFilter.setAclobjid(Operators.IN.operator);
			oFilter.setAclobjtype(Operators.IN.operator);
			return oFilter;
		}
		
		return null;
		
	}

	
	/**
	 * <p><b>
	 * Set dynamic filtering options. (Reference: Filter setting is specified only for string type methods)
	 * </b></p>
	 * 
	 * @param pObjFilter Object (Not Nullable) <br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappClassAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>classid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * 		  <table width="80%" border="1">
	 *          <caption>ZappContentAcl</caption>
	 * 			<tr bgcolor="#469CE5">
	 * 				<td><b>Name</b></td><td><b>Default filter</b>
	 * 			</tr>	
	 * 			<tr bgcolor="#95BEE1"><td>aclid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contentid</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>contenttype</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>acliobjd</td><td>IN</td></tr>
	 * 			<tr bgcolor="#95BEE1"><td>aclobjtype</td><td>IN</td></tr>
	 * 		  </table><br>
	 * @return Boolean <br>
	 * @see ZappClassAcl
	 * @see ZappContentAcl
	 */		
	public Object getDynamicFilter(Object pObjFilter) {
		
		if(pObjFilter instanceof ZappClassAcl) {
			ZappClassAcl pFilter = (ZappClassAcl) pObjFilter;
			ZappClassAcl oFilter = new ZappClassAcl();
			oFilter.setAclid(BindFilter.setFilter(Operators.IN.operator, pFilter.getAclid()));
			oFilter.setClassid(BindFilter.setFilter(Operators.IN.operator, pFilter.getClassid()));
			oFilter.setAclobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getAclobjid()));
			oFilter.setAclobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getAclobjtype()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappContentAcl) {
			ZappContentAcl pFilter = (ZappContentAcl) pObjFilter;
			ZappContentAcl oFilter = new ZappContentAcl();
			oFilter.setAclid(BindFilter.setFilter(Operators.IN.operator, pFilter.getAclid()));
			oFilter.setContentid(BindFilter.setFilter(Operators.IN.operator, pFilter.getAclobjid()));
			oFilter.setContenttype(BindFilter.setFilter(Operators.IN.operator, pFilter.getContenttype()));
			oFilter.setAclobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getAclobjid()));
			oFilter.setAclobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getAclobjtype()));
			return oFilter;
		}
		
		return null;

	}

}

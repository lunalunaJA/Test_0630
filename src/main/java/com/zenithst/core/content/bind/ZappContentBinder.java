/**
 *
 */
package com.zenithst.core.content.bind;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.archive.util.BindFilter;
import com.zenithst.archive.vo.ZArchMFile;
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
import com.zenithst.core.content.vo.ZappMarkedObject;
import com.zenithst.core.content.vo.ZappSharedObject;
import com.zenithst.core.content.vo.ZappTmpObject;
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
public class ZappContentBinder extends ZappDomain {

	/* Log */
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Check if PK is entered or not.
	 * @param pValue
	 * @return
	 */
	public boolean isEmptyPk(Object pValue) {

		String pk = BLANK;
		if(pValue instanceof ZappBundle) {	// Bundle
			pk = ZstFwValidatorUtils.fixNullString(((ZappBundle) pValue).getBundleid(), BLANK);
		}
		if(pValue instanceof ZappAdditoryBundle) {	// AdditoryBundle
			pk = ZstFwValidatorUtils.fixNullString(((ZappAdditoryBundle) pValue).getBundleid(), BLANK);
		}
		if(pValue instanceof ZappFile) {	// File
			pk = ZstFwValidatorUtils.fixNullString(((ZappFile) pValue).getMfileid(), BLANK);
		}
		if(pValue instanceof ZappClassObject) {	// Classification
			pk = ZstFwValidatorUtils.fixNullString(((ZappClassObject) pValue).getClassobjid(), BLANK);
		}
		if(pValue instanceof ZappLinkedObject) {	// Link
			pk = ZstFwValidatorUtils.fixNullString(((ZappLinkedObject) pValue).getLinkedobjid(), BLANK);
		}
		if(pValue instanceof ZappSharedObject) {	// Share
			pk = ZstFwValidatorUtils.fixNullString(((ZappSharedObject) pValue).getShareobjid(), BLANK);
		}
		if(pValue instanceof ZappLockedObject) {	// Lock
			pk = ZstFwValidatorUtils.fixNullString(((ZappLockedObject) pValue).getLockobjid(), BLANK);
		}
		if(pValue instanceof ZappTmpObject) {	 // Temporary
			pk = ZstFwValidatorUtils.fixNullString(((ZappTmpObject) pValue).getTmpobjid(), BLANK);
		}
		if(pValue instanceof ZappMarkedObject) {	 // Mark
			pk = ZstFwValidatorUtils.fixNullString(((ZappMarkedObject) pValue).getMarkedobjid(), BLANK);
		}
		if(pValue instanceof ZappKeywordObject) {	 // Keyword
			pk = ZstFwValidatorUtils.fixNullString(((ZappKeywordObject) pValue).getKwobjid(), BLANK);
		}
		if(pValue instanceof ZappContentWorkflow) {	 // Content-Workflow
			pk = ZstFwValidatorUtils.fixNullString(((ZappContentWorkflow) pValue).getCwfid(), BLANK);
		}
		if(pValue instanceof ZappComment) {	 // Comment
			pk = ZstFwValidatorUtils.fixNullString(((ZappComment) pValue).getCommentid(), BLANK);
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
		
		if(pValue instanceof ZappBundle) {
			ZappBundle pvo = (ZappBundle) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("bundleid")) continue;
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

		if(pValue instanceof ZappAdditoryBundle) {
			ZappAdditoryBundle pvo = (ZappAdditoryBundle) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("bundleid")) continue;
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
		
		if(pValue instanceof ZappFile) {
			ZappFile pvo = (ZappFile) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("mfileid") || field.getName().equals("isreleased")) continue;
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
		
		if(pValue instanceof ZArchMFile) {
			ZArchMFile pvo = (ZArchMFile) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("mfileid")) continue;
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
		
		if(pValue instanceof ZappClassObject) {
			ZappClassObject pvo = (ZappClassObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("classobjid")) continue;
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
		
		if(pValue instanceof ZappLinkedObject) {
			ZappLinkedObject pvo = (ZappLinkedObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("linkedobjid")) continue;
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
		
		if(pValue instanceof ZappSharedObject) {
			ZappSharedObject pvo = (ZappSharedObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("shareobjid")) continue;
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
		
		if(pValue instanceof ZappLockedObject) {
			ZappLockedObject pvo = (ZappLockedObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("lockobjid")) continue;
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
		
		if(pValue instanceof ZappTmpObject) {
			ZappTmpObject pvo = (ZappTmpObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("tmpobjid")) continue;
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
		
		if(pValue instanceof ZappKeyword) {
			ZappKeyword pvo = (ZappKeyword) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("kwordid")) continue;
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
		
		if(pValue instanceof ZappKeywordObject) {
			ZappKeywordObject pvo = (ZappKeywordObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("kwobjid")) continue;
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
		
		if(pValue instanceof ZappMarkedObject) {
			ZappMarkedObject pvo = (ZappMarkedObject) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("markedobjid")) continue;
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
		
		if(pValue instanceof ZappContentWorkflow) {
			ZappContentWorkflow pvo = (ZappContentWorkflow) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("cwfid")) continue;
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
		
		// Comment
		if(pValue instanceof ZappComment) {
			ZappComment pvo = (ZappComment) pValue; 
			for(Field field : pvo.getClass().getDeclaredFields()) {
				field.setAccessible(true);
	            if(field.getName().startsWith("obj") || field.getName().equals("commentid")) continue;
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
	
	public boolean isEmpty_File(ZappFile pValue) {
		
		if(ZstFwValidatorUtils.valid(pValue.getFno()) == true) {
			return false;
		}
		if(ZstFwValidatorUtils.valid(pValue.getRetentionid()) == true) {
			return false;
		}
		if(ZstFwValidatorUtils.valid(pValue.getExpiretime()) == true) {
			return false;
		}
		if(ZstFwValidatorUtils.valid(pValue.getHolderid()) == true) {
			return false;
		}
		if(ZstFwValidatorUtils.valid(pValue.getCreatorname()) == true) {
			return false;
		}
		if(ZstFwValidatorUtils.valid(pValue.getDiscarderid()) == true) {
			return false;
		}		
		if(ZstFwValidatorUtils.valid(pValue.getExt()) == true) {
			return false;
		}		
		if(ZstFwValidatorUtils.valid(pValue.getDynamic01()) == true) {
			return false;
		}
		if(ZstFwValidatorUtils.valid(pValue.getDynamic02()) == true) {
			return false;
		}
		if(ZstFwValidatorUtils.valid(pValue.getDynamic03()) == true) {
			return false;
		}

		return true;
	}

	/**
	 * Set default filtering options.
	 * @param Object
	 * @return Object
	 * @sample
	 */
	public Object getFilter(Object pFilter) {

		if(pFilter instanceof ZappBundle) {
			ZappBundle oFilter = new ZappBundle();
			oFilter.setBundleid(Operators.EQUAL.operator);
			oFilter.setBno(Operators.LIKE.operator);
			oFilter.setTitle(Operators.LIKE.operator);
			oFilter.setCreatorid(Operators.EQUAL.operator);
			oFilter.setCreatorname(Operators.LIKE.operator);
			oFilter.setHolderid(Operators.EQUAL.operator);
			oFilter.setCreatetime(Operators.BETWEEN.operator);
			oFilter.setUpdatetime(Operators.BETWEEN.operator);
			oFilter.setRetentionid(Operators.IN.operator);
			oFilter.setExpiretime(Operators.BETWEEN.operator);
			oFilter.setRetentionid(Operators.IN.operator);
			oFilter.setDiscarderid(Operators.IN.operator);
			oFilter.setState(Operators.IN.operator);
			return oFilter;
		}

		if(pFilter instanceof ZappAdditoryBundle) {
			ZappAdditoryBundle oFilter = new ZappAdditoryBundle();
			oFilter.setBundleid(Operators.EQUAL.operator);
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
			oFilter.setSummary(Operators.LIKE.operator);
			return oFilter;
		}		
		
		if(pFilter instanceof ZappFile) {
			ZappFile oFilter = new ZappFile();
			oFilter.setMfileid(Operators.EQUAL.operator);
			oFilter.setFno(Operators.LIKE.operator);
			oFilter.setLinkid(Operators.IN.operator);
			oFilter.setFilename(Operators.EQUAL.operator);
			oFilter.setCreator(Operators.EQUAL.operator);
			oFilter.setCreatorname(Operators.LIKE.operator);
			oFilter.setHolderid(Operators.EQUAL.operator);
			oFilter.setCreatetime(Operators.BETWEEN.operator);
			oFilter.setUpdatetime(Operators.BETWEEN.operator);
			oFilter.setExpiretime(Operators.BETWEEN.operator);
			oFilter.setDiscarderid(Operators.IN.operator);
			oFilter.setExt(Operators.IN.operator);
			oFilter.setRetentionid(Operators.IN.operator);
			oFilter.setState(Operators.IN.operator);
			oFilter.setDynamic01(Operators.IN.operator);
			oFilter.setDynamic02(Operators.IN.operator);
			oFilter.setDynamic03(Operators.IN.operator);
			oFilter.setSummary(Operators.LIKE.operator);
			return oFilter;
		}		
		
		if(pFilter instanceof ZArchMFile) {
			ZArchMFile oFilter = new ZArchMFile();
			oFilter.setMfileid(Operators.EQUAL.operator);
			oFilter.setLinkid(Operators.IN.operator);
			oFilter.setFilename(Operators.LIKE.operator);
			oFilter.setCreator(Operators.EQUAL.operator);
			oFilter.setCreatetime(Operators.BETWEEN.operator);
			oFilter.setUpdatetime(Operators.BETWEEN.operator);
			oFilter.setExpiredate(Operators.BETWEEN.operator);
			oFilter.setState(Operators.IN.operator);
			return oFilter;
		}		
		
		if(pFilter instanceof ZappClassObject) {
			ZappClassObject oFilter = new ZappClassObject();
			oFilter.setClassobjid(Operators.IN.operator);
			oFilter.setClassid(Operators.IN.operator);
			oFilter.setClasstype(Operators.IN.operator);
			oFilter.setCobjid(Operators.IN.operator);
			oFilter.setCobjtype(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappLinkedObject) {
			ZappLinkedObject oFilter = new ZappLinkedObject();
			oFilter.setLinkedobjid(Operators.IN.operator);
			oFilter.setSourceid(Operators.EQUAL.operator);
			oFilter.setTargetid(Operators.EQUAL.operator);
			oFilter.setLinkerid(Operators.EQUAL.operator);
			oFilter.setLinktime(Operators.BETWEEN.operator);
			oFilter.setLinktype(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappSharedObject) {
			ZappSharedObject oFilter = new ZappSharedObject();
			oFilter.setShareobjid(Operators.IN.operator);
			oFilter.setSobjid(Operators.IN.operator);
			oFilter.setSobjtype(Operators.IN.operator);
			oFilter.setSharerid(Operators.EQUAL.operator);
			oFilter.setReaderid(Operators.EQUAL.operator);
			oFilter.setReadertype(Operators.IN.operator);
			oFilter.setSharetime(Operators.BETWEEN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappLockedObject) {
			ZappLockedObject oFilter = new ZappLockedObject();
			oFilter.setLockobjid(Operators.IN.operator);
			oFilter.setLobjid(Operators.IN.operator);
			oFilter.setLobjtype(Operators.IN.operator);
//			oFilter.setLobjfileid(Operators.IN.operator);
			oFilter.setLockerid(Operators.EQUAL.operator);
			oFilter.setLocktime(Operators.BETWEEN.operator);
			oFilter.setReleasetime(Operators.BETWEEN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappTmpObject) {
			ZappTmpObject oFilter = new ZappTmpObject();
			oFilter.setTmpobjid(Operators.EQUAL.operator);
			oFilter.setTobjid(Operators.IN.operator);
			oFilter.setTobjtype(Operators.IN.operator);
			return oFilter;
		}		
		
		if(pFilter instanceof ZappKeyword) {
			ZappKeyword oFilter = new ZappKeyword();
			oFilter.setKwordid(Operators.EQUAL.operator);
			oFilter.setKword(Operators.IN.operator);
			oFilter.setIsactive(Operators.EQUAL.operator);
			return oFilter;
		}	
		
		if(pFilter instanceof ZappKeywordObject) {
			ZappKeywordObject oFilter = new ZappKeywordObject();
			oFilter.setKwobjid(Operators.EQUAL.operator);
			oFilter.setKobjid(Operators.IN.operator);
			oFilter.setKwordid(Operators.IN.operator);
			oFilter.setKobjtype(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappMarkedObject) {
			ZappMarkedObject oFilter = new ZappMarkedObject();
			oFilter.setMarkedobjid(Operators.EQUAL.operator);
			oFilter.setMobjid(Operators.IN.operator);
			oFilter.setMobjtype(Operators.IN.operator);
			oFilter.setMarkerid(Operators.IN.operator);
			oFilter.setMarktime(Operators.BETWEEN.operator);
			return oFilter;
		}

		if(pFilter instanceof ZappContentWorkflow) {
			ZappContentWorkflow oFilter = new ZappContentWorkflow();
			oFilter.setCwfid(Operators.EQUAL.operator);
			oFilter.setContentid(Operators.IN.operator);
			oFilter.setContenttype(Operators.IN.operator);
			oFilter.setDrafterid(Operators.IN.operator);
			oFilter.setDraftername(Operators.LIKE.operator);
			oFilter.setWferid(Operators.IN.operator);
			oFilter.setWfername(Operators.LIKE.operator);
			oFilter.setWftime(Operators.BETWEEN.operator);
			oFilter.setComments(Operators.LIKE.operator);
			oFilter.setStatus(Operators.IN.operator);
			return oFilter;
		}
		
		if(pFilter instanceof ZappComment) {
			ZappComment oFilter = new ZappComment();
			oFilter.setCommentid(Operators.EQUAL.operator);
			oFilter.setCobjid(Operators.IN.operator);
			oFilter.setCobjtype(Operators.IN.operator);
			oFilter.setCommenttime(Operators.BETWEEN.operator);
			oFilter.setCommenterid(Operators.IN.operator);
			oFilter.setCommenter(Operators.LIKE.operator);
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
		
		if(pObjFilter instanceof ZappBundle) {
			ZappBundle pFilter = (ZappBundle) pObjFilter;
			ZappBundle oFilter = new ZappBundle();
			oFilter.setBundleid(BindFilter.setFilter(BLANK, pFilter.getBundleid()));
			oFilter.setBno(BindFilter.setFilter(BLANK, pFilter.getBno()));
			oFilter.setTitle(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getTitle()));
			oFilter.setCreatorid(BindFilter.setFilter(BLANK, pFilter.getCreatorid()));
			oFilter.setCreatorname(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getCreatorname()));
			oFilter.setHolderid(BindFilter.setFilter(BLANK, pFilter.getHolderid()));
			oFilter.setCreatetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getCreatetime()));
			oFilter.setUpdatetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getUpdatetime()));
			oFilter.setRetentionid(BindFilter.setFilter(Operators.IN.operator, pFilter.getRetentionid()));
			oFilter.setExpiretime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getExpiretime()));
			oFilter.setRetentionid(BindFilter.setFilter(Operators.IN.operator, pFilter.getRetentionid()));
			oFilter.setDiscarderid(BindFilter.setFilter(BLANK, pFilter.getDiscarderid()));
			oFilter.setState(BindFilter.setFilter(Operators.IN.operator, pFilter.getState()));
			return oFilter;
		}

		if(pObjFilter instanceof ZappAdditoryBundle) {
			ZappAdditoryBundle pFilter = (ZappAdditoryBundle) pObjFilter;
			ZappAdditoryBundle oFilter = new ZappAdditoryBundle();
			oFilter.setBundleid(BindFilter.setFilter(BLANK, pFilter.getBundleid()));
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
			oFilter.setSummary(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getSummary()));
			return oFilter;
		}		
		
		if(pObjFilter instanceof ZappFile) {
			ZappFile pFilter = (ZappFile) pObjFilter;
			ZappFile oFilter = new ZappFile();
			oFilter.setMfileid(BindFilter.setFilter(BLANK, pFilter.getMfileid()));
			oFilter.setFno(BindFilter.setFilter(BLANK, pFilter.getFno()));
			oFilter.setLinkid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLinkid()));
			oFilter.setCreator(BindFilter.setFilter(BLANK, pFilter.getCreator()));
			oFilter.setCreatorname(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getCreatorname()));
			oFilter.setHolderid(BindFilter.setFilter(BLANK, pFilter.getHolderid()));
			oFilter.setCreatetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getCreatetime()));
			oFilter.setUpdatetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getUpdatetime()));
			oFilter.setExpiredate(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getExpiretime()));
			oFilter.setExpiretime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getExpiretime()));
			oFilter.setState(BindFilter.setFilter(Operators.IN.operator, pFilter.getState()));
			oFilter.setRetentionid(BindFilter.setFilter(Operators.IN.operator, pFilter.getRetentionid()));
			oFilter.setDiscarderid(BindFilter.setFilter(Operators.IN.operator, pFilter.getDiscarderid()));
			oFilter.setExt(BindFilter.setFilter(Operators.IN.operator, pFilter.getExt()));
			oFilter.setDynamic01(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic01()));
			oFilter.setDynamic02(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic02()));
			oFilter.setDynamic03(BindFilter.setFilter(Operators.IN.operator, pFilter.getDynamic03()));
			oFilter.setSummary(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getSummary()));
			return oFilter;
		}		
		
		if(pObjFilter instanceof ZArchMFile) {
			ZArchMFile pFilter = (ZArchMFile) pObjFilter;
			ZArchMFile oFilter = new ZArchMFile();
			oFilter.setMfileid(BindFilter.setFilter(BLANK, pFilter.getMfileid()));
			oFilter.setLinkid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLinkid()));
			oFilter.setFilename(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getFilename()));
			oFilter.setCreator(BindFilter.setFilter(BLANK, pFilter.getCreator()));
			oFilter.setCreatetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getCreatetime()));
			oFilter.setUpdatetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getUpdatetime()));
			oFilter.setExpiredate(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getExpiredate()));
			oFilter.setState(BindFilter.setFilter(Operators.IN.operator, pFilter.getState()));
			return oFilter;
		}			
		
		if(pObjFilter instanceof ZappClassObject) {
			ZappClassObject pFilter = (ZappClassObject) pObjFilter;
			ZappClassObject oFilter = new ZappClassObject();
			oFilter.setClassobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getClassobjid()));
			oFilter.setClassid(BindFilter.setFilter(Operators.IN.operator, pFilter.getClassid()));
			oFilter.setClasstype(BindFilter.setFilter(Operators.IN.operator, pFilter.getClasstype()));
			oFilter.setCobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getCobjid()));
			oFilter.setCobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getCobjtype()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappLinkedObject) {
			ZappLinkedObject pFilter = (ZappLinkedObject) pObjFilter;
			ZappLinkedObject oFilter = new ZappLinkedObject();
			oFilter.setLinkedobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLinkedobjid()));
			oFilter.setSourceid(BindFilter.setFilter(BLANK, pFilter.getSourceid()));
			oFilter.setTargetid(BindFilter.setFilter(BLANK, pFilter.getTargetid()));
			oFilter.setLinkerid(BindFilter.setFilter(BLANK, pFilter.getLinkerid()));
			oFilter.setLinktime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getLinktime()));
			oFilter.setLinktype(BindFilter.setFilter(Operators.IN.operator, pFilter.getLinktype()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappSharedObject) {
			ZappSharedObject pFilter = (ZappSharedObject) pObjFilter;
			ZappSharedObject oFilter = new ZappSharedObject();
			oFilter.setShareobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getShareobjid()));
			oFilter.setSobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getSobjid()));
			oFilter.setSobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getSobjtype()));
			oFilter.setSharerid(BindFilter.setFilter(BLANK, pFilter.getSharerid()));
			oFilter.setReaderid(BindFilter.setFilter(BLANK, pFilter.getReaderid()));
			oFilter.setReadertype(BindFilter.setFilter(Operators.IN.operator, pFilter.getReadertype()));
			oFilter.setSharetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getSharetime()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappLockedObject) {
			ZappLockedObject pFilter = (ZappLockedObject) pObjFilter;
			ZappLockedObject oFilter = new ZappLockedObject();
			oFilter.setLockobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLockobjid()));
			oFilter.setLobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLobjid()));
			oFilter.setLobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getLobjtype()));
//			oFilter.setLobjfileid(BindFilter.setFilter(Operators.IN.operator, pFilter.getLobjfileid()));
			oFilter.setLockerid(BindFilter.setFilter(BLANK, pFilter.getLockerid()));
			oFilter.setLocktime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getLocktime()));
			oFilter.setReleasetime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getReleasetime()));
			return oFilter;
		}		
		
		if(pObjFilter instanceof ZappTmpObject) {
			ZappTmpObject pFilter = (ZappTmpObject) pObjFilter;
			ZappTmpObject oFilter = new ZappTmpObject();
			oFilter.setTmpobjid(BindFilter.setFilter(BLANK, pFilter.getTmpobjid()));
			oFilter.setTobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getTobjid()));
			oFilter.setTobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getTobjtype()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappKeyword) {
			ZappKeyword pFilter = (ZappKeyword) pObjFilter;
			ZappKeyword oFilter = new ZappKeyword();
			oFilter.setKwordid(BindFilter.setFilter(BLANK, pFilter.getKwordid()));
			oFilter.setKword(BindFilter.setFilter(Operators.IN.operator, pFilter.getKword()));
			oFilter.setIsactive(BindFilter.setFilter(EQUAL, pFilter.getIsactive()));
			return oFilter;
		}	
		
		if(pObjFilter instanceof ZappKeywordObject) {
			ZappKeywordObject pFilter = (ZappKeywordObject) pObjFilter;
			ZappKeywordObject oFilter = new ZappKeywordObject();
			oFilter.setKwobjid(BindFilter.setFilter(BLANK, pFilter.getKwobjid()));
			oFilter.setKobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getKobjid()));
			oFilter.setKwordid(BindFilter.setFilter(Operators.IN.operator, pFilter.getKwordid()));
			oFilter.setKobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getKobjtype()));
			return oFilter;
		}		
		
		if(pObjFilter instanceof ZappMarkedObject) {
			ZappMarkedObject pFilter = (ZappMarkedObject) pObjFilter;
			ZappMarkedObject oFilter = new ZappMarkedObject();
			oFilter.setMarkedobjid(BindFilter.setFilter(BLANK, pFilter.getMarkedobjid()));
			oFilter.setMobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getMobjid()));
			oFilter.setMobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getMobjtype()));
			oFilter.setMarkerid(BindFilter.setFilter(Operators.IN.operator, pFilter.getMarkerid()));
			oFilter.setMarktime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getMarktime()));
			return oFilter;
		}	

		if(pObjFilter instanceof ZappContentWorkflow) {
			ZappContentWorkflow pFilter = (ZappContentWorkflow) pObjFilter;
			ZappContentWorkflow oFilter = new ZappContentWorkflow();
			oFilter.setCwfid(BindFilter.setFilter(BLANK, pFilter.getCwfid()));
			oFilter.setContentid(BindFilter.setFilter(Operators.IN.operator, pFilter.getContentid()));
			oFilter.setContenttype(BindFilter.setFilter(Operators.IN.operator, pFilter.getContenttype()));
			oFilter.setDrafterid(BindFilter.setFilter(Operators.IN.operator, pFilter.getDrafterid()));
			oFilter.setDraftername(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getDraftername()));
			oFilter.setWferid(BindFilter.setFilter(Operators.IN.operator, pFilter.getWferid()));
			oFilter.setWfername(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getWfername()));
			oFilter.setWftime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getWftime()));
			oFilter.setComments(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getComments()));
			oFilter.setStatus(BindFilter.setFilter(Operators.IN.operator, pFilter.getStatus()));
			return oFilter;
		}
		
		if(pObjFilter instanceof ZappComment) {
			ZappComment pFilter = (ZappComment) pObjFilter;
			ZappComment oFilter = new ZappComment();
			oFilter.setCommentid(BindFilter.setFilter(BLANK, pFilter.getCommentid()));
			oFilter.setCobjid(BindFilter.setFilter(Operators.IN.operator, pFilter.getCobjid()));
			oFilter.setCobjtype(BindFilter.setFilter(Operators.IN.operator, pFilter.getCobjtype()));
			oFilter.setComments(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getComments()));
			oFilter.setCommenttime(BindFilter.setFilter(Operators.BETWEEN.operator, pFilter.getCommenttime()));
			oFilter.setCommenterid(BindFilter.setFilter(Operators.IN.operator, pFilter.getCommenterid()));
			oFilter.setCommenter(BindFilter.setFilter(Operators.LIKE.operator, pFilter.getCommenter()));
			return oFilter;
		}		
		
		return null;

	}
}

package com.zenithst.core.common.utility;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenithst.core.acl.vo.ZappContentAcl;
import com.zenithst.core.classification.vo.ZappClassification;
import com.zenithst.core.content.vo.ZappAdditoryBundle;
import com.zenithst.core.content.vo.ZappClassObject;
import com.zenithst.core.content.vo.ZappFile;
import com.zenithst.core.content.vo.ZappKeyword;
import com.zenithst.core.system.vo.ZappEnv;

/**  
* <pre>
* <b>
* 1) Description : Utility class for handling JSON. <br>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappJSONUtils {

	/**
	 * Convert the input object to JSON.
	 * @param pObj
	 * @return
	 */
	static public String cvrtObjToJson(Object pObj) {
		
		ObjectMapper mapper = new ObjectMapper(); 
		String jsonStr = "";
		
		try { 
			mapper.setSerializationInclusion(Include.NON_NULL);
            jsonStr = mapper.writeValueAsString(pObj); 
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        }
		
		return jsonStr;
		
	}
	
	/**
	 * Convert JSON to the object.
	 * @param pObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static public Object cvrtJsonToObj(Object pObj, String pObjJson) {
		
		ObjectMapper mapper = new ObjectMapper(); 
		
		if(pObj != null) {
			
			if(pObj instanceof List) { 
				List<Object> rObj = (List<Object>) pObj;
				boolean[] check = {false, false, false, false, false, false, false}; 
				for(Object lObj : rObj) { 
					if(lObj instanceof ZappContentAcl) {
						check[0] = true;
					}
					if(lObj instanceof ZappClassification) {
						check[1] = true;
					}
					if(lObj instanceof ZappKeyword) {
						check[2] = true;
					}
					if(lObj instanceof ZappFile) {
						check[3] = true;
					}
					if(lObj instanceof ZappClassObject) {
						check[4] = true;
					}
					if(lObj instanceof String) {
						check[5] = true;
					}
				}
				
				try {
					if(check[0] == true) {
						List<ZappContentAcl> rList = Arrays.asList(mapper.readValue(pObjJson, ZappContentAcl[].class));
						return rList;
					}
					if(check[1] == true) {
						List<ZappClassification> rList = Arrays.asList(mapper.readValue(pObjJson, ZappClassification[].class));
						return rList;
					}
					if(check[2] == true) {
						List<ZappKeyword> rList = Arrays.asList(mapper.readValue(pObjJson, ZappKeyword[].class));
						return rList;
					}
					if(check[3] == true) {
						List<ZappFile> rList = Arrays.asList(mapper.readValue(pObjJson, ZappFile[].class));
						return rList;
					}
					if(check[4] == true) {
						System.out.println("=============");
						System.out.println(mapper.readValue(pObjJson, ZappClassObject[].class));
						List<ZappClassObject> rList = Arrays.asList(mapper.readValue(pObjJson, ZappClassObject[].class));
						return rList;
					}
					if(check[5] == true) {
						List<String> rList = Arrays.asList(mapper.readValue(pObjJson, String[].class));
						return rList;
					}
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(pObj instanceof Map) {
				Map<String, Object> rObj = (Map<String, Object>) pObj;
				for (Map.Entry<String, Object> entry : rObj.entrySet()) {
					if(entry.getValue() instanceof ZappEnv) {
						Map<String, ZappEnv> rMap = new HashMap<String, ZappEnv>();
						try {
							rMap = mapper.readValue(pObjJson, Map.class);
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return rMap;
					}
				}
			}
			
			if(pObj instanceof ZappContentAcl) {
				ZappContentAcl rObj = (ZappContentAcl) pObj;
				try {
					rObj = mapper.readValue(pObjJson, ZappContentAcl.class);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return rObj;
			}
			
			if(pObj instanceof ZappAdditoryBundle) {
				ZappAdditoryBundle rObj = (ZappAdditoryBundle) pObj;
				try {
					rObj = mapper.readValue(pObjJson, ZappAdditoryBundle.class);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return rObj;
			}

		}
		
		return pObj;
		
	}

}

package com.zenithst.core.common.message;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwValidatorUtils;

/**  
* <pre>
* <b>
* 1) Description : The purpose of this class is to manage messages. <br>
* 2) History : <br>
*         - v1.0 / 2020.11.14 / khlee / New
* 
* 3) Usage or Example : <br>
* 
*    @Autowired
*	 private ZappMessageMgtService service; <br>
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

@Service("zappMessageMgtService")
public class ZappMessageMgtServiceImpl extends ZappService implements ZappMessageMgtService {

	@Autowired @Qualifier("msg_ko")
	private Properties msg_ko;

	@Autowired @Qualifier("msg_en")
	private Properties msg_en;
	
	@Autowired @Qualifier("cap_ko")
	private Properties cap_ko;

	@Autowired @Qualifier("cap_en")
	private Properties cap_en;
	
	/**
	 * 
	 */
	public String getMessage(String key, String lang) {

		String langCode = ZstFwValidatorUtils.fixNullString(lang, ZstFwConst.LANGS.KOREAN.lang);
		String message = BLANK;

		if(langCode.equals(ZstFwConst.LANGS.KOREAN.lang)) {
			message = msg_ko.getProperty(key);
		} else if(langCode.equals(ZstFwConst.LANGS.ENGLISH.lang)) {
			message = msg_en.getProperty(key);
		} 
		
		return message;
		
	}

	/**
	 * 
	 */
	public String getCaption(String key, String lang) {
		
		String langCode = ZstFwValidatorUtils.fixNullString(lang, ZstFwConst.LANGS.KOREAN.lang);
		String caption = BLANK;

		if(langCode.equals(ZstFwConst.LANGS.KOREAN.lang)) {
			caption = cap_ko.getProperty(key);
		} else if(langCode.equals(ZstFwConst.LANGS.ENGLISH.lang)) {
			caption = cap_en.getProperty(key);
		} 
		
		return caption;
	}

	/**
	 * 
	 */
	public ZstFwResult getMessage(ZstFwResult zstFwResult, String lang) {
		
		String langCode = ZstFwValidatorUtils.fixNullString(lang, ZstFwConst.LANGS.KOREAN.lang);
		
		if(zstFwResult != null) {
			
			if(langCode.equals(ZstFwConst.LANGS.KOREAN.lang)) {
				zstFwResult.setResMessage(msg_ko.getProperty(zstFwResult.getResCode()));
			} else if(langCode.equals(ZstFwConst.LANGS.ENGLISH.lang)) {
				zstFwResult.setResMessage(msg_en.getProperty(zstFwResult.getResCode()));
			} 
			
		}
		
		return zstFwResult;
	}

	
}

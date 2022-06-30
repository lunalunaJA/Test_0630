package com.zenithst.core.authentication.salt;

import com.zenithst.archive.util.DateUtil;
import com.zenithst.archive.util.EncodeUtil;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.framework.util.ZstFwEncodeUtils;

public class ZappSalt {
	
	/**
	 * Generate new password.
	 * @param pObj
	 * @return
	 */
	public static ZappUser generatePasswd(ZappUser pObj) {
		
		StringBuffer pwd = new StringBuffer();
		
		pwd.append(pObj.getPasswd());
		pObj.setPasswdsalt(generateSalt(pObj));
		pwd.append(pObj.getPasswdsalt());
		pObj.setPasswd(ZstFwEncodeUtils.encodeString_SHA256(pwd.toString()));
		
		return pObj;
	}
	
	/**
	 * Generate new salt.
	 * @param pObj
	 * @return
	 */
	protected static String generateSalt(ZappUser pObj) {
		
		StringBuffer salt = new StringBuffer();
		
		salt.append(pObj.getLoginid());
		salt.append(pObj.getEmpno());
		salt.append(DateUtil.getCurrentDateTime());
		
		return EncodeUtil.encodeSha2(salt.toString());
	}
	
	
	/**
	 * Generate new password.
	 * @param pObj
	 * @return
	 */
	public static ZappUser generatePasswdSHA(ZappUser pObj) {
		
		StringBuffer pwd = new StringBuffer();
		
		pwd.append(pObj.getPasswd());
		pObj.setPasswdsalt(generateSaltSHA(pObj));
		pwd.append(pObj.getPasswdsalt());
		pObj.setPasswd(ZstFwEncodeUtils.encodeString_SHA256(pObj.getPasswd()));
		
		return pObj;
	}
	
	/**
	 * Generate new salt.
	 * @param pObj
	 * @return
	 */
	protected static String generateSaltSHA(ZappUser pObj) {
		
		StringBuffer salt = new StringBuffer();
		
		salt.append(pObj.getLoginid());
		salt.append(pObj.getEmpno());
		salt.append(DateUtil.getCurrentDateTime());
		
		return ZstFwEncodeUtils.encodeString_SHA256(salt.toString());
	}
}

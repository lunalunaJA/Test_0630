package com.zenithst.core.organ.vo;

import java.util.ArrayList;
import java.util.List;

import com.zenithst.core.common.extend.ZappDomain;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for group (IN) <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappGroupPar pIn = new ZappGroupPar();
*    ...
*    <br>    
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/


public class ZappGroupPar extends ZappDomain {

	private ZappGroup zappGroup = new ZappGroup();
	private ZappGroupUser zappGroupUser = new ZappGroupUser();
	private List<ZappGroupUser> zappGroupUsers = new ArrayList<ZappGroupUser>();
	private List<ZappGroupUserExtend> zappGroupUserExtends = new ArrayList<ZappGroupUserExtend>();
    
    /**
	 * <p><b>
	 * [OUT] Group Object (ZAPP_GROUP)
	 * </b></p>
	 * 
     * @return Group Object
     */ 
	public ZappGroup getZappGroup() {
		return zappGroup;
	}
    
    /**
	 * <p><b>
	 * [IN] Group Object (ZAPP_GROUP)
	 * </b></p>
     *
     * @param zappGroup Group Object
     */ 
	public void setZappGroup(ZappGroup zappGroup) {
		this.zappGroup = zappGroup;
	}
    
	/**
	 * <p><b>
	 * [OUT] Group User Object (ZAPP_GROUPUSER)
	 * </b></p>
	 * 
     * @return Group User Object
     */ 
	public ZappGroupUser getZappGroupUser() {
		return zappGroupUser;
	}
    
    /**
	 * <p><b>
	 * [IN] Group User Object (ZAPP_GROUPUSER)
	 * </b></p>
     *
     * @param zappGroupUser Group User Object
     */ 
	public void setZappGroupUser(ZappGroupUser zappGroupUser) {
		this.zappGroupUser = zappGroupUser;
	}
    
	/**
	 * <p><b>
	 * [OUT] Group User List Object (ZAPP_GROUPUSER)
	 * </b></p>
	 * 
     * @return Group User List Object
     */ 	
	public List<ZappGroupUser> getZappGroupUsers() {
		return zappGroupUsers;
	}
    
    /**
	 * <p><b>
	 * [IN] Group User List Object (ZAPP_GROUPUSER)
	 * </b></p>
     *
     * @param zappGroupUsers Group User List Object
     */ 
	public void setZappGroupUsers(List<ZappGroupUser> zappGroupUsers) {
		this.zappGroupUsers = zappGroupUsers;
	}
    
	/**
	 * <p><b>
	 * [OUT] Extendec group user List Object (ZAPP_GROUPUSER)
	 * </b></p>
	 * 
     * @return Extendec group user  List Object
     */ 
	public List<ZappGroupUserExtend> getZappGroupUserExtends() {
		return zappGroupUserExtends;
	}
    
    /**
	 * <p><b>
	 * [IN] Extendec group user List Object (ZAPP_GROUPUSER)
	 * </b></p>
     *
     * @param zappGroupUserExtends Extendec group user List Object
     */ 
	public void setZappGroupUserExtends(
			List<ZappGroupUserExtend> zappGroupUserExtends) {
		this.zappGroupUserExtends = zappGroupUserExtends;
	}
	
	
}
package com.zenithst.core.content.vo;

import com.zenithst.core.common.extend.ZappDomain;


/**  
* <pre>
* <b>
* 1) Description : Wrapper class for comment <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappComment pIn = new ZappComment();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_COMMENT</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>commentid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td></td><td>Comment ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>cobjid</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Object ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>cobjtype</td><td></td><td></td><td></td><td>VARCHAR(1)</td><td></td><td>Object Type</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>comments</td><td></td><td></td><td></td><td>VARCHAR(1000)</td><td></td><td>Comments</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>commenttime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Comment Time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>commenter</td><td></td><td></td><td></td><td>VARCHAR(200)</td><td></td><td>Commenter</td>
* 	 </tr>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappComment extends ZappDomain {

    private String commentid;
    private String cobjid;
    private String cobjtype;
    private String comments;
    private String commenttime;
    private String commenterid;
    private String commenter;
    
    /**
	 * <p><b>
	 * Default constructor
	 * </b></p>
     */ 
    public ZappComment() {}
    /**
	 * <p><b>
	 * Additional constructor #1
	 * </b></p>
     *
     * @param commentid Comment ID
     */    
    public ZappComment(String commentid) {
    	this.commentid = commentid;
    }
    
    /**
	 * <p><b>
	 * [OUT] Comment ID - Primary Key
	 * </b></p>
	 * 
     * @return Comment ID
     */
    public String getCommentid() {
        return commentid;
    }
    
    /**
	 * <p><b>
	 * [IN] Comment ID - Primary Key
	 * </b></p>
     *
     * @param commentid Comment ID
     */      
    public void setCommentid(String commentid) {
        this.commentid = commentid == null ? null : commentid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Object ID
	 * </b></p>
	 * 
     * @return Object ID
     */     
    public String getCobjid() {
        return cobjid;
    }
    
    /**
	 * <p><b>
	 * [IN] Object ID
	 * </b></p>
     *
     * @param cobjid Object ID
     */  
    public void setCobjid(String cobjid) {
        this.cobjid = cobjid == null ? null : cobjid.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Object Type
	 * </b></p>
	 * 
     * @return Object Type
     */      
    public String getCobjtype() {
		return cobjtype;
	}
    
    /**
	 * <p><b>
	 * [IN] Object Type
	 * </b></p>
     *
     * @param cobjtype Object Type
     */  
	public void setCobjtype(String cobjtype) {
		this.cobjtype = cobjtype;
	}
    
    /**
	 * <p><b>
	 * [OUT] Comments
	 * </b></p>
	 * 
     * @return Comments
     */    	
	public String getComments() {
        return comments;
    }
    
    /**
	 * <p><b>
	 * [IN] Comments
	 * </b></p>
     *
     * @param comments Comments
     */  
    public void setComments(String comments) {
        this.comments = comments == null ? null : comments.trim();
    }
    
    /**
	 * <p><b>
	 * [OUT] Comment Time
	 * </b></p>
	 * 
     * @return Comment Time
     */    
    public String getCommenttime() {
        return commenttime;
    }
    
    /**
	 * <p><b>
	 * [IN] Comment Time
	 * </b></p>
     *
     * @param commenttime Comment Time
     */  
    public void setCommenttime(String commenttime) {
        this.commenttime = commenttime == null ? null : commenttime.trim();
    }
    
	/**
	 * <p><b>
	 * [OUT] Commenter ID
	 * </b></p>
	 * 
     * @return Commenter ID
     */     
    public String getCommenterid() {
		return commenterid;
	}
    
    /**
 	 * <p><b>
 	 * [IN] Commenter ID
 	 * </b></p>
      *
      * @param commenterid Commenter ID
      */     
	public void setCommenterid(String commenterid) {
		this.commenterid = commenterid;
	}
	/**
	 * <p><b>
	 * [OUT] Commenter
	 * </b></p>
	 * 
     * @return Commenter
     */       
    public String getCommenter() {
        return commenter;
    }
    
    /**
	 * <p><b>
	 * [IN] Commenter
	 * </b></p>
     *
     * @param commenter Commenter
     */  
    public void setCommenter(String commenter) {
        this.commenter = commenter == null ? null : commenter.trim();
    }
 
}
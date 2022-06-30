package com.zenithst.core.content.vo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zenithst.archive.vo.ZArchExtend;
import com.zenithst.archive.vo.ZArchFile;
import com.zenithst.archive.vo.ZArchFormat;
import com.zenithst.archive.vo.ZArchVersion;

/**  
* <pre>
* <b>
* 1) Description : Wrapper class for version <br>
* 2) History : <br>
*         - v1.0 / 2020.11.04 / khlee / New
* 
* 3) Usage or Example : <br>
*
*    ZappFile pIn = new ZappFile();
*    ...
*    
* 4) Column Info. : <br>
* 	 <table width="80%" border="1">
*    <caption>ZAPP_MFILE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>mfileid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>ZARCH_MFILE 의 mfileid</td><td>File ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>fno</td><td></td><td></td><td>●</td><td>VARCHAR(50)</td><td></td><td>File no.</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>creatorname</td><td></td><td></td><td></td><td>VARCHAR(50)</td><td></td><td>Creator name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>holderid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Holder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>discarderid</td><td></td><td></td><td>●</td><td>VARCHAR(64)</td><td></td><td>Discarder ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>retentionid</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Retention period ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>expiretime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Expire date</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>ext</td><td></td><td></td><td></td><td>VARCHAR(30)</td><td></td><td>File extension</td>
* 	 </tr>
* 	 </table>
** 	 <table width="80%" border="1">
*    <caption>ZARCH_MFILE</caption>
* 	 <tr bgcolor="#12F0C9">
* 	   <td><b>No.</b></td><td><b>Fields</b></td><td><b>PK?</b></td><td><b>FK?</b></td><td><b>Null?</b></td><td><b>Size</b></td><td><b>Key</b></td><td><b>Note</b></td>
* 	 </tr>	
* 	 <tr bgcolor="#8EECDB">
* 	   <td>1</td><td>mfileid</td><td>●</td><td></td><td></td><td>CHAR(64)</td><td>HASH(2+3)</td><td>File ID(PK)</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>2</td><td>linkid</td><td></td><td></td><td></td><td>VARCHAR(64)</td><td></td><td>연결아이디</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>3</td><td>filename</td><td></td><td></td><td></td><td>TEXT</td><td></td><td>File name</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>4</td><td>seq</td><td></td><td></td><td></td><td>SMALLINT</td><td></td><td>순서</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>5</td><td>creator</td><td></td><td></td><td></td><td>CHAR(64)</td><td></td><td>Creator ID</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>6</td><td>createtime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>Create time</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>7</td><td>createtime</td><td></td><td></td><td></td><td>VARCHAR(25)</td><td></td><td>수정일시</td>
* 	 </tr>
* 	 <tr bgcolor="#8EECDB">
* 	   <td>8</td><td>state</td><td></td><td></td><td></td><td>VARCHAR(2)</td><td></td><td>State</td>
* 	 </tr>
* 	 </table>
* <br> 
* 
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappVersion extends ZArchVersion {

	/* Creator name */
	private String creatorname;
	
	
	public String getCreatorname() {
		return creatorname;
	}
	public void setCreatorname(String creatorname) {
		this.creatorname = creatorname;
	}
	
	private ZArchFile zArchFile;
	private ZArchFormat zArchFormat;

	public ZArchFile getzArchFile() {
		return zArchFile;
	}
	public void setzArchFile(ZArchFile zArchFile) {
		this.zArchFile = zArchFile;
	}
	public ZArchFormat getzArchFormat() {
		return zArchFormat;
	}
	public void setzArchFormat(ZArchFormat zArchFormat) {
		this.zArchFormat = zArchFormat;
	}
	
}

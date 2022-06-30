package com.zenithst.core.common.exception;

import com.zenithst.framework.domain.ZstFwResult;

/**  
* <pre>
* <b>
* 1) Description :  This exception is thrown when the user wants to directly generate an error. <br/>
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>

* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ZstFwResult zstFwResult;

	public ZappException(String msg) {
		super(msg);
	}

	public ZappException(String msg, ZstFwResult result){
		super("[" + result.getResCode() + "]"+ msg);
		this.zstFwResult = result;
	}

	public ZappException(ZstFwResult result){
		this.zstFwResult = result;
	}

	public ZstFwResult getZappResult() {
		return zstFwResult;
	}
	
	public ZappException(Enum<?> message) {
		super(message.toString());
	}
	
	public ZappException(Throwable tcause) {
		super(tcause);
	}

	public void setZappResult(ZstFwResult result) {
		this.zstFwResult = result;
	}	
	
	
	
}

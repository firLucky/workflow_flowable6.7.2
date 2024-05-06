package com.fir.flow.common.exception;


import com.fir.flow.config.result.StatusCode;


/**
 * 描述异常
 * @author fir
 */
public class DescriptionException extends RuntimeException{


	public DescriptionException(String message){
		super(message);
	}


	public DescriptionException(StatusCode statusCode){
		super(statusCode.getMsg());
	}
}
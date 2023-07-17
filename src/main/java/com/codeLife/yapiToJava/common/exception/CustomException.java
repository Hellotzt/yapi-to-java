package com.codeLife.yapiToJava.common.exception;


import com.codeLife.yapiToJava.common.param.CodeMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String code;
	
	private String msg;

	public CustomException(CodeMsg codeMsg) {
		this.code = codeMsg.getCode();
		this.msg = codeMsg.getMsg();
	}

}
package com.codeLife.yapiToJava.common.param;

import lombok.Data;

/**
 * 通用返回结果封装
 */
@Data
public class Result<T> {
	
	/**
     *  返回码
     */
    private String code;

    /**
     * 提示信息
     */
    private String msg;
    
    /**
     * 返回时间
     */
    private String timestamp;

    /**
     * 返回数据集合
     */
    private T data;

	/**
	 *  成功时候的调用
	 */
	public static Result<Object> success(CodeMsg codeMsg){
		return new Result<>(codeMsg);
	}

	public static Result<Object> success(){
		return new Result<>();
	}
	
	public static <T> Result<T> success(T data){
		return new Result<>(data);
	}

	public static Result<Object> fail(){
		return new Result<>(CodeMsg.FAIL);
	}

	public static Result<Object> fail(CodeMsg codeMsg){
		return new Result<>(codeMsg);
	}


	public Result() {
		this.code = CodeMsg.SUCCESS.getCode();
		this.msg = CodeMsg.SUCCESS.getMsg();
		this.timestamp = CodeMsg.SUCCESS.getTimestamp();
		this.data = null;
	}
	
	public Result(T data) {
		this.code = CodeMsg.SUCCESS.getCode();
		this.msg = CodeMsg.SUCCESS.getMsg();
		this.timestamp = CodeMsg.SUCCESS.getTimestamp();

		this.data = data;
	}

	public Result(CodeMsg codeMsg) {
		if(codeMsg != null) {
			this.code = codeMsg.getCode();
			this.msg = codeMsg.getMsg();
			this.timestamp = codeMsg.getTimestamp();
		}
	}
	

}
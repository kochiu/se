package com.sztx.se.common.exception;

/**
 * 系统异常类
 * 
 * @author zhihongp
 *
 */
public class SystemException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6971716908203238516L;

	public SystemException() {
		super();
	}

	public SystemException(String message) {
		super(message);
	}

	public SystemException(Throwable cause) {
		super(cause);
	}

	public SystemException(String message, Throwable cause) {
		super(message, cause);
	}

}

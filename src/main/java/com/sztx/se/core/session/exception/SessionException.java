package com.sztx.se.core.session.exception;

/**
 * @author zhihongp
 */
public class SessionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7425986834986996135L;

	public SessionException(String message) {
		super(message);
	}

	public SessionException(Throwable cause) {
		super(cause);
	}
	
	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}

}
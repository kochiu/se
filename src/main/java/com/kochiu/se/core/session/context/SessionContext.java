package com.kochiu.se.core.session.context;

import com.kochiu.se.common.util.session.HttpSessionProxy;

/**
 * 
 * @author zhihongp
 *
 */
public class SessionContext {

	private static final ThreadLocal<HttpSessionProxy> sessionContextHolder = new ThreadLocal<HttpSessionProxy>();

	public static HttpSessionProxy getSessionContext() {
		return sessionContextHolder.get();
	}

	public static boolean isExist() {
		if (sessionContextHolder.get() == null) {
			return false;
		}
		
		if (sessionContextHolder.get().getId() == null) {
			return false;
		}
		
		return true;
	}

	public static void setSessionContext(HttpSessionProxy httpSessionProxy) {
		sessionContextHolder.set(httpSessionProxy);
	}

	public static void clean() {
		sessionContextHolder.remove();
	}
}

package com.kochiu.se.web.interceptor;

public class InterceptorUtil {

	/**
	 * 是否被程序定义的Controller处理(true-是，false-否且会被spring默认的DefaultServletHttpRequestHandler处理)
	 * 
	 * @param handler
	 * @return
	 */
	public static boolean IsControllerHandle(Object handler) {
		String controller = handler.getClass().getSimpleName();

		if (controller.equals("DefaultServletHttpRequestHandler")) {
			return false;
		}

		return true;
	}
}

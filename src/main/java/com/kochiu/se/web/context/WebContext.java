package com.kochiu.se.web.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebContext {

	private static final ThreadLocal<Object[]> WEBCONTEXT_LOCAL = new ThreadLocal<Object[]>();

	/**
	 * 得到当前request(web.xml中filter链表结束后spring dispatcherServlet之前的HttpServletRequest)
	 * 
	 * @return request
	 */
	public static HttpServletRequest currentRequest() {
		Object[] locals = (Object[]) WEBCONTEXT_LOCAL.get();
		return locals == null ? null : (HttpServletRequest) locals[0];
	}

	/**
	 * 得到当前request(spring进入controller之前的HttpServletRequest)
	 * @return
	 */
	public static HttpServletRequest getHttpServletRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	/**
	 * 得到当前response
	 * 
	 * @return response
	 */
	public static HttpServletResponse currentResponse() {
		Object[] locals = (Object[]) WEBCONTEXT_LOCAL.get();
		return locals == null ? null : (HttpServletResponse) locals[1];
	}

	/**
	 * 在进入WebContextFilter过滤器时，将request和response注册到ThreadLocal中
	 * 
	 * @param request 要注入的request
	 * @param response 要注入的response
	 * @see #doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public static void registry(HttpServletRequest request, HttpServletResponse response) {
		Object[] locals = new Object[2];
		locals[0] = request;
		locals[1] = response;
		WEBCONTEXT_LOCAL.set(locals);
	}

	/**
	 * 在WebContextFilter过滤器完成时，将request和response从ThreadLocal中清除
	 */
	public static void release() {
		WEBCONTEXT_LOCAL.remove();
	}
}

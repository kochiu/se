package com.kochiu.se.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kochiu.se.web.context.WebContext;
import com.kochiu.se.core.service.InitializeService;

/**
 * 将request和response注册到WebContext中，结束时清除
 * 
 * @author zhihongp
 * 
 */
public class WebContextFilter implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		try {
			HttpServletRequest heq = (HttpServletRequest) request;
			HttpServletResponse hsr = (HttpServletResponse) response;
			WebContext.registry(heq, hsr);
			filterChain.doFilter(request, response);
		} finally {
			WebContext.release();
			InitializeService.clearDynamicSources();
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
	}

}

package com.kochiu.se.core.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kochiu.se.core.session.config.SessionConfig;
import org.apache.commons.lang.StringUtils;

import com.kochiu.se.common.util.session.HttpSessionProxy;
import com.kochiu.se.common.util.session.SessionUtil;
import com.kochiu.se.core.session.context.SessionContext;
import com.kochiu.se.core.session.persistence.SessionPersister;

/**
 * 
 * @author zhihongp
 *
 */
public class SessionManager {

	private SessionPersister sessionPersister;

	public void setSessionPersister(SessionPersister sessionPersister) {
		this.sessionPersister = sessionPersister;
	}
	
	/**
	 * 获取session
	 * 
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @param create
	 * @param sessionConfig
	 * @return
	 */
	public HttpSessionProxy getSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean create,
			SessionConfig sessionConfig) {
		String sessionName = sessionConfig.getName();
		String domain = StringUtils.isNotBlank(sessionConfig.getDomain()) ? sessionConfig.getDomain() : httpServletRequest.getServerName();
		String path = StringUtils.isNotBlank(sessionConfig.getPath()) ? sessionConfig.getPath() : httpServletRequest.getContextPath();
		boolean secure = sessionConfig.getSecure();
		boolean httpOnly = sessionConfig.getHttpOnly();
		int maxAge = sessionConfig.getMaxAge();
		int maxInactiveInterval = sessionConfig.getExpiredTime() * 1000;
		String sessionId = getSessionId(httpServletRequest, sessionName);
		HttpSessionProxy httpSessionProxy = null;

		// 判断客户端是否已经存储了session，没有则重新生成session放到当前session上下文中并返回给客户端
		if (StringUtils.isBlank(sessionId)) {
			boolean isExist = SessionContext.isExist();

			if (isExist) {
				httpSessionProxy = SessionContext.getSessionContext();
			} else {
				httpSessionProxy = SessionUtil.createSession(httpServletResponse, sessionName, domain, path, secure, httpOnly, maxAge, maxInactiveInterval);
				SessionContext.setSessionContext(httpSessionProxy);
			}

			return httpSessionProxy;
		}

		// 判断当前session上下文中是否已经创建了session，没有则从缓存中加载
		boolean isExist = SessionContext.isExist();

		if (isExist) {
			httpSessionProxy = SessionContext.getSessionContext();
		} else {
			httpSessionProxy = loadSession(sessionId);
		}

		boolean isExpired = false;

		if (httpSessionProxy == null) {
			if (create) {
				httpSessionProxy = SessionUtil.createSession(httpServletResponse, sessionName, domain, path, secure, httpOnly, maxAge, maxInactiveInterval);
			} else {
				deleteSession(httpServletRequest, httpServletResponse, sessionName, domain, path);
			}
		} else {
			SessionUtil.modifySessionIsNew(httpSessionProxy, false);
			isExpired = httpSessionProxy.getExpired();

			if (isExpired) {
				if (create) {
					httpSessionProxy = SessionUtil.createSession(httpServletResponse, sessionName, domain, path, secure, httpOnly, maxAge, maxInactiveInterval);
					isExpired = false;
				} else {
					deleteSession(httpServletRequest, httpServletResponse, sessionName, domain, path);
				}
			}

			SessionUtil.modifySessioExpired(httpSessionProxy, isExpired);
		}

		SessionContext.setSessionContext(httpSessionProxy);
		return httpSessionProxy;
	}

	public boolean saveSession(final HttpSessionProxy httpSession, final SessionConfig sessionConfig) {
		return sessionPersister.addSessionToCache(httpSession, sessionConfig);
	}

	/**
	 * 获取sessionId
	 * 
	 * @param httpServletRequest
	 * @param sessionName
	 * @return
	 */
	private String getSessionId(HttpServletRequest httpServletRequest, String sessionName) {
		return SessionUtil.getSessionIdFromCookie(httpServletRequest, sessionName);
	}

	/**
	 * 获取sessionId
	 * 
	 * @param httpServletRequest
	 * @param sessionName
	 * @return
	 */
	private boolean deleteSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String sessionName, String domain, String path) {
		return SessionUtil.deleteSession(httpServletRequest, httpServletResponse, sessionName, domain, path);
	}

	/**
	 * 加载session
	 * 
	 * @param sessionId
	 * @return
	 */
	private HttpSessionProxy loadSession(String sessionId) {
		HttpSessionProxy httpSessionProxy = getSessionFromPersister(sessionId);
		return httpSessionProxy;
	}

	/**
	 * 从缓存中获取session
	 * 
	 * @param key
	 * @return
	 */
	private HttpSessionProxy getSessionFromPersister(final String key) {
		return sessionPersister.getSessionFromCache(key);
	}

}

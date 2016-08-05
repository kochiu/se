package com.kochiu.se.web.interceptor;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kochiu.se.common.domain.Result;
import com.kochiu.se.common.util.CommonUtil;
import com.kochiu.se.common.util.http.URLUtil;
import com.kochiu.se.core.session.config.SessionConfig;
import com.kochiu.se.web.domain.HttpLog;
import com.kochiu.se.web.domain.RequestLog;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.util.date.DateUtil;
import com.kochiu.se.web.domain.ResponseLog;

/**
 * 
 * @author zhihongp
 * 
 */
public class LogInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

	private static final String DEFAULT_SESSION_NAME = "JSESSIONID";

	private static final String MODEL_VIEW_OBJECT = "org.springframework.validation";

	private static final String MODEL_VIEW_CONTENT_TYPE = "text/html;charset=UTF-8";

	/**
	 * 日志开关，默认为false不打开
	 */
	private boolean openLog;

	/**
	 * 日志最大长度，如果不传则默认1000，传-1则不限制日志打印长度
	 */
	private int logLength;

	@Autowired(required = false)
	private SessionConfig sessionConfig;

	public void setOpenLog(boolean openLog) {
		this.openLog = openLog;
	}

	public void setLogLength(int logLength) {
		this.logLength = logLength;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (openLog) {
			try {
				RequestLog requestLog = getRequestLog(request);
				request.setAttribute("requestLog", requestLog);
			} catch (Exception e) {
				LOG.error("LogInterceptor preHandle error", e);
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (openLog) {
			try {
				ResponseLog responseLog = getResponseLog(request, response, modelAndView);
				request.setAttribute("responseLog", responseLog);
			} catch (Exception e) {
				LOG.error("LogInterceptor postHandle error", e);
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		if (openLog) {
			try {
				Object requestObj = request.getAttribute("requestLog");
				Object responseObj = request.getAttribute("responseLog");
				RequestLog requestLog = null;
				ResponseLog responseLog = null;

				if (requestObj != null) {
					requestLog = (RequestLog) requestObj;
				}

				if (responseObj != null) {
					responseLog = (ResponseLog) responseObj;
				}

				if (requestLog == null) {
					requestLog = getRequestLog(request);
				}

				if (responseLog == null) {
					responseLog = getResponseLog(request, response, null);
				}

				Date start = (requestLog.getTime()) != null ? requestLog.getTime() : new Date();
				Date end = (responseLog.getTime()) != null ? responseLog.getTime() : new Date();
				long cost = end.getTime() - start.getTime();
				HttpLog httpLog = new HttpLog(requestLog, responseLog, cost + "ms");
				String log = getHttpLog(httpLog);
				int logLength = this.logLength != 0 ? this.logLength : ContextConstants.LOG_MAX_LENGTH;

				if (logLength != -1 && log.length() > logLength) {
					log = log.substring(0, logLength);
				}

				LOG.info(log);
			} catch (Throwable t) {
				LOG.error("LogInterceptor afterCompletion error", t);
			}
		}
	}

	private String getHttpLog(HttpLog httpLog) {
		String log = "[HttpLog] "
				+ JSON.toJSONStringWithDateFormat(httpLog, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
		return log;
	}

	private RequestLog getRequestLog(HttpServletRequest request) {
		Date time = new Date();
		String url = new String(request.getRequestURL());
		String addr = URLUtil.getClientAddr(request);
		String referer = request.getHeader("Referer");
		String accept = request.getHeader("Accept");
		String agent = request.getHeader("User-Agent");
		String contentType = request.getContentType();
		Cookie[] cookies = request.getCookies();
		String sessionId = null;
		String sessionName = (sessionConfig != null) ? sessionConfig.getName() : DEFAULT_SESSION_NAME;

		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];

				if (sessionName.equals(cookie.getName())) {
					sessionId = cookie.getValue();
					break;
				}
			}
		}

		RequestLog requestLog = new RequestLog();
		requestLog.setUrl(url);
		requestLog.setReferer(referer);
		requestLog.setAddr(addr);
		requestLog.setAccept(accept);
		requestLog.setAgent(agent);
		requestLog.setContentType(contentType);
		requestLog.setSessionId(sessionId);
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String> parameters = new LinkedHashMap<String, String>();

		if (parameterMap != null && !parameterMap.isEmpty()) {
			for (Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();
				String[] valueArray = entry.getValue();
				String value = CommonUtil.getStringArrayStr(valueArray, ",");
				parameters.put(key, value);
			}
		}

		requestLog.setParameters(parameters);
		requestLog.setTime(time);
		return requestLog;
	}

	private ResponseLog getResponseLog(HttpServletRequest request, HttpServletResponse response, ModelAndView modelAndView) {
		ResponseLog responseLog = new ResponseLog();
		String contentType = response.getContentType();

		if (StringUtil.isBlank(contentType)) {
			contentType = response.getHeader("Content-Type");
		}

		String setCookie = response.getHeader("Set-Cookie");

		if (modelAndView != null) {
			if (StringUtil.isBlank(contentType)) {
				contentType = MODEL_VIEW_CONTENT_TYPE;
			}

			String viewName = modelAndView.getViewName();
			responseLog.setContentType(contentType);
			responseLog.setSetCookie(setCookie);
			responseLog.setView(viewName);
			Map<String, Object> map = modelAndView.getModel();
			Map<String, Object> result = new LinkedHashMap<String, Object>();

			if (map != null && !map.isEmpty()) {
				for (Entry<String, Object> entry : map.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();

					if (key.contains(MODEL_VIEW_OBJECT) || value instanceof BeanPropertyBindingResult) {
						continue;
					}

					result.put(key, value);
				}

				responseLog.setResult(result);
			}
		} else {
			Result result = (Result) request.getAttribute("result");
			String callback = (String) request.getAttribute("callback");
			responseLog.setContentType(contentType);
			responseLog.setSetCookie(setCookie);
			responseLog.setResult(result);
			responseLog.setCallback(callback);
		}

		responseLog.setTime(new Date());
		return responseLog;
	}

}

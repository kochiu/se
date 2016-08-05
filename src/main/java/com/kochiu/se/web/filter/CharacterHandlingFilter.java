package com.kochiu.se.web.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.kochiu.se.common.util.BCConvertUtil;
import com.kochiu.se.web.context.WebContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.kochiu.se.common.util.StringUtil;

/**
 * HTTP请求的字符处理器，包括字符全角转半角，去掉字符串前后的空格等
 * 
 * @author zhihongp
 * 
 */
public class CharacterHandlingFilter extends CharacterEncodingFilter {

	private final static String DEFAULT_ENCODING = "UTF-8";

	private final static String ISO_ENCODING = "ISO-8859-1";

	private String encoding;

	private boolean forceEncoding = false;

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setForceEncoding(boolean forceEncoding) {
		this.forceEncoding = forceEncoding;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null)) {
			request.setCharacterEncoding(this.encoding);

			if (this.forceEncoding) {
				response.setCharacterEncoding(this.encoding);
			}
		}

		String parameterEncoding = (this.encoding == null) ? DEFAULT_ENCODING : this.encoding;
		RequestWrapper requestWrapper = new RequestWrapper(request, request.getParameterMap(), parameterEncoding);
		WebContext.registry(requestWrapper, response);
		filterChain.doFilter(requestWrapper, response);
	}

	public class RequestWrapper extends HttpServletRequestWrapper {

		private Map<String, String[]> params;

		private String encoding;

		public RequestWrapper(HttpServletRequest request, Map<String, String[]> parameterMap, String encoding) {
			super(request);
			this.encoding = encoding;
			handleRequest(request, parameterMap);
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return params;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			Vector<String> l = new Vector<String>(params.keySet());
			return l.elements();
		}

		@Override
		public String[] getParameterValues(String name) {
			Object v = params.get(name);

			if (v == null) {
				return null;
			} else if (v instanceof String[]) {
				return (String[]) v;
			} else if (v instanceof String) {
				return new String[] { (String) v };
			} else {
				return new String[] { v.toString() };
			}
		}

		@Override
		public String getParameter(String name) {
			Object v = params.get(name);

			if (v == null) {
				return null;
			} else if (v instanceof String[]) {
				String[] strArr = (String[]) v;
				if (strArr.length > 0) {
					return strArr[0];
				} else {
					return null;
				}
			} else if (v instanceof String) {
				return (String) v;
			} else {
				return v.toString();
			}
		}

		private void handleRequest(HttpServletRequest request, Map<String, String[]> parameterMap) {
			params = new HashMap<String, String[]>(parameterMap);
			Iterator<Entry<String, String[]>> iterator = params.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, String[]> entry = iterator.next();
				String key = entry.getKey();
				String[] value = entry.getValue();

				if (value == null) {
					params.put(key, value);
				} else {
					String[] valueArray = (String[]) value;

					for (int i = 0; i < valueArray.length; i++) {
						String valueStr = valueArray[i];

						if (StringUtil.isNotEmpty(valueStr)) {
							String newValue = (String) valueStr;

							try {
								if (newValue.equals(new String(newValue.getBytes(ISO_ENCODING), ISO_ENCODING))) {
									newValue = new String(newValue.getBytes(ISO_ENCODING), this.encoding);
								}
							} catch (Exception e) {
							}

							newValue = BCConvertUtil.qjTobj(newValue).trim();
							valueArray[i] = newValue;
						}
					}

					params.put(key, valueArray);
				}
			}
		}
	}
}

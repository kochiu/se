package com.kochiu.se.web.filter;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
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
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import com.kochiu.se.common.domain.Result;
import com.kochiu.se.common.domain.ResultCode;
import com.kochiu.se.common.util.http.URLUtil;
import com.kochiu.se.common.util.secret.RSAUtil;
import com.kochiu.se.web.context.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kochiu.se.common.util.StringUtil;
import com.kochiu.se.common.util.secret.AESUtil;

/**
 * HTTP请求加密解密过滤器(包括请求参数解密，返回结果加密等)
 * 
 * @author zhihongp
 * 
 */
public class EncryptFilter extends OncePerRequestFilter {

	/**
	 * 日志
	 */
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	public final static String ENCRPY_HEADER = "encrpy";

	public final static String PUBLIC_KEY = "publicKey";

	public final static String PRIVATE_KEY = "privateKey";

	public final static String SECRET_KEY = "secretKey";

	private final static String DEFAULT_SECRET_KEY = "2d1287777f4f45a8";

	private final static int NO_SECRET_KEY = 901;

	private final static String GET_PUBLIC_KEY_URL = "/getPublicKey";

	private final static String EXCHANGE_SECRET_KEY_URL = "/exchangeSecretKey";

	private boolean encrypt = false;

	private String secretKey;

	private String ignoreUris;

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public void setIgnoreUris(String ignoreUris) {
		this.ignoreUris = ignoreUris;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String currentURI = URLUtil.getURIWithoutSuffix(request.getRequestURI());

		if (isPublicKeyRequest(currentURI)) {
			processGetPublicKey(request, response);
			return;
		}

		if (isExchangeSecretKeyRequest(currentURI)) {
			processExchangeSecretKey(request, response);
			return;
		}

		if (!shouldFilter(currentURI)) {
			filterChain.doFilter(request, response);
			return;
		}

		boolean parameterDecrypt = getParameterDecrypt(request);
		String secretKey = null;

		if (parameterDecrypt) {
			secretKey = getSecretKey(request);

			if (secretKey == null) {
				processNoSecretKey(request, response);
				return;
			}
		}

		RequestWrapper requestWrapper = new RequestWrapper(request, request.getParameterMap(), parameterDecrypt, secretKey);
		ResponseWrapper responseWrapper = new ResponseWrapper(response);
		WebContext.registry(requestWrapper, responseWrapper);
		filterChain.doFilter(requestWrapper, responseWrapper);
		boolean encryptFlag = encrypt && getResponseEncrypt(request);
		String responseContent = responseWrapper.getContent();
		String responseStr = null;

		if (encryptFlag) {
			responseStr = handleResponse(responseContent, secretKey, requestWrapper, responseWrapper);
		} else {
			responseStr = responseContent;
		}

		PrintWriter writer = null;

		try {
			writer = response.getWriter();
		} catch (Exception e) {
		}

		if (writer != null) {
			writer.write(responseStr);
			writer.flush();
		}
	}

	public class RequestWrapper extends HttpServletRequestWrapper {

		private Map<String, String[]> params;

		private boolean decrypt;

		private String secretKey;

		public RequestWrapper(HttpServletRequest request, Map<String, String[]> parameterMap, boolean decrypt, String secretKey) {
			super(request);
			this.decrypt = decrypt;
			this.secretKey = secretKey;
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

						if (valueStr != null) {
							String newValue = (String) valueStr;

							if (decrypt) {
								newValue = AESUtil.decryptStr(newValue, secretKey);
							}
						}
					}

					params.put(key, valueArray);
				}
			}
		}
	}

	public class ResponseWrapper extends HttpServletResponseWrapper {

		private PrintWriter cachedWriter;

		private CharArrayWriter bufferedWriter;

		public ResponseWrapper(HttpServletResponse response) {
			super(response);
			bufferedWriter = new CharArrayWriter();
			cachedWriter = new PrintWriter(bufferedWriter);
		}

		@Override
		public PrintWriter getWriter() {
			return cachedWriter;
		}

		/**
		 * 获取输出内容
		 * 
		 * @return
		 */
		public String getContent() {
			return bufferedWriter.toString();
		}
	}

	private String handleResponse(String responseContent, String secretKey, RequestWrapper request, ResponseWrapper response) {
		if (secretKey == null) {
			secretKey = getSecretKey(request);
		}

		if (secretKey == null) {
			secretKey = this.secretKey != null ? this.secretKey : DEFAULT_SECRET_KEY;
			response.setStatus(NO_SECRET_KEY);
		}

		response.setHeader(ENCRPY_HEADER, "true");
		return AESUtil.encryptStr(responseContent, secretKey);
	}

	private boolean getParameterDecrypt(HttpServletRequest request) {
		String decrypt = request.getHeader(ENCRPY_HEADER);
		boolean decryptFlag = false;

		try {
			if (decrypt != null) {
				decryptFlag = Boolean.valueOf(decrypt);
			}
		} catch (Exception e) {
		}

		return decryptFlag;
	}

	private boolean getResponseEncrypt(HttpServletRequest request) {
		Object encrypt = request.getAttribute(ENCRPY_HEADER);
		boolean encryptFlag = false;

		try {
			if (encrypt != null) {
				encryptFlag = Boolean.valueOf(String.valueOf(encrypt));
			}
		} catch (Exception e) {
		}

		return encryptFlag;
	}

	private String getSecretKey(HttpServletRequest request) {
		String secretKey = null;
		HttpSession session = request.getSession();

		if (session != null) {
			secretKey = (String) session.getAttribute(SECRET_KEY);
		}

		return secretKey;
	}

	private void processNoSecretKey(HttpServletRequest request, HttpServletResponse response) {
		Result result = new Result(ResultCode.COMMON_SYSTEM_EXCEPTION, false);
		result.setDescription("No sercret key");
		String resultStr = Result.toJSONString(result);
		String responseStr = buildResponseStr(request, response, resultStr);
		response.setStatus(NO_SECRET_KEY);
		PrintWriter writer = null;

		try {
			writer = response.getWriter();
		} catch (Exception e) {
		}

		if (writer != null) {
			writer.write(responseStr);
		}
	}

	private void processGetPublicKey(HttpServletRequest request, HttpServletResponse response) {
		String publicKey = null;
		String privateKey = null;
		
		try {
			Map<String, Key> keyMap = RSAUtil.initKeys();
			publicKey = RSAUtil.getPublicKey(keyMap);
			privateKey = RSAUtil.getPrivateKey(keyMap);
		} catch (Exception e) {
			log.error("ProcessGetPublicKey error", e);
		}

		Result result = null;
		String responseStr = null;
		
		if (publicKey != null && privateKey != null) {
			HttpSession session = request.getSession();
			session.setAttribute(EncryptFilter.PUBLIC_KEY, publicKey);
			session.setAttribute(EncryptFilter.PRIVATE_KEY, privateKey);
			result = new Result(ResultCode.COMMON_SUCCESS, true);
			result.setProperty("publicKey", publicKey);
		} else {
			result = new Result(ResultCode.COMMON_SYSTEM_EXCEPTION, false);
			result.setDescription("Generate public key failure");
		}
		
		String resultStr = Result.toJSONString(result);
		responseStr = buildResponseStr(request, response, resultStr);
		PrintWriter writer = null;

		try {
			writer = response.getWriter();
		} catch (Exception e) {
		}

		if (writer != null) {
			writer.write(responseStr);
		}
	}

	private void processExchangeSecretKey(HttpServletRequest request, HttpServletResponse response) {
		String data = request.getParameter("secretKey");
		HttpSession session = request.getSession();
		String key = (String) session.getAttribute(EncryptFilter.PRIVATE_KEY);
		String secretKey = null;

		if (data != null && key != null) {
			try {
				secretKey = RSAUtil.decryptByPrivateKeyStr(data, key);
			} catch (Exception e) {
				log.warn("Decrypt secret key failure");
			}
		}

		Result result = null;

		if (secretKey != null) {
			result = new Result(ResultCode.COMMON_SUCCESS, true);
			session.setAttribute(EncryptFilter.SECRET_KEY, secretKey);
		} else {
			result = new Result(ResultCode.COMMON_SYSTEM_EXCEPTION, false);
			result.setDescription("Decrypt secret key failure");
		}

		String resultStr = Result.toJSONString(result);
		String responseStr = buildResponseStr(request, response, resultStr);
		PrintWriter writer = null;

		try {
			writer = response.getWriter();
		} catch (Exception e) {
		}

		if (writer != null) {
			writer.write(responseStr);
		}
	}

	private boolean shouldFilter(String currentURI) {
		String[] ignoreUris = getIgnoreUris(this.ignoreUris);

		if (ignoreUris != null && ignoreUris.length > 0) {
			for (int i = 0; i < ignoreUris.length; i++) {
				if (currentURI.equalsIgnoreCase(ignoreUris[i])) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean isPublicKeyRequest(String uri) {
		if (GET_PUBLIC_KEY_URL.equalsIgnoreCase(uri)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean isExchangeSecretKeyRequest(String uri) {
		if (EXCHANGE_SECRET_KEY_URL.equalsIgnoreCase(uri)) {
			return true;
		} else {
			return false;
		}
	}

	private String buildResponseStr(HttpServletRequest request, HttpServletResponse response, String resultStr) {
		StringBuffer responseSb = new StringBuffer();

		if (URLUtil.isAjaxUrl(request)) {
			if (URLUtil.isJsonp(request)) {
				response.setContentType("application/javascript;charset=UTF-8");
				String callback = request.getParameter("callback");
				responseSb.append(callback).append("(").append(resultStr).append(")");
				request.setAttribute("callback", callback);
			} else {
				response.setContentType("application/json;charset=UTF-8");
				responseSb.append(resultStr);
			}
		} else {
			response.setContentType("text/html");
			responseSb.append(resultStr);
		}

		String responseStr = responseSb.toString();
		return responseStr;
	}

	private String[] getIgnoreUris(String ignoreUris) {
		String[] ignoreUriArray = null;

		if (StringUtil.isNotBlank(ignoreUris)) {
			ignoreUriArray = ignoreUris.split(",");
		}

		return ignoreUriArray;
	}
}

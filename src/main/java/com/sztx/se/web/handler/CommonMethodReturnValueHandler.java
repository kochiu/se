package com.sztx.se.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.sztx.se.common.domain.Result;
import com.sztx.se.common.util.http.URLUtil;

/**
 * 对自定义的返回值进行处理，一般是序列化成json字符串
 * 
 * @author: zhihongp
 * 
 */
public class CommonMethodReturnValueHandler implements HandlerMethodReturnValueHandler {
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return Result.class.isAssignableFrom(returnType.getParameterType());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
			throws Exception {
		if (returnValue != null) {
			Result result = (Result) returnValue; 
			HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			response.setCharacterEncoding("UTF-8");
			StringBuffer responseSb = new StringBuffer();
			String resultStr = Result.toJSONString(result);
			
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
				response.setContentType("text/html;charset=UTF-8");
				responseSb.append(resultStr);
			}
			
			request.setAttribute("result", result);
			String responseStr = responseSb.toString();
			response.getWriter().write(responseStr);
			// 表明该请求已经处理，后面spring不会再处理
			mavContainer.setRequestHandled(true);
		}
	}

}

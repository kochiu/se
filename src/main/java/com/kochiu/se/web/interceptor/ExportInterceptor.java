//package com.kochiu.se.web.interceptor;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import ContextConstants;
//import Result;
//import ResultCode;
//import StringUtil;
//import URLUtil;
//import CellDefine;
//import ExcelExport;
//
///**
// * 导出拦截器，对带有特定参数的请求进行拦截
// * 
// * @author pzh
// * 
// */
//public class ExportInterceptor extends HandlerInterceptorAdapter {
//
//	private static final Logger LOG = LoggerFactory.getLogger(ExportInterceptor.class);
//
//	private static final String EXCELE_FLAG = "excel";
//
//	private static final String WORD_FLAG = "word";
//
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//		String exportFlag = request.getParameter(ContextConstants.EXPORT_FLAG);
//
//		if (StringUtil.isNotBlank(exportFlag)) {
//			try {
//				if (EXCELE_FLAG.equals(exportFlag)) {
//					handleExcelExport(request, response, handler, modelAndView);
//				} else if (WORD_FLAG.equals(exportFlag)) {
//					// do something ...
//				} else {
//					LOG.error("There is no support with " + exportFlag);
//				}
//			} catch (Exception e) {
//				Result result = new Result(ResultCode.COMMON_SYSTEM_ERROR, false);
//				result.setDescription(e.getMessage());
//				LOG.error("ExportInterceptor postHandle error, ", e);
//				response.setCharacterEncoding("UTF-8");
//				StringBuffer responseSb = new StringBuffer();
//				String resultStr = Result.toJSONString(result);
//
//				if (URLUtil.isAjaxUrl(request)) {
//					if (URLUtil.isJsonp(request)) {
//                      response.setContentType("application/javascript;charset=UTF-8");
//						String callback = request.getParameter("callback");
//						responseSb.append("(").append(callback).append(resultStr).append(")");
//						request.setAttribute("callback", callback);
//					} else {
//						response.setContentType("application/json;charset=UTF-8");
//						responseSb.append(resultStr);
//					}
//				} else {
//					response.setContentType("text/html;charset=UTF-8");
//				}
//
//				request.setAttribute("result", result);
//				String responseStr = responseSb.toString();
//				response.getWriter().write(responseStr);
//			}
//		}
//	}
//
//	private void handleExcelExport(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws IOException {
//		if (!InterceptorUtil.IsControllerHandle(handler)) {
//			return;
//		}
//
//		Object exportObject = request.getAttribute(ContextConstants.EXPORT_DATA);
//		List<CellDefine> cellDefines = getCellDefines(request.getParameter(ContextConstants.EXPORT_COLUMN_DESCR));
//		response.setCharacterEncoding("UTF-8");
//		response.setContentType("application/x-msexcel");
//		response.setHeader("Content-type", "application/x-msexcel");
//		String fileName = "export.xls";
//		response.setHeader("content-disposition", "attachment;filename=" + fileName);
//		OutputStream out = response.getOutputStream();
//		ExcelExport.exportToOutputStream(cellDefines, exportObject, out);
//		out.flush();
//
//		if (modelAndView != null) {
//			modelAndView.clear();
//		}
//
//		Result result = new Result(ResultCode.COMMON_SUCCESS, false);
//		result.setProperty("file", fileName);
//		request.setAttribute("result", result);
//	}
//
//	private List<CellDefine> getCellDefines(String cellDefineJson) {
//		if (cellDefineJson == null) {
//			return null;
//		}
//
//		JSONArray jsonArray = (JSONArray) JSON.parse(cellDefineJson);
//		int size = jsonArray.size();
//
//		if (size == 0) {
//			return null;
//		}
//
//		List<CellDefine> cellDefineList = new ArrayList<CellDefine>();
//
//		for (int i = 0; i < size; i++) {
//			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
//			Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();
//
//			for (Map.Entry<String, Object> entry : entrySet) {
//				cellDefineList.add(new CellDefine(entry.getKey(), entry.getValue().toString()));
//			}
//		}
//
//		return cellDefineList;
//	}
//
//}

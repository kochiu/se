package com.kochiu.se.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kochiu.se.common.domain.Result;
import com.kochiu.se.common.domain.ResultCode;
import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.dataaccess.mysql.config.PageQuery;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.util.StringUtil;
import com.kochiu.se.common.util.date.DateUtil;
import com.kochiu.se.common.util.poi.CellDefine;
import com.kochiu.se.common.util.poi.ExcelExport;
import com.kochiu.se.common.util.poi.ExcelFieldDefinition;
import com.kochiu.se.common.util.poi.ExcelResult;
import com.kochiu.se.common.util.poi.ExcelUtil;

/**
 * 基础Controller
 * 
 * @author zhihongp
 * 
 */
public abstract class BaseController {

	/**
	 * 日志
	 */
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	protected HttpServletRequest getHttpServletRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	/**
	 * jsonp安全处理
	 * 
	 * @return
	 */
	protected String jsonpSecurityFilter(String jsonp) {
		// TODO

		return null;
	}

	/**
	 * 判断您是否有xss攻击
	 * 
	 * @return
	 */
	protected boolean isSecurity(String value) {
		// TODO
		return true;
	}

	protected String getStringParameter(String name) {
		return getStringParameter(name, null);
	}

	protected String getStringParameter(String name, String defaultValue) {
		String value = getHttpServletRequest().getParameter(name);

		return StringUtils.isEmpty(value) ? defaultValue : value.trim();
	}

	protected BigDecimal getBigDecimalParameter(String name) {
		String value = getHttpServletRequest().getParameter(name);
		return StringUtils.isEmpty(value) ? null : new BigDecimal(value);
	}

	protected BigDecimal getBigDecimalParameter(String name, String defaultValue) {
		String value = getHttpServletRequest().getParameter(name);
		return StringUtils.isEmpty(value) ? new BigDecimal(defaultValue) : new BigDecimal(value);
	}

	protected Boolean getBooleanParameter(String name) {
		return getBooleanParameter(name, null);
	}

	protected Boolean getBooleanParameter(String name, Boolean defaultValue) {
		String value = getHttpServletRequest().getParameter(name);
		return StringUtils.isEmpty(value) ? defaultValue : Boolean.valueOf(value);
	}

	protected Integer getIntegerParameter(String name) {
		return getIntegerParameter(name, null);
	}

	protected Integer getIntegerParameter(String name, Integer defaultValue) {
		String value = getHttpServletRequest().getParameter(name);
		return StringUtils.isEmpty(value) ? defaultValue : Integer.valueOf(value);
	}

	protected Long getLongParameter(String name) {
		return getLongParameter(name, null);
	}

	protected Long getLongParameter(String name, Long defaultValue) {
		String value = getHttpServletRequest().getParameter(name);
		return StringUtils.isEmpty(value) ? defaultValue : Long.valueOf(value);
	}

	protected Float getFloatParameter(String name) {
		return getFloatParameter(name, null);
	}

	protected Float getFloatParameter(String name, Float defaultValue) {
		String value = getHttpServletRequest().getParameter(name);
		return StringUtils.isEmpty(value) ? defaultValue : Float.valueOf(value);
	}

	protected Double getDoubleParameter(String name) {
		return getDoubleParameter(name, null);
	}

	protected Double getDoubleParameter(String name, Double defaultValue) {
		String value = getHttpServletRequest().getParameter(name);
		return StringUtils.isEmpty(value) ? defaultValue : Double.valueOf(value);
	}

	protected Date getDateParameter(String name) {
		return getDateParameter(name, DateUtil.LONG_DATE_FORMAT_STR, null);
	}

	protected Date getDateParameter(String name, String format) {
		return getDateParameter(name, format, null);
	}

	protected Date getDateParameter(String name, String format, String defaultValue) {
		String value = getHttpServletRequest().getParameter(name);
		DateFormat dateFormat = new SimpleDateFormat(format);

		try {
			if (StringUtils.isBlank(value)) {
				return null == defaultValue ? null : dateFormat.parse(defaultValue);
			} else {
				if (StringUtil.isEmpty(format)) {
					Long timeMillis = null;
					try {
						timeMillis = Long.parseLong(value);
					} catch (NumberFormatException e) {
					}
					if (timeMillis != null) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(timeMillis);
						return calendar.getTime();
					} else {
						return null;
					}
				} else {
					return dateFormat.parse(value);
				}
			}
		} catch (ParseException e) {
			throw new SystemException("时间格式有误");
		}
	}

	protected MultipartFile getMultipartFile(HttpServletRequest request, String fileName) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		return multipartRequest.getFile(fileName);
	}

	protected PageQuery getPageQuery() {
		Integer pageNo = getIntegerParameter("pageNo", 1);
		Integer pageSize = getIntegerParameter("pageSize", 10);
		PageQuery page = new PageQuery(pageNo, pageSize);

		return page;
	}

	protected PageQuery getPageQueryNotNull() {
		Integer pageNo = getIntegerParameter("pageNo");
		Integer pageSize = getIntegerParameter("pageSize");

		if (pageNo == null || pageSize == null) {
			return null;
		}

		return new PageQuery(pageNo, pageSize);
	}

	protected PageQuery getDataTablePageQuery() {
		Integer pageSize = getIntegerParameter("iDisplayLength", 10);
		Integer iDisplayStart = getIntegerParameter("iDisplayStart", 1);
		Integer pageNo = iDisplayStart / pageSize + 1;
		PageQuery page = new PageQuery(pageNo, pageSize);
		return page;
	}

	protected void setOrderBy(PageQuery page) {
		String orderBy = getStringParameter("order_by");
		String orderType = getStringParameter("order_type");

		if (!StringUtils.isBlank(orderBy)) {
			page.setOrderBy(orderBy);
			page.setOrderType(orderType);
		}
	}

	protected boolean isExportRequest() {
		return getStringParameter(ContextConstants.EXPORT_FLAG) != null;
	}

	protected void setDataForExport(Object object) {
		getHttpServletRequest().setAttribute(ContextConstants.EXPORT_DATA, object);
	}

	// /**
	// * 导入excel
	// * @param file
	// * @param response
	// * @return
	// * @throws IOException
	// */
	// protected Workbook excelImport(MultipartFile file, HttpServletResponse
	// response) throws IOException {
	// String filePath = file.getOriginalFilename();
	//
	// return null;
	// // if (defaultExcelTypeFlag) {
	// // return ExcelUtil.importExcel(inputStream);
	// // } else {
	// // return ExcelUtil.importExcel(file, inputStream);
	// // }
	//
	// }

	/**
	 * 导出excel
	 * 
	 * @param objects file 导出的数据
	 * @param type 导出的数据类型
	 * @param file 文件名
	 * @param withHeader 是否导出报头
	 * @param excelFieldDefinition 导出格式定义
	 * @param beginRowNum 数据起始行号
	 * @param response 导出流
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	protected <T> ExcelResult excelExport(Collection<T> objects, Class<T> type, String file, boolean withHeader, String excelFieldDefinition, int beginRowNum,
			HttpServletResponse response) throws IOException, IllegalArgumentException, IllegalAccessException {
		List<ExcelFieldDefinition> fieldDefinitionList = getFieldDefines(excelFieldDefinition);
		String fileName = URLEncoder.encode(file, "UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-msexcel");
		response.setHeader("Content-type", "application/x-msexcel");
		response.setHeader("content-disposition", "attachment;filename=" + fileName);
		OutputStream outputStream = response.getOutputStream();
		ExcelResult excelResult = null;

		if (withHeader) {
			excelResult = ExcelUtil.exportExcelWithHeader(objects, type, fileName, outputStream, fieldDefinitionList, beginRowNum);
		} else {
			excelResult = ExcelUtil.exportExcel(objects, type, fileName, outputStream, fieldDefinitionList, beginRowNum);
		}

		outputStream.flush();
		return excelResult;
	}

	protected List<ExcelFieldDefinition> getFieldDefines(String excelFieldDefinitionStr) {
		if (excelFieldDefinitionStr == null) {
			return null;
		}

		JSONArray jsonArray = (JSONArray) JSON.parse(excelFieldDefinitionStr);
		int size = jsonArray.size();

		if (size == 0) {
			return null;
		}

		List<ExcelFieldDefinition> fieldDefineList = new ArrayList<ExcelFieldDefinition>();

		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();

			for (Map.Entry<String, Object> entry : entrySet) {
				ExcelFieldDefinition excelFieldDefinition = new ExcelFieldDefinition(Integer.valueOf(entry.getKey()), entry.getKey());
				fieldDefineList.add(excelFieldDefinition);
			}
		}

		return fieldDefineList;
	}

	protected void excelExport(String file, String cellDefineJson, Object exportObject, HttpServletResponse response) throws IOException {
		List<CellDefine> cellDefines = getCellDefines(cellDefineJson);
		String fileName = URLEncoder.encode(file + ExcelExport.DEFAULT_EXCEL_TYPE, "UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-msexcel");
		response.setHeader("Content-type", "application/x-msexcel");
		response.setHeader("content-disposition", "attachment;filename=" + fileName);
		OutputStream out = response.getOutputStream();
		ExcelExport.exportToOutputStream(cellDefines, exportObject, out);
		out.flush();
		Result result = new Result(ResultCode.COMMON_SUCCESS, false);
		result.setProperty("file", file);
		getHttpServletRequest().setAttribute("result", result);
	}

	private List<CellDefine> getCellDefines(String cellDefineJson) {
		if (cellDefineJson == null) {
			return null;
		}

		JSONArray jsonArray = (JSONArray) JSON.parse(cellDefineJson);
		int size = jsonArray.size();

		if (size == 0) {
			return null;
		}

		List<CellDefine> cellDefineList = new ArrayList<CellDefine>();

		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();

			for (Map.Entry<String, Object> entry : entrySet) {
				cellDefineList.add(new CellDefine(entry.getKey(), entry.getValue().toString()));
			}
		}

		return cellDefineList;
	}

}
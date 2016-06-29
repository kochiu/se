package com.sztx.se.common.util.poi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sztx.se.dataaccess.mysql.ddl.DdlConfig;

/**
 * @author: chenggui.huang
 * @date: 2014-02-13 14:57
 */
public class ExcelExport {

	public final static String DEFAULT_EXCEL_TYPE = ".xls";

	private final static String DEFAULT_FONT_NAME = "新宋体";

	private final static int DEFAULT_COLUMN_WIDTH = 14;

	private final static short DEFAULT_FONT_HEIGHT = 12;

	public static enum ExcelType {
		XLS, XLSX
	}

	public static void exportToOutputStream(Object exportObject, OutputStream outputStream) throws IOException {
		export(exportObject).write(outputStream);
	}

	public static Workbook export(Object exportObject) {
		return export(getDefaultDefines(exportObject), exportObject, ExcelType.XLS);
	}

	public static void exportToOutputStream(Collection<CellDefine> defines, Object exportObject, OutputStream outputStream) throws IOException {
		export(defines, exportObject).write(outputStream);
	}

	public static Workbook export(Collection<CellDefine> defines, Object exportObject) {
		return export(defines, exportObject, ExcelType.XLS);
	}

	public static void exportToOutputStream(Object exportObject, ExcelType excelType, OutputStream outputStream) throws IOException {
		export(exportObject, excelType).write(outputStream);
	}

	public static Workbook export(Object exportObject, ExcelType excelType) {
		return export(null, exportObject, excelType);
	}

	public static void exportToOutputStream(Collection<CellDefine> defines, Object exportObject, ExcelType excelType, OutputStream outputStream)
			throws IOException {
		export(defines, exportObject, excelType).write(outputStream);
	}

	public static Workbook export(Collection<CellDefine> defines, Object exportObject, ExcelType excelType) {
		Workbook workbook = null;

		if (ExcelType.XLS.equals(excelType)) {
			workbook = new HSSFWorkbook();
		} else {
			workbook = new XSSFWorkbook();
		}

		Sheet sheet = workbook.createSheet();
		sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);

		if (defines == null) {
			defines = getDefaultDefines(exportObject);
		}

		if (defines == null || defines.isEmpty()) {
			return workbook;
		}

		createHeaderRow(defines, sheet, createHeaderStyle(workbook));
		createDataRows(defines, sheet, exportObject, createDataStyle(workbook));
		return workbook;
	}

	private static CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT);
		font.setFontName(DEFAULT_FONT_NAME);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		return style;
	}

	private static CellStyle createDataStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT);
		font.setFontName(DEFAULT_FONT_NAME);
		style.setFont(font);
		return style;
	}

	private static void createHeaderRow(Collection<CellDefine> defines, Sheet sheet, CellStyle cellStyle) {
		Row headerRow = sheet.createRow(0);
		int count = 0;

		for (CellDefine define : defines) {
			Cell cell = headerRow.createCell(count++);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(define.getCellDescr());
		}
	}

	private static void createDataRows(Collection<CellDefine> defines, Sheet sheet, Object exportObject, CellStyle cellStyle) {
		if (exportObject == null) {
			return;
		}

		if (exportObject instanceof Collection) {
			Collection<?> collection = (Collection<?>) exportObject;

			for (Object object : collection) {
				createDataRow(defines, sheet, object, cellStyle);
			}

			return;
		}

		Class<?> clazz = exportObject.getClass();
		if (clazz.isArray()) {
			for (int i = 0, len = Array.getLength(exportObject); i < len; i++) {
				createDataRow(defines, sheet, Array.get(exportObject, i), cellStyle);
			}

			return;
		}

		createDataRow(defines, sheet, exportObject, cellStyle);
	}

	private static void createDataRow(Collection<CellDefine> defines, Sheet sheet, Object exportObject, CellStyle cellStyle) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);

		if (exportObject instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) exportObject;

			for (CellDefine define : defines) {
				createDataCell(row, map.get(define.getCellName()), cellStyle);
			}

			return;
		}

		if (exportObject instanceof Collection) {
			Collection<?> collection = (Collection<?>) exportObject;

			for (Object object : collection) {
				createDataCell(row, object, cellStyle);
			}

			return;
		}

		Class<?> clazz = exportObject.getClass();

		if (clazz.isArray()) {
			for (int i = 0, len = Array.getLength(exportObject); i < len; i++) {
				createDataCell(row, Array.get(exportObject, i), cellStyle);
			}

			return;
		}

		for (CellDefine key : defines) {
			createDataCell(row, getFieldValue(key.getCellName(), exportObject), cellStyle);
		}

	}

	private static Cell createDataCell(Row row, Object value, CellStyle cellStyle) {
		int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
		Cell cell = row.createCell(cellIndex);
		cell.setCellValue(formatToString(value));
		return cell;
	}

	private static Object getFieldValue(String filedName, Object exportObject) {
		Class<?> clazz = exportObject.getClass();
		Method method = null;

		try {
			method = clazz.getMethod("get" + upperFistChar(filedName));
		} catch (Exception e) {
		}

		if (method == null) {
			try {
				if (filedName.startsWith("is")) {
					method = clazz.getMethod(filedName);
				} else {
					method = clazz.getMethod("is" + upperFistChar(filedName));
				}
			} catch (Exception e) {
			}
		}

		try {
			if (method != null) {
				return method.invoke(exportObject);
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private static String formatToString(Object object) {
		if (object == null) {
			return "";
		} else if (object instanceof Date) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) object);
		} else {
			return object.toString();
		}
	}

	private static Collection<CellDefine> getDefaultDefines(Object exportObject) {
		if (exportObject == null) {
			return null;
		}

		if (exportObject instanceof Map) {
			Set<?> keys = ((Map<?, ?>) exportObject).keySet();
			List<CellDefine> defines = new ArrayList<>(keys.size());

			for (Object obj : keys) {
				defines.add(new CellDefine(obj.toString(), obj.toString()));
			}

			return defines;
		}

		if (exportObject instanceof Collection) {
			Collection<?> objects = (Collection<?>) exportObject;

			if (objects.isEmpty()) {
				return null;
			}

			Object obj = objects.iterator().next();
			return getDefaultDefines(obj);
		}

		Class<?> clazz = exportObject.getClass();

		if (clazz.isEnum()) {
			return null;
		}

		if (clazz.isArray()) {
			int len = Array.getLength(exportObject);

			if (len == 0) {
				return null;
			}

			return getDefaultDefines(Array.get(exportObject, 0));
		}

		List<Method> getters = getMethodsStartWith(exportObject, "get");
		List<CellDefine> defines = new ArrayList<>(getters.size());

		for (Method method : getters) {
			String cellName = lowerFistChar(method.getName().substring(3));
			defines.add(new CellDefine(cellName, cellName));
		}

		return defines;
	}

	private static String lowerFistChar(String str) {
		return String.valueOf(str.charAt(0)).toLowerCase() + str.substring(1);
	}

	private static String upperFistChar(String str) {
		return String.valueOf(str.charAt(0)).toUpperCase() + str.substring(1);
	}

	private static List<Method> getMethodsStartWith(Object object, String startWith) {
		if (object == null) {
			return null;
		}

		Method[] methods = object.getClass().getMethods();
		List<Method> methodList = new ArrayList<Method>();

		for (Method method : methods) {
			if (method.getName().startsWith(startWith) && method.getName().length() > 3 && !method.getName().equals("getClass")) {
				methodList.add(method);
			}
		}
		return methodList;
	}

	private static List<CellDefine> getCellDefines(String cellDefineJson) {
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

	public static void main(String[] args) throws IOException {
		String cellDefineJson = "[{\"table\": \"表名\"},{\"column\": \"分表字段\"},{\"tableNum\": \"表个数\"},{\"db\": \"数据库\"},{\"dbNum\": \"数据库个数\"}]";
		List<CellDefine> cellDefineList = ExcelExport.getCellDefines(cellDefineJson);
		List<DdlConfig> list = new ArrayList<DdlConfig>();
		DdlConfig ddlConfig1 = new DdlConfig("user", "id", 10, "demo", 2);
		DdlConfig ddlConfig2 = new DdlConfig("baby", "name", 4, "demo", 2);
		list.add(ddlConfig1);
		list.add(ddlConfig2);
		OutputStream out = new FileOutputStream("e:\\test4.xls");
		ExcelExport.exportToOutputStream(cellDefineList, list, out);
	}
}

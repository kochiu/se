package com.kochiu.se.common.util.poi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.kochiu.se.dataaccess.mysql.ddl.DdlConfig;
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

import com.kochiu.se.common.util.FileUtil;

/**
 * 
 * @author zhihongp
 *
 */
public class ExcelUtil {

	public final static String DEFAULT_EXCEL_TYPE = ".xls";

	public final static String NEW_EXCEL_TYPE = ".xlsx";

	public final static String DEFAULT_EXCEL_SUFFIX = "xls";

	public final static String NEW_EXCEL_SUFFIX = "xlsx";

	private final static String DEFAULT_FONT_NAME = "新宋体";

	private final static int DEFAULT_COLUMN_WIDTH = 14;

	private final static short DEFAULT_FONT_HEIGHT = 12;

	public static enum ExcelType {
		XLS, XLSX
	}

	/**
	 * 
	 * @param file
	 * @param beginRowNum
	 * @param type
	 * @param action
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> ExcelResult importExcelWithHeader(String file, int beginRowNum, Class<T> type, ExcelCallback<T> action) throws IOException,
			InstantiationException, IllegalAccessException {
		InputStream inputStream = new FileInputStream(file);
		ExcelResult excelResult = importExcelWithHeader(file, inputStream, beginRowNum, type, action);
		return excelResult;
	}

	/**
	 * 
	 * @param file
	 * @param beginIndex
	 * @param type
	 * @param action
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> ExcelResult importExcelWithHeader(String file, InputStream inputStream, int beginRowNum, Class<T> type, ExcelCallback<T> action)
			throws IOException, InstantiationException, IllegalAccessException {
		Date start = new Date();
		ExcelType excelType = getExcelTyByFile(file);
		ExcelResult excelResult = importE(inputStream, excelType, true, beginRowNum, type, action);
		Date end = new Date();
		long cost = end.getTime() - start.getTime();
		excelResult.setCost(cost);
		return excelResult;
	}

	/**
	 * 
	 * @param file
	 * @param type
	 * @param action
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> ExcelResult importExcel(String file, Class<T> type, ExcelCallback<T> action) throws IOException, InstantiationException,
			IllegalAccessException {
		InputStream inputStream = new FileInputStream(file);
		ExcelResult excelResult = importExcel(file, inputStream, type, action);
		return excelResult;
	}

	/**
	 * 
	 * @param file
	 * @param type
	 * @param action
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> ExcelResult importExcel(String file, InputStream inputStream, Class<T> type, ExcelCallback<T> action) throws IOException,
			InstantiationException, IllegalAccessException {
		Date start = new Date();
		ExcelType excelType = getExcelTyByFile(file);
		ExcelResult excelResult = importE(inputStream, excelType, false, 0, type, action);
		Date end = new Date();
		long cost = end.getTime() - start.getTime();
		excelResult.setCost(cost);
		return excelResult;
	}

	public static <T> ExcelResult exportExcelWithHeader(Collection<T> objects, Class<T> type, String file, List<ExcelFieldDefinition> fieldDefinitionList,
			int beginRowNum) throws IllegalArgumentException, IllegalAccessException, IOException {
		OutputStream outputStream = new FileOutputStream(file);
		ExcelResult excelResult = exportExcelWithHeader(objects, type, file, outputStream, fieldDefinitionList, beginRowNum);
		return excelResult;
	}

	public static <T> ExcelResult exportExcelWithHeader(Collection<T> objects, Class<T> type, String file, OutputStream outputStream,
			List<ExcelFieldDefinition> fieldDefinitionList, int beginRowNum) throws IllegalArgumentException, IllegalAccessException, IOException {
		Date start = new Date();
		ExcelType excelType = getExcelTyByFile(file);
		ExcelResult excelResult = exportE(objects, type, outputStream, excelType, true, fieldDefinitionList, beginRowNum);
		Date end = new Date();
		long cost = end.getTime() - start.getTime();
		excelResult.setCost(cost);
		return excelResult;
	}

	public static <T> ExcelResult exportExcel(Collection<T> objects, Class<T> type, String file, List<ExcelFieldDefinition> fieldDefinitionList, int beginRowNum)
			throws IllegalArgumentException, IllegalAccessException, IOException {
		OutputStream outputStream = new FileOutputStream(file);
		ExcelResult excelResult = exportExcel(objects, type, file, outputStream, fieldDefinitionList, beginRowNum);
		return excelResult;
	}

	public static <T> ExcelResult exportExcel(Collection<T> objects, Class<T> type, String file, OutputStream outputStream,
			List<ExcelFieldDefinition> fieldDefinitionList, int beginRowNum) throws IllegalArgumentException, IllegalAccessException, IOException {
		Date start = new Date();
		ExcelType excelType = getExcelTyByFile(file);
		ExcelResult excelResult = exportE(objects, type, outputStream, excelType, false, fieldDefinitionList, beginRowNum);
		Date end = new Date();
		long cost = end.getTime() - start.getTime();
		excelResult.setCost(cost);
		return excelResult;
	}

	public static Object getValue(Row row, int cellNum) {
		if (row == null) {
			return null;
		}

		Cell cell = row.getCell(cellNum);

		if (cell == null) {
			return null;
		}

		return getValue(cell);
	}

	public static String getValueStr(Row row, int cellNum) {
		if (row == null) {
			return null;
		}

		Cell cell = row.getCell(cellNum);

		if (cell == null) {
			return null;
		}

		return getValueStr(cell);
	}

	public static Object getValue(Cell cell) {
		int cellType = cell.getCellType();

		if (cellType == Cell.CELL_TYPE_BOOLEAN) {
			return cell.getBooleanCellValue();
		} else if (cellType == Cell.CELL_TYPE_NUMERIC) {
			return cell.getNumericCellValue();
		} else if (cellType == Cell.CELL_TYPE_FORMULA) {
			return cell.getCellFormula();
		} else {
			return cell.getStringCellValue();
		}
	}

	public static String getValueStr(Cell cell) {
		int cellType = cell.getCellType();

		if (cellType == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		} else if (cellType == Cell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else if (cellType == Cell.CELL_TYPE_FORMULA) {
			return cell.getCellFormula();
		} else {
			return cell.getStringCellValue();
		}
	}

	/**
	 * 
	 * @param inputStream
	 * @param excelType
	 * @param withHeader
	 * @param beginRowNum
	 * @param type
	 * @param action
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static <T> ExcelResult importE(InputStream inputStream, ExcelType excelType, boolean withHeader, int beginRowNum, Class<T> type,
			ExcelCallback<T> action) throws IOException, InstantiationException, IllegalAccessException {
		ExcelResult excelResult = null;
		Workbook workbook = null;

		if (ExcelType.XLS.equals(excelType)) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			workbook = new XSSFWorkbook(inputStream);
		}

		Sheet sheet = workbook.getSheetAt(0);

		if (sheet == null) {
			excelResult = new ExcelResult(false, 0, 0, 0);
			return excelResult;
		}

		T obj = null;
		excelResult = new ExcelResult();
		int lastRowNum = sheet.getLastRowNum();
		int totalNum = withHeader ? lastRowNum : lastRowNum + 1;
		excelResult.setTotalNum(totalNum);
		int successNum = 0;
		int failureNum = 0;

		for (int rowNum = 0; rowNum <= lastRowNum; rowNum++) {
			if (withHeader && rowNum < beginRowNum) {
				continue;
			}

			Row row = sheet.getRow(rowNum);

			if (row == null) {
				continue;
			}

			if (obj == null) {
				obj = type.newInstance();
			}

			Field[] fields = type.getDeclaredFields();

			for (Field field : fields) {
				ExcelField excelField = field.getAnnotation(ExcelField.class);

				if (excelField == null) {
					continue;
				}

				int cellNum = excelField.order();
				Class<?> fieldType = field.getType();
				Object value = getValue(row, cellNum);
				Object fieldValue = null;
				
				if (value != null) {
					Class<?> valueType = value.getClass();
					fieldValue = getFiledValue(value, fieldType, valueType);
				}
				
				field.setAccessible(true);
				field.set(obj, fieldValue);
			}

			boolean flag = action.handleImportData(obj, rowNum);

			if (flag) {
				successNum++;
			} else {
				failureNum++;
			}

			excelResult.setSuccessNum(successNum);
			excelResult.setFailureNum(failureNum);
		}

		boolean isSuccess = true;

		if (totalNum != successNum) {
			isSuccess = false;
		}

		excelResult.setSuccess(isSuccess);
		return excelResult;
	}

	private static <T> ExcelResult exportE(Collection<T> objects, Class<T> type, OutputStream outputStream, ExcelType excelType, boolean withHeader,
			List<ExcelFieldDefinition> fieldDefinitionList, int beginRowNum) throws IllegalArgumentException, IllegalAccessException, IOException {
		Workbook workbook = null;

		if (ExcelType.XLS.equals(excelType)) {
			workbook = new HSSFWorkbook();
		} else {
			workbook = new XSSFWorkbook();
		}

		Sheet sheet = workbook.createSheet();
		sheet.setDefaultColumnWidth(DEFAULT_COLUMN_WIDTH);
		int totalNum = objects.size();
		ExcelResult excelResult = new ExcelResult();
		excelResult.setTotalNum(totalNum);
		int successNum = 0;
		int failureNum = 0;

		if (withHeader) {
			Row row = sheet.createRow(0);

			if (fieldDefinitionList != null) {
				for (ExcelFieldDefinition fieldDefinition : fieldDefinitionList) {
					int cellNum = fieldDefinition.getOrder();
					String cellValue = fieldDefinition.getHeader();

					if (cellValue != null) {
						CellStyle cellStyle = getDefaultHeaderStyle(workbook);
						Cell cell = row.createCell(cellNum);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(cellValue);
					}
				}
			} else {
				Field[] fields = type.getDeclaredFields();

				for (Field field : fields) {
					ExcelField excelField = field.getAnnotation(ExcelField.class);

					if (excelField == null) {
						continue;
					}

					int cellNum = excelField.order();
					String cellValue = excelField.header();

					if (cellValue != null) {
						CellStyle cellStyle = getDefaultHeaderStyle(workbook);
						Cell cell = row.createCell(cellNum);
						cell.setCellStyle(cellStyle);
						cell.setCellValue(cellValue);
					}
				}
			}
		}

		for (T obj : objects) {
			try {
				Row row = sheet.createRow(sheet.getLastRowNum() + 1);

				Field[] fields = type.getDeclaredFields();

				for (Field field : fields) {
					ExcelField excelField = field.getAnnotation(ExcelField.class);

					if (excelField == null) {
						continue;
					}

					int cellNum = excelField.order();
					field.setAccessible(true);
					Object objValue = field.get(obj);
					CellStyle cellStyle = getDefaultDataStyle(workbook);
					Cell cell = row.createCell(cellNum);
					cell.setCellStyle(cellStyle);

					if (objValue == null) {
						String fieldValue = "";
						cell.setCellValue(fieldValue);
					} else if (objValue instanceof String) {
						String fieldValue = (String) objValue;
						cell.setCellValue(fieldValue);
					} else if (objValue instanceof Integer) {
						Integer fieldValue = (Integer) objValue;
						cell.setCellValue(fieldValue);
					} else if (objValue instanceof Boolean) {
						Boolean fieldValue = (Boolean) objValue;
						cell.setCellValue(fieldValue);
					} else if (objValue instanceof Float) {
						Float fieldValue = (Float) objValue;
						cell.setCellValue(fieldValue);
					} else if (objValue instanceof Short) {
						Short fieldValue = (Short) objValue;
						cell.setCellValue(fieldValue);
					} else if (objValue instanceof Date) {
						Date fieldValue = (Date) objValue;
						cell.setCellValue(fieldValue);
					} else {
						String fieldValue = objValue.toString();
						cell.setCellValue(fieldValue);
					}
				}

				successNum++;
			} catch (Exception e) {
				failureNum++;
			}

			excelResult.setSuccessNum(successNum);
			excelResult.setFailureNum(failureNum);
		}

		boolean isSuccess = true;

		if (totalNum != successNum) {
			isSuccess = false;
		}

		excelResult.setSuccess(isSuccess);
		workbook.write(outputStream);
		return excelResult;
	}

	private static ExcelType getExcelTyByFile(String file) {
		String suffix = FileUtil.getFileSuffix(file);
		ExcelType excelType = getExcelType(suffix);
		return excelType;
	}

	private static ExcelType getExcelType(String suffix) {
		ExcelType excelType = ExcelType.XLS;

		if (NEW_EXCEL_SUFFIX.equalsIgnoreCase(suffix)) {
			excelType = ExcelType.XLSX;
		}

		return excelType;
	}

	@SuppressWarnings("deprecation")
	private static Object getFiledValue(Object value, Class<?> fieldType, Class<?> valueType) {
		Object fieldValue = value;

		if (!fieldType.isAssignableFrom(valueType)) {
			if (fieldType.isAssignableFrom(String.class)) {
				fieldValue = String.valueOf(value);
			} else if (fieldType.isAssignableFrom(Integer.class)) {
				if (valueType.isAssignableFrom(String.class)) {
					String valueTmp = (String) value;
					fieldValue = Integer.valueOf(valueTmp);
				} else if (valueType.isAssignableFrom(Double.class)) {
					Double valueTmp = (Double) value;
					fieldValue = valueTmp.intValue();
				} else if (valueType.isAssignableFrom(Boolean.class)) {
					Boolean valueTmp = (Boolean) value;

					if (valueTmp) {
						fieldValue = 1;
					} else {
						fieldValue = 0;
					}
				}
			} else if (fieldType.isAssignableFrom(Long.class)) {
				if (valueType.isAssignableFrom(String.class)) {
					String valueTmp = (String) value;
					fieldValue = Long.valueOf(valueTmp);
				} else if (valueType.isAssignableFrom(Double.class)) {
					Double valueTmp = (Double) value;
					fieldValue = valueTmp.longValue();
				} else if (valueType.isAssignableFrom(Boolean.class)) {
					Boolean valueTmp = (Boolean) value;

					if (valueTmp) {
						fieldValue = 1;
					} else {
						fieldValue = 0;
					}
				}
			} else if (fieldType.isAssignableFrom(Double.class)) {
				if (valueType.isAssignableFrom(String.class)) {
					String valueTmp = (String) value;
					fieldValue = Double.valueOf(valueTmp);
				} else if (valueType.isAssignableFrom(Boolean.class)) {
					Boolean valueTmp = (Boolean) value;

					if (valueTmp) {
						fieldValue = 1;
					} else {
						fieldValue = 0;
					}
				}
			} else if (fieldType.isAssignableFrom(Boolean.class)) {
				if (valueType.isAssignableFrom(String.class)) {
					String valueTmp = (String) value;
					fieldValue = Boolean.valueOf(valueTmp);
				} else if (valueType.isAssignableFrom(Double.class)) {
					Double valueTmp = (Double) value;

					if (Double.compare(valueTmp, 1) == 0) {
						fieldValue = true;
					} else if (Double.compare(valueTmp, 0) == 0) {
						fieldValue = false;
					}
				}
			} else if (fieldType.isAssignableFrom(Date.class)) {
				if (valueType.isAssignableFrom(String.class)) {
					String valueTmp = (String) value;
					fieldValue = new Date(valueTmp);
				}
			} else if (fieldType.isAssignableFrom(Short.class)) {
				if (valueType.isAssignableFrom(String.class)) {
					String valueTmp = (String) value;
					fieldValue = Short.valueOf(valueTmp);
				} else if (valueType.isAssignableFrom(Double.class)) {
					Double valueTmp = (Double) value;
					fieldValue = valueTmp.shortValue();
				} else if (valueType.isAssignableFrom(Boolean.class)) {
					Boolean valueTmp = (Boolean) value;

					if (valueTmp) {
						fieldValue = 1;
					} else {
						fieldValue = 0;
					}
				}
			} else if (fieldType.isAssignableFrom(Float.class)) {
				if (valueType.isAssignableFrom(String.class)) {
					String valueTmp = (String) value;
					fieldValue = Float.valueOf(valueTmp);
				} else if (valueType.isAssignableFrom(Double.class)) {
					Double valueTmp = (Double) value;
					fieldValue = valueTmp.floatValue();
				} else if (valueType.isAssignableFrom(Boolean.class)) {
					Boolean valueTmp = (Boolean) value;

					if (valueTmp) {
						fieldValue = 1;
					} else {
						fieldValue = 0;
					}
				}
			}
		}

		return fieldValue;
	}

	private static CellStyle getDefaultHeaderStyle(Workbook workbook) {
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

	private static CellStyle getDefaultDataStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontHeightInPoints(DEFAULT_FONT_HEIGHT);
		font.setFontName(DEFAULT_FONT_NAME);
		style.setFont(font);
		return style;
	}

	@SuppressWarnings("unused")
	private static ExcelResult testExportExcel(String file) throws IllegalArgumentException, IllegalAccessException, IOException {
		List<DdlConfig> objects = new ArrayList<DdlConfig>();
		DdlConfig ddlConfig1 = new DdlConfig("user", "id", 10, "demo", 2, true);
		DdlConfig ddlConfig2 = new DdlConfig("order", "name", 4, "demo", 2, false);
		DdlConfig ddlConfig3 = new DdlConfig("good", "nick", 12, "demo", 2, true);
		objects.add(ddlConfig1);
		objects.add(ddlConfig2);
		objects.add(ddlConfig3);
		ExcelResult excelResult = ExcelUtil.exportExcelWithHeader(objects, DdlConfig.class, file, null, 1);
		return excelResult;
	}

	@SuppressWarnings("unused")
	private static ExcelResult testImportExcel(String file) throws InstantiationException, IllegalAccessException, IOException {
		ExcelResult excelResult = ExcelUtil.importExcelWithHeader(file, 1, DdlConfig.class, new ExcelCallback<DdlConfig>() {
			@Override
			public boolean handleImportData(DdlConfig obj, int rowNum) {
				boolean flag = true;
				if (rowNum == 1) {
					flag = false;
				}
				System.out.println("rowNum=" + rowNum + " Data=" + obj);
				return flag;
			}
		});

		return excelResult;
	}

}

package com.kochiu.se.common.util.poi;

/**
 * 
 * @author zhihongp
 *
 * @param <T>
 */
public interface ExcelCallback<T> {

	/**
	 * 处理excel导入的数据(一次处理一条数据)
	 * 
	 * @param obj 处理的对象
	 * @param rowNum 当前处理的行码
	 * @return 处理结果(true-成功, false-失败)
	 */
	public boolean handleImportData(T obj, int rowNum);
}

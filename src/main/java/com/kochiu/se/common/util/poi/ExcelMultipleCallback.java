package com.kochiu.se.common.util.poi;

import java.util.List;

/**
 * 
 * @author zhihongp
 *
 * @param <T>
 */
public interface ExcelMultipleCallback<T> {

	/**
	 * 批处理excel导入的数据(一次处理多条数据)
	 * 
	 * @param objList 处理的对象集
	 * @param perNum 每次处理的数据条数(注：每次最多处理1000条)
	 * @param rowStartNum 当前处理的起始行码
	 * @param rowEndNum 当前处理的结束行码
	 * @return 处理结果(true-成功, false-失败)
	 */
	public boolean handleImportData(List<T> objList, int perNum, int rowStartNum, int rowEndNum);
}

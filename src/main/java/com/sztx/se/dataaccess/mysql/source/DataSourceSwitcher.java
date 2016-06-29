package com.sztx.se.dataaccess.mysql.source;

/**
 * 
 * @author zhihongp
 * 
 */
public class DataSourceSwitcher {
	private static final ThreadLocal<String> dbContextHolder = new ThreadLocal<String>();
	private static final ThreadLocal<Boolean> dbContextReadWriteSeparate = new ThreadLocal<Boolean>();

	public static void setDataSourceTypeInContext(String dataSourceType) {
		dbContextHolder.set(dataSourceType);
		dbContextReadWriteSeparate.set(true);
	}

	/**
	 * 强制设置数据源到上下文中，可以跳过读写分离
	 * 
	 * @param dataSourceType
	 */
	public static void setDataSourceTypeForceInContext(String dataSourceType) {
		dbContextHolder.set(dataSourceType);
		dbContextReadWriteSeparate.set(false);
	}

	public static String getDataSourceType() {
		String dataSourceType = (String) dbContextHolder.get();
		return dataSourceType;
	}

	public static String getDataSourceTypeFromContext() {
		String dataSourceType = (String) dbContextHolder.get();
		return dataSourceType;
	}

	public static boolean getReadWriteSeparateFromContext() {
		Boolean readWriteSeparateFlag = dbContextReadWriteSeparate.get();

		if (readWriteSeparateFlag == null) {
			return true;
		} else {
			return readWriteSeparateFlag;
		}
	}

	public static void clearDataSourceType() {
		dbContextHolder.remove();
		dbContextReadWriteSeparate.remove();
	}

}

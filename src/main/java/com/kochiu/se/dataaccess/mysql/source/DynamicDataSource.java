package com.kochiu.se.dataaccess.mysql.source;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.common.util.ConsistenHashUtil;
import com.kochiu.se.core.context.SpringContextHolder;
import com.kochiu.se.dataaccess.mysql.client.InterceptorUtil;
import com.kochiu.se.dataaccess.mysql.ddl.DdlConfig;
import com.kochiu.se.dataaccess.mysql.ddl.DdlDb;
import com.kochiu.se.dataaccess.mysql.ddl.DdlTable;
import com.kochiu.se.dataaccess.mysql.ddl.interceptor.DdlInterceptor;
import com.kochiu.se.dataaccess.mysql.source.interceptor.SqlLogInterceptor;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源
 * 
 * @author zhihongp
 * 
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	private static final String DDL_DB_CLASS = "com.kochiu.se.dataaccess.mysql.ddl.interceptor.DdlDbInterceptor";

	private static final String DDL_TABLE_CLASS = "com.kochiu.se.dataaccess.mysql.ddl.interceptor.DdlTableInterceptor";

	private static final String SQL_LOG_CLASS = "com.kochiu.se.dataaccess.mysql.source.interceptor.SqlLogInterceptor";

	private static final String DEFAULT_DDL_FILE = "ddl-config.xml";

	private static Map<String, String> dataSourceDbMap;

	private static Map<String, DdlDb> dbDataSourceMap;

	private Map<Object, Object> dataSourceMap;

	private String defaultTargetDataSourceKey;

	private String ddlFile;

	private boolean ddlFlag;

	/**
	 * 读写分离开关，默认为false(true-从库读取，false-主库读取)
	 */
	private boolean readWriteSeparateFlag;

	/**
	 * 日志开关，默认为false不打开
	 */
	private boolean openLog;

	private long slowLimit = 1000l;

	public Map<String, String> getDataSourceDbMap() {
		return dataSourceDbMap;
	}

	public void setDataSourceDbMap(Map<String, String> dataSourceDbMap) {
		DynamicDataSource.dataSourceDbMap = dataSourceDbMap;
	}

	public Map<String, DdlDb> getDbDataSourceMap() {
		return dbDataSourceMap;
	}

	public void setDbDataSourceMap(Map<String, DdlDb> dbDataSourceMap) {
		DynamicDataSource.dbDataSourceMap = dbDataSourceMap;
	}

	public void setDefaultTargetDataSourceKey(String defaultTargetDataSourceKey) {
		this.defaultTargetDataSourceKey = defaultTargetDataSourceKey;
	}

	public void setDataSourceMap(Map<Object, Object> dataSourceMap) {
		this.dataSourceMap = dataSourceMap;
	}

	public void setDdlFile(String ddlFile) {
		this.ddlFile = ddlFile;
	}

	public void setDdlFlag(boolean ddlFlag) {
		this.ddlFlag = ddlFlag;
	}

	public void setReadWriteSeparateFlag(boolean readWriteSeparateFlag) {
		this.readWriteSeparateFlag = readWriteSeparateFlag;
	}

	public void setOpenLog(boolean openLog) {
		this.openLog = openLog;
	}

	public void setSlowLimit(long slowLimit) {
		this.slowLimit = slowLimit;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		String dataSource = DataSourceSwitcher.getDataSourceType();

		if (dataSource == null) {
			dataSource = defaultTargetDataSourceKey;
		}

		if (dataSource == null) {
			throw new SystemException("Can not find a dataSource.");
		}

		return dataSource;
	}

	/**
	 * 根据db获取数据源，如果isSlave为true则获取备库数据源，否则则获取主库数据源(注：如果备库数据源获取不到则使用主库数据源代替)
	 * 
	 * @param db 数据库名
	 * @param isSlave 是否获取备库
	 * @return 数据源
	 */
	public static String getDataSourceByDb(String db, boolean isSlave) {
		return DdlInterceptor.getDataSourceByDb(db, isSlave);
	}

	/**
	 * 根据MasterDataSource获取其对应的备库数据源(注：如果masterDataSource为null则直接获取当前数据源的备库数据源)
	 * 
	 * @param masterDataSource 主库数据源
	 * @return 备库数据源
	 */
	public static String getSlaveDataSourceByMasterDataSource(String masterDataSource) {
		return DdlInterceptor.getSlaveDataSourceByMasterDataSource(masterDataSource);
	}

	/**
	 * 根据数据源获取db
	 * 
	 * @param dataSource 数据源
	 * @return 数据库名
	 */
	public static String getDbByDataSource(String dataSource) {
		return DdlInterceptor.getDbByDataSource(dataSource);
	}

	/**
	 * 获取当前数据库
	 * 
	 * @return 当前数据库
	 */
	public static String getCurrentDb() {
		String currentDataSourceFromContext = DataSourceSwitcher.getDataSourceTypeFromContext();
		String currentDataSource = currentDataSourceFromContext;

		if (StringUtils.isBlank(currentDataSource)) {
			currentDataSource = DataSourceSwitcher.getDataSourceType();
		}

		return getDbByDataSource(currentDataSource);
	}

	public Set<String> getDataSources() {
		return dataSourceDbMap.keySet();
	}

	public DataSource getDataSource(String dataSourceKey) {
		DataSource dataSource = (DataSource) dataSourceMap.get(dataSourceKey);
		return dataSource;
	}

	public void initSqlLog() {
		if (openLog) {
			SqlLogInterceptor.setOpenLog(openLog);
			SqlLogInterceptor.setSlowLimit(slowLimit);
			InterceptorUtil.setSqlSessionFactoryBean(SpringContextHolder.applicationContext.getBean(SqlSessionFactoryBean.class));
			InterceptorUtil.dynamicAddInterceptor(SQL_LOG_CLASS);
		}
	}

	/**
	 * 初始化ddl配置，使用一致性Hash算法
	 * 
	 * @throws Exception
	 */
	public void initDdlConfig() {
		if (ddlFlag) {
			try {
				DdlInterceptor.setDdlFlag(ddlFlag);
				DdlInterceptor.setReadWriteSeparateFlag(readWriteSeparateFlag);
				InterceptorUtil.setSqlSessionFactoryBean(SpringContextHolder.applicationContext.getBean(SqlSessionFactoryBean.class));
				InterceptorUtil.dynamicAddInterceptor(DDL_DB_CLASS);
				InterceptorUtil.dynamicAddInterceptor(DDL_TABLE_CLASS);
				String ddlFile = StringUtils.isNotBlank(this.ddlFile) ? this.ddlFile : DEFAULT_DDL_FILE;
				URL url = Thread.currentThread().getContextClassLoader().getResource(ddlFile);

				if (url != null) {
					SAXReader saxReader = new SAXReader();
					Document document = saxReader.read(url);
					Element root = document.getRootElement();
					Element tables = root.element("tables");
					List<?> nodes = tables.elements("table");
					Iterator<?> iterator = nodes.iterator();

					if (DdlInterceptor.ddlConfigMap == null) {
						DdlInterceptor.ddlConfigMap = new HashMap<String, DdlConfig>();
					}

					if (DdlInterceptor.dataSourceDbMap == null || DdlInterceptor.dataSourceDbMap.isEmpty() || DdlInterceptor.dbDataSourceMap == null
							|| DdlInterceptor.dbDataSourceMap.isEmpty()) {
						buildDdlMap();
					}

					List<DdlTable> ddlTableList = null;

					while (iterator.hasNext()) {
						ddlTableList = new ArrayList<DdlTable>();
						Element e = (Element) iterator.next();
						String table = e.attributeValue("table").trim();
						String column = e.attributeValue("column") == null ? null : e.attributeValue("column").trim();
						Integer tableNum = e.attributeValue("tableNum") == null ? null : Integer.valueOf(e.attributeValue("tableNum").trim());
						String db = e.attributeValue("db") == null ? null : e.attributeValue("db").trim();
						Integer dbNum = e.attributeValue("dbNum") == null ? null : Integer.valueOf(e.attributeValue("dbNum").trim());
						DdlInterceptor.ddlConfigMap.put(table, new DdlConfig(table, column, tableNum, db, dbNum));

						if (tableNum != null && tableNum > 1) {
							for (int i = 1; i <= tableNum; i++) {
								if (dbNum == null || dbNum == 1) {
									ddlTableList.add(new DdlTable(table + "_" + i, 0));
								} else {
									if (dbNum > tableNum) {
										throw new SystemException("Ddl config error, tableNum should max than dbNum");
									}

									ddlTableList.add(new DdlTable(table + "_" + i, (i - 1) / (tableNum / dbNum) + 1));
								}
							}

							if (DdlInterceptor.consistenHashUtilMap == null) {
								DdlInterceptor.consistenHashUtilMap = new HashMap<String, ConsistenHashUtil<DdlTable>>();
							}

							DdlInterceptor.consistenHashUtilMap.put(table, new ConsistenHashUtil<DdlTable>(ddlTableList));
						}
					}

					DdlInterceptor.tablePattern = DdlInterceptor.getTablePattern(DdlInterceptor.ddlConfigMap.keySet());
				}
			} catch (Exception e) {
				throw new SystemException("InitDdlConfig error", e);
			}
		}
	}

	private void buildDdlMap() {
		DdlInterceptor.dataSourceDbMap = getDataSourceDbMap();
		DdlInterceptor.dbDataSourceMap = getDbDataSourceMap();
	}
}

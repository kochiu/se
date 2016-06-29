package com.kochiu.se.dataaccess.mysql.ddl.interceptor;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.kochiu.se.common.util.ConsistenHashUtil;
import com.kochiu.se.common.util.sql.SqlParserUtil;
import com.kochiu.se.dataaccess.mysql.ddl.DdlConfig;
import com.kochiu.se.dataaccess.mysql.ddl.DdlTable;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.kochiu.se.dataaccess.mysql.source.DataSourceSwitcher;

/**
 * 
 * @author zhihongp
 * 
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class DdlDbInterceptor extends DdlInterceptor implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		if (ddlFlag) {
			MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
			Object parameter = invocation.getArgs()[1];
			BoundSql boundSql = mappedStatement.getBoundSql(parameter);
			String interceptSql = boundSql.getSql();
			List<String> tableList = getTableList(interceptSql);
			boolean ddlFlag = getDdlFlag(interceptSql, tableList);
			String ddlDataSource = null;

			if (ddlFlag) {
				String standardSql = SqlParserUtil.handleSql(interceptSql, mappedStatement, boundSql);
				ddlDataSource = getDdlDataSource(standardSql, interceptSql, tableList);
			}

			if (StringUtils.isBlank(ddlDataSource)) {
				ddlDataSource = DataSourceSwitcher.getDataSourceTypeFromContext();
			}

			if (readWriteSeparateFlag) {
				if (SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType()) && DataSourceSwitcher.getReadWriteSeparateFromContext()) {
					ddlDataSource = getSlaveDataSourceByMasterDataSource(ddlDataSource);
				}
			}

			if (StringUtils.isNotBlank(ddlDataSource)) {
				String currentDataSourceFromContext = DataSourceSwitcher.getDataSourceTypeFromContext();
				String currentDataSource = currentDataSourceFromContext;

				if (StringUtils.isBlank(currentDataSource)) {
					currentDataSource = DataSourceSwitcher.getDataSourceType();
				}

				if (!ddlDataSource.equals(currentDataSource)) {
					DataSourceSwitcher.setDataSourceTypeInContext(ddlDataSource);
					Object obj = invocation.proceed();
					DataSourceSwitcher.setDataSourceTypeInContext(currentDataSourceFromContext);
					return obj;
				}
			}
		}

		Object obj = invocation.proceed();
		return obj;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	private String getDdlDataSource(String sql, String interceptSql, List<String> tableList) {
		// Pattern pattern = Pattern.compile(tablePattern,
		// Pattern.CASE_INSENSITIVE);
		// Matcher matcher = pattern.matcher(interceptSql);
		String ddlDb = null;
		String dataSource = null;
		Iterator<String> iterator = tableList.iterator();
		Set<String> ddlTableSet = ddlConfigMap.keySet();

		while (iterator.hasNext() && ddlDb == null) {
			String table = iterator.next();

			if (ddlTableSet.contains(table)) {
				DdlConfig ddlConfig = ddlConfigMap.get(table);

				if (ddlConfig == null) {
					continue;
				}

				String column = ddlConfig.getColumn();

				if (StringUtils.isBlank(column)) {
					ddlDb = ddlConfig.getDb();
				} else {
					String db = ddlConfig.getDb();
					Integer dbNum = ddlConfig.getDbNum();

					if (StringUtils.isBlank(db) && (dbNum == null || dbNum == 1)) {
						continue;
					} else if (dbNum == null || dbNum == 1) {
						ddlDb = db;
						continue;
					}

					ConsistenHashUtil<DdlTable> consistenHashUtil = consistenHashUtilMap.get(table);

					if (consistenHashUtil != null) {
						Object columnValue = null;

						try {
							columnValue = getColumnValue(sql, column);
						} catch (Exception e) {
							log.error("Get columnValue error", e);
						}

						if (columnValue != null) {
							DdlTable ddlTable = consistenHashUtil.get(columnValue);

							if (ddlTable != null) {
								Integer ddlDbNum = ddlTable.getDdlDbNum();

								if (ddlDbNum != null && ddlDbNum != 0) {
									if (StringUtils.isBlank(db)) {
										String currentDb = getCurrentDb();

										if (StringUtils.isNotBlank(currentDb)) {
											ddlDb = currentDb + "_" + ddlDbNum;
										}
									} else {
										ddlDb = db + "_" + ddlDbNum;
									}
								}
							}
						}
					}
				}
			}
		}
		// // 正则表达式方式考虑不完善，暂时不使用
		// while (matcher.find() && ddlDb == null) {
		// String table = matcher.group(1).trim();
		//
		// if (!tableList.contains(table)) {
		// continue;
		// }
		//
		// DdlConfig ddlConfig = ddlConfigMap.get(table);
		//
		// if (ddlConfig == null) {
		// continue;
		// }
		//
		// String column = ddlConfig.getColumn();
		//
		// if (StringUtils.isBlank(column)) {
		// ddlDb = ddlConfig.getDb();
		// } else {
		// String db = ddlConfig.getDb();
		// Integer dbNum = ddlConfig.getDbNum();
		//
		// if (StringUtils.isBlank(db) && (dbNum == null || dbNum == 1)) {
		// continue;
		// } else if (dbNum == null || dbNum == 1) {
		// ddlDb = db;
		// continue;
		// }
		//
		// ConsistenHashUtil<DdlTable> consistenHashUtil =
		// consistenHashUtilMap.get(table);
		//
		// if (consistenHashUtil != null) {
		// Object columnValue = null;
		//
		// try {
		// columnValue = getColumnValue(sql, column);
		// } catch (Exception e) {
		// log.error("Get columnValue error", e);
		// }
		//
		// if (columnValue != null) {
		// DdlTable ddlTable = consistenHashUtil.get(columnValue);
		//
		// if (ddlTable != null) {
		// Integer ddlDbNum = ddlTable.getDdlDbNum();
		//
		// if (ddlDbNum != null && ddlDbNum != 1) {
		// if (StringUtils.isBlank(db)) {
		// String currentDb = getCurrentDb();
		//
		// if (StringUtils.isNotBlank(currentDb)) {
		// ddlDb = currentDb + "_" + ddlDbNum;
		// }
		// } else {
		// ddlDb = db + "_" + ddlDbNum;
		// }
		// }
		// }
		// }
		// }
		// }
		// }

		if (StringUtils.isNotBlank(ddlDb)) {
			dataSource = getDataSourceByDb(ddlDb, false);
		}

		return dataSource;
	}

}
package com.sztx.se.dataaccess.mysql.source.interceptor;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sztx.se.common.domain.ContextConstants;
import com.sztx.se.common.util.date.DateUtil;
import com.sztx.se.common.util.sql.SqlParserUtil;

/**
 * 日志查询拦截器
 * 
 * @author zhihongp
 * 
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class SqlLogInterceptor implements Interceptor {

	private static final ThreadLocal<String> sqlLogContextHolder = new ThreadLocal<String>();

	private static final long DEFAULT_SLOW_LIMIT = 1000l;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static boolean openLog;

	private static long slowLimit;

	public static void setOpenLog(boolean openLog) {
		SqlLogInterceptor.openLog = openLog;
	}

	public static void setSlowLimit(long slowLimit) {
		SqlLogInterceptor.slowLimit = slowLimit;
	}

	public static String getExecuteSql() {
		String sql = (String) sqlLogContextHolder.get();
		return sql;
	}

	public static void setExecuteSql(String sql) {
		sqlLogContextHolder.set(sql);
	}

	public static void clearSqlLogContext() {
		sqlLogContextHolder.remove();
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (openLog) {
			long startTime = System.currentTimeMillis();
			long endTime = 0l;
			Object obj = null;

			try {
				obj = invocation.proceed();
			} catch (Throwable t) {
				obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
				throw t;
			} finally {
				endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
				long cost = endTime - startTime;
				long slowLimit = SqlLogInterceptor.slowLimit != 0l ? SqlLogInterceptor.slowLimit : DEFAULT_SLOW_LIMIT;
				boolean slowQuery = (cost > slowLimit);
				String result = "";

				if (obj != null) {
					result = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
				}

				MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
				String sql = getSql(invocation, mappedStatement);
				clearSqlLogContext();
				String sqlId = mappedStatement.getId();
				// 打印日志
				String sqlLog = getSqlLog(sqlId, sql, result, slowQuery, startTime, endTime, cost);

				if (sqlLog.length() > ContextConstants.LOG_MAX_LENGTH) {
					sqlLog = sqlLog.substring(0, ContextConstants.LOG_MAX_LENGTH);
				}

				log.info(sqlLog);
			}

			return obj;
		} else {
			Object obj = invocation.proceed();
			return obj;
		}
	}

	private String getSql(Invocation invocation, MappedStatement mappedStatement) {
		String sql = getExecuteSql();

		if (sql == null) {
			Object parameter = invocation.getArgs()[1];
			BoundSql boundSql = mappedStatement.getBoundSql(parameter);
			String interceptSql = boundSql.getSql();
			sql = SqlParserUtil.handleSql(interceptSql, mappedStatement, boundSql);
		}

		return sql;
	}

	private String getSqlLog(String method, String sql, String result, boolean slowQuery, long startTime, long endTime, long cost) {
		String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		String slowQueryLog = slowQuery ? "(slowQuery)" : "";

		return String.format("[Sql]" + slowQueryLog + " sql:%s|method:%s|result:%s|[start:%s, end:%s, cost:%dms]", sql, method, result, startTimeStr,
				endTimeStr, cost);
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

}

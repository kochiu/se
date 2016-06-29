//package com.kochiu.se.core.service;
//
//import java.io.IOException;
//import java.io.LineNumberReader;
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.LinkedList;
//import java.util.List;
//
//import javax.sql.DataSource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.EncodedResource;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.core.RedisCallback;
//import org.springframework.jdbc.datasource.DataSourceUtils;
//import org.springframework.jdbc.datasource.init.CannotReadScriptException;
//import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import MqMessageSenderSwitcher;
//import QuartzSwitcher;
//import HbaseSourceSwitcher;
//import MemcacheSourceSwitcher;
//import MongoSourceSwitcher;
//import DataSourceSwitcher;
//import DynamicDataSource;
//import DynamicRedisSource;
//import RedisSourceSwitcher;
//
//@Service("unitTestInitializeService")
//public class UnitTestInitializeService {
//	/**
//	 * 日志
//	 */
//	private final Logger log = LoggerFactory.getLogger(getClass());
//
//	private static final String DEFAULT_STATEMENT_SEPARATOR = ";";
//
//	private static final String DEFAULT_COMMENT_PREFIX = "--";
//
//	private static final String SQL_SCRIPT_ENCODING = "UTF-8";
//
//	private final String dbInitKey = "${jdbc.init.unit}";
//
//	private final String redisInitKey = "${jdbc.init.redis}";
//
//	private final String unitClearSqlKey = "${jdbc.init.sql}";
//
//	private final String unitDataSourceKey = "${jdbc.key.unit}";
//
//	private final String unitRedisSourceKey = "${redis.key.unit}";
//
//	private final String unitMemcacheSourceKey = "${memcache.key.unit}";
//
//	private final String unitMongoSourceKey = "${mongo.key.unit}";
//
//	private final String unitHbaseSourceKey = "${hbase.key.unit}";
//
//	private final String unitMqMessageSenderSourceKey = "${mq.producer.key.unit}";
//
//	private final String unitQuartzSourceKey = "${org.quartz.key.unit}";
//
//	@Autowired(required = false)
//	private DynamicRedisSource redisTemplate;
//
//	@Autowired(required = false)
//	private DynamicDataSource dynamicDataSource;
//
//	@Value(dbInitKey)
//	private String dbInit;
//
//	@Value(redisInitKey)
//	private String redisInit;
//
//	@Value(unitDataSourceKey)
//	private String unitDataSource;
//
//	@Value(unitClearSqlKey)
//	private String unitClearSql;
//
//	@Value(unitRedisSourceKey)
//	private String unitRedisSource;
//
//	@Value(unitMemcacheSourceKey)
//	private String unitMemcacheSource;
//
//	@Value(unitMongoSourceKey)
//	private String unitMongoSource;
//
//	@Value(unitHbaseSourceKey)
//	private String unitHbaseSource;
//
//	@Value(unitMqMessageSenderSourceKey)
//	private String unitMqMessageSenderSource;
//
//	@Value(unitQuartzSourceKey)
//	private String unitQuartzSource;
//
//	public void setUnitClearSql(String unitClearSql) {
//		this.unitClearSql = unitClearSql;
//	}
//
//	public void init() throws Exception {
//		switchSource();
//		clearDatabase();
//		clearRedis();
//	}
//
//	/**
//	 * 数据库初始化
//	 */
//	public void clearDatabase() {
//		if (dynamicDataSource != null) {
//			boolean dbInitFlag = false;
//
//			try {
//				dbInitFlag = Boolean.valueOf(dbInit);
//			} catch (Exception e) {
//			}
//
//			if (dbInitFlag) {
//				Resource resource = new ClassPathResource(unitClearSql);
//				excuteSQL(dynamicDataSource, applyEncodingIfNecessary(resource), false);
//			}
//		}
//
//	}
//
//	/**
//	 * 清空缓存并调用业务初始化类初始化数据到缓存中
//	 */
//	public void clearRedis() {
//		if (redisTemplate != null) {
//			boolean redisInitFlag = false;
//
//			try {
//				redisInitFlag = Boolean.valueOf(redisInit);
//			} catch (Exception e) {
//			}
//
//			if (redisInitFlag) {
//				redisTemplate.execute(new RedisCallback<Boolean>() {
//					@Override
//					public Boolean doInRedis(RedisConnection connection) {
//						connection.flushDb();
//						return true;
//					}
//				});
//			}
//		}
//	}
//
//	private void switchSource() {
//		if (unitDataSource != null && !unitDataSourceKey.equals(unitDataSource)) {
//			DataSourceSwitcher.setDataSourceTypeInContext(unitDataSource);
//		}
//
//		if (unitRedisSource != null && !unitRedisSourceKey.equals(unitRedisSource)) {
//			RedisSourceSwitcher.setRedisSourceTypeInContext(unitRedisSource);
//		}
//
//		if (unitMemcacheSource != null && !unitMemcacheSourceKey.equals(unitMemcacheSource)) {
//			MemcacheSourceSwitcher.setMemcacheSourceTypeInContext(unitMemcacheSource);
//		}
//
//		if (unitMongoSource != null && !unitMongoSourceKey.equals(unitMongoSource)) {
//			MongoSourceSwitcher.setMongoSourceTypeInContext(unitMongoSource);
//		}
//
//		if (unitHbaseSource != null && !unitHbaseSourceKey.equals(unitHbaseSource)) {
//			HbaseSourceSwitcher.setHbaseSourceTypeInContext(unitHbaseSource);
//		}
//
//		if (unitMqMessageSenderSource != null && !unitMqMessageSenderSourceKey.equals(unitMqMessageSenderSource)) {
//			MqMessageSenderSwitcher.setMqMessageSenderTypeInContext(unitMqMessageSenderSource);
//		}
//
//		if (unitQuartzSource != null && !unitQuartzSourceKey.equals(unitQuartzSource)) {
//			QuartzSwitcher.setQuartzTypeInContext(unitQuartzSource);
//		}
//	}
//
//	private EncodedResource applyEncodingIfNecessary(Resource script) {
//		if (script instanceof EncodedResource) {
//			return (EncodedResource) script;
//		} else {
//			return new EncodedResource(script, SQL_SCRIPT_ENCODING);
//		}
//	}
//
//	private void excuteSQL(DataSource dataSource, EncodedResource resource, boolean continueOnError) {
//		if (log.isInfoEnabled()) {
//			log.info("Executing SQL script from " + resource);
//		}
//
//		Connection connection = null;
//
//		try {
//			connection = DataSourceUtils.getConnection(dynamicDataSource);
//			long startTime = System.currentTimeMillis();
//			List<String> statements = new LinkedList<String>();
//			String script = null;
//
//			try {
//				script = readScript(resource);
//			} catch (IOException ex) {
//				throw new CannotReadScriptException(resource, ex);
//			}
//
//			splitSqlScript(script, DEFAULT_STATEMENT_SEPARATOR, DEFAULT_COMMENT_PREFIX, statements);
//			int lineNumber = 0;
//			Statement stmt = connection.createStatement();
//
//			try {
//				for (String statement : statements) {
//					lineNumber++;
//
//					try {
//						stmt.execute(statement);
//						int rowsAffected = stmt.getUpdateCount();
//
//						if (log.isDebugEnabled()) {
//							log.debug(rowsAffected + " returned as updateCount for SQL: " + statement);
//						}
//					} catch (SQLException ex) {
//						if (continueOnError) {
//							if (log.isDebugEnabled()) {
//								log.debug("Failed to execute SQL script statement at line " + lineNumber + " of resource " + resource + ": " + statement, ex);
//							}
//						} else {
//							throw new ScriptStatementFailedException(statement, lineNumber, resource, ex);
//						}
//					}
//				}
//			} finally {
//				try {
//					stmt.close();
//				} catch (Throwable e) {
//					log.debug("Could not close JDBC Statement", e);
//				}
//			}
//
//			long elapsedTime = System.currentTimeMillis() - startTime;
//
//			if (log.isInfoEnabled()) {
//				log.info("Done executing SQL script from " + resource + " in " + elapsedTime + " ms.");
//			}
//		} catch (Exception e) {
//			log.error("InitDatabase error", e);
//		} finally {
//			if (connection != null) {
//				DataSourceUtils.releaseConnection(connection, dynamicDataSource);
//			}
//		}
//	}
//
//	private String readScript(EncodedResource resource) throws IOException {
//		LineNumberReader lnr = new LineNumberReader(resource.getReader());
//
//		try {
//			String currentStatement = lnr.readLine();
//			StringBuilder scriptBuilder = new StringBuilder();
//
//			while (currentStatement != null) {
//				if (StringUtils.hasText(currentStatement) && (DEFAULT_COMMENT_PREFIX != null && !currentStatement.startsWith(DEFAULT_COMMENT_PREFIX))) {
//					if (scriptBuilder.length() > 0) {
//						scriptBuilder.append('\n');
//					}
//					scriptBuilder.append(currentStatement);
//				}
//				currentStatement = lnr.readLine();
//			}
//
//			maybeAddSeparatorToScript(scriptBuilder);
//			return scriptBuilder.toString();
//		} finally {
//			lnr.close();
//		}
//	}
//
//	private void maybeAddSeparatorToScript(StringBuilder scriptBuilder) {
//		String trimmed = DEFAULT_STATEMENT_SEPARATOR.trim();
//		if (trimmed.length() == DEFAULT_STATEMENT_SEPARATOR.length()) {
//			return;
//		}
//		
//		if (scriptBuilder.lastIndexOf(trimmed) == scriptBuilder.length() - trimmed.length()) {
//			scriptBuilder.append(DEFAULT_STATEMENT_SEPARATOR.substring(trimmed.length()));
//		}
//	}
//
//	private void splitSqlScript(String script, String delim, String commentPrefix, List<String> statements) {
//		StringBuilder sb = new StringBuilder();
//		boolean inLiteral = false;
//		boolean inEscape = false;
//		char[] content = script.toCharArray();
//
//		for (int i = 0; i < script.length(); i++) {
//			char c = content[i];
//			if (inEscape) {
//				inEscape = false;
//				sb.append(c);
//				continue;
//			}
//			// MySQL style escapes
//			if (c == '\\') {
//				inEscape = true;
//				sb.append(c);
//				continue;
//			}
//			if (c == '\'') {
//				inLiteral = !inLiteral;
//			}
//			if (!inLiteral) {
//				if (script.startsWith(delim, i)) {
//					// we've reached the end of the current statement
//					if (sb.length() > 0) {
//						statements.add(sb.toString());
//						sb = new StringBuilder();
//					}
//					i += delim.length() - 1;
//					continue;
//				} else if (script.startsWith(commentPrefix, i)) {
//					// skip over any content from the start of the comment to
//					// the EOL
//					int indexOfNextNewline = script.indexOf("\n", i);
//					if (indexOfNextNewline > i) {
//						i = indexOfNextNewline;
//						continue;
//					} else {
//						// if there's no newline after the comment, we must be
//						// at the end
//						// of the script, so stop here.
//						break;
//					}
//				} else if (c == ' ' || c == '\n' || c == '\t') {
//					// avoid multiple adjacent whitespace characters
//					if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
//						c = ' ';
//					} else {
//						continue;
//					}
//				}
//			}
//			sb.append(c);
//		}
//
//		if (StringUtils.hasText(sb)) {
//			statements.add(sb.toString());
//		}
//	}
//
//}

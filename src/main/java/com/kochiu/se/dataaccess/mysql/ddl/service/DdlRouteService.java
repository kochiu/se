package com.kochiu.se.dataaccess.mysql.ddl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.kochiu.se.common.util.ConsistenHashUtil;
import com.kochiu.se.core.domain.DdlRoute;
import com.kochiu.se.dataaccess.mysql.ddl.DdlConfig;
import com.kochiu.se.dataaccess.mysql.ddl.DdlTable;
import com.kochiu.se.dataaccess.mysql.source.DynamicDataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.kochiu.se.common.util.StringUtil;
import com.kochiu.se.dataaccess.mysql.ddl.interceptor.DdlInterceptor;

/**
 * 
 * @author zhihongp
 *
 */
@Service("ddlRouteService")
public class DdlRouteService {

	public List<DdlConfig> getDdlConfigList() {
		List<DdlConfig> ddlConfigList = new ArrayList<DdlConfig>();
		 
		for(Entry<String, DdlConfig> en : DdlInterceptor.ddlConfigMap.entrySet()){
			DdlConfig ddlConfig = en.getValue();
			ddlConfigList.add(ddlConfig);
		}

		return ddlConfigList;
	}

	public DdlRoute getDdlRoute(String table, String columnValue) {
		DdlConfig ddlConfig = DdlInterceptor.ddlConfigMap.get(table);
		
		if (ddlConfig == null) {
			return null;
		}
		
		String column = ddlConfig.getColumn();
		String ddlDbName = null;
		String ddlTableName = null;
		
		if (StringUtils.isBlank(column)) {
			ddlDbName = ddlConfig.getDb();
		} else {
			String db = ddlConfig.getDb();
			Integer dbNum = ddlConfig.getDbNum();
			
			if (StringUtils.isBlank(db) && (dbNum == null || dbNum == 1)) {
				ddlDbName = DdlInterceptor.getCurrentDb();
			} else if (dbNum == null || dbNum == 1) {
				ddlDbName = db;
			}
			
			ConsistenHashUtil<DdlTable> consistenHashUtil = DdlInterceptor.consistenHashUtilMap.get(table);

			if (consistenHashUtil != null) {
				if (columnValue != null) {
					DdlTable ddlTable = consistenHashUtil.get(columnValue);
					
					if (ddlTable != null) {
						Integer ddlDbNum = ddlTable.getDdlDbNum();

						if (ddlDbNum != null && ddlDbNum != 0) {
							if (StringUtils.isBlank(db)) {
								String currentDb = DdlInterceptor.getCurrentDb();
								
								if (StringUtils.isNotBlank(currentDb)) {
									ddlDbName = currentDb + "_" + ddlDbNum;
								}
							} else {
								ddlDbName = db + "_" + ddlDbNum;
							}
						}
					}
					
					ddlTableName = ddlTable.getDdlTableName();
				}
			}
		}
		
		if (StringUtil.isEmpty(ddlDbName)) {
			ddlDbName = DynamicDataSource.getCurrentDb();
		}
		
		return new DdlRoute(ddlDbName, ddlTableName, column, columnValue);
	}

}

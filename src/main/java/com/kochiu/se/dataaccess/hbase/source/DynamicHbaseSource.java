package com.kochiu.se.dataaccess.hbase.source;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.dataaccess.hbase.template.HbaseTemplateProxy;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.TableCallback;

/**
 * 动态hbase
 * 
 * @author zhihongp
 * 
 */
public class DynamicHbaseSource {

	private HbaseTemplateProxy defaultTargetHbaseSource;

	private Map<String, HbaseTemplateProxy> targetHbaseSources;

	public HbaseTemplateProxy getDefaultTargetHbaseSource() {
		return defaultTargetHbaseSource;
	}

	public void setDefaultTargetHbaseSource(HbaseTemplateProxy defaultTargetHbaseSource) {
		this.defaultTargetHbaseSource = defaultTargetHbaseSource;
	}

	public Map<String, HbaseTemplateProxy> getTargetHbaseSources() {
		return targetHbaseSources;
	}

	public void setTargetHbaseSources(Map<String, HbaseTemplateProxy> targetHbaseSources) {
		this.targetHbaseSources = targetHbaseSources;
	}

	public <T> T execute(String tableName, TableCallback<T> action) {
		HbaseTemplate hbaseTemplate = getHbaseTemplate();

		if (hbaseTemplate == null) {
			hbaseTemplate = defaultTargetHbaseSource.getHbaseTemplate();
		}

		if (hbaseTemplate == null) {
			throw new SystemException("Can not get a hbaseTemplate!");
		}

		return hbaseTemplate.execute(tableName, action);
	}

	public void afterPropertiesSet() {
		Set<Entry<String, HbaseTemplateProxy>> set = targetHbaseSources.entrySet();

		for (Entry<String, HbaseTemplateProxy> entry : set) {
			HbaseTemplateProxy hbaseTemplateProxy = entry.getValue();
			HbaseTemplate hbaseTemplate = hbaseTemplateProxy.getHbaseTemplate();
			hbaseTemplate.afterPropertiesSet();
		}
	}

	private HbaseTemplate getHbaseTemplate() {
		String hbaseSourceType = HbaseSourceSwitcher.getHbaseSourceType();
		HbaseTemplate hbaseTemplate = null;
		
		if (StringUtils.isNotBlank(hbaseSourceType)) {
			HbaseTemplateProxy hbaseTemplateProxy = targetHbaseSources.get(hbaseSourceType);

			if (hbaseTemplateProxy != null) {
				hbaseTemplate = hbaseTemplateProxy.getHbaseTemplate();
			} 
		} 
		
		if (hbaseTemplate == null) {
			hbaseTemplate = defaultTargetHbaseSource.getHbaseTemplate();
		}

		if (hbaseTemplate == null) {
			throw new SystemException("Can not get a hbaseTemplate!");
		}
		
		return hbaseTemplate;
	}

}

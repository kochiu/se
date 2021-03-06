package com.kochiu.se.core.tbschedule.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kochiu.se.core.context.SpringContextHolder;
import com.kochiu.se.core.tbschedule.config.ScheduleConfigServer;

/**
 * 
 * @author zhihongp
 *
 */
public class DynamicCreateScheduleManager {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private List<ScheduleConfigServer> scheduleConfigServerList;

	private DynamicSchedule dynamicSchedule;

	public void setScheduleConfigServerList(List<ScheduleConfigServer> scheduleConfigServerList) {
		this.scheduleConfigServerList = scheduleConfigServerList;
	}

	public void setDynamicSchedule(DynamicSchedule dynamicSchedule) {
		this.dynamicSchedule = dynamicSchedule;
	}

	public void initCreateSchedule() {
		registerSchedule();
	}

	private void registerSchedule() {
		Map<String, ScheduleConfigServer> targetScheduleConfigServerMap = new HashMap<String, ScheduleConfigServer>();
		List<ScheduleConfigServer> scheduleConfigServerList = new ArrayList<ScheduleConfigServer>();
		ScheduleConfigServer defaultScheduleConfigServer = null;
		
		if (this.scheduleConfigServerList == null || this.scheduleConfigServerList.isEmpty()) {
			Map<String, ScheduleConfigServer> scheduleConfigServerMap = SpringContextHolder.applicationContext.getBeansOfType(ScheduleConfigServer.class);

			if (scheduleConfigServerMap != null && !scheduleConfigServerMap.isEmpty()) {
				for (Entry<String, ScheduleConfigServer> en : scheduleConfigServerMap.entrySet()) {
					scheduleConfigServerList.add(en.getValue());
				}
			}
		} else {
			scheduleConfigServerList = this.scheduleConfigServerList;
		}

		for (ScheduleConfigServer scheduleConfigServer : scheduleConfigServerList) {
			String configServerKey = scheduleConfigServer.getConfigServerKey();
			targetScheduleConfigServerMap.put(configServerKey, scheduleConfigServer);
			boolean isDefault = scheduleConfigServer.getIsDefault();

			if (isDefault) {
				defaultScheduleConfigServer = scheduleConfigServer;
			}
		}

		dynamicSchedule.setTargetScheduleConfigServerMap(targetScheduleConfigServerMap);
		dynamicSchedule.setDefaultTargetScheduleSource(defaultScheduleConfigServer);
		dynamicSchedule.initTbScheduleLog();
		
		try {
			dynamicSchedule.afterPropertiesSet();
		} catch (Exception e) {
			log.error("Initialize tbschedule error", e);
		}
	}

}

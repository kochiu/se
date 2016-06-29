package com.sztx.se.core.tbschedule.component;

import com.sztx.se.core.tbschedule.config.ScheduleParameter;
import com.sztx.se.core.tbschedule.source.DynamicSchedule;

/**
 * 
 * @author zhihongp
 * 
 */
public class ScheduleManager {

	private DynamicSchedule dynamicSchedule;

	public void setDynamicSchedule(DynamicSchedule dynamicSchedule) {
		this.dynamicSchedule = dynamicSchedule;
	}

	/**
	 * 创建一个task，如果已存在该task，则更新该task
	 * 
	 * @param config
	 * @param configServerKey
	 */
	public void saveOrUpdateTask(ScheduleParameter config) {
		dynamicSchedule.saveOrUpdateTask(config, null);
	}

}

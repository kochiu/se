package com.kochiu.se.core.tbschedule.component;

import java.util.List;

import com.kochiu.se.core.tbschedule.config.ScheduleParameter;
import com.kochiu.se.core.tbschedule.source.DynamicSchedule;

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
	 * @param scheduleName
	 * @param taskName
	 * @param taskBeanName
	 * @param strategyName
	 * @param extraInfo
	 */
	public void saveOrUpdateTask(String scheduleName, String taskName, String taskBeanName, String strategyName, String extraInfo) {
		saveOrUpdateTask(scheduleName, taskName, taskBeanName, strategyName, 100, 100, null, ScheduleParameter.SLEEP_MODEL, extraInfo);
	}

	/**
	 * 创建一个task，如果已存在该task，则更新该task
	 * 
	 * @param scheduleName
	 * @param taskName
	 * @param taskBeanName
	 * @param strategyName
	 * @param fetchDataNumber
	 * @param taskItemList
	 * @param processType
	 * @param extraInfo
	 */
	public void saveOrUpdateTask(String scheduleName, String taskName, String taskBeanName, String strategyName, int fetchDataNumber, int executeNumber,
			List<String> taskItemList, int processType, String extraInfo) {
		ScheduleParameter config = creatScheduleParameter(taskName, taskBeanName, strategyName, fetchDataNumber, executeNumber, taskItemList, processType,
				extraInfo);
		dynamicSchedule.saveOrUpdateTask(scheduleName, config, null);
	}

	private ScheduleParameter creatScheduleParameter(String taskName, String taskBeanName, String strategyName, int fetchDataNumber, int executeNumber,
			List<String> taskItemList, int processType, String extraInfo) {
		ScheduleParameter scheduleParameter = new ScheduleParameter();
		scheduleParameter.setTaskName(taskName);
		scheduleParameter.setTaskBeanName(taskBeanName);
		scheduleParameter.setStrategyName(strategyName);
		scheduleParameter.setFetchDataNumber(fetchDataNumber);
		scheduleParameter.setExecuteNumber(executeNumber);
		scheduleParameter.setTaskItemList(taskItemList);
		scheduleParameter.setProcessType(processType);
		scheduleParameter.setExtraInfo(extraInfo);
		return scheduleParameter;
	}
}

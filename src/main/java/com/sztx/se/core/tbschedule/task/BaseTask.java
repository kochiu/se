package com.sztx.se.core.tbschedule.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.pamirs.schedule.IScheduleTaskDeal;
import com.taobao.pamirs.schedule.TaskItemDefine;

public abstract class BaseTask<T> implements IScheduleTaskDeal<T> {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	public String getTaskItems(List<TaskItemDefine> taskItemList){
		StringBuilder taskItems = new StringBuilder("(");
		for(int i = 0;i < taskItemList.size(); i++ ){
			TaskItemDefine taskItem = taskItemList.get(i);
			if(i != 0){
				taskItems.append(",");
			}
			taskItems.append(taskItem.getTaskItemId());
		}
		taskItems.append(")");
		return taskItems.toString();
	}
}

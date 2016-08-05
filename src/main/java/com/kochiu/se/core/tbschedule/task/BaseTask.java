package com.kochiu.se.core.tbschedule.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.util.date.DateUtil;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TaskItemDefine;

public abstract class BaseTask<T> implements IScheduleTaskDealMulti<T> {

	protected static final Logger log = LoggerFactory.getLogger(BaseTask.class);

	private static boolean openLog;

	private static int logLength;

	public static void setOpenLog(boolean openLog) {
		BaseTask.openLog = openLog;
	}

	public static void setLogLength(int logLength) {
		BaseTask.logLength = logLength;
	}

	/**
	 * 执行任务
	 * 
	 * @param tasks
	 * @param ownSign
	 * @return
	 * @throws Exception
	 */
	public abstract Object executeTask(T[] tasks, String ownSign) throws Exception;

	@Override
	public boolean execute(T[] tasks, String ownSign) throws Exception {
		boolean flag = true;

		if (openLog) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			Object obj = null;

			try {
				obj = executeTask(tasks, ownSign);
			} catch (Throwable t) {
				flag = false;
				obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
				throw t;
			} finally {
				String taskResult = "";

				if (obj != null) {
					taskResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
				}

				endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
				// 打印日志
				String tbScheduleLog = getTbScheduleLog(tasks, ownSign, taskResult, startTime, endTime);
				int logLength = BaseTask.logLength != 0 ? BaseTask.logLength : ContextConstants.LOG_MAX_LENGTH;

				if (logLength != -1 && tbScheduleLog.length() > logLength) {
					tbScheduleLog = tbScheduleLog.substring(0, logLength);
				}

				log.info(tbScheduleLog);
			}
		} else {
			try {
				executeTask(tasks, ownSign);
			} catch (Throwable t) {
				flag = false;
				throw t;
			}
		}

		return flag;
	}

	public List<String> getTaskItems(List<TaskItemDefine> taskItemList) {
		List<String> taskItems = new ArrayList<>();

		for (int i = 0; i < taskItemList.size(); i++) {
			TaskItemDefine taskItem = taskItemList.get(i);
			taskItems.add(taskItem.getTaskItemId());
		}

		return taskItems;
	}

	private String getTbScheduleLog(T[] tasks, String ownSign, String taskResult, long startTime, long endTime) {
		String tasksStr = "";
		int length = tasks.length;

		if (length > 0) {
			T task = tasks[0];
			tasksStr = JSON.toJSONStringWithDateFormat(task, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
		}

		long cost = endTime - startTime;
		String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		return String.format("Excute tbschedule task, number:%s|task:%s|ownSign:%s|result:%s|[start:%s, end:%s, cost:%dms]", length, tasksStr, ownSign,
				taskResult, startTimeStr, endTimeStr, cost);
	}
}

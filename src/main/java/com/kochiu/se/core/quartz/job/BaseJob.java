package com.kochiu.se.core.quartz.job;

import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.core.quartz.config.QuartzParameter;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.util.date.DateUtil;
import com.kochiu.se.core.quartz.component.QuartzManager;

/**
 * 自动任务调度指定的任务需要实现此接口
 * 
 * @author zhihongp
 * 
 */
public abstract class BaseJob implements Job {

	protected static final Logger log = LoggerFactory.getLogger(BaseJob.class);

	private static boolean openLog;

	private static int logLength;

	public static void setOpenLog(boolean openLog) {
		BaseJob.openLog = openLog;
	}

	public static void setLogLength(int logLength) {
		BaseJob.logLength = logLength;
	}

	/**
	 * job具体的业务逻辑
	 * 
	 * @param context
	 * @throws JobExecutionException
	 */
	public abstract Object executeJob(JobExecutionContext context) throws JobExecutionException;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (openLog) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			QuartzParameter quartzParameter = null;
			Object obj = null;

			try {
				quartzParameter = getQuartzParameter(context.getScheduler(), context.getJobDetail(), context.getTrigger());
				obj = executeJob(context);
			} catch (Throwable t) {
				obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
				throw t;
			} finally {
				String jobResult = "";

				if (obj != null) {
					jobResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
				}

				endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
				// 打印日志
				String quartzLog = getQuartzLog(quartzParameter, jobResult, startTime, endTime);
				int logLength = BaseJob.logLength != 0 ? BaseJob.logLength : ContextConstants.LOG_MAX_LENGTH;

				if (logLength != -1 && quartzLog.length() > logLength) {
					quartzLog = quartzLog.substring(0, logLength);
				}

				log.info(quartzLog);
			}
		} else {
			executeJob(context);
		}
	}

	private String getQuartzLog(QuartzParameter quartzParameter, String jobResult, long startTime, long endTime) {
		String quartzParameterStr = "";

		if (quartzParameter != null) {
			quartzParameterStr = JSON.toJSONStringWithDateFormat(quartzParameter, DateUtil.MAX_LONG_DATE_FORMAT_STR,
					SerializerFeature.DisableCircularReferenceDetect);
		}

		long cost = endTime - startTime;
		String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		return String.format("Excute quartz job, parameter:%s|result:%s|[start:%s, end:%s, cost:%dms]", quartzParameterStr, jobResult, startTimeStr,
				endTimeStr, cost);
	}

	private QuartzParameter getQuartzParameter(Scheduler scheduler, JobDetail jobDetail, Trigger trigger) {
		QuartzParameter quartzParameter = null;

		try {
			quartzParameter = QuartzManager.getQuartzParameter(scheduler, jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new SystemException("GetQuartzParameter error", e);
		}

		return quartzParameter;
	}

}

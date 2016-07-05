package com.kochiu.se.core.quartz.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.kochiu.se.core.quartz.config.QuartzParameter;
import org.quartz.CronExpression;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;

import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.core.quartz.source.DynamicQuartz;

/**
 * 
 * @author zhihongp
 * 
 */
public class QuartzManager {

	private DynamicQuartz dynamicQuartz;

	public void setDynamicQuartz(DynamicQuartz dynamicQuartz) {
		this.dynamicQuartz = dynamicQuartz;
	}

	/**
	 * 创建一个job，如果已存在该job，则创建失败
	 * 
	 * @param schedName
	 * @param jobName
	 * @param jobGroup
	 * @param jobClassName
	 * @param description
	 * @param isRecovery
	 * @param triggerName
	 * @param triggerGroup
	 * @param isCronTrigger
	 * @param expression
	 * @param startAt
	 * @param endAt
	 * @param extraInfo
	 * @return
	 */
	public boolean saveJob(String schedName, String jobName, String jobGroup, String jobClassName, String description, boolean isRecovery, String triggerName,
			String triggerGroup, boolean isCronTrigger, String expression, Date startAt, Date endAt, Map<String, String> extraInfo) {
		QuartzParameter config = creatQuartzParameter(schedName, jobName, jobGroup, jobClassName, description, isRecovery, triggerName, triggerGroup,
				isCronTrigger, expression, startAt, endAt, extraInfo);
		dynamicQuartz.saveOrUpdateJob(config, null, true);
		return true;
	}

	/**
	 * 更新一个job，如果该job不存在，则更新失败
	 * 
	 * @param schedName
	 * @param jobName
	 * @param jobGroup
	 * @param jobClassName
	 * @param description
	 * @param isRecovery
	 * @param triggerName
	 * @param triggerGroup
	 * @param isCronTrigger
	 * @param expression
	 * @param startAt
	 * @param endAt
	 * @param extraInfo
	 * @return
	 */
	public boolean updateJob(String schedName, String jobName, String jobGroup, String jobClassName, String description, boolean isRecovery,
			String triggerName, String triggerGroup, boolean isCronTrigger, String expression, Date startAt, Date endAt, Map<String, String> extraInfo) {
		QuartzParameter config = creatQuartzParameter(schedName, jobName, jobGroup, jobClassName, description, isRecovery, triggerName, triggerGroup,
				isCronTrigger, expression, startAt, endAt, extraInfo);
		dynamicQuartz.updateJob(config, null);
		return true;
	}

	/**
	 * 创建一个job，如果已存在该job，则更新该job
	 * 
	 * @param schedName
	 * @param jobName
	 * @param jobGroup
	 * @param jobClassName
	 * @param description
	 * @param isRecovery
	 * @param triggerName
	 * @param triggerGroup
	 * @param isCronTrigger
	 * @param expression
	 * @param startAt
	 * @param endAt
	 * @param extraInfo
	 * @return
	 */
	public boolean saveOrUpdateJob(String schedName, String jobName, String jobGroup, String jobClassName, String description, boolean isRecovery,
			String triggerName, String triggerGroup, boolean isCronTrigger, String expression, Date startAt, Date endAt, Map<String, String> extraInfo) {
		QuartzParameter config = creatQuartzParameter(schedName, jobName, jobGroup, jobClassName, description, isRecovery, triggerName, triggerGroup,
				isCronTrigger, expression, startAt, endAt, extraInfo);
		dynamicQuartz.saveOrUpdateJob(config, null, false);
		return true;
	}

	/**
	 * 暂停一个job
	 * 
	 * @throws SchedulerException
	 */
	public boolean pauseTrigger(String schedName, String triggerName, String triggerGroup) {
		QuartzParameter config = new QuartzParameter();
		config.setSchedName(schedName);
		config.setTriggerName(triggerName);
		config.setTriggerGroup(triggerGroup);
		dynamicQuartz.pauseTrigger(config, null);
		return true;
	}

	/**
	 * 恢复job
	 * 
	 * @throws SchedulerException
	 */
	public boolean resumeTrigger(String schedName, String triggerName, String triggerGroup) {
		QuartzParameter config = new QuartzParameter();
		config.setSchedName(schedName);
		config.setTriggerName(triggerName);
		config.setTriggerGroup(triggerGroup);
		dynamicQuartz.resumeTrigger(config, null);
		return true;
	}

	/**
	 * 删除该config指定的job的trigger
	 * 一个job可能有多个trigger,如果本次删除的trigger是该job唯一的触发器，则该job也会被删除
	 * 
	 */
	public boolean deleteTrigger(String schedName, String triggerName, String triggerGroup) {
		QuartzParameter config = new QuartzParameter();
		config.setSchedName(schedName);
		config.setTriggerName(triggerName);
		config.setTriggerGroup(triggerGroup);
		dynamicQuartz.deleteTrigger(config, null);
		return true;
	}

	public List<QuartzParameter> getAllJobs() {
		List<QuartzParameter> list = null;

		try {
			List<Scheduler> schedulerList = getAllSchedulers();

			if (schedulerList != null) {
				list = new ArrayList<QuartzParameter>();

				for (Scheduler scheduler : schedulerList) {
					Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());

					if (keys == null) {
						return list;
					}

					for (JobKey key : keys) {
						List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);

						if (triggers != null) {
							JobDetail jobDetail = scheduler.getJobDetail(key);

							for (Trigger trigger : triggers) {
								list.add(getQuartzParameter(scheduler, jobDetail, trigger));
							}
						}
					}
				}
			}
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}

		return list;
	}

	public List<QuartzParameter> getSchedulerJobs(String schedName) {
		List<QuartzParameter> list = new ArrayList<QuartzParameter>();

		try {
			Scheduler scheduler = getScheduler(schedName);
			Set<JobKey> keys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());

			if (keys == null) {
				return list;
			}

			for (JobKey key : keys) {
				List<? extends Trigger> triggers = scheduler.getTriggersOfJob(key);

				if (triggers != null) {
					JobDetail jobDetail = scheduler.getJobDetail(key);

					for (Trigger trigger : triggers) {
						list.add(getQuartzParameter(scheduler, jobDetail, trigger));
					}
				}
			}
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}

		return list;
	}

	public QuartzParameter getJob(String schedName, String jobName, String jobGroup, String triggerName, String triggerGroup) {
		Scheduler scheduler = getScheduler(schedName);
		JobKey jobKey = new JobKey(jobName, jobGroup);
		TriggerKey triggerKey = new TriggerKey(triggerName, triggerGroup);

		try {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			Trigger trigger = scheduler.getTrigger(triggerKey);

			if (jobDetail == null || trigger == null) {
				return null;
			}

			return getQuartzParameter(scheduler, jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}
	}

	public static QuartzParameter getQuartzParameter(Scheduler scheduler, JobDetail jobDetail, Trigger trigger) throws SchedulerException {
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		String jobClassName = jobDataMap.getString(DynamicQuartz.JOB_CLASS_NAME);

		if (jobClassName == null) {
			throw new SystemException("Quartz jobClassName is null");
		}

		Map<String, String> extraInfo = null;
		Set<Entry<String, Object>> set = jobDataMap.entrySet();

		if (!set.isEmpty()) {
			extraInfo = new HashMap<String, String>();

			for (Entry<String, Object> entry : set) {
				String key = entry.getKey();
				Object value = entry.getValue();

				if (!constantsMatch(key)) {
					String valueStr = (String) value;
					extraInfo.put(key, valueStr);
				}
			}
		}

		QuartzParameter quartzParameter = new QuartzParameter();
		quartzParameter.setSchedName(scheduler.getSchedulerName());
		quartzParameter.setJobName(jobDetail.getKey().getName());
		quartzParameter.setJobGroup(jobDetail.getKey().getGroup());
		quartzParameter.setJobClassName(jobClassName);
		quartzParameter.setDescription(jobDetail.getDescription());
		quartzParameter.setIsRecovery(jobDetail.requestsRecovery());
		quartzParameter.setTriggerName(trigger.getKey().getName());
		quartzParameter.setTriggerGroup(trigger.getKey().getGroup());
		quartzParameter.setIsCronTrigger(trigger.getJobDataMap().getBoolean(DynamicQuartz.IS_CRON_TRIGGER));
		quartzParameter.setExpression(trigger.getJobDataMap().getString(DynamicQuartz.TRIGGER_EXPRESSION));
		quartzParameter.setPrevFireTime(trigger.getPreviousFireTime());
		quartzParameter.setNextFireTime(trigger.getNextFireTime());
		quartzParameter.setStartAt(trigger.getStartTime());
		quartzParameter.setEndAt(trigger.getEndTime());
		quartzParameter.setStatus(scheduler.getTriggerState(trigger.getKey()).toString());
		quartzParameter.setExtraInfo(extraInfo);
		return quartzParameter;
	}

	private Scheduler getScheduler(String schedName) {
		return dynamicQuartz.getScheduler(null, schedName);
	}

	private List<Scheduler> getAllSchedulers() {
		return dynamicQuartz.getAllSchedulers(null);
	}

	private QuartzParameter creatQuartzParameter(String schedName, String jobName, String jobGroup, String jobClassName, String description,
			boolean isRecovery, String triggerName, String triggerGroup, boolean isCronTrigger, String expression, Date startAt, Date endAt,
			Map<String, String> extraInfo) {
		if (isCronTrigger) {
			try {
				new CronExpression(expression);
			} catch (Exception e) {
				throw new SystemException("Save a quartz job error, expression is not a cron", e);
			}
		} else {
			try {
				Integer.valueOf(expression);
			} catch (Exception e) {
				throw new SystemException("Save a quartz job error, expression is not a number", e);
			}
		}

		QuartzParameter quartzParameter = new QuartzParameter();
		quartzParameter.setSchedName(schedName);
		quartzParameter.setJobName(jobName);
		quartzParameter.setJobGroup(jobGroup);
		quartzParameter.setJobClassName(jobClassName);
		quartzParameter.setDescription(description);
		quartzParameter.setIsRecovery(isRecovery);
		quartzParameter.setTriggerName(triggerName);
		quartzParameter.setTriggerGroup(triggerGroup);
		quartzParameter.setIsCronTrigger(isCronTrigger);
		quartzParameter.setExpression(expression);
		quartzParameter.setStartAt(startAt);
		quartzParameter.setEndAt(endAt);
		quartzParameter.setExtraInfo(extraInfo);
		return quartzParameter;
	}

	private static boolean constantsMatch(String obj) {
		if (DynamicQuartz.IS_CRON_TRIGGER.equals(obj) || DynamicQuartz.JOB_CLASS_NAME.equals(obj) || DynamicQuartz.SCHEDULER_DATASOURCE_KEY.equals(obj)
				|| DynamicQuartz.TRIGGER_EXPRESSION.equals(obj)) {
			return true;
		} else {
			return false;
		}
	}
}

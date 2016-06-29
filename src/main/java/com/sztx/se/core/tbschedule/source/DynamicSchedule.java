package com.sztx.se.core.tbschedule.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sztx.se.common.exception.SystemException;
import com.sztx.se.core.context.SpringContextHolder;
import com.sztx.se.core.tbschedule.config.ScheduleConfigServer;
import com.sztx.se.core.tbschedule.config.ScheduleParameter;
import com.sztx.se.core.tbschedule.task.InitialTask;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;

public class DynamicSchedule {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Map<String, ScheduleConfigServer> targetScheduleConfigServerMap;

	private List<InitialTask> initialTaskList;

	private ScheduleConfigServer defaultTargetScheduleSource;

	public void setTargetScheduleConfigServerMap(Map<String, ScheduleConfigServer> targetScheduleConfigServerMap) {
		this.targetScheduleConfigServerMap = targetScheduleConfigServerMap;
	}

	public void setInitialTaskList(List<InitialTask> initialTaskList) {
		this.initialTaskList = initialTaskList;
	}

	public void setDefaultTargetScheduleSource(ScheduleConfigServer defaultTargetScheduleSource) {
		this.defaultTargetScheduleSource = defaultTargetScheduleSource;
	}

	public void afterPropertiesSet() throws Exception {
		Set<Entry<String, ScheduleConfigServer>> set = targetScheduleConfigServerMap.entrySet();

		for (Map.Entry<String, ScheduleConfigServer> entry : set) {
			ScheduleConfigServer scheduleConfigServer = entry.getValue();

			if (scheduleConfigServer != null) {
				TBScheduleManagerFactory scheduleManagerFactory = new TBScheduleManagerFactory();
				scheduleManagerFactory.setApplicationContext(SpringContextHolder.applicationContext);
				Map<String, String> zkConfig = new HashMap<String, String>();
				zkConfig.put("zkConnectString", scheduleConfigServer.getServerAddress());
				zkConfig.put("rootPath", "/tbschedule/" + scheduleConfigServer.getApplicationName());
				zkConfig.put("userName", scheduleConfigServer.getUsername());
				zkConfig.put("password", scheduleConfigServer.getPassword());
				zkConfig.put("zkSessionTimeout", String.valueOf(scheduleConfigServer.getTimeout()));
				zkConfig.put("isCheckParentPath", String.valueOf(scheduleConfigServer.getIsCheckParentPath()));
				scheduleManagerFactory.setZkConfig(zkConfig);
				scheduleConfigServer.setScheduleManagerFactory(scheduleManagerFactory);

				if (scheduleConfigServer.getAutoStartup()) {
					startSchedule(scheduleConfigServer);
				}
			}
		}

		for (InitialTask initialTask : initialTaskList) {
			ScheduleConfigServer scheduleConfigServer = initialTask.getScheduleConfigServer();

			if (scheduleConfigServer == null) {
				scheduleConfigServer = defaultTargetScheduleSource;
			}

			if (scheduleConfigServer.getAutoStartup()) {
				String configServerKey = scheduleConfigServer.getConfigServerKey();
				ScheduleParameter config = initialTask.getScheduleParameter();
				saveOrUpdateTask(config, configServerKey);
			}
		}
	}

	/**
	 * 
	 * @param configServerKey
	 * @return
	 */
	public TBScheduleManagerFactory getScheduleManagerFactory(String configServerKey) {
		if (configServerKey == null) {
			configServerKey = ScheduleSwitcher.getScheduleType();
		}

		TBScheduleManagerFactory scheduleManagerFactory = null;

		try {
			if (StringUtils.isNotBlank(configServerKey)) {
				ScheduleConfigServer scheduleConfigServer = targetScheduleConfigServerMap.get(configServerKey);

				if (scheduleConfigServer != null) {
					scheduleManagerFactory = scheduleConfigServer.getScheduleManagerFactory();
				}
			}
		} catch (Exception e) {
			log.error("GetScheduleManagerFactory error", e);
		}

		if (scheduleManagerFactory == null) {
			scheduleManagerFactory = defaultTargetScheduleSource.getScheduleManagerFactory();
		}

		if (scheduleManagerFactory == null) {
			throw new SystemException("Can not get a scheduleManagerFactory!");
		}

		return scheduleManagerFactory;
	}

	/**
	 * 
	 * @param config
	 * @param configServerKey
	 */
	public void saveOrUpdateTask(ScheduleParameter config, String configServerKey) {
		try {
			TBScheduleManagerFactory scheduleManagerFactory = getScheduleManagerFactory(configServerKey);
			// 创建任务的task信息
			ScheduleTaskType baseTaskType = new ScheduleTaskType();
			String baseTaskTypeName = config.getTaskName();
			baseTaskType.setBaseTaskType(baseTaskTypeName);
			baseTaskType.setDealBeanName(config.getTaskBeanName());
			baseTaskType.setExecuteNumber(config.getExecuteNumber());
			baseTaskType.setExpireOwnSignInterval(config.getExpireOwnSignInterval());
			baseTaskType.setFetchDataNumber(config.getFetchDataNumber());
			baseTaskType.setHeartBeatRate(config.getHeartBeatRate());
			baseTaskType.setJudgeDeadInterval(config.getJudgeDeadInterval());
			baseTaskType.setMaxTaskItemsOfOneThreadGroup(config.getMaxTaskItemsOfOneThreadGroup());
			baseTaskType.setPermitRunEndTime(config.getEndTime());
			baseTaskType.setPermitRunStartTime(config.getStartTime());
			baseTaskType.setSleepTimeInterval(config.getSleepTimeInterval());
			baseTaskType.setSleepTimeNoData(config.getSleepTimeNoData());
			baseTaskType.setSts(config.getStatus());
			baseTaskType.setTaskParameter(config.getExtraInfo());
			baseTaskType.setThreadNumber(config.getThreadNumber());

			if (config.getProcessType() == 0) {
				baseTaskType.setProcessorType("SLEEP");
			} else {
				baseTaskType.setProcessorType("NOTSLEEP");
			}

			String[] taskItems = config.getTaskItemList().toArray(new String[0]);
			baseTaskType.setTaskItems(taskItems);

			// 创建任务的strategy信息
			String strategyName = config.getStrategyName();
			ScheduleStrategy strategy = new ScheduleStrategy();
			strategy.setStrategyName(strategyName);

			if (config.getStrategyType() == 1) {
				strategy.setKind(ScheduleStrategy.Kind.Java);
			} else if (config.getStrategyType() == 2) {
				strategy.setKind(ScheduleStrategy.Kind.Bean);
			} else {
				strategy.setKind(ScheduleStrategy.Kind.Schedule);
			}

			strategy.setTaskName(baseTaskTypeName);
			strategy.setTaskParameter(config.getExtraInfo());
			strategy.setNumOfSingleServer(config.getNumOfSingleServer());
			strategy.setAssignNum(config.getAssignNum());
			strategy.setSts(config.getStatus());
			String[] ips = config.getIpList().toArray(new String[0]);
			strategy.setIPList(ips);

			scheduleManagerFactory.getScheduleDataManager().updateBaseTaskType(baseTaskType);
			scheduleManagerFactory.getScheduleStrategyManager().updateScheduleStrategy(strategy);
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}

	//
	// /**
	// * 暂停一个job
	// *
	// * @throws SchedulerException
	// */
	// public void pauseTrigger(String triggerName, String triggerGroup, String
	// configServerKey) {
	// Scheduler scheduler = getScheduler(configServerKey);
	//
	// try {
	// scheduler.pauseTrigger(new TriggerKey(triggerName, triggerGroup));
	// } catch (SchedulerException e) {
	// throw new SystemException(e);
	// }
	// }
	//
	// /**
	// * 恢复job
	// *
	// * @throws SchedulerException
	// */
	// public void resumeTrigger(String triggerName, String triggerGroup, String
	// configServerKey) {
	// Scheduler scheduler = getScheduler(configServerKey);
	//
	// try {
	// scheduler.resumeTrigger(new TriggerKey(triggerName, triggerGroup));
	// } catch (SchedulerException e) {
	// throw new SystemException(e);
	// }
	// }
	//
	// /**
	// * 删除该config指定的job的trigger
	// * 一个job可能有多个trigger,如果本次删除的trigger是该job唯一的触发器，则该job也会被删除
	// *
	// */
	// public void deleteTrigger(String triggerName, String triggerGroup, String
	// configServerKey) {
	// Scheduler scheduler = getScheduler(configServerKey);
	//
	// try {
	// scheduler.unscheduleJob(new TriggerKey(triggerName, triggerGroup));
	// } catch (SchedulerException e) {
	// throw new SystemException(e);
	// }
	// }
	//

	/**
	 * 
	 * @param scheduleConfigServer
	 * @throws Exception
	 */
	private void startSchedule(ScheduleConfigServer scheduleConfigServer) throws Exception {
		TBScheduleManagerFactory scheduleManagerFactory = scheduleConfigServer.getScheduleManagerFactory();
		scheduleManagerFactory.init();
		
		while (scheduleManagerFactory.isZookeeperInitialSucess() == false) {
			log.error("ZookeeperInitial fail ...");
			Thread.sleep(1000);
		}

		Thread.sleep(1000);
		log.info("TBSchedule [" + scheduleConfigServer.getConfigServerKey() + "] 自动启动");
	}

}

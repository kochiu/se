package com.kochiu.se.core.tbschedule.source;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.core.tbschedule.config.ScheduleParameter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kochiu.se.core.context.SpringContextHolder;
import com.kochiu.se.core.tbschedule.config.ScheduleConfigServer;
import com.kochiu.se.core.tbschedule.task.BaseTask;
import com.taobao.pamirs.schedule.strategy.ScheduleStrategy;
import com.taobao.pamirs.schedule.strategy.TBScheduleManagerFactory;
import com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType;

public class DynamicSchedule {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final static String SCHEDULE_ROOT = "/tbschedule";

	private final static String SCHEDULE_MANAGER_NAME = SCHEDULE_ROOT + "/" + "workerMonitorScheduler";

	private final static String SCHEDULE_SUFFIX = "Scheduler";

	private Map<String, ScheduleConfigServer> targetScheduleConfigServerMap;

	private ScheduleConfigServer defaultTargetScheduleSource;

	private Map<String, TBScheduleManagerFactory> initedScheduleManagerFactoryMap = new HashMap<String, TBScheduleManagerFactory>();

	private String hostRootPath;

	/**
	 * 日志开关，默认为false不打开
	 */
	private boolean openLog;

	/**
	 * 日志最大长度，如果不传则默认1000，传-1则不限制日志打印长度
	 */
	private int logLength;

	public void setTargetScheduleConfigServerMap(Map<String, ScheduleConfigServer> targetScheduleConfigServerMap) {
		this.targetScheduleConfigServerMap = targetScheduleConfigServerMap;
	}

	public void setDefaultTargetScheduleSource(ScheduleConfigServer defaultTargetScheduleSource) {
		this.defaultTargetScheduleSource = defaultTargetScheduleSource;
	}

	public void setOpenLog(boolean openLog) {
		this.openLog = openLog;
	}

	public void setLogLength(int logLength) {
		this.logLength = logLength;
	}

	public void afterPropertiesSet() throws Exception {
		Set<Entry<String, ScheduleConfigServer>> set = targetScheduleConfigServerMap.entrySet();

		for (Entry<String, ScheduleConfigServer> entry : set) {
			String configServerKey = entry.getKey();
			ScheduleConfigServer scheduleConfigServer = entry.getValue();

			if (scheduleConfigServer != null) {
				TBScheduleManagerFactory scheduleManagerFactory = new TBScheduleManagerFactory();
				scheduleManagerFactory.setApplicationContext(SpringContextHolder.applicationContext);
				Map<String, String> zkConfig = new HashMap<String, String>();
				String rootPath = SCHEDULE_ROOT + "/" + scheduleConfigServer.getApplicationName() + SCHEDULE_SUFFIX;
				hostRootPath = configServerKey + ":" + rootPath;
				zkConfig.put("zkConnectString", scheduleConfigServer.getServerAddress());
				zkConfig.put("rootPath", rootPath);
				zkConfig.put("userName", scheduleConfigServer.getUsername());
				zkConfig.put("password", scheduleConfigServer.getPassword());
				zkConfig.put("zkSessionTimeout", String.valueOf(scheduleConfigServer.getTimeout()));
				zkConfig.put("isCheckParentPath", String.valueOf(scheduleConfigServer.getIsCheckParentPath()));
				scheduleManagerFactory.setZkConfig(zkConfig);
				scheduleConfigServer.setScheduleManagerFactory(scheduleManagerFactory);

				if (scheduleConfigServer.getAutoStartup()) {
					startSchedule(rootPath, scheduleConfigServer);
				}
			}
		}
	}

	public void initTbScheduleLog() {
		BaseTask.setOpenLog(openLog);
		BaseTask.setLogLength(logLength);
	}

	/**
	 * 
	 * @param configServerKey
	 * @return
	 */
	public TBScheduleManagerFactory getScheduleManagerFactory(String configServerKey) {
		vailPermission(configServerKey);

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
	public void saveOrUpdateTask(String scheduleName, ScheduleParameter config, String configServerKey) {
		try {
			TBScheduleManagerFactory scheduleManagerFactory = getScheduleManagerFactory(configServerKey);
			initSchedule(scheduleName, scheduleManagerFactory);
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

			if (!scheduleManagerFactory.isZookeeperInitialSucess()) {
				String rootPath = scheduleManagerFactory.getZkConfig().get("rootPath");
				throw new SystemException("ScheduleManagerFactory " + rootPath + " Zookeeper initial fail!");
			}

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
	 * @param rootPath
	 * @param scheduleConfigServer
	 * @throws Exception
	 */
	private void startSchedule(String rootPath, ScheduleConfigServer scheduleConfigServer) throws Exception {
		TBScheduleManagerFactory scheduleManagerFactory = scheduleConfigServer.getScheduleManagerFactory();
		boolean flag = false;
		int num = 0;
		scheduleManagerFactory.init();

		while (true) {
			if (scheduleManagerFactory.isZookeeperInitialSucess()) {
				flag = true;
				break;
			}

			if (num >= 30) {
				flag = false;
				log.error("ZookeeperInitial fail ...");
				break;
			}

			num++;
			Thread.sleep(1000);
		}

		Thread.sleep(1000);
		initedScheduleManagerFactoryMap.put(rootPath, scheduleManagerFactory);

		if (flag) {
			log.info("TBSchedule [" + scheduleConfigServer.getConfigServerKey() + " " + rootPath + "] 自动启动");
		} else {
			String exceptionMessage = "TBSchedule [" + scheduleConfigServer.getConfigServerKey() + " " + rootPath + "] 启动失败";
			throw new SystemException(exceptionMessage);
		}
	}

	/**
	 * 
	 * @param scheduleManagerFactory
	 * @return
	 * @throws Exception
	 */
	private void initSchedule(String scheduleName, TBScheduleManagerFactory scheduleManagerFactory) throws Exception {
		Map<String, String> zkConfig = scheduleManagerFactory.getZkConfig();
		String newRootPath = SCHEDULE_ROOT + "/" + scheduleName;
		TBScheduleManagerFactory initedScheduleManagerFactory = initedScheduleManagerFactoryMap.get(newRootPath);
		boolean flag = false;

		if (initedScheduleManagerFactory == null) {
			zkConfig.put("rootPath", newRootPath);
			scheduleManagerFactory.setZkConfig(zkConfig);
			int num = 0;
			scheduleManagerFactory.initWithoutTimer();

			while (true) {
				if (scheduleManagerFactory.isZookeeperInitialSucess()) {
					flag = true;
					break;
				}

				if (num >= 30) {
					flag = false;
					log.error("Zookeeper initial fail ...");
					break;
				}

				num++;
				Thread.sleep(1000);
			}

			Thread.sleep(1000);
			initedScheduleManagerFactoryMap.put(newRootPath, scheduleManagerFactory);
		} else {
			flag = true;
		}

		if (!flag) {
			throw new SystemException("ScheduleManagerFactory " + newRootPath + " initial fail!");
		}
	}

	private void vailPermission(String configServerKey) {
		if (configServerKey == null) {
			configServerKey = ScheduleSwitcher.getScheduleType();
		}

		if (configServerKey == null) {
			configServerKey = defaultTargetScheduleSource.getConfigServerKey();
		}

		String managerRootPath = configServerKey + ":" + SCHEDULE_MANAGER_NAME;

		if (hostRootPath == null || !hostRootPath.equals(managerRootPath)) {
			throw new SystemException("Have no permission to operate!");
		}
	}
}

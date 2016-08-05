package com.kochiu.se.core.quartz.source;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.core.quartz.config.QuartzConfigServer;
import com.kochiu.se.core.quartz.config.QuartzParameter;
import com.kochiu.se.core.quartz.job.BaseJob;
import com.kochiu.se.core.quartz.job.JobProxy;
import com.kochiu.se.dataaccess.mysql.source.DynamicDataSource;
import org.apache.commons.lang.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.kochiu.se.common.util.StringUtil;
import com.kochiu.se.core.context.SpringContextHolder;

public class DynamicQuartz {

	public static final String IS_CRON_TRIGGER = "isCronTrigger";

	public static final String TRIGGER_EXPRESSION = "expression";

	public static final String JOB_CLASS_NAME = "jobClassName";

	public static final String SCHEDULER_DATASOURCE_KEY = "dataSourceKey";

	public static final String[] SCHEDULER_CONSTANTS = { IS_CRON_TRIGGER, TRIGGER_EXPRESSION, JOB_CLASS_NAME, SCHEDULER_DATASOURCE_KEY };

	private final static String QUARTZ_MANAGER_NAME = "workerMonitorScheduler";

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Map<String, QuartzConfigServer> targetQuartzConfigServerMap;

	private QuartzConfigServer defaultTargetQuartzSource;

	private String hostQuartzName;

	/**
	 * 日志开关，默认为false不打开
	 */
	private boolean openLog;

	/**
	 * 日志最大长度，如果不传则默认1000，传-1则不限制日志打印长度
	 */
	private int logLength;

	public void setTargetQuartzConfigServerMap(Map<String, QuartzConfigServer> targetQuartzConfigServerMap) {
		this.targetQuartzConfigServerMap = targetQuartzConfigServerMap;
	}

	public void setDefaultTargetQuartzSource(QuartzConfigServer defaultTargetQuartzSource) {
		this.defaultTargetQuartzSource = defaultTargetQuartzSource;
	}

	public void setOpenLog(boolean openLog) {
		this.openLog = openLog;
	}

	public void setLogLength(int logLength) {
		this.logLength = logLength;
	}

	public void afterPropertiesSet() throws Exception {
		Set<Entry<String, QuartzConfigServer>> set = targetQuartzConfigServerMap.entrySet();
		List<QuartzConfigServer> quartzConfigServerList = new ArrayList<QuartzConfigServer>();

		for (Entry<String, QuartzConfigServer> entry : set) {
			String configServerKey = entry.getKey();
			QuartzConfigServer quartzConfigServer = entry.getValue();

			if (quartzConfigServer != null) {
				Properties quartzProperties = quartzConfigServer.getQuartzProperties();
				Properties p = (Properties) quartzProperties.clone();
				String dataSourceName = quartzProperties.getProperty("org.quartz.jobStore.dataSource");
				String applicationName = quartzConfigServer.getApplicationName();
				String dataSourceKey = quartzConfigServer.getDataSourceKey();
				String instanceName = p.getProperty("org.quartz.scheduler.instanceName");
				instanceName = applicationName + instanceName;
				hostQuartzName = configServerKey + ":" + instanceName;
				p.setProperty("org.quartz.scheduler.instanceName", instanceName);
				p.setProperty("org.quartz.jobStore.dataSource", dataSourceKey);
				DynamicDataSource dynamicDataSource = (DynamicDataSource) SpringContextHolder.applicationContext.getBean(dataSourceName);
				final DataSource dataSource = dynamicDataSource.getDataSource(dataSourceKey);

				DBConnectionManager.getInstance().addConnectionProvider(dataSourceKey, new ConnectionProvider() {
					@Override
					public Connection getConnection() throws SQLException {
						return DataSourceUtils.doGetConnection(dataSource);
					}

					@Override
					public void shutdown() throws SQLException {
					}

					@Override
					public void initialize() throws SQLException {
					}
				});

				StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(p);
				quartzConfigServer.setStdSchedulerFactory(stdSchedulerFactory);
				quartzConfigServerList.add(quartzConfigServer);
			}
		}

		for (QuartzConfigServer quartzConfigServer : quartzConfigServerList) {
			if (quartzConfigServer.getAutoStartup()) {
				startQuartz(quartzConfigServer);
			}
		}
	}

	public QuartzSchedulerFactory getQuartzSchedulerFactory(String configServerKey, String schedName) {
		vailPermission(configServerKey);

		if (configServerKey == null) {
			configServerKey = QuartzSwitcher.getQuartzType();
		}

		QuartzSchedulerFactory quartzSchedulerFactory = null;

		try {
			if (StringUtils.isNotBlank(configServerKey)) {
				QuartzConfigServer quartzConfigServer = targetQuartzConfigServerMap.get(configServerKey);

				if (quartzConfigServer != null) {
					StdSchedulerFactory stdSchedulerFactory = quartzConfigServer.getStdSchedulerFactory();

					if (StringUtil.isNotBlank(schedName)) {
						Properties quartzProperties = quartzConfigServer.getQuartzProperties();
						Properties p = (Properties) quartzProperties.clone();
						p.setProperty("org.quartz.scheduler.instanceName", schedName);
						p.setProperty("org.quartz.jobStore.dataSource", quartzConfigServer.getDataSourceKey());
						stdSchedulerFactory.initialize(p);
					}

					Scheduler scheduler = stdSchedulerFactory.getScheduler();
					scheduler.getContext().put("org.quartz.jobStore.dataSource", quartzConfigServer.getDataSourceKey());
					quartzSchedulerFactory = new QuartzSchedulerFactory(stdSchedulerFactory, scheduler);
				}
			}
		} catch (Exception e) {
			log.error("GetQuartzSchedulerFactory error", e);
		}

		if (quartzSchedulerFactory == null) {
			quartzSchedulerFactory = getDefaultQuartzSchedulerFactory(schedName);
		}

		if (quartzSchedulerFactory == null) {
			throw new SystemException("Can not get a QuartzSchedulerFactory!");
		}

		return quartzSchedulerFactory;
	}

	public List<Scheduler> getAllSchedulers(String configServerKey) {
		if (configServerKey == null) {
			configServerKey = QuartzSwitcher.getQuartzType();
		}

		List<Scheduler> schedulers = null;
		List<String> schedulerNames = null;
		Connection conn = null;

		try {
			if (configServerKey == null) {
				configServerKey = QuartzSwitcher.getQuartzType();
			}

			QuartzConfigServer quartzConfigServer = null;

			if (StringUtils.isNotBlank(configServerKey)) {
				quartzConfigServer = targetQuartzConfigServerMap.get(configServerKey);
			} else {
				quartzConfigServer = defaultTargetQuartzSource;
			}

			conn = DBConnectionManager.getInstance().getConnection(quartzConfigServer.getDataSourceKey());
			Properties quartzProperties = quartzConfigServer.getQuartzProperties();
			String tablePrefix = quartzProperties.getProperty("org.quartz.jobStore.tablePrefix");
			schedulerNames = selectAllSchedulerNames(tablePrefix, conn);

			if (schedulerNames != null && !schedulerNames.isEmpty()) {
				schedulers = new LinkedList<Scheduler>();

				for (String schedName : schedulerNames) {
					QuartzSchedulerFactory quartzSchedulerFactory = getQuartzSchedulerFactory(configServerKey, schedName);
					Scheduler scheduler = quartzSchedulerFactory.getScheduler();
					schedulers.add(scheduler);
				}
			}

			if (schedulers == null) {
				schedulers = new ArrayList<Scheduler>();
			}
		} catch (Exception e) {
			log.error("GetAllSchedulers error", e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Throwable ignored) {
			}
		}

		return schedulers;
	}

	public void saveOrUpdateJob(QuartzParameter config, String configServerKey, boolean alreadyWarning) {
		try {
			QuartzSchedulerFactory quartzSchedulerFactory = getQuartzSchedulerFactory(configServerKey, config.getSchedName());
			Scheduler scheduler = quartzSchedulerFactory.getScheduler();
			String dataSourceKey = scheduler.getContext().getString("org.quartz.jobStore.dataSource");
			JobKey jobKey = JobKey.jobKey(config.getJobName(), config.getJobGroup());
			TriggerKey triggerKey = TriggerKey.triggerKey(config.getTriggerName(), config.getTriggerGroup());

			if (checkJobExists(scheduler, jobKey)) {
				if (alreadyWarning) {
					throw new SystemException("The job[schedName=" + scheduler.getSchedulerName() + ",jobName=" + config.getJobName() + ",jobGroup="
							+ config.getJobGroup() + "] is already exist");
				}
			}

			scheduler.unscheduleJob(triggerKey);
			scheduler.deleteJob(jobKey);
			JobDetail jobDetail = newJobDetail(config, JobProxy.class, dataSourceKey);
			Trigger trigger = newTrigger(config);
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}
	}

	public void updateJob(QuartzParameter config, String configServerKey) {
		try {
			QuartzSchedulerFactory quartzSchedulerFactory = getQuartzSchedulerFactory(configServerKey, config.getSchedName());
			Scheduler scheduler = quartzSchedulerFactory.getScheduler();
			String dataSourceKey = scheduler.getContext().getString("org.quartz.jobStore.dataSource");
			JobKey jobKey = JobKey.jobKey(config.getJobName(), config.getJobGroup());
			TriggerKey triggerKey = TriggerKey.triggerKey(config.getTriggerName(), config.getTriggerGroup());

			if (checkJobExists(scheduler, jobKey)) {
				scheduler.unscheduleJob(triggerKey);
				scheduler.deleteJob(jobKey);
				JobDetail jobDetail = newJobDetail(config, JobProxy.class, dataSourceKey);
				Trigger trigger = newTrigger(config);
				scheduler.scheduleJob(jobDetail, trigger);
			} else {
				throw new SystemException("The job[schedName=" + scheduler.getSchedulerName() + ",jobName=" + config.getJobName() + ",jobGroup="
						+ config.getJobGroup() + "] is not exist");
			}
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * 暂停一个job
	 * 
	 * @throws SchedulerException
	 */
	public void pauseTrigger(QuartzParameter config, String configServerKey) {
		String schedName = config.getSchedName();
		String triggerName = config.getTriggerName();
		String triggerGroup = config.getTriggerGroup();
		QuartzSchedulerFactory quartzSchedulerFactory = getQuartzSchedulerFactory(configServerKey, schedName);
		Scheduler scheduler = quartzSchedulerFactory.getScheduler();

		try {
			scheduler.pauseTrigger(new TriggerKey(triggerName, triggerGroup));
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * 恢复job
	 * 
	 * @throws SchedulerException
	 */
	public void resumeTrigger(QuartzParameter config, String configServerKey) {
		String schedName = config.getSchedName();
		String triggerName = config.getTriggerName();
		String triggerGroup = config.getTriggerGroup();
		QuartzSchedulerFactory quartzSchedulerFactory = getQuartzSchedulerFactory(configServerKey, schedName);
		Scheduler scheduler = quartzSchedulerFactory.getScheduler();

		try {
			scheduler.resumeTrigger(new TriggerKey(triggerName, triggerGroup));
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}
	}

	/**
	 * 删除该config指定的job的trigger
	 * 一个job可能有多个trigger,如果本次删除的trigger是该job唯一的触发器，则该job也会被删除
	 * 
	 */
	public void deleteTrigger(QuartzParameter config, String configServerKey) {
		String schedName = config.getSchedName();
		String triggerName = config.getTriggerName();
		String triggerGroup = config.getTriggerGroup();
		QuartzSchedulerFactory quartzSchedulerFactory = getQuartzSchedulerFactory(configServerKey, schedName);
		Scheduler scheduler = quartzSchedulerFactory.getScheduler();

		try {
			scheduler.unscheduleJob(new TriggerKey(triggerName, triggerGroup));
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}
	}

	public void initQuartzLog() {
		BaseJob.setOpenLog(openLog);
		BaseJob.setLogLength(logLength);
	}

	public class QuartzSchedulerFactory {

		private StdSchedulerFactory stdSchedulerFactory;

		private Scheduler scheduler;

		public QuartzSchedulerFactory(StdSchedulerFactory stdSchedulerFactory, Scheduler scheduler) {
			this.stdSchedulerFactory = stdSchedulerFactory;
			this.scheduler = scheduler;
		}

		public StdSchedulerFactory getStdSchedulerFactory() {
			return stdSchedulerFactory;
		}

		public void setStdSchedulerFactory(StdSchedulerFactory stdSchedulerFactory) {
			this.stdSchedulerFactory = stdSchedulerFactory;
		}

		public Scheduler getScheduler() {
			return scheduler;
		}

		public void setScheduler(Scheduler scheduler) {
			this.scheduler = scheduler;
		}
	}

	private QuartzSchedulerFactory getDefaultQuartzSchedulerFactory(String schedName) {
		QuartzSchedulerFactory defaultQuartzSchedulerFactory = null;

		try {
			StdSchedulerFactory defaultStdSchedulerFactory = defaultTargetQuartzSource.getStdSchedulerFactory();

			if (StringUtil.isNotBlank(schedName)) {
				Properties quartzProperties = defaultTargetQuartzSource.getQuartzProperties();
				Properties p = (Properties) quartzProperties.clone();
				p.setProperty("org.quartz.scheduler.instanceName", schedName);
				p.setProperty("org.quartz.jobStore.dataSource", defaultTargetQuartzSource.getDataSourceKey());
				defaultStdSchedulerFactory.initialize(p);
			}

			Scheduler defaultScheduler = defaultStdSchedulerFactory.getScheduler();
			defaultScheduler.getContext().put("org.quartz.jobStore.dataSource", defaultTargetQuartzSource.getDataSourceKey());
			defaultQuartzSchedulerFactory = new QuartzSchedulerFactory(defaultStdSchedulerFactory, defaultScheduler);
		} catch (SchedulerException e) {
			log.error("GetDefaultQuartzSchedulerFactory error", e);
		}

		return defaultQuartzSchedulerFactory;
	}

	private void startQuartz(QuartzConfigServer quartzConfigServer) throws SchedulerException {
		StdSchedulerFactory stdSchedulerFactory = quartzConfigServer.getStdSchedulerFactory();
		Scheduler scheduler = stdSchedulerFactory.getScheduler();
		scheduler.start();
		log.info("Quartz [" + quartzConfigServer.getConfigServerKey() + "] 自动启动");
	}

	private boolean checkJobExists(Scheduler scheduler, JobKey jobKey) {
		try {
			return scheduler.checkExists(jobKey);
		} catch (SchedulerException e) {
			throw new SystemException(e);
		}
	}

	private JobDetail newJobDetail(QuartzParameter config, Class<? extends Job> jobClass, String dataSource) {
		JobDetail jobDetail = JobBuilder.newJob(jobClass).requestRecovery(config.getIsRecovery()).withIdentity(config.getJobName(), config.getJobGroup())
				.withDescription(config.getDescription()).usingJobData(JOB_CLASS_NAME, config.getJobClassName())
				.usingJobData(SCHEDULER_DATASOURCE_KEY, dataSource).build();

		Map<String, String> extraInfoMap = config.getExtraInfo();
		JobBuilder jobBuilder = jobDetail.getJobBuilder();

		if (extraInfoMap != null) {
			for (Entry<String, String> en : extraInfoMap.entrySet()) {
				jobBuilder.usingJobData(en.getKey(), en.getValue());
			}
		}

		jobDetail = jobBuilder.build();
		return jobDetail;
	}

	private Trigger newTrigger(QuartzParameter config) {
		Trigger trigger = null;

		if (config.getIsCronTrigger()) {
			CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(config.getExpression()).withMisfireHandlingInstructionDoNothing();
			TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey(config.getTriggerName(), config.getTriggerGroup())).withSchedule(builder)
					.usingJobData(IS_CRON_TRIGGER, config.getIsCronTrigger()).usingJobData(TRIGGER_EXPRESSION, config.getExpression());

			if (config.getStartAt() == null || config.getStartAt().getTime() < new Date().getTime()) {
				triggerBuilder.startNow();
			} else {
				triggerBuilder.startAt(config.getStartAt());
			}

			if (config.getEndAt() != null) {
				triggerBuilder.endAt(config.getEndAt());
			}

			trigger = triggerBuilder.build();
		} else {
			SimpleScheduleBuilder builder = SimpleScheduleBuilder.simpleSchedule().repeatForever()
					.withIntervalInSeconds(Integer.valueOf(config.getExpression()));
			TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
					.withIdentity(TriggerKey.triggerKey(config.getTriggerName(), config.getTriggerGroup())).withSchedule(builder)
					.usingJobData(IS_CRON_TRIGGER, config.getIsCronTrigger()).usingJobData(TRIGGER_EXPRESSION, config.getExpression());

			if (config.getStartAt() == null || config.getStartAt().getTime() < new Date().getTime()) {
				triggerBuilder.startNow();
			} else {
				triggerBuilder.startAt(config.getStartAt());
			}

			if (config.getEndAt() != null) {
				triggerBuilder.endAt(config.getEndAt());
			}

			trigger = triggerBuilder.build();
		}

		return trigger;
	}

	private List<String> selectAllSchedulerNames(String tablePrefix, Connection conn) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> schedulerNames = new ArrayList<String>();

		try {
			String sql = "SELECT SCHED_NAME FROM " + tablePrefix + "JOB_DETAILS GROUP BY SCHED_NAME";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				String schedulerName = rs.getString("SCHED_NAME");
				schedulerNames.add(schedulerName);
			}

			return schedulerNames;
		} finally {
			closeResultSet(rs);
			closeStatement(ps);
		}
	}

	private static void closeResultSet(ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException ignore) {
			}
		}
	}

	private static void closeStatement(Statement statement) {
		if (null != statement) {
			try {
				statement.close();
			} catch (SQLException ignore) {
			}
		}
	}

	private void vailPermission(String configServerKey) {
		if (configServerKey == null) {
			configServerKey = QuartzSwitcher.getQuartzType();
		}

		if (configServerKey == null) {
			configServerKey = defaultTargetQuartzSource.getConfigServerKey();
		}

		String managerQuartzName = configServerKey + ":" + QUARTZ_MANAGER_NAME;

		if (hostQuartzName == null || !hostQuartzName.equals(managerQuartzName)) {
			throw new SystemException("Have no permission to operate!");
		}
	}
}

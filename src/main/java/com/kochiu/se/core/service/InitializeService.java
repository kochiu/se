package com.kochiu.se.core.service;

import com.kochiu.se.core.mq.source.DynamicCreateMqListenerManager;
import com.kochiu.se.core.mq.source.DynamicCreateMqProducerManager;
import com.kochiu.se.core.mq.source.DynamicMqListenerContainer;
import com.kochiu.se.core.mq.source.MqMessageSenderSwitcher;
import com.kochiu.se.core.quartz.source.DynamicCreateQuartzManager;
import com.kochiu.se.core.quartz.source.QuartzSwitcher;
import com.kochiu.se.core.tbschedule.source.DynamicCreateScheduleManager;
import com.kochiu.se.core.tbschedule.source.ScheduleSwitcher;
import com.kochiu.se.dataaccess.fastdfs.source.DynamicCreateFastdfsSourceManager;
import com.kochiu.se.dataaccess.hbase.source.DynamicCreateHbaseSourceManager;
import com.kochiu.se.dataaccess.hbase.source.HbaseSourceSwitcher;
import com.kochiu.se.dataaccess.memcache.source.DynamicCreateMemcacheSourceManager;
import com.kochiu.se.dataaccess.memcache.source.MemcacheSourceSwitcher;
import com.kochiu.se.dataaccess.mongo.source.DynamicCreateMongoSourceManager;
import com.kochiu.se.dataaccess.mongo.source.MongoSourceSwitcher;
import com.kochiu.se.dataaccess.mysql.source.DataSourceSwitcher;
import com.kochiu.se.dataaccess.mysql.source.DynamicCreateDataSourceManager;
import com.kochiu.se.dataaccess.redis.source.DynamicCreateRedisSourceManager;
import com.kochiu.se.dataaccess.redis.source.RedisSourceSwitcher;
import com.kochiu.se.rpc.dubbo.source.DubboClientSwitcher;
import com.kochiu.se.rpc.dubbo.source.DynamicCreateDubboClientManager;
import com.kochiu.se.rpc.dubbo.source.DynamicCreateDubboProviderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service("seInitializeService")
public class InitializeService implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired(required = false)
	private DynamicCreateDataSourceManager dynamicCreateDataSourceManager;

	@Autowired(required = false)
	private DynamicCreateMongoSourceManager dynamicCreateMongoSourceManager;

	@Autowired(required = false)
	private DynamicCreateHbaseSourceManager dynamicCreateHbaseSourceManager;

	@Autowired(required = false)
	private DynamicCreateRedisSourceManager dynamicCreateRedisSourceManager;

	@Autowired(required = false)
	private DynamicCreateMemcacheSourceManager dynamicCreateMemcacheSourceManager;

	@Autowired(required = false)
	private DynamicCreateFastdfsSourceManager dynamicCreateFastdfsSourceManager;

	@Autowired(required = false)
	private DynamicCreateDubboProviderManager dynamicCreateDubboProviderManager;

	@Autowired(required = false)
	private DynamicCreateDubboClientManager dynamicCreateDubboClientManager;

	@Autowired(required = false)
	private DynamicCreateMqProducerManager dynamicCreateMqProducerManager;

	@Autowired(required = false)
	private DynamicCreateMqListenerManager dynamicCreateMqListenerManager;

	@Autowired(required = false)
	private DynamicMqListenerContainer dynamicMqListenerContainer;

	@Autowired(required = false)
	private DynamicCreateQuartzManager dynamicCreateQuartzManager;

	@Autowired(required = false)
	private DynamicCreateScheduleManager dynamicCreateScheduleManager;

	@Autowired(required = false)
	private StartupCallback startupCallback;

	private volatile boolean initialFlag;

	@Override
	public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null && !initialFlag) {
			initialFlag = true;

			// 系统动态source初始化
			initDynamicSource();

			// 系统启动阶段的业务处理扩展
			initStartService();

			// 清理动态源
			clearDynamicSources();
		}
	}

	/**
	 * 清理动态源
	 */
	public static void clearDynamicSources() {
		DataSourceSwitcher.clearDataSourceType();
		HbaseSourceSwitcher.clearHbaseSourceType();
		MongoSourceSwitcher.clearMongoSourceType();
		RedisSourceSwitcher.clearRedisSourceType();
		MemcacheSourceSwitcher.clearMemcacheSourceType();
		DubboClientSwitcher.clearDubboClientType();
		MqMessageSenderSwitcher.clearMqMessageSenderContextType();
		QuartzSwitcher.clearQuartzContextType();
		ScheduleSwitcher.clearScheduleContextType();
	}

	/**
	 * 初始化系统数据源，包括数据库，缓存
	 * 
	 * @param applicationContext
	 */
	private void initDynamicSource() {
		if (dynamicCreateDataSourceManager != null) {
			dynamicCreateDataSourceManager.initCreateDataSource();
		}

		if (dynamicCreateMongoSourceManager != null) {
			dynamicCreateMongoSourceManager.initCreateMongoSource();
		}

		if (dynamicCreateHbaseSourceManager != null) {
			dynamicCreateHbaseSourceManager.initCreateHbaseSource();
		}

		if (dynamicCreateRedisSourceManager != null) {
			dynamicCreateRedisSourceManager.initCreateRedisSource();
		}

		if (dynamicCreateMemcacheSourceManager != null) {
			dynamicCreateMemcacheSourceManager.initCreateMemcacheSource();
		}

		if (dynamicCreateFastdfsSourceManager != null) {
			dynamicCreateFastdfsSourceManager.initCreateFastdfsSource();
		}

		if (dynamicCreateDubboProviderManager != null) {
			dynamicCreateDubboProviderManager.initCreateDubboProvider();
		}

		if (dynamicCreateDubboClientManager != null) {
			dynamicCreateDubboClientManager.initCreateDubboClient();
		}

		if (dynamicCreateMqProducerManager != null) {
			dynamicCreateMqProducerManager.initCreateMqProducer();
		}

		if (dynamicCreateMqListenerManager != null) {
			dynamicCreateMqListenerManager.initCreateMqListener();
		}

		if (dynamicCreateQuartzManager != null) {
			dynamicCreateQuartzManager.initCreateQuartz();
		}

		if (dynamicCreateScheduleManager != null) {
			dynamicCreateScheduleManager.initCreateSchedule();
		}
	}

	/**
	 * 初始化系统启动阶段指行的业务扩展
	 * 
	 * @param applicationContext
	 */
	private void initStartService() {
		if (startupCallback != null) {
			startupCallback.businessHandle();
		}
	}
}

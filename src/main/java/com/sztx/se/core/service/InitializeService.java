package com.sztx.se.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.sztx.se.core.mq.source.DynamicCreateMqListenerManager;
import com.sztx.se.core.mq.source.DynamicCreateMqProducerManager;
import com.sztx.se.core.mq.source.DynamicMqListenerContainer;
import com.sztx.se.core.mq.source.MqMessageSenderSwitcher;
import com.sztx.se.core.quartz.source.DynamicCreateQuartzManager;
import com.sztx.se.core.quartz.source.QuartzSwitcher;
import com.sztx.se.core.tbschedule.source.DynamicCreateScheduleManager;
import com.sztx.se.core.tbschedule.source.ScheduleSwitcher;
import com.sztx.se.dataaccess.fastdfs.source.DynamicCreateFastdfsSourceManager;
import com.sztx.se.dataaccess.hbase.source.DynamicCreateHbaseSourceManager;
import com.sztx.se.dataaccess.hbase.source.HbaseSourceSwitcher;
import com.sztx.se.dataaccess.memcache.source.DynamicCreateMemcacheSourceManager;
import com.sztx.se.dataaccess.memcache.source.MemcacheSourceSwitcher;
import com.sztx.se.dataaccess.mongo.source.DynamicCreateMongoSourceManager;
import com.sztx.se.dataaccess.mongo.source.MongoSourceSwitcher;
import com.sztx.se.dataaccess.mysql.source.DataSourceSwitcher;
import com.sztx.se.dataaccess.mysql.source.DynamicCreateDataSourceManager;
import com.sztx.se.dataaccess.redis.source.DynamicCreateRedisSourceManager;
import com.sztx.se.dataaccess.redis.source.RedisSourceSwitcher;
import com.sztx.se.rpc.dubbo.source.DubboClientSwitcher;
import com.sztx.se.rpc.dubbo.source.DynamicCreateDubboClientManager;
import com.sztx.se.rpc.dubbo.source.DynamicCreateDubboProviderManager;

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

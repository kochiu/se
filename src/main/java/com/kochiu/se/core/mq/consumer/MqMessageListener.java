package com.kochiu.se.core.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.util.date.DateUtil;

/**
 * 消息处理器
 * 
 * @author zhihongp
 * 
 * @param <T>
 */
public abstract class MqMessageListener implements MessageListener {

	private static final Logger log = LoggerFactory.getLogger(MqMessageListener.class);

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static boolean openLog;
	
	public static void setOpenLog(boolean openLog) {
		MqMessageListener.openLog = openLog;
	}
	
	public abstract Object handleMessage(String messageId, String messageContent, String queue);

	@Override
	public void onMessage(Message message) {
		if (openLog) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			Object obj = null;
			String messageId = "";
			String messageContent = "";
			String queue = "";

			try {
				messageId = message.getMessageProperties().getMessageId();
				messageContent = new String(message.getBody(), DEFAULT_CHARSET);
				queue = message.getMessageProperties().getReceivedRoutingKey();
				obj = handleMessage(messageId, messageContent, queue);
			} catch (Throwable t) {
				obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
				log.error("Handle mq message error, message=" + message, t);
			} finally {
				String mqResult = "";

				if (obj != null) {
					mqResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
				}

				endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
				// 打印日志
				String messageLog = getMessageLog(messageId, messageContent, queue, mqResult, startTime, endTime);

				if (messageLog.length() > ContextConstants.LOG_MAX_LENGTH) {
					messageLog = messageLog.substring(0, ContextConstants.LOG_MAX_LENGTH);
				}

				log.info(messageLog);
			}
		} else {
			try {
				String messageId = message.getMessageProperties().getMessageId();
				String messageContent = new String(message.getBody(), DEFAULT_CHARSET);
				String queue = message.getMessageProperties().getReceivedRoutingKey();
				handleMessage(messageId, messageContent, queue);
			} catch (Throwable t) {
				log.error("Handle mq message error, message=" + message, t);
			}
		}
	}

	private String getMessageLog(String messageId, String messageContent, String queue, String mqResult, long startTime, long endTime) {
		long cost = endTime - startTime;
		String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		return String.format("[Consumer] Receive mq message, messageId:%s|messageContent:%s|queue:%s|result:%s|[start:%s, end:%s, cost:%dms]", messageId,
				messageContent, queue, mqResult, startTimeStr, endTimeStr, cost);
	}

}
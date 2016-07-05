package com.kochiu.se.core.mq.source;

import java.util.Map;

import com.kochiu.se.common.util.UUIDUtil;
import com.kochiu.se.core.mq.producer.MqMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.kochiu.se.common.domain.ContextConstants;
import com.kochiu.se.common.exception.SystemException;
import com.kochiu.se.common.util.ReflectUtil;
import com.kochiu.se.common.util.date.DateUtil;

public class DynamicMqMessageSender implements MqMessageSender {

	private static final Logger log = LoggerFactory.getLogger(DynamicMqMessageSender.class);

	private static final String SUCCESS = "success";

	private RabbitTemplate defaultTargetRabbitTemplate;

	private Map<String, RabbitTemplate> targetRabbitTemplates;

	/**
	 * 日志开关，默认为false不打开
	 */
	private boolean openLog;

	public void setDefaultTargetRabbitTemplate(RabbitTemplate defaultTargetRabbitTemplate) {
		this.defaultTargetRabbitTemplate = defaultTargetRabbitTemplate;
	}

	public void setTargetRabbitTemplates(Map<String, RabbitTemplate> targetRabbitTemplates) {
		this.targetRabbitTemplates = targetRabbitTemplates;
	}

	public void setOpenLog(boolean openLog) {
		this.openLog = openLog;
	}

	public String sendMessage(final Object messageObj) {
		RabbitTemplate rabbitTemplate = getRabbitTemplate();

		if (rabbitTemplate == null) {
			rabbitTemplate = defaultTargetRabbitTemplate;
		}

		if (rabbitTemplate == null) {
			throw new SystemException("Can not get a rabbitTemplate!");
		}

		boolean flag = false;
		final String messageId = UUIDUtil.getNumUUID();

		if (openLog) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			String messageContent = "";
			String exchange = (String) ReflectUtil.getFieldValue(rabbitTemplate, "exchange");
			String routingKey = (String) ReflectUtil.getFieldValue(rabbitTemplate, "routingKey");
			Object obj = null;

			try {
				messageContent = JSON.toJSONStringWithDateFormat(messageObj, DateUtil.MAX_LONG_DATE_FORMAT_STR,
						SerializerFeature.DisableCircularReferenceDetect);

				rabbitTemplate.convertAndSend(messageObj, new MessagePostProcessor() {
					@Override
					public Message postProcessMessage(Message message) throws AmqpException {
						message.getMessageProperties().setMessageId(messageId);
						return message;
					}
				});

				obj = SUCCESS;
				flag = true;
			} catch (Throwable t) {
				obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
				log.error("Send mq message error, message=" + messageObj, t);
			} finally {
				String mqResult = "";
				String messageIdStr = "";

				if (obj != null) {
					mqResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
				}

				if (flag) {
					messageIdStr = messageId;
				}

				endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
				// 打印日志
				String messageLog = getMessageLog(messageIdStr, messageContent, exchange, routingKey, mqResult, startTime, endTime);

				if (messageLog.length() > ContextConstants.LOG_MAX_LENGTH) {
					messageLog = messageLog.substring(0, ContextConstants.LOG_MAX_LENGTH);
				}

				log.info(messageLog);
			}
		} else {
			rabbitTemplate.convertAndSend(messageObj, new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setMessageId(messageId);
					return message;
				}
			});

			flag = true;
		}

		if (flag) {
			return messageId;
		} else {
			return null;
		}
	}

	public String sendMessage(String exchange, String routingKey, final Object messageObj) {
		RabbitTemplate rabbitTemplate = getRabbitTemplate();

		if (rabbitTemplate == null) {
			rabbitTemplate = defaultTargetRabbitTemplate;
		}

		if (rabbitTemplate == null) {
			throw new SystemException("Can not get a rabbitTemplate!");
		}

		boolean flag = false;
		final String messageId = UUIDUtil.getNumUUID();

		if (openLog) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			String messageContent = "";
			Object obj = null;

			try {
				messageContent = JSON.toJSONStringWithDateFormat(messageObj, DateUtil.MAX_LONG_DATE_FORMAT_STR,
						SerializerFeature.DisableCircularReferenceDetect);

				rabbitTemplate.convertAndSend(exchange, routingKey, messageObj, new MessagePostProcessor() {
					@Override
					public Message postProcessMessage(Message message) throws AmqpException {
						message.getMessageProperties().setMessageId(messageId);
						return message;
					}
				});

				obj = SUCCESS;
				flag = true;
			} catch (Throwable t) {
				obj = t.getClass().getCanonicalName() + ":" + t.getMessage();
				log.error("Send mq message error, message=" + messageObj, t);
			} finally {
				String mqResult = "";
				String messageIdStr = "";

				if (obj != null) {
					mqResult = JSON.toJSONStringWithDateFormat(obj, DateUtil.MAX_LONG_DATE_FORMAT_STR, SerializerFeature.DisableCircularReferenceDetect);
				}

				if (flag) {
					messageIdStr = messageId;
				}

				endTime = (endTime == 0 ? System.currentTimeMillis() : endTime);
				// 打印日志
				String messageLog = getMessageLog(messageIdStr, messageContent, exchange, routingKey, mqResult, startTime, endTime);
				log.info(messageLog);
			}
		} else {
			rabbitTemplate.convertAndSend(exchange, routingKey, messageObj, new MessagePostProcessor() {
				@Override
				public Message postProcessMessage(Message message) throws AmqpException {
					message.getMessageProperties().setMessageId(messageId);
					return message;
				}
			});

			flag = true;
		}

		if (flag) {
			return messageId;
		} else {
			return null;
		}
	}

	private String getMessageLog(String messageId, String messageContent, String exchange, String routingKey, String mqResult, long startTime, long endTime) {
		long cost = endTime - startTime;
		String startTimeStr = DateUtil.formatDate(startTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		String endTimeStr = DateUtil.formatDate(endTime, DateUtil.MAX_LONG_DATE_FORMAT_STR);
		return String.format("[Producer] Send mq message, messageId:%s|messageContent:%s|exchange:%s|routingKey:%s|result:%s|[start:%s, end:%s, cost:%dms]",
				messageId, messageContent, exchange, routingKey, mqResult, startTimeStr, endTimeStr, cost);
	}

	private RabbitTemplate getRabbitTemplate() {
		String mqMessageSender = MqMessageSenderSwitcher.getMqMessageSenderType();

		if (mqMessageSender != null && !"".equals(mqMessageSender)) {
			RabbitTemplate rabbitTemplate = targetRabbitTemplates.get(mqMessageSender);
			return rabbitTemplate;
		} else {
			return null;
		}
	}

	public void afterPropertiesSet() {

	}

	public void initMqLog() {

	}
}

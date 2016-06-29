package com.sztx.se.rpc.dubbo.client;

/**
 * dubbo客户端工厂
 * 
 * @author zhihongp
 *
 */
public interface DubboClientFactory {

	/**
	 * 获取某个dubbo消费端
	 * 
	 * @param dubbo客户端对应的beanId
	 * @param type 客户端类
	 * @return dubbo客户端
	 */
	<T> T getDubboClient(String beanId);

	/**
	 * 获取某个dubbo消费端
	 * 
	 * @param dubbo客户端对应的bean
	 * @param type 客户端类
	 * @return dubbo客户端
	 */
	<T> T getDubboClient(DubboClient dubboClient);

}

package com.kochiu.se.dataaccess.redis;

import java.util.List;

/**
 * 
 * @author zhihongp
 * 
 */
public interface BaseRedisDAO {

	/**
	 * 序列化對象
	 * 
	 * @param obj
	 * @return
	 */
	byte[] serialize(Object obj);
	
	/**
	 * 反序列化对象
	 * @param type
	 * @param bytes
	 * @return
	 */
	<T> T deserialize(Class<T> type, byte[] bytes);
	
	/**
	 * 反序列化对象转化为列表
	 * @param type
	 * @param bytes
	 * @return
	 */
	<T> List<T> deserializeArray(Class<T> type, byte[] bytes);
}

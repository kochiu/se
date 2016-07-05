package com.kochiu.se.dataaccess.redis.impl;

import java.util.List;

import com.kochiu.se.dataaccess.redis.source.DynamicRedisSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.kochiu.se.dataaccess.redis.BaseRedisDAO;
import com.kochiu.se.dataaccess.redis.serialize.RedisSerializer;

/**
 * 
 * @author zhihongp
 * 
 */
public abstract class BaseRedisDAOImpl implements BaseRedisDAO {

	@Autowired(required = false)
	protected DynamicRedisSource redisTemplate;

	@Override
	public byte[] serialize(Object obj) {
		return RedisSerializer.serialize(obj);
	}

	@Override
	public <T> T deserialize(Class<T> type, byte[] bytes) {
		return RedisSerializer.deserialize(type, bytes);
	}

	@Override
	public <T> List<T> deserializeArray(Class<T> type, byte[] bytes) {
		return RedisSerializer.deserializeArray(type, bytes);
	}
}

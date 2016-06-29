package com.sztx.se.dataaccess.redis.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sztx.se.dataaccess.redis.BaseRedisDAO;
import com.sztx.se.dataaccess.redis.serialize.RedisSerializer;
import com.sztx.se.dataaccess.redis.source.DynamicRedisSource;

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

package com.kochiu.se.dataaccess.redis.callback;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;

import com.kochiu.se.dataaccess.redis.serialize.RedisSerializer;

public abstract class RedisCallback<T> implements org.springframework.data.redis.core.RedisCallback<T> {

	public abstract String getKey();
	
	public abstract T doInRedis(RedisConnection connection, byte[] key);
	
	@Override
	public T doInRedis(RedisConnection connection) throws DataAccessException {
		String key = getKey();
		return doInRedis(connection, RedisSerializer.serialize(key));
	}

}

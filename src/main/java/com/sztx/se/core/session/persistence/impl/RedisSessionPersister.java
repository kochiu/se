package com.sztx.se.core.session.persistence.impl;

import java.util.concurrent.ConcurrentMap;

import org.springframework.data.redis.connection.RedisConnection;

import com.pay1pay.framework.session.SessionMetadata;
import com.sztx.se.common.util.session.HttpSessionProxy;
import com.sztx.se.common.util.session.SessionUtil;
import com.sztx.se.core.session.config.SessionConfig;
import com.sztx.se.core.session.persistence.SessionPersister;
import com.sztx.se.dataaccess.redis.callback.RedisCallback;
import com.sztx.se.dataaccess.redis.serialize.RedisSerializer;
import com.sztx.se.dataaccess.redis.source.DynamicRedisSource;

public class RedisSessionPersister implements SessionPersister {

	// private static final String DEFAULT_SESSION_KEY = "Session:";

	private static final String DEFAULT_SESSION_KEY = "";

	private DynamicRedisSource dynamicRedisSource;

	public void setDynamicRedisSource(DynamicRedisSource dynamicRedisSource) {
		this.dynamicRedisSource = dynamicRedisSource;
	}

	@Override
	public HttpSessionProxy getSessionFromCache(final String key) {
		HttpSessionProxy httpSessionProxy = dynamicRedisSource.execute(new RedisCallback<HttpSessionProxy>() {
			@Override
			public String getKey() {
				return DEFAULT_SESSION_KEY + key;
			}

			@Override
			public HttpSessionProxy doInRedis(RedisConnection connection, byte[] key) {
				byte[] bytes = connection.get(key);
				SessionMetadata sessionMetadata = RedisSerializer.deserialize(SessionMetadata.class, bytes, dynamicRedisSource.getValueSerializer());
				HttpSessionProxy httpSessionProxy = null;

				if (sessionMetadata != null) {
					httpSessionProxy = getHttpSessionProxy(sessionMetadata);
				}

				return httpSessionProxy;
			}
		});

		return httpSessionProxy;
	}

	@Override
	public boolean addSessionToCache(final HttpSessionProxy httpSession, final SessionConfig sessionConfig) {
		if (httpSession == null) {
			return false;
		}

		final SessionMetadata sessionMetadata = getSessionMetadata(httpSession);

		boolean result = dynamicRedisSource.execute(new RedisCallback<Boolean>() {
			@Override
			public String getKey() {
				return DEFAULT_SESSION_KEY + sessionMetadata.getSessionId();
			}

			@Override
			public Boolean doInRedis(RedisConnection connection, byte[] key) {
				if (getExpired(sessionMetadata)) {
					connection.del(key);
				} else {
					long currentTime = System.currentTimeMillis();
					sessionMetadata.setLastAccessedTime(currentTime);
					long timeout = sessionMetadata.getMaxInactiveInterval() / 1000;
					connection.setEx(key, timeout, RedisSerializer.serialize(sessionMetadata, dynamicRedisSource.getValueSerializer()));
				}

				return true;
			}
		});

		return result;
	}

	private HttpSessionProxy getHttpSessionProxy(SessionMetadata sessionMetadata) {
		String sessionId = sessionMetadata.getSessionId();
		long creationTime = sessionMetadata.getCreationTime();
		int maxInactiveInterval = sessionMetadata.getMaxInactiveInterval();
		long lastAccessedTime = sessionMetadata.getLastAccessedTime();
		boolean expired = getExpired(sessionMetadata);
		boolean isNew = false;
		boolean isDirty = sessionMetadata.isChanged();
		ConcurrentMap<String, Object> data = sessionMetadata.getSessionMap();
		HttpSessionProxy httpSessionProxy = SessionUtil.createSession(sessionId, creationTime, maxInactiveInterval, lastAccessedTime, expired, isNew, isDirty,
				data);
		return httpSessionProxy;
	}

	private SessionMetadata getSessionMetadata(HttpSessionProxy httpSessionProxy) {
		SessionMetadata sessionMetadata = new SessionMetadata();
		sessionMetadata.setSessionId(httpSessionProxy.getId());
		sessionMetadata.setCreationTime(httpSessionProxy.getCreationTime());
		sessionMetadata.setLastAccessedTime(httpSessionProxy.getLastAccessedTime());
		sessionMetadata.setMaxInactiveInterval(httpSessionProxy.getMaxInactiveInterval());
		sessionMetadata.setSessionMap(httpSessionProxy.getData());
		sessionMetadata.setChanged(httpSessionProxy.isDirty());
		return sessionMetadata;
	}

	private boolean getExpired(SessionMetadata sessionMetadata) {
		return sessionMetadata.getLastAccessedTime() + sessionMetadata.getMaxInactiveInterval() <= System.currentTimeMillis();
	}

	// @Override
	// public HttpSessionProxy getSessionFromCache(final String key) {
	// HttpSessionProxy httpSessionProxy = dynamicRedisSource.execute(new
	// RedisCallback<HttpSessionProxy>() {
	// @Override
	// public String getKey() {
	// return DEFAULT_SESSION_KEY + key;
	// }
	//
	// @Override
	// public HttpSessionProxy doInRedis(RedisConnection connection, byte[] key)
	// {
	// byte[] bytes = connection.get(key);
	// SessionMeta sessionMeta = RedisSerializer.deserialize(SessionMeta.class,
	// bytes, dynamicRedisSource.getValueSerializer());
	// return sessionMeta;
	// }
	// });
	//
	// return httpSessionProxy;
	// }

	// @Override
	// public boolean addSessionToCache(final HttpSessionProxy httpSession,
	// final SessionConfig sessionConfig) {
	// if (httpSession == null) {
	// return false;
	// }
	//
	// final SessionMeta sessionMeta = getSessionMeta(httpSession);
	//
	// boolean result = dynamicRedisSource.execute(new RedisCallback<Boolean>()
	// {
	// @Override
	// public String getKey() {
	// return DEFAULT_SESSION_KEY + sessionMeta.getId();
	// }
	//
	// @Override
	// public Boolean doInRedis(RedisConnection connection, byte[] key) {
	// if (sessionMeta.getExpired()) {
	// connection.del(key);
	// } else {
	// long currentTime = System.currentTimeMillis();
	// sessionMeta.setLastAccessedTime(currentTime);
	// connection.setEx(key, sessionMeta.getMaxInactiveInterval(),
	// RedisSerializer.serialize(sessionMeta,
	// dynamicRedisSource.getValueSerializer()));
	// }
	//
	// return true;
	// }
	// });
	//
	// return result;
	// }

	// private SessionMeta getSessionMeta(HttpSessionProxy httpSessionProxy) {
	// SessionMeta sessionMeta = new SessionMeta();
	// sessionMeta.setId(httpSessionProxy.getId());
	// sessionMeta.setCreationTime(httpSessionProxy.getCreationTime());
	// sessionMeta.setLastAccessedTime(httpSessionProxy.getLastAccessedTime());
	// sessionMeta.setMaxInactiveInterval(httpSessionProxy.getMaxInactiveInterval());
	// sessionMeta.setData(httpSessionProxy.getData());
	// sessionMeta.setNew(httpSessionProxy.isNew());
	// sessionMeta.setDirty(httpSessionProxy.isDirty());
	// sessionMeta.setExpired(httpSessionProxy.isExpired());
	// return sessionMeta;
	// }

}

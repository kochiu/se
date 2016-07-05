/*
 * 文 件 名:  SessionMetadata.java
 * 版    权:  pay1pay Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年7月4日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.pay1pay.framework.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

/**
 * session元数据定义 <功能详细描述>
 * 
 * @author jetdu
 * @version [版本号, 2014年7月4日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@SuppressWarnings("serial")
public class SessionMetadata implements Serializable {
	private boolean changed = true;

	private boolean validated = true;

	private String sessionId;

	private long creationTime;

	private long lastAccessedTime;

	private int maxInactiveInterval;

	private ConcurrentMap<String, Object> sessionMap = new MapMaker().makeMap();

	private Set<String> removedKeys = new HashSet<>();

	public SessionMetadata() {
		super();
	}

	public SessionMetadata(String sessionId, long creationTime, long lastAccessedTime) {
		super();
		this.sessionId = sessionId;
		this.creationTime = creationTime;
		this.lastAccessedTime = lastAccessedTime;
	}

	public void put(String key, Object value) {
		changed = true;
		this.sessionMap.put(key, value);
	}

	public void remove(String key) {
		changed = true;
		removedKeys.add(key);
		this.sessionMap.remove(key);
	}

	public String getSessionId() {
		return sessionId;
	}

	public Object getSessionValue(String key) {
		return this.sessionMap.get(key);
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<String> getAttributeNames(String sessionId) {
		Set<Map.Entry<String, Object>> set = sessionMap.entrySet();
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : set) {
			list.add(entry.getKey());
		}
		return list;
	}

	public long getExpiredTime() {
		return this.lastAccessedTime + getMaxInactiveInterval();
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public boolean isValidated() {
		if (!validated) {
			return false;
		}
		if ((this.lastAccessedTime + this.getMaxInactiveInterval()) >= System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public ConcurrentMap<String, Object> getSessionMap() {
		return sessionMap;
	}

	public void setSessionMap(ConcurrentMap<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

	public Set<String> getRemovedKeys() {
		return removedKeys;
	}

	public void setRemovedKeys(Set<String> removedKeys) {
		this.removedKeys = removedKeys;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (creationTime ^ (creationTime >>> 32));
		result = prime * result + (int) (lastAccessedTime ^ (lastAccessedTime >>> 32));
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((sessionMap == null) ? 0 : sessionMap.hashCode());
		result = prime * result + (validated ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SessionMetadata other = (SessionMetadata) obj;
		if (creationTime != other.creationTime)
			return false;
		if (lastAccessedTime != other.lastAccessedTime)
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (sessionMap == null) {
			if (other.sessionMap != null)
				return false;
		} else if (!sessionMap.equals(other.sessionMap))
			return false;
		if (validated != other.validated)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SessionMetadata [validated=" + validated + ", sessionId=" + sessionId + ", creationTime=" + creationTime + ", lastAccessedTime=" + lastAccessedTime + ", sessionMap=" + sessionMap + "]";
	}

}

package com.kochiu.se.core.session.config;

import java.util.concurrent.ConcurrentMap;

import com.kochiu.se.common.util.session.HttpSessionProxy;

public class SessionMeta extends HttpSessionProxy {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5136347468997520406L;

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setData(ConcurrentMap<String, Object> data) {
		this.data = data;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	@Override
	public String toString() {
		return "SessionMeta [creationTime=" + creationTime + ", id=" + id + ", maxInactiveInterval=" + maxInactiveInterval + ", lastAccessedTime="
				+ lastAccessedTime + ", expired=" + expired + ", isNew=" + isNew + ", isDirty=" + isDirty + ", data=" + data + "]";
	}

}

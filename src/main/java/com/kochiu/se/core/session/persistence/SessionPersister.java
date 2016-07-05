/*
 * 文 件 名:  Persister.java
 * 版    权:  pay1pay Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  Administrator
 * 修改时间:  2014年7月4日
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.kochiu.se.core.session.persistence;

import com.kochiu.se.common.util.session.HttpSessionProxy;
import com.kochiu.se.core.session.config.SessionConfig;

/**
 * @author zhihongp
 */
public interface SessionPersister {

	HttpSessionProxy getSessionFromCache(final String key);
	
	boolean addSessionToCache(final HttpSessionProxy httpSession, final SessionConfig sessionConfig);
}

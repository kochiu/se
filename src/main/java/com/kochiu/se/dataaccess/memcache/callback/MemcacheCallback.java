package com.kochiu.se.dataaccess.memcache.callback;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.exception.MemcachedException;

import com.kochiu.se.dataaccess.memcache.client.XMemcachedClientProxy;

public interface MemcacheCallback<T> {

	public T doInMemcache(XMemcachedClientProxy memcachedClient) throws TimeoutException, InterruptedException, MemcachedException;
}

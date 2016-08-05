package com.kochiu.se.dataaccess.memcache.callback;

import java.util.concurrent.TimeoutException;

import com.kochiu.se.dataaccess.memcache.client.XMemcachedClientProxy;
import net.rubyeye.xmemcached.exception.MemcachedException;

public interface MemcacheCallback<T> {

	public T doInMemcache(XMemcachedClientProxy memcachedClient) throws TimeoutException, InterruptedException, MemcachedException;
}

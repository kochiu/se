package com.kochiu.se.dataaccess.memcache.impl;

import com.kochiu.se.dataaccess.memcache.BaseMemcacheDAO;
import org.springframework.beans.factory.annotation.Autowired;

import com.kochiu.se.dataaccess.memcache.source.DynamicMemcacheSource;

public abstract class BaseMemcacheDAOImpl implements BaseMemcacheDAO {
	
	@Autowired(required = false)
	protected DynamicMemcacheSource memcacheTemplate;
	
}

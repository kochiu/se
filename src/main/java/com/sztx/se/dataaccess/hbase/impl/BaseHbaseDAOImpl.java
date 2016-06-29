package com.sztx.se.dataaccess.hbase.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.sztx.se.dataaccess.hbase.BaseHbaseDAO;
import com.sztx.se.dataaccess.hbase.source.DynamicHbaseSource;

/**
 * 
 * @author zhihongp
 *
 */
public abstract class BaseHbaseDAOImpl implements BaseHbaseDAO {

	@Autowired(required = false)
	protected DynamicHbaseSource hbaseTemplate;

}

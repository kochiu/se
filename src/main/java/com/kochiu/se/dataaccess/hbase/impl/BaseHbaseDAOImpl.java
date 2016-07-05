package com.kochiu.se.dataaccess.hbase.impl;

import com.kochiu.se.dataaccess.hbase.BaseHbaseDAO;
import com.kochiu.se.dataaccess.hbase.source.DynamicHbaseSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author zhihongp
 *
 */
public abstract class BaseHbaseDAOImpl implements BaseHbaseDAO {

	@Autowired(required = false)
	protected DynamicHbaseSource hbaseTemplate;

}

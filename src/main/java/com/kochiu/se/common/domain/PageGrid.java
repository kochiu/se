/**
 * 
 */
package com.kochiu.se.common.domain;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * GRID JSON桥接转换类
 * 
 * @author zhihongp
 */
public final class PageGrid<T> {
	
	@JSONField(name = "total")
	private Integer size;

	@JSONField(name = "rows")
	private List<T> list;

	public PageGrid(List<T> list) {
		this(1, list);
	}
	
	public PageGrid(Integer size, List<T> list) {
		this.size = size;
		this.list = list;
	}

	public Integer getSize() {
		return size;
	}

	public List<T> getList() {
		return list;
	}
}
package com.kochiu.se.dataaccess.mongo.config;

import org.apache.commons.lang.StringUtils;

import com.kochiu.se.common.exception.SystemException;

/**
 * mongo排序对象
 * 
 * @author zhihongp
 * 
 */
public class Order {

	public static String SORT_RULE_ASC = "asc";

	public static String SORT_RULE_DESC = "desc";

	private String orderBy;

	private String orderType;

	public Order(String orderBy, String orderType) {
		this.orderBy = orderBy;
		this.orderType = orderType;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		if (!StringUtils.isBlank(orderType)) {
			validateOrderType(orderType);
		}

		this.orderType = orderType;
	}

	private void validateOrderType(String orderType) {
		if (!SORT_RULE_ASC.equalsIgnoreCase(orderType) && !SORT_RULE_DESC.equalsIgnoreCase(orderType)) {
			throw new SystemException("错误的排序类型，orderType：" + orderType);
		}
	}

	@Override
	public String toString() {
		return "Order [orderBy=" + orderBy + ", orderType=" + orderType + "]";
	}

}

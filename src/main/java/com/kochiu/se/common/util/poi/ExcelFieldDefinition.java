package com.kochiu.se.common.util.poi;

public class ExcelFieldDefinition {

	/**
	 * 报头序号
	 */
	private Integer order;

	/**
	 * 报头名字
	 */
	private String header;

	public ExcelFieldDefinition() {

	}

	public ExcelFieldDefinition(Integer order, String header) {
		this.order = order;
		this.header = header;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public String toString() {
		return "ExcelFieldDefinition [order=" + order + ", header=" + header + "]";
	}

}

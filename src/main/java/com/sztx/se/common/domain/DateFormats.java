package com.sztx.se.common.domain;

/**
 * 日期枚举
 * @author zhihongp
 *
 */
public enum DateFormats {
	
	YEAR_MONTH_DAY("yyyy-MM-dd"),
	YEAR_MONTH_DAY_HOUR_MINUTE_SECOND("yyyy-MM-dd HH:mm:ss"),
	YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_T("yyyy-MM-dd'T'HH:mm:ss Z"),
	YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLSECOND_T("yyyy-MM-dd'T'HH:mm:ss.SZ");
	
	/**
	 * 日期格式
	 */
	private String format;

	
	public String getFormat() {
		return format;
	}


	public void setFormat(String format) {
		this.format = format;
	}


	private DateFormats(String format) {
		this.format = format;
	}
	
}


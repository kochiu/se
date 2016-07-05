package com.kochiu.se.web.domain;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = { "contentType", "view", "callback", "result", "time" })
public class ResponseLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7748984325960374079L;

	private String contentType;

	private String view;

	private String callback;

	private Object result;

	private String setCookie;

	private Date time;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public String getSetCookie() {
		return setCookie;
	}

	public void setSetCookie(String setCookie) {
		this.setCookie = setCookie;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "ResponseLog [contentType=" + contentType + ", view=" + view + ", callback=" + callback + ", result=" + result + ", setCookie=" + setCookie
				+ ", time=" + time + "]";
	}

}

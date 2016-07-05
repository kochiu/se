package com.kochiu.se.web.domain;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = { "request", "response", "cost" })
public class HttpLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1481070340249341836L;

	@JSONField(name = "request")
	private RequestLog requestLog;

	@JSONField(name = "response")
	private ResponseLog responseLog;

	/**
	 * 用时(单位: 毫秒)
	 */
	private String cost;

	public HttpLog() {

	}

	public HttpLog(RequestLog requestLog, ResponseLog responseLog, String cost) {
		this.requestLog = requestLog;
		this.responseLog = responseLog;
		this.cost = cost;
	}

	public RequestLog getRequestLog() {
		return requestLog;
	}

	public void setRequestLog(RequestLog requestLog) {
		this.requestLog = requestLog;
	}

	public ResponseLog getResponseLog() {
		return responseLog;
	}

	public void setResponseLog(ResponseLog responseLog) {
		this.responseLog = responseLog;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "HttpLog [requestLog=" + requestLog + ", responseLog=" + responseLog + ", cost=" + cost + "]";
	}

}

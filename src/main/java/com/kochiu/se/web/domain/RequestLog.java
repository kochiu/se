package com.kochiu.se.web.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = { "addr", "url", "referer", "contentType", "accept", "sessionId", "parameters", "time" })
public class RequestLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7794798982246434472L;

	private String url;

	private String referer;

	private String addr;

	private String contentType;

	private String accept;

	private String agent;

	private String sessionId;

	private Date time;

	private Map<String, String> parameters;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "RequestLog [url=" + url + ", referer=" + referer + ", addr=" + addr + ", contentType=" + contentType + ", accept=" + accept + ", agent="
				+ agent + ", sessionId=" + sessionId + ", time=" + time + ", parameters=" + parameters + "]";
	}

}

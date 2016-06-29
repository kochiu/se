package com.sztx.se.web.domain;

public class ApplicationBean {

	private String name;

	private String loginExclude;

	private String authKey;

	private String authExclude;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoginExclude() {
		return loginExclude;
	}

	public void setLoginExclude(String loginExclude) {
		this.loginExclude = loginExclude;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getAuthExclude() {
		return authExclude;
	}

	public void setAuthExclude(String authExclude) {
		this.authExclude = authExclude;
	}

	@Override
	public String toString() {
		return "ApplicationBean [name=" + name + ", loginExclude=" + loginExclude + ", authKey=" + authKey + ", authExclude=" + authExclude + "]";
	}

}

package com.kochiu.se.rpc.dubbo.config;

/**
 * 
 * @author zhihongp
 *
 */
public class DubboConfigServer {

	/**
	 * 目前全局zk统一管理，暂不支持动态化
	 */
	private String configServerKey = "dubbo";

	private String applicationName;

	private String registryAddress;

	private String registryUsername;

	private String registryPassword;

	private boolean isDefault;

	public String getConfigServerKey() {
		return configServerKey;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getRegistryAddress() {
		return registryAddress;
	}

	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}

	public String getRegistryUsername() {
		return registryUsername;
	}

	public void setRegistryUsername(String registryUsername) {
		this.registryUsername = registryUsername;
	}

	public String getRegistryPassword() {
		return registryPassword;
	}

	public void setRegistryPassword(String registryPassword) {
		this.registryPassword = registryPassword;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String toString() {
		return "DubboConfigServer [configServerKey=" + configServerKey + ", applicationName=" + applicationName + ", registryAddress=" + registryAddress
				+ ", registryUsername=" + registryUsername + ", registryPassword=" + registryPassword + ", isDefault=" + isDefault + "]";
	}

}